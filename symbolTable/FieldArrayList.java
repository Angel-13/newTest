package symbolTable;

import java.util.ArrayList;

public class FieldArrayList
{
	private ArrayList<Field> array = new ArrayList<Field>();

	public void add(Field field)
	{
		this.array.add(field);
	}

	public int size()
	{
		return this.array.size();
	}

	public Field get(int i)
	{
		return this.array.get(i);
	}

	public boolean contains(Field field)
	{
		for(int i=0; i<this.array.size();i++){
			if((this.array.get(i).getType().getDescriptor().equals(field.getType().getDescriptor()))
					&& (this.array.get(i).getName().equals(field.getName())))
				return true;
		}
		return false;

	}
	
	public boolean containsByName(String name)
	{
		for(int i=0; i<this.array.size();i++){
			if(this.array.get(i).getName().equals(name))
				return true;
		}
		return false;

	}
	
	public int indexOf(Field f)
	{
		return this.array.indexOf(f);
	}
	
	public int indexOfByName(String name)
	{
		for(int i=0; i<this.array.size();i++){
			if(this.array.get(i).getName().equals(name))
				return i;
		}
		return -1;
	}
	
	public Field getFieldByName(String name){
		for(int i=0; i<this.array.size();i++){
			if(this.array.get(i).getName().equals(name))
				return array.get(i);
		}
		return null;
	}
	
	public boolean isEmpty(){
		return this.array.isEmpty();
	}
	
	public void clear(){
		this.array.clear();
	}
}