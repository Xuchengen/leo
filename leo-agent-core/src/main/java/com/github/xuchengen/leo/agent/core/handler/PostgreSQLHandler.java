package com.github.xuchengen.leo.agent.core.handler;

import cn.hutool.core.bean.BeanUtil;
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
 * PostgreSQL重建类处理器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/15 1:49 下午<br>
 */
public class PostgreSQLHandler implements Handler {

    private static final String PRODUCT_NAME = "PostgreSQL";

    private static final String TARGET_CLASS_NAME = "org/postgresql/jdbc/PgStatement";

    @Override
    public byte[] rebuild(byte[] classfileBuffer) {
        CtClass cc = null;
        try {
            ClassPool cp = ClassPool.getDefault();
            cp.importPackage(ShowSQLExecutor.class.getName());
            cc = cp.makeClass(new ByteArrayInputStream(classfileBuffer));
            CtMethod m1 = cc.getDeclaredMethod("executeInternal");
            m1.insertBefore(StrUtil.format("{ShowSQLExecutor.execute(\"{}\",this);}", PRODUCT_NAME));
            CtMethod m2 = cc.getDeclaredMethod("internalExecuteBatch");
            m2.insertBefore(StrUtil.format("{ShowSQLExecutor.execute(\"{}\",this);}", PRODUCT_NAME));
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

            Object query = BeanUtil.getFieldValue(object, "preparedQuery");

            if (Objects.isNull(query)) {
                return;
            }

            Object sqlObj = BeanUtil.getFieldValue(query, "key");

            if (Objects.isNull(sqlObj)) {
                return;
            }

            String originalSql = sqlObj.toString();

            if (StrUtil.isBlank(originalSql)) {
                return;
            }

            String replaceSql = object.toString();

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