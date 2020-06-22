import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class DNS {
    private HashMap<String, Integer> bankPorts;
    private ServerSocket serverSocket;
    private int numOfBanks; //this number must be subtracted by dnsPort

    public DNS(int dnsPort) throws IOException {
        bankPorts = new HashMap<>();
        numOfBanks = dnsPort + 1;
        new Server(dnsPort, this).start();
    }

    private synchronized void handle(DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException{
        String clientMessage = dataInputStream.readUTF();
        if(clientMessage.startsWith("bank")) {
            handleBank(clientMessage, dataOutputStream);
        } else {
            handleClient(clientMessage, dataOutputStream);
        }
    }

    private void handleClient(String clientMessage, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(bankPorts.get(clientMessage.substring("client".length())));
        dataOutputStream.flush();
    }

    private void handleBank(String clientMessage, DataOutputStream dataOutputStream) throws IOException {
        String bank = clientMessage.substring("bank,".length());
        bankPorts.put(bank, numOfBanks);
        dataOutputStream.writeInt(numOfBanks++);
        dataOutputStream.flush();
    }

    public int getBankServerPort(String bankName) {
        return bankPorts.get(bankName);
    }

    static class Server extends Thread {
        private ServerSocket serverSocket;
        private DNS parent;

        private Server(int dnsPort, DNS parent) throws IOException {
            serverSocket = new ServerSocket(dnsPort);
            this.parent = parent;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new ClientHandler(clientSocket, parent).start();
                } catch (IOException e) {}
            }
        }
    }

    static class ClientHandler extends Thread {
        Socket clientSocket;
        DNS dnsServer;
        DataOutputStream dataOutputStream;
        DataInputStream dataInputStream;

        public ClientHandler(Socket clientSocket, DNS dnsServer) throws IOException {
            dataInputStream =
                    new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            dataOutputStream =
                    new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            this.dnsServer = dnsServer;
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                dnsServer.handle(dataInputStream, dataOutputStream);
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}