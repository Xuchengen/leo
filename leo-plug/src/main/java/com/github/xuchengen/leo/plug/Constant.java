package com.github.xuchengen.leo.plug;

import java.io.File;

/**
 * 常量<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/26 4:33 下午<br>
 */
public interface Constant {

    /**
     * 插件ID
     */
    String PLUGIN_ID = "com.github.xuchengen.leo.plug";

    /**
     * 用户目录
     */
    String USER_HOME = System.getProperty("user.home");

    /**
     * 目录名称
     */
    String DIR_NAME = ".leo";

    /**
     * DB文件名称
     */
    String DB_FILE_NAME = "sql_dump.db";

    /**
     * DB文件路径
     */
    String DB_FILE_PATH = USER_HOME + File.separator + DIR_NAME + File.separator + DB_FILE_NAME;

    /**
     * SQLite JDBC URL
     */
    String SQLITE_JDBC_URL = "jdbc:sqlite:" + DB_FILE_PATH;

    /**
     * 全平台通用换行符
     */
    String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * 分割线
     */
    String SPLIT_LINE = "--------------------------------------------------------------------------------";

    /**
     * agnet命令行前缀
     */
    String AGENT_PREFIX = "-javaagent:";

    /**
     * agent参数项目ID
     */
    String AGENT_PARAM_PROJECT_ID = "leo.projectId";

    /**
     * 核心jar名称
     */
    String AGENT_CORE_JAR_NAME = "leo-agent-core";
}
