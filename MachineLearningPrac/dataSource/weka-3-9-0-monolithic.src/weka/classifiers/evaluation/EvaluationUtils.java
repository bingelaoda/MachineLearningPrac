/*   1:    */ package weka.classifiers.evaluation;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.classifiers.Classifier;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.RevisionHandler;
/*  10:    */ import weka.core.RevisionUtils;
/*  11:    */ 
/*  12:    */ public class EvaluationUtils
/*  13:    */   implements RevisionHandler
/*  14:    */ {
/*  15: 43 */   private int m_Seed = 1;
/*  16:    */   
/*  17:    */   public void setSeed(int seed)
/*  18:    */   {
/*  19: 47 */     this.m_Seed = seed;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public int getSeed()
/*  23:    */   {
/*  24: 52 */     return this.m_Seed;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public ArrayList<Prediction> getCVPredictions(Classifier classifier, Instances data, int numFolds)
/*  28:    */     throws Exception
/*  29:    */   {
/*  30: 67 */     ArrayList<Prediction> predictions = new ArrayList();
/*  31: 68 */     Instances runInstances = new Instances(data);
/*  32: 69 */     Random random = new Random(this.m_Seed);
/*  33: 70 */     runInstances.randomize(random);
/*  34: 71 */     if ((runInstances.classAttribute().isNominal()) && (numFolds > 1)) {
/*  35: 72 */       runInstances.stratify(numFolds);
/*  36:    */     }
/*  37: 74 */     for (int fold = 0; fold < numFolds; fold++)
/*  38:    */     {
/*  39: 75 */       Instances train = runInstances.trainCV(numFolds, fold, random);
/*  40: 76 */       Instances test = runInstances.testCV(numFolds, fold);
/*  41: 77 */       ArrayList<Prediction> foldPred = getTrainTestPredictions(classifier, train, test);
/*  42:    */       
/*  43: 79 */       predictions.addAll(foldPred);
/*  44:    */     }
/*  45: 81 */     return predictions;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public ArrayList<Prediction> getTrainTestPredictions(Classifier classifier, Instances train, Instances test)
/*  49:    */     throws Exception
/*  50:    */   {
/*  51: 96 */     classifier.buildClassifier(train);
/*  52: 97 */     return getTestPredictions(classifier, test);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public ArrayList<Prediction> getTestPredictions(Classifier classifier, Instances test)
/*  56:    */     throws Exception
/*  57:    */   {
/*  58:111 */     ArrayList<Prediction> predictions = new ArrayList();
/*  59:112 */     for (int i = 0; i < test.numInstances(); i++) {
/*  60:113 */       if (!test.instance(i).classIsMissing()) {
/*  61:114 */         predictions.add(getPrediction(classifier, test.instance(i)));
/*  62:    */       }
/*  63:    */     }
/*  64:117 */     return predictions;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public Prediction getPrediction(Classifier classifier, Instance test)
/*  68:    */     throws Exception
/*  69:    */   {
/*  70:131 */     double actual = test.classValue();
/*  71:132 */     double[] dist = classifier.distributionForInstance(test);
/*  72:133 */     if (test.classAttribute().isNominal()) {
/*  73:134 */       return new NominalPrediction(actual, dist, test.weight());
/*  74:    */     }
/*  75:136 */     return new NumericPrediction(actual, dist[0], test.weight());
/*  76:    */   }
/*  77:    */   
/*  78:    */   public String getRevision()
/*  79:    */   {
/*  80:147 */     return RevisionUtils.extract("$Revision: 10153 $");
/*  81:    */   }
/*  82:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.EvaluationUtils
 * JD-Core Version:    0.7.0.1
 */