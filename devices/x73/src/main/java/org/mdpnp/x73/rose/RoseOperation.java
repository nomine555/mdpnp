package org.mdpnp.x73.rose;

import java.util.Map;
import org.mdpnp.x73.OrdinalEnum;

/**
 * ROSE Remote Operations
 * ROSEapdus from 11073-20101-2004 E.2.1 ROSE
 * @author jplourde
 *
 */
public enum RoseOperation implements OrdinalEnum.IntType {
	Invoke(1),
	Result(2),
	Error(3),
	Reject(4),
	LinkedInvoke(5);
	
	private final int x;
	
	private RoseOperation(final int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, RoseOperation> map = OrdinalEnum.buildInt(RoseOperation.class);
	
	public static final RoseOperation valueOf(int x) {
		return map.get(x);
	}
	
	public final int asInt() {
	    return x;
	}
}
