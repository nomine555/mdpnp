package org.mdpnp.x73.rose.error;

import java.nio.ByteBuffer;

import org.mdpnp.x73.mddl.ManagedObjectIdentifier;

public class ErrorDetailInvalidObjectInstanceImpl implements ErrorDetailInvalidObjectInstance {

	private final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();
	
	@Override
	public void parse(ByteBuffer bb) {
		managedObject.parse(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
		managedObject.format(bb);
	}

	@Override
	public ManagedObjectIdentifier getManagedObject() {
		return managedObject;
	}
	@Override
	public String toString() {
		return ""+managedObject;
	}

}
