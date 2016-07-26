/*   1:    */ package weka.classifiers.trees.m5;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.classifiers.AbstractClassifier;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ import weka.core.Utils;
/*  10:    */ 
/*  11:    */ public class PreConstructedLinearModel
/*  12:    */   extends AbstractClassifier
/*  13:    */   implements Serializable
/*  14:    */ {
/*  15:    */   static final long serialVersionUID = 2030974097051713247L;
/*  16:    */   private double[] m_coefficients;
/*  17:    */   private double m_intercept;
/*  18:    */   private Instances m_instancesHeader;
/*  19:    */   private int m_numParameters;
/*  20:    */   
/*  21:    */   public PreConstructedLinearModel(double[] coeffs, double intercept)
/*  22:    */   {
/*  23: 68 */     this.m_coefficients = coeffs;
/*  24: 69 */     this.m_intercept = intercept;
/*  25: 70 */     int count = 0;
/*  26: 71 */     for (int i = 0; i < coeffs.length; i++) {
/*  27: 72 */       if (coeffs[i] != 0.0D) {
/*  28: 73 */         count++;
/*  29:    */       }
/*  30:    */     }
/*  31: 76 */     this.m_numParameters = count;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void buildClassifier(Instances instances)
/*  35:    */     throws Exception
/*  36:    */   {
/*  37: 87 */     this.m_instancesHeader = new Instances(instances, 0);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public double classifyInstance(Instance inst)
/*  41:    */     throws Exception
/*  42:    */   {
/*  43: 98 */     double result = 0.0D;
/*  44:101 */     for (int i = 0; i < this.m_coefficients.length; i++) {
/*  45:102 */       if ((i != inst.classIndex()) && (!inst.isMissing(i))) {
/*  46:104 */         result += this.m_coefficients[i] * inst.value(i);
/*  47:    */       }
/*  48:    */     }
/*  49:108 */     result += this.m_intercept;
/*  50:109 */     return result;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public int numParameters()
/*  54:    */   {
/*  55:118 */     return this.m_numParameters;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public double[] coefficients()
/*  59:    */   {
/*  60:127 */     return this.m_coefficients;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public double intercept()
/*  64:    */   {
/*  65:136 */     return this.m_intercept;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public String toString()
/*  69:    */   {
/*  70:145 */     StringBuffer b = new StringBuffer();
/*  71:146 */     b.append("\n" + this.m_instancesHeader.classAttribute().name() + " = ");
/*  72:147 */     boolean first = true;
/*  73:148 */     for (int i = 0; i < this.m_coefficients.length; i++) {
/*  74:149 */       if (this.m_coefficients[i] != 0.0D)
/*  75:    */       {
/*  76:150 */         double c = this.m_coefficients[i];
/*  77:151 */         if (first)
/*  78:    */         {
/*  79:152 */           b.append("\n\t" + Utils.doubleToString(c, 12, 4).trim() + " * " + this.m_instancesHeader.attribute(i).name() + " ");
/*  80:    */           
/*  81:154 */           first = false;
/*  82:    */         }
/*  83:    */         else
/*  84:    */         {
/*  85:156 */           b.append("\n\t" + (this.m_coefficients[i] < 0.0D ? "- " + Utils.doubleToString(Math.abs(c), 12, 4).trim() : new StringBuilder().append("+ ").append(Utils.doubleToString(Math.abs(c), 12, 4).trim()).toString()) + " * " + this.m_instancesHeader.attribute(i).name() + " ");
/*  86:    */         }
/*  87:    */       }
/*  88:    */     }
/*  89:164 */     b.append("\n\t" + (this.m_intercept < 0.0D ? "- " : "+ ") + Utils.doubleToString(Math.abs(this.m_intercept), 12, 4).trim());
/*  90:    */     
/*  91:166 */     return b.toString();
/*  92:    */   }
/*  93:    */   
/*  94:    */   public String getRevision()
/*  95:    */   {
/*  96:175 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  97:    */   }
/*  98:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.m5.PreConstructedLinearModel
 * JD-Core Version:    0.7.0.1
 */