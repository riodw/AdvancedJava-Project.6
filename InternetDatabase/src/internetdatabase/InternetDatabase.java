package internetdatabase;
/**
 * Date: 11/1/2015
 * @author RioWeber
 */
import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import java.sql.Date;

public class InternetDatabase
{
    /**Main method listens for a client connection on port
    * 3456. Runs continuously.
    **/
    public static void main(String[] args) throws Exception
    {
        ServerSocket server = new ServerSocket(3456);
        TreeMap<String, Socket> clientList = new TreeMap<String, Socket>();
        InternetDatabase web = new InternetDatabase();
        Tester dataBase = new Tester();

        while(true)
        {
            Socket client = server.accept();       
            web.new ClientThreadIN(client, clientList);        

            System.out.println((client.getLocalPort()) + " is connected");
        }    
    }

/**
 *ClientThreadIN thread takes the client socket object and the TreeMap of user to socket.
 * Each thread sends and reads input from its client.
*/
    class ClientThreadIN extends Thread
    {
        Socket clientIN;
        InternetDatabase web;
        TreeMap<String, Socket> clientList;
        Scanner fromClient;
        PrintWriter toClient;
    
        /*Constructor-- Sets up the input and output streams for the
          client socket that this thread handles.
          @param c The client Socket object.
          @param t The TreeMap of Users and their sockets.
        */
        public ClientThreadIN(Socket c,TreeMap<String, Socket> t) throws Exception
        {
            clientIN = c;
            clientList = t;      
            fromClient = new Scanner(clientIN.getInputStream());
            toClient   = new PrintWriter(clientIN.getOutputStream(), true);
            start();
        }
/** Run method executes while thread is alive.
 * The method first prompts the user
 * for a name to use for this chat session. Then the user name and socket is put
 * into the map. Next, the server then sends a list of the connected users as well
 * as some instructions concerning its protocol to the client. After that initial
 * setup, the thread listens for input from the client and directs the input to
 * the correct client output stream by retrieving the appropriate socket from
 * the map.
 */
        @Override
        public void run()
        {
            String s = "";
            String s2 = "";

            toClient.println("Enter user name: ");
            s = fromClient.nextLine();
            clientList.put(s,clientIN);
            toClient.println("Welcome ");
            toClient.println(s);
            toClient.println("Connected users:");

            Set users = clientList.keySet();
            for(Iterator i = users.iterator(); i.hasNext(); )
                toClient.println(i.next());

            toClient.println("Type: User_Name/Message to send message.");
            toClient.println("Type: " + s + "/DONE  to exit.");
            toClient.println("$");

            PrintWriter toPeer = null;

            while(true)
            {
                if(fromClient.hasNextLine())
                {
                    s = fromClient.nextLine();
                    StringTokenizer message = new StringTokenizer(s, "/");
                    String to = message.nextToken();
                    Socket destination = clientList.get(to);
                    try {
                        toPeer = new PrintWriter(destination.getOutputStream(), true);
                    }
                    catch(Exception e) { toClient.println(to + " not available."); } 

                    String text = message.nextToken();
                    String from = message.nextToken();

                    if(text.equals("DONE"))
                    {
                        clientList.remove(to);
                        toPeer.println("Goodbye "+from);
                        toPeer.println(from+":DONE");
                        fromClient.close();
                        toClient.close();
                        try{
                          clientIN.close();
                        }
                        catch (Exception e) {}
                    }
                    else
                    {             
                        toPeer.println(from + ":" + text);
                    }
                }//end if   
            }//end while
        }//end run  
    }//end of ClientThreadIN class
    
    public static class Tester
    {

    public Tester()
    {
        Statement st = null;
        Connection c = null;
        
        try {
            System.out.println("I am here");
        
            new com.mysql.jdbc.Driver();
            System.out.println("The Drivr is Loaded.");
            c = DriverManager.getConnection("jdbc:mysql://148.137.9.28/CS3", "rdw77236", "rdw77236");

            System.out.println("Connected successfully!");
            st = c.createStatement();

            //we want to create a table Students
            String sql = "DROP TABLE Users";
            st.executeUpdate(sql);
                System.out.println("-----\nDroped Table");

            sql = "CREATE TABLE Users(id CHAR(5), name VARCHAR(25), "
                    + "address VARCHAR(25),  phoneNumber CHAR(13), "
                    + "balance DOUBLE, birthDate DATE, PRIMARY KEY(id))";
            st.executeUpdate(sql);
                System.out.println("-----\nCreate Table BadStudents");
        
            sql = "INSERT INTO BadStudents VALUES(11111, 'Good Boy', '1 Lucky Street', '(123)456-7890', 1000.0, '1985-08-04')";
            st.executeUpdate(sql);
                System.out.println("-----\nInsert into BadStudnets 1");
                
            sql = "INSERT INTO BadStudents VALUES(22222, 'Good Girl', '2 Lucky Street', '(123)456-7890', 2000.0, '1985-08-04')";
            st.executeUpdate(sql);
                System.out.println("-----\nInsert into BadStudnets 2");
            
            sql = "INSERT INTO BadStudents VALUES(33333, 'Bad Boy', '3 Lucky Street', '(123)456-7890', 3000.0, '1985-08-04')";
            st.executeUpdate(sql);
                System.out.println("-----\nInsert into BadStudnets 3");

            //You may create logical conditions by using the relation operators:
            //      =, !=, >, <, >=, <=, >< and the logical opperators: AND, OR
            //sql = "UPDATE Students SET balance=5000.0 WHERE id=1111";
            //st.executeUpdate(sql);
                
            //************ OUTPUT QUERY ****************//
                sql = "SELECT * FROM BadStudents WHERE balance > 1000.0";
                System.out.println("\nOutPut:");
                ResultSet rs = st.executeQuery(sql);
                while(rs.next())
                    System.out.println(rs.getString("id") + ", " + rs.getString(2) + ", " + rs.getDouble("balance"));

            //./-END QUERY
            sql = "UPDATE BadStudents SET balance=5000.0 WHERE id=11111";
            st.executeUpdate(sql);
                System.out.println("\nUPDATE Student 1 balance");
            //************ OUTPUT QUERY ****************//
                sql = "SELECT * FROM BadStudents WHERE balance > 1000.0";
                System.out.println("\nOutPut:");
                rs = st.executeQuery(sql);
                while(rs.next())
                    System.out.println(rs.getString("id") + ", " + rs.getString(2) + ", " + rs.getDouble("balance"));

            //./-END QUERY
            sql = "DELETE FROM BadStudents WHERE id=11111";
            st.executeUpdate(sql);
                System.out.println("\nDELETE 11111");
            //************ OUTPUT QUERY ****************//
                sql = "SELECT * FROM BadStudents WHERE balance > 1000.0";
                System.out.println("\nOutPut:");
                rs = st.executeQuery(sql);
                while(rs.next())
                    System.out.println(rs.getString("id") + ", " + rs.getString(2) + ", " + rs.getDouble("balance"));

            //./-END QUERY
            sql = "INSERT INTO BadStudents VALUES('55555', 'David', '100 Danville', '(555)123-4567', 6000.0, '1965-12-14')";
            st.executeUpdate(sql);
            System.out.println("\nINSERT INTO 55555");
            
            sql = "SELECT id, name, birthDate FROM BadStudents WHERE birthDate > '1900-01-01' AND birthDate < '2011-12-31'";
            st.executeQuery(sql);
                System.out.println("\nSELECT id, name, birthDate FROM badStudnets");
            //************ OUTPUT QUERY ****************//
                System.out.println("\nOutPut:");
                rs = st.executeQuery(sql);
                while(rs.next())
                    System.out.println(rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getDate(3));
                
                DatabaseMetaData dm = c.getMetaData();
                rs = dm.getTypeInfo();
                    System.out.println("All types this dms can handle: ");
                while(rs.next())
                    System.out.println(rs.getString("TYPE_NAME"));
                
                String[] tables = {"TABLE"};
                rs = dm.getTables(null, null, "%", tables);
                System.out.println("\nTables in out Database: ");
                while(rs.next())
                    System.out.println(rs.getString("TABLE_NAME"));
                
                rs = dm.getColumns(null, null, "BadStudents", "%");
                System.out.println("\nAll COLUMN_NAMES in Database:");
                ResultSetMetaData rsmd = rs.getMetaData();
                int x = rsmd.getColumnCount();
                while(rs.next())
                {
                    for(int i=1; i <= x; i++)
                        System.out.print(rs.getString(i) + " ");
                    System.out.println("");
                }
            //./-END QUERY
                
                //Find out the MAX, MIN, Average balance in our table
            sql = "SELECT MAX(balance), MIN(balance), AVG(balance), COUNT(balance)"
                    + " FROM BadStudents";
            //************ OUTPUT QUERY ****************//
                rs = st.executeQuery(sql);
                    rs.next();
                    System.out.println("The Max: " + rs.getDouble(1) + 
                                     "\nThe Min: " + rs.getDouble(2) +
                                     "\nThe Average: " + rs.getDouble(3) +
                                     "\nNumber of Values: " + rs.getInt(4));
            //./-END QUERY
                    
            sql = "SELECT * FROM BadStudents WHERE balance>? AND balance<?";
            PreparedStatement pst = c.prepareStatement(sql);
            pst.setDouble(1, 1000.0);
            pst.setDouble(2, 5000.0);
            rs = pst.executeQuery();
            while(rs.next())
            {
                System.out.println(rs.getString(1) + " " +
                                   rs.getString("name") + " " +
                                   rs.getDouble("balance"));
            }
            
            sql = "SELECT * FROM BadStudents WHERE birthDate>? AND birthDate<?";
            pst = c.prepareStatement(sql);
            pst.setDate(1, Date.valueOf("1990-05-25"));
            pst.setDate(2, Date.valueOf("2010-12-31"));
            
            rs = pst.executeQuery();
            System.out.println("\nHere are the people you get: ");
            while(rs.next())
            {
                System.out.println(rs.getString(1) + " " +
                                   rs.getString("name") + " " +
                                   rs.getDouble("birthDate"));
            }
            
            c.commit();
            
            //WORKING TILL HERE
            
            try
            {
                sql = "UPDATE BadStudents SET birthDate = '1992-06-02'" +
                        " WHERE id='2222'";
                st.execute(sql);
                sql = "UPDATE BadStudents SET birthDate = '1992-06-02'" +
                        " WHERE id='33333'";
                st.execute(sql);
            } catch(Exception e)
                { c.rollback(); }
            
            rs = pst.executeQuery();
            System.out.println("\nHere are the people you get: ");
            while(rs.next())
            {
                System.out.println(rs.getString(1) + " " +
                                   rs.getString("name") + " " +
                                   rs.getDouble("birthDate"));
            }
            
            c.close();
            st.close();
            
        
        System.out.println("\n***********Working***********");
        }
        catch(Exception e){}
    }
}
}//end of ChatServer class
