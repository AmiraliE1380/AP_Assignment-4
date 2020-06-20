import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class BankServer {
    private HashMap<Integer, Integer> accounts;
    private int connectedClients;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public BankServer(String bankName, int dnsPort) {
        callDNS(bankName, dnsPort);
    }

    private void callDNS(String bankName, int dnsPort) {
        try {
            accounts = new HashMap<>();
            connectedClients = 0;
            socket = new Socket("127.0.0.1", dnsPort);
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            dataOutputStream.writeUTF("bank" + bankName + "," + dnsPort);
            dataOutputStream.flush();
            socket.close();
        } catch(Exception e) {}
    }

    public int getBalance(int userId) {
        return accounts.get(userId);
    }

    public int getNumberOfConnectedClients() {
        return 0;
    }
}
