import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Command {
  private static String workingDir = System.getProperty("user.dir");
  private static ProcessBuilder ps = new ProcessBuilder();

  public static String stringInput() {
    Scanner stdin = new Scanner(System.in);
    String a = stdin.nextLine();
    return a;
  }

  public static void main(String[] ar) {
    String inputCmd = "";
    ps.directory(new File(workingDir));
    do {
      System.out.print(ps.directory().getAbsolutePath() + "> ");
      inputCmd = stringInput();
      Command.run(inputCmd.split(" "));

    } while (!inputCmd.equalsIgnoreCase("quit"));

  }

  public static String run(String[] args) {
    if (args == null || args.length == 0)
      return "";

    ps = new ProcessBuilder(args);
    String entireOutput = "";

    ps.directory(new File(workingDir));

    if (args[0].equals("cd")) {
      if (args[1].endsWith("..")) {
        String tmp[] = workingDir.split(Pattern.quote(File.separator));
        workingDir = "";
        for (byte i = 0; i < tmp.length - 1; i++)
          workingDir += tmp[i] + File.separator;

        ps.directory(new File(workingDir));

      } else {
        if (new File(workingDir + File.separator + args[1]).exists())
          workingDir += File.separator + args[1];
        ps.directory(new File(workingDir));
      }
    }

    // System.out.println("Current dir " + ps.directory().getAbsolutePath());


    BufferedReader in = null;
    ps.command().add(0, "cmd.exe");
    ps.command().add(1, "/c");
    for (String a : ps.command())
      System.out.print(a + ", ");
    Process pr;
    try {
      pr = ps.start();

      in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        // System.out.println(line);
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
