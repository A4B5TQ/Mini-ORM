package miniORM.entityManager;

import java.sql.SQLException;

public interface DbContext {

    <E> boolean persist(E entity) throws IllegalAccessException, SQLException;

    <E> Iterable<E> find(Class<E> table);

    <E> Iterable<E> find(Class<E> table, String where);

    <E> E findFirst(Class<E> table);


    <E> E findFirst(Class<E> table, String where) throws SQLException, IllegalAccessException, InstantiationException;
}
