package symbolTable;

import java.util.ArrayList;

public class StackMapTableList {
	
	private ArrayList<StackMapTableObject> list = new ArrayList<StackMapTableObject>();

	public void add(StackMapTableObject e)
	{
		this.list.add(e);
	}
	
	public boolean contains(StackMapTableObject o)
	{
		return this.list.contains(o);
	}

	public StackMapTableObject get(int index)
	{
		return this.list.get(index);
	}

	public int indexOf(StackMapTableObject o)
	{
		return this.list.indexOf(o);
	}

	public int size()
	{
		return this.list.size();
	}

	public Object[] toArray()
	{
		return this.list.toArray();
	}
	
	public void remove(int index){
		this.list.remove(index);
	}
	
	public boolean isEmpty(){
		return this.list.isEmpty();
	}

}
