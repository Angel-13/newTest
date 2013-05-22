package symbolTable;

import java.util.ArrayList;

public class StringArrayList {
	
	private ArrayList<String> list = new ArrayList<String>();

	public void add(String e)
	{
		this.list.add(e);
	}

	public boolean contains(String o)
	{
		return this.list.contains(o);
	}

	public String get(int index)
	{
		return this.list.get(index);
	}

	public int indexOf(String o)
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
}
	