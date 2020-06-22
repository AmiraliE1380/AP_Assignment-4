import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;
import static org.junit.Assert.*;

public class BlockingRequestsTest {
    static DNS dnsServer;
    static BankServer server1;
    static BankServer server2;
    static final int DNS_PORT = 8080;


    @BeforeClass
    public static void createServers_B() throws IOException {
        dnsServer = new DNS(DNS_PORT);
        assertEquals(-1, dnsServer.getBankServerPort("mellat"));
        server1 = new BankServer("mellat", DNS_PORT);
        assertNotEquals(-1, dnsServer.getBankServerPort("mellat"));
        server2 = new BankServer("melli", DNS_PORT);
    }

    @Test
    public void testClientCreation_B() throws IOException {
        int priorNumberOfClients = server2.getNumberOfConnectedClients();
        new BankClient("melli", DNS_PORT);
        assertEquals(priorNumberOfClients + 1, server2.getNumberOfConnectedClients());
        //...
    }

    @Test
    public void testSingleServerSingleClient_B() throws IOException {
        BankClient client1 = new BankClient("mellat", DNS_PORT);
        assertEquals(0, server1.getBalance(111));
        assertEquals(0, server1.getBalance(222));
        client1.sendTransaction(111, +7);
        client1.sendTransaction(222, +7);
        client1.sendTransaction(111, +5);
        assertEquals(7, server1.getBalance(222));
        assertEquals(12, server1.getBalance(111));
        //...
    }


    @Test
    public void testSingleServerMultiClient_B() throws IOException {
        BankClient bankClient = new BankClient("mellat", DNS_PORT);
        BankClient bankClient1 = new BankClient("mellat", DNS_PORT);
        BankClient bankClient2 = new BankClient("melli", DNS_PORT);
        BankClient bankClient3 = new BankClient("melli", DNS_PORT);
        Assert.assertEquals(server1.getNumberOfConnectedClients(), 2);
        Assert.assertEquals(server2.getNumberOfConnectedClients(), 2);
        bankClient.sendTransaction(10, 1000);
        bankClient2.sendTransaction(11, -10);
        bankClient3.sendTransaction(11, 10);
        bankClient1.sendTransaction(10, -12);
        bankClient.sendTransaction(10, 13);
        bankClient.sendTransaction(12, 12);
        Assert.assertEquals(1001, server1.getBalance(10));
        Assert.assertEquals(12, server1.getBalance(12));
        Assert.assertEquals(0, server2.getBalance(0));
    }
}
