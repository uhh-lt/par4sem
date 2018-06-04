package database;

import static utils.Constants.LEMMA;
import static utils.Constants.WORD_ID;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class WordNetSynonym
{

    public static Set<String> getSynonymWords(String aWord)
        throws SQLException
    {
        String wordId = getWordId(aWord);
        Set<String> similarWords = new LinkedHashSet<String>();
        for (String synsetId : getSens(wordId)) {
            for (String word : getSimilarWords(getSynsets(synsetId))) {
                similarWords.add(word);
            }
        }
        return similarWords;

    }

    private static String getWordId(String aWord)
        throws SQLException
    {
        String query = "SELECT wordid FROM words where lemma =\'" + aWord + "\';";
        ResultSet rs = Par4SemResource.wordNetConn.createStatement().executeQuery(query);
        while (rs.next()) {
            return rs.getString(WORD_ID);
        }
        return null;
    }

    private static List<String> getSens(String aWordId)
        throws SQLException
    {
        List<String> senses = new ArrayList<String>();
        String query = "SELECT synsetid FROM senses where wordid =" + aWordId + ";";
        ResultSet sim = Par4SemResource.wordNetConn.createStatement().executeQuery(query);

        while (sim.next()) {
            senses.add(sim.getString("synsetid"));
        }
        return senses;

    }

    private static List<String>  getSynsets(String aSysnsetId)
        throws SQLException
    {
        List<String> synsets = new ArrayList<String>();
        String query = "SELECT wordid FROM senses where synsetid =" + aSysnsetId + ";";
        ResultSet sim = Par4SemResource.wordNetConn.createStatement().executeQuery(query);

        while (sim.next()) {
            synsets.add(sim.getString("wordid"));
        }
        return synsets;

    }

    private static List<String> getSimilarWords(List<String> aWordIds)
        throws SQLException
    {
        List<String> lemmas = new ArrayList<String>();

        String query = "SELECT lemma FROM words where wordid in ("
                + StringUtils.join(aWordIds, ',') + ");";
        ResultSet sim = Par4SemResource.wordNetConn.createStatement().executeQuery(query);

        while (sim.next()) {
            lemmas.add(sim.getString(LEMMA));
        }
        return lemmas;

    }
    
    public static Set<String> getMWEFromWN()
            throws SQLException
        {
            Set<String> lemmas = new HashSet<String>();

            String query = "SELECT lemma FROM words;";
            ResultSet sim = Par4SemResource.wordNetConn.createStatement().executeQuery(query);

            while (sim.next()) {
                String lemma = sim.getString(LEMMA);
                if(lemma.split(" ").length>1) {
                    lemmas.add(lemma);
                }
            }
            return lemmas;

        }
}
