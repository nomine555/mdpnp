package org.mdpnp.devices.philips.intellivue.dataexport.command;

import org.mdpnp.devices.philips.intellivue.dataexport.DataExportEvent;
import org.mdpnp.x73.cmise.CmiseMessage;
import org.mdpnp.x73.mddl.ManagedObjectIdentifier;
import org.mdpnp.x73.mddl.OIDType;

public interface EventReport extends CmiseMessage {
	ManagedObjectIdentifier getManagedObject();
	OIDType getEventType();
	void setEventType(OIDType oid);
	EventReport createConfirm();
	DataExportEvent getEvent();
	void setEvent(DataExportEvent event);
}
