import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class DNS {
    private HashMap<String, Integer> bankPorts;
    private ServerSocket serverSocket;

    public DNS(int dnsPort) throws IOException {
        bankPorts = new HashMap<>();
        serverSocket = new ServerSocket(dnsPort);
        waitForClients();
    }

    private void waitForClients() {
        while(true) {
            try {
                Socket clientSocket = serverSocket.accept();
                DataInputStream dataInputStream =
                        new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                DataOutputStream dataOutputStream =
                        new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                dataOutputStream.writeInt(bankPorts.get(dataInputStream.readUTF()));
                dataOutputStream.flush();
                clientSocket.close();
            } catch (IOException e) {}
        }
    }

    public int getBankServerPort(String bankName) {
        return bankPorts.get(bankName);
    }
}
