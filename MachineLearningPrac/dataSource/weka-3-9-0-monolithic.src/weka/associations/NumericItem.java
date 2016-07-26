/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Utils;
/*   6:    */ 
/*   7:    */ public class NumericItem
/*   8:    */   extends Item
/*   9:    */   implements Serializable
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = -7869433770765864800L;
/*  12:    */   protected double m_splitPoint;
/*  13:    */   protected Comparison m_comparison;
/*  14:    */   
/*  15:    */   public static enum Comparison
/*  16:    */   {
/*  17: 41 */     NONE,  EQUAL,  LESS_THAN_OR_EQUAL_TO,  GREATER_THAN;
/*  18:    */     
/*  19:    */     private Comparison() {}
/*  20:    */   }
/*  21:    */   
/*  22:    */   public NumericItem(Attribute att, double splitPoint, Comparison comp)
/*  23:    */     throws Exception
/*  24:    */   {
/*  25: 59 */     super(att);
/*  26: 61 */     if (!att.isNumeric()) {
/*  27: 62 */       throw new Exception("NumericItem must be constructed using a numeric attribute");
/*  28:    */     }
/*  29: 66 */     this.m_comparison = comp;
/*  30: 67 */     this.m_splitPoint = splitPoint;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public double getSplitPoint()
/*  34:    */   {
/*  35: 76 */     return this.m_splitPoint;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Comparison getComparison()
/*  39:    */   {
/*  40: 85 */     return this.m_comparison;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public String getItemValueAsString()
/*  44:    */   {
/*  45: 95 */     return Utils.doubleToString(this.m_splitPoint, 3);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public String getComparisonAsString()
/*  49:    */   {
/*  50:105 */     String result = null;
/*  51:107 */     switch (1.$SwitchMap$weka$associations$NumericItem$Comparison[this.m_comparison.ordinal()])
/*  52:    */     {
/*  53:    */     case 1: 
/*  54:109 */       result = "=";
/*  55:110 */       break;
/*  56:    */     case 2: 
/*  57:112 */       result = "<=";
/*  58:113 */       break;
/*  59:    */     case 3: 
/*  60:115 */       result = ">";
/*  61:116 */       break;
/*  62:    */     }
/*  63:121 */     return result;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String toString(boolean freq)
/*  67:    */   {
/*  68:134 */     StringBuffer result = new StringBuffer();
/*  69:135 */     result.append(this.m_attribute.name() + " ");
/*  70:136 */     switch (1.$SwitchMap$weka$associations$NumericItem$Comparison[this.m_comparison.ordinal()])
/*  71:    */     {
/*  72:    */     case 1: 
/*  73:138 */       result.append("=");
/*  74:139 */       break;
/*  75:    */     case 2: 
/*  76:141 */       result.append("<=");
/*  77:142 */       break;
/*  78:    */     case 3: 
/*  79:144 */       result.append(">");
/*  80:145 */       break;
/*  81:    */     }
/*  82:150 */     result.append(" " + Utils.doubleToString(this.m_splitPoint, 4));
/*  83:151 */     if (freq) {
/*  84:152 */       result.append(":" + this.m_frequency);
/*  85:    */     }
/*  86:155 */     return result.toString();
/*  87:    */   }
/*  88:    */   
/*  89:    */   public boolean equals(Object compareTo)
/*  90:    */   {
/*  91:165 */     if (!(compareTo instanceof NumericItem)) {
/*  92:166 */       return false;
/*  93:    */     }
/*  94:169 */     NumericItem b = (NumericItem)compareTo;
/*  95:170 */     if ((this.m_attribute.equals(b.getAttribute())) && (this.m_comparison == b.getComparison()) && (new Double(this.m_splitPoint).equals(new Double(b.getSplitPoint())))) {
/*  96:173 */       return true;
/*  97:    */     }
/*  98:176 */     return false;
/*  99:    */   }
/* 100:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.NumericItem
 * JD-Core Version:    0.7.0.1
 */