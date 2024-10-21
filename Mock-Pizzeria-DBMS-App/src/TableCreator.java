/*
 * Authors: Nathan Mette
 *          Miro Vanek
 *          Jeziel Banos Gonzalez
 *          Daniel
 * 
 * Course: [Redacted]
 * Instructor: Dr. Lester McCann
 * TAs:	Ahmad Musa, Samantha Cox, Priyansh Nayak
 * Due Date: April 29, 2024
 * 
 * Purpose: This program takes in the user's Oracle username and password and creates the
 *          tables being used for the Mock Pizzeria DBMS system.
 *   		
 *          This program is written in Java using Java 16.
 *          
 * Packages: java.sql, java.util.Dictionary, java.util.Scanner, java.Hashtable
 */

/*
 * WARNING: Only run if you are the one meant to store the
 *          tables on your Oracle account.
 */

import java.util.*;
import java.sql.*;

public class TableCreator {
    
    private static Connection dbconn;
    private static Scanner userInput;

    private static Dictionary<String, String> tables;

    private static String admins = "nathanieljmette, dlott, mirovanek, jezielbgon, mccann, ahmadmusa, bode1, priyanshnayak";

    public static void main(String[] args) {
        tableSetup();

        userInput = new Scanner(System.in);
        System.out.print("Enter Oracle username: ");
        String username = userInput.nextLine();
        System.out.print("Enter Oracle password: ");
        String password = userInput.nextLine();

        connectToDB(username, password);

        System.out.print("\n\nEnter names of tables to build ('list' to see all possible inputs): ");
        String input;
        while (!(input = userInput.nextLine()).equalsIgnoreCase("exit")) {
            if (input.charAt(0) == '-') { // We will be removing a table
                dropTable(input.replace("-", ""));
            } else if (input.equalsIgnoreCase("list")) { // Show all possible commands
                System.out.print("Possible commands:\n============================================\n" +
                    "all - Creates all relevant tables.\n" +
                    "clear - Removes all relevant tables.\n" +
                    "Individual tables (add a '-' in front of the name to remove):\n");
                Enumeration<String> keys = tables.keys();
                while (keys.hasMoreElements()) {
                    System.out.print("\t" + keys.nextElement() + "\n");
                }
                System.out.println("exit - Exits the program.\n" +
                    "============================================");
            } else if (input.equalsIgnoreCase("all")) { // Make every table needed
                Enumeration<String> keys = tables.keys();
                while (keys.hasMoreElements()) {
                    createTable(keys.nextElement());
                }
            } else if (input.equalsIgnoreCase("clear")) { // Remove all tables
                Enumeration<String> keys = tables.keys();
                while (keys.hasMoreElements()) {
                    dropTable(keys.nextElement());
                }
            } else { // Create individual table given by user
                createTable(input);
            }
            System.out.print("\n\nEnter names of tables to build ('list' to see all possible inputs): ");
        }
        userInput.close();
        disconnectFromDB();
    }

    /*---------------------------------------------------------------------
    |  Method tableSetup()
    |
    |  Purpose: Creates the values for the dictionary that holds the table names
    |           and parts of the command to create them.
    |
    |  Pre-condition:  None.
    |
    |  Post-condition: None.
    |
    |  Parameters: 
    |	   
    |
    |  Returns:  None.
    |
    |				   
	*-------------------------------------------------------------------*/
    private static void tableSetup() {
        tables = new Hashtable<>();
        tables.put("member", "member_id integer, " + 
                                "first_name varchar2(255), " +
                                "second_name varchar2(255), " +
                                "telephone_number varchar2(10), " +
                                "home_address varchar2(255), " +
                                "money_spent integer, " +
                                "num_of_visits integer, " + 
                                "ticket_total integer, " +
                                "token_total integer, " +
                                "PRIMARY KEY (member_id)");
        tables.put("membership", "tier_name varchar2(255), " +
                                    "discount_percentage float, " +
                                    "required_money integer, " +
                                    "PRIMARY KEY (tier_name)");
        tables.put("money_to_token_xact", "xact_id integer, " +
                                            "member_id integer, " +
                                            "date_occur TIMESTAMP NOT NULL, " +
                                            "usd_paid float, " +
                                            "tokens_bought integer NOT NULL, " +
                                            "PRIMARY KEY (xact_id, member_id)");
        tables.put("coupon", "coupon_id integer, " +
                                "name varchar2(255) NOT NULL, " +
                                "reward varchar2(255) NOT NULL, " +
                                "days_to_aquire integer, " +
                                "expiration_date DATE, " +
                                "PRIMARY KEY (coupon_id)");
        tables.put("ticket_to_prize_xact", "xact_id integer, " +
                                            "member_id integer, " +
                                            "date_occur TIMESTAMP NOT NULL, " +
                                            "prize_id integer NOT NULL, " +
                                            "PRIMARY KEY (xact_id, member_id)");
        tables.put("prize", "prize_id integer, " +
                                "prize_name varchar2(255), " +
                                "prize_price float, " +
                                "stock integer, " +
                                "active varchar2(1), " +
                                "PRIMARY KEY (prize_id)");
        tables.put("gameplay_instance", "gameplay_id integer, " +
                                            "member_id integer, " +
                                            "game_id integer NOT NULL, " +
                                            "score integer, " +
                                            "tickets_won integer, " +
                                            "PRIMARY KEY (gameplay_id)");
        tables.put("game", "game_id integer, " +
                                "game_name varchar2(255) NOT NULL, " +
                                "min_tickets integer, " +
                                "max_tickets integer, " +
                                "ticket_score_multiplier float, " +
                                "tokens_to_play integer, " +
                                "PRIMARY KEY (game_id)");
    }

    /*---------------------------------------------------------------------
    |  Method createTable(tableName)
    |
    |  Purpose: Creates a single table within the DB. Grants permissions to
    |           all relevant parties.
    |
    |  Pre-condition:  DB connection established.
    |
    |  Post-condition: DB connection established. Table created.
    |
    |  Parameters: 
    |	   tableName - The name of the table to create.
    |
    |  Returns:  None.
    |
    |				   
	*-------------------------------------------------------------------*/
    /**
     * Creates a table with the passed name. If old table exists, drop before creating new one.
     * @param tableName - Name of the table to create.
     */
    private static void createTable(String tableName) {
        if (tables.get(tableName) == null) {
            System.out.println("Not a valid table name.");
            return;
        }

        dropTable(tableName);

        String command = "CREATE TABLE " + tableName + " (" + tables.get(tableName) + ")";

        Statement stmt = null;
        try {
            stmt = dbconn.createStatement();
            System.out.println("Attempting to create table: " + tableName);
            stmt.executeUpdate(command);
            System.out.println("Table " + tableName + " created. Giving permissions to all relevant members.");
            stmt.executeUpdate("GRANT SELECT ON " + tableName + " TO " + admins);
            stmt.executeUpdate("GRANT INSERT ON " + tableName + " TO " + admins);
            stmt.executeUpdate("GRANT UPDATE ON " + tableName + " TO " + admins);
            stmt.executeUpdate("GRANT DELETE ON " + tableName + " TO " + admins);
            System.out.println("Finished giving permissions. Table setup for " + tableName + " complete.");
            stmt.close();
        } catch (SQLException e) {
            System.err.println("SQLException: Unable to create or execute a statement.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
            try {
				stmt.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
            userInput.close();
            disconnectFromDB();
            System.exit(-1);
        }
    }

    /*---------------------------------------------------------------------
    |  Method dropTable(tableName)
    |
    |  Purpose: Drops a single table within the DB with the associated name.
    |           If table doesn't exist, method will complete with no issue.
    |
    |  Pre-condition:  DB connection established.
    |
    |  Post-condition: DB connection established. Table dropped.
    |
    |  Parameters: 
    |	   tableName - The name of the table to drop.
    |
    |  Returns:  None.
    |
    |				   
	*-------------------------------------------------------------------*/
    /**
     * Drops the table with the given name.
     * @param tableName - Name of the table to drop.
     */
    private static void dropTable(String tableName) {
        Statement stmt = null;
        try {
            stmt = dbconn.createStatement();

            ResultSet tables = stmt.executeQuery("SELECT table_name FROM user_tables");
            while (tables.next()) {
                if (tables.getString(1).equalsIgnoreCase(tableName)) {
                    stmt.executeUpdate("DROP TABLE " + tableName);
                    break;
                }
            }
            tables.close();

            stmt.close();
        } catch (SQLException e) {
            System.err.println("SQLException: Unable to create or execute a statement.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
            try {
				stmt.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
            userInput.close();
            disconnectFromDB();
            System.exit(-1);
        }
    }

    /*---------------------------------------------------------------------
    |  Method connectToDB(username, password)
    |
    |  Purpose: Establishes a conncetion to the DB with the username and password
    |           provided.
    |
    |  Pre-condition:  DB connection not established.
    |
    |  Post-condition: DB connection established.
    |
    |  Parameters: 
    |       username - The username of the Oracle account.
    |       password - The password of the Oracle account.
    |
    |  Returns:  None.
    |
    |				   
	*-------------------------------------------------------------------*/
    /**
     * Connects to an Oracle database with the passed username and password.
     * @param username - The username of the user running the program.
     * @param password - The password of the user running the program.
     */
    private static void connectToDB(String username, String password) {
		final String oracleURL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
		
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("Uh ohhhhhh. ClassNotFoundException: Error loading Oracle JDBC driver. Perhaps the driver is not on the Classpath?");
			System.exit(-1);
		}
		
		try {
			dbconn = DriverManager.getConnection(oracleURL, username, password);
		} catch (SQLException e) {
			System.err.println("*** SQL Exception: Could not open JDBC connection.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
            userInput.close();
			System.exit(-1);
		}
	}

    /*---------------------------------------------------------------------
    |  Method disconnectFromDB()
    |
    |  Purpose: Closes the connection to the DB.
    |
    |  Pre-condition:  DB connection established.
    |
    |  Post-condition: DB connection closed.
    |
    |  Parameters: 
    |
    |  Returns:  None.
    |
    |				   
	*-------------------------------------------------------------------*/
    /**
     * Disconnects the DB connection if exists.
     */
    private static void disconnectFromDB() {
        try {
            if (dbconn != null) dbconn.close();
        } catch (SQLException e) {
            System.err.println("SQLException: Unable to close DB connection. Maybe it was already closed?");
            System.exit(-1);
        }
    }

}
