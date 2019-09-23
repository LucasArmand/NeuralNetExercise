import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class ImageMatrix extends Matrix{

	static BufferedImage fromMatrix(Matrix m) { //creates a BufferedImage from a matrix of pixel values, 0 - 255
		BufferedImage out = new BufferedImage(m.matrix.length,m.matrix[0].length,BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = out.getRaster();
		for(int i = 0; i < m.matrix.length; i++) {
			for(int j = 0; j < m.matrix[0].length; j++) {
				raster.setPixel(i, j, new double[] {m.matrix[i][j]});
			}
		}
		out.setData(raster);
		return out;
	}

	public ImageMatrix (BufferedImage image) { //creates a matrix of values, 0 - 1, from a BufferedImage
		super(image.getWidth(),image.getHeight());
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] = (255 - image.getRaster().getPixel(i, j, new double[] {0})[0] )/255;
			}
		}

	}
}
