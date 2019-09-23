import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.JPanel;

public class ImagePane extends JPanel{
	BufferedImage img;
	int sf;
	int xBox;
	int yBox;
	int xSize;
	int ySize;
	public ImagePane(BufferedImage i,int scale) { //takes an image i at scale factor scale
		img = i;
		sf = scale;
	}
	public void setImage(BufferedImage i) {
		img = i;
	}
	public BufferedImage getImage() {
		return img;
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.drawRenderedImage(img, AffineTransform.getScaleInstance(sf,sf)); //draws the image
		
	}
}
