package it.flowzz.domainanalytics.storage.impl;

import it.flowzz.domainanalytics.DomainAnalytics;
import it.flowzz.domainanalytics.storage.Storage;

import java.sql.*;
import java.util.HashMap;

public class StorageMySql implements Storage {

    private DomainAnalytics plugin;
    private Connection connection;
    private String host;
    private String database;
    private String username;
    private String password;
    private int port;

    public StorageMySql(DomainAnalytics plugin) {
        this.plugin = plugin;
        host = plugin.getConfig().getString("host");
        port = plugin.getConfig().getInt("port");
        database = plugin.getConfig().getString("database");
        username = plugin.getConfig().getString("username");
        password = plugin.getConfig().getString("password");
        try {
            openConnection();
            createTable();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS `" + database + "`.`domainrecords` ( `DOMAIN` VARCHAR(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL , `JOINCOUNT` INT(64) NOT NULL, CONSTRAINT DOMAIN_PK PRIMARY KEY (DOMAIN))CHARSET=utf8 COLLATE utf8_general_ci;");
    }




    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
        }
    }

    @Override
    public void save(HashMap<String, Integer> cache) {
        for(String domain : cache.keySet()){
            try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO domainrecords(DOMAIN,JOINCOUNT) VALUE (?,?) ON DUPLICATE KEY UPDATE JOINCOUNT=VALUES(JOINCOUNT)");
            statement.setString(1, domain);
            statement.setInt(2, cache.getOrDefault(domain,0));
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void load(HashMap<String, Integer> cache) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM domainrecords", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                    plugin.getJoinCache().put(resultSet.getString("DOMAIN"), resultSet.getInt("JOINCOUNT"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
