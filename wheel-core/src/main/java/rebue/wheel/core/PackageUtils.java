package rebue.wheel.core;

import java.io.File;
import java.nio.file.Path;

public class PackageUtils {
    public static String pathToName(Path path) {
        return pathToName(path.toString());
    }

    public static String pathToName(String path) {
        return path.replace('\\', '.').replace('/', '.');
    }

    public static Path nameToPath(String name) {
        return Path.of(nameToPath(name, File.separator.charAt(0)));
    }

    public static String nameToPath(String name, char separator) {
        return name.replace('.', separator);
    }
}
