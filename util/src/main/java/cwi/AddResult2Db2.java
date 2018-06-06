package cwi;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringEscapeUtils;
import database.Par4SemResource;

public class AddResult2Db2 {

    private static Connection conn;
    private static Statement st;

    private static Connection connPar4Sim;

    public static void main(String[] args) throws InstantiationException, IllegalAccessException,
                    ClassNotFoundException, IOException, SQLException {
        // addAricles2DB();
        // addAlltask1ToDb();
        // deleteHits("ComplexHITrenews");
        // String fileName = "src/main/resources/simplification/renews/oldhits/Sat Jun
        // 03 00:46:51 CEST 2017.suscces";
        // String fileName="src/main/resources/simplification/rewikip/oldhits/Wed Jun 14
        // 20:21:02 CEST 2017.suscces";
        // String fileName="src/main/resources/simplification/rewnews/oldhits/Wed Jun 14
        // 20:08:08 CEST 2017.suscces";
        // mergeHits("ComplexHITwnewsAll", fileName);
        // updateEducLevel("ComplexHITNewsAll");

        List<AnswerShared> ans = new ArrayList<>();
        ans.add(new AnswerShared("seid", 10, 15));
        ans.add(new AnswerShared("abebe", 20, 30));
        ans.add(new AnswerShared("Almaz", 6, 9));

        ans.forEach(an -> {
            System.out.println(an.getAnswer());
        });
        ans.sort((a, b) -> a.compareTo(b));
        ans.forEach(an -> {
            System.out.println(an.getAnswer());
        });

    }

    public static void addAricles2DB() throws InstantiationException, IllegalAccessException,
                    ClassNotFoundException, SQLException, IOException {
        initDB("enwiki", "localhost", "root", "1qazxsw2");
        String articleQuery = "INSERT INTO articles (articleId, article) VALUES (?,?)";
        PreparedStatement psArticle = conn.prepareStatement(articleQuery);
        for (File file : new File("src/main/resources/simplification/wikinews/").listFiles()) {
            if (!file.getName().endsWith(".txt")) {
                continue;
            }
            String article = FileUtils.readFileToString(file);
            int id = Integer.valueOf(FilenameUtils.getBaseName(file.getName()));
            psArticle.setInt(1, id);
            psArticle.setString(2, article);
            psArticle.addBatch();
        }
        psArticle.executeBatch();
        conn.commit();
    }

    public static void addHitPar4Sim(String hitId, String text, String cwiHitId, int iteration)
                    throws InstantiationException, IllegalAccessException, ClassNotFoundException,
                    SQLException, IOException {

        String articleQuery = "INSERT INTO hit (hitId, text,cwiHitId,iteration) VALUES (?,?,?,?)";
        PreparedStatement psArticle = Par4SemResource.par4SimConn.prepareStatement(articleQuery);
        psArticle.setString(1, hitId);
        psArticle.setString(2, text);
        psArticle.setString(3, cwiHitId);
        psArticle.setInt(4, iteration);
        psArticle.addBatch();
        psArticle.executeBatch();
        Par4SemResource.par4SimConn.commit();
    }

    public static void addHitCandidatePar4Sim(String hitId, String target, String candidate, int order)
                    throws InstantiationException, IllegalAccessException, ClassNotFoundException,
                    SQLException, IOException {

        String articleQuery = "INSERT INTO candidate (hitId, target,candidate, candidate_rank) VALUES (?,?,?,?)";
        PreparedStatement psArticle = Par4SemResource.par4SimConn.prepareStatement(articleQuery);
        psArticle.setString(1, hitId);
        psArticle.setString(2, target);
        psArticle.setString(3, candidate);
        psArticle.setInt(4, order);
        psArticle.addBatch();
        psArticle.executeBatch();
        Par4SemResource.par4SimConn.commit();
    }
    
    public static void addHitCandidateSuggestionPar4Sim(String hitId, String target, String candidate, double order)
                    throws InstantiationException, IllegalAccessException, ClassNotFoundException,
                    SQLException, IOException {

        String articleQuery = "INSERT INTO candidatesuggestion (cwiHitID, target,candidate, candidate_rank) VALUES (?,?,?,?)";
        PreparedStatement psArticle = Par4SemResource.par4SimConn.prepareStatement(articleQuery);
        psArticle.setString(1, hitId);
        psArticle.setString(2, target);
        psArticle.setString(3, candidate);
        psArticle.setDouble(4, order);
        psArticle.addBatch();
        psArticle.executeBatch();
        Par4SemResource.par4SimConn.commit();
    }
    
    public static void DeleteHitCandidateSuggestionPar4Sim(String cwiHitID)
                    throws InstantiationException, IllegalAccessException, ClassNotFoundException,
                    SQLException, IOException {

        String articleQuery = "DELETE FROM candidatesuggestion WHERE cwiHitID = ?";
        PreparedStatement psArticle = Par4SemResource.par4SimConn.prepareStatement(articleQuery);
        psArticle.setString(1, cwiHitID);
        psArticle.execute();
        Par4SemResource.par4SimConn.commit();
    }
    
    public static void addHitCandidateResponsePar4Sim(String hitId, String target, String candidate, int order)
                    throws InstantiationException, IllegalAccessException, ClassNotFoundException,
                    SQLException, IOException {

        String articleQuery = "INSERT INTO candidateresponse (hitId, target,candidate, candidate_rank) VALUES (?,?,?,?)";
        PreparedStatement psArticle = Par4SemResource.par4SimConn.prepareStatement(articleQuery);
        psArticle.setString(1, hitId);
        psArticle.setString(2, target);
        psArticle.setString(3, candidate);
        psArticle.setInt(4, order);
        psArticle.addBatch();
        psArticle.executeBatch();
        Par4SemResource.par4SimConn.commit();
    }

    
    public static void addResponse(String hitId, String workerId, String assignmentId, String text, String html, Timestamp timestamp)
                    throws InstantiationException, IllegalAccessException, ClassNotFoundException,
                    SQLException, IOException {
        
        String articleQuery = "INSERT INTO response(hitId, workerId, assignmentId, text, html, timestamp) VALUES (?,?,?,?,?,?)";
        PreparedStatement psArticle = Par4SemResource.par4SimConn.prepareStatement(articleQuery);
        psArticle.setString(1, hitId);
        psArticle.setString(2, workerId);
        psArticle.setString(3, assignmentId);
        psArticle.setString(4, text);
        psArticle.setString(5, html);
        psArticle.setTimestamp(6, timestamp);
        psArticle.addBatch();
        psArticle.executeBatch();
        Par4SemResource.par4SimConn.commit();
    }
    
    public static void logLocations(String workerId, String ip, String country, String code) {

        try {
        String insertlocation = "insert into location(workerId, ip, country, code) "
                + "values (?,?,?,?)";
        PreparedStatement psArticle = Par4SemResource.par4SimConn.prepareStatement(insertlocation);
        psArticle.setString(1, workerId);
        psArticle.setString(2, ip);
        psArticle.setString(3, country);
        psArticle.setString(4, code);
        psArticle.addBatch();
        psArticle.executeBatch();
        Par4SemResource.par4SimConn.commit();
        } catch (Exception e) {
            System.out.println("duplicate");
        }

    }
    
    public static void addw2vec(String word, String candidates) {

        try {
        String insertSql = "insert into w2vec(word, candidates) "
                        + "values (?, ?)";
        PreparedStatement psArticle = Par4SemResource.par4SimConn.prepareStatement(insertSql);
        psArticle.setString(1, word);
        psArticle.setString(2, candidates);

        psArticle.addBatch();
        psArticle.executeBatch();
        Par4SemResource.par4SimConn.commit();
        } catch (Exception e) {
            System.out.println("duplicate");
        }

    }

    public static boolean existsWorkerLocation(String workerId, String ip) throws SQLException {
        String query = "SELECT * " + "FROM location WHERE workerId = '"+workerId+"' AND ip = '"+ip+"'";
        ResultSet hits = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery(query);
        if(hits.first()) {
            return true;
        }
        else return false;
    }
    
    public static void aupdateArticles(String articleId, String hitId)
                    throws InstantiationException, IllegalAccessException, ClassNotFoundException,
                    SQLException, IOException {
        initDB("enwiki", "localhost", "root", "root");
        String articleQuery = "Update articleUrls set hitId=? where articleID=?";
        PreparedStatement psArticle = conn.prepareStatement(articleQuery);
        psArticle.setString(1, hitId);
        psArticle.setString(2, articleId);
        psArticle.addBatch();
        psArticle.executeBatch();
        conn.commit();
    }

    public static  List<String> getChangeTextResponsePar4Sim(String workerId, String hitId) throws SQLException {
        String query = "SELECT text  FROM response WHERE workerId='"+workerId+"' AND hitId='"+hitId+"' ORDER BY timestamp;";
        ResultSet hits = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery(query);

        List<String> changeTextsPerHit = new ArrayList<>();
        changeTextsPerHit.add(getOriginalText(workerId, hitId).get(0).toLowerCase());
        while (hits.next()) {
           
            changeTextsPerHit.add(hits.getString("text").toLowerCase());
        }
        return changeTextsPerHit;
    }
    
    public static  List<String> getHitIds4Text(int iter) throws SQLException {

        ResultSet hits = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery("SELECT distinct hitId  FROM answers;");

        List<String> hitIds = new ArrayList<>();
        while (hits.next()) {
           if (iter > 0 ) {
               if (iter == getIteration(hits.getString("hitId"))) {
            hitIds.add(hits.getString("hitId"));
               }
           }
           else { // all as training
               hitIds.add(hits.getString("hitId"));
           }
        }
        return hitIds;
    }
    
    public static  List<String> getHitIds4TextInIteration(int iterfrom, int IterTo) throws SQLException {

        ResultSet hits = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery("SELECT distinct hitId  FROM answers;");

        List<String> hitIds = new ArrayList<>();
        while (hits.next()) {
         // all as training
            if (iterfrom < 0) {
                hitIds.add(hits.getString("hitId"));
            }
            else if (getIteration(hits.getString("hitId")) >= iterfrom && getIteration(hits.getString("hitId")) <= IterTo) {
            hitIds.add(hits.getString("hitId"));
               }
           }
        return hitIds;
    }
    
    public static  List<String> getHitIds(int iter) throws SQLException {

        ResultSet hits = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery("SELECT distinct hitId  FROM hit;");

        List<String> hitIds = new ArrayList<>();
        while (hits.next()) {
           if (iter > 0 ) {
               if (iter == getIteration(hits.getString("hitId"))) {
            hitIds.add(hits.getString("hitId"));
               }
           }
           else { // all as training
               hitIds.add(hits.getString("hitId"));
           }
        }
        return hitIds;
    }
    public static int getIteration(String  hitId) throws SQLException {

        ResultSet hits = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery("SELECT iteration  FROM hit where hitId='"+hitId+"';");

      int iteration = 0;
        while (hits.next()) {
           
            iteration = hits.getInt("iteration");
        }
        return iteration;
    }
    
    public static  List<String> getOriginalText( String workerId, String hitId) throws SQLException {
        String query = "SELECT origTextSubmit  FROM answers WHERE workerId='"+workerId+"' AND hitId='"+hitId+"';";
        ResultSet hits = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery(query);

        List<String> origTets = new ArrayList<>();
        while (hits.next()) {
           
            origTets.add(hits.getString("origTextSubmit"));
        }
        return origTets;
    }
    
    public static  List<String> getSimpleText(String workerId, String hitId) throws SQLException {

        String query = "SELECT simpleTextSubmit  FROM answers WHERE workerId='"+workerId+"' AND hitId='"+hitId+"';";
        ResultSet hits = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery(query);


        List<String> simpleTexts = new ArrayList<>();
        while (hits.next()) { 
            simpleTexts.add(hits.getString("simpleTextSubmit"));
        }
        return simpleTexts;
    }
    
    public static  List<String> getWorkerIds4Text(String hitId) throws SQLException {

        ResultSet hits = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery("SELECT distinct workerId  FROM answers where hitId='"+hitId+"';");

        List<String> hitIds = new ArrayList<>();
        while (hits.next()) {
           
            hitIds.add(hits.getString("workerId"));
        }
        return hitIds;
    }
    
    public static  List<String> getApprovedWorkerIds4Text(String hitId) throws SQLException {

        ResultSet hits = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery("SELECT distinct workerId  FROM answers where hitId='"+hitId+"' AND status = 'Approved';");

        List<String> hitIds = new ArrayList<>();
        while (hits.next()) {
           
            hitIds.add(hits.getString("workerId"));
        }
        return hitIds;
    }
    // for iteration 1
    public static Map<String, List<String>> getTargetCandidates(String hitId) throws SQLException {
        String query = "SELECT target, candidate  FROM candidate WHERE hitId='"+hitId+"';";
        ResultSet candidateTargets = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery(query);

        Map<String, List<String>> candidatesPerTargets = new HashMap<>();
        while (candidateTargets.next()) { 
            candidatesPerTargets.putIfAbsent(candidateTargets.getString("target"), new ArrayList<>());
            candidatesPerTargets.get(candidateTargets.getString("target")).add(candidateTargets.getString("candidate"));
        }
        return candidatesPerTargets;
    }
    
    // for iteration 2 and above
    public static Map<String, List<String>> getTargetCandidatesFromSuggestions(String hitId) throws SQLException {
        String cwiHitId = getCWIHitId(hitId);
        String query = "SELECT target, candidate  FROM candidatesuggestion WHERE cwiHitId='"+cwiHitId+"';";
        ResultSet candidateTargets = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery(query);

        Map<String, List<String>> candidatesPerTargets = new HashMap<>();
        while (candidateTargets.next()) { 
            candidatesPerTargets.putIfAbsent(candidateTargets.getString("target"), new ArrayList<>());
            candidatesPerTargets.get(candidateTargets.getString("target")).add(candidateTargets.getString("candidate"));
        }
        return candidatesPerTargets;
    }
    
    public static Map<String, List<String>> getTargetCandidateResponses(String hitId) throws SQLException {
        String query = "SELECT target, candidate, candidate_rank  FROM candidateresponse WHERE hitId='"+hitId+"';";
        ResultSet candidateTargets = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery(query);

        Map<String, List<String>> candidatesPerTargets = new HashMap<>();
        while (candidateTargets.next()) { 
            candidatesPerTargets.putIfAbsent(candidateTargets.getString("target"), new ArrayList<>());
            candidatesPerTargets.get(candidateTargets.getString("target")).add(candidateTargets.getString("candidate") +"||" + candidateTargets.getInt("candidate_rank"));
        }
        return candidatesPerTargets;
    }
    
    
    // for NDCG
    
    public static Map<String, List<String>> getTargetCandidateSuggestionsRank(String cwiHitID) throws SQLException {
        String query = "SELECT target, candidate  FROM candidatesuggestion WHERE cwiHitID='"+cwiHitID+"' order by candidate_rank desc ;";
        ResultSet candidateTargets = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery(query);

        Map<String, List<String>> candidatesPerTargets = new HashMap<>();
        while (candidateTargets.next()) { 
            candidatesPerTargets.putIfAbsent(candidateTargets.getString("target"), new ArrayList<>());
            candidatesPerTargets.get(candidateTargets.getString("target")).add(candidateTargets.getString("candidate"));
        }
        return candidatesPerTargets;
    }
    
    public static Map<String, List<String>> getTargetCandidatesRank(String hitId) throws SQLException {
        String query = "SELECT target, candidate  FROM candidate WHERE hitId='"+hitId+"' order by candidate_rank desc ;";
        ResultSet candidateTargets = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery(query);

        Map<String, List<String>> candidatesPerTargets = new HashMap<>();
        while (candidateTargets.next()) { 
            candidatesPerTargets.putIfAbsent(candidateTargets.getString("target"), new ArrayList<>());
            candidatesPerTargets.get(candidateTargets.getString("target")).add(candidateTargets.getString("candidate"));
        }
        return candidatesPerTargets;
    }
    
    public static int getTargetCandidateResponsesRank(String hitId, String target, String candidate) throws SQLException {
        String query = "SELECT candidate_rank  FROM candidateresponse WHERE hitId='"+hitId+"' AND target ='"+target+"' AND candidate ='"+candidate+"';";
        ResultSet candidateTargets = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery(query);

        while (candidateTargets.next()) { 
            return candidateTargets.getInt("candidate_rank");
            }
        return 0;
    }
    
    
    public static Map<String, List<String>> getChangeHtmlResponsePar4Sim(String hitId) throws SQLException {

        ResultSet hits = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery("SELECT assignmentId, html  from response FROM response WHERE hitId='"+hitId+"'+ ORDER BY timestamp;");

        Map<String, List<String>> changeTextsPerHit = new HashMap<>();
        while (hits.next()) {
            changeTextsPerHit.putIfAbsent(hits.getString("assignmentId"), new ArrayList<>());
            changeTextsPerHit.get(hits.getString("assignmentId")).add(hits.getString("html"));
        }
        return changeTextsPerHit;
    }
    // So I know which articles are already in the prev hits (from=beginin, to=last
    public static Map<Integer, String> getSpanishArticles(int from, int to) throws SQLException,
                    InstantiationException, IllegalAccessException, ClassNotFoundException {
        initDB("ComplexHITSpanishText", "localhost", "root", "1qazxsw2");
        ResultSet articles = st.executeQuery("select *  from articles ;");
        Map<Integer, String> HITs = new HashMap<>();
        int i = 0;
        while (articles.next()) {
            i++;
            if (i >= from && i <= to) {
                String article = articles.getString("article");
                int id = articles.getInt("id");
                LineIterator sentLines = new LineIterator(new StringReader(article));
                StringBuffer texts = new StringBuffer();
                while (sentLines.hasNext()) {
                    String sentence = sentLines.next();
                    texts.append(sentence + "<br>");
                    if (texts.toString().split(" ").length > 140) {
                        HITs.put(id, texts.toString());
                        texts = new StringBuffer();
                        break; // one HIT per articles as per Sanjas suggestion
                    }
                }
            }
            if (i > to) {
                break;
            }
        }
        System.out.println("hits to be created =" + HITs.size());
        return HITs;
    }

    // n records discarding discard hits
    public static Map<String, String> getEnglishCWIData( int from, int to)
                    throws SQLException, InstantiationException, IllegalAccessException,
                    ClassNotFoundException {
        ResultSet hits = Par4SemResource.cwiConn.createStatement()
                        .executeQuery("select distinct hitID, question  from HIT ORDER BY hitID;");
        Map<Integer, String> HITs = new HashMap<>();
        int i = 0;

        Map<String, String> results = new HashMap<>();
        while (hits.next()) {
            i++;
            if (i > to) {
                break; // take a break
            } 
            if (i < from) {
                continue;
            }
          
            String hitId = hits.getString("hitId");
            String content = hits.getString("question");

            results.put(hitId, content);
        }
        System.out.println("hits to be created =" + HITs.size());
        return results;
    }

    public static Map<Integer, String> getFrenchArticles(int from, int to) throws SQLException,
                    InstantiationException, IllegalAccessException, ClassNotFoundException {
        initDB("ComplexHITFrench", "localhost", "root", "root");
        ResultSet articles = st.executeQuery("select *  from articles ;");
        Map<Integer, String> HITs = new HashMap<>();
        int i = 0;
        while (articles.next()) {
            i++;
            if (i >= from && i <= to) {
                String article = StringEscapeUtils.escapeHtml(articles.getString("article"));
                int id = articles.getInt("id");
                LineIterator sentLines = new LineIterator(new StringReader(article));
                // StringBuffer texts = new StringBuffer();
                while (sentLines.hasNext()) {
                    String sentence = sentLines.next();
                    HITs.put(id, sentence);
                    /*
                     * texts.append(sentence + "<br>"); if (texts.toString().split(" ").length >
                     * 140) { HITs.put(id, texts.toString()); texts = new StringBuffer(); break; //
                     * one HIT per articles as per Sanjas suggestion }
                     */
                }
            }
            if (i > to) {
                break;
            }
        }
        System.out.println("hits to be created =" + HITs.size());
        return HITs;
    }

    // So I know which articles are already in the prev hits (from=beginin, to=last
    public static Map<Integer, String> getGermanArticles(int from, int to) throws SQLException,
                    InstantiationException, IllegalAccessException, ClassNotFoundException {
        initDB("ComplexHITGermanText", "localhost", "root", "1qazxsw2");
        ResultSet articles = st.executeQuery("select *  from articles ;");
        Map<Integer, String> HITs = new HashMap<>();
        int i = 0;
        while (articles.next()) {
            i++;
            if (i >= from && i <= to) {
                String article = articles.getString("article");
                int id = articles.getInt("id");
                LineIterator sentLines = new LineIterator(new StringReader(article));
                StringBuffer texts = new StringBuffer();
                while (sentLines.hasNext()) {
                    String sentence = sentLines.next();
                    texts.append(sentence + "<br>");
                    if (texts.toString().split(" ").length > 140) {
                        HITs.put(id, texts.toString());
                        texts = new StringBuffer();
                        break; // one HIT per articles as per Sanjas suggestion
                    }
                }
            }
            if (i > to) {
                break;
            }
        }
        System.out.println("hits to be created =" + HITs.size());
        return HITs;
    }

    public static Map<String, String> reCreateNewsHIT(String aDbName) throws SQLException,
                    InstantiationException, IllegalAccessException, ClassNotFoundException {
        initDB(aDbName, "localhost", "root", "root");
        ResultSet articles = st.executeQuery("SELECT distinct hitId, question FROM HIT;");
        Map<String, String> HITs = new HashMap<>();
        while (articles.next()) {
            String article = articles.getString("question");
            String hitId = articles.getString("hitId");
            HITs.put(hitId, article);
        }
        System.out.println("hits to be created =" + HITs.size());
        return HITs;
    }

    public static Set<String> allNativeWorkers(String aDbName) throws SQLException,
                    InstantiationException, IllegalAccessException, ClassNotFoundException {
        initDB(aDbName, "localhost", "root", "root");
        ResultSet articles = st
                        .executeQuery("SELECT distinct workerId FROM HIT  where language='yes';");
        Set<String> workers = new HashSet<>();
        while (articles.next()) {
            String workerId = articles.getString("workerId");
            workers.add(workerId);
        }
        return workers;
    }

    public static Set<String> allNonNativeWorkers(String aDbName) throws SQLException,
                    InstantiationException, IllegalAccessException, ClassNotFoundException {
        initDB(aDbName, "localhost", "root", "root");
        ResultSet articles = st
                        .executeQuery("SELECT distinct workerId FROM HIT  where language='no';");
        Set<String> workers = new HashSet<>();
        while (articles.next()) {
            String workerId = articles.getString("workerId");
            workers.add(workerId);
        }
        return workers;
    }

    public static Set<String> thisHitWorkers(String aDbName, String aHitId) throws SQLException,
                    InstantiationException, IllegalAccessException, ClassNotFoundException {
        initDB(aDbName, "localhost", "root", "root");
        ResultSet articles = st.executeQuery(
                        "SELECT distinct workerId FROM HIT  where hitId ='" + aHitId + "';");
        Set<String> workers = new HashSet<>();
        while (articles.next()) {
            String workerId = articles.getString("workerId");
            workers.add(workerId);
        }
        return workers;
    }

    public static String getTask1Question(String hitId) throws SQLException {
        // initDB("ComplexHIT", "localhost", "root", "1qazxsw2");
        ResultSet questions = st.executeQuery(
                        "select distinct question from HIT where hitId = '" + hitId + "';");
        questions.next();
        String question = questions.getString("question").replace("<br>", "");
        return question;
    }

    public static String getPar4SimOriginalText(String hitId) throws SQLException {
        ResultSet questions = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery("select text from hit where hitId = '" + hitId + "';");
        questions.next();
        String question = questions.getString("text");
        return question;
    }

    public static List<AnswerShared> getTopNFromCwi(String hitId) throws SQLException {

        ResultSet questions = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery("select cwiHitId from hit where hitId = '" + hitId + "';");
        questions.next();
        String cwiHitId = questions.getString("cwiHitId");
        List<AnswerShared> topNCwis = new ArrayList<>();
        String topnQuery = "select answer, begin, end, count(*) as ct from answers where hitId ='"
                        + cwiHitId + "' group by answer, begin, end having ct > 2 order by ct desc";
        ResultSet topNCwiRs = Par4SemResource.cwiConn.createStatement().executeQuery(topnQuery);
        while (topNCwiRs.next()) {
            topNCwis.add(new AnswerShared(topNCwiRs.getString("answer"), topNCwiRs.getInt("begin"),
                            topNCwiRs.getInt("end")));
            /*
             * if (i++>8){ break; }
             */
        }
        topNCwis.sort((a, b) -> a.compareTo(b));
        return topNCwis;
    }

    public static List<String> getCwi() throws SQLException {
        List<String> topNCwis = new ArrayList<>();
        String topnQuery = "select distinct answer from answers;";
        ResultSet cwis = Par4SemResource.cwiConn.createStatement().executeQuery(topnQuery);
        while (cwis.next()) {
            topNCwis.add(cwis.getString("answer"));
        }
        return topNCwis;
    }

    
    public static String getCWIHitId(String hitId) throws SQLException {

        ResultSet questions = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery("select cwiHitId from hit where hitId = '" + hitId + "';");
        questions.next();
        String cwiHitId = questions.getString("cwiHitId");
       return  cwiHitId;
    }

    public static Set<String> getTopNFromCwiHighlight(String hitId) throws Exception {

        ResultSet questions = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery("select cwiHitId from hit where hitId = '" + hitId + "';");
        questions.next();
        String cwiHitId = questions.getString("cwiHitId");
       return  getTopCWI(cwiHitId);
    }

    public static  Set<String> getTopCWI(String cwiHitId) throws Exception {
        
        Set<String> topNCwis = new HashSet<>();
        String topnQuery = "select answer, begin, end, count(*) as ct from answers where hitId ='"
                        + cwiHitId + "' group by answer, begin, end having ct > 3 order by ct desc";
        ResultSet topNCwiRs = Par4SemResource.cwiConn.createStatement().executeQuery(topnQuery);
        while (topNCwiRs.next()) {
            String cwi = topNCwiRs.getString("answer");
            /*
             * //TODO MWE not allowed in this version if (cwi.split(" ").length>1){
             * continue; }
             */
            if (!contained(topNCwis, cwi))
                topNCwis.add(cwi);
            /*
             * if (i++>8){ break; }
             */
        }
        return topNCwis;
    }
    
    public static Map<String, String> getCWIHitIDs() throws SQLException {

        ResultSet cwiHitIds = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery("select hitId, cwiHitId from hit ;");
        Map<String, String> hitIds = new HashMap<>();
        while (cwiHitIds.next()) {
            hitIds.put(cwiHitIds.getString("cwiHitId"), cwiHitIds.getString("hitId"));
        }
        return hitIds;
    }

    public static  Set<String> getHitIds() throws SQLException {

        ResultSet cwiHitIds = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery("select hitId from hit ;");
       Set<String> hitIds =new HashSet<>();
        while (cwiHitIds.next()) {
            hitIds.add(cwiHitIds.getString("hitId"));
        }
        return hitIds;
    }
    
    private static boolean contained(Set<String> existings, String newcwi) {

        for (String oldcwi : existings) {
            if (oldcwi.contains(newcwi) || newcwi.contains(oldcwi)) {
                return true;
            }
        }
        return false;
    }

    public static Set<String> getHitIds(String dbName) throws SQLException, InstantiationException,
                    IllegalAccessException, ClassNotFoundException {
        initDB(dbName, "localhost", "root", "root");
        ResultSet hitId = st.executeQuery("select distinct hitId from HIT;");

        Set<String> hitIds = new HashSet<>();
        while (hitId.next()) {
            hitIds.add(hitId.getString("hitId"));
        }

        return hitIds;
    }

    public static void deleteHits(String dbName) throws SQLException, InstantiationException,
                    IllegalAccessException, ClassNotFoundException, IOException {
        initDB(dbName, "localhost", "root", "root");
        Set<String> existingHits = new HashSet<>();
        Set<String> allHits = getHitIds(dbName);
        for (String hitId : FileUtils.readFileToString(new File(
                        "src/main/resources/simplification/renews/hitids/Sat Jun 03 00:46:51 CEST 2017.suscces"))
                        .split("\n")) {
            existingHits.add(hitId);
        }
        allHits.removeAll(existingHits);
        for (String hitToDelete : allHits) {

            String query = "DELETE FROM HIT where hitId = ?";
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, hitToDelete);
            int x = preparedStmt.executeUpdate();
            System.out.println(x);
            query = "DELETE FROM answers where hitId = ?";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, hitToDelete);
            x = preparedStmt.executeUpdate();
            System.out.println(x);
        }
        conn.commit();
        conn.close();
    }

    public static void mergeHits(String dbName, String fileName)
                    throws SQLException, InstantiationException, IllegalAccessException,
                    ClassNotFoundException, IOException {
        initDB(dbName, "localhost", "root", "root");
        Map<String, String> oldHitMaps = new HashMap<>();
        Set<String> allHits = getHitIds(dbName);

        System.out.println(allHits.size());
        LineIterator it = FileUtils.lineIterator(new File(fileName));
        while (it.hasNext()) {
            String[] hitMaps = it.next().split("\t");
            oldHitMaps.put(hitMaps[1], hitMaps[0]);
        }

        for (String hitId : allHits) {
            if (oldHitMaps.keySet().contains(hitId)) {
                String updateHits = "UPDATE HIT set hitID = ? where hitId = ?";
                PreparedStatement preparedStmtHit = conn.prepareStatement(updateHits);
                preparedStmtHit.setString(1, oldHitMaps.get(hitId));
                preparedStmtHit.setString(2, hitId);
                preparedStmtHit.executeUpdate();
                String updateAnswers = "UPDATE answers set hitID = ? where hitId = ?";
                PreparedStatement preparedStmtanswer = conn.prepareStatement(updateAnswers);
                preparedStmtanswer.setString(1, oldHitMaps.get(hitId));
                preparedStmtanswer.setString(2, hitId);
                preparedStmtanswer.executeUpdate();

            }
        }
        conn.commit();
        conn.close();
    }

    public static void updateEducLevel(String dbName) throws SQLException, InstantiationException,
                    IllegalAccessException, ClassNotFoundException, IOException {
        initDB(dbName, "localhost", "root", "root");

        Map<String, String> hitLevels = new HashMap<>();

        ResultSet workerLevels = st.executeQuery("select workerID, level from answers;");

        while (workerLevels.next()) {
            String id = workerLevels.getString("workerID");
            String level = workerLevels.getString("level");
            if (level == null) {
                continue;
            }
            if (hitLevels.keySet().contains(id)) {
                if (!level.equals(hitLevels.get(id))) {
                    System.out.println("conflict");
                }
            } else {
                hitLevels.put(id, level);
            }
        }

        ResultSet workers = conn.createStatement().executeQuery("select workerID from HIT;");
        int i = 0;
        while (workers.next()) {
            String workerId = workers.getString("workerID");
            if (hitLevels.containsKey(workerId)) {
                String updateHits = "UPDATE HIT set level = ? where workerId = ?";
                PreparedStatement preparedStmtHit = conn.prepareStatement(updateHits);
                preparedStmtHit.setString(1, hitLevels.get(workerId));
                preparedStmtHit.setString(2, workerId);
                preparedStmtHit.executeUpdate();
                String updateAnswers = "UPDATE answers set level = ? where workerId = ?";
                PreparedStatement preparedStmtanswer = conn.prepareStatement(updateAnswers);
                preparedStmtanswer.setString(1, hitLevels.get(workerId));
                preparedStmtanswer.setString(2, workerId);
                preparedStmtanswer.executeUpdate();
                System.out.println(++i);
            }

        }
        conn.commit();
        conn.close();
    }

    public static void initDB(String dbName, String ip, String user, String pswd)
                    throws InstantiationException, IllegalAccessException, ClassNotFoundException,
                    SQLException {

        String url = "jdbc:mysql://" + ip + "/";
        String driver = "org.gjt.mm.mysql.Driver";
        String userName = user;
        String password = pswd;
        Class.forName(driver).newInstance();
        conn = DriverManager.getConnection(url + dbName, userName, password);
        conn.setAutoCommit(false);
        st = conn.createStatement();
    }

    public static void initPar4SimDB(String dbName, String ip, String user, String pswd)
                    throws InstantiationException, IllegalAccessException, ClassNotFoundException,
                    SQLException {

        String url = "jdbc:mysql://" + ip + "/";
        String driver = "org.gjt.mm.mysql.Driver";
        String userName = user;
        String password = pswd;
        Class.forName(driver).newInstance();
        connPar4Sim = DriverManager.getConnection(url + dbName, userName, password);
        connPar4Sim.setAutoCommit(false);
    }
}
