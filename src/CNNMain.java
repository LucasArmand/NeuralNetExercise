import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
public class CNNMain {
	static int cur = 1;
	
	static ImagePane pane;
	public static void main(String[] args) {
		JFrame frame = new JFrame("CNN");
		frame.setLayout(null);
		frame.setSize(1800,1200);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//File fImage = new File("C:\\users\\Lucas\\Desktop\\images")
		BufferedImage image = new BufferedImage(18,18,BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster = image.getRaster();
		//System.out.println(raster.getHeight() + ", " + raster.getWidth() + " :  " + raster.getNumDataElements() + " " + raster.getSampleModel().getNumBands());
		int width = image.getWidth();
		int height = image.getHeight();
		
		BufferedImage[] xArray = new BufferedImage[10];
		for(int i = 0; i< 10; i++) {
			try {
				BufferedImage color = ImageIO.read(new File("C:\\Users\\Lucas\\Desktop\\images\\x\\"+i+".png"));
				xArray[i] = new BufferedImage(18,18,BufferedImage.TYPE_BYTE_GRAY);
				Graphics g = xArray[i].getGraphics();
				g.drawImage(color, 0, 0, null);
				g.dispose();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		BufferedImage[] oArray = new BufferedImage[10];
		for(int i = 0; i< 10; i++) {
			try {
				BufferedImage color = ImageIO.read(new File("C:\\Users\\Lucas\\Desktop\\images\\o\\"+i+".png"));
				oArray[i] = new BufferedImage(18,18,BufferedImage.TYPE_BYTE_GRAY);
				Graphics g = oArray[i].getGraphics();
				g.drawImage(color, 0, 0, null);
				g.dispose();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		
		
		int sf = 25;
		
		JLabel curLabel = new JLabel(Integer.toString(cur),JLabel.CENTER);
		curLabel.setText(Integer.toString(cur));
		curLabel.setBounds(500,900,700,200);
		curLabel.setFont(new Font(curLabel.getFont().getName(), curLabel.getFont().getStyle(),curLabel.getFont().getSize() * 20));
		frame.add(curLabel);

		
		JButton back = new JButton("Previous");
		back.setBounds(200,900,300,200);
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( cur == 1) {
					cur = 20;
				}else {
					cur--;
				}
				curLabel.setText(Integer.toString(cur));
				if (cur <= 10) {
					pane.setImage(oArray[cur - 1]);
				}
				else {
					pane.setImage(xArray[cur - 11]);
				}
				pane.repaint();
			}
		});
		frame.add(back);
		
		JButton next = new JButton("Next");
		next.setBounds(1200,900,300,200);
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( cur == 20) {
					cur = 1;
				}else {
					cur++;
				}
				curLabel.setText(Integer.toString(cur));
				if (cur <= 10) {
					pane.setImage(oArray[cur - 1]);
				}
				else {
					pane.setImage(xArray[cur - 11]);
				}
				pane.repaint();
			}
		});
		frame.add(next);
		
		Matrix filter = new Matrix(3,3);
		filter.populate();
		
		JLabel filterLabel = new JLabel(filter.toString());
		filterLabel.setBounds(750,200,400,400);
		filterLabel.setFont(new Font(filterLabel.getFont().getName(), filterLabel.getFont().getStyle(),filterLabel.getFont().getSize() * 4));
		frame.add(filterLabel);
		
		
		if (cur <= 10) {
			pane = new ImagePane(oArray[cur - 1],sf);
		}
		else {
			pane = new ImagePane(xArray[cur - 11],sf);
		}
		pane.setBounds(200,200,18*sf,18*sf);
		frame.add(pane);
		/*
		ImagePane[] panes = new ImagePane[20];
		for(int i = 0; i < 20; i++) {
			if (i < 10) {
				panes[i] = new ImagePane(oArray[i],sf);
			}
			else {
				panes[i] = new ImagePane(xArray[i - 10],sf);
			}
			panes[i].setBounds((i * 20 * sf)%1800,50 + ((i * 20 * sf) / 1800) * 20 * sf,20 * sf ,20 * sf);
			frame.add(panes[i]);
			
		}
		*/

		frame.setVisible(true);
		
	}
}
