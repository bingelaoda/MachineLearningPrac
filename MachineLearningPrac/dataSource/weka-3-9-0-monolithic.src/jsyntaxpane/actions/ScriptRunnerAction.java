/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import javax.script.ScriptEngine;
/*  5:   */ import javax.script.ScriptEngineManager;
/*  6:   */ import javax.script.ScriptException;
/*  7:   */ import javax.swing.JOptionPane;
/*  8:   */ import javax.swing.SwingUtilities;
/*  9:   */ import javax.swing.text.JTextComponent;
/* 10:   */ import jsyntaxpane.SyntaxDocument;
/* 11:   */ 
/* 12:   */ public class ScriptRunnerAction
/* 13:   */   extends DefaultSyntaxAction
/* 14:   */ {
/* 15:   */   static ScriptEngineManager sem;
/* 16:   */   private ScriptEngine engine;
/* 17:   */   private String scriptExtension;
/* 18:   */   
/* 19:   */   public ScriptRunnerAction()
/* 20:   */   {
/* 21:36 */     super("SCRIPT_EXECUTE");
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/* 25:   */   {
/* 26:   */     try
/* 27:   */     {
/* 28:43 */       ScriptEngine eng = getEngine(target);
/* 29:44 */       if (eng != null) {
/* 30:45 */         getEngine(target).eval(target.getText());
/* 31:   */       }
/* 32:   */     }
/* 33:   */     catch (ScriptException ex)
/* 34:   */     {
/* 35:48 */       JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(target), "Error executing script:\n" + ex.getMessage(), "Script Error", 0);
/* 36:   */       
/* 37:   */ 
/* 38:   */ 
/* 39:52 */       ActionUtils.setCaretPosition(target, ex.getLineNumber(), ex.getColumnNumber());
/* 40:   */     }
/* 41:   */   }
/* 42:   */   
/* 43:   */   private ScriptEngine getEngine(JTextComponent target)
/* 44:   */   {
/* 45:59 */     if (this.engine == null)
/* 46:   */     {
/* 47:60 */       if (sem == null) {
/* 48:61 */         sem = new ScriptEngineManager();
/* 49:   */       }
/* 50:63 */       this.engine = sem.getEngineByExtension(this.scriptExtension);
/* 51:   */     }
/* 52:65 */     if (this.engine == null)
/* 53:   */     {
/* 54:66 */       int result = JOptionPane.showOptionDialog(target, "Script Engine for [" + this.scriptExtension + "] not found. Disable this Action?", "jsyntaxpane", 0, 0, null, null, null);
/* 55:74 */       if (result == 0) {
/* 56:75 */         setEnabled(false);
/* 57:   */       }
/* 58:   */     }
/* 59:78 */     return this.engine;
/* 60:   */   }
/* 61:   */   
/* 62:   */   public void setScriptExtension(String value)
/* 63:   */   {
/* 64:82 */     this.scriptExtension = value;
/* 65:   */   }
/* 66:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.ScriptRunnerAction
 * JD-Core Version:    0.7.0.1
 */