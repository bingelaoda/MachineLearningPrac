/*   1:    */ package weka.classifiers.trees.j48;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.RevisionHandler;
/*   9:    */ import weka.core.Utils;
/*  10:    */ 
/*  11:    */ public abstract class ClassifierSplitModel
/*  12:    */   implements Cloneable, Serializable, RevisionHandler
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 4280730118393457457L;
/*  15:    */   protected Distribution m_distribution;
/*  16:    */   protected int m_numSubsets;
/*  17:    */   
/*  18:    */   public Object clone()
/*  19:    */   {
/*  20: 55 */     Object clone = null;
/*  21:    */     try
/*  22:    */     {
/*  23: 58 */       clone = super.clone();
/*  24:    */     }
/*  25:    */     catch (CloneNotSupportedException e) {}
/*  26: 61 */     return clone;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public abstract void buildClassifier(Instances paramInstances)
/*  30:    */     throws Exception;
/*  31:    */   
/*  32:    */   public final boolean checkModel()
/*  33:    */   {
/*  34: 76 */     if (this.m_numSubsets > 0) {
/*  35: 77 */       return true;
/*  36:    */     }
/*  37: 79 */     return false;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public final double classifyInstance(Instance instance)
/*  41:    */     throws Exception
/*  42:    */   {
/*  43: 92 */     int theSubset = whichSubset(instance);
/*  44: 93 */     if (theSubset > -1) {
/*  45: 94 */       return this.m_distribution.maxClass(theSubset);
/*  46:    */     }
/*  47: 96 */     return this.m_distribution.maxClass();
/*  48:    */   }
/*  49:    */   
/*  50:    */   public double classProb(int classIndex, Instance instance, int theSubset)
/*  51:    */     throws Exception
/*  52:    */   {
/*  53:107 */     if (theSubset > -1) {
/*  54:108 */       return this.m_distribution.prob(classIndex, theSubset);
/*  55:    */     }
/*  56:110 */     double[] weights = weights(instance);
/*  57:111 */     if (weights == null) {
/*  58:112 */       return this.m_distribution.prob(classIndex);
/*  59:    */     }
/*  60:114 */     double prob = 0.0D;
/*  61:115 */     for (int i = 0; i < weights.length; i++) {
/*  62:116 */       prob += weights[i] * this.m_distribution.prob(classIndex, i);
/*  63:    */     }
/*  64:118 */     return prob;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public double classProbLaplace(int classIndex, Instance instance, int theSubset)
/*  68:    */     throws Exception
/*  69:    */   {
/*  70:131 */     if (theSubset > -1) {
/*  71:132 */       return this.m_distribution.laplaceProb(classIndex, theSubset);
/*  72:    */     }
/*  73:134 */     double[] weights = weights(instance);
/*  74:135 */     if (weights == null) {
/*  75:136 */       return this.m_distribution.laplaceProb(classIndex);
/*  76:    */     }
/*  77:138 */     double prob = 0.0D;
/*  78:139 */     for (int i = 0; i < weights.length; i++) {
/*  79:140 */       prob += weights[i] * this.m_distribution.laplaceProb(classIndex, i);
/*  80:    */     }
/*  81:142 */     return prob;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public double codingCost()
/*  85:    */   {
/*  86:152 */     return 0.0D;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public final Distribution distribution()
/*  90:    */   {
/*  91:160 */     return this.m_distribution;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public abstract String leftSide(Instances paramInstances);
/*  95:    */   
/*  96:    */   public abstract String rightSide(int paramInt, Instances paramInstances);
/*  97:    */   
/*  98:    */   public final String dumpLabel(int index, Instances data)
/*  99:    */     throws Exception
/* 100:    */   {
/* 101:184 */     StringBuffer text = new StringBuffer();
/* 102:185 */     text.append(data.classAttribute().value(this.m_distribution.maxClass(index)));
/* 103:    */     
/* 104:187 */     text.append(" (" + Utils.roundDouble(this.m_distribution.perBag(index), 2));
/* 105:188 */     if (Utils.gr(this.m_distribution.numIncorrect(index), 0.0D)) {
/* 106:189 */       text.append("/" + Utils.roundDouble(this.m_distribution.numIncorrect(index), 2));
/* 107:    */     }
/* 108:190 */     text.append(")");
/* 109:    */     
/* 110:192 */     return text.toString();
/* 111:    */   }
/* 112:    */   
/* 113:    */   public final String sourceClass(int index, Instances data)
/* 114:    */     throws Exception
/* 115:    */   {
/* 116:197 */     System.err.println("sourceClass");
/* 117:198 */     return this.m_distribution.maxClass(index);
/* 118:    */   }
/* 119:    */   
/* 120:    */   public abstract String sourceExpression(int paramInt, Instances paramInstances);
/* 121:    */   
/* 122:    */   public final String dumpModel(Instances data)
/* 123:    */     throws Exception
/* 124:    */   {
/* 125:213 */     StringBuffer text = new StringBuffer();
/* 126:214 */     for (int i = 0; i < this.m_numSubsets; i++)
/* 127:    */     {
/* 128:215 */       text.append(leftSide(data) + rightSide(i, data) + ": ");
/* 129:216 */       text.append(dumpLabel(i, data) + "\n");
/* 130:    */     }
/* 131:218 */     return text.toString();
/* 132:    */   }
/* 133:    */   
/* 134:    */   public final int numSubsets()
/* 135:    */   {
/* 136:226 */     return this.m_numSubsets;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public void resetDistribution(Instances data)
/* 140:    */     throws Exception
/* 141:    */   {
/* 142:234 */     this.m_distribution = new Distribution(data, this);
/* 143:    */   }
/* 144:    */   
/* 145:    */   public final Instances[] split(Instances data)
/* 146:    */     throws Exception
/* 147:    */   {
/* 148:246 */     int[] subsetSize = new int[this.m_numSubsets];
/* 149:247 */     for (Instance instance : data)
/* 150:    */     {
/* 151:248 */       int subset = whichSubset(instance);
/* 152:249 */       if (subset > -1)
/* 153:    */       {
/* 154:250 */         subsetSize[subset] += 1;
/* 155:    */       }
/* 156:    */       else
/* 157:    */       {
/* 158:252 */         double[] weights = weights(instance);
/* 159:253 */         for (int j = 0; j < this.m_numSubsets; j++) {
/* 160:254 */           if (Utils.gr(weights[j], 0.0D)) {
/* 161:255 */             subsetSize[j] += 1;
/* 162:    */           }
/* 163:    */         }
/* 164:    */       }
/* 165:    */     }
/* 166:262 */     Instances[] instances = new Instances[this.m_numSubsets];
/* 167:263 */     for (int j = 0; j < this.m_numSubsets; j++) {
/* 168:264 */       instances[j] = new Instances(data, subsetSize[j]);
/* 169:    */     }
/* 170:266 */     for (Instance instance : data)
/* 171:    */     {
/* 172:267 */       int subset = whichSubset(instance);
/* 173:268 */       if (subset > -1)
/* 174:    */       {
/* 175:269 */         instances[subset].add(instance);
/* 176:    */       }
/* 177:    */       else
/* 178:    */       {
/* 179:271 */         double[] weights = weights(instance);
/* 180:272 */         for (int j = 0; j < this.m_numSubsets; j++) {
/* 181:273 */           if (Utils.gr(weights[j], 0.0D))
/* 182:    */           {
/* 183:274 */             instances[j].add(instance);
/* 184:275 */             instances[j].lastInstance().setWeight(weights[j] * instance.weight());
/* 185:    */           }
/* 186:    */         }
/* 187:    */       }
/* 188:    */     }
/* 189:282 */     return instances;
/* 190:    */   }
/* 191:    */   
/* 192:    */   public abstract double[] weights(Instance paramInstance);
/* 193:    */   
/* 194:    */   public abstract int whichSubset(Instance paramInstance)
/* 195:    */     throws Exception;
/* 196:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.ClassifierSplitModel
 * JD-Core Version:    0.7.0.1
 */