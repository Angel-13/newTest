package milestone2;

import Code.Expression;
import compileTable.ByteWriter;

import symbolTable.ExpressionsList;
import symbolTable.Field;
import symbolTable.Method;
import symbolTable.ParameterList;
import symbolTable.StackMapTableList;
import symbolTable.StackMapTableObject;
import symbolTable.Type;
import tokens.Token;
import tokens.Tokens;

public class IfParser implements BodyParser {
	
	private final Method method;
	
	private IfParser ifparser;
	
	private LoopParser loopparser;
	
	private final Parser parser;
	
	private final Tokens tks;
	
	private final ErrorsClass errors;
	
	private ParameterList pList;
	
	private boolean expectingElse;
	
	private ByteWriter bWriter;
	
	private ExpressionsList expressions;
	
	private final StackMapTableList stackmap;
	
	private int startPos;
	
	public IfParser(Method m, Parser p, int start){
		this.method = m;
		this.parser = p;
		this.tks = new Tokens();
		this.errors = new ErrorsClass();
		this.pList= new ParameterList();
		this.ifparser = null;
		this.loopparser = null;
		this.expectingElse = false;
		this.bWriter = new ByteWriter();
		this.expressions = new ExpressionsList();
		this.stackmap = new StackMapTableList();
		this.startPos = start;
	}
	
	public boolean parse() {
		boolean b = true;
		Token t = this.expected(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
		if(this.iskCorrectToken(t)){
			ExpressionParser expression = new ExpressionParser(this.parser, this);
			b = expression.parseLogicalExpression(this.method);
			if(b){
					if(!this.parser.getError()){
						expression.addLogicalFieldWriter(this.method);
					}
					Token logicalOperator = new Token(-1);
					
					while(this.isNextTokenLogicOperatot()){
						logicalOperator = this.parser.getLfc().readNextToken();
						//String s = this.parser.getLfc().readNextToken().getText();
						//expression.addString(s);
						 expression.addString("");
						if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_OPEN))){
							this.expected(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
							b = expression.parseLogicalExpression(this.method);
							if(b){
								Token closeBracket = this.expected(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"));
								if(closeBracket.getToken() == -1){
									this.parser.getLfc().readNextToken();
									this.expected(new Token(this.tks.SEMICOLON, ";"));
									b = false;
								}
							}
						}else{
							b = expression.parseLogicalExpression(this.method);
						}
						if(!this.parser.getError()){
							expression.addLogicalFieldWriter(this.method);
						}
					}
					
					if(b){
						Token closeBracket = this.expected(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"));
						if(closeBracket.getToken() == -1){
							this.parser.getLfc().readNextToken();
							this.expected(new Token(this.tks.SEMICOLON, ";"));
							b = false;
						}
					}
					
					t = this.expected(new Token(this.tks.CURLY_BRACKET_OPEN, "{"));
					if(this.iskCorrectToken(t)){
						this.expectingElse = true;
						b = this.parseIfElseBody(logicalOperator);
					}else{
						this.parser.getLfc().readNextToken();
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b=false;
					}
			}
		}else{
			this.parser.getLfc().readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
			b=false;
		}
		return b;
	}

/***************************************************************************************
 *  - parseIfElseBody(Token t) 
 *  	- return true if the token t is not an unknown token
 *  	- otherwise false
 **************************************************************************************/
	private boolean parseIfElseBody(Token logicalOperator) {
		boolean b = true;
		while(!this.isNextToken(new Token(this.tks.CURLY_BRACKET_CLOSE))){
			if(this.isNextToken()){
				b = this.prepareForParseExpression();
				if(!b){
					break;
				}
			}else if(this.isNextToken(new Token(this.tks.IF))){
				this.expected(new Token(this.tks.IF));
				//this.ifparser = new IfParser(this.method, this.parser);
				b = this.ifparser.parse();
				if(!b){
					break;
				}
			}else if(this.isNextToken(new Token(this.tks.WHILE))){
				this.expected(new Token(this.tks.WHILE));
				//this.loopparser = new LoopParser(this.method, this.parser);
				b = this.loopparser.parse();
				if(!b){
					break;
				}
			}else{
				this.expected(new Token(this.tks.CURLY_BRACKET_CLOSE,"}"));
				this.parser.getLfc().readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b = false;
				break;
			}
		
		
		}
		if(b){
			Token t = this.expected(new Token(this.tks.CURLY_BRACKET_CLOSE,"}"));
			if(!this.iskCorrectToken(t)){
				this.parser.getLfc().readNextToken();
				b = false;
				this.expected(new Token(this.tks.SEMICOLON,	";"));
			}
		}
		if(b && this.isNextToken(new Token(this.tks.ELSE)) && this.expectingElse){
			this.expected(new Token(this.tks.ELSE));
			int value;
			if(!this.parser.getError()){
				int stackMapTableListSize = this.method.getStackMapTableList().size();
				
				
				if(logicalOperator.getToken() != -1){
					value = this.getLengthFromAllExpressions() + 2 - (this.expressions.get(0).getExpressionCode().size()-1) + 5;
					this.expressions.get(0).getExpressionCode().write2Byte(value);
					value = value - (this.expressions.get(0).getExpressionCode().size() + this.expressions.get(1).getExpressionCode().size())+2 + 1;
					this.expressions.get(1).getExpressionCode().write2Byte(value);
				}else{
					value = this.getLengthFromAllExpressions() + 2 - (this.expressions.get(0).getExpressionCode().size()-1) + 3;
					this.expressions.get(0).getExpressionCode().write2Byte(value);
				}
				if(stackMapTableListSize > 1){
					value =  this.startPos +  this.getLengthFromAllExpressions()-1 + 3;
				}else{
					value =  this.startPos +  this.getLengthFromAllExpressions() + 3;
					
				}
				/*if(!this.method.getStackMapTableList().isEmpty()){
					this.startPos = this.startPos +  this.getLengthFromAllExpressions() - 1 + 3;
					System.out.println(this.startPos);
				}else{
					this.startPos = this.startPos +  this.getLengthFromAllExpressions() + 3;
				}*/
				StackMapTableObject stcObj = new StackMapTableObject(value, this.method.getStackFrameFieldCounter());
				if(!this.method.getStackFrameFieldCounter().isEmpty()){
					stcObj.makeAppendFrame(this.method);
					this.method.getStackFrameFieldCounter().clear();
				}else{
					stcObj.makeSameFrame();
				}
				this.method.addToStackMapTableList(stcObj);
			}
			int expressionListSizeBefore = this.expressions.size()-1;
			this.expressions.get(expressionListSizeBefore).getExpressionCode().write1Byte(0xa7);

			b = this.parseElse();			
			
			if(!this.parser.getError()){
				value = this.getLengthFromAllExpressionsFromNumber(expressionListSizeBefore) + 2 + 1;
				this.expressions.get(expressionListSizeBefore).getExpressionCode().write2Byte(value);
				
				int stackMapTableListSize = this.method.getStackMapTableList().size();
				if(stackMapTableListSize > 1){
					value = this.getLengthFromAllExpressions()-1 - this.method.getStackMapTableList().get(stackMapTableListSize-1).getPosition() - 1 ;
				}else{
					//value = this.startPos + this.getLengthFromAllExpressions()-1;
					value = this.startPos + this.expressions.get(this.expressions.size()-1).getExpressionCode().size()-1;
				}
				StackMapTableObject stcObj = new StackMapTableObject(value, this.method.getStackFrameFieldCounter());
				if(!this.method.getStackFrameFieldCounter().isEmpty()){
					stcObj.makeAppendFrame(this.method);
					this.method.getStackFrameFieldCounter().clear();
				}else{
					stcObj.makeSameFrame();
				}
				this.method.addToStackMapTableList(stcObj);
			}
			
		}else if(!this.parser.getError() && this.expectingElse){
			int value = this.getLengthFromAllExpressions() + 2 - (this.expressions.get(0).getExpressionCode().size()-1);
			this.expressions.get(0).getExpressionCode().write2Byte(value);
			int stackMapTableListSize = this.method.getStackMapTableList().size();
			if(stackMapTableListSize > 1){
				value = this.getLengthFromAllExpressions()-1 - this.method.getStackMapTableList().get(stackMapTableListSize-1).getPosition() - 1 ;
			}else{
				value = this.startPos + this.getLengthFromAllExpressions()-1;
			}
			/*System.out.println(this.startPos+ "    TUJKAAAAAA");
			if(!this.method.getStackMapTableList().isEmpty()){
				this.startPos = this.startPos +  this.getLengthFromAllExpressions() - 1;
			}else{
				this.startPos = this.startPos +  this.getLengthFromAllExpressions();
			}*/
			StackMapTableObject stcObj = new StackMapTableObject(value, this.method.getStackFrameFieldCounter());
			if(!this.method.getStackFrameFieldCounter().isEmpty()){
				stcObj.makeAppendFrame(this.method);
				this.method.getStackFrameFieldCounter().clear();
			}else{
				stcObj.makeSameFrame();
			}
			this.method.addToStackMapTableList(stcObj);
		}
		this.pList = new ParameterList();
		return b;
	}
/***************************************************************************************
 *  - parseElse() 
 *  	- 
 **************************************************************************************/
	public boolean parseElse() {
		boolean b = true;
		if(this.isNextToken(new Token(this.tks.IF))){
			this.expected(new Token(this.tks.IF, "if"));
			this.pList = new ParameterList();
			b = this.parse();
		}else if(this.isNextToken(new Token(this.tks.CURLY_BRACKET_OPEN))){
			this.expected(new Token(this.tks.CURLY_BRACKET_OPEN, "{"));
			this.pList = new ParameterList();
			this.expectingElse = false;
			this.startPos = 0;
			b = this.parseIfElseBody(new Token(-1));
		}else{
			this.expected(new Token(this.tks.IF, "if"), new Token(this.tks.CURLY_BRACKET_OPEN,"{"));
			this.parser.getLfc().readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
			b = false;
		}
		
		return b;
	}

/***************************************************************************************
 *  - prepareForParseExpression() 
 *  	- return true if the token t is not an unknown token
 *  	- otherwise false
 **************************************************************************************/
	private boolean prepareForParseExpression(){
		boolean b = true;
		Token type = this.parser.getLfc().readNextToken();
		if(this.isNextToken(new Token(this.tks.ASSIGNMENT))){
			
			this.parser.getLfc().readNextToken();
			Field f;
			if(this.method.isContainingFildMethodAndClass(type.getText(),false)){
				f = this.method.findFieldInsideMethoAndClass(type.getText());
			}else{
				f = new Field(null, type.getText(), null, type);
			}
			ExpressionParser expression = new ExpressionParser(this.parser, this);
			b = expression.parseExpression(this.method, f, true, true);
			if(b){
				expression.addArithmeticFieldWriter(this.method, f);
			}
		}else if(this.isNextToken(new Token(this.tks.DOT))){
			this.parser.getLfc().readNextToken();
			ReferenceCallParser refCall = new ReferenceCallParser(this.parser.getLfc(), this.method.getClazz(), this.parser.getError(), this.parser);
			try {
				b = refCall.parseMethodOrFieldCall(type, this.method, this);
				
			} catch (Exception e) {
				System.out.println("ERROR AT REFERENCE CALL PARSER!!!");
				e.printStackTrace();
			}
		}else{
			Token name = this.expected(new Token(this.tks.IDENTIFIER, "Identifier"));
			if(this.iskCorrectToken(name)){
				Token equal = new Token(this.tks.ASSIGNMENT, "=");
				if(this.isNextToken(equal)){
					this.expected(equal);
					Field s = new Field(new Type(type), name.getText(), null, name);
					ExpressionParser expression = new ExpressionParser(this.parser, this);
					b = expression.parseExpression(this.method, s, false, true);
					if(b){
						b = this.method.isContainingFildMethodAndClassAndLoops(s.getName(), true);
						if(b){
							Field f = this.method.findFieldInsideMethoAndClassAndScope(s.getName());
							this.errors.printContainsFieldError(s.getToken(), f.getToken());
							b = false;
						}else{
							this.pList.addParameter(s);
							expression.addArithmeticFieldWriter(this.method, s);
							b = true;
						}
					}
				}else if(this.isNextToken(new Token(this.tks.SEMICOLON,";"))){
					b = this.method.isContainingFildMethodAndClassAndLoops(name.getText(), true);
					if(b){
						Field f = this.method.findFieldInsideMethoAndClassAndScope(name.getText());
						this.errors.printContainsFieldError(name, f.getToken());
						b = false;
					}else{
						this.pList.addParameter(new Field(new Type(type), name.getText(), null, name));
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						//expression.addArithmeticFieldWriter(this.method);
						b = true;
					}
				}else{
					Token err = this.parser.getLfc().readNextToken();
					this.errors.printError(err, new Token(this.tks.ASSIGNMENT, "="), new Token(this.tks.SEMICOLON, ";"));
					this.expected(new Token(this.tks.SEMICOLON, ";"));
					b = false;
				}
			}else{
				this.parser.getLfc().readNextToken();
				this.expected(new Token(this.tks.SEMICOLON, ";"));
				b=false;
			}
		}
		return b;
	}
	
	
	public void print() {
		// TODO Auto-generated method stub
		
	}
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
		if((this.parser.getLfc().lookAhead().getToken() == this.tks.INT) || (this.parser.getLfc().lookAhead().getToken() == this.tks.CHAR) 
				|| (this.parser.getLfc().lookAhead().getToken() == this.tks.IDENTIFIER)){
			return true;
		}
		return false;
	}
	
/***************************************************************************************
 *  - isNextToken(Token t) - checks if the next token is the same as the parameter t 
 *  - return true if it is otherwise false
 **************************************************************************************/
	private boolean isNextToken(Token t){
		Token t1 = this.parser.getLfc().lookAhead();
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
		if(this.parser.getLfc().lookAhead().getToken() != token.getToken()){
			this.errors.printError(this.parser.getLfc().lookAhead(), token);
			return new Token(-1 , "Unknown Token");
		}
		return this.parser.getLfc().readNextToken();
	}
/*******************************************************************************************
 *  expected((Token token, Token token2)) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private Token expected(Token token1, Token token2) {
		Token t = this.parser.getLfc().lookAhead();
		if((t.getToken() != token1.getToken()) && (t.getToken() != token2.getToken())){
			this.errors.printError(t, token1, token2);
			return new Token(-1 , "Unknown Token");
		}
		t =this.parser.getLfc().readNextToken();
		return t;
		
	}
/***************************************************************************************
 *  isContainingField(String name) 
 *  	-
 **************************************************************************************/
	public boolean isContainingField(String name) 
	{
		for(int i=0; i<this.pList.getSize();i++){
			if(this.pList.getParameter(i).getName().equals(name)){
				//System.out.println("Name1 " + this.pList.getParameter(i).getName() + ",  Name2 " + name);
				return true;
			}
		}
		return false;
	}
/***************************************************************************************
 *  getIfParser() (String name) 
 *  	-
 **************************************************************************************/
	public IfParser getIfParser() 
	{
		return this.ifparser;
	}
/***************************************************************************************
 *  printPlist(String name) 
 *  	-
 **************************************************************************************/
	public void printPlist(){
		
		for(int i = 0; i < this.pList.getSize(); i++){
			
			System.out.println("Name " + this.pList.getParameter(i).getName() + ",  Type  " + this.pList.getParameter(i).getType().getDescriptor() );
		}
	}
	
/***************************************************************************************
 *  isContainingField(String name) 
 *  	-
 **************************************************************************************/
	public Field getFieldByName(String name) 
	{
		for(int i=0; i<this.pList.getSize();i++){
			if(this.pList.getParameter(i).getName().equals(name)){
				return this.pList.getParameter(i);
			}
		}
		return null;
	}
/***************************************************************************************
 *  getByteWriter()
 *  	-
 **************************************************************************************/
	public ByteWriter getByteWriter() 
	{
		return this.bWriter;
	}
/***************************************************************************************
 *  setByteWriter()
 *  	-
 **************************************************************************************/
	/*public void setByteWriter(ByteWriter b) 
	{
		this.bWriter.writeAll(b);
		//this.bWriter.printByteArray();
	}*/

	public void addExpresssion(Expression ex){
		this.expressions.add(ex);
	}
	
	public ExpressionsList getExpressions(){
		return this.expressions;
	}

	public int getLengthFromAllExpressions() {
		int number = 0;
		for(int i = 0; i < this.expressions.size(); i++){
			number = number + this.expressions.get(i).getExpressionCode().size();
		}
		return number;
	}
	
	public int getLengthFromAllExpressionsFromNumber(int from) {
		int number = 0;
		for(int i = from + 1; i < this.expressions.size(); i++){
			number = number + this.expressions.get(i).getExpressionCode().size();
		}
		return number;
	}

/***************************************************************************************
 *  getStackMapTable()
 *  	-
 **************************************************************************************/
	public StackMapTableList getStackMapTable() 
	{
		return this.stackmap;
	}
	
/***************************************************************************************
 *  printStackMapTable()
 *  	-
 **************************************************************************************/
	public void printStackMapTable() 
	{
		for(int i = 0; i < this.stackmap.size(); i++){
			System.out.println(this.stackmap.get(i));
		}
	}
	
	public void makeByteWriter() {
		for(int i = 0; i < this.expressions.size(); i++){
			this.bWriter.writeAll(this.expressions.get(i).getExpressionCode());
		}
	}
/******************************************************************************************************************
 *  boolean isNextTokenLogicOperatot()
 *  	- 
 *********************************************************************************************************************/
	private boolean isNextTokenLogicOperatot() {
		if((this.parser.getLfc().lookAhead().getToken() == this.tks.AND) || (this.parser.getLfc().lookAhead().getToken() == this.tks.OR)){
			return true;
		}
		return false;
	}

}
