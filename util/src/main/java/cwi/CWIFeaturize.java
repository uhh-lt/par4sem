package cwi;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import com.googlecode.jweb1t.JWeb1TSearcher;

import feature.DataLine;
import feature.GF;
import mturk.util.GetParaphrase;

import utils.OpenNLPSegmenter;

public class CWIFeaturize {
	static JWeb1TSearcher web1t;
	public static Word2Vec p2v;
	static Map<String, Integer> wordCounts;
	static Map<String,Integer> dfCounts;
	static String lang = "en";
	
	
    static boolean trainMode = false;
   // static JWeb1TSearcher web1t;

    static GetParaphrase par;
    static Map<Long, List<DataLine>> dataLines;
    static List<String> ranklibResults = new ArrayList<>();
    static int instance = 1;
    
	    //static int wikiCounts =0;
	   public static void init(String w2vModelPath, String web1tFile, String wikiCountFile) throws IOException, PropertyVetoException {
	        p2v = WordVectorSerializer.readWord2VecModel(w2vModelPath);// (output2);
	        dataLines = new LinkedHashMap<>();
	        par = new GetParaphrase();
	        try {
	          //  web1t = new JWeb1TSearcher(new File(web1tFile), 1, 5);
	        } catch (Exception e) {

	        }
	        
	        wordCounts = GF.getWikiWordCt(wikiCountFile);
	        dfCounts = GF.getWikiDocFreq(wikiCountFile + ".df");

	    }
///Users/seidmuhieyimam/data/ph2vecmodel/ennewsmodel.only.vec /Volumes/UUI/LT/research/Semsch/googelngram/web1t /Users/seidmuhieyimam/git/research/semsch/machinelearning/src/main/resources/datasets/cwi/raw/news/News_Test.tsv /Users/seidmuhieyimam/Desktop/tmp/tmp/simplewiki-latest-pages-articles.clean.word.count.txt
	public static void main(String[] args) throws Exception {

		System.out.println(
				"Usage: java -jar genf.jar w2vecmodel web1tpath [train/testfile]dir  outputfolder mode[a=all,...]  ");
		String cwFile = args[2]; // train/test file to featurize

		try {
			if (args[6].equals("en")) {
				lang = "en:";
			} else if (args[6].equals("de")) {
				lang = "de:";
			} else if (args[6].equals("es")) {
				lang = "es:";
			}
		} catch (Exception e) {
			// DO nothing, language is not specified
		}
		System.out.println("language="+lang);
		LineIterator it = new LineIterator(new FileReader(new File(cwFile)));

		init(args[0], args[1], args[3]); // w2v modelpath, web1tdir, wikicount file

		File outDir = new File(args[2]+"_res"); //???
		FileUtils.forceDeleteOnExit(outDir);
		FileUtils.forceMkdir(outDir);

		ThreadLocal<FileOutputStream> os = ThreadLocal.withInitial(new Supplier<FileOutputStream>() {
			@Override
			public FileOutputStream get() {
				try {

					return new FileOutputStream(outDir + "/" + Thread.currentThread().getId() + ".txt");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

		});

		ExecutorService t = Executors.newFixedThreadPool(Integer.valueOf(100));

		while (it.hasNext()) {
			String s = it.next();
			t.execute(new Runnable() {
				@Override
				public void run() {
					try {
						// writeSvmOutputToFile(s, args[4], os);
						
						//IOUtils.write(generateFeatures(lang, s, args[5]) + "\n", os.get(), "UTF8");
					    IOUtils.write(generateFeatures(s) + "\n", os.get(), "UTF8");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		t.shutdown();
		while (!t.isTerminated())
			try {
				Thread.sleep(10L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

    public static String generateFeatures( String lines) throws Exception {
        
        String[] lineSeps = lines.split("\t");
     //   int lineNumber = Integer.parseInt(lineSeps[0]);
        String sent = lineSeps[1];
        String target = lineSeps[4];
       // String candidate = lineSeps[3];
        int index = Integer.valueOf(lineSeps[2]);

        int lable = Integer.valueOf(lineSeps[9]);
        List<Object> featurs = new ArrayList<>();
       
      //  featurs.add(target.replace("\'", "").replace("\"", ""));
        
        double len = (double)(GF.getWordLengthFts(target) );
        featurs.add(len);

        double syl = (double)(GF.getSylableFts(target) );
        featurs.add(syl);

        double syl2 = (double)(GF.getSylableFts2(target) );
        featurs.add(syl2);

        double vowls = (double) (GF.getVowleFts(target) );
        featurs.add(vowls);

        double wordfreq = (double)(GF.getWordFreq2(OpenNLPSegmenter.detectTokens(sent), target));
        featurs.add(wordfreq);

        double worddocsim = (double)(GF.getWordDocumentSimFts(lang, sent, target, p2v) );
        featurs.add(worddocsim);

     
       // featurs.addAll(Phrase2VecTrainTest.getMappedEmbedingVecsFeatures(lang, "w2vTarget",
         //       target.toLowerCase(), p2v));
       // featurs.addAll(Phrase2VecTrainTest.getMappedEmbedingVecsFeatures(lang, "w2vCandidate",
         //       candidate.toLowerCase(), p2v));

        double simWikiWCDFtsF1B = (double)(GF.getSimWikiWCDFtsF1B(target, dfCounts));
        featurs.add(simWikiWCDFtsF1B);

        double simWikiWCFts = (double)(GF.getSimWikiWCFts(target, wordCounts));
        featurs.add(simWikiWCFts);

        double simWikiWCDFtsG1B = (double)(GF.getSimWikiWCDFtsG1B(target, dfCounts));
        featurs.add(simWikiWCDFtsG1B);

        double simWikiWCDFts = (double) (GF.getSimWikiWCDFts(target, dfCounts));
        featurs.add(simWikiWCDFts);

        double cosineFts = (double)(GF.getCosineFtSs(lang,  sent, target, p2v) );
        if(Double.isNaN(cosineFts)) {
            cosineFts = 0.0;
        }
        featurs.add(cosineFts);

   //     GF.getWeb1T2g(sent, target, candidate, web1t, index, featurs);
    //    GF.getWeb1T3g(sent, target, candidate, web1t, index, featurs);
    //    GF.getWeb1T5g(sent, target, candidate, web1t, index, featurs);

      //  featurs.add(par.getPPDB2Score(StringEscapeUtils.escapeSql(target), StringEscapeUtils.escapeSql(candidate)));
      //  featurs.add(par.getSimplePPDB2Score(StringEscapeUtils.escapeSql(target), StringEscapeUtils.escapeSql(candidate)));
     //   featurs.add(par.getTermContextsCount(StringEscapeUtils.escapeSql(target), StringEscapeUtils.escapeSql(candidate)));
        featurs.add( par.getTermContextsScore(StringEscapeUtils.escapeSql(target)));
       
        double wordnetScore = (double) (par.getWNSynonym(target).size());
        featurs.add(wordnetScore);
        featurs.add(lable);
        return StringUtils.join(featurs, ",");
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
