import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import me.elhoussam.util.log.Database;
import me.elhoussam.util.log.Tracking;
import me.elhoussam.util.sys.TimeHandler;
public class Main {

  public static void main(String[] args) throws UnknownHostException, SocketException, ClassNotFoundException, SQLException {
    Database  o = Database.getInstance() ;
    o.setDatabaseName("setupPc.db");
    o.setDatabaseUrl("jdbc:sqlite");
    o.setDriverName("org.sqlite.JDBC");

    Tracking.globalSwitcher = true ;
    Tracking.setFolderName("utils");
    int time =TimeHandler.getCurrentTime();
    Tracking.info(true, TimeHandler.toString(time,true,true) );
    Tracking.error(true, TimeHandler.toString(time,true,true) );
    Tracking.echo( TimeHandler.getTimeString() );


    o.insertRecord("MY_TABLE", new Object[] {"hello",12," world",13," !",2});

  }
}
