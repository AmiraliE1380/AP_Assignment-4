import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class BankClient {
    private int bankPort;
    private Socket socket;
    private static final String PATH = "./src/";          // intellij
//    private static final String PATH = "./src/main/java/";  // quera

    public BankClient(String bankName, int dnsServerPort) throws IOException {
        socket = new Socket("127.0.0.1", dnsServerPort);
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        DataInputStream dataInputStream = new DataInputStream(new DataInputStream(socket.getInputStream()));
        dataOutputStream.writeUTF(bankName);
        dataOutputStream.flush();
        bankPort = dataInputStream.readInt();
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
