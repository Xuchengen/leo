package com.github.xuchengen.leo.plug;

import com.github.xuchengen.leo.dal.dao.SqlRecordDAO;
import com.github.xuchengen.leo.dal.dao.SwitchDAO;
import com.j256.ormlite.jdbc.JdbcConnectionSource;

/**
 * 应用上下文类<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/19 4:06 下午<br>
 */
public class ApplicationContext {

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

    /**
     * 初始化
     *
     * @throws Exception 异常
     */
    public ApplicationContext(String projectId) throws Exception {
        this.projectId = projectId;

        JdbcConnectionSource connectionSource = new JdbcConnectionSource(Constant.SQLITE_JDBC_URL);
        this.sqlRecordDAO = new SqlRecordDAO(connectionSource);
        this.switchDAO = new SwitchDAO(connectionSource);
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
