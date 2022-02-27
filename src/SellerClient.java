import java.rmi.RemoteException;
import java.io.IOException;
import java.util.Scanner;

public class SellerClient extends AuctionClient
{
    public SellerClient()
    {
        super();
        try {
            Scanner s = this.getScanner();
            int clientID = this.getClientID();
            Auction a = this.getAuction();
            System.out.print("Create auction (1) or close auction (2): ");
            int action = s.nextInt();

            switch (action) {
                case 1:
                    s.nextLine();
                    System.out.print("Description: ");
                    String desc = s.nextLine();
                    System.out.print("Starting Price: ");
                    double startPrice = s.nextDouble();
                    System.out.print("Reserve Price: ");
                    double reserve = s.nextDouble();

                    System.out.printf("Auction ID: %d\n", a.newAuction(clientID, desc, startPrice, reserve));
                    break;
                case 2:
                    System.out.print("Auction ID: ");
                    String str = a.closeAuction(clientID, s.nextInt());
                    System.out.println(str);
                    break;
                default:
                    s.close();
                    throw new IOException("Invalid Input");
            }
            s.close();
        }
        catch (RemoteException re) {
            System.out.println(re);
        }
        catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    public static void main(String[] args)
    {
        new SellerClient();
    }
}
