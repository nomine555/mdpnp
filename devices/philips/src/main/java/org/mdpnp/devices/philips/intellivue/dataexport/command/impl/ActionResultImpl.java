package org.mdpnp.devices.philips.intellivue.dataexport.command.impl;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.action.ActionFactory;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportAction;
import org.mdpnp.devices.philips.intellivue.dataexport.command.ActionResult;
import org.mdpnp.x73.Util;
import org.mdpnp.x73.mddl.ManagedObjectIdentifier;
import org.mdpnp.x73.mddl.OIDType;
import org.mdpnp.x73.mddl.ObjectClass;
import org.mdpnp.x73.rose.RoseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionResultImpl implements ActionResult {
	protected final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();
	protected OIDType actionType;
	
	protected RoseMessage message;
	protected DataExportAction action;
	
	private static final Logger log = LoggerFactory.getLogger(ActionResultImpl.class);
	
	@Override
	public void parse(ByteBuffer bb) {
		managedObject.parse(bb);
		actionType = OIDType.parse(bb);
		int length = Bits.getUnsignedShort(bb);
		action = ActionFactory.buildAction(actionType, false);
		if(null == action) {
			log.warn("Unknown action type:"+actionType);
			
			bb.position(bb.position()+length);
		} else {
			action.setAction(this);
			action.parse(bb);
			
		}
	}
	
	@Override
	public void parseMore(ByteBuffer bb) {
		managedObject.parse(bb);
		actionType = OIDType.parse(bb);
		int length = Bits.getUnsignedShort(bb);
		if(null == action) {
			log.warn("Unknown action type:"+actionType);
			bb.position(bb.position()+length);
		} else {
			action.parseMore(bb);
			
		}
	}

	@Override
	public void format(ByteBuffer bb) {
		managedObject.format(bb);
		actionType.format(bb);
		
		Util.PrefixLengthShort.write(bb, action);
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
	public String toString() {
		java.lang.String at = ObjectClass.valueOf(actionType.getType()) == null ? actionType.toString() : ObjectClass.valueOf(actionType.getType()).toString();
		return "[managedObject="+managedObject+",actionType="+at+",action="+action+"]";
	}
	
	@Override
	public RoseMessage getMessage() {
		return message;
	}
	
	@Override
	public void setMessage(RoseMessage message) {
		this.message = message;
	}
	
	@Override
	public void setActionType(OIDType type) {
		this.actionType = type;
	}

	@Override
	public DataExportAction getAction() {
		return action;
	}
	@Override
	public void setAction(DataExportAction action) {
		this.action = action;
	}
}
