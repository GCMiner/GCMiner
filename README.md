<a name="JdLRz"></a>
# GCMiner: Improving Java Deserialization Gadget Chain Mining via Reflection-Guided Exploit Generation

Java deserialization has been shown to introduce security-critical vulnerabilities, which can lead to serious consequences due to its highly dynamic features. To alleviate this problem, existing techniques have been proposed to statically analyze the call relations to identify deserialization-related method invocations for automatically mining exploitable gadget chains. Despite being effective, analyzing only explicit call relations will miss a number of potential gadget chains, for example, some gadget chains are formed due to the dynamic behaviors (e.g., reflection invocation) in the program. Through an empirical study on 86 gadget chains from one widely-used Java deserialization collection and 18 real-world Java applications, we found that: 1) the new set of sources and sinks can help identify more potential gadget chains; and 2) a large number of gadget chain construction and exploitation rely on the reflection invocation to homonymous methods.<br />Motivated by our findings, we propose a novel gadget chain mining technique named GCMiner. First, GCMiner constructs a joint graph structure, Deserialization-Aware Code Property Graph (DA-CPG), to identify deserialization-related method invocations, and retrieves suspicious gadget chains through customized query scripts. Then, GCMiner uses a reflection-guided exploit generation strategy to produce valid object inputs to verify the exploitability of identified gadget chains. The evaluation results show that GCMiner significantly outperforms state-of-the-art tools in chains identification and verification, and discovers 54 known gadget chains that cannot be identified by the baseline approaches.
<a name="ScN0I"></a>
## Prerequisites

Install the necessary dependencies before running the project:
<a name="ZRVYs"></a>
### Software:

- Soot
- Neo4j
- Java 8
- Apache Maven 3.8
<a name="G2LdD"></a>
### Thrid Party Liraries

- [Tabby](https://github.com/wh1t3p1g/tabby)
- [JQF 1.7](https://github.com/rohanpadhye/JQF)
<a name="KwsGN"></a>
## Setup

This section gives the steps, explanations and examples for getting the project running.
<a name="R4vZR"></a>
### 1) Clone this repo
`$ git clone https://github.com/GCMiner/GCMiner.git`
<a name="HSOG5"></a>
### 2) Install Prerequisites
<a name="QvMQH"></a>
### 3) Tabby
This step is necessary for the static analysis to construct Deserialization-Aware Code Property Graph (DA-CPG) and import generated graphs into Neo4j for chain identification and verification.
<a name="gr77a"></a>
## Structure
``` 
├── README.md                         <- The top-level README for developers using this project.
├── src/main/java
│   ├── ChainValidation               <- A toy example about object generation.
│   │   ├── Test.java                 <- Testing scripts.
│   │   ├── data.json                 <- Identified gadget chains of CC2 in Neo4j.
│   │   └── running example.png       <- Process of object generation.
│   ├── Preprocess                    <- Preprocessing scripts
│   │   └── GetChainsFromNeo4j.java   <- Loading graph data from Neo4j.
│   └── com                           <- Running scripts.
│       ├── result                    <- Collection of gadget chains in our benchmark.
│       ├── ObjectGenerator.java      <- A customized generator for object generation.
│       ├── ObjectLogic.java          <- Verify whether the sink method is reached.
│       └── ObjectTest.java           <- Main script files.
│
├── Data                              <- Detailed information about our experiments.
│   ├── Dataset.xlsx                  <- Statistical information about benchmark.
│   └── ScalabilityData.xlsx          <- Statistical information about applications.
│
└── main.py                           <- Necessary dependencies for running GCMiner.
```

## Dataset

<a name="hm94h"></a>
### Benchmark
In our experiments, we manually collect 86 known gadget chains from multiple famous Java applications. In our dataset, six types of statistical information (`Library`, `Application`, `CVE-ID`, `Affected Version`, `Severity`, `Gadget Chain`, ) are included.

- `Library` and `Application` present the deserialization libraries that cause the vulnerability and corresponding affected applications. `Library` of some applications are labeled as `N/A` due to the re-implementation of deserialization operations (i.e., not relying on any deserialization library).
- `CVE-ID` is the corresponding CVE-ID of the gadget chain (if available).
- `Affected Version` is the affected version of applications.
- `Severity` is the severity score of each vulnerability with `CVE-ID`.
- `Gadget Chain` is the gadget chains we manually collected.
<a name="x37Tf"></a>
#### Fields
Complete statistical information of each approach can be found in `Data/Dataset`.

| **Library** | **Application** | **CVE-ID** | **Affected Vetrsion** | **Severity** | **Gadget Chain** |
| --- | --- | --- | --- | --- | --- |
| ysoserial | AspectJWeaver | <br /> | <1.9.2 | <br /> | HashSet.readObject()//HashMap.put()//HashMap.hash()//TiedMapEntry.hashCode()//TiedMapEntry.getValue()//LazyMap.get()//SimpleCache$StorableCachingMap.put()//SimpleCache$StorableCachingMap.writeToPath()//FileOutputStream.write() |
| ysoserial | ... | ... | ... | ... | ... |
| ysoserial | Wicket1 |  | <6.23.0 |  | DiskFileItem.readObject() |
| ... | ... | ... | ... | ... | ... |
| N/A | Apache OFBiz3 | CVE-2019-0189 | <16.11.01 | 9.8 CRITICAL | java.util.PriorityQueue#readObject//java.util.PriorityQueue#heapify//java.util.PriorityQueue#siftDown//java.util.PriorityQueue#siftDownUsingComparator//org.apache.commons.collections4.comparators.TransformingComparator#compare//org.apache.commons.collections4.functors.InvokerTransformer#transform//java.lang.reflect.Method#invoke |
| N/A | Spring |  | <5.2.3.RELEASE | <br /> | SerializableTypeWrapper$MethodInvokeTypeProvider#readObject//Method#invoke//$Proxy0#newTransformer//JdkDynamicAopProxy#invoke//AopUtils#invokeJoinpointUsingReflection//Method#invoke//TemplatesImpl#newTransformer// |

<a name="C2zC8"></a>
### Scalability
In our dataset, seven types of statistical information (`#Application`, `#Call Edges`, `#Alias Edges`, `time cost`, `#Classes`, `#Methods`, `#LoC`, ) are included.

- `#Application` represents the applications containing exploitable gadget chains.
- `#Call Edges` is the number of call edges in our DA-CPG.
- `#Alias Edges` is the number of alias edges in our DA-CPG.
- `time cost` represents the time our approach used for graph construction/static analysis. We use `second` as the basic unit of measurement.
- `#Classes` is the number of classes of each project.
- `#Methods`is the number of methods of each project.
- `#LoC` is the number of lines of code of each project.
<a name="buBt9"></a>
#### Fields
Complete statistical information of each approach can be found in `Data/ScalabilityData`.

| **Application** | **#Call Edges** | **#Alias Edges** | **time cost (seconds)** | **#Classes** | **#Methods** | **#LoC** |
| --- | --- | --- | --- | --- | --- | --- |
| CC4-4.0 | 6916 | 2770 | 6 | 630 | 7383 | 101020 |
| CC-3.1 | 9034 | 3556 | 8 | 798 | 9695 | 101949 |
| xstream-1.4.17 | 9756 | 3958 | 13 | 1083 | 13701 | 80930 |
| ... | ... | ... | ... | ... | ... | ... |
| xbean-spring-4.7 | 3769 | 1361 | 6 | 457 | 6518 | 62091 |
| rome-1.0 | 4354 | 1067 | 5 | 423 | 6867 | 94517 |

<a name="cu7Ib"></a>
## Toy Example

We take the motivaing example [CVE-2021-21346](CVE-2021-21346) in [XStream](https://x-stream.github.io/index.html) to explain how GCMiner constructs an exploit object to trigger this vulnerability. The simplified gadget chain is shown below.
```java
//javax.naming.ldap.Rdn$RdnEntry.class
private static class RdnEntry implements Comparable<RdnEntry> {
    private String type;
    private Object value;
    ...
    public int compareTo(RdnEntry that) {
        int diff = type.compareToIgnoreCase(that.type);
        if (diff != 0) {
            return diff;
        }
        if (value.equals(that.value)) {
            return 0;
        } 
        return getValueComparable().compareTo(
            that.getValueComparable());
    }...}
//com.sun.org.apache.xpath.internal.objects.XString.class
public class XString extends XObject implements XMLString {
    public boolean equals(XObject obj2) {
        int t = obj2.getType();
        try {
            if (XObject.CLASS_NODESET == t) 
                return obj2.equals(this);
            else if(XObject.CLASS_BOOLEAN == t)
                return obj2.bool() == bool();
            else if(XObject.CLASS_NUMBER == t)
                return obj2.num() == num(); }
        catch(javax.xml.transform.TransformerException te) { 
            throw new com.sun.org.apache.xml.internal.utils.WrappedRuntimeException(te);}
        return xstr().equals(obj2.xstr());}
```
Method `compareTo` is a _**magic method **_which will be self-executed during deserialization. It contains a `value` field which wil be used at `line 11` by `equals`. Under normal conditions, the object `RdnEntry` will invoke the method `equals` in `java.lang.Object.class`. However, although the attackers can not directly modify the source code by using dynamic binding to invoke the overriding method `equals` in `com.sun.org.apache.xpath.internal.objects.XString.class`, they can use reflection (**this is why we use reflection instead of dynamic binding here**) to dynamically invoke `equals` at `line 19` to continue the execution of this chain. By using reflection, they can modify the field `value` to an object of class `XString`. Other two dynamic reflection operations in this chain are similar.
<a name="VlqcT"></a>
### Generated Explot Objects
By using reflection, we can constrcut a highly-structured object for fuzzing. The structure of our generated object for this example is shown as follows:

``` 
├── Object RdnEntry                   
           └── field value: Object XString
                                   └── field value: Object MultiUIDefaults  
                                                           └── field value: Object SwingLazyValue  
```
