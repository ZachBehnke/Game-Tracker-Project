package game_tracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;

import com.mysql.jdbc.PreparedStatement;

@SuppressWarnings("serial")
public class LoadDriver extends JPanel implements DocumentListener, ActionListener
{
	static JFrame frame = new JFrame("Game Tracker Project");
	static JTextArea txtarea = new JTextArea();
	static JScrollPane pane = new JScrollPane(txtarea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	JTextField searchTextBox = new JTextField(25);
	JTextField gameWriter = new JTextField(25);
	JTextField systemWriter = new JTextField(25);
	JTextField completeWriter = new JTextField(25);
	JTextField beatenWriter = new JTextField(25);
	
	public Statement statement = null;
	public ResultSet result = null;
	public ResultSet result_System = null;
	private String query;
	private String url = "jdbc:mysql://localhost:3306/DBTest";
	private String user = "root";
	private String pass = "greatsqldb";
	public Connection conn = DriverManager.getConnection(url, user, pass);
	boolean searching = true;
	boolean addingGame = true;
	
	String getGameTitle, getSystem, getComplete, getGameBeaten;
	
	Color Hilit_Color = Color.CYAN;
	final Highlighter hilit = new DefaultHighlighter();
	final Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Hilit_Color);
	
	JLabel stopSearch = new JLabel("Start Typing to Search for a Game");
	JLabel randomGame = new JLabel("Click Random Button to Find a Game to Play");
	
	JLabel systemSelect = new JLabel("Write the System Here");
	JLabel gameWrite = new JLabel("Write Game's Title Here");
	JLabel completeYN = new JLabel("Is the game complete?");
	JLabel beatenYN = new JLabel("Have you beaten this game?");
	
	int indexOfSystems = 5;
	String[] game_Systems = {"All", "PS3", "PS4", "Wii U", "Wii", "GCN"};
	JComboBox gameSystems = new JComboBox(game_Systems);
	
	public LoadDriver() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		connectionThings();
		buttonDisplay();
		
		gameSystems.setSelectedIndex(indexOfSystems);
		gameSystems.addActionListener(this);
		gameSystems.setBounds(500, 200, 100, 25);
		add(gameSystems);
	}
	
	public void connectionThings() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{	
		try 
		{	
            Class.forName("com.mysql.jdbc.Driver").newInstance(); 
			
			if (conn != null)
				System.out.println("Connected");
			query = "Select * from MasterGameList";
			statement = conn.createStatement();
			result = statement.executeQuery(query);
			txtarea.append("Game Title" + "\t" + "Game System" + "\t" + "Complete" + "\t" + "Game Beaten" + "\n");
			
			while (result.next())
			{
				String Game_Title = result.getString("Game_Title");
				String Game_System = result.getString("Game_System");
				String Complete = result.getString("Complete");
				String Game_Beaten = result.getString("Game_Beaten");
				txtarea.append(Game_Title + "\t" + Game_System + "\t" + Complete + "\t" + Game_Beaten + "\t" + "\n");
			}
		}
		catch (SQLException ex) 
		{
			System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		finally
		{
			if (statement != null)
				statement.close();
		}
	}
	
	public void buttonDisplay()
	{
		JButton searchButton = new JButton("Search Game List");
		JButton addGameButton = new JButton("Add a Game");
		JButton randomButton = new JButton("Random");
		JButton insertFile = new JButton("Insert New File");
		
		searchButton.setBounds(500, 500, 25, 25);
		addGameButton.setBounds(500, 400, 25, 25);
		randomButton.setBounds(500, 300, 25, 25);
		insertFile.setBounds(500, 200, 25, 25);
		
		searchTextBox.setBounds(675, 120, 100, 25);
		searchTextBox.getDocument().addDocumentListener(this);
		searchTextBox.setVisible(false);
				
		stopSearch.setBounds(700, 100, 100, 25);
		stopSearch.setVisible(false);
		
		systemSelect.setBounds(725, 80, 100, 25);
		systemSelect.setVisible(false);
		
		gameWrite.setBounds(750, 60, 100, 25);
		gameWrite.setVisible(false);
		
		completeYN.setBounds(775, 40, 100, 25);
		completeYN.setVisible(false);
		
		beatenYN.setBounds(800, 20, 100, 25);
		beatenYN.setVisible(false);
		
		gameWriter.setBounds(825, 15, 100, 25);
		gameWriter.setVisible(false);
		
		systemWriter.setBounds(850, 15, 100, 25);
		systemWriter.setVisible(false);
		
		completeWriter.setBounds(875, 15, 100, 25);
		completeWriter.setVisible(false);
		
		beatenWriter.setBounds(900, 15, 100, 25);
		beatenWriter.setVisible(false);
		
		searchButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				if (searching == true)
				{
					searching = false;
					searchButton.setText("Searching...");
					searchTextBox.setVisible(true);
					searchTextBox.setText("");
					stopSearch.setText("Click the Button to stop Searching");
					stopSearch.setVisible(true);
					randomGame.setVisible(false);
				}
				else
				{
					searching = true;
					searchButton.setText("Search Game List");
					searchTextBox.setVisible(false);
					stopSearch.setVisible(false);
					randomGame.setText("Click Random Button to find a Game to Play");
					randomGame.setVisible(true);
				}
					
			}
		});
		
		randomButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					query = "select game_title, game_system from NotBeatenGames Order By Rand() Limit 0, 1;";
					
					randomGame.setBounds(900, 100, 100, 25);
					
					statement = conn.createStatement();
					result = statement.executeQuery(query);
					
					while (result.next())
					{
						String game_title = result.getString("Game_Title");
						String Game_System = result.getString("Game_System");
						randomGame.setText(game_title + " (" + Game_System + ")");
					}
				}
				catch (SQLException e2) 
				{
					e2.printStackTrace();
				}		
			}
		});
		
		addGameButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				if (addingGame == true)
				{
					addingGame = false;
					systemSelect.setVisible(true);
					gameWrite.setVisible(true);
					completeYN.setVisible(true);
					beatenYN.setVisible(true);
					gameWriter.setVisible(true);
					systemWriter.setVisible(true);
					completeWriter.setVisible(true);
					beatenWriter.setVisible(true);
					gameWriter.setText(null);
					systemWriter.setText(null);
					completeWriter.setText(null);
					beatenWriter.setText(null);
				}
				
				else
				{
					addingGame = true;
					systemSelect.setVisible(false);
					gameWrite.setVisible(false);
					completeYN.setVisible(false);
					beatenYN.setVisible(false);
					gameWriter.setVisible(false);
					systemWriter.setVisible(false);
					completeWriter.setVisible(false);
					beatenWriter.setVisible(false);
					addAGame();
				}
			}
			
		});
		
		/*insertFile.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try 
				{
					Desktop.getDesktop().open(new File("c://"));
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});*/
		
		add(searchButton);
		add(addGameButton);
		add(randomButton);
		add(insertFile);
		add(searchTextBox);
		add(stopSearch);
		add(randomGame);
		add(systemSelect);
		add(gameWrite);
		add(gameWriter);
		add(systemWriter);
		add(completeWriter);
		add(beatenWriter);
		add(completeYN);
		add(beatenYN);
	}
	
	public void search()
	{
		txtarea.setHighlighter(hilit);
		hilit.removeAllHighlights();
        String s = searchTextBox.getText();
        
        if (s.length() <= 0)
        {
        	searchTextBox.setBackground(Color.WHITE);
        	stopSearch.setText("Start Typing to Search for a Game");
        	return;
        }
                 
        String content = txtarea.getText();
        int index = content.indexOf(s, 0);
        
        if (index >= 0) 
        {   // match found
            try
            {
                int end = index + s.length();
                hilit.addHighlight(index, end, painter);
                txtarea.setCaretPosition(end);
                searchTextBox.setBackground(Hilit_Color);
                stopSearch.setText("Game Found! Click the Button to Stop Searching");
            } catch (BadLocationException e)
            {
            	e.printStackTrace();
            }
                
        } else 
        {
            searchTextBox.setBackground(Color.RED);
            stopSearch.setText("Not Found!!");
        }
	}
	
	@SuppressWarnings("null")
	public void addAGame()
	{
		getGameTitle = gameWriter.getText();
		getSystem = systemWriter.getText();
		getComplete = completeWriter.getText();
		getGameBeaten = beatenWriter.getText();
		
		try
		{
			query = ("Insert Into MasterGameList (Game_Title, Game_System, Complete, Game_Beaten) values"
						+ "(" + "'" + getGameTitle + "'" + ","
						+ "'" + getSystem + "'" + "," + "'" + 
						getComplete + "'" + "," + "'" + getGameBeaten + "'" + ");");
				
			java.sql.PreparedStatement ps = conn.prepareStatement(query);
			int updateDB = ps.executeUpdate(query);
				
			if (getGameTitle != null)
				txtarea.append(getGameTitle + "\t" + getSystem + "\t" + getComplete + "\t" + getGameBeaten + "\t" + "\n");				
		}
		catch (SQLException e1) 
		{
			e1.printStackTrace();
		}
	}
	
	@Override
	public void changedUpdate(DocumentEvent arg0) {}

	@Override
	public void insertUpdate(DocumentEvent arg0) { search(); }
	@Override
	public void removeUpdate(DocumentEvent arg0) { search(); }
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		JComboBox cb = (JComboBox) e.getSource();
        String systemName = (String) cb.getSelectedItem();
		query = ("Select * from MasterGameList Where game_system = " + "'" + systemName + "'" + ";");
		if (systemName == "All")
			query = ("Select * from MasterGameList");
		PreparedStatement ps = null;
		try 
		{
			ps = (PreparedStatement) conn.prepareStatement(query);
			result_System = ps.executeQuery(query);
			txtarea.setText(null);
			txtarea.append("Game Title" + "\t" + "Game System" + "\t" + "Complete" + "\t" + "Game Beaten" + "\n");
			
			while (result_System.next())
			{
				String Game_Title = result_System.getString("Game_Title");
				String Game_System = result_System.getString("Game_System");
				String Complete = result_System.getString("Complete");
				String Game_Beaten = result_System.getString("Game_Beaten");
				txtarea.append(Game_Title + "\t" + Game_System + "\t" + Complete + "\t" + Game_Beaten + "\t" + "\n");			
			}
		}
		catch (SQLException e1) 
		{
			e1.printStackTrace();
		}
        finally
        {
        	try 
        	{ 
        		ps.close();
        		result_System.close();
        	}
        	catch (Exception ignored) {}
        }
	}
	
	public static void main (String[] args) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		LoadDriver loadDriver = new LoadDriver();
		txtarea.setEditable(false);
		txtarea.setVisible(true);
		frame.setResizable(true);
		frame.setBackground(Color.WHITE);
		frame.add(pane, BorderLayout.WEST);
		frame.add(loadDriver);
		frame.setSize(1000, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}