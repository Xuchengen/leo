package com.github.xuchengen.leo.agent.core;

import com.github.xuchengen.leo.agent.core.handler.*;
import com.github.xuchengen.leo.dal.dao.SqlRecordDAO;
import com.github.xuchengen.leo.dal.dao.SwitchDAO;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.logger.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 应用上下文类<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/19 4:06 下午<br>
 */
public class ApplicationContext {

    /**
     * 容器
     */
    private final Map<String, Class<Handler>> container = new HashMap<>();

    /**
     * 映射到处理器
     */
    private final Map<String, Handler> mapping = new HashMap<>();

    /**
     * 项目ID
     */
    private final String projectId;

    /**
     * SQL记录表数据访问对象
     */
    private final SqlRecordDAO sqlRecordDAO;

    /**
     * 开关表数据访问对象
     */
    private final SwitchDAO switchDAO;

    static {

    }

    public ApplicationContext() throws Exception {
        String projectId = System.getProperty(Constant.AGENT_PARAM_PROJECT_ID);
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "OFF");
        System.setProperty(LoggerFactory.LOG_TYPE_SYSTEM_PROPERTY, "LOCAL");
        this.projectId = projectId;

        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Constant.SQLITE_JDBC_URL);
        this.sqlRecordDAO = new SqlRecordDAO(connectionSource);
        this.switchDAO = new SwitchDAO(connectionSource);

        Handler mySqlHandler = new MySQLHandler();
        Handler postgreSqlHandler = new PostgreSQLHandler();
        Handler oracleHandler = new OracleHandler();
        Handler sqlServerHandler = new SQLServerHandler();

        mapping.put(mySqlHandler.getTargetClassName(), mySqlHandler);
        mapping.put(mySqlHandler.getProductName(), mySqlHandler);

        mapping.put(postgreSqlHandler.getTargetClassName(), postgreSqlHandler);
        mapping.put(postgreSqlHandler.getProductName(), postgreSqlHandler);

        mapping.put(oracleHandler.getTargetClassName(), oracleHandler);
        mapping.put(oracleHandler.getProductName(), oracleHandler);

        mapping.put(sqlServerHandler.getTargetClassName(), sqlServerHandler);
        mapping.put(sqlServerHandler.getProductName(), sqlServerHandler);
    }

    /**
     * 是否包含
     *
     * @param targetClassOrName 目标类或者名称
     * @return 布尔
     */
    public boolean contains(String targetClassOrName) {
        return mapping.containsKey(targetClassOrName);
    }

    /**
     * 获取处理器
     *
     * @param targetClassOrName 目标类或者名称
     * @return 处理器
     */
    public Handler getHandler(String targetClassOrName) {
        return mapping.get(targetClassOrName);
    }

    /**
     * 获取SQL记录表数据访问类
     *
     * @return SqlRecordDAO
     */
    public SqlRecordDAO getSqlRecordDAO() {
        return sqlRecordDAO;
    }

    /**
     * 获取开关表数据访问对象
     *
     * @return SwitchDAO
     */
    public SwitchDAO getSwitchDAO() {
        return switchDAO;
    }

    /**
     * 获取项目ID
     *
     * @return String
     */
    public String getProjectId() {
        return projectId;
    }

}
