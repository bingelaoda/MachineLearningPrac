/*   1:    */ package weka.classifiers.mi.miti;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Comparator;
/*   7:    */ import java.util.HashMap;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Instance;
/*  10:    */ 
/*  11:    */ public class Split
/*  12:    */   implements Serializable
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 147371323803237346L;
/*  15:    */   public Attribute attribute;
/*  16:    */   public double splitPoint;
/*  17:    */   public double score;
/*  18: 53 */   public boolean isNominal = false;
/*  19:    */   
/*  20:    */   public static Split getBestSplitPoint(Attribute a, ArrayList<Instance> enabled, HashMap<Instance, Bag> instanceBags, AlgorithmConfiguration settings)
/*  21:    */   {
/*  22:    */     IBestSplitMeasure bsm;
/*  23:    */     IBestSplitMeasure bsm;
/*  24: 64 */     if (settings.method == 1)
/*  25:    */     {
/*  26: 65 */       bsm = new Gini();
/*  27:    */     }
/*  28:    */     else
/*  29:    */     {
/*  30:    */       IBestSplitMeasure bsm;
/*  31: 66 */       if (settings.method == 3) {
/*  32: 67 */         bsm = new SSBEPP();
/*  33:    */       } else {
/*  34: 69 */         bsm = new MaxBEPP();
/*  35:    */       }
/*  36:    */     }
/*  37: 73 */     if (a.isNominal()) {
/*  38: 74 */       return getBestNominalSplitPoint(a, enabled, instanceBags, settings, bsm);
/*  39:    */     }
/*  40: 78 */     Collections.sort(enabled, new Comparator()
/*  41:    */     {
/*  42:    */       public int compare(Instance arg0, Instance arg1)
/*  43:    */       {
/*  44: 81 */         return Double.compare(arg0.value(this.val$a), arg1.value(this.val$a));
/*  45:    */       }
/*  46: 84 */     });
/*  47: 85 */     Split split = null;
/*  48:    */     SufficientStatistics ss;
/*  49:    */     SufficientStatistics ss;
/*  50: 88 */     if (!settings.useBagStatistics) {
/*  51: 89 */       ss = new SufficientInstanceStatistics(enabled, instanceBags);
/*  52:    */     } else {
/*  53: 91 */       ss = new SufficientBagStatistics(enabled, instanceBags, settings.bagCountMultiplier);
/*  54:    */     }
/*  55: 96 */     for (int i = 0; i < enabled.size() - 1; i++)
/*  56:    */     {
/*  57: 98 */       ss.updateStats((Instance)enabled.get(i), instanceBags);
/*  58:100 */       if (((Instance)enabled.get(i)).value(a) != ((Instance)enabled.get(i + 1)).value(a))
/*  59:    */       {
/*  60:104 */         double splitPoint = (((Instance)enabled.get(i)).value(a) + ((Instance)enabled.get(i + 1)).value(a)) / 2.0D;
/*  61:    */         
/*  62:    */ 
/*  63:107 */         double score = bsm.getScore(ss, settings.kBEPPConstant, settings.unbiasedEstimate);
/*  64:110 */         if (split == null)
/*  65:    */         {
/*  66:111 */           split = new Split();
/*  67:112 */           split.attribute = a;
/*  68:113 */           split.score = score;
/*  69:114 */           split.splitPoint = splitPoint;
/*  70:    */         }
/*  71:118 */         else if (score > split.score)
/*  72:    */         {
/*  73:119 */           split.score = score;
/*  74:120 */           split.splitPoint = splitPoint;
/*  75:    */         }
/*  76:    */       }
/*  77:    */     }
/*  78:124 */     return split;
/*  79:    */   }
/*  80:    */   
/*  81:    */   private static Split getBestNominalSplitPoint(Attribute a, ArrayList<Instance> enabled, HashMap<Instance, Bag> instanceBags, AlgorithmConfiguration settings, IBestSplitMeasure bsm)
/*  82:    */   {
/*  83:133 */     Split s = new Split();
/*  84:134 */     s.isNominal = true;
/*  85:135 */     s.attribute = a;
/*  86:136 */     SufficientStatistics[] ss = new SufficientStatistics[a.numValues()];
/*  87:137 */     if (!settings.useBagStatistics) {
/*  88:138 */       for (int i = 0; i < a.numValues(); i++) {
/*  89:139 */         ss[i] = new SufficientInstanceStatistics(enabled, instanceBags);
/*  90:    */       }
/*  91:    */     } else {
/*  92:142 */       for (int i = 0; i < a.numValues(); i++) {
/*  93:143 */         ss[i] = new SufficientBagStatistics(enabled, instanceBags, settings.bagCountMultiplier);
/*  94:    */       }
/*  95:    */     }
/*  96:147 */     for (Instance i : enabled) {
/*  97:148 */       ss[((int)i.value(a))].updateStats(i, instanceBags);
/*  98:    */     }
/*  99:150 */     double[] totals = new double[a.numValues()];
/* 100:151 */     double[] positiveCounts = new double[a.numValues()];
/* 101:152 */     for (int i = 0; i < a.numValues(); i++)
/* 102:    */     {
/* 103:153 */       totals[i] = ss[i].totalCountLeft();
/* 104:154 */       positiveCounts[i] = ss[i].positiveCountLeft();
/* 105:    */     }
/* 106:156 */     s.score = bsm.getScore(totals, positiveCounts, settings.kBEPPConstant, settings.unbiasedEstimate);
/* 107:    */     
/* 108:158 */     return s;
/* 109:    */   }
/* 110:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.miti.Split
 * JD-Core Version:    0.7.0.1
 */