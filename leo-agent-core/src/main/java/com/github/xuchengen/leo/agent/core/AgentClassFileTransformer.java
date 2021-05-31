package com.github.xuchengen.leo.agent.core;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * 代理类文件转换器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/5/8 10:19 上午<br>
 */
public class AgentClassFileTransformer implements ClassFileTransformer {

    public AgentClassFileTransformer() throws Exception {
        if (!ApplicationContextManager.isInitialized()) {
            ApplicationContextManager.init();
        }
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        ApplicationContext applicationContext = ApplicationContextManager.getInstance().getApplicationContext();
        if (applicationContext.contains(className)) {
            return applicationContext.getHandler(className).rebuild(classfileBuffer);
        }
        return classfileBuffer;
    }
}
