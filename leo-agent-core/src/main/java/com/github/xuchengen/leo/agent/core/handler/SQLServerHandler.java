package com.github.xuchengen.leo.agent.core.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * SQLServer重建处理器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/16 2:13 下午<br>
 */
public class SQLServerHandler implements Handler {

    private static final String PRODUCT_NAME = "Microsoft SQL Server";

    private static final String TARGET_CLASS_NAME = "com/microsoft/sqlserver/jdbc/SQLServerStatement";

    private Class<?> DATETIME_OFFSET_CLASS = null;

    @Override
    public byte[] rebuild(byte[] classfileBuffer) {
        CtClass cc = null;
        try {
            ClassPool cp = ClassPool.getDefault();
            cp.importPackage(ShowSQLExecutor.class.getName());
            cc = cp.makeClass(new ByteArrayInputStream(classfileBuffer));
            CtMethod m = cc.getDeclaredMethod("executeStatement");
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
            if (Objects.isNull(DATETIME_OFFSET_CLASS)) {
                DATETIME_OFFSET_CLASS = Class.forName("microsoft.sql.DateTimeOffset");
            }

            ApplicationContext applicationContext = ApplicationContextManager.getInstance().getApplicationContext();
            SwitchDAO switchDAO = applicationContext.getSwitchDAO();
            if (!switchDAO.isOpen(applicationContext.getProjectId())) {
                return;
            }

            String originalSql = (String) BeanUtil.getFieldValue(object, "userSQL");
            Object[] params = (Object[]) BeanUtil.getFieldValue(object, "inOutParam");

            if (Objects.isNull(originalSql) || StrUtil.isBlank(originalSql)) {
                return;
            }

            if (Objects.isNull(params)) {
                params = new Object[0];
            }

            List<String> paramValues = new ArrayList<>();
            boolean support = true;

            for (Object param : params) {
                Object setterDtv = BeanUtil.getFieldValue(param, "setterDTV");
                Object impl = BeanUtil.getFieldValue(setterDtv, "impl");
                Object value = BeanUtil.getFieldValue(impl, "value");

                if (value instanceof Long || value instanceof Double || value instanceof Integer
                        || value instanceof Float || value instanceof Short || value instanceof BigDecimal) {
                    paramValues.add(String.valueOf(value));
                    continue;
                }

                if (value instanceof Boolean) {
                    paramValues.add((boolean) value ? "1" : "0");
                    continue;
                }

                if (value instanceof String) {
                    paramValues.add(StrUtil.builder("'", value.toString(), "'").toString());
                    continue;
                }

                if (value instanceof Date) {
                    paramValues.add(StrUtil.builder("'", DateUtil.formatDateTime((Date) value), "'").toString());
                    continue;
                }

                if (DATETIME_OFFSET_CLASS.isInstance(value)) {
                    paramValues.add(StrUtil.builder("'", value.toString(), "'").toString());
                    continue;
                }

                paramValues.add("?");
                support = false;
            }

            String replaceSql = originalSql;

            if (support) {
                String[] sqlArray = originalSql.split("\\?");
                StringBuilder buf = new StringBuilder();
                for (int i = 0; i < sqlArray.length; i++) {
                    buf.append(sqlArray[i]);
                    if (i < paramValues.size()) {
                        buf.append(paramValues.get(i));
                    }
                }
                replaceSql = buf.toString();
            }

            SqlRecordDO sqlRecordDO = new SqlRecordDO();
            sqlRecordDO.setDatabaseName(PRODUCT_NAME);
            sqlRecordDO.setProjectId(applicationContext.getProjectId());
            sqlRecordDO.setOriginalSql(originalSql);
            sqlRecordDO.setReplaceSql(replaceSql);
            sqlRecordDO.setIsSupport(support);
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
