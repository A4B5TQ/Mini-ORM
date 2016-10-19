package miniORM.entityManager;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Mapper {

    static void map(Field field, Object instance, ResultSet resultSet, String fieldName) throws SQLException, IllegalAccessException {

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
}
