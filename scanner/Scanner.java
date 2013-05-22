/*****************************************************************************
* @author: Angel Mirkovski m/n:0422200
* @author: Fabian Winkler  m/n:0821409
*
* This class makes the token with the help of the LookForwardReader object and the Tokens object. 
* Whitespaces will be ignored. It has two parameters:
*	- reader - LookForwardReader, who gives the characters to the scanner and 
*	- tokens - who will be used so that the scanner makes the tokens.
*****************************************************************************/
package scanner;

import java.io.IOException;

import tokens.*;

public class Scanner {
	
	private final LookForwardReader reader;
	
	private final Tokens tokens;

	public Scanner(LookForwardReader reader)
	{
		this.reader = reader;
		this.tokens = new Tokens();
	}
//getNextToken() - returns the next token by moving the reader to the next position(whitespaces will be baypassed)
// if the next token is identifier, the reader reads the characters until whitespace
	public Token getNextToken()
	{
		
		this.readWhitespaces();

		if (!this.reader.hasNext())
			return new Token(tokens.EOF, "EOF", new Position(this.reader.getPosition().raw, this.reader.getPosition().column));

		char c = this.reader.readNext();

		if (c == ')')
			return new Token(tokens.ROUND_BRACKET_CLOSE, ")", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		if (c == '(')
			return new Token(tokens.ROUND_BRACKET_OPEN, "(", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		if (c == ']')
			return new Token(tokens.SQUARE_BRACKET_CLOSE, "]", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		if (c == '[')
			return new Token(tokens.SQUARE_BRACKET_OPEN, "[", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		if (c == '{')
			return new Token(tokens.CURLY_BRACKET_OPEN, "{", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		if (c == '}')
			return new Token(tokens.CURLY_BRACKET_CLOSE, "}", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		if (c == ',')
			return new Token(tokens.COMMA, ",", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));

		if (c == '@')
		{
			if (this.reader.hasNext())
				if (this.reader.lookAhead() == 'M')
				{
					this.reader.readNext();
					return new Token(tokens.METHOD_ANNOTATION, "@M", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 2));
				}else if (this.reader.lookAhead() == 'F')
				{
					this.reader.readNext();
					return new Token(tokens.FIELD_ANNOTATION, "@F", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 2));
				}else if (this.reader.lookAhead() == 'C')
				{
					this.reader.readNext();
					return new Token(tokens.CONSTRUCTOR_ANNOTATION, "@C", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 2));
				}
			//return new Token(tokens.NOT);
		}
		
		if (c == '!')
		{
			if (this.reader.hasNext())
				if (this.reader.lookAhead() == '=')
				{
					
					this.reader.readNext();
					return new Token(tokens.UNEQUAL, "!=", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 2));
				}
			return new Token(tokens.NOT, "not-operator", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		}
		if (c == '*')
			return new Token(tokens.MULT, "*", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		if (c == '/')
		{
			
			if (this.lookAhead('*'))
			{
				this.readLongComment();
				return this.getNextToken();
			}
			
			if (this.lookAhead('/'))
			{
				this.readShortComment();
				return this.getNextToken();
			}
			return new Token(tokens.DIV, "/", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		}
		if (c == '+')
		{
			if (this.lookAhead('+'))
				return new Token(tokens.INC, "++", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 2));
			return new Token(tokens.ADD, "+", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		}
		if (c == '-')
		{
			if (this.lookAhead('-'))
				return new Token(tokens.DEC, "--", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 2));
			return new Token(tokens.SUB, "-", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		}

		if (c == '<')
		{
			if (this.reader.hasNext())
				if (this.reader.lookAhead() == '=')
				{
					this.reader.readNext();
					return new Token(tokens.LESS_EQUAL, "<=", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 2));
				}
			return new Token(tokens.LESS, "<", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		}
		if (c == '=')
		{
			if (this.reader.hasNext())
				if (this.reader.lookAhead() == '=')
				{
					this.reader.readNext();
					return new Token(tokens.EQUAL, "==", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 2));
				}
			return new Token(tokens.ASSIGNMENT, "=", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		}
		if (c == '>')
		{
			if (this.reader.hasNext())
				if (this.reader.lookAhead() == '=')
				{
					this.reader.readNext();
					return new Token(tokens.GREATER_EQUAL, ">=", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 2));
				}
			return new Token(tokens.GREATER, ">", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		}
		if (c == '.')
			return new Token(tokens.DOT, ".", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		if (c == ';')
			return new Token(tokens.SEMICOLON, ";", new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		if (c == '&')
		{
			if (this.reader.hasNext())
				if (this.reader.lookAhead() == '&')
				{
					this.reader.readNext();
					return new Token(tokens.AND, new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 2));
				}
			return new Token(tokens.AND, new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		}
		if (c == '"')
		{
			return this.readStringLiteral();
		}
		if (c == '|')
		{
			if (this.reader.hasNext())
				if (this.reader.lookAhead() == '|')
				{
					this.reader.readNext();
					return new Token(tokens.OR, new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 2));
				}
			return new Token(tokens.OR, new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
		}
		if (c == '\'')
		{
			return this.readCharacterLiteral();
		}

		Character character = new Character(c);

		if (character.isDigit(c))
			return this.readNumber(c);

		if (character.isLetter(c))
			return this.readIdentifier(c);
		
		return new Token(tokens.UNKNOWN, character.toString(), new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
	}

//makeToken(int token) - makes new token from a number, specified for the token, and return it
	private Token makeToken(int token)
	{
		return new Token(token, new Position(this.reader.getPosition().raw, this.reader.getPosition().column));
	}

//readWhitespaces() - ignores(baypasses) the whitespaces by moving the reader to the next character 
	private void readWhitespaces()
	{
		boolean weiter = true;
		LookForwardReader reader = this.reader;
		while (reader.hasNext() & weiter)
		{
			if (reader.lookAhead() == ' ' | reader.lookAhead() == '\r'
					| reader.lookAhead() == '\n' | reader.lookAhead() == '\t')
				reader.readNext();
			else
				weiter = false;
		}
	}
//readShortComment() - ignores(baypasses) the short comments(comments like //)
	private void readShortComment()
	{
		boolean weiter = true;
		LookForwardReader reader = this.reader;
		while (reader.hasNext() & weiter)
			if (reader.readNext() == '\n')
				weiter = false;
		return;
	}
//readStringLiteral() - returns String Token with the actuall text inside the quotation marks
	private Token readStringLiteral()
	{
		StringBuilder sb = new StringBuilder();
		LookForwardReader reader = this.reader;
		boolean fertig = false;
		while (reader.hasNext() & !fertig)
			if (reader.lookAhead() != '\"')
				sb.append(this.readEscapedCharacter());
			else
				fertig = true;
		if (reader.readNext() == '\"')
			return this.makeLiteralToken(sb.toString());
		return this.makeFehlerToken("Erwarte dass String vor dem Ende der Datei zu Ende ist.");
	}
//readEscapedCharacter() - returns the escaped character(\n or \t or \' or \")
	private char readEscapedCharacter()
	{
		LookForwardReader reader = this.reader;
		char c = reader.readNext();
		if (c == '\\')
		{
			char escapedC = reader.readNext();
			if (escapedC == 'n')
				c = '\n';
			else if (escapedC == 't')
				c = '\t';
			else if (escapedC == '\'')
				c = '\'';
			else if (escapedC == '"')
				c = '\"';
			else if(escapedC == '\\')
				c='\\';
			else
			{
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("Kenne kein Escape für \'");
				stringBuilder.append(escapedC);
				stringBuilder.append("\'.");
				throw new RuntimeException(new IOException(
						stringBuilder.toString()));
			}
		}
		return c;
	}

//makeLiteralToken(String stringLiteral) - make the token and the text from the string inside the quotation marks
	private Token makeLiteralToken(String stringLiteral)
	{
		LookForwardReader reader = this.reader;
		return new Token(this.tokens.STRING_LITERAL,
				stringLiteral, new Position(this.reader.getPosition().raw, this.reader.getPosition().column - stringLiteral.length()));
	}
//makeFehlerToken(String fehlerText) - it makes unknown token if the string doesn't exists as known token and the string as text
	private Token makeFehlerToken(String fehlerText)
	{
		Character c = new Character(this.reader.getAkChar());
		return new Token(this.tokens.UNKNOWN,
				c.toString(), new Position(this.reader.getPosition().raw, this.reader.getPosition().column - fehlerText.length()));
	}
//readCharacterLiteral() - return token character with the character itself as text
	private Token readCharacterLiteral()
	{
		LookForwardReader reader = this.reader;
		char c = reader.readNext();
		if (c == '\\')
		{
			
			char escapedC = reader.readNext();
			if (escapedC == 'n')
				c = '\n';
			else if (escapedC == 'r')
				c = '\r';
			else if (escapedC == 't')
				c = '\t';
			else if (escapedC == '"')
				c = '"';
			else if (escapedC == '\'')
				c = '\'';
		}
		if (this.lookAhead('\''))
		{	
			if (c == '\n')
				return new Token(this.tokens.CHARACTER_LITERAL, "newline");
			else if (c == '\r')
				return new Token(this.tokens.CHARACTER_LITERAL, "return");
			else if (c == '\t')
				return new Token(this.tokens.CHARACTER_LITERAL, "tab");
			else if (c == '"'){
				Character character = new Character(c);
				return new Token(this.tokens.CHARACTER_LITERAL, character.toString());
			}else if (c == '\''){
				Character character = new Character(c);
				return new Token(this.tokens.CHARACTER_LITERAL, character.toString());
			}
			Character character = new Character(c);
			return new Token(this.tokens.CHARACTER_LITERAL, character.toString());
		}
		if (c == '\'')
		{	
			return new Token(this.tokens.UNKNOWN, "Unknown Token");
		}
		boolean fertig = false;
		while (reader.hasNext() & !fertig)
		{
			if (reader.readNext() == '\'')
				fertig = true;
		}

		Character character = new Character(c);
		return new Token(this.tokens.UNKNOWN,
				character.toString(), new Position(this.reader.getPosition().raw, this.reader.getPosition().column - 1));
	}

//readLongComment() - ignores(baypasses) the long comments(comments like /*    */)
	private boolean readLongComment()
	{
		LookForwardReader reader = this.reader;
		while (reader.hasNext())
		{
			char c = reader.readNext();
			if (c == '*')
				if (this.lookAhead('/'))
					return true;
		}
		return false;
	}
//lookAhead(char c) - returns true if next character has been read and read the move the reader to the next character, false otherwise
	private boolean lookAhead(char c)
	{
		LookForwardReader reader = this.reader;
		char gelesen = reader.lookAhead();
		if (gelesen == c)
		{
			reader.readNext();
			return true;
		}
		return false;
	}
//readNumber(char c) - return number token with the actuall number as string
	private Token readNumber(char c)
	{
		StringBuilder sb = new StringBuilder();
		LookForwardReader reader = this.reader;
		Character character = new Character('a');
		sb.append(c);

		boolean weiter = true;
		while (reader.hasNext() & weiter)
		{
			if (character.isLetterOrDigit(reader.lookAhead()))
				sb.append(reader.readNext());
			else
				weiter = false;
		}
		int integer = 0;
		try
		{
			Integer i = new Integer(1);
			integer = i.parseInt(sb.toString());
		}
		catch (NumberFormatException e)
		{
			return new Token(this.tokens.UNKNOWN,
					sb.toString(), new Position(this.reader.getPosition().raw, this.reader.getPosition().column - sb.toString().length()));
		}
		return new Token(this.tokens.NUMBER, sb.toString(), new Position(this.reader.getPosition().raw, this.reader.getPosition().column - sb.toString().length()));
	}
//readIdentifier(char c) - return identifier token with its name as string	
	private Token readIdentifier(char c)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(c);
		
		boolean weiter = true;

		LookForwardReader reader = this.reader;
		while (reader.hasNext() & weiter)
		{
			Character character = new Character('?');
			if (character.isLetterOrDigit(reader.lookAhead())
					| reader.lookAhead() == '_')
				sb.append(reader.readNext());
			else
				weiter = false;
		}
		String identifier = sb.toString();
		
		
		return new Token(this.tokens.identifier2Token(identifier), identifier, new Position(this.reader.getPosition().raw, this.reader.getPosition().column - identifier.length()));
	}
//getReader() - return LookForwardReader 
	public LookForwardReader getReader(){
		return this.reader;
	}
}
