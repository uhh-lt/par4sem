package controller;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.json.simple.JSONObject;

import com.google.gson.JsonArray;

import cwi.AnswerShared;
import cwi.CWI;
import cwi.CWIFeaturize;
import database.Par4SemResource;
import undoredo.ChangeLabelManager;
import undoredo.ChangeManager;
import undoredo.ParaphraseLabelChanger;
import undoredo.ParaphraseTextChanger;
import utils.OpenNLPSegmenter;


@WebServlet(value = "/SpellChecker", name = "SpellChecker")
public class CheckSpelling extends HttpServlet {
    private static final long serialVersionUID = -2908926119230344244L;

    private static final String GET_CWI = "get_incorrect_words";
    private static final String ACTION = "action";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
        doPost(request, response);
        }
        catch (EOFException e){
        	Controller.reInit();
        //    e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            System.out.println("======CHECKING====="+ request.getParameter("text"));
            post(request, response);
        } catch (Exception e) {
            Controller.reInit();
            e.printStackTrace();
        }
    }

    protected void post(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
      
        HttpSession session = request.getSession();
        String hitId = session.getId();
        String workerId = session.getId();
        String text = request.getParameter("text[]");
        String html = request.getParameter("html");
        String target = request.getParameter("word");
        //String assignmentId = request.getParameter("assignmentId");          

        PrintWriter out = response.getWriter();
        response.setContentType("text/plain");
        JSONObject obj = new JSONObject();

        String undo = (String) session.getAttribute(Controller.UNDO);
		if (undo != null) {
			highlightCwisUndo(out, obj, session, hitId+workerId);
			session.removeAttribute(Controller.UNDO);
			return;
		}

        // Highlighting the selection (right after the selection event
        if (session.getAttribute(Controller.SELECTION) != null) {

            try{
            text = Controller.getNewLinedText(html);
            }
            catch (Exception e){
                // HMMMM
            }
            String hitWorkerId = hitId + workerId;
            JsonArray existingMatch = Controller.changeLablesManagers.get(hitWorkerId).getLables();
            highlightSelection(out, obj, existingMatch, session, hitId);
          
            session.removeAttribute(Controller.SELECTION);
            return;
        }

        System.out.println("Action="+request.getParameter(ACTION));
        if (request.getParameter(ACTION).equals(GET_CWI)) {
            highlightCwis(out, obj, Controller.getNewLinedText(html), hitId, workerId);
        } 
        // replacing words - hence remove highlighted words (and do highlight immediaTELY)
        else {
            highlightCwisParaphrases(out, obj, target,Controller.getNewLinedText(html), hitId);
        }

    }

    @SuppressWarnings("unchecked")
    private void highlightSelection(PrintWriter out, JSONObject obj, JsonArray existingMatches,
            HttpSession session, String hitId) throws IOException, SQLException {
        String selection = (String) session.getAttribute(Controller.SELECTION);

        if (getCandidates3(selection).size() < 1) {
            obj.put("outcome", "success");
            JsonArray inArray = new JsonArray();
            inArray.add(existingMatches);
            obj.put("data", inArray);
            out.write(obj.toJSONString());
            out.close();
            out.close();
            return ;
        }

        obj.put("outcome", "success");
        JsonArray matches = existingMatches == null ? new JsonArray() : existingMatches;
        //matches.add(selection);
        JsonArray exArray = new JsonArray();
        exArray.add(matches);
        obj.put("data", exArray);
        out.write(obj.toJSONString());
        out.close();
       return;
    }

    @SuppressWarnings("unchecked")
    public void highlightCwis(PrintWriter out, JSONObject obj, String text, String hitId, String workerId) throws Exception {

		JsonArray lables = new JsonArray();
		obj.put("outcome", "success");
		JsonArray exArray = new JsonArray();

		String hitIdWorkerId = hitId + workerId;

   //     try {
            String path = System.getProperty("java.io.tmpdir");
            File tempFile = new File(path, "pred");
            FileOutputStream tmpOs = new FileOutputStream(tempFile);
            IOUtils.write("1,2,3,4,5,6,7,8,9,10,11,12,13,14\n", tmpOs, "UTF-8");
            List<String> allCwi = new ArrayList<>();
            List<String> posCwi = new ArrayList<>();
            for (String sent : OpenNLPSegmenter.detectSentences(text)){
                List<AnswerShared> negAnswers = CWI.getCandidateCWI(
                                new ArrayList<AnswerShared>(), sent);
                for (AnswerShared negAnswer : negAnswers) {
                    allCwi.add(negAnswer.getAnswer());
                    String line = "PH" + "\t" + sent + "\t" + negAnswer.getBegin() + "\t"
                                    + negAnswer.getEnd() + "\t" + negAnswer.getAnswer()
                                    + "\t0\t0\t0\t0\t0\t0\n";
                    IOUtils.write(CWIFeaturize.generateFeatures(line) + "\n", tmpOs, "UTF-8");
                }
            }
            int i = 0;
            List<Object> cwis = Controller.cl.predict(tempFile.getAbsolutePath());
            for (Object pred : cwis) {
                if (pred.equals(1.0)) {
                    posCwi.add(allCwi.get(i));
                }
                i++;
            }
		    
			//Set<String> topCwis = AddResult2Db2.getTopNFromCwiHighlight(hitId);
			Set<String> topCwis = new HashSet<>(posCwi);
//			int max = 0;
			for (String cwi : topCwis) {
			//	if (getCandidates3(cwi).size() > 0) {
				    
				    if(UpdateController.getCounts(text, cwi) <2) {
				        lables.add(cwi);
				    }
				  /*  if(StringUtils.countMatches(text, " "+cwi+ " ") < 2) {
				        lables.add(cwi);
			            }*/
//					max++;
					//Do we need to control howmany to highlight
					/*if(max > 15) {
						break;
					}*/
		//		}
			}
			Controller.changeManagers.putIfAbsent(hitIdWorkerId, new ChangeManager());
			Controller.changeManagers.get(hitIdWorkerId).addChangeable(new ParaphraseTextChanger(text));
			
			Controller.changeLablesManagers.putIfAbsent(hitIdWorkerId, new ChangeLabelManager());
            Controller.changeLablesManagers.get(hitIdWorkerId).addChangeable(new ParaphraseLabelChanger(lables));
/*		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			System.out.println("this fails but life continues");
			e.printStackTrace();
		}*/
		System.out.println("UNDO/REDO =====" + lables);
		obj.put("outcome", "success");
		exArray.add(lables);
		obj.put("data", exArray);
		out.write(obj.toJSONString());

		out.close();
    }
	    @SuppressWarnings("unchecked")
	    private void highlightCwisUndo(PrintWriter out, JSONObject obj, HttpSession session, String hitIdWorkerId) throws IOException {

			JsonArray lables = Controller.changeLablesManagers.get(hitIdWorkerId).getLables();
			
			obj.put("outcome", "success");
			JsonArray exArray = new JsonArray();
			System.out.println("UNDO/REDO =====" + lables);
			obj.put("outcome", "success");
			exArray.add(lables);
			obj.put("data", exArray);
			out.write(obj.toJSONString());

			out.close();
	    }
    private void highlightCwisParaphrases(PrintWriter out, JSONObject obj, String word, String text, String hitId) throws IOException, SQLException {
        JsonArray candidatesResponses = new JsonArray();
       // Map<String, Double> candidates = getCandidates2(word, AddResult2Db2.getCWIHitId(hitId), false);
        Map<String, Double> candidates = getCandidates(word);
        Map<String, Double> sortedCandidates = candidates.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        sortedCandidates.forEach((candidate, score) -> {
            candidatesResponses.add(StringEscapeUtils.escapeHtml4(candidate));
        });
        
        out.write(candidatesResponses.toString());
        out.close();
    }

   public Map<String, Double> getCandidates(String word) throws IOException {
        Map<String, Double> candidates = new HashMap<>();
        try {
            Controller.conf.getSimplePPDBParaphraseCwiHighlight(word, candidates);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            Controller.conf.getSingleParaphraseCwiHighlight(word, candidates);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            Controller.conf.getMWEParaphraseHighlight(word, candidates);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            Controller.conf.getSimilar(word, candidates, 10);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return candidates;
    }
    
    public static Map<String, Double> getCandidates2(String word, String cwiHitID, boolean suggesting) throws IOException, SQLException {
        
        Map<String, Double> suggestions = Controller.conf.getCandidateFromDBSuggestion(word, cwiHitID);
        if (suggestions!=null && suggestions.size()>0 &&!suggesting) {
            return suggestions;
        }
        
        
        Map<String, Double> candidates = new HashMap<>();
     //   try {
            Controller.conf.getSimplePPDBParaphraseCwiHighlight(word, candidates);
/*        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    //    try {
            Controller.conf.getSingleParaphraseCwiHighlight(word, candidates);
/*        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    //    try {
            Controller.conf.getMWEParaphraseHighlight(word, candidates);
    /*    } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        try {
            Controller.conf.getSimilar(word, candidates, 30);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
      /*      try {
                Controller.conf.getSimilar(word, candidates);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
        return candidates;
    }
    
    public static Map<String, Double> getCandidates3(String word) throws IOException, SQLException {       
            
            Map<String, Double> candidates = new HashMap<>();
         //   try {
                Controller.conf.getSimplePPDBParaphraseCwiHighlight(word, candidates);
    /*        } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
        //    try {
                Controller.conf.getSingleParaphraseCwiHighlight(word, candidates);
    /*        } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
        //    try {
                Controller.conf.getMWEParaphraseHighlight(word, candidates);
        /*    } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
            try {
                Controller.conf.getSimilar(word, candidates, 30);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
          /*      try {
                    Controller.conf.getSimilar(word, candidates);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*/
            return candidates;
        }

}
