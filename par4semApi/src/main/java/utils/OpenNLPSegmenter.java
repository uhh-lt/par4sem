package utils;

import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by only2dhir on 11-07-2017.
 */
public class OpenNLPSegmenter {

    static POSTaggerME postTagger;
    static POSModel posModel;
    static SentenceModel sentenceModel;
    static DictionaryLemmatizer lemmatizer;
    static TokenizerModel tokenModel;

    public static void initialize(Properties prop) {
        try {

            InputStream modelStream = new FileInputStream(
                            new File(prop.getProperty("en.pos.maxent.bin")));
            posModel = new POSModel(modelStream);
            postTagger = new POSTaggerME(posModel);
            InputStream is = new FileInputStream(new File(prop.getProperty("en.lemmatizer.dict")));
            lemmatizer = new DictionaryLemmatizer(is);
            InputStream modelIn = new FileInputStream(new File(prop.getProperty("en.sent.bin")));
            sentenceModel = new SentenceModel(modelIn);
            InputStream modelinToken = new FileInputStream(
                            new File(prop.getProperty("en.token.bin")));
            tokenModel = new TokenizerModel(modelinToken);
            modelIn.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Pairs geWordPosPairs(String text) {
        Pairs pairs = new Pairs();
        try {
            if (posModel != null) {
                POSTaggerME tagger = new POSTaggerME(posModel);
                if (tagger != null) {
                    String[] sentences = detectSentences(text);

                    for (String sentence : sentences) {
                        detectTokens(sentence);
                        String tokens[] = detectTokens(sentence);
                        String[] tags = tagger.tag(tokens);
                        for (int i = 0; i < tokens.length; i++) {
                            String word = tokens[i].trim();
                            String tag = tags[i].trim();
                            pairs.getKeys().add(word);
                            pairs.getValues().add(tag);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pairs;
    }

    public static String[] detectSentences(String paragraph) {

        SentenceDetector sentenceDetector = new SentenceDetectorME(sentenceModel);
        String sentences[] = sentenceDetector.sentDetect(paragraph);
        return sentences;
    }

    public static String[] detectTokens(String paragraph) {

        TokenizerME tokenizer = new TokenizerME(tokenModel);
        String[] tokens = tokenizer.tokenize(paragraph);
        return tokens;
    }

    public static void main(String[] args) throws IOException {
        // System.out.println(tagging.tag("I can can the can").getKeys());
        // System.out.println(tagging.tag("I can can the can").getValues());
        System.out.println(lemmatize("I can can the can"));
    }

    public static List<String> lemmatize(String sent) {
        Pairs pairs = geWordPosPairs(sent);
        List<String> lemmatized = new ArrayList<>();
        List<List<String>> lemmas = lemmatizer.lemmatize(pairs.getKeys(), pairs.getValues());
        int i = 0;
        for (List<String> lemma : lemmas) {

            if (lemma.get(0).equals("O")) {
                lemmatized.add((pairs.getKeys().get(i)));
            } else {
                lemmatized.add(lemma.get(0));
            }
            i++;
        }
        return lemmatized;
    }
}
