/*   1:    */ package weka.classifiers.trees.lmt;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.Instance;
/*   5:    */ import weka.core.Instances;
/*   6:    */ 
/*   7:    */ public class SimpleLinearRegression
/*   8:    */   implements Serializable
/*   9:    */ {
/*  10:    */   static final long serialVersionUID = 1779336022895414137L;
/*  11: 42 */   private int m_attributeIndex = -1;
/*  12: 45 */   private double m_slope = (0.0D / 0.0D);
/*  13: 48 */   private double m_intercept = (0.0D / 0.0D);
/*  14:    */   
/*  15:    */   public SimpleLinearRegression() {}
/*  16:    */   
/*  17:    */   public SimpleLinearRegression(int attIndex, double slope, double intercept)
/*  18:    */   {
/*  19: 62 */     this.m_attributeIndex = attIndex;
/*  20: 63 */     this.m_slope = slope;
/*  21: 64 */     this.m_intercept = intercept;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public void addModel(SimpleLinearRegression slr)
/*  25:    */     throws Exception
/*  26:    */   {
/*  27: 74 */     this.m_attributeIndex = slr.m_attributeIndex;
/*  28: 75 */     if (this.m_attributeIndex != -1)
/*  29:    */     {
/*  30: 76 */       this.m_slope += slr.m_slope;
/*  31: 77 */       this.m_intercept += slr.m_intercept;
/*  32:    */     }
/*  33:    */     else
/*  34:    */     {
/*  35: 79 */       this.m_slope = slr.m_slope;
/*  36: 80 */       this.m_intercept = slr.m_intercept;
/*  37:    */     }
/*  38:    */   }
/*  39:    */   
/*  40:    */   public double classifyInstance(Instance inst)
/*  41:    */   {
/*  42: 92 */     return this.m_intercept + this.m_slope * inst.value(this.m_attributeIndex);
/*  43:    */   }
/*  44:    */   
/*  45:    */   protected double[] computeMeans(Instances insts)
/*  46:    */   {
/*  47:102 */     double[] means = new double[insts.numAttributes()];
/*  48:103 */     double[] counts = new double[insts.numAttributes()];
/*  49:104 */     for (int j = 0; j < insts.numInstances(); j++)
/*  50:    */     {
/*  51:105 */       Instance inst = insts.instance(j);
/*  52:106 */       for (int i = 0; i < insts.numAttributes(); i++)
/*  53:    */       {
/*  54:107 */         means[i] += inst.weight() * inst.value(i);
/*  55:108 */         counts[i] += inst.weight();
/*  56:    */       }
/*  57:    */     }
/*  58:112 */     for (int i = 0; i < insts.numAttributes(); i++) {
/*  59:113 */       if (counts[i] > 0.0D) {
/*  60:114 */         means[i] /= counts[i];
/*  61:    */       } else {
/*  62:116 */         means[i] = 0.0D;
/*  63:    */       }
/*  64:    */     }
/*  65:119 */     return means;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void buildClassifier(Instances insts)
/*  69:    */   {
/*  70:130 */     double[] means = computeMeans(insts);
/*  71:131 */     double[] slopes = new double[insts.numAttributes()];
/*  72:132 */     double[] sumWeightedDiffsSquared = new double[insts.numAttributes()];
/*  73:133 */     int classIndex = insts.classIndex();
/*  74:136 */     for (int j = 0; j < insts.numInstances(); j++)
/*  75:    */     {
/*  76:137 */       Instance inst = insts.instance(j);
/*  77:    */       
/*  78:139 */       double yDiff = inst.value(classIndex) - means[classIndex];
/*  79:140 */       double weightedYDiff = inst.weight() * yDiff;
/*  80:143 */       for (int i = 0; i < insts.numAttributes(); i++)
/*  81:    */       {
/*  82:144 */         double diff = inst.value(i) - means[i];
/*  83:145 */         double weightedDiff = inst.weight() * diff;
/*  84:    */         
/*  85:    */ 
/*  86:148 */         slopes[i] += weightedYDiff * diff;
/*  87:    */         
/*  88:    */ 
/*  89:151 */         sumWeightedDiffsSquared[i] += weightedDiff * diff;
/*  90:    */       }
/*  91:    */     }
/*  92:156 */     double minSSE = 1.7976931348623157E+308D;
/*  93:157 */     this.m_attributeIndex = -1;
/*  94:158 */     for (int i = 0; i < insts.numAttributes(); i++) {
/*  95:161 */       if ((i != classIndex) && (sumWeightedDiffsSquared[i] != 0.0D))
/*  96:    */       {
/*  97:166 */         double numerator = slopes[i];
/*  98:167 */         slopes[i] /= sumWeightedDiffsSquared[i];
/*  99:168 */         double intercept = means[classIndex] - slopes[i] * means[i];
/* 100:    */         
/* 101:    */ 
/* 102:171 */         double sse = sumWeightedDiffsSquared[classIndex] - slopes[i] * numerator;
/* 103:174 */         if (sse < minSSE)
/* 104:    */         {
/* 105:175 */           minSSE = sse;
/* 106:176 */           this.m_attributeIndex = i;
/* 107:177 */           this.m_slope = slopes[i];
/* 108:178 */           this.m_intercept = intercept;
/* 109:    */         }
/* 110:    */       }
/* 111:    */     }
/* 112:    */   }
/* 113:    */   
/* 114:    */   public boolean foundUsefulAttribute()
/* 115:    */   {
/* 116:189 */     return this.m_attributeIndex != -1;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public int getAttributeIndex()
/* 120:    */   {
/* 121:198 */     return this.m_attributeIndex;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public double getSlope()
/* 125:    */   {
/* 126:207 */     return this.m_slope;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public double getIntercept()
/* 130:    */   {
/* 131:216 */     return this.m_intercept;
/* 132:    */   }
/* 133:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.lmt.SimpleLinearRegression
 * JD-Core Version:    0.7.0.1
 */