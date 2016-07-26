/*  1:   */ package weka.gui;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import java.text.SimpleDateFormat;
/*  5:   */ import java.util.Date;
/*  6:   */ 
/*  7:   */ public class SysErrLog
/*  8:   */   implements Logger
/*  9:   */ {
/* 10:   */   protected static String getTimestamp()
/* 11:   */   {
/* 12:43 */     return new SimpleDateFormat("yyyy.MM.dd hh:mm:ss").format(new Date());
/* 13:   */   }
/* 14:   */   
/* 15:   */   public void logMessage(String message)
/* 16:   */   {
/* 17:54 */     System.err.println("LOG " + getTimestamp() + ": " + message);
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void statusMessage(String message)
/* 21:   */   {
/* 22:65 */     System.err.println("STATUS: " + message);
/* 23:   */   }
/* 24:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.SysErrLog
 * JD-Core Version:    0.7.0.1
 */