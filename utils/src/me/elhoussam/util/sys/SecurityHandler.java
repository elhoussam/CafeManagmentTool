package me.elhoussam.util.sys;
import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import me.elhoussam.util.log.Tracking;
public class SecurityHandler {
  /*
   * Security Class Signleton PD that handle loading the Policy file
   * and apply it set the security manager
   * */
  private String securityPolicyPath = "conf/sec.policy" ;
  public static SecurityHandler instance = new SecurityHandler();
  private SecurityHandler() {}
  /*
   * InitSecurityPolicy() method load the security policy
   * by adding security.policy property into the system
   * */
  private String InitSecurityPolicy(String securityPolicy) {
    //setSecurityPolicyPath(securityPolicy);

    //getClass().getClassLoader();
    // this used to load the file from resourceFolder in side jar file
    //String DynamicPath =  getClass().getClassLoader().getResource( securityPolicyPath ).toString();

    try {
      String DynamicPath =  Paths.get(".").toAbsolutePath().normalize().toString()+
          File.separator +"config"+File.separator +"security.policy" ;
      Tracking.echo("path = "+DynamicPath);
      File policyfile = new File(DynamicPath);
      if( !(policyfile.exists()) ) {

        Tracking.info(true,"The External policy file doesn't exist.. we will use the internal");
        DynamicPath =  getClass().getClassLoader().getResource( securityPolicyPath ).toString();
      }
      Tracking.info(false,"security path = "+DynamicPath);

      System.setProperty( "java.security.policy",DynamicPath );
      Tracking.info(true,"The Security policy is initialized");
      return System.getProperty("java.security.policy");
    } catch (Exception e) {
      Tracking.error(true,"The Security policy does not initialized:"+e.getMessage() );    //ExceptionHandler.getMessage(e)
      return null ;
    }



  }
  /*
   * LoadSecurityPolicy() method set the security Manger after loading the Security file
   * */
  public String LoadSecurityPolicy(String policyPath) {
    try {
      String res = InitSecurityPolicy(policyPath);
      System.setSecurityManager(new SecurityManager()) ;
      Tracking.info(true,"The Security policy setup");
      return res;
    }catch(Exception e ) {
      Tracking.error(true,"The Security policy cant setup:"+e.getMessage()); //The Security policy does not initialized
      return null ;
    }
  }
  /*	String myLocalIp() :
   *	static method that return the ip of the machine
   *	in the current network
   */
  public static String myLocalIp() throws UnknownHostException, SocketException {
    //java.net.Authenticator  -- .preferIPv4Stack=true
    String ip = "none";
    DatagramSocket socket = new DatagramSocket();
    // by using any ip address and any port, then return the current ip
    socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
    ip = socket.getLocalAddress().getHostAddress();
    socket.close();
    return ip ;
  }
}
