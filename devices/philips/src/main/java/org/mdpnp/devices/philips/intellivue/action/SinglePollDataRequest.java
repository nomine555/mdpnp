package org.mdpnp.devices.philips.intellivue.action;

import org.mdpnp.devices.philips.intellivue.data.Type;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportAction;
import org.mdpnp.x73.mddl.OIDType;

public interface SinglePollDataRequest extends DataExportAction {
	int getPollNumber();
	void setPollNumber(int x);
	Type getPolledObjectType();
	OIDType getPolledAttributeGroup();
	void setPolledAttributeGroup(OIDType type);
}
