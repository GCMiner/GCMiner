package Preprocess;

import faketabby.newJson.*;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;

import java.util.*;

public class TEST {

    public static String mapString = null;

    public static List<Integer> sourceid = new LinkedList<>();

    public static List<Integer> sinkid = new LinkedList<>();

    public void setMapString(String mapString) {
        this.mapString = mapString;
    }

    public String getMapString() {
        return mapString;
    }

    public static List<String[]> edgeAll = new ArrayList<>();

    public void clearEdge(){
        edgeAll.clear();
    }

    public List<String[]> getEdgeAll() { return edgeAll; }
    //private static HashMap<String,HashMap> map = new HashMap();
    private static HashMap<String,List> map = new HashMap();
    private static List<HashMap> resultList = new LinkedList<>();

    public static void test() {

        int i = 1;
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));
        Session session = driver.session();

        String jsonPath = "./src/main/resources";

        try {


            //Map<Long, Node> nodesMap = new HashMap<>();
            //Map<Long, Node> nodesMap = new LinkedHashMap<>(); //HashMap存入数据与取出数据顺序不一致


            String cmdSql1 = "match (source:Method {SIGNATURE:'<java.util.PriorityQueue: void readObject(java.io.ObjectInputStream)>'})\n" +
                    "match (sink:Method {SIGNATURE:'<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>'})\n" +
                    "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS', 10) yield path \n" +
                    "return path limit 50";

            //CC:4.0    CC2,4,8
            //CC:3.1    CC5,6,7,9
            //CC:3.2.1  CC10

            String cmdSqlcc2 = "//cc2\n" +
                    "match path = (m1:Method {SIGNATURE:'<java.util.PriorityQueue: void readObject(java.io.ObjectInputStream)>'})-[:CALL ]->(m2:Method {NAME:'heapify'})-[:CALL ]->(m3)-[:CALL]->(m4:Method {NAME:'siftDownUsingComparator'})-[:CALL]->(m5)-[:ALIAS*]-(m6 )-[:CALL]->(m7)-[:ALIAS*]-(m8:Method)-[:CALL]->(m9:Method {IS_SINK:true}) return path";

            String cmdSqlcc5  = "// cc5\n" +
                    "match path=(m1:Method {SIGNATURE:'<javax.management.BadAttributeValueExpException: void readObject(java.io.ObjectInputStream)>'})-[:CALL]->(m2:Method {NAME:'toString'})-[:ALIAS*]-(m3:Method {SIGNATURE:'<org.apache.commons.collections.keyvalue.TiedMapEntry: java.lang.String toString()>'})-[:CALL]->(m4:Method {NAME:'getValue'})-[:CALL]->(m5:Method {NAME:'get'})-[:ALIAS*1..2]-(m6:Method {NAME:'get'})-[:CALL]->(m7:Method {NAME:'transform'})-[:ALIAS*]-(m8:Method)-[:CALL]->(m9:Method {IS_SINK:true}) return path";

            String cmdSqlcc6 = "//cc6cc10\n" +
                    "match (source:Method {SIGNATURE:'<java.util.HashSet: void readObject(java.io.ObjectInputStream)>'})-[:CALL]->(m2:Method) where (m2.NAME in ['defaultReadObject','readFloat','readObject'])=false\n" +
                    "match (sink:Method {IS_SINK:true,NAME:'invoke'})<-[:CALL]-(m1:Method {NAME:'transform'})\n" +
                    "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS', 10) yield path\n" +
                    "return *";

            String cmdSqlcc7 ="//cc7\n" +
                    "match path=(m1:Method {SIGNATURE:'<java.util.Hashtable: void readObject(java.io.ObjectInputStream)>'})-[:CALL ]->(m2:Method {NAME:'reconstitutionPut'})-[:CALL ]->(m3:Method {NAME:'equals'})-[:ALIAS*..2]-(m4:Method)-[:CALL ]->(m5:Method {NAME:'get'})-[:ALIAS*1..2]-(m6:Method {NAME:'get'})-[:CALL]->(m7:Method {NAME:'transform'})-[:ALIAS*]-(m8:Method)-[:CALL]->(m9:Method {IS_SINK:true})  return path";

            String cmdSqlcc8 = "//cc8\n" +
                    "match path=(m1:Method {SIGNATURE:'<org.apache.commons.collections4.bag.TreeBag: void readObject(java.io.ObjectInputStream)>'})-[:CALL ]->(m2:Method {NAME:'doReadObject'})-[:CALL ]->(m3:Method {NAME:'put'})-[:ALIAS*1..4]-(m4:Method)-[:CALL ]->(m5:Method {NAME:'compare'})-[:CALL ]->(m6:Method)-[:ALIAS*]-(m7:Method {SIGNATURE:'<org.apache.commons.collections4.comparators.TransformingComparator: int compare(java.lang.Object,java.lang.Object)>'})-[:CALL ]->(m8)-[:ALIAS*]-(m9:Method)-[:CALL*..5 ]->(m10:Method {IS_SINK:true}) return path";

            String cmdSqlcc9="//cc9\n" +
                    "match path=(m1:Method {SIGNATURE:'<java.util.Hashtable: void readObject(java.io.ObjectInputStream)>'})-[:CALL]->(m2:Method {NAME:'reconstitutionPut'})-[:CALL]->(m3:Method {NAME:'hashCode'})-[:ALIAS*]-(m4:Method {SIGNATURE:'<org.apache.commons.collections.keyvalue.TiedMapEntry: int hashCode()>'})-[:CALL]->(m5:Method {NAME:'getValue'})-[:CALL]->(m6:Method {NAME:'get'})-[:ALIAS*1..2]-(m7:Method {NAME:'get'})-[:CALL]->(m8:Method {NAME:'transform'})-[:ALIAS*]-(m9:Method)-[:CALL]->(m10:Method {IS_SINK:true}) return path";
            String cmdSql =
                    "match (source:Method {NAME:'readObject'})\n" +
                            "match (sink:Method {SIGNATURE:'<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>'})\n" +
                            "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS', 10) yield path \n" +
                            "return path limit 2";

            // XStream

            //CVE-2021-21346
            String xstream ="//xstream 2021-1\n" +
                    "match p1 = (m3:Method {CLASSNAME:'com.sun.org.apache.xpath.internal.objects.XString'})-[:ALIAS*..3]-(m2:Method {NAME:'equals'})<-[:CALL]-(source:Method {NAME:'compareTo',CLASSNAME:'javax.naming.ldap.Rdn$RdnEntry'})\n" +
                    "match p2 = (sink:Method {IS_SINK:true,NAME:'invoke'})<-[:CALL]-(m1:Method {CLASSNAME:'sun.swing.SwingLazyValue',NAME:'createValue'}) \n" +
                    "call apoc.algo.allSimplePaths(m1, m3, '<CALL|ALIAS', 12) yield path\n" +
                    "return apoc.path.combine(apoc.path.combine(p2,path),p1) limit 20";




            //CVE-2021-21351
            String xstream1 = "//xstream 2021-2\n" +
                    "match (source:Method {NAME:'compareTo',CLASSNAME:'javax.naming.ldap.Rdn$RdnEntry'}) \n" +
                    "match p1 = (sink:Method {IS_SINK:true,NAME:'invoke'})<-[:CALL]-(m1:Method {CLASSNAME:'com.sun.org.apache.xml.internal.dtm.ref.IncrementalSAXSource_Xerces',NAME:'parseSome'}) " +
                    "call apoc.algo.allSimplePaths(m1, source, '<CALL|ALIAS', 12) yield path\n" +
                    "return apoc.path.combine(p1,path) limit 20";

            //CVE-2021-21345
            String xstream2 = "//xstream cve-2021-21345\n" +
                    "match path=(source:Method {NAME:'compare',CLASSNAME:'sun.awt.datatransfer.DataTransferer$IndexOrderComparator'})-[:CALL]->(m1:Method {NAME:'compareIndices'})-[:CALL]->(m2:Method {NAME:'get'})-[:ALIAS*..3]-(m3:Method {CLASSNAME:'com.sun.xml.internal.ws.client.ResponseContext'})-[:CALL]->(m4:Method {NAME:'getAttachments'})-[:ALIAS*..3]-(m5:Method {CLASSNAME:'com.sun.xml.internal.ws.encoding.xml.XMLMessage$XMLMultiPart'})-[:CALL]->(m6:Method {NAME:'getMessage'})-[:CALL]->(m7:Method {NAME:'getInputStream'})-[:ALIAS*..3]-(m8:Method {CLASSNAME:'com.sun.xml.internal.ws.message.JAXBAttachment'})-[:CALL]->(m9:Method {NAME:'asInputStream'})-[:CALL]->(m10:Method {NAME:'writeTo'})-[:CALL]->(m11:Method {NAME:'marshal'})-[:ALIAS*..3]-(m12:Method {SIGNATURE:'<com.sun.xml.internal.ws.db.glassfish.BridgeWrapper: void marshal(java.lang.Object,java.io.OutputStream,javax.xml.namespace.NamespaceContext,javax.xml.bind.attachment.AttachmentMarshaller)>'})-[:CALL]->(m13:Method {NAME:'marshal'})-[:CALL]->(m14:Method {NAME:'marshal'})-[:ALIAS*..3]-(m15:Method {CLASSNAME:'com.sun.xml.internal.bind.v2.runtime.BridgeImpl'})-[:CALL]->(m16:Method {NAME:'write'})-[:CALL]->(m17:Method {NAME:'childAsXsiType'})-[:CALL]->(m18:Method {NAME:'serializeURIs'})-[:ALIAS]-(m19:Method {CLASSNAME:'com.sun.xml.internal.bind.v2.runtime.ClassBeanInfoImpl'})-[:CALL]->(m20:Method {NAME:'get'})-[:ALIAS]-(m21:Method {CLASSNAME:'com.sun.xml.internal.bind.v2.runtime.reflect.Accessor$GetterSetterReflection'})-[:CALL]->(m22:Method {IS_SINK:true, NAME:'invoke'}) return path";

            //ImageIO filter
            String xstream3 = "//xstream 2020-1\n" +
                    "match p1 = (from:Method {IS_SINK:true,NAME:'invoke'})<-[:CALL]-(m1:Method {NAME:'filter'})-[:ALIAS]-(m2:Method)<-[:CALL]-(m3:Method {NAME:'advance'})<-[:CALL]-(m4:Method {NAME:'next'})-[:ALIAS]-(m5:Method )<-[:CALL]-(m6:Method {NAME:'nextElement'})-[:ALIAS]-(m7:Method)<-[:CALL]-(m8:Method {NAME:'nextStream'})\n" +
                    "match (to:Method {NAME:'hashCode'})  \n" +
                    "call apoc.algo.allSimplePaths(m8, to, '<CALL|ALIAS', 12) yield path \n" +
                    "return apoc.path.combine(p1,path) limit 20";

            String xstream4 = "match (source:Method {NAME:'nextElement'})\n" +
                    "                    <-[:HAS]-(cls:Class)-[:INTERFACE|EXTENDS*]\n" +
                    "                    ->(cls1:Class {NAME:'java.util.Enumeration'})\n" +
                    "match p1 = (m1:Method)<-[:CALL]-(source:Method)\n" +
                    "match (sink:Method {IS_SINK:true, VUL:'JNDI'})\n" +
                    "call apoc.algo.allSimplePaths(sink, m1, '<CALL|ALIAS', 8) yield path \n" +
                    "where none(n in nodes(path) where n.CLASSNAME in ['java.beans.EventHandler','java.lang.ProcessBuilder',\n" +
                    "'javax.imageio.ImageIO$ContainsFilter','jdk.nashorn.internal.objects.NativeString',\n" +
                    "'com.sun.corba.se.impl.activation.ServerTableEntry',\n" +
                    "'com.sun.tools.javac.processing.JavacProcessingEnvironment$NameProcessIterator',\n" +
                    "'sun.awt.datatransfer.DataTransferer$IndexOrderComparator','sun.swing.SwingLazyValue',\n" +
                    "'com.sun.jndi.toolkit.dir.LazySearchEnumerationImpl','sun.rmi.registry.RegistryImpl_Stub',\n" +
                    "\n" +
                    "'com.sun.jndi.dns.BindingEnumeration','com.sun.jndi.cosnaming.CNBindingEnumeration','com.sun.jndi.toolkit.dir.HierMemDirCtx$FlatBindings','com.sun.jndi.ldap.LdapReferralException'])\n" +
                    "return apoc.path.combine(path,p1) limit 50";

            //CVE-2020-26217
            String xstream5 = "match path = (m1:Method {CLASSNAME:'jdk.nashorn.internal.objects.NativeString',NAME:'hashCode'})-[:CALL]->(m2:Method {CLASSNAME:'jdk.nashorn.internal.objects.NativeString',NAME:'getStringValue'})-[:CALL]->(m3)-[:ALIAS]-(m4)-[:ALIAS]-(m5:Method {CLASSNAME:'com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data',NAME:'toString'})-[:CALL]->(m6:Method {CLASSNAME:'com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data',NAME:'get'})-[:CALL]->(m7:Method {CLASSNAME:'com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx',NAME:'readFrom'})-[:CALL]->(m8)-[:ALIAS]-(m9:Method {CLASSNAME:'java.io.SequenceInputStream',NAME:'read'})-[:CALL]->(m10:Method {CLASSNAME:'java.io.SequenceInputStream',NAME:'nextStream'})-[:CALL]->(m11)-[:ALIAS]-(m12:Method {CLASSNAME:'javax.swing.MultiUIDefaults$MultiUIDefaultsEnumerator',NAME:'nextElement'})-[:CALL]->(m13)-[:ALIAS]-(m14:Method {CLASSNAME:'javax.imageio.spi.FilterIterator',NAME:'next'})-[:CALL]->(m15:Method {CLASSNAME:'javax.imageio.spi.FilterIterator',NAME:'advance'})-[:CALL]->(m16)-[:ALIAS]-(m17:Method {CLASSNAME:'javax.imageio.ImageIO$ContainsFilter',NAME:'filter'})-[:CALL]->(m18:Method {IS_SINK:true})\n" +
                    "RETURN path limit 1";

            //CVE-2021-29505
            String xstream6 = "match path1 = (m27:Method{CLASSNAME:'com.sun.jndi.rmi.registry.BindingEnumeration',NAME:'next'})-[:ALIAS]-(m26:Method)<-[:CALL]-(m25:Method{CLASSNAME:'com.sun.jndi.toolkit.dir.LazySearchEnumerationImpl',NAME:'findNextMatch'})<-[:CALL]-(m24:Method{CLASSNAME:'com.sun.jndi.toolkit.dir.LazySearchEnumerationImpl',NAME:'nextElement'})<-[:CALL]-(m23:Method)-[:ALIAS]-(m22:Method)<-[:CALL]-(m21:Method{CLASSNAME:'com.sun.org.apache.xml.internal.security.keys.storage.implementations.KeyStoreResolver$KeyStoreIterator',NAME:'findNextCert'})<-[:CALL]-(m20:Method{CLASSNAME:'com.sun.org.apache.xml.internal.security.keys.storage.implementations.KeyStoreResolver$KeyStoreIterator',NAME:'hasNext'})-[:ALIAS]-(m19:Method)<-[:CALL]-(m18:Method{CLASSNAME:'com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage',NAME:'makeProgress'})<-[:CALL]-(m17:Method{CLASSNAME:'com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage',NAME:'parseAll'})<-[:CALL]-(m16:Method{CLASSNAME:'com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage',NAME:'getAttachments'})<-[:CALL]-(m15:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePullMultipart',NAME:'parseAll'})<-[:CALL]-(m14:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePullMultipart',NAME:'parse'})-[:ALIAS]-(m13:Method)<-[:CALL]-(m12:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart',NAME:'getCount'})<-[:CALL]-(m11:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.soap.MessageImpl',NAME:'initializeAllAttachments'})<-[:CALL]-(m10:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.soap.MessageImpl',NAME:'getAttachments'})-[:ALIAS]-(m9:Method)<-[:CALL]-(m8:Method{CLASSNAME:'com.sun.xml.internal.ws.message.saaj.SAAJMessage$SAAJAttachmentSet'})<-[:CALL]-(m7:Method{CLASSNAME:'com.sun.xml.internal.ws.message.saaj.SAAJMessage',NAME:'getAttachments'})<-[:CALL]-(m6:Method{CLASSNAME:'com.sun.xml.internal.ws.message.saaj.SAAJMessage',NAME:'copy'})-[:ALIAS]-(m5:Method)<-[:CALL]-(m4:Method{CLASSNAME:'com.sun.xml.internal.ws.api.message.Packet',NAME:'toString'})-[:ALIAS]-(m3:Method)<-[:CALL]-(m2:Method {CLASSNAME:'com.sun.org.apache.xpath.internal.objects.XString',NAME:'equals'})-[:ALIAS*]-(m1:Method)<-[:CALL]-(source:Method {CLASSNAME:'javax.naming.ldap.Rdn$RdnEntry',NAME:'compareTo'})\n" +
                    "\n" +
                    "match(sink:Method{CLASSNAME:'sun.rmi.registry.RegistryImpl_Stub',NAME:'lookup'})\n" +
                    "\n" +
                    "call apoc.algo.allSimplePaths(sink, m27, '<CALL|ALIAS', 20) yield path\n" +
                    "\n" +
                    "return apoc.path.combine(path,path1) limit 1 ";

            //CVE-2021-39145(CVE-2021-39151)
            String xstream7 = "match path = (source:Method {CLASSNAME:'javax.naming.ldap.Rdn$RdnEntry',NAME:'compareTo'})-[:CALL]->(m1:Method)-[:ALIAS*]-(m2:Method {CLASSNAME:'com.sun.org.apache.xpath.internal.objects.XString',NAME:'equals'})-[:CALL]->(m3:Method)-[:ALIAS]-(m4:Method{CLASSNAME:'com.sun.xml.internal.ws.api.message.Packet',NAME:'toString'})-[:CALL]->(m5:Method)-[:ALIAS]-(m6:Method{CLASSNAME:'com.sun.xml.internal.ws.message.saaj.SAAJMessage',NAME:'copy'})-[:CALL]->(m7:Method{CLASSNAME:'com.sun.xml.internal.ws.message.saaj.SAAJMessage',NAME:'getAttachments'})-[:CALL]->(m8:Method{CLASSNAME:'com.sun.xml.internal.ws.message.saaj.SAAJMessage$SAAJAttachmentSet'})-[:CALL]->(m9:Method)-[:ALIAS]-(m10:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.soap.MessageImpl',NAME:'getAttachments'})-[:CALL]->(m11:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.soap.MessageImpl',NAME:'initializeAllAttachments'})-[:CALL]->(m12:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart',NAME:'getCount'})-[:CALL]->(m13:Method)-[:ALIAS]-(m14:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePullMultipart',NAME:'parse'})-[:CALL]->(m15:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePullMultipart',NAME:'parseAll'})-[:CALL]->(m16:Method{CLASSNAME:'com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage',NAME:'getAttachments'})-[:CALL]->(m17:Method{CLASSNAME:'com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage',NAME:'parseAll'})-[:CALL]->(m18:Method{CLASSNAME:'com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage',NAME:'makeProgress'})-[:CALL]->(m19:Method)-[:ALIAS]-(m20:Method{CLASSNAME:'com.sun.org.apache.xml.internal.security.keys.storage.implementations.KeyStoreResolver$KeyStoreIterator',NAME:'hasNext'})-[:CALL]->(m21:Method{CLASSNAME:'com.sun.org.apache.xml.internal.security.keys.storage.implementations.KeyStoreResolver$KeyStoreIterator',NAME:'findNextCert'})-[:CALL]->(m22)-[:ALIAS]-(m23:Method{CLASSNAME:'com.sun.jndi.ldap.AbstractLdapNamingEnumeration',NAME:'nextElement'})-[:CALL]->(m24:Method{CLASSNAME:'com.sun.jndi.ldap.AbstractLdapNamingEnumeration',NAME:'nextElement'})-[:CALL]->(m25:Method{CLASSNAME:'com.sun.jndi.ldap.AbstractLdapNamingEnumeration',NAME:'next'})-[:CALL]->(m26:Method{CLASSNAME:'com.sun.jndi.ldap.AbstractLdapNamingEnumeration',NAME:'nextImpl'})-[:CALL]->(m27:Method{CLASSNAME:'com.sun.jndi.ldap.AbstractLdapNamingEnumeration',NAME:'nextAux'})-[:CALL]->(m28:Method{CLASSNAME:'com.sun.jndi.ldap.AbstractLdapNamingEnumeration',NAME:'createItem'})-[:ALIAS]-(m29:Method{CLASSNAME:'com.sun.jndi.ldap.LdapBindingEnumeration',NAME:'createItem'})-[:CALL]->(m30:Method{CLASSNAME:'com.sun.jndi.ldap.LdapBindingEnumeration',NAME:'createItem'})-[:CALL]->\n" +
                    "(sink:Method{CLASSNAME:'javax.naming.spi.DirectoryManager',NAME:'getObjectInstance'})\n" +
                    "return path";

            //CVE-2021-39147
            String xstream8 = "match path = (source:Method {CLASSNAME:'javax.naming.ldap.Rdn$RdnEntry',NAME:'compareTo'})-[:CALL]->(m1:Method)-[:ALIAS*]-(m2:Method {CLASSNAME:'com.sun.org.apache.xpath.internal.objects.XString',NAME:'equals'})-[:CALL]->(m3:Method)-[:ALIAS]-(m4:Method{CLASSNAME:'com.sun.xml.internal.ws.api.message.Packet',NAME:'toString'})-[:CALL]->(m5:Method)-[:ALIAS]-(m6:Method{CLASSNAME:'com.sun.xml.internal.ws.message.saaj.SAAJMessage',NAME:'copy'})-[:CALL]->(m7:Method{CLASSNAME:'com.sun.xml.internal.ws.message.saaj.SAAJMessage',NAME:'getAttachments'})-[:CALL]->(m8:Method{CLASSNAME:'com.sun.xml.internal.ws.message.saaj.SAAJMessage$SAAJAttachmentSet'})-[:CALL]->(m9:Method)-[:ALIAS]-(m10:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.soap.MessageImpl',NAME:'getAttachments'})-[:CALL]->(m11:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.soap.MessageImpl',NAME:'initializeAllAttachments'})-[:CALL]->(m12:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart',NAME:'getCount'})-[:CALL]->(m13:Method)-[:ALIAS]-(m14:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePullMultipart',NAME:'parse'})-[:CALL]->(m15:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePullMultipart',NAME:'parseAll'})-[:CALL]->(m16:Method{CLASSNAME:'com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage',NAME:'getAttachments'})-[:CALL]->(m17:Method{CLASSNAME:'com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage',NAME:'parseAll'})-[:CALL]->(m18:Method{CLASSNAME:'com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage',NAME:'makeProgress'})-[:CALL]->(m19:Method)-[:ALIAS]-(m20:Method{CLASSNAME:'com.sun.org.apache.xml.internal.security.keys.storage.implementations.KeyStoreResolver$KeyStoreIterator',NAME:'hasNext'})-[:CALL]->(m21:Method{CLASSNAME:'com.sun.org.apache.xml.internal.security.keys.storage.implementations.KeyStoreResolver$KeyStoreIterator',NAME:'findNextCert'})-[:CALL]->(m22)-[:ALIAS]-(m23:Method{CLASSNAME:'com.sun.jndi.ldap.AbstractLdapNamingEnumeration',NAME:'nextElement'})-[:CALL]->(m24:Method{CLASSNAME:'com.sun.jndi.ldap.AbstractLdapNamingEnumeration',NAME:'nextElement'})-[:CALL]->(m25:Method{CLASSNAME:'com.sun.jndi.ldap.AbstractLdapNamingEnumeration',NAME:'next'})-[:CALL]->(m26:Method{CLASSNAME:'com.sun.jndi.ldap.AbstractLdapNamingEnumeration',NAME:'nextImpl'})-[:CALL]->(m27:Method{CLASSNAME:'com.sun.jndi.ldap.AbstractLdapNamingEnumeration',NAME:'nextAux'})-[:CALL]->(m28:Method{CLASSNAME:'com.sun.jndi.ldap.AbstractLdapNamingEnumeration',NAME:'createItem'})-[:ALIAS]-(m29:Method{CLASSNAME:'com.sun.jndi.ldap.LdapSearchEnumeration',NAME:'createItem'})-[:CALL]->(m30:Method{CLASSNAME:'com.sun.jndi.ldap.LdapSearchEnumeration',NAME:'createItem'})-[:CALL]->\n" +
                    "(sink:Method{CLASSNAME:'javax.naming.spi.DirectoryManager',NAME:'getObjectInstance'})\n" +
                    "return path";

            //CVE-2021-39148
            String xstream9 = "match path = (source:Method {CLASSNAME:'javax.naming.ldap.Rdn$RdnEntry',NAME:'compareTo'})-[:CALL]->(m1:Method)-[:ALIAS*]-(m2:Method {CLASSNAME:'com.sun.org.apache.xpath.internal.objects.XString',NAME:'equals'})-[:CALL]->(m3:Method)-[:ALIAS]-(m4:Method{CLASSNAME:'com.sun.xml.internal.ws.api.message.Packet',NAME:'toString'})-[:CALL]->(m5:Method)-[:ALIAS]-(m6:Method{CLASSNAME:'com.sun.xml.internal.ws.message.saaj.SAAJMessage',NAME:'copy'})-[:CALL]->(m7:Method{CLASSNAME:'com.sun.xml.internal.ws.message.saaj.SAAJMessage',NAME:'getAttachments'})-[:CALL]->(m8:Method{CLASSNAME:'com.sun.xml.internal.ws.message.saaj.SAAJMessage$SAAJAttachmentSet'})-[:CALL]->(m9:Method)-[:ALIAS]-(m10:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.soap.MessageImpl',NAME:'getAttachments'})-[:CALL]->(m11:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.soap.MessageImpl',NAME:'initializeAllAttachments'})-[:CALL]->(m12:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart',NAME:'getCount'})-[:CALL]->(m13:Method)-[:ALIAS]-(m14:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePullMultipart',NAME:'parse'})-[:CALL]->(m15:Method{CLASSNAME:'com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimePullMultipart',NAME:'parseAll'})-[:CALL]->(m16:Method{CLASSNAME:'com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage',NAME:'getAttachments'})-[:CALL]->(m17:Method{CLASSNAME:'com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage',NAME:'parseAll'})-[:CALL]->(m18:Method{CLASSNAME:'com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage',NAME:'makeProgress'})-[:CALL]->(m19:Method)-[:ALIAS]-(m20:Method{CLASSNAME:'com.sun.org.apache.xml.internal.security.keys.storage.implementations.KeyStoreResolver$KeyStoreIterator',NAME:'hasNext'})-[:CALL]->(m21:Method{CLASSNAME:'com.sun.org.apache.xml.internal.security.keys.storage.implementations.KeyStoreResolver$KeyStoreIterator',NAME:'findNextCert'})-[:CALL]->(m22:Method{NAME:'nextElement',CLASSNAME:'java.util.Enumeration'})-[:ALIAS]-(m23:Method{NAME:'nextElement',CLASSNAME:'com.sun.jndi.toolkit.dir.ContextEnumerator'})-[:CALL]->(m24:Method{NAME:'nextElement',CLASSNAME:'com.sun.jndi.toolkit.dir.ContextEnumerator'})-[:CALL]->(m25:Method{NAME:'next',CLASSNAME:'com.sun.jndi.toolkit.dir.ContextEnumerator'})-[:CALL]->(m26:Method{NAME:'getNextDescendant',CLASSNAME:'com.sun.jndi.toolkit.dir.ContextEnumerator'})-[:CALL]->\n" +
                    "(m27:Method{CLASSNAME:'com.sun.jndi.toolkit.dir.ContextEnumerator',NAME:'prepNextChild'})-[:CALL]->\n" +
                    "(m28:Method{CLASSNAME:'com.sun.jndi.toolkit.dir.ContextEnumerator',NAME:'getNextChild'})-[:CALL]->\n" +
                    "(m29)-[:ALIAS]-\n" +
                    "(m30:Method{CLASSNAME:'javax.naming.spi.ContinuationContext',NAME:'getNameParser'})-[:CALL]->(m31:Method{CLASSNAME:'javax.naming.spi.ContinuationContext',NAME:'getTargetContext'})-[:CALL]->\n" +
                    "(m32:Method{NAME:'getContext',CLASSNAME:'javax.naming.spi.NamingManager'})\n" +
                    "return path";

            //JDK
            String jdk1 = "//jdbcrowsetimpl\n" +
                    "match path=(m1:Method)-[:CALL*..5]->(m2:Method {IS_SINK:true,NAME:'lookup'}) where m1.NAME =~ 'set.*' return path";

            String jdk2 = "//LdapAttribute\n" +
                    "match (source:Method {HAS_PARAMETERS:false}) where source.NAME =~ 'get.*' and (source.IS_SERIALIZABLE = true or source.IS_STATIC=true)\n" +
                    "match (sink:Method {NAME:'lookup'})\n" +
                    "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS', 3) yield path\n" +
                    "return path limit 20";

            String jdk3 = "// URLDNS jdk 8\n" +
                    "match path=(source:Method {NAME:'readObject'})-[:CALL]->(m1:Method {NAME:'hash'})-[:CALL]->(m2:Method {NAME:'hashCode'})\n" +
                    "return path";


            //clojure
            String clojure = "match path = (m2:Method{CLASSNAME:'clojure.inspector.proxy$javax.swing.table.AbstractTableModel$ff19274a',NAME:'hashCode'})-[:CALL]->(m3)-[:ALIAS]-(sink:Method{CLASSNAME:'clojure.main$eval_opt',NAME:'invoke'}) \n" +
                    "return path ";

            //cp30
            String cp30 = "match path = (m1:Method{CLASSNAME:'com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase',NAME:'readObject'})-[*..3]-(m2:Method{CLASSNAME:'com.mchange.v2.naming.ReferenceIndirector$ReferenceSerialized',NAME:'getObject'})-[*..2]-(m3:Method{CLASSNAME:'com.sun.jndi.rmi.registry.RegistryContext',NAME:'lookup'})\n" +
                    "RETURN path";

            ///////固定ground truth的source和sink
            //CVE-2021-29505
            String gt1 = "match (source:Method {CLASSNAME:'javax.naming.ldap.Rdn$RdnEntry',NAME:'compareTo'})\n" +
                    "match(sink:Method{CLASSNAME:'sun.rmi.registry.RegistryImpl_Stub',NAME:'lookup'})\n" +
                    "call apoc.algo.allSimplePaths(sink, source, '<CALL|ALIAS', 20) yield path " +
                    "where none(n in nodes(path) where n.CLASSNAME in ['java.beans.EventHandler','java.lang.ProcessBuilder',\n" +
                    "'javax.imageio.ImageIO$ContainsFilter','jdk.nashorn.internal.objects.NativeString',\n" +
                    "'com.sun.corba.se.impl.activation.ServerTableEntry',\n" +
                    "'com.sun.tools.javac.processing.JavacProcessingEnvironment$NameProcessIterator',\n" +
                    "'sun.awt.datatransfer.DataTransferer$IndexOrderComparator','sun.swing.SwingLazyValue',\n" +
                    "'com.sun.jndi.toolkit.dir.LazySearchEnumerationImpl','sun.rmi.registry.RegistryImpl_Stub',\n" +
                    "\n" +
                    "'com.sun.jndi.dns.BindingEnumeration','com.sun.jndi.cosnaming.CNBindingEnumeration','com.sun.jndi.toolkit.dir.HierMemDirCtx$FlatBindings','com.sun.jndi.ldap.LdapReferralException','java.util.HashMap.class','java.util.HashSet.class','java.util.Hashtable.class','java.util.LinkedHashMap.class','java.util.LinkedHashSet.class'])\n" +
                    "return path";
            //CVE-2020-26217
            String gt2 = "match (m1:Method {CLASSNAME:'jdk.nashorn.internal.objects.NativeString',NAME:'hashCode'})\n" +
                    "match(sink:Method{CLASSNAME:'java.lang.reflect.Method',NAME:'invoke'})\n" +
                    "call apoc.algo.allSimplePaths(sink, m1, '<CALL|ALIAS', 20) yield path\n" +
                    "return path";
            //CVE-2021-39145(CVE-2021-39151)
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
            //CVE-2021-39148
            String gt4 = "match (m1:Method {CLASSNAME:'javax.naming.ldap.Rdn$RdnEntry',NAME:'compareTo'})  match(sink:Method{NAME:'getContext',CLASSNAME:'javax.naming.spi.NamingManager'})\n" +
                    "call apoc.algo.allSimplePaths(sink, m1, '<CALL|ALIAS', 15) yield path\n" +
                    "where none(n in nodes(path) where n.CLASSNAME in ['java.beans.EventHandler','java.lang.ProcessBuilder',\n" +
                    "'javax.imageio.ImageIO$ContainsFilter','jdk.nashorn.internal.objects.NativeString',\n" +
                    "'com.sun.corba.se.impl.activation.ServerTableEntry',\n" +
                    "'com.sun.tools.javac.processing.JavacProcessingEnvironment$NameProcessIterator',\n" +
                    "'sun.awt.datatransfer.DataTransferer$IndexOrderComparator','sun.swing.SwingLazyValue',\n" +
                    "'com.sun.jndi.toolkit.dir.LazySearchEnumerationImpl','sun.rmi.registry.RegistryImpl_Stub'])\n" +
                    "return path";
            
            //cc2
            String gt5 = "match (m1:Method {SIGNATURE:'<java.util.PriorityQueue: void readObject(java.io.ObjectInputStream)>'})\n" +
                    "match(sink:Method{IS_SINK:true})\n" +
                    "call apoc.algo.allSimplePaths(sink, m1, '<CALL|ALIAS', 20) yield path\n" +
                    "return path";

            //h
            String gt6 = "match (source:Method {NAME:'readObject',IS_SERIALIZABLE:true}) where (source.CLASSNAME in ['java.text.SimpleDateFormat','javax.swing.JOptionPane']) = false\n" +
                    "match p1 = (sink:Method {IS_SINK:true,NAME:'invoke'})<-[:CALL]-(m1:Method {NAME:'get'}) where m1.CLASSNAME =~ 'org.hibernate.*'\n" +
                    "call apoc.algo.allSimplePaths(m1, source, '<CALL|ALIAS', 12) yield path\n" +
                    "return apoc.path.combine(p1,path)";


            Result result = session.run(cmdSql1);


            Scanner sc = new Scanner(System.in);

            System.out.println("初步demo还未完善。分两轮执行：第一轮输入1，第二轮输入2");

            int round = sc.nextInt();

            int choose = 0;
            if(round==2){
                System.out.println("该查询语言从source还是sink开始查？3代表source，4代表sink");
                choose = sc.nextInt();
            }


            int numOfPath = 1;

            for (Record record : result.list()) {
                //System.out.println(record);
                List<Value> values = record.values();

                Map<Long, Node> nodesMap = new LinkedHashMap<>();
                for (Value value : values) {
                    //System.out.println(value);


                    if (value.type().name().equals("PATH")) {

                        String temp = "";
                        int num = 1;

                        Path p = value.asPath();

                        if(choose==4){
                            if(numOfPath==1740){

                            }
                            System.out.println("第"+numOfPath+"条"+p);
                            /*System.out.println(1);
                            System.out.println(p.toString());
                            System.out.println(2);*/
                            String sid = p.toString().substring(p.toString().lastIndexOf("(")+1,p.toString().lastIndexOf(")"));
                            System.out.println("source:"+sid);
                            sourceid.add(Integer.parseInt(sid));
                            String kid = p.toString().substring(p.toString().indexOf("(")+1,p.toString().indexOf(")"));
                            sinkid.add(Integer.parseInt(kid));
                        }
                        if(choose==3){
                            System.out.println(p);
                            /*System.out.println(1);
                            System.out.println(p.toString());
                            System.out.println(2);*/
                            String sid = p.toString().substring(p.toString().indexOf("(")+1,p.toString().indexOf(")"));
                            System.out.println("source:"+sid);
                            sourceid.add(Integer.parseInt(sid));
                            String kid = p.toString().substring(p.toString().lastIndexOf("(")+1,p.toString().lastIndexOf(")"));
                            sinkid.add(Integer.parseInt(kid));
                        }



                        Iterable<Node> nodes = p.nodes();

                        for (Node node : nodes) {
                            nodesMap.put(node.id(), node);
                        }

                        Iterable<Relationship> relationships = p.relationships();
                        if(choose==3){//source到sink
                            for (Relationship relationship : relationships) {



                                int startID = (int) relationship.startNodeId();
                                int endID = (int) relationship.endNodeId();
                                String relation = relationship.type();

                                String[] edgeInfo = new String[3];



                                /*System.out.println("--------------------------------------");
                                System.out.println(startID + "-" + relation + "-" + endID);
                                System.out.println("--------------------------------------");*/


                                edgeInfo[0] = String.valueOf(startID);
                                edgeInfo[1] = relation;
                                edgeInfo[2] = String.valueOf(endID);


                                if(num>1&&!edgeInfo[0].equals(temp)){
                                    edgeInfo[0] = String.valueOf(endID);
                                    edgeInfo[2] = String.valueOf(startID);
                                }
                                num++;
                                temp = edgeInfo[2];
                                edgeAll.add(edgeInfo);

                            }
                        }

                        if(choose==4){//sink到source，最后链表反转变成source到sink
                            for (Relationship relationship : relationships) {

                                int startID = (int) relationship.startNodeId();
                                int endID = (int) relationship.endNodeId();
                                String relation = relationship.type();

                                String[] edgeInfo = new String[3];


                                /*System.out.println("--------------------------------------");
                                System.out.println(startID + "-" + relation + "-" + endID);
                                System.out.println("--------------------------------------");*/

                                edgeInfo[0] = String.valueOf(startID);
                                edgeInfo[1] = relation;
                                edgeInfo[2] = String.valueOf(endID);

                                if(num==1){
                                    System.out.println("---------------");
                                    System.out.println("StartID:"+startID);
                                    System.out.println("---------------");
                                }


                                if(num>1&&!edgeInfo[2].equals(temp)){
                                    edgeInfo[0] = String.valueOf(endID);
                                    edgeInfo[2] = String.valueOf(startID);
                                }
                                num++;
                                temp = edgeInfo[0];
                                edgeAll.add(edgeInfo);

                            }
                            //反转链表
                            Collections.reverse(edgeAll);
                        }

                        String s = "chain" + i;
                        if(round==2){
                            //map.put(s, NewJson.returnMap(i));
                            resultList.add(NewJson.returnMap(i));
                        }


                        i++;
                        TEST test = new TEST();
                        test.clearEdge();

                        numOfPath++;
                        System.out.println(numOfPath);
                    }


                    //NewJson.returnMap().clear();

                }

                if(round==1){
                    if (Map2Json.createJsonFile(Map2Json.toJson(nodesMap), jsonPath, "chains")) {
                        //System.out.println("成功存入JSON！");
                    } else {
                        System.out.println("存入失败！");
                    }
                }
                //思路：不存成json文件，直接存json格式然后读是否可以？
               /* System.out.println("11111111111111111111");
                if(round==2){
                    System.out.println("2222222222222222");
                    Map2Json.toJson(nodesMap);
                    System.out.println(Map2Json.toJson(nodesMap));
                    TEST test = new TEST();
                    test.setMapString(Map2Json.toJson(nodesMap));
                    String s = "chain" + i;
                    map.put(s, NewJson.returnMap(i));
                    System.out.println("333333333333333333");
                    Map2Json.createJsonFile1(Map2Json.toJson1(map), jsonPath, "NewChains");
                }*/



            }

            for(int x = 0;x<sourceid.size();x++){
                System.out.println(sourceid.get(x));
            }

            if(round==2){
                    map.put("results",resultList);
                    Map2Json.createJsonFile1(Map2Json.toJson1(map), jsonPath, "NewChains");

            }




        } catch (Exception e) {
            System.err.println(e.getClass() + "," + e.getMessage());
        }

    }


}

