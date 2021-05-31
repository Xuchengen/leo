package com.github.xuchengen.leo.dal.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * 开关表实体类<br>
 * 作者：徐承恩<br>
 * 邮箱：xuchengen@gmail.com<br>
 * 日期：2021/4/28 3:00 下午<br>
 */
@DatabaseTable(tableName = "t_switch")
public class SwitchDO {

    @DatabaseField(id = true)
    private Long id;

    @DatabaseField(columnName = "project_id")
    private String projectId;

    @DatabaseField(columnName = "is_open", dataType = DataType.BOOLEAN_INTEGER)
    private Boolean isOpen;

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

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean open) {
        this.isOpen = open;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
