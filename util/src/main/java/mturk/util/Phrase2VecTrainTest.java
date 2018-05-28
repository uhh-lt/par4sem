package mturk.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.deeplearning4j.models.word2vec.Word2Vec;

import feature.Feature;

public class Phrase2VecTrainTest {
	public static void main(String[] args) throws Exception {
		String filePath = args[0];
		String output2 = args[2];

		LineIterator mweIt = FileUtils.lineIterator(new File(args[1]));

		Set<String> mwes = new HashSet<>();
		while (mweIt.hasNext()) {
			mwes.add(mweIt.next());
		}

		//Phrase2VecTrainer.trainPhrase2Vec(filePath, mwes, output2);

		// System.out.println("reading posModel");

		/*
		 * System.out.println("reading posModel ..."); Word2Vec p2v =
		 * WordVectorSerializer.readWord2VecModel(
		 * "/Users/seidmuhieyimam/data/ph2vecmodel/ennewsmodel.only.vec");//(
		 * output2); System.out.println("reading posModel ... done");
		 * 
		 * System.out.
		 * println("Type a word or multi-word(up to tri-gram) to get the ten most similar word/s. Type q to quite"
		 * ); Scanner keyboard = new Scanner(System.in); String n = "";
		 * while(!n.equals("q")){ n = new Scanner(System.in).nextLine();
		 * System.out.println(p2v.getLayerSize()); Collection<String> lst =
		 * p2v.wordsNearest(n.trim().replace(" ", "_"), 10);
		 * System.out.println("10 Words closest to '"+n +" :" + lst);
		 * System.out.
		 * println("Type a word or multi-word(up to tri-gram) to get the ten most similar word/s. Type q to quite:"
		 * );
		 * 
		 * List<Double> vecs = new ArrayList<>(); for(int i =0;
		 * i<p2v.getLayerSize();i++){ vecs.add(p2v.getWordVector(n)[i]);
		 * double[] x = p2v.getWordVector(n); }
		 * System.out.println(StringUtils.join(vecs," "));
		 * //System.out.println(p2v.getWordVectorMatrixNormalized(n)); //
		 * System.out.println(p2v.similarWordsInVocabTo(n, 0.5));
		 * //System.out.println(p2v.wordsNearestSum(n, 10));
		 * System.out.println(p2v.wordsNearest(new ArrayList<>(Arrays.asList(new
		 * String[]{"girl"})),new ArrayList<>(Arrays.asList(new
		 * String[]{"boy"})), 10)); }
		 * 
		 * 
		 * 
		 * keyboard.close();
		 */
	}

	public static List<String> getEmbedingVecsFeatures(String lang, String label, String word, Word2Vec p2v) {
		List<String> w2vFeatures = new ArrayList<>();
		try {
			for (int i = 0; i < p2v.getLayerSize(); i++) {
				w2vFeatures.add(label + i + ": " + p2v.getWordVector(lang+word.toLowerCase())[i]);
			}

		} catch (Exception e) { // OOV
			for (int i = 0; i < p2v.getLayerSize(); i++) {
				w2vFeatures.add(label + i + ": 0.0");
			}
		}

		return w2vFeatures;
	}
	
	   public static List<Feature> getMappedEmbedingVecsFeatures(String lang, String label, String word, Word2Vec p2v) {
	        List<Feature> w2vFeatures = new ArrayList<>();
	        try {
	            for (int i = 0; i < p2v.getLayerSize(); i++) {
	                w2vFeatures.add(new Feature().addName(label+i).addValue(p2v.getWordVector(lang+word.toLowerCase())[i]));
	            }

	        } catch (Exception e) { // OOV
	            for (int i = 0; i < p2v.getLayerSize(); i++) {
	                w2vFeatures.add(new Feature().addName(label+i).addValue(0.0));
	            }
	        }

	        return w2vFeatures;
	    }
	    
	
	public static List<Double> getEmbedingVecs(String lang, String word, Word2Vec p2v) {
		List<Double> w2vFeatures = new ArrayList<>();
		try {
			for (int i = 0; i < p2v.getLayerSize(); i++) {
				w2vFeatures.add(p2v.getWordVector(lang+word.toLowerCase())[i]);
			}

		} catch (Exception e) { // OOV
			for (int i = 0; i < p2v.getLayerSize(); i++) {
				w2vFeatures.add( 0.0d);
			}
		}

		return w2vFeatures;
	}

	public static List<String> getAvgTrigramEmbeddingVecs(String label, String word, String wordBf, String wordAf,
			Word2Vec p2v) {
		List<String> w2vFeatures = new ArrayList<>();

		List<Double> wordEmbd = new ArrayList<>();
		List<Double> wordAfEmbd = new ArrayList<>();
		List<Double> wordBfEmbd = new ArrayList<>();

		try {
			for (int i = 0; i < p2v.getLayerSize(); i++) {
				wordEmbd.add(p2v.getWordVector(word)[i]);
			}

		} catch (Exception e) { // OOV
			for (int i = 0; i < p2v.getLayerSize(); i++) {
				wordEmbd.add(0.0d);
			}
		}

		try {
			for (int i = 0; i < p2v.getLayerSize(); i++) {
				wordAfEmbd.add(p2v.getWordVector(wordAf)[i]);
			}

		} catch (Exception e) { // OOV
			for (int i = 0; i < p2v.getLayerSize(); i++) {
				wordAfEmbd.add(0.0d);
			}
		}

		try {
			for (int i = 0; i < p2v.getLayerSize(); i++) {
				wordBfEmbd.add(p2v.getWordVector(wordBf)[i]);
			}

		} catch (Exception e) { // OOV
			for (int i = 0; i < p2v.getLayerSize(); i++) {
				wordBfEmbd.add(0.0d);
			}
		}

		for (int i = 0; i < wordEmbd.size(); i++) {
			w2vFeatures.add(label + i + ": " + (wordEmbd.get(i) + wordAfEmbd.get(i) + wordBfEmbd.get(i)) / 3);
		}

		return w2vFeatures;
	}

	public static List<String> getAvgEmbeddingVecs(String lang, String label, String document, Word2Vec p2v) {
		List<String> w2vFeatures = new ArrayList<>();

	Map<Integer, List<Double>>wordEmbd = new HashMap<>();

		for (String word : document.split(" ")) {
			if(Stopwords.isStopword(word)){
				continue;
			}
			if(Stopwords.isStemmedStopword(word)){
				continue;
			}
			try {
				for (int i = 0; i < p2v.getLayerSize(); i++) {
					wordEmbd.putIfAbsent(i, new ArrayList<>());
					wordEmbd.get(i).add(p2v.getWordVector(lang+word)[i]);
				}

			} catch (Exception e) { // OOV
				for (int i = 0; i < p2v.getLayerSize(); i++) {
					wordEmbd.putIfAbsent(i, new ArrayList<>());
					wordEmbd.get(i).add(0.0d);
				}
			}
		}

		for (int i = 0; i < wordEmbd.size(); i++) {
			w2vFeatures.add(label + i + ": " +wordEmbd.get(i).stream().mapToDouble(f -> f.doubleValue()).average());
		}
		return w2vFeatures;
	}
	
	public static List<Double> getAvgEmbeddingVecs(String lang, String document, Word2Vec p2v) {
	
		List<Double> wordEmbd = new ArrayList<>();
		Map<Integer, List<Double>>wordEmbdAll = new HashMap<>();
		for (String word : document.split(" ")) {
			if(Stopwords.isStopword(word)){
				continue;
			}
		/*	if(Stopwords.isStemmedStopword(word)){
				continue;
			}*/
			try {
				for (int i = 0; i < p2v.getLayerSize(); i++) {
					wordEmbdAll.putIfAbsent(i, new ArrayList<>());
					wordEmbdAll.get(i).add(p2v.getWordVector(lang+word.toLowerCase())[i]);
				}

			} catch (Exception e) { // OOV
				for (int i = 0; i < p2v.getLayerSize(); i++) {
					wordEmbdAll.putIfAbsent(i, new ArrayList<>());
					wordEmbdAll.get(i).add(0.0d);
				}
			}
		}

		for (int i = 0; i < wordEmbdAll.size(); i++) {
			wordEmbd.add(wordEmbdAll.get(i).stream().mapToDouble(f -> f.doubleValue()).average().getAsDouble());
		}
		return wordEmbd;
	}

}
