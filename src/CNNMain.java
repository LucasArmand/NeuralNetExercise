import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CNNMain {
	
	static int fcinput; //number of fully connected input neurons
	static int hidden; //number of fully connected hidden neurons
	static int output; //number of fully connected output neurons
	static int fclayers; //number of fully connected layers
	static int loops; //number of loops to run the training data
	static int convLayers; //number of convolutional layers
	static double learningRate; //the multiplier that controls the rate and precision of the back propagation
	static double[][] tInputData;  //training data array for inputs
	static double[][] tOutputData; //traning data array for expected outputs
	static double[][] avgErr; //array for storing the average error of the trial
	
	static Filter[][][] filters; //Filter array for all of the filters in the convolutional part of the network
	static Matrix tInput; //matrix for the input data
	static Matrix tOutput; //matrix for the output data
	static Matrix[] n; // fully connected neuron array
	static Matrix[] nd; //fully connected neuron derivatives array
	static Matrix[] w; //fully connected weights
	static Matrix[] dw; //fully connected weights derivatives
	static int sf; //scale factor for image rendering
	
	static boolean mouseDown; //is true when the mouse is being pressed down
	static int numFilters; //number of filters in the convolutional network
	
	public static int next(String str,int last,char c) { //helper method for getting data from the storage text files, returns the position of the next char c after last
		for(int i = last; i < str.length(); i++) {
			if(str.charAt(i) == c) {
				return i;
			}
		}
		return -1;
	}
	
	public static double sigmoid(double a) { //applies the sigmoid activation function to a
		return(1.0 / (1 + Math.pow(Math.E,-1 * a)));
	}
	
	public static Matrix[] startW(int input ,int hidden,int output,int layers, Matrix[] m, boolean pop) { //initializes the weight matrices for the fully connected weights, pop tells whether or not to populate them with random values
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
	
	public static Matrix[] startN(int input ,int hidden,int output,int layers, Matrix[] n) { //initializes the neuron matrices for the fully connected neurons, given input size, hidden size, and output size, as well as layer count
		n[0] = new Matrix(1,input);
		n[layers-1] = new Matrix(1,output);
		for (int i = 0; i < layers;i++) {
			if ( i > 0 && i < layers - 1) {
				n[i] = new Matrix(1,hidden);
			}
		}
		return n;
	}
	
	public static int testNetwork(BufferedImage inputImage) { //runs the forward pass and returns the resulting number, this code is explained in more detail in the training section of the program
	
		Pool[][] poolHelpers = new Pool[convLayers][];
		Matrix[][] layers = new Matrix[convLayers][];
		Matrix[][] poolLayers = new Matrix[convLayers][];

		int depth = 1;
		for(int l = 0; l < convLayers; l++) {
			depth = (int)Math.pow(numFilters, l); //depth is equal to the number of conv + pool layers in the last layer


			layers[l] = new Matrix[depth * numFilters]; //sets new conv layer length to have one slot for every sub-layer in each filter connecting to it
				if(l == 0) {;
					for(int i = 0; i < numFilters; i++) {
						layers[l][i] = filters[l][i][0].evaluate(new ImageMatrix(inputImage)).ReLU(); //evaluating initial layer with depth of 1
					}
				}else {
					for(int k = 0; k < numFilters; k++) { //number of filter
						for(int j = 0; j < depth; j++) { //depth of last layer
							layers[l][k * depth + j] = filters[l][k][j].evaluate(poolLayers[l-1][j]).ReLU();
						}
					}
				}
			
			
			poolLayers[l] = new Matrix[depth * numFilters];
			poolHelpers[l] = new Pool[depth * numFilters];
			for(int i = 0; i < poolLayers[l].length; i++) {
				poolHelpers[l][i] = new Pool(2,2,2);
				poolLayers[l][i] = poolHelpers[l][i].pool(layers[l][i]);
			}

		}
		
		int finalX = poolLayers[convLayers-1][0].matrix.length;
		int finalY = poolLayers[convLayers-1][0].matrix[0].length;
		Matrix fcInput = new Matrix(1,finalX * finalY * depth * numFilters); //setting the size of the fcInput

		for(int i = 0; i < depth*numFilters; i++) {
			for(int j = 0; j < finalX; j++) {
				for(int k = 0; k < finalY; k++) {
					fcInput.matrix[0][(i * finalX * finalY) + (j * finalY) + k] = poolLayers[convLayers -1][i].matrix[j][k]; //flattening last pool layer to one long matrix
				}
			}
		}

		
		tInput = fcInput;
		n = new Matrix[fclayers];
		n = startN(fcinput,hidden,output,fclayers,n);
		n[0] = tInput;
		n[0].addBias(1);
		for(int i = 0; i < fclayers-1; i++) {
			//System.out.println(i + " running");
			n[i+1] = n[i].dot(w[i]);
			n[i+1].sigmoid();
			n[i+1].addBias(1);
		}
		n[fclayers-1] = n[fclayers-2].dot(w[fclayers-2]);
		n[fclayers-1].sigmoid();

		int choice = 0;
		double last = n[fclayers-1].matrix[0][0];
		for(int i = 0; i < n[fclayers-1].matrix[0].length; i++) {
			if( n[fclayers-1].matrix[0][i] > last) {
				last = n[fclayers-1].matrix[0][i];
				choice = i; //picking the greatest output, indicates the highest probability of that number being the one drawn, returns the highest
			}
		}
		return choice;

	}
	
	public static void loadNetwork(String name) throws NumberFormatException, IOException { //loading the network from the text file
		File inputFile = new File("C:\\Users\\lucas_000\\Desktop\\"+ name +".txt");
		BufferedReader reader = new BufferedReader(new FileReader(inputFile)); //this is used to read the file
		String line;
		boolean weightMode = false; //determins if we are reading fc weights or filter weights
		int readIndexX = 0; //indices for keeping track of where we are in the new arrays
		int readIndexY = 0;
		ArrayList<Matrix> tempW = new ArrayList<Matrix>(); //temporary array lists that will be turned into arrays later
		ArrayList<double[][]> tempB = new ArrayList<double[][]>();
		ArrayList<Matrix[][]> tempFilters = new ArrayList<Matrix[][]>();
		int curW = -1;
		int curDepth = 1;
		int filterI = 0;
		int filterJ = 0;
		while((line = reader.readLine()) != null) {
			if(line.charAt(0) == 'h') {
				hidden = Integer.parseInt(line.substring(1)); //checking for information in the file about the hidden variable
			}
			if(line.charAt(0) == 'd') {
				fclayers = Integer.parseInt(line.substring(1)); //checking for information in the file about the fclayers variable
			}

			if(weightMode) { //checks if we are still reading weights, if so, change the indices to reflect the new line
				readIndexX++;
				readIndexY=0;
			}
			if(line.charAt(0) == '<') { //checks for information about the fc weights held in <>
				weightMode = false;
				tempW.add(new Matrix(Integer.parseInt(line.substring(1,next(line,1,','))),Integer.parseInt(line.substring(next(line,1,',')+1,line.length() - 1))));
				curW++;
			}
			int last = 0;
			if(line.charAt(1) =='w') { //signals the start of a new fc weight matrix
				readIndexX = 0;
				last = 5;
				weightMode = true;
				
			}
			
			while(weightMode && next(line,last,',') != -1) {
				tempW.get(curW).matrix[readIndexX][readIndexY] = Double.parseDouble(line.substring(last,next(line,last,','))); //stores the weight value from the text file into the temporary array list
				
				last = next(line,last,',') + 1;
				readIndexY++;
			}
			if(line.charAt(0) == '{') { // the { symbol signifies the end of the weight matrix data and the start of the filter data
				weightMode = false;
				numFilters = Integer.parseInt(line.substring(1,line.length()-1));
			}
			if(line.charAt(1) =='f') {//checking for new filter line in text file
				
				int first = next(line,1,'[');
				filterI = Integer.parseInt(line.substring(first + 1,next(line,first,']'))); //identifying the values of the stored indices
				int second = next(line,first + 1,'[');
				filterJ = Integer.parseInt(line.substring(second + 1,next(line + 1,second,']')));
				int third = next(line,second + 1,'[');
				double bias = Double.parseDouble(line.substring(third + 1,next(line + 1,third,']')));
				if(Integer.parseInt(line.substring(2,3)) == tempFilters.size()) {
					tempFilters.add(new Matrix[numFilters][curDepth]); //initializing the new filter matrices
					tempB.add(new double[numFilters][curDepth]); //initializing the new bias matrices
					curDepth *= numFilters; //changing the curDepth to reflect the change in filter size
				}
				int x = 0;
				int y = 0;
				last = next(line,0,':');
				System.out.println(filterI +", " + filterJ);
				tempFilters.get(tempFilters.size()-1)[filterI][filterJ] = new Matrix(3,3);
				tempB.get(tempFilters.size()-1)[filterI][filterJ] = bias;
				while(next(line,last,',') != -1) {
					tempFilters.get(tempFilters.size()-1)[filterI][filterJ].matrix[x][y] = Double.parseDouble(line.substring(last + 1,next(line,last + 1,','))); //storing the data into the temporary filter array list
					y++;
					last = next(line,last + 1,',');
					if(line.charAt(last + 1) == '|') { //checking for |, which indicates a new row in the filter
						last += 1;
						y = 0;
						x++;
					}
				}		
			}
		}
		w = new Matrix[tempW.size()];
		for(int i = 0; i < tempW.size(); i++) {
			w[i] = tempW.get(i); //setting the static w array to the temporary 
		}
		filters = new Filter[tempFilters.size()][][];
		for(int i = 0; i < tempFilters.size();i++) {
			filters[i] = new Filter[numFilters][];
			for(int j = 0; j < numFilters; j++) {
				filters[i][j] = new Filter[(int)Math.pow(numFilters, i)];
				for(int k = 0; k < Math.pow(numFilters, i);k++){
					filters[i][j][k] = new Filter(3,3,1); //setting the static filter array to the values stored in the temporary
					filters[i][j][k].bias = tempB.get(i)[j][k];
					filters[i][j][k].matrix = tempFilters.get(i)[j][k].matrix;
				}
			}
		}
		reader.close();
	}
	
	public static void main(String[] args) throws IOException { //the main method

		//the following are hyper-parameters. They are manually set factors about the convolutional neural network that determine it's capabilities. Different configurations of these can result in better or worse models
		convLayers = 3; //number of convolutional layers
		int depth = 1; //current depth of the filter space
		numFilters = 10; //number of filters per layer
		int numTrainingSets = 10; //number of training data answers (numbers 0 through 9)
		int samplesPerSet = 22; //number of training examples per set
		int loops = 300000; //number of loops to train the CNN for (CNN = convolutional neural network)
		double learningRate = .005; //multiplier that determines the accuracy and speed at which the network trains (higher = more speed, less accuracy)
		fclayers = 6; //number of fully connected layers in the fully connected neural network
		hidden = 200; //number of hidden neurons per layer in the fully connected network (hidden neurons are all the neurons that aren't the input or output neurons)
		
		BufferedImage[][] numbers = new BufferedImage[numTrainingSets][samplesPerSet]; //this stores the image data for the training set
		boolean doTrain = false; //a boolean for manual control of the program, whether it should train or load a old model. For the example you are seeing, this will be set to false, as training takes a long time and has no visual aspects
		ImageMatrix[] input = new ImageMatrix[numTrainingSets * samplesPerSet]; //flattens the numbers array
		tOutputData = new double[numTrainingSets*samplesPerSet][numTrainingSets]; //expected output data for each training example
		if(doTrain) {
			for(int i = 0; i < numTrainingSets; i++) { //this set of loops goes through each of the training data sets in their file (on my computer at home) and stores them into the the training data set
				for(int j = 0; j < samplesPerSet; j++) {
					BufferedImage color = ImageIO.read(new File("C:\\Users\\Lucas\\Desktop\\images\\" + (i) + "\\"+i+" ("+(j + 1)+").png"));
					numbers[i][j] = new BufferedImage(16,16,BufferedImage.TYPE_BYTE_GRAY);
					Graphics g = numbers[i][j].getGraphics();
					g.drawImage(color, 0, 0, null);
					g.dispose();
					input[j * numTrainingSets + i] = new ImageMatrix(numbers[i][j]);
					tOutputData[j * numTrainingSets + i][i] = 1; // the training example for the number 4, for example, is {0,0,0,0,1,0,0,0,0,0}, as the 1 is in the 5th index, which corresponds to the number 4 training data
				}
			}
		}
		
		int sf = 10;

		//initializing the arrays for the convolutional part of the network
		filters = new Filter[convLayers][][];
		Pool[][] poolHelpers = new Pool[convLayers][];
		Matrix[][] layers = new Matrix[convLayers][];
		Matrix[][] poolLayers = new Matrix[convLayers][];
		Matrix[][] reluLayers = new Matrix[convLayers][];
		
		int t; //current training set
		
		//begin forward pass
		if(doTrain) { 
			for(int p = 0; p < loops; p++) { //looping "loops" times
				
				t=p%(numTrainingSets * samplesPerSet); //setting the current training example by taking the modulus of the loops with the total number of training examples
				for(int l = 0; l < convLayers; l++) {
					depth = (int)Math.pow(numFilters, l); //depth is equal to the number of conv + pool layers in the last layer
	
					if(p == 0) {
						filters[l] = new Filter[numFilters][depth]; //setting up current filter layer (which filter) - (where it is convolving in the last layer)
						for(int i = 0; i < numFilters; i++) {
							for(int j = 0; j < depth; j++) {
								filters[l][i][j] = new Filter(3,3,1); //creating the i-th filter in layer l that reads from the poolLayers[l-1][j]
								filters[l][i][j].populate(); //randomly filling layers
							}
						}
					}
					layers[l] = new Matrix[depth * numFilters];
					reluLayers[l] = new Matrix[depth * numFilters];//sets new conv layer length to have one slot for every sub-layer in each filter connecting to it
						if(l == 0) {;
							for(int i = 0; i < numFilters; i++) {
								reluLayers[l][i] = filters[l][i][0].evaluate(input[t]); //evaluating initial layer with depth of 1, hence last array always equaling 0
								layers[l][i] = reluLayers[l][i].ReLU(); //apply the ReLU function (all negatives go to 0)
							}
						}else {
							for(int k = 0; k < numFilters; k++) { //number of filter
								for(int j = 0; j < depth; j++) { //depth of last layer
									reluLayers[l][k * depth + j] = filters[l][k][j].evaluate(poolLayers[l-1][j]); // evaluation each poolLayer of the previous layer with the corresponding filter
									layers[l][k * depth + j] = reluLayers[l][k * depth + j].ReLU(); //apply the ReLU function (all negatives go to 0)
								}
							}
						}
					
					
					poolLayers[l] = new Matrix[depth * numFilters];
					poolHelpers[l] = new Pool[depth * numFilters];
					for(int i = 0; i < poolLayers[l].length; i++) {
						poolHelpers[l][i] = new Pool(2,2,2); // 2 x 2 area, moving 2 pixels per step
						poolLayers[l][i] = poolHelpers[l][i].pool(layers[l][i]); //pooling (taking the max out of a moving 2x2 tile) the layer
					}
	
				}
				
				int finalX = poolLayers[convLayers-1][0].matrix.length;
				int finalY = poolLayers[convLayers-1][0].matrix[0].length;
				Matrix fcInput = new Matrix(1,finalX * finalY * depth * numFilters); //setting the size of the fcInput: fcInput is the flattened (only one long row) version of the final poolLayers
	
				for(int i = 0; i < depth*numFilters; i++) {
					for(int j = 0; j < finalX; j++) {
						for(int k = 0; k < finalY; k++) {
							fcInput.matrix[0][(i * finalX * finalY) + (j * finalY) + k] = poolLayers[convLayers -1][i].matrix[j][k]; // flattens the final pooling layer
						}
					}
				}
				
				
				fcinput = fcInput.matrix[0].length;
				
				output = numTrainingSets;
				tInputData = fcInput.matrix;
				
				tInput = new Matrix(1,tInputData[0].length);
				tInput = fcInput;
				tOutput = new Matrix(1,tOutputData.length);
				
				tOutput.matrix[0] = tOutputData[t];
				avgErr = new double[tOutputData.length][output];
				
				n = new Matrix[fclayers]; 
				nd = new Matrix[fclayers];
				dw = new Matrix[fclayers-1];
				
				
				n = startN(fcinput,hidden,output,fclayers,n); //initialzing the arrays for the fully connected forward pass using the helper methods
				nd = startN(fcinput,hidden,output,fclayers,nd);
				if(p==0) {
					w = new Matrix[fclayers-1];
					w = startW(fcinput,hidden,output,fclayers,w,true); //only sets the weights to random values if it's running for the first time (we wouldn't want to override the training that we did in the last loop)
				}
				dw = startW(fcinput,hidden,output,fclayers,dw,false);
	
				n[0] = tInput;
				n[0].addBias(1); //adds a bias to the neuron matrix
	
				for(int i = 0; i < fclayers-1; i++) { //this loop here controls the entire forward pass for the fully connected network
					n[i+1] = n[i].dot(w[i]); //multiplication (matrix multiplication simplifies the overall problem)
					n[i+1].sigmoid(); //activation function
					n[i+1].addBias(1);
				}
				n[fclayers-1] = n[fclayers-2].dot(w[fclayers-2]);
				n[fclayers-1].sigmoid(); //final step and activation
				
				
				for(int x = 0; x < output; x++) {
					avgErr[t][x] = Math.pow(tOutputData[t][x] - n[fclayers-1].matrix[0][x], 2); //calculating the error for this step, squared
				}
				
				double sum = 0;
				for (int i = 0; i < avgErr.length; i++) {
					for (int j = 0; j < avgErr[0].length; j++) {
						sum += avgErr[i][j];
					}
				}
				if(p%1000 == 0) {
					System.out.println("Final:\n" + n[fclayers-1]);
					System.out.println("expected:\n" + tOutput);
					System.out.println("Average squared error for loop " + p + ": " + sum / (avgErr.length * avgErr[0].length)); //printing information every 1000 loops while training, reporting the error and the actual final values
				}
				
				//end forward pass
				
				//being back propagate
				for(int l = nd.length-1; l >= 0; l--) {
					for(int i = 0; i < nd[l].matrix[0].length;i++) {
						if (l == nd.length - 1) {	
							nd[l].matrix[0][i] = (n[l].matrix[0][i] * ( 1 - n[l].matrix[0][i])) * (n[l].matrix[0][i] - tOutput.matrix[0][i]); //calculating the derivative of each neuron in the last fc layer with respect to the error
						}
						else if (l > 0){
							for (int j = 0; j < nd[l+1].matrix[0].length; j++) {
								nd[l].matrix[0][i] += w[l].matrix[i][j] * nd[l+1].matrix[0][j]; //calculating the derivative of each neuron with respect to the neurons they connect to in the next layer
							}
							nd[l].matrix[0][i] = nd[l].matrix[0][i] * (n[l].matrix[0][i] * (1 - n[l].matrix[0][i])); // sigmoid function derivative (f(x) * (1-f(x))
						}else {
							for (int j = 0; j < nd[l+1].matrix[0].length; j++) {
								nd[l].matrix[0][i] += w[l].matrix[i][j] * nd[l+1].matrix[0][j]; //calculating the derivative of each neuron in the first layer with respect to the neurons in the next layer
							}
						}	
					}
				}
				
				for (int l = 0; l < w.length; l++) {
					for (int i = 0; i < w[l].matrix.length; i++) {
						for(int j = 0; j < w[l].matrix[0].length; j++) {
							dw[l].matrix[i][j] += nd[l+1].matrix[0][j] * n[l].matrix[0][i]; //finding the derivative of the weights based off of the node derivatives
						}
					}
				}
				for (int l = 0; l < w.length; l++) {
					for (int i = 0; i < w[l].matrix.length; i++) {
						for(int j = 0; j < w[l].matrix[0].length; j++) {
							w[l].matrix[i][j] -= dw[l].matrix[i][j] * learningRate; //moving down the gradient, changing the weights by their derivative with respect to the error, causing the error to go down
						}
					}
				}
				
				//initialization of the convolutional network derivatives
				Matrix[][][] dFilters = new Matrix[filters.length][][];
				Matrix[][] dLayers = new Matrix[layers.length][];
				Matrix[][] dPoolLayers = new Matrix[poolLayers.length][];
				
				// ConvNet back propagation
				for(int l = convLayers - 1; l >= 0; l--) {
					dPoolLayers[l] = new Matrix[depth * numFilters];
					if(l == convLayers - 1) { //check for conv last layer (happens first)
						for(int i = 0; i < depth * numFilters; i++) {
							dPoolLayers[l][i] = new Matrix(finalX,finalY);
							for(int j = 0; j < finalX; j++) {
								for(int k = 0; k < finalY; k++) {
									//fcInput.matrix[0][(i * finalX * finalY) + (j * finalY) + k] = poolLayers[convLayers -1][i].matrix[j][k]; this is how we got here, we are doing the reverse
									dPoolLayers[l][i].matrix[j][k] = nd[0].matrix[0][(i * finalX * finalY) + (j * finalY) + k]; // unflattens the derivative of the first fc layer, opposite of flattening
								}
							}
						}
					}
					else { //if not the last conv layer
						for(int i = 0; i < depth * numFilters; i++) { //depth of this layer
							for(int j = 0; j < numFilters; j++) { //number of filters
									dPoolLayers[l][i] = new Matrix(dPoolLayers[l+1][0].matrix.length * 2, dPoolLayers[l+1][0].matrix[0].length * 2); //setting up the pooling matrix size
									dPoolLayers[l][i].add(filters[l+1][j][i].reverseConvolve(layers[l+1][j * depth * numFilters + i])); //reversing the convolution function to move the derivatives across the filters in reverse
							}
						}
					}
	
					//dPoolLayers to dLayers
					dLayers[l] = new Matrix[depth * numFilters];
					for(int i = 0; i < depth * numFilters; i++) {
						dLayers[l][i] = new Matrix(layers[l][i].matrix.length,layers[l][i].matrix[0].length);
						for(int x = 0; x < poolHelpers[l][i].xPositions.matrix.length; x++) {
							for(int y = 0; y < poolHelpers[l][i].yPositions.matrix[0].length; y++) {
								//recalling which values we actually used from the poolHelpers, and then applying the derivatives to those values
								dLayers[l][i].matrix[(int)poolHelpers[l][i].xPositions.matrix[x][y]][(int)poolHelpers[l][i].yPositions.matrix[x][y]] = dPoolLayers[l][i].matrix[x][y]; 
							}
						}
	
	
					}
					
					dFilters[l] = new Matrix[numFilters][depth]; //initializing the derivative of the filters for this layer
					if(l > 0) {
						for(int k = 0; k < numFilters; k++) { //number of filter
							for(int j = 0; j < depth ; j++) { //depth of last layer
								dFilters[l][k][j] = new Matrix(filters[l][k][j].matrix.length,filters[l][k][j].matrix[0].length);
								dFilters[l][k][j].add(filters[l][k][j].getDF(poolLayers[l-1][j], dLayers[l][k * depth + j])); //calculating the derivative of the filter[l][k][j] using a method in the Filter class
							}
						}
					}else {
						for(int k = 0; k < numFilters; k++) {
							dFilters[0][k][0] = new Matrix(filters[0][k][0].matrix.length,filters[0][k][0].matrix[0].length);
							dFilters[0][k][0] = filters[0][k][0].getDF(input[t], dLayers[l][k]); //using the input layer as the previous layer for the first convolutional layer
						}
					}		
					
					depth = depth / (numFilters); //changing the depth in the opposite way we did it in the forward pass
				}
				
				
				for(int l = 0; l < convLayers; l++) {
					for(int i = 0; i < dFilters[l].length; i++) {
						for(int j = 0; j < dFilters[l][i].length;j++) {
							for(int x = 0; x < dFilters[l][i][j].matrix.length; x++) {
								for(int y = 0; y < dFilters[l][i][j].matrix[0].length; y++) {
									filters[l][i][j].matrix[x][y] -= dFilters[l][i][j].matrix[x][y] * learningRate; //subtracting the derivatives of the filters to move down the gradient, lowering the error
								}
							}
						}
					}
				}
			}
		}
		
		if(!doTrain) {
			loadNetwork("model"); //loading the weights instead from "model.txt" instead of training new weights
		}

		
		if(doTrain) {
			BufferedWriter file = new BufferedWriter(new FileWriter(Double.toString(System.currentTimeMillis() % 10000) +".txt")); //makes the new file based on a random number (current time)
			file.write("h" + hidden); //stores hidden variable
			file.newLine();
			file.write("d"+fclayers); //stores fclayers variable
			file.newLine();
			for(int i = 0; i < w.length; i++) {
				file.write("<"+w[i].matrix.length +","+w[i].matrix[0].length+">"); //weight matrix header
				file.newLine();
				file.write("[w" + i + "]:");//weight matrix sub-header
				file.flush();
				for(int j = 0; j < w[i].matrix.length;j++) {
					for(int k = 0; k < w[i].matrix[0].length; k++) {
					file.write(Double.toString(w[i].matrix[j][k])+","); //actual weight matrix data
					file.flush();
				}
				file.newLine();
				file.flush();
				}
			}
			file.write("{"+numFilters+"}"); //filter matrix header
			for(int i = 0; i < filters.length; i++) {
				for(int j = 0; j < filters[i].length; j++) {
					for(int k = 0; k < filters[i][j].length; k++) {
						file.newLine();
						file.write("[f" + i+"][" + j+ "]["+ k+ "]["+filters[i][j][k].bias+"]:"); //filter matrix sub header
						file.flush();
						for(int x = 0; x < filters[i][j][k].matrix.length; x++) {
							for(int y = 0; y < filters[i][j][k].matrix[0].length; y++) {
								file.write(filters[i][j][k].matrix[x][y] +",");//actual filter matrix data
								file.flush();
							}
							file.write("|"); //separator for filter matrix
						}
					}
				}
			}
			file.flush();
			file.close();
		}
		CNNFrame frame = new CNNFrame("Convolutional Neural Network"); //creates the new GUI frame
		frame.setVisible(true); //makes the new GUI frame visible
		frame.addMouseListener(new MouseListener() { //listener for mouse information
			public void mouseClicked(MouseEvent arg0) {}

			public void mouseEntered(MouseEvent arg0) {}

			public void mouseExited(MouseEvent arg0) {
			}
			public void mousePressed(MouseEvent arg0) {
				mouseDown = true;	
			}
			public void mouseReleased(MouseEvent arg0) {
				mouseDown = false;
			}
		});
		
		Matrix drawnImage = new Matrix(16,16); //new drawing matrix
		drawnImage.fill(255); //setting every pixel in the matrix to white
		
		sf = 25; //setting the scale factor for the image to 25 times larger than 16 x 16
		
		Timer drawingTimer = new Timer(); //new timer for reading the mouse movements
		ImagePane drawRender = new ImagePane(ImageMatrix.fromMatrix(drawnImage),sf); //GUI component that holds the drawing image
		drawRender.setBounds(25,275,401,401);
		frame.add(drawRender);
		
		
		JButton clear = new JButton("Clear"); //adding a button that clears the drawing
		clear.setBounds(450,350,100,50);
		frame.add(clear);
		clear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				drawnImage.fill(255);
				drawRender.setImage(ImageMatrix.fromMatrix(drawnImage));
				frame.repaint();
			}
		});
		
		JLabel resultLabel = new JLabel(""); //the text field that shows the answer given by the CNN
		resultLabel.setBounds(525,300,400,400);
		resultLabel.setHorizontalAlignment(JLabel.CENTER);
		resultLabel.setFont(new Font("calibri",Font.PLAIN,400));
		frame.add(resultLabel);
		
		JButton pass = new JButton("Pass");
		pass.setBounds(450,550,100,50);
		frame.add(pass);
		pass.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				resultLabel.setText(Integer.toString(testNetwork(drawRender.img))); //sets the text in the text field to the result given by the current CNN
			}
		});
		
		TimerTask drawingTask = new TimerTask() { //every 5 milliseconds, check if the position of the mouse is over the drawing, and if it is clicking, draw on that pixel
			public void run() {
				if(mouseDown) {
					if(frame.getMousePosition() != null) {
						try {
							int sf = 25;
							int mouseX = (int)(frame.getMousePosition().getX()/sf - 1.3);
							int mouseY = (int)(frame.getMousePosition().getY()/sf - 11.9);
							if(mouseX < drawnImage.matrix.length && mouseX >= 0 && mouseY < drawnImage.matrix[0].length && mouseY >= 0) {
								if(drawnImage.matrix[mouseX][mouseY] == 255) {
									drawnImage.matrix[mouseX][mouseY] = 0;
									drawRender.setImage(ImageMatrix.fromMatrix(drawnImage));
									frame.repaint();
								}
							}
						}catch(NullPointerException e) {}
					}
				}
			}
		};
		
		drawingTimer.schedule(drawingTask, 1,5); //set the drawing timer to run

		
		}
	
	}

