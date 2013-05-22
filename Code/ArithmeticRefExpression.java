package Code;

import mapsTable.FieldIntMap;
import compileTable.ByteWriter;
import compileTable.Operations;
import scanner.LookForwardScanner;
import symbolTable.Class;
import symbolTable.Field;
import symbolTable.Method;
import tokens.Token;
import tokens.Tokens;

public class ArithmeticRefExpression implements Expression{

	private final Class clazz;
	
	private final Method method;
	
	private final ByteWriter code;
	
	private final Operations operations;
	
	private final Tokens tokens;
	
	private final FieldIntMap fieldsMap;
	
	private final LookForwardScanner lfc;
	
	private final Field field;
	
	private final Field classReference;
	
	private int position;
	
	private final String expression;
	
	public ArithmeticRefExpression(Class clazz,FieldIntMap fieldMap, Method m, LookForwardScanner lfc, Field field, int position, Field classReference, String expression){
		this.clazz = clazz;
		this.method = m;
		this.code = new ByteWriter();
		this.operations = new Operations();
		this.tokens = new Tokens();
		this.fieldsMap = fieldMap;
		this.lfc = lfc;
		this.field = field;
		this.position = position;
		this.classReference = classReference;
		this.expression = expression;
	}
	
	public void make(){
		this.lfc.readNextToken();
		
		this.code.writeAll(this.getALoadCodeForRefereceClass(this.classReference));
		if(this.field.getType().isArray()){
			this.code.write1Byte(this.operations.GETFIELD);
			this.code.write2Byte(this.clazz.getFieldIntMap().get(this.field));
			this.code.writeAll(this.getCodeForPushNumber(this.position));
		}
		this.code.writeAll(this.getCode());
		if(this.field.getType().isArray()){
			this.code.write1Byte(this.operations.IASTORE);
		}else{
			this.code.write1Byte(this.operations.PUTFIELD);
			this.code.write2Byte(this.clazz.getFieldIntMap().get(this.field));
		}
	}
	
	public void makeForRetrun(){
		this.code.writeAll(this.getALoadCodeForRefereceClass(this.classReference));
		if(this.field.getType().isArray()){
			this.code.write1Byte(this.operations.GETFIELD);
			this.code.write2Byte(this.clazz.getFieldIntMap().get(this.field));
			this.code.writeAll(this.getCodeForPushNumber(this.position));
			this.code.write1Byte(this.operations.IALOAD);
		}else{
			this.code.write1Byte(this.operations.GETFIELD);
			this.code.write2Byte(this.clazz.getFieldIntMap().get(this.field));
		}
		this.code.write1Byte(this.operations.IRETURN);
	}
	
	@Override
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

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ByteWriter getExpressionCode() {
		return this.code;
	}
	
	public ByteWriter getCodeForIdentifeerOrNumber(Token t, boolean negative){
		ByteWriter b = new ByteWriter();
		if(t.getToken() == this.tokens.IDENTIFIER){
			if(this.lfc.lookAhead().getToken() == this.tokens.DOT){
				this.lfc.readNextToken();
				b.writeAll(this.getPositionOfReference(t));
			}else{
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
			}
		}else{
			int number;
			if(negative){
				number = Integer.parseInt("-" + t.getText());
			}else{
				number = Integer.parseInt(t.getText());
			}
			//TODO Make it functional for other expressions
			if(this.isNextTokenOperator()){
				Token op = this.lfc.readNextToken();
				int number1 = Integer.parseInt(this.lfc.readNextToken().getText());
				number = this.operate(op, number, number1);
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
	
	private ByteWriter getPositionOfReference(Token t) {
		ByteWriter b = new ByteWriter();
		Token refToken = this.lfc.readNextToken();
		Field fieldRef = this.clazz.getFieldFromFieldRef(refToken.getText());
		Field classRef = this.method.getFieldByName(t.getText());
		if(fieldRef.getType().isArray()){
			this.lfc.readNextToken();
			int pos = Integer.parseInt(this.lfc.readNextToken().getText());
			this.lfc.readNextToken();
			b.writeAll(this.getALoadCodeForRefereceClass(classRef));
			b.write1Byte(this.operations.GETFIELD);
			b.write2Byte(this.clazz.getFieldIntMap().get(fieldRef));
			b.writeAll(this.getCodeForPushNumber(pos));
			b.write1Byte(this.operations.IALOAD);
		}else{
			b.writeAll(this.getALoadCodeForRefereceClass(classRef));
			b.write1Byte(this.operations.GETFIELD);
			b.write2Byte(this.clazz.getFieldIntMap().get(fieldRef));
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
	

	private ByteWriter getALoadCodeForRefereceClass(Field f){
		ByteWriter b = new ByteWriter();
		int position = this.fieldsMap.get(f);
		if((position >=0) && (position <= 3)){
			b.write1Byte(this.operations.getALOADbyNumber(position));
		}else{
			b.write1Byte(this.operations.ALOAD);
			b.write1Byte(position);
		}
		return b;
	}
	
	private ByteWriter getCodeForPushNumber(int number){
		ByteWriter b = new ByteWriter();
		if((number >=0) && (position <= 5)){
			b.write1Byte(this.operations.getICONSTbyNumber(number));
		}else{
			b.write1Byte(this.operations.BIPUSH);
			b.write1Byte(number);
		}
		return b;
	}
	
/*******************************************************************************************
 *  isNextTokenOperator() 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private boolean isNextTokenOperator(){
		Token t1 = this.lfc.lookAhead();
		if((t1.getToken() == this.tokens.ADD) || (t1.getToken() == this.tokens.SUB) || (t1.getToken() == this.tokens.DIV) || (t1.getToken() == this.tokens.MULT)){
			return true;
		}
		return false;
	}
	
/*******************************************************************************************
 *  int operate(Token operator, int s, int s1)
 *  	- 
 *******************************************************************************************/
	private int operate(Token operator, int s, int s1){
		if(operator.getToken() == this.tokens.ADD){
			return s + s1;
		}else if(operator.getToken() == this.tokens.DIV){
			return s/s1;
		}else if(operator.getToken() == this.tokens.MULT){
			return s*s1;
		}else{
			return s-s1;
		}
	}
}
