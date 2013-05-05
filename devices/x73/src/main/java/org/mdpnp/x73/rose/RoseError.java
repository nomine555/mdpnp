package org.mdpnp.x73.rose;

import org.mdpnp.x73.rose.error.ErrorDetail;
import org.mdpnp.x73.rose.error.RemoteError;

public interface RoseError extends RoseMessage {
	int getInvoke();
	RemoteError getError();
	ErrorDetail getErrorDetail();
}
