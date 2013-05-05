package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.x73.Message;

public class Altitude implements Message {

	private short altitude;
	
	@Override
	public void parse(ByteBuffer bb) {
		altitude = bb.getShort();
	}

	@Override
	public void format(ByteBuffer bb) {
		bb.putShort(altitude);
	}

}
