package org.mdpnp.devices.philips.intellivue.dataexport.event;

import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportEvent;
import org.mdpnp.x73.mddl.ManagedObjectIdentifier;

public interface MdsCreateEvent extends DataExportEvent {
	AttributeValueList getAttributes();
	ManagedObjectIdentifier getManagedObject();
}
