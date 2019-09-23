
public class PoolTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Matrix l1 = new Matrix(4,4);
		l1.populate();
		Filter f = new Filter(3,3,1);
		f.matrix = new double[][] {{1,0,0},{0,1,0},{0,0,1}};
		Matrix l2 = new Matrix(4,4);
		l2 = f.evaluate(l1);
		System.out.println(l1);
		//System.out.println(f);
		System.out.println(l2);
		//System.out.println(f.reverseConvolve(l2));
		System.out.println(f.getDF(l1, l2));
		double sum = 0;
		for(int i = 0; i < l1.matrix.length; i++) {
			for(int j = 0; j < l1.matrix[0].length; j++) {
				sum += l1.matrix[i][j] * l2.matrix[i][j];
			}
		}
		System.out.println(sum);
	}

}
