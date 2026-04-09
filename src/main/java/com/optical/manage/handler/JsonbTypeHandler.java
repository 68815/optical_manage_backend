package com.optical.manage.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(String.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class JsonbTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        pgObject.setValue(parameter);
        ps.setObject(i, pgObject);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object jsonb = rs.getObject(columnName);
        if (jsonb == null) {
            return null;
        }
        return jsonb.toString();
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object jsonb = rs.getObject(columnIndex);
        if (jsonb == null) {
            return null;
        }
        return jsonb.toString();
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object jsonb = cs.getObject(columnIndex);
        if (jsonb == null) {
            return null;
        }
        return jsonb.toString();
    }
}
