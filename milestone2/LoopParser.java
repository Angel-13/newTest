package milestone2;

import Code.Expression;
import compileTable.ByteWriter;
import compileTable.Operations;

import symbolTable.ExpressionsList;
import symbolTable.Field;
import symbolTable.Method;
import symbolTable.ParameterList;
import symbolTable.StackMapTable;
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
	
	private ByteWriter bWriter;
	
	private ExpressionsList expressions;
	
	private final StackMapTable stackmap;
	
	
	public LoopParser(Method m, Parser p){
		this.method = m;
		this.parser = p;
		this.tks = new Tokens();
		this.errors = new ErrorsClass();
		this.pList= new ParameterList();
		this.ifparser = null;
		this.loopparser = null;
		this.bWriter = new ByteWriter();
		this.stackmap = new StackMapTable();
		this.expressions = new ExpressionsList();
	}
	
/***************************************************************************************
 *  - parse()
 *  	- 
 **************************************************************************************/
	public boolean parse() {
		
		/********************************************************************************/
		this.stackmap.add(this.method.getStartOfLoopPosition());
		/*******************************************************************************/
		boolean b = true;
		Token t = this.expected(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
		if(this.iskCorrectToken(t)){
			ExpressionParser expression = new ExpressionParser(this.parser, this);
			b = expression.parseLogicalExpression(this.method);
			if(b){
				
				t = this.expected(new Token(this.tks.CURLY_BRACKET_OPEN, "{"));
				if(this.iskCorrectToken(t)){
					expression.addLogicalFieldWriter(this.method);
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
	//DO TUKAAAAAAA

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
			}else if(this.isNextToken(new Token(this.tks.IF))){
				this.expected(new Token(this.tks.IF));
				this.ifparser = new IfParser(this.method, this.parser);
				b = this.ifparser.parse();
				if(!b){
					break;
				}
				//System.out.println(this.ifparser.getLengthFromAllExpressions());
				this.addExpresssionsFromIfPasres(this.ifparser.getExpressions());
			}else if(this.isNextToken(new Token(this.tks.WHILE))){
				this.expected(new Token(this.tks.WHILE));
				this.loopparser = new LoopParser(this.method, this.parser);
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
			this.expressions.get(this.expressions.size()-1).getExpressionCode().write1Byte(0xa7);
			this.expressions.get(this.expressions.size()-1).getExpressionCode().write2Byte(65536 - value);
			value = this.getLengthFromAllExpressions() + 2 - (this.expressions.get(0).getExpressionCode().size()-1);
			this.expressions.get(0).getExpressionCode().write2Byte(value);
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
		int startposition = this.expressions.get(0).getExpressionCode().size()+2;
		this.ifparser.setElsePosition(startposition, true);
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
				expression.addArithmeticFieldWriter(this.method);
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
							expression.addArithmeticFieldWriter(this.method);
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
 *  isContainingField(String name) 
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
	
/**********************************************************************************************************************************************************************/
	public int getLengthFromAllExpressions() {
		int number = 0;
		for(int i = 0; i < this.expressions.size(); i++){
			number = number + this.expressions.get(i).getExpressionCode().size();
		}
		return number;
	}
	
	public void setElsePosition(int startPosition){
		if(this.expressions.size() <= 1){
			int i = this.expressions.get(0).getExpressionCode().size();
			this.expressions.get(0).getExpressionCode().write2Byte(startPosition+i);
			this.stackmap.add(startPosition+i);
		}else{
			Operations operations = new Operations();
			//int number = this.getLengthFromAllExpressions();
			this.expressions.get(0).getExpressionCode().write2Byte(this.stackmap.get(1));
			this.expressions.get(1).getExpressionCode().write1Byte(operations.GOTO);
			this.expressions.get(1).getExpressionCode().write2Byte(this.stackmap.get(0));
		}
		
		
		//this.makeTheRealStackMapTable();
		
	}
/***************************************************************************************
 *  makeTheRealStackMapTable()
 *  	-
 **************************************************************************************/
	public void makeStackMapTable() {
		for(int i = 0; i < this.expressions.size(); i++){
			this.bWriter.writeAll(this.expressions.get(i).getExpressionCode());
		}
		for(int i = 0; i < this.bWriter.size(); i++){
			if((this.bWriter.getByteArray()[i]+256)== 0xa7){
				if(this.stackmap.size()<=1){
					this.stackmap.add(i+2);
				}else{
					int number = i+1 - this.stackmap.get(this.stackmap.size()-1);
					this.stackmap.add(number);
				}
			}
		}
	}

/***************************************************************************************
 *  getStackMapTable()
 *  	-
 **************************************************************************************/
	public StackMapTable getStackMapTable() 
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
/***************************************************************************************
 *  makeByteWriter(int startPosition)
 *  	-
 **************************************************************************************/
	public void makeByteWriter(int startPosition) {
		Operations operations = new Operations();
		int number = this.getLengthFromAllExpressions();
		this.stackmap.add(startPosition+number+2+2);
		System.out.println(this.stackmap.get(1));
		this.expressions.get(0).getExpressionCode().write2Byte(this.stackmap.get(1)-1);
		this.expressions.get(1).getExpressionCode().write1Byte(operations.GOTO);
		number = startPosition + this.getLengthFromAllExpressions()-1;
		System.out.println(number);
		this.expressions.get(1).getExpressionCode().write2Byte(65536 - number);
		
		for(int i = 0; i < this.expressions.size(); i++){
			this.bWriter.writeAll(this.expressions.get(i).getExpressionCode());
		}
		this.bWriter.printByteArray();
		//this.makeTheRealStackMapTable();
	}

	
}
