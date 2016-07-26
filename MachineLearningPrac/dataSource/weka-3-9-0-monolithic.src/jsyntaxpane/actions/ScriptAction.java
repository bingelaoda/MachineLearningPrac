/*   1:    */ package jsyntaxpane.actions;
/*   2:    */ 
/*   3:    */ import java.awt.event.ActionEvent;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.InputStreamReader;
/*   6:    */ import java.io.Reader;
/*   7:    */ import java.util.regex.Pattern;
/*   8:    */ import javax.script.Invocable;
/*   9:    */ import javax.script.ScriptEngine;
/*  10:    */ import javax.script.ScriptEngineManager;
/*  11:    */ import javax.script.ScriptException;
/*  12:    */ import javax.swing.JOptionPane;
/*  13:    */ import javax.swing.text.JTextComponent;
/*  14:    */ import jsyntaxpane.SyntaxDocument;
/*  15:    */ import jsyntaxpane.util.Configuration;
/*  16:    */ import jsyntaxpane.util.Configuration.StringKeyMatcher;
/*  17:    */ import jsyntaxpane.util.JarServiceProvider;
/*  18:    */ 
/*  19:    */ public class ScriptAction
/*  20:    */   extends DefaultSyntaxAction
/*  21:    */ {
/*  22:    */   static final String SCRIPT_FUNCTION = "SCRIPT_FUNCTION";
/*  23:    */   
/*  24:    */   public ScriptAction()
/*  25:    */   {
/*  26: 40 */     super("scripted-action");
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/*  30:    */   {
/*  31: 46 */     if (getValue("SCRIPT_FUNCTION") != null)
/*  32:    */     {
/*  33: 47 */       String f = getValue("SCRIPT_FUNCTION").toString();
/*  34:    */       try
/*  35:    */       {
/*  36: 49 */         engine.put("TARGET", target);
/*  37: 50 */         engine.put("SDOC", sDoc);
/*  38: 51 */         engine.put("DOT", Integer.valueOf(dot));
/*  39: 52 */         engine.put("EVENT", e);
/*  40: 53 */         engine.put("ACTION", this);
/*  41: 54 */         engine.put("AU", ActionUtils.getInstance());
/*  42: 55 */         invocable.invokeFunction(f, new Object[0]);
/*  43:    */       }
/*  44:    */       catch (ScriptException ex)
/*  45:    */       {
/*  46: 57 */         showScriptError(target, ex);
/*  47:    */       }
/*  48:    */       catch (NoSuchMethodException ex)
/*  49:    */       {
/*  50: 59 */         showScriptError(target, ex);
/*  51:    */       }
/*  52:    */     }
/*  53:    */     else
/*  54:    */     {
/*  55: 62 */       JOptionPane.showMessageDialog(target, "Action does not have script function configured", "Error in Script", 2);
/*  56:    */     }
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setFunction(String name)
/*  60:    */   {
/*  61: 68 */     putValue("SCRIPT_FUNCTION", name);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void config(Configuration config, String name)
/*  65:    */   {
/*  66: 73 */     super.config(config, name);
/*  67: 75 */     for (Configuration.StringKeyMatcher m : config.getKeys(Pattern.compile("Script\\.((\\w|-)+)\\.URL"))) {
/*  68: 76 */       getScriptFromURL(m.value);
/*  69:    */     }
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void getScriptFromURL(String url)
/*  73:    */   {
/*  74: 85 */     InputStream is = JarServiceProvider.findResource(url, getClass().getClassLoader());
/*  75: 86 */     if (is != null)
/*  76:    */     {
/*  77: 87 */       Reader reader = new InputStreamReader(is);
/*  78:    */       try
/*  79:    */       {
/*  80: 89 */         engine.eval(reader);
/*  81:    */       }
/*  82:    */       catch (ScriptException ex)
/*  83:    */       {
/*  84: 91 */         showScriptError(null, ex);
/*  85:    */       }
/*  86:    */     }
/*  87:    */     else
/*  88:    */     {
/*  89: 94 */       JOptionPane.showMessageDialog(null, "No script is found in: " + url, "Error in Script", 2);
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   private void showScriptError(JTextComponent target, Exception ex)
/*  94:    */   {
/*  95:100 */     JOptionPane.showMessageDialog(target, ex.getMessage(), "Error in Script", 2);
/*  96:    */   }
/*  97:    */   
/*  98:112 */   static final ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
/*  99:113 */   static final Invocable invocable = (Invocable)engine;
/* 100:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.ScriptAction
 * JD-Core Version:    0.7.0.1
 */