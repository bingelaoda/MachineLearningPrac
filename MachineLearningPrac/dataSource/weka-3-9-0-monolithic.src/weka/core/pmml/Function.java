/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ 
/*   7:    */ public abstract class Function
/*   8:    */   implements Serializable
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -6997738288201933171L;
/*  11:    */   protected String m_functionName;
/*  12: 46 */   protected ArrayList<Attribute> m_parameterDefs = null;
/*  13:    */   
/*  14:    */   public String getName()
/*  15:    */   {
/*  16: 50 */     return this.m_functionName;
/*  17:    */   }
/*  18:    */   
/*  19:    */   public abstract String[] getParameterNames();
/*  20:    */   
/*  21:    */   public abstract void setParameterDefs(ArrayList<Attribute> paramArrayList)
/*  22:    */     throws Exception;
/*  23:    */   
/*  24:    */   public abstract Attribute getOutputDef();
/*  25:    */   
/*  26:    */   public abstract double getResult(double[] paramArrayOfDouble)
/*  27:    */     throws Exception;
/*  28:    */   
/*  29:    */   public static Function getFunction(String name)
/*  30:    */   {
/*  31:160 */     Function result = null;
/*  32:    */     
/*  33:162 */     name = name.trim();
/*  34:163 */     if (name.equals("+")) {
/*  35:164 */       result = new BuiltInArithmetic(BuiltInArithmetic.Operator.ADDITION);
/*  36:165 */     } else if (name.equals("-")) {
/*  37:166 */       result = new BuiltInArithmetic(BuiltInArithmetic.Operator.SUBTRACTION);
/*  38:167 */     } else if (name.equals("*")) {
/*  39:168 */       result = new BuiltInArithmetic(BuiltInArithmetic.Operator.MULTIPLICATION);
/*  40:169 */     } else if (name.equals("/")) {
/*  41:170 */       result = new BuiltInArithmetic(BuiltInArithmetic.Operator.DIVISION);
/*  42:171 */     } else if (name.equals(BuiltInMath.MathFunc.MIN.toString())) {
/*  43:172 */       result = new BuiltInMath(BuiltInMath.MathFunc.MIN);
/*  44:173 */     } else if (name.equals(BuiltInMath.MathFunc.MAX.toString())) {
/*  45:174 */       result = new BuiltInMath(BuiltInMath.MathFunc.MAX);
/*  46:175 */     } else if (name.equals(BuiltInMath.MathFunc.SUM.toString())) {
/*  47:176 */       result = new BuiltInMath(BuiltInMath.MathFunc.SUM);
/*  48:177 */     } else if (name.equals(BuiltInMath.MathFunc.AVG.toString())) {
/*  49:178 */       result = new BuiltInMath(BuiltInMath.MathFunc.AVG);
/*  50:179 */     } else if (name.equals(BuiltInMath.MathFunc.LOG10.toString())) {
/*  51:180 */       result = new BuiltInMath(BuiltInMath.MathFunc.LOG10);
/*  52:181 */     } else if (name.equals(BuiltInMath.MathFunc.LN.toString())) {
/*  53:182 */       result = new BuiltInMath(BuiltInMath.MathFunc.LN);
/*  54:183 */     } else if (name.equals(BuiltInMath.MathFunc.SQRT.toString())) {
/*  55:184 */       result = new BuiltInMath(BuiltInMath.MathFunc.SQRT);
/*  56:185 */     } else if (name.equals(BuiltInMath.MathFunc.ABS.toString())) {
/*  57:186 */       result = new BuiltInMath(BuiltInMath.MathFunc.ABS);
/*  58:187 */     } else if (name.equals(BuiltInMath.MathFunc.EXP.toString())) {
/*  59:188 */       result = new BuiltInMath(BuiltInMath.MathFunc.EXP);
/*  60:189 */     } else if (name.equals(BuiltInMath.MathFunc.POW.toString())) {
/*  61:190 */       result = new BuiltInMath(BuiltInMath.MathFunc.POW);
/*  62:191 */     } else if (name.equals(BuiltInMath.MathFunc.THRESHOLD.toString())) {
/*  63:192 */       result = new BuiltInMath(BuiltInMath.MathFunc.THRESHOLD);
/*  64:193 */     } else if (name.equals(BuiltInMath.MathFunc.FLOOR.toString())) {
/*  65:194 */       result = new BuiltInMath(BuiltInMath.MathFunc.FLOOR);
/*  66:195 */     } else if (name.equals(BuiltInMath.MathFunc.CEIL.toString())) {
/*  67:196 */       result = new BuiltInMath(BuiltInMath.MathFunc.CEIL);
/*  68:197 */     } else if (name.equals(BuiltInMath.MathFunc.ROUND.toString())) {
/*  69:198 */       result = new BuiltInMath(BuiltInMath.MathFunc.ROUND);
/*  70:199 */     } else if (name.equals(BuiltInString.StringFunc.UPPERCASE.toString())) {
/*  71:200 */       result = new BuiltInString(BuiltInString.StringFunc.UPPERCASE);
/*  72:201 */     } else if (name.equals(BuiltInString.StringFunc.SUBSTRING.toString())) {
/*  73:202 */       result = new BuiltInString(BuiltInString.StringFunc.SUBSTRING);
/*  74:203 */     } else if (name.equals(BuiltInString.StringFunc.TRIMBLANKS.toString())) {
/*  75:204 */       result = new BuiltInString(BuiltInString.StringFunc.TRIMBLANKS);
/*  76:    */     }
/*  77:207 */     return result;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static Function getFunction(String name, TransformationDictionary transDict)
/*  81:    */     throws Exception
/*  82:    */   {
/*  83:224 */     Function result = getFunction(name);
/*  84:227 */     if ((result == null) && (transDict != null)) {
/*  85:228 */       result = transDict.getFunction(name);
/*  86:    */     }
/*  87:231 */     if (result == null) {
/*  88:232 */       throw new Exception("[Function] unknown/unsupported function " + name);
/*  89:    */     }
/*  90:235 */     return result;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String toString()
/*  94:    */   {
/*  95:239 */     return toString("");
/*  96:    */   }
/*  97:    */   
/*  98:    */   public String toString(String pad)
/*  99:    */   {
/* 100:243 */     return pad + getClass().getName();
/* 101:    */   }
/* 102:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.Function
 * JD-Core Version:    0.7.0.1
 */