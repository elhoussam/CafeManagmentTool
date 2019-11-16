import java.net.SocketException;
import java.net.UnknownHostException;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.SecurityHandler;
public class Main {

	public static int subject() {
		
		int a=2,b=0;
		return a/b;
	}
	public static void main(String[] args) throws UnknownHostException, SocketException {
		Tracking.globalSwitcher = true ;
	    Tracking.setFolderName("utils");
		Tracking.info(true,"Start Here"); 
		Tracking.error (false ," error qsdfsqdqsdf" +SecurityHandler.myLocalIp() );
	}

}
