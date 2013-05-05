package org.mdpnp.devices.philips.intellivue.dataexport;

import java.nio.ByteBuffer;

import org.mdpnp.x73.Formatable;
import org.mdpnp.x73.Parseable;

public interface DataExportEvent extends Parseable, Formatable {
	void parseMore(ByteBuffer bb);
}
