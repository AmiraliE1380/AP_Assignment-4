import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class BankServer {
    private HashMap<Integer, Integer> accounts;     //First Integer is account ID, second is balance
    private int connectedClients;
    private int port; //TODO: THINK OF A WAY TO GENERATE PORT NUMBERS
    private Socket socket;

    public BankServer(String bankName, int dnsPort) {
        callDNS(bankName, dnsPort);
    }

    private void callDNS(String bankName, int dnsPort) {
        try {
            accounts = new HashMap<>();
            connectedClients = 0;
            socket = new Socket("127.0.0.1", dnsPort);
            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOutputStream.writeUTF("bank," + bankName);
            dataOutputStream.flush();
            port = dataInputStream.readInt();
            socket.close();
            new Server(this).start();
        } catch(Exception e) {}
    }

    private synchronized void executeTransaction(DataInputStream dataInputStream, DataOutputStream dataOutputStream)
            throws IOException {
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
        dataOutputStream.writeUTF("Successful!");
        dataOutputStream.flush();
    }

    public int getBalance(int userId) {
        return accounts.get(userId);
    }

    public int getNumberOfConnectedClients() {
        return connectedClients;
    }

    static class Server extends Thread {
        private BankServer bankServer;

        public Server(BankServer bankServer) {
            this.bankServer = bankServer;
        }

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(bankServer.port);
                while(true) {
                    Socket socket = serverSocket.accept();
                    bankServer.connectedClients++;
                    new ClientHandler(socket, bankServer).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class ClientHandler extends Thread {
        BankServer bankServer;
        Socket socket;
        DataOutputStream dataOutputStream;
        DataInputStream dataInputStream;

        public ClientHandler(Socket socket, BankServer bankServer) throws IOException {
            this.bankServer = bankServer;
            this.socket = socket;
            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        }

        @Override
        public void run() {
            try {
                bankServer.executeTransaction(dataInputStream, dataOutputStream);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bankServer.connectedClients--;
        }
    }
}
