package org.mdpnp.devices.philips.intellivue.dataexport;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.dataexport.command.ActionResult;
import org.mdpnp.x73.Formatable;
import org.mdpnp.x73.Parseable;

public interface DataExportAction extends Parseable, Formatable {
	void parseMore(ByteBuffer bb);
	ActionResult getAction();
	void setAction(ActionResult action);
}
