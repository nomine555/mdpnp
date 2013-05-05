package org.mdpnp.x73.rose.error;

import java.util.Map;

import org.mdpnp.x73.OrdinalEnum;

public enum ErrorStatus implements OrdinalEnum.IntType {
	AccessDenied(2),
	NoSuchAttribute(5),
	InvalidAttributeValue(6),
	InvalidOperation(24),
	InvalidOperator(25);
	
	private final int x;
	
	private ErrorStatus(final int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, ErrorStatus> map = OrdinalEnum.buildInt(ErrorStatus.class);
	
	public static final ErrorStatus valueOf(int x) {
		return map.get(x);
	}
	
	public final int asInt() {
		return x;
	}
}
