package Code;

import mapsTable.FieldIntMap;
import scanner.LookForwardScanner;
import symbolTable.Class;
import symbolTable.Field;
import symbolTable.Method;
import tokens.Tokens;
import compileTable.ByteWriter;
import compileTable.Operations;

public class NewArrayExpression implements Expression{
	
	private final Operations operations;

	private final ByteWriter code;
	
	private final Method method;
	
	private final Field fieldRef;
	
	private final Class clazzRef;
	
	private final String lange;
	
	private final Field classRef;
	
	public NewArrayExpression(Method method, Field f, Class clazz, String lange, Field classRef){
		this.operations = new Operations();
		this.code = new ByteWriter();
		this.method = method;
		this.clazzRef = clazz;
		this.fieldRef = f;
		this.lange = lange;
		this.classRef = classRef;
	}
	@Override
	public ByteWriter getCode() {
		int position = this.method.getFieldMap().get(this.classRef);
		if((position >=0) && (position <=3)){
			this.code.write1Byte(this.operations.getALOADbyNumber(position));
		}else{
			this.code.write1Byte(this.operations.ALOAD);
			this.code.write1Byte(position);
		}
		int number =  Integer.parseInt(this.lange);
		this.fieldRef.setSize(number);
		if((number >=0) && (number <=5)){
			this.code.write1Byte(this.operations.getICONSTbyNumber(number));
			
		}else{
			this.code.write1Byte(this.operations.BIPUSH);
			this.code.write1Byte(number);
		}
		this.code.write1Byte(this.operations.NEWARRAY);
		//TODO To implement for other types
		if(this.fieldRef.getType().getBaseType().isInteger()){
			this.code.write1Byte(0x0a);
		}
		this.code.write1Byte(this.operations.PUTFIELD);
		this.code.write2Byte(this.method.getClazz().getFieldIntMap().get(this.fieldRef));
		return this.code;
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

}
