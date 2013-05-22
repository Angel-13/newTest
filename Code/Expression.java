package Code;

import compileTable.ByteWriter;

public interface Expression {
	
	public ByteWriter getCode();
	public boolean validate();
	public ByteWriter getExpressionCode();

}
