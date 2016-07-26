/*   1:    */ package weka.classifiers.functions.loss;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.Option;
/*   6:    */ import weka.core.OptionHandler;
/*   7:    */ import weka.core.Utils;
/*   8:    */ 
/*   9:    */ public class ApproximateAbsoluteError
/*  10:    */   implements LossFunction, OptionHandler
/*  11:    */ {
/*  12: 51 */   protected double m_Epsilon = 0.01D;
/*  13:    */   
/*  14:    */   public String globalInfo()
/*  15:    */   {
/*  16: 60 */     return "Approximate absolute error for MLPRegressor and MLPClassifier:\nloss(a, b) = sqrt((a-b)^2+epsilon)";
/*  17:    */   }
/*  18:    */   
/*  19:    */   public double loss(double pred, double actual)
/*  20:    */   {
/*  21: 72 */     double diff = pred - actual;
/*  22: 73 */     return Math.sqrt(diff * diff + this.m_Epsilon);
/*  23:    */   }
/*  24:    */   
/*  25:    */   public double derivative(double pred, double actual)
/*  26:    */   {
/*  27: 84 */     double diff = pred - actual;
/*  28: 85 */     return diff / Math.sqrt(diff * diff + this.m_Epsilon);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public String epsilonTipText()
/*  32:    */   {
/*  33: 93 */     return "The epsilon parameter for the approximate absolute error.";
/*  34:    */   }
/*  35:    */   
/*  36:    */   public double getEpsilon()
/*  37:    */   {
/*  38:101 */     return this.m_Epsilon;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setEpsilon(double epsilon)
/*  42:    */   {
/*  43:109 */     this.m_Epsilon = epsilon;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public Enumeration<Option> listOptions()
/*  47:    */   {
/*  48:120 */     Vector<Option> newVector = new Vector(1);
/*  49:    */     
/*  50:122 */     newVector.addElement(new Option("\tEpsilon to be added (default: 0.01).", "E", 1, "-E <double>"));
/*  51:    */     
/*  52:    */ 
/*  53:125 */     return newVector.elements();
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void setOptions(String[] options)
/*  57:    */     throws Exception
/*  58:    */   {
/*  59:147 */     String epsilon = Utils.getOption('E', options);
/*  60:148 */     if (epsilon.length() != 0) {
/*  61:149 */       setEpsilon(Double.parseDouble(epsilon));
/*  62:    */     } else {
/*  63:151 */       setEpsilon(0.01D);
/*  64:    */     }
/*  65:154 */     Utils.checkForRemainingOptions(options);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public String[] getOptions()
/*  69:    */   {
/*  70:165 */     Vector<String> options = new Vector();
/*  71:    */     
/*  72:167 */     options.add("-E");
/*  73:168 */     options.add("" + getEpsilon());
/*  74:    */     
/*  75:170 */     return (String[])options.toArray(new String[0]);
/*  76:    */   }
/*  77:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.loss.ApproximateAbsoluteError
 * JD-Core Version:    0.7.0.1
 */