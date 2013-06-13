package milestone2;

import Code.Expression;

import symbolTable.ExpressionsList;
import symbolTable.Field;
import symbolTable.Method;
import symbolTable.ParameterList;
import symbolTable.StackMapTableList;
import symbolTable.StackMapTableObject;
import symbolTable.Type;
import tokens.Token;
import tokens.Tokens;


public class LoopParser implements BodyParser{

	private final Method method;
	
	private IfParser ifparser;
	
	private LoopParser loopparser;
	
	private final Parser parser;
	
	private final Tokens tks;
	
	private final ErrorsClass errors;
	
	private final ParameterList pList;
	
	private ExpressionsList expressions;
	
	private final StackMapTableList stackmap;
	
	private int startPos;
	
	
	public LoopParser(Method m, Parser p, int start){
		this.method = m;
		this.parser = p;
		this.tks = new Tokens();
		this.errors = new ErrorsClass();
		this.pList= new ParameterList();
		this.ifparser = null;
		this.loopparser = null;
		this.stackmap = this.method.getStackMapTableList();
		this.expressions = new ExpressionsList();
		this.startPos = start;
	}
	
/***************************************************************************************
 *  - parse()
 *  	- 
 **************************************************************************************/
	public boolean parse() {
		if(this.startPos != 0){
			StackMapTableObject stcObj = new StackMapTableObject(startPos, this.method.getStackFrameFieldCounter());
			if(!this.method.getStackFrameFieldCounter().isEmpty()){
				stcObj.makeAppendFrame(this.method);
				this.method.getStackFrameFieldCounter().clear();
			}else{
				stcObj.makeSameFrame();
			}
			this.method.addToStackMapTableList(stcObj);
			this.startPos = 0;
		}
		boolean b = true;
		Token t = this.expected(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
		if(this.iskCorrectToken(t)){
			ExpressionParser expression = new ExpressionParser(this.parser, this);
			b = expression.parseLogicalExpression(this.method);
			
			if(b){
				if(!this.parser.getError()){
					expression.addLogicalFieldWriter(this.method);
				}
				while(this.isNextTokenLogicOperatot()){
					String s = this.parser.getLfc().readNextToken().getText();
					expression.addString(s);
					/*if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_OPEN))){
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
					}*/
					b = expression.parseLogicalExpression(this.method);
					if(!this.parser.getError()){
						expression.addLogicalFieldWriter(this.method);
					}
				}
				
				/*if(b){
					Token closeBracket = this.expected(new Token(this.tks.ROUND_BRACKET_CLOSE, ")"));
					if(closeBracket.getToken() == -1){
						this.parser.getLfc().readNextToken();
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
					}
				}*/
					
				this.startPos = this.getLengthFromAllExpressions() + 2;
				t = this.expected(new Token(this.tks.CURLY_BRACKET_OPEN, "{"));
				if(this.iskCorrectToken(t)){
					b = this.parsewhileBody();
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

	public void print() {
		// TODO Auto-generated method stub
		
	}

/***************************************************************************************
 *  - whileBody() 
 *  	- 
 **************************************************************************************/	
	private boolean parsewhileBody() {
		boolean b = true;
		while(!this.isNextToken(new Token(this.tks.CURLY_BRACKET_CLOSE))){
			if(this.isNextToken()){
				
				b = this.prepareForParseExpression();
				if(!b){
					break;
				}
				int length = this.expressions.size() - 1;
				this.startPos = this.startPos + this.expressions.get(length).getExpressionCode().size();
				
			}else if(this.isNextToken(new Token(this.tks.IF))){
				this.expected(new Token(this.tks.IF));
				int startForIf = this.getLengthFromAllExpressions() + 2;
				this.ifparser = new IfParser(this.method, this.parser, startForIf);
				b = this.ifparser.parse();
				if(!b){
					break;
				}
				this.addExpresssionsFromIfPasres(this.ifparser.getExpressions());
				this.startPos = 0;
			}else if(this.isNextToken(new Token(this.tks.WHILE))){
				this.expected(new Token(this.tks.WHILE));
				//this.loopparser = new LoopParser(this.method, this.parser);
				b = this.loopparser.parse();
				if(!b){
					break;
				}
			}else{
				//Token t = this
				
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
		
		if(b){
			int value = this.getLengthFromAllExpressions() + 2;
			//Writing code for goto(0xa7) from while loop
			this.expressions.get(this.expressions.size()-1).getExpressionCode().write1Byte(0xa7);
			this.expressions.get(this.expressions.size()-1).getExpressionCode().write2Byte(65536 - value);
			value = this.getLengthFromAllExpressions() + 2 - (this.expressions.get(0).getExpressionCode().size()-1);
			this.expressions.get(0).getExpressionCode().write2Byte(value);
			this.startPos = this.startPos + 2;
			StackMapTableObject stcObj = new StackMapTableObject(this.startPos, this.method.getStackFrameFieldCounter());
			stcObj.makeSameFrame();
			this.method.addToStackMapTableList(stcObj);
			
			/*for(int i = 0; i < this.expressions.size(); i++){
				this.bWriter.writeAll(this.expressions.get(i).getExpressionCode());
			}
			this.bWriter.printByteArray();*/
		}
		
		return b;
	}
	
/***************************************************************************************
 *  - addExpresssionsFromIfPasres(ExpressionsList expressions2)
 *  	- 
 **************************************************************************************/
	private void addExpresssionsFromIfPasres(ExpressionsList ex) {
		//int startposition = this.expressions.get(0).getExpressionCode().size()+2;
		//this.ifparser.setElsePosition(startposition, true);
		//System.out.println(this.ifparser.getLengthFromAllExpressions());
		//this.stackmap.add(this.ifparser.getLengthFromAllExpressions());
		//THIS IS GUILTY ONLY FOR IF ELSE AND NOTHING ELSE IN WHILE LOOP
		//TODO IF NOT ONLY IF ELSE CONDITION IN THE WHILE LOOP TO IMPLEMENT TO GENERATE CODE
		for(int i = 0; i < ex.size(); i++){
			this.expressions.add(ex.get(i));
		}
		/*for(int i = 0; i < this.expressions.size(); i++){
			this.bWriter.writeAll(this.expressions.get(i).getExpressionCode());
		}
		this.bWriter.printByteArray();*/
		
		
	}

/***************************************************************************************
 *  - whileBody() 
 *  	- 
 **************************************************************************************/
	private boolean prepareForParseExpression() {
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
	
	
//*********** TO BE CHECKED ********************************************************/
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
/***************************************************************************************
 *  isContainingField(String name) 
 *  	-
 **************************************************************************************/
	public boolean isContainingField(String name) 
	{
		for(int i=0; i<this.pList.getSize();i++){
			if(this.pList.getParameter(i).getName().equals(name)){
				return true;
			}
		}
		return false;
	}
/***************************************************************************************
 *  IfParser getIfParser() 
 *  	-
 **************************************************************************************/
	public IfParser getIfParser() 
	{
		return this.ifparser;
	}
	
	public void addExpresssion(Expression ex){
		this.expressions.add(ex);
	}
	
	public ExpressionsList getExpressions(){
		return this.expressions;
	}
	
/***************************************************************************************
 *  int getLengthFromAllExpressions()
 *  	- 
 **************************************************************************************/
	public int getLengthFromAllExpressions() {
		int number = 0;
		for(int i = 0; i < this.expressions.size(); i++){
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
