package org.mdpnp.x73.rose.error;

import org.mdpnp.x73.mddl.OIDType;

public interface ErrorDetailNoSuchAction extends ErrorDetail {
	OIDType getObjectClass();
	OIDType getActionType();
}
