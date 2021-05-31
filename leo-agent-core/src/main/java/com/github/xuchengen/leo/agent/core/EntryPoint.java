package com.github.xuchengen.leo.agent.core;

import java.lang.instrument.Instrumentation;

/**
 * 入口点<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/12 10:03 上午<br>
 */
public class EntryPoint {

    public static void premain(String agentOps, Instrumentation inst) throws Exception {
        inst.addTransformer(new AgentClassFileTransformer());
    }

}
