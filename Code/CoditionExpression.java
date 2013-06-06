package Code;

import mapsTable.FieldIntMap;
import milestone2.BodyParser;
import scanner.LookForwardScanner;
import symbolTable.Field;
import symbolTable.Method;
import tokens.Token;
import tokens.Tokens;
import compileTable.ByteWriter;
import compileTable.Operations;

public class CoditionExpression implements Expression{

	private final Operations operations;
	
	private final Tokens tokens;

	private final ByteWriter code;
	
	private final FieldIntMap fieldsMap;
	
	//private final BodyParser parser;
	
	private final Method method;
	
	private final LookForwardScanner lfc;
	
	public CoditionExpression(LookForwardScanner lfc, FieldIntMap fieldMap, Method m){
		this.operations = new Operations();
		this.lfc = lfc;
		this.code = new ByteWriter();
		this.fieldsMap = fieldMap;
		this.tokens = new Tokens();
		this.method = m;
		this.make();
	}

	private void make() {
		this.code.writeAll(this.getCode());
		//this.code.printByteArray();
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ByteWriter getCode() {
		ByteWriter b = new ByteWriter();
		Token leftSide = this.lfc.readNextToken();
		b.writeAll(this.getCodeForIdentifeerOrNumber(leftSide, false));
		if(this.isNextTokenOperator()){
			Token arOperator = this.lfc.readNextToken();
			Token rSideArithmetic = this.lfc.readNextToken();
			b.writeAll(this.getCodeForIdentifeerOrNumber(rSideArithmetic, false));
			b.write1Byte(this.getOperationFromArithemticOperatorToken(arOperator));
		}
		Token operator = this.lfc.readNextToken();
		Token rightSide = this.lfc.readNextToken();
		b.writeAll(this.getCodeForIdentifeerOrNumber(rightSide, false));
		b.write1Byte(this.getOperationFromOperatorToken(operator));
		return b;
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
		Field fieldRef = this.method.getClazz().getFieldFromFieldRef(refToken.getText());
		Field classRef = this.method.getFieldByName(t.getText());
		if(fieldRef.getType().isArray()){
			this.lfc.readNextToken();
			Token identifierOrNumber = this.lfc.readNextToken();
			b.writeAll(this.getALoadCodeForRefereceClass(classRef));
			b.write1Byte(this.operations.GETFIELD);
			b.write2Byte(this.method.getClazz().getFieldIntMap().get(fieldRef));
			if(method.isContainingFildMethodAndClassAndLoops(identifierOrNumber.getText(), false)){
				Field f = method.findFieldInsideMethoAndClassAndScope(identifierOrNumber.getText());
				int mapPostition = method.getFieldMap().get(f);
				if((mapPostition>=0) && (mapPostition<=3)){
					b.write1Byte(this.operations.getILOADbyNumber(mapPostition));
				}else{
					b.write1Byte(this.operations.ILOAD);
					b.write1Byte(mapPostition);
				}
			}else{
				int number =  Integer.parseInt(identifierOrNumber.getText());
				b.writeAll(this.getCodeForPushNumber(number));
				
			}
			b.write1Byte(this.operations.IALOAD);
			this.lfc.readNextToken();
			
		}else{
			b.writeAll(this.getALoadCodeForRefereceClass(classRef));
			b.write1Byte(this.operations.GETFIELD);
			b.write2Byte(this.method.getClazz().getFieldIntMap().get(fieldRef));
		}
		
		return b;
	}
	
	private int getOperationFromArithemticOperatorToken(Token token) {
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
	
	private int getOperationFromOperatorToken(Token token) {
		
		if(token.getToken() == this.tokens.UNEQUAL){
			return this.operations.IF_ICMPEQ;
		}else if(token.getToken() == this.tokens.EQUAL){
			return this.operations.IF_ICMPNE;
		}else if(token.getToken() == this.tokens.GREATER){
			return this.operations.IF_ICMPLE;
		}else if(token.getToken() == this.tokens.GREATER_EQUAL){
			return this.operations.IF_ICMPLT;
		}else if(token.getToken() == this.tokens.LESS){
			return this.operations.IF_ICMPGE;
		}else if(token.getToken() == this.tokens.LESS_EQUAL){
			return this.operations.IF_ICMPGT;
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
		if((number >=0) && (number <= 5)){
			b.write1Byte(this.operations.getICONSTbyNumber(number));
		}else{
			b.write1Byte(this.operations.BIPUSH);
			b.write1Byte(number);
		}
		return b;
	}
	
	public ByteWriter getExpressionCode(){
		return this.code;
	}
	
/*******************************************************************************************
 *  isNextTokenOperator() 
 *  	- 
 *******************************************************************************************/
	private boolean isNextTokenOperator(){
		Token t1 = this.lfc.lookAhead();
		if((t1.getToken() == this.tokens.ADD) || (t1.getToken() == this.tokens.SUB) || (t1.getToken() == this.tokens.DIV) || (t1.getToken() == this.tokens.MULT)){
			return true;
		}
		return false;
	}
	

}
