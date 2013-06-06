package compileTable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import symbolTable.Class;
import symbolTable.Field;
import symbolTable.FieldArrayList;
import symbolTable.Method;
import symbolTable.MethodArrayList;

public class ClassFile {
	
	private final Class clazz;
	
	private final String name;
	
	private ByteWriter code;
	
	private final ConstantPool cp;
	
	private final Operations operations;
	
	
	public ClassFile(Class clazz, String name){
		this.clazz = clazz;
		this.name = name;
		this.code = new ByteWriter();
		this.cp = new ConstantPool(this.clazz);
		this.operations = new Operations();
		this.makeStartJavaCode();
		this.make();

		OutputStream f;
		
		try {
			f = new FileOutputStream ("C:\\Users\\User\\Desktop\\Compiler Tests\\" + name);
			this.code.writeTo(f);
			f.close();
		} catch (IOException e) {
				e.printStackTrace();
			
		}
		
	}
	
	private void makeStartJavaCode(){
		this.code.write4Byte(0xCAFEBABE);
		this.writeMinorMajorVersion();
	}
	
	private void writeMinorMajorVersion(){
		this.code.write2Byte(0x0000);
		this.code.write2Byte(0x0033);
	}
	
	public ConstantPool getConstatnPool(){
		return this.cp;
	}
	
	public void printClassFile(){
		this.code.printByteArray();
	}

	public void make() {
		//this.cp
		this.cp.addMethodMap(this.clazz.getMethods().get(0));
		
		for(int i = 0; i<this.clazz.getClassReferences().size(); i++){
			
			this.cp.addClassMap(this.clazz.getClassReferences().get(i));
			this.cp.addMethodMap(this.clazz.getClassReferences().get(i).getMethoddFromClassMethodsByName("<init>"));
		}
		
		for(int i = 0; i<this.clazz.getFieldReferences().size(); i++){
			this.cp.addField(this.clazz.getFieldReferences().get(i));
		}
		
		
		this.cp.addClassMap(this.clazz);
		this.cp.addClassMap(this.clazz.getSuperClass());
		
		if(!this.clazz.getFields().isEmpty()){
			this.makeFieldsUtf8();
		
			
		}
		this.makeCostantUTF8();
		
		this.cp.makeByteWriter();
		
		this.code.write2Byte(this.cp.getSize());
		//Create Constant pool table
		this.code.writeAll(this.cp.getByteWriter());
		//Writing the class access flags
		this.writeAccessFlags();
		//Writing this class to the byte code output
		this.code.write2Byte(this.cp.getClassMap().get(this.clazz));
		//Writing the super class to the byte code output
		this.code.write2Byte(this.cp.getClassMap().get(this.clazz.getSuperClass()));
		//Interface count
		this.code.write2Byte(0x00);
		//TODO TO BE CHECKED
		//Fields count
		this.code.write2Byte(this.clazz.getFields().size());
		if(!this.clazz.getFields().isEmpty()){
			this.makeFieldCode();
		}
		
		//Methods count
		this.code.write2Byte(this.clazz.getMethods().size());
		this.makeCodeForMethods();

		this.code.write2Byte(0x01);
		this.code.write2Byte(this.cp.getUtf8Map().get("SourceFile"));
		this.code.write4Byte(0x02);
		String source = this.clazz.getName() + ".java";
		this.code.write2Byte(this.cp.getUtf8Map().get(source));
		
		
	}
	private void makeFieldsUtf8() {
		for(int i = 0; i<this.clazz.getFields().size(); i++){
			this.cp.addUtf8(this.clazz.getFields().get(i).getName());
			if(!this.cp.getUtf8Map().containsKey(this.clazz.getFields().get(i).getType().getDescriptor())){
				this.cp.addUtf8(this.clazz.getFields().get(i).getType().getDescriptor());
			}
		}
		
	}

	private void makeFieldCode() {
		for(int i = 0; i<this.clazz.getFields().size(); i++){
			//System.out.println("TUKA SUM");
			Field f = this.clazz.getFields().get(i);
			this.writeFieldsAccessFlags(f);
			this.code.write2Byte(this.cp.getUtf8Map().get(f.getName()));
			this.code.write2Byte(this.cp.getUtf8Map().get(f.getType().getDescriptor()));
			this.code.write2Byte(0x00);
		}
		
	}
	
	private void makeCostantUTF8() {
		this.makeParseClassMethodClassIndex();
		this.cp.addUtf8("Code");
		if(this.checkStackMapTable()){
			this.cp.addUtf8("StackMapTable");
		}
		this.cp.addUtf8("SourceFile");
		this.cp.addUtf8(this.clazz.getName() + ".java");
		this.addNameAndType();
		this.cp.addUtf8(this.clazz.getName());
		this.cp.addUtf8(this.clazz.getSuperClass().getName());
		this.addUtf8FieldRef();
		
	}
	

	private void addUtf8FieldRef() {
		for(int i = 0; i < this.clazz.getFieldReferences().size(); i++){
			if(!this.cp.getUtf8Map().containsKey(this.clazz.getFieldReferences().get(i).getName())){
				this.cp.addUtf8(this.clazz.getFieldReferences().get(i).getName());
			}
			if(!this.cp.getUtf8Map().containsKey(this.clazz.getFieldReferences().get(i).getType().getDescriptor())){
				this.cp.addUtf8(this.clazz.getFieldReferences().get(i).getType().getDescriptor());
			}
			
		}
		
	}

	private void addNameAndType() {
		int counter = 0;
		this.cp.addUtf8("NameAndType");
		counter++;
		for(int i = 0; i < this.clazz.getClassReferences().size(); i++){
			this.cp.addUtf8(this.clazz.getClassReferences().get(i).getName());
		}
		
		for(int i = 0; i < this.clazz.getFieldReferences().size(); i++){
			this.cp.addUtf8("NameAndType"+counter);
			counter++;
		}
	}

	private boolean checkStackMapTable() {
		boolean b = true;
		for(int i = 0; i < this.clazz.getMethods().size(); i++){
			Method m = this.clazz.getMethods().get(i);
			if((m.getLoopParser() ==  null) && (m.getIfParser() ==  null)){
				b = false;
			}else{
				b = true;
			}
			
		}
		return b;
	}

	private void writeAccessFlags() {
		this.code.write2Byte(0x21);
		
	}

	private void makeCodeForMethods() {
		for(int i = 0; i < this.clazz.getMethods().size(); i++){
			
			Method m = this.clazz.getMethods().get(i);
			this.writeMethodAccessFlags(m);
			this.code.write2Byte(this.cp.getUtf8Map().get(m.getName()));
			this.code.write2Byte(this.cp.getUtf8Map().get(m.getParametersDescriptor()));
			//System.out.println(m.getName()+ "   " + m.getParametersDescriptor());
			if(m.getName().equals("<init>")){
				this.makeCodeForInitMethod(m);
			}else{
				this.makeCodeForMethod(m);
			}
		}

		
	}

	private void writeMethodAccessFlags(Method m) {
		if(!m.isPrivate() && m.isStatic()){
			this.code.write2Byte(0x09);
		}else if(!m.isPrivate()){
			this.code.write2Byte(0x01);
		}else{
			this.code.write2Byte(0x02);
		}
		
	}
	
	private void writeFieldsAccessFlags(Field f) {
		if(!f.isPrivate() && f.isStatic()){
			this.code.write2Byte(0x09);
		}else if(!f.isPrivate()){
			this.code.write2Byte(0x01);
		}else{
			this.code.write2Byte(0x02);
		}
		
	}

	private void makeCodeForMethod(Method method) {
		this.code.write2Byte(1);
		this.code.write2Byte(this.cp.getUtf8Map().get("Code"));
		ByteWriter b = new ByteWriter();
		b.write2Byte(method.getMaxStack());
		b.write2Byte(method.getFieldMapPostition());
		//method.getByteWriter().printByteArray();
		b.write4Byte(method.getByteWriter().size());
		b.writeAll(method.getByteWriter());
		b.write2Byte(0x00);
		if((method.getLoopParser() !=  null) || (method.getIfParser() !=  null)){
			b.write2Byte(1);
			b.write2Byte(this.cp.getUtf8Map().get("StackMapTable"));
			b.writeAll(method.getStackMapTableCode());
			//b.write4Byte(0x05);
			//b.write2Byte(method.getLoopParser().getStackMapTable().size());
			//for(int i = 0; i < method.getLoopParser().getStackMapTable().size(); i++){

				
				//b.write1Byte(method.getLoopParser().getStackMapTable().get(i));
			//}
		}else{
			b.write2Byte(0);
		}
		this.code.write4Byte(b.size());
		this.code.writeAll(b);
	}

	private void makeCodeForInitMethod(Method method) {
		this.code.write2Byte(0x01);
		this.code.write2Byte(this.cp.getUtf8Map().get("Code"));
		
		ByteWriter b = new ByteWriter();
		b.write2Byte(0x01);
		b.write2Byte(method.getFieldMapPostition());
		b.write4Byte(0x05);
		writeInitCode(b);
		b.write2Byte(0x00);
		b.write2Byte(0x00);
		this.code.write4Byte(b.size());
		this.code.writeAll(b);
	}

	

	private void writeInitCode(ByteWriter b) {
		b.write1Byte(this.operations.ALOAD_0);
		b.write1Byte(this.operations.INVOKESPECIAL);
		b.write2Byte(this.cp.getMethodMap().get(this.clazz.getMethods().get(0)));
		b.write1Byte(this.operations.RETURN);
	}

	private void makeParseClassMethodClassIndex() {
		//System.out.println(this.cp.getUtf8Map().get("I"));
		MethodArrayList m = this.clazz.getMethods();
		for(int i=0; i<m.size();i++){
			this.cp.addUtf8(m.get(i).getName());
			if(m.get(i).getParameterList().getSize() == 0){
				this.cp.addUtf8("()" + m.get(i).getRetrunType().getDescriptor());
			}else{
				String str = this.getUtf8ParameterString(m.get(i));
				this.cp.addUtf8(str);
				
			}
			
		}
	}

	private String getUtf8ParameterString(Method method) {
		String str = "(";
		for(int i = 0; i < method.getParameterList().getSize(); i++){
			str = str + method.getParameterList().getParameter(i).getType().getDescriptor();
		}
		str = str + ")" + method.getRetrunType().getDescriptor();
		return str;
	}

}
