/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.util.Hashtable;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.classifiers.AbstractClassifier;
/*   6:    */ import weka.classifiers.Classifier;
/*   7:    */ import weka.classifiers.RandomizableIteratedSingleClassifierEnhancer;
/*   8:    */ import weka.classifiers.meta.nestedDichotomies.ClassBalancedND;
/*   9:    */ import weka.classifiers.meta.nestedDichotomies.DataNearBalancedND;
/*  10:    */ import weka.classifiers.meta.nestedDichotomies.ND;
/*  11:    */ import weka.core.Attribute;
/*  12:    */ import weka.core.Capabilities;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Randomizable;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ import weka.core.TechnicalInformation;
/*  18:    */ import weka.core.TechnicalInformation.Field;
/*  19:    */ import weka.core.TechnicalInformation.Type;
/*  20:    */ import weka.core.TechnicalInformationHandler;
/*  21:    */ import weka.core.Utils;
/*  22:    */ 
/*  23:    */ public class END
/*  24:    */   extends RandomizableIteratedSingleClassifierEnhancer
/*  25:    */   implements TechnicalInformationHandler
/*  26:    */ {
/*  27:    */   static final long serialVersionUID = -4143242362912214956L;
/*  28:204 */   protected Hashtable<String, Classifier> m_hashtable = null;
/*  29:    */   
/*  30:    */   public END()
/*  31:    */   {
/*  32:211 */     this.m_Classifier = new ND();
/*  33:    */   }
/*  34:    */   
/*  35:    */   protected String defaultClassifierString()
/*  36:    */   {
/*  37:222 */     return "weka.classifiers.meta.nestedDichotomies.ND";
/*  38:    */   }
/*  39:    */   
/*  40:    */   public String globalInfo()
/*  41:    */   {
/*  42:233 */     return "A meta classifier for handling multi-class datasets with 2-class classifiers by building an ensemble of nested dichotomies.\n\nFor more info, check\n\n" + getTechnicalInformation().toString();
/*  43:    */   }
/*  44:    */   
/*  45:    */   public TechnicalInformation getTechnicalInformation()
/*  46:    */   {
/*  47:250 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  48:251 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Lin Dong and Eibe Frank and Stefan Kramer");
/*  49:252 */     result.setValue(TechnicalInformation.Field.TITLE, "Ensembles of Balanced Nested Dichotomies for Multi-class Problems");
/*  50:    */     
/*  51:254 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "PKDD");
/*  52:255 */     result.setValue(TechnicalInformation.Field.YEAR, "2005");
/*  53:256 */     result.setValue(TechnicalInformation.Field.PAGES, "84-95");
/*  54:257 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*  55:    */     
/*  56:259 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/*  57:260 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Eibe Frank and Stefan Kramer");
/*  58:261 */     additional.setValue(TechnicalInformation.Field.TITLE, "Ensembles of nested dichotomies for multi-class problems");
/*  59:    */     
/*  60:263 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "Twenty-first International Conference on Machine Learning");
/*  61:    */     
/*  62:265 */     additional.setValue(TechnicalInformation.Field.YEAR, "2004");
/*  63:266 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "ACM");
/*  64:    */     
/*  65:268 */     return result;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public Capabilities getCapabilities()
/*  69:    */   {
/*  70:278 */     Capabilities result = super.getCapabilities();
/*  71:    */     
/*  72:    */ 
/*  73:281 */     result.setMinimumNumberInstances(1);
/*  74:    */     
/*  75:    */ 
/*  76:284 */     return result;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void buildClassifier(Instances data)
/*  80:    */     throws Exception
/*  81:    */   {
/*  82:298 */     getCapabilities().testWithFail(data);
/*  83:    */     
/*  84:    */ 
/*  85:301 */     data = new Instances(data);
/*  86:302 */     data.deleteWithMissingClass();
/*  87:304 */     if ((!(this.m_Classifier instanceof ND)) && (!(this.m_Classifier instanceof ClassBalancedND)) && (!(this.m_Classifier instanceof DataNearBalancedND))) {
/*  88:307 */       throw new IllegalArgumentException("END only works with ND, ClassBalancedND or DataNearBalancedND classifier");
/*  89:    */     }
/*  90:312 */     this.m_hashtable = new Hashtable();
/*  91:    */     
/*  92:314 */     this.m_Classifiers = AbstractClassifier.makeCopies(this.m_Classifier, this.m_NumIterations);
/*  93:    */     
/*  94:    */ 
/*  95:317 */     Random random = data.getRandomNumberGenerator(this.m_Seed);
/*  96:318 */     for (Classifier m_Classifier2 : this.m_Classifiers)
/*  97:    */     {
/*  98:321 */       ((Randomizable)m_Classifier2).setSeed(random.nextInt());
/*  99:324 */       if ((this.m_Classifier instanceof ND)) {
/* 100:325 */         ((ND)m_Classifier2).setHashtable(this.m_hashtable);
/* 101:327 */       } else if ((this.m_Classifier instanceof ClassBalancedND)) {
/* 102:328 */         ((ClassBalancedND)m_Classifier2).setHashtable(this.m_hashtable);
/* 103:330 */       } else if ((this.m_Classifier instanceof DataNearBalancedND)) {
/* 104:331 */         ((DataNearBalancedND)m_Classifier2).setHashtable(this.m_hashtable);
/* 105:    */       }
/* 106:336 */       m_Classifier2.buildClassifier(data);
/* 107:    */     }
/* 108:    */   }
/* 109:    */   
/* 110:    */   public double[] distributionForInstance(Instance instance)
/* 111:    */     throws Exception
/* 112:    */   {
/* 113:350 */     double[] sums = new double[instance.numClasses()];
/* 114:352 */     for (int i = 0; i < this.m_NumIterations; i++) {
/* 115:353 */       if (instance.classAttribute().isNumeric() == true)
/* 116:    */       {
/* 117:354 */         sums[0] += this.m_Classifiers[i].classifyInstance(instance);
/* 118:    */       }
/* 119:    */       else
/* 120:    */       {
/* 121:356 */         double[] newProbs = this.m_Classifiers[i].distributionForInstance(instance);
/* 122:357 */         for (int j = 0; j < newProbs.length; j++) {
/* 123:358 */           sums[j] += newProbs[j];
/* 124:    */         }
/* 125:    */       }
/* 126:    */     }
/* 127:362 */     if (instance.classAttribute().isNumeric() == true)
/* 128:    */     {
/* 129:363 */       sums[0] /= this.m_NumIterations;
/* 130:364 */       return sums;
/* 131:    */     }
/* 132:365 */     if (Utils.eq(Utils.sum(sums), 0.0D)) {
/* 133:366 */       return sums;
/* 134:    */     }
/* 135:368 */     Utils.normalize(sums);
/* 136:369 */     return sums;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public String toString()
/* 140:    */   {
/* 141:381 */     if (this.m_Classifiers == null) {
/* 142:382 */       return "END: No model built yet.";
/* 143:    */     }
/* 144:384 */     StringBuffer text = new StringBuffer();
/* 145:385 */     text.append("All the base classifiers: \n\n");
/* 146:386 */     for (Classifier m_Classifier2 : this.m_Classifiers) {
/* 147:387 */       text.append(m_Classifier2.toString() + "\n\n");
/* 148:    */     }
/* 149:390 */     return text.toString();
/* 150:    */   }
/* 151:    */   
/* 152:    */   public String getRevision()
/* 153:    */   {
/* 154:400 */     return RevisionUtils.extract("$Revision: 10342 $");
/* 155:    */   }
/* 156:    */   
/* 157:    */   public static void main(String[] argv)
/* 158:    */   {
/* 159:409 */     runClassifier(new END(), argv);
/* 160:    */   }
/* 161:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.END
 * JD-Core Version:    0.7.0.1
 */