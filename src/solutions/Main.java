package solutions;

import miniORM.connector.Connector;
import miniORM.entityManager.EntityManager;
import solutions.entities.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws ParseException {

        EntityManager em = null;
        Connection connection = null;

        try {

            // Initial setup and create database
            Connector.initConnection("mysql", "root", "1234", "localhost", "3306", "mini_orm");

            connection = Connector.getConnection();

            em = new EntityManager(connection);

            //Create Entity and insert value
            User user = new User("Ivan", "Ivanov", 27,new Date());

            em.persist(user);

            //Find first
            User newUser = em.findFirst(User.class);
            System.out.println(String.format("Id: %d, Usernmae: %s, Password: %s, Age: %d, Register date: %s",
                    newUser.getId(), newUser.getUsername(), newUser.getPassword(), newUser.getAge(), newUser.getRegistrationDate().toString()));

            //Find
            em.find(User.class).forEach(e -> System.out.println(String.format("Id: %d, Usernmae: %s, Password: %s, Age: %d, Register date: %s",
                    e.getId(), e.getUsername(), e.getPassword(), e.getAge(), e.getRegistrationDate().toString())));

            //Delete
            em.delete(User.class,2L);


        }catch (SQLException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } finally {
            try {
                if (em != null) {
                    em.closeConnections();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
