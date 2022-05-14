package Preprocess;

import com.google.gson.internal.bind.util.ISO8601Utils;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
* 该文件测试用
* 只查询链的数量
*
*
* */
public class GetChainsFromNeo4j {
    public static void main(String args[]) {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));
        Session session = driver.session();

        int num = 1;

        //String txtPath = "D:/java_test/fake_tabby";



            //Source:
            //1.java.lang.Object.hashCode
            //2.java.lang.Object.toString
            //3.java.lang.Comparable.compareTo
            //4.java.io.ObjectInputStream.readObject--java.io.ObjectInput.readObject--java.util.PriorityQueue.readObject
            //5.equals
            //6.finalize

            //SINK:
            //1.java.lang.Class.forName
            //2.java.lang.reflect.Method.invoke--sun.reflect.misc.MethodUtil.invoke
            //3.java.lang.Runtime.exec
            //4.javax.naming.Context--java.rmi.registry.Registry--org.springframework.jndi.JndiTemplate.lookup

            String cmdSql11 = "match (source:Method {CLASSNAME:'java.lang.Object',NAME:'hashCode'})\n" +
                    "match (sink:Method {CLASSNAME:'java.lang.Class', NAME:'forName'})\n" +
                    "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS',12) yield path \n" +
                    "return path";
            String cmdSql12 = "match (source:Method {CLASSNAME:'java.lang.Object',NAME:'hashCode'})\n" +
                "match (sink:Method {CLASSNAME:'java.lang.reflect.Method', NAME:'invoke'})\n" +
                "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS',12) yield path \n" +
                "return path";
            String cmdSql13 = "match (source:Method {CLASSNAME:'java.lang.Object',NAME:'hashCode'})\n" +
                "match (sink:Method {CLASSNAME:'java.lang.Runtime', NAME:'exec'})\n" +
                "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS',12) yield path \n" +
                "return path";
            String cmdSql14 = "match (source:Method {CLASSNAME:'java.lang.Object',NAME:'hashCode'})\n" +
                    "match (sink:Method {NAME:'lookup'}) where sink.CLASSNAME in ['javax.naming.Context','java.rmi.registry.Registry','org.springframework.jndi.JndiTemplate']\n" +
                    "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS',12) yield path \n" +
                    "return path";
            String cmdSql21 = "match (source:Method {CLASSNAME:'java.lang.Object',NAME:'toString'})\n" +
                "match (sink:Method {CLASSNAME:'java.lang.Class', NAME:'forName'})\n" +
                "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS',12) yield path \n" +
                "return path";
            String cmdSql22 = "match (source:Method {CLASSNAME:'java.lang.Object',NAME:'toString'})\n" +
                "match (sink:Method {CLASSNAME:'java.lang.reflect.Method', NAME:'invoke'})\n" +
                "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS',12) yield path \n" +
                "return path";
            String cmdSql23 = "match (source:Method {CLASSNAME:'java.lang.Object',NAME:'toString'})\n" +
                "match (sink:Method {CLASSNAME:'java.lang.Runtime', NAME:'exec'})\n" +
                "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS',12) yield path \n" +
                "return path";
            String cmdSql24 = "match (source:Method {CLASSNAME:'java.lang.Object',NAME:'toString'})\n" +
                "match (sink:Method {NAME:'lookup'}) where sink.CLASSNAME in ['javax.naming.Context','java.rmi.registry.Registry','org.springframework.jndi.JndiTemplate']\n" +
                "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS',12) yield path \n" +
                "return path";
            String cmdSql31 = "match (source:Method {CLASSNAME:'java.lang.Comparable',NAME:'compareTo'})\n" +
                "match (sink:Method {CLASSNAME:'java.lang.Class', NAME:'forName'})\n" +
                "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS',12) yield path \n" +
                "return path";
            String cmdSql32 = "match (source:Method {CLASSNAME:'java.lang.Comparable',NAME:'compareTo'})\n" +
                "match (sink:Method {CLASSNAME:'java.lang.reflect.Method', NAME:'invoke'})\n" +
                "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS',12) yield path \n" +
                "return path";
            String cmdSql33 = "match (source:Method {CLASSNAME:'java.lang.Comparable',NAME:'compareTo'})\n" +
                "match (sink:Method {CLASSNAME:'java.lang.Runtime', NAME:'exec'})\n" +
                "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS',12) yield path \n" +
                "return path";
            String cmdSql34 = "match (source:Method {CLASSNAME:'java.lang.Comparable',NAME:'compareTo'})\n" +
                "match (sink:Method {NAME:'lookup'}) where sink.CLASSNAME in ['javax.naming.Context','java.rmi.registry.Registry','org.springframework.jndi.JndiTemplate']\n" +
                "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS',12) yield path \n" +
                "return path";
            String cmdSql41 = "match (source:Method {NAME:'readObject'}) where source.CLASSNAME in ['java.io.ObjectInputStream','java.io.ObjectInput','java.util.PriorityQueue']\n" +
                "match (sink:Method {CLASSNAME:'java.lang.Class', NAME:'forName'})\n" +
                "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS',12) yield path \n" +
                "return path";
            String cmdSql42 = "match (source:Method {NAME:'readObject'}) where source.CLASSNAME in ['java.io.ObjectInputStream','java.io.ObjectInput','java.util.PriorityQueue']\n" +
                "match (sink:Method {CLASSNAME:'java.lang.reflect.Method', NAME:'invoke'})\n" +
                "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS',12) yield path \n" +
                "return path";
            String cmdSql43 = "match (source:Method {NAME:'readObject'}) where source.CLASSNAME in ['java.io.ObjectInputStream','java.io.ObjectInput','java.util.PriorityQueue']\n" +
                "match (sink:Method {CLASSNAME:'java.lang.Runtime', NAME:'exec'})\n" +
                "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS',12) yield path \n" +
                "return path";
            String cmdSql44 = "match (source:Method {NAME:'readObject'}) where source.CLASSNAME in ['java.io.ObjectInputStream','java.io.ObjectInput','java.util.PriorityQueue']\n" +
                "match (sink:Method {NAME:'lookup'}) where sink.CLASSNAME in ['javax.naming.Context','java.rmi.registry.Registry','org.springframework.jndi.JndiTemplate']\n" +
                "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS',12) yield path \n" +
                "return path";
        String gt1 = "match (source:Method {CLASSNAME:'javax.naming.ldap.Rdn$RdnEntry',NAME:'compareTo'})\n" +
                "match(sink:Method{CLASSNAME:'sun.rmi.registry.RegistryImpl_Stub',NAME:'lookup'})\n" +
                "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS', 15) yield path\n" +
                "where none(n in nodes(path) where n.CLASSNAME in ['java.beans.EventHandler','java.lang.ProcessBuilder',\n" +
                "'javax.imageio.ImageIO$ContainsFilter','jdk.nashorn.internal.objects.NativeString',\n" +
                "'com.sun.corba.se.impl.activation.ServerTableEntry',\n" +
                "'com.sun.tools.javac.processing.JavacProcessingEnvironment$NameProcessIterator',\n" +
                "'sun.awt.datatransfer.DataTransferer$IndexOrderComparator','sun.swing.SwingLazyValue',\n" +
                "'com.sun.jndi.toolkit.dir.LazySearchEnumerationImpl','sun.rmi.registry.RegistryImpl_Stub'])\n" +
                "return path";
        String gt2 = "match (m1:Method {CLASSNAME:'jdk.nashorn.internal.objects.NativeString',NAME:'hashCode'})\n" +
                "match(sink:Method{CLASSNAME:'java.lang.reflect.Method',NAME:'invoke'})\n" +
                "call apoc.algo.allSimplePaths(sink, m1, '<CALL|ALIAS', 20) yield path\n" +
                "where none(n in nodes(path) where n.CLASSNAME in ['java.beans.EventHandler','java.lang.ProcessBuilder',\n" +
                "'javax.imageio.ImageIO$ContainsFilter','jdk.nashorn.internal.objects.NativeString',\n" +
                "'com.sun.corba.se.impl.activation.ServerTableEntry',\n" +
                "'com.sun.tools.javac.processing.JavacProcessingEnvironment$NameProcessIterator',\n" +
                "'sun.awt.datatransfer.DataTransferer$IndexOrderComparator','sun.swing.SwingLazyValue',\n" +
                "'com.sun.jndi.toolkit.dir.LazySearchEnumerationImpl','sun.rmi.registry.RegistryImpl_Stub'])\n" +
                "return path";
        String gt3 = "match (m1:Method {CLASSNAME:'javax.naming.ldap.Rdn$RdnEntry',NAME:'compareTo'})\n" +
                "match(sink:Method{CLASSNAME:'javax.naming.spi.DirectoryManager',NAME:'getObjectInstance'})\n" +
                "call apoc.algo.allSimplePaths(sink, m1, '<CALL|ALIAS', 10) yield path\n" +
                "where none(n in nodes(path) where n.CLASSNAME in ['java.beans.EventHandler','java.lang.ProcessBuilder',\n" +
                "'javax.imageio.ImageIO$ContainsFilter','jdk.nashorn.internal.objects.NativeString',\n" +
                "'com.sun.corba.se.impl.activation.ServerTableEntry',\n" +
                "'com.sun.tools.javac.processing.JavacProcessingEnvironment$NameProcessIterator',\n" +
                "'sun.awt.datatransfer.DataTransferer$IndexOrderComparator','sun.swing.SwingLazyValue',\n" +
                "'com.sun.jndi.toolkit.dir.LazySearchEnumerationImpl','sun.rmi.registry.RegistryImpl_Stub'])\n" +
                "return path";
        String gt4 = "match (m1:Method {CLASSNAME:'javax.naming.ldap.Rdn$RdnEntry',NAME:'compareTo'})  match(sink:Method{NAME:'getContext',CLASSNAME:'javax.naming.spi.NamingManager'})\n" +
                "call apoc.algo.allSimplePaths(sink, m1, '<CALL|ALIAS', 15) yield path\n" +
                "where none(n in nodes(path) where n.CLASSNAME in ['java.beans.EventHandler','java.lang.ProcessBuilder',\n" +
                "'javax.imageio.ImageIO$ContainsFilter','jdk.nashorn.internal.objects.NativeString',\n" +
                "'com.sun.corba.se.impl.activation.ServerTableEntry',\n" +
                "'com.sun.tools.javac.processing.JavacProcessingEnvironment$NameProcessIterator',\n" +
                "'sun.awt.datatransfer.DataTransferer$IndexOrderComparator','sun.swing.SwingLazyValue',\n" +
                "'com.sun.jndi.toolkit.dir.LazySearchEnumerationImpl','sun.rmi.registry.RegistryImpl_Stub'])\n" +
                "return path";

        String gt5 = "match (source:Method{CLASSNAME:'java.io.ObjectInputStream',NAME:'readObject'})\n" +
                "match (sink:Method{CLASSNAME:'clojure.main$eval_opt',NAME:'invoke'})\n" +
                "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS', 17) yield path \n" +
                "return path limit 2";

            Result result = session.run(gt5);
        //cc [1.1] 1826657 [1.2] 1229976 [1.3] 1300 [1.4] 8054 [2.1] 3544901 [2.2] 2153326  [2.3] 2413 [2.4] 13369 [3.1] 364473 [3.2] 153936 [3.3] 225 [3.4] 710 [4.1] 4708 [4.2] 2525 [4.3] 13 [4.4] 2 [5.1]
        //xstream 1159819 91805 864 5156   1777054 168482 1721 9342      232077 9064 170 628        2658 224 13 2
        //cp30  1077319 90245 845 5173      1666025 160694 1616 9415    219237 8872 156 631     2016 180 13 2
        //mchange-commons-java  1106299 92801 851 5254  1651900 165108 1596 9311        222957 8933 156 628    2152 167 13 2
        //CC4 1516574 270895 1155 6940      2674653 490975 2239 12381      306518  26085 211 698    3245 521 13 2
        //clojure 2483657 796741 10730 6833  3169309 1340598 14147 8251    310608 97647 481 764  2248 555 13 2
        //vaadin  1116536 102169 926 5271    1710714 167050 1744 9661    230123 11465 156 633   3288 385 13 2
        //rome    1315166 176217 1053 5659   1614546 263151 1591 9177    218064 22529 156 628   2016 215 13 2


            while (result.hasNext()) {
                Record record = result.next();
                List<Value> values = record.values();
                for (Value value : values) {
                    if (value.type().name().equals("PATH")) {

                        Path p = value.asPath();
                        System.out.println(p);
                        /*if(num==3996||num==3995||num==3994
                               ){
                            System.out.println("第"+num+"条"+p+"第"+num);
                            System.out.println("-----------");
                            System.out.println();
                            System.out.println();

                        }*/

                        System.out.println(num);
                        num++;
                        /*if(num%50==0){
                            System.out.println(num);
                        }*/



                    }
                }


            }
        System.out.println(num);
    }


}
