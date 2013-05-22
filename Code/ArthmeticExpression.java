package Code;

import scanner.LookForwardScanner;
import symbolTable.Method;
import tokens.Token;
import tokens.Tokens;
import mapsTable.FieldIntMap;
import compileTable.ByteWriter;
import compileTable.Operations;

public class ArthmeticExpression implements Expression{
	
	
	private final Operations operations;
	
	private final Tokens tokens;

	private final ByteWriter code;
	
	private final FieldIntMap fieldsMap;
	
	private final Method method;
	
	private final LookForwardScanner lfc;
	
	public ArthmeticExpression(LookForwardScanner lfc, FieldIntMap fieldMap, Method method){
		this.operations = new Operations();
		//this.tokenList = new TokenArrayList();
		this.lfc = lfc;
		this.code = new ByteWriter();
		this.fieldsMap = fieldMap;
		this.method = method;
		this.tokens = new Tokens();
		this.makeSeparateTokens();
	}
	
	public ArthmeticExpression(FieldIntMap fieldMap, Method method){
		this.operations = new Operations();
		//this.tokenList = new TokenArrayList();
		this.lfc = null;
		this.code = new ByteWriter();
		this.fieldsMap = fieldMap;
		this.method = method;
		this.tokens = new Tokens();
	}
	
	private void makeSeparateTokens() {
		if(this.lfc.lookAhead().getToken()==this.tokens.ASSIGNMENT){
			System.out.println("akeSeparateTokens() from ArthmeticExpression");
		}else{
			Token leftSide = this.lfc.readNextToken();
			while(this.lfc.lookAhead().getToken()!=this.tokens.ASSIGNMENT){
				
				leftSide =this.lfc.readNextToken();
			}
			this.lfc.readNextToken();
			this.code.writeAll(this.getCode());
			int position = this.fieldsMap.get(this.method.getFieldByName(leftSide.getText()));
			if((position>=0) && (position<=3)){
				this.code.write1Byte(this.operations.getISTOREbyNumber(position));
			}else{
				this.code.write1Byte(this.operations.ISTORE);
				this.code.write1Byte(position);
			}
		}
		
		
	}
/****************************SAC-OD TUKA***************************************************************************/
	/*public static boolean isT1SmallerPriority(Token t1, Token t2) {
		int ti1 = t1.getToken();
		int ti2 = t2.getToken();
		if (ti1 % 2 == 1) {
			ti1++;
		}
		if (ti2 % 2 == 1) {
			ti2++;
		}
		return ti1 >= ti2;
	}*/
/*****************************DO TUKA************************************************************************/
	/*@Override
	public ByteWriter getCode() {
		ByteWriter b = new ByteWriter();
		TokenArrayList out = new TokenArrayList();
		TokenStack stack = new TokenStack();
		Tokens t = new Tokens();
		
		while (this.lfc.lookAhead().getToken() != this.tokens.SEMICOLON) {
			// If token is an operator
			
			Token token = this.lfc.readNextToken();
			
			if (this.isTokenOperator(token)) {
				// While stack not empty AND stack top element
				// is an operator
				while (!stack.empty() && this.isTokenOperator(stack.peek())) {
					if (isT1SmallerPriority(token, stack.peek())) {
						out.add(stack.pop());
						continue;
					}
					break;
				}
				// Push the new operator on the stack
				stack.push(token);
			}
			// If token is a left bracket '('
			else if (token.getToken() == t.ROUND_BRACKET_OPEN) {
				stack.push(token); //   
			}
			// If token is a right bracket ')'
			else if (token.getToken() == t.ROUND_BRACKET_CLOSE) {
				while (!stack.empty() && stack.peek().getToken() != t.ROUND_BRACKET_OPEN) {
					out.add(stack.pop());
				}
				stack.pop();
			}
			// If token is a number
			else {
				out.add(token);
			}
		}
		while (!stack.empty()) {
			out.add(stack.pop());
		}
		int i=0;
		while(i<out.size()){
			if(this.isTokenOperator(out.get(i))){
				b.write1Byte(this.getOperationFromOperatorToken(out.get(i)));
			}else{
				
			}
		}
		
		return b;*/
/******************MOE FUNKCIONIRA***********************************************************///
	public ByteWriter getCode() {
		ByteWriter b = new ByteWriter();
		int pos = 0;
		while(this.lfc.lookAhead().getToken() != this.tokens.SEMICOLON){
			/******ADDE******************/
			
			if(this.lfc.lookAhead().getToken() == this.tokens.ADD){
				this.lfc.readNextToken();
				if(pos !=0){
					
					if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_OPEN){
						this.lfc.readNextToken();
						b.writeAll(this.getCode());
					}else{
						b.writeAll(this.getCodeForIdentifeerOrNumber(this.lfc.readNextToken(), false));
					}
					while((this.lfc.lookAhead().getToken() == this.tokens.DIV) ||(this.lfc.lookAhead().getToken() == this.tokens.MULT)){
						Token op = this.lfc.readNextToken();
						if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_OPEN){
							this.lfc.readNextToken();
							b.writeAll(this.getCode());
							b.write1Byte(this.getOperationFromOperatorToken(op));
						}else{
							b.writeAll(this.getCodeForIdentifeerOrNumber(this.lfc.readNextToken(), false));
							b.write1Byte(this.getOperationFromOperatorToken(op));
						}
						
					}
					
					b.write1Byte(this.operations.IADD);
				}
				/******ISUB******************/
			}else if(this.lfc.lookAhead().getToken() == this.tokens.SUB){
				this.lfc.readNextToken();
				if(pos !=0){
					if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_OPEN){
						this.lfc.readNextToken();
						b.writeAll(this.getCode());
					}else{
						b.writeAll(this.getCodeForIdentifeerOrNumber(this.lfc.readNextToken(), false));
					}
					while((this.lfc.lookAhead().getToken() == this.tokens.DIV) ||(this.lfc.lookAhead().getToken() == this.tokens.MULT)){
						Token op = this.lfc.readNextToken();
						if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_OPEN){
							this.lfc.readNextToken();
							b.writeAll(this.getCode());
							b.write1Byte(this.getOperationFromOperatorToken(op));
						}else{
							b.writeAll(this.getCodeForIdentifeerOrNumber(this.lfc.readNextToken(), false));
							b.write1Byte(this.getOperationFromOperatorToken(op));
						}
					}
					
					b.write1Byte(this.operations.ISUB);
				}else{
					if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_OPEN){
						
						this.lfc.readNextToken();
						b.writeAll(this.getCode());
						b.write1Byte(this.operations.INEG);
					}else{
						b.writeAll(this.getCodeForIdentifeerOrNumber(this.lfc.readNextToken(), true));
					}
					while((this.lfc.lookAhead().getToken() == this.tokens.DIV) ||(this.lfc.lookAhead().getToken() == this.tokens.MULT)){
						Token op = this.lfc.readNextToken();
						if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_OPEN){
							this.lfc.readNextToken();
							b.writeAll(this.getCode());
							//System.out.println(this.lfc.lookAhead().getText());
							b.write1Byte(this.getOperationFromOperatorToken(op));
						}else{
							b.writeAll(this.getCodeForIdentifeerOrNumber(this.lfc.readNextToken(), false));
							b.write1Byte(this.getOperationFromOperatorToken(op));
						}
					}
				}
			}else if((this.lfc.lookAhead().getToken() == this.tokens.DIV) || (this.lfc.lookAhead().getToken() == this.tokens.MULT)){
				while((this.lfc.lookAhead().getToken() == this.tokens.DIV) ||(this.lfc.lookAhead().getToken() == this.tokens.MULT)){
					Token op = this.lfc.readNextToken();
					if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_OPEN){
						this.lfc.readNextToken();
						b.writeAll(this.getCode());
						b.write1Byte(this.getOperationFromOperatorToken(op));
					}else{
						b.writeAll(this.getCodeForIdentifeerOrNumber(this.lfc.readNextToken(), false));
						b.write1Byte(this.getOperationFromOperatorToken(op));
					}
				}
			}else if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_OPEN){
				this.lfc.readNextToken();
				b.writeAll(this.getCode());
			}else if ((this.lfc.lookAhead().getToken() == this.tokens.IDENTIFIER) || (this.lfc.lookAhead().getToken() == this.tokens.NUMBER)){
				b.writeAll(this.getCodeForIdentifeerOrNumber(this.lfc.readNextToken(), false));
			}
			if(this.lfc.lookAhead().getToken() == this.tokens.ROUND_BRACKET_CLOSE){
				this.lfc.readNextToken();
				return b;
			}
			pos++;
			
		}
		
		return b;
	}	
	
	private int getOperationFromOperatorToken(Token token) {
		if(token.getToken() == this.tokens.DIV){
			return this.operations.IDIV;
		}else if(token.getToken() == this.tokens.ADD){
			return this.operations.IADD;
		}else if(token.getToken() == this.tokens.SUB){
			return this.operations.ISUB;
		}else if(token.getToken() == this.tokens.DIV){
			return this.operations.IDIV;
		}else if(token.getToken() == this.tokens.MULT){
			return this.operations.IMUL;
		}else if(token.getToken() == this.tokens.NEGATE){
			return this.operations.INEG;
		}
		return -2;
	}


	@Override
	public boolean validate() {
		
		return false;
	}
	
	/*private boolean isTokenOperator(Token token){
		if((this.tokens.ADD == token.getToken()) || (this.tokens.DIV == token.getToken()) 
				|| (this.tokens.MULT == token.getToken()) || (this.tokens.SUB == token.getToken())){
			return true;
		}
		return false;
	}*/
	
	public ByteWriter getCodeForIdentifeerOrNumber(Token t, boolean negative){
		ByteWriter b = new ByteWriter();
		if(t.getToken() == this.tokens.IDENTIFIER){
			int position = this.fieldsMap.get(this.method.getFieldByName(t.getText()));
			
			if((position>=0) && (position<=3)){
				b.write1Byte(this.operations.getILOADbyNumber(position));
				if(negative){
					b.write1Byte(this.operations.INEG);
					}
			}else{
				b.write1Byte(this.operations.ILOAD);
				b.write1Byte(position);
			}
		}else{
			int number;
			if(negative){
				number = Integer.parseInt("-" + t.getText());
			}else{
				number = Integer.parseInt(t.getText());
			}
			if((number >=0) && (number <=5)){
				b.write1Byte(this.operations.getICONSTbyNumber(number));
				
			}else{
				b.write1Byte(this.operations.BIPUSH);
				b.write1Byte(number);
			}
		}
		return b;
	}
	
	public ByteWriter getExpressionCode(){
		return this.code;
	}
		

}
