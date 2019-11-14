import me.elhoussam.util.log.Tracking;
public class Main {

	public static int subject() {
		
		int a=2,b=0;
		return a/b;
	}
	public static void main(String[] args) {
		Tracking.globalSwitcher = true ;
		Tracking.setFolderName("main",false);	
		Tracking.error (false ," error OFF" );
		Tracking.error (false," error OFF" ); 
		Tracking.info (true," warningMsg ON ");
		Tracking.warning (true," warningMsg2 ON ");
	}

}
