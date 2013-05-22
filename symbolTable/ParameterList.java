package symbolTable;

public class ParameterList {
	
	private final FieldArrayList parameters;

	public ParameterList()
	{
		this.parameters = new FieldArrayList();
		
	}

	
	public void addParameter(Field parameter)
	{
		FieldArrayList parameters = this.parameters;
		parameters.add(parameter);
		
	}

	
	public int getSize()
	{
		return this.parameters.size();
	}

	
	public Field getParameter(int i)
	{
		return this.parameters.get(i);
	}

	public boolean contains(Field field){
		return this.parameters.contains(field);
	}
	
	public boolean containsByName(String name){
		return this.parameters.contains(name);
	}
	
	public int indexOfByName(String name){
		return this.parameters.indexOfByName(name);
	}
	
	/*public Signature makeSignature()
	{
		Type[] types = new Type[this.parameters.size()];
		for (int i = 0; i < this.parameters.size(); i++)
			types[i] = this.parameters.get(i).getType();
		return new Signature(types);
	}*/
	public Field getFieldByName(String name){
		return this.parameters.getFieldByName(name);
	}
	
	public boolean isEmpty(){
		return this.parameters.isEmpty();
	}
}
