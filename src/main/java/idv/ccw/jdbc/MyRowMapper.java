package idv.ccw.jdbc;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

public class MyRowMapper<T> implements RowMapper<T> {
    private final Log logger = LogFactory.getLog(getClass());

    private Class<T> mappedClass;

    private Map<String, PropertyDescriptor> mappedFields;

    private boolean primitivesDefaultedForNullValue = false;

    public void setMappedClass(Class<T> mappedClass) {
        if (this.mappedClass == null) {
            initialize(mappedClass);
        } else {
            if (!this.mappedClass.equals(mappedClass)) {
                throw new InvalidDataAccessApiUsageException("The mapped class can not be reassigned to map to "
                        + mappedClass + " since it is already providing mapping for " + this.mappedClass);
            }
        }
    }

    private void initialize(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
        this.mappedFields = new HashMap<String, PropertyDescriptor>();
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);

        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];

            if (pd.getWriteMethod() != null) {
                this.mappedFields.put(pd.getName().toLowerCase(), pd);
                String underscoredName = underscoreName(pd.getName());

                if (!pd.getName().toLowerCase().equals(underscoredName)) {
                    this.mappedFields.put(underscoredName, pd);
                }
            }
        }
    }

    private String underscoreName(String name) {
        StringBuilder result = new StringBuilder();

        if (name != null && name.length() > 0) {
            result.append(Character.toLowerCase(name.charAt(0)));

            for (int i = 1; i < name.length(); i++) {
                char c = name.charAt(i);

                if (Character.isLetter(c) && c == Character.toUpperCase(c)) {
                    result.append('_');
                    result.append(Character.toLowerCase(c));
                } else {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

    @Override
    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
        T mappedObject = createInstance(this.mappedClass);
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        for (int index = 1; index <= columnCount; index++) {
            String column = JdbcUtils.lookupColumnName(rsmd, index).toLowerCase();
            PropertyDescriptor pd = this.mappedFields.get(column);

            if (pd != null) {
                try {
                    Object value = JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());

                    if (logger.isTraceEnabled() && rowNumber == 0) {
                        logger.trace("Mapping column '" + column + "' to property '" + pd.getName() + "' of type "
                                + pd.getPropertyType());
                    }

                    try {
                        bw.setPropertyValue(pd.getName(), value);
                    } catch (TypeMismatchException e) {
                        if (value == null && primitivesDefaultedForNullValue) {
                            logger.debug("Intercepted TypeMismatchException for row " + rowNumber + " and column '"
                                    + column + "' with value " + value + " when setting property '" + pd.getName()
                                    + "' of type " + pd.getPropertyType() + " on object: " + mappedObject);
                        } else {
                            throw e;
                        }
                    }
                } catch (NotWritablePropertyException ex) {
                    throw new DataRetrievalFailureException(
                            "Unable to map column " + column + " to property " + pd.getName(), ex);
                }
            }
        }

        return mappedObject;
    }

    private T createInstance(Class<T> clazz) {
        if (clazz == null)
            throw new IllegalStateException("Mapped class was not specified");

        if (clazz.isInterface())
            throw new BeanInstantiationException(clazz, "Specified class is an interface");

        try {
            return clazz.newInstance();
        } catch (InstantiationException ex) {
            throw new BeanInstantiationException(clazz, "Is it an abstract class?", ex);
        } catch (IllegalAccessException ex) {
            throw new BeanInstantiationException(clazz, "Is the constructor accessible?", ex);
        }
    }

    public void setPrimitivesDefaultedForNullValue(boolean primitivesDefaultedForNullValue) {
        this.primitivesDefaultedForNullValue = primitivesDefaultedForNullValue;
    }

    public boolean isPrimitivesDefaultedForNullValue() {
        return primitivesDefaultedForNullValue;
    }

    public static <T> MyRowMapper<T> newInstance(Class<T> mappedClass) {
        MyRowMapper<T> newInstance = new MyRowMapper<T>();
        newInstance.setMappedClass(mappedClass);
        return newInstance;
    }
}