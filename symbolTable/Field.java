package symbolTable;

import tokens.Token;



public class Field {
	
	private final Type type;
	
	private final String name;
	
	private final String clas;
	
	private final Class clazz;
	
	private final boolean isStatic;
	
	private final boolean isPrivate;
	
	private final boolean isFinal;
	
	private final Token token;
	
	private int size;
	
	private int value;
	
	public Field(Type type, String name, Class clazz, Token t){
		this.name = name;
		this.type = type;
		this.clas = null;
		this.clazz = clazz;
		this.isStatic = false;
		this.isPrivate = false;
		this.isFinal = false;
		this.token = t;
		this.size = 0;
		this.value = 0;
	}
	
	public Field(Type type, String name, Class clazz, boolean isStatic, boolean isPrivate, boolean isFinal, Token t){
		this.name = name;
		this.type = type;
		this.clas = null;
		this.clazz = clazz;
		this.isStatic = isStatic;
		this.isPrivate = isPrivate;
		this.isFinal = isFinal;
		this.token = t;
		this.size = 0;
		this.value = 0;
	}

	public String getName() {
		return this.name;
	}

	public Type getType() {
		return this.type;
	}
	
	public String getStringClass() {
		return this.clas;
	}
	
	public Class getClazz() {
		return this.clazz;
	}
	
	public boolean isStatic() {
		return this.isStatic;
	}
	
	public boolean isPrivate() {
		return this.isPrivate;
	}
	
	public boolean isFinal() {
		return this.isFinal;
	}
	
	public Token getToken(){
		return this.token;
	}
	
	public int getSize(){
		return this.size;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public void setSize(int size){
		this.size = size;
	}
	
	public void setValue(int i){
		this.value = i;
	}
}
