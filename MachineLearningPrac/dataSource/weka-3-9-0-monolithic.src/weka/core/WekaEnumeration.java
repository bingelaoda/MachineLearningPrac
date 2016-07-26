/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.List;
/*   5:    */ 
/*   6:    */ public class WekaEnumeration<E>
/*   7:    */   implements Enumeration<E>, RevisionHandler
/*   8:    */ {
/*   9:    */   private int m_Counter;
/*  10:    */   private final List<E> m_Vector;
/*  11:    */   private final int m_SpecialElement;
/*  12:    */   
/*  13:    */   public WekaEnumeration(List<E> vector)
/*  14:    */   {
/*  15: 55 */     this.m_Counter = 0;
/*  16: 56 */     this.m_Vector = vector;
/*  17: 57 */     this.m_SpecialElement = -1;
/*  18:    */   }
/*  19:    */   
/*  20:    */   public WekaEnumeration(List<E> vector, int special)
/*  21:    */   {
/*  22: 70 */     this.m_Vector = vector;
/*  23: 71 */     this.m_SpecialElement = special;
/*  24: 72 */     if (special == 0) {
/*  25: 73 */       this.m_Counter = 1;
/*  26:    */     } else {
/*  27: 75 */       this.m_Counter = 0;
/*  28:    */     }
/*  29:    */   }
/*  30:    */   
/*  31:    */   public final boolean hasMoreElements()
/*  32:    */   {
/*  33: 87 */     if (this.m_Counter < this.m_Vector.size()) {
/*  34: 88 */       return true;
/*  35:    */     }
/*  36: 90 */     return false;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public final E nextElement()
/*  40:    */   {
/*  41:102 */     E result = this.m_Vector.get(this.m_Counter);
/*  42:    */     
/*  43:104 */     this.m_Counter += 1;
/*  44:105 */     if (this.m_Counter == this.m_SpecialElement) {
/*  45:106 */       this.m_Counter += 1;
/*  46:    */     }
/*  47:108 */     return result;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public String getRevision()
/*  51:    */   {
/*  52:118 */     return RevisionUtils.extract("$Revision: 10203 $");
/*  53:    */   }
/*  54:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.WekaEnumeration
 * JD-Core Version:    0.7.0.1
 */