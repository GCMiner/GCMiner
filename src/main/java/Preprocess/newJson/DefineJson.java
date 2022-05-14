package Preprocess.newJson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;

import java.util.HashMap;

public class DefineJson {

    private int id;
    private String value;


    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void setValue(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

}
