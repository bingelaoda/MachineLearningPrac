/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.util.Random;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ import weka.core.Utils;
/*   8:    */ 
/*   9:    */ public class CrossValidationSplitResultProducer
/*  10:    */   extends CrossValidationResultProducer
/*  11:    */ {
/*  12:    */   static final long serialVersionUID = 1403798164046795073L;
/*  13:    */   
/*  14:    */   public String globalInfo()
/*  15:    */   {
/*  16:132 */     return "Carries out one split of a repeated k-fold cross-validation, using the set SplitEvaluator to generate some results. Note that the run number is actually the nth split of a repeated k-fold cross-validation, i.e. if k=10, run number 100 is the 10th fold of the 10th cross-validation run. This producer's sole purpose is to allow more fine-grained distribution of cross-validation experiments. If the class attribute is nominal, the dataset is stratified.";
/*  17:    */   }
/*  18:    */   
/*  19:    */   public void doRunKeys(int run)
/*  20:    */     throws Exception
/*  21:    */   {
/*  22:151 */     if (this.m_Instances == null) {
/*  23:152 */       throw new Exception("No Instances set");
/*  24:    */     }
/*  25:156 */     Object[] seKey = this.m_SplitEvaluator.getKey();
/*  26:157 */     Object[] key = new Object[seKey.length + 3];
/*  27:158 */     key[0] = Utils.backQuoteChars(this.m_Instances.relationName());
/*  28:159 */     key[2] = ("" + ((run - 1) % this.m_NumFolds + 1));
/*  29:160 */     key[1] = ("" + ((run - 1) / this.m_NumFolds + 1));
/*  30:161 */     System.arraycopy(seKey, 0, key, 3, seKey.length);
/*  31:162 */     if (this.m_ResultListener.isResultRequired(this, key)) {
/*  32:    */       try
/*  33:    */       {
/*  34:164 */         this.m_ResultListener.acceptResult(this, key, null);
/*  35:    */       }
/*  36:    */       catch (Exception ex)
/*  37:    */       {
/*  38:167 */         throw ex;
/*  39:    */       }
/*  40:    */     }
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void doRun(int run)
/*  44:    */     throws Exception
/*  45:    */   {
/*  46:183 */     if ((getRawOutput()) && 
/*  47:184 */       (this.m_ZipDest == null)) {
/*  48:185 */       this.m_ZipDest = new OutputZipper(this.m_OutputFile);
/*  49:    */     }
/*  50:189 */     if (this.m_Instances == null) {
/*  51:190 */       throw new Exception("No Instances set");
/*  52:    */     }
/*  53:194 */     int fold = (run - 1) % this.m_NumFolds;
/*  54:195 */     run = (run - 1) / this.m_NumFolds + 1;
/*  55:    */     
/*  56:    */ 
/*  57:198 */     Instances runInstances = new Instances(this.m_Instances);
/*  58:199 */     Random random = new Random(run);
/*  59:200 */     runInstances.randomize(random);
/*  60:201 */     if (runInstances.classAttribute().isNominal()) {
/*  61:202 */       runInstances.stratify(this.m_NumFolds);
/*  62:    */     }
/*  63:206 */     Object[] seKey = this.m_SplitEvaluator.getKey();
/*  64:207 */     Object[] key = new Object[seKey.length + 3];
/*  65:208 */     key[0] = Utils.backQuoteChars(this.m_Instances.relationName());
/*  66:209 */     key[1] = ("" + run);
/*  67:210 */     key[2] = ("" + (fold + 1));
/*  68:211 */     System.arraycopy(seKey, 0, key, 3, seKey.length);
/*  69:212 */     if (this.m_ResultListener.isResultRequired(this, key))
/*  70:    */     {
/*  71:216 */       for (int tempFold = 0; tempFold < fold; tempFold++) {
/*  72:217 */         runInstances.trainCV(this.m_NumFolds, tempFold, random);
/*  73:    */       }
/*  74:220 */       Instances train = runInstances.trainCV(this.m_NumFolds, fold, random);
/*  75:221 */       Instances test = runInstances.testCV(this.m_NumFolds, fold);
/*  76:    */       try
/*  77:    */       {
/*  78:223 */         Object[] seResults = this.m_SplitEvaluator.getResult(train, test);
/*  79:224 */         Object[] results = new Object[seResults.length + 1];
/*  80:225 */         results[0] = getTimestamp();
/*  81:226 */         System.arraycopy(seResults, 0, results, 1, seResults.length);
/*  82:227 */         if (this.m_debugOutput)
/*  83:    */         {
/*  84:228 */           String resultName = ("" + run + "." + (fold + 1) + "." + Utils.backQuoteChars(runInstances.relationName()) + "." + this.m_SplitEvaluator.toString()).replace(' ', '_');
/*  85:    */           
/*  86:    */ 
/*  87:231 */           resultName = Utils.removeSubstring(resultName, "weka.classifiers.");
/*  88:232 */           resultName = Utils.removeSubstring(resultName, "weka.filters.");
/*  89:233 */           resultName = Utils.removeSubstring(resultName, "weka.attributeSelection.");
/*  90:    */           
/*  91:235 */           this.m_ZipDest.zipit(this.m_SplitEvaluator.getRawResultOutput(), resultName);
/*  92:    */         }
/*  93:237 */         this.m_ResultListener.acceptResult(this, key, results);
/*  94:    */       }
/*  95:    */       catch (Exception ex)
/*  96:    */       {
/*  97:240 */         throw ex;
/*  98:    */       }
/*  99:    */     }
/* 100:    */   }
/* 101:    */   
/* 102:    */   public String toString()
/* 103:    */   {
/* 104:253 */     String result = "CrossValidationSplitResultProducer: ";
/* 105:254 */     result = result + getCompatibilityState();
/* 106:255 */     if (this.m_Instances == null) {
/* 107:256 */       result = result + ": <null Instances>";
/* 108:    */     } else {
/* 109:258 */       result = result + ": " + Utils.backQuoteChars(this.m_Instances.relationName());
/* 110:    */     }
/* 111:260 */     return result;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public String getRevision()
/* 115:    */   {
/* 116:270 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 117:    */   }
/* 118:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.CrossValidationSplitResultProducer
 * JD-Core Version:    0.7.0.1
 */