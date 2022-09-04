package edu.berkeley.cs.jqf.examples.GadgetChain;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.io.*;
import java.lang.Object;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import com.pholser.junit.quickcheck.internal.GeometricDistribution;
import edu.berkeley.cs.jqf.examples.common.Dictionary;
import org.apache.commons.lang3.reflect.FieldUtils;
import sun.misc.Unsafe;

public class ExploitGenerator extends Generator<Object> {

    private static final GeometricDistribution geom = new GeometricDistribution();
    private static final double MEAN_ARRAY_DEPTH = 1.2;
    private final List<Object> dictionary = new ArrayList<>();

    private final String[] memberDictionary = {
            "foo",
            "key"
    };

    public ExploitGenerator () {
        super(Object.class);
    }

    public void configure(Dictionary dict) throws IOException {

        HashSet hashSet = new HashSet(1);
        hashSet.add("key");

        // Read dictionary words
        try (InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(dict.value())) {
            if (in == null) {
                throw new FileNotFoundException("Dictionary file not found: " + dict);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String item;
            while ((item = br.readLine()) != null) {
                if (item.equals("java.util.HashSet")) {
                    dictionary.add(hashSet);
                }
                else if (item.equals("java.util.HashMap")) {
                    Field field = hashSet.getClass().getDeclaredField("map");
                    field.setAccessible(true);
                    HashMap hashset_map = (HashMap)field.get(hashSet);
                    dictionary.add(hashset_map);
                } else {
                    dictionary.add(objectInit(item));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object objectInit (String classname) throws Exception {
        Class<?> target_classname = Class.forName(classname);
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        return unsafe.allocateInstance(target_classname);
    }


    @Override
    public Object generate(SourceOfRandomness random, GenerationStatus status) {
        HashMap hashMap = new HashMap<>();
        hashMap.put("key", "value");
        
        for (int idx = 0; idx < dictionary.size() - 1; idx++) {
            if (!HashMap.class.isAssignableFrom(dictionary.get(idx).getClass())
                    && Map.class.isAssignableFrom(dictionary.get(idx).getClass())) {
                Field mapField = null;
                try {
                    mapField = dictionary.get(idx).getClass().getSuperclass().getDeclaredField("map");
                } catch (NoSuchFieldException e) {
                    //e.printStackTrace();
                }
                mapField.setAccessible(true);
                try {
                    mapField.set(dictionary.get(idx), hashMap);
                } catch (IllegalAccessException e) {
                    //e.printStackTrace();
                }
            }

            //Field[] target_field = dictionary.get(idx).getClass().getDeclaredFields();//获取所有属性
            Field[] target_field = FieldUtils.getAllFields(dictionary.get(idx).getClass());
            for (Field field : target_field) {
                field.setAccessible(true);
                if (!Modifier.isStatic(field.getModifiers())) {
                    if (field.getType().isAssignableFrom(dictionary.get(idx + 1).getClass())) {
                        try {
                            field.set(dictionary.get(idx), dictionary.get(idx + 1));
                        } catch (IllegalAccessException e) {
                            //e.printStackTrace();
                        }
                        continue;
                    }
                    if (field.getType() == int.class) {
                        try {
                            field.set(dictionary.get(idx), random.nextInt(0, 5));
                        } catch (IllegalAccessException e) {
                            //e.printStackTrace();
                        }
                    }
                    if (Map.class.isAssignableFrom(field.getType())) {
                        try {
                            field.set(dictionary.get(idx), hashMap);
                        } catch (IllegalAccessException e) {
                            //e.printStackTrace();
                        }
                    }
                    if (field.getType() == Object.class) {
                        try {
                            field.set(dictionary.get(idx), random.choose(memberDictionary));
                        } catch (IllegalAccessException e) {
                            //e.printStackTrace();
                        }
                    }
                    if (field.getType().isArray()) {
                        int depth = geom.sampleWithMean(MEAN_ARRAY_DEPTH, random);
                        if (field.getType().getName().substring(2, field.getType().getName().length() - 1).equals("java.lang.Object")) {
                            Object[] objects = new Object[depth];
                            for (int i = 0; i < depth; i++) {
                                //int choice = random.nextInt(dictionary.size());
                                try {
                                    objects[i] = dictionary.get(random.nextInt(dictionary.size()));
                                } catch (Exception e) {
                                    //e.printStackTrace();
                                }
                            }
                            try {
                                field.set(dictionary.get(idx), objects);
                            } catch (IllegalAccessException e) {
                                //e.printStackTrace();
                            }
                        } else if (dictionary.get(idx).getClass() == HashMap.class) {
                            Object[] array = new Object[0];
                            try {
                                array = (Object[]) field.get(dictionary.get(idx));
                            } catch (IllegalAccessException e) {
                                //e.printStackTrace();
                            }
                            Object node = array[0];
                            if (node == null) {
                                node = array[1];
                            }
                            Field keyField = null;
                            try {
                                keyField = node.getClass().getDeclaredField("key");
                            } catch (NoSuchFieldException e) {
                                //e.printStackTrace();
                            }
                            assert keyField != null;
                            keyField.setAccessible(true);
                            try {
                                keyField.set(node, dictionary.get(idx + 1));
                            } catch (IllegalAccessException e) {
                                //e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        return dictionary.get(0);

    }
}
