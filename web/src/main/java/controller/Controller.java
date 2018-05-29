package controller;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.springframework.web.client.RestTemplate;

import cwi.AddResult2Db2;
import cwi.AnswerShared;
import cwi.CWIFeaturize;
import database.Par4SemResource;
import ml.Classification;
import mturk.util.GetParaphrase;
import undoredo.ChangeLabelManager;
import undoredo.ChangeManager;
import utils.Utils;

@WebServlet(value = "/checkDocument", name = "checkDocument")
public class Controller extends HttpServlet {
    private static final long serialVersionUID = -2908926119230344244L;
    // static Par4SemResource con = new Par4SemResource();
   public static GetParaphrase conf;
    static Properties prop;
  //  public static final String HIT_MATCHES = "hit-matches";
    public static final String UNDO ="undo";
    public static final String SELECTION= "selection";
    public static final String WORKERID = "workerId";
    public static final String HITID = "hitId";
    public static final String ASSIGNMENTID ="assignmentId";
    public static final String HTML = "html";
    public static final String START = "start";
    public static final String ORIG_TEXT = "orig_text";
    public static final int TRESHOLD = 58;
    
   // public static final String HITLABLES ="hit-lables";
   
    static String prevDoc = "";
    JSONArray PrevMatches = null;
   static Map<String, ChangeManager> changeManagers;
   static Map<String, ChangeLabelManager> changeLablesManagers;
//   public static Word2Vec p2v;
   public static RestTemplate restTemplate = new RestTemplate();
   public static Classification cl = new Classification();
 // public static CWIFeaturize cwif = new CWIFeaturize();
    // StanfordLemmatizer slem;

    @Override
    public void init() throws ServletException {
        try {

            ClassLoader classLoader = Controller.class.getClassLoader();
            InputStream in = classLoader.getResourceAsStream("/config/semsch.properties");
       //     InputStream in = classLoader.getResourceAsStream("semsch.properties"); // when updating candidates from UpdateCandidate
            Properties prop = Utils.getConfigs(in);
            System.out.println("DB Initialization ...");
            initResources(prop);
            
            

            System.out.println("DB Initialization ... Done");
        } catch (Exception e) {
            System.out.println("Failed to Initialize "+ e.getMessage());
        }

    }
    

    public static void reInit() throws Exception {
       Controller cr = new Controller();
       try {
       Par4SemResource.ppdb2Conn.close();
       Par4SemResource.dTConn.close();
       Par4SemResource.par4SimConn.close();
       Par4SemResource.cwiConn.close();
       Par4SemResource.simpleppdbConn.close();
       Par4SemResource.wordNetConn.close();
      
      // Par4SemResource.sql2oPar4Sim.getDataSource().getConnection().close();
       }
       catch (Exception e){
           System.out.println("closing" + e.getMessage());
       }
       dbInit(prop);
     //  cr.init();

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            doPost(request, response);
            }
            catch (EOFException e){
                init();
            }
    }

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
    	 HttpSession session = request.getSession();
		String body = request.getReader().readLine();
		String textbody = (String) session.getAttribute("text");
		String hitId = request.getParameter("hitId");
		String texts = request.getParameter("text");
		System.out.println("Texts =" + texts);

		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		// System.out.println(syn.getXml(text));
		// System.out.println(result);
		// response.getWriter().write(text);
		JSONObject jOMatches = new JSONObject();

		JSONArray paraphrases = new JSONArray();

		if (PrevMatches != null && prevDoc.equals(textbody)) {
			paraphrases = PrevMatches;
		} else if(textbody !=null) {
		//	 textbody = URLDecoder.decode(body.split("=")[2].substring(6).split("&")[0], "UTF-8");
			
			try {
			List<AnswerShared> topCwis = AddResult2Db2.getTopNFromCwi((String)session.getAttribute("hitId"));
				paraphrases= conf.getSimplePPDBParaphraseCwi(textbody, topCwis, paraphrases);
				paraphrases= conf.getSingleParaphraseCwi(textbody, topCwis, paraphrases);
				PrevMatches = paraphrases;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/**
			try {
				setSpellErorrs(textbody, beginOffsets, paraphrases);
				System.out.println("Getting from SimplePPDB...");
				// NOT required
				// addFromSimplePPDB(textbody, beginOffsets, paraphrases);
				conf.getSimplePPDBParaphrase(textbody, beginOffsets, paraphrases);
				System.out.println("Getting from SimplePPDB... DONE");
				System.out.println("Getting MWE...");
				// NOT required
				// setMWEs(textbody, beginOffsets, paraphrases);
				conf.getMWEParaphrase(textbody, beginOffsets, paraphrases);
				System.out.println("Getting MWE...DONE");
				System.out.println("Getting SINGLE...");
				paraphrases = conf.getSingleParaphrase(textbody, beginOffsets, paraphrases);
				System.out.println("Getting SINGLE...DONE");
				prevDoc = textbody;
				PrevMatches = paraphrases;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("error" + e.getCause());
			}
			*/
		}
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		for (int i = 0; i < paraphrases.length(); i++) {
			jsonValues.add(paraphrases.getJSONObject(i));
		}

		Collections.sort(jsonValues, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject a, JSONObject b) {
				int valA = a.getInt("offset");
				int valB = b.getInt("offset");

				return ((Integer) valA).compareTo((Integer) valB);
				// if you want to change the sort order, simply use the
				// following:
				// return -valA.compareTo(valB);
			}
		});
		JSONArray sortedJsonArray = new JSONArray();
		for (int i = 0; i < paraphrases.length(); i++) {
			sortedJsonArray.put(jsonValues.get(i));
		}

		jOMatches.put("matches", sortedJsonArray);
		System.out.println(jOMatches.toString());
		response.getWriter().println(jOMatches.toString());
		// response.getWriter().println(result);
	}
    
	public static void initResources(Properties prop) throws Exception {
		System.out.println("Inside initResources ...");
		try {
			// Par4SemResource.ppdb1Init(prop);
		} catch (Exception e) {
			// TODO
		}
		try {
			// Par4SemResource.initLM(prop);
		} catch (Exception e) {
			// TODO
		}
		dbInit(prop);

		Par4SemResource.init(prop);
		conf = new GetParaphrase();
		changeManagers = new HashMap<String, ChangeManager>();
		changeLablesManagers = new HashMap<String, ChangeLabelManager>();
		try {
			System.out.println("Inside initResources phrase2Vec...");
			//p2v = WordVectorSerializer.readWord2VecModel(prop.getProperty("w2vModelPath"));
		//	p2v = WordVectorSerializer.readWord2VecModel(prop.getProperty("w2vModelPath"));
			System.out.println("Inside initResources phrase2Vec... Done");
		} catch (Exception e) {
			System.out.println("Inside initResources phrase2Vec... Failed");
		}
		
		cl.train(prop);
		CWIFeaturize.init(prop.getProperty("w2vModelPath"), null, prop.getProperty("wikiCountFile"));
	}


    private static void dbInit(Properties prop) throws Exception {
        try {
			System.out.println("Inside initResources ppdb2Init...");
			Par4SemResource.ppdb2Init(prop);
			System.out.println("Inside initResources ppdb2Init... DONE");
		} catch (Exception e) {
			throw new Exception("Error connecting PPDB2"+e.getMessage());
		}
		try {
			System.out.println("Inside initResources jobimtextInit...");
			Par4SemResource.jobimtextInit();
			System.out.println("Inside initResources jobimtextInit... DONE");
		} catch (Exception e) {
		    System.out.println("Error initResources jobimtextInit... Failed " + e.getMessage() );
			// TODO
		}
		try {
			System.out.println("Inside initResources wordNetInit...");
			Par4SemResource.wordNetInit(prop);
			System.out.println("Inside initResources wordNetInit... DONE");
		} catch (Exception e) {
		    System.out.println("Error initResources wordNetInit... Failed "+e.getMessage());
			// TODO
		}
		/*
		 * try { Par4SemResource.readSimplePPDBFile(prop); } catch (Exception e) { // TODO
		 * }
		 */
		try {
			System.out.println("Inside initResources initSimplePpdb...");
			Par4SemResource.initSimplePpdb(prop);
			System.out.println("Inside initResources initSimplePpdb... DONE");
		} catch (Exception e) {
			// TODO
		}

		try {
			System.out.println("Inside initResources initPar4SimDB...");
			Par4SemResource.initPar4SimDB(prop);
			System.out.println("Inside initResources initPar4SimDB... DONE");
		} catch (Exception e) {
		    System.out.println("Error initResources initPar4SimDB... Failed" + e.getMessage());
			// TODO
		}
		try {
			System.out.println("Inside initResources initCwiDB...");
			Par4SemResource.initCwiDB(prop);
			System.out.println("Inside initResources initCwiDB... DONE");
		} catch (Exception e) {
			// TODO
		}
    }

    static Properties getConfigs(String config) throws IOException {
        Properties prop = new Properties();
        InputStream input = null;

        input = new FileInputStream(config);

        prop.load(input);

        prop.getProperty("dbname");
        prop.getProperty("dbuser");
        prop.getProperty("dbaddress");
        prop.getProperty("dbpass");
        prop.getProperty("indexname");
        
        return prop;

    }
    
    public static String getNewLinedText(String html) {
        String text;
        text = html.replaceAll("<br>", "THISISNEWLINE");
        text = Jsoup.parse(text).text();
        text = text.replaceAll("THISISNEWLINE", "\n");
        return text;
    }
    
    public static String getHitId(HttpSession session){
        return (String)session.getAttribute("hitId");
    }
    
   public static String getWorkerId(HttpSession session){
        return (String)session.getAttribute("workerId");
    }
   
   public static String getAssignmentId(HttpSession session){
       return (String)session.getAttribute("assignmentId");
   }
   
   public static String getTarget(HttpSession session){
       return (String)session.getAttribute("word");
   }
}
