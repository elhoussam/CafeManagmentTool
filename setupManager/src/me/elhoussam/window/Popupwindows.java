package me.elhoussam.window;

import me.elhoussam.core.Manager;
import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.log.Tracking;

public class Popupwindows {
  private static FileChooser myFileChooser = null;


  private static void initFileChooser( infoInterface remoteObj )  {
    try {
      if( myFileChooser == null || !myFileChooser.getCurrentRef().equals(remoteObj)  )
        myFileChooser = new FileChooser(remoteObj);
    } catch (Exception e) {
      Tracking.error(true,"initFileChooser "+e.getLocalizedMessage());
      e.printStackTrace();
    }
  }

  public static void showScreenshot(String args) {
    Screenshot obj = new Screenshot(args);
    obj.showOpenDialog();
  }

  public static String selectFilesOnPc(int pcNumber){
    infoInterface infOBJ = Manager.getListofPcs().get(pcNumber).getRef();
    initFileChooser(infOBJ);
    int val = myFileChooser.showOpenDialog();
    if( val != -1) {
      String path = myFileChooser.getSelectedPaths();
      Tracking.echo("selectFilesOnPc ["+path +"]");
      return path;
    }

    return "";
  }
}
