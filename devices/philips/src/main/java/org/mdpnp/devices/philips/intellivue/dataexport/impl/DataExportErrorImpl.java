package org.mdpnp.devices.philips.intellivue.dataexport.impl;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.x73.Util;
import org.mdpnp.x73.rose.RoseError;
import org.mdpnp.x73.rose.RoseOperation;
import org.mdpnp.x73.rose.error.ErrorDetail;
import org.mdpnp.x73.rose.error.ErrorDetailAccessDeniedImpl;
import org.mdpnp.x73.rose.error.ErrorDetailGetListImpl;
import org.mdpnp.x73.rose.error.ErrorDetailInvalidArgumentValueImpl;
import org.mdpnp.x73.rose.error.ErrorDetailInvalidObjectInstanceImpl;
import org.mdpnp.x73.rose.error.ErrorDetailInvalidScopeImpl;
import org.mdpnp.x73.rose.error.ErrorDetailNoSuchActionImpl;
import org.mdpnp.x73.rose.error.ErrorDetailNoSuchObjectClassImpl;
import org.mdpnp.x73.rose.error.ErrorDetailNoSuchObjectInstanceImpl;
import org.mdpnp.x73.rose.error.ErrorDetailProcessingFailureImpl;
import org.mdpnp.x73.rose.error.ErrorDetailSetListImpl;
import org.mdpnp.x73.rose.error.RemoteError;

public class DataExportErrorImpl implements RoseError {

	private int invoke;
	private RemoteError error;
	private ErrorDetail detail;
	
	private static final ErrorDetail buildErrorDetail(RemoteError error) {
		switch(error) {
		case GetListError:
			return new ErrorDetailGetListImpl();
		case SetListError:
			return new ErrorDetailSetListImpl();
		case NoSuchAction:
			return new ErrorDetailNoSuchActionImpl();
		case NoSuchObjectClass:
			return new ErrorDetailNoSuchObjectClassImpl();
		case NoSuchObjectInstance:
			return new ErrorDetailNoSuchObjectInstanceImpl();
		case AccessDenied:
			return new ErrorDetailAccessDeniedImpl();
		case ProcessingFailure:
			return new ErrorDetailProcessingFailureImpl();
		case InvalidArgumentValue:
			return new ErrorDetailInvalidArgumentValueImpl();
		case InvalidScope:
			return new ErrorDetailInvalidScopeImpl();
		case InvalidObjectInstance:
			return new ErrorDetailInvalidObjectInstanceImpl();
		default:
			throw new IllegalArgumentException("Unknown error type:"+error);
		}
	}
	
	@SuppressWarnings("unused")
    @Override
	public void parse(ByteBuffer bb) {
		invoke = Bits.getUnsignedShort(bb);
		error = RemoteError.valueOf(Bits.getUnsignedShort(bb));
		int length = Bits.getUnsignedShort(bb);
		detail = buildErrorDetail(error);
		
		detail.parse(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, invoke);
		Bits.putUnsignedShort(bb, error.asInt());
		Util.PrefixLengthShort.write(bb, detail);
	}
	@Override
	public RemoteError getError() {
		return error;
	}
	@Override
	public int getInvoke() {
		return invoke;
	}
	
	@Override
	public ErrorDetail getErrorDetail() {
		return detail;
	}
	
	@Override
	public String toString() {
		return "[error="+error+",invoke="+invoke+",detail="+detail+"]";
	}
	@Override
	public RoseOperation getRemoteOperation() {
		return RoseOperation.Error;
	}

	@Override
	public void setInvoke(int i) {
		this.invoke = i;
	}
	
}
