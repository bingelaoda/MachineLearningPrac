/*    1:     */ package weka.classifiers.mi;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import weka.core.Optimization;
/*    5:     */ import weka.core.RevisionUtils;
/*    6:     */ 
/*    7:     */ class TLDSimple_Optm
/*    8:     */   extends Optimization
/*    9:     */ {
/*   10:     */   private double[] num;
/*   11:     */   private double sSq;
/*   12:     */   private double[] xBar;
/*   13:     */   
/*   14:     */   public void setNum(double[] n)
/*   15:     */   {
/*   16: 946 */     this.num = n;
/*   17:     */   }
/*   18:     */   
/*   19:     */   public void setSgmSq(double s)
/*   20:     */   {
/*   21: 951 */     this.sSq = s;
/*   22:     */   }
/*   23:     */   
/*   24:     */   public void setXBar(double[] x)
/*   25:     */   {
/*   26: 955 */     this.xBar = x;
/*   27:     */   }
/*   28:     */   
/*   29:     */   protected double objectiveFunction(double[] x)
/*   30:     */   {
/*   31: 963 */     int numExs = this.num.length;
/*   32: 964 */     double NLL = 0.0D;
/*   33:     */     
/*   34: 966 */     double w = x[0];double m = x[1];
/*   35: 967 */     for (int j = 0; j < numExs; j++) {
/*   36: 969 */       if (!Double.isNaN(this.xBar[j]))
/*   37:     */       {
/*   38: 972 */         double bag = 0.0D;
/*   39:     */         
/*   40: 974 */         bag += Math.log(w * this.num[j] + this.sSq);
/*   41: 976 */         if ((Double.isNaN(bag)) && (this.m_Debug)) {
/*   42: 977 */           System.out.println("???????????1: " + w + " " + m + "|x-: " + this.xBar[j] + "|n: " + this.num[j] + "|S^2: " + this.sSq);
/*   43:     */         }
/*   44: 982 */         bag += this.num[j] * (m - this.xBar[j]) * (m - this.xBar[j]) / (w * this.num[j] + this.sSq);
/*   45: 983 */         if ((Double.isNaN(bag)) && (this.m_Debug)) {
/*   46: 984 */           System.out.println("???????????2: " + w + " " + m + "|x-: " + this.xBar[j] + "|n: " + this.num[j] + "|S^2: " + this.sSq);
/*   47:     */         }
/*   48: 990 */         NLL += bag;
/*   49:     */       }
/*   50:     */     }
/*   51: 994 */     return NLL;
/*   52:     */   }
/*   53:     */   
/*   54:     */   protected double[] evaluateGradient(double[] x)
/*   55:     */   {
/*   56:1003 */     double[] g = new double[x.length];
/*   57:1004 */     int numExs = this.num.length;
/*   58:     */     
/*   59:1006 */     double w = x[0];double m = x[1];
/*   60:1007 */     double dw = 0.0D;double dm = 0.0D;
/*   61:1009 */     for (int j = 0; j < numExs; j++) {
/*   62:1011 */       if (!Double.isNaN(this.xBar[j]))
/*   63:     */       {
/*   64:1014 */         dw += this.num[j] / (w * this.num[j] + this.sSq) - this.num[j] * this.num[j] * (m - this.xBar[j]) * (m - this.xBar[j]) / ((w * this.num[j] + this.sSq) * (w * this.num[j] + this.sSq));
/*   65:     */         
/*   66:     */ 
/*   67:1017 */         dm += 2.0D * this.num[j] * (m - this.xBar[j]) / (w * this.num[j] + this.sSq);
/*   68:     */       }
/*   69:     */     }
/*   70:1020 */     g[0] = dw;
/*   71:1021 */     g[1] = dm;
/*   72:1022 */     return g;
/*   73:     */   }
/*   74:     */   
/*   75:     */   protected double[] evaluateHessian(double[] x, int index)
/*   76:     */   {
/*   77:1031 */     double[] h = new double[x.length];
/*   78:     */     
/*   79:     */ 
/*   80:     */ 
/*   81:1035 */     int numExs = this.num.length;
/*   82:     */     double w;
/*   83:     */     double m;
/*   84:1038 */     switch (index)
/*   85:     */     {
/*   86:     */     case 0: 
/*   87:1040 */       w = x[0];
/*   88:1041 */       m = x[1];
/*   89:1043 */       for (int j = 0; j < numExs; j++) {
/*   90:1044 */         if (!Double.isNaN(this.xBar[j]))
/*   91:     */         {
/*   92:1048 */           h[0] += 2.0D * Math.pow(this.num[j], 3.0D) * (m - this.xBar[j]) * (m - this.xBar[j]) / Math.pow(w * this.num[j] + this.sSq, 3.0D) - this.num[j] * this.num[j] / ((w * this.num[j] + this.sSq) * (w * this.num[j] + this.sSq));
/*   93:     */           
/*   94:     */ 
/*   95:     */ 
/*   96:1052 */           h[1] -= 2.0D * (m - this.xBar[j]) * this.num[j] * this.num[j] / ((this.num[j] * w + this.sSq) * (this.num[j] * w + this.sSq));
/*   97:     */         }
/*   98:     */       }
/*   99:1055 */       break;
/*  100:     */     case 1: 
/*  101:1058 */       w = x[0];
/*  102:1059 */       m = x[1];
/*  103:1061 */       for (int j = 0; j < numExs; j++) {
/*  104:1062 */         if (!Double.isNaN(this.xBar[j]))
/*  105:     */         {
/*  106:1066 */           h[0] -= 2.0D * (m - this.xBar[j]) * this.num[j] * this.num[j] / ((this.num[j] * w + this.sSq) * (this.num[j] * w + this.sSq));
/*  107:     */           
/*  108:     */ 
/*  109:1069 */           h[1] += 2.0D * this.num[j] / (w * this.num[j] + this.sSq);
/*  110:     */         }
/*  111:     */       }
/*  112:     */     }
/*  113:1073 */     return h;
/*  114:     */   }
/*  115:     */   
/*  116:     */   public String getRevision()
/*  117:     */   {
/*  118:1083 */     return RevisionUtils.extract("$Revision: 10369 $");
/*  119:     */   }
/*  120:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.TLDSimple_Optm
 * JD-Core Version:    0.7.0.1
 */