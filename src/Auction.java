import java.io.IOException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

public interface Auction extends java.rmi.Remote
{   
    /**
     * Register a new client
     * @param name
     * @param email
     * @return a unique client
     * @throws RemoteException
     */
    public int registerClient(String name, String email) throws RemoteException;
    
    /**
     * Create a new auction
     * @param clientID the auction owner
     * @param desc item description
     * @param startPrice starting price
     * @param reserve reserve price
     * @return a unique auction ID
     * @throws RemoteException
     */
    public int newAuction(int clientID, String desc, double startPrice, double reserve)
    throws RemoteException;

    /**
     * Close an auction
     * @param clientID
     * @param auctionID
     * @return the final status of the auction, e.g. the winner and final bid
     * @throws RemoteException
     */
    public String closeAuction(int clientID, int auctionID)
    throws RemoteException;

    /**
     * Lists all active auctions
     * @return a formatted string for output at client
     * @throws RemoteException
     */
    public String listActive() throws RemoteException;

    /**
     * Make a new bid on an item
     * @param clientID
     * @param auctionID
     * @param bid
     * @return true if bid successfully placed; false otherwise
     * @throws RemoteException
     */
    public boolean placeBid(int clientID, int auctionID, double bid) throws RemoteException;
}
                
