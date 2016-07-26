/*  1:   */ package weka.core.pmml;
/*  2:   */ 
/*  3:   */ public class PMMLUtils
/*  4:   */ {
/*  5:   */   public static String pad(String source, String padChar, int length, boolean leftPad)
/*  6:   */   {
/*  7:43 */     StringBuffer temp = new StringBuffer();
/*  8:45 */     if (leftPad)
/*  9:   */     {
/* 10:46 */       for (int i = 0; i < length; i++) {
/* 11:47 */         temp.append(padChar);
/* 12:   */       }
/* 13:49 */       temp.append(source);
/* 14:   */     }
/* 15:   */     else
/* 16:   */     {
/* 17:51 */       temp.append(source);
/* 18:52 */       for (int i = 0; i < length; i++) {
/* 19:53 */         temp.append(padChar);
/* 20:   */       }
/* 21:   */     }
/* 22:56 */     return temp.toString();
/* 23:   */   }
/* 24:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.PMMLUtils
 * JD-Core Version:    0.7.0.1
 */