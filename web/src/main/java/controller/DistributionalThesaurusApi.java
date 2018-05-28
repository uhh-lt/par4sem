package controller;


import org.jobimtext.api.struct.DatabaseThesaurusDatastructure;
import org.jobimtext.api.struct.IThesaurusDatastructure;

/**
 * This example exemplifies the Distributional Thesaurus API
 *
 */
public class DistributionalThesaurusApi {
    public static void main(String[] args) {
        //String dtConfig="config/medline_trigram_dt.xml";
        String dtConfig="src/main/resources/config/medline_trigram_dt.xml";
//      IThesaurusMap<String, String> dt = new DcaLightThesaurusMap(dtConfig);
        IThesaurusDatastructure<String, String> dt = new DatabaseThesaurusDatastructure(dtConfig);
        dt.connect();
        ///get all similar terms for a given term
        System.out.println(dt.getSimilarTerms("protein"));
        ///get top 10 similar terms for a given term
        System.out.println(dt.getSimilarTerms("day",10));
        ///get similar terms to the given one which have a score above 10.0
        System.out.println(dt.getSimilarTerms("day",10.0));
        dt.destroy();

        //the api can easily be used with Databases and also with the old dca
        // String dcaApiConfig="config/dcaConfig.xml";
        // String db2ApiConfig="config/db2Config.xml";
        // String mysqlApiConfig="config/mysqlConfig.xml";
        //IThesaurusMap<String, String> dt = new DcaLightThesaurusMap(dcaApiConfig);
        //IThesaurusMap<String, String> dt = new DatabaseThesaurusMap(db2ApiConfig);
        //IThesaurusMap<String, String> dt = new DatabaseThesaurusMap(mysqlApiConfig);

        //You don't want to use Maps as return type but lists with datatypes??
        //use the IThesaurusDatastructure, yet there is no implementation for the DcA light, but it should be easily done
        //IThesaurusDatastructure<String, String> dtDataStruct = new DatabaseThesaurusDatastructure(dcaLightApiConfig);
        //dtDataStruct = new DCAThesaurusDatastructure(dcaLightApiConfig);



    }
}
