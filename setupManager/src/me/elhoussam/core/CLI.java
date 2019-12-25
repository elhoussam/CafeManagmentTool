package me.elhoussam.core;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.interfaces.infoInterface.STATE;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.ExceptionHandler;
import me.elhoussam.util.sys.TimeHandler;
import me.elhoussam.window.Popupwindows;

public class CLI {
  private ArrayList<Integer> lastWorkTimePcs = new ArrayList<Integer>();
  private String mainOptions[][] = {
      {"list active pc", "cmd to all" }, // managerOptions  0
      {"shutdown all pcs","logInAllPcs","logOutAllPcs", "quit" ,"choise :"}, // cmd to all  1
      {"shutdown","restart","run cmd","logIn","logOut","pauseTime","os name","life time","start time",
        "Screenshot", "COPYFILE","List Procces"//2
      }, //2
      {"Quit","your choise :"}// option to pcs 3
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
      Pc pcObj = Manager.getListofPcs().get(pcn);
      try {
        STATE pcState = pcObj.getRef().getPcState();

        int lastWork = pcObj.getRef().getWorkTime() ;
        int time =   pcObj.getRef().OpenPc(lastWork);

        Tracking.echo("Pc(" + (pcn+1) + ") LogIn ... done "+
            (pcState.equals(STATE.CLOSED)?"close ":"pause ")+"time\n"+time);
      } catch (Exception e) {
        Tracking.error(true, "Manager CLI error"+
            ExceptionHandler.getMessage(e));
      }
    }else {
      Tracking.echo("Pc("+pcn+") not connected");
    }
  }

  private void __logOutAllPcs() {
    Tracking.echo("log out all pcs ... done\n");
  }

  private void __logOutPcN(int pcn) {
    if ( checkIndexIsExist(pcn) ) {
      Pc pcObj = Manager.getListofPcs().get(pcn);
      try {
        int lastCloseTime = pcObj.getRef().getCloseTime();

        int workTime = pcObj.getRef().ClosePc( lastCloseTime);
        pcObj.setLastWorkTime( workTime );
        Tracking.echo("Pc(" + (pcn+1) + ") LogOut ... done \t workTime :"+
            TimeHandler.toString(workTime, true,true,true)+"\n");
      } catch (Exception e) {
        Tracking.error(true, "Manager CLI error"+
            ExceptionHandler.getMessage(e));
      }

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



  private void cmdToAllPcs(int i) {
    currentOptions += "cmdToAll>";

    do {
      showOption(currentOptions, mainOptions[1]);
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
    ArrayList<String> pcs = new ArrayList<String>();
    for (i = 0; i < numberOfActivePcs; i++) {
      pcs.add("Pc("+(i+1)+")");
      //option += "\t" + (i + 1) + "-Pc(" +( i + 1 )+ ")\n";
    }
    String option[] = pcs.toArray( new String[0]);
    do {
      showOption(currentOptions, option);
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
      showOption(  currentOptions, mainOptions[2]);
      i = byteInput();
      switch (i) {
        case 1:
          __shutdownPcN(pcn);
          break;
        case 2:
          __restartPcN(pcn);
          break;
        case 3:
          __runCmdOnPcN(pcn);
          break;
        case 4:
          __logInPcN(pcn);
          break;
        case 5:
          __logOutPcN(pcn);
          break;
        case 6:
          __pauseTime(pcn);
          break;
        case 7:
          __osName(pcn);
          break;
        case 8:
          __showLifeTime(pcn);
          break;
        case 9:
          __showStartTime(pcn);
          break;
        case 10:
          __takeSceenshot(pcn);
          break;
        case 11:
          __copyFile(pcn);
          break;
        case 12:
          __processList(pcn);
          break;
      }

    } while (i != 0);
    removeLastOption();

  }
  private void __processList(int pcn) {
    if ( checkIndexIsExist(pcn) ) {
      Tracking.echo("PID\tPTIME\tMEM\tCPU\tPNOM");
      infoInterface infOBJ = Manager.getListofPcs().get(pcn).getRef();
      try {
        String res = infOBJ.getProcessList();
        Tracking.echo(res);
      }catch (RemoteException e) {
        Tracking.error(true, "Manager CLI error"+
            ExceptionHandler.getMessage(e));
      }
    }else {
      Tracking.echo("Pc("+pcn+") not connected");
    }
  }
  private void __runCmdOnPcN(int pcn) {
    if ( checkIndexIsExist(pcn) ) {
      infoInterface infOBJ = Manager.getListofPcs().get(pcn).getRef();
      String outputResult=null,currentCmd="";
      currentOptions += "RUN>";

      do {
        Tracking.echo(currentOptions+"\n\ttype your command:");
        currentCmd = stringInput().trim();

        try {
          if ( !currentCmd.isEmpty() &&  !currentCmd.equalsIgnoreCase("quit") )
            outputResult =  infOBJ.runCommand( currentCmd.split(" ")  ) ;

        } catch (RemoteException e) {
          outputResult = "Unknow" ;
          Tracking.error(true, "Manager CLI error"+
              ExceptionHandler.getMessage(e));
        }finally {
          Tracking.echo( outputResult );
        }

      }while( !currentCmd.equalsIgnoreCase("quit") );
      this.removeLastOption();
    }else {
      Tracking.echo("Pc("+pcn+") not connected");
    }

  }
  private void __restartPcN(int pcn) {
    if ( checkIndexIsExist(pcn) ) {
      infoInterface infOBJ = Manager.getListofPcs().get(pcn).getRef();
      Boolean shutdownResult=false;
      try {
        shutdownResult = infOBJ.restart();

      } catch (RemoteException e) {
        shutdownResult = false ;
        Tracking.error(true, "Manager CLI error"+
            ExceptionHandler.getMessage(e));
      }finally {
        Tracking.echo("Shutdown pc N" + pcn + " ... " +
            ((shutdownResult==true)?"Done":"Unknown")
            +"\n");
      }
    }else {
      Tracking.echo("Pc("+pcn+") not connected");
    }

  }
  private void __shutdownPcN(int pcn) {

    if ( checkIndexIsExist(pcn) ) {
      infoInterface infOBJ = Manager.getListofPcs().get(pcn).getRef();
      Boolean shutdownResult=false;
      try {
        shutdownResult = infOBJ.shutdown();

      } catch (RemoteException e) {
        shutdownResult = false ;
        Tracking.error(true, "Manager CLI error"+
            ExceptionHandler.getMessage(e));
      }finally {
        Tracking.echo("Shutdown pc N" + pcn + " ... " +
            ((shutdownResult==true)?"Done":"Unknown")
            +"\n");
      }


    }else {
      Tracking.echo("Pc("+pcn+") not connected");
    }
  }
  private void __pauseTime(int pcn) {
    if ( checkIndexIsExist(pcn) ) {
      Pc pcObj = Manager.getListofPcs().get(pcn);
      try {
        int lastPauseTime = pcObj.getRef().getPauseTime();
        int workTime = pcObj.getRef().PausePc(lastPauseTime);
        pcObj.setLastWorkTime( workTime );
        Tracking.echo("Pc(" + (pcn+1) + ") FreezeTime ... done \t workTime :"+
            TimeHandler.toString(workTime, true,true,true)+"\n");
      } catch (Exception e) {
        Tracking.error(true, "Manager CLI error"+
            ExceptionHandler.getMessage(e));
      }

    }else {
      Tracking.echo("Pc("+pcn+") not connected");
    }
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

  private void showOption(String header, String... options) {
    Tracking.echo(header);
    for(byte i = 0; i< options.length ;i++) {
      System.out.print("\t"+(i+1)+"-"+options[i]);
      if( (i+1) % 3 == 0)
        Tracking.echo("");
    }
    Tracking.echo( "\t0-"+mainOptions[3][0]);
    System.out.print("\t"+mainOptions[3][1]);


  }

  private void startCommandLigneInterface(byte i) {

    do {
      showOption(currentOptions, mainOptions[0]);
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
