package org.mdpnp.devices.philips.intellivue.data;
import java.nio.ByteBuffer;

import org.mdpnp.x73.Formatable;

public interface EnumMessage<E> extends Formatable {
	E parse(ByteBuffer bb);
}
