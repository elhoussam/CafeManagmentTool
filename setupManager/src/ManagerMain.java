import me.elhoussam.core.Manager;
import me.elhoussam.util.log.Database;

public class ManagerMain {
  public static void main(String[] argv) throws Exception {
    Database o = Database.getInstance();
    o.setDatabaseName("setupPc.db");
    o.setDatabaseUrl("jdbc:sqlite");
    o.setDriverName("org.sqlite.JDBC");
    Manager.start();
    //new CLI();
  }
}
