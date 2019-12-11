import me.elhoussam.core.Pc;

public class PcMain {
  public static void main(String[] args) {
    Pc.start((args == null || args.length == 0) ? "" : args[0]);
  }

}
