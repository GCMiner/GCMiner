package Preprocess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import org.neo4j.driver.types.Node;


public class Map2Json {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static int i = 1 ;

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    //map转json
    public static String toJson(Map<Long, Node> map) {
        if (map == null)
            map = new HashMap<Long, Node>();
        return gson.toJson(map);
    }
    public static String toJson1(Map<String, List> map) {
        if (map == null)
            map = new HashMap<>();
        return gson.toJson(map);
    }

    public static String toJson2(Map<String, Object> map) {
        if (map == null)
            map = new HashMap<String, Object>();
        return gson.toJson(map);
    }


    //写入json文件
    public static boolean createJsonFile(String jsonString, String filePath, String fileName) {
        boolean flag = true;

        Map2Json map2Json = new Map2Json();
        int i = map2Json.getI();
        //System.out.println(i);


        String fullPath = filePath + File.separator + fileName+ i + ".json";
        i++;
        //System.out.println(i);
        map2Json.setI(i);

        try{
            File file = new File(fullPath);
            if(!file.exists());
            file.createNewFile();

            Writer write = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
            write.write(jsonString);

            write.flush();
            write.close();

        }catch (Exception e){
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    public static boolean createJsonFile1(String jsonString, String filePath, String fileName) {
        boolean flag = true;

        Map2Json map2Json = new Map2Json();


        String fullPath = filePath + File.separator + fileName + ".json";


        try{
            File file = new File(fullPath);
            if(!file.exists());
            file.createNewFile();

            Writer write = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
            write.write(jsonString);

            write.flush();
            write.close();

        }catch (Exception e){
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }
}
