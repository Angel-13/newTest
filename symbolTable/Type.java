package symbolTable;

import tokens.*;

public class Type
{
	
	private final Tokens tokens;

	public final int INT;

	public final int CHAR;

	public final int BOOLEAN;

	public final int VOID;
	
	public final int CLASS;
	
	public final int ARRAY;

	public final int STRING;
	
	public final int NULL;

	public final int BYTE;


	private final int type;

	private final Class clazz;

	private final Type arrayType;

	public final int LONG;

	public final int FLOAT;

	public final int DOUBLE;

	public final int SHORT;

	
	public Type()
	{
		this.INT = 0;
		this.CHAR = 1;
		this.BOOLEAN = 2;
		this.VOID = 4;
		this.CLASS = 5;
		this.ARRAY = 6;
		this.STRING = 7;
		this.NULL = 8;
		this.LONG = 9;
		this.BYTE = 10;
		this.FLOAT = 11;
		this.DOUBLE = 12;
		this.SHORT = 13;

		this.tokens = new Tokens();
		this.type = this.NULL;
		this.clazz = null;
		this.arrayType = null;

		return;
	}

	public Type(Type arrayBaseType)
	{
		this.INT = 0;
		this.CHAR = 1;
		this.BOOLEAN = 2;
		this.VOID = 4;
		this.CLASS = 5;
		this.ARRAY = 6;
		this.STRING = 7;
		this.NULL = 8;
		this.LONG = 9;
		this.BYTE = 10;
		this.FLOAT = 11;
		this.DOUBLE = 12;
		this.SHORT = 13;

		this.tokens = new Tokens();
		this.type = this.ARRAY;
		this.arrayType = arrayBaseType;
		this.clazz = null;
		return;
	}
	
	public Type(int t)
	{
		this.INT = 0;
		this.CHAR = 1;
		this.BOOLEAN = 2;
		this.VOID = 4;
		this.CLASS = 5;
		this.ARRAY = 6;
		this.STRING = 7;
		this.NULL = 8;
		this.LONG = 9;
		this.BYTE = 10;
		this.FLOAT = 11;
		this.DOUBLE = 12;
		this.SHORT = 13;


		this.tokens = new Tokens();
		this.type = t;
		this.clazz = null;
		this.arrayType = null;
		return;
	}

	public Type(Class clazz)
	{
		this.INT = 0;
		this.CHAR = 1;
		this.BOOLEAN = 2;
		this.VOID = 4;
		this.CLASS = 5;
		this.ARRAY = 6;
		this.STRING = 7;
		this.NULL = 8;
		this.LONG = 9;
		this.BYTE = 10;
		this.FLOAT = 11;
		this.DOUBLE = 12;
		this.SHORT = 13;


		this.tokens = new Tokens();
		this.type = this.CLASS;
		this.clazz = clazz;
		this.arrayType = null;
		return;
	}
	
	public Type(Token t)
	{
		this.INT = 0;
		this.CHAR = 1;
		this.BOOLEAN = 2;
		this.VOID = 4;
		this.CLASS = 5;
		this.ARRAY = 6;
		this.STRING = 7;
		this.NULL = 8;
		this.LONG = 9;
		this.BYTE = 10;
		this.FLOAT = 11;
		this.DOUBLE = 12;
		this.SHORT = 13;

		//System.out.println(t.getText());
		this.tokens = new Tokens();
//TODO make a real class object not only by name
		if (t.getToken() == this.tokens.BOOLEAN)
			this.type = this.BOOLEAN;
		else if (t.getToken() == this.tokens.CHAR)
			this.type = this.CHAR;
		else if (t.getToken() == this.tokens.INT)
			this.type = this.INT;
		else if (t.getToken() == this.tokens.NUMBER)
			this.type = this.INT;
		else if (t.getToken() == this.tokens.NULL)
			this.type = this.NULL;
		else if(t.getToken() == this.tokens.IDENTIFIER){
				this.type = this.CLASS;
		}else if(t.getToken() == this.tokens.VOID){
			this.type = this.VOID;
		}else if(t.getToken() == this.tokens.STRING_LITERAL){
			this.type = this.STRING;
		}else{
			this.type = -1;
		}
		this.arrayType = null;
		
		if(this.isClass()){
			this.clazz = new Class(t.getText(), null, "");
		}else if(this.isString()){
			this.clazz = new Class("java/lang/String", new Class("java/lang/Object", null, "java/lang/"), "java/lang/String","java/lang/");	
		}else{
			this.clazz = null;
		}
		
		
		return;
	}

	public String toString()
	{
		String ret=" ";
		if (this.type == this.INT)
			ret = "INT";
		else if (this.type == this.CHAR)
			ret = "CHAR";
		else if (this.type == this.BOOLEAN)
			ret = "BOOLEAN";
		else if (this.type == this.VOID)
			ret = "VOID";
		else if (this.type == this.CLASS)
			ret = this.clazz.getName();
			//ret = this.clazz.toString();
		else if (this.type == this.ARRAY)
			ret = this.arrayType.toString() + "[]";
		else if (this.type == this.NULL)
			ret = "NULL";
		else if (this.type == this.BYTE)
			ret = "BYTE";
		else if (this.type == this.LONG)
			ret = "LONG";
		else if (this.type == this.STRING)
			ret = "STRING";
		return ret;
	}

	public String toInternalTypeName()
	{
		String internalForm = "";
		if (this.type == this.INT)
			internalForm = "I";
		if (this.type == this.CHAR)
			internalForm = "C";
		if (this.type == this.BOOLEAN)
			internalForm = "Z";
		if (this.type == this.VOID)
			internalForm = "V";
		if (this.type == this.CLASS)
		{
			Class clazz = this.clazz;
		}
		if (this.type == this.ARRAY)
		{
			Type arrayType = this.arrayType;
			internalForm = "[" + arrayType.toInternalTypeName();
		}
		return internalForm;
	}
	public Class getClazz()
	{
		return this.clazz;
	}

	public boolean isInteger()
	{
		return this.type == this.INT;
	}

	public boolean isChar()
	{
		return this.type == this.CHAR;
	}

	public boolean isBoolean()
	{
		return this.type == this.BOOLEAN;
	}

	/*public boolean isString()
	{
		if (this.type == this.STRING)
			return true;
		if (!this.isClass())
			return false;
		String string = this.getClazz().toString();
		return string.equals("java.lang.String");
	}*/


	public boolean isClass()
	{
		return this.type == this.CLASS;
	}
	
	public boolean isString()
	{
		return this.type == this.STRING;
	}

 
	public boolean isNull()
	{
		return this.type == this.NULL;
	}
 
	/*public int getPrimitiveArrayTypeNumber()
	{
		if (this.type == this.INT)
			return 10;
		else if (this.type == this.BOOLEAN)
			return 4;
		else if (this.type == this.CHAR)
			return 5;
	}*/

 
	public boolean isArray()
	{
		return this.type == this.ARRAY;
	}
	public Type getBaseType()
	{
		return this.arrayType;
	}

	public int getArrayDimension()
	{
		if (this.getBaseType().isArray())
			return this.getBaseType().getArrayDimension() + 1;
		return 1;
	}
 
	public boolean isVoid()
	{
		return this.type == this.VOID;
	}

	public String getDescriptor()
	{
		if (this.isInteger())
			return "I";
		if (this.isBoolean())
			return "Z";
		if (this.isChar())
			return "C";
		if (this.isVoid())
			return "V";
		if (this.isClass())
			return "L" + this.getClazz().getName()+ ";";
		if (this.isArray()){
			return "[" + this.getBaseType().getDescriptor();
		}
		if (this.isString()){
			return "L" + this.getClazz().getName()+ ";";
		}
		return " ";
	}
	
	public int getType(){
		return this.type;
	}
}
