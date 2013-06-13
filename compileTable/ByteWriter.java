package compileTable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;


public class ByteWriter
{
	
	private final ByteArrayOutputStream buffer;
	
	private DataOutputStream out;

	public ByteWriter()
	{
		this.buffer = new ByteArrayOutputStream();
		this.out = new DataOutputStream(this.buffer);
		return;
	}


	public void write1Byte(int value)
	{
		DataOutputStream out = this.out;
		try {
			out.writeByte(value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}


	public void write2Byte(int value)
	{
		DataOutputStream out = this.out;
		try {
			out.writeShort(value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	public void write4Byte(int value)
	{
		DataOutputStream out = this.out;
		try {
			out.writeInt(value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	public void writeTo(OutputStream output) 
	{
		ByteArrayOutputStream buffer = this.buffer;
		try {
			output.write(buffer.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

	/*public boolean equals(ByteWriter obj)
	{
		ByteArrayOutputStream buffer = this.buffer;
		Arrays arraysStatic = new Arrays();
		return arraysStatic.compareArrays(buffer.toByteArray(),
				obj.buffer.toByteArray());
	}*/

	public byte[] getByteArray()
	{
		ByteArrayOutputStream buffer = this.buffer;
		return buffer.toByteArray();
	}

	public int size()
	{
		ByteArrayOutputStream buffer = this.buffer;
		return buffer.size();
	}

	/**
	 * Schreibt den Inhalt des übergebenen {@link ByteWriter ByteWriters} in
	 * diesen rein.
	 */
	public void writeAll(ByteWriter byteWriter)
	{
		int offset = this.size();
		try {
			this.buffer.write(byteWriter.getByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*for (int i = 0; i < byteWriter.exceptions.size(); i++)
			this.exceptions.add(new ExceptionHandle(
					byteWriter.exceptions.get(i), offset));*/
		return;
	}

	public void writeAll(byte[] byteArray) 
	{
		try {
			this.out.write(byteArray);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
	public void printByteArray(){
		StringBuffer sb = new StringBuffer(this.getByteArray().length * 2);
		for (int i = 0; i < this.getByteArray().length; i++) {
		      int v = this.getByteArray()[i] & 0xff;
		      if (v < 16) {
		        sb.append('0');
		      }
		      if(i%2==0 && i>0){
		    	  sb.append(Integer.toHexString(v) + " ");
		      }else{
		    	  sb.append(Integer.toHexString(v) + "");
		      }
		      if((i%40 == 0)){
		    	  sb.append("\n");
		      }
		}
		System.out.println(sb.toString().toUpperCase());
	}
	
	public void writeChars(String s){
		try {
			this.out.writeChars(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
