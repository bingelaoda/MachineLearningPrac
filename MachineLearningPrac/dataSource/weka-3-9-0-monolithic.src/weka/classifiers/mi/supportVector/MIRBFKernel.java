/*   1:    */ package weka.classifiers.mi.supportVector;
/*   2:    */ 
/*   3:    */ import weka.classifiers.functions.supportVector.RBFKernel;
/*   4:    */ import weka.core.Capabilities;
/*   5:    */ import weka.core.Capabilities.Capability;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.MultiInstanceCapabilitiesHandler;
/*   9:    */ import weka.core.RevisionUtils;
/*  10:    */ 
/*  11:    */ public class MIRBFKernel
/*  12:    */   extends RBFKernel
/*  13:    */   implements MultiInstanceCapabilitiesHandler
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = -8711882393708956962L;
/*  16:    */   protected double[][] m_kernelPrecalc;
/*  17:    */   
/*  18:    */   public MIRBFKernel() {}
/*  19:    */   
/*  20:    */   public MIRBFKernel(Instances data, int cacheSize, double gamma)
/*  21:    */     throws Exception
/*  22:    */   {
/*  23: 95 */     super(data, cacheSize, gamma);
/*  24:    */   }
/*  25:    */   
/*  26:    */   protected double evaluate(int id1, int id2, Instance inst1)
/*  27:    */     throws Exception
/*  28:    */   {
/*  29:109 */     double result = 0.0D;
/*  30:    */     Instances insts1;
/*  31:    */     Instances insts1;
/*  32:111 */     if (id1 == -1) {
/*  33:112 */       insts1 = new Instances(inst1.relationalValue(1));
/*  34:    */     } else {
/*  35:114 */       insts1 = new Instances(this.m_data.instance(id1).relationalValue(1));
/*  36:    */     }
/*  37:115 */     Instances insts2 = new Instances(this.m_data.instance(id2).relationalValue(1));
/*  38:    */     
/*  39:117 */     double precalc1 = 0.0D;
/*  40:118 */     for (int i = 0; i < insts1.numInstances(); i++) {
/*  41:119 */       for (int j = 0; j < insts2.numInstances(); j++)
/*  42:    */       {
/*  43:120 */         if (id1 == -1) {
/*  44:121 */           precalc1 = dotProd(insts1.instance(i), insts1.instance(i));
/*  45:    */         } else {
/*  46:123 */           precalc1 = this.m_kernelPrecalc[id1][i];
/*  47:    */         }
/*  48:125 */         double res = Math.exp(this.m_gamma * (2.0D * dotProd(insts1.instance(i), insts2.instance(j)) - precalc1 - this.m_kernelPrecalc[id2][j]));
/*  49:    */         
/*  50:127 */         result += res;
/*  51:    */       }
/*  52:    */     }
/*  53:131 */     return result;
/*  54:    */   }
/*  55:    */   
/*  56:    */   protected void initVars(Instances data)
/*  57:    */   {
/*  58:140 */     super.initVars(data);
/*  59:    */     
/*  60:142 */     this.m_kernelPrecalc = new double[data.numInstances()][];
/*  61:    */   }
/*  62:    */   
/*  63:    */   public Capabilities getCapabilities()
/*  64:    */   {
/*  65:152 */     Capabilities result = super.getCapabilities();
/*  66:    */     
/*  67:    */ 
/*  68:155 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  69:156 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/*  70:157 */     result.disable(Capabilities.Capability.MISSING_VALUES);
/*  71:    */     
/*  72:    */ 
/*  73:160 */     result.enableAllClasses();
/*  74:    */     
/*  75:    */ 
/*  76:163 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/*  77:    */     
/*  78:165 */     return result;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public Capabilities getMultiInstanceCapabilities()
/*  82:    */   {
/*  83:176 */     Capabilities result = super.getCapabilities();
/*  84:    */     
/*  85:    */ 
/*  86:179 */     result.disableAllClasses();
/*  87:180 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  88:    */     
/*  89:182 */     return result;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void buildKernel(Instances data)
/*  93:    */     throws Exception
/*  94:    */   {
/*  95:194 */     if (!getChecksTurnedOff()) {
/*  96:195 */       getCapabilities().testWithFail(data);
/*  97:    */     }
/*  98:197 */     initVars(data);
/*  99:199 */     for (int i = 0; i < data.numInstances(); i++)
/* 100:    */     {
/* 101:200 */       Instances insts = new Instances(data.instance(i).relationalValue(1));
/* 102:201 */       this.m_kernelPrecalc[i] = new double[insts.numInstances()];
/* 103:202 */       for (int j = 0; j < insts.numInstances(); j++) {
/* 104:203 */         this.m_kernelPrecalc[i][j] = dotProd(insts.instance(j), insts.instance(j));
/* 105:    */       }
/* 106:    */     }
/* 107:    */   }
/* 108:    */   
/* 109:    */   public String getRevision()
/* 110:    */   {
/* 111:213 */     return RevisionUtils.extract("$Revision: 9142 $");
/* 112:    */   }
/* 113:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.supportVector.MIRBFKernel
 * JD-Core Version:    0.7.0.1
 */