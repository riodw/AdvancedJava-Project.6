package internetdatabase;
/**
 *
 * @author RioWeber
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client
{
    public static void main(String[] args)
    {
        try
        {
            //Socket server = new Socket("148.137.146.159", 5678);
            // For Project 6
            Socket server = new Socket("204.186.182.178", 3456);
            Scanner scanner = new Scanner(server.getInputStream());
            PrintWriter output = new PrintWriter(server.getOutputStream(), true);
            Scanner fromKeyboard = new Scanner(System.in);

            while(fromKeyboard.hasNextLine())
            {
                output.println(fromKeyboard.nextLine());
                System.out.println(scanner.nextLine());
            }
            server.close();
            scanner.close();
            output.close();
            fromKeyboard.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
}
