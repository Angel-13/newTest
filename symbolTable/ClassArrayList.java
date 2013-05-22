package symbolTable;

import java.util.ArrayList;
import symbolTable.Class;

public class ClassArrayList {
	
	private ArrayList<Class> array = new ArrayList<Class>();

	public void add(Class clazz)
	{
		this.array.add(clazz);
	}

	public int size()
	{
		return this.array.size();
	}

	public Class get(int i)
	{
		return this.array.get(i);
	}

	public boolean contains(String clazz)
	{
		return this.array.contains(clazz);
	}

	public int indexOf(String c)
	{
		return this.array.indexOf(c);
	}

}
