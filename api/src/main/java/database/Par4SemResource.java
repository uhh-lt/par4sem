package database;


import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.jobimtext.api.struct.DatabaseThesaurusDatastructure;
import org.jobimtext.api.struct.IThesaurusDatastructure;

import de.tudarmstadt.lt.lm.service.StringProviderMXBean;
import utils.OpenNLPSegmenter;



public class Par4SemResource {
    public static Connection simpleppdbConn;

    public static Connection ppdb2Conn;
    public static Connection dTConn;

    public static Connection wordNetConn;
  //  public static Statement wordNetSt;
    public static IThesaurusDatastructure<String, String> dt;
    public static List<PPDBLines> simplePPDBLists;
    
    public static StringProviderMXBean lmServer;
    public static WordNetSynonym wordNetSynonyms;

    
    public static Connection  cwiConn;
    public static Connection  par4SimConn;
   OpenNLPSegmenter openNLPSegmenter;
    
    public static void init(Properties prop) {
        wordNetSynonyms = new WordNetSynonym();
        OpenNLPSegmenter.initialize(prop);

    }
    public static void ppdb2Init(Properties prop)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException, PropertyVetoException {

     ppdb2Conn = PPDB2DataSource.getInstance(prop).getConnection();
        
    }

    public static void dt4SenseInit(Properties prop)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException, PropertyVetoException {      
        dTConn = DTDataSource.getInstance(prop).getConnection();
    }

    public static void wordNetInit(Properties prop)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException, PropertyVetoException {
        wordNetConn = WNDataSource.getInstance(prop).getConnection();
    }

    public static void initSimplePpdb(Properties prop)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException, PropertyVetoException {
        simpleppdbConn = SimplePPDBDataSource.getInstance(prop).getConnection();
    }
    
    public static void jobimtextInit() throws IOException {
        ClassLoader classLoader = Par4SemResource.class.getClassLoader();
        InputStream in = classLoader.getResourceAsStream("config/DT_wikipedia_trigram.xml");
        
        File tempFile = File.createTempFile("prefix-", "-suffix");
        OutputStream outputStream = new FileOutputStream(tempFile);
        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = in.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }
        outputStream.close();
        tempFile.deleteOnExit();
        dt = new DatabaseThesaurusDatastructure(tempFile);
        dt.connect();
    }
    
	public static void initCwiDB(Properties prop)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException, PropertyVetoException {
	    cwiConn = CWIDataSource.getInstance(prop).getConnection();
	}
	
	public static void initPar4SimDB(Properties prop)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException, PropertyVetoException {
	    par4SimConn = Par4SimDataSource.getInstance(prop).getConnection();
	    par4SimConn.setAutoCommit(false);
	}
		
    
}
