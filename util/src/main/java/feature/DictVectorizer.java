package feature;

import com.google.common.primitives.Doubles;
import com.google.gson.Gson;

/*
 * Copyright 2017 Spotify AB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import com.spotify.featran.java.JFeatureExtractor;
import com.spotify.featran.java.JFeatureSpec;
import com.spotify.featran.transformers.OneHotEncoder;
import com.spotify.featran.transformers.StandardScaler;


import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class DictVectorizer {
	public static Map<String, String> featureSettings;
	public static Map<String, Integer> featureId;

	public static void init() {
		featureSettings =  new HashMap<>();
		featureId = new HashMap<>();
	}

	   public static void loadSettings(String settingsPAth) throws IOException { 
	       featureId = new HashMap<>();
	       Gson  gson = new Gson();
	       String json = org.apache.commons.io.FileUtils.readFileToString(new File(settingsPAth),"UTF8");
	       featureSettings = gson.fromJson(json,  HashMap.class);
	    }
	
    public static List<List<String>> getScaledFeatureValue(List<String> record, String featureName,
            boolean isTarining) {
        List<List<String>> scaledValue = new ArrayList<>();
        boolean isNumeric = true;
        for (String r : record) {
            try {
                Double.parseDouble(r);

            } catch (Exception e) {
                isNumeric = false;
                break;
            }
        }
        if (featureSettings == null) {
            init();
        }

        if (!isNumeric) {
            JFeatureSpec<String> oneHotEncoder = JFeatureSpec.<String>create().required(r -> r,
                    OneHotEncoder.apply(featureName));
            JFeatureExtractor<String> oneHotFeatures;

            if (isTarining) {
                oneHotFeatures = oneHotEncoder.extract(record);
            } else {
                oneHotFeatures = oneHotEncoder.extractWithSettings(record, featureSettings.get(featureName));
            }

            for (String fname : oneHotFeatures.featureNames()) {

                if (featureId.get(fname) == null) {
                    featureId.put(fname, featureId.keySet().size() + 1);
                }
            }
            JFeatureSpec<Double> standardScaler = JFeatureSpec.<Double>create().required(r -> r,
                    StandardScaler.apply("scaled", true, true));
            for (double[] oneHotEncoded : oneHotFeatures.featureValuesDouble()) {
                JFeatureExtractor<Double> scaled = standardScaler.extract(Doubles.asList(oneHotEncoded));
                List<String> scaledList = new ArrayList<>();
                int i = 0;
                for (double[] scaledVale : scaled.featureValuesDouble()) {
                    for (double value : scaledVale) {
                        scaledList.add(featureId.get(oneHotFeatures.featureNames().get(i)) + ":" + value);
                        i++;
                    }
                }
                scaledValue.add(scaledList);
            }
            if (isTarining) {
                String settings = oneHotFeatures.featureSettings();
                featureSettings.put(featureName, settings);
            }
        }

        else {

            if (featureId.get(featureName) == null) {
                featureId.put(featureName, featureId.keySet().size() + 1);
            }
            JFeatureSpec<Double> standardScaler = JFeatureSpec.<Double>create().required(r -> r,
                    StandardScaler.apply("scaled", true, true));
            double[] doubleValues = Arrays.stream(record.stream().toArray(String[]::new))
                    .mapToDouble(Double::parseDouble).toArray();

            JFeatureExtractor<Double> scaled;

            if (isTarining) {
                scaled = standardScaler.extract(Doubles.asList(doubleValues));
            } else {
                scaled = standardScaler.extractWithSettings(Doubles.asList(doubleValues),
                        featureSettings.get(featureName));
            }
            for (double[] scaledVale : scaled.featureValuesDouble()) {
                List<String> scaledList = new ArrayList<>();
                for (double value : scaledVale) {
                    if (Double.isNaN(value)) {
                        value = 0.0;
                    }
                    if (Double.isInfinite(value)) {
                        value = 1.0;
                    }
                    scaledList.add(featureId.get(featureName) + ":" + value);
                }
                scaledValue.add(scaledList);
            }
            if (isTarining) {
                String settings = scaled.featureSettings();
                featureSettings.put(featureName, settings);
            }
        }
        return scaledValue;
	}

    public static void saveSettings(String path) throws IOException {
        Gson gson = new Gson();
        
        FileUtils.write(new File(path), gson.toJson(featureSettings), "UTF8");

    }
	public static void main(String[] args) throws IOException {

	    loadSettings("/Users/seidmuhieyimam/Desktop/tmp/tmp/settings");
		List<String> ms = Arrays.asList(new String[] {"0.4",".5","10","0.0000034e6","123"});

		System.out.println(getScaledFeatureValue(ms, "bag", true));
		
		DataLine line1 = new DataLine();
		Feature f1 = new Feature();
		f1.setName("a");
		f1.setValue("yes");

		
		Feature f2 = new Feature();
        f2.setName("b");
        f2.setValue("10");
        
        Feature f3 = new Feature();
        f3.setName("c");
        f3.setValue("red");
        
        List<Feature> features = new ArrayList<>();
        features.add(f1);
        features.add(f2);
        features.add(f3);
        
        line1.setFeatures(features);
        line1.setSentNum(1);
        line1.setLable(2);
        
        DataLine line2 = new DataLine();
        f1 = new Feature();
        f1.setName("a");
        f1.setValue("no");
        
        f2 = new Feature();
        f2.setName("b");
        f2.setValue("20");
        
        f3 = new Feature();
        f3.setName("c");
        f3.setValue("blue");
        features = new ArrayList<>();
        features.add(f1);
        features.add(f2);
        features.add(f3);
        
        line2.setFeatures(features);
        line2.setSentNum(2);
        line2.setLable(2);
        
        DataLine line3 = new DataLine();
        f1 = new Feature();
        f1.setName("a");
        f1.setValue("yes");
        
        f2 = new Feature();
        f2.setName("b");
        f2.setValue("10");
        
        f3 = new Feature();
        f3.setName("c");
        f3.setValue("black");
        
        features = new ArrayList<>();
        features.add(f1);
        features.add(f2);
        features.add(f3);
        
        line3.setFeatures(features);
        line3.setSentNum(3);
        line3.setLable(2);
        
        DataLine line4 = new DataLine();
        f1 = new Feature();
        f1.setName("a");
        f1.setValue("yes");
        
        f2 = new Feature();
        f2.setName("b");
        f2.setValue("10");
        
        f3 = new Feature();
        f3.setName("c");
        f3.setValue("black");
        
        features = new ArrayList<>();
        features.add(f1);
        features.add(f2);
        features.add(f3);
        
        line4.setFeatures(features);
        line4.setSentNum(4);
        line4.setLable(2);
        
        List<DataLine> lines = new ArrayList<>();
        lines.add(line1);
        lines.add(line2);
        lines.add(line3);
        lines.add(line4);
        Map<String, List<String>> featureMapes = new LinkedHashMap<>();
        Map<Integer, List<Object>> scaledFeatures = new LinkedHashMap<>();
        for(DataLine line:lines) {
            scaledFeatures.put(line.getSentNum(), new ArrayList<>());
            for(Feature feature:line.getFeatures()) {
                featureMapes.putIfAbsent(feature.getName(), new ArrayList<>());
                featureMapes.get(feature.getName()).add(feature.getValue().toString());
            }
        }
       
        for (String feature : featureMapes.keySet()) {
            int i = 0;
            for (int line : scaledFeatures.keySet()) {
                scaledFeatures.get(line).add(StringUtils.join(getScaledFeatureValue(featureMapes.get(feature), feature, true).get(i),","));
            }
        }
        
        scaledFeatures.forEach((line, feature) ->{
            System.out.println("line "+ line +":" + StringUtils.join(feature,","));
        });
       saveSettings("/Users/seidmuhieyimam/Desktop/tmp/tmp/settings");

		/*
		JFeatureSpec<String> fs1 = JFeatureSpec.<String>create().required(r -> r, OneHotEncoder.apply("f1:"));
		JFeatureExtractor<String> f11 = fs1.extract(ms);

		System.out.println(f11.featureNames());

		JFeatureSpec<Double> fs2 = JFeatureSpec.<Double>create().required(r -> r,
				StandardScaler.apply("min-max", true, true));
		int i = 0;
		for (double[] f : f11.featureValuesDouble()) {
			System.out.println("record:" + i);
			i++;
			JFeatureExtractor<Double> f12 = fs2.extract(Doubles.asList(f));

			for (double[] scaledValue : f12.featureValuesDouble()) {

			}
		}

		// Random input
		List<Record> records = randomRecords();
		System.out.println("=====Records======");
		for (Record x : records) {
			System.out.println(x.d + " " + x.s + " " + x.s2);
		}
		System.out.println("=====Records======");
		// Start building a feature specification
		JFeatureSpec<Record> fs = JFeatureSpec.<Record>create()
				.required(r -> r.d, MinMaxScaler.apply("min-max", 0.0, 1.0))
				.required(r -> r.s, OneHotEncoder.apply("one-hot"))
				.required(r -> r.s2, OneHotEncoder.apply("one-hot2"));

		// Extract features from List<Record>
		JFeatureExtractor<Record> f1 = fs.extract(records);

		System.out.println(f1.featureNames());

		// Get feature values as double[]
		for (double[] f : f1.featureValuesDouble()) {
			System.out.println(Arrays.toString(f));
		}

		// Get feature values as DoubleSparseArray
		for (DoubleSparseArray f : f1.featureValuesDoubleSparse()) {
			String s = String.format("indices: [%s], values: [%s], length: %d", Arrays.toString(f.indices()),
					Arrays.toString(f.values()), f.length());
			System.out.println(s);
		}

		// Extract settings as a JSON string
		String settings = f1.featureSettings();
		System.out.println(settings);

		// Extract features from new records ,but reuse previously saved settings
		fs.extractWithSettings(randomRecords(), settings); */
	}
}
