package org.mdpnp.devices.philips.intellivue.dataexport;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.Protocol;
import org.mdpnp.x73.rose.RoseMessage;

public interface DataExportProtocol extends Protocol {
	
	Header getHeader();
	
	void format(RoseMessage message, ByteBuffer bb);
	
	@Override
	RoseMessage parse(ByteBuffer bb);

}
