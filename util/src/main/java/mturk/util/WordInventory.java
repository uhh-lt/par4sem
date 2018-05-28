package mturk.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * From here https://www.cs.utexas.edu/~scottm/cs324e/CodeSamples/WordInventory2.java
 * @author seid
 *
 */
public class WordInventory {
    // instance variables
    private Map<String, Integer> words;
    
    public WordInventory(Scanner sc) {
        words = new HashMap<String, Integer>();
        
        while(sc.hasNext()) {
            String temp = sc.next().toLowerCase();
            if(words.containsKey(temp)) {
                int freq = words.get(temp);
                freq++;
                words.put(temp, freq);
            }
            else {
                words.put(temp, 1);
            }
        }
    }
    
    public void showResults(int num) {
        Set<String> keys = words.keySet();
        ArrayList<WordFreqPair> rankedWords = new ArrayList<WordFreqPair>(keys.size());
        for(String key : keys) {
            int freq = words.get(key);
            WordFreqPair pair = new WordFreqPair(key, freq);
            rankedWords.add(pair);
        }
        Collections.sort(rankedWords);
        int numToShow = Math.min(num, rankedWords.size());
        int mostWords = calcMost(rankedWords);
        System.out.println(mostWords);
        for(int i = 0; i < numToShow; i++) {
            int predicted = (int) (1.0 / (i + 1) * mostWords);
            int actual = rankedWords.get(i).freq;
            double error = 100.0 * (predicted - actual) / predicted;
            System.out.println("rank: " + (i + 1) + " " + rankedWords.get(i) 
                    + ", predicted: " + predicted + " error: " + error);
        }
    }
    
    public List<WordFreqPair> gwtCommonWords(int num) {
        Set<String> keys = words.keySet();
        ArrayList<WordFreqPair> rankedWords = new ArrayList<WordFreqPair>(keys.size());
        for(String key : keys) {
            int freq = words.get(key);
            WordFreqPair pair = new WordFreqPair(key, freq);
            rankedWords.add(pair);
        }
        Collections.sort(rankedWords);
        if(rankedWords.size()<num){
            return new ArrayList<WordFreqPair>();
        }
        
        List<WordFreqPair> commonWOrds = new ArrayList<WordFreqPair>();
        for(int i = 0; i < num; i++) {
            commonWOrds.add(rankedWords.get(i) );
        }
        return commonWOrds;
    }
    
    public List<WordFreqPair> getBelowAverageRank() {
        Set<String> keys = words.keySet();
        ArrayList<WordFreqPair> rankedWords = new ArrayList<WordFreqPair>(keys.size());
        for(String key : keys) {
            int freq = words.get(key);
            WordFreqPair pair = new WordFreqPair(key, freq);
            rankedWords.add(pair);
        }
        Collections.sort(rankedWords);
        
        List<WordFreqPair> unPopularWords = new ArrayList<WordFreqPair>();
        for(int i =  rankedWords.size()/2; i < rankedWords.size(); i++) {
            unPopularWords.add(rankedWords.get(i) );
        }
        return unPopularWords;
    }
    
    private static int calcMost(ArrayList<WordFreqPair> rankedWords) {
        int numToUse = Math.min(rankedWords.size(), 250);
        long total = 0;
        for(int i = 0; i < numToUse; i++) {
            total += (i + 1) * rankedWords.get(i).freq;
        }
        return (int) (total / numToUse);
    }

    public int size() {
        return words.size();
    }
    
    public void showSome(int num) {
        int i = 0;
        for(String key : words.keySet()) {
            System.out.println(key + " " + words.get(key));
            i++;
            if(i == num) {
                break;
            }
        }
    }
    
    public static class WordFreqPair implements Comparable<WordFreqPair>{
        private int freq;
        private String word;
        
        private WordFreqPair(String w, int f) {
            freq = f;
            word = w;
        }
        
        public String getWord(){
            return word;
        }
        
        public int getFrew(){
            return freq;
        }
        @Override
        public boolean equals(Object other){
            boolean result = other instanceof WordFreqPair;
            if(result) {
                WordFreqPair otherPair = (WordFreqPair) other;
                result = word.equals(otherPair.word);
            }
            return result;
        }
        
        @Override
        public String toString() {
            return word + " " + freq;
        }
        
        @Override
        public int compareTo(WordFreqPair other) {
            return other.freq - freq;
        }
    }
}