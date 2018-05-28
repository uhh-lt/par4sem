package rank;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;

import cwi.AddResult2Db2;
import database.Par4SemResource;
import utils.Utils;

public class NDCGScorer {

    static Map<Integer, Double> dcg = new HashMap<>();
    Map<Integer, Double> idcg = new HashMap<>();

    public static void main(String[] args) throws PropertyVetoException, SQLException {
        init();
      //  int iter = 2; 0.666445211944501
        int iter = 2;
        Map<String, String> cwiHitIds = AddResult2Db2.getCWIHitIDs();
        List<Double> ndcgs = new ArrayList<>();
        for (String cwiHitID : cwiHitIds.keySet()) {
            String hitId = cwiHitIds.get(cwiHitID);
            if (AddResult2Db2.getIteration(hitId) !=iter) {
                continue;
            }
            Map<String, List<String>> targetSuggestions = AddResult2Db2
                            .getTargetCandidateSuggestionsRank(cwiHitID);
            
       //     Map<String, List<String>> targetSuggestions = AddResult2Db2 70% NDCG
        //                    .getTargetCandidatesRank(hitId);
            for (String target : targetSuggestions.keySet()) {
                List<Integer> values = new ArrayList<>();
                for (String candidate : targetSuggestions.get(target)) {
                    int val = AddResult2Db2.getTargetCandidateResponsesRank(hitId, StringEscapeUtils.escapeSql(target),
                                    StringEscapeUtils.escapeSql(candidate));
                 //   val = normalizeValue(val);
                    values.add(val);
                }
                double gain = getDCG(values);
                Collections.sort(values, Collections.reverseOrder());
                double maxGain = getIDCG(values);
                if (maxGain == 0.0) {
                    continue;
                }
                double ndcg1 = gain / maxGain;
                ndcgs.add(ndcg1);
            }
        }

        System.out.println(getSums(ndcgs)/ndcgs.size());
    }

    private static int normalizeValue(int val) {
        if (val < 0) {
            val = 0;
        }
        else if (val <2) {
            val = 1;
        }
        else if (val < 4) {
            val = 2;
        }
        else if (val < 6) {
            val = 3;
        }
        else if (val <8) {
            val = 4;
        }
        else {
            val = 5;
        }
        return val;
    }

    private static double getSums (List<Double> ndcgs) {
        double sumndcgs = 0.0;
        for(double ndcg:ndcgs) {
            sumndcgs = sumndcgs + ndcg;
        }
        return sumndcgs;
    }
    private static double getIDCG(List<Integer> values) {
        double maxGain = 0.0;
        for (int i = 0; i < values.size(); i++) {
            maxGain = maxGain + getGain(values.get(i)) * positionDisc(i + 1);
        }
        return maxGain;
    }

    private static double getDCG(List<Integer> values) {
        double gain = 0.0;
        for (int i = 0; i < values.size(); i++) {
            gain = gain + getGain(values.get(i)) * positionDisc(i + 1);
        }
        return gain;
    }

    private static double getGain(int n) {
        return Math.pow(2, n) - 1;
    }

    private static double positionDisc(int n) {
        return 1 / (Math.log(n + 1) / Math.log(2));
    }

    public static void init() throws PropertyVetoException {
        try {

            ClassLoader classLoader = NDCGScorer.class.getClassLoader();
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
