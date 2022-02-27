import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.io.IOException;
import java.util.Scanner;
import java.rmi.Naming;

public class AuctionClient
{
    private int clientID;
    private Scanner s;
    private Auction a;

    public AuctionClient()
    {
        try {
            s = new Scanner(System.in); 
            a = (Auction) Naming.lookup("rmi://localhost/AuctionService");

            System.out.println("Listing Auctions...");
            System.out.println();
            System.out.println(a.listActive());

            System.out.print("Register (1) or Login (2): ");
            int request = s.nextInt();

            switch(request) {
                case 1:
                    s.nextLine();
                    System.out.print("Name: ");
                    String name = s.nextLine();
                    System.out.print("Email Address: ");
                    String email = s.nextLine();
                    clientID = a.registerClient(name, email);
                    System.out.println("Your client ID is: " + clientID);
                    break;
                case 2:
                    System.out.print("Enter ID: ");
                    clientID = s.nextInt();
                    break;
                default:
                    s.close();
                    throw new IOException("Invalid input");
            }
        }
        catch (NotBoundException nbe) {
            System.out.println(nbe);
        }
        catch (MalformedURLException murle) {
            System.out.println(murle);
        }
        catch (RemoteException re) {
            System.out.println(re);
        }
        catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    public int getClientID()
    {
        return clientID;
    }

    public Scanner getScanner()
    {
        return s;
    }

    public Auction getAuction()
    {
        return a;
    }
}