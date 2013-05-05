package org.mdpnp.x73.rose.error;

import org.mdpnp.x73.mddl.ManagedObjectIdentifier;

public interface ErrorDetailInvalidObjectInstance extends ErrorDetail {
	ManagedObjectIdentifier getManagedObject();
}
