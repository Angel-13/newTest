package mapsTable;

import java.util.HashMap;
import symbolTable.Method;

public class MethodIntMap {
	private final HashMap<Method, Integer> map = new HashMap<Method, Integer>();

	public Integer get(Object key)
	{
		return this.map.get(key);
	}

	
	public Integer put(Method key, Integer value)
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
