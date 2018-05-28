package mturk.util;

import static utils.Constants.DBLQ;
import static utils.Constants.ERROR_END;
import static utils.Constants.ERROR_LENGTH;
import static utils.Constants.ERROR_START;
import static utils.Constants.FROMX;
import static utils.Constants.FROMY;
import static utils.Constants.MSG;
import static utils.Constants.OFFSET;
import static utils.Constants.REPLACEMENT;
import static utils.Constants.RESULT_END;
import static utils.Constants.RESULT_START;
import static utils.Constants.RULE;
import static utils.Constants.RULEID;
import static utils.Constants.SCORE;
import static utils.Constants.TARGET;
import static utils.Constants.TOX;
import static utils.Constants.TOY;
import static utils.Constants.XML_HEADER;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jobimtext.api.struct.Order2;
import org.jobimtext.api.struct.Sense;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import cwi.AnswerShared;
import cwi.CWIFeaturize;
import database.Par4SemResource;
import database.WordNetSynonym;
import utils.OpenNLPSegmenter;

public class GetParaphrase {

    List<String> commonWords;

    public GetParaphrase() throws FileNotFoundException {
        commonWords = new ArrayList<String>();
    }


    // get list of paraphrases if exist. The word will not have paraphrases
    // candidate if it
    // is popular based on zipf law
    public Set<String> getTokenParaphes(String aBefor, String aAfter, String aWord,
                    boolean aWithSense) throws SQLException, IOException {
        if (commonWords.contains(aWord)) {
            return new HashSet<String>();
        }
        Set<String> synonyms;

        if (aWithSense) {
            synonyms = getMultSenseSynonyms(aWord);
        } else {
            synonyms = getSynonyms(aWord);
        }
        if (synonyms.size() == 0) {
            return new HashSet<String>();
        }

        Map<String, Double> lmResults = new HashMap<String, Double>();
        getLmStat(synonyms, lmResults, aBefor, aAfter);
        // sort syn by lm
        Map<String, Double> sortedMap = sortByLmStat(lmResults, false);

        Set<String> topXSynonyms = new HashSet<String>();

        for (String synonym : sortedMap.keySet()) {
            String lWord = OpenNLPSegmenter.lemmatize(aWord).get(0);
            String lSyn = OpenNLPSegmenter.lemmatize(synonym).get(0);
            if (lWord.equals(lSyn)) {
                continue;
            }
            if (lWord.contains(lSyn) || lSyn.contains(lWord)) {
                continue;
            }
            topXSynonyms.add(lSyn);
            // topXSynonyms.add(synonym);
            if (topXSynonyms.size() >= 5) {
                break;
            }
        }
        synonyms = topXSynonyms;
        return synonyms;
    }

    public Set<String> getMweParaphes(String aBefor, String aAfter, String aWord)
                    throws SQLException {
        Set<String> synonyms = getMweParaphrase(aWord);
        if (synonyms.size() == 0) {
            return new HashSet<String>();
        }

        Map<String, Double> lmResults = new HashMap<String, Double>();
        /*
         * StringProviderMXBean lmServer =
         * AbstractStringProvider.connectToServer("frink.lt.informatik.tu-darmstadt.de",
         * 1098, "testlm");
         */
        getLmStat(synonyms, lmResults, aBefor, aAfter);
        // sort syn by lm
        Map<String, Double> sortedMap = sortByLmStat(lmResults, false);

        Set<String> topXSynonyms = new HashSet<String>();

        for (String synonym : sortedMap.keySet()) {
            String lWord =  OpenNLPSegmenter.lemmatize(aWord).get(0);
            String lSyn = OpenNLPSegmenter.lemmatize(synonym).get(0);
            for (String m : synonym.split(" ")) {
                if (m.replace("\'", "").length() < 2) {
                    continue;
                }
            }

            if (lWord.equals(lSyn)) {
                continue;
            }
            topXSynonyms.add(lSyn);
            // topXSynonyms.add(synonym);
            /*
             * if (topXSynonyms.size() >= 10) { break; }
             */
        }
        synonyms = topXSynonyms;
        return synonyms;
    }

    public JSONArray getSingleParaphrase(String aText, List<Integer> aBeginOffsets,
                    JSONArray jAmatches) throws IOException, SQLException {
        /*
         * BufferedReader bufReader = new BufferedReader(new StringReader(aText));
         * String line = null;
         */
        int fromY = 0;

        int totLine = 0;
        int fromX = 0;
        int offset = 0;
        ClassLoader classLoader = getClass().getClassLoader();
        List<String> stopwords = FileUtils
                        .readLines(new File(classLoader.getResource("stopwords.txt").getFile()));
        for (String sentence : OpenNLPSegmenter.detectSentences(aText)) {
            int inLineFromX = 0;
            int inLineToX = 0;
            StringTokenizer st = new StringTokenizer(sentence, " ");
            while (st.hasMoreTokens()) {
                String word = st.nextToken();

                fromX = totLine + sentence.indexOf(word, inLineToX);
                inLineFromX = sentence.indexOf(word, inLineToX);
                inLineToX = inLineFromX + word.length();
                offset = fromX + fromY;

                if (stopwords.contains(word.toLowerCase())) {
                    continue;
                }
                if (commonWords.contains(word)) {
                    continue;
                }

                Set<String> synonyms = getSynonyms(word);

                if (synonyms.size() == 0) {
                    continue;
                }
                if (aBeginOffsets.contains(offset + 2)) {
                    continue;
                } else {
                    aBeginOffsets.add(offset + 2);
                }
                Map<String, Double> lmResults = new HashMap<String, Double>();

                String before = sentence.substring(0, inLineFromX);
                String after = sentence.substring(inLineToX);
                getLmStat(synonyms, lmResults, before, after);
                // sort syn by lm
                Map<String, Double> sortedMap = sortByLmStat(lmResults, false);
                Set<String> topXSynonyms = new HashSet<String>();

                for (String synonym : sortedMap.keySet()) {
                    // topXSynonyms.add(slem.lemmatize(synonym));
                    topXSynonyms.add(synonym);
                    /*
                     * if (topXSynonyms.size() > 10) { break; }
                     */
                }
                synonyms = topXSynonyms;

                JSONObject objects = getObjects(offset, word.length(), synonyms);
                // if(Par4SemResource.paraphrases)

                jAmatches.put(objects);

            }
            fromY++;
            totLine = totLine + sentence.length();
        }
        return jAmatches;
    }

    public JSONArray getMWEParaphrase(String aText, List<Integer> aBeginOffsets,
                    JSONArray jAmatches) throws IOException, SQLException {
        int fromY = 0;

        int totLine = 0;
        int fromX = 0;
        int offset = 0;
        ClassLoader classLoader = getClass().getClassLoader();
        List<String> stopwords = FileUtils
                        .readLines(new File(classLoader.getResource("stopwords.txt").getFile()));

        String mweFile = FileUtils.readFileToString(
                        new File(classLoader.getResource("mwe/allMwe.txt").getFile()));
        Set<String> mwes = new HashSet<String>();
        for (String mw : mweFile.split("\n")) {
            mwes.add(mw);
        }
        for (String sentence : OpenNLPSegmenter.detectSentences(aText)) {
            int inLineFromX = 0;
            int inLineToX = 0;
            Set<String> mweInSentence = GenerateNgram.generateNgramsUpto(3, sentence);
            for (String word : mweInSentence) {

                if (!mwes.contains(word)) {
                    continue;
                }
                fromX = totLine + sentence.indexOf(word, inLineToX);
                inLineFromX = sentence.indexOf(word, inLineToX);
                inLineToX = inLineFromX + word.length();
                offset = fromX + fromY;

                if (inLineFromX < 0 || inLineToX < 0) {
                    continue;
                }

                if (stopwords.contains(word.toLowerCase())) {
                    continue;
                }
                if (commonWords.contains(word)) {
                    continue;
                }

                String before = sentence.substring(0, inLineFromX);
                String after = sentence.substring(inLineToX);

                Set<String> synonyms = getMweParaphes(before, after, word);

                if (synonyms.size() == 0) {
                    continue;
                }
                if (aBeginOffsets.contains(offset + 2)) {
                    continue;
                } else {
                    aBeginOffsets.add(offset + 2);
                }
                Map<String, Double> lmResults = new HashMap<String, Double>();

                getLmStat(synonyms, lmResults, before, after);
                // sort syn by lm
                Map<String, Double> sortedMap = sortByLmStat(lmResults, false);

                Set<String> topXSynonyms = new HashSet<String>();

                for (String synonym : sortedMap.keySet()) {
                    // topXSynonyms.add(slem.lemmatize(synonym));
                    topXSynonyms.add(synonym);
                    /*
                     * if (topXSynonyms.size() > 10) { break; }
                     */
                }
                synonyms = topXSynonyms;

                JSONObject objects = getObjects(offset, word.length(), synonyms);

                jAmatches.put(objects);

            }
            fromY++;
            totLine = totLine + sentence.length();
        }
        return jAmatches;
    }

    public void getMWEParaphraseHighlight(String word, Map<String, Double> candidates)
                    throws IOException, SQLException {
        for (String synonym : getMweParaphrase(word)) {
            // topXSynonyms.add(slem.lemmatize(synonym));
            candidates.putIfAbsent(synonym, 0.0);
            candidates.put(synonym, candidates.get(synonym) + 1);
        }
    }

    public void getSimilar(String word, Map<String, Double> candidates, int n)
                    throws IOException, SQLException {
        for (String synonym : CWIFeaturize.p2v.wordsNearest(word.trim().replace(" ", "_"), n)) {
            // topXSynonyms.add(slem.lemmatize(synonym));
            candidates.putIfAbsent(synonym, 0.0);
            candidates.put(synonym, candidates.get(synonym) + 1);
        }
    }
    /**
     * When the W2V stuffs are stored in the DB (for par4Sim paper)
     */
    public void getSimilar(String word, Map<String, Double> candidates)
                    throws IOException, SQLException {
        for (String synonym : getW2VecSimDB(word)) {
            // topXSynonyms.add(slem.lemmatize(synonym));
            candidates.putIfAbsent(synonym, 0.0);
            candidates.put(synonym, candidates.get(synonym) + 1);
        }
    }

    public Collection<String> getSimilar(String word, int n) throws IOException, SQLException {

        return CWIFeaturize.p2v.wordsNearest(word.trim().replace(" ", "_"), n);

    }

    public JSONArray getSimplePPDBParaphrase(String aText, List<Integer> aBeginOffsets,
                    JSONArray jAmatches) throws IOException, SQLException {
        int fromY = 0;
        int totLine = 0;
        int fromX = 0;
        int offset = 0;
        ClassLoader classLoader = getClass().getClassLoader();
        List<String> stopwords = FileUtils
                        .readLines(new File(classLoader.getResource("stopwords.txt").getFile()));
        for (String sentence : OpenNLPSegmenter.detectSentences(aText)) {
            int inLineFromX = 0;
            int inLineToX = 0;
            Set<String> mweInSentence = GenerateNgram.generateNgramsUpto(3, sentence);
            for (String word : mweInSentence) {

                fromX = totLine + sentence.indexOf(word, inLineToX);
                inLineFromX = sentence.indexOf(word, inLineToX);
                inLineToX = inLineFromX + word.length();
                offset = fromX + fromY;

                if (inLineFromX < 0 || inLineToX < 0) {
                    continue;
                }
                if (stopwords.contains(word.toLowerCase())) {
                    continue;
                }
                if (commonWords.contains(word)) {
                    continue;
                }

                Set<String> synonyms = getSimplePPDB(word);

                if (synonyms.size() == 0) {
                    continue;
                }
                if (aBeginOffsets.contains(offset + 2)) {
                    continue;
                } else {
                    aBeginOffsets.add(offset + 2);
                }
                Map<String, Double> lmResults = new HashMap<String, Double>();

                String before = sentence.substring(0, inLineFromX);
                String after = sentence.substring(inLineToX);
                getLmStat(synonyms, lmResults, before, after);
                // sort syn by lm
                Map<String, Double> sortedMap = sortByLmStat(lmResults, false);

                Set<String> topXSynonyms = new HashSet<String>();

                for (String synonym : sortedMap.keySet()) {
                    // topXSynonyms.add(slem.lemmatize(synonym));
                    topXSynonyms.add(synonym);
                    /*
                     * if (topXSynonyms.size() > 10) { break; }
                     */
                }
                synonyms = topXSynonyms;

                JSONObject objects = getObjects(offset, word.length(), synonyms);

                jAmatches.put(objects);

            }
            fromY++;
            totLine = totLine + sentence.length();
        }
        return jAmatches;
    }

    public JSONArray getSimplePPDBParaphraseCwi(String aText, List<AnswerShared> topNCwis,
                    JSONArray jAmatches) throws IOException, SQLException {
        int fromY = 0;
        int totLine = 0;
        int fromX = 0;
        int offset = 0;

        for (String sentence : aText.split("\n")) {
            int inLineFromX = 0;
            int inLineToX = 0;
            // if in this sentence
            for (AnswerShared topCwi : topNCwis) {

                // int begin = sentence.indexOf(topCwi.getAnswer(), topCwi.getBegin() - 10);
                // int end = begin + topCwi.getAnswer().length();
                String word = topCwi.getAnswer();

                fromX = totLine + sentence.indexOf(word, inLineToX);
                inLineFromX = sentence.indexOf(word, inLineToX);
                inLineToX = inLineFromX + word.length();
                offset = fromX + fromY;

                if (inLineFromX < 0 || inLineToX < 0) {
                    continue;
                }

                Set<String> synonyms = getSimplePPDB(word);
                if (synonyms.size() < 1) {
                    continue;
                }

                Map<String, Double> lmResults = new HashMap<String, Double>();

                String before = sentence.substring(0, inLineFromX);
                String after = sentence.substring(inLineToX);
                getLmStat(synonyms, lmResults, before, after);
                // sort syn by lm
                Map<String, Double> sortedMap = sortByLmStat(lmResults, false);

                Set<String> topXSynonyms = new HashSet<String>();

                for (String synonym : sortedMap.keySet()) {
                    // topXSynonyms.add(slem.lemmatize(synonym));
                    topXSynonyms.add(synonym);
                    /*
                     * if (topXSynonyms.size() > 10) { break; }
                     */
                }
                synonyms = topXSynonyms;

                JSONObject objects = getObjectsCwi(offset, word.length(), synonyms);

                jAmatches.put(objects);

            }
            fromY++;
            totLine = totLine + sentence.length();
        }
        return jAmatches;
    }

    public void getSimplePPDBParaphraseCwiHighlight(String word, Map<String, Double> candidates)
                    throws IOException, SQLException {

        for (String synonym : getSimplePPDB(word)) {
            candidates.putIfAbsent(synonym, 0.0);
            candidates.put(synonym, candidates.get(synonym) + 1);
        }
    }

    public JSONArray getSingleParaphraseCwi(String aText, List<AnswerShared> topNCwis,
                    JSONArray jAmatches) throws IOException, SQLException {
        int fromY = 0;
        int totLine = 0;
        int fromX = 0;
        int offset = 0;

        for (String sentence : aText.split("\n")) {
            int inLineFromX = 0;
            int inLineToX = 0;
            // if in this sentence
            for (AnswerShared topCwi : topNCwis) {

                // int begin = sentence.indexOf(topCwi.getAnswer(), topCwi.getBegin() - 10);
                // int end = begin + topCwi.getAnswer().length();
                String word = topCwi.getAnswer();

                fromX = totLine + sentence.indexOf(word, inLineToX);
                inLineFromX = sentence.indexOf(word, inLineToX);
                inLineToX = inLineFromX + word.length();
                offset = fromX + fromY;

                if (inLineFromX < 0 || inLineToX < 0) {
                    continue;
                }

                // if(begin <0){
                // continue;
                // }

                Set<String> synonyms = getSynonyms(word);

                if (synonyms.size() < 1) {
                    continue;
                }

                Map<String, Double> lmResults = new HashMap<String, Double>();

                String before = sentence.substring(0, inLineFromX);
                String after = sentence.substring(inLineToX);
                getLmStat(synonyms, lmResults, before, after);
                // sort syn by lm
                Map<String, Double> sortedMap = sortByLmStat(lmResults, false);

                Set<String> topXSynonyms = new HashSet<String>();

                for (String synonym : sortedMap.keySet()) {
                    // topXSynonyms.add(slem.lemmatize(synonym));
                    topXSynonyms.add(synonym);
                    /*
                     * if (topXSynonyms.size() > 10) { break; }
                     */
                }
                synonyms = topXSynonyms;

                JSONObject objects = getObjects(offset, word.length(), synonyms);

                jAmatches.put(objects);
            }
            fromY++;
            totLine = totLine + sentence.length();
        }
        return jAmatches;
    }

    public void getSingleParaphraseCwiHighlight(String word, Map<String, Double> candidates)
                    throws IOException, SQLException {
        for (String synonym : getSynonyms(word)) {
            // topXSynonyms.add(slem.lemmatize(synonym));
            candidates.putIfAbsent(synonym, 0.0);
            candidates.put(synonym, candidates.get(synonym) + 1);
        }

    }

    public static JSONObject getObjects(int offset, int len, Set<String> synonyms) {

        JSONObject objects = new JSONObject();
        objects.put("message", "Select candidate Paraphrase");
        objects.put("offset", offset + 2);
        objects.put("length", len);

        JSONArray repArr = new JSONArray();
        for (String rep : synonyms) {
            JSONObject r = new JSONObject();
            r.put("value", rep);
            repArr.put(r);
        }
        objects.put("replacements", repArr);

        JSONObject rulObj = new JSONObject();
        rulObj.put("id", "SEMANTIC_RULE");
        rulObj.put("description", "PLACEHOLDERDESCRIPTION");
        rulObj.put("issueType", "PLACEHOLDERISSUETYPE");
        JSONObject rulCatObj = new JSONObject();
        rulCatObj.put("id", "PLACEHOLDERID");
        rulCatObj.put("name", "PLACEHOLDERNAME");
        rulObj.put("category", rulCatObj);

        return objects.put("rule", rulObj);
    }

    public static JSONObject getObjectsCwi(int offset, int len, Set<String> synonyms) {

        JSONObject objects = new JSONObject();
        objects.put("message", "Select candidate Paraphrase");
        objects.put("offset", offset);
        objects.put("length", len);

        JSONArray repArr = new JSONArray();
        for (String rep : synonyms) {
            JSONObject r = new JSONObject();
            r.put("value", rep);
            repArr.put(r);
        }
        objects.put("replacements", repArr);

        JSONObject rulObj = new JSONObject();
        rulObj.put("id", "SEMANTIC_RULE");
        rulObj.put("description", "PLACEHOLDERDESCRIPTION");
        rulObj.put("issueType", "PLACEHOLDERISSUETYPE");
        JSONObject rulCatObj = new JSONObject();
        rulCatObj.put("id", "PLACEHOLDERID");
        rulCatObj.put("name", "PLACEHOLDERNAME");
        rulObj.put("category", rulCatObj);

        return objects.put("rule", rulObj);
    }

    public String getXml(String aText) throws IOException, SQLException {
        StringBuffer errorAsXml = new StringBuffer();
        errorAsXml.append(XML_HEADER);
        errorAsXml.append(RESULT_START);
        BufferedReader bufReader = new BufferedReader(new StringReader(aText));
        String line = null;
        int fromY = 0;

        int totLine = 0;
        int fromX = 0;
        int toX = 0;
        int offset = 0;
        while ((line = bufReader.readLine()) != null) {
            int inLineFromX = 0;
            int inLineToX = 0;
            StringTokenizer st = new StringTokenizer(line, " ");
            while (st.hasMoreTokens()) {
                String word = st.nextToken();

                fromX = totLine + line.indexOf(word, inLineToX);
                inLineFromX = line.indexOf(word, inLineToX);
                inLineToX = inLineFromX + word.length();
                toX = fromX + word.length();
                offset = fromX + fromY;

                ClassLoader classLoader = getClass().getClassLoader();
                List<String> stopwords = FileUtils.readLines(
                                new File(classLoader.getResource("stopwords.txt").getFile()));
                if (stopwords.contains(word.toLowerCase())) {
                    continue;
                }
                if (!commonWords.contains(word)) {
                    continue;
                }

                Set<String> synonyms = getSynonyms(word);
                if (synonyms.size() == 0) {
                    continue;
                }

                Map<String, Double> lmResults = new HashMap<String, Double>();

                String before = line.substring(0, inLineFromX);
                String after = line.substring(inLineToX);

                /*
                 * StringProviderMXBean lmServer = AbstractStringProvider
                 * .connectToServer("frink.lt.informatik.tu-darmstadt.de", 1098, "testlm");
                 */
                getLmStat(synonyms, lmResults, before, after);
                // sort syn by lm
                Map<String, Double> sortedMap = sortByLmStat(lmResults, false);

                Set<String> topXSynonyms = new HashSet<String>();

                for (String synonym : sortedMap.keySet()) {
                    // topXSynonyms.add(slem.lemmatize(synonym));
                    topXSynonyms.add(synonym);
                    if (topXSynonyms.size() > 10) {
                        break;
                    }
                }
                synonyms = topXSynonyms;

                // provide all suggestions (for the start)

                String replacements = "\"" + StringUtils.join(synonyms, "#").replace("\'", "")
                                + "\"";
                errorAsXml.append(ERROR_START);
                errorAsXml.append(FROMY + DBLQ + fromY + DBLQ + FROMX + DBLQ + fromX + DBLQ + TOY
                                + DBLQ + fromY + DBLQ + TOX + DBLQ + toX + DBLQ + RULEID + RULE
                                + MSG + RULE + REPLACEMENT + replacements + OFFSET + DBLQ + offset
                                + DBLQ + ERROR_LENGTH + DBLQ + word.length() + DBLQ);
                errorAsXml.append(ERROR_END);
            }
            fromY++;
            totLine = totLine + line.length();
        }
        errorAsXml.append(RESULT_END);
        return errorAsXml.toString();
    }


    public List<List<List<String>>> getParaphrasedText(String aCandidateText, String aReference,
                    int aNumberOfSuggestions) throws IOException, SQLException {
        List<List<List<String>>> sentences = new ArrayList<List<List<String>>>();

        BufferedReader candidateBufReader = new BufferedReader(new StringReader(aCandidateText));
        String line = null;
        StringTokenizer reftTexts = new StringTokenizer(aReference, "\n");

        int totLine = 0;
        /*
         * StringProviderMXBean lmServer =
         * AbstractStringProvider.connectToServer("frink.lt.informatik.tu-darmstadt.de",
         * 1098, "testlm");
         */

        while ((line = candidateBufReader.readLine()) != null) {
            int inLineFromX = 0;
            int inLineToX = 0;
            StringTokenizer st = new StringTokenizer(line, " ");
            List<String> refTokens = Arrays.asList(reftTexts.nextToken().trim().split("\\s+"));
            List<List<String>> sentenceWords = new ArrayList<List<String>>();

            while (st.hasMoreTokens()) {
                List<String> paraphrases = new ArrayList<String>();
                String word = st.nextToken();

                inLineFromX = line.indexOf(word, inLineToX);
                inLineToX = inLineFromX + word.length();

                ClassLoader classLoader = getClass().getClassLoader();
                List<String> stopwords = FileUtils.readLines(
                                new File(classLoader.getResource("stopwords.txt").getFile()));
                if (stopwords.contains(word.toLowerCase())
                                || refTokens.contains(word.toLowerCase())) {
                    paraphrases.add(word);
                    sentenceWords.add(paraphrases);
                    continue;
                }

                Set<String> synonyms = getSynonyms(word);

                Map<String, Double> lmResults = new HashMap<String, Double>();
                if (synonyms.size() == 0) {
                    paraphrases.add(word);
                    sentenceWords.add(paraphrases);
                    continue;
                }
                String before = line.substring(0, inLineFromX);
                String after = line.substring(inLineToX);

                getLmStat(synonyms, lmResults, before, after);
                // sort syn by lm
                Map<String, Double> sortedMap = sortByLmStat(lmResults, false);
                // assume the word at position one to be the best paraphrase
                int numIt = aNumberOfSuggestions;
                for (String paraphrase : sortedMap.keySet()) {
                    paraphrases.add(paraphrase);
                    numIt--;
                    if (numIt == 0) {
                        break;
                    }
                }
                sentenceWords.add(paraphrases);
            }

            sentences.add(sentenceWords);
            totLine = totLine + line.length();
        }
        return sentences;
    }

    private void getLmStat(Set<String> synonyms, Map<String, Double> lmResults, String before,
                    String after) {
        // for every synonym, calculate the lm (Map(syn, lm value)
        for (String synonym : synonyms) {
            try {
                StringBuffer sent = new StringBuffer();
                sent.append(before + " " + synonym + " " + after);
                lmResults.put(synonym, Par4SemResource.lmServer.getSequenceLog10Probability(
                                StringUtils.normalizeSpace(sent.toString())));
                // lmResults.put(synonym, Par4SemResource.lmServer.getPerplexity(before + " " +
                // synonym + " " + after,false));
            } catch (Exception e) {
                lmResults.put(synonym, 0d);
                // continue;
            }
        }
    }

    public double getLMPerplexity(String synonym, String before, String after) {

        try {
            // return lmServer.getPerplexity(before + " " + synonym + " " + after);
            return Par4SemResource.lmServer.getPerplexity(before + " " + synonym + " " + after);
        } catch (Exception e) {
            return 0.0;
        }

    }

    public double getLMSeqLog10Prob(String synonym, String before, String after) {

        try {
            return Par4SemResource.lmServer
                            .getSequenceLog10Probability(before + " " + synonym + " " + after);
        } catch (Exception e) {
            return 0.0;
        }

    }

    /**
     * for the given word, get similar words fom ppdb and JobimText. Consider those
     * found in both resources.
     *
     * @param aWord
     * @return
     * @throws SQLException
     */
    private Set<String> getSynonyms(String aWord) throws SQLException {
        aWord = aWord.replace("\'", "").replace("\"", "").toLowerCase();

        // get synonyms from WordNet
        Set<String> wordNetSynonyms = Par4SemResource.wordNetSynonyms.getSynonymWords(aWord);
        // get synonyms from ppdb
        Set<String> ppdbParaphrases = getPPDBTargets(
                        aWord.replace("\'", "").replace("\"", "").toLowerCase());
        // get smilar words from jobimtext
        List<Order2> sim = Par4SemResource.dt.getSimilarTerms(aWord);
        Set<String> jobimTextSimilarTerms = new LinkedHashSet<String>();

        for (Order2 order2 : sim) {
            String target = order2.key.toLowerCase();
            if (target.length() > 2 && !(target.equals(aWord))) {
                jobimTextSimilarTerms.add(target.trim());
            }
        }
        List<String> paraphrase = new ArrayList<String>(wordNetSynonyms);
        if (wordNetSynonyms.size() > 50) {
            paraphrase = new ArrayList<String>(wordNetSynonyms).subList(0, 49);
        }
        if (jobimTextSimilarTerms.size() > 50) {
            paraphrase.addAll(new ArrayList<String>(jobimTextSimilarTerms).subList(0, 49));
        } else {
            paraphrase.addAll(new ArrayList<String>(jobimTextSimilarTerms));
        }
        if (ppdbParaphrases.size() > 50) {
            paraphrase.addAll(new ArrayList<String>(ppdbParaphrases).subList(0, 49));
        } else {
            paraphrase.addAll(new ArrayList<String>(ppdbParaphrases));
        }
        paraphrase.remove(aWord);

        return new LinkedHashSet<String>(paraphrase);
        // aWord = aWord.replace("\'", "").replace("\"", "").toLowerCase();
        //
        // // get synonyms from WordNet
        // Set<String> wordNetSynonyms = WordNetSynonyms.getSynonymWords(aWord);
        // // get synonyms from ppdb
        // Set<String> ppdbParaphrases = getSynonymWordsFromPPDB(
        // aWord.replace("\'", "").replace("\"", "").toLowerCase());
        // // get smilar words from jobimtext
        // List<Order2> sim = dt.getSimilarTerms(aWord);
        // Set<String> jobimTextSimilarTerms = new HashSet<String>();
        //
        // for (Order2 order2 : sim) {
        // String target = order2.key.toLowerCase();
        // if (target.length() > 2 && !(target.equals(aWord))) {
        // jobimTextSimilarTerms.add(target);
        // }
        // }
        //
        // // intersection of similar/synonymous words in at least two resources
        // Set<String> ppdbAndJobim = new HashSet<String>();
        // Set<String> temp = new HashSet<String>();
        //
        // // ppdb and jobim intersections
        // temp.addAll(ppdbParaphrases);
        // ppdbParaphrases.retainAll(jobimTextSimilarTerms);
        // ppdbAndJobim.addAll(ppdbParaphrases);
        //
        // // ppdb and wordnet intersections
        // Set<String> ppdbAndWordNet = new HashSet<String>();
        // temp.retainAll(wordNetSynonyms);
        // ppdbAndWordNet.addAll(temp);
        //
        // // wordnet and jbim intersections
        // wordNetSynonyms.retainAll(jobimTextSimilarTerms);
        //
        // // combine agian
        // wordNetSynonyms.addAll(ppdbAndWordNet);
        // wordNetSynonyms.addAll(ppdbAndJobim);
        //
        // return wordNetSynonyms;
    }

    private Set<String> getMultSenseSynonyms(String aWord) throws SQLException, IOException {
        Set<String> multiSenseCand = new LinkedHashSet<>();
        aWord = aWord.replace("\'", "").replace("\"", "").toLowerCase();
        List<List<String>> sensesList = getDTSenses(aWord);
        if (sensesList.size() < 2) {
            return new HashSet<>();
        }

        String lWord = OpenNLPSegmenter.lemmatize(aWord).get(0);
        if (lWord.length() < 4) {
            return new HashSet<>();
        }
        int check = 0;

        Set<String> ppdb2TargetsWith = new HashSet<>();
        for (String ppdbEntry : getPPDB2TargetsWithScores(aWord, 1000).keySet()) {
            ppdb2TargetsWith.add(OpenNLPSegmenter.lemmatize(ppdbEntry.toLowerCase().trim()).get(0));
        }
        Set<String> nonPPDB = new LinkedHashSet<>();
        out: while (true) {
            boolean exist = false;
            for (List<String> snl : sensesList) {
                if (snl.size() > 0) {
                    exist = true;
                }
            }
            if (!exist) {
                break out;
            }
            // iterate and remove the first every time
            if (check > 0) {
                check = 0;
                for (int j = 0; j < sensesList.size(); j++) {
                    if (sensesList.get(j).size() > 0) {
                        sensesList.get(j).remove(check);
                    }
                }
            }
            // get the first sense for each sense group
            inner: for (int j = 0; j < sensesList.size(); j++) {
                if (multiSenseCand.size() > 4) {
                    break out;
                }
                if (sensesList.get(j).size() > check) {
                    String lSyn = OpenNLPSegmenter.lemmatize(sensesList.get(j).get(check)).get(0);
                    if (lSyn.length() < 3) {
                        continue;
                    }
                    if (lWord.equals(lSyn)) {
                        continue;
                    }
                    if (lWord.contains(lSyn) || lSyn.contains(lWord)) {
                        continue;
                    }
                    for (String entry : multiSenseCand) {
                        if (OpenNLPSegmenter.lemmatize(entry).get(0).equals(lSyn)) {
                            continue inner;
                        }
                    }
                    // we need five candidates anyways. Store them for later
                    // add-upper
                    if (!ppdb2TargetsWith.contains(lSyn)) {
                        nonPPDB.add(sensesList.get(j).get(check));
                        continue;
                    }
                    multiSenseCand.add(sensesList.get(j).get(check));
                    check++;
                }
            }
            check++; // hence if inner loop do not add a value
        }

        // multiSenseCand.retainAll(getPPDB2TargetsWithScores(aWord,
        // 1000).keySet());
        if (multiSenseCand.size() < 5) {
            for (String reserve : nonPPDB) {
                if (multiSenseCand.size() == 5) {
                    break;
                } else {
                    multiSenseCand.add(reserve);
                }
            }
        }
        return multiSenseCand;
    }

    private Set<String> getMweParaphrase(String aWord) throws SQLException {
        aWord = aWord.replace("\'", "").replace("\"", "").toLowerCase();

        // get synonyms from WordNet
        Set<String> wordNetSynonyms = Par4SemResource.wordNetSynonyms.getSynonymWords(aWord);
        // get synonyms from ppdb
        Set<String> ppdbParaphrases = getPPDBTargets(
                        aWord.replace("\'", "").replace("\"", "").toLowerCase());
        // get smilar words from jobimtext
        List<Order2> sim = Par4SemResource.dt.getSimilarTerms(aWord);
        Set<String> jobimTextSimilarTerms = new HashSet<String>();

        for (Order2 order2 : sim) {
            String target = order2.key.toLowerCase();
            if (target.length() > 2 && !(target.equals(aWord))) {
                jobimTextSimilarTerms.add(target.trim());
            }
        }
        wordNetSynonyms.addAll(jobimTextSimilarTerms);
        wordNetSynonyms.addAll(ppdbParaphrases);
        wordNetSynonyms.remove(aWord);

        return wordNetSynonyms;
    }

    public List<List<String>> getDTSenses(String aWord) throws SQLException {
        aWord = aWord.replace("\'", "").replace("\"", "");// .toLowerCase();

        // get synonyms from WordNet
        List<List<String>> senses = new ArrayList<>();
        List<Sense> dtSenses = Par4SemResource.dt.getSenses(aWord);
        for (Sense sens : dtSenses) {
            senses.add(sens.getSenses());

        }

        return senses;
    }

    public Set<String> getSimplePPDB(String target) throws SQLException {

        ResultSet res = Par4SemResource.simpleppdbConn.createStatement()
                        .executeQuery("Select outputPhrase from ppdb where inputPhrase = '"
                                        + StringEscapeUtils.escapeSql(target) + "';");
        Set<String> simples = new HashSet<>();
        while (res.next()) {
            simples.add(res.getString("outputPhrase").trim());
        }
        return simples;
    }

    public Set<String> getW2VecSimDB(String target) throws SQLException {

        ResultSet res = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery("Select candidates from w2vec where word = '" + target + "';");
        Set<String> simples = new HashSet<>();
        JsonArray cands = new JsonArray();
       
        while (res.next()) {       
            JsonParser parser = new JsonParser();
            JsonElement elements = parser.parse(res.getString("candidates").trim());
            cands  = elements.getAsJsonArray();
        }
       for (int i=0;i<cands.size();i++) {
           simples.add(cands.get(i).getAsString());
        }
        return simples;
    }
    
    public   Map<String, Double> getCandidateFromDBSuggestion(String target, String cwiHitID) throws SQLException {

        ResultSet res = Par4SemResource.par4SimConn.createStatement()
                        .executeQuery("Select candidate, candidate_rank from candidatesuggestion where target = '" + target + "' AND  cwiHitID='"+cwiHitID+"';");
        Map<String, Double> candidates = new HashMap<>();
        
        while (res.next()) {       
        
            candidates.put(res.getString("candidate"), res.getDouble("candidate_rank"));
        }
        return candidates;
    }
    
    public int getTermContextsCount(String aWord, String aCandidate) throws SQLException {
        aWord = aWord.replace("\'", "").replace("\"", "");
        List<List<String>> senses1 = getDTSenses(aWord);
        List<List<String>> senses2 = getDTSenses(aCandidate);
        int numSenses = 0;
        for (int i = 0; i < senses1.size(); i++) {
            for (int j = 0; j < senses2.size(); j++) {
                List<String> s = senses1.get(i);
                s.retainAll(senses2.get(j));
                numSenses = numSenses + s.size();
            }
        }

        return numSenses;
    }

    public Long getTermContextsScore(String aCandidate) throws SQLException {
        return Par4SemResource.dt.getTermCount(aCandidate);
    }

    public List<String> getDTSimilarities(String aWord) throws SQLException {
        aWord = aWord.replace("\'", "").replace("\"", "").toLowerCase();

        List<Order2> sim = Par4SemResource.dt.getSimilarTerms(aWord);
        List<String> simWords = new ArrayList<>();
        for (Order2 order2 : sim) {
            String target = order2.key.toLowerCase();
            simWords.add(target);
        }

        return simWords;
    }

    public Set<String> getPPDBTargets(String aWord) throws SQLException {
        Set<String> similarWords = new LinkedHashSet<String>();

        String query = "SELECT DISTINCT target FROM ppdb where source =\'" + aWord + "\' limit 20;";
        ResultSet rs = Par4SemResource.ppdb2Conn.createStatement().executeQuery(query);
        while (rs.next()) {
            String target = rs.getString(TARGET).replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
            if (target.length() > 2 && !(target.equals(aWord))) {
                similarWords.add(target.trim());
            }
        }
        return similarWords;
    }

    public Map<String, Double> getPPDB2TargetsWithScores(String aWord, int aLimit)
                    throws SQLException {
        Map<String, Double> witScores = new LinkedHashMap<>();
        String query = "SELECT DISTINCT * FROM ppdb where source =\'" + aWord
                        + "\'  order by ppdb2score desc limit " + aLimit + ";";
        ResultSet rs = Par4SemResource.ppdb2Conn.createStatement().executeQuery(query);
        while (rs.next()) {
            String target = rs.getString(TARGET).replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase()
                            .trim();
            if (target.length() > 2 && !(target.equals(aWord))) {
                if (witScores.get(target) == null) {
                    witScores.put(target, rs.getDouble(SCORE));
                }
            }
        }
        return witScores;
    }

    public Double getPPDB2Score(String aWord, String aTraget) throws SQLException {
        String query = "SELECT " + SCORE + " FROM ppdb WHERE source =\'" + aWord
                        + "\'  AND target=\'" + aTraget + "\'";
        ResultSet rs = Par4SemResource.ppdb2Conn.createStatement().executeQuery(query);
        if (rs.next()) {
            return rs.getDouble(SCORE);
        }
        return 0.0;
    }

    public Double getPPDB2Score(String aWord) throws SQLException {
        String target = aWord.split("\t")[1].replace("\'", "").replace("\"", "");
        String source = aWord.split("\t")[0].replace("\'", "").replace("\"", "");

        String query = "SELECT " + SCORE + " FROM ppdb WHERE source =\'" + source
                        + "\'  AND target=\'" + target + "\'";

        try {
            ResultSet rs = Par4SemResource.ppdb2Conn.createStatement().executeQuery(query);
            if (rs.next()) {
                return rs.getDouble(SCORE);
            }
        } catch (Exception e) {
            return 0.0;
        }

        return 0.0;
    }

    // from:
    // http://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
    public static Map<String, Double> sortByLmStat(Map<String, Double> unsortMap, boolean order) {
        List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(
                        unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Double>>() {
            @Override
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public Set<String> getMWEFromWN() throws SQLException {

        return Par4SemResource.wordNetSynonyms.getMWEFromWN();
    }

    public Set<String> getWNSynonym(String aWord)
                    throws SQLException
                {
                    aWord = aWord.replace("\'", "").replace("\"", "").toLowerCase();

                    return WordNetSynonym.getSynonymWords(aWord);


                }
}
