/*   1:    */ package weka.classifiers.trees.m5;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.RevisionHandler;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ import weka.core.Utils;
/*  10:    */ 
/*  11:    */ public final class YongSplitInfo
/*  12:    */   implements Cloneable, Serializable, SplitEvaluate, RevisionHandler
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 1864267581079767881L;
/*  15:    */   private int number;
/*  16:    */   private int first;
/*  17:    */   private int last;
/*  18:    */   private int position;
/*  19:    */   private double maxImpurity;
/*  20:    */   private double leftAve;
/*  21:    */   private double rightAve;
/*  22:    */   private int splitAttr;
/*  23:    */   private double splitValue;
/*  24:    */   
/*  25:    */   public YongSplitInfo(int low, int high, int attr)
/*  26:    */   {
/*  27: 62 */     this.number = (high - low + 1);
/*  28: 63 */     this.first = low;
/*  29: 64 */     this.last = high;
/*  30: 65 */     this.position = -1;
/*  31: 66 */     this.maxImpurity = -1.0E+020D;
/*  32: 67 */     this.splitAttr = attr;
/*  33: 68 */     this.splitValue = 0.0D;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public final SplitEvaluate copy()
/*  37:    */     throws Exception
/*  38:    */   {
/*  39: 77 */     YongSplitInfo s = (YongSplitInfo)clone();
/*  40:    */     
/*  41: 79 */     return s;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public final void initialize(int low, int high, int attr)
/*  45:    */   {
/*  46: 91 */     this.number = (high - low + 1);
/*  47: 92 */     this.first = low;
/*  48: 93 */     this.last = high;
/*  49: 94 */     this.position = -1;
/*  50: 95 */     this.maxImpurity = -1.0E+020D;
/*  51: 96 */     this.splitAttr = attr;
/*  52: 97 */     this.splitValue = 0.0D;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public final String toString(Instances inst)
/*  56:    */   {
/*  57:107 */     StringBuffer text = new StringBuffer();
/*  58:    */     
/*  59:109 */     text.append("Print SplitInfo:\n");
/*  60:110 */     text.append("    Instances:\t\t" + this.number + " (" + this.first + "-" + this.position + "," + (this.position + 1) + "-" + this.last + ")\n");
/*  61:    */     
/*  62:112 */     text.append("    Maximum Impurity Reduction:\t" + Utils.doubleToString(this.maxImpurity, 1, 4) + "\n");
/*  63:    */     
/*  64:114 */     text.append("    Left average:\t" + this.leftAve + "\n");
/*  65:115 */     text.append("    Right average:\t" + this.rightAve + "\n");
/*  66:116 */     if (this.maxImpurity > 0.0D) {
/*  67:117 */       text.append("    Splitting function:\t" + inst.attribute(this.splitAttr).name() + " = " + this.splitValue + "\n");
/*  68:    */     } else {
/*  69:120 */       text.append("    Splitting function:\tnull\n");
/*  70:    */     }
/*  71:123 */     return text.toString();
/*  72:    */   }
/*  73:    */   
/*  74:    */   public final void attrSplit(int attr, Instances inst)
/*  75:    */     throws Exception
/*  76:    */   {
/*  77:138 */     int low = 0;
/*  78:139 */     int high = inst.numInstances() - 1;
/*  79:140 */     initialize(low, high, attr);
/*  80:141 */     if (this.number < 4) {
/*  81:142 */       return;
/*  82:    */     }
/*  83:145 */     int len = high - low + 1 < 5 ? 1 : (high - low + 1) / 5;
/*  84:    */     
/*  85:147 */     this.position = low;
/*  86:    */     
/*  87:149 */     int part = low + len - 1;
/*  88:150 */     Impurity imp = new Impurity(part, attr, inst, 5);
/*  89:152 */     for (int i = low + len; i <= high - len - 1; i++)
/*  90:    */     {
/*  91:154 */       imp.incremental(inst.instance(i).classValue(), 1);
/*  92:156 */       if (!Utils.eq(inst.instance(i + 1).value(attr), inst.instance(i).value(attr))) {
/*  93:158 */         if (imp.impurity > this.maxImpurity)
/*  94:    */         {
/*  95:159 */           this.maxImpurity = imp.impurity;
/*  96:160 */           this.splitValue = ((inst.instance(i).value(attr) + inst.instance(i + 1).value(attr)) * 0.5D);
/*  97:    */           
/*  98:162 */           this.leftAve = (imp.sl / imp.nl);
/*  99:163 */           this.rightAve = (imp.sr / imp.nr);
/* 100:164 */           this.position = i;
/* 101:    */         }
/* 102:    */       }
/* 103:    */     }
/* 104:    */   }
/* 105:    */   
/* 106:    */   public double maxImpurity()
/* 107:    */   {
/* 108:177 */     return this.maxImpurity;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public int splitAttr()
/* 112:    */   {
/* 113:187 */     return this.splitAttr;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public int position()
/* 117:    */   {
/* 118:198 */     return this.position;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public double splitValue()
/* 122:    */   {
/* 123:208 */     return this.splitValue;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public String getRevision()
/* 127:    */   {
/* 128:218 */     return RevisionUtils.extract("$Revision: 11269 $");
/* 129:    */   }
/* 130:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.m5.YongSplitInfo
 * JD-Core Version:    0.7.0.1
 */