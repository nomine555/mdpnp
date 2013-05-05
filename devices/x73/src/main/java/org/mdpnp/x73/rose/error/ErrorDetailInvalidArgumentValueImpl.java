package org.mdpnp.x73.rose.error;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.x73.mddl.ManagedObjectIdentifier;
import org.mdpnp.x73.mddl.OIDType;

public class ErrorDetailInvalidArgumentValueImpl implements ErrorDetailInvalidArgumentValue {
	private final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();
	private OIDType actionType;
	private int length;
	
	
	@Override
	public void parse(ByteBuffer bb) {
		managedObject.parse(bb);
		actionType = OIDType.parse(bb);
		length = Bits.getUnsignedShort(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
		managedObject.format(bb);
		actionType.format(bb);
		Bits.putUnsignedShort(bb, length);
	}

	@Override
	public ManagedObjectIdentifier getManagedObject() {
		return managedObject;
	}

	@Override
	public OIDType getActionType() {
		return actionType;
	}

	@Override
	public int getLength() {
		return length;
	}
	
	@Override
	public String toString() {
		return "[managedObject="+managedObject+",actionType="+actionType+",length="+length+"]";
	}

}
