package symbolTable;

import java.util.ArrayList;

public class ConstructorArrayList {
	
	private ArrayList<Constructor> array = new ArrayList<Constructor>();

	public void add(Constructor c)
	{
		this.array.add(c);
	}

	public int size()
	{
		return this.array.size();
	}

	public Constructor get(int i)
	{
		return this.array.get(i);
	}

	public boolean contains(Constructor c)
	{
		return this.array.contains(c);
	}

	public int indexOf(Constructor c)
	{
		return this.array.indexOf(c);
	}


}
