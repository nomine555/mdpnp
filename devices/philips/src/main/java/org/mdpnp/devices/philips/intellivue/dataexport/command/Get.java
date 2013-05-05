package org.mdpnp.devices.philips.intellivue.dataexport.command;

import java.util.List;

import org.mdpnp.x73.cmise.CmiseMessage;
import org.mdpnp.x73.mddl.OIDType;

public interface Get extends CmiseMessage {
	List<OIDType> getAttributeId();
}
