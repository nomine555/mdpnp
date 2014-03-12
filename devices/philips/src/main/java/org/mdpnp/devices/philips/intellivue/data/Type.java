/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class Type implements Value {
    private NomPartition nomPartition = NomPartition.Object;

    private OIDType oidType = OIDType.lookup(0);

    public Type() {
    }

    public Type(ObjectClass objClass) {
        this(NomPartition.Object, OIDType.lookup(objClass.asInt()));
    }

    public Type(NomPartition nomPartition, OIDType oidType) {
        this.nomPartition = nomPartition;
        this.oidType = oidType;
    }

    @Override
    public void parse(ByteBuffer bb) {
        this.nomPartition = NomPartition.valueOf(Bits.getUnsignedShort(bb));
        this.oidType = OIDType.lookup(Bits.getUnsignedShort(bb));
    }

    @Override
    public void format(ByteBuffer bb) {
        bb.putShort(nomPartition.asShort());
        oidType.format(bb);

    }

    public NomPartition getNomPartition() {
        return nomPartition;
    }

    public OIDType getOidType() {
        return oidType;
    }

    public void setNomPartition(NomPartition nomPartition) {
        this.nomPartition = nomPartition;
    }

    public void setOidType(OIDType oidType) {
        this.oidType = oidType;
    }

    @Override
    public java.lang.String toString() {

        return "[nomPartition=" + nomPartition + ",oidType=" + oidType + "]";
    }

}
