/*   1:    */ package weka.classifiers.mi.supportVector;
/*   2:    */ 
/*   3:    */ import weka.classifiers.functions.supportVector.PolyKernel;
/*   4:    */ import weka.core.Capabilities;
/*   5:    */ import weka.core.Capabilities.Capability;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.MultiInstanceCapabilitiesHandler;
/*   9:    */ import weka.core.RevisionUtils;
/*  10:    */ 
/*  11:    */ public class MIPolyKernel
/*  12:    */   extends PolyKernel
/*  13:    */   implements MultiInstanceCapabilitiesHandler
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = 7926421479341051777L;
/*  16:    */   
/*  17:    */   public MIPolyKernel() {}
/*  18:    */   
/*  19:    */   public MIPolyKernel(Instances data, int cacheSize, double exponent, boolean lowerOrder)
/*  20:    */     throws Exception
/*  21:    */   {
/*  22: 96 */     super(data, cacheSize, exponent, lowerOrder);
/*  23:    */   }
/*  24:    */   
/*  25:    */   protected double evaluate(int id1, int id2, Instance inst1)
/*  26:    */     throws Exception
/*  27:    */   {
/*  28:111 */     Instances data1 = new Instances(inst1.relationalValue(1));
/*  29:    */     Instances data2;
/*  30:    */     Instances data2;
/*  31:113 */     if (id1 == id2) {
/*  32:114 */       data2 = new Instances(data1);
/*  33:    */     } else {
/*  34:116 */       data2 = new Instances(this.m_data.instance(id2).relationalValue(1));
/*  35:    */     }
/*  36:118 */     double res = 0.0D;
/*  37:119 */     for (int i = 0; i < data1.numInstances(); i++) {
/*  38:120 */       for (int j = 0; j < data2.numInstances(); j++)
/*  39:    */       {
/*  40:121 */         double result = dotProd(data1.instance(i), data2.instance(j));
/*  41:124 */         if (getUseLowerOrder()) {
/*  42:125 */           result += 1.0D;
/*  43:    */         }
/*  44:127 */         if (getExponent() != 1.0D) {
/*  45:128 */           result = Math.pow(result, getExponent());
/*  46:    */         }
/*  47:131 */         res += result;
/*  48:    */       }
/*  49:    */     }
/*  50:135 */     return res;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public Capabilities getCapabilities()
/*  54:    */   {
/*  55:145 */     Capabilities result = super.getCapabilities();
/*  56:    */     
/*  57:    */ 
/*  58:148 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  59:149 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/*  60:150 */     result.disable(Capabilities.Capability.MISSING_VALUES);
/*  61:    */     
/*  62:    */ 
/*  63:153 */     result.enableAllClasses();
/*  64:    */     
/*  65:    */ 
/*  66:156 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/*  67:    */     
/*  68:158 */     return result;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public Capabilities getMultiInstanceCapabilities()
/*  72:    */   {
/*  73:169 */     Capabilities result = super.getCapabilities();
/*  74:    */     
/*  75:    */ 
/*  76:172 */     result.disableAllClasses();
/*  77:173 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  78:    */     
/*  79:175 */     return result;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void clean()
/*  83:    */   {
/*  84:182 */     this.m_storage = null;
/*  85:183 */     this.m_keys = null;
/*  86:184 */     this.m_kernelMatrix = ((double[][])null);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public String getRevision()
/*  90:    */   {
/*  91:193 */     return RevisionUtils.extract("$Revision: 9142 $");
/*  92:    */   }
/*  93:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.supportVector.MIPolyKernel
 * JD-Core Version:    0.7.0.1
 */