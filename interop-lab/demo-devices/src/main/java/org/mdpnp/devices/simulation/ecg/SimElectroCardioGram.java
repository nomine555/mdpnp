/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.devices.simulation.ecg;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;

/**
 * @author Jeff Plourde
 *
 */
public class SimElectroCardioGram extends AbstractSimulatedConnectedDevice {

    protected final InstanceHolder<ice.SampleArray> i, ii, iii;

    protected final InstanceHolder<ice.Numeric> respiratoryRate, heartRate;

    private class MySimulatedElectroCardioGram extends SimulatedElectroCardioGram {
        @Override
        protected void receiveECG(Number[] iValues, Number[] iiValues, Number[] iiiValues, int heartRateValue, int respiratoryRateValue,
                double msPerSample) {
            sampleArraySample(i, iValues, (int) msPerSample, null);
            sampleArraySample(ii, iiValues, (int) msPerSample, null);
            sampleArraySample(iii, iiiValues, (int) msPerSample, null);
            numericSample(heartRate, heartRateValue, null);
            numericSample(respiratoryRate, respiratoryRateValue, null);
        }
    }

    private final MySimulatedElectroCardioGram ecg = new MySimulatedElectroCardioGram();

    @Override
    public void connect(String str) {
        ecg.connect(executor);
        super.connect(str);
    }

    @Override
    public void disconnect() {
        ecg.disconnect();
        super.disconnect();
    }

    public SimElectroCardioGram(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);

        i = createSampleArrayInstance(rosetta.MDC_ECG_AMPL_ST_I.VALUE);
        ii = createSampleArrayInstance(rosetta.MDC_ECG_AMPL_ST_II.VALUE);
        iii = createSampleArrayInstance(rosetta.MDC_ECG_AMPL_ST_III.VALUE);
        respiratoryRate = createNumericInstance(rosetta.MDC_RESP_RATE.VALUE);
        heartRate = createNumericInstance(rosetta.MDC_ECG_CARD_BEAT_RATE.VALUE);

        deviceIdentity.model = "ECG (Simulated)";
        writeDeviceIdentity();
    }

    @Override
    protected String iconResourceName() {
        return "ecg.png";
    }
}
