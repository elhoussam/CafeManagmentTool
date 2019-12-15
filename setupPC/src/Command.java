import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Command {

  public static String run(String[] args) {
    if (args == null || args.length == 0)
      return "";
    ProcessBuilder ps = new ProcessBuilder(args);
    String entireOutput = "";
    ps.redirectErrorStream(true);
    BufferedReader in = null;
    Process pr;
    try {
      pr = ps.start();
      in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        System.out.println(line);
        entireOutput += line + "\n";
      }
      pr.waitFor();
      in.close();
    } catch (InterruptedException | IOException e) {
      entireOutput = "The Command does not exist";
    } finally {
      System.out.println("Output : \n " + entireOutput);
      System.out.println("ok!");
      if (in != null)
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
    }
    return entireOutput;
  }
}
