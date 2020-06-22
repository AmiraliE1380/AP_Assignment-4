import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class DNS {
    private HashMap<String, Integer> bankPorts;
    private ServerSocket serverSocket;
    private int numOfBanks; //this number must be subtracted by 8000

    public DNS(int dnsPort) throws IOException {
        bankPorts = new HashMap<>();
        serverSocket = new ServerSocket(dnsPort);
        numOfBanks = 8000;
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
                    handleBank(dataInputStream, dataOutputStream);
                } else {
                    handleClient(dataInputStream, dataOutputStream);
                }
                clientSocket.close();
            } catch (IOException e) {}
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
}