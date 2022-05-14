import examples.Student;
import javassist.*;

public class Monitor {
    final static ClassPool pool = ClassPool.getDefault();
    final static String classname = "examples.Student";
    public void studentLearnMonitor() throws Exception{
        CtClass ss = pool.getCtClass(classname);
        CtClass.debugDump="./dump";
        String methodname = "learn";
        CtMethod learn_ori = ss.getDeclaredMethod(methodname);
        //拷贝一份learn方法
        CtMethod learn_cp = CtNewMethod.copy(learn_ori,learn_ori.getName()+"_cp",ss,null);
        //添加拷贝后的方法
        ss.addMethod(learn_cp);
        //修改learn方法：原代码前后添加时间
        String src = "{"+
                "long start = System.currentTimeMillis();" +
                learn_ori.getName()+"_cp($$);"+
                "long end = System.currentTimeMillis();"+
                "System.out.println(end-start);"+
                "}";
        learn_ori.setBody(src);
        ss.toClass();
        //生成.class文件，主要用于调试，查看是否有代码片段被忽略
        //ss.writeFile();
        Student s = new Student();
        s.learn();
    }

    public void studentDisplayMonitor() throws Exception{
        CtClass ss = pool.getCtClass(classname);
        CtClass.debugDump="./dump";
        //添加字段name
        CtField param = new CtField(pool.get("java.lang.String"),"name", ss);
        // 访问级别是 private
        param.setModifiers(Modifier.PRIVATE);
        // 初始值是 "Frankyu"
        ss.addField(param, CtField.Initializer.constant("Frankyu"));
        String methodname = "display";
        CtMethod display = ss.getDeclaredMethod(methodname);
        String src = "{"+
                "System.out.println($0.age);" +
                "$0.name=\"frankyu\";" +
                "System.out.println($0.name);"+
                "}";
        display.insertBefore(src);
        if(true){
            System.out.println("Hello javassist + 1");
        }
        src ="if(true){" +
                "System.out.println(\"Hello javassist +2\");" +
                "}";
        display.insertAt(7,src);
        src = "{"+
                "System.out.println($0.grade);"+
                "}";
        display.insertAfter(src);
        ss.toClass();
        //ss.writeFile();
        Student s = new Student();
        s.display();
    }

    public static void main(String[] args) throws Exception{
        Monitor m = new Monitor();
        // 由于类冻结问题，两个方法不可同时调用
//            m.studentLearnMonitor();
        m.studentDisplayMonitor();
    }

}