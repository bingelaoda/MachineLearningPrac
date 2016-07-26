/*   1:    */ package weka.classifiers.evaluation;
/*   2:    */ 
/*   3:    */ import weka.core.RevisionHandler;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ 
/*   6:    */ public class TwoClassStats
/*   7:    */   implements RevisionHandler
/*   8:    */ {
/*   9: 36 */   private static final String[] CATEGORY_NAMES = { "negative", "positive" };
/*  10:    */   private double m_TruePos;
/*  11:    */   private double m_FalsePos;
/*  12:    */   private double m_TrueNeg;
/*  13:    */   private double m_FalseNeg;
/*  14:    */   
/*  15:    */   public TwoClassStats(double tp, double fp, double tn, double fn)
/*  16:    */   {
/*  17: 60 */     setTruePositive(tp);
/*  18: 61 */     setFalsePositive(fp);
/*  19: 62 */     setTrueNegative(tn);
/*  20: 63 */     setFalseNegative(fn);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public void setTruePositive(double tp)
/*  24:    */   {
/*  25: 68 */     this.m_TruePos = tp;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void setFalsePositive(double fp)
/*  29:    */   {
/*  30: 73 */     this.m_FalsePos = fp;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public void setTrueNegative(double tn)
/*  34:    */   {
/*  35: 78 */     this.m_TrueNeg = tn;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setFalseNegative(double fn)
/*  39:    */   {
/*  40: 83 */     this.m_FalseNeg = fn;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public double getTruePositive()
/*  44:    */   {
/*  45: 88 */     return this.m_TruePos;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public double getFalsePositive()
/*  49:    */   {
/*  50: 93 */     return this.m_FalsePos;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public double getTrueNegative()
/*  54:    */   {
/*  55: 98 */     return this.m_TrueNeg;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public double getFalseNegative()
/*  59:    */   {
/*  60:103 */     return this.m_FalseNeg;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public double getTruePositiveRate()
/*  64:    */   {
/*  65:119 */     if (0.0D == this.m_TruePos + this.m_FalseNeg) {
/*  66:120 */       return 0.0D;
/*  67:    */     }
/*  68:122 */     return this.m_TruePos / (this.m_TruePos + this.m_FalseNeg);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public double getFalsePositiveRate()
/*  72:    */   {
/*  73:139 */     if (0.0D == this.m_FalsePos + this.m_TrueNeg) {
/*  74:140 */       return 0.0D;
/*  75:    */     }
/*  76:142 */     return this.m_FalsePos / (this.m_FalsePos + this.m_TrueNeg);
/*  77:    */   }
/*  78:    */   
/*  79:    */   public double getPrecision()
/*  80:    */   {
/*  81:159 */     if (0.0D == this.m_TruePos + this.m_FalsePos) {
/*  82:160 */       return 0.0D;
/*  83:    */     }
/*  84:162 */     return this.m_TruePos / (this.m_TruePos + this.m_FalsePos);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public double getRecall()
/*  88:    */   {
/*  89:181 */     return getTruePositiveRate();
/*  90:    */   }
/*  91:    */   
/*  92:    */   public double getFMeasure()
/*  93:    */   {
/*  94:198 */     double precision = getPrecision();
/*  95:199 */     double recall = getRecall();
/*  96:200 */     if (precision + recall == 0.0D) {
/*  97:201 */       return 0.0D;
/*  98:    */     }
/*  99:203 */     return 2.0D * precision * recall / (precision + recall);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public double getFallout()
/* 103:    */   {
/* 104:219 */     if (0.0D == this.m_TruePos + this.m_FalsePos) {
/* 105:220 */       return 0.0D;
/* 106:    */     }
/* 107:222 */     return this.m_FalsePos / (this.m_TruePos + this.m_FalsePos);
/* 108:    */   }
/* 109:    */   
/* 110:    */   public ConfusionMatrix getConfusionMatrix()
/* 111:    */   {
/* 112:234 */     ConfusionMatrix cm = new ConfusionMatrix(CATEGORY_NAMES);
/* 113:235 */     cm.set(0, 0, this.m_TrueNeg);
/* 114:236 */     cm.set(0, 1, this.m_FalsePos);
/* 115:237 */     cm.set(1, 0, this.m_FalseNeg);
/* 116:238 */     cm.set(1, 1, this.m_TruePos);
/* 117:239 */     return cm;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public String toString()
/* 121:    */   {
/* 122:249 */     StringBuffer res = new StringBuffer();
/* 123:250 */     res.append(getTruePositive()).append(' ');
/* 124:251 */     res.append(getFalseNegative()).append(' ');
/* 125:252 */     res.append(getTrueNegative()).append(' ');
/* 126:253 */     res.append(getFalsePositive()).append(' ');
/* 127:254 */     res.append(getFalsePositiveRate()).append(' ');
/* 128:255 */     res.append(getTruePositiveRate()).append(' ');
/* 129:256 */     res.append(getPrecision()).append(' ');
/* 130:257 */     res.append(getRecall()).append(' ');
/* 131:258 */     res.append(getFMeasure()).append(' ');
/* 132:259 */     res.append(getFallout()).append(' ');
/* 133:260 */     return res.toString();
/* 134:    */   }
/* 135:    */   
/* 136:    */   public String getRevision()
/* 137:    */   {
/* 138:270 */     return RevisionUtils.extract("$Revision: 10169 $");
/* 139:    */   }
/* 140:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.TwoClassStats
 * JD-Core Version:    0.7.0.1
 */