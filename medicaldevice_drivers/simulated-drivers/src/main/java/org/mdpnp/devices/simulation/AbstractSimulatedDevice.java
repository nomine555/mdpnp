/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation;

import ice.DeviceIdentity;
import ice.DeviceIdentityTypeCode;

import java.util.UUID;

import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.UDI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSimulatedDevice extends AbstractDevice {
    private static final Logger log = LoggerFactory.getLogger(AbstractSimulatedDevice.class);


    public AbstractSimulatedDevice(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
        UDI.randomUDI(deviceIdentity);
        writeDeviceIdentity();
    }
}
