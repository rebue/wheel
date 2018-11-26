package rebue.wheel.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;

import rebue.wheel.baseintf.EnumBase;

/**
 * mybatis自动处理枚举类型的转换
 * 启用方法: 在spring boot使用
 * 1. 依赖mybatis-spring-boot-starter
 * 2. 配置文件中配置 mybatis.default-enum-type-handler=rebue.wheel.mybatis.AutoEnumTypeHandler
 */
public class AutoEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
    private BaseTypeHandler<E> typeHandler = null;

    public AutoEnumTypeHandler(final Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        if (EnumBase.class.isAssignableFrom(type)) {
            // 如果实现了BaseCodeEnum则使用我们自定义的转换器
            typeHandler = new EnumTypeHandler<>(type);
        } else {
            // 默认转换器也可换成EnumOrdinalTypeHandler
            typeHandler = new EnumTypeHandler<>(type);
        }
    }

    @Override
    public void setNonNullParameter(final PreparedStatement ps, final int i, final E parameter, final JdbcType jdbcType) throws SQLException {
        typeHandler.setNonNullParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public E getNullableResult(final ResultSet rs, final String columnName) throws SQLException {
        return typeHandler.getNullableResult(rs, columnName);
    }

    @Override
    public E getNullableResult(final ResultSet rs, final int columnIndex) throws SQLException {
        return typeHandler.getNullableResult(rs, columnIndex);
    }

    @Override
    public E getNullableResult(final CallableStatement cs, final int columnIndex) throws SQLException {
        return typeHandler.getNullableResult(cs, columnIndex);
    }
}