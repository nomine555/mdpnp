package org.mdpnp.devices.philips.intellivue.dataexport.impl;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportResult;
import org.mdpnp.devices.philips.intellivue.dataexport.command.CommandFactory;
import org.mdpnp.x73.Util;
import org.mdpnp.x73.cmise.CmiseMessage;
import org.mdpnp.x73.cmise.CmiseOperation;
import org.mdpnp.x73.rose.RoseOperation;

public class DataExportResultImpl implements DataExportResult {
	protected int invokeId;
	protected CmiseOperation commandType;

	
	protected CmiseMessage command;
	
	@Override
	public int getInvoke() {
		return invokeId;
	}

	@Override
	public void setInvoke(int i) {
		this.invokeId = i;
	}
	@Override
	public RoseOperation getRemoteOperation() {
		return RoseOperation.Result;
	}

	public static int peekInvokeId(ByteBuffer bb) {
		return 0xFFFF & bb.getShort(bb.position() + 0);
	}
	
	@SuppressWarnings("unused")
    @Override
	public void parse(ByteBuffer bb) {
		invokeId = Bits.getUnsignedShort(bb);
		commandType = CmiseOperation.valueOf(Bits.getUnsignedShort(bb));
		int length = Bits.getUnsignedShort(bb);
		command = CommandFactory.buildCommand(commandType, true);
		command.setMessage(this);
		command.parse(bb);
	}
	
	@SuppressWarnings("unused")
    public void parseMore(ByteBuffer bb) {
		invokeId = Bits.getUnsignedShort(bb);
		commandType = CmiseOperation.valueOf(Bits.getUnsignedShort(bb));
		int length = Bits.getUnsignedShort(bb);
		command.setMessage(this);
		command.parseMore(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, invokeId);
		Bits.putUnsignedShort(bb, commandType.asInt());
		Util.PrefixLengthShort.write(bb, command);
	}


	@Override
	public CmiseOperation getCommandType() {
		return commandType;
	}

	@Override
	public void setCommand(CmiseMessage dec) {
		this.command = dec;
	}

	@Override
	public CmiseMessage getCommand() {
		return command;
	}

	@Override
	public String toString() {
		return "[invokeId="+invokeId+",commandType="+commandType+",command="+command+"]";
	}
	
	@Override
	public void setCommandType(CmiseOperation commandType) {
		this.commandType = commandType;
	}
	


}
