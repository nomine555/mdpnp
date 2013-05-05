package org.mdpnp.x73;

/**
 * Any object composable into a ByteBuffer
 * @author jplourde
 *
 */
public interface Formatable {
	void format(java.nio.ByteBuffer bb);
}
