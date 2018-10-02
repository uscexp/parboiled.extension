/*
 * Copyright (C) 2014 - 2018 by haui - all rights reserved
 */
package com.github.uscexp.parboiled.extension.parser;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.errors.GrammarException;
import org.parboiled.support.ParsingResult;

import com.github.uscexp.parboiled.extension.nodes.AstTreeNode;
import com.github.uscexp.parboiled.extension.nodes.treeconstruction.AstTreeNodeBuilder;

/**
 * @author haui
 *
 */
public class Parser {

	private Parser() {
	}

	public static <V> AstTreeNode<V> parseInput(Class<? extends BaseParser<V>> parserClass, Rule rule, String input) {
		return parseInput(parserClass, rule, input, false);
	}

	public static <V> AstTreeNode<V> parseInput(Class<? extends BaseParser<V>> parserClass, Rule rule, String input, boolean removeAstNopTreeNodes) {
		final AstTreeNodeBuilder<V> treeNodeBuilder = new AstTreeNodeBuilder<>(parserClass, removeAstNopTreeNodes, rule);

		ParsingResult<V> result = treeNodeBuilder.run(input);

		if (treeNodeBuilder.getRootNode() == null) {
			String errors = treeNodeBuilder.getParsingErrors();
			String errorMessage = String.format("Error parsing input: %s", errors);
			throw new GrammarException(errorMessage);
		}

		return treeNodeBuilder.getRootNode();
	}
}
