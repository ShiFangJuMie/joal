import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class TestMethods {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("com.turn.ttorrent.common.Torrent");
        for (Method m : clazz.getDeclaredMethods()) {
            System.out.println(m.getName() + " -> " + m.getReturnType().getName());
        }
    }
}
