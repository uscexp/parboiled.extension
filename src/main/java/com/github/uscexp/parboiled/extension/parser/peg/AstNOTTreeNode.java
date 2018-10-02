/*
 * Copyright (C) 2014 - 2018 by haui - all rights reserved
 */
package com.github.uscexp.parboiled.extension.parser.peg;

import com.github.uscexp.parboiled.extension.util.IStack;

/**
 * Command implementation for the <code>PegParser</code> rule: NOT.
 */
public class AstNOTTreeNode<V> extends AstPegBaseTreeNode<V> {

	public AstNOTTreeNode(String rule, String value) {
		super(rule, value);
	}

	@Override
	protected void interpretAfterChilds(Long id) throws ReflectiveOperationException {
		super.interpretAfterChilds(id);
		IStack<Object> stack = this.processStore.getTierStack();
		String param = (String) stack.pop();
		stack.push(TEST_NOT + "(" + param + ")");
	}
}
