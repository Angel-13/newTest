package milestone2;

import java.io.IOException;
import java.io.StringReader;

import Code.ArithmeticExpression;
import Code.CoditionExpression;
import Code.NewArrayExpression;
import Code.NewExpression;

import scanner.LookForwardReader;
import scanner.LookForwardScanner;
import scanner.Scanner;
import symbolTable.Field;
import symbolTable.Method;
import symbolTable.Class;
import symbolTable.ParameterList;
import symbolTable.Type;
import tokens.Token;
import tokens.Tokens;

public class ExpressionParser {

	private final Parser p;
		
	private final ErrorsClass errors;
	
	private final Tokens tks;
	
	private final ParameterList pList;
	
	private final BodyParser bodyParser;
	
	private String str;
	
	public ExpressionParser(Parser p, BodyParser bodyParser){
		this.p = p;
		this.errors = new ErrorsClass();
		this.tks = new Tokens();
		this.pList = new ParameterList();
		this.bodyParser = bodyParser;
		this.str = "";
	}
	
	public ExpressionParser(Parser p){
		this.p = p;
		this.errors = new ErrorsClass();
		this.tks = new Tokens();
		this.pList = new ParameterList();
		this.bodyParser = null;
		this.str = "";
	}
		
/*******************************************************************************************
 *  parseExpression(Method m, Field f, boolean isFieldAlreadyDeclared) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	public boolean parseExpression(Method m, Field f, boolean isFieldAlreadyDeclared, boolean scopeCall){
		boolean b = true;
		this.pList.addParameter(f);
		if(this.isNextTokenSubAddOperator()){
			this.p.getLfc().readNextToken();
		}

		this.str = "= ";
		while(!this.isNextToken(new Token(this.tks.SEMICOLON, ";"))){

		
			Token t = this.expected(new Token(this.tks.NUMBER, "Number"), new Token(this.tks.IDENTIFIER, "Identifier"));
			if(this.iskCorrectToken(t)){
				this.str = this.str +  " " + t.getText();
				if(!this.isNextToken(new Token(this.tks.SEMICOLON, ";"))){
					if(!this.isNextTokenOperator()){
						Token err = this.p.getLfc().readNextToken();
						this.errors.printExpectsOperatorError(err);
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
						break;
					}else{
						this.str = str + " " +this.p.getLfc().readNextToken().getText();
						if(this.isNextToken(new Token(this.tks.NUMBER)) || this.isNextToken(new Token(this.tks.IDENTIFIER))){
							if(scopeCall){
								this.pList.addParameter(this.makeSkopeField(m, t));
							}else{
								this.pList.addParameter(this.makeField(m, t));
							}
						}else{
							Token nextToken = this.p.getLfc().readNextToken();
							this.errors.printError(nextToken, new Token(this.tks.NUMBER, "Number"), new Token(this.tks.IDENTIFIER, "Identifier"));
							this.expected(new Token(this.tks.SEMICOLON, ";"));
							b = false;
							break;
						}
					}
				}else{
					if(scopeCall){
						this.pList.addParameter(this.makeSkopeField(m, t));
					}else{
						this.pList.addParameter(this.makeField(m, t));
					}
				}
			}else{
				this.p.getLfc().readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
				break;
			}
		}

		
		if(b){
			Token t = this.expected(new Token(this.tks.SEMICOLON, ";"));
			if(this.iskCorrectToken(t)){
				str = str + t.getText();
				b = this.checkParametersCompatability();
			}else{
				b = false;
			}
		}
		if(!isFieldAlreadyDeclared && b){
			if(m.isContainingFild(f.getName())){
				this.errors.printContainsFieldError(f.getToken());
				b = false;
			}
		}
		/*if(b){
			
			if(this.ifparser == null){
				this.addByteWriterToMethodBody(str,m);
			}else{
				this.addByteWriterToIfScope(str, m);
			}
		}*/
		
		return b;
	}

/*******************************************************************************************
 *  addArithmeticFieldWriter(Method m)
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	public void addArithmeticFieldWriter(Method m, Field classRef) {
		if(this.bodyParser == null){
			this.addByteWriterToMethodBody(str,m, classRef);
		}else{
			this.addByteWriterToIfScope(str, m, classRef);
		}
		
	}
/*******************************************************************************************
 *  addByteWriterToIfScope(String str, Method m) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private void addByteWriterToIfScope(String str, Method m, Field classReference) {//Method m, Token name, Field fieldFromClassRef, Class clazz, int position, Field classRef
		LookForwardScanner lfc;
		try {
			lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new StringReader(str))));
			ArithmeticExpression ax = new ArithmeticExpression(lfc, m.getFieldMap(), m, classReference, str) ;
			//System.out.println(ax.getExpressionCode().size());
			//ax.getExpressionCode().printByteArray();
			ax.make(false);
			//System.out.println(ax.getExpressionCode().size() + "   size");
			this.bodyParser.addExpresssion(ax);
			//this.ifparser.setByteWriter(ax.getExpressionCode());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

/*******************************************************************************************
 *  addByteWriterToMethodBody(String str, Method m) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private void addByteWriterToMethodBody(String str, Method m, Field classReference) {
		LookForwardScanner lfc;
		try {
			lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new StringReader(str))));
			ArithmeticExpression ax = new ArithmeticExpression(lfc, m.getFieldMap(), m, classReference, str);
			//System.out.println(ax.getExpressionCode().size());
			//ax.getExpressionCode().printByteArray();
			ax.make(false);
			m.addExpresssion(ax);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

/*******************************************************************************************
 *  parseLogicalExpression(Method m) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	public boolean parseLogicalExpression(Method m) {
		boolean b = true;	
		//System.out.println(this.p.getLfc().lookAhead().getText());
		if(this.isNextToken()){
			Token name1 = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"), new Token(this.tks.NUMBER, "Number"));
			if(this.iskCorrectToken(name1)){
				if(this.isNextToken(new Token(this.tks.DOT))){
					this.expected(new Token(this.tks.DOT, "."));
					if(this.isNextToken(new Token(this.tks.IDENTIFIER))){
						Token refField1 = this.p.getLfc().readNextToken();
						this.str = this.str + name1.getText() + "." + refField1.getText();
						this.checkReferenceFieldExistenceAndParseArrays(m, name1, refField1);
						if(this.isNextTokenOperator()){
							this.str = this.str + this.p.getLfc().readNextToken().getText();
							Token name3 = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
							if(name3.getToken() != -1){
								this.checkTypeCompatability(name1, refField1, name3, null);
								this.str = this.str + name3.getText();
							}else{
								this.p.getLfc().readNextToken();
								this.expected(new Token(this.tks.SEMICOLON, ";"));
								b = false;
							}
						}
						Token operator =  this.expectedComparableOperator();
						if(operator.getToken() != -1){
							this.str= this.str + operator.getText();
							Token name2;
							if(this.isNextToken(new Token(this.tks.NUMBER))){
								name2 = this.expected(new Token(this.tks.NUMBER, "Number"));
								this.checkTypeCompatability(name1, refField1, name2, null);
								this.str = this.str + name2.getText();
							}else if(this.isNextToken(new Token(this.tks.IDENTIFIER))){
								name2 = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
								if(this.isNextToken(new Token(this.tks.DOT))){
									this.expected(new Token(this.tks.DOT, "."));
									if(this.isNextToken(new Token(this.tks.IDENTIFIER))){
										Token refField2 = this.p.getLfc().readNextToken();
										this.str = this.str  + name2.getText() + "." + refField2.getText();
										this.checkReferenceFieldExistenceAndParseArrays(m, name2, refField2);
										if(!this.p.getError()){
											this.checkTypeCompatability(name1, refField1, name2, refField2);
										}
									}else{
										this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
										this.p.getLfc().readNextToken();
										this.expected(new Token(this.tks.SEMICOLON, ";"));
										b = false;
									}
								}else{
									this.str = this.str + operator.getText() + name2.getText();
									this.checkTypeCompatability(name1, refField1, name2, null);
								}
							}else{
								this.expected(new Token(this.tks.IDENTIFIER, "Identifier"), new Token(this.tks.NUMBER, "Number"));
								this.p.getLfc().readNextToken();
								this.expected(new Token(this.tks.SEMICOLON, ";"));
								b = false;
							}
						}else{
							this.expected(new Token(this.tks.SEMICOLON, ";"));
							b = false;
						}
					}else{
						this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
						this.p.getLfc().readNextToken();
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
					}
				}else if(this.isNextExpectedComparableOperator()){
					Token operator = this.expectedComparableOperator();
					Token name2 = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"), new Token(this.tks.NUMBER, "Number"));
					if(this.iskCorrectToken(name2)){
						Field f1 = this.makeSkopeField(m, name1);
						Field f2 = this.makeSkopeField(m, name2);
						this.pList.addParameter(f1);
						this.pList.addParameter(f2);
						this.str = f1.getName() + " " + operator.getText() + " " + f2.getName();
						b = this.checkParametersCompatability();
					}else{
						this.p.getLfc().readNextToken();
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
					}
				}else{
					Token t = this.p.getLfc().readNextToken();
					this.errors.printComparableOperatorError(t);
					this.p.getLfc().readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b = false;
				}
			}else{
				this.p.getLfc().readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
		}else{
			//TODO implement char and boolean
			this.expected(new Token(this.tks.IDENTIFIER, "Identfier"), new Token(this.tks.NUMBER, "Number"));
			this.p.getLfc().readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
			b=false;
		}
		return b;
	}
	
/**********************************************************************************************************************
 *  void checkTypeCompatability(Token name1, Token refField1, Token name2, Token refField2)
 *  	- 
 *********************************************************************************************************************/
	private void checkTypeCompatability(Token name1, Token refField1, Token name2, Token refField2) {
		//TODO TO FINISH THE CODE FOR COMPARATION BETWEEN TWO VARIABLE TYPES
	}

/**********************************************************************************************************************
 *  boolean checkReferenceFieldExistenceAndParseArrays(Method m, Token name1, Token refField1)
 *  	- 
 *********************************************************************************************************************/
	private boolean checkReferenceFieldExistenceAndParseArrays(Method m, Token name1, Token refField1) {
		boolean b = true;
		if(m.isContainingFildMethodAndClassAndLoops(name1.getText(), false)){
			Field classRef = m.findFieldInsideMethoAndClassAndScope(name1.getText());
			if(!classRef.getType().isClass()){
				this.p.setError(true);
				System.out.println("Errot at: Class ExpressionParser, method: checkReferenceFieldExistenceAndParseArrays(Method m, Token name1, Token refField1)");
			}else{
				if(classRef.getClazz().isAllreadyContainingField(refField1.getText())){
					Field fieldRef = classRef.getClazz().getFieldFromClassFieldsByName(refField1.getText());
					if(fieldRef.getType().isArray()){
						Token t = this.expected(new Token(this.tks.SQUARE_BRACKET_OPEN, "["));
						if(t.getToken() != -1){
							Token nextT = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"), new Token(this.tks.NUMBER, "Number"));
							if(nextT.getToken() != -1){
								if(nextT.getToken() == this.tks.IDENTIFIER){
									if(m.isContainingFildMethodAndClassAndLoops(nextT.getText(), false)){
										Field counter = m.findFieldInsideMethoAndClassAndScope(nextT.getText());
										if(counter.getType().isInteger()){
											this.str = this.str + t.getText() + nextT.getText();
										}else{
											this.p.setError(true);
											System.out.println("Errot at: Class ExpressionParser, method: checkReferenceFieldExistenceAndParseArrays(Method m, Token name1, Token refField1)");
										}
									}else{
										this.p.setError(true);
										this.errors.printFieldDoesNotExists(nextT);
									}
								}else{
									this.str = this.str + t.getText() + nextT.getText();
								}
								Token t1 = this.expected(new Token(this.tks.SQUARE_BRACKET_CLOSE, "]"));
								if(t1.getToken() != -1){
									this.str = this.str + t1.getText();
								}else{
									this.p.getLfc().readNextToken();
									this.expected(new Token(this.tks.SEMICOLON, ";"));
									b = false;
								}
							}else{
								this.p.getLfc().readNextToken();
								this.expected(new Token(this.tks.SEMICOLON, ";"));
								b = false;
							}
						}else{
							this.p.getLfc().readNextToken();
							this.expected(new Token(this.tks.SEMICOLON, ";"));
							b = false;
						}
					}
				}else{
					this.p.setError(true);
					this.errors.printReferenceDoesNotExist(refField1);
				}
			}
		}else{
			this.p.setError(true);
			this.errors.printFieldDoesNotExists(name1);
		}
		return b;
	}

/*******************************************************************************************
 *  addLogicalFieldWriter(Method m)
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	public void addLogicalFieldWriter(Method m) {
		this.addByteWriterToLogicalIfScope(m);
		
	}
/*******************************************************************************************
 *  addByteWriterToLogicalIfScope(Method m) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private void addByteWriterToLogicalIfScope(Method m) {
		LookForwardScanner lfc;
		try {
			
			lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new StringReader(this.str))));
			CoditionExpression ax = new CoditionExpression(lfc, m.getFieldMap(), m);
			//System.out.println(ax.getExpressionCode().size());
			//ax.getExpressionCode().printByteArray();
			this.bodyParser.addExpresssion(ax);
			//m.addExpresssion(ax);
			//this.ifparser.setByteWriter(ax.getExpressionCode());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

/***************************************************************************************
 *  - checkParametersCompatability()
 *  	- return true, if the next token and the field before equal are the same type
 *  	- otherwise false
 **************************************************************************************/	
	private boolean checkParametersCompatability() {
		boolean b = true;
		/*for(int i = 0; i < this.pList.getSize(); i++){
			if(this.pList.getParameter(i).getType() == null){
				this.errors.identifierDoesNotExistsError(this.pList.getParameter(i).getToken());
				b = false;
			}
		}*/
		if(this.pList.getParameter(0).getType() == null){
			this.errors.identifierDoesNotExistsError(this.pList.getParameter(0).getToken());
			b = false;
		}
		if(!b){
			return b;
		}
		for(int i = 1; i < this.pList.getSize(); i++){
			
			if(this.pList.getParameter(i).getType() == null){
				this.errors.identifierDoesNotExistsError(this.pList.getParameter(i).getToken());
				b = false;
			}else{
				
				if(this.pList.getParameter(0).getType().isClass()){
					
					if(!this.pList.getParameter(0).getType().getDescriptor().equals(this.pList.getParameter(i).getType().getDescriptor())){
						this.errors.printNotCompatibleTypes(this.pList.getParameter(0), this.pList.getParameter(i));
						b = false;
					}
				}else if(this.pList.getParameter(0).getType().isArray()){
					if(this.pList.getParameter(0).getType().getBaseType().getType() != this.pList.getParameter(i).getType().getType()){
						this.errors.printNotCompatibleTypes(this.pList.getParameter(0), this.pList.getParameter(i));
						b = false;
					}
				}
				else{
					if(this.pList.getParameter(0).getType().getType() != this.pList.getParameter(i).getType().getType()){
						this.errors.printNotCompatibleTypes(this.pList.getParameter(0), this.pList.getParameter(i));
						b = false;
					}
				}
			}
		}
		return b;
	}
/**************** TO be checked what is used and what not ****************************/	


/***************************************************************************************
 *  - iskCorrectToken(Token t) 
 *  	- return true if the token t is not an unknown token
 *  	- otherwise false
 **************************************************************************************/
	private boolean iskCorrectToken(Token t){
		if(t.getToken() != -1){
			return true;
		}
		return false;
	}
/***************************************************************************************
 *  - isNextToken() - checks if the next token is char, int, indetifier or string 
 *  - return true if it is otherwise false
 **************************************************************************************/
	private boolean isNextToken(){
		if((this.p.getLfc().lookAhead().getToken() == this.tks.INT) || (this.p.getLfc().lookAhead().getToken() == this.tks.CHAR) 
				|| (this.p.getLfc().lookAhead().getToken() == this.tks.IDENTIFIER)){
			return true;
		}
		return false;
	}
	
/***************************************************************************************
 *  - isNextToken(Token t) - checks if the next token is the same as the parameter t 
 *  - return true if it is otherwise false
 **************************************************************************************/
	private boolean isNextToken(Token t){
		Token t1 = this.p.getLfc().lookAhead();
		if(t1.getToken() == t.getToken()){
			return true;
		}
		return false;
	}
/***************************************************************************************
 *  expected(Token token) 
 *  	-
 **************************************************************************************/
	private Token expected(Token token) 
	{
		if(this.p.getLfc().lookAhead().getToken() != token.getToken()){
			this.errors.printError(this.p.getLfc().lookAhead(), token);
			this.p.setError(true);
			return new Token(-1 , "Unknown Token");
		}
		return this.p.getLfc().readNextToken();
	}
	
/*******************************************************************************************
 *  expected((Token token, Token token2)) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private Token expected(Token token1, Token token2) {
		Token t = this.p.getLfc().lookAhead();
		if((t.getToken() != token1.getToken()) && (t.getToken() != token2.getToken())){
			this.errors.printError(t, token1, token2);
			this.p.setError(true);
			return new Token(-1 , "Unknown Token");
		}
		t = this.p.getLfc().readNextToken();
		return t;
		
	}
/***************************************************************************************
 *  expected(Token token) 
 *  	-
 **************************************************************************************/
	private Token expectedComparableOperator() 
	{
		Token t = this.p.getLfc().lookAhead();
		if((t.getToken() != this.tks.LESS) && (t.getToken() != this.tks.LESS_EQUAL) && (t.getToken() != this.tks.GREATER) && (t.getToken() != this.tks.GREATER_EQUAL) 
				&& (t.getToken() != this.tks.EQUAL) &&  (t.getToken() != this.tks.UNEQUAL)){
			this.errors.printComparableOperatorError(t);
			return new Token(-1 , "Unknown Token");
		}
		return this.p.getLfc().readNextToken();
	}
/***************************************************************************************
 *  boolean isNextExpectedComparableOperator() 
 *  	-
 **************************************************************************************/
	private boolean isNextExpectedComparableOperator() 
	{
		Token t = this.p.getLfc().lookAhead();
		if((t.getToken() != this.tks.LESS) && (t.getToken() != this.tks.LESS_EQUAL) && (t.getToken() != this.tks.GREATER) && (t.getToken() != this.tks.GREATER_EQUAL) 
				&& (t.getToken() != this.tks.EQUAL) &&  (t.getToken() != this.tks.UNEQUAL)){
			return false;
		}
		return true;
	}
/*******************************************************************************************
 *  isNextTokenSubAddOperator() 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private boolean isNextTokenSubAddOperator(){
		Token t1 = this.p.getLfc().lookAhead();
		if((t1.getToken() == this.tks.ADD) || (t1.getToken() == this.tks.SUB)){
			return true;
		}
		return false;
	}
/*******************************************************************************************
 *  isNextTokenOperator() 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private boolean isNextTokenOperator(){
		Token t1 = this.p.getLfc().lookAhead();
		if((t1.getToken() == this.tks.ADD) || (t1.getToken() == this.tks.SUB) || (t1.getToken() == this.tks.DIV) || (t1.getToken() == this.tks.MULT)){
			return true;
		}
		return false;
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
 *  makeField(Token t)
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private Field makeField(Method m, Token t){
		Field f;
		if(this.isIdentifierToken(t)){
			if(m.isContainingFildMethodAndClass(t.getText(),false)){
				f = m.findFieldInsideMethoAndClass(t.getText());
			}else{
				f = new Field(null, t.getText(), null, t);
			}
		}else{
			f = new Field(new Type(new Token(this.tks.INT, "int")), t.getText(), null, t);
		}
		return f;
	}
	
/*******************************************************************************************
 *  makeField(Token t)
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private Field makeSkopeField(Method m, Token t){
		Field f;
		if(this.isIdentifierToken(t)){
			if(m.isContainingFildMethodAndClassAndLoops(t.getText(), false)){
				f = m.findFieldInsideMethoAndClassAndScope(t.getText());
			}else{
				f = new Field(null, t.getText(), null, t);
				//this.errors.printFieldDoesNotExists(t);
				//this.p.setError(true);
			}
		}else{
			f = new Field(new Type(new Token(this.tks.INT, "int")), t.getText(), null, t);
		}
		return f;
	}
	
/*******************************************************************************************
 *  boolean parseTokenNew(Field f)
 *  		- 
 *******************************************************************************************/
	public boolean parseTokenNew(Field f, Method m) {
		boolean b = true;
		Token type = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"), new Token(this.tks.INT, "int"));
		if(type.getToken() != -1){
			if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
				this.p.getLfc().readNextToken();
				
				Token numberOrIdentfier = this.expected(new Token(this.tks.NUMBER, "Number"), new Token(this.tks.IDENTIFIER, "Identfier"));
				if(numberOrIdentfier.getToken() != -1){
					Token to = this.expected(new Token(this.tks.SQUARE_BRACKET_CLOSE,"]"));
					if(to.getToken() != -1){
						//this.str = numberOrIdentfier.getText();
						
						to = this.expected(new Token(this.tks.SEMICOLON, ";"));
						if(to.getToken() == -1){
							
							this.p.getLfc().readNextToken();
							b=false;
						}
						if(this.isIdentifierToken(numberOrIdentfier)){
							//TODO to implement for checking inside loop 
							if(m.isContainingFildMethodAndClassAndLoops(numberOrIdentfier.getText(), false)){
								Field field = m.findFieldInsideMethoAndClassAndScope(numberOrIdentfier.getText());
								
								if(!field.getType().isInteger()){
									Field f1 = new Field(new Type(numberOrIdentfier.getToken()), numberOrIdentfier.getText(), null, numberOrIdentfier);
									Field f2 = new Field(new Type(this.tks.INT), numberOrIdentfier.getText(), null, numberOrIdentfier);
									this.errors.printNotCompatibleTypes(f1, f2);
									this.p.setError(true);
								}
							}
						}

						
						this.str = numberOrIdentfier.getText();
						if(b){
							if(!this.isSameType(f.getType(), new Type(new Type(type)))){
								this.errors.printNotCompatibleClasses(f,type);
								this.p.setError(true);
							}
						}
					}else{
						this.p.getLfc().readNextToken();
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b=false;
					}
				}else{
					this.p.getLfc().readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b=false;
				}
			}else if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_OPEN))){
				this.expected(new Token(this.tks.ROUND_BRACKET_OPEN,"("));
				//TODO to make it work(it works for no parameters)
				b = this.parseMethodCallParametersOrNewObjectCreation(f);
				if(b){
					if(!this.isSameType(f.getType(), new Type(type))){
						this.errors.printNotCompatibleClasses(f,type);
						this.p.setError(true);
					}
				}
			}else{
				this.p.getLfc().readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
			}
		}else{
			this.p.getLfc().readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
			b = false;
		}

		
		return b;
	}
/*******************************************************************************************
*  boolean checkTypes(Type type, Type type2)
*  		- 
*******************************************************************************************/
	private boolean isSameType(Type type, Type type2) {
		return type.toString().equals(type2.toString());
	}
/*******************************************************************************************
 *  boolean parseMethodCallParametersOrNewObjectCreation(Field f)
 *  		- 
 *******************************************************************************************/
	private boolean parseMethodCallParametersOrNewObjectCreation(Field f) {
		boolean b = true;
		Token t = this.expected(new Token(this.tks.ROUND_BRACKET_CLOSE,")"));
		if(t.getToken()== -1){
			this.p.getLfc().readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
			b = false;
		}
		if(b){
			t = this.expected(new Token(this.tks.SEMICOLON, ";"));
			if(t.getToken() == -1){
				this.p.getLfc().readNextToken();
				b=false;
			}
		}
		return b;
	}

/*******************************************************************************************
 *  void addNewFieldWriter(Method m)
 *  	-  
 *******************************************************************************************/
	public void addNewFieldWriter(Method m, Field f) {
		if(this.bodyParser == null){
			this.addNewByteWriterToMethodBody(f,m);
		}else{
			//this.addByteWriterToIfScope(f., m);
		}
		
	}
/*******************************************************************************************
 *  private void addNewByteWriterToMethodBody(String str2, Method m)
 *  	-  
 *******************************************************************************************/
	private void addNewByteWriterToMethodBody(Field f, Method m) {
		LookForwardScanner lfc;
		try {
			lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new StringReader(str))));
			NewExpression newex = new NewExpression(lfc, m.getFieldMap(), m, f);
			//System.out.println(ax.getExpressionCode().size());
			//newex.getExpressionCode().printByteArray();
			newex.getCode();
			//newex.getExpressionCode().printByteArray();
			m.addExpresssion(newex);
			//m.pr
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
/*******************************************************************************************
 *  boolean parseFieldRefExpression()
 *  	-  
 *******************************************************************************************/
	public boolean parseFieldRefExpression(Token fieldRef, Method m, Class classRef) {
		boolean b = true;
		this.pList.addParameter(this.makeField(classRef, fieldRef));
		if(this.isNextTokenSubAddOperator()){
			this.p.getLfc().readNextToken();
		}
		
		this.str = "= ";
		
		//TODO PARSE ERRORS FOR ARITHMETIC EXRPESSIONS WITH REF
		while(!this.isNextToken(new Token(this.tks.SEMICOLON, ";"))){
			this.str = this.str + this.p.getLfc().readNextToken().getText();
			//System.out.println("TUKA");
			
			/*Token t;
			if(this.isNextToken(new Token(this.tks.IDENTIFIER)) || this.isNextToken(new Token(this.tks.NUMBER))){
				t = this.expected(new Token(this.tks.NUMBER, "Number"), new Token(this.tks.IDENTIFIER, "Identifier"));
				this.str = this.str +  " " + t.getText();
				if(!this.isNextToken(new Token(this.tks.SEMICOLON, ";"))){
					if(this.isNextTokenOperator()){
						this.str = str + " " +this.p.getLfc().readNextToken().getText();
					}else if(this.isn){
						
					}
					
				}else{
					this.pList.addParameter(this.makeField(m, t));
				}
			}
			
			//Token t = this.expected(new Token(this.tks.NUMBER, "Number"), new Token(this.tks.IDENTIFIER, "Identifier"));
			/*if(this.iskCorrectToken(t)){
				this.str = this.str +  " " + t.getText();
				if(!this.isNextToken(new Token(this.tks.SEMICOLON, ";"))){
					if(!this.isNextTokenOperator()){
						Token err = this.p.getLfc().readNextToken();
						this.errors.printExpectsOperatorError(err);
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
						break;
					}else{
						this.str = str + " " +this.p.getLfc().readNextToken().getText();
						if(this.isNextToken(new Token(this.tks.NUMBER)) || this.isNextToken(new Token(this.tks.IDENTIFIER))){
							this.pList.addParameter(this.makeField(m, t));
						}else{
							Token nextToken = this.p.getLfc().readNextToken();
							this.errors.printError(nextToken, new Token(this.tks.NUMBER, "Number"), new Token(this.tks.IDENTIFIER, "Identifier"));
							this.expected(new Token(this.tks.SEMICOLON, ";"));
							b = false;
							break;
						}
					}
				}else{
					this.pList.addParameter(this.makeField(m, t));
				}
			}else{
				this.p.getLfc().readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
				break;
			}*/
		}

		if(b){
			Token t = this.expected(new Token(this.tks.SEMICOLON, ";"));
			if(this.iskCorrectToken(t)){
				str = str + t.getText();
				//this.p.setError(!this.checkParametersCompatability());
			}else{
				b = false;
			}
		}
		return b;
	}
/*******************************************************************************************
 *  Field makeField(Class classRef, Token fieldRef)
 *  	-  
 *******************************************************************************************/
	private Field makeField(Class classRef, Token fieldRef) {
		Field f;
		if(this.isIdentifierToken(fieldRef)){
			if(classRef.isAllreadyContainingField(fieldRef.getText())){
				f = classRef.getFieldFromClassFieldsByName(fieldRef.getText());
			}else{
				f = new Field(null, fieldRef.getText(), null, fieldRef);
			}
		}else{
			f = new Field(new Type(new Token(this.tks.INT, "int")), fieldRef.getText(), null, fieldRef);
		}
		return f;
	}
/*******************************************************************************************
 *  void addArithmeticFieldRefWriter(Method m, Token name, Field fieldFromClassRef, Class clazz)
 *  	-  
 *******************************************************************************************/
	public void addArithmeticFieldRefWriter(Method m, Field fieldFromClassRef, Class clazz, int position, Field classRef) {
		LookForwardScanner lfc;
		try {
			lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new StringReader(this.str))));
			ArithmeticExpression ar = new ArithmeticExpression(clazz, m.getFieldMap(), m,lfc, fieldFromClassRef, position, classRef, this.str);
			ar.make(true);
			//ar.getExpressionCode().printByteArray();
			m.addExpresssion(ar);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
/*******************************************************************************************
*  void addArithmeticFieldRefWriterInScope(Method m, Token name, Field fieldFromClassRef, Class clazz)
*  	-  
*******************************************************************************************/
	public void addArithmeticFieldRefWriterInScope(Method m, Field fieldFromClassRef, Class clazz, int position, Field classRef) {
		LookForwardScanner lfc;
		try {
			lfc = new LookForwardScanner(new Scanner(new LookForwardReader(new StringReader(this.str))));
			ArithmeticExpression ar = new ArithmeticExpression(clazz, m.getFieldMap(), m,lfc, fieldFromClassRef, position, classRef, this.str);
			ar.make(true);
			//ar.getExpressionCode().printByteArray();
			this.bodyParser.addExpresssion(ar);
		} catch (IOException e) {
			e.printStackTrace();							
		}
		
	}
/*******************************************************************************************
 *  void addNewFieldRefWriter(Method m, Field fieldFromClassRef, Class clazz)
 *  	-  
 *******************************************************************************************/
	public void addNewFieldArrayWriter(Method m, Field fieldArray, Class clazz, Field classRef) {
		NewArrayExpression narray = new NewArrayExpression(m, fieldArray, clazz, this.str, classRef);
		//System.out.println(this.str);
		narray.getCode();
		//narray.getExpressionCode().printByteArray();
		m.addExpresssion(narray);
	}
/*******************************************************************************************
 *  void addNewFieldRefWriter(Method m, Field fieldFromClassRef, Class clazz)
 *  	-  
 *******************************************************************************************/
	/*public void parseArrayExpression() {
		
	}*/
/*******************************************************************************************
 *  boolean parseReturnRef(Field f)
 *  	-  
 *******************************************************************************************/
	public boolean parseReturnRef(Field f, Class clazz, Method m) {
		boolean b = true;
		Token fieldRef = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
		if(fieldRef.getToken() != -1){
			//Token t = this.expected(new Token(this.tks.SEMICOLON, ";"));
			Field fieldReference = clazz.getFieldFromFieldRef(fieldRef.getText());
			if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
				this.expected(new Token(this.tks.SQUARE_BRACKET_OPEN, "["));
				Token arrPos = this.expected(new Token(this.tks.NUMBER,"Number"));
				if(arrPos.getToken() != -1){
					int arrPossition = Integer.parseInt(arrPos.getText());
					Token t = this.expected(new Token(this.tks.SQUARE_BRACKET_CLOSE, "]"));
					if(t.getToken() != -1){
						if(this.isNextToken(new Token(this.tks.SEMICOLON))){
							
							if(!m.isContainingFildMethodAndClass(f.getName(), false)){
								this.errors.identifierDoesNotExistsError(f.getToken());
								this.p.setError(true);
							}else if(!clazz.isAllreadyContainingFieldReference(fieldRef.getText())){
								//TODO Implement to search it with byte reader
								this.errors.printReferenceDoesNotExist(fieldRef);
								this.p.setError(true);
							}else if(!fieldReference.getType().isArray()){
								this.errors.printExpectedArrayFieldRef(fieldReference);
								b = false;
								this.p.setError(true);
							}else if(m.getRetrunType().getType() != fieldReference.getType().getBaseType().getType()){
								this.errors.printNotCompatibleTypes(fieldReference, m);
								this.p.setError(true);
							}else if(arrPossition >= fieldReference.getSize()){
								//TODO
								System.out.println("ERROR SIZE OVERFLOW!");
								this.p.setError(true);
							}else{
								if(!this.p.getError()){
									ArithmeticExpression ar = new ArithmeticExpression(clazz, m.getFieldMap(), m, null, fieldReference, arrPossition, f, "");
									ar.makeForRetrun();
									m.addExpresssion(ar);
								}
							}
						}else{
							this.expected(new Token(this.tks.SEMICOLON, ";"));
							this.p.getLfc().readNextToken();
							b = false;
						}
					}else{
						this.p.getLfc().readNextToken();
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
					}
				}else{
					this.p.getLfc().readNextToken();
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b = false;
				}
			}else if(this.isNextToken(new Token(this.tks.SEMICOLON))){
				//TODO
				if(m.getRetrunType().getType() != fieldReference.getType().getType()){
					this.errors.printNotCompatibleTypes(fieldReference, m);
					this.p.setError(true);
				}else{
					if(!this.p.getError()){
						ArithmeticExpression ar = new ArithmeticExpression(clazz, m.getFieldMap(), m, null, fieldReference, -1, f, "");
						ar.makeForRetrun();
						m.addExpresssion(ar);
					}
				}
			}else{
				this.expected(new Token(this.tks.SEMICOLON));
				this.p.getLfc().readNextToken();
				b = false;
			}
		}else{
			this.p.getLfc().readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
			b = false;
		}
		return b;
		
	}
	
	public void addString(String s){
		this.str = s;
	}
	
	public String getString(){
		return this.str;
	}
}
