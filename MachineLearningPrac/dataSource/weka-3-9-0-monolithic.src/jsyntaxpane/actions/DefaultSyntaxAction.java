/*   1:    */ package jsyntaxpane.actions;
/*   2:    */ 
/*   3:    */ import java.awt.event.ActionEvent;
/*   4:    */ import java.net.URL;
/*   5:    */ import java.util.regex.Pattern;
/*   6:    */ import javax.swing.ImageIcon;
/*   7:    */ import javax.swing.text.JTextComponent;
/*   8:    */ import javax.swing.text.TextAction;
/*   9:    */ import jsyntaxpane.SyntaxDocument;
/*  10:    */ import jsyntaxpane.util.Configuration;
/*  11:    */ import jsyntaxpane.util.Configuration.StringKeyMatcher;
/*  12:    */ import jsyntaxpane.util.ReflectUtils;
/*  13:    */ 
/*  14:    */ public abstract class DefaultSyntaxAction
/*  15:    */   extends TextAction
/*  16:    */   implements SyntaxAction
/*  17:    */ {
/*  18:    */   public static final String ACTION_PREFIX = "Action.";
/*  19:    */   public static final String SMALL_ICONS_LOC_PREFIX = "/META-INF/images/small-icons/";
/*  20:    */   public static final String LARGE_ICONS_LOC_PREFIX = "/META-INF/images/large-icons/";
/*  21:    */   
/*  22:    */   public DefaultSyntaxAction(String actionName)
/*  23:    */   {
/*  24: 35 */     super(actionName);
/*  25: 36 */     putValue("Name", actionName);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void config(Configuration config, String name)
/*  29:    */   {
/*  30: 42 */     String actionName = name.substring("Action.".length());
/*  31: 43 */     for (Configuration.StringKeyMatcher m : config.getKeys(Pattern.compile(Pattern.quote(name) + "\\.((\\w|-)+)"))) {
/*  32: 45 */       if (!ReflectUtils.callSetter(this, m.group1, m.value)) {
/*  33: 46 */         putValue(m.group1, m.value);
/*  34:    */       }
/*  35:    */     }
/*  36: 49 */     putValue("Name", actionName);
/*  37: 51 */     if (getValue("SmallIcon") == null) {
/*  38: 52 */       setSmallIcon(actionName + ".png");
/*  39:    */     }
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void actionPerformed(ActionEvent e)
/*  43:    */   {
/*  44: 58 */     JTextComponent text = getTextComponent(e);
/*  45: 59 */     SyntaxDocument sdoc = ActionUtils.getSyntaxDocument(text);
/*  46: 60 */     if (text != null) {
/*  47: 61 */       actionPerformed(text, sdoc, text.getCaretPosition(), e);
/*  48:    */     }
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/*  52:    */   {
/*  53: 75 */     throw new UnsupportedOperationException("Not yet implemented");
/*  54:    */   }
/*  55:    */   
/*  56:    */   public String toString()
/*  57:    */   {
/*  58: 80 */     return "Action " + getValue("Name") + "of type " + getClass().getSimpleName();
/*  59:    */   }
/*  60:    */   
/*  61:    */   public final void setMenuText(String text)
/*  62:    */   {
/*  63: 88 */     putValue("Name", text);
/*  64: 91 */     if (getValue("ShortDescription") == null) {
/*  65: 92 */       putValue("ShortDescription", text);
/*  66:    */     }
/*  67:    */   }
/*  68:    */   
/*  69:    */   public final void setToolTip(String text)
/*  70:    */   {
/*  71:101 */     putValue("ShortDescription", text);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public final void setLargeIcon(String url)
/*  75:    */   {
/*  76:110 */     URL loc = getClass().getResource("/META-INF/images/large-icons/" + url);
/*  77:111 */     if (loc != null)
/*  78:    */     {
/*  79:112 */       ImageIcon i = new ImageIcon(loc);
/*  80:113 */       putValue("SwingLargeIconKey", i);
/*  81:    */     }
/*  82:    */   }
/*  83:    */   
/*  84:    */   public final void setSmallIcon(String url)
/*  85:    */   {
/*  86:122 */     URL loc = getClass().getResource("/META-INF/images/small-icons/" + url);
/*  87:123 */     if (loc != null)
/*  88:    */     {
/*  89:124 */       ImageIcon i = new ImageIcon(loc);
/*  90:125 */       putValue("SmallIcon", i);
/*  91:    */     }
/*  92:    */   }
/*  93:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.DefaultSyntaxAction
 * JD-Core Version:    0.7.0.1
 */