/*   1:    */ package org.boon.logging;
/*   2:    */ 
/*   3:    */ import java.io.PrintWriter;
/*   4:    */ import java.util.logging.Formatter;
/*   5:    */ import java.util.logging.LogRecord;
/*   6:    */ import org.boon.Exceptions;
/*   7:    */ import org.boon.primitive.CharBuf;
/*   8:    */ 
/*   9:    */ public class BoonLogFormatter
/*  10:    */   extends Formatter
/*  11:    */ {
/*  12: 50 */   private static String LINE_SEPARATOR = System.getProperty("line.separator");
/*  13:    */   
/*  14:    */   public String format(LogRecord record)
/*  15:    */   {
/*  16: 55 */     CharBuf sb = CharBuf.create(255);
/*  17: 56 */     sb.jsonDate(record.getMillis());
/*  18:    */     
/*  19: 58 */     sb.add("[").add(Thread.currentThread().getName()).append("]");
/*  20:    */     
/*  21:    */ 
/*  22:    */ 
/*  23:    */ 
/*  24:    */ 
/*  25:    */ 
/*  26:    */ 
/*  27:    */ 
/*  28:    */ 
/*  29:    */ 
/*  30:    */ 
/*  31:    */ 
/*  32:    */ 
/*  33:    */ 
/*  34:    */ 
/*  35: 74 */     sb.add(record.getLevel()).add(" [");
/*  36: 75 */     sb.add(record.getLoggerName()).add("]").add("  ");
/*  37: 76 */     sb.add(record.getMessage());
/*  38:    */     
/*  39: 78 */     sb.append(LINE_SEPARATOR);
/*  40: 82 */     if (record.getThrown() != null)
/*  41:    */     {
/*  42: 86 */       StackTraceElement[] filteredStackTrace = Exceptions.getFilteredStackTrace(record.getThrown().getStackTrace());
/*  43: 88 */       if (filteredStackTrace.length > 0) {
/*  44: 89 */         Exceptions.stackTraceToJson(sb, filteredStackTrace);
/*  45:    */       }
/*  46: 93 */       PrintWriter pw = new PrintWriter(sb);
/*  47: 94 */       record.getThrown().printStackTrace(pw);
/*  48: 95 */       pw.close();
/*  49:    */       
/*  50:    */ 
/*  51: 98 */       record.getThrown().printStackTrace(sb);
/*  52:    */     }
/*  53:100 */     return sb.toString();
/*  54:    */   }
/*  55:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.logging.BoonLogFormatter
 * JD-Core Version:    0.7.0.1
 */