package symbolTable;

import mapsTable.ClassIntMap;
import mapsTable.FieldIntMap;
import mapsTable.MethodIntMap;
import mapsTable.StringIntMap;


public class Class {
	
	private final MethodArrayList methods;
	
	private final MethodArrayList methodsRef;
	
	private final MethodArrayList methodsToBeCheckedIfExists;
	
	private final FieldArrayList fields;
	
	private final ClassArrayList importedClasses;
	
	private final ClassArrayList usedClasses;
	
	private final ConstructorArrayList constructorList;
	
	private final String name;
	
	private final Class superClass;
	
	private final String packageName;
	
	private final String filePath;
	
	private final ClassArrayList classReferences;
	
	private final ClassArrayList staticClassReferences;
	
	private final FieldArrayList fieldReferences;
	
	private final StringArrayList stringReferences;
	
	private final ClassIntMap classIntMap;
	
	private final StringIntMap stringIntMap;
	
	private final MethodIntMap methodIntMap;
	
	private final FieldIntMap fieldIntMap;
	
	private int counter;
	
	public Class(String name, Class superClass, String packageName, String filePath){
		this.importedClasses = new ClassArrayList();
		this.methods = new MethodArrayList();
		this.fields = new FieldArrayList();
		this.name = name;
		this.superClass = superClass;
		this.constructorList = new ConstructorArrayList();
		this.packageName = packageName;
		this.filePath = filePath;
		this.usedClasses = new ClassArrayList();
		this.classReferences = new ClassArrayList();
		this.classIntMap = new ClassIntMap();
		this.fieldReferences = new FieldArrayList();
		this.fieldIntMap = new FieldIntMap();
		this.counter = 1;
		this.methodsToBeCheckedIfExists = new MethodArrayList();
		this.methodIntMap = new MethodIntMap();
		this.methodsRef = new MethodArrayList();
		this.staticClassReferences = new ClassArrayList();
		this.stringReferences = new StringArrayList();
		this.stringIntMap = new StringIntMap();
		this.addInitMethod();
	}

	public Class(String name, Class superClass, String filePath){
		this.importedClasses = new ClassArrayList();
		this.methods = new MethodArrayList();
		this.fields = new FieldArrayList();
		this.name = name;
		this.superClass = superClass;
		this.constructorList = new ConstructorArrayList();
		this.packageName = "";
		this.filePath = filePath;
		this.usedClasses = new ClassArrayList();
		this.classReferences = new ClassArrayList();
		this.classIntMap = new ClassIntMap();
		this.fieldReferences = new FieldArrayList();
		this.fieldIntMap = new FieldIntMap();
		this.counter = 1;
		this.methodsToBeCheckedIfExists = new MethodArrayList();
		this.methodIntMap = new MethodIntMap();
		this.methodsRef = new MethodArrayList();
		this.staticClassReferences = new ClassArrayList();
		this.stringReferences = new StringArrayList();
		this.stringIntMap = new StringIntMap();
		this.addInitMethod();
	}


/**************************************************************************************
 *  addInitMethod()
 * 		-
 *************************************************************************************/
	private void addInitMethod() {
		Method m = new Method(null, "<init>", new Type(4), new ParameterList(), this.superClass, false, false);
		//this.counter++;
		this.methods.add(m);
		this.addMethodReference(m);
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getFilePath(){
		return this.filePath;
	}
	
	public MethodArrayList getMethods(){
		return this.methods;
	}
	
	public MethodArrayList getMethodsToBeCheckedIfExists(){
		return this.methodsToBeCheckedIfExists;
	}
	
	public FieldArrayList getFields(){
		return this.fields;
	}
	
	public Class getSuperClass(){
		return this.superClass;
	}
	
	public StringArrayList getStringReferences(){
		return this.stringReferences;
	}
	
	public StringIntMap getStringIntMap(){
		return this.stringIntMap;
	}
	
	public ClassArrayList getImportedArrayClasses(){
		return this.importedClasses;
	}
	
	public int getCounter(){
		return this.counter;
	}
	
	public ClassArrayList getUsedClasses(){
		return this.usedClasses;
	}
	
	public ClassArrayList getClassReferences(){
		return this.classReferences;
	}
	
	public ClassArrayList getStaticClassReferences(){
		return this.staticClassReferences;
	}
	
	public FieldArrayList getFieldReferences(){
		return this.fieldReferences;
	}
	
	public ClassIntMap getClassIntMap(){
		return this.classIntMap;
	}
	
	public FieldIntMap getFieldIntMap(){
		return this.fieldIntMap;
	}
	
	public MethodArrayList getMethodReferences(){
		return this.methodsRef;
	}
	
	public MethodIntMap getMethodIntMap(){
		return this.methodIntMap;
	}
	
	public void addStrinReference(String str){
		this.stringReferences.add(str);
		this.addStringRefInStringIntMap(str);
	}
	
	public void addCassReference(Class classReference){
		this.classReferences.add(classReference);
		this.addClassInClassIntMap(classReference);
	}
	
	public void addStaticCassReference(Class classReference){
		this.staticClassReferences.add(classReference);
		this.addStaticClassInClassIntMap(classReference);
	}
	
	public void addMethodReference(Method methodReference){
		this.methodsRef.add(methodReference);
		this.addMethodRefInMethodIntMap(methodReference);
	}
	
	private void addStringRefInStringIntMap(String str) {
		this.stringIntMap.put(str, counter);
		this.counter = this.counter + 1;
	}
	
	private void addMethodRefInMethodIntMap(Method methodReference) {
		this.methodIntMap.put(methodReference, counter);
		this.counter = this.counter + 1;
	}
	
	public void addFieldReference(Field fielReference){
		this.fieldReferences.add(fielReference);
		this.addFieldInFieldIntMap(fielReference);
	}
	
	private void addClassInClassIntMap(Class classReference) {
		this.classIntMap.put(classReference, counter);
		this.counter = this.counter + 2;
	}
	
	private void addStaticClassInClassIntMap(Class classReference) {
		this.classIntMap.put(classReference, counter);
		this.counter = this.counter + 1;
	}
	
	private void addFieldInFieldIntMap(Field f) {
		this.fieldIntMap.put(f, counter);
		this.counter = this.counter + 1;
	}

	public ConstructorArrayList getconstructorList(){
		return this.constructorList;
	}
	
	public void addImportedClass(Class importedClass){
		this.importedClasses.add(importedClass);
	}
	
	public void addUsedClasses(Class used){
		this.usedClasses.add(used);
	}
	
	public void addClassMethod(Method m){
		this.methods.add(m);
		//this.classFile.getConstatnPool().addMethodMap(m);
	}
	
	public void addToMethodsToBeCheckedIfExists(Method m){
		this.methodsToBeCheckedIfExists.add(m);
		//this.classFile.getConstatnPool().addMethodMap(m);
	}
	
	public void addClassField(Field field){
		this.fields.add(field);
		//this.classFile.getConstatnPool().addField(field);
	}

	public boolean isAllreadyContainingMethod(Method m1){
		boolean isEqual = false;
		for(int i=0; i<this.methods.size();i++){
			Method m = this.methods.get(i);
			if((m.getName().equals(m1.getName())) 
					&& (m.getParameterList().getSize() == m1.getParameterList().getSize())){
				isEqual = this.areParmeterListSame(m.getParameterList(), m1.getParameterList());
				if(isEqual){
					return isEqual;
				}
			}
			isEqual = false;
		}
	
		return isEqual;
	}
	
	public String getPackageName(){
		return this.packageName;
	}
	
	public boolean isAllreadyContainingField(String name){
		for(int i=0; i<this.fields.size();i++){
			if(this.fields.get(i).getName().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isAllreadyContainingClassReference(String name){
		for(int i=0; i<this.classReferences.size();i++){
			//System.out.println(this.classReferences.get(i).getName());
			if(this.classReferences.get(i).getName().equals(name)){
				
				return true;
			}
		}
		return false;
	}
	
	public boolean isAllreadyContainingClass(String name){
		for(int i=0; i<this.importedClasses.size();i++){
			if(this.importedClasses.get(i).getName().equals(name)){
				return true;
			}
		}
		
		for(int i=0; i<this.usedClasses.size();i++){
			if(this.usedClasses.get(i).getName().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isAllreadyUsedClass(String name){
		for(int i=0; i<this.usedClasses.size();i++){
			if(this.usedClasses.get(i).getName().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	public Field getFieldFromClassFieldsByName(String name){
		for(int i=0; i< this.fields.size();i++){
			if(this.fields.get(i).getName().equals(name)){
				return this.fields.get(i);
			}
		}
		return null;
	}
	
	public Method getMethoddFromClassMethodsByName(String name, ParameterList pmList){
		for(int i=0; i<this.methods.size();i++){
			Method m = this.methods.get(i);
			if(m.getName().equals(name)){
				if(m.getParameterList().getSize() == pmList.getSize()){
					if(this.areParmeterListSame(m.getParameterList(), pmList)){
						return m;
					}
				}
			}
		}
		return null;
	}
	
	public Method getMethoddFromClassMethodReferenceByName(String name, ParameterList pmList){
		for(int i=0; i<this.methodsRef.size();i++){
			
			Method m = this.methodsRef.get(i);
			if(m.getName().equals(name)){
				if(m.getParameterList().getSize() == pmList.getSize()){
					if(this.areParmeterListSame(m.getParameterList(), pmList)){
						return m;
					}
				}
			}
		}
		return null;
	}
	
	public void printFields(){
		for(int i = 0;i < this.fields.size(); i++){
			System.out.println("Name: " + this.fields.get(i).getName() + ", Type: " + this.fields.get(i).getType().toString());
			
		}
		
	}
	
	public void printSymbolTable(){
		this.printPackage();
		this.printImportedCLasses();
		this.printFields();
		this.printMethods();
		
	}
	
	private boolean areParmeterListSame(ParameterList p1, ParameterList p2){
		boolean isEqual = true;
		for(int j=0; j < p1.getSize(); j++){
			//System.out.println(p1.getParameter(j).getType().toString() +"   TO STRING  " + p2.getParameter(j).getType().toString());
			isEqual = isEqual && this.areSameTypes(p1.getParameter(j).getType(), p2.getParameter(j).getType());
			/*if(p1.getParameter(j).getType().getType() == p2.getParameter(j).getType().getType()){
				Type t = p1.getParameter(j).getType();
				Type t1 = p2.getParameter(j).getType();
				if(t.isArray()){
					if(t.getBaseType().getType() == t1.getBaseType().getType()){
						isEqual = isEqual && true;
					}else{
						isEqual = isEqual && false;
					}
				}else{
					isEqual = isEqual && true;
				}
			}else{
				isEqual = isEqual && false;
			}*/
		}
		return isEqual;
	}
		
	
	private void printMethods() {
		
		for(int i=0 ; i<this.methods.size(); i++){
			System.out.println("Method Name: " + this.methods.get(i).getName() + " with parameters: "
					+ this.methods.get(i).printParameters());
			
		}	
		
		System.out.println("Method Name: " + this.methods.get(1).getName() + ", return type: " + this.methods.get(1).getRetrunType().getType()  + ", isStatic " + this.methods.get(1).isStatic() +
				", isPrivate " + this.methods.get(1).isPrivate() + " with parameters: " + this.methods.get(1).getParametersDescriptor());
		System.out.println(this.methods.get(1).getLocalVariables().size());
		System.out.println(this.methods.get(1).getLocalVariables().get(0).getName());
		System.out.println(this.methods.get(1).getLocalVariables().get(0).getType().toString());
		System.out.println(this.methods.get(1).getLocalVariables().get(0).getType().getDescriptor());
			
	}

	public void printImportedCLasses() {
		String str ="Imported Classes: ";
		for(int i=0 ; i<this.importedClasses.size(); i++){
			str = str + this.importedClasses.get(i).getName();
			System.out.println("Filepath: " + this.importedClasses.get(i).getFilePath() + "   " + str);
			str ="Imported Classes: ";
		}	
		//System.out.println(str);
	}

	private void printPackage() {
		System.out.println("Package: " + this.packageName);
		
	}

	public boolean makeUsedAndImportedClasses() {
		boolean b = false;
		String str = this.filePath;
		for(int i=0 ; i<this.importedClasses.size(); i++){
			str = str + this.importedClasses.get(i).getName() + ".java";
			
			str = this.filePath;
		}
		//new File(filePath);
		return b;
	}
	
	
	public void printClassReferences() {
		String str ="Class Reference: ";
		for(int i=0 ; i<this.classReferences.size(); i++){
			str = str + this.classReferences.get(i).getName();
			System.out.println("Filepath: " + this.classReferences.get(i).getFilePath() + "   " + str + "  ClassIntMap position: " + this.classIntMap.get(this.classReferences.get(i)));
			str ="Class Reference: ";
		}	
		//System.out.println(str);
	}
	
	public void printUsedClasses() {
		String str ="Used Classes: ";
		for(int i=0 ; i<this.usedClasses.size(); i++){
			str = str + this.usedClasses.get(i).getName();
			System.out.println("Filepath: " + this.usedClasses.get(i).getFilePath() + "   " + str);
			str ="Used Classes: ";
		}	
		//System.out.println(str);
	}

	public boolean isAllreadyContainingFieldReference(String fieldOrMethodName) {
		for(int i=0; i<this.fieldReferences.size();i++){
			if(this.fieldReferences.get(i).getName().equals(fieldOrMethodName)){
				return true;
			}
		}
		return false;
	}

	public Field getFieldFromFieldRef(String fieldOrMethodName) {
		for(int i=0; i< this.fieldReferences.size();i++){
			if(this.fieldReferences.get(i).getName().equals(fieldOrMethodName)){
				return this.fieldReferences.get(i);
			}
		}
		return null;
	}
	
	public Class getUsedClassByName(String className) {
		for(int i=0; i< this.usedClasses.size(); i++){
			if(this.usedClasses.get(i).getName().equals(className)){
				return this.usedClasses.get(i);
			}
		}
		return null;
	}
	
	public boolean isNumberMappedToMethod(int num){
		//for(int i = 0; i < this)
		return this.methodIntMap.containsValue(num);
	}
	
	public boolean isNumberMappedToClass(int num){
		return this.classIntMap.containsValue(num);
	}

	public boolean isNumberMappedToField(int num){
		return this.fieldIntMap.containsValue(num);
	}
	
	public boolean isNumberMappedToString(int num){
		return this.stringIntMap.containsValue(num);
	}
	
	public boolean isNumberMapped(int num){
		return this.isNumberMappedToClass(num) || this.isNumberMappedToMethod(num) || this.isNumberMappedToField(num) || this.isNumberMappedToString(num);
	}
	
	public String getStringdMappedToValue(int num){
		for(int i = 0; i < this.stringReferences.size(); i++){
			String s = this.stringReferences.get(i);
			int value = this.stringIntMap.get(s);
			if(value == num){
				return s;
			}
		}
		return null;
	}
	
	public Method getMethodMappedToValue(int num){
		for(int i = 0; i < this.methodsRef.size(); i++){
			Method m = this.methodsRef.get(i);
			int value = this.methodIntMap.get(m);
			if(value == num){
				return m;
			}
		}
		return null;
	}
	
	public Class getClassMappedToValue(int num){
		for(int i = 0; i < this.classReferences.size(); i++){
			Class c = this.classReferences.get(i);
			int value = this.classIntMap.get(c);
			if(value == num){
				return c;
			}
		}
		
		for(int i = 0; i < this.staticClassReferences.size(); i++){
			Class c = this.staticClassReferences.get(i);
			int value = this.classIntMap.get(c);
			if(value == num){
				return c;
			}
		}
		return null;
	}

	public Field getFieldMappedToValue(int num){
		for(int i = 0; i < this.fieldReferences.size(); i++){
			Field f = this.fieldReferences.get(i);
			int value = this.fieldIntMap.get(f);
			if(value == num){
				return f;
			}
		}
		return null;
	}
	
	public boolean isAllreadyContainingMethodRef(Method m1){
		boolean isEqual = false;
		for(int i=0; i<this.methodsRef.size();i++){
			Method m = this.methodsRef.get(i);
			if((m.getName().equals(m1.getName())) 
					&& (m.getParameterList().getSize() == m1.getParameterList().getSize())){
				isEqual = this.areParmeterListSame(m.getParameterList(), m1.getParameterList());
				if(isEqual){
					return true;
				}
			}
			isEqual = false;
		}
	
		return isEqual;
	}
	
	private boolean areSameTypes(Type t1, Type t2) {
		if(t1.getType() == t2.getType()){
			if(t1.isArray()){
				if(t1.getBaseType().getType() == t2.getBaseType().getType()){
					return true;
				}else{
					return false;
				}
			}else{
				return true;
			}
		}else{
			return false;
		}
	}
	
	public void printMethodReferences() {
		String str ="Method Refence Name: ";
		for(int i=0 ; i<this.methodsRef.size(); i++){
			str = str + this.methodsRef.get(i).getName();
			System.out.println(str);
			str ="Method Refence Name: ";
		}	
		//System.out.println(str);
	}
	
	public boolean isAllreadyContainingMethodInMethodsToBeChecked(Method m1){
		boolean isEqual = false;
		for(int i=0; i<this.methodsToBeCheckedIfExists.size();i++){
			Method m = this.methodsToBeCheckedIfExists.get(i);
			if((m.getName().equals(m1.getName())) 
					&& (m.getParameterList().getSize() == m1.getParameterList().getSize())){
				isEqual = this.areParmeterListSame(m.getParameterList(), m1.getParameterList());
				if(isEqual){
					this.methodsToBeCheckedIfExists.remove(i);
					return true;
				}
			}
			isEqual = false;
		}
	
		return isEqual;
	}
	
	/*public String getStringFromStrinArrayListByName(String stri) {
		for(int i=0 ; i<this.stringReferences.size(); i++){
			if(this.stringReferences.get(i).equals(anObject))
		}	
		return "";
	}*/
	
}
