package org.mdpnp.x73.cmise;

import java.nio.ByteBuffer;

import org.mdpnp.x73.Message;
import org.mdpnp.x73.mddl.ManagedObjectIdentifier;
import org.mdpnp.x73.rose.RoseMessage;

public interface CmiseMessage extends Message {
	void parseMore(ByteBuffer bb);
	void setMessage(RoseMessage message);
	RoseMessage getMessage();
	ManagedObjectIdentifier getManagedObject();
}
