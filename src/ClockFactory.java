import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class ClockFactory {
    public static Clock getInstance(String className, Properties myProp) {
        Clock myClock = null;
        try {
            Class<?> myClass = Class.forName(className);
            Constructor myCon = myClass.getConstructor(new Class[]{Properties.class});
            myClock = (Clock)myCon.newInstance(new Object[]{myProp});
        } catch (ClassNotFoundException cnfe) {
            System.err.println(cnfe);
        } catch (InstantiationException ie) {
            System.err.println(ie);
        } catch (IllegalAccessException iae) {
            System.err.println(iae);
        } catch (InvocationTargetException ite) {
            System.err.println(ite);
        } catch (NoSuchMethodException nsme) {
            System.err.println(nsme);
        }

        return myClock;
    }
}
