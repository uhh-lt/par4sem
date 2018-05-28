package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import controller.Controller;

public class GetParaphrase extends mturk.util.GetParaphrase{

    List<String> commonWords;

    public GetParaphrase() throws FileNotFoundException {
        commonWords = new ArrayList<String>();
    }


    public void getSimilarRest(String word, Map<String, Double> candidates, int n)
                    throws IOException, SQLException {
      //  for (String synonym : Controller.p2v.wordsNearest(word.trim().replace(" ", "_"), n)) {
        for (String synonym : (List<String>) Controller.restTemplate.getForObject("http://localhost:8081/near/"+word.trim(), ArrayList.class)) {
            // topXSynonyms.add(slem.lemmatize(synonym));
            candidates.putIfAbsent(synonym, 0.0);
            candidates.put(synonym, candidates.get(synonym) + 1);
        }
    }

    public Collection<String> getSimilarRest(String word, int n) throws IOException, SQLException {

        //  return Controller.p2v.wordsNearest(word.trim().replace(" ", "_"), n);
          return (List<String>) Controller.restTemplate.getForObject("http://localhost:8081/near/"+word.trim(), ArrayList.class);

      }
}
