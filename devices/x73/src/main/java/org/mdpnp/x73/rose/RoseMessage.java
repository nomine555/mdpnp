package org.mdpnp.x73.rose;

import org.mdpnp.x73.Message;

public interface RoseMessage extends Message {
	int getInvoke();
	void setInvoke(int i);
	RoseOperation getRemoteOperation();
}
