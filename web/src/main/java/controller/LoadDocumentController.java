package controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;

import cwi.AddResult2Db2;

@WebServlet(value = "/loadDocument", name = "loadDocument")
public class LoadDocumentController extends HttpServlet {
    private static final long serialVersionUID = -2908926119230344244L;

    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
        Controller.fixHeaders(response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {

        doPost(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
        try {
            System.out.println("======LOADING=====" + request.getParameter("text"));
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

    protected void post(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException, SQLException {

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
        String start = request.getParameter(Controller.START);
        // String hitId = session.getId();
        // String workerId = session.getId();
    	// String workerId = session.getId();
        String workerId = request.getRemoteHost();
        String text = request.getParameter("content");
        String undo = request.getParameter(Controller.UNDO);
        //String assignmentId = request.getParameter(Controller.ASSIGNMENTID);

        if (start != null) {
            session.removeAttribute(Controller.UNDO);
            return;
        }

        if (Controller.hitId != null)
            session.setAttribute(Controller.HITID, Controller.hitId);
        if (workerId != null)
            session.setAttribute(Controller.WORKERID, workerId);

        // UNDO TEXT
        if (undo != null && undo.equals("undo")) {
            doUndo(response, Controller.hitId + workerId, session);
            session.setAttribute(Controller.UNDO, undo);
            return;
        }

        // REDO TEXT
        if (undo != null && undo.equals("redo")) {
            doRedo(response, Controller.hitId + workerId, session);
            session.setAttribute(Controller.UNDO, undo);
            return;
        }

        // BEGIN CHANGE MTURK
        // if (text == null) {
        try {
        	Set<String> hitIds = AddResult2Db2.getHitIds();
        	Controller.hitId = new ArrayList<>(hitIds).get((new Random()).nextInt(hitIds.size()));
            // IF NO DB, GET it FROm ...
            text = AddResult2Db2.getPar4SimOriginalText(Controller.hitId);// .replaceAll("\n", " ");
            // text = text.replaceAll("(\n)", "&#013; &#010;");
             session.setAttribute(Controller.ORIG_TEXT, text);
        } catch (Exception e) {

            text = getRandomeFile();
            session.setAttribute(Controller.ORIG_TEXT, text);
        }

        // END CHANGE MTURK

        /**
         * BEGIN CHANGE MTURK This is for MTurk
         * 
         * session.setAttribute(Controller.ORIG_TEXT, text); System.out.println("Text
         * Load = " + text + "HitId=" + hitId); if (text == null) { text = "Error!!!
         * Please reload page or return the HIT and contact the requester"; } END CHANGE
         * MTURK
         */
        session.setAttribute("text", text);
        session.setAttribute("hitId", Controller.hitId);
        PrintWriter out = response.getWriter();
        response.setContentType("text/plain");
        // out.write(StringEscapeUtils.escapeHtml4(text));
        out.write(StringEscapeUtils.escapeHtml4(text));
        out.close();

    }

    private void doRedo(HttpServletResponse response, String hitIdWorkerId, HttpSession session)
                    throws IOException {

        try {
            PrintWriter out = response.getWriter();
            response.setContentType("text/plain");
            String text = Controller.changeManagers.get(hitIdWorkerId).redo();
            Controller.changeLablesManagers.get(hitIdWorkerId).redo();
            System.out.println("********REDO TEXTS********" + text);
            out.write(StringEscapeUtils.escapeHtml4(text));
            out.close();
        } catch (Exception e) {
            session.removeAttribute(Controller.UNDO);
            PrintWriter out = response.getWriter();
            response.setContentType("text/plain");
            out.write("Redo over!");
            out.close();
        }
    }

    private void doUndo(HttpServletResponse response, String hitIdWorkerId, HttpSession session)
                    throws IOException {

        try {
            PrintWriter out = response.getWriter();
            response.setContentType("text/plain");
            String text = Controller.changeManagers.get(hitIdWorkerId).undo();
            Controller.changeLablesManagers.get(hitIdWorkerId).undo();
            System.out.println("********UNDO TEXTS********" + text);
            out.write(StringEscapeUtils.escapeHtml4(text));
            out.close();
        } catch (Exception e) {
            session.removeAttribute(Controller.UNDO);
            PrintWriter out = response.getWriter();
            response.setContentType("text/plain");
            out.write("Undo over!");
            out.close();
        }
    }

    private String getRandomeFile() {

        ServletContext context = getServletContext();
        String path = context.getRealPath("/files/");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("files/").getFile());
        
        File[] files = new File(file.getAbsolutePath()).listFiles();

        Random rand = new Random();

        try {
            return FileUtils.readFileToString(files[rand.nextInt(files.length)]);
        } catch (IOException e) {
            return "So dear Teacher, will you please take him by his hand and teach him things he will have to know, teaching him - but gently, if you can. Teach him that for every enemy, there is a friend. He will have to know that all men are not just, that all men are not true. But teach him also that for every scoundrel there is a hero, that for every crooked politician, there is a dedicated leader.";

        }
    }

}
