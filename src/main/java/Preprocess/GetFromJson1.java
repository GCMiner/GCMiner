package Preprocess;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;


import java.io.*;

public class GetFromJson1 {

    //private static String path = GetFromJson1.class.getClassLoader().getResource("chains.json").getPath();

    private static int[] arr = new int[3];

    public static Object getClass(int id, int j) {

        String path = GetFromJson1.class.getClassLoader().getResource("chains"+j+".json").getPath();
        String s = readJsonFile(path);
        TEST test = new TEST();
        //String s = test.getMapString();
        JSONObject jobj = JSON.parseObject(s);
        JSONObject nodeId = jobj.getJSONObject(String.valueOf(id));
        //System.out.println(nodeId.getJSONObject("properties"));
        JSONObject properties = nodeId.getJSONObject("properties");
        //System.out.println(properties.getJSONObject("POLLUTED_POSITION"));
        JSONObject Class = properties.getJSONObject("CLASSNAME");
        Object str = Class.get("val");
        return str;

    }

    public static Object getMethod(int id, int j) {

        String path = GetFromJson1.class.getClassLoader().getResource("chains"+j+".json").getPath();
        String s = readJsonFile(path);
        TEST test = new TEST();
        //String s = test.getMapString();
        JSONObject jobj = JSON.parseObject(s);
        JSONObject nodeId = jobj.getJSONObject(String.valueOf(id));
        //System.out.println(nodeId.getJSONObject("properties"));
        JSONObject properties = nodeId.getJSONObject("properties");
        //System.out.println(properties.getJSONObject("POLLUTED_POSITION"));
        JSONObject Method = properties.getJSONObject("NAME");
        Object str = Method.get("val");
        return str;

    }

    public static boolean getSink(int id, int j){
        boolean flag = false;
        String path = GetFromJson1.class.getClassLoader().getResource("chains"+j+".json").getPath();
        String s = readJsonFile(path);
        TEST test = new TEST();
        //String s = test.getMapString();
        JSONObject jobj = JSON.parseObject(s);
        JSONObject nodeId = jobj.getJSONObject(String.valueOf(id));
        JSONObject properties = nodeId.getJSONObject("properties");
        JSONObject Method = properties.getJSONObject("WUDIAN");
        String str = Method.get("val").toString();
        if(str.contains("TRUE")){
            flag = true;
        }

        return flag;
    }

//    public static boolean getSource(int id, int j,String name){
//        boolean flag = false;
//        String path = GetFromJson1.class.getClassLoader().getResource("chains"+j+".json").getPath();
//        String s = readJsonFile(path);
//        TEST test = new TEST();
//        //String s = test.getMapString();
//        JSONObject jobj = JSON.parseObject(s);
//        JSONObject nodeId = jobj.getJSONObject(String.valueOf(id));
//        JSONObject properties = nodeId.getJSONObject("properties");
//        JSONObject Method = properties.getJSONObject("NAME");
//        String str = Method.get("val").toString();
//        if(str.equals(name)){
//            flag = true;
//        }
//
//        return flag;
//    }

    public static int[] newArr(int[] arr){
        int[] arr1 = new int[3];
        for(int i=0;i<arr.length;i++){
            if(arr[i]>0){
                arr1[i]=1;
            }
        }
        return arr1;
    }


    //读取json文件
    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

