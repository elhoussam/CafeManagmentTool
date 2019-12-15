import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class shutdown {

  public static void main(String[] args) throws IOException {
    String shutdownCmd = "shutdown -s -t 60";
    Process child = Runtime.getRuntime().exec(shutdownCmd);

    System.out.println("Input " + child.getInputStream().toString());
    BufferedReader in = null;
    in = new BufferedReader(new InputStreamReader(child.getInputStream()));
    String line;
    while ((line = in.readLine()) != null) {
      System.out.println(line);
    }

    System.out.println("Output " + child.getOutputStream().toString());
  }

}
