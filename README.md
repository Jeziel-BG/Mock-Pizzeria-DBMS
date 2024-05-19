Name: Mock-Pizzeria-DBMS


Description:
This project houses two java programs for interacting with the Pizzeria's database.

TableCreator.java creates the tables for the database.
App.java is a text-based app that gives the user insert, delete, partial update, and limited query interactions with the database.



Compilation:
1. Ensure both .java files are downloaded and located in the same directory on your lectura account.
2. Compile both files using the command: javac TableCreator.java App.java


First Time Execution Instructions:
1. Run the command: export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}
- Ignore if you have already done this on your current lectura connection.
2. Run the command: java TableCreator.java
3. Enter your Oracle username and password as the program asks for your input.
4. The program will then prompt you for a command. To create all the tables, type the command: all
- Otherwise, to see all possible commands, enter: list
5. After the program has finished creating the tables, type in the command: exit
6. Proceed to the "Subsequent Executions" portions.



Subsequent Executions:
1. Run the command: export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}
- Ignore if you have already done this on your current lectura connection.
2. Run the command: java App.java
3. Enter your Oracle username and password as the program asks for your input.
4. The program will then ask if you would like to query or modify the database.
- Enter 'query' to query the database.
- Enter 'modify' to modify the database.
- Enter 'exit' to end the program.

Queries:
1. User prompted to enter which query they would like to run.
- Enter in the number they would like to run.
- Enter 'exit' to back out of query mode.
- All query options will be displayed for the user.
2. After a query, the user will be asked if they would like to stay in query mode.
- 'y' to return to step 1.
- 'n' to return to back out of query mode.

Modify:
1. Progam will ask which table they would like to modify.
- 'member', 'prize', or 'game'
2. After selection, user will be prompted to choose how they would like to modify.
- member has 'insert', 'delete', or 'update'
- prize has 'insert' or 'delete'
- game has 'insert' or 'delete'
3a. Insert: User will be prompted for each individual attribute that they are allowed to edit.
- All entered one by one.
3b. Delete: The user will be prompted to enter in the ID of the chosen entity to delete. After entered, it will delete.
3c. Update: User will be prompted to enter the ID of which member they will change. Then they will be prompted to enter the new information they can change.
- All entered one by one.
- The ID of the member will NOT change.
4. After this, the user will be prompted to see if they wish to continue modifying the current table.
- If yes, repeat back to Step 2.
5. The user will ask if they wish to continue modifying.
- If yes, jump back to Step 1
6. The user will be asked if they would like to quit the program.
- If 'y', exits the program.
- If 'n', returns to the main menu.




Work Split:
Miro - Queries 3 and 4. General debugging. Entering test data into DB.
Jeziel - Update DB methods. App.java UI. Entering test data into DB. General debugging. Overall DB Design and design.pdf.
Nathan - Insert and delete methods. TableCreator.java program. Entering test data into DB. Debugging queries. Readme.txt
Daniel - Queries 1 and 2 and debugging queries.
