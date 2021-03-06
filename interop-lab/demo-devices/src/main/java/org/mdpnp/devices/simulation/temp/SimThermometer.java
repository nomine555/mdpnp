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
package org.mdpnp.devices.simulation.temp;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;

/**
 * @author Jeff Plourde
 *
 */
public class SimThermometer extends AbstractSimulatedConnectedDevice {

    protected InstanceHolder<ice.Numeric> temperature1, temperature2;

    private class MySimulatedThermometer extends SimulatedThermometer {
        @Override
        protected void receiveTemp1(float temperature1) {
            SimThermometer.this.temperature1 = numericSample(SimThermometer.this.temperature1, temperature1, rosetta.MDC_TEMP_BLD.VALUE, 0, null);
        }

        @Override
        protected void receiveTemp2(float temperature2) {
            SimThermometer.this.temperature2 = numericSample(SimThermometer.this.temperature2, temperature2, rosetta.MDC_TEMP_BLD.VALUE, 1, null);
        }
    }

    private final MySimulatedThermometer thermometer = new MySimulatedThermometer();

    @Override
    public void connect(String str) {
        thermometer.connect(executor);
        super.connect(str);
    }

    @Override
    public void disconnect() {
        thermometer.disconnect();
        super.disconnect();
    }

    public SimThermometer(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
        deviceIdentity.model = "Thermometer (Simulated)";
        writeDeviceIdentity();
    }

    @Override
    protected String iconResourceName() {
        return null;
    }
}
