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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Oracle重建类处理器<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/13 4:55 下午<br>
 */
public class OracleHandler implements Handler {

    private static final String PRODUCT_NAME = "Oracle";

    private static final String TARGET_CLASS_NAME = "oracle/jdbc/driver/OraclePreparedStatement";

    private static Class<?> NUM_CLASS = null;

    private static Class<?> STRING_CLASS = null;

    private static Class<?> DATE_CLASS = null;

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
            if (Objects.isNull(NUM_CLASS) || Objects.isNull(STRING_CLASS) || Objects.isNull(DATE_CLASS)) {
                NUM_CLASS = Class.forName("oracle.jdbc.driver.VarnumBinder");
                STRING_CLASS = Class.forName("oracle.jdbc.driver.VarcharBinder");
                DATE_CLASS = Class.forName("oracle.jdbc.driver.DateCommonBinder");
            }

            ApplicationContext applicationContext = ApplicationContextManager.getInstance().getApplicationContext();
            SwitchDAO switchDAO = applicationContext.getSwitchDAO();
            if (!switchDAO.isOpen(applicationContext.getProjectId())) {
                return;
            }

            Object sqlObject = BeanUtil.getFieldValue(object, "sqlObject");
            Object[] currentRowBinders = (Object[]) BeanUtil.getFieldValue(object, "currentRowBinders");

            if (Objects.isNull(sqlObject)) {
                return;
            }

            String originalSql = sqlObject.toString();

            if (StrUtil.isBlank(originalSql)) {
                return;
            }

            if (Objects.isNull(currentRowBinders)) {
                currentRowBinders = new Object[0];
            }

            List<String> paramValues = new ArrayList<>();
            boolean support = true;

            for (Object currentRowBinder : currentRowBinders) {
                Object paramVal = BeanUtil.getFieldValue(currentRowBinder, "paramVal");

                if (NUM_CLASS.isInstance(currentRowBinder)) {
                    paramValues.add(String.valueOf(paramVal));
                    continue;
                }

                if (STRING_CLASS.isInstance(currentRowBinder)) {
                    paramValues.add(StrUtil.builder("'", String.valueOf(paramVal), "'").toString());
                    continue;
                }

                if (DATE_CLASS.isInstance(currentRowBinder)) {
                    String template = "TO_DATE('{}','yyyy-mm-dd HH24:mi:ss')";
                    String dateStr = DateUtil.formatDateTime((Date) paramVal);
                    paramValues.add(StrUtil.format(template, dateStr));
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