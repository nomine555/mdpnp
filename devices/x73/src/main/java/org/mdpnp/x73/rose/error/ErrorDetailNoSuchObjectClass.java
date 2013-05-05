package org.mdpnp.x73.rose.error;

import org.mdpnp.x73.mddl.OIDType;

public interface ErrorDetailNoSuchObjectClass extends ErrorDetail {
	OIDType getObjectClass();
}
