/*   1:    */ package weka.classifiers.functions.supportVector;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.core.Instance;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ 
/*   8:    */ public class NormalizedPolyKernel
/*   9:    */   extends PolyKernel
/*  10:    */ {
/*  11:    */   static final long serialVersionUID = 1248574185532130851L;
/*  12:    */   
/*  13:    */   public NormalizedPolyKernel()
/*  14:    */   {
/*  15: 76 */     setExponent(2.0D);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public NormalizedPolyKernel(Instances dataset, int cacheSize, double exponent, boolean lowerOrder)
/*  19:    */     throws Exception
/*  20:    */   {
/*  21: 91 */     super(dataset, cacheSize, exponent, lowerOrder);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public String globalInfo()
/*  25:    */   {
/*  26:101 */     return "The normalized polynomial kernel.\nK(x,y) = <x,y>/sqrt(<x,x><y,y>) where <x,y> = PolyKernel(x,y)";
/*  27:    */   }
/*  28:    */   
/*  29:    */   public double eval(int id1, int id2, Instance inst1)
/*  30:    */     throws Exception
/*  31:    */   {
/*  32:120 */     double div = Math.sqrt(super.eval(id1, id1, inst1) * (this.m_keys != null ? super.eval(id2, id2, this.m_data.instance(id2)) : super.eval(-1, -1, this.m_data.instance(id2))));
/*  33:124 */     if (div != 0.0D) {
/*  34:125 */       return super.eval(id1, id2, inst1) / div;
/*  35:    */     }
/*  36:127 */     return 0.0D;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setExponent(double value)
/*  40:    */   {
/*  41:137 */     if (value != 1.0D) {
/*  42:138 */       super.setExponent(value);
/*  43:    */     } else {
/*  44:140 */       System.out.println("A linear kernel, i.e., Exponent=1, is not possible!");
/*  45:    */     }
/*  46:    */   }
/*  47:    */   
/*  48:    */   public String toString()
/*  49:    */   {
/*  50:    */     String result;
/*  51:    */     String result;
/*  52:151 */     if (getUseLowerOrder()) {
/*  53:152 */       result = "Normalized Poly Kernel with lower order: K(x,y) = (<x,y>+1)^" + getExponent() + "/" + "((<x,x>+1)^" + getExponent() + "*" + "(<y,y>+1)^" + getExponent() + ")^(1/2)";
/*  54:    */     } else {
/*  55:155 */       result = "Normalized Poly Kernel: K(x,y) = <x,y>^" + getExponent() + "/" + "(<x,x>^" + getExponent() + "*" + "<y,y>^" + getExponent() + ")^(1/2)";
/*  56:    */     }
/*  57:158 */     return result;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public String getRevision()
/*  61:    */   {
/*  62:167 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  63:    */   }
/*  64:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.supportVector.NormalizedPolyKernel
 * JD-Core Version:    0.7.0.1
 */