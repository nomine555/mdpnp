package org.mdpnp.x73.rose.error;

import java.util.List;

import org.mdpnp.x73.Formatable;
import org.mdpnp.x73.Parseable;
import org.mdpnp.x73.cmise.ModifyOperator;
import org.mdpnp.x73.mddl.ManagedObjectIdentifier;
import org.mdpnp.x73.mddl.OIDType;

public interface ErrorDetailSetList extends ErrorDetail {
	interface SetError extends Parseable, Formatable {
		ErrorStatus getErrorStatus();
		ModifyOperator getModifyOperator();
		OIDType getOid();
	};
	ManagedObjectIdentifier getManagedObject();
	List<SetError> getList();
}
