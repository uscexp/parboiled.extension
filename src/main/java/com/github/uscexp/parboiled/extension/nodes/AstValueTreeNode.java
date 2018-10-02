/*
 * Copyright (C) 2014 - 2018 by haui - all rights reserved
 */
package com.github.uscexp.parboiled.extension.nodes;

import com.github.uscexp.parboiled.extension.annotations.AstValue;
import com.github.uscexp.parboiled.extension.interpreter.AstInterpreter;
import com.github.uscexp.parboiled.extension.interpreter.ProcessStore;


/**
 * {@link AstTreeNode} for {@link AstValue} annotated parser rules.
 * will be instanciated automatically from the {@link AstInterpreter}.
 * 
 * @author haui
 *
 */
public class AstValueTreeNode<V> extends AstTreeNode<V> {

	private Class<?> valueType;
	private Class<?> factoryClass;
	private String factoryMethod;
	
	public AstValueTreeNode(String rule, String value, Class<?> valueType, Class<?> factoryClass, String factoryMethod) {
		super(rule, value);
		this.valueType = valueType;
		this.factoryClass = factoryClass;
		this.factoryMethod = factoryMethod;
	}

	@Override
	protected void interpretBeforeChilds(Long id) throws Exception {
	}

	@Override
	protected void interpretAfterChilds(Long id) throws Exception {
		ProcessStore.getInstance(id).getStack().push(convert(value, valueType, factoryClass, factoryMethod));
	}
}
