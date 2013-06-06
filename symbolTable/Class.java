package symbolTable;

import mapsTable.ClassIntMap;
import mapsTable.FieldIntMap;


public class Class {
	
	private final MethodArrayList methods;
	
	private final FieldArrayList fields;
	
	private final ClassArrayList importedClasses;
	
	private final ClassArrayList usedClasses;
	
	private final ConstructorArrayList constructorList;
	
	private final String name;
	
	private final Class superClass;
	
	private final String packageName;
	
	private final String filePath;
	
	private final ClassArrayList classReferences;
	
	private final FieldArrayList fieldReferences;
	
	private final ClassIntMap classIntMap;
	
	private final FieldIntMap fieldIntMap;
	
	public int counter;
	
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
		this.addInitMethod();
	}


/**************************************************************************************
 *  addInitMethod()
 * 		-
 *************************************************************************************/
	private void addInitMethod() {
		Method m = new Method(null, "<init>", new Type(4), new ParameterList(), this.superClass, false, false);
		this.counter++;
		this.methods.add(m);
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
	
	public FieldArrayList getFields(){
		return this.fields;
	}
	
	public Class getSuperClass(){
		return this.superClass;
	}
	
	public ClassArrayList getImportedArrayClasses(){
		return this.importedClasses;
	}
	
	public ClassArrayList getUsedClasses(){
		return this.usedClasses;
	}
	
	public ClassArrayList getClassReferences(){
		return this.classReferences;
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
	
	public void addCassReference(Class classReference){
		this.classReferences.add(classReference);
		this.addClassInClassIntMap(classReference);
	}
	
	public void addFieldReference(Field fielReference){
		this.fieldReferences.add(fielReference);
		this.addFieldInFieldIntMap(fielReference);
	}
	
	private void addClassInClassIntMap(Class classReference) {
		this.classIntMap.put(classReference, counter);
		this.counter = this.counter + 2;
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
	
	public void addClassField(Field field){
		this.fields.add(field);
		//this.classFile.getConstatnPool().addField(field);
	}

	public boolean isAllreadyContainingMethod(Method m1){
		boolean isEqual = false;
		for(int i=0; i<this.methods.size();i++){
			isEqual = true;
			Method m = this.methods.get(i);
			if((m.getName().equals(m1.getName())) 
					&& (m.getParameterList().getSize() == m1.getParameterList().getSize())){
				for(int j=0; j<m.getParameterList().getSize();j++){
					if(m.getParameterList().getParameter(j).getType().getType() == m1.getParameterList().getParameter(j).getType().getType()){
						isEqual = isEqual && true;
					}else{
						isEqual = isEqual && false;
					}
				}
				if(isEqual){
					return true;
				}
				
			}else{
				isEqual = false;
			}
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
	
	public Field getFieldFromClassFieldsByName(String name){
		for(int i=0; i< this.fields.size();i++){
			if(this.fields.get(i).getName().equals(name)){
				return this.fields.get(i);
			}
		}
		return null;
	}
	
	public Method getMethoddFromClassMethodsByName(String name){
		for(int i=0; i<this.methods.size();i++){
			Method m = this.methods.get(i);
			if(m.getName().equals(name)){
				return m;
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
}
