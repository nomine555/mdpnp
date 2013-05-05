package org.mdpnp.x73.cmise;

import java.util.Map;

import org.mdpnp.x73.OrdinalEnum;

public enum CmiseOperation implements OrdinalEnum.IntType {
	EventReport(0),
	ConfirmedEventReport(1),
	Get(3),
	Set(4),
	ConfirmedSet(5),
	Action(6),
	ConfirmedAction(7),
	Create(8),
	Delete(9);
	
	private final int x;
	
	private CmiseOperation(final int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, CmiseOperation> map = OrdinalEnum.buildInt(CmiseOperation.class);
	
	public static final CmiseOperation valueOf(int x) {
	    return map.get(x);
	}
	
	public final int asInt() {
	    return x;
	}
}
