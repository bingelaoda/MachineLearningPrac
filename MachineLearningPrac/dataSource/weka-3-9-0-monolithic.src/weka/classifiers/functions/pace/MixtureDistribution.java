/*   1:    */ package weka.classifiers.functions.pace;
/*   2:    */ 
/*   3:    */ import weka.core.RevisionHandler;
/*   4:    */ import weka.core.TechnicalInformation;
/*   5:    */ import weka.core.TechnicalInformation.Field;
/*   6:    */ import weka.core.TechnicalInformation.Type;
/*   7:    */ import weka.core.TechnicalInformationHandler;
/*   8:    */ import weka.core.matrix.DoubleVector;
/*   9:    */ import weka.core.matrix.IntVector;
/*  10:    */ 
/*  11:    */ public abstract class MixtureDistribution
/*  12:    */   implements TechnicalInformationHandler, RevisionHandler
/*  13:    */ {
/*  14:    */   protected DiscreteFunction mixingDistribution;
/*  15:    */   public static final int NNMMethod = 1;
/*  16:    */   public static final int PMMethod = 2;
/*  17:    */   
/*  18:    */   public TechnicalInformation getTechnicalInformation()
/*  19:    */   {
/*  20: 75 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.PHDTHESIS);
/*  21: 76 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Wang, Y");
/*  22: 77 */     result.setValue(TechnicalInformation.Field.YEAR, "2000");
/*  23: 78 */     result.setValue(TechnicalInformation.Field.TITLE, "A new approach to fitting linear models in high dimensional spaces");
/*  24: 79 */     result.setValue(TechnicalInformation.Field.SCHOOL, "Department of Computer Science, University of Waikato");
/*  25: 80 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Hamilton, New Zealand");
/*  26:    */     
/*  27: 82 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/*  28: 83 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Wang, Y. and Witten, I. H.");
/*  29: 84 */     additional.setValue(TechnicalInformation.Field.YEAR, "2002");
/*  30: 85 */     additional.setValue(TechnicalInformation.Field.TITLE, "Modeling for optimal probability prediction");
/*  31: 86 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings of the Nineteenth International Conference in Machine Learning");
/*  32: 87 */     additional.setValue(TechnicalInformation.Field.YEAR, "2002");
/*  33: 88 */     additional.setValue(TechnicalInformation.Field.PAGES, "650-657");
/*  34: 89 */     additional.setValue(TechnicalInformation.Field.ADDRESS, "Sydney, Australia");
/*  35:    */     
/*  36: 91 */     return result;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public DiscreteFunction getMixingDistribution()
/*  40:    */   {
/*  41:100 */     return this.mixingDistribution;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setMixingDistribution(DiscreteFunction d)
/*  45:    */   {
/*  46:107 */     this.mixingDistribution = d;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void fit(DoubleVector data)
/*  50:    */   {
/*  51:114 */     fit(data, 1);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void fit(DoubleVector data, int method)
/*  55:    */   {
/*  56:122 */     DoubleVector data2 = (DoubleVector)data.clone();
/*  57:123 */     if (data2.unsorted()) {
/*  58:123 */       data2.sort();
/*  59:    */     }
/*  60:125 */     int n = data2.size();
/*  61:126 */     int start = 0;
/*  62:    */     
/*  63:128 */     DiscreteFunction d = new DiscreteFunction();
/*  64:129 */     for (int i = 0; i < n - 1; i++) {
/*  65:130 */       if ((separable(data2, start, i, data2.get(i + 1))) && (separable(data2, i + 1, n - 1, data2.get(i))))
/*  66:    */       {
/*  67:132 */         DoubleVector subset = data2.subvector(start, i);
/*  68:133 */         d.plusEquals(fitForSingleCluster(subset, method).timesEquals(i - start + 1));
/*  69:    */         
/*  70:135 */         start = i + 1;
/*  71:    */       }
/*  72:    */     }
/*  73:138 */     DoubleVector subset = data2.subvector(start, n - 1);
/*  74:139 */     d.plusEquals(fitForSingleCluster(subset, method).timesEquals(n - start));
/*  75:    */     
/*  76:141 */     d.sort();
/*  77:142 */     d.normalize();
/*  78:143 */     this.mixingDistribution = d;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public DiscreteFunction fitForSingleCluster(DoubleVector data, int method)
/*  82:    */   {
/*  83:158 */     if (data.size() < 2) {
/*  84:158 */       return new DiscreteFunction(data);
/*  85:    */     }
/*  86:159 */     DoubleVector sp = supportPoints(data, 0);
/*  87:160 */     PaceMatrix fi = fittingIntervals(data);
/*  88:161 */     PaceMatrix pm = probabilityMatrix(sp, fi);
/*  89:162 */     PaceMatrix epm = new PaceMatrix(empiricalProbability(data, fi).timesEquals(1.0D / data.size()));
/*  90:    */     
/*  91:    */ 
/*  92:    */ 
/*  93:166 */     IntVector pvt = IntVector.seq(0, sp.size() - 1);
/*  94:    */     DoubleVector weights;
/*  95:169 */     switch (method)
/*  96:    */     {
/*  97:    */     case 1: 
/*  98:171 */       weights = pm.nnls(epm, pvt);
/*  99:172 */       break;
/* 100:    */     case 2: 
/* 101:174 */       weights = pm.nnlse1(epm, pvt);
/* 102:175 */       break;
/* 103:    */     default: 
/* 104:177 */       throw new IllegalArgumentException("unknown method");
/* 105:    */     }
/* 106:180 */     DoubleVector sp2 = new DoubleVector(pvt.size());
/* 107:181 */     for (int i = 0; i < sp2.size(); i++) {
/* 108:182 */       sp2.set(i, sp.get(pvt.get(i)));
/* 109:    */     }
/* 110:185 */     DiscreteFunction d = new DiscreteFunction(sp2, weights);
/* 111:186 */     d.sort();
/* 112:187 */     d.normalize();
/* 113:188 */     return d;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public abstract boolean separable(DoubleVector paramDoubleVector, int paramInt1, int paramInt2, double paramDouble);
/* 117:    */   
/* 118:    */   public abstract DoubleVector supportPoints(DoubleVector paramDoubleVector, int paramInt);
/* 119:    */   
/* 120:    */   public abstract PaceMatrix fittingIntervals(DoubleVector paramDoubleVector);
/* 121:    */   
/* 122:    */   public abstract PaceMatrix probabilityMatrix(DoubleVector paramDoubleVector, PaceMatrix paramPaceMatrix);
/* 123:    */   
/* 124:    */   public PaceMatrix empiricalProbability(DoubleVector data, PaceMatrix intervals)
/* 125:    */   {
/* 126:244 */     int n = data.size();
/* 127:245 */     int k = intervals.getRowDimension();
/* 128:246 */     PaceMatrix epm = new PaceMatrix(k, 1, 0.0D);
/* 129:249 */     for (int j = 0; j < n; j++) {
/* 130:250 */       for (int i = 0; i < k; i++)
/* 131:    */       {
/* 132:251 */         double point = 0.0D;
/* 133:252 */         if ((intervals.get(i, 0) == data.get(j)) || (intervals.get(i, 1) == data.get(j))) {
/* 134:253 */           point = 0.5D;
/* 135:254 */         } else if ((intervals.get(i, 0) < data.get(j)) && (intervals.get(i, 1) > data.get(j))) {
/* 136:255 */           point = 1.0D;
/* 137:    */         }
/* 138:256 */         epm.setPlus(i, 0, point);
/* 139:    */       }
/* 140:    */     }
/* 141:259 */     return epm;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public String toString()
/* 145:    */   {
/* 146:269 */     return "The mixing distribution:\n" + this.mixingDistribution.toString();
/* 147:    */   }
/* 148:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.pace.MixtureDistribution
 * JD-Core Version:    0.7.0.1
 */