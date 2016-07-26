/*   1:    */ package weka.core.logging;
/*   2:    */ 
/*   3:    */ import java.io.BufferedWriter;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileWriter;
/*   6:    */ import java.text.SimpleDateFormat;
/*   7:    */ import java.util.Date;
/*   8:    */ import java.util.Properties;
/*   9:    */ import java.util.regex.Matcher;
/*  10:    */ import weka.core.RevisionUtils;
/*  11:    */ import weka.core.WekaPackageManager;
/*  12:    */ 
/*  13:    */ public class FileLogger
/*  14:    */   extends ConsoleLogger
/*  15:    */ {
/*  16:    */   protected File m_LogFile;
/*  17:    */   protected String m_LineFeed;
/*  18:    */   
/*  19:    */   protected void initialize()
/*  20:    */   {
/*  21: 52 */     super.initialize();
/*  22:    */     
/*  23:    */ 
/*  24: 55 */     this.m_LogFile = getLogFile();
/*  25:    */     try
/*  26:    */     {
/*  27: 58 */       if ((this.m_LogFile != null) && (this.m_LogFile.exists())) {
/*  28: 59 */         this.m_LogFile.delete();
/*  29:    */       }
/*  30:    */     }
/*  31:    */     catch (Exception e)
/*  32:    */     {
/*  33: 62 */       e.printStackTrace();
/*  34:    */     }
/*  35: 66 */     this.m_LineFeed = System.getProperty("line.separator");
/*  36:    */   }
/*  37:    */   
/*  38:    */   protected File getLogFile()
/*  39:    */   {
/*  40: 78 */     String filename = m_Properties.getProperty("LogFile", "%w" + File.separator + "weka.log");
/*  41: 79 */     filename = filename.replaceAll("%t", Matcher.quoteReplacement(System.getProperty("java.io.tmpdir")));
/*  42: 80 */     filename = filename.replaceAll("%h", Matcher.quoteReplacement(System.getProperty("user.home")));
/*  43: 81 */     filename = filename.replaceAll("%c", Matcher.quoteReplacement(System.getProperty("user.dir")));
/*  44: 82 */     filename = filename.replaceAll("%w", Matcher.quoteReplacement(WekaPackageManager.WEKA_HOME.toString()));
/*  45: 83 */     if ((System.getProperty("%") != null) && (System.getProperty("%").length() > 0)) {
/*  46: 84 */       filename = filename.replaceAll("%%", Matcher.quoteReplacement(System.getProperty("%")));
/*  47:    */     }
/*  48: 87 */     File result = new File(filename);
/*  49:    */     
/*  50: 89 */     return result;
/*  51:    */   }
/*  52:    */   
/*  53:    */   protected void append(String s)
/*  54:    */   {
/*  55:100 */     if (this.m_LogFile == null) {
/*  56:101 */       return;
/*  57:    */     }
/*  58:    */     try
/*  59:    */     {
/*  60:105 */       BufferedWriter writer = new BufferedWriter(new FileWriter(this.m_LogFile, true));
/*  61:106 */       writer.write(s);
/*  62:107 */       writer.flush();
/*  63:108 */       writer.close();
/*  64:    */     }
/*  65:    */     catch (Exception e) {}
/*  66:    */   }
/*  67:    */   
/*  68:    */   protected void doLog(Logger.Level level, String msg, String cls, String method, int lineno)
/*  69:    */   {
/*  70:126 */     super.doLog(level, msg, cls, method, lineno);
/*  71:    */     
/*  72:    */ 
/*  73:129 */     append(m_DateFormat.format(new Date()) + " " + cls + " " + method + this.m_LineFeed + level + ": " + msg + this.m_LineFeed);
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String getRevision()
/*  77:    */   {
/*  78:140 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  79:    */   }
/*  80:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.logging.FileLogger
 * JD-Core Version:    0.7.0.1
 */