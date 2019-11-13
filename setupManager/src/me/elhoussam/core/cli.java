package me.elhoussam.core;

import java.rmi.Naming;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import me.elhoussam.interfaces.infoInterface;
import me.elhoussam.util.log.Tracking;

public class cli {
	private String options []= {
			"\t1-list active pc\n\t2-cmd to all\n\t0-quit\n # choise :", // managerOptions
			"\t1-shutdown all pcs\n\t2-logInAllPcs\n\t3-logOutAllPcs 0-quit\n # choise :",	// cmd to all
			"\t1-shutdown\n\t2-Login\n\t3-logoff\n\t4-os name\n\t0-quit\n # choise :",// option to pcs
	};
	private String currentOptions = "ManagerApp>";
	
	public cli() {
		startCommandLigneInterface((byte)-1);
		Tracking.echo("Exit...\n");
		System.exit(0);
	}
	private void startCommandLigneInterface(byte i) { 
		do {
			showOption(currentOptions +"\n"+ options[0]);
			i = byteInput();
			switch(i) {
				case 1 : listActivePc(-1); break;
				case 2 : cmdToAllPcs(-1) ;break;
			}
		}while(i != 0);
	}
	private void cmdToAllPcs(int i) {
		currentOptions+="cmdToAll>";
 
		do { 
			showOption(currentOptions +"\n"+ options[1]);
			i = byteInput();
			switch(i) {
			case 1 : __shutdownAllPcs(); break;
			case 2 : __logInAllPcs() ;break;
			case 3 : __logOutAllPcs() ;break;
			}
			
		}while(i != 0);
		currentOptions="ManagerApp>";	
	}

	private void listActivePc(int i) {
		currentOptions+="listActivePc>";
		int numberOfActivePcs = Manager.get().getListeActivePc().size();
		String option = "";
		for( i=0;i<numberOfActivePcs;i++ ) {
			option+="\t"+(i+1)+"-Pc("+i+1+")\n\t";
		}
		option+="\n\t0-quit\n # choise :";
  
		do {
			showOption(currentOptions +"\n"+ option );
			i = byteInput();
			if(i != 0) pcPickedN(i-1);
		}while(i != 0);
		currentOptions="ManagerApp>";			
	}
	private void pcPickedN(int pcn) {
		currentOptions+="Pc("+(pcn+1)+")>";
		byte i = -1 ;
		 do{//if( i != 1 && i != 2 && i != 3)
			showOption(currentOptions +"\n"+ options[2]);
			i = byteInput();
			switch(i) {
			case 1 : __shutdownPcN(pcn); break;
			case 2 : __logInPcN(pcn) ;break;
			case 3 : __logOutPcN(pcn) ;break;
			case 4 : __osName(pcn) ;break;
			}
			
		}while(i != 0);
		removeLastOption();
		
	}
	private void __osName(int pcn) {

		String ip = Manager.get().getListeActivePc().get(pcn);
		try {
			String fullPath =  "//"+ ip  +"/pcWait" ;

			infoInterface infoObj;
			infoObj = (infoInterface) Naming.lookup( fullPath );

			//Tracking.info(false,"Thread Checker lookup for "+fullPath);
			String result= infoObj.get("os.name");
			//Tracking.info(false,"Thread Checker get info :"+result+" from "+ip);
			Tracking.echo( result ) ;
		}catch (Exception e) {
			Tracking.error(true,"Thread Checker ("+ip+"):Not connected"  );
			//ExceptionHandler.getMessage(e)
		}
		
	}
	private void removeLastOption() {
		String localarray [] = currentOptions.split(">");
		List<String> strList = new LinkedList<String>( Arrays.asList( localarray ) );
		int ind = localarray.length -1;
		strList.remove(ind );
		currentOptions = String.join(">", strList);
	}
	
	private void __logOutPcN(int pcn) {
		Tracking.echo("log Out pc N"+pcn+" ... done\n");
	}
	private void __logInPcN(int pcn) {
		Tracking.echo("log In pc N"+pcn+" ... done\n");
		
	}
	private void __shutdownPcN(int pcn) {
		Tracking.echo("Shutdown pc N"+pcn+" ... done\n");
	}
	private void __logOutAllPcs() {
		Tracking.echo("log out all pcs ... done\n");
	}
	private void __logInAllPcs() {
		Tracking.echo("log in all pcs ... done\n");
	}
	private void __shutdownAllPcs() {
		Tracking.echo("shutdown all pcs ... done\n");
	}
	public static byte byteInput() {
		Scanner stdin = new Scanner(System.in);
		stdin.next();
		return stdin.nextByte() ;
	}
	private void showOption(String options) {
		Tracking.echo(options);
	}
}
