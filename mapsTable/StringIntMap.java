package mapsTable;

import java.util.HashMap;

public class StringIntMap {
	
	HashMap<String, Integer> map = new HashMap<String, Integer>();

	public void put(String name, Integer integer)
	{
		this.map.put(name, integer);
	}
	
	public boolean containsKey(String key)
	{
		return this.map.containsKey(key);
	}

	public Integer get(String name)
	{
		return this.map.get(name);
	}

	public String toString()
	{
		return this.map.toString();
	}
	
	public int size(){
		return this.map.size();
	}

}
