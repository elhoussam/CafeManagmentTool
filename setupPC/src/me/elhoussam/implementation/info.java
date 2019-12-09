package me.elhoussam.implementation;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

  private static String file = "";

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
  public int getWorkTime() throws RemoteException {
    return Pc.getWorkTime();
  }

  @Override
  public void OpenPc(int lastWorkTime) throws RemoteException {

    if (!Pc.getCurrentState().equals(STATE.WORKING))
      Pc.Open(lastWorkTime);
    Tracking.echo("OpenPc :: disable PreventingWindows "
        + TimeHandler.toString(lastWorkTime, true, true, true) + " "
        + TimeHandler.toString(connection.currentTimeManagerPc(), true, true, true));
  }

  @Override
  public int PausePc() throws RemoteException {
    int val = -2;
    if (Pc.getCurrentState().equals(STATE.WORKING))
      val = Pc.Pause();
    Tracking
        .echo("PausePc :: Enable PreventingWindows " + TimeHandler.toString(val, true, true, true)
            + " " + TimeHandler.toString(connection.currentTimeManagerPc(), true, true, true));
    return val;
  }

  @Override
  public int ClosePc() throws RemoteException {
    int val = -3;
    if (!Pc.getCurrentState().equals(STATE.CLOSED))
      val = Pc.Close();
    Tracking
        .echo("ClosePc :: Enable PreventingWindows " + TimeHandler.toString(val, true, true, true)
            + " " + TimeHandler.toString(connection.currentTimeManagerPc(), true, true, true));
    return val;
  }

  @Override
  public STATE getPcState() throws RemoteException {
    return Pc.getCurrentState();
  }

}
