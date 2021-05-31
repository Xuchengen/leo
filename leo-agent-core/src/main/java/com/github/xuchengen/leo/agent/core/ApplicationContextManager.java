package com.github.xuchengen.leo.agent.core;

/**
 * 应用上下文管理者<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/30 10:17 上午<br>
 */
public class ApplicationContextManager {

    private static ApplicationContextManager applicationContextManager;

    private ApplicationContext applicationContext;

    private ApplicationContextManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static ApplicationContextManager getInstance() {
        if (applicationContextManager == null) {
            throw new RuntimeException();
        }
        return applicationContextManager;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void init() throws Exception {
        if (applicationContextManager != null) {
            throw new RuntimeException();
        }
        applicationContextManager = new ApplicationContextManager(new ApplicationContext());
    }

    public static boolean isInitialized() {
        return applicationContextManager != null;
    }
}
