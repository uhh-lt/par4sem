package cwi.hit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.IOUtils;

public class GenerateTSVMFromTaskShared {
	private static Connection conn;
	private static Statement st;
	
	public static void main(String[] args)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		
//		initDB("ComplexHITFrench", "localhost", "root", "root");
		initDB("ComplexHITGermanText", "localhost", "root", "root");
		 count();
		
	}

	private static void count() throws FileNotFoundException, SQLException, IOException {


		File outputTrain = new File("/Users/seidmuhieyimam/Dropbox/PhD/PhD/CWI_Shared_TAsk/dataset/german/HitCount.tsv");
		FileOutputStream outStreamTrain = new FileOutputStream(outputTrain);
			
		ResultSet hitIds = st.executeQuery("select distinct hitId, question from HIT ;");
		while (hitIds.next()){
			String hitId = hitIds.getString("hitId");
			
			Set<String> blacklists = new HashSet();
			blacklists.add("A3TKUXUTDX6FBF");
			blacklists.add("AD3ZSVTCXP0Y5");
			String queryNo = "select count(distinct workerId) no from answers where hitId ='"+hitId+"' and language ='no'AND workerId NOT IN ('"+org.apache.commons.lang.StringUtils.join(blacklists,"','")+"') group by hitId;";
			ResultSet answersNo = conn.createStatement().executeQuery(queryNo);
			
			int no = 0;
			while(answersNo.next()){
				no = answersNo.getInt("no");
			}
			
			String yesquery = "select count(distinct workerId) yes from answers where hitId ='"+hitId+"' and language ='yes' AND workerId NOT IN ('"+org.apache.commons.lang.StringUtils.join(blacklists,"','")+"') group by hitId;";
			ResultSet answersYes = conn.createStatement().executeQuery(yesquery);
			
			int yes = 0;
			while(answersYes.next()){
				yes = answersYes.getInt("yes");
			}	
			IOUtils.write(hitId +"\t"+yes+"\t"+no+"\n", outStreamTrain);				
		}
		
		outStreamTrain.close();
	}
	
	public static void initDB(String dbName, String ip, String user, String pswd)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {

		String url = "jdbc:mysql://" + ip + "/";
		String driver = "org.gjt.mm.mysql.Driver";
		String userName = user;
		String password = pswd;
		Class.forName(driver).newInstance();
		conn = DriverManager.getConnection(url + dbName, userName, password);
		conn.setAutoCommit(false);
		st = conn.createStatement();
	}


}
