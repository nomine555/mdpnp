package org.mdpnp.devices.philips.intellivue.dataexport.command;

import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.x73.cmise.CmiseMessage;
import org.mdpnp.x73.mddl.ManagedObjectIdentifier;

public interface GetResult extends CmiseMessage {
	ManagedObjectIdentifier getManagedObject();
	AttributeValueList getAttributeList();
}
 