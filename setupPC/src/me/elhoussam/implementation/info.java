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
import javax.imageio.ImageIO;
import me.elhoussam.core.Pc;
import me.elhoussam.core.connection;
import me.elhoussam.interfaces.ManagerPcInterface;
import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;
import me.elhoussam.util.sys.TimeHandler;

public class info extends UnicastRemoteObject implements infoInterface {

  public info() throws RemoteException {}

  /*
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
    file = f;
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
        while (mylen > 0) {
          c.sendData(f1.getName(), mydata, mylen);
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

}
