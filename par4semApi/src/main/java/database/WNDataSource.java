package database;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;

public class WNDataSource {

    private static WNDataSource datasource;
    private BasicDataSource ds;

    private WNDataSource(Properties prop) throws IOException, SQLException, PropertyVetoException {
        ds = new BasicDataSource();
        
        String url = "jdbc:mysql://" + prop.getProperty("dtServer") + ":" + prop.getProperty("dbMysqlPort") + "/";
        String dbName = prop.getProperty("semschDbname");
        String driver = "com.mysql.jdbc.Driver";
        String userName = prop.getProperty("dbuserName");
        String password = prop.getProperty("dbpassword");
        String charSet = "?useUnicode=true";
        
        ds.setDriverClassName(driver);
        ds.setUsername(userName);
        ds.setPassword(password);
        ds.setUrl(url + dbName + charSet);
       
     // the settings below are optional -- dbcp can work with defaults
        ds.setMinIdle(5);
        ds.setMaxIdle(20);
        ds.setMaxOpenPreparedStatements(180);

    }

    public static WNDataSource getInstance(Properties prop) throws IOException, SQLException, PropertyVetoException {
        if (datasource == null) {
            datasource = new WNDataSource(prop);
            return datasource;
        } else {
            return datasource;
        }
    }

    public Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }

}