/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import weka.classifiers.Classifier;
/*   4:    */ import weka.classifiers.UpdateableClassifier;
/*   5:    */ import weka.classifiers.functions.SGD;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.OptionHandler;
/*  10:    */ import weka.core.Range;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.Utils;
/*  13:    */ import weka.filters.Filter;
/*  14:    */ import weka.filters.unsupervised.instance.RemoveWithValues;
/*  15:    */ 
/*  16:    */ public class MultiClassClassifierUpdateable
/*  17:    */   extends MultiClassClassifier
/*  18:    */   implements OptionHandler, UpdateableClassifier
/*  19:    */ {
/*  20:    */   private static final long serialVersionUID = -1619685269774366430L;
/*  21:    */   
/*  22:    */   public MultiClassClassifierUpdateable()
/*  23:    */   {
/*  24: 84 */     this.m_Classifier = new SGD();
/*  25:    */   }
/*  26:    */   
/*  27:    */   public String globalInfo()
/*  28:    */   {
/*  29: 94 */     return "A metaclassifier for handling multi-class datasets with 2-class classifiers. This classifier is also capable of applying error correcting output codes for increased accuracy. The base classifier must be an updateable classifier";
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void buildClassifier(Instances insts)
/*  33:    */     throws Exception
/*  34:    */   {
/*  35:102 */     if (this.m_Classifier == null) {
/*  36:103 */       throw new Exception("No base classifier has been set!");
/*  37:    */     }
/*  38:106 */     if (!(this.m_Classifier instanceof UpdateableClassifier)) {
/*  39:107 */       throw new Exception("Base classifier must be updateable!");
/*  40:    */     }
/*  41:110 */     super.buildClassifier(insts);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void updateClassifier(Instance instance)
/*  45:    */     throws Exception
/*  46:    */   {
/*  47:122 */     if (!instance.classIsMissing())
/*  48:    */     {
/*  49:124 */       if (this.m_Classifiers.length == 1)
/*  50:    */       {
/*  51:125 */         ((UpdateableClassifier)this.m_Classifiers[0]).updateClassifier(instance);
/*  52:126 */         return;
/*  53:    */       }
/*  54:129 */       for (int i = 0; i < this.m_Classifiers.length; i++) {
/*  55:130 */         if (this.m_Classifiers[i] != null)
/*  56:    */         {
/*  57:131 */           this.m_ClassFilters[i].input(instance);
/*  58:132 */           Instance converted = this.m_ClassFilters[i].output();
/*  59:133 */           if (converted != null)
/*  60:    */           {
/*  61:134 */             converted.dataset().setClassIndex(this.m_ClassAttribute.index());
/*  62:135 */             ((UpdateableClassifier)this.m_Classifiers[i]).updateClassifier(converted);
/*  63:138 */             if (this.m_Method == 3) {
/*  64:139 */               this.m_SumOfWeights[i] += converted.weight();
/*  65:    */             }
/*  66:    */           }
/*  67:    */         }
/*  68:    */       }
/*  69:    */     }
/*  70:    */   }
/*  71:    */   
/*  72:    */   public double[] distributionForInstance(Instance inst)
/*  73:    */     throws Exception
/*  74:    */   {
/*  75:157 */     if (this.m_Classifiers.length == 1) {
/*  76:158 */       return this.m_Classifiers[0].distributionForInstance(inst);
/*  77:    */     }
/*  78:161 */     double[] probs = new double[inst.numClasses()];
/*  79:162 */     if (this.m_Method == 3)
/*  80:    */     {
/*  81:163 */       double[][] r = new double[inst.numClasses()][inst.numClasses()];
/*  82:164 */       double[][] n = new double[inst.numClasses()][inst.numClasses()];
/*  83:166 */       for (int i = 0; i < this.m_ClassFilters.length; i++) {
/*  84:167 */         if ((this.m_Classifiers[i] != null) && (this.m_SumOfWeights[i] > 0.0D))
/*  85:    */         {
/*  86:168 */           Instance tempInst = (Instance)inst.copy();
/*  87:169 */           tempInst.setDataset(this.m_TwoClassDataset);
/*  88:170 */           double[] current = this.m_Classifiers[i].distributionForInstance(tempInst);
/*  89:171 */           Range range = new Range(((RemoveWithValues)this.m_ClassFilters[i]).getNominalIndices());
/*  90:    */           
/*  91:173 */           range.setUpper(this.m_ClassAttribute.numValues());
/*  92:174 */           int[] pair = range.getSelection();
/*  93:175 */           if ((this.m_pairwiseCoupling) && (inst.numClasses() > 2))
/*  94:    */           {
/*  95:176 */             r[pair[0]][pair[1]] = current[0];
/*  96:177 */             n[pair[0]][pair[1]] = this.m_SumOfWeights[i];
/*  97:    */           }
/*  98:179 */           else if (current[0] > current[1])
/*  99:    */           {
/* 100:180 */             probs[pair[0]] += 1.0D;
/* 101:    */           }
/* 102:    */           else
/* 103:    */           {
/* 104:182 */             probs[pair[1]] += 1.0D;
/* 105:    */           }
/* 106:    */         }
/* 107:    */       }
/* 108:187 */       if ((this.m_pairwiseCoupling) && (inst.numClasses() > 2)) {
/* 109:    */         try
/* 110:    */         {
/* 111:189 */           return pairwiseCoupling(n, r);
/* 112:    */         }
/* 113:    */         catch (IllegalArgumentException ex) {}
/* 114:    */       }
/* 115:193 */       if (Utils.gr(Utils.sum(probs), 0.0D)) {
/* 116:194 */         Utils.normalize(probs);
/* 117:    */       }
/* 118:196 */       return probs;
/* 119:    */     }
/* 120:198 */     probs = super.distributionForInstance(inst);
/* 121:    */     
/* 122:    */ 
/* 123:    */ 
/* 124:    */ 
/* 125:    */ 
/* 126:    */ 
/* 127:    */ 
/* 128:206 */     return probs;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public String getRevision()
/* 132:    */   {
/* 133:216 */     return RevisionUtils.extract("$Revision: 9248 $");
/* 134:    */   }
/* 135:    */   
/* 136:    */   public static void main(String[] argv)
/* 137:    */   {
/* 138:225 */     runClassifier(new MultiClassClassifierUpdateable(), argv);
/* 139:    */   }
/* 140:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.MultiClassClassifierUpdateable
 * JD-Core Version:    0.7.0.1
 */