package database;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import utils.Utils;

public class PPDBLines {
    double paraphraseScore;
    double simplificationScore;
    String syntacticCategory;
    String inputPhrase;
    String outputPhrase;
   static int i =0;
    public PPDBLines() {

    }

    public PPDBLines addParaphraseScore(double paraphraseScore) {
        this.paraphraseScore = paraphraseScore;
        return this;
    }

    public PPDBLines addSimplificationScore(double simplificationScore) {
        this.simplificationScore = simplificationScore;
        return this;
    }

    public PPDBLines addSyntacticCategory(String syntacticCategory) {
        this.syntacticCategory = syntacticCategory;
        return this;
    }

    public PPDBLines addParaphraseScore(String syntacticCategory) {
        this.syntacticCategory = syntacticCategory;
        return this;
    }

    public PPDBLines addInputPhrase(String inputPhrase) {
        this.inputPhrase = inputPhrase;
        return this;
    }

    public PPDBLines addOutputPhrase(String outputPhrase) {
        this.outputPhrase = outputPhrase;
        return this;
    }

    public double getParaphraseScore() {
        return paraphraseScore;
    }

    public double getSimplificationScore() {
        return simplificationScore;
    }

    public String getSyntacticCategory() {
        return syntacticCategory;
    }

    public String getInputPhrase() {
        return inputPhrase;
    }

    public String getOutputPhrase() {
        return outputPhrase;
    }

    public static void addSimplePPDB()
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException, PropertyVetoException {
        ClassLoader classLoader = PPDBLines.class.getClassLoader();
        InputStream in = classLoader.getResourceAsStream("semsch.properties");
        Properties prop = Utils.getConfigs(in);
        Par4SemResource.initSimplePpdb(prop);
        String articleQuery = "INSERT INTO ppdb (paraphraseScore, simplificationScore, syntacticCategory, inputPhrase, outputPhrase) VALUES (?,?,?,?,?)";
        PreparedStatement simple = Par4SemResource.simpleppdbConn.prepareStatement(articleQuery);
        Par4SemResource.simplePPDBLists.stream().forEach(line -> {
            try {
                i++;
                simple.setDouble(1, line.getParaphraseScore());
                simple.setDouble(2, line.getSimplificationScore());
                simple.setString(3, line.getSyntacticCategory());
                simple.setString(4, line.getInputPhrase());
                simple.setString(5, line.getOutputPhrase());
                simple.addBatch();
                if(i%100000==0) {
                    System.out.println(i);
                    simple.executeBatch();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        });

        simple.executeBatch();
        Par4SemResource.simpleppdbConn.commit();
    }

}