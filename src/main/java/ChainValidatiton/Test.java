package ChainValidatiton;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.misc.Unsafe;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Test {
    public static String getData(String fileName) {
        String Path="src\\test\\java\\com\\" + fileName + ".json";
        BufferedReader reader = null;
        StringBuilder laststr = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(Path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            reader = new BufferedReader(inputStreamReader);
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                laststr.append(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr.toString();
    }
    public static String get_ClassName(JsonNode results, int chain_id, int node_id) {
        JsonNode ClassName = results.get(chain_id).get("ClassName");
        String classname = null;
        for (int i=0; i<ClassName.size(); i++) {
            if (ClassName.get(i).get("id").intValue() == node_id) {
                classname = ClassName.get(i).get("value").textValue();
                break;
            }
        }
        return classname;
    }

    public static String get_MethodName(JsonNode results, int chain_id, int node_id) {
        JsonNode MethodName = results.get(chain_id).get("MethodName");
        String methodname = null;
        for (int i=0; i<MethodName.size(); i++) {
            if (MethodName.get(i).get("id").intValue() == node_id) {
                methodname = MethodName.get(i).get("value").textValue();
                break;
            }
        }
        return methodname;
    }

    public static <classname> Object object_init(String classname) throws Exception {

        Class<?> target_classname = Class.forName(classname);    //返回传入类对应的实例对象
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);

        classname PropertyObject = (classname) unsafe.allocateInstance(target_classname);
        return PropertyObject;
    }

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(getData("data"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert node != null;
        JsonNode results = node.get("results");

        Object object_input = null;
        System.out.println("-----------------Loading data，including" + results.size() + "gadget chains!-----------------");

        for (int i=0;i<results.size();i++) {
            System.out.println("------------------------Gadget Chain" + (i+1) + "-----------------------");
            int SourceID = results.get(i).get("SourceID").intValue();
            int SinkID = results.get(i).get("Sink").intValue();
            System.out.println( "Source：" + get_ClassName(results, i, SourceID) + "." + get_MethodName(results, i, SourceID));
            System.out.println( "Sink：" + get_ClassName(results, i, SinkID) + "." + get_MethodName(results, i, SinkID));

            try {
                object_input = object_init(get_ClassName(results, i, SourceID));
            } catch (Exception e) {
                e.printStackTrace();
            }
            ArrayList<Object> object_list = new ArrayList<>();
            object_list.add(object_input);

            JsonNode Edges = results.get(i).get("Edges");
            a:for (int j = 0; j < Edges.size(); j++ ) {
                String relation = Edges.get(j).get("relation").textValue();
                if (relation.equals("ALIAS")) {
                    String ReflectClass = get_ClassName(results, i, Edges.get(j).get("Exit_id").intValue());
                    Class<?> clazz = null;
                    try {
                        clazz = Class.forName(ReflectClass);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    assert clazz != null;
                    Class<?>[] interfaces = clazz.getInterfaces();
                    String Polluted_Class = null;
                    for (Class<?> inter : interfaces) {
                        if (inter.getName().equals("java.io.Serializable"))
                            continue;
                        Polluted_Class = inter.getName();
                    }
                    try {
                        object_list.add(object_init(ReflectClass));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Field[] fields = object_list.get(object_list.size()-2).getClass().getDeclaredFields();//获取所有属性
                    int count_fields = 1;
                    for (Field field : fields) {
                        field.setAccessible(true);
                        if (field.getType().getName().equals(Polluted_Class)) {
                            try {
                                field.set(object_list.get(object_list.size()-2), object_list.get(object_list.size()-1));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        if (count_fields == fields.length) {
                            System.out.println("Fail to construct！");
                            break a;
                        }
                        count_fields++;
                    }
                }
                if (j == (Edges.size()-1)) {
                    System.out.println("-----------------------This gadget chain may be exploitable！-----------------------");
                }
            }
        }
        System.out.println("---------------------------All chains have been scanned----------------------------");
    }
}
