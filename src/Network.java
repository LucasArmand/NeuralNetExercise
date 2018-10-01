
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
		int input = 2;
		int hidden = 2;
		int output = 1;
		
		double learningRate = 1;
		
		int layers = 3;
		
		//input data fields
		double[][] tInputData = {{1,0},{0,1},{1,1},{0,0}};
		double[][] tOutputData = {{1},{1},{0},{0}};
		//System.out.println("before");
		Matrix tError = new Matrix(0,tOutputData.length);
		Matrix tInput = new Matrix(0,tInputData.length);
		Matrix tOutput = new Matrix(0,tOutputData.length);
		//System.out.println("after");
		
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
		
		tInput.printMatrix();
		n = startN(input,hidden,output,layers,n);
		w = startW(input,hidden,output,layers,w,true);
		dw = startW(input,hidden,output,layers,dw,false);
		nd = startN(input,hidden,output,layers,nd);
		//System.out.println("after");
		int loops = 1000;
		tInput.printMatrix();
		//System.out.print(tInput.getMatrix());
		int tLen = tInputData.length;
		//System.out.println("after");
		for (int p = 0; p < loops; p++) {
			dw = new Matrix[layers-1];
			dw = startW(input,hidden,output,layers,dw,false);
			for(int z = 0; z < tLen; z++) {
				tInput = new Matrix(tInputData[z]);
				tOutput = new Matrix(tOutputData[z]);
				tError = new Matrix(1,output);
	
				n = new Matrix[layers];
				nd = new Matrix[layers];
				n = startN(input,hidden,output,layers,n);
				
				nd = startN(input,hidden,output,layers,nd);
				//n[0].printMatrix();
				n[0] = tInput;
				//n[0].printMatrix();
				n[0].addBias(1);
				//System.out.println("hi");
				//n[0].printMatrix();
				for(int i = 0; i < layers-1; i++) {
					n[i+1] = n[i].dot(w[i]);
					
					n[i+1].sigmoid();
					n[i+1].addBias(1);
					//System.out.println("hiInside");
					n[i].printMatrix();
					System.out.println("____"+i+"___");
					w[i].printMatrix();
					System.out.println("_ _ _ _ _ _ _");
				}
				n[layers-1] = n[layers-2].dot(w[layers-2]);
				n[layers-1].sigmoid();
				System.out.print("The answer for " + tInput.getMatrix() + " is ");
				n[layers-1].printMatrix();
				tOutput.printMatrix();
				tError = tOutput.sub(n[layers-1]);
				
				/*
				for (Matrix i:w) {
					i.printMatrix();
					System.out.println("////????////????");
				}
			 	*/
				
				//J is where it comes from, I is where its going to
				//System.out.println(w.length);
				
				for(int l = nd.length-1; l > 0; l--) {
					//nd[l].printMatrix();
					//System.out.println(l);
					for(int i = 0; i < nd[l].matrix[0].length;i++) {
						if (l == nd.length - 1) {	
							
							nd[l].matrix[0][i] = (n[l].matrix[0][i] * ( 1 - n[l].matrix[0][i])) * (n[l].matrix[0][i] - tOutput.matrix[0][i]);
						}
						else if(l == nd.length - 2){
							for (int j = 0; j < nd[l+1].matrix[0].length; j++) {
								
								//System.out.println("T " + w[l-1].matrix[j][i]);
								nd[l+1].printMatrix();
								System.out.println(l + " " + i + " " + j + " " + w[l].matrix[i][j]);
								nd[l].matrix[0][i] += w[l].matrix[i][j] * nd[l+1].matrix[0][j];
							}
							System.out.println("Here it is before " + nd[l].matrix[0][i]);
							nd[l].matrix[0][i] = nd[l].matrix[0][i] * (n[l].matrix[0][i] * (1 - n[l].matrix[0][i])); //sig
							System.out.println("Here it is " + nd[l].matrix[0][i]);
						}else {
							for (int j = 0; j < nd[l+1].matrix[0].length - 1; j++) {
								nd[l+1].printMatrix();
								System.out.println(l + " " + i + " " + j + " " + w[l-1].matrix[j][i]);
								nd[l].matrix[0][i] += w[l-1].matrix[j][i] * nd[l+1].matrix[0][j];
							}
							nd[l].matrix[0][i] *= (n[l].matrix[0][i] * (1 - n[l].matrix[0][i])); //sig
						}
						
					}
					//nd[l].printMatrix();
				}
				
				for (int l = 0; l < w.length; l++) {
					for (int i = 0; i < w[l].matrix.length; i++) {
						for(int j = 0; j < w[l].matrix[0].length; j++) {
							dw[l].matrix[i][j] += nd[l+1].matrix[0][j] * n[l].matrix[0][i];
							//System.out.print(dw[l].matrix[i][j] + " : ");
							//w[l].matrix[i][j] -= dw[l].matrix[i][j] * learningRate;
						}
					}
				}
				System.out.println("XXXXXXXXXXXXXXXXXXXXXX");
				for (Matrix d:dw) {
					d.printMatrix();
				}
				System.out.println("XXXXXXXXXXXXXXXXXXXXXX");
				for (int l = 0; l < w.length; l++) {
					for (int i = 0; i < w[l].matrix.length; i++) {
						for(int j = 0; j < w[l].matrix[0].length; j++) {
							//w[l].matrix[i][j] -= dw[l].matrix[i][j] * learningRate;
						}
					}
					//dw[l].printMatrix();
				}
				//calculating derivative for each node 
			}
			//todo: make program for getting W derivative through using nd derivatives
			
			for (int l = 0; l < w.length; l++) {
				for (int i = 0; i < w[l].matrix.length; i++) {
					for(int j = 0; j < w[l].matrix[0].length; j++) {
						w[l].matrix[i][j] -= dw[l].matrix[i][j] * learningRate;
					}
				}
				//dw[l].printMatrix();
			}
			
		
		
		}
		//System.out.println("Error of " + tError[0].getMatrix() + " and squared " + tError[0].square().getMatrix(););
		
	}

}







































