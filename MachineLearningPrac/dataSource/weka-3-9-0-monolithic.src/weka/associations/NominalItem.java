/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ 
/*   6:    */ public class NominalItem
/*   7:    */   extends Item
/*   8:    */   implements Serializable
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = 2182122099990462066L;
/*  11:    */   protected int m_valueIndex;
/*  12:    */   
/*  13:    */   public NominalItem(Attribute att, int valueIndex)
/*  14:    */     throws Exception
/*  15:    */   {
/*  16: 52 */     super(att);
/*  17: 54 */     if (att.isNumeric()) {
/*  18: 55 */       throw new Exception("NominalItem must be constructed using a nominal attribute");
/*  19:    */     }
/*  20: 57 */     this.m_attribute = att;
/*  21: 58 */     if (this.m_attribute.numValues() == 1) {
/*  22: 59 */       this.m_valueIndex = 0;
/*  23:    */     } else {
/*  24: 61 */       this.m_valueIndex = valueIndex;
/*  25:    */     }
/*  26:    */   }
/*  27:    */   
/*  28:    */   public int getValueIndex()
/*  29:    */   {
/*  30: 71 */     return this.m_valueIndex;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public String getItemValueAsString()
/*  34:    */   {
/*  35: 80 */     return this.m_attribute.value(this.m_valueIndex);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public String getComparisonAsString()
/*  39:    */   {
/*  40: 89 */     return "=";
/*  41:    */   }
/*  42:    */   
/*  43:    */   public String toString(boolean freq)
/*  44:    */   {
/*  45:102 */     String result = this.m_attribute.name() + "=" + this.m_attribute.value(this.m_valueIndex);
/*  46:103 */     if (freq) {
/*  47:104 */       result = result + ":" + this.m_frequency;
/*  48:    */     }
/*  49:106 */     return result;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public boolean equals(Object compareTo)
/*  53:    */   {
/*  54:114 */     if (!(compareTo instanceof NominalItem)) {
/*  55:115 */       return false;
/*  56:    */     }
/*  57:118 */     NominalItem b = (NominalItem)compareTo;
/*  58:119 */     if ((this.m_attribute.equals(b.getAttribute())) && (this.m_valueIndex == b.getValueIndex())) {
/*  59:122 */       return true;
/*  60:    */     }
/*  61:125 */     return false;
/*  62:    */   }
/*  63:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.NominalItem
 * JD-Core Version:    0.7.0.1
 */