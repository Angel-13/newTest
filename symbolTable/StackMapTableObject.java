package symbolTable;

import compileTable.ByteWriter;


public class StackMapTableObject{
	
	private final int position;
	
	private final FieldArrayList stackFrameFieldCounter;
	
	private final ByteWriter code;
	
	public StackMapTableObject(int position, FieldArrayList stackFrameFieldCounter){
		this.position = position;
		this.stackFrameFieldCounter = stackFrameFieldCounter;
		this.code = new ByteWriter();
	}
	
	public void makeAppendFrame(Method m){
		this.code.write1Byte(this.getFrameType());
		this.code.write2Byte(position);
		this.makeVerificationTypeInfo(m);
	}

	public void makeSameFrame(){
		this.code.write1Byte(this.position);
		
	}

	public void makeChopFrame(){
		
	}	
	
	public ByteWriter getExpressionCode() {
		return this.code;
	}
	
	private int getFrameType(){
		if(this.stackFrameFieldCounter.size() == 1){
			return 252;
		}else if(this.stackFrameFieldCounter.size() == 2){
			return 253;
		}else{
			return 254;
		}
	}
	
	private void makeVerificationTypeInfo(Method m) {
		for(int i = 0; i < this.stackFrameFieldCounter.size(); i++){
			Field f = this.stackFrameFieldCounter.get(i);
			if(f.getType().isClass()){
				this.code.write1Byte(7);
				this.code.write2Byte(m.getClazz().getClassIntMap().get(f.getClazz()));
			}else if(f.getType().isInteger()){
				this.code.write1Byte(1);
			}else if(f.getType().isNull()){
				this.code.write1Byte(5);
			}
		}
		
	}
	
	public int getPosition(){
		return this.position;
	}
}
