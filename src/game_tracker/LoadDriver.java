/*
 * @Author: Zachary Behnke
 * @Description: This is the main class for the application, which handles everything the application does. It might
 * be split into other classes later, as the project/code develops.
 * NOTE: You need to have MySQL running on your system for the program to work.
 * */

package game_tracker;

//These are all of the imports that I had to grab.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;

import com.mysql.jdbc.PreparedStatement;

@SuppressWarnings("serial")
public class LoadDriver extends JPanel implements DocumentListener, ActionListener
{
	//This is where I initialize all of the variables being used throughout the program.
	static JFrame frame = new JFrame("Game Tracker Project");
	static JTextArea txtarea = new JTextArea();
	static JScrollPane pane = new JScrollPane(txtarea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	JTextField searchTextBox = new JTextField(25);
	JTextField gameWriter = new JTextField(25);
	JTextField systemWriter = new JTextField(25);
	JTextField completeWriter = new JTextField(25);
	JTextField beatenWriter = new JTextField(25);
	JTextField deleteWriter = new JTextField(25);
	
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
	boolean deletingGame = true;
	boolean updatingGame = true;
	
	String getGameTitle, getSystem, isComplete, isBeaten, getDeletedGame;
	
	//Used for the searching method.
	Color Hilit_Color = Color.CYAN;
	Color Bad_Color = Color.RED;
	final Highlighter hilit = new DefaultHighlighter();
	final Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Hilit_Color);
	final Highlighter.HighlightPainter dontAdd = new DefaultHighlighter.DefaultHighlightPainter(Bad_Color);
	
	JLabel stopSearch = new JLabel("Start Typing to Search for a Game");
	JLabel randomGame = new JLabel("");
	JLabel countNumber = new JLabel("");
	
	JLabel systemSelect = new JLabel("Write the System Here.");
	JLabel gameWrite = new JLabel("Write Game's Title Here.");
	JLabel completeYN = new JLabel("Is the game complete?");
	JLabel beatenYN = new JLabel("Have you beaten this game?");
	JLabel pickSystem = new JLabel("Pick a System to see Games for it.");
	JLabel deleteGame = new JLabel("Write the Game's Title to delete it.");
	
	//Used to select your system of choice.
	int indexOfSystems = 5;
	String[] game_Systems = {"All", "PS3", "PS4", "Wii U", "Wii", "GCN", 
			"NES", "SNES", "N64", "3DS", "Vita", "DS", "DC", "PS1", 
			"PS2", "Xbox", "Saturn", "GEN"};
	JComboBox<String> gameSystems = new JComboBox<String>(game_Systems);
		
	//Basic constructor to run methods and set the layout.
	public LoadDriver() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		setLayout(null);
		connectionThings();
		buttonDisplay();
		
		gameSystems.setSelectedIndex(indexOfSystems);
		gameSystems.addActionListener(this);
		gameSystems.setBounds(35, 285, 100, 25);
		pickSystem.setBounds(35, 260, 200, 25);
		add(gameSystems);
		add(pickSystem);
	}
	
	//This method just set up the basic connection to the mySQL server.
	@SuppressWarnings("static-access")
	public void connectionThings() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{	
		try 
		{	
            Class.forName("com.mysql.jdbc.Driver").newInstance(); 
			
            //Checks for a connection, then runs a query.
			if (conn != null)
				System.out.println("Connected");
			query = "Select * from MasterGameList";
			statement = conn.createStatement();
			result = statement.executeQuery(query);
			txtarea.append("Game Title                                             " + 
			"\t" + "System" + "\t" + "Complete" + "\t" + "Game Beaten" + "\n" + "\n");
			
			//This appends all the games from MasterGameList to the text area, where everything is seen.
			while (result.next())
			{
				String Game_Title = result.getString("Game_Title");
				String Game_System = result.getString("Game_System");
				String Complete = result.getString("Complete");
				String Game_Beaten = result.getString("Game_Beaten");
				if (Game_Title.length() < 20)
					Game_Title = Game_Title.format("%-55s", Game_Title);
				if (Game_Title.length() < 45)
					Game_Title = Game_Title.format("%-45s", Game_Title);
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
	
	//This function deals with the button displays, setting the bounds of each element, and what buttons do what.
	public void buttonDisplay()
	{
		JButton searchButton = new JButton("Search Game List");
		JButton addGameButton = new JButton("Add a Game");
		JButton randomButton = new JButton("Find Random Game");
		JButton deleteGameButton = new JButton("Delete a Game");
		JButton updateGameButton = new JButton("Update a Game");
		JButton countButton = new JButton("Total # of Games");
		
		searchButton.setBounds(35, 25, 150, 25);
		addGameButton.setBounds(35, 60, 150, 25);
		randomButton.setBounds(35, 95, 150, 25);
		deleteGameButton.setBounds(35, 130, 150, 25);
		updateGameButton.setBounds(35, 165, 150, 25);
		countButton.setBounds(35, 200, 150, 25);
		
		searchTextBox.setBounds(200, 25, 200, 25);
		searchTextBox.getDocument().addDocumentListener(this);
		searchTextBox.setVisible(false);
				
		stopSearch.setBounds(200, 60, 300, 25);
		stopSearch.setVisible(false);
		
		gameWrite.setBounds(200, 20, 300, 25);
		gameWrite.setVisible(false);
		
		gameWriter.setBounds(375, 20, 300, 25);
		gameWriter.setVisible(false);
		
		systemSelect.setBounds(200, 55, 300, 25);
		systemSelect.setVisible(false);
		
		systemWriter.setBounds(375, 55, 100, 25);
		systemWriter.setVisible(false);
		
		completeYN.setBounds(200, 90, 300, 25);
		completeYN.setVisible(false);
		
		completeWriter.setBounds(375, 90, 100, 25);
		completeWriter.setVisible(false);
		
		beatenYN.setBounds(200, 130, 300, 25);
		beatenYN.setVisible(false);
		
		beatenWriter.setBounds(375, 130, 100, 25);
		beatenWriter.setVisible(false);
		
		deleteGame.setBounds(200, 115, 300, 25);
		deleteGame.setVisible(false);
		
		deleteWriter.setBounds(200, 135, 300, 25);
		deleteWriter.setVisible(false);
		
		deleteGameButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (deletingGame == true)
				{
					deletingGame = false;
					deleteGameButton.setText("Deleting Game...");
					deleteWriter.setVisible(true);
					deleteGame.setVisible(true);
					searchButton.setEnabled(false);
					addGameButton.setEnabled(false);
					randomButton.setEnabled(false);
					updateGameButton.setEnabled(false);
					countButton.setEnabled(false);
					randomGame.setVisible(false);
					countNumber.setVisible(false);
				}
				else
				{
					deletingGame = true;
					deleteGameButton.setText("Delete a Game");
					deleteWriter.setVisible(false);
					deleteGame.setVisible(false);
					searchButton.setEnabled(true);
					addGameButton.setEnabled(true);
					randomButton.setEnabled(true);
					updateGameButton.setEnabled(true);
					countButton.setEnabled(true);
					randomGame.setText("");
					randomGame.setVisible(true);
					countNumber.setText("");
					countNumber.setVisible(true);
					try 
					{
						deleteAGame();
					} catch (SQLException | BadLocationException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		});
		
		searchButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				if (searching == true)
				{
					searching = false;
					searchButton.setText("Searching...");
					searchTextBox.setVisible(true);
					addGameButton.setEnabled(false);
					randomButton.setEnabled(false);
					deleteGameButton.setEnabled(false);
					updateGameButton.setEnabled(false);
					countButton.setEnabled(false);
					searchTextBox.setText("");
					stopSearch.setText("Click the Button to stop Searching");
					stopSearch.setVisible(true);
					randomGame.setVisible(false);
					countNumber.setVisible(false);
				}
				else
				{
					searching = true;
					searchButton.setText("Search Game List");
					searchTextBox.setVisible(false);
					stopSearch.setVisible(false);
					randomGame.setText("");
					randomGame.setVisible(true);
					countNumber.setText("");
					countNumber.setVisible(true);
					addGameButton.setEnabled(true);
					randomButton.setEnabled(true);
					countButton.setEnabled(true);
					deleteGameButton.setEnabled(true);
					updateGameButton.setEnabled(true);
				}
					
			}
		});
		
		randomButton.addActionListener(new ActionListener()
		{
			//this button selects a non-beaten game from the list, randomly
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					query = "Select game_title, game_system from MasterGameList Where INSTR(Game_Beaten, 'No') > 0 Order By Rand() Limit 0, 1 ;";
					
					randomGame.setBounds(210, 95, 400, 25);
					
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
					randomGame.setVisible(false);
					countNumber.setVisible(false);
					searchTextBox.setVisible(false);
					stopSearch.setVisible(false);
					randomButton.setEnabled(false);
					deleteGameButton.setEnabled(false);
					searchButton.setEnabled(false);
					updateGameButton.setEnabled(false);
					countButton.setEnabled(false);
					addGameButton.setText("Add This Game");
				}
				
				else
				{
					addingGame = true;
					addGameButton.setText("Add a Game");
					systemSelect.setVisible(false);
					gameWrite.setVisible(false);
					completeYN.setVisible(false);
					beatenYN.setVisible(false);
					gameWriter.setVisible(false);
					systemWriter.setVisible(false);
					completeWriter.setVisible(false);
					beatenWriter.setVisible(false);
					randomGame.setText("");
					randomGame.setVisible(true);
					countNumber.setText("");
					countNumber.setVisible(true);
					randomButton.setEnabled(true);
					deleteGameButton.setEnabled(true);
					searchButton.setEnabled(true);
					updateGameButton.setEnabled(true);
					countButton.setEnabled(true);
					try 
					{
						addAGame();
					} catch (SQLException | BadLocationException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		});
		
		updateGameButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (updatingGame == true)
				{
					gameWriter.setText("");
					updatingGame = false;
					gameWriter.setVisible(true);
					completeWriter.setVisible(true);
					beatenWriter.setVisible(true);
					gameWrite.setVisible(true);
					completeYN.setVisible(true);
					beatenYN.setVisible(true);
					randomGame.setVisible(false);
					countNumber.setVisible(false);
					randomButton.setEnabled(false);
					deleteGameButton.setEnabled(false);
					searchButton.setEnabled(false);
					addGameButton.setEnabled(false);
					countButton.setEnabled(false);
					updateGameButton.setText("Updating Game...");
				}
				else
				{
					updatingGame = true;
					gameWriter.setVisible(false);
					completeWriter.setVisible(false);
					beatenWriter.setVisible(false);
					gameWrite.setVisible(false);
					completeYN.setVisible(false);
					beatenYN.setVisible(false);
					randomGame.setText("");
					randomGame.setVisible(true);
					countNumber.setText("");
					countNumber.setVisible(true);
					randomButton.setEnabled(true);
					deleteGameButton.setEnabled(true);
					searchButton.setEnabled(true);
					addGameButton.setEnabled(true);
					countButton.setEnabled(true);
					updateGameButton.setText("Update a Game");
					try
					{
						updateAGame();
					} catch (SQLException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		});
		
		countButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				countNumber.setBounds(200, 200, 100, 25);
				countNumber.setText(String.valueOf(txtarea.getLineCount() - 2));
			}
		});
		
		//adds all of the components to the frame 
		add(searchButton);
		add(addGameButton);
		add(randomButton);
		add(deleteGameButton);
		add(updateGameButton);
		add(countButton);
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
		add(deleteGame);
		add(deleteWriter);
		add(countNumber);
	}
	
	//this function searches through the text area to find the game you type into the text box.
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
        {   
        	// match found
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
	
	//this function adds a game to the database, and it is shown in the text area, appended to the bottom.
	@SuppressWarnings("static-access")
	public void addAGame() throws SQLException, BadLocationException
	{
		getGameTitle = gameWriter.getText().replace("'","''");
		getSystem = systemWriter.getText();
		isBeaten = beatenWriter.getText();
		isComplete = completeWriter.getText();
		
		boolean empty;
		
		if (getGameTitle.length() < 20)
			getGameTitle = getGameTitle.format("%-55s", getGameTitle);
		if (getGameTitle.length() < 45 && getGameTitle.length() > 20)
			getGameTitle = getGameTitle.format("%-45s", getGameTitle);
		
		//this checks whether or not any of the text fields are empty, and if so, it doesn't add the game.
		if (getGameTitle.isEmpty() || getSystem.isEmpty() || isBeaten.isEmpty() || isComplete.isEmpty())
		{
			empty = true;
			query = ("Delete from MasterGameList where Game_Title = ''");
		}
			
		//inserts the game, with all its respective information
		else
		{
			empty = false;
		
			query = ("Insert Into MasterGameList (Game_Title, Game_System, Complete, Game_Beaten) values "
					+ "(" + "'" + getGameTitle + "'" + ","
					+ "'" + getSystem + "'" + "," + "'" + 
					isComplete + "'" + "," + "'" + isBeaten + "'" + ");");
		}
			
		PreparedStatement ps = null;
		
		try
		{	
			ps = (PreparedStatement) conn.prepareStatement(query);
			@SuppressWarnings("unused")
			int updateDB = ps.executeUpdate(query);
			
			if (empty == false)
				txtarea.append(getGameTitle + "\t" + getSystem + "\t" + isComplete + "\t" + isBeaten + "\t" + "\n");				
		}
		catch (SQLException e1) 
		{
			e1.printStackTrace();
		}
		finally
		{
			ps.close();
		}
	}
	
	//this function deletes a game from the database, still needs work however, since you have to reload the app
	//to see the change
	public void deleteAGame() throws SQLException, BadLocationException
	{
		getDeletedGame = deleteWriter.getText().replace("'", "''");
	        		
		query = ("Delete from MasterGameList where game_title = " + "'" + getDeletedGame + "'" + ";");
		
		PreparedStatement ps = null;
		
		try
		{
			ps = (PreparedStatement) conn.prepareStatement(query);
			@SuppressWarnings("unused")
			int deleteGameDB = ps.executeUpdate(query);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			ps.close();
		}
	}
	
	public void updateAGame() throws SQLException
	{	
		String gameTitle = gameWriter.getText().replace("'", "''");
		String isBeaten = beatenWriter.getText();
		String isComplete = completeWriter.getText();
		
		query = ("Update MasterGameList Set game_beaten =" + "'" + isBeaten  + "'" + ", complete =" + "'" + isComplete + "'" + "where game_title =" + "'" + gameTitle + "'" + ";");
		
		PreparedStatement ps = null;
		try 
		{
			ps = (PreparedStatement) conn.prepareStatement(query);
			@SuppressWarnings("unused")
			int updateGameDB = ps.executeUpdate(query);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			ps.close();
		}
	}
	
	//these three functions are used for the searching function.
	@Override
	public void changedUpdate(DocumentEvent arg0)
	{}

	@Override
	public void insertUpdate(DocumentEvent arg0) 
	{ 
		search(); 
	}
	
	@Override
	public void removeUpdate(DocumentEvent arg0)
	{
		search();
	}
	
	//This action performed function handles when you switch systems
	@SuppressWarnings("static-access")
	@Override
	public void actionPerformed(ActionEvent e)
	{
		@SuppressWarnings("unchecked")
		JComboBox<String> cb = (JComboBox<String>) e.getSource();
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
			txtarea.append("Game Title                                             " + 
					"\t" + "System" + "\t" + "Complete" + "\t" + "Game Beaten" + "\n" + "\n");			
			while (result_System.next())
			{
				String Game_Title = result_System.getString("Game_Title");
				String Game_System = result_System.getString("Game_System");
				String Complete = result_System.getString("Complete");
				String Game_Beaten = result_System.getString("Game_Beaten");
				
				if (Game_Title.length() < 20)
					Game_Title = Game_Title.format("%-55s", Game_Title);
				if (Game_Title.length() < 45)
					Game_Title = Game_Title.format("%-45s", Game_Title);
				
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
	
	//This is the main function used to run the application.
	public static void main (String[] args) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) screenSize.getWidth();
		int height = (int) screenSize.getHeight();
		LoadDriver loadDriver = new LoadDriver();
		txtarea.setEditable(false);
		txtarea.setVisible(true);
		pane.setPreferredSize(new Dimension(550, 1000));
		frame.setResizable(true);
		frame.setBackground(Color.WHITE);
		frame.add(pane, BorderLayout.WEST);
		frame.add(loadDriver);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}