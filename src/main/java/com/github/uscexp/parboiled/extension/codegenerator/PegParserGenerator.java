/*
 * Copyright (C) 2014 - 2018 by haui - all rights reserved
 */
package com.github.uscexp.parboiled.extension.codegenerator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.common.Preconditions;

import com.github.uscexp.parboiled.extension.exception.AstInterpreterException;
import com.github.uscexp.parboiled.extension.exception.PegParserGeneratorException;
import com.github.uscexp.parboiled.extension.interpreter.AstInterpreter;
import com.github.uscexp.parboiled.extension.interpreter.ProcessStore;
import com.github.uscexp.parboiled.extension.nodes.AstTreeNode;
import com.github.uscexp.parboiled.extension.parser.Parser;
import com.github.uscexp.parboiled.extension.parser.peg.PegParser;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;

/**
 * The {@link PegParserGenerator} generates a {@link BaseParser} extended parser
 * java class file, from a given Parser Expression Grammar (PEG) file. The
 * allowed PEG syntax is based on Bryan Ford's definitions
 * (http://bford.info/pub/lang/peg).
 *
 * @author haui
 */
public class PegParserGenerator {

	private static Logger logger = Logger.getLogger(PegParserGenerator.class.getName());

	public static final long CLOSE = "close".hashCode();
	public static final long OPEN = "open".hashCode();
	public static final String CODE_MODEL = "codeModel";
	public static final String DEFINED_CLASS = "definedClass";
	private File file = null;

	private PegParser parser;
	private AstTreeNode<String> rootNode;
	private String parserClassString;
	private String genericTypeName;

	private AstInterpreter<String> astInterpreter;

	public void generateParserFromFile(String parserClassString, String genericTypeName, String sourceOutputPath, String pegInputPath,
			Charset encoding)
			throws PegParserGeneratorException, AstInterpreterException {
		String input = null;

		if (encoding == null) {
			encoding = Charset.defaultCharset();
		}

		file = new File(pegInputPath);

		if (!file.exists()) {
			throw new PegParserGeneratorException(String.format("File %s does not exist!", pegInputPath));
		}

		try {
			byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
			input = new String(encoded, encoding);
		} catch (IOException e) {
			throw new PegParserGeneratorException(String.format("File %s couldn't be read!", pegInputPath), e);
		}

		generateParserFromString(parserClassString, genericTypeName, sourceOutputPath, input);
	}

	public void generateParserFromString(String parserClassString, String genericTypeName, String sourceOutputPath, String pegInput)
			throws PegParserGeneratorException, AstInterpreterException {
		Preconditions.checkNotNull(pegInput);

		parser = Parboiled.createParser(PegParser.class);

		rootNode = Parser.parseInput(PegParser.class, parser.grammar(), pegInput, true);

		generateParserClassFile(parserClassString, genericTypeName, sourceOutputPath);

	}

	private void generateParserClassFile(String parserClassString, String genericTypeName, String sourceOutputPath)
			throws AstInterpreterException, PegParserGeneratorException {
		astInterpreter = new AstInterpreter<>();

		Long id = new Date().getTime();
		Long idOpen = id + OPEN;
		try {
			this.parserClassString = parserClassString;
			this.genericTypeName = genericTypeName;
			generateCode(sourceOutputPath, id, idOpen);
		} catch (JClassAlreadyExistsException | IOException e) {
			throw new PegParserGeneratorException("PEG parser generator unexpected error", e);
		} finally {
			astInterpreter.cleanUp(id);
			astInterpreter.cleanUp(idOpen);
		}
	}

	private void generateCode(String sourceOutputPath, Long id, Long idOpen)
			throws JClassAlreadyExistsException, AstInterpreterException, IOException {
		JCodeModel codeModel = new JCodeModel();
		ProcessStore<String> processStore = ProcessStore.getInstance(id);
		ProcessStore<Object> openProcessStore = ProcessStore.getInstance(idOpen);

		JDefinedClass definedClass = prepareDefinedClass(codeModel);
		// set the result object
		processStore.setNewVariable(CODE_MODEL, codeModel);
		processStore.setNewVariable(DEFINED_CLASS, definedClass);
		processStore.tierOneUp(true);
		openProcessStore.tierOneUp(true);

		astInterpreter.interpretBackwardOrder(parser.getClass(), rootNode, id);

		codeModel.build(new File(sourceOutputPath));
	}

	private JDefinedClass prepareDefinedClass(JCodeModel codeModel)
			throws JClassAlreadyExistsException {
		JDefinedClass definedClass = codeModel._class(parserClassString);
		JClass genericType = getGenericType(codeModel);
		JClass superClass = codeModel.ref(BaseParser.class).narrow(genericType);

		definedClass._extends(superClass);
		JDocComment javadoc = definedClass.javadoc();
		String filename = (file != null) ? ("'" + file.getName() + "'") : "";
		String lastWord = (file != null) ? "input file" : "input string";
		String jdComment = String.format("Generated {@link BaseParser} implementation form %s PEG %s.", filename, lastWord);
		javadoc.add(jdComment);
		javadoc.add("\n\n@author PegParserGenerator");

		return definedClass;
	}

	private JClass getGenericType(JCodeModel codeModel) {
		Class<?> genericClass = null;
		try {
			genericClass = Class.forName(genericTypeName);
		} catch (ClassNotFoundException e) {
			genericClass = null;
		}
		JClass genericType = codeModel.ref(genericTypeName);
		if (genericClass == null) {
			genericType = codeModel.ref(genericTypeName);
		} else {
			genericType = codeModel.ref(genericClass);
		}
		return genericType;
	}

	public static void main(String[] args) {
		try {
			PegParserGenerator pegParserGenerator = new PegParserGenerator();

			String parserClassString = args[0];
			String genericTypeName = args[1];
			String sourceOutputPath = args[2];
			String pegFileInputPath = args[3];
			Charset inputFileEncoding = null;

			if (args.length == 5) {
				inputFileEncoding = Charset.forName(args[4]);
			}
			pegParserGenerator.generateParserFromFile(parserClassString, genericTypeName, sourceOutputPath, pegFileInputPath,
					inputFileEncoding);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Unexpected error occured", e);

			logger.log(Level.SEVERE,
					"Syntax: java -cp <dependencies> com.github.uscexp.parboiled.extension.codegenerator.PegParserGenerator parserClassString genericTypeName sourceOutputPath pegFileInputPath <inputFileEncoding>");
		}
	}

}
