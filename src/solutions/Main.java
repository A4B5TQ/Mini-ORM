package solutions;

import miniORM.connector.Connector;
import miniORM.entityManager.EntityManager;
import solutions.entities.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Main {
    public static void main(String[] args) throws ParseException {


        // Initial setup and create database
        try {
            Connector.initConnection("mysql", "root", "1234", "localhost", "3306", "mini_orm");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Connection connection = Connector.getConnection();

        EntityManager em = new EntityManager(connection);


        //Create Entity and insert value
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); // your template here
        java.util.Date dateStr = formatter.parse("1988-21-12");
        java.sql.Date dateDB = new java.sql.Date(dateStr.getTime());

        User user = new User("Ivan", "Ivanov", 27, dateDB);

        try {
            em.persist(user);
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                em.closeConnections();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //Find first
        User user1 = null;
        try {

            user1 = em.findFirst(User.class);
            System.out.println(String.format("Id: %d, Usernmae: %s, Password: %s, Age: %d, Register date: %s",
                    user1.getId(), user1.getUsername(),user1.getPassword(),user1.getAge(), user1.getRegistrationDate().toString()));

        } catch (IllegalAccessException | InstantiationException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                em.closeConnections();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //Find
        try {

            em.find(User.class).forEach(e -> System.out.println(String.format("Id: %d, Usernmae: %s, Password: %s, Age: %d, Register date: %s",
                    e.getId(), e.getUsername(),e.getPassword(),e.getAge(), e.getRegistrationDate().toString())));

        } catch (IllegalAccessException | InstantiationException | SQLException e) {
            e.printStackTrace();

        } finally {
            try {
                em.closeConnections();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //Delete
//        try {
//            em.delete(User.class,2L);
//        } catch (IllegalAccessException | SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                em.closeConnections();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
