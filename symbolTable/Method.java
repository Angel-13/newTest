package symbolTable;

//import parser.Parser;
import compileTable.ByteReader;
import compileTable.ByteWriter;

import Code.Expression;
import mapsTable.FieldIntMap;
import milestone2.IfParser;
import milestone2.LoopParser;
import milestone2.Parser;

public class Method {
	
	private final ParameterList pList; 
	
	private final FieldArrayList localVariables;
	
	private final String name;
	
	private final Type returnType;
	
	private final boolean isStatic;
	
	private final boolean isPrivate;
	
	private final Class clazz;
	
	private final Parser parser;
	
	private IfParser ifparser;
	
	private LoopParser loopparser;
	
	private final FieldIntMap filedmap;
	
	private int position;
	
	private ExpressionsList expressions;
	
	private final ByteWriter bWriter;
	
	private final ByteWriter stackMapTable;
	
	private final FieldArrayList StackFrameFieldCounter;
	
	private final FieldArrayList allReadyDefined;
	
	private final StackMapTableList stackMapTablelist;
	
	private int maxStack;
	
	public Method(Parser parser, String name, Type returnType, ParameterList pList, Class clazz, boolean isStatic, boolean isPrivate){
		this.parser = parser;
		this.isPrivate = isPrivate;
		this.isStatic = isStatic;
		this.pList = pList;
		this.name = name;
		this.returnType = returnType;
		this.clazz = clazz;
		this.localVariables = new FieldArrayList();
		this.loopparser = null;
		this.ifparser = null;
		this.filedmap = new FieldIntMap();
		this.bWriter = new ByteWriter();
		this.StackFrameFieldCounter = new FieldArrayList();
		this.allReadyDefined = new FieldArrayList();
		this.stackMapTablelist = new StackMapTableList();
		this.stackMapTable = new ByteWriter();
		if(this.isStatic){
			this.position = 0;
		}else{
			this.position = 1;
		}
		this.expressions = new ExpressionsList();
		this.maxStack = 0;
		this.fieldMapInitialization();
	}

	private void fieldMapInitialization() {
		for(int i = 0; i < this.pList.getSize(); i++){
			this.addFildInFieldMap(this.pList.getParameter(i));
		}
	}

	public ParameterList getParameterList(){
		return this.pList;
		
	}
	
	public FieldArrayList getLocalVariables(){
		return this.localVariables;
	}
	
	public FieldArrayList getStackFrameFieldCounter(){
		return this.StackFrameFieldCounter;
	}
	
	public FieldArrayList getAlreadyDefinedFields(){
		return this.allReadyDefined;
	}
	
	public ByteWriter getStackMapTableCode(){
		return this.stackMapTable;
	}
	
	public void addParameter(Field field){
		this.pList.addParameter(field);
		this.addFildInFieldMap(field);
	}
	
	public void addToStackMapTableList(StackMapTableObject smtl){
		this.stackMapTablelist.add(smtl);
	}
	
	public void addFieldToStackFrameFieldCounter(Field field){
		this.StackFrameFieldCounter.add(field);
	}
	
	public void addToAlreadyDefinedFields(Field field){
		this.allReadyDefined.add(field);
	}
	
	public void addLocalVariable(Field field){
		this.localVariables.add(field);
		this.addFildInFieldMap(field);
	}
	
	public void ResetStackFrameFieldCounter(){
		this.StackFrameFieldCounter.clear();
	}
	
	public StackMapTableList getStackMapTableList(){
		return this.stackMapTablelist;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Class getClazz(){
		return this.clazz;
	}
	
	public Type getRetrunType(){
		return this.returnType;
	}
	
	public boolean isStatic(){
		return this.isStatic;
	}
	
	public boolean isPrivate(){
		return this.isPrivate;
	}
	
	
	public Parser getParser(){
		return this.parser;
	}
	
	public ByteWriter getByteWriter(){
		return this.bWriter;
	}
	
	public Field getFieldByName(String name){
		for(int i=0; i<this.pList.getSize(); i++){
			if(this.pList.getParameter(i).getName().equals(name)){
				return this.pList.getParameter(i);
			}
		}
		for(int i=0; i<this.localVariables.size(); i++){
			if(this.localVariables.get(i).getName().equals(name)){
				return this.localVariables.get(i);
			}
		}
		
		return null;
	}
	
	public boolean isContainingFildMethodAndClass(String name, boolean forError){
		for(int i=0; i<this.pList.getSize(); i++){
			if(this.pList.getParameter(i).getName().equals(name)){
				return true;
			}
		}
		for(int i=0; i<this.localVariables.size(); i++){
			if(this.localVariables.get(i).getName().equals(name)){
				return true;
			}
		}
		boolean b = false;
		if(!forError){
			b = this.clazz.isAllreadyContainingField(name);
		}
		return b;
	}
	
	public boolean isContainingFildMethodAndClassAndLoops(String name, boolean forError){
		for(int i=0; i<this.pList.getSize(); i++){
			if(this.pList.getParameter(i).getName().equals(name)){
				return true;
			}
		}
		for(int i=0; i<this.localVariables.size(); i++){
			if(this.localVariables.get(i).getName().equals(name)){
				return true;
			}
		}
		boolean b = false;
		if(!forError){
			b = this.clazz.isAllreadyContainingField(name);
			
			if(b){
				return b;
			}
		}
		IfParser ifp= this.ifparser;
		while( ifp != null){
			b = ifp.isContainingField(name);
			if(b){
				break;
			}
			ifp = ifp.getIfParser();
		}
		return b;
	}
	
	public Field findFieldInsideMethoAndClassAndScope(String name){
		int j = 0;
		while (j < this.pList.getSize()){
			if(this.pList.getParameter(j).getName().equals(name)){
				return this.pList.getParameter(j);
			}
			j++;
		}
		for(int i=0; i<this.localVariables.size(); i++){
			if(this.localVariables.get(i).getName().equals(name)){
				return this.localVariables.get(i);
			}
		}
		
		Field f = this.clazz.getFieldFromClassFieldsByName(name);
		IfParser ifp= this.ifparser;
		LoopParser lp = this.loopparser;
		while( ifp != null){
			if(ifp.isContainingField(name)){
				return ifp.getFieldByName(name);
			}
			ifp = ifp.getIfParser();
		}
		
		return f;
		
	}
	
	public void printVariables(){
		for(int i=0; i<this.pList.getSize(); i++){
			System.out.println("Name: " + this.pList.getParameter(i).getName() + ", Type: " + this.pList.getParameter(i).getType().toString());
		}
		for(int i=0; i<this.localVariables.size(); i++){
			System.out.println("Name: " + this.localVariables.get(i).getName() + ", Type: " + this.localVariables.get(i).getType().toString());
		}
	}
	
	public int getVariableSize(){
		return this.pList.getSize()+this.localVariables.size();
	}
	
	public Field findFieldInsideMethoAndClass(String name){
		int j = 0;
		while (j < this.pList.getSize()){
			if(this.pList.getParameter(j).getName().equals(name)){
				return this.pList.getParameter(j);
			}
			j++;
		}
		for(int i=0; i<this.localVariables.size(); i++){
			if(this.localVariables.get(i).getName().equals(name)){
				return this.localVariables.get(i);
			}
		}
		
		Field f = this.clazz.getFieldFromClassFieldsByName(name);
		
		return f;
		
	}
	
	public boolean isContainingFild(String name){
		for(int i=0; i<this.pList.getSize(); i++){
			if(this.pList.getParameter(i).getName().equals(name)){
				return true;
			}
		}
		for(int i=0; i<this.localVariables.size(); i++){
			if(this.localVariables.get(i).getName().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	public void setIfParser(IfParser i){
		this.ifparser = i;
	}
	
	public void setLoopParser(LoopParser l){
		this.loopparser = l;
	}
	
	/*public void printScopes(){
		
		IfParser ifp= this.ifparser;
		while( ifp != null){
			ifp.printPlist();
			
			ifp = ifp.getIfParser();
		}
	}*/
	
	public String printParameters(){
		String str = "";
		for(int i = 0; i <this.pList.getSize(); i++){
			str = str + "Name:" + this.pList.getParameter(i).getName() + ", Type:" + this.pList.getParameter(i).getType().toString() + "\n";
		}
		System.out.println(str);
		return str;
	}
	
	public void addFildInFieldMap(Field f){
		this.filedmap.put(f, position);
		this.position = this.position + 1;
	}
	
	public FieldIntMap getFieldMap(){
		return this.filedmap;
	}
	
	public int getFieldMapPostition(){
		return this.position;
	}
	
	public void printFieldsMapSize(){
		for(int i = 0; i <this.pList.getSize(); i++){
			System.out.println(this.filedmap.get(this.pList.getParameter(i)));
		}
	}
	
	public void addExpresssion(Expression ex){
		this.expressions.add(ex);
	}
	
	public ExpressionsList Expressions(){
		return this.expressions;
	}
	
	public IfParser getIfParser(){
		return this.ifparser;
	}

	public int calculateExpressionPosition() {
		int number = 0;
		for(int i = 0; i < this.expressions.size(); i++){
			number = number + this.expressions.get(i).getExpressionCode().size();
		}
		IfParser ifp = this.ifparser;
		int i = 0;
		if(ifp != null){
			while(ifp.getIfParser() != null){
				number = number + ifp.getLengthFromAllExpressions();
				ifp = ifp.getIfParser();
			}
		}
		return number;
	}
	
	public int getStartOfLoopPosition(){
		int pos=0;
		for(int i = 0; i < this.expressions.size(); i++){
			pos = pos + this.expressions.get(i).getExpressionCode().size();
		}
		return pos;
	}

	public void makeByteWriter() {
		for(int i = 0; i < this.expressions.size(); i++){
			this.bWriter.writeAll(this.expressions.get(i).getExpressionCode());
		}
		if(this.returnType.isVoid()){
			//0xb1 - is return byte code
			this.bWriter.write1Byte(0xb1);
		}
	}
	
	public void addEpressions(ExpressionsList ex){
		for(int i = 0; i < ex.size(); i++){
			this.expressions.add(ex.get(i));
		}
	}

	/*public void makeItFinishTheByteCodeForMethod(Token t, String s) {
		ArthmeticExpression ax = new ArthmeticExpression(this.filedmap, this);
		this.bWriter.writeAll(ax.getCodeForIdentifeerOrNumber(t, false));
		this.bWriter.write1Byte(0xac);
		
	}*/
	
	public String getParametersDescriptor(){
		String str = "(";
		for(int i = 0; i < this.getParameterList().getSize(); i++){
			str = str + this.getParameterList().getParameter(i).getType().getDescriptor();
		}
		str = str + ")" + this.getRetrunType().getDescriptor();
		return str;
	}
	
	public LoopParser getLoopParser(){
		return this.loopparser;
	}
	
	public void printExpressionsSize(){
		System.out.println(this.expressions.size());
	}

	private int calculateMaxStackSize() {
		ByteReader br;
		try {
			br = new ByteReader(this.bWriter);
			this.maxStack = br.getMaxStack();
			return this.maxStack;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//br.getMaxStack();
		return 0;
	}
	
	public int getMaxStack(){
		this.maxStack = this.calculateMaxStackSize();
		return this.maxStack;
	}
	
	public void printLocalFieldNames(){
		for(int i = 0; i < this.localVariables.size(); i++){
			System.out.println(this.localVariables.get(i).getName());
		}
	}
	
	public void printStackFrameFieldCounter(){
		for(int i = 0; i < this.StackFrameFieldCounter.size(); i++){
			System.out.println(this.StackFrameFieldCounter.get(i).getName());
		}
	}
	
	public void makeStackMapTableCode(){
		int numberOfEtnries = this.stackMapTablelist.size();
		ByteWriter b = new ByteWriter();
		for(int i = 0; i < this.stackMapTablelist.size(); i++){
			b.writeAll(this.stackMapTablelist.get(i).getExpressionCode());
		}
		int attribute_lenght = b.size() + 2;
		this.stackMapTable.write4Byte(attribute_lenght);
		this.stackMapTable.write2Byte(numberOfEtnries);
		this.stackMapTable.writeAll(b);
		
	}
	
	public int getStackMapTableListSum(){
		int sum = 0;
		for(int i=0; i<this.stackMapTablelist.size(); i++){
			if(i==0){
				sum = sum + this.stackMapTablelist.get(i).getPosition();
			}else{
				sum = sum + this.stackMapTablelist.get(i).getPosition() + 1;
			}
			
		}
		return sum;
	}
}
