import javax.swing.*;
import java.awt.Font;


public class CNNFrame extends JFrame{ //GUI structure that holds the basics of the GUI
	public CNNFrame(String header) {
		super(header);
		this.setBounds(200,200,1000,800);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		setLayout(null);
		JLabel name = new JLabel("Lucas Armand");
		name.setBounds(this.getWidth()/2 - 200,25,400,50);
		name.setHorizontalAlignment(JLabel.CENTER);
		name.setFont(new Font("calibri",Font.BOLD,40));
		add(name);
		
		JLabel title = new JLabel("Convolutional Neural Network");
		title.setBounds(this.getWidth()/2 - 400,75,800,100);
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setFont(new Font("calibri",Font.BOLD,60));
		add(title);
		
		JLabel subtitle = new JLabel("for Digit Recognition (0-9)");
		subtitle.setBounds(this.getWidth()/2 - 200,125,400,100);
		subtitle.setHorizontalAlignment(JLabel.CENTER);
		subtitle.setFont(new Font("calibri",Font.PLAIN,30));
		add(subtitle);
		
		JLabel instructions1 = new JLabel("Please draw a number from 0 to 9 here:");
		instructions1.setBounds(50,200,400,100);
		instructions1.setFont(new Font("calibri",Font.PLAIN,20));
		add(instructions1);
		
		JLabel instructions2 = new JLabel("The network's guess of what you drew is:");
		instructions2.setBounds(550,200,400,100);
		instructions2.setFont(new Font("calibri",Font.PLAIN,20));
		add(instructions2);
		
	}
}
