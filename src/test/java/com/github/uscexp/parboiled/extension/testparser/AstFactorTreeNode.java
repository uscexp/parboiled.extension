/*
 * Copyright (C) 2014 - 2018 by haui - all rights reserved
 */
package com.github.uscexp.parboiled.extension.testparser;

import com.github.uscexp.parboiled.extension.interpreter.ProcessStore;
import com.github.uscexp.parboiled.extension.nodes.AstCommandTreeNode;
import com.github.uscexp.parboiled.extension.util.IStack;

/**
 * @author haui
 *
 */
public class AstFactorTreeNode extends AstCommandTreeNode<Double> {

	public AstFactorTreeNode(String rule, String value) {
		super(rule, value);
	}

	@Override
	protected void interpretBeforeChilds(Long id) throws Exception {
	}

	@Override
	protected void interpretAfterChilds(Long id) throws Exception {
		if(value.indexOf('^') > -1) {
			IStack<Object> stack = ProcessStore.getInstance(id).getStack();
			Double right = (Double) StackAccessUtil.pop(stack, Double.class);
			if(!(stack.peek() instanceof Double))
				stack.pop();
			Double left = (Double) StackAccessUtil.pop(stack, Double.class);
			double result = 0;
			result = Math.pow(left, right);
			stack.push(result);
		}
	}

}
