package mapsTable;

import java.util.HashMap;
import symbolTable.Field;

public class FieldIntMap {
	
	private final HashMap<Field, Integer> map = new HashMap<Field, Integer>();
	
	public Integer get(Object key)
	{
		return this.map.get(key);
	}

	public Integer put(Field key, Integer value)
	{
		return this.map.put(key, value);
	}

	public boolean containsKey(Object key)
	{
		return this.map.containsKey(key);
	}
	
	public int size(){
		return this.map.size();
	}
}
