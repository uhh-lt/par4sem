package feature;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class DataLine {
    int sentNum;
    int lable;
    List<Feature> features;
    public int getSentNum() {
        return sentNum;
    }
    public void setSentNum(int sentNum) {
        this.sentNum = sentNum;
    }
    public int getLable() {
        return lable;
    }
    public void setLable(int lable) {
        this.lable = lable;
    }
    public List<Feature> getFeatures() {
        return features;
    }
    public void setFeatures(List<Feature> features) {
        this.features = features;
    }
    
    @Override
    public String toString() {
        List<String> values = new ArrayList<>();
        values.add(getSentNum()+ "-"+getLable());
        
        for (Feature f: getFeatures()) {
            values.add(f.getName()+":"+f.getValue().toString());
        }
        return StringUtils.join(values, ",");
    }
    
}
