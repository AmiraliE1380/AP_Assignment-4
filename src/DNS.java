import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class DNS {
    private HashMap<String, Integer> bankPorts;
    private ServerSocket serverSocket;

    public DNS(int dnsPort) throws IOException {
        bankPorts = new HashMap<>();
        serverSocket = new ServerSocket(dnsPort);
        waitForBanksAndClients();
    }

    private void waitForBanksAndClients() {
        while(true) {
            try {
                Socket clientSocket = serverSocket.accept();
                DataInputStream dataInputStream =
                        new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                DataOutputStream dataOutputStream =
                        new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                if(dataInputStream.readUTF().startsWith("bank")) {
                    handleBank(clientSocket, dataInputStream, dataOutputStream);
                } else {
                    handleClient(clientSocket, dataInputStream, dataOutputStream);
                }
            } catch (IOException e) {}
        }
    }

    private void handleClient(Socket clientSocket, DataInputStream dataInputStream, DataOutputStream dataOutputStream)
            throws IOException {
        dataOutputStream.writeInt(bankPorts.get(dataInputStream.readUTF().substring("client".length())));
        dataOutputStream.flush();
        clientSocket.close();
    }

    private void handleBank(Socket clientSocket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {

    }

    public int getBankServerPort(String bankName) {
        return bankPorts.get(bankName);
    }
}
