package example;

import java.lang.instrument.Instrumentation;

public class DemoAgent {

    public static void premain(String args, Instrumentation instr){
        instr.addTransformer(new ClassLoadingLogger());
    }
}


