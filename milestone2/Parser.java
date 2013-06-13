package milestone2;

import java.io.File;
import java.io.FileReader;

import compileTable.ByteReader;
import compileTable.ClassFile;

import tokens.*;
import scanner.LookForwardReader;
import scanner.LookForwardScanner;
import scanner.Scanner;
import symbolTable.Field;
import symbolTable.Method;
import symbolTable.ParameterList;
import symbolTable.StackMapTableObject;
import symbolTable.TokenArrayList;
import symbolTable.Type;

import symbolTable.Class;


public class Parser {
	
	private LookForwardScanner lfc;
	
	private final Tokens tks = new Tokens();
	
	private boolean error; 
	
	private final String fileName;
	
	private final String fileDirPath;
	
	private final Class clazz;
	
	//private final Operations operations;
	
	private final ErrorsClass errors;
	
	private final ClassFile classfile;
	
	private int startPos;
	
	public Parser(String filePath) throws Exception{
		this.errors = new ErrorsClass();
		this.lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new FileReader(new File(filePath)))));
		this.startPos = -1;
		this.fileName = new File(filePath).getName();
		this.fileDirPath = new File(filePath).getParent() + "/";
		this.error = false;
		this.parseErrors(false);
		String packageName = this.parsePackage();
		
		this.clazz = new Class(this.fileName.split("\\.")[0], new Class("java/lang/Object", null, "java/lang/"), packageName, this.fileDirPath);//array[array.length-1].split("\\.")[0], new Class("java/lang/Object", null));
		System.out.println(this.clazz.getName());
		this.parseImports();
		this.parseClass();
		this.lfc.closeReader();
		
		//System.out.println(this.clazz.getMethods().get(1).getLocalVariables().size());
		//this.clazz.printImportedCLasses();
		//System.out.println(this.clazz.getFilePath());
		
		//ByteReader br = new ByteReader(this.fileDirPath + "Milestone3.class");
		//boolean k = br.findMethodOrFieldFromClassFile("h","I");
		//System.out.println(k);
		//br.closeReader();
		/*if(!this.error){
			this.error = this.clazz.makeUsedAndImportedClasses();
		}*/
		//this.clazz.getMethods().get(1).makeByteWriter();
		//this.clazz.getMethods().get(1).getByteWriter().printByteArray();
		//System.out.println(this.clazz.getMethods().get(1).calculateMaxStackSize());
		//this.clazz.printFields();
		
		//this.clazz.printImportedCLasses();
		//System.out.println();
		//this.clazz.printUsedClasses();
		//System.out.println();
		//this.clazz.printClassReferences();
		//System.out.println(this.clazz.getClassReferences().get(0).getFields().size());
		//System.out.println(this.clazz.getClassReferences().get(0).getFields().get(0).getSize());
		
		//this.clazz.getMethods().get(1).printParameters();
		//this.clazz.getMethods().get(1).printVariables();
		//this.clazz.printSymbolTable();
		if(!this.error){
			for(int i = 1; i < this.clazz.getMethods().size(); i++){
				this.clazz.getMethods().get(i).makeByteWriter();
			}
			this.classfile = new ClassFile(this.clazz, this.clazz.getName() + ".class");
		}else{
			this.classfile = null;
		}

		//System.out.println(clazz.getMethodsToBeCheckedIfExists().size()+ " SIZE");
		/*for(int i=0; i< clazz.getMethodsToBeCheckedIfExists().size(); i++){
			System.out.print(clazz.getMethodsToBeCheckedIfExists().get(i).getName());
			for(int j=0; j< clazz.getMethodsToBeCheckedIfExists().get(i).getParameterList().getSize(); j++){
				System.out.println(",  Parameters : " + clazz.getMethodsToBeCheckedIfExists().get(i).getParameterList().getParameter(j).getType().getDescriptor());
			}
		}*/
		//this.classfile = null;
	}

/*******************************************************************************************
 *  parsePackage() - parse the the package name
 *  			   - adds in the name of the package in the symboltable
 *******************************************************************************************/
	private String parsePackage() {
		Token t = this.lfc.lookAhead();
		if(t.getToken() == this.tks.PACKAGE){
			this.lfc.readNextToken();
			t = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
			if(t.getToken() == -1){
				this.expected(new Token(tks.SEMICOLON, ";"));
				this.parseErrors(true);
			}else{
				String str = t.getText();
				t = this.expected(new Token(tks.SEMICOLON, ";"));
				if(t.getToken() != -1){
					return str;
				}else{
					this.parseErrors(true);
				}
			}
		}
		return "";
		
	}
	
	
/*******************************************************************************************
 *  parseImports() - parse the all the imports in one file
 *  			   - also makes from imports classes and adds in symboltable as imported classes
 *  			   - checks if the folder and the file are existing
 *******************************************************************************************/
	private void parseImports(){
		File file = new File("");
		this.parseErrors(false);
		Token t = this.lfc.lookAhead();
		boolean b = true;
		while (t.getToken() == this.tks.IMPORT)
		{
			this.lfc.readNextToken();
			Token token = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
			String packageName = token.getText();
			if (token.getToken() == -1){
				token= this.expected(new Token(this.tks.SEMICOLON, ";"));
				this.parseErrors(true);
			}else {
				String importName = "";
				importName = token.getText() + file.separator;
				token = this.expected(new Token(this.tks.DOT, "."));
				if(token.getToken() == -1){
					token= this.expected(new Token(this.tks.SEMICOLON, ";"));
					this.parseErrors(true);
					b = false;
				}else{
					token = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));  
					if(token.getToken() == -1){
						token= this.expected(new Token(this.tks.SEMICOLON, ";"));
						this.parseErrors(true);
						b = false;
					}else
					{
						importName = importName + token.getText();
						t = this.lfc.lookAhead();
						while (t.getToken()!= this.tks.SEMICOLON){
							token = this.expected(new Token(this.tks.DOT, "."));
							if (token.getToken() == -1){
								token= this.expected(new Token(this.tks.SEMICOLON, ";"));
								this.parseErrors(true);
								b = false;
								break;
							}else{
								importName = importName + file.separator;
								token = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
								if(token.getToken() == -1){
									token= this.expected(new Token(this.tks.SEMICOLON, ";"));
									this.parseErrors(true);
									b = false;
									break;
								}
								importName = importName + token.getText();
							}
							t = this.lfc.lookAhead();
						}
						if(b){
							this.expected(new Token(this.tks.SEMICOLON, ";"));
						}
					}
					
					if(this.clazz.isAllreadyContainingClass(importName) && !b){
						this.errors.printIsAlreadyImportedClass(importName);
						this.error = true;
					}else{
						String str = this.clazz.getFilePath() + importName + ".java";
						if(!this.checkDir(packageName)){
							this.errors.printPackageDoesNotExists(token, packageName);
							this.error = true;
						}else if(!this.checkFile(str)){
							this.errors.printFileDoesNotExists(token, importName);
							this.error = true;
						}else{
							String fileNameImport = new File(str).getName();
							String filePathImport = new File(str).getParent() + "\\";
							this.clazz.addImportedClass(new Class(fileNameImport.split("\\.")[0], null, packageName, filePathImport));
							//System.out.println(this.clazz.getImportedArrayClasses().get(0).getName());
						}
					}
					//TODO To make it works for more subfolders 
					
					
				}
			}
			this.parseErrors(false);
			t = this.lfc.lookAhead();
		}
	}
/*******************************************************************************************
 * boolean checkFile(String importName) 
 *  		-
 *******************************************************************************************/
	private boolean checkFile(String importName) {
		String str = importName;
		boolean checkFile = new File(str).isFile();
		return checkFile;
	}
/*******************************************************************************************
 *  boolean checkDir(String packageName)
 *  		-
 *******************************************************************************************/
	private boolean checkDir(String packageName) {
		String str1 = this.clazz.getFilePath() + packageName;
		boolean checkDir = new File(str1).exists();
		return checkDir;
	}

/******  Beginning the file only PUBLIC,PACKAGE OR IMPORT are allowed ************************/
/*******************************************************************************************
 *  parseFirstError() - parse the error at the beginning of the file if there is an error
 *  else continuing with parsing
 *******************************************************************************************/
	private void parseErrors(boolean isAlreadyParsed) {
		Token t = this.lfc.lookAhead();
		if(isAlreadyParsed && ((t.getToken()!=this.tks.IMPORT) && (t.getToken()!=this.tks.PUBLIC) && (t.getToken()!=this.tks.PACKAGE))){
			this.lfc.readNextToken();
		}
		t = this.lfc.lookAhead();
		if((t.getToken()!=this.tks.IMPORT) && (t.getToken()!=this.tks.PUBLIC) && (t.getToken()!=this.tks.PACKAGE)){
			this.removeErrorTokens();
		}
	}

/*******************************************************************************************
 *  removeErrorTokens() - removing the errors and prints the error
 *******************************************************************************************/
	private void removeErrorTokens() {
		Token t = this.lfc.lookAhead();
		while((t.getToken()!=this.tks.IMPORT) && (t.getToken()!=this.tks.PUBLIC) && (t.getToken()!=this.tks.PACKAGE)){
			 if(t.getToken() == this.tks.SEMICOLON){
				this.lfc.readNextToken();
			 }else if(t.getToken() == this.tks.EOF){
					System.exit(0);
			 }else{
				t = this.lfc.readNextToken();
				this.printErrorBeforeClassDef(t);
				if(this.error != true){
					this.error = true;
				}
			 }
			 t = this.lfc.lookAhead();
		}
		
	}
/*******************************************************************************************
 *  printErrorBeforeClassDef(Token t) - prints an error that happens before class definition
 *******************************************************************************************/	
	private void printErrorBeforeClassDef(Token t) {
		System.out.println("Error at line: " + t.getTokenPosition().raw + ", raw: "
				+ t.getTokenPosition().column + ", next Token: " + t.getText() +
					", next expected Tokens: Import Identifier or Public Modifier or Package Identifier!!!");
	}


/*******************************************************************************************
 *  removeSemicolons() - removing the semicolons before tokens, if there are 
 *******************************************************************************************/
	/*private void removeSemicolons() {
		Token t = this.lfc.lookAhead();
		while(t.getToken() == this.tks.SEMICOLON){
			this.lfc.readNextToken();
			t = this.lfc.lookAhead();
		}
	}*/
/****************** Till here **********************************************************************/

	public Parser(Class clazz, String filePath) throws Exception{
		this.errors = new ErrorsClass();
		//this.operations = new Operations();
		this.fileName = filePath;
		this.fileDirPath = new File(filePath).getParent();
		this.lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new FileReader(new File(filePath)))));
		this.error = false;
		this.clazz = clazz;
		//this.parseClass();
		this.lfc.closeReader();
		this.lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new FileReader(new File(filePath)))));
		this.classfile = new ClassFile(this.clazz, this.clazz.getName() + ".class");
		
	}
	
	
/*******************************************************************************************
 *  parseClass() - parse only the class declaration and
 *  			 - checks if the name of the class match with filename 
 *  			 - if class declaration is not in the proper way written the parsing stops
 * @throws Exception 
 *******************************************************************************************/
	private void parseClass() throws Exception{
		
		String className = "";
		Token modifier = this.expected(new Token(this.tks.PUBLIC, "public"));
		if(modifier.getToken() == -1){
			System.exit(0);
			
		}
		Token t = this.expected(new Token(this.tks.CLASS, "class"));
		if(t.getToken() == -1){
			System.exit(0);
		}
		Token classNameToken = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
		if(classNameToken.getToken() == -1){
			System.exit(0);
		}
		
		className = classNameToken.getText();
		if(!this.clazz.getName().equals(className)){
			this.error = true;
			System.out.println("Error at line: " + classNameToken.getTokenPosition().raw + ", raw: "
					+ classNameToken.getTokenPosition().column + ", Class Name: " + className +
						", does not match with the file name: " + this.clazz.getName());
			
		}
		
		
		t = this.expected(new Token(this.tks.CURLY_BRACKET_OPEN, "{"));
		if(classNameToken.getToken() == -1){
			System.exit(0);
		}
		
		this.parseClassBody();
	}
	
/*******************************************************************************************
 *  parseClassBody() - 
 *  			 	 -  
 *  				 - 
 * @throws Exception 
 *******************************************************************************************/
	private void parseClassBody() throws Exception {
		boolean finished = false;
		Token token = this.lfc.lookAhead();
		while (!finished)
		{
			//System.out.println(token.getText());

			if (token.getToken() == this.tks.FIELD_ANNOTATION){
				this.expected(this.lfc.lookAhead());
				this.parseClassFieldDeclaration();
			}else if (token.getToken() == this.tks.CONSTRUCTOR_ANNOTATION){
				this.expected(this.lfc.lookAhead());
				this.parseConstructorDeclaration();
			}else if (token.getToken() == this.tks.METHOD_ANNOTATION){
				this.expected(this.lfc.lookAhead());
				Method m = this.parseMethodDeclaration();
				/*if(m != null){
					m.makeByteWriter();
				}*/
			}else if (token.getToken() == this.tks.SEMICOLON){
				this.expected(this.lfc.lookAhead());
			}else if (token.getToken() == this.tks.EOF){
				this.expected(new Token(this.tks.CURLY_BRACKET_CLOSE, "}"));
				finished = true;
			}else if (token.getToken() == this.tks.CURLY_BRACKET_CLOSE){
				this.expected(this.lfc.lookAhead());
				token = this.lfc.lookAhead();
				while(token.getToken() != this.tks.EOF){
					if(this.expected(new Token(this.tks.EOF,"EOF")).getToken() == -1){
						token = this.lfc.readNextToken();
					}
					token = this.lfc.lookAhead();
				}
				finished = true;
			}else{
				System.out.println("Error at line: " + token.getTokenPosition().raw + ", raw: " + token.getTokenPosition().column +", exptected Tokens: " 
						+ new Token(this.tks.FIELD_ANNOTATION, "@F").getText() + " or " + new Token(this.tks.METHOD_ANNOTATION, "@M").getText()
							+ " or " + new Token(this.tks.CONSTRUCTOR_ANNOTATION, "@C").getText());
				if(!this.error){
					this.error = true;
				}
				this.lfc.readNextToken();
			}
			token = this.lfc.lookAhead();
		}

		
	}
	
	
/*******************************************************************************************
 *  parseMethodDeclaration()
 *  		- 
 *  	 	-  
 *  		- 
 * @throws Exception 
 *******************************************************************************************/
	private Method parseMethodDeclaration() throws Exception {
		Token modifier = this.expected(new Token(this.tks.PUBLIC,"public"), new Token(this.tks.PRIVATE,"private"));
		if(modifier.getToken() != -1){
			boolean isStatic = this.isStatic();
			if(this.isNextToken() || this.isNextToken(new Token(this.tks.VOID))){
				Token type = this.lfc.readNextToken();
				Token name = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
				if(name.getToken() != -1){
					
					Token t = this.expected(new Token(this.tks.ROUND_BRACKET_OPEN,"("));
					if(t.getToken() != -1){	
						ParameterList pList = new ParameterList();
						if(this.parseParameterList(pList)){ 
							boolean isPrivate = false;
							if(modifier.getToken() == this.tks.PRIVATE){
								isPrivate = true;
							}
							Token openMathodBody = this.expected(new Token(this.tks.CURLY_BRACKET_OPEN, "{"));
							if(this.iskCorrectToken(openMathodBody)){
								Method m = new Method(this, name.getText(), new Type(type), pList, this.clazz, isStatic, isPrivate);
								boolean b = this.parseMethodBody(m);
								if(b){
									if(this.clazz.isAllreadyContainingMethod(m)){
										this.errors.printAlreadyImplementedMethodError(m);
									}else{
										this.checkIfFileOrClassExists(type);
										this.clazz.addClassMethod(m);
										clazz.isAllreadyContainingMethodInMethodsToBeChecked(m);
										return m;
									}
								}
							}else{
								this.lfc.readNextToken();
								this.expected(new Token(this.tks.SEMICOLON, ";"));
							}
						}else{
							this.lfc.readNextToken();
							this.expected(new Token(this.tks.SEMICOLON,";"));
						}
					}else{
						this.lfc.readNextToken();
						this.expected(new Token(this.tks.SEMICOLON,";"));
					}
				}else{
					if(!this.isMethodOrFieldOrConstructor()){
						this.lfc.readNextToken();
					}
					this.expected(new Token(this.tks.SEMICOLON,";"));
				}
			}else{
				Token t = this.lfc.readNextToken();
				this.printIlegalStartOfType(t);
				this.expected(new Token(this.tks.SEMICOLON,";"));
			}
			
		}else{
			this.lfc.readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
		}
		return null;
	}
/*******************************************************************************************
 *  parseMethodBody(Method m)
 *  		- 
 *  	 	-  
 *  		- 
 * @throws Exception 
 *******************************************************************************************/
	private boolean parseMethodBody(Method m) throws Exception {
		
		boolean b = true;
		while(!this.isNextToken(new Token(this.tks.CURLY_BRACKET_CLOSE, "}"))){
			if(this.isNextToken()){
				Token type = this.lfc.readNextToken();
				if(this.isNextToken(new Token(this.tks.ASSIGNMENT))){
					this.lfc.readNextToken();
					Field f;
					ExpressionParser expression = new ExpressionParser(this);
					if(m.isContainingFildMethodAndClass(type.getText(),false)){
						f = m.findFieldInsideMethoAndClass(type.getText());
						if(this.isNextToken(new Token(this.tks.NEW))){
							this.lfc.readNextToken();
							//TODO str to make to be it for parameters  -->  expression.parseTokenNew(s, str);
							b = expression.parseTokenNew(f, m);
							if(!b){
								break;
							}else{
								if(!this.clazz.isAllreadyContainingClassReference(f.getType().toString())
										&& !this.error){
									
									this.clazz.addCassReference(f.getClazz());
								}
								if(!this.error){
									expression.addNewFieldWriter(m, f);
								}
							}
						}else{
							b = expression.parseExpression(m, f, true, false);
							if(b){
								if(!this.clazz.isAllreadyContainingClassReference(f.getType().toString()) && !this.isIdentifierToken(f.getToken())){
									this.clazz.addCassReference(f.getClazz());
								}
								if(!this.error){
									expression.addArithmeticFieldWriter(m, f);
								}
							}
						}
						if(!this.error){
							if(m.getLocalVariables().containsByName(f.getName())){
								if(!m.getAlreadyDefinedFields().containsByName(f.getName())){
									m.addToAlreadyDefinedFields(f);
									m.addFieldToStackFrameFieldCounter(f);
								}
							}
						}
					}else{
						f = new Field(null, type.getText(), null, type);
						b = expression.parseExpression(m, f, true, false);
						if(b){
							if(!this.clazz.isAllreadyContainingClassReference(f.getType().toString()) && !this.isIdentifierToken(f.getToken())
									&& !this.error){
								this.clazz.addCassReference(f.getClazz());
							}
							if(!this.error){
								expression.addArithmeticFieldWriter(m, f);
							}
						}
						if(!this.error){
							if(m.getLocalVariables().containsByName(f.getName())){
								if(!m.getAlreadyDefinedFields().containsByName(f.getName())){
									m.addToAlreadyDefinedFields(f);
									m.addFieldToStackFrameFieldCounter(f);
								}
							}
						}
					}
				}else if(this.isNextToken(new Token(this.tks.DOT))){
					this.lfc.readNextToken();
					ReferenceCallParser refCall = new ReferenceCallParser(this.lfc, this.clazz, this.error, this);
					b = refCall.parseMethodOrFieldCall(type, m, null);
					this.error = refCall.getError();
					if(!b){
						break;
					}
				}else if(this.isNextToken(new Token(this.tks.IDENTIFIER))){
					Token name = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
					Token equal = new Token(this.tks.ASSIGNMENT, "=");
					if(this.isNextToken(equal)){
						this.lfc.readNextToken();
						String str = this.getFilePath(type);
						Field s;
						if(m.isContainingFildMethodAndClass(name.getText(),false)){
							s = m.findFieldInsideMethoAndClass(name.getText());
						}else{
							Class cl = this.clazz.getUsedClassByName(type.getText());
							if(cl == null){
								s = new Field(new Type(type), name.getText(), new Class(type.getText(), null, str), name);
							}else{
								s = new Field(new Type(type), name.getText(), cl, name);
							}
						}
						ExpressionParser expression = new ExpressionParser(this);
						if(this.isNextToken(new Token(this.tks.NEW))){
							this.lfc.readNextToken();
							//TODO str to make to be it for parameters  -->  expression.parseTokenNew(s, str);
							b = expression.parseTokenNew(s, m);
							if(!b){
								break;
							}else{
								//m.addLocalVariable(s);
								if(m.isContainingFildMethodAndClass(s.getName(), true)){
									this.errors.printContainsFieldError(s.getToken());
									this.error = true;
								}else{
									m.addLocalVariable(s);
								}
								if(!this.clazz.isAllreadyContainingClassReference(s.getType().toString())){
									this.clazz.addCassReference(s.getClazz());
								}
								this.checkIfFileOrClassExists(type);
								if(!this.error){
									
									expression.addNewFieldWriter(m, s);
								}
							}
						}else{
							b = expression.parseExpression(m, s, true, false);
							if(b){
								
								if(!this.clazz.isAllreadyContainingClassReference(s.getType().toString()) && !this.isIdentifierToken(s.getToken())
										&& !this.error){
									this.clazz.addCassReference(s.getClazz());
								}
								if(m.isContainingFildMethodAndClass(s.getName(), true)){
									this.errors.printContainsFieldError(s.getToken());
									this.error = true;
								}else{
									m.addLocalVariable(s);
								}
								this.checkIfFileOrClassExists(type);
								if(!this.error){
									expression.addArithmeticFieldWriter(m, s);
								}
							}else{
								break;
							}
						}
						if(!this.error){
							if(m.getLocalVariables().containsByName(s.getName())){
								if(!m.getAlreadyDefinedFields().containsByName(s.getName())){
									m.addToAlreadyDefinedFields(s);
									m.addFieldToStackFrameFieldCounter(s);
								}
							}
						}
					}else if(this.isNextToken(new Token(this.tks.SEMICOLON,";"))){
						b = m.isContainingFildMethodAndClass(name.getText(), true);
						if(b){
							Field f = m.findFieldInsideMethoAndClass(name.getText());
							this.errors.printContainsFieldError(name, f.getToken());
							b = false;
						}else{
							String str = this.getFilePath(type);
							this.checkIfFileOrClassExists(type);
							Field s;
							Class cl = this.clazz.getUsedClassByName(type.getText());
							if(cl == null){
								s = new Field(new Type(type), name.getText(), new Class(type.getText(), null, str), name);
							}else{
								s = new Field(new Type(type), name.getText(), cl, name);
							}
							if(m.isContainingFildMethodAndClass(s.getName(), true)){
								this.errors.printContainsFieldError(s.getToken());
								this.error = true;
							}else{
								m.addLocalVariable(s);
							}
							this.expected(new Token(this.tks.SEMICOLON, ";"));
							b = true;
						}
					}else{
						Token err = this.lfc.readNextToken();
						this.errors.printError(err, new Token(this.tks.ASSIGNMENT, "="), new Token(this.tks.SEMICOLON, ";"));
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
					}
				}else{
					this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
					this.lfc.readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b=false;
					break;
				}
			}else if(this.isNextToken(new Token(this.tks.IF))){
				int start = 0;
				if(this.startPos == -1){
					start = m.getStartOfLoopPosition() + 1;
				}else{
					start = m.getStartOfLoopPosition() - this.startPos;
				}
				this.expected(new Token(this.tks.IF));
				IfParser ifparser = new IfParser(m, this, start);
				m.setIfParser(ifparser);
				b = ifparser.parse();
				if(!b){
					break;
				}
				m.addEpressions(ifparser.getExpressions());
				this.startPos = m.getStartOfLoopPosition();
			}else if(this.isNextToken(new Token(this.tks.WHILE))){
				
				int start = 0;
				if(this.startPos == -1){
					start = m.getStartOfLoopPosition();
				}else{
					start = m.getStartOfLoopPosition() - this.startPos;
					if(start != 0){
						start = start-1;
					}
				}
				this.expected(new Token(this.tks.WHILE));
				LoopParser loopparser = new LoopParser(m, this, start);
				m.setLoopParser(loopparser);
				b = loopparser.parse();
				if(!b){
					break;
				}
				m.addEpressions(loopparser.getExpressions());
				this.startPos = m.getStartOfLoopPosition();
			}else if(this.isNextToken(new Token(this.tks.RETURN))){
				b = this.checkReturnTypeValidation(m);
				if(!b){
					break;
				}
				Token semicolon = this.expected(new Token(this.tks.SEMICOLON, ";"));
				if(!this.iskCorrectToken(semicolon)){
					b=false;
					break;
				}
			}else{
				Token err = this.lfc.readNextToken();
				this.errors.printMethodBodyError(err);
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b=false;
				break;
			}
			
		}
		if(b){

			
			Token err = this.expected(new Token(this.tks.CURLY_BRACKET_CLOSE, "}"));
			if(b && !this.iskCorrectToken(err)){
				this.lfc.readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
		}
		if(!b){
			this.error = true;
		}
		if(!this.error){
			if((m.getIfParser() != null) || (m.getLoopParser() != null)){
				m.makeStackMapTableCode();
			}
		}
		//m.printStackFrameFieldCounter();
		/*if(!this.error){
			m.makeByteWriter();
			m.getByteWriter().printByteArray();
			m.printStackFrameFieldCounter();
		}*/
		return b;
	}


/*******************************************************************************************
 *  String getFilePath(Token type) 
 *  		- 
 *******************************************************************************************/
	private String getFilePath(Token type) {
		if(type.getToken() == this.tks.IDENTIFIER){
			//TODO Implement for java/lang/ or find similar solution
			Class cl = null;
			for(int i=0; i<this.clazz.getImportedArrayClasses().size();i++){
				if(this.clazz.getImportedArrayClasses().get(i).getName().equals(type.getText())){
					cl = this.clazz.getImportedArrayClasses().get(i);
					break;
				}
			}
			if(cl != null){
				return  cl.getFilePath();
			}
		}
		return this.clazz.getFilePath();
	}

/*******************************************************************************************
 *  checkReturnTypeValidation(Method m)
 *  		- 
 *  	 	-  
 *  		- 
 *******************************************************************************************/
	private boolean checkReturnTypeValidation(Method m) {
		if(m.getRetrunType().getType() != new Type().VOID){
			
			Token err = this.expected(new Token(this.tks.RETURN, "return"));
			if(!this.iskCorrectToken(err)){
				this.lfc.readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				return false;
			}
			if(this.isNextToken()){
				Token t = this.lfc.readNextToken();
				if(t.getToken() == this.tks.IDENTIFIER){
					boolean b = m.isContainingFildMethodAndClass(t.getText(), false);
					if(b){
						Field f = m.findFieldInsideMethoAndClass(t.getText());
						if(this.isNextToken(new Token(this.tks.DOT))){
							this.lfc.readNextToken();
							ExpressionParser ex = new ExpressionParser(this);
							b = ex.parseReturnRef(f, this.clazz, m);
						}else{
							if(f.getType().getType() != m.getRetrunType().getType()){
								this.errors.printNotCompatibleTypes(f, m);
								this.error = true;
							}
							if(this.isNextToken(new Token(this.tks.SEMICOLON))){
								return true;
							}else{
								return false;
							}
						}
						//TODO TO MAKE IT WORK FOR CLASS AS RETURN TYPE 
					}else{
						this.errors.identifierDoesNotExistsError(t);
						return false;
					}
				}else{
					Field f = new Field(new Type(t), t.getText(), null, t);
					if(f.getType().getType() != m.getRetrunType().getType()){
						this.errors.printNotCompatibleTypes(f, m);
						return false;
					}
				}
			}else{
				Token t = this.lfc.readNextToken();
				this.errors.printErrorFieldDeclaration(t);
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				return false;
			}
		}
		return true;
	}

/*******************************************************************************************
 *  parseConstructorDeclaration()
 *  		- 
 *  	 	-  
 *  		- 
 *******************************************************************************************/
	private void parseConstructorDeclaration() {
		// TODO Auto-generated method stub
		
	}

/*******************************************************************************************
 *  parseClassFieldDeclaration() - 
 *  			 	 -  
 *  				 - 
 *******************************************************************************************/
	private void parseClassFieldDeclaration() {
		Token modifier = this.expected(new Token(this.tks.PUBLIC,"public"), new Token(this.tks.PRIVATE,"private"));
		if(modifier.getToken() != -1){
			boolean isStatic = this.isStatic();
			boolean isFinal = this.isFinal();
			if(this.isNextToken()){
				Token type = this.lfc.readNextToken();
				if(this.isNextToken(new Token(this.tks.IDENTIFIER))){
					Token name = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));	
					boolean isPrivate = false;
					if(modifier.getToken() == this.tks.PRIVATE){
						isPrivate = true;
					}
					Token semicolon = this.expected(new Token(this.tks.SEMICOLON, ";"));
					if(this.iskCorrectToken(semicolon)){
						Field f = new Field(new Type(type), name.getText(), this.clazz, isStatic, isPrivate, isFinal, name);
						if(this.clazz.isAllreadyContainingField(f.getName())){
							this.printContainsFieldError(name);
							this.error = true;
						}else{
							this.checkIfFileOrClassExists(type);
							this.clazz.addClassField(f);
						}
					}
					
				}else if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
					this.expected(new Token(this.tks.SQUARE_BRACKET_OPEN, "["));
					Field f = this.parseArray(modifier, isStatic, isFinal, type);
					if(f != null){
						Token tok = this.expected(new Token(this.tks.SEMICOLON, ";"));
						if(tok.getToken() != -1){
							if(this.clazz.isAllreadyContainingField(f.getName())){
								this.printContainsFieldError(f.getToken());
								this.error = true;
							}else{
								this.checkIfFileOrClassExists(type);
								this.clazz.addClassField(f);
							}
						}
					}
				}else{
					if(!this.isMethodOrFieldOrConstructor()){
						this.lfc.readNextToken();
					}
					this.expected(new Token(this.tks.SEMICOLON,";"));
				}
			}else{
				Token t = this.lfc.readNextToken();
				this.printIlegalStartOfType(t);
				this.expected(new Token(this.tks.SEMICOLON,";"));
			}
			
		}else{
			this.lfc.readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
		}

		
	}
/*****************************************************************************************************************
 * void checkIfFileOrClassExists(Token type)
 * 		-  
 ****************************************************************************************************************/
	private void checkIfFileOrClassExists(Token type) {
		if(type.getToken() == this.tks.IDENTIFIER){
			//TODO Implement for java/lang/ or find similar solution
			if(!this.clazz.isAllreadyContainingClass(type.getText())){
				if(type.getText().equals("String")){
					//TODO read the parent class from class file
					this.clazz.addUsedClasses(new Class("java/lang/String", null, "java/lang/String", "java/lang/"));//type.getText(), null, "java/lang/String", "java/lang/String"));
				}else{
					if(!this.checkFile(this.clazz.getFilePath()  + type.getText() + ".java")){
						this.errors.printFileDoesNotExists(type, type.getText());
						this.error = true;
					}else{
						String dirPath = this.clazz.getFilePath();
						this.clazz.addUsedClasses(new Class(type.getText(), null, dirPath));
					}
				}
			}
		}
	}

/*****************************************************************************************************************
 * parseArray()
 * 		-  
 ****************************************************************************************************************/
	private Field parseArray(Token modifier, boolean isStatic, boolean isFinal, Token type) {
		Token t = this.expected(new Token(this.tks.SQUARE_BRACKET_CLOSE, "]"));
		if(t.getToken() != -1){
			t = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
			if(t.getToken() != -1){
				Field f = new Field(new Type(new Type(type)), t.getText(), this.clazz, isStatic, false, isFinal, t);
				return f; 
				
			}else{
				this.lfc.readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
			}
		}else{
			this.lfc.readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
		}
		return null;
	}

/*****************************************************************************************************************
 * parseParameterList(ParameterList p) - parse the parameters from the constructors or methods,
 * the method returns 
 * 	- either empty list, if there is error, or if there is no parameters
 *  - or a list with the parameters  
 ****************************************************************************************************************/
	private boolean parseParameterList(ParameterList p) {
		boolean b = true;
		while(!this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"))){
			if(this.isNextToken()){
				Token type = this.lfc.readNextToken();
				this.checkIfFileOrClassExists(type);
				if(this.isNextToken(new Token(this.tks.IDENTIFIER))){
					Token name = this.lfc.readNextToken();
					Token comma = new Token(this.tks.COMMA, ",");
					if(this.isNextToken(comma)){
						this.expected(comma);
						if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"))){
							Token errorToken = this.lfc.readNextToken();
							this.printErrorFieldDeclaration(errorToken);
							this.expected(new Token(this.tks.SEMICOLON,";"));
							b = false;
							break;
						}else{
							String str = this.getFilePath(type);
							Field f = new Field(new Type(type), name.getText(), new Class(type.getText(), null, str), name);
							if(p.containsByName(f.getName())){
								this.printContainsFieldError(name);
							}else{
								p.addParameter(f);
							}
						}
					}else if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"))){
						Field f = new Field(new Type(type), name.getText(), this.clazz, name);
						if(p.containsByName(f.getName())){
							this.printContainsFieldError(name);
						}else{
							p.addParameter(f);
						}
					}else{
						this.expected(comma, new Token(this.tks.ROUND_BRACKET_CLOSE, ")"));
						this.lfc.readNextToken();
						this.expected(new Token(this.tks.SEMICOLON,";"));
						b = false;
						break;
					}
				}else if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
					this.lfc.readNextToken();
					Field f = this.parseArray(null, false, false, type);
					if(f == null){
						b = false;
						break;
					}
					Token comma = new Token(this.tks.COMMA, ",");
					if(this.isNextToken(comma)){
						this.expected(comma);
						if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"))){
							Token errorToken = this.lfc.readNextToken();
							this.printErrorFieldDeclaration(errorToken);
							this.expected(new Token(this.tks.SEMICOLON,";"));
							b = false;
							break;
						}else{
							if(p.containsByName(f.getName())){
								this.printContainsFieldError(f.getToken());
							}else{
								p.addParameter(f);
							}
						}
					}else if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"))){
						if(p.containsByName(f.getName())){
							this.printContainsFieldError(f.getToken());
						}else{
							p.addParameter(f);
						}
					}else{
						this.expected(comma, new Token(this.tks.ROUND_BRACKET_CLOSE, ")"));
						this.lfc.readNextToken();
						this.expected(new Token(this.tks.SEMICOLON,";"));
						b = false;
						break;
					}
				}else{
					this.lfc.readNextToken();
					this.expected(new Token(this.tks.SEMICOLON,";"));
					b = false;
					break;
				}
			}else{
				Token t = this.lfc.readNextToken();
				this.printIlegalStartOfType(t);
				this.expected(new Token(this.tks.SEMICOLON,";"));
				b = false;
				break;
			}
		}
		
		if(b){
			Token token = this.expected(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"));
			if(token.getToken() == 1){
				this.lfc.readNextToken();
				p = new ParameterList();
				b = false;
				this.expected(new Token(this.tks.SEMICOLON,";"));
			}
		}else{
			p = new ParameterList();
		}
		return b;
	}
	
/*******************************************************************************************
 *  printIlegalStartOfExpression(Field f) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private void printIlegalStartOfType(Token t) {
		System.out.println("Error at line: " + t.getTokenPosition().raw + ", raw: "
				+ t.getTokenPosition().column + ", next Token: " + t.getText()
					+ ", illegal start of type!!!");
	}
/*******************************************************************************************
 *  printContainsFieldError(Field f) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private void printContainsFieldError(Token token) {
		System.out.println("Error at line: " + token.getTokenPosition().raw + ", raw: "
				+ token.getTokenPosition().column + ", field name: " + token.getText() + " allready exists in the class!!!");
	}
/*******************************************************************************************
 *  expected((Token token, Token token2)) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private Token expected(Token token1, Token token2) {
		Token t = this.lfc.lookAhead();
		if((t.getToken() != token1.getToken()) && (t.getToken() != token2.getToken())){
			this.error = true;
			this.printError(t, token1, token2);
			return new Token(-1 , "Unknown Token");
		}
		t = this.lfc.readNextToken();
		return t;
		
	}
/***************************************************************************************************************************/	
	private Token expected(Token token) 
	{
		if(this.lfc.lookAhead().getToken() != token.getToken()){
			if(this.error != true){
				this.error = true;
			}
			this.printError(this.lfc.lookAhead(), token);
			return new Token(-1 , "Unknown Token");
		}
		return this.lfc.readNextToken();
	}
/*******************************************************************************************
 *  Token expected(TokenArrayList tk)
 *  	- 
 *******************************************************************************************/
	private Token expected(TokenArrayList tk) {
		for(int i = 0; i < tk.size(); i++){
			if(this.lfc.lookAhead().getToken() == tk.get(i).getToken()){
				return this.lfc.readNextToken();
			}
		}
		
		if(this.error != true){
			this.error = true;
		}
		this.errors.printExpectedMoreTokensError(this.lfc.lookAhead(), tk);
		return new Token(-1 , "Unknown Token");
		
	}

	
	private void printError(Token token, Token token1){
		System.out.println("Error at line: " + token.getTokenPosition().raw + ", raw: "
				+ token.getTokenPosition().column + ", next Token: " + token.getText()
					+ ", expeceted Token: " + token1.getText());
	}
	
	private void printError(Token token, Token token1, Token token2){
		System.out.println("Error at line: " + token.getTokenPosition().raw + ", raw: "
				+ token.getTokenPosition().column + ", next Token: " + token.getText()
					+ ", expeceted Token: " + token1.getText() + " or " + token2.getText());
	}

	
/*	private Token expectedTokens(Token token) 
	{
		if((token.getToken()!=this.tks.IMPORT) && (token.getToken()!=this.tks.PUBLIC) && (token.getToken()!=this.tks.CLASS) 
				&& (token.getToken()!=this.tks.SEMICOLON)){
			if(this.error != true){
				this.error = true;
			}
			this.printMoreTokensError(token);
			return new Token(-1);
		}
		return this.lfc.readNextToken();
	}
	
	private void printMoreTokensError(Token token){
		System.out.println("Error at line: " + token.getTokenPosition().raw + ", raw: "
				+ token.getTokenPosition().column + ", next Token: " + token.getText() + ", next expected Tokens: Semicolon, or Class or Import Identifier, or Public Modifier");
	}*/
/***************************************************************************************************************************/
	/***************************  Field declaration parse errors *****************************************/
	/*******************************************************************************************
	 *  parseErrorsFieldDeclaration() - parse the error at the beginning of the file if there is an error
	 *  else continuing with parsing
	 *******************************************************************************************/
		private void parseErrorsFieldDeclaration(boolean isAlreadyParsed) {
			Token t = this.lfc.lookAhead();
			if(isAlreadyParsed && ((t.getToken()!=this.tks.STATIC) && (t.getToken()!=this.tks.IDENTIFIER) && (t.getToken()!=this.tks.FINAL)
					&& (t.getToken()!=this.tks.INT) && (t.getToken()!=this.tks.CHAR))){
				this.lfc.readNextToken();
			}
			t = this.lfc.lookAhead();
			if((t.getToken()!=this.tks.STATIC) && (t.getToken()!=this.tks.IDENTIFIER) && (t.getToken()!=this.tks.FINAL)
					&& (t.getToken()!=this.tks.INT) && (t.getToken()!=this.tks.CHAR)){
				this.removeErrorTokensFieldDeclaration();
			}
		}

/*******************************************************************************************
 *  removeErrorTokensFieldDeclaration() - removing the errors and prints the error
 *******************************************************************************************/
	private void removeErrorTokensFieldDeclaration() {
		Token t = this.lfc.lookAhead();
		while((t.getToken()!=this.tks.STATIC) && (t.getToken()!=this.tks.IDENTIFIER) && (t.getToken()!=this.tks.FINAL)
				&& (t.getToken()!=this.tks.INT) && (t.getToken()!=this.tks.CHAR)){
			 if(t.getToken() == this.tks.SEMICOLON){
				this.lfc.readNextToken();
			 }else if(t.getToken() == this.tks.EOF){
					System.exit(0);
			 }else{
				t = this.lfc.readNextToken();
				this.printErrorFieldDeclaration(t);
				this.error = true;
			 }
			 t = this.lfc.lookAhead();
		}
		
	}
/*******************************************************************************************
 *  printErrorFieldDeclaration(Token t) - prints an error that happens before class definition
 *******************************************************************************************/	
	private void printErrorFieldDeclaration(Token t) {
		System.out.println("Error at line: " + t.getTokenPosition().raw + ", raw: "
				+ t.getTokenPosition().column + ", next Token: " + t.getText() +
					", next expected Tokens: Identifier or int type or char type!!!");
	}


	/*******************************************************************************************
	 *  removeSemicolons() - removing the semicolons before tokens, if there are 
	 *******************************************************************************************/
		/*private void removeSemicolons() {
			Token t = this.lfc.lookAhead();
			while(t.getToken() == this.tks.SEMICOLON){
				this.lfc.readNextToken();
				t = this.lfc.lookAhead();
			}
		}*/
	/****************** Till here **********************************************************************/
/***************************************************************************************
 *  - isNextToken() - checks if the next token is char, int, indetifier or string 
 *  - return true if it is otherwise false
 **************************************************************************************/
	private boolean isNextToken(){
		if(((this.lfc.lookAhead().getToken() == this.tks.INT) || (this.lfc.lookAhead().getToken() == this.tks.IDENTIFIER)
				|| (this.lfc.lookAhead().getToken() == this.tks.CHAR))){
			return true;
		}
		return false;
	}
	
/***************************************************************************************
 *  - isNextToken(Token t) - checks if the next token is the same as the parameter t 
 *  - return true if it is otherwise false
 **************************************************************************************/
	private boolean isNextToken(Token t){
		Token t1 = this.lfc.lookAhead();
		if(t1.getToken() == t.getToken()){
			return true;
		}
		return false;
	}
	
/***************************************************************************************
 *  - isStatic() 
 *  	- return true if the next token is static
 *  	- otherwise false
 **************************************************************************************/
	private boolean isStatic(){
		if(this.lfc.lookAhead().getToken() == this.tks.STATIC){
			this.expected(new Token(this.tks.STATIC, "static"));
			return true;
		}
		return false;
	}
		
/***************************************************************************************
 *  - isFinal() 
 *  	- return true if the next token is final
 *  	- otherwise false
 **************************************************************************************/
	private boolean isFinal(){
		if(this.lfc.lookAhead().getToken() == this.tks.FINAL){
			this.expected(new Token(this.tks.FINAL, "final"));
			return true;
		}
		return false;
	}
	
/***************************************************************************************
 *  - iskCorrectToken(Token t) 
 *  	- return true if the token t is not an unknow token
 *  	- otherwise false
 **************************************************************************************/
	private boolean iskCorrectToken(Token t){
		if(t.getToken() != -1){
			return true;
		}
		return false;
	}

/*********************************************************************
 *  - Check if next token is CONSTRUCTOR_ANNOTATION or METHOD_ANNOTATION or FIELD_ANNOTATION
 *  - returns true if its one of them, and
 *  - false if its not
 ********************************************************************/
	private boolean isMethodOrFieldOrConstructor(){
		if(!((this.lfc.lookAhead().getToken() == this.tks.CONSTRUCTOR_ANNOTATION) || (this.lfc.lookAhead().getToken() == this.tks.METHOD_ANNOTATION)
				|| (this.lfc.lookAhead().getToken() == this.tks.FIELD_ANNOTATION))){
			return false;
		}
		return true;
	}
	
/*********************************************************************
 *  getLfc()
 *  	- returns the LookForwardScanner lfc
 ********************************************************************/
	public LookForwardScanner getLfc(){
		return this.lfc;
	}
/*******************************************************************************************
 *  isIdentifierToken(Token t) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private boolean isIdentifierToken(Token t){
		if(t.getToken() == this.tks.IDENTIFIER){
			return true;
		}
		return false;
	}
/*******************************************************************************************
 *  boolean getError()
 *  	- 
 *******************************************************************************************/
	public boolean getError(){
		return this.error;
	}
	
/*******************************************************************************************
 *  void getError(boolean b)
 *  	- 
 *******************************************************************************************/
	public void setError(boolean b){
		this.error = b;
	}
}