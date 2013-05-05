package org.mdpnp.devices.philips.intellivue.dataexport;

import org.mdpnp.x73.cmise.CmiseMessage;
import org.mdpnp.x73.cmise.CmiseOperation;
import org.mdpnp.x73.rose.RoseMessage;

public interface DataExportResult extends RoseMessage {
	CmiseOperation getCommandType();
	void setCommandType(CmiseOperation commandType);
	CmiseMessage getCommand();
	void setCommand(CmiseMessage dec);
	void parseMore(java.nio.ByteBuffer bb);
}
