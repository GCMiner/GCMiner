package Preprocess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandUtil {
    public static void main(String[] args) {

        commandUtil();

    }


    //调用cmd命令运行tabby
    public static void commandUtil(){
        String cmd = "cmd /c cd /d D:\\toolofdeserialization\\tabby &&  "
                + "java -jar build\\libs\\tabby-1.1.0.RELEASE.jar cases\\jars --isJDKProcess"
                + "&& java -jar build\\libs\\tabby-1.1.0.RELEASE.jar --isSaveOnly";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(new String(line.getBytes(), "GBK"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            process.destroy();
        }
    }



}