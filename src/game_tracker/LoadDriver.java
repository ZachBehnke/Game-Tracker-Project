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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;

import com.mysql.jdbc.PreparedStatement;

@SuppressWarnings("serial")
public class LoadDriver extends JPanel implements DocumentListener
{
	//This is where I initialize all of the variables being used throughout the program.
	static JTextArea txtarea = new JTextArea();
	static JScrollPane pane = new JScrollPane(txtarea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	JTextField searchTextBox = new JTextField(25);
	JTextField gameWriter = new JTextField(25);
	JTextField systemWriter = new JTextField(25);
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
	public static boolean active = true;
	
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
	
	int indexComplete = 2;
	String[] game_complete = {"Yes", "No"};
	JComboBox<String> completeGame = new JComboBox<String>(game_complete);
	
	int indexBeaten = 2;
	String[] game_beaten = {"Yes", "No"};
	JComboBox<String> beatenGame = new JComboBox<String>(game_beaten);
		
	//Basic constructor to run methods and set the layout.
	public LoadDriver() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		txtarea.setEditable(false);
		txtarea.setVisible(true);
		
		setLayout(null);
		connectionThings();
		buttonDisplay();
		
		gameSystems.setSelectedIndex(indexOfSystems);
		gameSystems.setBounds(35, 425, 100, 25);
		pickSystem.setBounds(35, 400, 200, 25);
		
		add(completeGame);
		add(gameSystems);
		add(pickSystem);
		
		pickSystem();
	}
	
	//This method just set up the basic connection to the mySQL server.
	public void connectionThings() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{	
            Class.forName("com.mysql.jdbc.Driver").newInstance(); 
			
            //Checks for a connection, then runs a query.
			if (conn != null)
				System.out.println("Connected");
			
			selectQuery();
	}
	
	@SuppressWarnings("static-access")
	public void selectQuery() throws SQLException
	{
		try
		{
			txtarea.append("Game Title                                             " + 
					"\t" + "System" + "\t" + "Complete" + "\t" + "Game Beaten" + "\n" + "\n");
			
			query = "Select * from MasterGameList Order By Game_Title";
			statement = conn.createStatement();
			result = statement.executeQuery(query);
			
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
		JButton saveButton = new JButton("Save Current Data");
		JButton loadButton = new JButton("Load File");
		JButton deleteButton = new JButton("Clear Table");
		JButton refreshButton = new JButton("Refresh Table");
			
		searchButton.setBounds(35, 25, 150, 25);
		addGameButton.setBounds(35, 60, 150, 25);
		randomButton.setBounds(35, 95, 150, 25);
		deleteGameButton.setBounds(35, 130, 150, 25);
		updateGameButton.setBounds(35, 165, 150, 25);
		countButton.setBounds(35, 200, 150, 25);
		saveButton.setBounds(35, 235, 150, 25);
		loadButton.setBounds(35, 270, 150, 25);
		deleteButton.setBounds(35, 305, 150, 25);
		refreshButton.setBounds(35, 340, 150, 25);
		
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
		
		completeGame.setBounds(375, 90, 100, 25);
		completeGame.setVisible(false);
		
		beatenYN.setBounds(200, 130, 300, 25);
		beatenYN.setVisible(false);
		
		beatenGame.setBounds(375, 130, 100, 25);
		beatenGame.setVisible(false);
		
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
					completeGame.setVisible(true);
					beatenGame.setVisible(true);
					gameWriter.setText(null);
					systemWriter.setText(null);
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
					completeGame.setVisible(false);
					beatenGame.setVisible(false);
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
					completeGame.setVisible(true);
					beatenGame.setVisible(true);
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
					completeGame.setVisible(false);
					beatenGame.setVisible(false);
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
				countNumber.setText(String.valueOf(txtarea.getLineCount() - 3));
			}
		});
		
		saveButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				saveGameFile();
			}
		});
		
		loadButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try 
					{
						loadGameFile();
					} catch (SQLException e1) 
					{
						e1.printStackTrace();
					}
			}
		});
		
		deleteButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				clearDB();
			}
		}
		);
		
		refreshButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				txtarea.setText(null);
				try {
					selectQuery();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		);
		
		//adds all of the components to the frame 
		add(searchButton);
		add(addGameButton);
		add(randomButton);
		add(deleteGameButton);
		add(updateGameButton);
		add(countButton);
		add(saveButton);
		add(loadButton);
		add(deleteButton);
		add(refreshButton);
		add(searchTextBox);
		add(stopSearch);
		add(randomGame);
		add(systemSelect);
		add(gameWrite);
		add(gameWriter);
		add(systemWriter);
		add(completeGame);
		add(beatenGame);
		add(completeYN);
		add(beatenYN);
		add(deleteGame);
		add(deleteWriter);
		add(countNumber);
	}
	
	public void clearDB()
	{	
		PreparedStatement stmt;
		query = ("Delete from MasterGameList");
		txtarea.setText(null);
		try 
		{
			stmt = (PreparedStatement) conn.prepareStatement(query);
			@SuppressWarnings("unused")
			int deleteGameDB = stmt.executeUpdate(query);
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
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
		choose();
		
		getGameTitle = gameWriter.getText().replace("'","''");
		getSystem = systemWriter.getText();
		isBeaten = (String) beatenGame.getSelectedItem();
		isComplete = (String) completeGame.getSelectedItem();
						
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
		choose();
		
		String gameTitle = gameWriter.getText().replace("'", "''");
		String isBeaten = (String) beatenGame.getSelectedItem();
		String isComplete = (String) completeGame.getSelectedItem();
		
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
	
	public void loadGameFile() throws SQLException
	{	
		//clearDB();

		FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("Text File", "txt");
		final JFileChooser loadFileChooser = new JFileChooser();
		loadFileChooser.setApproveButtonText("Load");
		loadFileChooser.setDialogTitle("Load File");
		loadFileChooser.setFileFilter(extensionFilter);
		int actionDialog = loadFileChooser.showOpenDialog(this);
		
		if (actionDialog != JFileChooser.APPROVE_OPTION)
			return;
		
		if (actionDialog == JFileChooser.APPROVE_OPTION)
			clearDB();
		
		File loadFile = loadFileChooser.getSelectedFile();
		String filePath = loadFile.getAbsolutePath();
		filePath = filePath.replace("\\", "/");
		
		query  = ("LOAD DATA INFILE " + "'" + filePath + "'" + " INTO TABLE MasterGameList LINES TERMINATED BY '\r\n' IGNORE 2 LINES;");
		
		Statement stmt;
		
		try
		{
			stmt = conn.createStatement();
			stmt.executeUpdate(query);
			selectQuery();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			stmt = null;
		}
	}
	
	public void saveGameFile()
	{
		FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("Text File", "txt");
		final JFileChooser saveAsFileChooser = new JFileChooser();
		saveAsFileChooser.setApproveButtonText("Save");
		saveAsFileChooser.setDialogTitle("Save Game List");
		saveAsFileChooser.setFileFilter(extensionFilter);
		int actionDialog = saveAsFileChooser.showOpenDialog(this);
		
		if (actionDialog != JFileChooser.APPROVE_OPTION)
			return;
		
		File gameFile = saveAsFileChooser.getSelectedFile();
		
		if (!gameFile.getName().endsWith(".txt"))
			gameFile = new File(gameFile.getAbsolutePath() + ".txt");
		
		BufferedWriter outFile = null;
		try
		{
			outFile = new BufferedWriter(new FileWriter(gameFile));
			txtarea.write(outFile);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}	
		finally
		{
			if (outFile != null)
			{
				try
				{
					outFile.close();
				}
				catch (IOException e) {}
			}	
		}
	}
	
	public void pickSystem()
	{
		gameSystems.addActionListener(new ActionListener()
		{
			@Override
			@SuppressWarnings("static-access")
			public void actionPerformed(ActionEvent e) 
			{
				@SuppressWarnings("unchecked")
				JComboBox<String> cb = (JComboBox<String>) e.getSource();
		        String systemName = (String) cb.getSelectedItem();
				query = ("Select * from MasterGameList Where game_system = " + "'" + systemName + "'" + "Order By Game_Title" + ";");
				if (systemName == "All")
					query = ("Select * from MasterGameList Order By Game_Title");
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
		});
	}
	
	public void choose()
	{		
		completeGame.addActionListener(new ActionListener()
		{
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e)
			{
				JComboBox<String> cb = (JComboBox<String>) e.getSource();
		        @SuppressWarnings("unused")
				String completed = (String) cb.getSelectedItem();
			}
		});
		
		beatenGame.addActionListener(new ActionListener()
		{
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e)
			{
				JComboBox<String> cb = (JComboBox<String>) e.getSource();
		        @SuppressWarnings("unused")
				String beaten = (String) cb.getSelectedItem();
			}
		});
	}
}