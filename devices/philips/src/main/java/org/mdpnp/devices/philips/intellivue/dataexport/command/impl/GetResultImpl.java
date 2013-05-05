package org.mdpnp.devices.philips.intellivue.dataexport.command.impl;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.devices.philips.intellivue.dataexport.command.GetResult;
import org.mdpnp.x73.mddl.ManagedObjectIdentifier;
import org.mdpnp.x73.rose.RoseMessage;

public class GetResultImpl implements GetResult {

	private final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();
	private final AttributeValueList attr = new AttributeValueList();
	
	private RoseMessage message;
	
	@Override
	public void parse(ByteBuffer bb) {
		parse(bb, true);
	}
	@Override
	public void parseMore(ByteBuffer bb) {
		parse(bb, false);
	}
	
	private void parse(ByteBuffer bb, boolean clear) {
		managedObject.parse(bb);
		if(clear) {
			attr.reset();
		}
		attr.parse(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
		managedObject.format(bb);
		attr.format(bb);
	}

	@Override
	public ManagedObjectIdentifier getManagedObject() {
		return managedObject;
	}

	@Override
	public AttributeValueList getAttributeList() {
		return attr;
	}
	
	@Override
	public String toString() {
		return "[managedObject="+managedObject+",attrs="+attr+"]";
	}
	@Override
	public void setMessage(RoseMessage message) {
		this.message = message;
	}
	@Override
	public RoseMessage getMessage() {
		return message;
	}

}
