import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePane extends JPanel{
	BufferedImage img;
	int sf;
	public ImagePane(BufferedImage i,int scale) {
		img = i;
		sf = scale;
	}
	public void setImage(BufferedImage i) {
		img = i;
	}
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.drawRenderedImage(img, AffineTransform.getScaleInstance(sf,sf));
		//g.drawImage(img, 0,0,img.getHeight(),img.getWidth(),null);
		
	}
}
