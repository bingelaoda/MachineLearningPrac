/*   1:    */ package weka.classifiers.trees.m5;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.core.Instances;
/*   5:    */ import weka.core.RevisionHandler;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ 
/*   8:    */ public final class Impurity
/*   9:    */   implements RevisionHandler
/*  10:    */ {
/*  11:    */   double n;
/*  12:    */   int attr;
/*  13:    */   double nl;
/*  14:    */   double nr;
/*  15:    */   double sl;
/*  16:    */   double sr;
/*  17:    */   double s2l;
/*  18:    */   double s2r;
/*  19:    */   double sdl;
/*  20:    */   double sdr;
/*  21:    */   double vl;
/*  22:    */   double vr;
/*  23:    */   double sd;
/*  24:    */   double va;
/*  25:    */   double impurity;
/*  26:    */   int order;
/*  27:    */   
/*  28:    */   public Impurity(int partition, int attribute, Instances inst, int k)
/*  29:    */   {
/*  30: 64 */     Values values = new Values(0, inst.numInstances() - 1, inst.classIndex(), inst);
/*  31: 65 */     this.attr = attribute;
/*  32: 66 */     this.n = inst.numInstances();
/*  33: 67 */     this.sd = values.sd;
/*  34: 68 */     this.va = values.va;
/*  35:    */     
/*  36: 70 */     values = new Values(0, partition, inst.classIndex(), inst);
/*  37: 71 */     this.nl = (partition + 1);
/*  38: 72 */     this.sl = values.sum;
/*  39: 73 */     this.s2l = values.sqrSum;
/*  40:    */     
/*  41: 75 */     values = new Values(partition + 1, inst.numInstances() - 1, inst.classIndex(), inst);
/*  42: 76 */     this.nr = (inst.numInstances() - partition - 1);
/*  43: 77 */     this.sr = values.sum;
/*  44: 78 */     this.s2r = values.sqrSum;
/*  45:    */     
/*  46: 80 */     this.order = k;
/*  47: 81 */     incremental(0.0D, 0);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public final String toString()
/*  51:    */   {
/*  52: 90 */     StringBuffer text = new StringBuffer();
/*  53:    */     
/*  54: 92 */     text.append("Print impurity values:\n");
/*  55: 93 */     text.append("    Number of total instances:\t" + this.n + "\n");
/*  56: 94 */     text.append("    Splitting attribute:\t\t" + this.attr + "\n");
/*  57: 95 */     text.append("    Number of the instances in the left:\t" + this.nl + "\n");
/*  58: 96 */     text.append("    Number of the instances in the right:\t" + this.nr + "\n");
/*  59: 97 */     text.append("    Sum of the left:\t\t\t" + this.sl + "\n");
/*  60: 98 */     text.append("    Sum of the right:\t\t\t" + this.sr + "\n");
/*  61: 99 */     text.append("    Squared sum of the left:\t\t" + this.s2l + "\n");
/*  62:100 */     text.append("    Squared sum of the right:\t\t" + this.s2r + "\n");
/*  63:101 */     text.append("    Standard deviation of the left:\t" + this.sdl + "\n");
/*  64:102 */     text.append("    Standard deviation of the right:\t" + this.sdr + "\n");
/*  65:103 */     text.append("    Variance of the left:\t\t" + this.vr + "\n");
/*  66:104 */     text.append("    Variance of the right:\t\t" + this.vr + "\n");
/*  67:105 */     text.append("    Overall standard deviation:\t\t" + this.sd + "\n");
/*  68:106 */     text.append("    Overall variance:\t\t\t" + this.va + "\n");
/*  69:107 */     text.append("    Impurity (order " + this.order + "):\t\t" + this.impurity + "\n");
/*  70:    */     
/*  71:109 */     return text.toString();
/*  72:    */   }
/*  73:    */   
/*  74:    */   public final void incremental(double value, int type)
/*  75:    */   {
/*  76:118 */     double y = 0.0D;double yl = 0.0D;double yr = 0.0D;
/*  77:120 */     switch (type)
/*  78:    */     {
/*  79:    */     case 1: 
/*  80:122 */       this.nl += 1.0D;
/*  81:123 */       this.nr -= 1.0D;
/*  82:124 */       this.sl += value;
/*  83:125 */       this.sr -= value;
/*  84:126 */       this.s2l += value * value;
/*  85:127 */       this.s2r -= value * value;
/*  86:128 */       break;
/*  87:    */     case -1: 
/*  88:130 */       this.nl -= 1.0D;
/*  89:131 */       this.nr += 1.0D;
/*  90:132 */       this.sl -= value;
/*  91:133 */       this.sr += value;
/*  92:134 */       this.s2l -= value * value;
/*  93:135 */       this.s2r += value * value;
/*  94:136 */       break;
/*  95:    */     case 0: 
/*  96:    */       break;
/*  97:    */     default: 
/*  98:139 */       System.err.println("wrong type in Impurity.incremental().");
/*  99:    */     }
/* 100:142 */     if (this.nl <= 0.0D)
/* 101:    */     {
/* 102:143 */       this.vl = 0.0D;
/* 103:144 */       this.sdl = 0.0D;
/* 104:    */     }
/* 105:    */     else
/* 106:    */     {
/* 107:147 */       this.vl = ((this.nl * this.s2l - this.sl * this.sl) / (this.nl * this.nl));
/* 108:148 */       this.vl = Math.abs(this.vl);
/* 109:149 */       this.sdl = Math.sqrt(this.vl);
/* 110:    */     }
/* 111:151 */     if (this.nr <= 0.0D)
/* 112:    */     {
/* 113:152 */       this.vr = 0.0D;
/* 114:153 */       this.sdr = 0.0D;
/* 115:    */     }
/* 116:    */     else
/* 117:    */     {
/* 118:156 */       this.vr = ((this.nr * this.s2r - this.sr * this.sr) / (this.nr * this.nr));
/* 119:157 */       this.vr = Math.abs(this.vr);
/* 120:158 */       this.sdr = Math.sqrt(this.vr);
/* 121:    */     }
/* 122:161 */     if (this.order <= 0)
/* 123:    */     {
/* 124:161 */       System.err.println("Impurity order less than zero in Impurity.incremental()");
/* 125:    */     }
/* 126:162 */     else if (this.order == 1)
/* 127:    */     {
/* 128:163 */       y = this.va;yl = this.vl;yr = this.vr;
/* 129:    */     }
/* 130:    */     else
/* 131:    */     {
/* 132:165 */       y = Math.pow(this.va, 1.0D / this.order);
/* 133:166 */       yl = Math.pow(this.vl, 1.0D / this.order);
/* 134:167 */       yr = Math.pow(this.vr, 1.0D / this.order);
/* 135:    */     }
/* 136:170 */     if ((this.nl <= 0.0D) || (this.nr <= 0.0D)) {
/* 137:171 */       this.impurity = 0.0D;
/* 138:    */     } else {
/* 139:173 */       this.impurity = (y - this.nl / this.n * yl - this.nr / this.n * yr);
/* 140:    */     }
/* 141:    */   }
/* 142:    */   
/* 143:    */   public String getRevision()
/* 144:    */   {
/* 145:183 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 146:    */   }
/* 147:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.m5.Impurity
 * JD-Core Version:    0.7.0.1
 */