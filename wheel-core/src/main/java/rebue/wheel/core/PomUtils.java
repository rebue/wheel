package rebue.wheel.core;

import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PomUtils {

    public static PomProps getPomProps(String pomPropsPath, Class<?> clazz) throws IOException {
        InputStream resource = clazz.getResourceAsStream(pomPropsPath);
        if (resource == null) throw new RuntimeException("missing " + pomPropsPath);
        PomProps pomProps = new PomProps();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("groupId")) {
                    pomProps.setGroupId(line.split("=")[1]);
                } else if (line.startsWith("artifactId")) {
                    pomProps.setArtifactId(line.split("=")[1]);
                } else if (line.startsWith("version")) {
                    pomProps.setVersion(line.split("=")[1]);
                } else if (line.startsWith("timestamp")) {
                    pomProps.setTimestamp(line.split("=")[1]);
                }
            }
        }
        return pomProps;
    }

    @Data
    public static class PomProps {
        private String groupId;
        private String artifactId;
        private String version;
        private String timestamp;
    }

}
