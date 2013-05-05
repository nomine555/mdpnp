package org.mdpnp.x73.rose.error;

import java.nio.ByteBuffer;

import org.mdpnp.x73.mddl.OIDType;

public class ErrorDetailNoSuchObjectClassImpl implements ErrorDetailNoSuchObjectClass {

	private OIDType objectClass;
	
	@Override
	public OIDType getObjectClass() {
		return objectClass;
	}

	@Override
	public void parse(ByteBuffer bb) {
		objectClass = OIDType.parse(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
		objectClass.format(bb);
		
	}
	
	@Override
	public String toString() {
		return ""+objectClass;
	}
	
}
