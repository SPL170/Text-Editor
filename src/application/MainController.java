package application;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController implements Initializable
{
	@FXML
	private AnchorPane anchor;
	String sqlCommands = "";
	
	@FXML
	private TextArea textArea;
	
	@FXML
	private TreeView treeView;
	
	@FXML
	private ListView messages;
	
	@FXML
	private MenuItem aboutMenuItem;
	
	@FXML
	private MenuItem openMenuItem;
	
	@FXML
	private MenuItem runMenuItem;
	
	@FXML
	private MenuItem saveMenuItem;
	
	@FXML
	private MenuItem copyMenuItem;
	String copy = "";
	
	@FXML
	private MenuItem pasteMenuItem;
	
	@FXML
	private MenuItem prefMenuItem;
	
	@FXML
	private Button allCom;
	
	@FXML
	private Button oneCom;
	
	@FXML
	private Button sweepCom;
	
	@FXML
	private TextArea selectArea;
	
	@FXML
	private TextFlow messageArea;
	
	Image err = new Image(getClass().getResourceAsStream("Error Icon.png"));
	ImageView errorpic = null;
	
	Image stat = new Image(getClass().getResourceAsStream("Status Icon.png"));
	ImageView statuspic = null;
	
	@Override
	public void initialize(URL path, ResourceBundle resources) 
	{	
		//Sets the minimum size for the window
		anchor.setMinHeight(614.0);
		anchor.setMinWidth(749.0);
		
		//Ask how to update this
		File choice = new File(".");
		treeView.setRoot(this.makeBranch(choice));
		
		//Makes the Select box non-editable
		selectArea.setEditable(false);
		
		//Sets the image for the all commands button
		Image allCommands = new Image(getClass().getResourceAsStream("AllCommands.png"));
		allCom.setGraphic(new ImageView(allCommands));
		
		//Runs every SQL Command currently in the editor
		allCom.setOnAction(e -> {
			sqlCommands = textArea.getText();
			
			//Prepares the SQL Commands for a Windows OS
			char one = ';';
			char two = '\r';
			sqlCommands = sqlCommands.replace(one, two);
			
			//Makes the SQL Commands into a file
			createTempFile("TempFile");
			insert("TempFile", sqlCommands);
			
			//Mimics the database class and manipulates the file
			databaseMimic("Tempfile");
			
			//Deletes the SQL Command file
			deleteTempFile("TempFile");
			
			//Refreshes the directory tree
			treeView.setRoot(this.makeBranch(choice));
		});
		
		//Sets the image for the one commands button
		Image oneCommand = new Image(getClass().getResourceAsStream("1 command.png"));
		oneCom.setGraphic(new ImageView(oneCommand));
		
		//Does shit
		oneCom.setOnAction(e -> {
			sqlCommands = textArea.getSelectedText();

			//Prepares the SQL Commands for a Windows OS
			char one = ';';
			char two = '\r';
			sqlCommands = sqlCommands.replace(one, two);
			
			//Makes the SQL Commands into a file
			createTempFile("TempFile");
			insert("TempFile", sqlCommands);
			
			//Mimics the database class and manipulates the file
			databaseMimic("Tempfile");
			
			//Deletes the SQL Command file
			deleteTempFile("TempFile");
			
			//Refreshes directory tree as the last action
			treeView.setRoot(this.makeBranch(choice));
		});
		
		//Sets the image for the sweep button
		Image sweepCommand = new Image(getClass().getResourceAsStream("sweep.png"));
		sweepCom.setGraphic(new ImageView(sweepCommand));
		
		//Clears the SQL Command editor
		sweepCom.setOnAction(e -> {
			textArea.setText("");
		});
		
		//Runs a certain file with commands
		runMenuItem.setOnAction(e -> {
			showSingleFileChooser();
			sqlCommands = textArea.getText();
			
			//Prepares the SQL Commands for a Windows OS
			char one = ';';
			char two = '\r';
			sqlCommands = sqlCommands.replace(one, two);
			
			//Makes the SQL Commands into a file
			createTempFile("TempFile");
			insert("TempFile", sqlCommands);
			
			//Mimics the database class and manipulates the file
			databaseMimic("Tempfile");
			
			//Deletes the SQL Command file
			deleteTempFile("TempFile");
			
			//Refreshes directory tree as the last action
			treeView.setRoot(this.makeBranch(choice));
		});
		
		//Opens the commands in a file and displays them to the SQL Command editor
		openMenuItem.setOnAction(new FileChooseListener());
		
		//Saves changes made to the currently open file
		saveMenuItem.setOnAction(new FileSaveListener());
		
		//Copy highlighted text
		copyMenuItem.setOnAction(e -> {
			copy = textArea.getSelectedText();
		});
		
		//Paste the currently highlighted text
		pasteMenuItem.setOnAction(e -> {
			textArea.appendText(copy);
		});
		
		//Opens a dialog box
		prefMenuItem.setOnAction(new PreferenceListener());
		
		//Opens an alert box, displaying the purpose of the project
		aboutMenuItem.setOnAction(new AboutListener());
	}
	
	/**
	 * databaseMimic
	 * This method is essentially the Database.java class from project 1 implemented as a method in the MainController with
	 * the only difference being that this method also prints the status and error messages.
	 * 
	 * @author John Stephenson
	 * @date 3/17/2017
	 * @param String fileName: a string of the name of the file.
	 */
	private void databaseMimic(String fileName)
	{
		messageArea.getChildren().clear();
		
		String message = "";
		String line = "";
		String statusMessage = "";
		String errorMessage = "";
		String messageDisplay = "";
		String selectDisplay = "";
		Text messageText = null;
		int dot = 0;
		
		try
		{
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String status = "status.txt";
			String error = "error.txt";			
			String directory = "";
			String commandLine = "";
			String curMessage = "";

			//creating the status file
			File statFile = new File(status);
			statFile.createNewFile();

			//creating the error file
			File errFile = new File(error);
			errFile.createNewFile();

			//checks if the status file needs to be re-written
			boolean statAppend = false;
			if(statFile.length() > 0){
				statAppend = false;
			}
			else
			{
				statAppend = true;
			}

			//checks if the error file needs to be re-written
			boolean errAppend = false;
			if(errFile.length() > 0)
			{
				errAppend = false;
			}
			else
			{
				statAppend = true;
			}

			//FileWriter for status file
			FileWriter statusFW = new FileWriter(status, statAppend);
			BufferedWriter statusBW = new BufferedWriter(statusFW);
			PrintWriter statusPW = new PrintWriter(statusBW);

			//FileWriter for error file
			FileWriter errorFW = new FileWriter(error, errAppend);
			BufferedWriter errorBW = new BufferedWriter(errorFW);
			PrintWriter errorPW = new PrintWriter(errorBW);

			DBCommands DBcomm = new DBCommands();

			//creates an ArrayList to store each word of a line from the DBUSER.txt file
			List<String> commands = new ArrayList<>();
			Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
			Matcher regexMatcher;
	
			//separates each word while keeping the quoted words together		
			while((line = bufferedReader.readLine()) != null) 
			{
				regexMatcher = regex.matcher(line);
				while(regexMatcher.find()) 
				{
					if(regexMatcher.group(1) != null)
					{
						//Add double-quoted string without the quotes
						commands.add(regexMatcher.group(1));
					}
					else
					{
						//Add unquoted word
						commands.add(regexMatcher.group());
					} 
				}

				//if-else conditionals to determine which method to run
				String[] command = commands.toArray(new String[commands.size()]);
				commandLine = "(" + line + ")";

				if(!(line.equals("")))
				{
					//CREATE method
					if(command[0].equals("CREATE"))
					{
						if(command[1].equals("DATABASE"))
						{
							boolean check = DBcomm.createDatabase(command[2]);
							
							//appending to the status or error log file
							try
							{
								ImageView errorpic = new ImageView(err);
								ImageView statuspic = new ImageView(stat);
								if(check == true)
								{
									curMessage = "Database " + command[2] + " was created." + "\n" + commandLine + "\n\n";
									messageText = new Text(curMessage);
									messageArea.getChildren().addAll(statuspic, messageText);
									statusMessage += curMessage;
								}
								else
								{
									curMessage = "Database " + command[2] + " was not created." + "\n" + commandLine + "\n\n";
									messageText = new Text(curMessage);
									messageArea.getChildren().addAll(errorpic, messageText);
									errorMessage += curMessage;
								}	
							}
							catch(Exception e)
							{
								ImageView errorpic = new ImageView(err);
								ImageView statuspic = new ImageView(stat);
								curMessage = e.getMessage();
								messageText = new Text(curMessage);
								messageArea.getChildren().addAll(errorpic, messageText);
								errorMessage += curMessage;
							}
		
						}
						else if(command[1].equals("TABLE"))
						{
	
							boolean check = DBcomm.createTable(command[2]);
							dot = command[2].indexOf('.');
							fileName = command[2].substring(dot + 1);
							ImageView errorpic = new ImageView(err);
							ImageView statuspic = new ImageView(stat);
							if(check == true)
							{
								curMessage = "Table " + fileName + " was created."  + "\n" + commandLine + "\n\n";
								messageText = new Text(curMessage);
								messageArea.getChildren().addAll(statuspic, messageText);
								statusMessage += curMessage;
							}
							else
							{
								curMessage = "Table " + fileName + " was not created."  + "\n" + commandLine + "\n\n";
								messageText = new Text(curMessage);
								messageArea.getChildren().addAll(errorpic, messageText);
								errorMessage += curMessage;
							}
						}
						else
						{
							ImageView errorpic = new ImageView(err);
							ImageView statuspic = new ImageView(stat);
							curMessage = "CREATE " + command[1] + " method is not allowed." + "\n" + commandLine + "\n\n";
							messageText = new Text(curMessage);
							messageArea.getChildren().addAll(errorpic, messageText);
							errorMessage += curMessage;
						}
					}
					//DROP method
					else if(command[0].equals("DROP"))
					{
						ImageView errorpic = new ImageView(err);
						ImageView statuspic = new ImageView(stat);
						if(command[1].equals("DATABASE"))
						{
							File database = new File(command[2]);
							if(DBcomm.dropDatabase(database))
							{
								curMessage = "Database " + command[2] + " was dropped." + "\n" + commandLine + "\n\n";
								messageText = new Text(curMessage);
								messageArea.getChildren().addAll(statuspic, messageText);
								statusMessage += curMessage;
							}
							else
							{
								curMessage = "Database " + command[2] + " was not dropped." + "\n" + commandLine + "\n\n";
								messageText = new Text(curMessage);
								messageArea.getChildren().addAll(errorpic, messageText);
								errorMessage += curMessage;
							}
						}
						else if(command[1].equals("TABLE"))
						{
							dot = command[2].indexOf('.');
							fileName = command[2].substring(dot + 1);
							directory = command[2].substring(0, dot);
							File table = new File(directory + "/" + fileName);
					
							if(table.exists())
							{	
								if(DBcomm.dropTable(table))
								{
									curMessage = "Table " + fileName + " was dropped." + "\n" + commandLine + "\n\n";
									messageText = new Text(curMessage);
									messageArea.getChildren().addAll(statuspic, messageText);
									statusMessage += curMessage;
								}
								else
								{
									curMessage = "Table " + fileName + " was not dropped." + "\n" + commandLine + "\n\n";
									messageText = new Text(curMessage);
									messageArea.getChildren().addAll(errorpic, messageText);
									errorMessage += curMessage;
								}
							}
							else
							{
								curMessage = "Table " + fileName + " does not exist.";
								messageText = new Text(curMessage);
								messageArea.getChildren().addAll(errorpic, messageText);
								errorMessage += curMessage;
							}
						}
						else
						{
							curMessage = "DROP " + command[1]  + " method is not allowed." + "\n" + commandLine + "\n\n";
							messageText = new Text(curMessage);
							messageArea.getChildren().addAll(errorpic, messageText);
							errorMessage += curMessage;
						}
					}
					//INSERT method
					else if(command[0].equals("INSERT"))
					{
						ImageView errorpic = new ImageView(err);
						ImageView statuspic = new ImageView(stat);
						if(command[2].equals("INTO"))
						{
							boolean check = DBcomm.insert(command[3], command[1]);
							dot = command[3].indexOf('.');
							fileName = command[3].substring(dot + 1);
		
							if(check == true)
							{
								curMessage = command[1] + " was inserted into " + fileName + "." + "\n" + commandLine + "\n\n";
								messageText = new Text(curMessage);
								messageArea.getChildren().addAll(statuspic, messageText);
								statusMessage += curMessage;
							}
							else
							{
								curMessage = command[1] + " was not inserted into " + fileName + "." + "\n" + commandLine + "\n\n";
								messageText = new Text(curMessage);
								messageArea.getChildren().addAll(errorpic, messageText);
								errorMessage += curMessage;
							}		
						}
						else
						{
							curMessage = "That variance of the INSERT command is not allowed." + "\n" + commandLine + "\n\n";
							messageText = new Text(curMessage);
							messageArea.getChildren().addAll(errorpic, messageText);
							errorMessage += curMessage;
						}
					}
					//SELECT method
					else if(command[0].equals("SELECT"))
					{
						ImageView errorpic = new ImageView(err);
						ImageView statuspic = new ImageView(stat);
						if(command[1].equals("*"))
						{
							if(command[2].equals("FROM"))
							{
								//SELECT method with where
								if(command.length > 5)
								{
									String output = DBcomm.selectWithWhere(commandLine, command[3], command[7]);
									dot = command[3].indexOf('.');
									fileName = command[3].substring(dot + 1);
									directory = command[3].substring(0, dot);
	
									if(output.length() > 0)
									{
										char letter = output.charAt(0);
										if(letter  == 'e')
										{
											curMessage = output.substring(1);
											messageText = new Text(curMessage);
											messageArea.getChildren().addAll(errorpic, messageText);
											errorMessage += curMessage;
										}
										else
										{
											curMessage = output;
											selectArea.setText(curMessage);
											curMessage = "The record " + curMessage + " was found." + "\n" + commandLine + "\n\n";
											messageText = new Text(curMessage);
											messageArea.getChildren().addAll(statuspic, messageText);
											statusMessage += curMessage;
										}
									}
									else
									{
										curMessage = "The file " + fileName + "had no records to select from.";
										messageText = new Text(curMessage);
										messageArea.getChildren().addAll(statuspic, messageText);
										statusMessage += curMessage;
									}
								}
								//SELECT method without where
								else
								{
									dot = command[3].indexOf('.');
									fileName = command[3].substring(dot + 1);
									directory = command[3].substring(0, dot);
									String output = DBcomm.selectWithoutWhere(command[3], commandLine);
	
									if(output.length() > 0)
									{
										char letter = output.charAt(0);
										if(letter == 'e')
										{
											curMessage = output.substring(1);
											messageText = new Text(curMessage);
											messageArea.getChildren().addAll(errorpic, messageText);
											errorMessage += curMessage;
										}
										else
										{
											curMessage = output;
											selectArea.setText(curMessage);
											curMessage = "All records from " + directory + "." + fileName + " were read." + 
											 		     "\n" + commandLine + " \n"; ;
											messageText = new Text(curMessage);
											messageArea.getChildren().addAll(statuspic, messageText);
											statusMessage += curMessage;
										}
									}
									else
									{
										curMessage = "The selected file had no records." + "\n" + commandLine + "\n\n";
										messageText = new Text(curMessage);
										messageArea.getChildren().addAll(statuspic, messageText);
										statusMessage += curMessage;
									}
								}
							}
						}
						else
						{
							curMessage = command[0] + " " + command[1] + " command is not allowed.";
							messageText = new Text(curMessage);
							messageArea.getChildren().addAll(errorpic, messageText);
							errorMessage += curMessage;
						}
					}
					//DELETE method
					else if(command[0].equals("DELETE"))
					{
						ImageView errorpic = new ImageView(err);
						ImageView statuspic = new ImageView(stat);
						if(command[1].equals("FROM"))
						{	
							boolean deleteWOCheck;
							//delete with where method
							if(command.length > 3)
							{
								curMessage = "DELETE FROM WHERE COLUMN = is not a valid command.\n" + commandLine + "\n\n";
								messageText = new Text(curMessage);
								messageArea.getChildren().addAll(errorpic, messageText);
								errorMessage += curMessage;
							}
							//delete without where method
							else
							{
								dot = command[2].indexOf('.');
								fileName = command[2].substring(dot + 1);
								directory = command[2].substring(0, dot);
								deleteWOCheck = DBcomm.deleteWithoutWhere(command[2]);
								
								if(deleteWOCheck)
								{
									curMessage = "Records in " + fileName + " were deleted." + "\n" + commandLine + "\n\n";
									messageText = new Text(curMessage);
									messageArea.getChildren().addAll(statuspic, messageText);
									statusMessage += curMessage;
								}
								else
								{
									curMessage = "Records in " + fileName + " were not deleted." + "\n" + commandLine + "\n\n";
									messageText = new Text(curMessage);
									messageArea.getChildren().addAll(errorpic, messageText);
									errorMessage += curMessage;
								}
							}
						}
						else
						{
							curMessage = "DELETE " + command[1] + " command does not exist." + "\n" + commandLine + "\n\n";
							messageText = new Text(curMessage);
							messageArea.getChildren().addAll(errorpic, messageText);
							errorMessage += curMessage;
						}
					}
					//None existent method
					else
					{
						ImageView errorpic = new ImageView(err);
						ImageView statuspic = new ImageView(stat);
						curMessage = command[0] + " command is not allowed." + "\n" + commandLine + "\n\n";
						messageText = new Text(curMessage);
						messageArea.getChildren().addAll(errorpic, messageText);
						errorMessage += curMessage;
					}
				}
				else
				{
					curMessage = "The command line was empty." + "\n" + commandLine + "\n\n";
					errorMessage += curMessage;
				}
				
				//clears commands List for next line
				commands.clear();
			}
			statusPW.println(statusMessage);
			errorPW.println(errorMessage);
			
			//messageArea.setText(messageDisplay);
			
			statusPW.close();
			errorPW.close();
			bufferedReader.close();
		}
		catch(FileNotFoundException e)
		{
			message += "Unable to open file " + fileName + " | " + e.getMessage();
			messageText = new Text(message);
			messageArea.getChildren().add(messageText);
		}
		catch(IOException e)
		{
			message += "Error reading file " + fileName + " | " + e.getMessage();
			messageText = new Text(message);
			messageArea.getChildren().add(messageText);
		}
	}
	
	/**
	 * makeBranch
	 * This method makes a tree branch each time it goes through a directory and checks if each individual file
	 * is also a directory.
	 * 
	 * @author Sidharth Rajiv
	 * @date 3/15/2017
	 * @param File directory: The parameter is the highest directory the tree will ever go to.
	 * @return TreeItem<String>
	 */
	public TreeItem<String> makeBranch(File directory)
	{
        TreeItem<String> tree = new TreeItem<String>(directory.getName());
        for(File f : directory.listFiles()) 
        {
        	//Then we call the function recursively
            if(f.isDirectory())
            {
                tree.getChildren().add(makeBranch(f));
            } 
            else 
            {
                tree.getChildren().add(new TreeItem<String>(f.getName()));
            }
        }
        return tree;
    }
	
	private class FileSaveListener implements EventHandler<ActionEvent>
	{
		@Override
		public void handle(ActionEvent e)
		{
			FileChooser fileChooser = new FileChooser();
			
			File file = fileChooser.showSaveDialog(null);
			if(file != null)
			{
				saveFile(textArea.getText(), file);
			}
			else
			{
				Text text = new Text("The file " + file.getName() + " is empty.");
				messageArea.getChildren().add(text);
			}
		}
	}
	
	/**
	 * saveFile
	 * This method implements the save command of the GUI.
	 * 
	 * @author John Stephenson
	 * @date 3/15/2017
	 * @param contents
	 * @param file
	 */
	private void saveFile(String contents, File file)
	{
		try 
		{
            FileWriter fileWriter = null;
             
            fileWriter = new FileWriter(file);
            fileWriter.write(contents);
            fileWriter.close();
        } 
		catch (IOException ex) 
		{
			messageArea.getChildren().clear();
			Text text = new Text(ex.getMessage());
			messageArea.getChildren().add(text);
        }
	}

	private class PreferenceListener implements EventHandler<ActionEvent>
	{
		@Override
		public void handle(ActionEvent e)
		{
			Stage stage = new Stage();
			try
			{
				preferenceStart(stage);
			}
			catch(Exception error)
			{
				Text messageText = null;
				messageText = new Text(error.getMessage());
				messageArea.getChildren().add(messageText);
			}
		}
	}
	
	/**
	 * preferenceStart
	 * This method implements the selected FXML file and opens the GUI.
	 * 
	 * @author John Stephenson
	 * @date 3/15/2017
	 * @param stage
	 * @throws Exception
	 */
	private void preferenceStart(Stage stage) throws Exception
	{
		Parent root = FXMLLoader.load(getClass().getResource("PreferenceFXML.fxml"));
		root.setLayoutX(0);
		root.setLayoutY(0);
		Scene scene = new Scene(root);
		
		stage.setScene(scene);
		stage.show();
	}
		
	private class AboutListener implements EventHandler<ActionEvent>
	{
		@Override
		public void handle(ActionEvent e)
		{
			Stage stage = new Stage();
			
			try
			{
				aboutStart(stage);
			}
			catch(Exception error)
			{
				Text text = new Text(error.getMessage());
				messageArea.getChildren().add(text);
			}
		}
	}
	
	/**
	 * aboutStart
	 * This method implements the selected FXML file and opens the GUI.
	 * 
	 * @author John Stephenson
	 * @date 3/18/2017
	 * @param stage
	 * @throws Exception
	 */
	private void aboutStart(Stage stage) throws Exception
	{
		Parent root = FXMLLoader.load(getClass().getResource("About.fxml"));
		root.setLayoutX(0);
		root.setLayoutY(0);
		Scene scene = new Scene(root);
		
		stage.setScene(scene);
		stage.show();
	}
	
	private class FileChooseListener implements EventHandler<ActionEvent> 
	{
		@Override
		public void handle(ActionEvent e)
		{
			showSingleFileChooser();
		}
	}
	
	/**
	 * showSingleFileChooser
	 * This method is specifically for the OPEN, RUn etc where it opens a file selection window.
	 * 
	 * @author John Stephenson
	 * @date 3/15/2017
	 */
	private void showSingleFileChooser()
	{
		FileChooser fileChooser = new FileChooser();
		File selectedFile = fileChooser.showOpenDialog(null);
		
		if(selectedFile != null)
		{	
			String filePath = selectedFile.getAbsolutePath();
			String display = readSelectedFile(filePath);
			textArea.setText(display);
		}
	}
		
	/**
	 * readSelectedFile
	 * This method reads any file that is chosen by the FileChooser.
	 * 
	 * @author John Stephenson
	 * @date 3/15/2017
	 * @param path
	 * @return String: containing the contents of the file in the string format.
	 */
	private String readSelectedFile(String path)
	{
		File file = new File(path);
		FileReader fr = null;
		BufferedReader br = null;
		String output = "";
		
		try
		{
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String curLine = "";
			
			while((curLine = br.readLine()) != null)
			{
				output += curLine + "\r\n";
			}
		}
		catch(IOException e)
		{
			messageArea.getChildren().clear();
			Text text = new Text(e.getMessage());
			messageArea.getChildren().add(text);
		}
		
		return output;
	}
	
	/**
	 * readWholeFile
	 * This method does a similar thing to the ReadSelected File except that it has the file name and directory it is in.
	 * 
	 * @author John Stephenson
	 * @date 3/14/2017
	 * @param location: A String variable with the desired text file to be read
	 */
	private String readWholeFile(String location)
	{
		String message = "";
		int dot = location.indexOf('.');
		String fileName = location.substring(dot + 1);
		String directory = location.substring(0, dot);
		File table = new File(directory + "/" + fileName);
		File database = new File(directory);
		
		try
		{
			FileReader fr1 = new FileReader(directory + "/" + fileName);
			FileReader fr2 = new FileReader(directory + "/" + fileName);
			BufferedReader br1 = new BufferedReader(fr1);
			BufferedReader br2 = new BufferedReader(fr2);
			String curLine = "";
			String printCommand = "";
			int count = 0;
			
			while((curLine = br1.readLine()) != null)
			{
				count++;
			}
			br1.close();
			
			if(count > 0)
			{
				while((curLine = br2.readLine()) != null)
				{
					message += curLine + "\n";
				}
			}
		}
		catch(IOException event)
		{
			messageArea.getChildren().clear();
			Text text = new Text(event.getMessage());
			messageArea.getChildren().add(text);
		}
		return message;
	}
	
	/**
	 * createTempFile
	 * The method takes in 1 String parameter to create a temporary text file
	 * that will hold the text from the SQL Command editor area
	 * 
	 * @author John Stephenson
	 * @date 03/09/2017
	 * @param String location: A String variable with the name of the temporary file
	 */
	public void createTempFile(String tableName)
	{	
		File table = null;
		
		//if the file does not exist, then create it
		try
		{
			table = new File(tableName);
			table.createNewFile();
		}
		catch(Exception e) 
		{
			messageArea.getChildren().clear();
			Text text = new Text("Temp table " + tableName + " could not be created.");
			messageArea.getChildren().add(text);
		}
	}
	
	/** 
	 * deleteTempFile
	 * The method takes in 1 String parameter to delete the temporary file that
	 * held the text from the SQL Command editor area
	 * 
	 * @author John Stephenson
	 * @date 03/09/2017
	 * @param String location: A String variable with the file name to be deleted
	 */
	public void deleteTempFile(String tableName)
	{
		//if the table exists, then delete it
		File table = new File(tableName);
		if(table.exists())
		{
			table.delete();
		}
	}
	
	/**
	 * insert
	 * The method takes 2 String parameters, one for the pathway of the file and
	 * the other for the record to be inserted to the file. After checking that the
	 * file and its parent directory exist, the method then inserts the desired
	 * record into the desired file. The method returns a boolean variable 
	 * depending on whether or not the record is or is not inserted into the file.
	 * 
	 * @author John Stephenson
	 * @date 03/09/2017
	 * @param String location: A String variable with the file name to be deleted
	 * @param String input: A String variable with the text from the SQL Command
	 * editor area to be inserted to the temporary file
	 */
	public void insert(String location, String input)
	{
		File table = new File(location);
		
		FileWriter fileFW = null;
		PrintWriter filePW = null;

		if(table.exists()) 
		{
			try 
			{
				//appending to the specific file
				fileFW = new FileWriter(location, true);
				filePW = new PrintWriter(fileFW);
				
				filePW.println(input);
				filePW.close();
			}
			catch(Exception e) 
			{
				messageArea.getChildren().clear();
				Text text = new Text(e.getMessage());
				messageArea.getChildren().add(text);
			}
		}
		else
		{
			messageArea.getChildren().clear();
			Text text = new Text("Table " + location + " does not exist.");
			messageArea.getChildren().add(text);
		}	
	}
}

