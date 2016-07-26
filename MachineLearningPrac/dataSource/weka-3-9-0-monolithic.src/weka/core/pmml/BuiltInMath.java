/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Utils;
/*   6:    */ 
/*   7:    */ public class BuiltInMath
/*   8:    */   extends Function
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -8092338695602573652L;
/*  11:    */   
/*  12:    */   static abstract enum MathFunc
/*  13:    */   {
/*  14: 46 */     MIN("min"),  MAX("max"),  SUM("sum"),  AVG("avg"),  LOG10("log10"),  LN("ln"),  SQRT("sqrt"),  ABS("abs"),  EXP("exp"),  POW("pow"),  THRESHOLD("threshold"),  FLOOR("floor"),  CEIL("ceil"),  ROUND("round");
/*  15:    */     
/*  16:    */     private final String m_stringVal;
/*  17:    */     
/*  18:    */     abstract double eval(double[] paramArrayOfDouble);
/*  19:    */     
/*  20:    */     abstract boolean legalNumParams(int paramInt);
/*  21:    */     
/*  22:    */     abstract String[] getParameterNames();
/*  23:    */     
/*  24:    */     private MathFunc(String funcName)
/*  25:    */     {
/*  26:240 */       this.m_stringVal = funcName;
/*  27:    */     }
/*  28:    */     
/*  29:    */     public String toString()
/*  30:    */     {
/*  31:244 */       return this.m_stringVal;
/*  32:    */     }
/*  33:    */   }
/*  34:    */   
/*  35:249 */   protected MathFunc m_func = MathFunc.ABS;
/*  36:    */   
/*  37:    */   public BuiltInMath(MathFunc func)
/*  38:    */   {
/*  39:256 */     this.m_func = func;
/*  40:257 */     this.m_functionName = this.m_func.toString();
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void setParameterDefs(ArrayList<Attribute> paramDefs)
/*  44:    */     throws Exception
/*  45:    */   {
/*  46:269 */     this.m_parameterDefs = paramDefs;
/*  47:271 */     if (!this.m_func.legalNumParams(this.m_parameterDefs.size())) {
/*  48:272 */       throw new Exception("[BuiltInMath] illegal number of parameters for function: " + this.m_functionName);
/*  49:    */     }
/*  50:    */   }
/*  51:    */   
/*  52:    */   public Attribute getOutputDef()
/*  53:    */   {
/*  54:284 */     return new Attribute("BuiltInMathResult:" + this.m_func.toString());
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String[] getParameterNames()
/*  58:    */   {
/*  59:296 */     return this.m_func.getParameterNames();
/*  60:    */   }
/*  61:    */   
/*  62:    */   public double getResult(double[] incoming)
/*  63:    */     throws Exception
/*  64:    */   {
/*  65:310 */     if (this.m_parameterDefs == null) {
/*  66:311 */       throw new Exception("[BuiltInMath] incoming parameter structure has not been set");
/*  67:    */     }
/*  68:314 */     if (!this.m_func.legalNumParams(incoming.length)) {
/*  69:315 */       throw new Exception("[BuiltInMath] wrong number of parameters!");
/*  70:    */     }
/*  71:318 */     double result = this.m_func.eval(incoming);
/*  72:    */     
/*  73:320 */     return result;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String toString()
/*  77:    */   {
/*  78:324 */     String result = this.m_func.toString() + "(";
/*  79:325 */     for (int i = 0; i < this.m_parameterDefs.size(); i++)
/*  80:    */     {
/*  81:326 */       result = result + ((Attribute)this.m_parameterDefs.get(i)).name();
/*  82:327 */       if (i != this.m_parameterDefs.size() - 1) {
/*  83:328 */         result = result + ", ";
/*  84:    */       } else {
/*  85:330 */         result = result + ")";
/*  86:    */       }
/*  87:    */     }
/*  88:333 */     return result;
/*  89:    */   }
/*  90:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.BuiltInMath
 * JD-Core Version:    0.7.0.1
 */