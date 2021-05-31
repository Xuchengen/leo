package com.github.xuchengen.leo.plug.service;

import cn.hutool.core.date.DateUtil;
import com.github.xuchengen.leo.dal.dao.SqlRecordDAO;
import com.github.xuchengen.leo.dal.dao.SwitchDAO;
import com.github.xuchengen.leo.dal.entity.SqlRecordDO;
import com.github.xuchengen.leo.plug.ApplicationContext;
import com.github.xuchengen.leo.plug.ApplicationContextManager;
import com.github.xuchengen.leo.plug.Constant;
import com.github.xuchengen.leo.plug.LeoBundle;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.sql.SQLException;
import java.util.List;

/**
 * Sql dump 项目级服务<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/30 3:35 下午<br>
 */
@Service
public final class PrintService {

    /**
     * 项目
     */
    private final Project project;

    /**
     * 控制台视图
     */
    private final ConsoleView consoleView;

    private final SqlRecordDAO sqlRecordDAO;

    private final SwitchDAO switchDAO;

    /**
     * 是否正在运行
     */
    private boolean run;

    /**
     * 构造方法
     *
     * @param project 项目对象
     */
    public PrintService(Project project) throws Exception {
        // 初始化上下文管理器
        if (!ApplicationContextManager.isInitialized()) {
            ApplicationContextManager.init(project.getLocationHash());
        }

        ApplicationContext applicationContext = ApplicationContextManager.getInstance().getApplicationContext();
        this.project = project;
        this.consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        this.sqlRecordDAO = applicationContext.getSqlRecordDAO();
        this.switchDAO = applicationContext.getSwitchDAO();
    }

    /**
     * 获取控制台视图
     *
     * @return ConsoleView
     */
    public ConsoleView getConsoleView() {
        return this.consoleView;
    }

    /**
     * 是否正在运行
     *
     * @return boolean
     */
    public boolean isRun() {
        return run;
    }

    /**
     * 启动
     */
    public synchronized void start() {
        if (run) {
            return;
        }

        try {
            switchDAO.open(project.getLocationHash());
            run = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            while (run) {
                try {
                    List<SqlRecordDO> sqlRecordDOS = sqlRecordDAO.queryByProjectId(project.getLocationHash());
                    for (SqlRecordDO sqlRecordDO : sqlRecordDOS) {

                        ConsoleViewContentType consoleViewContentType = ConsoleViewContentType.LOG_INFO_OUTPUT;

                        if (!sqlRecordDO.getIsSupport()) {
                            consoleViewContentType = ConsoleViewContentType.LOG_ERROR_OUTPUT;
                        }

                        consoleView.print(Constant.LINE_SEPARATOR + Constant.SPLIT_LINE,
                                ConsoleViewContentType.LOG_INFO_OUTPUT);

                        String stringBuilder =
                                Constant.LINE_SEPARATOR +
                                        LeoBundle.message("sql.databaseName") +
                                        sqlRecordDO.getDatabaseName() +
                                        Constant.LINE_SEPARATOR +
                                        LeoBundle.message("sql.threadId") +
                                        sqlRecordDO.getThreadId() +
                                        Constant.LINE_SEPARATOR +
                                        LeoBundle.message("sql.datetime") +
                                        DateUtil.formatDateTime(sqlRecordDO.getCreateTime()) +
                                        Constant.LINE_SEPARATOR +
                                        LeoBundle.message("sql.originalSql") +
                                        Constant.LINE_SEPARATOR +
                                        sqlRecordDO.getOriginalSql() +
                                        Constant.LINE_SEPARATOR +
                                        LeoBundle.message("sql.replaceSql") +
                                        Constant.LINE_SEPARATOR +
                                        sqlRecordDO.getReplaceSql();

                        consoleView.print(stringBuilder, consoleViewContentType);
                    }
                    Thread.sleep(3_000);
                } catch (Exception e) {
                }
            }
        });
    }

    /**
     * 停止
     */
    public synchronized void stop() {
        try {
            if (run) {
                run = false;
            }
            switchDAO.close(project.getLocationHash());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
