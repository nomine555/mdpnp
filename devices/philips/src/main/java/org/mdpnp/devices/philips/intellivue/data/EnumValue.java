package org.mdpnp.devices.philips.intellivue.data;

import org.mdpnp.x73.Message;


public interface EnumValue<T extends EnumMessage<T>> extends Message {
	T getEnum();
	void setEnum(T t);
}
