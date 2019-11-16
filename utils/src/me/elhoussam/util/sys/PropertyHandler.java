package me.elhoussam.util.sys;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;
import me.elhoussam.util.log.Tracking;

public class PropertyHandler {
  private String pathOfProperties="config"+File.separator+"config.properties";

  public PropertyHandler(String pathOfConfig) {
    if(!pathOfConfig.trim().isEmpty())
      pathOfProperties = pathOfConfig;
  } 


  public String getPropetry(String key) { 
    try{
      InputStream input = getClass().getClassLoader().getResourceAsStream(pathOfProperties);
      Properties prop = new Properties();
      // load a properties file
      prop.load(input);
      Tracking.info(true, "PropertyHandler load input");
      String val = prop.getProperty( key );
      Tracking.info(true,"PropertyHandler read value");
      // get the property value and print it out
      return (val);
    } catch (IOException ex) {
      Tracking.error(true,"PropertyHandler Failed to:"+ ExceptionHandler.getMessage(ex));
      return null ;
    }
  }
}
