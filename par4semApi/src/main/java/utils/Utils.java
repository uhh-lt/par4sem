package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {

    static public Properties getConfigs(InputStream input) throws IOException {
        Properties prop = new Properties();
        prop.load(input);
        return prop;

    }
}
