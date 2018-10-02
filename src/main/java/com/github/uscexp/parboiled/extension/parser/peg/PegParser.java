/*
 * Copyright (C) 2014 - 2018 by haui - all rights reserved
 */
package com.github.uscexp.parboiled.extension.parser.peg;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

import com.github.uscexp.parboiled.extension.annotations.AstCommand;

/**
 * @author haui
 */
@BuildParseTree
public class PegParser extends BaseParser<String> {

	public Rule grammar() {
		return Sequence(S(), OneOrMore(definition()), EOI);
	}

	@AstCommand
	public Rule definition() {
		return Sequence(name(), arrow(), expression(), S());
	}

	public Rule expression() {
		return Sequence(sequence(), zerooOrMore());
	}

	@AstCommand
	public Rule sequence() {
		return OneOrMore(prefix());
	}

	public Rule zerooOrMore() {
		return ZeroOrMore(Sequence(OR(), sequence()));
	}

	public Rule prefix() {
		return Sequence(Optional(FirstOf(LOOKAHEAD(), NOT())), suffix());
	}

	public Rule suffix() {
		return Sequence(primary(), Optional(FirstOf(OPTION(), ONEORMORE(), ZEROORMORE())), S());
	}

	public Rule primary() {
		return FirstOf(Sequence(name(), TestNot(arrow())), groupExpr(), literal(), classs(), ANYY());
	}

	@AstCommand
	public Rule name() {
		return Sequence(identifier(), S());
	}

	public Rule identifier() {
		return Sequence(identStart(), ZeroOrMore(identCont()));
	}

	public Rule identCont() {
		return FirstOf(identStart(), CharRange('0', '9'));
	}

	public Rule identStart() {
		return FirstOf(alpha(), Ch('_'));
	}

	public Rule groupExpr() {
		return Sequence(OPEN(), expression(), CLOSE(), S());
	}

	@AstCommand
	public Rule literal() {
		return FirstOf(Sequence(quote(), ZeroOrMore(Sequence(TestNot(quote()), character())), quote(), S()),
				Sequence(doubleQuote(), ZeroOrMore(Sequence(TestNot(doubleQuote()), character())), doubleQuote(), S()));
	}

	@AstCommand
	public Rule classs() {
		return Sequence(SQUAREOPEN(), ZeroOrMore(Sequence(TestNot(SQUARECLOSE()), charRange())), SQUARECLOSE(), S());
	}

	@AstCommand
	public Rule charRange() {
		return FirstOf(Sequence(character(), Ch('-'), character()), character());
	}

	@AstCommand
	public Rule character() {
		return FirstOf(Sequence(backSlash(),
				FirstOf(quote(), doubleQuote(), backQuote(), backSlash(), AnyOf("nrt"),
						Sequence(CharRange('0', '2'), CharRange('0', '7'), CharRange('0', '7')),
						Sequence(CharRange('0', '7'), Optional(CharRange('0', '7'))))),
				Sequence(TestNot(backSlash()), ANY));
	}

	public Rule backSlash() {
		return Ch('\\');
	}

	public Rule quote() {
		return Ch('\'');
	}

	public Rule doubleQuote() {
		return Ch('\"');
	}

	public Rule backQuote() {
		return Ch('`');
	}

	// Terminals

	public Rule arrow() {
		return Sequence(String("<-"), S());
	}

	@AstCommand
	public Rule OR() {
		return Sequence(Ch('/'), S());
	}

	@AstCommand
	public Rule LOOKAHEAD() {
		return Sequence(Ch('&'), S());
	}

	@AstCommand
	public Rule NOT() {
		return Sequence(Ch('!'), S());
	}

	@AstCommand
	public Rule OPTION() {
		return Sequence(Ch('?'), S());
	}

	@AstCommand
	public Rule ZEROORMORE() {
		return Sequence(Ch('*'), S());
	}

	@AstCommand
	public Rule ONEORMORE() {
		return Sequence(Ch('+'), S());
	}

	@AstCommand
	public Rule OPEN() {
		return Sequence(Ch('('), S());
	}

	@AstCommand
	public Rule CLOSE() {
		return Sequence(Ch(')'), S());
	}

	public Rule SQUAREOPEN() {
		return Ch('[');
	}

	public Rule SQUARECLOSE() {
		return Ch(']');
	}

	@AstCommand
	public Rule ANYY() {
		return Sequence(Ch('.'), S());
	}

	// blanks

	public Rule EOL() {
		return FirstOf(String("\r\n"), Ch('\n'), Ch('\r'));
	}

	public Rule comment() {
		return Sequence(String("#"), ZeroOrMore(Sequence(TestNot(EOL()), ANY)), FirstOf(EOL(), EOI));
	}

	public Rule S() {
		return ZeroOrMore(FirstOf(Ch(' '), Ch('\t'), EOL(), comment()));
	}

	/**
	 * ALPHA as defined by RFC 5234, appendix B, section 1: ASCII letters
	 *
	 * <p>
	 * Therefore a-z, A-Z.
	 * </p>
	 *
	 * @return a rule
	 */
	public Rule alpha() {
		return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'));
	}
}
