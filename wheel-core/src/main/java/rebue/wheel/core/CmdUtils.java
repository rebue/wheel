package rebue.wheel.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CmdUtils {
    /**
     * 执行命令
     *
     * @param cmdSections 命令的分节数组
     * @return 执行完成的状态
     */
    public static int exec(String[] cmdSections) throws InterruptedException, IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(cmdSections);
        // 获取正常输出流
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        }

        // 获取错误输出流
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                PrintUtils.printError("%s%n", line);
            }
        }

        int status = process.waitFor();
        System.out.printf("executed status: %d%n", status);
        return status;
    }
}
