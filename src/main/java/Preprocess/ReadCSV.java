package Preprocess;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ReadCSV {

    //public static List<HashMap> list = new ArrayList<>();
    public static List<List> listAll = new ArrayList<>();

    public List<List> getListAll() {
        return listAll;
    }

    public void setListAll(List<List> listAll) {
        this.listAll = listAll;
    }

    public static void main(String[] args) {

        String csvFile = "C:/Users/18710/Desktop/csv/GRAPHDB_PUBLIC_METHODS.csv";

        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(csvFile));
            String[] line;
            String result = null;
            int num = 0;
            while ((line = reader.readNext()) != null&&num<2) {
                List<HashMap> list = new ArrayList<>();
                if(num==0){
                    num++;
                    continue;
                }
                    HashMap<Integer,String> map = new HashMap<>();
                    //System.out.println("Params ="+line[15]);
                    result = line[15];

                    //System.out.println("1:"+result);
                    result = result.replaceAll("\"","");
                    //System.out.println("2:"+result);
                    result = result.substring(1,result.length()-1);
                    if(result.isEmpty()){
                        //System.out.println("为空");
                        List<HashMap> l = new LinkedList<>();
                        listAll.add(l);
                        num++;
                        continue;
                    }
                    System.out.println("3:"+result);
                    String str1;
                    if(result.contains("],")){
                        str1 = result.substring(result.indexOf("["),result.indexOf(",",result.indexOf(",")+1));
                    }else{
                        str1 = result.substring(result.indexOf("["),result.indexOf("]")+1);
                    }
                    System.out.println("4:"+str1);
                    int key = Integer.parseInt(str1.substring(1,str1.indexOf(",")));
                    //System.out.println("5:"+key);
                    String value = str1.substring(str1.indexOf(",")+1, str1.lastIndexOf("]"));
                    //System.out.println("6:"+value);
                    map.put(key,value);
                    //System.out.println(JSON.toJSON(map));
                    //System.out.println(map);
                    list.add(map);
                    //System.out.println("list:"+list);
                    while (result.contains("],")){
                        HashMap<Integer,String> map1 = new HashMap<>();
                        result = result.substring(str1.length()+1);
                        //System.out.println(":"+result);
                        if(result.contains("],")){
                            str1 = result.substring(result.indexOf("["),result.indexOf(",",result.indexOf(",")+1));
                        }else{
                            str1 = result.substring(result.indexOf("["),result.lastIndexOf("]")+1);
                        }
                        //System.out.println(":"+str1);
                        key = Integer.parseInt(str1.substring(1,str1.indexOf(",")));
                        //System.out.println(":"+key);
                        value = str1.substring(str1.indexOf(",")+1, str1.lastIndexOf("]"));
                        //System.out.println(":"+value);
                        map1.put(key,value);
                        //System.out.println(JSON.toJSON(map1));
                        //System.out.println(map);
                        list.add(map1);
                        //System.out.println("list:"+list);
                    }


                num++;
                listAll.add(list);

                System.out.println(num);
            }
            System.out.println("----------listAll:"+listAll);

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

    }
}
