package org.mdpnp.devices.philips.intellivue.action;

import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.x73.Formatable;
import org.mdpnp.x73.Parseable;
import org.mdpnp.x73.mddl.Handle;

public interface ObservationPoll extends Parseable, Formatable {
	Handle getHandle();
	AttributeValueList getAttributes();
}