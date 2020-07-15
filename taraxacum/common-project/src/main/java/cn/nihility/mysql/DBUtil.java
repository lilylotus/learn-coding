package cn.nihility.mysql;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 数据库工具类
 *
 * @author dandelion
 * @date 2020-05-07 11:27
 */
public class DBUtil {

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        final Connection connection = DBUtil.getConnection();
        System.out.println(connection);
    }


    public static Connection getConnection() throws IOException, SQLException, ClassNotFoundException {
        final InputStream in = DBUtil.class.getResourceAsStream("/properties/mysql.properties");
        Properties properties = new Properties();
        properties.load(in);

        final String driver = properties.getProperty("driver");
        final String url = properties.getProperty("url");
        final String username = properties.getProperty("username");
        final String password = properties.getProperty("password");

        //System.out.println("driver: " + driver + " url : " + url + " user: " + username + " password: " + password);

        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public static void release(Connection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
