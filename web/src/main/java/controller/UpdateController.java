package controller;

import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import undoredo.ParaphraseLabelChanger;
import undoredo.ParaphraseTextChanger;

@WebServlet(value = "/updater", name = "updater")
public class UpdateController extends HttpServlet {
	private static final long serialVersionUID = -2908926119230344244L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			doPost(request, response);
		} catch (EOFException e) {
			init();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			System.out.println("======UPDATING=====" + request.getParameter("text"));
			post(request, response);
		} catch (Exception e) {
			try {
                Controller.reInit();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
		   // e.printStackTrace();
		}
	}

	protected void post(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		HttpSession session = request.getSession();
		String selection = request.getParameter("textSelected");
		String html = request.getParameter(Controller.HTML);
	    String hitId = session.getId();
	    String workerId = session.getId();
	    String hitIdWorkerId = hitId+workerId;
		JsonArray matches = new JsonArray();
		
		for (JsonElement match:Controller.changeLablesManagers.get(hitIdWorkerId).getLables()) {
		    matches.add(match);
		}
		
		 if(getCounts(Controller.getNewLinedText(html), selection) < 2 && CheckSpelling.getCandidates3(selection).size() > 2) {
		     matches.add(selection);
		     Controller.changeLablesManagers.get(hitIdWorkerId).addChangeable(new ParaphraseLabelChanger(matches));
		     Controller.changeManagers.get(hitIdWorkerId).addChangeable(new ParaphraseTextChanger(Controller.getNewLinedText(html)));
	            }
		PrintWriter out = response.getWriter();
		response.setContentType("text/plain");

		session.setAttribute(Controller.SELECTION, selection);
		out.write(Controller.getNewLinedText(html));
		session.setAttribute(Controller.HTML, html);
		out.close();

	}
	
	public static int getCounts(String text, String word) {
	    int count = 0;
	    if (StringUtils.countMatches(text, " "+word+ " ") >0) {
	       count = count +StringUtils.countMatches(text, " "+word+ " ");
	    }
	    if (StringUtils.countMatches(text, "\n"+word+ " ") >0) {
	        count = count +StringUtils.countMatches(text, "\n"+word+ " ");
        }
	    if (StringUtils.countMatches(text, " "+word+ "\n") > 0) {
	        count = count +StringUtils.countMatches(text, " "+word+ "\n");
        }
	    if (StringUtils.countMatches(text, " "+word+ ",") > 0) {
            count = count +StringUtils.countMatches(text, " "+word+ ",");
        }
	    if (StringUtils.countMatches(text, ","+word+ "\n") > 0) {
            count = count +StringUtils.countMatches(text, ","+word+ "\n");
        }
	    if (StringUtils.countMatches(text, " "+word+ ".") > 0) {
	        count = count +StringUtils.countMatches(text, " "+word+ ".");
        }
	    if (StringUtils.countMatches(text, " "+word+ "\"") > 0) {
            count = count +StringUtils.countMatches(text, " "+word+ "\"");
        }
	    if (StringUtils.countMatches(text, "\""+word+ "\"") > 0) {
            count = count +StringUtils.countMatches(text, "\""+word+ "\"");
        }
	    if (StringUtils.countMatches(text, "\""+word+ " ") > 0) {
            count = count +StringUtils.countMatches(text, "\""+word+ " ");
        }
	    return count;
	}

}
