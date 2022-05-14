package Preprocess.newJson;

import com.alibaba.fastjson.JSON;

public class EdgeInfo {
    private  int entry_id;
    private  int exit_id;
    private  String edge;

    public void setEntry_id(int entry_id){
        this.entry_id = entry_id;
    }

    public int getEntry_id(){
        return entry_id;
    }

    public void setExit_id(int exit_id){
        this.exit_id = exit_id;
    }

    public int getExit_id(){
        return exit_id;
    }

    public void setEdge(String edge){
        this.edge = edge;
    }

    public String getEdge() {
        return edge;
    }

}
