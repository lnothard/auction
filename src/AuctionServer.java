import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class AuctionServer
{

    public AuctionServer()
    {
        try {
            LocateRegistry.createRegistry(1099);
            Auction a = new AuctionImpl();
            Naming.rebind("rmi://localhost/AuctionService", a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        new AuctionServer();
    }
}
