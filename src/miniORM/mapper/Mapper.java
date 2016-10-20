package miniORM.mapper;

import miniORM.persistence.Column;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public interface Mapper {

    /**
     * @param instance  - new instance from entity class
     * @param resultSet - result set from database
     * @param fields    - properties in entity class
     * @return - filled instance from entity class
     * @throws SQLException
     * @throws IllegalAccessException
     */
    static <E> E map(E instance, ResultSet resultSet, Field... fields) throws SQLException, IllegalAccessException {

        for (Field field : fields) {

            String fieldName = getFieldName(field);

            field.setAccessible(true);

            if (field.getType().isAssignableFrom(Integer.class) ||
                    field.getType().isAssignableFrom(Integer.TYPE)) {

                field.set(instance, resultSet.getInt(fieldName));

            } else if (field.getType().isAssignableFrom(Long.class) ||
                    field.getType().isAssignableFrom(Long.TYPE)) {

                field.set(instance, resultSet.getLong(fieldName));

            } else if (field.getType().isAssignableFrom(String.class)) {

                field.set(instance, resultSet.getString(fieldName));

            } else if (field.getType().isAssignableFrom(Date.class)) {

                field.set(instance, resultSet.getDate(fieldName));
            } else if (field.getType().isAssignableFrom(Double.TYPE) ||
                    field.getType().isAssignableFrom(Double.class)) {

                field.set(instance, resultSet.getDate(fieldName));
            }
        }
        return instance;
    }

    /**
     * @param field - field from entity class
     * @return the value of the name type of Column annotation
     * or if it’s not set returns the name of the field.
     */
    static String getFieldName(Field field) {

        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            return column.name().isEmpty() ? field.getName() : column.name();
        }
        return field.getName();
    }
}
