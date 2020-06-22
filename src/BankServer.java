import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class BankServer {
    private HashMap<Integer, Integer> accounts;     //First Integer is account ID, second is balance
    private int connectedClients;
    private int port;
    private Socket socket;
    private ServerSocket serverSocket;
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
            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOutputStream.writeUTF("bank," + bankName);
            dataOutputStream.flush();
            port = dataInputStream.readInt();
            socket.close();
            waitForClients();
        } catch(Exception e) {}
    }

    private void waitForClients() {
        try {
            serverSocket = new ServerSocket(port);
            while(true) {
                socket = serverSocket.accept();
                executeTransaction(socket);
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeTransaction(Socket socket) throws IOException {
        dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        String transaction = dataInputStream.readUTF();
        int userId = Integer.parseInt(transaction.split(",")[0]);
        int amount = Integer.parseInt(transaction.split(",")[1]);
        if(accounts.get(userId) == null) {
            if(amount >= 0) {
                accounts.put(userId, amount);
            }
        } else {
            int balance = accounts.get(userId);
            if(amount >= 0) {
                accounts.replace(userId, amount + balance);
            } else {
                if(balance + amount >= 0) {
                    accounts.replace(userId, balance + amount);
                }
            }
        }
        dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        dataOutputStream.writeUTF("Successful!");
        dataOutputStream.flush();
    }

    public int getBalance(int userId) {
        return accounts.get(userId);
    }

    public int getNumberOfConnectedClients() {
        return connectedClients;
    }
}
