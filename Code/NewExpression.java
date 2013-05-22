package Code;

import mapsTable.FieldIntMap;
import scanner.LookForwardScanner;
import symbolTable.Field;
import symbolTable.Method;
import tokens.Token;
import tokens.Tokens;
import compileTable.ByteWriter;
import compileTable.Operations;

public class NewExpression implements Expression{

	private final Operations operations;
	
	private final Tokens tokens;

	private final ByteWriter code;
	
	private final FieldIntMap fieldsMap;
	
	private final Method method;
	
	private final LookForwardScanner lfc;
	
	private final Field f;
	
	public NewExpression(LookForwardScanner lfc, FieldIntMap fieldMap, Method method, Field f){
		this.operations = new Operations();
		//this.tokenList = new TokenArrayList();
		this.lfc = lfc;
		this.code = new ByteWriter();
		this.fieldsMap = fieldMap;
		this.method = method;
		this.tokens = new Tokens();
		this.f = f;
	}
	
	public NewExpression(FieldIntMap fieldMap, Method method){
		this.operations = new Operations();
		//this.tokenList = new TokenArrayList();
		this.lfc = null;
		this.code = new ByteWriter();
		this.fieldsMap = fieldMap;
		this.method = method;
		this.tokens = new Tokens();
		this.f = null;
	}
	
	
	@Override
	public ByteWriter getCode() {
		this.code.write1Byte(0xbb);
		this.code.write2Byte(this.method.getClazz().getClassIntMap().get(this.f.getClazz()));
		this.code.write1Byte(0x59);
		this.code.write1Byte(0xb7);
		this.code.write2Byte(this.method.getClazz().getClassIntMap().get(this.f.getClazz())+1);
		this.code.writeAll(this.getCodeForStore(this.fieldsMap.get(this.f)));
		return this.code;
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
	
	private ByteWriter getCodeForStore(int i){
		ByteWriter b = new ByteWriter();
		if((i>=0) && (i<=3)){
			b.write1Byte(this.operations.getASTROEbyNumber(i));
		}else{
			b.write1Byte(this.operations.ASTORE);
			b.write2Byte(i);
		}
		return b;
	}

}
