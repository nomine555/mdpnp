package org.mdpnp.devices.philips.intellivue;

import java.nio.ByteBuffer;

import org.mdpnp.x73.Message;

public interface Protocol {
	Message parse(ByteBuffer bb);
	void format(Message message, ByteBuffer bb);
}
