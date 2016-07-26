/*  1:   */ package org.apache.commons.codec;
/*  2:   */ 
/*  3:   */ import java.util.Comparator;
/*  4:   */ 
/*  5:   */ public class StringEncoderComparator
/*  6:   */   implements Comparator
/*  7:   */ {
/*  8:   */   private StringEncoder stringEncoder;
/*  9:   */   
/* 10:   */   public StringEncoderComparator() {}
/* 11:   */   
/* 12:   */   public StringEncoderComparator(StringEncoder stringEncoder)
/* 13:   */   {
/* 14:52 */     this.stringEncoder = stringEncoder;
/* 15:   */   }
/* 16:   */   
/* 17:   */   public int compare(Object o1, Object o2)
/* 18:   */   {
/* 19:70 */     int compareCode = 0;
/* 20:   */     try
/* 21:   */     {
/* 22:73 */       Comparable s1 = (Comparable)this.stringEncoder.encode(o1);
/* 23:74 */       Comparable s2 = (Comparable)this.stringEncoder.encode(o2);
/* 24:75 */       compareCode = s1.compareTo(s2);
/* 25:   */     }
/* 26:   */     catch (EncoderException ee)
/* 27:   */     {
/* 28:78 */       compareCode = 0;
/* 29:   */     }
/* 30:80 */     return compareCode;
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.codec.StringEncoderComparator
 * JD-Core Version:    0.7.0.1
 */