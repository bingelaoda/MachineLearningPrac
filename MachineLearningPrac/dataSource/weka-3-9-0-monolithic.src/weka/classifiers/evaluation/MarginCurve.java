/*   1:    */ package weka.classifiers.evaluation;
/*   2:    */ 
/*   3:    */ import java.io.InputStreamReader;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import weka.classifiers.meta.LogitBoost;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.DenseInstance;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.RevisionHandler;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.Utils;
/*  14:    */ 
/*  15:    */ public class MarginCurve
/*  16:    */   implements RevisionHandler
/*  17:    */ {
/*  18:    */   public Instances getCurve(ArrayList<Prediction> predictions)
/*  19:    */   {
/*  20: 66 */     if (predictions.size() == 0) {
/*  21: 67 */       return null;
/*  22:    */     }
/*  23: 70 */     Instances insts = makeHeader();
/*  24: 71 */     double[] margins = getMargins(predictions);
/*  25: 72 */     int[] sorted = Utils.sort(margins);
/*  26: 73 */     int binMargin = 0;
/*  27: 74 */     int totalMargin = 0;
/*  28: 75 */     insts.add(makeInstance(-1.0D, binMargin, totalMargin));
/*  29: 76 */     for (int element : sorted)
/*  30:    */     {
/*  31: 77 */       double current = margins[element];
/*  32: 78 */       double weight = ((NominalPrediction)predictions.get(element)).weight();
/*  33: 79 */       totalMargin = (int)(totalMargin + weight);
/*  34: 80 */       binMargin = (int)(binMargin + weight);
/*  35:    */       
/*  36: 82 */       insts.add(makeInstance(current, binMargin, totalMargin));
/*  37: 83 */       binMargin = 0;
/*  38:    */     }
/*  39: 86 */     return insts;
/*  40:    */   }
/*  41:    */   
/*  42:    */   private double[] getMargins(ArrayList<Prediction> predictions)
/*  43:    */   {
/*  44: 98 */     double[] margins = new double[predictions.size()];
/*  45: 99 */     for (int i = 0; i < margins.length; i++)
/*  46:    */     {
/*  47:100 */       NominalPrediction pred = (NominalPrediction)predictions.get(i);
/*  48:101 */       margins[i] = pred.margin();
/*  49:    */     }
/*  50:103 */     return margins;
/*  51:    */   }
/*  52:    */   
/*  53:    */   private Instances makeHeader()
/*  54:    */   {
/*  55:113 */     ArrayList<Attribute> fv = new ArrayList();
/*  56:114 */     fv.add(new Attribute("Margin"));
/*  57:115 */     fv.add(new Attribute("Current"));
/*  58:116 */     fv.add(new Attribute("Cumulative"));
/*  59:117 */     return new Instances("MarginCurve", fv, 100);
/*  60:    */   }
/*  61:    */   
/*  62:    */   private Instance makeInstance(double margin, int current, int cumulative)
/*  63:    */   {
/*  64:131 */     int count = 0;
/*  65:132 */     double[] vals = new double[3];
/*  66:133 */     vals[(count++)] = margin;
/*  67:134 */     vals[(count++)] = current;
/*  68:135 */     vals[(count++)] = cumulative;
/*  69:136 */     return new DenseInstance(1.0D, vals);
/*  70:    */   }
/*  71:    */   
/*  72:    */   public String getRevision()
/*  73:    */   {
/*  74:146 */     return RevisionUtils.extract("$Revision: 10153 $");
/*  75:    */   }
/*  76:    */   
/*  77:    */   public static void main(String[] args)
/*  78:    */   {
/*  79:    */     try
/*  80:    */     {
/*  81:158 */       Utils.SMALL = 0.0D;
/*  82:159 */       Instances inst = new Instances(new InputStreamReader(System.in));
/*  83:160 */       inst.setClassIndex(inst.numAttributes() - 1);
/*  84:161 */       MarginCurve tc = new MarginCurve();
/*  85:162 */       EvaluationUtils eu = new EvaluationUtils();
/*  86:163 */       LogitBoost classifier = new LogitBoost();
/*  87:164 */       classifier.setNumIterations(20);
/*  88:165 */       ArrayList<Prediction> predictions = eu.getTrainTestPredictions(classifier, inst, inst);
/*  89:    */       
/*  90:167 */       Instances result = tc.getCurve(predictions);
/*  91:168 */       System.out.println(result);
/*  92:    */     }
/*  93:    */     catch (Exception ex)
/*  94:    */     {
/*  95:170 */       ex.printStackTrace();
/*  96:    */     }
/*  97:    */   }
/*  98:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.MarginCurve
 * JD-Core Version:    0.7.0.1
 */