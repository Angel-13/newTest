package mapsTable;

import java.util.HashMap;
import symbolTable.Class;
import symbolTable.M;

public class ClassIntMap {
	
	private final HashMap<Class, Integer> map = new HashMap<Class, Integer>();

	@M
	public boolean containsKey(Object key)
	{
		return this.map.containsKey(key);
	}

	@M
	public Integer get(Object key)
	{
		return this.map.get(key);
	}

	@M	
	public Integer put(Class key, Integer value)
	{
		return this.map.put(key, value);
	}
	
	public int size(){
		return this.map.size();
	}

}
