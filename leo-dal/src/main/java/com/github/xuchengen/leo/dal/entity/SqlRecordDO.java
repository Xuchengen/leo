package com.github.xuchengen.leo.dal.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * SQL记录表实体类<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/28 2:50 下午<br>
 */
@DatabaseTable(tableName = "t_sql_record")
public class SqlRecordDO {

    @DatabaseField(id = true)
    private Long id;

    @DatabaseField(columnName = "project_id")
    private String projectId;

    @DatabaseField(columnName = "database_name")
    private String databaseName;

    @DatabaseField(columnName = "original_sql")
    private String originalSql;

    @DatabaseField(columnName = "replace_sql")
    private String replaceSql;

    @DatabaseField(columnName = "thread_id")
    private Long threadId;

    @DatabaseField(columnName = "flag", dataType = DataType.BOOLEAN_INTEGER)
    private Boolean flag;

    @DatabaseField(columnName = "is_support", dataType = DataType.BOOLEAN_INTEGER)
    private Boolean isSupport;

    @DatabaseField(columnName = "create_time", dataType = DataType.DATE_LONG)
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getOriginalSql() {
        return originalSql;
    }

    public void setOriginalSql(String originalSql) {
        this.originalSql = originalSql;
    }

    public String getReplaceSql() {
        return replaceSql;
    }

    public void setReplaceSql(String replaceSql) {
        this.replaceSql = replaceSql;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public Boolean getIsSupport() {
        return isSupport;
    }

    public void setIsSupport(Boolean isSupport) {
        this.isSupport = isSupport;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
