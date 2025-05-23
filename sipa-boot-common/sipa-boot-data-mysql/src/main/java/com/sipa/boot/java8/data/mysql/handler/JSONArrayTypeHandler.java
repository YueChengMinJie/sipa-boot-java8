package com.sipa.boot.java8.data.mysql.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.alibaba.fastjson.JSONArray;

/**
 * @author sunyukun
 * @date 2019/4/15
 */
public class JSONArrayTypeHandler extends BaseTypeHandler<JSONArray> {
    /**
     * 设置非空参数。
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONArray parameter, JdbcType jdbcType)
        throws SQLException {
        ps.setString(i, String.valueOf(parameter.toJSONString()));
    }

    /**
     * 根据列名，获取可以为空的结果。
     */
    @Override
    public JSONArray getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String sqlJson = rs.getString(columnName);
        if (null != sqlJson) {
            return JSONArray.parseArray(sqlJson);
        }
        return null;
    }

    /**
     * 根据列索引，获取可以为空的结果。
     */
    @Override
    public JSONArray getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String sqlJson = rs.getString(columnIndex);
        if (null != sqlJson) {
            return JSONArray.parseArray(sqlJson);
        }
        return null;
    }

    /**
     * 根据列索引，获取可以为空的结果。
     */
    @Override
    public JSONArray getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String sqlJson = cs.getString(columnIndex);
        if (null != sqlJson) {
            return JSONArray.parseArray(sqlJson);
        }
        return null;
    }
}
