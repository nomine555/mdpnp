package org.mdpnp.devices.philips.intellivue.dataexport.command;

import org.mdpnp.devices.philips.intellivue.dataexport.DataExportAction;
import org.mdpnp.x73.cmise.CmiseMessage;
import org.mdpnp.x73.mddl.OIDType;

public interface ActionResult extends CmiseMessage {

	OIDType getActionType();
	void setActionType(OIDType type);
	DataExportAction getAction();
	void setAction(DataExportAction action);
}
