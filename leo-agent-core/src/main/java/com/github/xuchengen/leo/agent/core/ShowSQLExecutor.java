package com.github.xuchengen.leo.agent.core;

/**
 * 展示SQL执行器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/13 12:46 下午<br>
 */
public class ShowSQLExecutor {

    /**
     * 执行
     *
     * @param name   名称
     * @param object 参数
     */
    public static void execute(String name, Object object) {
        try {
            if (!ApplicationContextManager.isInitialized()) {
                ApplicationContextManager.init();
            }

            ApplicationContext applicationContext = ApplicationContextManager.getInstance().getApplicationContext();
            if (applicationContext.contains(name)) {
                applicationContext.getHandler(name).showSql(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
