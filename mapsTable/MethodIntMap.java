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

	
	public boolean containsKey(Method key)
	{
		return this.map.containsKey(key);
	}
	
	public boolean containsValue(Integer key)
	{
		return this.map.containsValue(key);
	}
	
	public int size(){
		return this.map.size();
	}
	
	public String toString()
	{
		return this.map.toString();
	}
}
