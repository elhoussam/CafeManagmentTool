
import me.elhoussam.util.log.Tracking;
public class Main {

	public static int subject() {
		int a=2,b=0;
		return a/b;
		 
	}
	public static void main(String[] args) {
		
		Tracking.setFolderName("main",false);	

		Tracking.error(" error ");
		try {
		int res = subject();
		}catch(Exception e ) {
			System.out.println("getLocalizedMessage :"+e.getLocalizedMessage() );
			System.out.println("getMessage :"+e.getMessage() );
			System.out.println("getName :"+e.getClass().getName() );
			System.out.println("getSimpleName :"+e.getClass().getSimpleName() );
			System.out.println("getCanonicalName :"+e.getClass().getCanonicalName() );
		}
		Tracking.info(" infos ");

	}

}
