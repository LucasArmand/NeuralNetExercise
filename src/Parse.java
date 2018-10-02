
public class Parse {
	private final char[] ALPHA = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	
	public int[] parse(String in) {
		in = in.toLowerCase();
		int[] out = new int[in.length()];
		for (int c = 0; c < in.length(); c++) {
			
			for(int i = 0; i < ALPHA.length; i++) {
				//System.out.print(ALPHA[i] + " " + in.charAt(c));
				if (ALPHA[i] == in.charAt(c)) {
					out[c] = i;
				}
			}
		}
		return out;
	}
	public void print(int[] nums) {
		System.out.print("[");
		for (int i:nums) {
			System.out.print(i + " ");
		}
		System.out.print("]");
	}
}
