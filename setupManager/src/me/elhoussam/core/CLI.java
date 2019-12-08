package me.elhoussam.core;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;
import me.elhoussam.util.sys.TimeHandler;
import me.elhoussam.window.Popupwindows;

public class CLI {

  private String options[] = {"\t1-list active pc\n\t2-cmd to all\n\t0-quit\n # choise :", // managerOptions
      "\t1-shutdown all pcs\n\t2-logInAllPcs\n\t3-logOutAllPcs 0-quit\n # choise :", // cmd to all
      "\t1-shutdown\n\t2-Login\n\t3-logoff\n\t4-os name\n\t5-life time\n\t6-start time\n\t7-COPYFILE\n\t8-Screenshot\n\t0-quit\n # choise :",// option to pcs
  };

  private String currentOptions = "ManagerApp>";

  public static byte byteInput() {
    Scanner stdin = new Scanner(System.in);
    byte a = stdin.nextByte();
    return a;

  }
  public static String stringInput() {
    Scanner stdin = new Scanner(System.in);
    String a = stdin.nextLine();
    return a;
  }
  public CLI() {
    startCommandLigneInterface((byte) -1);
    Tracking.echo("Exit...\n");
    System.exit(0);
  }

  private void __logInAllPcs() {
    Tracking.echo("log in all pcs ... done\n");
  }

  private void __logInPcN(int pcn) {
    if ( checkIndexIsExist(pcn) ) {
      Tracking.echo("log In pc N" + pcn + " ... done\n");
    }else {
      Tracking.echo("Pc("+pcn+") not connected");
    }
  }

  private void __logOutAllPcs() {
    Tracking.echo("log out all pcs ... done\n");
  }

  private void __logOutPcN(int pcn) {
    if ( checkIndexIsExist(pcn) ) {
      Tracking.echo("log Out pc N" + pcn + " ... done\n");
    }else {
      Tracking.echo("Pc("+pcn+") not connected");
    }
  }

  private void __osName(int pcn) {
    if ( checkIndexIsExist(pcn) ) {

      Manager.getListofPcs().get(pcn).getIpAddress();
      try {
        infoInterface infOBJ = Manager.getListofPcs().get(pcn).getRef();
        // Tracking.info(true,"Thread Checker lookup for "+fullPath);
        String result = infOBJ.get("os.name");
        // Tracking.info(true,"Thread Checker get info :"+result+" from "+ip);
        Tracking.echo(result);
      } catch (Exception e) {
        Tracking.error(true, "Manager CLI error"+
            ExceptionHandler.getMessage(e));
        // ExceptionHandler.getMessage(e)
      }
    }else {
      Tracking.echo("Pc("+pcn+") not connected");
    }

  }

  private void __showLifeTime(int pcn) {

    if ( checkIndexIsExist(pcn) ) {
      infoInterface infOBJ = Manager.getListofPcs().get(pcn).getRef();
      try {
        Tracking.echo(
            TimeHandler.toString(infOBJ.getLifeTime(),true,true,true)
            );
      } catch (RemoteException e) {
        Tracking.error(true, "Manager CLI error"+
            ExceptionHandler.getMessage(e));
        // ExceptionHandler.getMessage(e)
      }
    }else {
      Tracking.echo("Pc("+pcn+") not connected");
    }
  }

  private void __shutdownAllPcs() {
    Tracking.echo("shutdown all pcs ... done\n");
  }

  private void __shutdownPcN(int pcn) {

    if ( checkIndexIsExist(pcn) ) {
      Tracking.echo("Shutdown pc N" + pcn + " ... done\n");
    }else {
      Tracking.echo("Pc("+pcn+") not connected");
    }
  }

  private void cmdToAllPcs(int i) {
    currentOptions += "cmdToAll>";

    do {
      showOption(currentOptions + "\n" + options[1]);
      i = byteInput();
      switch (i) {
        case 1:
          __shutdownAllPcs();
          break;
        case 2:
          __logInAllPcs();
          break;
        case 3:
          __logOutAllPcs();
          break;
      }

    } while (i != 0);
    currentOptions = "ManagerApp>";
  }

  private void listActivePc(int i) {
    currentOptions += "listActivePc>";
    int numberOfActivePcs = Manager.getListofPcs().size();
    String option = "";
    for (i = 0; i < numberOfActivePcs; i++) {
      option += "\t" + (i + 1) + "-Pc(" +( i + 1 )+ ")\n";
    }
    option += "\n\t0-quit\n # choise :";

    do {
      showOption(currentOptions + "\n" + option);
      i = byteInput();
      if(i != 0) {
        if ( checkIndexIsExist(i-1)  )pcPickedN(i - 1);
        else Tracking.echo("Pc("+i+") is not in the list");
      }
    } while (i != 0);
    currentOptions = "ManagerApp>";
  }

  private void pcPickedN(int pcn) {
    currentOptions += "Pc(" + (pcn + 1) + ")>";
    byte i = -1;
    do {// if( i != 1 && i != 2 && i != 3)
      showOption(currentOptions + "\n" + options[2]);
      i = byteInput();
      switch (i) {
        case 1:
          __shutdownPcN(pcn);
          break;
        case 2:
          __logInPcN(pcn);
          break;
        case 3:
          __logOutPcN(pcn);
          break;
        case 4:
          __osName(pcn);
          break;
        case 5:
          __showLifeTime(pcn);
          break;
        case 6:
          __showStartTime(pcn);
          break;
        case 7:
          __copyFile(pcn);
          break;
        case 8:
          __takeSceenshot(pcn);
          break;
      }

    } while (i != 0);
    removeLastOption();

  }
  private Boolean checkIndexIsExist( int pcn) {
    int lastIndex = Manager.getListofPcs().size()-1;
    return ( pcn>=0 && pcn <= lastIndex )?true:false;
  }
  private void __takeSceenshot(int pcn) {
    if ( checkIndexIsExist(pcn) ) {
      infoInterface infOBJ = Manager.getListofPcs().get(pcn).getRef();
      try {
        String nameSc = infOBJ.getSceenshotNow();
        if( !nameSc.isEmpty())   Popupwindows.showScreenshot(nameSc);
        //Popupwindows.viewScreenshot( nameSc );
        else Tracking.echo("impossible to invoke screenshot right now");
      } catch (RemoteException e) {
        Tracking.error(true,"can't take screenshot");
        e.printStackTrace();
      }
    }else {
      Tracking.echo("Pc("+pcn+") not connected");
    }

  }
  private void __copyFile(int pcn) {
    // choose file to copy into myserver [info.setName]
    String filePath = Popupwindows.selectFilesOnPc(pcn);
    //Tracking.echo("__copyFile ["+filePath+"]");
    //fileChooserMain.main(null );
    // start copy the file methods [login]
    if( !filePath.trim().isEmpty() ) {
      if (  checkIndexIsExist(pcn)  ) {
        infoInterface infOBJ = Manager.getListofPcs().get(pcn).getRef();
        //specifie the choosen file
        //Tracking.echo("Enter path file:");
        //String f = stringInput();
        try {
          infOBJ.setFile(filePath);
          infOBJ.login(Manager.getObject());
        } catch (RemoteException e) {
          Tracking.error(true, "Manager CLI error"+
              ExceptionHandler.getMessage(e));
        }
      }else {
        Tracking.echo("#Pc("+pcn+") not connected");
      }
    }else {

      Tracking.echo("you haven't choose file");
    }
  }
  private void __showStartTime(int pcn) {
    if ( checkIndexIsExist(pcn) ) {
      infoInterface infOBJ = Manager.getListofPcs().get(pcn).getRef();
      try {
        Tracking.echo(
            TimeHandler.toString(infOBJ.getStartTime(),true,true,true)
            );
      } catch (RemoteException e) {
        Tracking.error(true, "Manager CLI error"+
            ExceptionHandler.getMessage(e));
        // ExceptionHandler.getMessage(e)
      }
    }else {
      Tracking.echo("Pc("+pcn+") not connected");
    }
  }

  private void removeLastOption() {
    String localarray[] = currentOptions.split(">");
    List<String> strList = new LinkedList<String>(Arrays.asList(localarray));
    int ind = localarray.length - 1;
    strList.remove(ind);
    currentOptions = String.join(">", strList);
  }

  private void showOption(String options) {
    Tracking.echo(options);
  }

  private void startCommandLigneInterface(byte i) {

    do {
      showOption(currentOptions + "\n" + options[0]);
      i = byteInput();

      switch (i) {
        case 1:
          listActivePc(-1);
          break;
        case 2:
          cmdToAllPcs(-1);
          break;
      }
    } while (i != 0);
  }
}
