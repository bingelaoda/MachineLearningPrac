/*   1:    */ package weka.core.logging;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.text.SimpleDateFormat;
/*   5:    */ import java.util.Date;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ import weka.core.Tee;
/*   8:    */ 
/*   9:    */ public class OutputLogger
/*  10:    */   extends FileLogger
/*  11:    */ {
/*  12:    */   protected OutputPrintStream m_StreamOut;
/*  13:    */   protected OutputPrintStream m_StreamErr;
/*  14:    */   protected Tee m_StdOut;
/*  15:    */   protected Tee m_StdErr;
/*  16:    */   
/*  17:    */   public static class OutputPrintStream
/*  18:    */     extends PrintStream
/*  19:    */   {
/*  20:    */     protected OutputLogger m_Owner;
/*  21:    */     protected String m_LineFeed;
/*  22:    */     
/*  23:    */     public OutputPrintStream(OutputLogger owner, PrintStream stream)
/*  24:    */       throws Exception
/*  25:    */     {
/*  26: 61 */       super();
/*  27:    */       
/*  28: 63 */       this.m_Owner = owner;
/*  29: 64 */       this.m_LineFeed = System.getProperty("line.separator");
/*  30:    */     }
/*  31:    */     
/*  32:    */     public void flush() {}
/*  33:    */     
/*  34:    */     public void print(int x)
/*  35:    */     {
/*  36: 79 */       this.m_Owner.append("" + x);
/*  37:    */     }
/*  38:    */     
/*  39:    */     public void print(boolean x)
/*  40:    */     {
/*  41: 88 */       this.m_Owner.append("" + x);
/*  42:    */     }
/*  43:    */     
/*  44:    */     public void print(String x)
/*  45:    */     {
/*  46: 97 */       this.m_Owner.append("" + x);
/*  47:    */     }
/*  48:    */     
/*  49:    */     public void print(Object x)
/*  50:    */     {
/*  51:106 */       this.m_Owner.append("" + x);
/*  52:    */     }
/*  53:    */     
/*  54:    */     public void println()
/*  55:    */     {
/*  56:113 */       this.m_Owner.append(this.m_LineFeed);
/*  57:    */     }
/*  58:    */     
/*  59:    */     public void println(int x)
/*  60:    */     {
/*  61:122 */       this.m_Owner.append(x + this.m_LineFeed);
/*  62:    */     }
/*  63:    */     
/*  64:    */     public void println(boolean x)
/*  65:    */     {
/*  66:131 */       this.m_Owner.append(x + this.m_LineFeed);
/*  67:    */     }
/*  68:    */     
/*  69:    */     public void println(String x)
/*  70:    */     {
/*  71:140 */       this.m_Owner.append(x + this.m_LineFeed);
/*  72:    */     }
/*  73:    */     
/*  74:    */     public void println(Object x)
/*  75:    */     {
/*  76:150 */       this.m_Owner.append(x + this.m_LineFeed);
/*  77:    */     }
/*  78:    */   }
/*  79:    */   
/*  80:    */   protected void initialize()
/*  81:    */   {
/*  82:170 */     super.initialize();
/*  83:    */     try
/*  84:    */     {
/*  85:173 */       this.m_StdOut = new Tee(System.out);
/*  86:174 */       System.setOut(this.m_StdOut);
/*  87:175 */       this.m_StreamOut = new OutputPrintStream(this, this.m_StdOut.getDefault());
/*  88:176 */       this.m_StdOut.add(this.m_StreamOut);
/*  89:    */       
/*  90:178 */       this.m_StdErr = new Tee(System.err);
/*  91:179 */       System.setErr(this.m_StdErr);
/*  92:180 */       this.m_StreamErr = new OutputPrintStream(this, this.m_StdErr.getDefault());
/*  93:181 */       this.m_StdErr.add(this.m_StreamErr);
/*  94:    */     }
/*  95:    */     catch (Exception e) {}
/*  96:    */   }
/*  97:    */   
/*  98:    */   protected void doLog(Logger.Level level, String msg, String cls, String method, int lineno)
/*  99:    */   {
/* 100:199 */     append(m_DateFormat.format(new Date()) + " " + cls + " " + method + this.m_LineFeed + level + ": " + msg + this.m_LineFeed);
/* 101:    */   }
/* 102:    */   
/* 103:    */   public String getRevision()
/* 104:    */   {
/* 105:210 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 106:    */   }
/* 107:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.logging.OutputLogger
 * JD-Core Version:    0.7.0.1
 */