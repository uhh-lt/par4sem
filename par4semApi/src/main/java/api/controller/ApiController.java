package api.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.service.Classification;
import api.service.W2VecService;

@RestController
public class ApiController {
    @Autowired
    private Classification boostService;
    @Autowired
    private W2VecService w2v;

    @RequestMapping("/hello")
    public String sayHi() {
        return "Hi";
    }

    @RequestMapping("/train")
    public void train() throws IOException, URISyntaxException {
        boostService.train();
    }

    @RequestMapping("/predict")
    public void predict() throws IOException, URISyntaxException {
        boostService.test();
    }

    @RequestMapping("/similar/{word}")
    public Map<String, Double> getSimilar(@PathVariable String word)
                    throws IOException, URISyntaxException {
        Map<String, Double> candidates = new HashMap<>();
        w2v.getSimilar(word,candidates, 30);
        return candidates;
    }
    
    @RequestMapping("/near/{word}")
    public List<String> getNear(@PathVariable String word)
                    throws IOException, URISyntaxException {
      return   w2v.getSimilar(word, 30);
    }

}
