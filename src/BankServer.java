import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class BankServer {
    private HashMap<Integer, Integer> accounts;
    private int connectedClients;

    public BankServer(String bankName, int dnsPort) throws IOException {
        accounts = new HashMap<>();
        connectedClients = 0;
    }

    public int getBalance(int userId) {
        return accounts.get(userId);
    }

    public int getNumberOfConnectedClients() {
        return 0;
    }
}
