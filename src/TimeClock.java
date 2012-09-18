import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class TimeClock {
    public TimeClock(Properties prop, Clock myClock) {
        new ClockDisplay(myClock);
    }

    public static void main(String[] args) {
        Properties prop;

        prop = loadProperties("config.properties");

        Clock myClock = ClockFactory.getInstance(prop.getProperty("storage", "FileClock"), prop);

        new TimeClock(prop, myClock);
    }

    public static Properties loadProperties(String location) {
        Properties prop = new Properties();
        try {
            prop.load(new InputStreamReader(new FileInputStream(location)));
        } catch(IOException exception) {
            System.err.println("Error: " + exception.getMessage());
            System.exit(1);
        }

        return prop;
    }
}
