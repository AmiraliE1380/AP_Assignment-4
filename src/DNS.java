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
                if(dataInputStream.readUTF().startsWith("bank")) {
                    handleBank(dataInputStream);
                } else {
                    handleClient(clientSocket, dataInputStream);
                }
                clientSocket.close();
            } catch (IOException e) {}
        }
    }

    private void handleClient(Socket clientSocket, DataInputStream dataInputStream) throws IOException {
        DataOutputStream dataOutputStream =
                new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
        dataOutputStream.writeInt(bankPorts.get(dataInputStream.readUTF().substring("client".length())));
        dataOutputStream.flush();
    }

    private void handleBank(DataInputStream dataInputStream) throws IOException {
        int bankPort = Integer.parseInt(dataInputStream.readUTF().split(",")[0].substring("bank".length()));
        String bankName = dataInputStream.readUTF().replace("bank" + bankPort + ",", "");
        bankPorts.put(bankName, bankPort);
    }

    public int getBankServerPort(String bankName) {
        return bankPorts.get(bankName);
    }
}
