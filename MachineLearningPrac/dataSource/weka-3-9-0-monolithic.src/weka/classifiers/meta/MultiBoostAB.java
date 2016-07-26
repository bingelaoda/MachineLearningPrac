/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Random;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.TechnicalInformation;
/*  13:    */ import weka.core.TechnicalInformation.Field;
/*  14:    */ import weka.core.TechnicalInformation.Type;
/*  15:    */ import weka.core.TechnicalInformationHandler;
/*  16:    */ import weka.core.Utils;
/*  17:    */ 
/*  18:    */ public class MultiBoostAB
/*  19:    */   extends AdaBoostM1
/*  20:    */   implements TechnicalInformationHandler
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = -6681619178187935148L;
/*  23:170 */   protected int m_NumSubCmtys = 3;
/*  24:173 */   protected Random m_Random = null;
/*  25:    */   
/*  26:    */   public String globalInfo()
/*  27:    */   {
/*  28:184 */     return "Class for boosting a classifier using the MultiBoosting method.\n\nMultiBoosting is an extension to the highly successful AdaBoost technique for forming decision committees. MultiBoosting can be viewed as combining AdaBoost with wagging. It is able to harness both AdaBoost's high bias and variance reduction with wagging's superior variance reduction. Using C4.5 as the base learning algorithm, Multi-boosting is demonstrated to produce decision committees with lower error than either AdaBoost or wagging significantly more often than the reverse over a large representative cross-section of UCI data sets. It offers the further advantage over AdaBoost of suiting parallel execution.\n\nFor more information, see\n\n" + getTechnicalInformation().toString();
/*  29:    */   }
/*  30:    */   
/*  31:    */   public TechnicalInformation getTechnicalInformation()
/*  32:    */   {
/*  33:209 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  34:210 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Geoffrey I. Webb");
/*  35:211 */     result.setValue(TechnicalInformation.Field.YEAR, "2000");
/*  36:212 */     result.setValue(TechnicalInformation.Field.TITLE, "MultiBoosting: A Technique for Combining Boosting and Wagging");
/*  37:    */     
/*  38:214 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/*  39:215 */     result.setValue(TechnicalInformation.Field.VOLUME, "Vol.40");
/*  40:216 */     result.setValue(TechnicalInformation.Field.NUMBER, "No.2");
/*  41:217 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Kluwer Academic Publishers");
/*  42:218 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Boston");
/*  43:    */     
/*  44:220 */     return result;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public Enumeration<Option> listOptions()
/*  48:    */   {
/*  49:231 */     Vector<Option> vec = new Vector(1);
/*  50:    */     
/*  51:233 */     vec.addElement(new Option("\tNumber of sub-committees. (Default 3)", "C", 1, "-C <num>"));
/*  52:    */     
/*  53:    */ 
/*  54:236 */     vec.addAll(Collections.list(super.listOptions()));
/*  55:    */     
/*  56:238 */     return vec.elements();
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setOptions(String[] options)
/*  60:    */     throws Exception
/*  61:    */   {
/*  62:309 */     String subcmtyString = Utils.getOption('C', options);
/*  63:310 */     if (subcmtyString.length() != 0) {
/*  64:311 */       setNumSubCmtys(Integer.parseInt(subcmtyString));
/*  65:    */     } else {
/*  66:313 */       setNumSubCmtys(3);
/*  67:    */     }
/*  68:316 */     super.setOptions(options);
/*  69:    */     
/*  70:318 */     Utils.checkForRemainingOptions(options);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public String[] getOptions()
/*  74:    */   {
/*  75:329 */     Vector<String> options = new Vector();
/*  76:    */     
/*  77:331 */     options.add("-C");
/*  78:332 */     options.add("" + getNumSubCmtys());
/*  79:    */     
/*  80:334 */     Collections.addAll(options, super.getOptions());
/*  81:    */     
/*  82:336 */     return (String[])options.toArray(new String[0]);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public String numSubCmtysTipText()
/*  86:    */   {
/*  87:346 */     return "Sets the (approximate) number of subcommittees.";
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void setNumSubCmtys(int subc)
/*  91:    */   {
/*  92:356 */     this.m_NumSubCmtys = subc;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public int getNumSubCmtys()
/*  96:    */   {
/*  97:366 */     return this.m_NumSubCmtys;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public void buildClassifier(Instances training)
/* 101:    */     throws Exception
/* 102:    */   {
/* 103:378 */     this.m_Random = new Random(this.m_Seed);
/* 104:    */     
/* 105:380 */     super.buildClassifier(training);
/* 106:    */     
/* 107:382 */     this.m_Random = null;
/* 108:    */   }
/* 109:    */   
/* 110:    */   protected void setWeights(Instances training, double reweight)
/* 111:    */     throws Exception
/* 112:    */   {
/* 113:396 */     int subCmtySize = this.m_Classifiers.length / this.m_NumSubCmtys;
/* 114:398 */     if ((this.m_NumIterationsPerformed + 1) % subCmtySize == 0)
/* 115:    */     {
/* 116:400 */       if (getDebug()) {
/* 117:401 */         System.err.println(this.m_NumIterationsPerformed + " " + subCmtySize);
/* 118:    */       }
/* 119:404 */       double oldSumOfWeights = training.sumOfWeights();
/* 120:408 */       for (int i = 0; i < training.numInstances(); i++) {
/* 121:409 */         training.instance(i).setWeight(-Math.log(this.m_Random.nextDouble() * 9999.0D / 10000.0D));
/* 122:    */       }
/* 123:414 */       double sumProbs = training.sumOfWeights();
/* 124:415 */       for (int i = 0; i < training.numInstances(); i++) {
/* 125:416 */         training.instance(i).setWeight(training.instance(i).weight() * oldSumOfWeights / sumProbs);
/* 126:    */       }
/* 127:    */     }
/* 128:    */     else
/* 129:    */     {
/* 130:420 */       super.setWeights(training, reweight);
/* 131:    */     }
/* 132:    */   }
/* 133:    */   
/* 134:    */   public String toString()
/* 135:    */   {
/* 136:433 */     if (this.m_ZeroR != null)
/* 137:    */     {
/* 138:434 */       StringBuffer buf = new StringBuffer();
/* 139:435 */       buf.append(getClass().getName().replaceAll(".*\\.", "") + "\n");
/* 140:436 */       buf.append(getClass().getName().replaceAll(".*\\.", "").replaceAll(".", "=") + "\n\n");
/* 141:    */       
/* 142:    */ 
/* 143:439 */       buf.append("Warning: No model could be built, hence ZeroR model is used:\n\n");
/* 144:    */       
/* 145:441 */       buf.append(this.m_ZeroR.toString());
/* 146:442 */       return buf.toString();
/* 147:    */     }
/* 148:445 */     StringBuffer text = new StringBuffer();
/* 149:447 */     if (this.m_NumIterations == 0)
/* 150:    */     {
/* 151:448 */       text.append("MultiBoostAB: No model built yet.\n");
/* 152:    */     }
/* 153:449 */     else if (this.m_NumIterations == 1)
/* 154:    */     {
/* 155:450 */       text.append("MultiBoostAB: No boosting possible, one classifier used!\n");
/* 156:451 */       text.append(this.m_Classifiers[0].toString() + "\n");
/* 157:    */     }
/* 158:    */     else
/* 159:    */     {
/* 160:453 */       text.append("MultiBoostAB: Base classifiers and their weights: \n\n");
/* 161:454 */       for (int i = 0; i < this.m_NumIterations; i++) {
/* 162:455 */         if ((this.m_Classifiers != null) && (this.m_Classifiers[i] != null))
/* 163:    */         {
/* 164:456 */           text.append(this.m_Classifiers[i].toString() + "\n\n");
/* 165:457 */           text.append("Weight: " + Utils.roundDouble(this.m_Betas[i], 2) + "\n\n");
/* 166:    */         }
/* 167:    */         else
/* 168:    */         {
/* 169:459 */           text.append("not yet initialized!\n\n");
/* 170:    */         }
/* 171:    */       }
/* 172:462 */       text.append("Number of performed Iterations: " + this.m_NumIterations + "\n");
/* 173:    */     }
/* 174:465 */     return text.toString();
/* 175:    */   }
/* 176:    */   
/* 177:    */   public String getRevision()
/* 178:    */   {
/* 179:475 */     return RevisionUtils.extract("$Revision: 10366 $");
/* 180:    */   }
/* 181:    */   
/* 182:    */   public static void main(String[] argv)
/* 183:    */   {
/* 184:484 */     runClassifier(new MultiBoostAB(), argv);
/* 185:    */   }
/* 186:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.MultiBoostAB
 * JD-Core Version:    0.7.0.1
 */