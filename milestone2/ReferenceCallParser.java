package milestone2;

import java.io.File;

import compileTable.ByteReader;

import scanner.LookForwardScanner;
import symbolTable.Field;
import symbolTable.Class;
import symbolTable.Method;
import symbolTable.TokenArrayList;
import tokens.Token;
import tokens.Tokens;

public class ReferenceCallParser {
	
	private Field f;
	
	private Field fieldFromClassRef;
	
	private boolean error;
	
	private ErrorsClass errors;
	
	private final LookForwardScanner lfc;
	
	private final Class clazz;
	
	private final Tokens tks;
//TODO To change ExpressionParser so that he would not need parser as parameter but LookForwardScanner instead
	private final Parser p; 
	
	public ReferenceCallParser(LookForwardScanner lfc, Class clazz, boolean error, Parser p){
		this.errors = new ErrorsClass();
		this.lfc = lfc;
		this.tks = new Tokens();
		this.clazz = clazz;
		this.error = error;
		this.p = p;
	}
/*******************************************************************************************
 *  boolean parseMethodOrFieldCall(Token type)) 
 *  		- 
 * @throws Exception 
 *******************************************************************************************/
	public boolean parseMethodOrFieldCall (Token name, Method m, BodyParser bParser) throws Exception{
		boolean b = true;
		boolean isContainigField = m.isContainingFildMethodAndClass(name.getText(), true);
		this.f = null;
		if(!isContainigField){
			this.error = true;
			this.errors.identifierDoesNotExistsError(name);
		}else{
			this.f = m.findFieldInsideMethoAndClass(name.getText());
		}
		Token fieldOrMethodName = this.expected(new Token(this.tks.IDENTIFIER,"Identifier"));
		if(fieldOrMethodName.getToken() != -1){
			if(!isContainigField){
				TokenArrayList tk = new TokenArrayList();
				tk.add(new Token(this.tks.ASSIGNMENT, "="));
				tk.add(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
				tk.add(new Token(this.tks.SQUARE_BRACKET_OPEN, "["));
				Token er = this.expected(tk);
				if(er.getToken() != -1){
					if(er.getToken() == this.tks.ASSIGNMENT){
						//TODO To parse Without generating the code because there is an error
						//this.parseAssignmentWhitoutCodeGeneration();
					}else{
						this.expected(new Token(this.tks.ASSIGNMENT, "="));
						this.lfc.readNextToken();
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
					}
				}else{
					b = false;
				}	
			}else{
				if(this.isNextToken(new Token(this.tks.ASSIGNMENT))){
					this.lfc.readNextToken();
					b = this.parseFieldCall(f, fieldOrMethodName, m, name, -1, bParser);
				}else if(this.isNextToken(new Token(this.tks.ROUND_BRACKET_OPEN))){
					//TODO IMPLEMENT METHOD CALLS
				}else if(this.isNextToken(new Token(this.tks.SQUARE_BRACKET_OPEN))){
					this.expected(new Token(this.tks.SQUARE_BRACKET_OPEN, "["));
					Token number = this.expected(new Token(this.tks.NUMBER, "Number"), new Token(this.tks.IDENTIFIER, "Identifier"));
					if(number.getToken() != -1){
						Token t = this.expected(new Token(this.tks.SQUARE_BRACKET_CLOSE, "]"));
						if(t.getToken() !=-1){
							t = this.expected(new Token(this.tks.ASSIGNMENT, "="));
							if(t.getToken() != -1){
								int num = Integer.parseInt(number.getText());
								//System.out.println(f.getName() + "   sdasdasd   " + number.getText());
								b = this.parseFieldCall(f, fieldOrMethodName, m, name, num, bParser);
							}else{
								this.lfc.readNextToken();
								this.expected(new Token(this.tks.SEMICOLON, ";"));
								b = false;
							}
						}else{
							this.lfc.readNextToken();
							this.expected(new Token(this.tks.SEMICOLON, ";"));
							b = false;
						}
					}else{
						this.lfc.readNextToken();
						this.expected(new Token(this.tks.SEMICOLON, ";"));
						b = false;
					}
				}else{
					Token error = this.lfc.readNextToken();
					TokenArrayList tk = new TokenArrayList();
					tk.add(new Token(this.tks.ASSIGNMENT, "="));
					tk.add(new Token(this.tks.ROUND_BRACKET_OPEN, "("));
					tk.add(new Token(this.tks.SQUARE_BRACKET_OPEN, "["));
					this.errors.printExpectedMoreTokensError(error, tk);
					
					this.error = true;
					b = false;
				}	
			}		
		}else{
			this.lfc.readNextToken();
			this.expected(new Token(this.tks.SEMICOLON, ";"));
			b = false;
		}
		return b;
	}
/*******************************************************************************************
 *  String getFilePath(Token type) 
 *  		- 
 * @throws Exception 
 *******************************************************************************************/
	private boolean parseFieldCall(Field f, Token fieldOrMethodName, Method m, Token name, int position, BodyParser bParser) throws Exception {
		boolean b = true;
		ExpressionParser ex = new ExpressionParser(this.p, bParser);
		if(this.clazz.isAllreadyContainingClassReference(f.getClazz().getName())){
			if(this.clazz.isAllreadyContainingFieldReference(fieldOrMethodName.getText())){
				this.fieldFromClassRef = this.clazz.getFieldFromFieldRef(fieldOrMethodName.getText());
			}else{
				
				String filePathClass = f.getClazz().getFilePath() + f.getClazz().getName() + ".class";
				String filePathJava = f.getClazz().getFilePath() + f.getClazz().getName() + ".java";
				//System.out.println(filePathClass);
				//System.out.println(filePathJava);
				if(this.checkFile(filePathClass)){
					//System.out.println(filePathClass);
					ByteReader breader = new ByteReader(filePathClass);
					this.fieldFromClassRef = breader.findField(fieldOrMethodName,f.getClazz());
				}else if(this.checkFile(filePathJava)){
					System.out.println(filePathJava);
					Parser pr = new Parser(filePathJava);
					System.out.println(this.checkFile(filePathClass));
					this.error = pr.getError();
					if(!this.error){
						ByteReader breader = new ByteReader(filePathClass);
						this.fieldFromClassRef = breader.findField(fieldOrMethodName,f.getClazz());
					}else{
						//TODO
						System.out.println(this.clazz.getName() + "  ERROR");
						this.fieldFromClassRef = null;
					}
				}else{
					this.fieldFromClassRef = null;
				}
			}
			if(this.fieldFromClassRef == null){
				this.error = true;
				this.errors.printFieldDoesNotExists(fieldOrMethodName);
			}else{
				if(!this.clazz.isAllreadyContainingFieldReference(fieldOrMethodName.getText())){
					f.getClazz().addClassField(this.fieldFromClassRef);
					this.clazz.addFieldReference(this.fieldFromClassRef);
				}
				//System.out.println(fieldFromClassRef.getName() + "     "  + position);
				//TODO Make it better(we know if fieldFromClassRef is array field or normal field)
				if(this.isNextToken(new Token(this.tks.NEW))){
					if(position != -1){
						this.error = true;
						b = false;
						System.out.println("ERROR");
						//TODO PRINT THE ERROR 
					}else{
						this.lfc.readNextToken();
						b = ex.parseTokenNew(this.fieldFromClassRef, m);
						if(b){
							if(!this.error){
								if(this.fieldFromClassRef.getType().isArray()){
									ex.addNewFieldArrayWriter(m, this.fieldFromClassRef, f.getClazz(), f);
								}else{
									//TODO TO IMPLEMET NEW TOKEN FOR FIELD REFERENCES
								}
							}
						}
					}
				}else{
					if(position != -1){
						if(fieldFromClassRef.getType().isArray()){
							if(fieldFromClassRef.getSize() <= position){
								this.error = true;
								System.out.println("ERROR1");
								//TODO PRINT THE ERROR 
							}
						}else{
							this.error = true;
							b = false;
							System.out.println("ERROR2");
							//TODO PRINT THE ERROR 
						}
					}else{
						
					}
					if(b){
						b = ex.parseFieldRefExpression(fieldOrMethodName, m, f.getClazz());
						if(b){
							if(!this.error){
								if(bParser == null){
									ex.addArithmeticFieldRefWriter(m,fieldFromClassRef, this.clazz, position, f);
								}else{
									ex.addArithmeticFieldRefWriterInScope(m,fieldFromClassRef, this.clazz, position, f);
								}
							}
						}
					}
					
				}
			}
			
		}
		return b;
	}
	
/*******************************************************************************************
 *  Token expected(Token token) 
 *  	- 
 *******************************************************************************************/	
	private Token expected(Token token) 
	{
		if(this.lfc.lookAhead().getToken() != token.getToken()){
			if(this.error != true){
				this.error = true;
			}
			this.errors.printError(this.lfc.lookAhead(), token);
			return new Token(-1 , "Unknown Token");
		}
		return this.lfc.readNextToken();
	}
	
/*******************************************************************************************
 *  Token expected(TokenArrayList tk)
 *  	- 
 *******************************************************************************************/
	private Token expected(TokenArrayList tk) {
		for(int i = 0; i < tk.size(); i++){
			if(this.lfc.lookAhead().getToken() == tk.get(i).getToken()){
				return this.lfc.readNextToken();
			}
		}
		
		if(this.error != true){
			this.error = true;
		}
		this.errors.printExpectedMoreTokensError(this.lfc.lookAhead(), tk);
		return new Token(-1 , "Unknown Token");
		
	}
	
/***************************************************************************************
 *  - isNextToken(Token t) - checks if the next token is the same as the parameter t 
 *  - return true if it is otherwise false
 **************************************************************************************/
	private boolean isNextToken(Token t){
		Token t1 = this.lfc.lookAhead();
		if(t1.getToken() == t.getToken()){
			return true;
		}
		return false;
	}
	
/*******************************************************************************************
 * boolean checkFile(String importName) 
 *  		-
 *******************************************************************************************/
	private boolean checkFile(String importName) {
		String str = importName;
		boolean checkFile = new File(str).isFile();
		return checkFile;
	}
	
/*******************************************************************************************
 *  expected((Token token, Token token2)) 
 *  	- 
 *  	-  
 *  	- 
 *******************************************************************************************/
	private Token expected(Token token1, Token token2) {
		Token t = this.lfc.lookAhead();
		if((t.getToken() != token1.getToken()) && (t.getToken() != token2.getToken())){
			this.errors.printError(t, token1, token2);
			this.error = true;
			return new Token(-1 , "Unknown Token");
		}
		t = this.lfc.readNextToken();
		return t;
		
	}
	
	public Field getF(){
		return this.f;
	}
	
	public Field getfieldFromClassRef(){
		return this.fieldFromClassRef;
	}
	
	public boolean getError(){
		return this.error;
	}
}
