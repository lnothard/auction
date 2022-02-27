import org.jgroups.*;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.Util;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class BackendReplica extends ReceiverAdapter
{
    private static final short PUT = 1;
    private static final short PUT_ALL = 2;
    private static final short REMOVE = 3;
    private static final short REPLACE = 4;

    protected static Map<Short, Method> methods;

    static {
        try {
            methods = new HashMap<>(4);
            methods.put(PUT, BackendReplica.class.getMethod("_put",
                    Integer.class,
                    AuctionItem.class));
            methods.put(PUT_ALL, BackendReplica.class.getMethod("_putAll",
                    TreeMap.class));
            methods.put(REMOVE, BackendReplica.class.getMethod("_remove",
                    Integer.class));
            methods.put(REPLACE, BackendReplica.class.getMethod("_replace",
                    Integer.class,
                    AuctionItem.class,
                    AuctionItem.class));
        } catch (NoSuchMethodException nsme) {
            System.out.println(nsme);
        }
    }

    private final JChannel channel;
    protected TreeMap<Integer, AuctionItem> data;
    protected RpcDispatcher rpcDisp;
    protected final RequestOptions callOptions = new RequestOptions(ResponseMode.GET_ALL, 5000);

    public BackendReplica(JChannel channel) throws Exception
    {
        this.channel = channel;
        this.data = new TreeMap<>();
        channel.setReceiver(this);
        channel.connect(AuctionImpl.cluster_name, null, 10000);
        rpcDisp = new RpcDispatcher(channel, this);
        rpcDisp.setMethodLookup(id -> methods.get(id));
    }

    public void stop()
    {
        rpcDisp.stop();
        Util.close(channel);
    }

    public void put(Integer key, AuctionItem value)
    {
        try {
            MethodCall call = new MethodCall(PUT, key, value);
            rpcDisp.callRemoteMethods(null, call, callOptions);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public AuctionItem get(Integer key)
    {
        return data.get(key);
    }

    public AuctionItem remove(Integer key)
    {
        AuctionItem item = data.get(key);
        try {
            MethodCall call = new MethodCall(REMOVE, key);
            rpcDisp.callRemoteMethods(null, call, callOptions);
            return item;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public void replace(Integer key, AuctionItem old, AuctionItem _new)
    {
        try {
            MethodCall call = new MethodCall(REPLACE, key, old, _new);
            rpcDisp.callRemoteMethods(null, call, callOptions);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean isEmpty()
    {
        return data.isEmpty();
    }

    /* callbacks */
    public void _put(Integer key, AuctionItem value)
    {
        data.put(key, value);
    }

    public void _remove(Integer key)
    {
        data.remove(key);
    }

    public void _replace(Integer key, AuctionItem old, AuctionItem _new)
    {
        data.replace(key, old, _new);
    }

    public void _putAll(TreeMap<Integer, AuctionItem> data)
    {
        if (data == null) return;
        for (Map.Entry<Integer, AuctionItem> item : data.entrySet()) {
            this.data.put(item.getKey(), item.getValue());
        }
    }

    public int getNumReplicas()
    {
        return channel.getView().size();
    }

    @Override
    public void getState(OutputStream ostream) throws Exception
    {
        TreeMap<Integer, AuctionItem> copy = new TreeMap<>();
        for (Map.Entry<Integer, AuctionItem> item : data.entrySet()) {
            copy.put(item.getKey(), item.getValue());
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(ostream, 1024))) {
            oos.writeObject(copy);
        }
    }

    @Override
    public void setState(InputStream istream) throws Exception
    {
        TreeMap<Integer, AuctionItem> _new;
        try (ObjectInputStream ois = new ObjectInputStream(istream)) {
            _new = (TreeMap<Integer, AuctionItem>) ois.readObject();
        }
        if (_new != null) {
            _putAll(_new);
        }
    }
}
