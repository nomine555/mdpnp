package org.mdpnp.x73.rose.error;

import java.util.Map;

import org.mdpnp.x73.OrdinalEnum;

public enum RemoteError implements OrdinalEnum.IntType {
	NoSuchObjectClass(0),
	NoSuchObjectInstance(1),
	AccessDenied(2),
	GetListError(7),
	SetListError(8),
	NoSuchAction(9),
	ProcessingFailure(10),
	InvalidArgumentValue(15),
	InvalidScope(16),
	InvalidObjectInstance(17);
	
	private final int x;
	
	private RemoteError(final int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, RemoteError> map = OrdinalEnum.buildInt(RemoteError.class);
	
	public static final RemoteError valueOf(int x) {
		return map.get(x);
	}
	public final int asInt() {
		return x;
	}

}
