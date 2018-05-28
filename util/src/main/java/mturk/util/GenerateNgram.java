package mturk.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenerateNgram
{
    public static void main(String[] args)
    {

            System.out.println(generateNgramsUpto("I like to go to the city by now", 3));

    }

    public static List<String> generateNgramsUpto(String text, int n)
    {
        // Use .isEmpty() instead of .length() == 0
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("null or empty text");
        }
        String[] words = text.split(" ");
        List<String> list = new ArrayList<String>();
        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder keyBuilder = new StringBuilder(words[i].trim());
            for (int j = 1; j < n - 1; j++) {
                keyBuilder.append(' ').append(words[i + j].trim());
            }
            String key = keyBuilder.toString();
            list.add(key+" "+ words[i + n - 1]);
        }
        return list;
    }
    
    public static List<String> ngrams(int n, String str) {
        List<String> ngrams = new ArrayList<String>();
        String[] words = str.split(" ");
        for (int i = 0; i < words.length - n + 1; i++)
            ngrams.add(concat(words, i, i+n));
        return ngrams;
    }

    public static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append((i > start ? " " : "") + words[i]);
        return sb.toString();
    }

    public static Set<String> generateNgramsUpto(int gram, String sentence) {
        Set<String> ngrams = new HashSet<>();
        for (int n = 1; n <= gram; n++) {
            for (String ngram : ngrams(n,sentence))
                ngrams.add(ngram);
        }
        return ngrams;
    }
}
