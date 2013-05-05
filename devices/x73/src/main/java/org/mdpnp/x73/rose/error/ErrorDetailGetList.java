package org.mdpnp.x73.rose.error;

import java.util.List;

import org.mdpnp.x73.Formatable;
import org.mdpnp.x73.Parseable;
import org.mdpnp.x73.mddl.ManagedObjectIdentifier;
import org.mdpnp.x73.mddl.OIDType;

public interface ErrorDetailGetList extends ErrorDetail {
	interface GetError extends Parseable, Formatable {
		ErrorStatus getErrorStatus();
		OIDType getOid();
	};
	ManagedObjectIdentifier getManagedObject();
	List<GetError> getList();
}
