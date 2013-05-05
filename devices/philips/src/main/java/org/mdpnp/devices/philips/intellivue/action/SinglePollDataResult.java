package org.mdpnp.devices.philips.intellivue.action;

import java.util.List;

import org.mdpnp.devices.philips.intellivue.data.AbsoluteTime;
import org.mdpnp.devices.philips.intellivue.data.RelativeTime;
import org.mdpnp.devices.philips.intellivue.data.Type;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportAction;
import org.mdpnp.x73.mddl.OIDType;

public interface SinglePollDataResult extends DataExportAction {
	
	int getPollNumber();
	void setPollNumber(int pollNumber);
	RelativeTime getRelativeTime();
	AbsoluteTime getAbsoluteTime();
	Type getPolledObjType();
	OIDType getPolledAttributeGroup();
	List<SingleContextPoll> getPollInfoList();
}
