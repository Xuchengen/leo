package com.github.xuchengen.leo.agent.core.handler;

/**
 * 重建类处理器接口<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/13 3:29 下午<br>
 */
public interface Handler {

    /**
     * 重构类
     *
     * @param classfileBuffer 类文件字节数组
     * @return 类文件字节数组
     */
    byte[] rebuild(byte[] classfileBuffer);

    /**
     * 展示SQL
     *
     * @param object 参数
     */
    void showSql(Object object);

    /**
     * 获取目标类名称
     *
     * @return 目标类名称
     */
    String getTargetClassName();

    /**
     * 获取产品名称
     *
     * @return 产品名称
     */
    String getProductName();
}
