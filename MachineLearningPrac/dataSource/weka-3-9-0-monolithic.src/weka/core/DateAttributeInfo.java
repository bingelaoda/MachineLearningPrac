/*  1:   */ package weka.core;
/*  2:   */ 
/*  3:   */ import java.text.SimpleDateFormat;
/*  4:   */ 
/*  5:   */ public class DateAttributeInfo
/*  6:   */   implements AttributeInfo
/*  7:   */ {
/*  8:   */   protected SimpleDateFormat m_DateFormat;
/*  9:   */   
/* 10:   */   public DateAttributeInfo(String dateFormat)
/* 11:   */   {
/* 12:37 */     if (dateFormat != null) {
/* 13:38 */       this.m_DateFormat = new SimpleDateFormat(dateFormat);
/* 14:   */     } else {
/* 15:40 */       this.m_DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
/* 16:   */     }
/* 17:42 */     this.m_DateFormat.setLenient(false);
/* 18:   */   }
/* 19:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.DateAttributeInfo
 * JD-Core Version:    0.7.0.1
 */