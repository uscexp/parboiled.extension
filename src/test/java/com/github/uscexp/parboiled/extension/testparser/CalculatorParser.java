/*
 * Copyright (C) 2014 - 2018 by haui - all rights reserved
 */
package com.github.uscexp.parboiled.extension.testparser;

import org.parboiled.BaseParser;
import org.parboiled.Rule;

import com.github.uscexp.parboiled.extension.annotations.AstCommand;
import com.github.uscexp.parboiled.extension.annotations.AstValue;

/**
 * @author haui
 *
 */
public class CalculatorParser extends BaseParser<Double> {

	@AstCommand(classname = "com.github.uscexp.parboiled.extension.testparser.AstResultTreeNode")
	public Rule inputLine() {
		return Sequence(expression(), EOI);
	}

	@AstCommand
	protected Rule expression() {
		return Sequence(term(), ZeroOrMore(FirstOf("+ ", "- "), term()));
	}

	@AstCommand
	protected Rule term() {
		return Sequence(factor(), ZeroOrMore(FirstOf("* ", "/ "), factor()));
	}

	@AstCommand
	protected Rule factor() {
		return Sequence(atom(), ZeroOrMore(FirstOf("^ ", atom())));
	}

	@AstValue(valueType = String.class, factoryClass = String.class) // only for testing, not necessary
	protected Rule atom() {
		return FirstOf(number(), squareRoot(), parens());
	}

	@AstValue(valueType = String.class) // only for testing, not necessary
	protected Rule parens() {
		return Sequence('(', expression(), ')');
	}

	@AstCommand
	protected Rule squareRoot() {
		return Sequence("SQRT ", parens());
	}

	@AstValue(valueType = Double.class, factoryClass = Double.class, factoryMethod = "valueOf")
	protected Rule number() {
		return Sequence(

				// we use another Sequence in the "Number" Sequence so we can easily access the input text matched
				// by the three enclosed rules with "match()" or "matchOrDefault()"
				Sequence(Optional('-'), OneOrMore(digit()), Optional('.', OneOrMore(digit()))), whiteSpace());
	}

	protected Rule whiteSpace() {
		return ZeroOrMore(AnyOf(" \t\f"));
	}

	// we redefine the rule creation for string literals to automatically match trailing whitespace if the string
	// literal ends with a space character, this way we don't have to insert extra whitespace() rules after each
	// character or string literal
	@Override
	protected Rule fromStringLiteral(String string) {
		return string.endsWith(" ") ? Sequence(String(string.substring(0, string.length() - 1)), whiteSpace()) : String(string);
	}

	/**
	 * DIGIT as defined by RFC 5234, appendix B, section 1 (0 to 9)
	 *
	 * @return a rule
	 */
	public Rule digit() {
		return CharRange('0', '9');
	}
}
