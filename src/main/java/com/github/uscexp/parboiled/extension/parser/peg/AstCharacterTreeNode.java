/*
 * Copyright (C) 2014 - 2018 by haui - all rights reserved
 */
package com.github.uscexp.parboiled.extension.parser.peg;

/**
 * Command implementation for the <code>PegParser</code> rule: character.
 */
public class AstCharacterTreeNode<V> extends AstPegBaseTreeNode<V> {

	public AstCharacterTreeNode(String rule, String value) {
		super(rule, value);
	}

	@Override
	protected void interpretAfterChilds(Long id) throws ReflectiveOperationException {
		super.interpretAfterChilds(id);
		if ((this.value != null) && (!this.value.isEmpty())) {
			this.processStore.getTierStack().push(CH + "('" + this.value + "')");
		}
	}
}
