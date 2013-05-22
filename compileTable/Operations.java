package compileTable;

import tokens.Tokens;

public class Operations
{
	public final int ALOAD_0;

	public final int ALOAD_2;

	public final int ALOAD_1;

	public final int ALOAD_3;

	public final Tokens tokens;
	
	/** Push null */
	
	public final int ACONST_NULL;
	/**
	 * Push int constant 0
	 */
	
	public final int ICONST_0;
	/**
	 * Push int constant 1
	 */
	
	public final int ICONST_1;
	/**
	 * Push int constant 2
	 */
	public final int ICONST_2;
	/**
	 * Push int constant 3
	 */
	public final int ICONST_3;
	
	/** Push int constant 4 */
	
	public final int ICONST_4;
	/** Push int constant 5 */
	
	public final int ICONST_5;
	/** Push byte, nächste Byte: byte */
	public final int BIPUSH;
	/**
	 * Push item from runtime constant pool
	 * 
	 * nächstes Byte: index
	 * 
	 * @deprecated use {@link #LDC_W}
	 */
	
	@Deprecated
	public final int LDC;
	
	public final int LDC_W;
	/** nächstes byte: index */
	
	public final int ILOAD;
	/** Load reference from local variable, nächstes byte: index */
	
	public final int ALOAD;
	/** Load int from array */
	
	public final int IALOAD;
	/** Load reference from array */
	
	public final int AALOAD;
	/** Load byte or boolean from array */
	
	public final int BALOAD;
	/** Load char from array */
	
	public final int CALOAD;
	/** Nächstes Byte: Index, an dem gespeichert werden soll. */
	
	public final int ISTORE;
	/** Store reference into local variable */
	
	public final int ASTORE;
	/** @deprecated use {@link #ISTORE} and 0 */
	@Deprecated
	public final int ASTORE_0;
	
	public final int ASTORE_1;
	
	public final int ASTORE_2;
	
	public final int ASTORE_3;
	
	public final int ILOAD_0;
	
	public final int ILOAD_1;
	
	public final int ILOAD_2;
	
	public final int ILOAD_3;
	
	public final int ISTORE_0;
	/** @deprecated use {@link #ISTORE} and 1 */
	@Deprecated
	
	public final int ISTORE_1;
	/** @deprecated use {@link #ISTORE} and 2 */
	@Deprecated
	
	public final int ISTORE_2;
	/** @deprecated use {@link #ISTORE} and 3 */
	@Deprecated
	
	public final int ISTORE_3;
	/** Store into int array */
	
	public final int IASTORE;
	/** Store into reference array */
	
	public final int AASTORE;
	/** Store into byte or boolean array */
	
	public final int BASTORE;
	/** Store into char array */
	
	public final int CASTORE;
	/** Duplicate the top operand stack value */
	
	public final int DUP;
	/** Add int */
	
	public final int IADD;
	/** Subtract int */
	
	public final int ISUB;
	/** Multiply int */
	
	public final int IMUL;
	/** Divide int */
	
	public final int IDIV;
	/** Negate int */
	
	public final int INEG;
	/** Boolean AND int */
	
	public final int IAND;
	/** Boolean OR int */
	
	public final int IOR;
	/** nächste Bytes: index, const */
	
	public final int IINC;
	/**
	 * Branch if int comparison with zero succeeds. eq succeeds if and only if
	 * value = 0
	 */
	
	public final int IFEQ;
	/**
	 * Branch if int comparison with zero succeeds, next bytes: branchbyte 1,
	 * branchbyte 2
	 */
	
	public final int IFNE;
	
	public final int IFLE;
	
	public final int IFLT;
	
	public final int IFGT;
	/**
	 * Branch if int comparison with zero succeeds, next bytes: branchbyte1,
	 * branchbyte2
	 * 
	 * @deprecated use {@link #ICONST_0} and {@link #IF_ICMPGE}
	 */
	@Deprecated
	
	public final int IFGE;
	/**
	 * Branch if int comparison succeeds, equal, nächste bytes: branchbyte 1,
	 * branchbyte 2 Branch if int comparison succeeds: eq succeeds if and only
	 * if value1 = value2.
	 * 
	 * nächste bytes: branchbyte 1, branchbyte 2
	 */
	
	public final int IF_ICMPEQ;
	/**
	 * Branch if int comparison succeeds, greater equal, nächste bytes:
	 * branchbyte 1, branchbyte 2 Branch if int comparison succeeds: ne succeeds
	 * if and only if value1 != value2.
	 * 
	 * nächste bytes: branchbyte 1, branchbyte 2
	 */
	
	public final int IF_ICMPNE;
	/**
	 * Branch if int comparison succeeds, greater equal, nächste bytes:
	 * branchbyte 1, branchbyte 2 Branch if int comparison succeeds: ne succeeds
	 * if and only if value1 < value2.
	 * 
	 * nächste bytes: branchbyte 1, branchbyte 2
	 */
	
	public final int IF_ICMPLT;
	/**
	 * Branch if int comparison succeeds, nächste bytes: branchbyte 1,
	 * branchbyte 2
	 */
	
	public final int IF_ICMPGE;
	/**
	 * succeeds if and only if value1 > value2, nächste bytes: branchbyte 1,
	 * branchbyte 2
	 */
	
	public final int IF_ICMPGT;
	/**
	 * greater, Branch if int comparison succeeds, nächstes bytes: branchbyte 1,
	 * branchbyte 2
	 */
	
	public final int IF_ICMPLE;
	/** Branch always, next bytes: branchbyte 1, branchbyte 2 */
	
	public final int GOTO;
	/** Return int from method */
	
	public final int IRETURN;
	/** return void from method */
	
	public final int RETURN;
	/** Fetch field from object, next bytes: indexbyte1, indexbyte2 */
	
	public final int GETFIELD;
	/** Set field in object, next bytes: indexbyte1, indexbyte2 */
	
	public final int PUTFIELD;
	/**
	 * Invoke instance method; dispatch based on class.
	 * 
	 * Danach: indexbyte1, indexbyte2
	 * 
	 * Stack: objectref, arg0..n => nix
	 */
	
	public final int INVOKEVIRTUAL;
	/**
	 * Invoke instance method; special handling for superclass, private, and
	 * instance initialization method invocations next bytes: indexbyte1,
	 * indexbyte2
	 */
	
	public final int INVOKESPECIAL;
	/** Create new object, next bytes: indexbyte1, indexbyte2 */
	
	public final int NEW;
	/** Create new array, next byte: atype-byte */
	
	public final int NEWARRAY;
	/** Create new array of reference, next bytes: indexbyte1, indexbyte2 */
	
	public final int ANEWARRAY;
	/** Throw exception or error */
	
	public final int ATHROW;
	/**
	 * Create new multidimensional array
	 * 
	 * next bytes: indexbyte1, indexbyte2, dimensions
	 */
	
	public final int MULTIANEWARRAY;
	
	public final int IF_ACMPNE;
	
	public final int IF_ACMPEQ;
	
	public final int ARRAYLENGTH;
	
	public final int POP;
	
	public final int ARETURN;
	
	public final int PUTSTATIC;
	
	public final int GETSTATIC;
	
	public final int INVOKESTATIC;
	
	public final int SIPUSH;

	
	public Operations()
	{
		this.ALOAD_0 = 0x2a;
		this.ALOAD_1 = 0x2b;
		this.ALOAD_2 = 0x2c;
		this.ALOAD_3 = 0x2d;
		this.ILOAD_0 = 0x1a;
		this.ILOAD_1 = 0x1b;
		this.ILOAD_2 = 0x1c;
		this.ILOAD_3 = 0x1d;
		this.IFNE = 0x9a;
		this.IFLE = 0x9e;
		this.IFGT = 0x9d;
		this.ACONST_NULL = 0x1;
		this.ICONST_0 = 0x03;
		this.ICONST_1 = 0x04;
		this.ICONST_2 = 0x05;
		this.ICONST_3 = 0x06;
		this.ICONST_4 = 0x07;
		this.ICONST_5 = 0x08;
		this.BIPUSH = 0x10;
		this.SIPUSH = 0x11;
		this.LDC = 0x12;
		this.LDC_W = 0x13;
		this.ILOAD = 0x15;
		this.ALOAD = 0x19;
		this.IALOAD = 0x2e;
		this.AALOAD = 0x32;
		this.BALOAD = 0x33;
		this.CALOAD = 0x34;
		this.ISTORE = 0x36;
		this.ASTORE = 0x3a;
		this.ISTORE_0 = 0x3b;
		this.ISTORE_1 = 0x3c;
		this.ISTORE_2 = 0x3d;
		this.ISTORE_3 = 0x3e;
		this.IASTORE = 0x4f;
		this.AASTORE = 0x53;
		this.BASTORE = 0x54;
		this.CASTORE = 0x55;
		this.POP = 0x57;
		this.DUP = 0x59;
		this.IADD = 0x60;
		this.ISUB = 0x64;
		this.IMUL = 0x68;
		this.IDIV = 0x6c;
		this.INEG = 0x74;
		this.IAND = 0x7e;
		this.IOR = 0x80;
		this.IINC = 0x84;
		this.IFEQ = 0x99;
		this.IFLT = 0x9b;
		this.IFGE = 0x9c;
		this.IF_ICMPEQ = 0x9f;
		this.IF_ICMPNE = 0xa0;
		this.IF_ICMPLT = 0xa1;
		this.IF_ICMPGE = 0xa2;
		this.IF_ICMPGT = 0xa3;
		this.IF_ICMPLE = 0xa4;
		this.IF_ACMPEQ = 0xa5;
		this.IF_ACMPNE = 0xa6;
		this.GOTO = 0xa7;
		this.IRETURN = 0xac;
		this.ARETURN = 0xb0;
		this.RETURN = 0xb1;
		this.GETSTATIC = 0xb2;
		this.PUTSTATIC = 0xb3;
		this.GETFIELD = 0xb4;
		this.PUTFIELD = 0xb5;
		this.INVOKEVIRTUAL = 0xb6;
		this.INVOKESPECIAL = 0xb7;
		this.INVOKESTATIC = 0xb8;
		this.NEW = 0xbb;
		this.NEWARRAY = 0xbc;
		this.ANEWARRAY = 0xbd;
		this.ARRAYLENGTH = 0xbe;
		this.ATHROW = 0xbf;
		this.MULTIANEWARRAY = 0xc5;
		this.ASTORE_0 = 0x4b;
		this.ASTORE_1 = 0x4c;
		this.ASTORE_2 = 0x4d;
		this.ASTORE_3 = 0x4e;
		this.tokens = new Tokens();
	}
	
	public int getISTOREbyNumber(int number){
		if(number == 0){
			return this.ISTORE_0;
		}else if(number == 1){
			return this.ISTORE_1;
		}else if(number == 2){
			return this.ISTORE_2;
		}else{ 
			return this.ISTORE_3;
		}
		
	}
	
	public int getILOADbyNumber(int number){
		if(number == 0){
			return this.ILOAD_0;
		}else if(number == 1){
			return this.ILOAD_1;
		}else if(number == 2){
			return this.ILOAD_2;
		}else 
			return this.ILOAD_3;
		
	}
	
	public int getASTROEbyNumber(int number){
		if(number == 0){
			return this.ASTORE_0;
		}else if(number == 1){
			return this.ASTORE_1;
		}else if(number == 2){
			return this.ASTORE_2;
		}else 
			return this.ASTORE_3;
		
	}
	
	public int getALOADbyNumber(int number){
		if(number == 0){
			return this.ALOAD_0;
		}else if(number == 1){
			return this.ALOAD_1;
		}else if(number == 2){
			return this.ALOAD_2;
		}else 
			return this.ALOAD_3;
		
	}
	
	public int getICONSTbyNumber(int number){
		if(number == 0){
			return this.ICONST_0;
		}else if(number == 1){
			return this.ICONST_1;
		}else if(number == 2){
			return this.ICONST_2;
		}else if(number == 3){
			return this.ICONST_3;
		}else if(number == 4){
			return this.ICONST_4;
		}else{
			return this.ICONST_5;
		}
	}
	
	public int geBranchInstructionForNull(int token){
		if(token == tokens.EQUAL){
			return this.IFNE;
		}else if(token == tokens.GREATER){
			return this.IFLE;
		}else if(token == tokens.GREATER_EQUAL){
			return this.IFLT;
		}else if(token == tokens.LESS){
			return this.IFGE;
		}else if(token == tokens.LESS_EQUAL){
			return this.IFGT;
		}else {
			return this.IFEQ;
		}
	}
	
	public int geBranchInstructionForInteger(int token){
		if(token == tokens.EQUAL){
			return this.IF_ICMPNE;
		}else if(token == tokens.GREATER){
			return this.IF_ICMPLE;
		}else if(token == tokens.GREATER_EQUAL){
			return this.IF_ICMPLT;
		}else if(token == tokens.LESS){
			return this.IF_ICMPGE;
		}else if(token == tokens.LESS_EQUAL){
			return this.IF_ICMPGT;
		}else {
			return this.IF_ICMPEQ;
		}
	}
	
}
