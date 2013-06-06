public class TestMilestone5{

	@M
	public static int milestone_4(String[] args){
		int i;
		int len;
		
		Milestone5 rec;
		i = 0;
		len = 10;//try some different positive values here
		rec = new Milestone5();
		rec.max = 0;
		rec.sum = 0;
		rec.avg = 42;
		rec.data = new int[len];
		// statically initialize rec->data with 'len' values, e.g.,
		// rec->data[0] = 23; ...; rec->data[len-1] = 42;
		rec.data[0] = 23;
		rec.data[1] = 25;
		rec.data[2] = 26;
		rec.data[3] = 26;
		rec.data[4] = 26;
		rec.data[2] = 47;
		rec.data[5] = 26;
		rec.data[6] = 26;
		
		while (i < len) {
			rec.sum = rec.sum + rec.data[i];
			if (rec.data[i] > rec.max) {
				rec.max = rec.data[i];
			}
			i = i + 1;
		}
		if (len != 0 && (rec.sum / len > 0)) {
			rec.avg = rec.sum / len;
		}else{
			rec.avg = 0;
		}
		return rec.avg;
	}
}