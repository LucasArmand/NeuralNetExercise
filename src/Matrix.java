import java.util.Random;

public class Matrix { //a special Matrix class, that holds a 2-D array of doubles, allows for many different methods
	public double[][] matrix;
 
	Matrix(int rows, int columns) {//new matrix with rows rows and columns columns
		matrix = new double[rows][columns];
	}
	Matrix(double[] data) { //new matrix based off of 1-D double array
		matrix = new double[1][data.length];
		for(int i = 0; i < data.length; i++) {
			matrix[0][i] = data[i];
		}
	}
	Matrix (double[][] m){ //new matrix based off of 2-D double array
		matrix = m;
	}
	Matrix (Matrix m) { //constructs the matrix based on another matrix's dimensions
		matrix = new double[m.matrix.length][m.matrix[0].length];
	}

	
	public String toString() { //gives the matrix as a string
		String out = "";
		for (double[] x:matrix) {
			out += ("[ ");
		for(double y:x){
			out += y + ", ";
		}
		out += ("]\n");
		}
		return out ;
	}
	
	
	public Matrix ReLU() { //applies the ReLU function to every value in this matrix (all negative values go to 0)
		double[][] temp = matrix;
		for (int i = 0; i < temp.length; i++) {
			for (int j = 0; j < temp[0].length; j++) {
				temp[i][j] = Math.max(temp[i][j], 0);
			}
		}
		return new Matrix(temp);
	}
	
	public double get(int x, int y) { //returns the value of this matrix at [x][y]
		return matrix[x][y];
	}
	public void add(Matrix m) { //adds each element
		if(m.matrix.length == matrix.length && m.matrix[0].length == matrix[0].length) {
			for(int i = 0; i < matrix.length; i++) {
				for(int j = 0; j < matrix[0].length;j++) {
					matrix[i][j] += m.matrix[i][j];
				}
			}
		}
	}
	public void addBias(double b) { //adds a new element to the matrix, the bias term
		double[][] augmented = new double[1][matrix[0].length + 1];
		for (int i = 0; i < matrix[0].length;i++) {
			augmented[0][i] = matrix[0][i];
		}
		augmented[0][matrix[0].length] = b;
		matrix = augmented;
	}
	
	public Matrix square() { //squares each term in this matrix
		Matrix m = new Matrix(this);
		for (int i = 0; i < m.matrix.length; i++) {
			for (int j = 0; j < m.matrix[0].length; j++) {
				m.matrix[i][j] = m.matrix[i][j] * m.matrix[i][j];
			}
		}
		return m;
	}
	
	public void sigmoid() { //applies the sigmoid function to every element in this matrix
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] = (1.0 / (1 + Math.pow(Math.E,-1 * matrix[i][j])));
			}
		}
	}
	
	public Matrix sub (Matrix b) { //subtracts each element from this matrix with each element in b
		if (matrix.length == b.matrix.length && matrix[0].length == b.matrix[0].length) {
			Matrix result = new Matrix(matrix.length,matrix[0].length);
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[0].length; j++) {
					result.matrix[i][j] = matrix[i][j] - b.matrix[i][j];
				}
			}
			return result;
		}else {
			return new Matrix(0,0);
		}
	}
	
	public void add(double b) { //adds b to every value in the matrix
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] += b;
			}
		}
	}
	
	public void fill (double a){ //fills the matrix with a
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] = a;
			}
		}
	}
	public Matrix singleMult (double a){ //multiplies every element in the matrix by a scalar a
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] *= a;
			}
		}
		return this;
	}
	
	public void populate (){ //fills the matrix with random values
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] = (double)(new Random().nextInt(201) - 100) / 100;
			}
		}
	}
	
	public Matrix dot (Matrix other){ //multiplies the two matrices together
		double[][] b = other.matrix;
		double[][] a = matrix;
		if (a[0].length == b.length) {
			int outA = a.length; //num rows in A
			int outB = b[0].length; //num columns in B
			int commonLength = a[0].length;
			Matrix product = new Matrix(outA,outB);
			for (int i = 0; i < outA;i++) { //for each row in matrix a
				for (int j = 0; j < outB;j++) { //for each column in matrix b
					for (int k = 0; k < commonLength; k++) { //going down row i in matrix a and column j in matrix b
						product.matrix[i][j] += a[i][k] * b[k][j];
					}
				}
			}
			return product;
		}
		else {
			System.out.println("Attempted multiplication without matching rows/columns");
			return new Matrix(0,0);
		}
	}
	
	public double average() { //average value across the entire matrix
		double sum = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				sum += matrix[i][j];
			}
		}
		return sum / (matrix.length * matrix[0].length);
	}
			
	/*
	 * [vertical down][horizontal right]
	 * [-y][x]
	 * [row][column]
	 * Reference 3x3 matrix
	 *        #0     #1    #2
	 * #0 [ [0][0],[0][1],[0][2] ]
	 * #1 [ [1][0],[1][1],[1][2] ]
	 * #2 [ [2][0],[2][1],[2][2] ]
	 */
	
}


