package cwi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mturk.util.Stopwords;
import utils.OpenNLPSegmenter;
import utils.Pairs;

public class CWI {
    
    public static List<AnswerShared> getCandidateCWI( List<AnswerShared> processedAnswers, String sent)
                    throws IOException {

        List<AnswerShared> thisPAnswer = new ArrayList<>();
        int i = 0;
        int begin = 0;
        Pairs pairs = OpenNLPSegmenter.geWordPosPairs(sent);
        for ( String token : pairs.getKeys()) {

            //token = token.substring(0, token.lastIndexOf("-"));
            if (!validPOs4Demo().contains(pairs.getValues().get(i))) {
                i++;
                continue;
            }
            i++;
            if (token.length() < 3) {
                continue;
            }
            if (token.contains("\"") || token.contains("'")) {
                continue;
            }
            if (token.equals("austro")) {
                System.out.println("here");
            }
            int negBegin = sent.indexOf(token, begin - 10);
            if (negBegin < 0) {
                continue;
            }
            int negEnd = negBegin + token.length();
            AnswerShared ansSHared = new AnswerShared(token, negBegin, negEnd);
            if (processedAnswers.contains(ansSHared)) {
                continue;
            }
            if (token.contains(",") || token.contains(":")) {
                continue;
            }
            if (token.contains(",") || token.contains(":")) {
                continue;
            }
            Pattern p = Pattern.compile("[$&+,:();`.„“=?@†#|*/\\[\\]\\\\\"]",
                            Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(token);
            boolean b = m.find();
            if (Stopwords.isStopword(token) || b) {
                continue;
            }

            thisPAnswer.add(ansSHared);
            
            begin = begin + token.length()+1;
      
        }
        return thisPAnswer;

    }
       
    public static List<String> validPOs4Demo() {
        return Arrays.asList(new String[] { "JJ", "JJR", "JJS", "NN", "NNS", "NNP","RB", "VB", "VBD", "VBG", "BN", "VBP", "VBZ" ,"VBN"});
    }

}
