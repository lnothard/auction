import java.io.Serializable;

public class AuctionItem implements Serializable
{
    private int itemID;
    private String itemDescription;
    private double startingPrice;
    private double reservePrice;
    private double currentBid;
    private Client highestBidder;
    private Client seller;

    public AuctionItem(int itemID, String itemDescription, double startingPrice, double reservePrice, Client seller)
    {
        this.itemDescription = itemDescription;
        this.startingPrice = startingPrice;
        this.reservePrice = reservePrice;
        this.itemID = itemID;
        this.seller = seller;
    }

    public int getItemID()
    {
        return itemID;
    }

    public String getItemDescription()
    {
        return itemDescription;
    }

    public double getCurrentBid()
    {
        return currentBid;
    }

    public double getStartingPrice()
    {
        return startingPrice;
    }

    public double getReservePrice()
    {
        return reservePrice;
    }

    public Client getHighestBidder()
    {
        return highestBidder;
    }

    public Client getSeller()
    {
        return seller;
    }

    public void setCurrentBid(double currentBid)
    {
        this.currentBid = currentBid;
    }

    public void setHighestBidder(Client highestBidder)
    {
        this.highestBidder = highestBidder;
    }
}