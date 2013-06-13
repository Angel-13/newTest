package symbolTable;

import java.util.ArrayList;

import Code.Expression;

public class ExpressionsList {
	
		private ArrayList<Expression> list = new ArrayList<Expression>();

		public boolean add(Expression e)
		{
			return this.list.add(e);
		}

		public boolean contains(Expression o)
		{
			return this.list.contains(o);
		}

		public Expression get(int index)
		{
			return this.list.get(index);
		}

		public int indexOf(Expression o)
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
}
