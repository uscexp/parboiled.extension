/*
 * Copyright (C) 2014 - 2018 by haui - all rights reserved
 */
package com.github.uscexp.parboiled.extension.testparser.subclass;

import org.parboiled.Rule;

import com.github.uscexp.parboiled.extension.testparser.CalculatorParser;

/**
 * @author haui
 *
 */
public class ExtendedCalculatorParser extends CalculatorParser {

	public Rule extendeInputLine() {
		return Sequence(inputLine(), true);
	}
}
