package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.json.simple.JSONObject;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import controller.CheckSpelling;

public class SemschJob implements org.quartz.Job {

    public SemschJob() {
    }
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.err.println("Hello SEMSCH!  Your Job is executing.");
        CheckSpelling sp = new CheckSpelling();
        PrintWriter out = null;
        try {
            out = new PrintWriter(new File("tmp"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String text = "Woman are smarter than guys in average 3 out of four people . ";
        JSONObject obj = new JSONObject();
        String hitId = "3456";
        String workerId = "7654";

        try {
            sp.highlightCwis(out, obj, text, hitId, workerId);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}