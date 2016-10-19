package solutions;

import miniORM.connector.Connector;
import miniORM.entityManager.EntityManager;
import solutions.entities.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws IllegalAccessException, SQLException, ParseException, InstantiationException {


        // Type password
        Connector.initConnection("mysql", "root", "", "localhost", "3306", "mini_orm");

        Connection connection = Connector.getConnection();

        EntityManager em = new EntityManager(connection);

        //Date mock
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); // your template here
//        java.util.Date dateStr = formatter.parse("1988-21-12");
//        java.sql.Date dateDB = new java.sql.Date(dateStr.getTime());
//
//        User user = new User("Ivan", "Ivanov", 27, dateDB);
//
//        em.persist(user);

        User user1 = em.findFirst(User.class,null);
        String debug = "";
    }
}
