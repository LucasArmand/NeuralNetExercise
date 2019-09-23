import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class Filter extends Matrix{

	private int x; //half r
	private int y; //half c
	private int r; //rows
	private int c; //columns
	private int s; //stride
	public double bias = 0;
	Filter(int rows, int columns, int stride) {
		super(rows, columns);
		r = rows;
		c = columns;
		x = (rows / 2); // = 1 for rows = 3
		y = (columns / 2); // = 1 for columns = 3
		s = stride; //number of pixel the filter moves per step
	}
	
	public Matrix evaluate(Matrix input){ //convolving method
		
		int imgX = input.matrix.length;
		int imgY = input.matrix[0].length;
		
		Matrix out = new Matrix(imgX, imgY);
		
		Matrix paddedInput = new Matrix(imgX + 2, imgY + 2); //padding of size one 
		for(int i = 0; i < paddedInput.matrix.length; i++) {
			for(int j = 0; j < paddedInput.matrix[0].length; j++) {
				if(i == 0 || i == paddedInput.matrix.length - 1  || j == 0 || j == paddedInput.matrix[0].length - 1) {
					paddedInput.matrix[i][j] = 0;
				}else {
					paddedInput.matrix[i][j] = input.matrix[i-1][j-1]; //this loop returns the the same input but with 0's all around the border, which ensures that we can take a 3x3 filter centered on every single input pixel
				}
			}
		}

		for(int px = 1; px < imgX+1; px+= s) {
			for(int py = 1; py < imgY+1; py += s) {
				double total = 0;
				for(int fx = -x; fx <= x; fx++) { // -1, 0, 1
					for(int fy = -y; fy <= y; fy++) {// -1, 0 ,1
						total += get(fx + x, fy + y) * (paddedInput.matrix[px + fx][py + fy]); //adds up all the values of the filter and the input
					}
				}
				out.matrix[px - 1][py - 1] = (total) + bias; //stores the added up values, plus the bias, into the correct spot in the output
			}
		}
		return out;
	}
	public Matrix reverseConvolve(Matrix input) { //takes the "input" matrix, and then multiplies it by this filter, but in reverse, and returns that output
		int imgX = input.matrix.length;
		int imgY = input.matrix[0].length;
		
		Matrix out = new Matrix(imgX,imgY);
		
		Matrix paddedInput = new Matrix(imgX + 2, imgY + 2);
		for(int i = 0; i < paddedInput.matrix.length; i++) {
			for(int j = 0; j < paddedInput.matrix[0].length; j++) {
				if(i == 0 || i == paddedInput.matrix.length - 1  || j == 0 || j == paddedInput.matrix[0].length - 1) {
					paddedInput.matrix[i][j] = 0;
				}else {
					paddedInput.matrix[i][j] = input.matrix[i-1][j-1]; //padding the matrix to ensure no out of bounds exceptions
				}
			}
		}

		for(int i = 1; i < imgX + 1; i++) {
			for(int j = 1; j < imgY + 1; j++) {
				for(int k = -x; k <= x; k++) {
					for(int l = -y; l <= y; l++) {
						int xDex = i - 1 + k; //corresponding outputs in the previous layer
						int yDex = j - 1 + l;
						if(xDex >= 0 && yDex >= 0 && xDex < imgX && yDex < imgY) {

							out.matrix[xDex][yDex] += paddedInput.matrix[i][j] * get(k+x,l+y);
						}
					}
				}
			}
		}
		return out;
	}
	
	public Matrix getDF(Matrix first, Matrix next) { //gets the derivatitve of this matrix between the layers first and next
		int imgX = first.matrix.length;
		int imgY = first.matrix[0].length;
		Matrix out = new Matrix(r,c);
		for(int i = 0; i < imgX; i++) {
			for(int j = 0; j < imgY;j++) {
				for(int k = -x; k <= x; k++) {
					for(int l = -y; l <= y; l++) {
						if(i + k >= 0 && i + k < imgX && j + l >= 0 && j + l < imgY) {
							out.matrix[k + x][l + y] += first.matrix[i+k][j+l] * next.matrix[i][j];
							bias -= next.matrix[i][j] * .001; //also adjusts the bias here for convenience, as the next matrix is already being accessed
						}
					}
				}
			}
		}
		return out;
	}
	

	public Filter sub (Matrix b) { //subtract method from matrix, but returns a filter
		if (matrix.length == b.matrix.length && matrix[0].length == b.matrix[0].length) {
			Filter result = new Filter(r,c,s);
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[0].length; j++) {
					result.matrix[i][j] = matrix[i][j] - b.matrix[i][j];
				}
			}
			return result;
		}else {
			return null;
		}
	}
	
	

}
