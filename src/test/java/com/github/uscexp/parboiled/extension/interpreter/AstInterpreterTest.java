/*
 * Copyright (C) 2014 by haui - all rights reserved
 */
package com.github.uscexp.parboiled.extension.interpreter;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.errors.GrammarException;

import com.github.uscexp.parboiled.extension.nodes.AstTreeNode;
import com.github.uscexp.parboiled.extension.parser.Parser;
import com.github.uscexp.parboiled.extension.testparser.CalculatorParser;
import com.github.uscexp.parboiled.extension.testparser.subclass.ExtendedCalculatorParser;
import com.github.uscexp.parboiled.extension.util.AstTreeUtil;

/**
 * @author haui
 *
 */
public class AstInterpreterTest {

	@Test
	public void testHappyCase() throws Exception {
		String input = "2 * 2 + 2 * 3";
		CalculatorParser calculatorParser = Parboiled.createParser(CalculatorParser.class);

		AstTreeNode<Double> rootNode = Parser.parseInput(CalculatorParser.class, calculatorParser.inputLine(), input);

		AstInterpreter<Double> interpreter = new AstInterpreter<>();
		Long id = new Date().getTime();
		interpreter.interpretBackwardOrder(CalculatorParser.class, rootNode, id);
		Object result = ProcessStore.getInstance(id).getStack().peek();

		assertEquals(new Double(10), result);
		interpreter.cleanUp(id);
		System.out.println("-------------------");
	}

	@Test
	public void testHappyCaseRemoveAstNopTreeNodes() throws Exception {
		String input = "2 * 2 + 2 * 3";
		CalculatorParser calculatorParser = Parboiled.createParser(CalculatorParser.class);

		AstTreeNode<Double> rootNode = Parser.parseInput(CalculatorParser.class, calculatorParser.inputLine(), input, true);

		AstInterpreter<Double> interpreter = new AstInterpreter<>();
		Long id = new Date().getTime();
		interpreter.interpretBackwardOrder(calculatorParser.getClass(), rootNode, id);
		Object result = ProcessStore.getInstance(id).getStack().peek();

		assertEquals(new Double(10), result);

		AstTreeUtil.printAstTree(rootNode, System.out);
		interpreter.cleanUp(id);
		System.out.println("-------------------");
	}

	@Test
	public void testSqrt() throws Exception {
		String input = "SQRT(4)";
		CalculatorParser calculatorParser = Parboiled.createParser(CalculatorParser.class);

		AstTreeNode<Double> rootNode = Parser.parseInput(CalculatorParser.class, calculatorParser.inputLine(), input);

		AstInterpreter<Double> interpreter = new AstInterpreter<>();
		Long id = new Date().getTime();
		interpreter.interpretBackwardOrder(calculatorParser.getClass(), rootNode, id);
		Object result = ProcessStore.getInstance(id).getStack().peek();

		assertEquals(new Double(2), result);
		interpreter.cleanUp(id);
		System.out.println("-------------------");
	}

	@Test(expected = GrammarException.class)
	public void testWrongSyntax() throws Exception {
		String input = ")2(2+2*3";
		CalculatorParser calculatorParser = Parboiled.createParser(CalculatorParser.class);

		AstTreeNode<Double> rootNode = Parser.parseInput(CalculatorParser.class, calculatorParser.inputLine(), input);
		System.out.println("-------------------");

		AstInterpreter<Double> interpreter = new AstInterpreter<>();
		Long id = new Date().getTime();
		interpreter.interpretBackwardOrder(calculatorParser.getClass(), rootNode, id);
		interpreter.cleanUp(id);
	}

	@Test
	public void testExtendedParserHappyCase() throws Exception {
		String input = "2 * 2 + 2 * 3";
		ExtendedCalculatorParser calculatorParser = Parboiled.createParser(ExtendedCalculatorParser.class);

		AstTreeNode<Double> rootNode = Parser.parseInput(CalculatorParser.class, calculatorParser.inputLine(), input);

		AstInterpreter<Double> interpreter = new AstInterpreter<>();
		Long id = new Date().getTime();
		interpreter.interpretBackwardOrder(ExtendedCalculatorParser.class, rootNode, id);
		Object result = ProcessStore.getInstance(id).getStack().peek();

		assertEquals(new Double(10), result);
		interpreter.cleanUp(id);
		System.out.println("-------------------");
	}
}
