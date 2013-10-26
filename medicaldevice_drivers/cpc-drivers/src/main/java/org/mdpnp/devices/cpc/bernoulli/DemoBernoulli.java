package org.mdpnp.devices.cpc.bernoulli;

import ice.Numeric;
import ice.SampleArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.UDI;
import org.mdpnp.devices.connected.AbstractConnectedDevice;
import org.mdpnp.devices.connected.TimeAwareInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class DemoBernoulli extends AbstractConnectedDevice implements Runnable {
    protected final Map<String, InstanceHolder<Numeric>> numerics = new HashMap<String, InstanceHolder<Numeric>>();
    private final Map<String, InstanceHolder<SampleArray>> waveforms = new HashMap<String, InstanceHolder<SampleArray>>();

    private class MyBernoulli extends Bernoulli {

        public MyBernoulli() {

        }

        private String priorLocation, priorStatus;

        @Override
        public void location(String location) {
            if (null == priorLocation || !priorLocation.equals(location)) {
                log.info("location=" + location);
                priorLocation = location;
            }

        }

        @Override
        public void status(String status) {
            if (null == priorStatus || !priorStatus.equals(status)) {
                log.info("status=" + status);
                priorStatus = status;
            }
            if ("UP".equals(status)) {
                inited = true;
            } else {
                inited = false;
                close();
            }
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
        }

        // private final MutableEnumerationUpdate cLock = new
        // MutableEnumerationUpdateImpl(PulseOximeter.C_LOCK);
        protected void measurement(String name, String value) {
            // if("SPO2_C_LOCK".equals(name)) {
            // if("ON".equals(value)) {
            // cLock.setValue(PulseOximeter.CLock.On);
            // gateway.update(DemoBernoulli.this, cLock);
            // } else if("OFF".equals(value)) {
            // cLock.setValue(PulseOximeter.CLock.Off);
            // gateway.update(DemoBernoulli.this, cLock);
            // } else {
            // log.warn("Unknown SPO2_C_LOCK value:"+value);
            // }
            // return;
            // }

            InstanceHolder<Numeric> numeric = numerics.get(name);
            // MutableTextUpdate text = texts.get(name);

            if (null != numeric) {
                try {
                    numeric.data.value = Float.parseFloat(value);
                    numericDataWriter.write(numeric.data, numeric.handle);
                } catch (NumberFormatException nfe) {
                    log.warn(name + "=" + value + " is not a number");
                }
                // log.trace(numeric.toString());
                return;
                // } else if(null != text) {
                // text.setValue(value);
                // gateway.update(DemoBernoulli.this, text);
                // log.trace(text.toString());
                // return;
            } else {
                log.warn("Orphaned Measure:" + name + "=" + value);
            }

        }

        @Override
        protected void measurementGroup(String name, Number[] n, double msPerSample) {
            super.measurementGroup(name, n, msPerSample);
            InstanceHolder<SampleArray> waveform = waveforms.get(name);
            if (null != waveform) {
                waveform.data.millisecondsPerSample = (int) msPerSample;
                waveform.data.values.clear();
                for (Number _n : n) {
                    waveform.data.values.addFloat(_n.floatValue());
                }
                log.trace("SIZE=" + waveform.data.values.size() + " LENGTH=" + n.length);
                sampleArrayDataWriter.write(waveform.data, waveform.handle);

                log.trace(waveform.toString());
            } else {
                log.warn("Orphaned Measure:" + name + "=" + Arrays.toString(n));
            }
        }

        @Override
        protected void device(String bid, String make, String model) {
            super.device(bid, make, model);

            bid = null == bid ? "" : bid.replaceAll("\\_", " ");
            make = null == make ? "" : make.replaceAll("\\_", " ");
            model = null == model ? "" : model.replaceAll("\\_", " ");

            // In DDS world continually republishing the same sample is JUST
            // NOISE
            if (!bid.equals(deviceIdentity.serial_number) || !make.equals(deviceIdentity.manufacturer)
                    || !model.equals(deviceIdentity.model)) {
                deviceIdentity.serial_number = bid;
                deviceIdentity.manufacturer = make;
                deviceIdentity.model = model;
                writeDeviceIdentity();
            }
        }
    }

    protected void addWaveform(String name, String tagName) {
        waveforms.put(name, createSampleArrayInstance(tagName));
        log.trace("Added Waveform:" + name + " tag=" + tagName);
    }

    protected void addNumeric(String name, String tagName) {
        numerics.put(name, createNumericInstance(tagName));
        log.trace("Added Numeric:" + name + " tag=" + tagName);
    }

    private static String getValue(String name) throws Exception {
        try {
            Class<?> cls = Class.forName(name);
            return (String) cls.getField("VALUE").get(null);
        } catch (Exception e) {
            // If it's not a class then maybe it's a static member
            int lastIndexOfDot = name.lastIndexOf('.');
            if (lastIndexOfDot < 0) {
                throw new ClassNotFoundException("Cannot find " + name, e);
            }
            try {
                Class<?> cls = Class.forName(name.substring(0, lastIndexOfDot));
                Object obj = cls.getField(name.substring(lastIndexOfDot + 1, name.length())).get(null);
                return (String) obj.getClass().getMethod("value").invoke(obj);
            } catch (Exception e1) {
                throw new ClassNotFoundException("Cannot find " + name, e1);
            }

        }

    }

    protected static void populateMap(Map<String, String> numerics, Map<String, String> waveforms)
            throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                DemoBernoulli.class.getResourceAsStream("bernoulli.map")));
        String line = null;
        while (null != (line = br.readLine())) {
            line = line.trim();
            if ('#' != line.charAt(0)) {
                String v[] = line.split("\t");
                if (v.length < 3) {
                    log.warn("Bad line in bernoulli.map:" + line);
                } else {
                    v[2] = v[2].trim();
                    String value = getValue(v[1]);
                    if (null == value) {
                        log.warn("Cannot find value for " + v[1]);
                        continue;
                    }
                    if ("W".equals(v[2])) {
                        if (waveforms.containsKey(v[0])) {
                            throw new RuntimeException("Duplicate values for waveform " + v[0] + " "
                                    + waveforms.get(v[0]) + " and " + value);
                        }
                        waveforms.put(v[0], value);
                    } else if ("N".equals(v[2])) {
                        if (numerics.containsKey(v[0])) {
                            throw new RuntimeException("Duplicate values for numeric " + v[0] + " "
                                    + numerics.get(v[0]) + " and " + value);
                        }
                        numerics.put(v[0], value);
                    } else {
                        log.warn("Unknown field type=" + v[2]);
                    }
                }
            }
        }
    }

    public DemoBernoulli(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);

        // Random UDI is for the device module
        // this allows the module to exist within the ICE in a disconnected
        // state
        // and allows other components to request a connection to the attached
        // device
        UDI.randomUDI(deviceIdentity);
        writeDeviceIdentity();

        try {
            Map<String, String> numerics = new HashMap<String, String>();
            Map<String, String> waveforms = new HashMap<String, String>();
            populateMap(numerics, waveforms);
            for (String name : numerics.keySet()) {
                addNumeric(name, numerics.get(name));
            }
            for (String name : waveforms.keySet()) {
                addWaveform(name, waveforms.get(name));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (MAX_QUIET_TIME > 0L) {
            executor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    watchdog();
                }
            }, 0L, MAX_QUIET_TIME, TimeUnit.MILLISECONDS);
        }

    }

    private final MyBernoulli myBernoulli = new MyBernoulli();

    @Override
    protected ice.ConnectionType getConnectionType() {
        return ice.ConnectionType.Network;
    }

    @Override
    protected String iconResourceName() {
        return "450c.png";
    }

    @Override
    public void connect(String str) {
        int port = 17008;
        if (str.contains(":")) {
            int colon = str.lastIndexOf(':');
            port = Integer.parseInt(str.substring(colon + 1, str.length()));
            str = str.substring(0, colon);
        }

        log.trace("connect requested to " + str);
        synchronized (this) {
            this.host = str;
            this.port = port;

            switch (getState().ordinal()) {
            case ice.ConnectionState._Connected:
            case ice.ConnectionState._Negotiating:
            case ice.ConnectionState._Connecting:
                return;
            case ice.ConnectionState._Disconnected:
            case ice.ConnectionState._Disconnecting:
                stateMachine.transitionWhenLegal(ice.ConnectionState.Connecting);
                break;
            }
            currentThread = new Thread(this, "BernoulliImpl Processing");
            currentThread.setDaemon(true);
            currentThread.start();
        }

    }

    private long previousAttempt = 0L;
    private TimeAwareInputStream tais;

    private long getConnectInterval() {
        return 5000L;
    }

    @Override
    public void run() {
        log.info(Thread.currentThread().getName() + " (" + Thread.currentThread().getId() + ") begins");

        Socket socket = null;

        long now = System.currentTimeMillis();

        // Staying in the Connecting state while awaiting another time interval
        while (now < (previousAttempt + getConnectInterval())) {
            setConnectionInfo("Waiting to reconnect... " + ((previousAttempt + getConnectInterval()) - now) + "ms");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                log.error("", e);
            }
            now = System.currentTimeMillis();
        }
        setConnectionInfo("");
        previousAttempt = now;
        try {
            log.trace("Invoking Socket.connect(" + host + ")");
            socket = new Socket(host, port);

            this.socket = socket;
            if (!stateMachine.transitionIfLegal(ice.ConnectionState.Negotiating)) {
                throw new IllegalStateException("Cannot begin negotiating from " + getState());
            }

            // This thread will drive the next state transition
            Thread t = new Thread(new Negotiation(socket.getOutputStream()), "Subscription");
            t.setDaemon(true);
            t.start();

            myBernoulli.process(tais = new TimeAwareInputStream(socket.getInputStream()));
        } catch (UnknownHostException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info(Thread.currentThread().getName() + " (" + Thread.currentThread().getId() + ")  ends");
            ice.ConnectionState priorState = getState();
            close();
            stateMachine.transitionIfLegal(ice.ConnectionState.Disconnected);
            switch (priorState.ordinal()) {
            case ice.ConnectionState._Connected:
            case ice.ConnectionState._Connecting:
            case ice.ConnectionState._Negotiating:
                log.trace("process thread died unexpectedly, trying to reconnect");
                connect(host);
                break;
            default:
            }
        }

    }

    private Socket socket;

    protected String host;
    private int port;
    private Thread currentThread;

    private static final long MAX_QUIET_TIME = 4000L;

    protected void watchdog() {
        TimeAwareInputStream tais = this.tais;
        if (null != tais) {
            long quietTime = System.currentTimeMillis() - tais.getLastReadTime();
            if (quietTime > MAX_QUIET_TIME) {
                if (ice.ConnectionState.Connected.equals(getState())) {
                    log.warn("WATCHDOG - closing after " + quietTime + "ms quiet time (exceeds " + MAX_QUIET_TIME + ")");
                    // close should cause the processing thread to end... which
                    // will spawn a connect on its exit
                    close();
                }
            }
        }

    }

    @Override
    public void disconnect() {
        log.trace("disconnect requested");
        synchronized (this) {
            switch (getState().ordinal()) {
            case ice.ConnectionState._Disconnected:
            case ice.ConnectionState._Disconnecting:
                return;
            case ice.ConnectionState._Connecting:
            case ice.ConnectionState._Connected:
            case ice.ConnectionState._Negotiating:
                stateMachine.transitionWhenLegal(ice.ConnectionState.Disconnecting);

                try {
                    socket.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
                break;
            }
        }
    }

    private static final Logger log = LoggerFactory.getLogger(DemoBernoulli.class);

    @Override
    public void shutdown() {
        close();
        super.shutdown();
    }

    private void close() {
        log.trace("close");
        Socket socket = this.socket;
        this.socket = null;

        if (socket != null) {
            try {
                log.trace("attempting to close socket");
                socket.close();
                log.trace("close - socket closed without error");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.debug("close - socket was already null");
        }
    }

    private volatile boolean inited;

    public class Negotiation implements Runnable {
        private final OutputStream outputStream;

        public Negotiation(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            log.trace(Thread.currentThread().getName() + " (" + Thread.currentThread().getId() + ") begins");
            inited = false;
            try {
                log.trace("invoking sendSubscription");

                long now = System.currentTimeMillis();
                long giveup = now + MAX_QUIET_TIME;

                Bernoulli.sendSubscription(outputStream);

                while (true) {

                    synchronized (this) {
                        if (inited || System.currentTimeMillis() > giveup) {
                            break;
                        }
                        try {
                            wait(1000L);
                        } catch (InterruptedException e) {
                            log.error("", e);
                        }
                    }
                    if (giveup > System.currentTimeMillis()) {
                        Bernoulli.sendSubscription(outputStream);
                    }
                }

                // } catch (JAXBException e) {
                // log.error(e.getMessage(), e);
            } catch (IOException e1) {
                log.error(e1.getMessage(), e1);
            } finally {
                log.trace(Thread.currentThread().getName() + " (" + Thread.currentThread().getId() + ") ends");
                if (inited) {
                    log.trace("sendSubscription returns true");
                    stateMachine.transitionIfLegal(ice.ConnectionState.Connected);
                } else {
                    log.trace("sendSubscription returns false");
                    if (ice.ConnectionState.Negotiating.equals(getState())) {
                        log.trace("canceling negotation via close()");
                        close();
                    }
                }
            }

        }

    }

    // private final Runnable nibpRequest = new Runnable() {
    // public void run() {
    // int port = 2050;
    // try {
    // String host = DemoBernoulli.this.host;
    // String bid = guidUpdate.getValue();
    // if(null != host && bid != null && !"".equals(bid)) {
    // if(Bernoulli.sendCommand(DemoBernoulli.this.host, port,
    // guidUpdate.getValue(), Bernoulli.CMD_REQUEST_NIBP)) {
    // log.debug("Successfully requested NIBP");
    // } else {
    // log.error("Failed to request NIBP");
    // }
    // } else {
    // log.warn("Insufficient info to request NIBP host="+host+" bid="+bid);
    // }
    // } catch (IOException e) {
    // log.error("Error requesting NIBP", e);
    // }
    // }
    // };

    // @Override
    // public void update(IdentifiableUpdate<?> command) {
    // if(NoninvasiveBloodPressure.REQUEST_NIBP.equals(command.getIdentifier()))
    // {
    // executor.schedule(nibpRequest, 0L, TimeUnit.MILLISECONDS);
    // }
    // super.update(command);
    // }
}
