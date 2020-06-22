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
        serverSocket = new ServerSocket(dnsPort);
        numOfBanks = dnsPort + 1;
        run();
    }

    private void run() {
        while(true) {
            try {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket, this).start();
            } catch (IOException e) {}
        }
    }

    private synchronized void handle(DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException{
        if(dataInputStream.readUTF().startsWith("bank")) {
            handleBank(dataInputStream, dataOutputStream);
        } else {
            handleClient(dataInputStream, dataOutputStream);
        }
    }

    private void handleClient(DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(bankPorts.get(dataInputStream.readUTF().substring("client".length())));
        dataOutputStream.flush();
    }

    private void handleBank(DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        String bank = dataInputStream.readUTF().substring("bank,".length());
        bankPorts.put(bank, numOfBanks);
        dataOutputStream.writeInt(numOfBanks++);
        dataOutputStream.flush();
    }

    public int getBankServerPort(String bankName) {
        return bankPorts.get(bankName);
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
                handleClient();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleClient() throws IOException {
            dnsServer.handle(dataInputStream, dataOutputStream);
            clientSocket.close();
        }
    }
}