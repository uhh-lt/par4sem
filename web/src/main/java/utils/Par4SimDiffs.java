package utils;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.github.difflib.algorithm.DiffException;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;

import cwi.AddResult2Db2;
import database.Par4SemResource;
import utils.Utils;

public class Par4SimDiffs {
    public static void main(String[] args)
                    throws DiffException, SQLException, PropertyVetoException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
        // create a configured DiffRowGenerator
        DiffRowGenerator generator = DiffRowGenerator.create().showInlineDiffs(true)
                        .mergeOriginalRevised(true).inlineDiffByWord(true).oldTag(f -> "~~")
                        .newTag(f -> "##").build();
        init();
      // for (String workerId : AddResult2Db2.getWorkerIds4Text(hitId)) {
//        candidateResponseFromHistory(generator, 3);
        // fromAnswers(generator);
        
  //      generateTrainingData(generator, -1); TRAIN ALL    
      //  generateTrainingData(generator, -1, -1, "/Users/seidmuhieyimam/data/semsch/par4sim/train_it2.txt");
        //Test it3 on it 1 and 2
     //   generateTrainingData(generator, 3, 3, "/Users/seidmuhieyimam/data/semsch/par4sim/test.ndgc.it3.txt");
     //   generateTrainingData(generator, -1, -1, "/Users/seidmuhieyimam/data/semsch/par4sim/train_it3.txt");
        
   // --++--
   //     generateTrainingData(generator, 2, 2, "/Users/seidmuhieyimam/data/semsch/par4sim/train_it2.txt");
    //    generateTrainingData(generator, 2, 2, "/Users/seidmuhieyimam/data/semsch/par4sim/test.ndgc.it2.txt");
  //    generateTrainingData(generator, 2, 3, "/Users/seidmuhieyimam/data/semsch/par4sim/train_it3.txt");
    //    generateTrainingData(generator, 2, 2, "/Users/seidmuhieyimam/data/semsch/par4sim/test.ndgc.it2.txt");

   // --++--
        
  //    candidateResponseFromHistory(generator, 4);
 //   generateTrainingData(generator, -1, -1, "/Users/seidmuhieyimam/data/semsch/par4sim/train_it4.txt");
//      generateTrainingData(generator, 4, 4, "/Users/seidmuhieyimam/data/semsch/par4sim/test.ndgc.it4.txt");
      
//          candidateResponseFromHistory(generator, 5);
      //   generateTrainingData(generator, -1, -1, "/Users/seidmuhieyimam/data/semsch/par4sim/train_it4.txt");
 //          generateTrainingData(generator, 5, 5, "/Users/seidmuhieyimam/data/semsch/par4sim/test.ndgc.it5.txt");

        
  //    candidateResponseFromHistory(generator, 6);
  //   generateTrainingData(generator, -1, -1, "/Users/seidmuhieyimam/data/semsch/par4sim/train_it4.txt");
 //      generateTrainingData(generator, 6, 6, "/Users/seidmuhieyimam/data/semsch/par4sim/test.ndgc.it6.txt");
    
        
      //      candidateResponseFromHistory(generator, 7);
    //         generateTrainingData(generator, 7, 7, "/Users/seidmuhieyimam/data/semsch/par4sim/test.ndgc.it7.txt");
          
        
           generateTrainingData(generator, 2, 5, "/Users/seidmuhieyimam/data/semsch/par4sim/train_it7.txt");
    }

    private static void fromAnswers(DiffRowGenerator generator, int iter) throws DiffException, SQLException {
        int total = 0;
        for (String hitId : AddResult2Db2.getHitIds4Text(iter)) {
            List<Par4SimAnswer> answers = new ArrayList<>();
            for (String workerId : AddResult2Db2.getWorkerIds4Text(hitId)) {
                String origText = AddResult2Db2.getOriginalText(workerId, hitId).get(0);
                String simpleText = AddResult2Db2.getSimpleText(workerId, hitId).get(0);
                List<DiffRow> rows = generator.generateDiffRows(Arrays.asList(origText),
                                Arrays.asList(simpleText));

                for (DiffRow row : rows) {
                    String rowOldLine = row.getOldLine().replaceAll("~~ ~~", " ")
                                    .replaceAll("## ##", " ");
                    String targets[] = StringUtils.substringsBetween(rowOldLine, "~~", "~~");
                    String candidates[] = StringUtils.substringsBetween(rowOldLine, "##", "##");
                    int i = 0;
                    for (String target : targets) {
                        System.out.println(target + "^^" + candidates[i]);
                        Par4SimAnswer answer = new Par4SimAnswer(target, candidates[i], 1);
                        if (answers.contains(answer)) {
                            answer = answers.get(answers.indexOf(answer));
                            answers.get(answers.indexOf(answer)).setCount(answer.getCount() + 1);
                        } else {
                            answers.add(answer);
                        }
                        i++;
                    }

                }
            }

            total = total + answers.size();
            for (Par4SimAnswer answer : answers) {
                System.out.println(answer.getTarget() + "----" + answer.getCandidate() + " "
                                + answer.getCount());
            }
        }

    }

    private static void generateTrainingData(DiffRowGenerator generator, int iterFrom, int IterTo, String file)
                    throws DiffException, SQLException, InstantiationException,
                    IllegalAccessException, ClassNotFoundException, IOException {
        int lineNumber = 116;
        FileOutputStream os = new FileOutputStream(new File(file));
        for (String hitId : AddResult2Db2.getHitIds4TextInIteration(iterFrom, IterTo)) {
            String origText = AddResult2Db2.getPar4SimOriginalText(hitId);
            // iteration 1
            // Map<String, List<String>> canddiateserTarges = AddResult2Db2.getTargetCandidates(hitId);
             // iteration 2 and above
             Map<String, List<String>> canddiateseTargets = AddResult2Db2.getTargetCandidatesFromSuggestions(hitId);
            Map<String, List<String>> canddiateseTargetsResponse = AddResult2Db2
                            .getTargetCandidateResponses(hitId);
            for (String line : origText.split("\n")) {
                
                for (String target : canddiateseTargets.keySet()) {
                    if (line.contains(target)) {
                        if (canddiateseTargetsResponse.get(target) != null) {
                            for (String candidate : canddiateseTargets.get(target)) {
                                int count = getCandidateCount(
                                                canddiateseTargetsResponse.get(target), candidate);
                                String trainLine = lineNumber + "\t" + line + "\t" + target +"\t"
                                                + candidate +"\t"+ line.indexOf(target) + "\t0.0\t" + count;
                                IOUtils.write(trainLine+"\n", os,"UTF8");
                            }

                        }
                        // ignored target
                        else {
                            for (String candidate : canddiateseTargets.get(target)) {
                                int count = 0;
                                String trainLine = lineNumber + "\t" + line + "\t" + target +"\t"
                                               + candidate + "\t"+ line.indexOf(target) + "\t0.0\t" + count;
                                IOUtils.write(trainLine+"\n", os,"UTF8");
                            }
                        }
                        lineNumber++;
                    }
                   
                }
               
            }
           
        }
    }
    
    private static void candidateResponseFromHistory(DiffRowGenerator generator, int iter) throws DiffException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
        int total = 0;
       // int 
        for (String hitId : AddResult2Db2.getHitIds4Text(iter)) {
            List<Par4SimAnswer> answers = new ArrayList<>();
            // iteration 1
           // Map<String, List<String>> canddiateserTarges = AddResult2Db2.getTargetCandidates(hitId);
            // iteration 2 and above
            Map<String, List<String>> canddiateserTarges = AddResult2Db2.getTargetCandidatesFromSuggestions(hitId);        

            for (String workerId : AddResult2Db2.getApprovedWorkerIds4Text(hitId)) {
            //for (String workerId : AddResult2Db2.getWorkerIds4Text(hitId)) {
                List<String> changeSentences = AddResult2Db2.getChangeTextResponsePar4Sim(workerId,
                                hitId);
                String[] sentArrays = new String[changeSentences.size()];
                sentArrays = changeSentences.toArray(sentArrays);
                for (int i = 0; i < sentArrays.length - 1; i++) {
                    int index = i;
                    List<DiffRow> rows = generator.generateDiffRows(
                                    Arrays.asList(sentArrays[index]),
                                    Arrays.asList(sentArrays[index + 1]));

                    for (DiffRow row : rows) {

                        String rowOldLine = row.getOldLine(); /*.replaceAll("~~ ~~", " ")
                                        .replaceAll("## ##", " ");*/
                        
                        String targets[] = StringUtils.substringsBetween(rowOldLine, "~~", "~~");
                        String candidates[] = StringUtils.substringsBetween(rowOldLine, "##", "##");
                        int j = 0;
                        if (targets != null && candidates != null) {
                            for (String target : targets) {
                                try {
                                    String x = candidates[j];
                                } catch (Exception e) {
                                    continue;
                                }
                                target = getTarget(canddiateserTarges.keySet(), target);
                                if ( target == null ){
                                    continue;
                                }
                                if ( candidates[j] == null) {
                                    continue;
                                }
      
                                candidates[j] = getCandidate(getTargets(canddiateserTarges, target),  candidates[j], sentArrays[index + 1]);
                                if ( candidates[j] == null) {
                                    continue;
                                }
                                Par4SimAnswer answer = new Par4SimAnswer(target, candidates[j], 1);
                                if (answers.contains(answer)) {
                                    answer = answers.get(answers.indexOf(answer));
                                    answers.get(answers.indexOf(answer))
                                                    .setCount(answer.getCount() + 1);
                                } else {
                                    answers.add(answer);
                                }
                                j++;
                            }
                        }
                    }

                }
            }
            total = total + answers.size();
            for (Par4SimAnswer answer : answers) {
                System.out.println(answer.getTarget() + "----" + answer.getCandidate() + " "
                                + answer.getCount());
                try {
                AddResult2Db2.addHitCandidateResponsePar4Sim(hitId, answer.getTarget(), answer.getCandidate(), answer.getCount());
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        System.out.println(total);
    }

    private static String getTarget(Set<String> targets, String target) {

        for (String t : targets) {
            if (target.equals(t.toLowerCase())) {
                return target;
            }
        }
        for (String t : targets) {
            if (t.toLowerCase().contains(target)) {
                return t;
            }
        }
        return null;
    }
    
    private static List<String>getTargets(Map<String, List<String>> candidateTargets, String target) {

        for (String t : candidateTargets.keySet()) {
            if (target.toLowerCase().equals(t.toLowerCase())) {
                return candidateTargets.get(t);
            }
        }

        return null;
    }
    private static int getCandidateCount(List<String> candidates, String candidate) {

        for (String t : candidates) {
            if (candidate.equals(t.split("\\|\\|")[0])) {
                return Integer.parseInt(t.split("\\|\\|")[1]);
            }
        }
        return 0;
    }
    
  private static  String getCandidate(List<String> candidates, String candidate, String simpleText) {
        for (String c : candidates) {
            if (candidate.equals(c.toLowerCase())) {
                return candidate;
            }
        }
        for (String c : candidates) {
            if (c.toLowerCase().contains(candidate) && simpleText.contains(c.toLowerCase())) {
                return c;
            }
        }
        return null;
    }

    public static void init() throws PropertyVetoException {
        try {

            ClassLoader classLoader = Par4SimDiffs.class.getClassLoader();
            InputStream in = classLoader.getResourceAsStream("semsch.properties");
            Properties prop = Utils.getConfigs(in);
            System.out.println("DB Initialization ...");
            Par4SemResource.initCwiDB(prop);
            Par4SemResource.initPar4SimDB(prop);

            System.out.println("DB Initialization ... Done");
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException
                        | SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
