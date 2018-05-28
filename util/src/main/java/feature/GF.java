package feature;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.word2vec.Word2Vec;

import com.googlecode.jweb1t.JWeb1TSearcher;

import info.debatty.java.utils.SparseDoubleVector;
import mturk.util.NgramUtils;
import mturk.util.Phrase2VecTrainTest;
import net.davidashen.text.Hyphenator;
import net.davidashen.text.RuleDefinition;
import net.davidashen.text.Utf8TexParser;
import net.davidashen.text.Utf8TexParser.TexParserException;

public class GF {

    public static String checkToken(int lineNumber, int pos, List<String> tokens, String token,
                    String cToken, String t) {
        if (!token.equals(t) && !(token.contains(t) || t.contains(token))) {
            System.out.println(lineNumber + " pos=" + pos);

            boolean found = false;
            int posPrev = pos - 1;
            System.out.println(lineNumber + " prevpos=" + posPrev);
            cToken = tokens.get(posPrev);
            t = cToken.toString();
            t = t.substring(0, t.lastIndexOf("-"));
            if (token.equals(t) || token.contains(t) || t.contains(token)) {
                found = true;
            }
            int posNext = pos + 1;
            while (!found) {
                cToken = tokens.get(posNext);
                posNext++;
                t = cToken.toString();
                t = t.substring(0, t.lastIndexOf("-"));
                if (token.equals(t) || token.contains(t) || t.contains(token)) {
                    break;
                }
                System.out.println(cToken);

            }
        }
        return cToken;
    }

    public double termFrequency(List<String> doc, String term) {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        return result; // result / doc.size()
    }

    public double documentFrequency(List<List<String>> docs, String term) {
        double n = 0;
        for (List<String> doc : docs) {
            for (String word : doc) {
                if (term.equalsIgnoreCase(word)) {
                    n++;
                    break;
                }
            }
        }
        return n; // Math.log(docs.size() / n);
    }

    public static void getTriGramEmbeddingFts(String sent, String token, int begin,
                    List<String> featurs, Word2Vec p2v) {
        String allBf = sent.substring(0, begin);
        String allAf = sent.substring(begin + token.length());
        String bf = "";
        String af = "";
        if (allBf.split(" ").length == 0) {
            bf = allAf.split(" ")[0];
            af = allAf.split(" ")[1];
        } else if (allAf.split(" ").length == 0) {
            af = allBf.split(" ")[1];
            bf = allBf.split(" ")[0];
        } else {
            bf = allBf.split(" ")[allBf.split(" ").length - 1];
            af = allAf.split(" ")[0];
        }
        featurs.addAll(Phrase2VecTrainTest.getAvgTrigramEmbeddingVecs("avgTriGramWordsEmbedding",
                        token.toLowerCase(), af.toLowerCase(), bf.toLowerCase(), p2v));
    }

    public static void getDocumentAverageEmbeddingsFts(String lang, String sent,
                    List<String> featurs, Word2Vec p2v) {
        featurs.addAll(Phrase2VecTrainTest.getAvgEmbeddingVecs(lang, "documentAvgEmbeddings", sent,
                        p2v));
    }

    public static List<String> setWordDocumentSimFts(String lang, String sent, String token,
                    Word2Vec p2v) {
        List<String> features = new ArrayList<>();
        List<Double> w2vFeatures = Phrase2VecTrainTest.getEmbedingVecs(lang, token.toLowerCase(),
                        p2v);
        List<Double> doc2vecFeaures = Phrase2VecTrainTest.getAvgEmbeddingVecs(lang, sent, p2v);
        features.add("word2VecSimilarity: " + cosineSimilarity(w2vFeatures, doc2vecFeaures));
        return features;
    }

    public static double getWordDocumentSimFts(String lang, String sent, String token,
                    Word2Vec p2v) {
        List<Double> w2vFeatures = Phrase2VecTrainTest.getEmbedingVecs(lang, token.toLowerCase(),
                        p2v);
        List<Double> doc2vecFeaures = Phrase2VecTrainTest.getAvgEmbeddingVecs(lang, sent, p2v);
        return cosineSimilarity(w2vFeatures, doc2vecFeaures);
    }

    public static void ee(String sent, String token, int begin, List<String> featurs,
                    Word2Vec p2v) {
        String allBf = sent.substring(0, begin);
        String allAf = sent.substring(begin + token.length());
        String bf = "";
        String af = "";
        if (allBf.split(" ").length == 0) {
            bf = allAf.split(" ")[0];
            af = allAf.split(" ")[1];
        } else if (allAf.split(" ").length == 0) {
            af = allBf.split(" ")[1];
            bf = allBf.split(" ")[0];
        } else {
            bf = allBf.split(" ")[allBf.split(" ").length - 1];
            af = allAf.split(" ")[0];
        }
        featurs.addAll(Phrase2VecTrainTest.getAvgTrigramEmbeddingVecs("avgTriGramWordsEmbedding",
                        token.toLowerCase(), af.toLowerCase(), bf.toLowerCase(), p2v));
        featurs.addAll(Phrase2VecTrainTest.getEmbedingVecsFeatures("", "wordsEmbedding",
                        token.toLowerCase(), p2v));
    }

    public static void getw2vecTrigFts(String sent, String token, int begin, List<String> featurs,
                    Word2Vec p2v) {
        String allBf = sent.substring(0, begin);
        String allAf = sent.substring(begin + token.length());
        String bf = "";
        String af = "";
        if (allBf.split(" ").length == 0) {
            bf = allAf.split(" ")[0];
            af = allAf.split(" ")[1];
        }

        else if (allAf.split(" ").length == 0) {
            af = allBf.split(" ")[1];
            bf = allBf.split(" ")[0];
        } else {
            bf = allBf.split(" ")[allBf.split(" ").length - 1];
            af = allAf.split(" ")[0];
        }
        featurs.addAll(Phrase2VecTrainTest.getAvgTrigramEmbeddingVecs("avgTriGramWordsEmbedding",
                        token.toLowerCase(), af.toLowerCase(), bf.toLowerCase(), p2v));
    }

    public static void setw2vecFts(String token, List<String> featurs, Word2Vec p2v) {
        featurs.addAll(Phrase2VecTrainTest.getEmbedingVecsFeatures("", "wordsEmbedding",
                        token.toLowerCase(), p2v));
    }

    public static List<String> getjaccardFts(String lang, String sent, String token, Word2Vec p2v) {
        List<Double> w2vFeatures = Phrase2VecTrainTest.getEmbedingVecs(lang, token.toLowerCase(),
                        p2v);
        List<Double> doc2vecFeaures = Phrase2VecTrainTest.getAvgEmbeddingVecs(lang, sent, p2v);
        return getJcardSim(w2vFeatures, doc2vecFeaures);

    }

    public static List<String> getJcardSim(List<Double> w2vFeatures, List<Double> doc2vecFeaures) {
        List<String> features = new ArrayList<>();
        SparseDoubleVector doc = new SparseDoubleVector(
                        doc2vecFeaures.stream().mapToDouble(Double::doubleValue).toArray());
        SparseDoubleVector word = new SparseDoubleVector(
                        w2vFeatures.stream().mapToDouble(Double::doubleValue).toArray());
        features.add("word2docjaccard: " + doc.jaccard(word));
        return features;
    }

    public static List<String> getCosineFts(String lang, String sent, String token, Word2Vec p2v) {

        List<Double> w2vFeatures = Phrase2VecTrainTest.getEmbedingVecs(lang, token.toLowerCase(),
                        p2v);
        List<Double> doc2vecFeaures = Phrase2VecTrainTest.getAvgEmbeddingVecs(lang, sent, p2v);
        getCosineSim(w2vFeatures, doc2vecFeaures);
        return getCosineSim(w2vFeatures, doc2vecFeaures);
    }

    public static List<String> getCosineSim(List<Double> w2vFeatures, List<Double> doc2vecFeaures) {
        List<String> features = new ArrayList<>();
        SparseDoubleVector doc = new SparseDoubleVector(
                        doc2vecFeaures.stream().mapToDouble(Double::doubleValue).toArray());
        SparseDoubleVector word = new SparseDoubleVector(
                        w2vFeatures.stream().mapToDouble(Double::doubleValue).toArray());
        features.add("word2doccosine: " + doc.cosineSimilarity(word));
        return features;
    }

    public static double getCosineFtSs(String lang, String sent, String token, Word2Vec p2v) {

        List<Double> w2vFeatures = Phrase2VecTrainTest.getEmbedingVecs(lang, token.toLowerCase(),
                        p2v);
        List<Double> doc2vecFeaures = Phrase2VecTrainTest.getAvgEmbeddingVecs(lang, sent, p2v);
        return getCosineSims(w2vFeatures, doc2vecFeaures);
    }

    public static double getCosineSims(List<Double> w2vFeatures, List<Double> doc2vecFeaures) {
        SparseDoubleVector doc = new SparseDoubleVector(
                        doc2vecFeaures.stream().mapToDouble(Double::doubleValue).toArray());
        SparseDoubleVector word = new SparseDoubleVector(
                        w2vFeatures.stream().mapToDouble(Double::doubleValue).toArray());
        return doc.cosineSimilarity(word);

    }

    public static List<String> getDotProductFts(String lang, String sent, String token,
                    Word2Vec p2v) {
        List<String> features = new ArrayList<>();
        List<Double> w2vFeatures = Phrase2VecTrainTest.getEmbedingVecs(lang, token.toLowerCase(),
                        p2v);
        List<Double> doc2vecFeaures = Phrase2VecTrainTest.getAvgEmbeddingVecs(lang, sent, p2v);
        SparseDoubleVector doc = new SparseDoubleVector(
                        doc2vecFeaures.stream().mapToDouble(Double::doubleValue).toArray());
        SparseDoubleVector word = new SparseDoubleVector(
                        w2vFeatures.stream().mapToDouble(Double::doubleValue).toArray());
        features.add("word2docdotProduct: " + doc.dotProduct(word));
        return features;
    }

    public static void wfe(String sent, String token, int begin, List<String> featurs,
                    JWeb1TSearcher web1t) throws IOException {
        String bf = sent.substring(0, begin);
        String af = sent.substring(begin + token.length());

        long unigramFreq = NgramUtils.getunigramFreqs(token, web1t);
        long freq2gram = NgramUtils.get2gramFreqs(token, bf, af, web1t);
        long freq3gram = NgramUtils.get3gramFreqs(token, bf, af, web1t);
        long freq5gram = NgramUtils.get5gramFreqs(token, bf, af, web1t);

        double uni = (double) unigramFreq / (unigramFreq + 1 + freq2gram + freq3gram + freq5gram);
        double bi = (double) freq2gram / (unigramFreq + 1 + freq2gram + freq3gram + freq5gram);
        double tri = (double) freq3gram / (unigramFreq + 1 + freq2gram + freq3gram + freq5gram);
        featurs.add("wordlength: " + token.length());
        featurs.add("unigramFreq: " + uni);
        featurs.add("freq2gram: " + bi);
        featurs.add("freq3gram: " + tri);
    }

    public static void setGoogle1gramCountFts(String sent, String token, int begin,
                    List<String> featurs, JWeb1TSearcher web1t) throws IOException {
        String bf = sent.substring(0, begin);
        String af = sent.substring(begin + token.length());

        long unigramFreq = NgramUtils.getunigramFreqs(token, web1t);
        long freq2gram = NgramUtils.get2gramFreqs(token, bf, af, web1t);
        long freq3gram = NgramUtils.get3gramFreqs(token, bf, af, web1t);
        long freq5gram = NgramUtils.get5gramFreqs(token, bf, af, web1t);

        double uni = (double) unigramFreq / (unigramFreq + 1 + freq2gram + freq3gram + freq5gram);
        featurs.add("unigramFreq: " + uni);
    }

    public static void setGooglegramsCountFts(String sent, String token, int begin,
                    List<String> featurs, JWeb1TSearcher web1t) throws IOException {
        String bf = sent.substring(0, begin);
        String af = sent.substring(begin + token.length());

        long unigramFreq = NgramUtils.getunigramFreqs(token, web1t);
        long freq2gram = NgramUtils.get2gramFreqs(token, bf, af, web1t);
        long freq3gram = NgramUtils.get3gramFreqs(token, bf, af, web1t);
        long freq5gram = NgramUtils.get5gramFreqs(token, bf, af, web1t);

        double gram = unigramFreq + freq2gram + freq3gram + freq5gram;
        System.out.println("gram=" + gram);
        featurs.add("web1tgrams: " + gram);

    }

    public static void setGooglegramsCountFts(String sent, String token, String cToken, int pos,
                    List<String> featurs, JWeb1TSearcher web1t) throws IOException {
        try {
            String bf = sent.substring(0, pos);
            String af = sent.substring(pos + token.length());

            long unigramFreq = NgramUtils.getunigramFreqs(token, web1t);
            long freq2gram = NgramUtils.get2gramFreqs(token, bf, af, web1t);
            long freq3gram = NgramUtils.get3gramFreqs(token, bf, af, web1t);
            long freq5gram = NgramUtils.get5gramFreqs(token, bf, af, web1t);

            double gram = unigramFreq + freq2gram + freq3gram + freq5gram;
            featurs.add("web1tgrams: " + gram);
        } catch (Exception e) {
            System.out.println(sent + "\t" + token + "\t" + pos + "\t" + cToken);
            featurs.add("web1tgrams: " + 0.0);
        }

    }

    public static void setGoogle2gramCountFts(String sent, String token, int begin,
                    List<String> featurs, JWeb1TSearcher web1t) throws IOException {
        String bf = sent.substring(0, begin);
        String af = sent.substring(begin + token.length());

        long unigramFreq = NgramUtils.getunigramFreqs(token, web1t);
        long freq2gram = NgramUtils.get2gramFreqs(token, bf, af, web1t);
        long freq3gram = NgramUtils.get3gramFreqs(token, bf, af, web1t);
        long freq5gram = NgramUtils.get5gramFreqs(token, bf, af, web1t);

        double bi = (double) freq2gram / (unigramFreq + 1 + freq2gram + freq3gram + freq5gram);
        featurs.add("freq2gram: " + bi);
    }

    public static void setGoogle3gramCountFts(String sent, String token, int begin,
                    List<String> featurs, JWeb1TSearcher web1t) throws IOException {
        String bf = sent.substring(0, begin);
        String af = sent.substring(begin + token.length());

        long unigramFreq = NgramUtils.getunigramFreqs(token, web1t);
        long freq2gram = NgramUtils.get2gramFreqs(token, bf, af, web1t);
        long freq3gram = NgramUtils.get3gramFreqs(token, bf, af, web1t);
        long freq5gram = NgramUtils.get5gramFreqs(token, bf, af, web1t);

        double tri = (double) freq3gram / (unigramFreq + 1 + freq2gram + freq3gram + freq5gram);
        featurs.add("freq3gram: " + tri);
    }

    public static void na(String sent, String token, int begin, List<String> featurs,
                    JWeb1TSearcher web1t, String pos) throws IOException {
        String bf = sent.substring(0, begin);
        String af = sent.substring(begin + token.length());

        long unigramFreq = NgramUtils.getunigramFreqs(token, web1t);
        long freq2gram = NgramUtils.get2gramFreqs(token, bf, af, web1t);
        long freq3gram = NgramUtils.get3gramFreqs(token, bf, af, web1t);
        long freq5gram = NgramUtils.get5gramFreqs(token, bf, af, web1t);

        double uni = (double) unigramFreq / (unigramFreq + 1 + freq2gram + freq3gram + freq5gram);
        double bi = (double) freq2gram / (unigramFreq + 1 + freq2gram + freq3gram + freq5gram);
        double tri = (double) freq3gram / (unigramFreq + 1 + freq2gram + freq3gram + freq5gram);

        /*
         * StringProviderMXBean strprovider = AbstractStringProvider.connectToServer(
         * "ltcpu3.informatik.uni-hamburg.de", 1098, "enwikinews"); double perp =
         * NgramUtils.getPerplexity(token, bf, af, strprovider); double seqp=
         * NgramUtils.getseqProb(token, bf, af, strprovider);
         */
        featurs.add("partofspeach: " + pos);
        featurs.add("wordlength: " + token.length());
        featurs.add("unigramFreq: " + uni);
        featurs.add("freq2gram: " + bi);
        featurs.add("freq3gram: " + tri);
        /*
         * featurs.add("perp: " + perp); featurs.add("seqp: " + seqp);
         */
        featurs.add("vowls: " + (double) token.toLowerCase().replaceAll("a|e|i|o|u|", "").length()
                        / (token.length()));
        featurs.add("syllablecounter: " + (double) countSyllables(token) / token.length());
    }

    public static void setVowleFts(String token, List<String> featurs) {

        featurs.add("vowls: " + (double) token.toLowerCase()
                        .replaceAll("a|e|i|o|u|ö|ü|ä|í|ú|ó|", "").length() / (token.length()));
    }

    public static double getVowleFts(String token) {

        return (double) token.toLowerCase().replaceAll("a|e|i|o|u|ö|ü|ä|í|ú|ó|", "").length()
                        / (token.length());
    }

    public static void setWordFreq(List<String> tokens, String token, List<String> featurs) {
        Map<String, Integer> wordCounts = new HashMap<>();
        for (String t : tokens) {
            String word = t.toString();
            word = word.substring(0, word.lastIndexOf("-")).replaceAll("[^a-zA-Z0-9\\.\\s]", "");
            wordCounts.put(word.toLowerCase(), wordCounts.getOrDefault(word.toLowerCase(), 0) + 1);
        }
        featurs.add("wordFrequencyHIT: " + wordCounts.getOrDefault(token.toLowerCase(), 1));
    }

    public static void setWordFreq2(List<String> tokens, String token, List<String> featurs) {
        Map<String, Integer> wordCounts = new HashMap<>();
        for (String t : tokens) {
            String word = t.toString();
            // word = word.substring(0,
            // word.lastIndexOf("-")).replaceAll("[^a-zA-Z0-9\\.\\s]", "");
            wordCounts.put(word.toLowerCase(), wordCounts.getOrDefault(word.toLowerCase(), 0) + 1);
        }
        featurs.add("wordFrequencyHIT2: " + wordCounts.getOrDefault(token.toLowerCase(), 1));
    }

    public static double getWordFreq2(String[] tokens, String token) {
        Map<String, Integer> wordCounts = new HashMap<>();
        for (String t : tokens) {
            String word = t.toString();
            wordCounts.put(word.toLowerCase(), wordCounts.getOrDefault(word.toLowerCase(), 0) + 1);
        }
        return wordCounts.getOrDefault(token.toLowerCase(), 1);
    }

    public static void setPOSFts(String pos, List<String> featurs) {
        featurs.add("partofspeach: " + pos);
    }

    public static void setWordLengthFts(String token, List<String> featurs) {
        featurs.add("wordlength: " + token.length());
    }

    public static void setWordLengthFtsMean(String token, List<String> featurs) {
        featurs.add("wordlength: " + ((double) token.length() / 6));
    }

    public static int getWordLengthFts(String token) {
        return token.length();
    }

    public static void setSylableFts(String token, List<String> featurs) {

        featurs.add("syllablecounter: " + (double) countSyllables(token) / token.length());
    }

    public static double getSylableFts(String token) {

        return (double) countSyllables(token) / token.length();
    }

    public static void setSylableFts2(String token, List<String> featurs)
                    throws IOException, TexParserException {

        featurs.add("syllablecounter2: " + (double) countSyllables2(token) / token.length());
    }

    public static double getSylableFts2(String token) throws IOException, TexParserException {

        return (double) countSyllables2(token) / token.length();
    }

    public static int countSyllables(String word) {

        ArrayList<String> tokens = new ArrayList<String>();
        String regexp = "[bcdfghjklmnpqrstvwxz]*[aeiouy]+[bcdfghjklmnpqrstvwxz]*";
        Pattern p = Pattern.compile(regexp);
        Matcher m = p.matcher(word.toLowerCase());

        while (m.find()) {
            tokens.add(m.group());
        }
        // check if e is at last and e is not the only vowel or not
        if (tokens.size() > 1 && tokens.get(tokens.size() - 1).equals("e"))
            return tokens.size() - 1; // e is at last and not the only vowel so
                                      // total syllable -1
        return tokens.size();
    }

    public static int countSyllables2(String word) throws IOException, TexParserException {

        Charset utf8 = Charset.forName("UTF-8");
        Hyphenator hyphenator = new Hyphenator();
        Utf8TexParser parser = new Utf8TexParser();
        File hyph;
        InputStreamReader ruleFileReader;
        try {
            ClassPathResource rsrc = new ClassPathResource("hyph-sv-utf8.tex");
            ruleFileReader = new InputStreamReader(rsrc.getInputStream());

            // hyph =
            // Paths.get(GF.class.getClassLoader().getResource("hyph-sv-utf8.tex").toURI()).toFile();
        } catch (Exception e) {
            ruleFileReader = new InputStreamReader(new FileInputStream("hyph-sv-utf8.tex"), utf8);
        }
        // InputStreamReader ruleFileReader = new InputStreamReader(new
        // FileInputStream("hyph-sv-utf8.tex"), utf8);

        RuleDefinition r = parser.parse(ruleFileReader);
        hyphenator.setRuleSet(r);
        ruleFileReader.close();
        String hyphenedTerm = hyphenator.hyphenate("unbleivble");

        String hyphens[] = hyphenedTerm.split("\u00AD");

        return hyphens.length;
    }

    public static void getSimWikiWCFts(String word, List<String> featurs,
                    Map<String, Integer> wordCounts) {
        featurs.add("simpleWikiCountFeatures: " + wordCounts.getOrDefault(word,
                        wordCounts.getOrDefault(word.toLowerCase(), 0)));
    }

    public static void getSimWikiWCFts(String word, List<String> featurs,
                    Map<String, Integer> wordCounts, int allCount) {
        featurs.add("simpleWikiCountFeatures: " + ((double) wordCounts.getOrDefault(word,
                        wordCounts.getOrDefault(word.toLowerCase(), 0)) / allCount));
    }

    public static int getSimWikiWCFts(String word, Map<String, Integer> wordCounts) {
        return wordCounts.getOrDefault(word, wordCounts.getOrDefault(word.toLowerCase(), 0));
    }

    // document frequency obtimized for F1 Binary features
    public static void getSimWikiWCDFtsF1B(String word, List<String> featurs,
                    Map<String, Integer> wordCounts) {
        Object val = wordCounts.get(word);
        if (val != null && Integer.valueOf(val.toString()) > 551) { // 31/481/788/919 German (1991)
                                                                    // Spanish (20991)
            featurs.add("simpleWikiDocumentF4F1BCountFeatures: 0");
        } else {
            featurs.add("simpleWikiDocumentF4F1BCountFeatures: 1");
        }
    }

    public static int getSimWikiWCDFtsF1B(String word, Map<String, Integer> wordCounts) {
        Object val = wordCounts.get(word);
        if (val != null && Integer.valueOf(val.toString()) > 551) { // 31/481/788/919 German (1991)
                                                                    // Spanish (20991)
            return 0;// featurs.add("simpleWikiDocumentF4F1BCountFeatures: 0");
        } else {
            return 1;// featurs.add("simpleWikiDocumentF4F1BCountFeatures: 1");
        }
    }

    // just the counts
    public static void getSimWikiWCDFts(String word, List<String> featurs,
                    Map<String, Integer> wordCounts) {
        Object val = wordCounts.get(word);
        if (val != null) {
            featurs.add("simpleWikiDFCountFeatures: " + wordCounts.getOrDefault(word,
                            wordCounts.getOrDefault(word.toLowerCase(), 0)));
        } else {
            featurs.add("simpleWikiDFCountFeatures: 0");
        }

    }

    public static int getSimWikiWCDFts(String word, Map<String, Integer> wordCounts) {
        Object val = wordCounts.get(word);
        if (val != null) {
            return wordCounts.getOrDefault(word, wordCounts.getOrDefault(word.toLowerCase(), 0));
        } else {
            return 0;
        }

    }

    // document frequency optimized for G1 Binary features
    public static void getSimWikiWCDFtsG1B(String word, List<String> featurs,
                    Map<String, Integer> wordCounts) {
        Object val = wordCounts.get(word);
        if (val != null && Integer.valueOf(val.toString()) > 551) {// 330/520/788/919 German(11991)
                                                                   // Spanish (29991)
            featurs.add("simpleWikiDocumentF4G1BCountFeatures: 0");
        } else {
            featurs.add("simpleWikiDocumentF4G1BCountFeatures: 1");
        }
    }

    public static int getSimWikiWCDFtsG1B(String word, Map<String, Integer> wordCounts) {
        Object val = wordCounts.get(word);
        if (val != null && Integer.valueOf(val.toString()) > 551) {// 330/520/788/919 German(11991)
                                                                   // Spanish (29991)
            return 0;
        } else {
            return 1;
        }

    }

    public static Map<String, Integer> getWikiWordCt(String countFile)
                    throws IllegalArgumentException, FileNotFoundException {

        Map<String, Integer> wordCounts = new HashMap<>();

        LineIterator it = new LineIterator(new FileReader(new File(countFile)));
        while (it.hasNext()) {
            String line = it.next();
            try {
                wordCounts.put(line.split(",")[0].substring(1),
                                Integer.valueOf(line.split(",")[1].replace(")", "")));
            } catch (Exception e) {
                // who cares
            }
        }
        return wordCounts;
    }

    public static Map<String, Integer> getWikiDocFreq(String countFile)
                    throws IllegalArgumentException, FileNotFoundException {
        Map<String, Integer> wordCounts = new HashMap<>();
        LineIterator it = new LineIterator(new FileReader(new File(countFile)));
        while (it.hasNext()) {
            try {
                String line = it.next();
                String[] lines = line.split("\t");
                if (lines.length != 2)
                    continue;
                wordCounts.put(lines[0], Integer.valueOf(lines[1]));
            } catch (Exception e) {
                // who cares !!!
            }
        }
        return wordCounts;
    }

    public static void getWeb1T2g(String sentence, String target, String candidate,
                    JWeb1TSearcher web1t, int index, List<Feature> featurs) throws IOException {
        long targetFrq2g = 0l;
        long candFrq2g = 0l;

        String before = sentence.substring(0, index);
        String after = sentence.substring(index + target.length());

        long[] freq2gram = NgramUtils.get2gramFreqs(target, candidate, before, after, web1t);
        targetFrq2g = targetFrq2g + freq2gram[0];
        candFrq2g = candFrq2g + freq2gram[1];

        double web1t2fq = ((double) (targetFrq2g + 1) / (candFrq2g + 1));
        if ((candFrq2g + "").equals("Infinity") || (candFrq2g + "").equals("NaN")) {
            web1t2fq = 0.0;
        }

        featurs.add(new Feature().addName("web1t2fq").addValue(web1t2fq));
    }

    public static void getWeb1T3g(String sentence, String target, String candidate,
                    JWeb1TSearcher web1t, int index, List<Feature> featurs) throws IOException {

        long targetFrq3g = 0l;
        long candFrq3g = 0l;

        String before = sentence.substring(0, index);
        String after = sentence.substring(index + target.length());

        long[] freq3gram = NgramUtils.get3gramFreqs(target, candidate, before, after, web1t);
        targetFrq3g = targetFrq3g + freq3gram[0];
        candFrq3g = candFrq3g + freq3gram[1];

        double web1t3fq = ((double) (targetFrq3g + 1) / (candFrq3g + 1));
        if ((web1t3fq + "").equals("Infinity") || (web1t3fq + "").equals("NaN")) {
            web1t3fq = 0.0;
        }
        featurs.add(new Feature().addName("web1t3fq").addValue(web1t3fq));

    }

    public static void getWeb1T5g(String sentence, String target, String candidate,
                    JWeb1TSearcher web1t, int index, List<Feature> featurs) throws IOException {

        long targetFrq5g = 0l;
        long candFrq5g = 0l;

        String before = sentence.substring(0, index);
        String after = sentence.substring(index + target.length());

        long[] freq5gram = NgramUtils.get5gramFreqs(target, candidate, before, after, web1t);
        targetFrq5g = targetFrq5g + freq5gram[0];
        candFrq5g = candFrq5g + freq5gram[1];

        double web1t5fq = ((double) (targetFrq5g + 1) / (candFrq5g + 1));
        if ((candFrq5g + "").equals("Infinity") || (candFrq5g + "").equals("NaN")) {
            web1t5fq = 0.0;
        }
        featurs.add(new Feature().addName("web1t5fq").addValue(web1t5fq));
    }

    public static double cosineSimilarity(List<Double> vectorA, List<Double> vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
            normA += Math.pow(vectorA.get(i), 2);
            normB += Math.pow(vectorB.get(i), 2);
        }
        return dotProduct / ((Math.sqrt(normA) * Math.sqrt(normB)) + 1);
    }

    public static void cosineIndividualVecSimilarity(List<Double> vectorA, List<Double> vectorB,
                    List<String> featurs) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
            normA += Math.pow(vectorA.get(i), 2);
            normB += Math.pow(vectorB.get(i), 2);
            featurs.add("docTermSim" + i + ": "
                            + dotProduct / ((Math.sqrt(normA) * Math.sqrt(normB)) + 1));

        }
    }

    static void addSentenceNumberToFile(String fileName) throws IOException {
        LineIterator it = FileUtils.lineIterator(new File(fileName));
        String tmpFile = fileName + ".sentnum.txt";
        FileOutputStream os = new FileOutputStream(tmpFile);
        int i = 1;
        while (it.hasNext()) {
            String line = it.next();
            IOUtils.write(i + "\t" + line + "\n", os, "UTF8");
            i++;
        }
    }
}
