public class Test{
	@M
	public static int milestone_4(String[] args){
		int a;
		Milestone4 m  = new Milestone4();
		a = 5;
		m.arr = new int[3];
		m.arr[1] = 4;
		m.g = 7;
		m.f = 9;
		m.arr[0] = a + (a - m.arr[1] * m.f - a/m.g*(12-11));
		return m.arr[0];
	}
}