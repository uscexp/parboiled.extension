/*
 * Copyright (C) 2014 by haui - all rights reserved
 */
package com.github.uscexp.parboiled.extension.parser.peg;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.parboiled.Parboiled;

import com.github.uscexp.parboiled.extension.nodes.AstTreeNode;
import com.github.uscexp.parboiled.extension.parser.Parser;

/**
 * @author haui
 *
 */
public class PegParserTest {

	@Test
	public void test() {

		String input = "S <- &(A c) a+ B !(a/b/c)\n" +
				"A <- a A? b\n" +
				"B <- b B? c";

		PegParser parser = Parboiled.createParser(PegParser.class);
		AstTreeNode<String> rootNode = Parser.parseInput(PegParser.class, parser.grammar(), input);

		assertNotNull(rootNode);

		System.out.println("Root node text: " + rootNode.getValue());
	}
}
