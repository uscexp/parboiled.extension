/*
 * Copyright (C) 2014 - 2018 by haui - all rights reserved
 */
package com.github.uscexp.parboiled.extension.parser;

import org.parboiled.support.ParsingResult;

import com.github.uscexp.parboiled.extension.nodes.AstTreeNode;

/**
 * @author haui
 *
 */
public class AstTreeParserResult<V> {

	private ParsingResult<V> parsingResult;

	private AstTreeNode<V> rootNode;

	public AstTreeParserResult(ParsingResult<V> parsingResult, AstTreeNode<V> rootNode) {
		super();
		this.parsingResult = parsingResult;
		this.rootNode = rootNode;
	}

	public ParsingResult<V> getParsingResult() {
		return parsingResult;
	}

	public AstTreeNode<V> getRootNode() {
		return rootNode;
	}
}
