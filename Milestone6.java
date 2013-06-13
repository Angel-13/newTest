public class Milestone6{
	@M
	public void even_or_odd(int[] numbers, int len){
		int i = 0;
		while(i < len){
			if((numbers[i] >= 0) && even(numbers[i]) > 0){
				System.out.println(numbers[i]);//+ " is even");
			}else if((numbers[i] >= 0) && odd(numbers[i]) > 0){
				System.out.println(numbers[i]);// + " is odd");
			}else{
				System.out.println("number is < 0, sorry...");
			}
			i = i + 1;
		}
	}
	@M
	private int even(int num){
		if(num == 0){
			return 1;
		}else{
			return odd(num-1);
		}
	}
	@M
	private int odd(int num){
		if(num == 0){
			return 0;
		}else{
			return even(num-1);
		}
	}
}



