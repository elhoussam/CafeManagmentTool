import me.elhoussam.core.Pc;
import me.elhoussam.util.log.Database;

public class PcMain {
  public static void main(String[] args) {
    Database o = Database.getInstance();
    o.setDatabaseName("setupPc.db");
    o.setDatabaseUrl("jdbc:sqlite");
    o.setDriverName("org.sqlite.JDBC");

    Pc.start((args == null || args.length == 0) ? "" : args[0]);
  }

}
