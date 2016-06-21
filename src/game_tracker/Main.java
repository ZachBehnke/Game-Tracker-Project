package game_tracker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Main extends JFrame
{
	static JFrame frame = new JFrame("Game Tracker Project");

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) screenSize.getWidth();
		int height = (int) screenSize.getHeight();
		LoadDriver loadDriver = new LoadDriver();
		frame.add(loadDriver);
		frame.setResizable(true);
		frame.add(loadDriver.pane, BorderLayout.WEST);
		loadDriver.pane.setPreferredSize(new Dimension(575, 1000));
		loadDriver.txtarea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		frame.setBackground(Color.WHITE);
		frame.add(loadDriver);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
