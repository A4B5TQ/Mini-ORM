package miniORM.entityManager;

import java.sql.SQLException;
import java.util.Set;

public interface DbContext {

    /**
     * Insert or update entity depending if it is attached to the context
     *
     * @param entity object from class annotated with "@Entity"
     * @return True if operation is successfully or false if it`s not
     * @throws IllegalAccessException - is thrown when an application tries
     *                                to reflectively create an instance (other than an array),
     *                                set or get a field, or invoke a method, but the currently
     *                                executing method does not have access to the definition of
     *                                the specified class, field, method or constructor.
     */
    <E> boolean persist(E entity) throws IllegalAccessException, SQLException;

    /**
     * @param table object from class annotated with "@Entity"
     * @return collection of all entity objects of type <E>
     * or null if the entity does not exist
     */
    <E> Set<E> find(Class<E> table) throws SQLException, IllegalAccessException, InstantiationException;

    /**
     * @param where user specific clause(s) for search criteria
     * @param table object from class annotated with "@Entity"
     * @return collection of all entity objects of type E
     * matching the criteria given in “where”
     * or null if the entity does not exist
     */
    <E> Set<E> find(Class<E> table, String where) throws SQLException, IllegalAccessException, InstantiationException;

    /**
     * @param table object from class annotated with "@Entity"
     * @return the first entity object of type E
     * or null if the entity does not exist
     */
    <E> E findFirst(Class<E> table) throws SQLException, IllegalAccessException, InstantiationException;

    /**
     * @param table object class annotated with "@Entity"
     * @param where user specific clause(s) for search criteria
     * @return the first entity object of type E matching the criteria given in “where”
     * or null if the entity does not exist
     */
    <E> E findFirst(Class<E> table, String where) throws SQLException, IllegalAccessException, InstantiationException;

    /**
     * Delete entity object from database by given id;
     *
     * @param table - object class annotated with "@Entity"
     * @param id    - Unique primary key
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     */
    <E> boolean delete(Class<E> table, Long id) throws SQLException, IllegalAccessException;

    /**
     * Close all open connections
     *
     * @throws SQLException
     */
    void closeConnections() throws SQLException;
}
