package com.github.xuchengen.leo.dal.dao;

import com.github.xuchengen.leo.dal.entity.SqlRecordDO;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.jdbc.spring.DaoFactory;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * SQL记录表访问类<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/29 1:01 下午<br>
 */
public class SqlRecordDAO {

    private final Dao<SqlRecordDO, Long> dao;

    private final ConnectionSource connectionSource;

    /**
     * 构造方法
     *
     * @param connectionSource 连接源对象
     * @throws SQLException SQL异常对象
     */
    public SqlRecordDAO(ConnectionSource connectionSource) throws SQLException {
        this.connectionSource = connectionSource;
        dao = DaoFactory.createDao(connectionSource, SqlRecordDO.class);
    }

    /**
     * 插入数据
     *
     * @param sqlRecordDO sql记录表实体对象
     * @throws SQLException SQL异常对象
     */
    public void insert(SqlRecordDO sqlRecordDO) throws SQLException {
        dao.create(sqlRecordDO);
    }

    /**
     * 按照项目编号查询
     *
     * @param projectId 项目编号
     * @return List
     * @throws SQLException SQL异常对象
     */
    public List<SqlRecordDO> queryByProjectId(String projectId) throws SQLException {
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("project_id", projectId);
        conditionMap.put("flag", false);
        final List<SqlRecordDO> list = dao.queryForFieldValues(conditionMap);

        TransactionManager transactionManager = new TransactionManager(connectionSource);
        transactionManager.callInTransaction(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                for (SqlRecordDO sqlRecordDO : list) {
                    dao.deleteById(sqlRecordDO.getId());
                }
                return null;
            }
        });
        return list;
    }

    /**
     * 删除创建时间小于等于传入时间参数的记录
     *
     * @param date 时间
     * @throws SQLException SQL异常对象
     */
    public void deleteForLessThanCreateTime(String projectId, Date date) throws SQLException {
        DeleteBuilder<SqlRecordDO, Long> builder = dao.deleteBuilder();
        builder.where().eq("project_id", projectId).and().le("create_time", date);
        builder.delete();
    }
}