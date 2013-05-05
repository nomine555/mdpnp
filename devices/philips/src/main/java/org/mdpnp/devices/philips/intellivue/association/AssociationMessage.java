package org.mdpnp.devices.philips.intellivue.association;

import java.nio.ByteBuffer;

import org.mdpnp.x73.Message;

public interface AssociationMessage extends Message {
	AssociationMessageType getType();
	byte[] getPresentationHeader();
	byte[] getPresentationTrailer();
	boolean advancePastPresentationHeader(ByteBuffer bb);
}
