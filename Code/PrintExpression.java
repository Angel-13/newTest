package Code;

import compileTable.ByteWriter;
import compileTable.Operations;
import scanner.LookForwardScanner;
import symbolTable.Class;
import symbolTable.Field;
import symbolTable.Method;
import symbolTable.ParameterList;
import symbolTable.Type;
import tokens.Token;
import tokens.Tokens;

public class PrintExpression implements Expression{
	
	private final ByteWriter code;
	
	private final Class mainClass;
	
	private final Method mainMethod;
	
	private final Operations operations;
	
	private final LookForwardScanner lfc;
	
	private final Tokens tokens;
	
	public PrintExpression(Class mainClass, Method mainMethod, LookForwardScanner lfc){
		this.code = new ByteWriter();
		this.mainClass = mainClass;
		this.mainMethod = mainMethod;
		this.operations = new Operations();
		this.lfc = lfc;
		this.tokens = new Tokens();
	}
	
	public void makePrintCode(){
		this.code.write1Byte(this.operations.GETSTATIC);
		//System.out.println(printField.getName() + "   PRINT EXPRESSION CLASS");
		Field f = this.mainClass.getFieldFromFieldRef("out");
		this.code.write2Byte(this.mainClass.getFieldIntMap().get(f));
		
		Token parameter = this.lfc.readNextToken();
		//System.out.println(parameter.getText());
		if(parameter.getToken() == this.tokens.IDENTIFIER){
			Field p = this.mainMethod.getFieldByName(parameter.getText());
			if(p.getType().isArray()){
				this.lfc.readNextToken();
				this.code.writeAll(this.getALoadCodeForRefereceClass(p));
				Token length = this.lfc.readNextToken();
				Field len = this.mainMethod.getFieldByName(length.getText());
				this.code.writeAll(this.getCodeForIdentifeerOrNumber(length, false));
				this.code.write1Byte(this.operations.IALOAD);
				ParameterList pList = new ParameterList();
				pList.addParameter(new Field(len.getType(), ""));
				Method printlnMethod = this.mainClass.getMethoddFromClassMethodReferenceByName("println", pList);
				this.code.write1Byte(this.operations.INVOKEVIRTUAL);
				this.code.write2Byte(this.mainClass.getMethodIntMap().get(printlnMethod));
			}else{
				//TODO
			}
		}else if(parameter.getToken() == this.tokens.STRING_LITERAL){
			this.code.write1Byte(this.operations.LDC);
			this.code.write1Byte(this.mainClass.getStringIntMap().get(parameter.getText()));
			ParameterList pList = new ParameterList();
			Field stringLi = new Field(new Type(parameter), parameter.getText());
			pList.addParameter(stringLi);
			Method printlnMethod = this.mainClass.getMethoddFromClassMethodReferenceByName("println", pList);
			this.code.write1Byte(this.operations.INVOKEVIRTUAL);
			this.code.write2Byte(this.mainClass.getMethodIntMap().get(printlnMethod));
		}else{
			//TODO
		}
		//this.code.printByteArray();
	}
	
	@Override
	public ByteWriter getCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ByteWriter getExpressionCode() {
		// TODO Auto-generated method stub
		return this.code;
	}
	
	public ByteWriter getCodeForIdentifeerOrNumber(Token t, boolean negative){
		ByteWriter b = new ByteWriter();
		if(t.getToken() == this.tokens.IDENTIFIER){
			if(this.lfc.lookAhead().getToken() == this.tokens.DOT){
				//TODO
			}else{
				int position = this.mainMethod.getFieldMap().get(this.mainMethod.getFieldByName(t.getText()));
				
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
				if(number > 127){
					b.write1Byte(this.operations.SIPUSH);
					b.write2Byte(number);
				}else{
					b.write1Byte(this.operations.BIPUSH);
					b.write1Byte(number);
				}
			}
		}
		return b;
	}
	
	/*private ByteWriter getPositionOfReference(Token t) {
		ByteWriter b = new ByteWriter();
		Token refToken = this.lfc.readNextToken();
		Field fieldRef = this.clazz.getFieldFromFieldRef(refToken.getText());
		Field classRef = this.method.getFieldByName(t.getText());
		if(fieldRef.getType().isArray()){
			this.lfc.readNextToken();
			Token identifierOrNumber = this.lfc.readNextToken();
			b.writeAll(this.getALoadCodeForRefereceClass(classRef));
			b.write1Byte(this.operations.GETFIELD);
			b.write2Byte(this.clazz.getFieldIntMap().get(fieldRef));
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
			b.write2Byte(this.clazz.getFieldIntMap().get(fieldRef));
		}
		
		return b;
	}*/
	
	private ByteWriter getALoadCodeForRefereceClass(Field f){
		
		ByteWriter b = new ByteWriter();
		int position = this.mainMethod.getFieldMap().get(f);
		if((position >=0) && (position <= 3)){
			b.write1Byte(this.operations.getALOADbyNumber(position));
		}else{
			b.write1Byte(this.operations.ALOAD);
			b.write1Byte(position);
		}
		return b;
	}

}
