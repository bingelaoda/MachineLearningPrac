/*   1:    */ package weka.classifiers.trees.ht;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.HashMap;
/*   5:    */ import java.util.LinkedHashMap;
/*   6:    */ import java.util.LinkedList;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.Map;
/*   9:    */ import java.util.Map.Entry;
/*  10:    */ import weka.core.Utils;
/*  11:    */ 
/*  12:    */ public class NominalConditionalSufficientStats
/*  13:    */   extends ConditionalSufficientStats
/*  14:    */   implements Serializable
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = -669902060601313488L;
/*  17:    */   protected double m_totalWeight;
/*  18:    */   protected double m_missingWeight;
/*  19:    */   
/*  20:    */   protected class ValueDistribution
/*  21:    */     implements Serializable
/*  22:    */   {
/*  23:    */     private static final long serialVersionUID = -61711544350888154L;
/*  24: 61 */     protected final Map<Integer, WeightMass> m_dist = new LinkedHashMap();
/*  25:    */     private double m_sum;
/*  26:    */     
/*  27:    */     protected ValueDistribution() {}
/*  28:    */     
/*  29:    */     public void add(int val, double weight)
/*  30:    */     {
/*  31: 66 */       WeightMass count = (WeightMass)this.m_dist.get(Integer.valueOf(val));
/*  32: 67 */       if (count == null)
/*  33:    */       {
/*  34: 68 */         count = new WeightMass();
/*  35: 69 */         count.m_weight = 1.0D;
/*  36: 70 */         this.m_sum += 1.0D;
/*  37: 71 */         this.m_dist.put(Integer.valueOf(val), count);
/*  38:    */       }
/*  39: 73 */       count.m_weight += weight;
/*  40: 74 */       this.m_sum += weight;
/*  41:    */     }
/*  42:    */     
/*  43:    */     public void delete(int val, double weight)
/*  44:    */     {
/*  45: 78 */       WeightMass count = (WeightMass)this.m_dist.get(Integer.valueOf(val));
/*  46: 79 */       if (count != null)
/*  47:    */       {
/*  48: 80 */         count.m_weight -= weight;
/*  49: 81 */         this.m_sum -= weight;
/*  50:    */       }
/*  51:    */     }
/*  52:    */     
/*  53:    */     public double getWeight(int val)
/*  54:    */     {
/*  55: 86 */       WeightMass count = (WeightMass)this.m_dist.get(Integer.valueOf(val));
/*  56: 87 */       if (count != null) {
/*  57: 88 */         return count.m_weight;
/*  58:    */       }
/*  59: 91 */       return 0.0D;
/*  60:    */     }
/*  61:    */     
/*  62:    */     public double sum()
/*  63:    */     {
/*  64: 95 */       return this.m_sum;
/*  65:    */     }
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void update(double attVal, String classVal, double weight)
/*  69:    */   {
/*  70:104 */     if (Utils.isMissingValue(attVal))
/*  71:    */     {
/*  72:105 */       this.m_missingWeight += weight;
/*  73:    */     }
/*  74:    */     else
/*  75:    */     {
/*  76:107 */       new Integer((int)attVal);
/*  77:108 */       ValueDistribution valDist = (ValueDistribution)this.m_classLookup.get(classVal);
/*  78:110 */       if (valDist == null)
/*  79:    */       {
/*  80:111 */         valDist = new ValueDistribution();
/*  81:112 */         valDist.add((int)attVal, weight);
/*  82:113 */         this.m_classLookup.put(classVal, valDist);
/*  83:    */       }
/*  84:    */       else
/*  85:    */       {
/*  86:115 */         valDist.add((int)attVal, weight);
/*  87:    */       }
/*  88:    */     }
/*  89:119 */     this.m_totalWeight += weight;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public double probabilityOfAttValConditionedOnClass(double attVal, String classVal)
/*  93:    */   {
/*  94:125 */     ValueDistribution valDist = (ValueDistribution)this.m_classLookup.get(classVal);
/*  95:126 */     if (valDist != null)
/*  96:    */     {
/*  97:127 */       double prob = valDist.getWeight((int)attVal) / valDist.sum();
/*  98:128 */       return prob;
/*  99:    */     }
/* 100:131 */     return 0.0D;
/* 101:    */   }
/* 102:    */   
/* 103:    */   protected List<Map<String, WeightMass>> classDistsAfterSplit()
/* 104:    */   {
/* 105:137 */     Map<Integer, Map<String, WeightMass>> splitDists = new HashMap();
/* 106:139 */     for (Map.Entry<String, Object> cls : this.m_classLookup.entrySet())
/* 107:    */     {
/* 108:140 */       classVal = (String)cls.getKey();
/* 109:141 */       ValueDistribution attDist = (ValueDistribution)cls.getValue();
/* 110:143 */       for (Map.Entry<Integer, WeightMass> att : attDist.m_dist.entrySet())
/* 111:    */       {
/* 112:144 */         Integer attVal = (Integer)att.getKey();
/* 113:145 */         WeightMass attCount = (WeightMass)att.getValue();
/* 114:    */         
/* 115:147 */         Map<String, WeightMass> clsDist = (Map)splitDists.get(attVal);
/* 116:148 */         if (clsDist == null)
/* 117:    */         {
/* 118:149 */           clsDist = new HashMap();
/* 119:150 */           splitDists.put(attVal, clsDist);
/* 120:    */         }
/* 121:153 */         WeightMass clsCount = (WeightMass)clsDist.get(classVal);
/* 122:155 */         if (clsCount == null)
/* 123:    */         {
/* 124:156 */           clsCount = new WeightMass();
/* 125:157 */           clsDist.put(classVal, clsCount);
/* 126:    */         }
/* 127:160 */         clsCount.m_weight += attCount.m_weight;
/* 128:    */       }
/* 129:    */     }
/* 130:    */     String classVal;
/* 131:165 */     List<Map<String, WeightMass>> result = new LinkedList();
/* 132:166 */     for (Map.Entry<Integer, Map<String, WeightMass>> v : splitDists.entrySet()) {
/* 133:167 */       result.add(v.getValue());
/* 134:    */     }
/* 135:170 */     return result;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public SplitCandidate bestSplit(SplitMetric splitMetric, Map<String, WeightMass> preSplitDist, String attName)
/* 139:    */   {
/* 140:177 */     List<Map<String, WeightMass>> postSplitDists = classDistsAfterSplit();
/* 141:178 */     double merit = splitMetric.evaluateSplit(preSplitDist, postSplitDists);
/* 142:179 */     SplitCandidate candidate = new SplitCandidate(new UnivariateNominalMultiwaySplit(attName), postSplitDists, merit);
/* 143:    */     
/* 144:    */ 
/* 145:182 */     return candidate;
/* 146:    */   }
/* 147:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ht.NominalConditionalSufficientStats
 * JD-Core Version:    0.7.0.1
 */