package api.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class W2VecService {
    @Autowired
    private Environment environment;
    private Word2Vec p2v;

    @PostConstruct
    public void init() {
        p2v = WordVectorSerializer.readWord2VecModel(environment.getProperty("w2vModelPath"));
    }

    @Deprecated
    public void getSimilar(String word, Map<String, Double> candidates, int n) {

        for (String synonym : p2v.wordsNearest(word.trim().replace(" ", "_"), n)) {
            // topXSynonyms.add(slem.lemmatize(synonym));
            candidates.putIfAbsent(synonym, 0.0);
            candidates.put(synonym, candidates.get(synonym) + 1);
        }
        }  
    public List<String> getSimilar(String word, int n) {

        return (List<String>) p2v.wordsNearest(word.trim().replace(" ", "_"), n);

    }
}
