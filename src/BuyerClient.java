import java.rmi.RemoteException;
import java.util.Scanner;

public class BuyerClient extends AuctionClient
{
    public BuyerClient()
    {
        super();
        try {
            Scanner s = this.getScanner();
            int clientID = this.getClientID();
            Auction a = this.getAuction();
            System.out.print("ID of item to bid on: ");
            int auctionID = s.nextInt();
            System.out.print("Amount to bid: ");
            double bid = s.nextDouble();

            if (!a.placeBid(clientID, auctionID, bid)) {
                System.out.println("Failed (bid is equal or lower to the current highest bid)");
            }
            s.close();
        }
        catch (RemoteException re) {
            System.out.println(re);
        }
    }

    public static void main(String[] args)
    {
        new BuyerClient();
    }
}
