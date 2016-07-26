/*  1:   */ package weka.associations;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ import weka.core.Attribute;
/*  5:   */ 
/*  6:   */ public class BinaryItem
/*  7:   */   extends NominalItem
/*  8:   */   implements Serializable
/*  9:   */ {
/* 10:   */   private static final long serialVersionUID = -3372941834914147669L;
/* 11:   */   
/* 12:   */   public BinaryItem(Attribute att, int valueIndex)
/* 13:   */     throws Exception
/* 14:   */   {
/* 15:50 */     super(att, valueIndex);
/* 16:52 */     if ((att.isNumeric()) || ((att.isNominal()) && (att.numValues() > 2))) {
/* 17:53 */       throw new Exception("BinaryItem must be constructed using a nominal attribute with at most 2 values!");
/* 18:   */     }
/* 19:   */   }
/* 20:   */   
/* 21:   */   public boolean equals(Object compareTo)
/* 22:   */   {
/* 23:63 */     if (!(compareTo instanceof BinaryItem)) {
/* 24:64 */       return false;
/* 25:   */     }
/* 26:67 */     BinaryItem b = (BinaryItem)compareTo;
/* 27:68 */     if ((this.m_attribute.equals(b.getAttribute())) && (this.m_valueIndex == b.getValueIndex())) {
/* 28:71 */       return true;
/* 29:   */     }
/* 30:74 */     return false;
/* 31:   */   }
/* 32:   */   
/* 33:   */   public int hashCode()
/* 34:   */   {
/* 35:78 */     return (this.m_attribute.name().hashCode() ^ this.m_attribute.numValues()) * this.m_frequency;
/* 36:   */   }
/* 37:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.BinaryItem
 * JD-Core Version:    0.7.0.1
 */