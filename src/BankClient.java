import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class BankClient {
    private int bankPort;
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    private static final String PATH = "./src/";          // intellij
//    private static final String PATH = "./src/main/java/";  // quera

    public BankClient(String bankName, int dnsServerPort) {
        getBankPort(bankName, dnsServerPort);
    }

    private void getBankPort(String bankName, int dnsServerPort) {
        try {
            socket = new Socket("127.0.0.1", dnsServerPort);
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            dataInputStream = new DataInputStream(new DataInputStream(socket.getInputStream()));
            dataOutputStream.writeUTF("client" + bankName);
            dataOutputStream.flush();
            bankPort = dataInputStream.readInt();
            socket.close();
        } catch (IOException e) {}
    }

    public void sendTransaction(int userId, int amount) {

    }

    public void sendAllTransactions(String fileName, final int timeBetweenTransactions) {
        final File file = new File(PATH + fileName);
        
        if (timeBetweenTransactions > 0) {
            try {
                sleep(timeBetweenTransactions);
                //TODO:
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
