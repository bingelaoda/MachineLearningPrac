/*   1:    */ package weka.core.stopwords;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.core.Option;
/*  10:    */ import weka.core.OptionHandler;
/*  11:    */ import weka.core.Utils;
/*  12:    */ 
/*  13:    */ public abstract class AbstractStopwords
/*  14:    */   implements OptionHandler, StopwordsHandler, Serializable
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = -1975256329586388142L;
/*  17:    */   protected boolean m_Initialized;
/*  18:    */   protected boolean m_Debug;
/*  19:    */   
/*  20:    */   public abstract String globalInfo();
/*  21:    */   
/*  22:    */   protected void reset()
/*  23:    */   {
/*  24: 62 */     this.m_Initialized = false;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public Enumeration<Option> listOptions()
/*  28:    */   {
/*  29: 72 */     Vector<Option> result = new Vector();
/*  30:    */     
/*  31: 74 */     result.addElement(new Option("\tIf set, stopword scheme is run in debug mode and\n\tmay output additional info to the console", "D", 0, "-D"));
/*  32:    */     
/*  33:    */ 
/*  34:    */ 
/*  35:    */ 
/*  36: 79 */     return result.elements();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setOptions(String[] options)
/*  40:    */     throws Exception
/*  41:    */   {
/*  42: 90 */     setDebug(Utils.getFlag("D", options));
/*  43:    */   }
/*  44:    */   
/*  45:    */   public String[] getOptions()
/*  46:    */   {
/*  47:100 */     List<String> options = new ArrayList();
/*  48:102 */     if (getDebug()) {
/*  49:103 */       options.add("-D");
/*  50:    */     }
/*  51:106 */     return (String[])options.toArray(new String[options.size()]);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void setDebug(boolean debug)
/*  55:    */   {
/*  56:115 */     this.m_Debug = debug;
/*  57:116 */     reset();
/*  58:    */   }
/*  59:    */   
/*  60:    */   public boolean getDebug()
/*  61:    */   {
/*  62:125 */     return this.m_Debug;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public String debugTipText()
/*  66:    */   {
/*  67:135 */     return "If set to true, stopwords scheme may output additional info to the console.";
/*  68:    */   }
/*  69:    */   
/*  70:    */   protected void error(String msg)
/*  71:    */   {
/*  72:145 */     System.err.println(getClass().getName() + "-ERROR: " + msg);
/*  73:    */   }
/*  74:    */   
/*  75:    */   protected void debug(String msg)
/*  76:    */   {
/*  77:154 */     System.err.println(getClass().getName() + "-DEBUG: " + msg);
/*  78:    */   }
/*  79:    */   
/*  80:    */   protected void initialize() {}
/*  81:    */   
/*  82:    */   protected abstract boolean is(String paramString);
/*  83:    */   
/*  84:    */   public boolean isStopword(String word)
/*  85:    */   {
/*  86:183 */     if (!this.m_Initialized)
/*  87:    */     {
/*  88:184 */       if (this.m_Debug) {
/*  89:185 */         debug("Initializing stopwords");
/*  90:    */       }
/*  91:186 */       initialize();
/*  92:187 */       this.m_Initialized = true;
/*  93:    */     }
/*  94:190 */     boolean result = is(word);
/*  95:191 */     if (this.m_Debug) {
/*  96:192 */       debug(word + " --> " + result);
/*  97:    */     }
/*  98:194 */     return result;
/*  99:    */   }
/* 100:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.stopwords.AbstractStopwords
 * JD-Core Version:    0.7.0.1
 */