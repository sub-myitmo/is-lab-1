package ru.is1.config.utils;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class GenericEnumType<E extends Enum<E>> implements UserType<E> {

    private final Class<E> enumClass;

    public GenericEnumType(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public Class<E> returnedClass() {
        return enumClass;
    }

    @Override
    public boolean equals(E value1, E value2) throws HibernateException {
        return Objects.equals(value1, value2);
    }

    @Override
    public int hashCode(E value) throws HibernateException {
        return Objects.hashCode(value);
    }

    @Override
    public E nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
            throws SQLException {
        String value = rs.getString(position);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, E value, int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            st.setString(index, value.name());
        }
    }

    @Override
    public E deepCopy(E value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(E value) throws HibernateException {
        return value;
    }

    @Override
    public E assemble(Serializable cached, Object owner) throws HibernateException {
        return (E) cached;
    }

    @Override
    public E replace(E original, E target, Object owner) throws HibernateException {
        return original;
    }
}