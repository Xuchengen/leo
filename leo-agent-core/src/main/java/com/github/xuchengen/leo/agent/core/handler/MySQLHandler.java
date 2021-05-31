package com.github.xuchengen.leo.agent.core.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.xuchengen.leo.agent.core.ApplicationContext;
import com.github.xuchengen.leo.agent.core.ApplicationContextManager;
import com.github.xuchengen.leo.agent.core.ShowSQLExecutor;
import com.github.xuchengen.leo.dal.dao.SwitchDAO;
import com.github.xuchengen.leo.dal.entity.SqlRecordDO;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Objects;

/**
 * MySQL重建类处理器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/13 3:31 下午<br>
 */
public class MySQLHandler implements Handler {

    private static final String PRODUCT_NAME = "MySQL";

    private static final String TARGET_CLASS_NAME = "com/mysql/jdbc/PreparedStatement";

    @Override
    public byte[] rebuild(byte[] classfileBuffer) {
        CtClass cc = null;
        try {
            ClassPool cp = ClassPool.getDefault();
            cp.importPackage(ShowSQLExecutor.class.getName());
            cc = cp.makeClass(new ByteArrayInputStream(classfileBuffer));
            CtMethod m = cc.getDeclaredMethod("executeInternal");
            m.insertBefore(StrUtil.format("{ShowSQLExecutor.execute(\"{}\",this);}", PRODUCT_NAME));
            return cc.toBytecode();
        } catch (Exception e) {
            return classfileBuffer;
        } finally {
            if (Objects.nonNull(cc)) {
                cc.detach();
            }
        }
    }

    @Override
    public void showSql(Object object) {
        try {
            ApplicationContext applicationContext = ApplicationContextManager.getInstance().getApplicationContext();
            SwitchDAO switchDAO = applicationContext.getSwitchDAO();
            if (!switchDAO.isOpen(applicationContext.getProjectId())) {
                return;
            }

            String originalSql = (String) BeanUtil.getFieldValue(object, "originalSql");

            if (StrUtil.isBlank(originalSql)) {
                return;
            }

            String replaceSql = ReflectUtil.invoke(object, "asSql");

            SqlRecordDO sqlRecordDO = new SqlRecordDO();
            sqlRecordDO.setDatabaseName(PRODUCT_NAME);
            sqlRecordDO.setProjectId(applicationContext.getProjectId());
            sqlRecordDO.setOriginalSql(originalSql);
            sqlRecordDO.setReplaceSql(replaceSql);
            sqlRecordDO.setIsSupport(true);
            sqlRecordDO.setFlag(false);
            sqlRecordDO.setThreadId(Thread.currentThread().getId());
            sqlRecordDO.setCreateTime(new Date());
            applicationContext.getSqlRecordDAO().insert(sqlRecordDO);
        } catch (Exception e) {
        }
    }

    @Override
    public String getTargetClassName() {
        return TARGET_CLASS_NAME;
    }

    @Override
    public String getProductName() {
        return PRODUCT_NAME;
    }
}