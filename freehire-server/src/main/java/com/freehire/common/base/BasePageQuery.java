package com.freehire.common.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页查询基类
 *
 * @author FreeHire
 * @since 1.0.0
 */
@Data
public class BasePageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方式（asc/desc）
     */
    private String orderType = "desc";

    /**
     * 转换为MyBatis-Plus的Page对象
     */
    public <T> Page<T> toPage() {
        return new Page<>(current, size);
    }

    /**
     * 获取偏移量
     */
    public int getOffset() {
        return (current - 1) * size;
    }
}

