
import me.elhoussam.util.log.Tracking;
public class Main {

	public static void subject() {

		Tracking.setFolderName("subject");
		Tracking.warning(" warning ");
	}
	public static void main(String[] args) {
		
		Tracking.setFolderName("main");	

		Tracking.error(" error ");
		subject();
		Tracking.info(" infos ");

	}

}
