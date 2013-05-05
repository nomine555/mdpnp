package org.mdpnp.x73;

/**
 * Any object decomposable from a ByteBuffer
 * @author jplourde
 *
 */
public interface Parseable {
	void parse(java.nio.ByteBuffer bb);
}
