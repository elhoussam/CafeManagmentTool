package me.elhoussam.util.sys;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;
import me.elhoussam.util.log.Tracking;
public class PropertyHandler {
  private String pathOfProperties="conf/conf.properties";
  public PropertyHandler(String pathOfConfig) {
    if(!pathOfConfig.trim().isEmpty())
      pathOfProperties = pathOfConfig;
  }
  public static String loadExternalConfig() {

    String DynamicPath =  Paths.get(".").toAbsolutePath().normalize().toString()+
        File.separator +"config"+File.separator+ "config.properties" ;
    //Tracking.echo("Conf path "+DynamicPath);
    File policyfile = new File(DynamicPath);
    if( !(policyfile.exists()) )
      DynamicPath =  null ;
    return DynamicPath ;

  }
  public String getPropetry(String key) {
    try{
      // Load external config/config.properties :: beside the executable jar file
      String con = loadExternalConfig();
      InputStream input = null ;
      //Tracking.echo( (con==null )?"NULL":con);
      // if we don't found the file than will use the inside config
      if( con == null ) {
        Tracking.info(true,"The External config file doesn't exist.. we will use the internal");
        //con =  getClass().getClassLoader().getResource( pathOfProperties ).toString();
        //input = new FileInputStream(con);

        input = getClass().getClassLoader().getResourceAsStream(pathOfProperties);

      }else input = new FileInputStream(con);
      Tracking.info(false,"config path = "+con);

      Properties prop = new Properties();
      // load a properties file
      prop.load(input);
      String val = prop.getProperty( key );
      Tracking.info(true,"PropertyHandler read value "+val);
      // get the property value and print it out
      return (val);
    } catch (IOException ex) {
      Tracking.error(true,"PropertyHandler Failed to:"+ ExceptionHandler.getMessage(ex));
      return null ;
    }
  }
}
