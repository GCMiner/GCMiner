package com;

import java.util.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.junit.runner.RunWith;
import com.pholser.junit.quickcheck.*;
import com.pholser.junit.quickcheck.generator.*;
import edu.berkeley.cs.jqf.fuzz.*;


@RunWith(JQF.class)
public class ObjectTest {
    @Fuzz
    public void testGadgetChain(@From(ObjectGenerator.class) Object ExploitObject) throws Exception {
        assertTrue(ExploitObject + " should be a gadget chain", ObjectLogic.isGadgetChain(ExploitObject));

    }
}
