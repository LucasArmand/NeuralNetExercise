import java.util.Random;

public class Matrix {
	public double[][] matrix;
	Matrix(int rows, int columns) {
		matrix = new double[rows][columns];
	}
	Matrix(double[] data) {
		matrix = new double[1][data.length];
		for(int i = 0; i < data.length; i++) {
			matrix[0][i] = data[i];
		}
	}
	
	Matrix (Matrix m) {
		matrix = new double[m.matrix.length][m.matrix[0].length];
	}
	
	public String getMatrix() {
		String output = "";
		for (double[] x:matrix) {
			output += "[ ";
			for(double y:x){
				output += y + " ";
			}
		}
		return (output + "]\n");
	}
	
	public String toString() {
		String out = "<html>";
		for (double[] x:matrix) {
			out += ("[ ");
		for(double y:x){
			out += (y + " ");
		}
		out += ("]<br/>");
		}
		return out + "</html>";
	}
	
	public void printMatrix() {
		for (double[] x:matrix) {
			System.out.print("[ ");
		for(double y:x){
			System.out.print(y + " ");
		}
		System.out.print("]\n");
		}
	}
	
	public void addBias(double b) {
		double[][] augmented = new double[1][matrix[0].length + 1];
		for (int i = 0; i < matrix[0].length;i++) {
			augmented[0][i] = matrix[0][i];
		}
		augmented[0][matrix[0].length] = b;
		matrix = augmented;
	}
	
	public Matrix square() {
		Matrix m = new Matrix(this);
		for (int i = 0; i < m.matrix.length; i++) {
			for (int j = 0; j < m.matrix[0].length; j++) {
				m.matrix[i][j] = m.matrix[i][j] * m.matrix[i][j];
			}
		}
		return m;
	}
	
	public void sigmoid() {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] = (1.0 / (1 + Math.pow(Math.E,-1 * matrix[i][j])));
			}
		}
	}
	
	public Matrix sub (Matrix b) {
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
	
	public void add(double b) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] += b;
			}
		}
	}
	
	public void fill (double a){
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] = a;
			}
		}
	}
	public void singleMult (double a){
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] *= a;
			}
		}
	}
	
	public void populate (){
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] = (double)(new Random().nextInt(201) - 100) / 100;
			}
		}
	}
	
	public Matrix dot (Matrix other){
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
	
	public double average() {
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


