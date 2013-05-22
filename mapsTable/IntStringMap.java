package mapsTable;

import java.util.HashMap;

public class IntStringMap {
	
	HashMap<Integer, String> map = new HashMap<Integer, String>();
	
	public void put(Integer integer, String name)
	{
		this.map.put(integer, name);
	}
	
	public boolean containsKey(Integer key)
	{
		return this.map.containsKey(key);
	}

	public String get(Integer integer)
	{
		return this.map.get(integer);
	}

	public String toString()
	{
		return this.map.toString();
	}
	
	public int size(){
		return this.map.size();
	}

}
