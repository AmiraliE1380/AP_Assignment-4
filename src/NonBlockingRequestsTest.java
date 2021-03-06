import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.io.IOException;
import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class NonBlockingRequestsTest {
    static DNS dnsServer;
    static BankServer server1;
    static BankServer server2;
    static final int DNS_PORT = 8090;


    @BeforeClass
    public static void createServers_N() throws IOException {
        dnsServer = new DNS(DNS_PORT);
        server1 = new BankServer("mellat", DNS_PORT);
        server2 = new BankServer("melli", DNS_PORT);
    }

    @Test
    public void testSingleServerSingleClient_N() throws IOException {
        BankClient client1 = new BankClient("mellat", DNS_PORT);
        client1.sendAllTransactions("testA", 0);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(11, server1.getBalance(11111));
    }

    @Test
    public void testSingleServerMultiClient_N() throws IOException, InterruptedException {
        DNS dns = new DNS(8080);
        BankServer bankServer1 = new BankServer("Saman", 8080);
        BankServer bankServer2 = new BankServer("Melli", 8080);
        sleep(2000);
        BankClient bankClient = new BankClient("Saman", 8080);
        bankClient.sendTransaction(1, -1);
        sleep(2000);
        bankClient.sendTransaction(1, 1000);
        Assert.assertEquals(1000, bankServer1.getBalance(1));
        //...
    }

    @Test
    public void testMultiServerMultiClient_N() throws IOException {

    }
}
