package org.mdpnp.x73.rose.error;

import org.mdpnp.x73.mddl.ManagedObjectIdentifier;
import org.mdpnp.x73.mddl.OIDType;

public interface ErrorDetailInvalidArgumentValue extends ErrorDetail {
	ManagedObjectIdentifier getManagedObject();
	OIDType getActionType();
	int getLength();
}
