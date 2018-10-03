import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
		}
		return n;
	}
	
	public static void main(String[] args) throws IOException {

		
		File english = new File("english.txt");
		File italian = new File("italian.txt");
		FileReader englishFile = new FileReader(english);
		FileReader italianFile = new FileReader(italian);
		BufferedReader englishReader = new BufferedReader(englishFile);
		BufferedReader italianReader = new BufferedReader(italianFile);
		StringBuffer englishBuffer = new StringBuffer();
		StringBuffer italianBuffer = new StringBuffer();
		
		String line;
		

		while ((line = englishReader.readLine()) != null) {
			englishBuffer.append(line);
			englishBuffer.append("\n");
		}
		englishReader.close();
		while ((line = italianReader.readLine()) != null) {
			italianBuffer.append(line);
			italianBuffer.append("\n");
		}
		italianReader.close();
		System.out.println("Contents of file:");
		System.out.println(italianBuffer.toString());
		System.out.println(englishBuffer.toString());
		
		String[] englishWords = englishBuffer.toString().split("\n");
		String[] italianWords = italianBuffer.toString().split("\n");
		
		double[][] englishNums = new double[englishWords.length][15];
		double[][] italianNums = new double[italianWords.length][15];
		
		Parse parse = new Parse();
		
		for(int i = 0; i < englishWords.length; i++) {
			englishNums[i] = parse.parse(englishWords[i],15);
		}
		for(int i = 0; i < italianWords.length; i++) {
			italianNums[i] = parse.parse(italianWords[i],15);
		}
		parse.print(englishNums[0]);
		parse.print(italianNums[0]);
		
		
		
		//length of Matrix of each section
		int input = 15;
		int hidden = 25;
		int output = 1;
		
		double[][] combinedDictionary = new double[englishNums.length + italianNums.length][5];
		double[][] langResult = new double[englishNums.length + italianNums.length][1];
		for(int i = 0; i < combinedDictionary.length; i++) {
			if (i < englishNums.length) {
				combinedDictionary[i] = englishNums[i];
				langResult[i][0] = 1;
			}else {
				combinedDictionary[i] = italianNums[i-englishNums.length];
				langResult[i][0] = 0;
			}
		}

		double learningRate = 0.05;
		
		int layers = 10;
		
		//input data fields
		double[][] tInputData = combinedDictionary;
		double[][] tOutputData = langResult;
		Matrix tInput = new Matrix(0,tInputData.length);
		Matrix tOutput = new Matrix(0,tOutputData.length);
		
		
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
		
		int loops = 1000;
		int tLen = tInputData.length;
		
		for (int p = 0; p < loops; p++) {
			
			dw = new Matrix[layers-1];
			dw = startW(input,hidden,output,layers,dw,false);
			
			for(int z = 0; z < tLen; z++) {
				tInput = new Matrix(tInputData[z]);
				tOutput = new Matrix(tOutputData[z]);
				
				//start forward pass
				n = new Matrix[layers];
				nd = new Matrix[layers];
				n = startN(input,hidden,output,layers,n);
				nd = startN(input,hidden,output,layers,nd);
				n[0] = tInput;
				n[0].addBias(1);
				for(int i = 0; i < layers-1; i++) {
					n[i+1] = n[i].dot(w[i]);
					n[i+1].sigmoid();
					n[i+1].addBias(1);
				}
				n[layers-1] = n[layers-2].dot(w[layers-2]);
				n[layers-1].sigmoid();
				//end forward pass
				
				//System.out.print("The answer for " + tInput.getMatrix() + "is " + n[layers-1].getMatrix());
				//System.out.println("Expected: " + tOutput.getMatrix());

				for(int l = nd.length-1; l > 0; l--) {
					//nd[l-1].printMatrix();
					for(int i = 0; i < nd[l].matrix[0].length;i++) { //problem with longer hidden values are in this block
						if (l == nd.length - 1) {	
							nd[l].matrix[0][i] = (n[l].matrix[0][i] * ( 1 - n[l].matrix[0][i])) * (n[l].matrix[0][i] - tOutput.matrix[0][i]);
						}
						else {
							for (int j = 0; j < nd[l+1].matrix[0].length; j++) {
								//System.out.println(l + " " + i + " " + j + " " + w[l].matrix[i][j]);
								nd[l].matrix[0][i] += w[l].matrix[i][j] * nd[l+1].matrix[0][j];
							}
							nd[l].matrix[0][i] = nd[l].matrix[0][i] * (n[l].matrix[0][i] * (1 - n[l].matrix[0][i])); //sig
						}		
					}
				}
				//finding the derivative of the weights based off of the node derivatives
				for (int l = 0; l < w.length; l++) {
					for (int i = 0; i < w[l].matrix.length; i++) {
						for(int j = 0; j < w[l].matrix[0].length; j++) {
							dw[l].matrix[i][j] += nd[l+1].matrix[0][j] * n[l].matrix[0][i];
						}
					}
				}
			}
			//applying the added derivatives of the weights to the weights
			for (int l = 0; l < w.length; l++) {
				for (int i = 0; i < w[l].matrix.length; i++) {
					for(int j = 0; j < w[l].matrix[0].length; j++) {
						w[l].matrix[i][j] -= dw[l].matrix[i][j] * learningRate;
					}
				}
			}
			//w[0].printMatrix();
			//w[1].printMatrix();
		}
		
		//running tests
		tInput.matrix[0] = parse.parse("ciao",15);
		n = new Matrix[layers];
		nd = new Matrix[layers];
		n = startN(input,hidden,output,layers,n);
		nd = startN(input,hidden,output,layers,nd);
		n[0] = tInput;
		n[0].addBias(1);
		for(int i = 0; i < layers-1; i++) {
			n[i+1] = n[i].dot(w[i]);
			n[i+1].sigmoid();
			n[i+1].addBias(1);
		}
		n[layers-1] = n[layers-2].dot(w[layers-2]);
		n[layers-1].sigmoid();
		//end forward pass
		
		System.out.print("The answer for " + tInput.getMatrix() + "is " + n[layers-1].getMatrix());
		System.out.println("Expected: " + tOutput.getMatrix());
		
		
	}
	

}







































