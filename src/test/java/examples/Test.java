package examples;
import javassist.*;

import java.lang.reflect.Method;

public class Test {
    public static void createStudent() throws  Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.makeClass("src\\test\\java\\examples.Student");
        // 字段名为name
        CtField param = new CtField(pool.get("java.lang.String"),"name", cc);
        // 访问级别是 private
        param.setModifiers(Modifier.PRIVATE);
        // 初始值是 "Frankyu"
        cc.addField(param, CtField.Initializer.constant("Frankyu"));

        // 生成 getter、setter 方法
        cc.addMethod(CtNewMethod.setter("setName", param));
        cc.addMethod(CtNewMethod.getter("getName", param));

        // 添加无参的构造函数
        CtConstructor cons = new CtConstructor(new CtClass[]{}, cc);
        cons.setBody("{name = \"yubo\";}");
        cc.addConstructor(cons);

        // 5. 添加有参的构造函数
        cons = new CtConstructor(new CtClass[]{pool.get("java.lang.String")}, cc);
        // $0=this / $1,$2,$3... 代表方法参数
        cons.setBody("{$0.name = $1;}");
        cc.addConstructor(cons);

        // 6. 创建一个名为printName方法，无参数，无返回值，输出name值
        CtMethod ctMethod = new CtMethod(CtClass.voidType, "printName", new CtClass[]{}, cc);
        ctMethod.setModifiers(Modifier.PUBLIC);
        ctMethod.setBody("{System.out.println(name);}");
        cc.addMethod(ctMethod);

        //这里会将这个创建的类对象编译为.class文件
        cc.writeFile("");
    }
    public static void usingStudent() throws Exception{
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.getCtClass("src\\test\\java\\examples.Student");
        // 实例化
        Object student = cc.toClass().newInstance();
        // 设置值
        Method setName = student.getClass().getMethod("setName", String.class);
        setName.invoke(student, "junjie");
        // 输出值
        Method execute = student.getClass().getMethod("printName");
        execute.invoke(student);
    }
    public static void main(String[] args) throws Exception{
        createStudent();
        usingStudent();
    }
}