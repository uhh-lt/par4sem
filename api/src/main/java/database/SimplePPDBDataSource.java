package database;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;

public class SimplePPDBDataSource {

    private static SimplePPDBDataSource datasource;
    private BasicDataSource ds;

    private SimplePPDBDataSource(Properties prop) throws IOException, SQLException, PropertyVetoException {
        ds = new BasicDataSource();
        
        String url = "jdbc:mysql://" + prop.getProperty("simpleppdbserver") + ":" + prop.getProperty("simplPPDBMysqlPort") + "/";
        String dbName = prop.getProperty("simpleppdbdb");
        String driver = "com.mysql.jdbc.Driver";
        String userName = prop.getProperty("simpleppdbuser");
        String password = prop.getProperty("simpleppdbpassword");
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

    public static SimplePPDBDataSource getInstance(Properties prop) throws IOException, SQLException, PropertyVetoException {
        if (datasource == null) {
            datasource = new SimplePPDBDataSource(prop);
            return datasource;
        } else {
            return datasource;
        }
    }

    public Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }

}