package com.github.xuchengen.leo.dal.dao;

import com.github.xuchengen.leo.dal.entity.SwitchDO;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.jdbc.spring.DaoFactory;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 开关表数据访问类<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/29 2:24 下午<br>
 */
public class SwitchDAO {

    private final Dao<SwitchDO, Long> dao;

    private final ConnectionSource connectionSource;

    /**
     * 构造方法
     *
     * @param connectionSource 连接源对象
     * @throws SQLException SQL异常对象
     */
    public SwitchDAO(ConnectionSource connectionSource) throws SQLException {
        this.connectionSource = connectionSource;
        dao = DaoFactory.createDao(connectionSource, SwitchDO.class);
    }

    /**
     * 创建
     *
     * @param projectId 项目ID
     * @throws SQLException SQL异常
     */
    public void create(final String projectId) throws SQLException {
        final List<SwitchDO> list = dao.queryForEq("project_id", projectId);
        TransactionManager transactionManager = new TransactionManager(connectionSource);
        transactionManager.callInTransaction(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                for (SwitchDO switchDO : list) {
                    dao.deleteById(switchDO.getId());
                }
                SwitchDO switchDO = new SwitchDO();
                switchDO.setProjectId(projectId);
                switchDO.setIsOpen(false);
                switchDO.setCreateTime(new Date());
                dao.create(switchDO);
                return null;
            }
        });
    }

    /**
     * 根据项目ID删除
     *
     * @param projectId 项目ID
     * @throws SQLException
     */
    public void deleteByProjectId(String projectId) throws SQLException {
        DeleteBuilder<SwitchDO, Long> deleteBuilder = dao.deleteBuilder();
        deleteBuilder.where().eq("project_id", projectId);
        deleteBuilder.delete();
    }

    /**
     * 是否打开
     *
     * @param projectId 项目ID
     * @return boolean
     * @throws SQLException SQL异常
     */
    public boolean isOpen(String projectId) throws SQLException {
        List<SwitchDO> list = dao.queryForEq("project_id", projectId);
        if (null != list && list.size() > 0) {
            return list.get(0).getIsOpen();
        }
        return false;
    }

    /**
     * 打开
     *
     * @param projectId 项目ID
     * @throws SQLException SQL异常对象
     */
    public void open(final String projectId) throws SQLException {
        openOrClose(projectId, true);
    }

    /**
     * 关闭
     *
     * @param projectId 项目ID
     * @throws SQLException SQL异常对象
     */
    public void close(final String projectId) throws SQLException {
        openOrClose(projectId, false);
    }

    /**
     * 打开或者关闭
     *
     * @param projectId 项目ID
     * @param isOpen    是否打开
     * @throws SQLException SQL异常对象
     */
    private void openOrClose(final String projectId, final boolean isOpen) throws SQLException {
        final List<SwitchDO> list = dao.queryForEq("project_id", projectId);
        TransactionManager transactionManager = new TransactionManager(connectionSource);
        transactionManager.callInTransaction(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                for (SwitchDO switchDO : list) {
                    UpdateBuilder<SwitchDO, Long> builder = dao.updateBuilder();
                    builder.where().eq("id", switchDO.getId());
                    builder.updateColumnValue("is_open", isOpen);
                    builder.update();
                }
                return null;
            }
        });
    }
}
