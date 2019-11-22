import java.net.SocketException;
import java.net.UnknownHostException;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.TimeHandler;
public class Main {

  public static void main(String[] args) throws UnknownHostException, SocketException {
    Tracking.globalSwitcher = true ;
    Tracking.setFolderName("utils");
    int time =TimeHandler.getCurrentTime();
    Tracking.echo( TimeHandler.toString(time,true,true) );
    Tracking.echo( TimeHandler.getTimeString() );

  }
}
