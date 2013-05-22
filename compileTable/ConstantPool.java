package compileTable;

import mapsTable.ClassIntMap;
import mapsTable.FieldIntMap;
import mapsTable.IntStringMap;
import mapsTable.MethodIntMap;
import mapsTable.StringIntMap;
import symbolTable.Class;
import symbolTable.Field;
import symbolTable.Method;
import symbolTable.StringArrayList;

public class ConstantPool
{

	private final ByteWriter byteWriter;
	
	private final ClassIntMap classMap;
	
	private final MethodIntMap methodMap;
	
	private final StringIntMap utf8Map;
	
	//private final IntStringMap utf8IntStringMap;
	
	private final FieldIntMap fieldMap;
	
	private final Class clazz;
	
	private final StringArrayList types;
	
	private int counter; 
	
	public ConstantPool(Class clazz){
		this.clazz = clazz;
		this.classMap = new ClassIntMap();
		this.methodMap = new MethodIntMap();
		this.utf8Map = new StringIntMap();
		this.fieldMap = new FieldIntMap();
		this.byteWriter = new ByteWriter();
		this.types = new StringArrayList();
		//this.utf8IntStringMap = new IntStringMap();
		this.counter = 1;
	}
	
	public ByteWriter getByteWriter(){
		return this.byteWriter;
	}
	
	public ClassIntMap getClassMap(){
		return this.classMap;
	}
	
	public MethodIntMap getMethodMap(){
		return this.methodMap;
	}
	
	public StringIntMap getUtf8Map(){
		return this.utf8Map;
	}
	
	/*public IntStringMap getUtf8IntStringMap(){
		return this.utf8IntStringMap;
	}*/
	
	/*public void addUtf8IntString(Integer integer, String name) {
		this.utf8IntStringMap.put(integer, name);
	}*/
	
	public void addUtf8(String str) {
		this.utf8Map.put(str, this.counter);
		this.counter++;
	}
	
	public void addClassMap(Class clazz){
		this.classMap.put(clazz, this.counter);
		this.counter++;
	}
	
	public void addMethodMap(Method m){
		this.methodMap.put(m, this.counter);
		this.counter++;
	}
	
	public void addField(Field field){
		this.fieldMap.put(field, this.counter);
		this.counter++;
	}
	
	public int getSize(){
		return this.counter;
	}
	public FieldIntMap getFieldMap(){
		return this.fieldMap;
	}
	
	public int getCounter(){
		return this.counter;
	}

	public void makeByteWriter() {
		int counter = 0;
		this.byteWriter.write1Byte(0x0a);
		this.byteWriter.write2Byte(this.classMap.get(this.clazz.getMethods().get(0).getClazz()));
		this.byteWriter.write2Byte(this.utf8Map.get("NameAndType"));
		counter++;
		for(int i = 0;i <this.clazz.getClassReferences().size(); i++){
			this.byteWriter.write1Byte(0x07);
			this.byteWriter.write2Byte(this.utf8Map.get(this.clazz.getClassReferences().get(i).getName()));
			this.byteWriter.write1Byte(0x0a);
			this.byteWriter.write2Byte(this.classMap.get(this.clazz.getClassReferences().get(i)));
			this.byteWriter.write2Byte(this.utf8Map.get("NameAndType"));
			
		}
		for(int i = 0; i < this.clazz.getFieldReferences().size(); i++){
			this.byteWriter.write1Byte(0x09);
			this.byteWriter.write2Byte(this.classMap.get(this.clazz.getFieldReferences().get(i).getClazz()));
			this.byteWriter.write2Byte(this.utf8Map.get("NameAndType" + counter));
			counter++;
		}
		
		this.byteWriter.write1Byte(0x07);
		this.byteWriter.write2Byte(this.utf8Map.get(this.clazz.getName()));
		this.byteWriter.write1Byte(0x07);
		this.byteWriter.write2Byte(this.utf8Map.get(this.clazz.getSuperClass().getName()));
		
		this.writeUtfFields();
		this.writeUtfMethods();
		
		this.byteWriter.write1Byte(0x01);
		this.byteWriter.write2Byte("Code".length());
		this.writeUtfStrings("Code");
		
		if(this.utf8Map.containsKey("StackMapTable")){
			this.byteWriter.write1Byte(0x01);
			this.byteWriter.write2Byte("StackMapTable".length());
			this.writeUtfStrings("StackMapTable");
		}
		
		this.byteWriter.write1Byte(0x01);
		this.byteWriter.write2Byte("SourceFile".length());
		this.writeUtfStrings("SourceFile");
		
		String sourcefile = this.clazz.getName() + ".java";
		this.byteWriter.write1Byte(0x01);
		this.byteWriter.write2Byte(sourcefile.length());
		this.writeUtfStrings(sourcefile);
		
		this.byteWriter.write1Byte(0x0c);
		this.byteWriter.write2Byte(this.utf8Map.get(this.clazz.getMethods().get(0).getName()));
		this.byteWriter.write2Byte(this.utf8Map.get(this.clazz.getMethods().get(0).getParametersDescriptor()));
		
		for(int i = 0;i <this.clazz.getClassReferences().size(); i++){
			this.byteWriter.write1Byte(0x01);
			this.byteWriter.write2Byte(this.clazz.getClassReferences().get(i).getName().length());
			this.writeUtfStrings(this.clazz.getClassReferences().get(i).getName());
		}
		for(int i = 0; i < this.clazz.getFieldReferences().size(); i++){
			this.byteWriter.write1Byte(0x0c);
			this.byteWriter.write2Byte(this.utf8Map.get(this.clazz.getFieldReferences().get(i).getName()));
			this.byteWriter.write2Byte(this.utf8Map.get(this.clazz.getFieldReferences().get(i).getType().getDescriptor()));
		}
		
		this.byteWriter.write1Byte(0x01);
		this.byteWriter.write2Byte(this.clazz.getName().length());
		this.writeUtfStrings(this.clazz.getName());
		
		this.byteWriter.write1Byte(0x01);
		this.byteWriter.write2Byte(this.clazz.getSuperClass().getName().length());
		this.writeUtfStrings(this.clazz.getSuperClass().getName());
		//this.byteWriter.printByteArray();
		StringArrayList strings = new StringArrayList();
		for(int i = 0; i < this.clazz.getFieldReferences().size(); i++){
			//System.out.println(this.utf8Map.get(this.clazz.getFieldReferences().get(i).getName()) + "    " + this.clazz.getFieldReferences().get(i).getName()  + "   " + this.clazz.getFieldReferences().get(i).getType().getDescriptor());
			this.byteWriter.write1Byte(0x01);
			this.byteWriter.write2Byte(this.clazz.getFieldReferences().get(i).getName().length());
			this.writeUtfStrings(this.clazz.getFieldReferences().get(i).getName());
			
			if(!strings.contains(this.clazz.getFieldReferences().get(i).getType().getDescriptor())){
				strings.add(this.clazz.getFieldReferences().get(i).getType().getDescriptor());
				this.byteWriter.write1Byte(0x01);
				this.byteWriter.write2Byte(this.clazz.getFieldReferences().get(i).getType().getDescriptor().length());
				this.writeUtfStrings(this.clazz.getFieldReferences().get(i).getType().getDescriptor());
			}
		}
		
	}
	
	private void writeUtfFields(){
		for(int i = 0; i < this.clazz.getFields().size(); i++){
			Field f = this.clazz.getFields().get(i);
			this.byteWriter.write1Byte(0x01);
			this.byteWriter.write2Byte(f.getName().length());
			this.writeUtfStrings(f.getName());
			if(!this.types.contains(f.getType().getDescriptor())){
				this.types.add(f.getType().getDescriptor());
				this.byteWriter.write1Byte(0x01);
				this.byteWriter.write2Byte(f.getType().getDescriptor().length());
				this.writeUtfStrings(f.getType().getDescriptor());
			}
			//this.writeUtfMethodParameters(m);
		}
	}
	
	private void writeUtfMethods() {
		for(int i = 0; i < this.clazz.getMethods().size(); i++){
			Method m = this.clazz.getMethods().get(i);
			this.byteWriter.write1Byte(0x01);
			this.byteWriter.write2Byte(m.getName().length());
			this.writeUtfStrings(m.getName());
			this.writeUtfMethodParameters(m);
		}
	}

	private void writeUtfMethodParameters(Method method) {
		this.byteWriter.write1Byte(0x01);
		String str = "(";
		for(int i = 0; i < method.getParameterList().getSize(); i++){
			str = str + method.getParameterList().getParameter(i).getType().getDescriptor();
		}
		str = str + ")" + method.getRetrunType().getDescriptor();
		this.byteWriter.write2Byte(str.length());
		this.writeUtfStrings(str);
	}
	
	private void writeUtfStrings(String str){
		for(int i = 0; i < str.length(); i++){
			this.byteWriter.write1Byte(str.charAt(i));
		}
	}
	
	public void printConstantPool(){
		this.byteWriter.printByteArray();
	}
	
}
