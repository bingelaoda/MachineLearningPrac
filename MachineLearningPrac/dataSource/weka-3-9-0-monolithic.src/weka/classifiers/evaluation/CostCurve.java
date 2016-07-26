/*   1:    */ package weka.classifiers.evaluation;
/*   2:    */ 
/*   3:    */ import java.io.InputStreamReader;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import weka.classifiers.Classifier;
/*   7:    */ import weka.classifiers.functions.Logistic;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.DenseInstance;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.RevisionHandler;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ 
/*  15:    */ public class CostCurve
/*  16:    */   implements RevisionHandler
/*  17:    */ {
/*  18:    */   public static final String RELATION_NAME = "CostCurve";
/*  19:    */   public static final String PROB_COST_FUNC_NAME = "Probability Cost Function";
/*  20:    */   public static final String NORM_EXPECTED_COST_NAME = "Normalized Expected Cost";
/*  21:    */   public static final String THRESHOLD_NAME = "Threshold";
/*  22:    */   
/*  23:    */   public Instances getCurve(ArrayList<Prediction> predictions)
/*  24:    */   {
/*  25: 74 */     if (predictions.size() == 0) {
/*  26: 75 */       return null;
/*  27:    */     }
/*  28: 77 */     return getCurve(predictions, ((NominalPrediction)predictions.get(0)).distribution().length - 1);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Instances getCurve(ArrayList<Prediction> predictions, int classIndex)
/*  32:    */   {
/*  33: 91 */     if ((predictions.size() == 0) || (((NominalPrediction)predictions.get(0)).distribution().length <= classIndex)) {
/*  34: 93 */       return null;
/*  35:    */     }
/*  36: 96 */     ThresholdCurve tc = new ThresholdCurve();
/*  37: 97 */     Instances threshInst = tc.getCurve(predictions, classIndex);
/*  38:    */     
/*  39: 99 */     Instances insts = makeHeader();
/*  40:100 */     int fpind = threshInst.attribute("False Positive Rate").index();
/*  41:101 */     int tpind = threshInst.attribute("True Positive Rate").index();
/*  42:102 */     int threshind = threshInst.attribute("Threshold").index();
/*  43:106 */     for (int i = 0; i < threshInst.numInstances(); i++)
/*  44:    */     {
/*  45:107 */       double fpval = threshInst.instance(i).value(fpind);
/*  46:108 */       double tpval = threshInst.instance(i).value(tpind);
/*  47:109 */       double thresh = threshInst.instance(i).value(threshind);
/*  48:110 */       double[] vals = new double[3];
/*  49:111 */       vals[0] = 0.0D;
/*  50:112 */       vals[1] = fpval;
/*  51:113 */       vals[2] = thresh;
/*  52:114 */       insts.add(new DenseInstance(1.0D, vals));
/*  53:115 */       vals = new double[3];
/*  54:116 */       vals[0] = 1.0D;
/*  55:117 */       vals[1] = (1.0D - tpval);
/*  56:118 */       vals[2] = thresh;
/*  57:119 */       insts.add(new DenseInstance(1.0D, vals));
/*  58:    */     }
/*  59:122 */     return insts;
/*  60:    */   }
/*  61:    */   
/*  62:    */   private Instances makeHeader()
/*  63:    */   {
/*  64:132 */     ArrayList<Attribute> fv = new ArrayList();
/*  65:133 */     fv.add(new Attribute("Probability Cost Function"));
/*  66:134 */     fv.add(new Attribute("Normalized Expected Cost"));
/*  67:135 */     fv.add(new Attribute("Threshold"));
/*  68:136 */     return new Instances("CostCurve", fv, 100);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public String getRevision()
/*  72:    */   {
/*  73:146 */     return RevisionUtils.extract("$Revision: 10169 $");
/*  74:    */   }
/*  75:    */   
/*  76:    */   public static void main(String[] args)
/*  77:    */   {
/*  78:    */     try
/*  79:    */     {
/*  80:159 */       Instances inst = new Instances(new InputStreamReader(System.in));
/*  81:    */       
/*  82:161 */       inst.setClassIndex(inst.numAttributes() - 1);
/*  83:162 */       CostCurve cc = new CostCurve();
/*  84:163 */       EvaluationUtils eu = new EvaluationUtils();
/*  85:164 */       Classifier classifier = new Logistic();
/*  86:165 */       ArrayList<Prediction> predictions = new ArrayList();
/*  87:166 */       for (int i = 0; i < 2; i++)
/*  88:    */       {
/*  89:167 */         eu.setSeed(i);
/*  90:168 */         predictions.addAll(eu.getCVPredictions(classifier, inst, 10));
/*  91:    */       }
/*  92:171 */       Instances result = cc.getCurve(predictions);
/*  93:172 */       System.out.println(result);
/*  94:    */     }
/*  95:    */     catch (Exception ex)
/*  96:    */     {
/*  97:175 */       ex.printStackTrace();
/*  98:    */     }
/*  99:    */   }
/* 100:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.CostCurve
 * JD-Core Version:    0.7.0.1
 */