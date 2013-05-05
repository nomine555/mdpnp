package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.x73.Formatable;
import org.mdpnp.x73.Parseable;
import org.mdpnp.x73.Util;
import org.mdpnp.x73.mddl.AttributeId;
import org.mdpnp.x73.mddl.OIDType;

public class AttributeValueList implements Parseable, Formatable, Util.PrefixLengthShort.Builder<Attribute<?>> {
	private final List<Attribute<?>> list = new java.util.ArrayList<Attribute<?>>();
	private final List<AttributeValueAssertion> recycle = new java.util.ArrayList<AttributeValueAssertion>();
	private final Map<OIDType, Attribute<?>> map = new java.util.HashMap<OIDType, Attribute<?>>();
	
	@Override
	public Attribute<?> build() {
		return newAVA();
	}
	
	private AttributeValueAssertion newAVA() {
		if(recycle.isEmpty()) {
			return new AttributeValueAssertion();
		} else {
			return recycle.remove(0);
		}
	}
	
	public void reset() {
		for(Formatable f : list) {
			if(f instanceof AttributeValueAssertion) {
				recycle.add((AttributeValueAssertion) f);
			}
		}
		list.clear();
		map.clear();
	}
	
	@Override
	public void format(ByteBuffer bb) {
		Util.PrefixLengthShort.write(bb, list);
	}
	
	
	
	@Override
	public void parse(ByteBuffer bb) {
		parse(bb, true);
	}
	
	public void parseMore(ByteBuffer bb) {
		parse(bb, false);
	}
	
	private void parse(ByteBuffer bb, boolean clear) {
		Util.PrefixLengthShort.read(bb, list, clear, this);
		
		
		for(Attribute<?> a : list) {
			map.put(a.getOid(), a);
		}

	}
	
	public void put(OIDType type, Attribute<?> a) {
		list.add(a);
		map.put(type, a);


		// TODO this is ugly
//		ByteBuffer bb = ByteBuffer.allocate(5000);
//		bb.order(ByteOrder.BIG_ENDIAN);
//		f.format(bb);
//		bb.flip();
//		byte[] buf = new byte[bb.remaining()];
//		bb.get(buf);
//		add(type, buf);
	}
	
	public boolean remove(OIDType type) {
		Attribute<?> a = map.remove(type);
		if(null != a) {
			list.remove(a);
			if(a instanceof AttributeValueAssertion) {
				recycle.add((AttributeValueAssertion)a);
			}
			return true;
		} else {
			return false;
		}
	}
	
	public Attribute<?> get(OIDType type) {
		return map.get(type);
	}
	
	public boolean get(Attribute<?> p) {
		return get(p.getOid(), p);
	}
	
	public boolean get(OIDType type, Attribute<?> p) {
		Attribute<?> a = map.get(type);
		if(a == null) {
			return false;
		} else if(a instanceof AttributeValueAssertion) {
			if(p == null) {
				return false;
			} else {
				int idx = list.indexOf(a);
				ByteBuffer bb = ByteBuffer.wrap(((AttributeValueAssertion)a).getValue().getArray()); 
				bb.order(ByteOrder.BIG_ENDIAN);
				p.parse(bb);
				list.set(idx, p);
				map.put(type, p);
				return true;
			}
		} else {
			return false;
		}
	}
	
	public void add(OIDType type, byte[] value) {
		AttributeValueAssertion ava = newAVA();
		ava.setValue(value);
		ava.setOid(type);
		add(ava);
	}
	
	public void add(AttributeValueAssertion ava) {
		list.add(ava);
		map.put(ava.getOid(), ava);
	}
	
	public void add(Attribute<?> attr) {
		put(attr.getOid(), attr);
	}
	
	public Collection<Attribute<?>> getList() {
		return list;
	}
	public Map<OIDType, Attribute<?>> getMap() {
		return map;
	}
	
	@Override
	public java.lang.String toString() {
		StringBuilder sb = new StringBuilder("{");
		for(Attribute<?> a : list) {
//			Attribute a = AttributeFactory.getAttribute(ava.getOid());
			if(null == a) {
				sb.append(a).append(",");
			} else {
				sb.append(AttributeId.valueOf(a.getOid().getType()));
				sb.append("=").append(a);
				sb.append(",");
			}
		}
		if(sb.length() > 1) {
			sb.delete(sb.length() - 1, sb.length());
		}
		sb.append("}");
		return sb.toString();
	}
	
	

}
