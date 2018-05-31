package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.text.similarity.LevenshteinDistance;

import cwi.AddResult2Db2;
import database.Par4SemResource;
import undoredo.ParaphraseLabelChanger;
import undoredo.ParaphraseTextChanger;

@WebServlet(value = "/logger", name = "logger")
public class LoggingController extends HttpServlet {
    private static final long serialVersionUID = -2908926119230344244L;

    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Controller.fixHeaders(response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
        try {
            doPost(request, response);
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
        try {
            System.out.println("======LOGGING=====");
            post(request, response);
        } catch (Exception e) {
            try {
                Controller.reInit();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    protected void post(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException, InstantiationException,
                    IllegalAccessException, ClassNotFoundException, SQLException {

        try {
            Controller.fixHeaders(response);
            // Your code here...
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain");
            response.getWriter().println(Controller.buildErrorMessage(e));
        } catch (Throwable e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain");
            response.getWriter().println(Controller.buildErrorMessage(e));
        }
        
        HttpSession session = request.getSession();

        String text = request.getParameter("text");
        String html = request.getParameter("html");

        String undo = request.getParameter("undo");
        // String hitId = session.getId();
        // String workerId = session.getId();

        String hitId = request.getRemoteAddr();
        String workerId = request.getRemoteHost();

        String assignmentId = request.getParameter("assignmentId");

        String ip = request.getParameter("pi");
        String country = request.getParameter("cnt");
        String code = request.getParameter("cd");

        if (ip != null && (!AddResult2Db2.existsWorkerLocation(workerId, ip))) {
            logLocations(workerId, ip, country, code);
            session.removeAttribute(Controller.UNDO);
        } else if (undo != null) {
            text = Controller.getNewLinedText(html);
            logActivity(session, text, html, hitId, response);
        } else if (text != null) {

            text = Controller.getNewLinedText(html);
            logActivity(session, text, html, hitId, workerId, assignmentId, response);
        }
    }

    private void logActivity(HttpSession session, String text, String html, String hitId,
                    String workerId, String assignmentId, HttpServletResponse response)
                    throws IOException, InstantiationException, IllegalAccessException,
                    ClassNotFoundException, SQLException {
        System.out.println("+++++++++++LOGGING TEXT+++++++++\n" + text);
        Controller.changeManagers.get(hitId + workerId)
                        .addChangeable(new ParaphraseTextChanger(text));
        Controller.changeLablesManagers.get(hitId + workerId)
                        .addChangeable(new ParaphraseLabelChanger(Controller.changeLablesManagers
                                        .get(hitId + workerId).getLables()));
        AddResult2Db2.addResponse(hitId, workerId, assignmentId, text, html,
                        new Timestamp(System.currentTimeMillis()));
        PrintWriter out = response.getWriter();
        response.setContentType("text/plain");
        Double sim = getSim((String) session.getAttribute(Controller.ORIG_TEXT), text);
        System.out.println("Similarity = " + sim);
        if (sim < Controller.TRESHOLD) {
            out.write("no"); // no enough change done so far
        } else {
            out.write("yes"); // enough change is done, enable submit button
        }
        out.close();
    }

    private void logActivity(HttpSession session, String text, String html, String hitId,
                    HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();
        response.setContentType("text/plain");
        Double sim = getSim((String) session.getAttribute(Controller.ORIG_TEXT), text);
        System.out.println("Similarity = " + sim);
        if (sim < Controller.TRESHOLD) {
            out.write("no"); // no enough change done so far
        } else {
            out.write("yes"); // enough change is done, enable submit button
        }
        out.close();
    }

    private void logLocations(String workerId, String ip, String country, String code) {

        AddResult2Db2.logLocations(workerId, ip, country, code);

    }

    class WorkerLocation {
        String workerId;
        String ip;
        String country;
        String code;
    }

    private double getSim(String origText, String changeText) {
        LevenshteinDistance sm = new LevenshteinDistance();
        return sm.apply(origText, changeText);
    }
}
