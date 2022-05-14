package com;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.misc.Unsafe;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ReadChain {
    public String getData(String fileName) {
        String Path="src\\test\\java\\com\\result\\" + fileName + ".json";
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

    public String get_ClassName (JsonNode results, int chain_id, int node_id) {
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

    public String get_MethodName (JsonNode results, int chain_id, int node_id) {
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

    public int[] get_ReflectClassId (JsonNode Edges, int edge_id) {
        int[] result_list = new int[2];
        int classId;
        if (Edges.get(edge_id-1).get("edge").textValue().equals("CALL") &
                Edges.get(edge_id+1).get("edge").textValue().equals("CALL")) {
            classId = Edges.get(edge_id).get("exit_id").intValue();
            result_list[0] = edge_id;
            result_list[1] = classId;
        }
        else {
            int a;
            for (a=edge_id; a<Edges.size(); a++) {
                if (Edges.get(a).get("edge").textValue().equals("CALL")) {
                    classId = Edges.get(a).get("entry_id").intValue();
                    result_list[0] = a-1;
                    result_list[1] = classId;
                    break;
                }
            }
        }
        return result_list;
    }

    public <classname> Object object_init (String classname) throws Exception {

        Class<?> target_classname = Class.forName(classname);    //返回传入类对应的实例对象
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);

        classname PropertyObject = (classname) unsafe.allocateInstance(target_classname);
        return PropertyObject;
    }

    public Object isGadgetChain() {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(getData("NewChains(1)"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //将Json串以树状结构读入内存
        assert node != null;
        JsonNode results = node.get("results");//得到results这个节点下的信息

        Object object_input = null;
        System.out.println("-----------------正在载入数据，当前数据集中包含" + results.size() + "条调用链-----------------");
        for (int i=0;i<results.size();i++) {
            System.out.println("------------------------当前验证第" + (i+1) + "条潜在调用链-----------------------");
            int SourceID = results.get(i).get("sourceID").intValue();
            int SinkID = results.get(i).get("sinkID").intValue();
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
                String relation = Edges.get(j).get("edge").textValue();
                try {
                    if (relation.equals("ALIAS") &&
                            !(Modifier.isAbstract(Class.forName(get_ClassName(results, i, get_ReflectClassId(Edges, j)[1])).getModifiers()))) {
                        String ReflectClass = get_ClassName(results, i, get_ReflectClassId(Edges, j)[1]);
                        j = get_ReflectClassId(Edges, j)[0];
                        System.out.println("待实例化的类为：" + ReflectClass);
                        Class<?> clazz = null;
                        try {
                            clazz = Class.forName(ReflectClass);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        assert clazz != null;
                        Class<?>[] interfaces = clazz.getInterfaces();
                        String Polluted_Class = null;
                        int flag = 0;
                        int number_of_interfaces = 1;
                        b:for (Class<?> inter : interfaces) {
                            if (inter.getName().equals("java.io.Serializable")) {
                                flag = 1;
                                number_of_interfaces++;
                                continue;
                            }
                            Polluted_Class = inter.getName();
                            System.out.println("待赋值的属性类型为：" + Polluted_Class);
                            try {
                                object_list.add(object_init(ReflectClass));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (object_list.get(object_list.size()-2).getClass().getGenericSuperclass().getTypeName().equals("java.util.Dictionary<K, V>")) {
                                System.out.println("目标待赋值对象存在泛型属性Key，赋值成功！");
                                continue;
                            }
                            Field[] fields = object_list.get(object_list.size()-2).getClass().getDeclaredFields();//获取所有属性
                            int count_fields = 1;
                            for (Field field : fields) {
                                field.setAccessible(true);
                                if (field.getType().getName().equals(Polluted_Class) ||
                                        field.getType().getName().equals("java.lang.Object")) {
                                    try {
                                        field.set(object_list.get(object_list.size()-2), object_list.get(object_list.size()-1));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println("赋值成功！");
                                    break b;
                                }
                                if (count_fields == fields.length && number_of_interfaces == (interfaces.length-1)) {
                                    System.out.println("赋值失败！当前链不存在反序列化调用风险");
                                    break a;
                                }
                                count_fields++;
                            }
                            number_of_interfaces++;
                        }
                        if (flag == 0 && number_of_interfaces == interfaces.length) {
                            System.out.println("反序列化失败！当前链不存在反序列化调用风险");
                            break;
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (j == (Edges.size()-1)) {
                    System.out.println("-----------------------当前链存在反序列化调用风险！-----------------------");
                }
            }
        }
        System.out.println("---------------------------全部链已扫描完毕----------------------------");
        return object_input;
    }
}
