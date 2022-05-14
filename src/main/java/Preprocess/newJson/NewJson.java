package Preprocess.newJson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import faketabby.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class NewJson {

    public static String sink;

    /*private static List<DefineJson> list1 = new ArrayList<DefineJson>();
    private static List<DefineJson> list2 = new ArrayList<DefineJson>();
    private static List<EdgeInfo> list3 = new ArrayList<>();*/
    /*private static HashMap<String, Object> hashMap1 = new HashMap<String, Object>();
    private static HashMap<String, Object> hashMap2 = new HashMap<String, Object>();


    public static HashMap<String, Object> getHashMap1() {
        return hashMap1;
    }

    public static void setHashMap1(HashMap<String, Object> hashMap1) {
        NewJson.hashMap1 = hashMap1;
    }

    public static HashMap<String, Object> getHashMap2() {
        return hashMap2;
    }*/


    public static HashMap<String, Object> returnMap(int j) {
        List<Integer> listOfSource = TEST.sourceid;
        List<Integer> listOfSink = TEST.sinkid;
        List<DefineJson> list1 = new ArrayList<DefineJson>();
        List<DefineJson> list2 = new ArrayList<DefineJson>();
        List<EdgeInfo> list3 = new ArrayList<>();

        HashMap<String, Object> hashMap1 = new HashMap<String, Object>();
        hashMap1.clear();

        //HashMap<String, Object> hashMap2 = new HashMap<String, Object>();


        String[] keyArr = TestGetKey.keyInArr(j);


        for (int i = 0; i < keyArr.length; i++) {
            //System.out.println(keyArr.length);
            DefineJson defineJson = new DefineJson();
            defineJson.setId(Integer.parseInt(keyArr[i]));
            defineJson.setValue((GetFromJson1.getClass(Integer.parseInt(keyArr[i]),j)).toString());
            list1.add(defineJson);

        }
        hashMap1.put("ClassName", list1);
        //System.out.println(JSON.toJSON(hashMap1));

        for (int i = 0; i < keyArr.length; i++) {
            //System.out.println(keyArr.length);
            DefineJson defineJson = new DefineJson();
            defineJson.setId(Integer.parseInt(keyArr[i]));
            defineJson.setValue((GetFromJson1.getMethod(Integer.parseInt(keyArr[i]),j)).toString());
            list2.add(defineJson);

        }
        hashMap1.put("MethodName", list2);
        //System.out.println(list2);
        //System.out.println(JSON.toJSON(hashMap1));

        /*for (int i = 0; i < keyArr.length; i++) {

            if (GetFromJson1.getSink(Integer.parseInt(keyArr[i]),j)) {
                sink = keyArr[i];
                hashMap1.put("sinkID", Integer.parseInt(keyArr[i]));
            }
            *//*if(GetFromJson1.getSource(Integer.parseInt(keyArr[i]),j,source)){
                hashMap1.put("sourceID", Integer.parseInt(keyArr[i]));
            }*//*

        }*/

        hashMap1.put("sourceID", listOfSource.get(j-1));
        hashMap1.put("sinkID", listOfSink.get(j-1));

        List<String[]> list = (TEST.edgeAll);

        for (int i = 0; i < list.size(); i++) {
            EdgeInfo edgeInfo = new EdgeInfo();
            String[] str = list.get(i);
            edgeInfo.setEntry_id(Integer.parseInt(str[0]));
            edgeInfo.setEdge(str[1]);
            edgeInfo.setExit_id(Integer.parseInt(str[2]));
            list3.add(edgeInfo);

        }
        hashMap1.put("Edges", list3);
        //System.out.println(JSON.toJSON(hashMap1));




        return hashMap1;

    }


}
