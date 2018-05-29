/**
 * Copyright (C) 2013-2018 Vasilis Vryniotis <bbriniotis@datumbox.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ml;

import com.datumbox.framework.common.Configuration;
import com.datumbox.framework.core.common.dataobjects.Dataframe;
import com.datumbox.framework.core.common.dataobjects.Record;
import com.datumbox.framework.common.dataobjects.TypeInference;
import com.datumbox.framework.common.utilities.RandomGenerator;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import com.datumbox.framework.core.machinelearning.classification.MultinomialNaiveBayes;
import com.datumbox.framework.core.machinelearning.ensemblelearning.Adaboost;
import com.datumbox.framework.core.machinelearning.featureselection.PCA;
import com.datumbox.framework.core.machinelearning.modelselection.metrics.ClassificationMetrics;
import com.datumbox.framework.core.machinelearning.preprocessing.MinMaxScaler;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class Classification {


    private MinMaxScaler numericalScaler;
    private Configuration configuration;
    private PCA featureSelection;
    private Adaboost classifier;

    public static void main(String[] args) {
        Classification cl = new Classification();
        //cl.train();
        cl.test();
    }

    public void train( Properties prop)  throws Exception{


        // Initialization
        // --------------
        RandomGenerator.setGlobalSeed(42L); // optionally set a specific seed for all Random objects
        configuration = Configuration.getConfiguration(); // default configuration based on

        Dataframe trainingDataframe;
        try (Reader fileReader = new InputStreamReader(new FileInputStream(
                        new File(prop.getProperty("traincwi"))),
                        "UTF-8")) {
            LinkedHashMap<String, TypeInference.DataType> headerDataTypes = new LinkedHashMap<>();

            headerDataTypes.put("1", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("2", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("3", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("4", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("5", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("6", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("7", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("8", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("9", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("10", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("11", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("12", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("13", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("14", TypeInference.DataType.NUMERICAL);

            trainingDataframe = Dataframe.Builder.parseCSVFile(fileReader, "14", headerDataTypes,
                            ',', '"', "\r\n", null, null, configuration);
        } catch (UncheckedIOException | IOException ex) {
            throw new RuntimeException(ex);
        }

        // Transform Dataframe
        // -----------------

        // Scale continuous variables
        MinMaxScaler.TrainingParameters nsParams = new MinMaxScaler.TrainingParameters();
        numericalScaler = MLBuilder.create(nsParams, configuration);

        numericalScaler.fit_transform(trainingDataframe);
        numericalScaler.save("CWI_Num");

        // Feature Selection
        // -----------------

        // Perform dimensionality reduction using PCA

        PCA.TrainingParameters featureSelectionParameters = new PCA.TrainingParameters();
        featureSelectionParameters.setMaxDimensions(trainingDataframe.xColumnSize() - 1); // remove
                                                                                          // one
                                                                                          // dimension
        featureSelectionParameters.setWhitened(false);
        featureSelectionParameters.setVariancePercentageThreshold(0.99999995);

        featureSelection = MLBuilder.create(featureSelectionParameters, configuration);
        featureSelection.fit_transform(trainingDataframe);
        featureSelection.save("CWI_Feat");

        // Fit the classifier
        // ------------------

        /*
         * SoftMaxRegression.TrainingParameters param = new
         * SoftMaxRegression.TrainingParameters(); param.setTotalIterations(200);
         * param.setLearningRate(0.009);
         */

        Adaboost.TrainingParameters param = new Adaboost.TrainingParameters();
        param.setMaxWeakClassifiers(10);

        MultinomialNaiveBayes.TrainingParameters trainingParameters = new MultinomialNaiveBayes.TrainingParameters();
        trainingParameters.setMultiProbabilityWeighted(true);

        param.setWeakClassifierTrainingParameters(trainingParameters);

        classifier = MLBuilder.create(param, configuration);
        classifier.fit(trainingDataframe);
        classifier.save("CWI_class");

        // Close Dataframes.
        trainingDataframe.close();
    }

    public void test() {

        Dataframe testingDataframe;
        try (Reader fileReader = new InputStreamReader(
                        new FileInputStream(new File(
                                        "src/main/resources/datasets/cwi/raw/news/News_Test.csv")),
                        "UTF-8")) {
            LinkedHashMap<String, TypeInference.DataType> headerDataTypes = new LinkedHashMap<>();

            headerDataTypes.put("1", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("2", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("3", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("4", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("5", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("6", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("7", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("8", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("9", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("10", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("11", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("12", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("13", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("14", TypeInference.DataType.NUMERICAL);

            testingDataframe = Dataframe.Builder.parseCSVFile(fileReader, "14", headerDataTypes,
                            ',', '"', "\r\n", null, null, configuration);
        } catch (UncheckedIOException | IOException ex) {
            throw new RuntimeException(ex);
        }

        // Use the classifier
        // ------------------

        // Apply the same numerical scaling on testingDataframe
        numericalScaler.transform(testingDataframe);

        // Apply the same featureSelection transformations on testingDataframe
        featureSelection.transform(testingDataframe);

        // Use the classifier to make predictions on the testingDataframe
        classifier.predict(testingDataframe);

        // Get validation metrics on the test set
        ClassificationMetrics vm = new ClassificationMetrics(testingDataframe);

        System.out.println("Results:");
        for (Map.Entry<Integer, Record> entry : testingDataframe.entries()) {
            Integer rId = entry.getKey();
            Record r = entry.getValue();
            System.out.println("Record " + rId + " - Real Y: " + r.getY() + ", Predicted Y: "
                            + r.getYPredicted());
        }

        System.out.println("Classifier Accuracy: " + vm.getAccuracy());
        System.out.println("Fa1: " + vm.getMacroF1());
        System.out.println("pa: " + vm.getMacroPrecision());
        System.out.println("ra: " + vm.getMacroRecall());

        System.out.println("F1: " + vm.getMicroF1());
        System.out.println("p: " + vm.getMicroPrecision());
        System.out.println("r: " + vm.getMicroRecall());

        testingDataframe.close();
    }

    public List<Object> predict(String fileName) throws Exception {

        Dataframe testingDataframe;
        try (Reader fileReader = new InputStreamReader(new FileInputStream(new File(fileName)),
                        "UTF-8")) {
            LinkedHashMap<String, TypeInference.DataType> headerDataTypes = new LinkedHashMap<>();

            headerDataTypes.put("1", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("2", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("3", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("4", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("5", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("6", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("7", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("8", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("9", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("10", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("11", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("12", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("13", TypeInference.DataType.NUMERICAL);
            headerDataTypes.put("14", TypeInference.DataType.NUMERICAL);

            testingDataframe = Dataframe.Builder.parseCSVFile(fileReader, "14", headerDataTypes,
                            ',', '"', "\r\n", null, null, configuration);
        } catch (UncheckedIOException | IOException ex) {
            throw new RuntimeException(ex);
        }

        // Use the classifier
        // ------------------

        // Apply the same numerical scaling on testingDataframe
        numericalScaler.transform(testingDataframe);

        // Apply the same featureSelection transformations on testingDataframe
        featureSelection.transform(testingDataframe);

        // Use the classifier to make predictions on the testingDataframe
        classifier.predict(testingDataframe);

        // Get validation metrics on the test set
        ClassificationMetrics vm = new ClassificationMetrics(testingDataframe);
        List<Object> predictions = new ArrayList<>();
        for (Map.Entry<Integer, Record> entry : testingDataframe.entries()) {
            Integer rId = entry.getKey();
            Record r = entry.getValue();
            System.out.println("Record " + rId + " - Real Y: " + r.getY() + ", Predicted Y: "
                            + r.getYPredicted());
            predictions.add(r.getYPredicted());
        }

        System.out.println("Classifier Accuracy: " + vm.getAccuracy());
        System.out.println("Fa1: " + vm.getMacroF1());
        System.out.println("pa: " + vm.getMacroPrecision());
        System.out.println("ra: " + vm.getMacroRecall());

        System.out.println("F1: " + vm.getMicroF1());
        System.out.println("p: " + vm.getMicroPrecision());
        System.out.println("r: " + vm.getMicroRecall());
        testingDataframe.close();

        return predictions;
    }

}
