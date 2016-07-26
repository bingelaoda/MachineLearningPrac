/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ 
/*   6:    */ public class BuiltInString
/*   7:    */   extends Function
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = -7391516909331728653L;
/*  10:    */   protected StringFunc m_func;
/*  11:    */   
/*  12:    */   static abstract enum StringFunc
/*  13:    */   {
/*  14: 24 */     UPPERCASE("uppercase"),  SUBSTRING("substring"),  TRIMBLANKS("trimBlanks");
/*  15:    */     
/*  16:    */     private String m_stringVal;
/*  17:    */     
/*  18:    */     abstract String eval(Object[] paramArrayOfObject);
/*  19:    */     
/*  20:    */     abstract boolean legalNumParams(int paramInt);
/*  21:    */     
/*  22:    */     abstract String[] getParameterNames();
/*  23:    */     
/*  24:    */     private StringFunc(String funcName)
/*  25:    */     {
/*  26: 74 */       this.m_stringVal = funcName;
/*  27:    */     }
/*  28:    */     
/*  29:    */     public String toString()
/*  30:    */     {
/*  31: 78 */       return this.m_stringVal;
/*  32:    */     }
/*  33:    */   }
/*  34:    */   
/*  35: 86 */   protected Attribute m_outputDef = null;
/*  36:    */   
/*  37:    */   BuiltInString(StringFunc func)
/*  38:    */   {
/*  39: 89 */     this.m_func = func;
/*  40: 90 */     this.m_functionName = this.m_func.toString();
/*  41:    */   }
/*  42:    */   
/*  43:    */   public Attribute getOutputDef()
/*  44:    */   {
/*  45:101 */     if (this.m_outputDef == null)
/*  46:    */     {
/*  47:102 */       if (this.m_func == StringFunc.SUBSTRING) {
/*  48:105 */         this.m_outputDef = new Attribute("BuiltInStringResult:substring", (ArrayList)null);
/*  49:    */       }
/*  50:108 */       Attribute inputVals = (Attribute)this.m_parameterDefs.get(0);
/*  51:109 */       ArrayList<String> newVals = new ArrayList();
/*  52:110 */       for (int i = 0; i < inputVals.numValues(); i++)
/*  53:    */       {
/*  54:111 */         String inVal = inputVals.value(i);
/*  55:112 */         newVals.add(this.m_func.eval(new Object[] { inVal }));
/*  56:    */       }
/*  57:114 */       this.m_outputDef = new Attribute("BuiltInStringResult:" + this.m_func.toString(), newVals);
/*  58:    */     }
/*  59:117 */     return this.m_outputDef;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String[] getParameterNames()
/*  63:    */   {
/*  64:129 */     return this.m_func.getParameterNames();
/*  65:    */   }
/*  66:    */   
/*  67:    */   private Object[] setUpArgs(double[] incoming)
/*  68:    */   {
/*  69:134 */     Object[] args = new Object[incoming.length];
/*  70:135 */     Attribute input = (Attribute)this.m_parameterDefs.get(0);
/*  71:136 */     args[0] = input.value((int)incoming[0]);
/*  72:137 */     for (int i = 1; i < incoming.length; i++) {
/*  73:138 */       args[i] = new Integer((int)incoming[i]);
/*  74:    */     }
/*  75:141 */     return args;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public double getResult(double[] incoming)
/*  79:    */     throws Exception
/*  80:    */   {
/*  81:156 */     if (this.m_parameterDefs == null) {
/*  82:157 */       throw new Exception("[BuiltInString] incoming parameter structure has not been set");
/*  83:    */     }
/*  84:160 */     if (!this.m_func.legalNumParams(incoming.length)) {
/*  85:161 */       throw new Exception("[BuiltInString] wrong number of parameters!");
/*  86:    */     }
/*  87:165 */     Object[] args = setUpArgs(incoming);
/*  88:    */     
/*  89:    */ 
/*  90:168 */     String result = this.m_func.eval(args);
/*  91:169 */     int resultI = this.m_outputDef.indexOfValue(result);
/*  92:170 */     if (resultI < 0) {
/*  93:171 */       if (this.m_outputDef.isString()) {
/*  94:173 */         resultI = this.m_outputDef.addStringValue(result);
/*  95:    */       } else {
/*  96:175 */         throw new Exception("[BuiltInString] unable to find value " + result + " in nominal result type!");
/*  97:    */       }
/*  98:    */     }
/*  99:180 */     return resultI;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setParameterDefs(ArrayList<Attribute> paramDefs)
/* 103:    */     throws Exception
/* 104:    */   {
/* 105:224 */     this.m_parameterDefs = paramDefs;
/* 106:226 */     if (!this.m_func.legalNumParams(this.m_parameterDefs.size())) {
/* 107:227 */       throw new Exception("[BuiltInMath] illegal number of parameters for function: " + this.m_functionName);
/* 108:    */     }
/* 109:    */   }
/* 110:    */   
/* 111:    */   public String toString()
/* 112:    */   {
/* 113:233 */     String result = this.m_func.toString() + "(";
/* 114:234 */     for (int i = 0; i < this.m_parameterDefs.size(); i++)
/* 115:    */     {
/* 116:235 */       result = result + ((Attribute)this.m_parameterDefs.get(i)).name();
/* 117:236 */       if (i != this.m_parameterDefs.size() - 1) {
/* 118:237 */         result = result + ", ";
/* 119:    */       } else {
/* 120:239 */         result = result + ")";
/* 121:    */       }
/* 122:    */     }
/* 123:242 */     return result;
/* 124:    */   }
/* 125:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.BuiltInString
 * JD-Core Version:    0.7.0.1
 */