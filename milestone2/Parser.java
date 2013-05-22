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
	
	public Parser(String filePath) throws Exception{
		this.errors = new ErrorsClass();
		this.lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new FileReader(new File(filePath)))));
		//System.out.println(new File(filePath).getPath());
		//System.out.println(new File(filePath).getName());
		//System.out.println(new File(filePath).getAbsolutePath());
		//System.out.println(new File(filePath).getCanonicalPath() );
		this.fileName = new File(filePath).getName();
		this.fileDirPath = new File(filePath).getParent() + "/";
		//System.out.println(new File(this.fileDirPath+ "Milestone4.java").isFile());
		//System.out.println(fileDirPath);
		/*File f = new File(new File(filePath).getParent());
		if( f.isDirectory() )
		{
			System.out.println("TUKAA");
			File[] filelist = f.listFiles();
			for( File t : filelist )
			{
				System.out.println( t.getAbsolutePath() );
				System.out.println( t.exists() );
			}
		}
		else
		{
			System.out.println( f.getAbsolutePath() );
			System.out.println( f.exists() );
		}*/
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
			//System.out.println("TUIKA");
			for(int i = 1; i < this.clazz.getMethods().size(); i++){
				this.clazz.getMethods().get(i).makeByteWriter();
			}
			this.classfile = new ClassFile(this.clazz, this.clazz.getName() + ".class");
		}else{
			this.classfile = null;
		}
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
							//System.out.println("TUKA SUMMMM");
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
							b = expression.parseTokenNew(f);
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
								if(!this.clazz.isAllreadyContainingClassReference(f.getType().toString()) && !this.isIdentifierToken(f.getToken())
										&& !this.error){
									this.clazz.addCassReference(f.getClazz());
								}
								if(!this.error){
									expression.addArithmeticFieldWriter(m);
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
								expression.addArithmeticFieldWriter(m);
							}
						}
					}
				}else if(this.isNextToken(new Token(this.tks.DOT))){
					this.lfc.readNextToken();
					b = this.parseMethodOrFieldCall(type, m);
					if(!b){
						break;
					}
				}else if(this.isNextToken(new Token(this.tks.IDENTIFIER))){
					Token name = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
					Token equal = new Token(this.tks.ASSIGNMENT, "=");
					if(this.isNextToken(equal)){
						this.lfc.readNextToken();
						String str = this.getFilePath(type);
						Field s = new Field(new Type(type), name.getText(), new Class(type.getText(), null, str), name);
						ExpressionParser expression = new ExpressionParser(this);
						if(this.isNextToken(new Token(this.tks.NEW))){
							this.lfc.readNextToken();
							//TODO str to make to be it for parameters  -->  expression.parseTokenNew(s, str);
							b = expression.parseTokenNew(s);
							if(!b){
								break;
							}else{
								m.addLocalVariable(s);
								if(!this.clazz.isAllreadyContainingClassReference(s.getType().toString()) && !this.error){
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
								m.addLocalVariable(s);
								this.checkIfFileOrClassExists(type);
								if(!this.error){
									expression.addArithmeticFieldWriter(m);
								}
							}else{
								break;
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
							m.addLocalVariable(new Field(new Type(type), name.getText(), new Class(type.getText(), null, str), name));
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
					Token name = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
					name = this.lfc.readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b=false;
					break;
				}
			}else if(this.isNextToken(new Token(this.tks.IF))){
				this.expected(new Token(this.tks.IF));
				IfParser ifparser = new IfParser(m, this);
				m.setIfParser(ifparser);
				b = ifparser.parse();
				if(!b){
					break;
				}
				
				//TODO TO MAKE THE SAME LIKE FOR WHILE LOOP
			}else if(this.isNextToken(new Token(this.tks.WHILE))){
				this.expected(new Token(this.tks.WHILE));
				LoopParser loopparser = new LoopParser(m, this);
				m.setLoopParser(loopparser);
				b = loopparser.parse();
				if(!b){
					break;
				}
				//System.out.println(loopparser.getLengthFromAllExpressions());
				//int number = loopparser.getLengthFromAllExpressions() - loopparser.getStackMapTable().get(loopparser.getStackMapTable().size()-1) - 2;
				//loopparser.getStackMapTable().add(number);
				//loopparser.printStackMapTable();
				loopparser.makeStackMapTable();
				//loopparser.printStackMapTable();
				
				m.addEpressions(loopparser.getExpressions());
		/**********************************************************************************************************************/
				m.makeByteWriter();
				//m.getByteWriter().printByteArray();
			}else if(this.isNextToken(new Token(this.tks.RETURN))){
				b = this.checkReturnTypeValidation(m);
				if(!b){
					break;
				}
				//Token t = this.lfc.readNextToken();
				Token semicolon = this.expected(new Token(this.tks.SEMICOLON, ";"));
				if(!this.iskCorrectToken(semicolon)){
					b=false;
					break;
				}
				//m.makeItFinishTheByteCodeForMethod(t);

				//m.getByteWriter().printByteArray();
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
		return b;
	}
/*******************************************************************************************
 *  boolean parseMethodOrFieldCall(Token type)) 
 *  		- 
 * @throws Exception 
 *******************************************************************************************/
	private boolean parseMethodOrFieldCall (Token name, Method m) throws Exception{
		boolean b = true;
		boolean isContainigField = m.isContainingFildMethodAndClass(name.getText(), true);
		Field f = null;
		if(!isContainigField){
			this.error = true;
			this.errors.identifierDoesNotExistsError(name);
		}else{
			f = m.findFieldInsideMethoAndClass(name.getText());
		}
		Token fieldOrMethodName = this.expected(new Token(this.tks.IDENTIFIER,"Identifier"));
		if(fieldOrMethodName.getToken() != -1){
			if(!isContainigField){
				TokenArrayList tk = new TokenArrayList();
				tk.add(new Token(this.tks.ASSIGNMENT, "="));
				tk.add(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
				tk.add(new Token(this.tks.SQUARE_BRACKET_OPEN, "["));
				Token er = this.expected(tk);
				if(er.getToken() != -1){
					if(er.getToken() == this.tks.ASSIGNMENT){
						//TODO To parse Without generating the code because there is an error
						//this.parseAssignmentWhitoutCodeGeneration();
					}else{
						this.expected(new Token(this.tks.ASSIGNMENT, "="));
						this.lfc.readNextToken();
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
					}
				}else{
					b = false;
				}	
			}else{
				if(this.isNextToken(new Token(this.tks.ASSIGNMENT))){
					this.lfc.readNextToken();
					b = this.parseFieldCall(f, fieldOrMethodName, m, name, -1);
				}else if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_OPEN))){
					//TODO IMPLEMENT METHOD CALLS
				}else if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
					this.expected(new Token(this.tks.SQUARE_BRACKET_OPEN, "["));
					Token number = this.expected(new Token(this.tks.NUMBER, "Number"));
					if(number.getToken() != -1){
						Token t = this.expected(new Token(this.tks.SQUARE_BRACKET_CLOSE, "]"));
						if(t.getToken() !=-1){
							t = this.expected(new Token(this.tks.ASSIGNMENT, "="));
							if(t.getToken() != -1){
								int num = Integer.parseInt(number.getText());
								b = this.parseFieldCall(f, fieldOrMethodName, m, name, num);
							}else{
								this.lfc.readNextToken();
								this.expected(new Token(this.tks.SEMICOLON, ";"));
								b = false;
							}
						}else{
							this.lfc.readNextToken();
							this.expected(new Token(this.tks.SEMICOLON, ";"));
							b = false;
						}
					}else{
						this.lfc.readNextToken();
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
					}
				}else{
					Token error = this.lfc.readNextToken();
					TokenArrayList tk = new TokenArrayList();
					tk.add(new Token(this.tks.ASSIGNMENT, "="));
					tk.add(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
					tk.add(new Token(this.tks.SQUARE_BRACKET_OPEN, "["));
					this.errors.printExpectedMoreTokensError(error, tk);
					
					this.error = true;
					b = false;
				}	
			}		
		}else{
			this.lfc.readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
			b = false;
		}
		return b;
	}
/*******************************************************************************************
 *  String getFilePath(Token type) 
 *  		- 
 * @throws Exception 
 *******************************************************************************************/
private boolean parseFieldCall(Field f, Token fieldOrMethodName, Method m, Token name, int position) throws Exception {
	boolean b = true;
	ExpressionParser ex = new ExpressionParser(this);
	if(this.clazz.isAllreadyContainingClassReference(f.getClazz().getName())){
		Field fieldFromClassRef;
		if(this.clazz.isAllreadyContainingFieldReference(fieldOrMethodName.getText())){
			fieldFromClassRef = this.clazz.getFieldFromFieldRef(fieldOrMethodName.getText());
		}else{
			
			String filePathClass = f.getClazz().getFilePath() + f.getClazz().getName() + ".class";
			String filePathJava = f.getClazz().getFilePath() + f.getClazz().getName() + ".java";
			//System.out.println( this.clazz.getFilePath());
			//System.out.println(filePathClass);
			if(this.checkFile(filePathClass)){
				ByteReader breader = new ByteReader(filePathClass);
				fieldFromClassRef = breader.findField(fieldOrMethodName,f.getClazz());
			}else if(this.checkFile(filePathJava)){
				System.out.println(filePathClass);
				Parser pr = new Parser(filePathJava);
				this.error = pr.getError();
				if(!this.error){
					ByteReader breader = new ByteReader(filePathClass);
					fieldFromClassRef = breader.findField(fieldOrMethodName,f.getClazz());
				}else{
					//TODO
					System.out.println(this.clazz.getName() + "  ERROR");
					fieldFromClassRef = null;
				}
			}else{
				fieldFromClassRef = null;
			}
		}
		if(fieldFromClassRef == null){
			this.error = true;
			this.errors.printFieldDoesNotExists(fieldOrMethodName);
		}else{
			if(!this.clazz.isAllreadyContainingFieldReference(fieldOrMethodName.getText())){
				f.getClazz().addClassField(fieldFromClassRef);
				this.clazz.addFieldReference(fieldFromClassRef);
			}
			//TODO Make it better(we know if fieldFromClassRef is array field or normal field)
			if(this.isNextToken(new Token(this.tks.NEW))){
				if(position != -1){
					this.error = true;
					b = false;
					System.out.println("ERROR");
					//TODO PRINT THE ERROR 
				}else{
					this.lfc.readNextToken();
					b = ex.parseTokenNew(fieldFromClassRef);
					if(b){
						if(!this.error){
							if(fieldFromClassRef.getType().isArray()){
								ex.addNewFieldArrayWriter(m, fieldFromClassRef, f.getClazz(), f);
							}else{
								//TODO TO IMPLEMET NEW TOKEN FOR FIELD REFERENCES
							}
						}
					}
				}
			}else{
				if(position != -1){
					if(fieldFromClassRef.getType().isArray()){
						if(fieldFromClassRef.getSize() <= position){
							this.error = true;
							System.out.println("ERROR1");
							//TODO PRINT THE ERROR 
						}
					}else{
						this.error = true;
						b = false;
						System.out.println("ERROR2");
						//TODO PRINT THE ERROR 
					}
				}else{
					
				}
				if(b){
					b = ex.parseFieldRefExpression(fieldOrMethodName, m, f.getClazz());
					if(b){
						if(!this.error){
							ex.addArithmeticFieldRefWriter(m,name ,fieldFromClassRef, this.clazz, position, f);
						}
					}
				}
				
			}
		}
		
	}
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
 *  loopParseByte(Method m, LoopParser loopparser)
 *  		- 
 *  	 	-  
 *  		- 
 *******************************************************************************************/
	private void loopParseByte(Method m, LoopParser loopparser) {
		int startPosition = m.calculateExpressionPosition();
		loopparser.makeByteWriter(startPosition);
	}

/*******************************************************************************************
 *  makeIfParserByteCode((Method m, IfParser ifp))
 *  		- 
 *  	 	-  
 *  		- 
 *******************************************************************************************/
	private void makeIfParserExpressionFinish(Method m, IfParser ifp) {
		int startPosition = m.calculateExpressionPosition();
		//System.out.println("Startposition: " + startPosition);
		ifp.setElsePosition(startPosition, false);
		//System.out.println(ifp.getByteWriter().size());
		//System.out.println(ifp.getStackMapTable().size());
		ifp.printStackMapTable();
		ifp.getByteWriter().printByteArray();
		
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
					this.clazz.addUsedClasses(new Class(type.getText(), null, "java/lang/String", "java/lang/String"));
				}else{
					System.out.println(this.clazz.getFilePath()  + type.getText() + ".java");
					if(!this.checkFile(this.clazz.getFilePath()  + type.getText() + ".java")){
						System.out.println("TUKSSSSSS");
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