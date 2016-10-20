package miniORM.entityManager;

import java.sql.SQLException;
import java.util.Set;

public interface DbContext {

    <E> boolean persist(E entity) throws IllegalAccessException, SQLException;

    <E> Set<E> find(Class<E> table) throws SQLException, IllegalAccessException, InstantiationException;

    <E> Set<E> find(Class<E> table, String where) throws SQLException, IllegalAccessException, InstantiationException;

    <E> E findFirst(Class<E> table) throws SQLException, IllegalAccessException, InstantiationException;


    <E> E findFirst(Class<E> table, String where) throws SQLException, IllegalAccessException, InstantiationException;

    <E> boolean delete(Class<E> table, Long Id) throws SQLException, IllegalAccessException;
}
