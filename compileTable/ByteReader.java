package compileTable;

import java.io.File;
import java.io.FileReader;

import mapsTable.IntStringMap;

import scanner.LookForwardReader;
import symbolTable.Field;
import symbolTable.Type;
import tokens.Token;
import tokens.Tokens;
import symbolTable.Class;

public class ByteReader {
	
	private LookForwardReader lfc;
	
	private final Tokens tks;
	
	private int cpLength;
	
	private int counter;
	
	private boolean hasMethodName;
	
	private boolean hasParameters;
	
	private final IntStringMap intStringMap;
	
	private final ByteWriter bw; 
	
	private final Operations op;
	
	public ByteReader(String filepath) throws Exception{
		this.op = new Operations();
		this.bw = null;
		this.lfc = new LookForwardReader(new FileReader(new File(filepath)));
		this.counter = 1;
		this.hasMethodName = false;
		this.hasParameters = false;
		this.intStringMap  = new IntStringMap();
		this.tks = new Tokens();
		this.makePostition();
	}
	
	public ByteReader(ByteWriter b) throws Exception{
		this.op = new Operations();
		this.bw = b;
		this.lfc = null;
		this.counter = 1;
		this.hasMethodName = false;
		this.hasParameters = false;
		this.intStringMap  = new IntStringMap();
		this.tks = new Tokens();
		//this.makePostition();
	}
	
	private void makePostition() {
		for(int i = 0; i < 8; i++){
			this.lfc.readNext();
		}
		this.cpLength = this.lfc.readNext() * 256 + this.lfc.readNext();
		System.out.println(this.cpLength + "    this.cpLength");
		//System.out.println(this.cpLength);
	}

	public boolean findMethodOrFieldFromClassFile(String methodName, String parameters){
		//boolean b = false;
		//while(this.lfc.hasNext()){
		while(this.counter < this.cpLength){
			char c = this.lfc.readNext();
			if(c == 0x0A){
				this.parseMethodRef();
			}else if(c == 0x07){
				this.parseClass();
			}else if(c == 0x09){
				this.parseFieldRef();
			}else if(c == 0x0C){
				this.parseNemeAndType();
			}else if(c == 0x01){
				this.parseUTF8(methodName, parameters);
			}
			/*if(b){
				break;
			}*/
			this.counter++;
			
		}
		
		this.parseRestOfByteCode();
		/*int z = this.lfc.readNext();
		int z1 = this.lfc.readNext();
		System.out.println(z + "    -      " + z1);*/
		return this.hasMethodName && this.hasParameters;
	}
	
	private void parseRestOfByteCode() {
		//TODO To implement reading the methods-fields modifiers
		
	}

	private void parseUTF8(String methodName, String parameters) {
		//System.out.println("TUKA SUM!!");
		int utf8Lenght = this.lfc.readNext() * 256 + this.lfc.readNext();
		String utf8Name = "";
		for(int i=0; i<utf8Lenght ; i++){
			utf8Name = utf8Name + this.lfc.readNext();
		}
		this.intStringMap.put(this.counter, utf8Name);
		if(!this.hasMethodName){
			if(utf8Name.equals(methodName)){
				this.hasMethodName = true;
			}
		}
		if(!this.hasParameters){
			if(utf8Name.equals(parameters)){
				this.hasParameters = true;
			}
		}
		//System.out.println("Imeto e : " + utf8Name);
		//return false;
	}

	private void parseNemeAndType() {
		for(int i=0; i<4 ; i++){
			this.lfc.readNext();
		}
	}

	private void parseFieldRef() {
		for(int i=0; i<4 ; i++){
			this.lfc.readNext();
		}
	}

	private void parseClass() {
		for(int i=0; i<2 ; i++){
			this.lfc.readNext();
		}
	}

	private void parseMethodRef() {
		for(int i=0; i<4 ; i++){
			this.lfc.readNext();
		}
	}
	
	public void closeReader(){
		this.lfc.closeLookForwardReader();
	}

	public Field findField(Token fieldOrMethodName, Class clazz) {
		this.findMethodOrFieldFromClassFile(fieldOrMethodName.getText(), "");
		for(int i = 0; i < 6; i++){
			this.lfc.readNext();
		}
		int interfaceCounter = this.lfc.readNext() * 256 + this.lfc.readNext();
		if(interfaceCounter != 0){
			//TODO Parse intefrace
		}
		int fieldCounter = this.lfc.readNext() * 256 + this.lfc.readNext();
		
		if(fieldCounter != 0){
			return this.parseFields(fieldOrMethodName, clazz, fieldCounter);
		}else{
			return null;
		}
	}

	private Field parseFields(Token fieldOrMethodName, Class clazz, int fieldCounter) {
		int access_flags = 0;
		int name_index = 0;
		int descriptor_index = 0;
		int attributes_count = 0;
		
		
		for(int i = 0; i < fieldCounter; i++){
			access_flags = this.lfc.readNext() * 256 + this.lfc.readNext();
			name_index = this.lfc.readNext() * 256 + this.lfc.readNext();
			descriptor_index = this.lfc.readNext() * 256 + this.lfc.readNext();
			attributes_count = this.lfc.readNext() * 256 + this.lfc.readNext();
			
			String fieldName = this.intStringMap.get(name_index);
			String type = this.intStringMap.get(descriptor_index);
			if(fieldName.equals(fieldOrMethodName.getText())){
				return this.parseDecodeField(access_flags, fieldName, type, clazz, fieldOrMethodName);
			}
			if(attributes_count != 0){
				//this.parseAttribute_Info8);
			}
		}
		return null;
	}

	private Field parseDecodeField(int access_flags, String fieldName,
			String type, Class clazz, Token fieldRef) {
		boolean isPrivate = false;
		boolean isStatic = false;
		if(access_flags == 2){
			isPrivate = true;
		}else{
			isStatic = true;
		}
		if(type.charAt(0) == 'I'){
			return new Field(new Type(new Token(this.tks.INT, "int")), fieldName, clazz, fieldRef);
		}else if(type.charAt(0) == 'C'){
			//TODO To implement for Char
		}else if(type.charAt(0) == '['){
			if(type.charAt(1) == 'L'){
				String objectName = "";
				for(int i = 1; i < type.length()-1; i++){
					objectName = objectName + type.charAt(i);
				}
				return new Field(new Type(new Type(new Class(objectName, null, ""))), fieldName, clazz, fieldRef);
			}else if(type.charAt(1) == 'I'){
				return new Field(new Type(new Type(new Token(this.tks.INT, "int"))), fieldName, clazz, fieldRef);
			}else if(type.charAt(1) == 'C'){
				//TODO To implement for Char
			}else{
				//TODO To implement more dimensional Arrays and also other types
			}
		}else if(type.charAt(0) == 'L'){
			String objectName = "";
			for(int i = 1; i < type.length()-1; i++){
				objectName = objectName + type.charAt(i);
			}
			//TODO Check if class exists
			return new Field(new Type(new Class(objectName, null, "")), fieldName, clazz, fieldRef);
		}else{
			//TODO To implement for other Types
		}
		return null;
	}

	public int getMaxStack() {
		int maxSize = 0;
		int counterSize = 0;
		byte[] barray = this.bw.getByteArray();
		for(int i = 0; i < barray.length; i++){
			int c = barray[i];
			if(c < 0){
				c = c + 256;
			}
			//= (char) (barray[i] & 0xff)
			if(this.op.NEW == c){
				i = i+2;
				counterSize++;
			}else if(this.op.DUP == c){
				counterSize++;
			}else if(this.op.INVOKESPECIAL == c){
				i = i+2;
				counterSize--;
			}else if(this.isAstore(c)){
				counterSize--;
			}else if(this.isALoad(c)){
				counterSize++;
			}else if(this.isNumber(c)){
				counterSize++;
			}else if(this.op.IASTORE == c){
				counterSize = 0;
			}else if(this.op.PUTFIELD == c){
				i = i+2;
				counterSize = counterSize-2;
			}else if(this.op.BIPUSH == c){
				i = i + 1;
				counterSize++;
			}else if(this.op.NEWARRAY == c){
				i = i+1;
			}else if(this.op.GETFIELD == c){
				i = i+2;
			}else if(this.isILoad(c)){
				counterSize++;
			}else if(this.op.IALOAD == c){
				counterSize--;
			}else if(this.op.IMUL == c){
				counterSize--;
			}else if(this.op.ISUB == c){
				counterSize--;
			}else if(this.op.IDIV == c){
				counterSize--;
			}else if(this.op.IADD == c){
				counterSize--;
			}
			if(maxSize < counterSize){
				maxSize = counterSize;
			}
		}
		return maxSize;
	}

	private boolean isILoad(int c) {
		if(this.op.ILOAD == c){
			return true;
		}else if(this.op.ILOAD_0 == c){
			return true;
		}else if(this.op.ILOAD_1 == c){
			return true;
		}else if(this.op.ILOAD_2== c){
			return true;
		}else if(this.op.ILOAD_3== c){
			return true;
		}
	return false;
	}

	private boolean isNumber(int c) {
		if(this.op.ICONST_0 == c){
			return true;
		}else if(this.op.ICONST_0 == c){
			return true;
		}else if(this.op.ICONST_1 == c){
			return true;
		}else if(this.op.ICONST_2== c){
			return true;
		}else if(this.op.ICONST_3== c){
			return true;
		}else if(this.op.ICONST_4== c){
			return true;
		}else if(this.op.ICONST_5== c){
			return true;
		}
	return false;
	}

	private boolean isAstore(int c) {
		if(this.op.ASTORE == c){
			return true;
		}else if(this.op.ASTORE_0 == c){
			return true;
		}else if(this.op.ASTORE_1 == c){
			return true;
		}else if(this.op.ASTORE_2== c){
			return true;
		}else if(this.op.ASTORE_3== c){
			return true;
		}
	return false;
	}
	
	private boolean isALoad(int c) {
		if(this.op.ALOAD == c){
			return true;
		}else if(this.op.ALOAD_0 == c){
			return true;
		}else if(this.op.ALOAD_1 == c){
			return true;
		}else if(this.op.ALOAD_2== c){
			return true;
		}else if(this.op.ALOAD_3== c){
			return true;
		}
	return false;
	}
}
