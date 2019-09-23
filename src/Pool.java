import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class Pool {

	private int r;
	private int c;
	private int s;
	public Matrix xPositions;
	public Matrix yPositions;
	Pool(int rows, int columns, int stride) {
		r = rows;
		c = columns;
		s = stride;
		// TODO Auto-generated constructor stub
	}
	
	public Matrix pool(Matrix input) { //returns a matrix with half the size and with the max value of each 2x2 square as the new matrix index
		int xSize = input.matrix.length;
		int ySize = input.matrix[0].length;
		
		Matrix output = new Matrix(xSize/2,ySize/2);
		xPositions = new Matrix(xSize/2,ySize/2);
		yPositions = new Matrix(xSize/2,ySize/2);
		for(int i = 0; i < xSize; i += s) {
			for(int j = 0; j < ySize; j += s) {
				output.matrix[i/s][j/s] = input.matrix[i][j];
				xPositions.matrix[i/s][j/s] = i;
				yPositions.matrix[i/s][j/s] = j;
				for(int dx = 0; dx < r; dx++) {
					for(int dy = 0; dy < c; dy++) {
						if(input.matrix[i+dx][j+dy] > output.matrix[i/s][j/s]) {
							output.matrix[i/s][j/s] = input.matrix[i+dx][j+dy];
							xPositions.matrix[i/s][j/s] = i + dx; //remembering the positions of the max values for use later during differentiation
							yPositions.matrix[i/s][j/s] = j + dy;
						}
					}
				}
			}
		}
		return output;
	}

}

