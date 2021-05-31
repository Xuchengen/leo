package com.github.xuchengen.leo.plug.listener;

import cn.hutool.core.io.FileUtil;
import com.github.xuchengen.leo.dal.entity.SqlRecordDO;
import com.github.xuchengen.leo.plug.ApplicationContext;
import com.github.xuchengen.leo.plug.ApplicationContextManager;
import com.github.xuchengen.leo.plug.Constant;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;

/**
 * 项目级监听器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/28 1:12 下午<br>
 */
public class SqlDumpProjectListener implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        try {
            // 项目打开时将DB文件写入用户目录
            if (!FileUtil.exist(Constant.DB_FILE_PATH)) {
                FileUtil.mkParentDirs(Constant.DB_FILE_PATH);
                InputStream inputStream = SqlRecordDO.class.getClassLoader().getResourceAsStream(Constant.DB_FILE_NAME);
                FileUtil.writeFromStream(inputStream, Constant.DB_FILE_PATH);
            }

            // 初始化上下文管理器
            if (!ApplicationContextManager.isInitialized()) {
                ApplicationContextManager.init(project.getLocationHash());
            }

            ApplicationContext applicationContext = ApplicationContextManager.getInstance().getApplicationContext();

            // 创建开关记录
            applicationContext.getSwitchDAO().create(project.getLocationHash());

            // 删除SQL记录
            applicationContext.getSqlRecordDAO().deleteForLessThanCreateTime(project.getLocationHash(), new Date());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void projectClosed(@NotNull Project project) {
        try {
            ApplicationContext applicationContext = ApplicationContextManager.getInstance().getApplicationContext();

            // 删除开关记录
            applicationContext.getSwitchDAO().deleteByProjectId(project.getLocationHash());

            // 删除SQL记录
            applicationContext.getSqlRecordDAO().deleteForLessThanCreateTime(project.getLocationHash(), new Date());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
