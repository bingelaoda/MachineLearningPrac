/*   1:    */ package weka.gui.scripting;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Font;
/*   6:    */ import java.awt.GridLayout;
/*   7:    */ import java.util.Properties;
/*   8:    */ import javax.swing.BorderFactory;
/*   9:    */ import javax.swing.ImageIcon;
/*  10:    */ import javax.swing.JLabel;
/*  11:    */ import javax.swing.JPanel;
/*  12:    */ import javax.swing.JTextPane;
/*  13:    */ import javax.swing.text.Document;
/*  14:    */ import weka.core.Utils;
/*  15:    */ import weka.gui.BrowserHelper;
/*  16:    */ import weka.gui.ComponentHelper;
/*  17:    */ import weka.gui.visualize.VisualizeUtils;
/*  18:    */ 
/*  19:    */ public class GroovyPanel
/*  20:    */   extends FileScriptingPanel
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = -3475707604414854111L;
/*  23:    */   public static final String PROPERTIES_FILE = "weka/gui/scripting/Groovy.props";
/*  24:    */   
/*  25:    */   protected JTextPane newCodePane()
/*  26:    */   {
/*  27:    */     Properties props;
/*  28:    */     try
/*  29:    */     {
/*  30: 67 */       props = Utils.readProperties("weka/gui/scripting/Groovy.props");
/*  31:    */     }
/*  32:    */     catch (Exception e)
/*  33:    */     {
/*  34: 70 */       e.printStackTrace();
/*  35: 71 */       props = new Properties();
/*  36:    */     }
/*  37: 74 */     JTextPane result = new JTextPane();
/*  38: 75 */     if (props.getProperty("Syntax", "false").equals("true"))
/*  39:    */     {
/*  40: 76 */       SyntaxDocument doc = new SyntaxDocument(props);
/*  41: 77 */       result.setDocument(doc);
/*  42: 78 */       result.setBackground(doc.getBackgroundColor());
/*  43:    */     }
/*  44:    */     else
/*  45:    */     {
/*  46: 81 */       result.setForeground(VisualizeUtils.processColour(props.getProperty("ForegroundColor", "black"), Color.BLACK));
/*  47: 82 */       result.setBackground(VisualizeUtils.processColour(props.getProperty("BackgroundColor", "white"), Color.WHITE));
/*  48: 83 */       result.setFont(new Font(props.getProperty("FontName", "monospaced"), 0, Integer.parseInt(props.getProperty("FontSize", "12"))));
/*  49:    */     }
/*  50: 86 */     return result;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public ImageIcon getIcon()
/*  54:    */   {
/*  55: 95 */     return ComponentHelper.getImageIcon("weka/gui/scripting/images/groovy_small.png");
/*  56:    */   }
/*  57:    */   
/*  58:    */   protected JPanel getAboutPanel()
/*  59:    */   {
/*  60:107 */     JPanel result = new JPanel(new BorderLayout());
/*  61:108 */     result.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
/*  62:    */     
/*  63:    */ 
/*  64:111 */     result.add(new JLabel(ComponentHelper.getImageIcon("weka/gui/scripting/images/groovy_medium.png")), "Center");
/*  65:    */     
/*  66:    */ 
/*  67:114 */     JPanel panel = new JPanel(new GridLayout(5, 1));
/*  68:115 */     panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
/*  69:116 */     result.add(panel, "South");
/*  70:    */     
/*  71:118 */     panel.add(new JLabel("Groovy homepage"));
/*  72:119 */     panel.add(BrowserHelper.createLink("http://groovy.codehaus.org/", null));
/*  73:120 */     panel.add(new JLabel(" "));
/*  74:121 */     panel.add(new JLabel("Weka and Groovy"));
/*  75:122 */     panel.add(BrowserHelper.createLink("http://weka.wikispaces.com/Using+Weka+from+Groovy", null));
/*  76:    */     
/*  77:124 */     return result;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public String getPlainTitle()
/*  81:    */   {
/*  82:133 */     return "Groovy Console";
/*  83:    */   }
/*  84:    */   
/*  85:    */   protected Script newScript(Document doc)
/*  86:    */   {
/*  87:143 */     return new GroovyScript(doc);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public static void main(String[] args)
/*  91:    */   {
/*  92:152 */     showPanel(new GroovyPanel(), args);
/*  93:    */   }
/*  94:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.scripting.GroovyPanel
 * JD-Core Version:    0.7.0.1
 */