/*   1:    */ package weka.gui.scripting;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import javax.swing.text.Document;
/*   5:    */ import weka.core.Utils;
/*   6:    */ import weka.core.scripting.Jython;
/*   7:    */ import weka.gui.ExtensionFileFilter;
/*   8:    */ 
/*   9:    */ public class JythonScript
/*  10:    */   extends Script
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = 3469648507172973169L;
/*  13:    */   public JythonScript() {}
/*  14:    */   
/*  15:    */   public static class JythonThread
/*  16:    */     extends Script.ScriptThread
/*  17:    */   {
/*  18:    */     public JythonThread(Script owner, String[] args)
/*  19:    */     {
/*  20: 58 */       super(args);
/*  21:    */     }
/*  22:    */     
/*  23:    */     protected void doRun()
/*  24:    */     {
/*  25: 73 */       Class<?>[] classes = { String.class };
/*  26: 74 */       Object[] params = { this.m_Owner.getFilename().getPath() };
/*  27: 75 */       String argv = "sys.argv = ['" + Utils.backQuoteChars(this.m_Owner.getFilename().getPath()) + "'";
/*  28: 77 */       for (int i = 0; i < getArgs().length; i++)
/*  29:    */       {
/*  30: 78 */         String arg = Utils.backQuoteChars(getArgs()[i]);
/*  31: 79 */         argv = argv + ", '" + arg + "'";
/*  32:    */       }
/*  33: 81 */       argv = argv + "]";
/*  34:    */       
/*  35: 83 */       Jython jython = new Jython();
/*  36:    */       
/*  37:    */ 
/*  38: 86 */       jython.invoke("exec", new Class[] { String.class }, new Object[] { "import sys" });
/*  39:    */       
/*  40: 88 */       jython.invoke("exec", new Class[] { String.class }, new Object[] { argv });
/*  41:    */       
/*  42:    */ 
/*  43: 91 */       jython.invoke("execfile", classes, params);
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   public JythonScript(Document doc)
/*  48:    */   {
/*  49:108 */     super(doc);
/*  50:    */   }
/*  51:    */   
/*  52:    */   public JythonScript(Document doc, File file)
/*  53:    */   {
/*  54:119 */     super(doc, file);
/*  55:    */   }
/*  56:    */   
/*  57:    */   public ExtensionFileFilter[] getFilters()
/*  58:    */   {
/*  59:131 */     ExtensionFileFilter[] result = new ExtensionFileFilter[1];
/*  60:132 */     result[0] = new ExtensionFileFilter(getDefaultExtension(), "Jython script (*" + getDefaultExtension() + ")");
/*  61:    */     
/*  62:    */ 
/*  63:135 */     return result;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String getDefaultExtension()
/*  67:    */   {
/*  68:146 */     return ".py";
/*  69:    */   }
/*  70:    */   
/*  71:    */   protected boolean canExecuteScripts()
/*  72:    */   {
/*  73:156 */     return Jython.isPresent();
/*  74:    */   }
/*  75:    */   
/*  76:    */   protected void preCheck(String[] args)
/*  77:    */     throws Exception
/*  78:    */   {
/*  79:170 */     super.preCheck(args);
/*  80:172 */     if (!Jython.isPresent()) {
/*  81:173 */       throw new Exception("Jython classes are not present in CLASSPATH!");
/*  82:    */     }
/*  83:    */   }
/*  84:    */   
/*  85:    */   public Script.ScriptThread newThread(String[] args)
/*  86:    */   {
/*  87:185 */     return new JythonThread(this, args);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public static void main(String[] args)
/*  91:    */     throws Exception
/*  92:    */   {
/*  93:195 */     runScript(new JythonScript(), args);
/*  94:    */   }
/*  95:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.scripting.JythonScript
 * JD-Core Version:    0.7.0.1
 */