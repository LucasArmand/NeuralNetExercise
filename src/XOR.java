
public class XOR {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Network n = new Network(2,2,1,5, new double[][] {{0,0},{0,1},{1,0},{1,1}}, new double[][] {{0},{1},{1},{0}});
		n.train(1000000, .05);
		System.out.println("1 and 1 " + n.run(new double[]{1,1})[0]);
		System.out.println("1 and 0 " + n.run(new double[]{1,0})[0]);
		System.out.println("0 and 1 " + n.run(new double[]{0,1})[0]);
		System.out.println("0 and 0 " + n.run(new double[]{0,0})[0]);
	}

}
