package org.mdpnp.devices.philips.intellivue.dataexport.command;

import java.util.List;

import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.x73.Formatable;
import org.mdpnp.x73.Parseable;
import org.mdpnp.x73.cmise.CmiseMessage;
import org.mdpnp.x73.cmise.ModifyOperator;

public interface Set extends CmiseMessage {
	interface AttributeModEntry extends Parseable, Formatable {
		ModifyOperator getModifyOperator();
        Attribute<?> getAttributeValueAssertion();
	}
	
	List<AttributeModEntry> getList();
//	void add(ModifyOperator modifyOperator, AttributeValueAssertion ava);
	void add(ModifyOperator modifyOperator, Attribute<?> attribute);
	SetResult createResult();
}
