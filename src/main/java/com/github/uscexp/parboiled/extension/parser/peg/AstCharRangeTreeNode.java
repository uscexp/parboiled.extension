/*
 * Copyright (C) 2014 - 2018 by haui - all rights reserved
 */
package com.github.uscexp.parboiled.extension.parser.peg;

import com.github.uscexp.parboiled.extension.util.IStack;

/**
 * Command implementation for the <code>PegParser</code> rule: charRange.
 */
public class AstCharRangeTreeNode<V> extends AstPegBaseTreeNode<V> {

	public AstCharRangeTreeNode(String rule, String value) {
		super(rule, value);
	}

	@Override
	protected void interpretAfterChilds(Long id) throws ReflectiveOperationException {
		super.interpretAfterChilds(id);
		IStack<Object> stack = this.processStore.getTierStack();
		String rangeStart = (String) stack.pop();
		String rangeEnd = "";
		if (!stack.isEmpty()) {
			rangeEnd = (String) stack.peek();
		}
		if ((rangeEnd.startsWith("Ch('")) && (this.value.length() > 1)) {
			rangeStart = rangeStart.substring(3, rangeStart.length() - 1);
			rangeEnd = rangeEnd.substring(3, rangeEnd.length() - 1);
			stack.pop();
			stack.push(CHAR_RANGE + "(" + rangeStart + ", " + rangeEnd + ")");
		} else {
			stack.push(rangeStart);
		}
	}
}
