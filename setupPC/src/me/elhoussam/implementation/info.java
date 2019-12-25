package me.elhoussam.implementation;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import me.elhoussam.core.Pc;
import me.elhoussam.core.connection;
import me.elhoussam.interfaces.ManagerPcInterface;
import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;
import me.elhoussam.util.sys.StringHandler;
import me.elhoussam.util.sys.TimeHandler;

public class info extends UnicastRemoteObject implements infoInterface {

  private static String file = "";

  public info() throws RemoteException {}

  /**
   * String String getter() : public method To give the manager information about the active pc, and
   * it represent the sevice in PC side.
   */
  public void getScreenshot(String picName) {
    Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    try {
      Robot robot = new Robot();
      BufferedImage img = robot.createScreenCapture(rectangle);
      ImageIO.write(img, "jpg", new File(picName));

      Tracking.info(true, "info screenshot done:");
    } catch (AWTException | IOException e) {
      e.printStackTrace();
      Tracking.error(true, "info screenshot:" + ExceptionHandler.getMessage(e));
    }
  }

  @Override
  public String getSceenshotNow() throws RemoteException {
    String scName = "screenshot.jpg";
    try {
      this.getScreenshot(scName);
      // copy file now+
      this.setFile(scName);
      this.login(connection.getManagerRef());

      Tracking.info(true, "info screenshotNow done:");
      return scName;
    } catch (Exception e) {
      // IOException
      Tracking.error(true, "info screenshotNow :" + ExceptionHandler.getMessage(e));
      e.printStackTrace();
      return "";
    }

  }

  @Override
  public String get(String property) throws RemoteException {
    String str = System.getProperty(property);
    Tracking.info(true, "info getter Triggered");
    return str;
  }

  @Override
  public int getLifeTime() throws RemoteException {
    int startTime = Pc.getStartTime();
    return TimeHandler.timeDifference(startTime);
  }

  @Override
  public String getIpAddress() throws RemoteException {
    return Pc.getMyIp();
  }

  @Override
  public int getStartTime() throws RemoteException {
    return Pc.getStartTime();
  }

  @Override
  public void setFile(String f) {
    file = StringHandler.separatorsToSystem(f);
  }

  @Override
  public boolean login(ManagerPcInterface c) throws RemoteException {
    /*
     *
     * Sending The File...
     *
     */
    try {
      File f1 = new File(file);
      if (f1.exists()) {
        FileInputStream in = new FileInputStream(f1);
        byte[] mydata = new byte[1024 * 1024];
        int mylen = in.read(mydata);
        String filename = f1.getName();
        filename = filename.replaceFirst("[.]", connection.currentTimeManagerPc() + ".");
        Tracking.echo("fileName " + filename);
        while (mylen > 0) {
          c.sendData(filename, mydata, mylen);
          mylen = in.read(mydata);
        }
      } else {
        Tracking.echo("file (" + file + ") does not exist");
      }

      Tracking.info(true, "info login done:");
    } catch (Exception e) {
      Tracking.error(true, "info login :" + ExceptionHandler.getMessage(e));
    }

    return true;
  }

  @Override
  public ArrayList<String> getRootDir(Boolean option) throws RemoteException {
    FileSystemView mySystemView = FileSystemView.getFileSystemView();;

    // true : paths OR false : names
    ArrayList<String> here = new ArrayList<String>();
    // add root directory
    for (File e : mySystemView.getRoots()) {
      if (!StringHandler.checkIfExist((option) ? e.getPath() : e.getName(), here))
        here.add(StringHandler.fixEndingOf((option) ? e.getPath() : e.getName()));
    }
    // add home directory
    if (!StringHandler.checkIfExist(
        StringHandler.fixEndingOf(((option == false) ? mySystemView.getHomeDirectory().getName()
            : mySystemView.getHomeDirectory().getPath())),
        here))
      here.add(StringHandler.fixEndingOf((option) ? mySystemView.getHomeDirectory().getPath()
          : mySystemView.getHomeDirectory().getName()));

    // add drive names
    File[] drives = File.listRoots();
    if (drives != null && drives.length > 0) {
      for (File aDrive : drives) {
        if (!StringHandler.checkIfExist(StringHandler.fixEndingOf(aDrive.getPath()), here))
          here.add(StringHandler.fixEndingOf((option) ? aDrive.getPath() : aDrive.getPath()));
        // propably 90% getName return empty string
      }
    }
    Tracking.echo(here);
    return here;
  }

  @Override
  public String[] changeDirAndListContent(String path) throws RemoteException {
    if (path == null)
      return null;
    Tracking.echo(path + " Was recieved");
    path = StringHandler.separatorsToSystem(path);

    Tracking.echo(path + " Checked");
    File dir = new File(path);
    if (dir.isDirectory()) {
      String[] filesList = dir.list();
      Tracking.echo("##############################" + dir);
      if (filesList != null && filesList.length > 0) {
        // Tracking.echo(filesList.toString());
        for (int i = 0; i < filesList.length; i++) {
          String st = dir.getPath();
          String file = filesList[i];
          st = StringHandler.fixEndingOf(st) + file;
          if ((new File(st)).isDirectory()) {
            Tracking.echo(st + File.separator);
            filesList[i] += File.separator;
          } else if ((new File(st)).isFile()) {
            Tracking.echo(st);
          }
        }
      } else {
        Tracking.echo(dir.getName() + " is Empty");
      }
      return filesList;
    } else {
      Tracking.echo(dir.getName() + " is not a folder");
      return null;
    }
  }

  @Override
  public byte fileOrDirectory(String path) throws RemoteException {
    if (path == null)
      return -1;
    path = StringHandler.separatorsToSystem(path);
    // return 0 if DIR, 1 if FILE, -1 if nether
    File myfile = (new File(path));
    if (myfile.exists()) {
      return (byte) ((myfile.isDirectory()) ? 0 : 1);
    }
    return -1;
  }

  @Override
  public STATE getPcState() throws RemoteException {
    return Pc.getCurrentState();
  }

  @Override
  public int getCloseTime() throws RemoteException {
    return Pc.getCloseTime();
  }

  @Override
  public int getPauseTime() throws RemoteException {
    return Pc.getPauseTime();
  }

  @Override
  public int getWorkTime() throws RemoteException {
    return Pc.getWorkTime();
  }

  @Override
  public int OpenPc(int lastWorkTime) throws RemoteException {
    int val = -3;
    STATE pcState = Pc.getCurrentState();
    if (pcState.equals(STATE.CLOSED))
      val = Pc.Open(0); // return closeTime
    else if (pcState.equals(STATE.PAUSSED))
      val = Pc.Resume(lastWorkTime); // return pauseTime
    Tracking.echo(pcState.toString() + "->OpenPc:disable Windows "
        + ((pcState.equals(STATE.CLOSED)) ? "closeTime" : "pauseTime")
        + TimeHandler.toString(val, true, true, true) + " "
        + TimeHandler.toString(connection.currentTimeManagerPc(), true, true, true));
    return val;
  }

  @Override
  public int PausePc(int lastPauseTime) throws RemoteException {
    int val = -2;
    STATE pcState = Pc.getCurrentState();
    if (pcState.equals(STATE.WORKING))
      val = Pc.Pause(lastPauseTime);

    Tracking
        .echo("PausePc :: Enable PreventingWindows " + TimeHandler.toString(val, true, true, true)
            + " " + TimeHandler.toString(connection.currentTimeManagerPc(), true, true, true));
    return val;
  }

  @Override
  public int ClosePc(int lastCloseTime) throws RemoteException {
    int val = -3;
    if (!Pc.getCurrentState().equals(STATE.CLOSED))
      val = Pc.Close(lastCloseTime);
    Tracking
        .echo("ClosePc :: Enable PreventingWindows " + TimeHandler.toString(val, true, true, true)
            + " " + TimeHandler.toString(connection.currentTimeManagerPc(), true, true, true));
    return val;
  }

  @Override
  public Boolean shutdown() throws RemoteException {
    String os = this.get("os.name").toLowerCase();
    String shutdownCmd = "shutdown " + ((os.contains("win")) ? " -s -t 0" : " -P 0");
    return this.runCommand(shutdownCmd);

  }

  @Override
  public Boolean restart() throws RemoteException {
    String os = this.get("os.name").toLowerCase();
    String restartCmd = "shutdown " + ((os.contains("win")) ? " -r -t 0" : " -r 0");
    return this.runCommand(restartCmd);
  }

  private Boolean runCommand(String cmd) {
    try {
      Process child = Runtime.getRuntime().exec(cmd);
      // child.destroy();
      return true;
    } catch (IOException e) {
      Tracking.error(true, "runCommand :" + ExceptionHandler.getMessage(e));
      return false;
    }
  }

  @Override
  public String runCommand(String[] args) throws RemoteException {
    if (args == null || args.length == 0)
      return "";

    ProcessBuilder ps = new ProcessBuilder(args);
    String entireOutput = "";

    // System.out.println("Current dir " + ps.directory().getAbsolutePath());

    String os = this.get("os.name").toLowerCase();
    BufferedReader in = null;
    ps.command().add(0, ((os.contains("win")) ? "cmd.exe" : "bash"));
    ps.command().add(1, ((os.contains("win")) ? "/c" : "-c"));
    for (String a : ps.command())
      System.out.print(a + ", ");
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

      Tracking.error(true, "runCommand :" + ExceptionHandler.getMessage(e));
    } finally {
      System.out.println("Output : \n " + entireOutput);
      if (in != null)
        try {
          in.close();
        } catch (IOException e) {
          Tracking.error(true, "runCommand :" + ExceptionHandler.getMessage(e));
        }
    }
    return entireOutput;
  }

  @Override
  public String getProcessList() throws RemoteException {

    String os = this.get("os.name").toLowerCase();

    String cmd = ((os.contains("win")) ? "whoami" : "whoami");

    Tracking.echo(cmd);

    String username = runCommand(new String[] {cmd});
    cmd = ((os.contains("win"))
        ? "tasklist -v -nh -fi \"username eq " + username.trim() + " \" -fi \"status eq running\" "
        : "ps -u " + username.trim() + " -o pid= -o time= -o %cpu= -o %mem= -o command= ");
    Tracking.echo(cmd);

    String output = this.runCommand(new String[] {cmd});
    String finalOutput = "";
    String arr[] = output.trim().split("\\r?\\n");
    for (String e : arr) {
      if (os.contains("win")) {

        String array[] = e.split("\\s+"); // multiple spaces ot tab

        finalOutput += array[1] + "\t" + array[8] + "\t" + array[4] + " unknown\t" + array[0];
        // finalOutput += e + " len" + array.length;
      } else {
        finalOutput += e;
        // finalOutput += e + " len" + array.length;
      }
      finalOutput += "\n";
    }
    Tracking.echo(finalOutput);
    return finalOutput;

  }



}
