Creators: Sidharth Rajiv, John Stephenson

When saving a file, no ‘.’ or ‘/’ or ‘“”’ in the filename.

If running commands multiple times, the status, error, and any databases that were created must be deleted before each run.

When opening a file to be run, any quotation marks in the file must be re-typed in the SQL Command editor in order to have the “correct” quotation marks.

If a command is incorrectly entered and execution does not properly finish, the new TempFile must be deleted before anymore running

How to Run the Methods
Creating a directory - CREATE DATABASE directory_name;
Deleting a directory - DROP DATABASE directory_name;
Creating a text file - CREATE TABLE directory_name.file_name;
Deleting a text file - DROP TABLE directory_name.file_name;
Inserting into a text file - INSERT "stuff" INTO directory_name.File_name;
Displaying all contents of a file - SELECT * FROM directory_name.file_name;
Displaying a specific record in a file - SELECT * FROM directory_name.file_name WHERE EQUAL TO "stuff";
