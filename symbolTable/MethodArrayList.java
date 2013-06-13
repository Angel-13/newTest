package symbolTable;

import java.util.ArrayList;

public class MethodArrayList
{
	private ArrayList<Method> list = new ArrayList<Method>();

	public boolean add(Method e)
	{
		return this.list.add(e);
	}

	public boolean contains(Method o)
	{
		return this.list.contains(o);
	}

	public Method get(int index)
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
	
	public void clear(){
		this.list.clear();
	}
	
	public boolean isEmpty(){
		return this.list.isEmpty();
	}
	
	public Method remove(int index){
		return this.list.remove(index);
	}
}
