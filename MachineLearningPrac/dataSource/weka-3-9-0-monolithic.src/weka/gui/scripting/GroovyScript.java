/*   1:    */ package weka.gui.scripting;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import javax.swing.text.Document;
/*   5:    */ import weka.core.scripting.Groovy;
/*   6:    */ import weka.gui.ExtensionFileFilter;
/*   7:    */ 
/*   8:    */ public class GroovyScript
/*   9:    */   extends Script
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = -3708517162415549420L;
/*  12:    */   public GroovyScript() {}
/*  13:    */   
/*  14:    */   public static class GroovyThread
/*  15:    */     extends Script.ScriptThread
/*  16:    */   {
/*  17:    */     public GroovyThread(Script owner, String[] args)
/*  18:    */     {
/*  19: 58 */       super(args);
/*  20:    */     }
/*  21:    */     
/*  22:    */     protected boolean hasMethod(Object groovy, String name)
/*  23:    */     {
/*  24:    */       boolean result;
/*  25:    */       try
/*  26:    */       {
/*  27: 71 */         groovy.getClass().getMethod(name, new Class[] { [Ljava.lang.String.class });
/*  28: 72 */         result = true;
/*  29:    */       }
/*  30:    */       catch (Exception e)
/*  31:    */       {
/*  32: 75 */         result = false;
/*  33:    */       }
/*  34: 78 */       return result;
/*  35:    */     }
/*  36:    */     
/*  37:    */     protected void doRun()
/*  38:    */     {
/*  39: 87 */       Object groovy = Groovy.newInstance(this.m_Owner.getFilename(), Object.class);
/*  40: 88 */       if (hasMethod(groovy, "run")) {
/*  41: 89 */         Groovy.invoke(groovy, "run", new Class[] { [Ljava.lang.String.class }, new Object[] { getArgs() });
/*  42: 90 */       } else if (hasMethod(groovy, "main")) {
/*  43: 91 */         Groovy.invoke(groovy, "main", new Class[] { [Ljava.lang.String.class }, new Object[] { getArgs() });
/*  44:    */       } else {
/*  45: 93 */         throw new IllegalStateException("Neither 'run' nor 'main' method found!");
/*  46:    */       }
/*  47:    */     }
/*  48:    */   }
/*  49:    */   
/*  50:    */   public GroovyScript(Document doc)
/*  51:    */   {
/*  52:110 */     super(doc);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public GroovyScript(Document doc, File file)
/*  56:    */   {
/*  57:121 */     super(doc, file);
/*  58:    */   }
/*  59:    */   
/*  60:    */   public ExtensionFileFilter[] getFilters()
/*  61:    */   {
/*  62:132 */     ExtensionFileFilter[] result = new ExtensionFileFilter[1];
/*  63:133 */     result[0] = new ExtensionFileFilter(getDefaultExtension(), "Groovy script (*" + getDefaultExtension() + ")");
/*  64:    */     
/*  65:135 */     return result;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public String getDefaultExtension()
/*  69:    */   {
/*  70:145 */     return ".groovy";
/*  71:    */   }
/*  72:    */   
/*  73:    */   protected boolean canExecuteScripts()
/*  74:    */   {
/*  75:154 */     return Groovy.isPresent();
/*  76:    */   }
/*  77:    */   
/*  78:    */   protected void preCheck(String[] args)
/*  79:    */     throws Exception
/*  80:    */   {
/*  81:166 */     super.preCheck(args);
/*  82:168 */     if (!Groovy.isPresent()) {
/*  83:169 */       throw new Exception("Groovy classes are not present in CLASSPATH!");
/*  84:    */     }
/*  85:    */   }
/*  86:    */   
/*  87:    */   public Script.ScriptThread newThread(String[] args)
/*  88:    */   {
/*  89:179 */     return new GroovyThread(this, args);
/*  90:    */   }
/*  91:    */   
/*  92:    */   public static void main(String[] args)
/*  93:    */     throws Exception
/*  94:    */   {
/*  95:189 */     runScript(new GroovyScript(), args);
/*  96:    */   }
/*  97:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.scripting.GroovyScript
 * JD-Core Version:    0.7.0.1
 */