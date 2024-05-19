/*=============================================================================
 |   Assignment:  Program #4
 |       Author:  Miro Vanek (mirovanek@arizona.edu)
 |                Jeziel Banos Gonzalez (jezielbgon@arizona.edu)
 |                Nathaniel Mette
 |                Daniel
 |
 |       Course:  CS460 (Database Design), Spring 2024
 |   Instructor:  L. McCann
 |          TAs:  Ahmad Musa, Jake Bode, Priyansh Nayak
 |     Due Date:  4/29, at the beginning of class
 |
 |     Language:  Java (JDK 16)
 |     Packages:  java.sql, java.util.Dictionary, java.util.Scanner, java.Hashtable
 |  Compile/Run:  Compile and run on lectura  
 							Compile: javac App.java  
 |						 	 Run: java App {oracle username} {oracle password}
 +-----------------------------------------------------------------------------
 |
 |  Description:  This program is meant to access a database associated with
 |                 a pizzeria to keep track of various information relating to
 |                 its customers/members.
 |                
 |        Input:  
 |
 |       Output:  
 |
 |   Techniques:  
 |						
 |
 |				  No particular algorithms are used.
 |
 |   Required Features Not Included: All required features are included.
 |
 |   Known Bugs:  None; the program operates correctly
 |
 |   
 |
 *===========================================================================*/

import java.sql.*;
import java.util.Dictionary;
import java.util.Scanner;
import java.util.Hashtable;

public class App {

    private static final String DB_URL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";

    // Place this variable in front of any table name in SQL queries to test on our testing DB.
    private static final String testPrefix = "nathanieljmette.";

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java App <username> <password>");
            return;
        }

        final String USER = args[0];
        final String PASS = args[1];

        // Below meant to display version of java for error checking
        // Should display 'Version: 16.*' (where * is any string)
        System.out.println("Version: " + Runtime.version());
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            userModeControl(conn, testPrefix);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        scanner.close();

    }

    /**
     * Execute a query and print the results. 
     * @param conn - Connection to the database
     * @param query - SQL query to execute
     * @param columns - Columns to print from the result set
     * @throws SQLException
     */
    private static void executeQuery(Connection conn, String query, String[] columns) throws SQLException {
        Statement stmt = null; // Statement object to execute the query
        ResultSet rs = null; // ResultSet object to store the query results
        try {
            stmt = conn.createStatement(); // Create a statement object
            System.out.println("made statement");
            rs = stmt.executeQuery(query); // Execute the query
            System.out.println("executing query");

            // Print the results
            while (rs.next()) {
                for (String column : columns) {
                    System.out.print(column + ": " + rs.getString(column) + ", ");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            // Close the ResultSet and Statement objects
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    // ========================================================================
    // FOR ORGANIZATION PURPOSES, ALL USER INTERFACE METHODS ARE BELOW THIS LINE
    // ========================================================================

    /*---------------------------------------------------------------------
    |  Method userMode()
    |
    |  Purpose: Used to determine what mode the user wants to use, query or
    |           modification mode.
    |
    |  Pre-condition:  Global scanner is active
    |
    |  Post-condition: Global scanner is active
    |
    |  Parameters: None
    |	   
    |
    |  Returns:  0 if the user wants to use modify mode, 1 if the user wants query mode, 
    |            2 if the user wants to exit the program.
    |
    |				   
	*-------------------------------------------------------------------*/
    private static int userMode(){
        
        System.out.println("What would you like to do? [Modify], [Query] or [Exit]");
        System.out.println("Please type \"Modify\", \"Query\" or \"Exit\"");

        boolean continueAsking = true;
        while(continueAsking){
            String userInput = scanner.nextLine();

            if(userInput.toUpperCase().equals("MODIFY")){
                System.out.println("Modify mode has been selected");
                
                return 0;
            }

            else if(userInput.toUpperCase().equals("QUERY")){
                System.out.println("Query Mode has been selected");
                return 1;
            }

            else if(userInput.toUpperCase().equals("EXIT")){
                System.out.println("Exiting the program");
                return 2;
            }

            else{
                System.out.println("Could not identify wanted mode");
                System.out.println("Please type \"Modify\", \"Query\", or \"Exit\"");
            }
        }
        return -1; // needed or else java gets mad
    }

    /**
     * Method: userModeControl()
     * Purpose: This method is used as a hub to determine what mode the user wants to use the DB in. This allows
     *          users to switch between modes without having to restart the program.
     * @param: None
     * @return: None
     */

     /*---------------------------------------------------------------------
    |  Method userModeControl()
    |
    |  Purpose: This method is used as a hub to determine what mode the user 
    |           wants to use the DB in. This allows users to switch between 
    |           modes without having to restart the program.
    |
    |  Pre-condition:  Global scanner is active, Connection is open
    |
    |  Post-condition: Global scanner is active, Connection is open
    |
    |  Parameters: Connection conn - connection to oracle
    |              String prefix - the prefix to every table name that is needed
    |	   
    |
    |  Returns: None
    |
    |				   
	*-------------------------------------------------------------------*/
    private static void userModeControl(Connection conn, String prefix){

        int continueOperations = 1;
        while(continueOperations == 1){
            int modeSelected = userMode();
            // exit
            if (modeSelected == 2){
                return;
            }
            // query mode
            else if(modeSelected == 1){
                continueOperations = queryUIFunc(conn, prefix); // will return 1 if user wants to switch modes 
            }
            // modify mode
            else if(modeSelected == 0){
                continueOperations = modifyUIFunc(conn); // will return 1 if user wants to switch modes 
            }
        }
    }

    /*---------------------------------------------------------------------
    |  Method modifyUIFunc()
    |
    |  Purpose: This method communicates to the user what relations they are 
    |           allowed to modify and queries them in which one they want to 
    |           edit.
    |
    |  Pre-condition:  Global scanner is active, Connection is open
    |
    |  Post-condition: Global scanner is active, Connection is open
    |
    |  Parameters: Connection conn- a connect to orcale
    |	   
    |
    |  Returns: int - used to communicate to other functions that the user
    |                  is quitting the program, 0 to quit, 1 to continue
    |
    |				   
	*-------------------------------------------------------------------*/
    private static int modifyUIFunc(Connection conn) {
        System.out.println("====================================================================");
        System.out.println("You are currently in Modification mode.");
        System.out.println("Here are the entites you are able to interact with: \nMember \nGame \nPrize");
        System.out.println("Please select what type of entity you would like to modify");
        System.out.println("====================================================================");

        while (true) {
            String entityType = scanner.nextLine();

            if(entityType.toUpperCase().equals("MEMBER")){
                memberModificationUI(conn);
                System.out.println("Would you like to continue modifications? (y/n)");
                String userInput = scanner.nextLine();
                boolean decision = checkYesOrNo(userInput);
                if(decision == false){
                    break;
                }
            }
            else if(entityType.toUpperCase().equals("GAME")){
                gameModificationUI(conn);
                System.out.println("Would you like to continue modifications? (y/n)");
                String userInput = scanner.nextLine();
                boolean decision = checkYesOrNo(userInput);
                if(decision == false){
                    break;
                }
            }
            else if(entityType.toUpperCase().equals("PRIZE")){
                prizeModificationUI(conn);
                System.out.println("Would you like to continue modifications? (y/n)");
                String userInput = scanner.nextLine();
                boolean decision = checkYesOrNo(userInput);
                if(decision == false){
                    break;
                }
            }
            else{
                System.out.println("Sorry, we didn't catch that");
                System.out.println("Please type \"Member\", \"Game\", or \"Prize\"");
            }
        }

        System.out.println("Do you want to quit the program? (y/n)");
        String userInput = scanner.nextLine();
        boolean decision = checkYesOrNo(userInput);
        // time to ask the user what mode they want
        if(decision == false){
            return 1;
        }
        // user has selected to quit the program
        return 0;
    }

    /*---------------------------------------------------------------------
    |  Method prizeModificationUI()
    |
    |  Purpose: This method communicates to the user how they can edit the 
    |           prize relation. Then queries the user if they want to insert
    |           or delete.
    |
    |  Pre-condition:  Global scanner is active, Connection is open
    |
    |  Post-condition: Global scanner is active, Connection is open
    |
    |  Parameters: Connection conn- a connect to orcale
    |	   
    |
    |  Returns: None 
    |
    |				   
	*-------------------------------------------------------------------*/
    private static void prizeModificationUI(Connection conn) {
        System.out.println("============================================================================");
        System.out.println("You currently have selected [PRIZE]");
        System.out.println("You may [INSERT] or [DELETE] prizes. If you would like to quit type \"quit\"");
        System.out.println("============================================================================");

        System.out.println("Please type \"Insert\" or \"Delete\"");
        
        boolean continueAsking = true;
        while (continueAsking) {
            String userInput = scanner.nextLine();

            if(userInput.toUpperCase().equals("INSERT")){
                Dictionary<String, String> data = collectPrizeInfo();
                insertPrize(conn, data);

                System.out.println("Would you like to continue modifications of the DB? (y/n)");
                userInput = scanner.nextLine();
                boolean decision = checkYesOrNo(userInput);
                if(decision == false){
                    continueAsking = false;
                }

            }
            else if(userInput.toUpperCase().equals("DELETE")){
                System.out.println("What is the prize's ID?");
                String id = scanner.nextLine();
                deleteTuple(conn, "nathanieljmette.prize", id);
                System.out.println("Would you like to continue modifications of the DB? (y/n)");
                userInput = scanner.nextLine();
                boolean decision = checkYesOrNo(userInput);
                if(decision == false){
                    continueAsking = false;
                }
            }
            else if (userInput.toUpperCase().equals("QUIT")){
                continueAsking = false;
            }
            else{
                System.out.println("Sorry, we could not understand your request. Please type insert, delete, or quit");
            }
        }

        System.out.println("Quitting modification of prizes");
    }

    /*---------------------------------------------------------------------
    |  Method: collectPrizeInfo()
    |
    |  Purpose: This method is used to collect the information for a new 
    |            tuple to add to the prize relation from the user
    |
    |  Pre-condition:  Global scanner is active
    |
    |  Post-condition: Global scanner is active
    |
    |  Parameters: None
    |	   
    |
    |  Returns: Dictionary data - a <String, String> dictionary with keys as
    |           relation field names and values as values collected from user 
    |
    |				   
	*-------------------------------------------------------------------*/
    private static Dictionary<String,String> collectPrizeInfo(){
        Dictionary<String,String> data = new Hashtable<String,String>();
        System.out.println("What is the prize's name?");
        data.put("prize_name", scanner.nextLine());

        System.out.println("What is the prize's ticket price?");
        data.put("prize_price", scanner.nextLine());

        System.out.println("What is the prize's current stock?");
        data.put("stock", scanner.nextLine());

        data.put("active", "1"); // 1 means active

        return data;
    }

    /*---------------------------------------------------------------------
    |  Method gameModificationUI()
    |
    |  Purpose: This method communicates to the user how they can edit the 
    |           game relation. Then queries the user if they want to insert
    |           or delete.
    |
    |  Pre-condition:  Global scanner is active, Connection is open
    |
    |  Post-condition: Global scanner is active, Connection is open
    |
    |  Parameters: Connection conn- a connect to orcale
    |	   
    |
    |  Returns: None 
    |
    |				   
	*-------------------------------------------------------------------*/
    private static void gameModificationUI(Connection conn) {
        System.out.println("============================================================================");
        System.out.println("You currently have selected [GAME]");
        System.out.println("You may [INSERT] or [DELETE] games. If you would like to quit type \"quit\"");
        System.out.println("============================================================================");

        System.out.println("Please type \"Insert\" or \"Delete\"");
        
        boolean continueAsking = true;
        while (continueAsking) {
            String userInput = scanner.nextLine();

            if(userInput.toUpperCase().equals("INSERT")){
                Dictionary<String,String> data = collectGameInfo();
                insertGame(conn, data);
                System.out.println("Would you like to continue modifications of the DB? (y/n)");
                userInput = scanner.nextLine();
                boolean decision = checkYesOrNo(userInput);
                if(decision == false){
                    continueAsking = false;
                }

            }
            else if(userInput.toUpperCase().equals("DELETE")){
                System.out.println("What is the game's ID?");
                String id = scanner.nextLine();
                deleteTuple(conn, "nathanieljmette.game", id); // not checking if ID is valid, deleteTuple handles error case


                System.out.println("Would you like to continue modifications of the DB? (y/n)");
                userInput = scanner.nextLine();
                boolean decision = checkYesOrNo(userInput);
                if(decision == false){
                    continueAsking = false;
                }
            }
            else if (userInput.toUpperCase().equals("QUIT")){
                continueAsking = false;
            }
            else{
                System.out.println("Sorry, we could not understand your request. Please type insert, delete, or quit");
            }
        }

        System.out.println("Quitting modification of games");
    }


    /*---------------------------------------------------------------------
    |  Method: collectGameInfo()
    |
    |  Purpose: This method is used to collect the information for a new 
    |            tuple to add to the game relation from the user
    |
    |  Pre-condition:  Global scanner is active
    |
    |  Post-condition: Global scanner is active
    |
    |  Parameters: None
    |	   
    |
    |  Returns: Dictionary data - a <String, String> dictionary with keys as
    |           relation field names and values as values collected from user 
    |
    |				   
	*-------------------------------------------------------------------*/
    private static Dictionary<String,String> collectGameInfo(){
        Dictionary<String,String> data = new Hashtable<String,String>();
        System.out.println("What is the game's name?");
        data.put("game_name", scanner.nextLine());

        System.out.println("What is the game's min ticket reward?");
        data.put("min_tickets", scanner.nextLine());

        System.out.println("What is the game's max ticket reward?");
        data.put("max_tickets", scanner.nextLine());

        System.out.println("What is the game's ticket score multiplier?");
        data.put("ticket_score_multiplier", scanner.nextLine());

        System.out.println("What is the game's token cost?");
        data.put("tokens_to_play", scanner.nextLine());
        return data;
    }

    /*---------------------------------------------------------------------
    |  Method memberModificationUI()
    |
    |  Purpose: This method communicates to the user how they can edit the 
    |           member relation. Then queries the user if they want to insert
    |           delete or update.
    |
    |  Pre-condition:  Global scanner is active, Connection is open
    |
    |  Post-condition: Global scanner is active, Connection is open
    |
    |  Parameters: Connection conn- a connect to orcale
    |	   
    |
    |  Returns: None 
    |
    |				   
	*-------------------------------------------------------------------*/
    private static void memberModificationUI(Connection conn) {
        System.out.println("=======================================================================================");
        System.out.println("You currently have selected [MEMBER]");
        System.out.println("You may [INSERT], [DELETE] OR [UPDATE] members. If you would like to quit type \"quit\"");
        System.out.println("=======================================================================================");

        System.out.println("Please type \"Insert\" or \"Delete\" or \"Update\"");
        
        boolean continueAsking = true;
        while (continueAsking) {
            String userInput = scanner.nextLine();
            //inserting
            if(userInput.toUpperCase().equals("INSERT")){
                Dictionary<String, String> data = collectMemberInfo();

                insertMember(conn, data); // NEED A CONNECTION
                System.out.println("Would you like to continue modifications of the DB? (y/n)");
                userInput = scanner.nextLine();
                boolean decision = checkYesOrNo(userInput);
                if(decision == false){
                    continueAsking = false;
                }

            }
            // deleting
            else if(userInput.toUpperCase().equals("DELETE")){
                System.out.println("Please provide the member's ID number:");
                userInput = scanner.nextLine();
                deleteTuple(conn, "nathanieljmette.member", userInput);
                System.out.println("Would you like to continue modifications of the DB? (y/n)");
                userInput = scanner.nextLine();
                boolean decision = checkYesOrNo(userInput);
                if(decision == false){
                    continueAsking = false;
                }
            }
            // updating
            else if(userInput.toUpperCase().equals("UPDATE")){
                Dictionary<String, String> data = collectMemberInfo();
                updateMember(conn, data);
                System.out.println("Would you like to continue modifications of the DB? (y/n)");
                userInput = scanner.nextLine();
                boolean decision = checkYesOrNo(userInput);
                if(decision == false){
                    continueAsking = false;
                }
            }
            else if (userInput.toUpperCase().equals("QUIT")){
                continueAsking = false;
            }
            else{
                System.out.println("Sorry, we could not understand your request. Please type insert, delete, or quit");
            }
        }

        System.out.println("Quitting modification of members");
    }

    /*---------------------------------------------------------------------
    |  Method: collectMemberInfo()
    |
    |  Purpose: This method is used to collect the information for a new 
    |            tuple to add to the member relation from the user
    |
    |  Pre-condition:  Global scanner is active
    |
    |  Post-condition: Global scanner is active
    |
    |  Parameters: None
    |	   
    |
    |  Returns: Dictionary data - a <String, String> dictionary with keys as
    |           relation field names and values as values collected from user 
    |
    |				   
	*-------------------------------------------------------------------*/
    private static Dictionary<String, String> collectMemberInfo(){
        Dictionary<String,String> newData = new Hashtable<String, String>();
        System.out.println("What is member's first name?");
        newData.put( "first_name", scanner.nextLine());

        System.out.println("What is member's last name?");
        newData.put( "second_name", scanner.nextLine());

        System.out.println("What is member's phone number?");
        newData.put( "telephone_number", scanner.nextLine());

        System.out.println("What is member's home address?");
        newData.put( "home_address", scanner.nextLine());

        newData.put("money_spent", "0");
        newData.put("num_of_visits", "1");
        newData.put("ticket_total", "0");
        newData.put("token_total", "0");
        return newData;


    }

    /*---------------------------------------------------------------------
    |  Method: checkYesOrNo()
    |
    |  Purpose: Validate if the user has entered yes or no. This is used by
    |           many UI functions above.
    |
    |  Pre-condition:  Global scanner is active
    |
    |  Post-condition: Global scanner is active
    |
    |  Parameters: String userInput - the original input given by the user
    |	   
    |
    |  Returns: Boolean - true if user says yes, false if user says no 
    |
    |				   
	*-------------------------------------------------------------------*/
    private static boolean checkYesOrNo(String userInput) {
        boolean continueAsking = true;
        String input = userInput;
        // continue until valid input
        while(continueAsking){
            if(input.toUpperCase().equals("Y") || input.toUpperCase().equals("YES")){
                return true;
            }
            else if(input.toUpperCase().equals("N") || input.toUpperCase().equals("NO")){
                return false;
            }
            else{
                System.out.println("Sorry, we could not understand you");
                System.out.println("Please type \"YES\" or \"NO\"");
                input = scanner.nextLine();
            }

        }

        return false; // needed or else java gets angry
        
    }

    /*---------------------------------------------------------------------
    |  Method: checkYesOrNo()
    |
    |  Purpose: Validate if the user has entered yes or no. This is used by
    |           many UI functions above.
    |
    |  Pre-condition:  Global scanner is active, Connection is active
    |
    |  Post-condition: Global scanner is active, Connection is active
    |
    |  Parameters: Connection conn- connection to oracle
    |              String prefix - prefix all tables should be using
    |	   
    |
    |  Returns: int - returns 1 if the user wants to continue the program
    |                 returns 0 if the user wants to quit the program 
    |
    |				   
	*-------------------------------------------------------------------*/
    private static int queryUIFunc(Connection conn, String prefix) {
        boolean continueAsking = true;
        
        while(continueAsking){
            System.out.println("====================================================================");
            System.out.println("You are currently in Query mode");
            System.out.println("Here are the queries you can select:");
            System.out.println("Query1: High scores & their holders for each game");
            System.out.println("Query2: Members who've spent >= $100 this month on tokens");
            System.out.println("Query3: Prizes able to be purchased by given member");
            System.out.println("Query4: Given a member ID, show history of prizes bought by member");
            System.out.println("====================================================================");
            System.out.println("Please enter the number of the query you would like to run [1,2,3,4]");
            String userInput = scanner.nextLine();
            try{
                int queryNum = Integer.parseInt(userInput);
                if(queryNum == 1){
                    queryOne(conn, prefix);
                }else if(queryNum == 2){
                    queryTwo(conn, prefix);
                }else if(queryNum == 3){
                    queryThree(conn, prefix);
                }else if(queryNum == 4){
                    queryFour(conn, prefix);
                }else{
                    System.out.println("Sorry, the number chosen is invalid. Please type a number 1-4");
                }

                System.out.println("Would you like to continue in query mode? (y/n)");
                userInput = scanner.nextLine();
                boolean decision = checkYesOrNo(userInput);
                // time to ask the user what mode they want
                if(decision == false){
                    return 1;
                }
                    
                
            }catch(Exception e){
                System.out.println("Sorry, the input contained non numerics, please type a number 1-4");
            }
        }

        // user has selected to quit the program
        return 0;
    }
    

    // ========================================================================
    // FOR ORGANIZATION PURPOSES, ALL MODIFICATION METHODS ARE BELOW THIS LINE
    // ========================================================================

    /*---------------------------------------------------------------------
    |  Method: insertMember(conn, attrs)
    |
    |  Purpose: Insert a single member tuple into the member table.
    |
    |  Pre-condition:  DB connection established. Member table editable.
    |
    |  Post-condition: DB connection established. New member tuple added.
    |
    |  Parameters: 
    |       conn - The connection to the DB.
    |       attrs - A dictionary of all related attributes.
    |
    |  Returns: None.
    |
    |				   
	*-------------------------------------------------------------------*/
    /**
     * Inserts a new Member tuple into the Member table within the DB. Requires all relevant attributes (excluding member_id) within the dictionary to continue.
     * @param conn - The connection to the DB.
     * @param attrs - A dictionary containing the relevant attributes of the Member table.
     */
    private static void insertMember(Connection conn, Dictionary<String, String> attrs) {
        if (attrs.size() < 8) {
            System.out.println("Missing attributes. Please ensure all attributes have been entered.");
            return;
        }
        if (ensureMemberValuesValid(attrs) < 8) {
            System.out.println("Some attributes are invalid. Please ensure all attributes use the correct format.");
            return;
        }

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT max(nathanieljmette.member.member_id) FROM nathanieljmette.member");
            if (rs.next()) {
                attrs.put("member_id", Integer.toString(rs.getInt(1)+1));
            } else {
                attrs.put("member_id", "1");
            }
        } catch (SQLException e) {
            System.err.println("SQLException: Unable to execute query. Is the connection setup properly?");
            System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        String command = "INSERT INTO nathanieljmette.member VALUES(" +
                            attrs.get("member_id") + "," +
                            "\'" +attrs.get("first_name")+ "\'" +"," +
                            "\'" +attrs.get("second_name")+ "\'"  +"," +
                            "\'" +attrs.get("telephone_number")+ "\'"+"," +
                            "\'" +attrs.get("home_address") + "\'"+"," +
                            attrs.get("money_spent") +"," +
                            attrs.get("num_of_visits") +"," +
                            attrs.get("ticket_total") +"," +
                            attrs.get("token_total") +
                            ")";
        executeUpdate(conn, command);
    }

    /*---------------------------------------------------------------------
    |  Method: insertGame(conn, attrs)
    |
    |  Purpose: Insert a single game tuple into the game table.
    |
    |  Pre-condition:  DB connection established. Game table editable.
    |
    |  Post-condition: DB connection established. New game tuple added.
    |
    |  Parameters: 
    |       conn - The connection to the DB.
    |       attrs - A dictionary of all related attributes.
    |
    |  Returns: None.
    |
    |				   
	*-------------------------------------------------------------------*/
    /**
     * Inserts a new Game tuple into the Game table within the DB. Requires all relevant attributes (excluding game_id) within the dictionary to continue.
     * @param conn - The connection to the DB.
     * @param attrs - A dictionary containing the relevant attributes of the Game table.
     */
    private static void insertGame(Connection conn, Dictionary<String, String> attrs) {
        if (attrs.size() < 5) {
            System.out.println("Missing attributes. Please ensure all attributes have been entered.");
            return;
        }
        if (ensureGameValuesValid(attrs) < 5) {
            System.out.println("Some attributes are invalid. Please ensure all attributes use the correct format.");
            return;
        }

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT max(nathanieljmette.game.game_id) FROM nathanieljmette.game");
            if (rs.next()) {
                attrs.put("game_id", Integer.toString(rs.getInt(1)+1));
            } else {
                attrs.put("game_id", "1");
            }
        } catch (SQLException e) {
            System.err.println("SQLException: Unable to execute query. Is the connection setup properly?");
            System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        String command = "INSERT INTO nathanieljmette.game VALUES(" +
                            attrs.get("game_id") + "," +
                            "\'"+ attrs.get("game_name")+ "\'" +"," +
                            attrs.get("min_tickets") +"," +
                            attrs.get("max_tickets") +"," +
                            attrs.get("ticket_score_multiplier") +"," +
                            attrs.get("tokens_to_play") +
                            ")";
        executeUpdate(conn, command);
    }

    /*---------------------------------------------------------------------
    |  Method: insertPrize(conn, attrs)
    |
    |  Purpose: Insert a single prize tuple into the prize table.
    |
    |  Pre-condition:  DB connection established. Prize table editable.
    |
    |  Post-condition: DB connection established. New prize tuple added.
    |
    |  Parameters: 
    |       conn - The connection to the DB.
    |       attrs - A dictionary of all related attributes.
    |
    |  Returns: None.
    |
    |				   
	*-------------------------------------------------------------------*/
    /**
     * Inserts a new Prize tuple into the Prize table within the DB. Requires all relevant attributes (excluding prize_id) within the dictionary to continue.
     * @param conn - The connection to the DB.
     * @param attrs - A dictionary containing the relevant attributes of the Prize table.
     */
    private static void insertPrize(Connection conn, Dictionary<String, String> attrs) {
        if (attrs.size() < 4) {
            System.out.println("Missing attributes. Please ensure all attributes have been entered.");
            return;
        }
        if (ensurePrizeValuesValid(attrs) < 4) {
            System.out.println("Some attributes are invalid. Please ensure all attributes use the correct format.");
            return;
        }

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT max(nathanieljmette.prize.prize_id) FROM nathanieljmette.prize");
            if (rs.next()) {
                attrs.put("prize_id", Integer.toString(rs.getInt(1)+1));
            } else {
                attrs.put("prize_id", "1");
            }
        } catch (SQLException e) {
            System.err.println("SQLException: Unable to execute query. Is the connection setup properly?");
            System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        String command = "INSERT INTO nathanieljmette.prize VALUES(" +
                            attrs.get("prize_id") + "," +
                            "\'" + attrs.get("prize_name")+ "\'" +"," +
                            attrs.get("prize_price") +"," +
                            attrs.get("stock") +"," +
                            attrs.get("active") +
                            ")";
        executeUpdate(conn, command);
    }

    /*---------------------------------------------------------------------
    |  Method: deleteTuple(conn, tableName, id)
    |
    |  Purpose: Delete a tuple from the passed table with the passed id.
    |           Will also remove any related tuples from other relations to
    |           prevent deletion anomalies.
    |
    |  Pre-condition:  DB connection established. All related tables editable.
    |
    |  Post-condition: DB connection established. All related tuples deleted.
    |
    |  Parameters: 
    |       conn - The connection to the DB.
    |       tableName - The name of the table to delete a tuple from.
    |       id - The ID of the tuple to remove.
    |
    |  Returns: Boolean stating if method executed properly.
    |
    |				   
	*-------------------------------------------------------------------*/
    /**
     * Deletes a tuple from the passed table with the corresponding ID value.
     * Removes tuples from corresponding tables to prevent deletion anomolies.
     * @param conn - The connection to the DB.
     * @param tableName - The name of the table to remove from.
     * @param id - The ID value to remove.
     * @return - Boolean stating if the program was able to execute properly. Should return false if ID in incorrect format.
     */
    private static boolean deleteTuple(Connection conn, String tableName, String id) {
        if (!id.matches("\\d+")) return false;
        tableName = tableName.toLowerCase();
        if (tableName.equals("nathanieljmette.member")) {
            deleteTupleHelper(conn, "nathanieljmette.money_to_token_xact", "nathanieljmette.money_to_token_xact.member_id", id);
            deleteTupleHelper(conn, "nathanieljmette.ticket_to_prize_xact", "nathanieljmette.ticket_to_prize_xact.member_id", id);
            deleteTupleHelper(conn, "nathanieljmette.gameplay_instance", "nathanieljmette.gameplay_instance.member_id", id);
            deleteTupleHelper(conn, tableName, "nathanieljmette.member.member_id", id);
        } else if (tableName.equals("nathanieljmette.game")) {
            deleteTupleHelper(conn, "nathanieljmette.gameplay_instance", "nathanieljmette.gameplay_instance.game_id", id);
            deleteTupleHelper(conn, tableName, "nathanieljmette.game.game_id", id);

        } else if (tableName.equals("nathanieljmette.prize")) {
            deleteTupleHelper(conn, "nathanieljmette.ticket_to_prize_xact", "nathanieljmette.ticket_to_prize_xact.prize_id", id);
            deleteTupleHelper(conn, tableName, "nathanieljmette.prize.prize_id", id);

        }
        return true;
    }

    /*---------------------------------------------------------------------
    |  Method: deleteTupleHelper(conn, tableName, id_type, id)
    |
    |  Purpose: Delete a single tuple from the passed table.
    |
    |  Pre-condition:  DB connection established. Passed table editable.
    |
    |  Post-condition: DB connection established. Removed relevant tuple.
    |
    |  Parameters: 
    |       conn - The connection to the DB.
    |       tableName - The table to modify.
    |       id_type - The attribute to search for.
    |       id - The ID corresponding to the tuple to be deleted.
    |
    |  Returns: None.
    |
    |				   
	*-------------------------------------------------------------------*/
    /**
     * Creates the deletion command and executes a delete command for the DB.
     * @param conn - The connection to the DB.
     * @param tableName - The name of the table to modify.
     * @param id_type - The name of the attribute to search for.
     * @param id - The ID corresponding to the tuple to be deleted.
     */
    private static void deleteTupleHelper(Connection conn, String tableName, String id_type, String id) {
        String command = "DELETE FROM " + tableName + " WHERE " + id_type + "=" + id;
        executeUpdate(conn, command);
    }

    /*---------------------------------------------------------------------
    |  Method: updateMember()
    |
    |  Purpose: Validate if the user has entered yes or no. This is used by
    |           many UI functions above.
    |
    |  Pre-condition:  Global scanner is active
    |
    |  Post-condition: Global scanner is active
    |
    |  Parameters: Connection conn - connection to oracle
    |               Dictionary <String, String> attrs - dictionary with keys as
    |               field names and values as values to be inserted to that field
    |               name
    |	   
    |
    |  Returns: None 
    |
    |				   
	*-------------------------------------------------------------------*/
    private static void updateMember(Connection conn, Dictionary<String, String> attrs){
        if (attrs.size() < 8) {
            System.out.println("Missing attributes. Please ensure all attributes have been entered.");
            return;
        }
        if (ensureMemberValuesValid(attrs) < 8) {
            System.out.println("Some attributes are invalid. Please ensure all attributes use the correct format.");
            return;
        }

        System.out.println("what is the member's ID");
        attrs.put("member_id", scanner.nextLine());

        // sql command construction
        String command = "UPDATE nathanieljmette.member SET first_name = "+  "\'" + attrs.get("first_name")+ "\'"
         + " , second_name = "+  "\'" +  attrs.get("second_name")+ "\'" +
                        " , telephone_number = " +  "\'"+ attrs.get("telephone_number")+ "\'" + " , home_address = " 
                        +  "\'"+attrs.get("home_address")+ "\'" +
                        " WHERE member_id = " + attrs.get("member_id");
        executeUpdate(conn, command);
    }

    /*---------------------------------------------------------------------
    |  Method: executeUpdate(conn, command)
    |
    |  Purpose: Execute a SQL update command with the passed SQL command.
    |
    |  Pre-condition:  DB connection established. Command valid.
    |
    |  Post-condition: DB connection established. Command complete.
    |
    |  Parameters: 
    |       conn - The connection to the DB.
    |       command - The SQL update command to complete.
    |
    |  Returns: None.
    |
    |				   
	*-------------------------------------------------------------------*/
    /**
     * Executes an SQL update command. Includes insert, update, and delete commands.
     * @param conn - The conncetion to the database.
     * @param command - The SQL command to execute.
     */
    private static void executeUpdate(Connection conn, String command) {
        Statement stmt = null;
        try {
            System.out.println(command);
            stmt = conn.createStatement();
            stmt.executeUpdate(command);
        } catch (SQLException e) {
            System.err.println("SQLException: Unable to execute update. Are you missing a value?");
            System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    // ========================================================================
    // FOR ORGANIZATION PURPOSES, ALL ATTR VERIFICATION METHODS ARE BELOW THIS LINE
    // ========================================================================

    /*---------------------------------------------------------------------
    |  Method: ensureMemberValuesValid(attrs)
    |
    |  Purpose: Reads through a dictionary of all the different values associated
    |           with the member table and ensures they are in proper format.
    |
    |  Pre-condition:  Dictionary populated.
    |
    |  Post-condition: Dictionary populated.
    |
    |  Parameters: 
    |       attrs - A dictionary of all related attributes.
    |
    |  Returns: Int stating the count of usable attributes within the dictionary.
    |
    |				   
	*-------------------------------------------------------------------*/
    /**
     * Check to see if values within a dictionary corresponding to the attributes of the Member table can be input into the DB.
     * @param attrs - Dictionary holding the attributes to be verified if they can be utilized. Keys should reference the attribute name as within the DB.
     * @return Number of attributes that are valid to use. If return matches size of Dictionary passed, then all values are usable.
     */
    private static int ensureMemberValuesValid(Dictionary<String, String> attrs) {
        int matches = 0;
        if (attrs.get("member_id") != null && attrs.get("member_id").matches("\\d+")) matches++;
        if (attrs.get("first_name") != null && attrs.get("first_name").length() <= 255) matches++;
        if (attrs.get("second_name") != null && attrs.get("second_name").length() <= 255) matches++;
        if (attrs.get("telephone_number") != null && attrs.get("telephone_number").matches("\\d{10}")) matches++;
        if (attrs.get("home_address") != null && attrs.get("home_address").length() <= 255) matches++;
        if (attrs.get("money_spent") != null && attrs.get("money_spent").matches("\\d*\\.?\\d{1,2}")) matches++;
        if (attrs.get("num_of_visits") != null && attrs.get("num_of_visits").matches("\\d+")) matches++;
        if (attrs.get("ticket_total") != null && attrs.get("ticket_total").matches("\\d+")) matches++;
        if (attrs.get("token_total") != null && attrs.get("token_total").matches("\\d+")) matches++;
        return matches;
    }

    /*---------------------------------------------------------------------
    |  Method: ensureGameValuesValid(attrs)
    |
    |  Purpose: Reads through a dictionary of all the different values associated
    |           with the game table and ensures they are in proper format.
    |
    |  Pre-condition:  Dictionary populated.
    |
    |  Post-condition: Dictionary populated.
    |
    |  Parameters: 
    |       attrs - A dictionary of all related attributes.
    |
    |  Returns: Int stating the count of usable attributes within the dictionary.
    |
    |				   
	*-------------------------------------------------------------------*/
    /**
     * Check to see if values within a dictionary corresponding to the attributes of the Game table can be input into the DB.
     * @param attrs - Dictionary holding the attributes to be verified if they can be utilized. Keys should reference the attribute name as within the DB.
     * @return Number of attributes that are valid to use. If return matches size of Dictionary passed, then all values are usable.
     */
    private static int ensureGameValuesValid(Dictionary<String, String> attrs) {
        int matches = 0;
        if (attrs.get("game_id") != null && attrs.get("game_id").matches("\\d+")) matches++;
        if (attrs.get("game_name") != null && attrs.get("game_name").length() <= 255) matches++;
        if (attrs.get("min_tickets") != null && attrs.get("min_tickets").matches("\\d+")) matches++;
        if (attrs.get("max_tickets") != null && attrs.get("max_tickets").matches("\\d+")) matches++;
        if (attrs.get("ticket_score_multiplier") != null && attrs.get("ticket_score_multiplier").matches("\\d*\\.?\\d+")) matches++;
        if (attrs.get("tokens_to_play") != null && attrs.get("tokens_to_play").matches("\\d+")) matches++;
        return matches;
    }

    /*---------------------------------------------------------------------
    |  Method: ensurePrizeValuesValid(attrs)
    |
    |  Purpose: Reads through a dictionary of all the different values associated
    |           with the prize table and ensures they are in proper format.
    |
    |  Pre-condition:  Dictionary populated.
    |
    |  Post-condition: Dictionary populated.
    |
    |  Parameters: 
    |       attrs - A dictionary of all related attributes.
    |
    |  Returns: Int stating the count of usable attributes within the dictionary.
    |
    |				   
	*-------------------------------------------------------------------*/
    /**
     * Check to see if values within a dictionary corresponding to the attributes of the Prize table can be input into the DB.
     * @param attrs - Dictionary holding the attributes to be verified if they can be utilized. Keys should reference the attribute name as within the DB.
     * @return Number of attributes that are valid to use. If return matches size of Dictionary passed, then all values are usable.
     */
    private static int ensurePrizeValuesValid(Dictionary<String, String> attrs) {
        int matches = 0;
        if (attrs.get("prize_id") != null && attrs.get("prize_id").matches("\\d+")) matches++;
        if (attrs.get("prize_name") != null && attrs.get("prize_name").length() <= 255) matches++;
        if (attrs.get("prize_price") != null && attrs.get("prize_price").matches("\\d*\\.?\\d{1,2}")) matches++;
        if (attrs.get("stock") != null && attrs.get("stock").matches("\\d+")) matches++;
        if (attrs.get("active") != null && attrs.get("active").matches("0|1")) matches++;
        return matches;
    }


    // ========================================================================
    // FOR ORGANIZATION PURPOSES, ALL QUERY METHODS ARE BELOW THIS LINE
    // ========================================================================

    /*---------------------------------------------------------------------
    |  Method queryFour(Connection conn, String testPrefix)
    |
    |  Purpose: For a given member_ID, lists all of the prizes that member has purchased.
    |           This is non-trivial because the company can try to offer new, unique prizes
    |           to regulars who have earned many of the products previously or currently in
    |           stock.
    |
    |  Pre-condition:  A connection must be made to the oracle database
    |
    |  Post-condition: Displays the prizes previous purchased by a member
    |
    |  Parameters:
    |	   Connection conn - The connection to the oracle database
    |	   String testPrefix - A prefix to the table names to properly access each table
    |
    |  Returns:  None.
    |
    |				   
	*-------------------------------------------------------------------*/
    private static void queryFour(Connection conn, String testPrefix) {
        System.out.println("Please provide a member ID:");
        String memberIDstr = scanner.nextLine();
        //verifies that provided input is an integer
        try {
            int memberID = Integer.parseInt(memberIDstr);
        } catch (Exception e) {
            System.out.println("Invalid member ID format, it must be all integers");
        }
        String sql4 = "SELECT p.prize_name, p.prize_price " +
                    "FROM "+testPrefix+"prize p " +
                    "JOIN "+testPrefix+"ticket_to_prize_xact tpx ON tpx.prize_id = p.prize_id " +
                    "JOIN "+testPrefix+"member m ON tpx.member_id = m.member_id " +
                    "WHERE m.member_id = " + memberIDstr;

        try {
            executeQuery(conn, sql4, new String[]{"Prize_Name", "Prize_Price"});
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /*---------------------------------------------------------------------
    |  Method queryThree(Connection conn, String testPrefix)
    |
    |  Purpose: For a given member, list all arcade rewards that they can purchase with their tickets.
    |
    |  Pre-condition:  A connection must be made to the oracle database
    |
    |  Post-condition: Displays the prizes a given member can purchase with their tickets
    |
    |  Parameters:
    |	   Connection conn - The connection to the oracle database
    |	   String testPrefix - A prefix to the table names to properly access each table
    |
    |  Returns:  None.
    |
    |				   
	*-------------------------------------------------------------------*/
    private static void queryThree(Connection conn, String testPrefix) {
        System.out.println("Please provide a member ID:");
        String memberIDstr = scanner.nextLine();
        //verifies that provided input is an integer
        try {
            int memberID = Integer.parseInt(memberIDstr);
        } catch (Exception e) {
            System.out.println("Invalid member ID format, it must be all integers");
        }
        String sql3 = "SELECT Prize.Prize_Name "+
                      "FROM " + testPrefix + "Prize " + 
                      "WHERE EXISTS (SELECT " + "ticket_total FROM " + testPrefix + "member WHERE member_id=" + memberIDstr + 
                      " AND " + testPrefix + "prize.prize_price <= " + testPrefix + "member.ticket_total)";

        try {
            executeQuery(conn, sql3, new String[]{"Prize_Name"});
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

    /*---------------------------------------------------------------------
    |  Method queryTwo(Connection conn, String testPrefix)
    |
    |  Purpose: Give the names and membership information 
    |            of all members who have spent at least $100 on tokens in the past month.
    |
    |  Pre-condition:  A connection must be made to the oracle database
    |
    |  Post-condition: Displays all members who have spent at least $100 in the past month
    |
    |  Parameters:
    |	   Connection conn - The connection to the oracle database
    |	   String testPrefix - A prefix to the table names to properly access each table
    |
    |  Returns:  None.
    |
    |				   
	*-------------------------------------------------------------------*/
    private static void queryTwo(Connection conn, String testPrefix) {
        String sql2 = "SELECT m.First_Name, m.Second_Name, m.Telephone_Number, " +
                      "Total_Spent, " +
                      "CASE " +
                      "WHEN Total_Spent >= (SELECT Required_Money FROM " + testPrefix + "Membership WHERE Tier_Name = 'Diamond') THEN 'Diamond' " +
                      "WHEN Total_Spent >= (SELECT Required_Money FROM " + testPrefix + "Membership WHERE Tier_Name = 'Gold') THEN 'Gold' " +
                      "ELSE 'Silver' " +
                      "END AS Membership_Tier " +
                      "FROM " + testPrefix + "Member m JOIN ( " +
                      "SELECT Member_ID, SUM(USD_Paid) AS Total_Spent " +
                      "FROM " + testPrefix + "Money_to_Token_Xact " +
                      "WHERE Date_Occur > ADD_MONTHS(SYSDATE, -1) " +
                      "GROUP BY Member_ID " +
                      "HAVING SUM(USD_Paid) >= 100 " +
                      ") mt ON m.Member_ID = mt.Member_ID";
        try {
            executeQuery(conn, sql2, new String[]{"First_Name", "Second_Name", "Telephone_Number", "Total_Spent", "Membership_Tier"});
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*---------------------------------------------------------------------
    |  Method queryOne(Connection conn, String testPrefix)
    |
    |  Purpose: List all games in the arcade and the names of the members who have 
    |          the current high scores.
    |
    |  Pre-condition:  A connection must be made to the oracle database
    |
    |  Post-condition: Displays all high scores achieved on each game with the associated member
    |
    |  Parameters:
    |	   Connection conn - The connection to the oracle database
    |	   String testPrefix - A prefix to the table names to properly access each table
    |
    |  Returns:  None.
    |
    |				   
	*-------------------------------------------------------------------*/
    private static void queryOne(Connection conn, String testPrefix) {
        String sql1 = "SELECT gi.score, g.game_name, m.first_name, m.second_name " +
                       "FROM "+testPrefix+"game g " +
                       "JOIN ( " +
                       "    SELECT * " +
                       "    FROM "+testPrefix+"gameplay_instance gi1 " +
                       "    WHERE NOT EXISTS ( " +
                       "        SELECT * " +
                       "        FROM "+testPrefix+"gameplay_instance gi2 " +
                       "        WHERE gi2.score > gi1.score AND gi2.game_id = gi1.game_id " +
                       "    ) " +
                       ") gi ON g.game_id = gi.game_id " +
                       "JOIN "+testPrefix+"member m ON m.member_id = gi.member_id";
     
        System.out.println(sql1);
        try {
            executeQuery(conn, sql1, new String[]{"Score", "Game_Name", "First_Name", "Second_Name"});
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}