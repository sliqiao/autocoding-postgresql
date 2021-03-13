package com.autocoding.threadpool;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Objects;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 任务执行统计信息
 *
 * @ClassName: RunnableStatInfo
 * @author: QiaoLi
 * @date: Oct 13, 2020 5:22:21 PM
 */
@Slf4j
@Data
public class RunnableStatInfo {
    /**
     * 任务Id
     */
    private String id;
    /**
     * 原runnableHashCode
     */
    private int oriRunnableHashCode;
    /**
     * 任务描述
     */
    private String desc;
    /**
     * 任务提交时间
     */
    private Date submittedTime;
    /**
     * 任务开始执行时间
     */
    private Date startTime;
    /**
     * 任务结束执行时间
     */
    private Date endTime;



    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        if (!super.equals(object)) {
            return false;
        }
        RunnableStatInfo that = (RunnableStatInfo) object;
        return java.util.Objects.equals(id, that.id);


    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
