/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ 
/*   6:    */ public abstract class Item
/*   7:    */   implements Serializable, Comparable<Item>
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = -430198211081183575L;
/*  10:    */   protected int m_frequency;
/*  11:    */   protected Attribute m_attribute;
/*  12:    */   
/*  13:    */   public Item(Attribute att)
/*  14:    */   {
/*  15: 46 */     this.m_attribute = att;
/*  16:    */   }
/*  17:    */   
/*  18:    */   public void increaseFrequency(int f)
/*  19:    */   {
/*  20: 55 */     this.m_frequency += f;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public void decreaseFrequency(int f)
/*  24:    */   {
/*  25: 64 */     this.m_frequency -= f;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void increaseFrequency()
/*  29:    */   {
/*  30: 71 */     this.m_frequency += 1;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public void decreaseFrequency()
/*  34:    */   {
/*  35: 78 */     this.m_frequency -= 1;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public int getFrequency()
/*  39:    */   {
/*  40: 87 */     return this.m_frequency;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public Attribute getAttribute()
/*  44:    */   {
/*  45: 96 */     return this.m_attribute;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public abstract String getItemValueAsString();
/*  49:    */   
/*  50:    */   public abstract String getComparisonAsString();
/*  51:    */   
/*  52:    */   public String toString()
/*  53:    */   {
/*  54:120 */     return toString(false);
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String toString(boolean freq)
/*  58:    */   {
/*  59:133 */     String result = this.m_attribute.name();
/*  60:134 */     if (freq) {
/*  61:135 */       result = result + ":" + this.m_frequency;
/*  62:    */     }
/*  63:137 */     return result;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public int compareTo(Item comp)
/*  67:    */   {
/*  68:147 */     if (this.m_frequency == comp.getFrequency()) {
/*  69:149 */       return -1 * this.m_attribute.name().compareTo(comp.getAttribute().name());
/*  70:    */     }
/*  71:151 */     if (comp.getFrequency() < this.m_frequency) {
/*  72:152 */       return -1;
/*  73:    */     }
/*  74:154 */     return 1;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public boolean equals(Object compareTo)
/*  78:    */   {
/*  79:162 */     if (!(compareTo instanceof Item)) {
/*  80:163 */       return false;
/*  81:    */     }
/*  82:166 */     Item b = (Item)compareTo;
/*  83:167 */     if (this.m_attribute.equals(b.getAttribute())) {
/*  84:168 */       return true;
/*  85:    */     }
/*  86:171 */     return false;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public int hashCode()
/*  90:    */   {
/*  91:175 */     return (this.m_attribute.name().hashCode() ^ this.m_attribute.numValues()) * this.m_frequency;
/*  92:    */   }
/*  93:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.Item
 * JD-Core Version:    0.7.0.1
 */