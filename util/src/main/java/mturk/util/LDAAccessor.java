package mturk.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import jgibblda.Inferencer;
import jgibblda.LDACmdOption;
import jgibblda.Model;
import jgibblda.Pair;

public class LDAAccessor {
	public static void main(String[] args) {
		LDACmdOption ldaOption = new LDACmdOption();
		ldaOption.inf = true;
		ldaOption.dir = args[0];
		ldaOption.modelName = "posModel-final";
		ldaOption.niters = 100;
		Inferencer inferencer = new Inferencer();
		inferencer.init(ldaOption);
		String[] test = { "politics bill clinton", "law court", "football match" };
		Model newModel = inferencer.inference(test);
	/*	ldaOption.dfile = args[1]; 
		Model newModel = inferencer.inference();		*/
		
		
		for (int i = 0; i < newModel.data.M; i++) {
			for (int j = 0; j < newModel.data.docs[i].length; ++j) {
				int lid = newModel.data.docs[i].words[j];
				int gid = newModel.data.lid2gid.get(lid);
				System.out.println(gid + ":" + newModel.z[i].get(j) + " ");
				System.out.println(newModel.z[i]);		
				System.out.println(newModel.data.localDict.id2word.get(lid));
			}
		}
		Map<String, List<Double>> wordTopicVectors =  getWordVectors(newModel);
		System.out.println(newModel.data.localDict.id2word.keySet());
		for(String word:wordTopicVectors.keySet()){
			System.out.println(word+"="+wordTopicVectors.get(word));
		}
		Map<Integer, List<Double>> docVecs =  getDocVectors(newModel);
		System.out.println(docVecs);
		
		System.out.println("politics ="+ getWordVector(newModel, "politics"));
	}
	
	public static Map<Integer, List<Double>>  getDocVectors(Model newModel  ){	
		
		Map<Integer, List<Double>> docVecs = new HashMap<>();
		for (int m = 0; m < newModel.M; m++){
			for (int k = 0; k < newModel.K; k++){			
				docVecs.putIfAbsent(m, new ArrayList<>());
				docVecs.get(m).add(newModel.theta [m][k]);
			}	
		}
		return  docVecs;
	}
	
	public static Map<String, List<Double>>  getWordVectors(Model newModel){	
		 Map<String, List<Double>> wordTopicVectors = new HashMap<>();
			for (int k = 0; k < newModel.K; k++){
				List<Pair> wordsProbsList = new ArrayList<Pair>(); 
				for (int w = 0; w < newModel.V; w++){
					Pair p = new Pair(w, newModel.phi[k][w], false);				
					wordsProbsList.add(p); 
				}			
				Collections.sort(wordsProbsList);
				
				for (int i = 0; i < newModel.V; i++){
					if (newModel.data.localDict.contains((Integer)wordsProbsList.get(i).first)){
						String word = newModel.data.localDict.getWord((Integer)wordsProbsList.get(i).first);						
						//System.out.println("\t" + word + " " + wordsProbsList.get(i).second + "\n");
						wordTopicVectors.putIfAbsent(word, new ArrayList<>());
						wordTopicVectors.get(word).add((double)wordsProbsList.get(i).second);
					}
				}
			} //end foreach topic	
			
			return  wordTopicVectors;
	}
	
	public static List<Double>  getWordVector(Model newModel, String word){	
		List<Double> wordVector = new ArrayList<>();
		boolean isoov = true;
		int widx = 0;
		for (int w = 0; w < newModel.V; w++) {
			if (word.equals(newModel.data.localDict.getWord(w))) {
				isoov = false;
				widx = w;
				break;
			}
		}
		if (isoov) {
			for (int k = 0; k < newModel.K; k++) {
				wordVector.add(0.0);
			}
		} else {
			for (int k = 0; k < newModel.K; k++) {
				wordVector.add(newModel.phi[k][widx]);
			}
		}
	
		return  wordVector;
}
}
