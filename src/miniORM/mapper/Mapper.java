package miniORM.mapper;

import miniORM.persistence.Column;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Mapper {

    private static String fieldName;

    public static <E> E map(E instance, ResultSet resultSet, Field... fields) throws SQLException, IllegalAccessException {

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
            }
        }
        return instance;
    }

    private static String getFieldName(Field field) {

        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            return column.name().isEmpty() ? field.getName() : column.name();
        }
        return field.getName();
    }
}
