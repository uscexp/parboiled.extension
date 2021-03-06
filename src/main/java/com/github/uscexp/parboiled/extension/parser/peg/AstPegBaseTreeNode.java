/*
 * Copyright (C) 2014 - 2018 by haui - all rights reserved
 */
package com.github.uscexp.parboiled.extension.parser.peg;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.parboiled.BaseParser;

import com.github.uscexp.parboiled.extension.codegenerator.PegParserGenerator;
import com.github.uscexp.parboiled.extension.codegenerator.ReservedJavaWords;
import com.github.uscexp.parboiled.extension.interpreter.ProcessStore;
import com.github.uscexp.parboiled.extension.nodes.AstCommandTreeNode;
import com.github.uscexp.parboiled.extension.util.IStack;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;

public class AstPegBaseTreeNode<V> extends AstCommandTreeNode<V> {
	protected static final String ZERO_OR_MORE = "ZeroOrMore";
	protected static final String ONE_OR_MORE = "OneOrMore";
	protected static final String OPTIONAL = "Optional";
	protected static final String FIRST_OF = "FirstOf";
	protected static final String SEQUENCE = "Sequence";
	protected static final String TEST_NOT = "TestNot";
	protected static final String TEST = "Test";
	protected static final String STRING = "String";
	protected static final String CHAR_RANGE = "CharRange";
	protected static final String CH = "Ch";
	protected static final String ANY = "ANY";

	protected ProcessStore<String> processStore;
	protected ProcessStore<String> openProcessStore;
	protected ProcessStore<String> closeProcessStore;
	protected JCodeModel codeModel;
	protected JDefinedClass definedClass;
	private List<Field> constants = new ArrayList<>();
	private Map<String, String> methodNameMap = new HashMap<>();
	private Map<String, Boolean> existenceMap = new HashMap<>();
	protected boolean tearedUp = false;

	public AstPegBaseTreeNode(String rule, String value) {
		super(rule, value);
		Field[] fields = BaseParser.class.getDeclaredFields();
		Field[] arrayOfField1;
		int j = (arrayOfField1 = fields).length;
		for (int i = 0; i < j; i++) {
			Field field = arrayOfField1[i];
			if ((Modifier.isPublic(field.getModifiers())) && (Modifier.isFinal(field.getModifiers())) && (Modifier.isStatic(field.getModifiers()))) {
				this.constants.add(field);
			}
		}
	}

	@Override
	protected void interpretAfterChilds(Long id) throws ReflectiveOperationException {
		this.codeModel = ((JCodeModel) this.processStore.getVariable("codeModel"));
		this.definedClass = ((JDefinedClass) this.processStore.getVariable("definedClass"));
	}

	protected String checkPostponedAction(String bodyString) {
		String openMethod = "";
		IStack<Object> openStack = this.openProcessStore.getTierStack();
		if (!openStack.isEmpty()) {
			openMethod = (String) openStack.pop();
		}
		if ((openMethod.equals(ZERO_OR_MORE)) || (openMethod.equals(ONE_OR_MORE)) || (openMethod.equals(OPTIONAL)) || (openMethod.equals(FIRST_OF))) {
			bodyString = openMethod + "(" + bodyString + ")";
		} else if (openMethod != null && !openMethod.isEmpty()) {
			openStack.push(openMethod);
		}
		return bodyString;
	}

	protected String getMethodName(String expressionName) {
		String methodName = this.methodNameMap.get(expressionName);
		if (methodName == null) {
			if (expressionName.equals(expressionName.toUpperCase())) {
				methodName = expressionName;
			} else {
				methodName = expressionName.substring(0, 1).toLowerCase() + expressionName.substring(1);
			}
			methodName = ReservedJavaWords.getUnreservedWord(methodName);
			this.methodNameMap.put(expressionName, methodName);
		}
		return methodName;
	}

	protected boolean checkExistence(String methodName) {
		Boolean result = this.existenceMap.get(methodName);
		if (result == null) {
			result = Boolean.valueOf(false);
			for (Field constant : this.constants) {
				if (constant.getName().equals(methodName)) {
					result = Boolean.valueOf(true);
					break;
				}
			}
			this.existenceMap.put(methodName, result);
		}
		return result.booleanValue();
	}

	@Override
	protected void interpretBeforeChilds(Long id) throws ReflectiveOperationException {
		this.processStore = ProcessStore.getInstance(id);
		this.openProcessStore = ProcessStore.getInstance(Long.valueOf(id.longValue() + PegParserGenerator.OPEN));
	}
}
