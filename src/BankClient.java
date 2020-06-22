import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTransaction(int userId, int amount) {
        try {
            socket = new Socket("127.0.0.1", bankPort);
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            dataOutputStream.writeUTF(userId + "," + amount);
            dataOutputStream.flush();
            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataInputStream.readUTF();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendAllTransactions(String fileName, final int timeBetweenTransactions) {
        final File file = new File(PATH + fileName);
        ArrayList<String[]> transactions = readFile(file);

        if (timeBetweenTransactions > 0) {
            try {
                sleep(timeBetweenTransactions);
                for(String[] transaction : transactions) {
                    sendTransaction(Integer.parseInt(transaction[0]), Integer.parseInt(transaction[1]));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<String[]> readFile(File file) {
        ArrayList<String[]> transactions = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()) {
                String transaction = scanner.nextLine();
                transactions.add(transaction.split("\\s"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}
