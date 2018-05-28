package database;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;

public class PPDB2DataSource {

    private static PPDB2DataSource datasource;
    private BasicDataSource ds;

    private PPDB2DataSource(Properties prop) throws IOException, SQLException, PropertyVetoException {
        ds = new BasicDataSource();
        
        String url = "jdbc:mysql://" + prop.getProperty("ppdb2Server") + ":" + prop.getProperty("dbMysqlPort") + "/";
        String dbName = prop.getProperty("ppdb2dbName");
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

    public static PPDB2DataSource getInstance(Properties prop) throws IOException, SQLException, PropertyVetoException {
        if (datasource == null) {
            datasource = new PPDB2DataSource(prop);
            return datasource;
        } else {
            return datasource;
        }
    }

    public Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }

}