import java.io.IOException;
import java.io.Serializable;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import java.security.SecureRandom;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;

public class Client implements Serializable
{
    private int clientID;
    private String emailAddress;
    private String name;

    public Client(int clientID, String name, String emailAddress)
    {
        this.clientID = clientID;
        this.name = name;
        this.emailAddress = emailAddress;
    }

    public String getEmailAddr()
    {
        return emailAddress;
    }

    public String getName()
    {
        return name;
    }

    public int getClientID()
    {
        return clientID;
    }
}
