
public class Network {
	public static double sigmoid(double a) {
		return(1.0 / (1 + Math.pow(Math.E,-1 * a)));
	}
	public static Matrix[] startW(int input ,int hidden,int output,int layers, Matrix[] m, boolean pop) {
		m[0] = new Matrix(input + 1,hidden);
		m[layers-2] = new Matrix(hidden + 1,output);
		for (int i = 0; i < layers-1;i++) {
			if ( i > 0 && i < layers - 2) {
				m[i] = new Matrix(hidden + 1,hidden);
			}
			if (pop) m[i].populate();
		}
		return m;
	}
	public static Matrix[] startN(int input ,int hidden,int output,int layers, Matrix[] n) {
		n[0] = new Matrix(1,input);
		n[layers-1] = new Matrix(1,output);
		for (int i = 0; i < layers;i++) {
			if ( i > 0 && i < layers - 1) {
				n[i] = new Matrix(1,hidden);
			}
			//n[i].printMatrix();
			//System.out.println("----------");
		}
		return n;
	}
	public static void main(String[] args) {

		//length of Matrix of each section
		int input = 3;
		int hidden = 4;
		int output = 3;
		
		int layers = 4;
		
		//input data fields
		double[][] tInputData = {{1,0,2},{2,1,2},{1,1,2}};
		double[][] tOutputData = {{1,0,0},{1,0.5,0},{1,0,1}};
		
		Matrix[] tError = new Matrix[tOutputData.length];
		Matrix[] tInput = new Matrix[tInputData.length];
		Matrix[] tOutput = new Matrix[tOutputData.length];
		
		//making array of matrices of input  data
		for(int i = 0; i < tInput.length; i++) {
			tInput[i] = new Matrix(tInputData[i]);
			tOutput[i] = new Matrix(tOutputData[i]);
			tError[i] = new Matrix(1,output);
			//tInput[i].printMatrix();
			//tOutput[i].printMatrix();
		}
		
		
		Matrix[] n = new Matrix[layers];
		Matrix[] nd = new Matrix[layers];
		Matrix[] w = new Matrix[layers-1];
		Matrix[] dw = new Matrix[layers-1];
		
		/*   |b0  b1  b1
		 * _____________
		 * a0|a0b0 a0b1 a0b2
		 * a1|a1b0 a1b1 a1b2
		 * a2|a2b0 a2b1 a2b2
		 * etc..
		 */
		n = startN(input,hidden,output,layers,n);
		w = startW(input,hidden,output,layers,w,true);
		dw = startW(input,hidden,output,layers,dw,false);
		nd = startN(input,hidden,output,layers,nd);
		

		n[0] = tInput[0];
		n[0].addBias(1);
		for(int i = 0; i < layers-1; i++) {
			n[i+1] = n[i].dot(w[i]);
			
			n[i+1].sigmoid();
			n[i+1].addBias(1);
			n[i].printMatrix();
			System.out.println("____"+i+"___");
			w[i].printMatrix();
			System.out.println("_ _ _ _ _ _ _");
		}
		n[layers-1] = n[layers-2].dot(w[layers-2]);
		n[layers-1].sigmoid();
		n[layers-1].printMatrix();
		tOutput[0].printMatrix();
		tError[0] = tOutput[0].sub(n[layers-1]);
		
		for (Matrix d:dw) {
			d.fill(1);
		}
		/*
		for (Matrix i:w) {
			i.printMatrix();
			System.out.println("////????////????");
		}
	 	*/
		
		//J is where it comes from, I is where its going to
		//System.out.println(w.length);
		
		for(int l = nd.length-1; l > 0; l--) {
			nd[l].printMatrix();
			System.out.println(l);
			for(int i = 0; i < nd[l].matrix[0].length;i++) {
				if (l == nd.length - 1) {	
						nd[l].matrix[0][i] = (n[l].matrix[0][i] * ( 1 - n[l].matrix[0][i])) * (n[l].matrix[0][i] - tOutput[0].matrix[0][i]);
				}
				else {
					for (int j = 0; j < nd[l+1].matrix[0].length; j++) {
						nd[l].matrix[0][i] = w[l-1].matrix[i][j] * nd[l+1].matrix[0][j];
					}
				}
				
			}
			nd[l].printMatrix();
		}
		//calculating derivative for each node 
		
		//todo: make program for getting W derivative through using nd derivatives

		
		//System.out.println("Error of " + tError[0].getMatrix() + " and squared " + tError[0].square().getMatrix(););
		
	}

}
