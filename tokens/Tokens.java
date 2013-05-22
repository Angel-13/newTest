/********************************************************************************
* @author: Angel Mirkovski m/n:0422200
* @author: Fabian Winkler  m/n:0821409
*
* This class defines all the tokens as a numbers, that our compiler recognize.
* Every token has a uniqe number, 
* which is will be define at the creation of this object and cannot be changed.
********************************************************************************/
package tokens;

public class Tokens
{
	
	public final int IDENTIFIER;
	
	public final int ROUND_BRACKET_OPEN;
	
	public final int ROUND_BRACKET_CLOSE;
	
	public final int EOF;
	
	public final int UNKNOWN;
	
	public final int CURLY_BRACKET_OPEN;
	
	public final int CURLY_BRACKET_CLOSE;
	
	public final int BOOLEAN;
	
	public final int INT;
	
	public final int CHAR;
	
	public final int PUBLIC;
	
	public final int PRIVATE;
	
	public final int FINAL;
	
	public final int NOT;
	
	public final int MULT;
	
	public final int DIV;
	
	public final int ADD;
	
	public final int SUB;
	
	public final int LESS;
	
	public final int LESS_EQUAL;
	
	public final int EQUAL;
	
	public final int GREATER_EQUAL;
	
	public final int GREATER;
	
	public final int UNEQUAL;
	
	public final int RETURN;
	
	public final int IF;
	
	public final int CLASS;
	
	public final int TRUE;
	
	public final int FALSE;
	
	public final int ASSIGNMENT;
	
	public final int NUMBER;

	public final int DOT;
	
	public final int SEMICOLON;
	
	public final int AND;
	
	public final int COMMA;
	
	public final int CHARACTER_LITERAL;
	
	public final int OR;
	
	public final int STRING_LITERAL;
	
	public final int STATIC;
	
	public final int VOID;

	public final int SQUARE_BRACKET_OPEN;
	
	public final int SQUARE_BRACKET_CLOSE;
	
	public final int NEW;
	
	public final int INC;
	
	public final int DEC;
	
	public final int NEGATE;
	
	public final int WHILE;
	
	public final int FOR;
	
	public final int ELSE;
	
	public final int NULL;
	
	public final int BYTE;
	
	public final int FIELD_ANNOTATION;
	
	public final int CONSTRUCTOR_ANNOTATION;

	public final int METHOD_ANNOTATION;
	
	public final int IMPORT;
	
	public final int PACKAGE;
	
	//public final int STRING;


	public Tokens()
	{
		this.IDENTIFIER = 0;
		this.ROUND_BRACKET_OPEN = 1;
		this.ROUND_BRACKET_CLOSE = 2;
		this.PACKAGE = 3;
		this.EOF = 4;
		this.UNKNOWN = 5;
		this.CURLY_BRACKET_OPEN = 6;
		this.CURLY_BRACKET_CLOSE = 7;
		this.BOOLEAN = 8;
		this.INT = 9;
		this.CHAR = 10;
		this.PUBLIC = 11;
		this.PRIVATE = 12;
		this.FINAL = 13;
		this.NOT = 14;
		this.MULT = 15;
		this.DIV = 16;
		this.ADD = 17;
		this.SUB = 18;
		this.LESS = 19;
		this.LESS_EQUAL = 20;
		this.EQUAL = 21;
		this.GREATER_EQUAL = 22;
		this.GREATER = 23;
		this.UNEQUAL = 24;
		this.RETURN = 26;
		this.IF = 28;
		this.IMPORT = 29;
		this.CLASS = 31;
		this.TRUE = 32;
		this.FALSE = 33;
		this.ASSIGNMENT = 34;
		this.NUMBER = 35;
		this.DOT = 36;
		this.SEMICOLON = 37;
		this.AND = 38;
		this.COMMA = 39;
		this.CHARACTER_LITERAL = 40;
		this.OR = 41;
		this.STRING_LITERAL = 42;
		this.STATIC = 43;
		this.VOID = 44;
		this.SQUARE_BRACKET_OPEN = 45;
		this.SQUARE_BRACKET_CLOSE = 46;
		this.NEW = 47;
		this.INC = 48;
		this.DEC = 480;
		this.WHILE = 49;
		this.FOR = 50;
		this.ELSE = 53;
		this.NULL = 58;
		this.BYTE = 59;
		this.FIELD_ANNOTATION = 50;
		this.CONSTRUCTOR_ANNOTATION = 51;
		this.METHOD_ANNOTATION = 52;
		this.NEGATE = 88;
		//this.STRING = 53;
	}
//identifier2Token(String identifier) - returns the number of the token given as string 
	public int identifier2Token(String identifier)
	{
		if (identifier.equals("boolean"))
			return this.BOOLEAN;
		if (identifier.equals("int"))
			return this.INT;
		if (identifier.equals("char"))
			return this.CHAR;
		if (identifier.equals("public"))
			return this.PUBLIC;
		if (identifier.equals("private"))
			return this.PRIVATE;
		if (identifier.equals("final"))
			return this.FINAL;
		if (identifier.equals("return"))
			return this.RETURN;
		if (identifier.equals("if"))
			return this.IF;
		if (identifier.equals("class"))
			return this.CLASS;
		if (identifier.equals("true"))
			return this.TRUE;
		if (identifier.equals("false"))
			return this.FALSE;
		if (identifier.equals("new"))
			return this.NEW;
		if (identifier.equals("while"))
			return this.WHILE;
		if (identifier.equals("static"))
			return this.STATIC;
		if (identifier.equals("void"))
			return this.VOID;
		if (identifier.equals("else"))
			return this.ELSE;
		if (identifier.equals("null"))
			return this.NULL;
		if (identifier.equals("byte"))
			return this.BYTE;
		if (identifier.equals("for"))
			return this.FOR;
		if (identifier.equals("import"))
			return this.IMPORT;
		if(identifier.equals("package"))
			return this.PACKAGE;
		return this.IDENTIFIER;
	}

//tokenInt2String(int token) - returns the string(text) of the token given as number 
	public String tokenInt2String(int token)
	{
		return "\"" + this.tokenInt2StringPrivate(token) + "\"";
	}
//tokenInt2StringPrivate(int token) - makes(changes) from(the) token given as number (to) a string(name of the token) 
	private String tokenInt2StringPrivate(int token)
	{
		if (token == this.AND)
			return "AND-OPERATOR";
		if (token == this.IDENTIFIER)
			return "IDENTIFIER";
		if (token == this.ROUND_BRACKET_OPEN)
			return "(";
		if (token == this.ROUND_BRACKET_CLOSE)
			return ")";
		if (token == this.EOF)
			return "EOF";
		if (token == this.UNKNOWN)
			return "Unknown Token";
		if (token == this.CURLY_BRACKET_OPEN)
			return "{";
		if (token == this.CURLY_BRACKET_CLOSE)
			return "}";
		if (token == this.BOOLEAN)
			return "BOOLEAN";
		if (token == this.INT)
			return "INT";
		if (token == this.CHAR)
			return "CHAR";
		if (token == this.PUBLIC)
			return "PUBLIC";
		if (token == this.PRIVATE)
			return "PRIVATE";
		if (token == this.FINAL)
			return "FINAL";
		if (token == this.NOT)
			return "NOT-OPERATOR";
		if (token == this.MULT)
			return "MULTIPLY OPERATOR";
		if (token == this.DIV)
			return "DIVIDE OPERATOR";
		if (token == this.ADD)
			return "ADDITION OPERATOR";
		if (token == this.SUB)
			return "SUBSTRACTION OPERATOR";
		if (token == this.LESS)
			return "LESS-OPERATOR";
		if (token == this.LESS_EQUAL)
			return "LESS_EQUAL-OPERATOR";
		if (token == this.EQUAL)
			return "EQUAL-OPERATOR";
		if (token == this.GREATER)
			return "GREATER-OPERATOR";
		if (token == this.GREATER_EQUAL)
			return "GREATER_EQUAL-OPERATOR";
		if (token == this.UNEQUAL)
			return "UNEQUAL-OPERATOR";
		if (token == this.RETURN)
			return "RETURN";
		if (token == this.FOR)
			return "FOR";
		if (token == this.IF)
			return "IF";
		if (token == this.INC)
			return "INCREMENT-OPERATOR";
		if (token == this.CLASS)
			return "CLASS";
		if (token == this.TRUE)
			return "TRUE";
		if (token == this.FALSE)
			return "FALSE";
		if (token == this.ASSIGNMENT)
			return "ASSIGNMENT OPERATOR";
		if (token == this.NUMBER)
			return "NUMBER";
		if (token == this.DOT)
			return "DOT";
		if (token == this.SEMICOLON)
			return "SEMICOLON";
		if (token == this.COMMA)
			return "COMMA";
		if (token == this.CHARACTER_LITERAL)
			return "CHARACTER_LITERAL";
		if (token == this.OR)
			return "OR";
		if (token == this.STRING_LITERAL)
			return "STRING_LITERAL";
		if (token == this.STATIC)
			return "STATIC";
		if (token == this.VOID)
			return "VOID";
		if (token == this.SQUARE_BRACKET_OPEN)
			return "[";
		if (token == this.SQUARE_BRACKET_CLOSE)
			return "]";
		if (token == this.NEW)
			return "NEW";
		if (token == this.DEC)
			return "DECREMENT-OPERATOR";
		if (token == this.WHILE)
			return "WHILE";
		if (token == this.ELSE)
			return "ELSE";
		if (token == this.NULL)
			return "NULL";
		if (token == this.FIELD_ANNOTATION)
			return "@F";
		if (token == this.CONSTRUCTOR_ANNOTATION)
			return "@C";
		if (token == this.METHOD_ANNOTATION)
			return "@M";
		if (token == this.IMPORT)
			return "IMPORT";
		if (token == this.NEGATE)
			return "NEGATION";
		return "unknown Token";
	}
}
