import org.jgroups.JChannel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class AuctionImpl
extends java.rmi.server.UnicastRemoteObject
implements Auction
{
    public static final String cluster_name = "AuctionCluster";
    private final TreeMap<Integer, Client> clients;
    private final LinkedList<BackendReplica> replicas;
    private BackendReplica local;

    public AuctionImpl() throws RemoteException
    {
        super();
        clients = new TreeMap<>();
        replicas = new LinkedList<>();
        try {
            for (int i = 0; i < 3; i++) addReplica();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Admin();
    }

    private void addReplica() throws Exception
    {
        replicas.addLast(new BackendReplica(new JChannel("udp.xml")));
        local = replicas.getLast();
    }

    private void removeReplica()
    {
        replicas.removeFirst().stop();
        local = replicas.getLast();
    }

    private int getNumReplicas()
    {
        return local.getNumReplicas();
    }

    public synchronized int registerClient(String name, String email) throws RemoteException
    {
        int clientID;
        if (!clients.isEmpty()) clientID = clients.lastKey() + 1;
        else clientID = 1;
        Client c = new Client(clientID, name, email);
        clients.put(clientID, c);
        return clientID;
    }

    public synchronized String listActive() throws RemoteException
    {
        String s = "";
        for (Map.Entry<Integer, AuctionItem> item : local.data.entrySet()) {
            AuctionItem a = item.getValue();
            s = s.concat(String.format("Item: %d\nDescription: %s\nStarting Price: %.2f\nCurrent Bid: %.2f\n\n",
                item.getKey(), a.getItemDescription(), a.getStartingPrice(), a.getCurrentBid()));
        }
        return s;
    }

    public synchronized String closeAuction(int clientID, int auctionID) throws RemoteException
    {
        AuctionItem fin;
        if (clientID == local.get(auctionID).getSeller().getClientID()) {
            fin = local.remove(auctionID);
            if (fin.getHighestBidder() != null) {
                if (fin.getCurrentBid() >= fin.getReservePrice()){
                    return String.format("%s won the auction with a bid of £%.2f, contact: %s", fin.getHighestBidder().getName(),
                            fin.getCurrentBid(), fin.getHighestBidder().getEmailAddr());
                }
            }
            return "Item not sold (reserve price was not reached)";
        }
        return "Unauthorised action (you did not create this auction)";
    }

    public synchronized int newAuction(int clientID, String desc, double startPrice, double reserve) throws RemoteException
    {
        int itemID;
        if (!local.isEmpty()) itemID = local.data.lastKey() + 1;
        else itemID = 1;
        AuctionItem a = new AuctionItem(itemID, desc, startPrice, reserve, clients.get(clientID));
        local.put(itemID, a);
        return itemID;
    }

    public synchronized boolean placeBid(int clientID, int auctionID, double bid) throws RemoteException
    {
        AuctionItem old = local.get(auctionID);
        AuctionItem a = local.get(auctionID);
        if (a.getHighestBidder() == null && bid >= a.getStartingPrice()) {
            a.setCurrentBid(bid);
            a.setHighestBidder(clients.get(clientID));
            local.replace(auctionID, old, a);
            return true;
        }
        else if (a.getHighestBidder() != null && bid > a.getCurrentBid()) {
            a.setCurrentBid(bid);
            a.setHighestBidder(clients.get(clientID));
            local.replace(auctionID, old, a);
            return true;
        }
        return false;
    }

    class Admin extends JFrame implements ActionListener
    {
        JButton addReplica;
        JButton removeReplica;
        JLabel numReplicas;

        public Admin() {
            addReplica = new JButton("Add Replica");
            addReplica.addActionListener(this);
            removeReplica = new JButton("Remove Replica");
            removeReplica.addActionListener(this);
            numReplicas = new JLabel();
            numReplicas.setText(Integer.toString(getNumReplicas()));
            add(addReplica);
            add(removeReplica);
            add(numReplicas);
            setLayout(new FlowLayout());
            setSize(500, 500);
            setVisible(true);
        }

        public void actionPerformed(ActionEvent e)
        {
            try {
                if (e.getSource().equals(addReplica)) addReplica();
                else removeReplica();
                numReplicas.setText(Integer.toString(getNumReplicas()));
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}