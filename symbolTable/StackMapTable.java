package symbolTable;

import java.util.ArrayList;

public class StackMapTable {
	
	private ArrayList<Integer> list = new ArrayList<Integer>();

	public boolean add(Integer e)
	{
		return this.list.add(e);
	}

	public boolean contains(Integer o)
	{
		return this.list.contains(o);
	}

	public Integer get(int index)
	{
		return this.list.get(index);
	}

	public int indexOf(Method o)
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

}
