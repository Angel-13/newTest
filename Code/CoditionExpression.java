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
		Token operator = this.lfc.readNextToken();
		Token rightSide = this.lfc.readNextToken();
		b.writeAll(this.getCodeForIdentifeerOrNumber(leftSide, false));
		b.writeAll(this.getCodeForIdentifeerOrNumber(rightSide, false));
		b.write1Byte(this.getOperationFromOperatorToken(operator));
		return b;
	}

	private ByteWriter getCodeForIdentifeerOrNumber(Token t, boolean negative){
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
	
	public ByteWriter getExpressionCode(){
		return this.code;
	}

}
