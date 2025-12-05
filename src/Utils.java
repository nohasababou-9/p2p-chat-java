import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static String historyFileName(String username) {
        return "history_" + username + ".txt";
    }

    public static synchronized void saveIncoming(String username, String line) {
        saveLine(username, "[IN] " + timestamp() + " " + line);
    }

    public static synchronized void saveOutgoing(String username, String line) {
        saveLine(username, "[OUT] " + timestamp() + " " + line);
    }

    private static void saveLine(String username, String text) {
        String fname = historyFileName(username);
        try (FileWriter fw = new FileWriter(fname, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(text);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("[Utils] Failed to write history: " + e.getMessage());
        }
    }

    private static String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
