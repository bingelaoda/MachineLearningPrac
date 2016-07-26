/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ 
/*   6:    */ public class BuiltInArithmetic
/*   7:    */   extends Function
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 2275009453597279459L;
/*  10:    */   
/*  11:    */   static abstract enum Operator
/*  12:    */   {
/*  13: 45 */     ADDITION(" + "),  SUBTRACTION(" - "),  MULTIPLICATION(" * "),  DIVISION(" / ");
/*  14:    */     
/*  15:    */     private final String m_stringVal;
/*  16:    */     
/*  17:    */     abstract double eval(double paramDouble1, double paramDouble2);
/*  18:    */     
/*  19:    */     private Operator(String opName)
/*  20:    */     {
/*  21: 71 */       this.m_stringVal = opName;
/*  22:    */     }
/*  23:    */     
/*  24:    */     public String toString()
/*  25:    */     {
/*  26: 75 */       return this.m_stringVal;
/*  27:    */     }
/*  28:    */   }
/*  29:    */   
/*  30: 80 */   protected Operator m_operator = Operator.ADDITION;
/*  31:    */   
/*  32:    */   public BuiltInArithmetic(Operator op)
/*  33:    */   {
/*  34: 87 */     this.m_operator = op;
/*  35: 88 */     this.m_functionName = this.m_operator.toString();
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setParameterDefs(ArrayList<Attribute> paramDefs)
/*  39:    */     throws Exception
/*  40:    */   {
/*  41:100 */     this.m_parameterDefs = paramDefs;
/*  42:102 */     if (this.m_parameterDefs.size() != 2) {
/*  43:103 */       throw new Exception("[Arithmetic] wrong number of parameters. Recieved " + this.m_parameterDefs.size() + ", expected 2.");
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String[] getParameterNames()
/*  48:    */   {
/*  49:115 */     String[] result = { "A", "B" };
/*  50:116 */     return result;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public Attribute getOutputDef()
/*  54:    */   {
/*  55:126 */     return new Attribute("BuiltInArithmeticResult:" + this.m_operator.toString());
/*  56:    */   }
/*  57:    */   
/*  58:    */   public double getResult(double[] incoming)
/*  59:    */     throws Exception
/*  60:    */   {
/*  61:140 */     if (this.m_parameterDefs == null) {
/*  62:141 */       throw new Exception("[BuiltInArithmetic] incoming parameter structure has not been set!");
/*  63:    */     }
/*  64:144 */     if ((this.m_parameterDefs.size() != 2) || (incoming.length != 2)) {
/*  65:145 */       throw new Exception("[BuiltInArithmetic] wrong number of parameters!");
/*  66:    */     }
/*  67:148 */     double result = this.m_operator.eval(incoming[0], incoming[1]);
/*  68:    */     
/*  69:150 */     return result;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public String toString()
/*  73:    */   {
/*  74:154 */     return toString("");
/*  75:    */   }
/*  76:    */   
/*  77:    */   public String toString(String pad)
/*  78:    */   {
/*  79:158 */     return pad + ((Attribute)this.m_parameterDefs.get(0)).name() + this.m_functionName + ((Attribute)this.m_parameterDefs.get(1)).name();
/*  80:    */   }
/*  81:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.BuiltInArithmetic
 * JD-Core Version:    0.7.0.1
 */