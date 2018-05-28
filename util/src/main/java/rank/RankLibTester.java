package rank;

import java.io.File;

import ciir.umass.edu.eval.Evaluator;

public class RankLibTester {
    
//  static File dataFile = new File("/Users/seidmuhieyimam/Downloads/MQ2008/Fold1/train.txt");
 //   static File testFile = new File("/Users/seidmuhieyimam/Downloads/MQ2008/Fold1/test.txt");
 //   static File dataFile = new File("/Users/seidmuhieyimam/Downloads/ranklib_train.dat");
    
    
    
//    static File dataFile = new File("/Users/seidmuhieyimam/Downloads/train.csv");
//    static File testFile = new File("/Users/seidmuhieyimam/Downloads/train.csv");
//    static File dataFile = new File("/Users/seidmuhieyimam/Desktop/tmp/tmp/train.tsv.ft");
  //  static File testFile = new File("/Users/seidmuhieyimam/Desktop/tmp/tmp/test.tsv.ft");
//    static File qRel = new File("/Users/seidmuhieyimam/Desktop/tmp/tmp/train.qrel");
    
//   static File dataFile = new File("/Users/seidmuhieyimam/Desktop/tmp/tmp/lex.mturk.train.tsv.ft");
//   static File testFile = new File("/Users/seidmuhieyimam/Desktop/tmp/tmp/lex.mturk.test.tsv.ft");
//   static File qRel = new File("/Users/seidmuhieyimam/data/semsch/lex.mturk.14/lex.mturk.train.qrel");
   static File featureFile = new File("/Users/seidmuhieyimam/Desktop/tmp/tmp/feature.txt");
    
    
    
  //  File dataFile = new File("/Users/seidmuhieyimam/Desktop/tmp/tmp/featurestrain.txt");
   //  File testFile = new File("/Users/seidmuhieyimam/Desktop/tmp/tmp/featurestest.txt");
   
   
   // static File qRel = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.txt.qrel");
 
  // TEST
//     static File dataFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.txt.ft");
// static File testFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/test.ndgc.it2.txt.ft");
   //Train
//   static File dataFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.current_3.txt.ft");
//   static File testFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/test.it3.txt.ft");
  
   //TEST
// static File dataFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.current.txt.ft");
// static File testFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/test.ndgc.it3.txt.ft");
  // Test 
// static File dataFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.current.txt.ft");
// static File testFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/test.it4.txt.ft");
  
 //  static File dataFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.current_it2.txt.ft");
 //  static File testFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/test.ndgc.it3.txt.ft");
   
//   static File dataFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.current_it3.txt.ft");
//   static File testFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/test.ndgc.it4.txt.ft");
  
 //  static File dataFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.current_it4.txt.ft");
 //  static File testFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/test.it5.txt.ft");
   
 //static File dataFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.current_it4.txt.ft");
// static File testFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/test.ndgc.it5.txt.ft");
   
 // static File dataFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.current_it5.txt.ft");
//  static File testFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/test.it6.txt.ft");

//   static File dataFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.current_it5.txt.ft");
 // static File testFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/test.ndgc.it6.txt.ft");
   
   
//    static File dataFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.current_it6.txt.ft");
//    static File testFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/test.it7.txt.ft");
   
// static File dataFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.current_it6.txt.ft");
 //static File testFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/test.ndgc.it7.txt.ft");
 
 //static File dataFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.current_it7.txt.ft");
 // static File testFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/test.it8.txt.ft");
   
   //see ALL
// static File dataFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.current_it2.txt.ft");
// static File dataFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.txt.ft");
 //static File testFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/test.ndgc.it8.txt.ft");
 
// static File dataFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.current_it8.txt.ft");
//  static File testFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/test.it9.txt.ft");

 static File dataFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/train.current_it2.txt.ft");
 //  static File dataFile = new File("/Users/seidmuhieyimam/Desktop/tmp/tmp/train.tsv.ft");
 static File testFile = new File("/Users/seidmuhieyimam/data/semsch/par4sim/test.ndgc.it9.txt.ft");
 
    static File modelFile = new File(dataFile.getPath()+".model");
public static void main(String[] args) {
    try {
        dataFile = new File(args[0]);
        testFile = new File (args[1]);
    }
    catch (Exception e) {
        
    }
    Evaluator.main(new String[] {});
//  trainMap();
 //   trainError();
    trainNDCG();
//    trainPatN();
//    testErrorat10();
//    testPatK();
//    testNDCG();
//    testMAP();
   rank();
 //   Evaluator.main(new String[] {});
}
private static void trainMap () {
        Evaluator.main(new String[]{
            "-train", dataFile.getPath(),
   //        "-feature", featureFile.getPath(),
          //  "-qrel", qRel.getPath(),
            "-metric2t", "map",
            "-ranker", Integer.toString(6),
            "-norm","linear",
     //       "-frate", "1.0",
    //        "-bag", "100",
     //       "-gmax","5",
        //    "-round", "100",
          //  "-tree", "2000",
      //      "-shrinkage","0.05",
        //    "-epoch", "100",
       //     "-thread","100",
            "-test",testFile.getPath(),
            "-save", modelFile.getPath()});
}
private static void trainError () {
    Evaluator.main(new String[]{
        "-train", dataFile.getPath(),
   //     "-qrel", qRel.getPath(),
      //  "-metric2t", "map",
        "-ranker", Integer.toString(6),
        "-frate", "1.0",
        "-bag", "10",
        "-round", "10",
        "-epoch", "10",
        "-thread","100",
        "-test",testFile.getPath(),
        "-save", modelFile.getPath()});
}
private static void trainNDCG () {
    Evaluator.main(new String[]{
        "-train", dataFile.getPath(),
    //    "-qrel", qRel.getPath(),
        "-metric2t", "NDCG@10",
        "-ranker", Integer.toString(6),
        "-frate", "1.0",
        "-bag", "10",
        "-round", "10",
        "-epoch", "10",
        "-thread","100",
        "-test",testFile.getPath(),
        "-save", modelFile.getPath()});
}

private static void trainPatN () {
    Evaluator.main(new String[]{
        "-train", dataFile.getPath(),
       // "-qrel", qRel.getPath(),
        "-metric2t", "P@5",
        "-ranker", Integer.toString(6),
        "-frate", "1.0",
        "-bag", "100",
        "-gmax","5",
        "-round", "100",
        "-tree", "2000",
        "-shrinkage","0.05",
        "-epoch", "100",
        "-thread","100",
        "-test",testFile.getPath(),
        "-save", modelFile.getPath()});
}


private static void testMAP () {
            Evaluator.main(new String[]{
                "-load",  modelFile.getPath(),
                "-metric2T", "MAP",
                "-test",testFile.getPath()});
}
private static void testNDCG () {
    Evaluator.main(new String[]{
        "-load",  modelFile.getPath(),
        "-metric2T", "NDCG@5",
        "-test",testFile.getPath()});
}
private static void testPatK () {
    Evaluator.main(new String[]{
      //      "-train", dataFile.getPath(),
       //     "-metric2t", "P@5",
        "-load",  modelFile.getPath(),
        "-metric2T", "P@5",
        "-test",testFile.getPath()});
}

private static void rank () {
    Evaluator.main(new String[]{
      //      "-train", dataFile.getPath(),
       //     "-metric2t", "P@5",
                    "-metric2T", "NDCG@10",
  //                  "-norm","linear",
        "-load",  modelFile.getPath(),
        "-rank",  testFile.getPath(),
        "-score",testFile.getPath()+".score"});
}

private static void testErrorat10 () {
    Evaluator.main(new String[]{
      //      "-train", dataFile.getPath(),
       //     "-metric2t", "P@5",
        "-load",  modelFile.getPath(),
        "-metric2T", "ERR@10",
        "-test",testFile.getPath()});
}


}
