/*   1:    */ package weka.classifiers.trees.m5;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.Instance;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.core.RevisionHandler;
/*   7:    */ import weka.core.RevisionUtils;
/*   8:    */ import weka.core.Utils;
/*   9:    */ import weka.experiment.PairedStats;
/*  10:    */ import weka.experiment.Stats;
/*  11:    */ 
/*  12:    */ public final class CorrelationSplitInfo
/*  13:    */   implements Cloneable, Serializable, SplitEvaluate, RevisionHandler
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = 4212734895125452770L;
/*  16:    */   private int m_position;
/*  17:    */   private double m_maxImpurity;
/*  18:    */   private int m_splitAttr;
/*  19:    */   private double m_splitValue;
/*  20:    */   private int m_number;
/*  21:    */   
/*  22:    */   public CorrelationSplitInfo(int low, int high, int attr)
/*  23:    */   {
/*  24: 74 */     initialize(low, high, attr);
/*  25:    */   }
/*  26:    */   
/*  27:    */   public final SplitEvaluate copy()
/*  28:    */     throws Exception
/*  29:    */   {
/*  30: 82 */     CorrelationSplitInfo s = (CorrelationSplitInfo)clone();
/*  31:    */     
/*  32: 84 */     return s;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public final void initialize(int low, int high, int attr)
/*  36:    */   {
/*  37: 95 */     this.m_number = (high - low + 1);
/*  38: 96 */     this.m_position = -1;
/*  39: 97 */     this.m_maxImpurity = -1.797693134862316E+308D;
/*  40: 98 */     this.m_splitAttr = attr;
/*  41: 99 */     this.m_splitValue = 0.0D;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public final void attrSplit(int attr, Instances inst)
/*  45:    */     throws Exception
/*  46:    */   {
/*  47:113 */     int low = 0;
/*  48:114 */     int high = inst.numInstances() - 1;
/*  49:115 */     PairedStats full = new PairedStats(0.01D);
/*  50:116 */     PairedStats leftSubset = new PairedStats(0.01D);
/*  51:117 */     PairedStats rightSubset = new PairedStats(0.01D);
/*  52:118 */     int classIndex = inst.classIndex();
/*  53:    */     
/*  54:    */ 
/*  55:121 */     double order = 2.0D;
/*  56:    */     
/*  57:123 */     initialize(low, high, attr);
/*  58:125 */     if (this.m_number < 4) {
/*  59:126 */       return;
/*  60:    */     }
/*  61:129 */     int len = high - low + 1 < 5 ? 1 : (high - low + 1) / 5;
/*  62:130 */     this.m_position = low;
/*  63:132 */     for (int i = low; i < len; i++)
/*  64:    */     {
/*  65:133 */       full.add(inst.instance(i).value(attr), inst.instance(i).value(classIndex));
/*  66:    */       
/*  67:135 */       leftSubset.add(inst.instance(i).value(attr), inst.instance(i).value(classIndex));
/*  68:    */     }
/*  69:139 */     for (i = len; i < inst.numInstances(); i++)
/*  70:    */     {
/*  71:140 */       full.add(inst.instance(i).value(attr), inst.instance(i).value(classIndex));
/*  72:    */       
/*  73:142 */       rightSubset.add(inst.instance(i).value(attr), inst.instance(i).value(classIndex));
/*  74:    */     }
/*  75:146 */     full.calculateDerived();
/*  76:    */     
/*  77:148 */     double allVar = full.yStats.stdDev * full.yStats.stdDev;
/*  78:149 */     allVar = Math.abs(allVar);
/*  79:150 */     allVar = Math.pow(allVar, 1.0D / order);
/*  80:152 */     for (i = low + len; i < high - len - 1; i++)
/*  81:    */     {
/*  82:153 */       rightSubset.subtract(inst.instance(i).value(attr), inst.instance(i).value(classIndex));
/*  83:    */       
/*  84:155 */       leftSubset.add(inst.instance(i).value(attr), inst.instance(i).value(classIndex));
/*  85:158 */       if (!Utils.eq(inst.instance(i + 1).value(attr), inst.instance(i).value(attr)))
/*  86:    */       {
/*  87:160 */         leftSubset.calculateDerived();
/*  88:161 */         rightSubset.calculateDerived();
/*  89:    */         
/*  90:163 */         double leftCorr = Math.abs(leftSubset.correlation);
/*  91:164 */         double rightCorr = Math.abs(rightSubset.correlation);
/*  92:165 */         double leftVar = leftSubset.yStats.stdDev * leftSubset.yStats.stdDev;
/*  93:166 */         leftVar = Math.abs(leftVar);
/*  94:167 */         leftVar = Math.pow(leftVar, 1.0D / order);
/*  95:168 */         double rightVar = rightSubset.yStats.stdDev * rightSubset.yStats.stdDev;
/*  96:169 */         rightVar = Math.abs(rightVar);
/*  97:170 */         rightVar = Math.pow(rightVar, 1.0D / order);
/*  98:    */         
/*  99:172 */         double score = allVar - leftSubset.count / full.count * leftVar - rightSubset.count / full.count * rightVar;
/* 100:    */         
/* 101:    */ 
/* 102:    */ 
/* 103:176 */         leftCorr = leftSubset.count / full.count * leftCorr;
/* 104:177 */         rightCorr = rightSubset.count / full.count * rightCorr;
/* 105:180 */         if ((!Utils.eq(score, 0.0D)) && 
/* 106:181 */           (score > this.m_maxImpurity))
/* 107:    */         {
/* 108:182 */           this.m_maxImpurity = score;
/* 109:183 */           this.m_splitValue = ((inst.instance(i).value(attr) + inst.instance(i + 1).value(attr)) * 0.5D);
/* 110:    */           
/* 111:185 */           this.m_position = i;
/* 112:    */         }
/* 113:    */       }
/* 114:    */     }
/* 115:    */   }
/* 116:    */   
/* 117:    */   public double maxImpurity()
/* 118:    */   {
/* 119:199 */     return this.m_maxImpurity;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public int splitAttr()
/* 123:    */   {
/* 124:209 */     return this.m_splitAttr;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public int position()
/* 128:    */   {
/* 129:220 */     return this.m_position;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public double splitValue()
/* 133:    */   {
/* 134:230 */     return this.m_splitValue;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public String getRevision()
/* 138:    */   {
/* 139:240 */     return RevisionUtils.extract("$Revision: 10169 $");
/* 140:    */   }
/* 141:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.m5.CorrelationSplitInfo
 * JD-Core Version:    0.7.0.1
 */