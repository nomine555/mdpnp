package org.mdpnp.devices.philips.intellivue.action;

import java.util.List;

import org.mdpnp.x73.Formatable;
import org.mdpnp.x73.Parseable;

public interface SingleContextPoll extends Parseable, Formatable {
	int getMdsContext();
	List<ObservationPoll> getPollInfo();
	
}