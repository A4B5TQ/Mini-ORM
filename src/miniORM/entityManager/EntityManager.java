package miniORM.entityManager;

import miniORM.mapper.Mapper;
import miniORM.persistence.Column;
import miniORM.persistence.Entity;
import miniORM.persistence.Id;
import miniORM.typeDefinition.DateParser;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

public class EntityManager implements DbContext {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private Connection connection;
    private Set<Object> persistedEntities;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public EntityManager(Connection connection) {
        this.connection = connection;
        this.persistedEntities = new HashSet<>();
    }

    @Override
    public <E> boolean persist(E entity) throws IllegalAccessException, SQLException {

        Field primary = this.getId(entity.getClass());
        primary.setAccessible(true);
        Object value = primary.get(entity);

        this.doCreate(entity, primary);

        if (value == null || (Long) value <= 0) {
            return this.doInsert(entity, primary);
        }

        return this.doUpdate(entity, primary);
    }


    @Override
    @SuppressWarnings("unchecked")
    public <E> Set<E> find(Class<E> table) throws SQLException, IllegalAccessException, InstantiationException {

        this.statement = this.connection.createStatement();

        String query = "SELECT * FROM " + this.getTableName(table);

        this.resultSet = this.statement.executeQuery(query);

        if (this.persistedEntities.size() > 0) {
            this.persistedEntities.clear();
        }

        while (this.resultSet.next()) {
            E entity = table.newInstance();
            entity = this.fillEntity(table, this.resultSet, entity);
            this.persistedEntities.add(entity);
        }
        return Collections.unmodifiableSet(new HashSet<>(this.persistedEntities.stream()
                .map(e -> ((E) e)).collect(Collectors.toSet())));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Set<E> find(Class<E> table, String where) throws SQLException, IllegalAccessException, InstantiationException {
        this.statement = this.connection.createStatement();

        String query = "SELECT * FROM " + this.getTableName(table) + " WHERE 1=1 "
                + (where != null ? " AND " + where : "");

        ResultSet resultSet = this.statement.executeQuery(query);
        if (this.persistedEntities.size() > 0) {
            this.persistedEntities.clear();
        }
        while (resultSet.next()) {
            E entity = table.newInstance();
            entity = this.fillEntity(table, resultSet, entity);
            this.persistedEntities.add(entity);
        }

        return Collections.unmodifiableSet(new HashSet<>(this.persistedEntities.stream()
                .map(e -> ((E) e)).collect(Collectors.toSet())));
    }

    @Override
    public <E> E findFirst(Class<E> table) throws SQLException, IllegalAccessException, InstantiationException {

        this.statement = this.connection.createStatement();

        String query = "SELECT * FROM " + this.getTableName(table) + " LIMIT 1";

        ResultSet resultSet = this.statement.executeQuery(query);

        E entity = table.newInstance();

        resultSet.next();

        return this.fillEntity(table, resultSet, entity);
    }


    @Override
    public <E> E findFirst(Class<E> table, String where) throws SQLException, IllegalAccessException, InstantiationException {

        this.statement = this.connection.createStatement();

        String query = "SELECT * FROM " + this.getTableName(table) + " WHERE 1 "
                + (where != null ? " AND " + where : "") + " LIMIT 1";

        ResultSet resultSet = this.statement.executeQuery(query);

        E entity = table.newInstance();

        resultSet.next();

        return this.fillEntity(table, resultSet, entity);
    }


    @Override
    public <E> boolean delete(Class<E> table, Long id) throws SQLException, IllegalAccessException {
        String query = "DELETE FROM " + this.getTableName(table)
                + " WHERE " + Mapper.getFieldName(this.getId(table)) + "= ?";
        this.preparedStatement = this.connection.prepareStatement(query);
        this.preparedStatement.setLong(1, id);
        return this.preparedStatement.execute();
    }

    public void closeConnections() throws SQLException {

        if (this.resultSet != null) {
            this.resultSet.close();
        }

        if (this.statement != null) {
            this.statement.close();
        }

        if (this.preparedStatement != null) {
            this.preparedStatement.close();
        }
    }

    /**
     * @param entity class annotated with "@Entity" annotation
     * @return the value of the name type of Entity annotation
     * or if it’s not set returns the name of the entity
     */
    private <E> String getTableName(Class<E> entity) {

        if (entity.isAnnotationPresent(Entity.class)) {

            Entity pojo = entity.getAnnotation(Entity.class);

            return pojo.name().isEmpty() ? entity.getSimpleName() : pojo.name();
        }

        return entity.getSimpleName();
    }

    /**
     * @param clazz entity class
     * @return field with Id annotations of the given entity.
     * If there is no field with Id annotations throw exception.
     */
    private Field getId(Class clazz) throws IllegalAccessException {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field;
            }
        }
        throw new IllegalAccessException("@Id parameter is missing");
    }

    /**
     * @param field   - field from entity class
     * @param primary - field annotated with "@Id"
     * @return corresponding data types in MySQL
     */
    private String getDbType(Field field, Field primary) {

        field.setAccessible(true);

        if (field.getName().equals(primary.getName())) {
            return "BIGINT AUTO_INCREMENT PRIMARY KEY";
        }

        switch (field.getType().getSimpleName().toLowerCase()) {

            case "int":

                return "INT";

            case "string":
                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);
                    return "VARCHAR" + "(" + column.length() + ")";
                }

                return "VARCHAR(255)";

            case "date":
                return "DATE";
            case "boolean":
                return "BIT";
            case "double":
                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);
                    return "DOUBLE" + "(" + column.scale() + "," + column.precision() + ")";
                }
                return "DOUBLE";
        }
        return null;
    }

    /**
     * Create a table if it doesn't exist
     *
     * @param entity  object of type entity
     * @param primary - field annotated with "@Id" annotation
     * @return <code>true</code> if the first result is a <code>ResultSet</code>
     * object; <code>false</code> if it is an update count or there are
     * no results
     * @throws SQLException if a database access error occurs,
     *                      this method is called on a closed <code>Statement</code>,
     *                      the method is called on a
     *                      <code>PreparedStatement</code> or <code>CallableStatement</code>
     */
    private <E> boolean doCreate(E entity, Field primary) throws SQLException {

        String query = "CREATE TABLE IF NOT EXISTS " +
                this.getTableName(entity.getClass()) + " (";

        List<String> queryItems = new ArrayList<>();

        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String item = Mapper.getFieldName(field) + " " +
                    this.getDbType(field, primary);
            queryItems.add(item);
        }

        query = query + String.join(", ", queryItems) + ")";

        this.statement = this.connection.createStatement();
        return this.statement.execute(query);
    }

    /**
     * Insert new entity into database
     *
     * @param entity  object of type entity
     * @param primary - field annotated with "@Id" annotation
     * @return <code>true</code> if the first result is a <code>ResultSet</code>
     * object; <code>false</code> if it is an update count or there are
     * no results
     * @throws IllegalAccessException - Is thrown when an application tries
     *                                to reflectively create an instance (other than an array),
     *                                set or get a field, or invoke a method, but the currently
     *                                executing method does not have access to the definition of
     *                                the specified class, field, method or constructor.
     * @throws SQLException           - If a database access error occurs,
     *                                this method is called on a closed <code>Statement</code>,
     *                                the method is called on a
     *                                <code>PreparedStatement</code> or <code>CallableStatement</code>
     */
    private <E> boolean doInsert(E entity, Field primary) throws IllegalAccessException, SQLException {

        String query = "INSERT INTO "
                + this.getTableName(entity.getClass())
                + " " + "( ";

        List<String> columns = new ArrayList<>();

        List<String> values = new ArrayList<>();

        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(Id.class)) {
                columns.add(Mapper.getFieldName(field));
                if (!field.getType().isAssignableFrom(Date.class)) {
                    values.add("\'" + field.get(entity) + "\'");
                } else {
                    values.add("\'" + DateParser.parseDate((Date) field.get(entity), DATE_FORMAT) + "\'");
                }
            }
        }

        query = query + String.join(", ", columns) + ") " +
                "VALUES " + "(" + String.join(", ", values) + ")";

        return connection.prepareStatement(query).execute();
    }

    /**
     * Update existing entity in database
     *
     * @param entity  object of type entity
     * @param primary - field annotated with "@Id" annotation
     * @return <code>true</code> if the first result is a <code>ResultSet</code>
     * object; <code>false</code> if it is an update count or there are
     * no results
     * @throws IllegalAccessException - Is thrown when an application tries
     *                                to reflectively create an instance (other than an array),
     *                                set or get a field, or invoke a method, but the currently
     *                                executing method does not have access to the definition of
     *                                the specified class, field, method or constructor.
     * @throws SQLException           - If a database access error occurs,
     *                                this method is called on a closed <code>Statement</code>,
     *                                the method is called on a
     *                                <code>PreparedStatement</code> or <code>CallableStatement</code>
     */
    private <E> boolean doUpdate(E entity, Field primary) throws SQLException, IllegalAccessException {
        String query = "UPDATE " + this.getTableName(entity.getClass()) + " SET ";
        String where = " WHERE " + primary.getName() + "=?";

        List<String> rows = new ArrayList<>();

        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(Id.class)) {
                String row = Mapper.getFieldName(field) + "=";
                if (!field.getType().isAssignableFrom(Date.class)) {
                    rows.add(row + "\'" + field.get(entity) + "\'");
                } else {
                    rows.add(row + "\'" + DateParser.parseDate((Date) field.get(entity), DATE_FORMAT) + "\'");
                }
            }
        }

        query = query + String.join(", ", rows);
        Long id = (Long)primary.get(entity);

        this.preparedStatement = connection.prepareStatement(query + where);
        this.preparedStatement.setLong(1,id);
        return this.preparedStatement.execute();
    }


    /**
     * Map result set from database to Entity
     *
     * @param table     - table object class annotated with "@Entity"
     * @param resultSet - result from query
     * @param entity    - object instance from Entity class
     * @return - instanced object from entity class
     * @throws SQLException
     * @throws IllegalAccessException
     */
    private <E> E fillEntity(Class<E> table, ResultSet resultSet, E entity) throws SQLException, IllegalAccessException {

        return Mapper.map(entity, resultSet, table.getDeclaredFields());

    }
}
