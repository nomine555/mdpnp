package org.mdpnp.x73.rose;

import org.mdpnp.x73.cmise.CmiseMessage;
import org.mdpnp.x73.cmise.CmiseOperation;

public interface RoseInvoke extends RoseMessage {
	CmiseOperation getCommandType();
	void setCommandType(CmiseOperation commandType);
	CmiseMessage getCommand();
	void setCommand(CmiseMessage dec);
}
