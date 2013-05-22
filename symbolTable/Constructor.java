package symbolTable;

import milestone2.Parser;

public class Constructor {
	
	private final ParameterList pList; 
	
	private final FieldArrayList localVariables;
	
	private final String name;
	
	private final boolean isPrivate;
	
	private final Class clazz;
	
	private final Parser parser;
	
	public Constructor(Parser parser, String name, ParameterList pList, Class clazz, boolean isPrivate){
		this.parser = parser;
		this.isPrivate = isPrivate;
		this.pList = pList;
		this.name = name;
		this.clazz = clazz;
		this.localVariables = new FieldArrayList();
	}
	
	public ParameterList getParameterList(){
		return this.pList;
		
	}
	
	public FieldArrayList getLocalVariables(){
		return this.localVariables;
	}
	
	public void addParameter(Field field){
		this.pList.addParameter(field);
	}
	
	public void addLocalVariable(Field field){
		this.localVariables.add(field);
	}
	
	public String getName(){
		return this.name;
	}
	
	public Class getClazz(){
		return this.clazz;
	}
	
	public boolean isPrivate(){
		return this.isPrivate;
	}
	
	
	public Parser getParser(){
		return this.parser;
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
	
	public void printVariables(){
		for(int i=0; i<this.pList.getSize(); i++){
			System.out.print(this.pList.getParameter(i).getName()+", ");
		}
		for(int i=0; i<this.localVariables.size(); i++){
			System.out.print(this.localVariables.get(i).getName()+", ");
		}
	}
	
	public int getVariableSize(){
		return this.pList.getSize()+this.localVariables.size();
	}
}
