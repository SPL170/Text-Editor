package application;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;

public class DBCommands
{
	/**
	* createDatabase 
	* The method takes in a String parameter, creates a File object of that name, 
	* checks if it already exists, and proceeds to create a directory with that name
	* if it does not already. The method returns as boolean value depending on the
	* success or failure of having created the database.
	*
	* @author John Stephenson
	* @date 02/12/2017
	* @param String dbName: A String variable passing the name for the desired database
	*/
	public boolean createDatabase(String dbName){
		
		File database = new File(dbName);

		//if the database does not exist, then create it
		if(database.exists()) 
		{
			return false;
		}
		else 
		{
			database.mkdir();
			return true;
		}
	}

	/**
	*dropDatabase 
	*The method takes in a File parameter of the desired database to be dropped.
	*The method checks if it exists and if it is a directory. If it passes, the
	*method then checks if the database has files and deletes those before going
	*on to delete the database. The method returns a boolean variable depending
	*on the success or failure of the delete method from in the File class.
	*
	* @author John Stephenson
	* @date 02/12/2017
	* @param File database: A File object of the database desired to be dropped
	*/
	public boolean dropDatabase(File database){

		boolean check = false;

		//checks if the file exists
		if(database.exists()) 
		{
			//checks if the file is a directory
			if(database.isDirectory()) 
			{
				//checks if the directory has files
				if(database.length() < 0) 
				{	
					database.delete();
					check = true;
				}
				else 
				{
					File[] files = database.listFiles();
					for(int i = 0; i < files.length; i++) 
					{
						files[i].delete();
					}

					database.delete();
					check = true;
				}
			}
			else
			{
				check = false;
			}
		}
		else
		{
			check = false;
		}
		return check;
	}

	/**
	* createTable 
	* The method takes in a String variable that will be the name of the
	* desired table. The method then makes a File object with the tableName
	* parameter and checks if the table already exists. If not, then the 
	* method creates it. The method returns a boolean variable depending on
	* whether of not the table was created.
	*
	* @author John Stephenson
	* @date 02/12/2017
	* @param String tableName: A String variable passing the value of the desired table's name
	*/
	public boolean createTable(String tableName){
		
		File table = null;
		boolean check = true;

		//breaking the name into the directory and file	
		int dot = tableName.indexOf('.');
		String directory = tableName.substring(0,dot);
		String fileName = tableName.substring(dot + 1);
		
		//if the file does not exist, then create it
		try 
		{
			table = new File(directory + "/" + fileName);
			if(table.exists())
			{
				check = false;
			}
			else 
			{
				table.createNewFile();
				check = true;
			}
		}
		catch(Exception e) 
		{
			check = false;
		}
		
		if(check)
		{
			return true;
		}
		else 
		{
			return false;
		}
	}

	 /**
	 * dropTable
	 * The method checks if the table to drop exists. If it does, the table
	 * is deleted. The method returns a boolean variable depending on whether
	 * the desired table is or is not dropped.
	 * 
	 * @author John Stephenson
	 * @date 02/12/2017
	 * @param File table: A File object referencing the table to be deleted
	 */
	public boolean dropTable(File table)
	{
		//if the table exists, then delete it
		boolean check;
		if(table.exists())
		{
			if(table.delete()) 
			{
				check = true;
			}
			else 
			{
				check = false;
			}
		}
		else
		{
			check = false;
		}
		return check;
	}

	 /**
	 * insert
	 * The method takes two String parameters, one for the pathway of the file and
	 * the other for the record to be inserted to the file. After checking that the
	 * file and its parent directory exist, the method then inserts the desired
	 * record into the desired file. The method returns a boolean variable 
	 * depending on whether or not the record is or is not inserted into the file.
	 * 
	 * @author John Stephenson
	 * @date 02/12/2017
	 * @param String location: A string variable with the directory and file name of the table
	 * @param String input: A string variable with the desired record to be inserted
	 */
	public boolean insert(String location, String input){
		
		boolean check = false;
		int dot = location.indexOf('.');

		String directory = location.substring(0, dot);
		File database = new File(directory);
		String file = location.substring(dot + 1);
		File table = new File(directory + "/" + file);
		
		FileWriter fileFW = null;
		BufferedWriter fileBW = null;
		PrintWriter filePW = null;

		if(database.exists()) 
		{	
			if(table.exists()) 
			{
				try 
				{
					//appending to the specific file
					fileFW = new FileWriter(directory + "/" + file, true);
					fileBW = new BufferedWriter(fileFW);
					filePW = new PrintWriter(fileBW);
					
					filePW.println(input);
					filePW.close();
					
					check = true;
				}
				catch(Exception e) 
				{
					check = false;
				}
			}
			else
			{
				check = false;
			}
		}
		else 
		{
			check = false;
		}	
		return check;
	}

	 /**
	 * selectWithWhere
	 * The method takes in 3 String parameters. After checking if the directory and
	 * table exist, the method then reads in the desired table and checks if the 
	 * desired file has any records to read. If it does, every line is read in and
	 * stored in a String variable called output. Should the program go wrong,
	 * output has a letter "e" inserted into the start of the variable to denote
	 * that it is an error that is handled in the Driver class. The method returns
	 * a String variable
	 *  
	 * @author John Stephenson
	 * @date 02/12/2017
	 * @param String commandLine: A String variable with the current line read from the file
	 * @param String location: A String variable with the directory and file name of the table
	 * @param String lineToFind: A String variable with the desired record to find from
	 * 							 desired table
	 */
	public String selectWithWhere(String commandLine, String location, String lineToFind){
		
		String output = "";
		int dot = location.indexOf('.');
		int count = 0;
		String directory = location.substring(0, dot);
		String fileName = location.substring(dot + 1);
		File table = new File(directory + "/" + fileName);
		File database = new File(directory);

		if(database.exists())
		{
			if(table.exists())
			{
				try
				{
					FileReader file = new FileReader(directory + "/" + fileName);
					BufferedReader br = new BufferedReader(file);
					String curLine = "";

					while((curLine = br.readLine()) != null)
					{
						if(curLine.equals(lineToFind))
						{
							count++;
						}
					}
				}
				catch(Exception e)
				{
					output = "eThe error: " + e.getMessage() + "\n" + commandLine + "\n";
					return output;
				}
			}
			else
			{
				output = "eThe Table " + fileName + " does not exist.";
				output += "\n" + commandLine + "\n\n";
				return output;
			}
		}
		else
		{
			output = "eThe Database " + directory + " does not exist";
			output = "\n" + commandLine + "\n";
			return output;
		}

		if(count > 0)
		{
			output = lineToFind;
			return output;
		}
		else
		{
			output = "eThe record " + lineToFind + " was not found.";
			output += "\n" + commandLine + "\n";
			return output;
		}
	}
	
	 /**
	 * selectWithoutWhere
	 * The method takes in 2 String parameters. After checking that the table and its
	 * parent directory exist, the method reads in the file and stores each line
	 * of the file to a String variable called output. Should something go wrong,
	 * the output variable will have a letter "e" and an error message stored in it
	 * and returned.
	 * 
	 * @author John Stephenson
	 * @date 02/12/2017
	 * @param String location: A String variable with the directory and file name of the table
	 * @param String commandLine: A String variable with the directory and file name of the table
	 */
	public String selectWithoutWhere(String location, String commandLine)
	{
		String output = "";
		int dot = location.indexOf('.');
		String directory = location.substring(0, dot);
		String fileName = location.substring(dot + 1);
		File table = new File(directory + "/" + fileName);
		File database = new File(directory);

		if(database.exists()) 
		{
			if(table.exists()) 
			{
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
							output += curLine + "\n";
						}
						return output;
					}
					else
					{
						output = "eThe file had no records to read.";
						output += "\n" + commandLine + "\n";
						return output;
					}
				}
				catch(Exception e) 
				{
					output = "eThe error: " + e.getMessage();
					output += "\n" + commandLine + "\n";
					return output;
				}
			}
			else
			{
				output = "eThe Table " + fileName + " does not exist.";
				output +=  "\n" + commandLine + "\n";
				return output;
			}
		}
		else
		{
			output = "eThe Database " + directory + " does not exist.";
			output += "\n" + commandLine + "\n";
			return output;
		}
	}

	 /**
	 * deleteWithoutWhere
	 * The method takes in 1 String variable. After checking if the database and table exist,
	 * the method then deletes the file and replaces it with an empty file of the same name.
	 * The method returns a boolean variable depending on whether or not the aforementioned
	 * process succeeds or fails.
	 * 
	 * @author John Stephenson
	 * @date 02/12/2017
	 * @param String location: A String variable with the directory and file name of the table
	 */
	public boolean deleteWithoutWhere(String location){
	
		boolean check = false;
		int dot = location.indexOf('.');
		String fileName = location.substring(dot + 1);
		String directory = location.substring(0, dot);
		File table = new File(directory + "/" + fileName);
		File database = new File(directory);

		if(database.exists()) 
		{
			if(table.exists()) 
			{
				if(table.delete())
				{
					try 
					{
						table.createNewFile();
						check = true;
					}
					catch(Exception e) 
					{
						check = false;
					}
				}
				else
				{
					check = false;
				}
			}
			else
			{
				check = false;
			}
		}
		else
		{
			check = false;
		}

		return check;
	}

	 /**
	 * deleteWithWhere
	 * The method takes 2 String parameters. After checking if the database and file exist,
	 * an empty temporary file of the original file is made. Then the original file is
	 * read, line-by-line, and compared to the line to remove. If the current line read from
	 * the original file does not equal the line to remove, then the current line is written
	 * into the temporary file. When the method is finished reading the original file, the
	 * original file is deleted and the temporary file's path value is changed to the old
	 * file's path value. The method returns a String variable message whose value is a 
	 * String dependent on the success or failure of the program.
	 * 
	 * @author John Stephenson
	 * @date 02/12/2017
	 * @param String location: A String variable with the directory and file name of the table
	 * @param String lineToRemove: A string variable with the desired record to remove
	 */
	public static String deleteWithWhere(String location, String lineToRemove){
		
		String message = "";
		int dot = location.indexOf('.');
		String fileName = location.substring(dot + 1);
		String directory = location.substring(0, dot);
		File table = new File(directory + "/" + fileName);
		File database = new File(directory);
		
		if(database.exists())
		{
			if(table.exists())
			{
				try 
				{
		            //Construct the new file that will later be renamed to the original filename. 
		            File tempFile = new File(table.getAbsolutePath() + ".tmp");
		            BufferedReader br = new BufferedReader(new FileReader(table));
		            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
		            String line;
		            
		            //Read from the original file and write to the new 
		            //unless content matches data to be removed.
		            while ((line = br.readLine()) != null) {
		                if (!line.trim().equals(lineToRemove)) {
		                    pw.println(line);
		                    pw.flush();
		                }
		            }
		            pw.close();
		            br.close();
		 
		            try
		            {
		            	table.delete();
		            }
		            catch(Exception e)
		            {
		            	message = "The error: " + e.getMessage();
		            	return message;
		            }
		            
		            //Delete the original file
		            if (!table.delete())
		            {
		            	message = "eThe file could not be deleted.";
		                return message;
		            }
		            else
		            {
		            	//Rename the new file to the filename the original file had.
			            if (!tempFile.renameTo(table))
			            {
			            	message = "eThe file could not be renamed.";
			                return message;
			            }
			            else
			            {
			            	message = "The instances of record " + lineToRemove + " were deleted from " + fileName + ".";
			            	return message;
			            }
		            }
		        }
				catch (Exception e) 
				{
		            message = "eThe error:" + e.getMessage();
		            return message;
		        }
			}
			else
			{
					message  = "eThe table " + fileName + " does not exist.";
					return message;
			}
		}
		else
		{
			message = "eThe database " + directory + " does not exist.";
			return message;
		}
	}

}
