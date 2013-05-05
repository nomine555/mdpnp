package org.mdpnp.devices.philips.intellivue.attribute;

import org.mdpnp.x73.Message;
import org.mdpnp.x73.mddl.OIDType;

public interface Attribute<T extends Message> extends Message {
	OIDType getOid();
	T getValue();
}
