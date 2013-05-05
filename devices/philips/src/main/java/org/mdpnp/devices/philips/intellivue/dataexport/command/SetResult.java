package org.mdpnp.devices.philips.intellivue.dataexport.command;

import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.x73.cmise.CmiseMessage;

public interface SetResult extends CmiseMessage {
	AttributeValueList getAttributes();
}
