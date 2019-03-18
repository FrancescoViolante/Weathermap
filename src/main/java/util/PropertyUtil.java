package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public  class PropertyUtil {
  
  private static Properties myProperties;
  
    static{

        File file = new File("./src/main/resources/config.properties");
        try {

            myProperties = new Properties();
            FileInputStream myPropertiesInputStream = new FileInputStream(file);
            myProperties.load(myPropertiesInputStream);

        } catch (IOException e) {

            e.printStackTrace();

        }        

    }

    public static String getPropertyValue(String key) {
        return myProperties.getProperty(key);
    }
}