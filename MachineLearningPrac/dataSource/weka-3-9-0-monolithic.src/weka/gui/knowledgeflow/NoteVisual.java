/*   1:    */ package weka.gui.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Graphics;
/*   6:    */ import javax.swing.BorderFactory;
/*   7:    */ import javax.swing.JLabel;
/*   8:    */ import javax.swing.JPanel;
/*   9:    */ import weka.knowledgeflow.StepManagerImpl;
/*  10:    */ import weka.knowledgeflow.steps.Note;
/*  11:    */ 
/*  12:    */ public class NoteVisual
/*  13:    */   extends StepVisual
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = -3291021235652124916L;
/*  16: 45 */   protected JLabel m_label = new JLabel();
/*  17: 48 */   protected int m_fontSizeAdjust = -1;
/*  18:    */   
/*  19:    */   public void setStepManager(StepManagerImpl manager)
/*  20:    */   {
/*  21: 57 */     super.setStepManager(manager);
/*  22:    */     
/*  23: 59 */     removeAll();
/*  24: 60 */     setLayout(new BorderLayout());
/*  25: 61 */     setBorder(new ShadowBorder(2, Color.GRAY));
/*  26: 62 */     this.m_label.setText(convertToHTML(((Note)getStepManager().getManagedStep()).getNoteText()));
/*  27:    */     
/*  28: 64 */     this.m_label.setOpaque(true);
/*  29: 65 */     this.m_label.setBackground(Color.YELLOW);
/*  30: 66 */     JPanel holder = new JPanel();
/*  31: 67 */     holder.setLayout(new BorderLayout());
/*  32: 68 */     holder.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
/*  33: 69 */     holder.setOpaque(true);
/*  34: 70 */     holder.setBackground(Color.YELLOW);
/*  35: 71 */     holder.add(this.m_label, "Center");
/*  36: 72 */     add(holder, "Center");
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setHighlighted(boolean highlighted)
/*  40:    */   {
/*  41: 81 */     if (highlighted) {
/*  42: 82 */       setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLUE));
/*  43:    */     } else {
/*  44: 85 */       setBorder(new ShadowBorder(2, Color.GRAY));
/*  45:    */     }
/*  46: 87 */     revalidate();
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setDisplayConnectors(boolean dc)
/*  50:    */   {
/*  51: 98 */     this.m_displayConnectors = dc;
/*  52: 99 */     this.m_connectorColor = Color.blue;
/*  53:    */     
/*  54:101 */     setHighlighted(dc);
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setDisplayConnectors(boolean dc, Color c)
/*  58:    */   {
/*  59:112 */     setDisplayConnectors(dc);
/*  60:113 */     this.m_connectorColor = c;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public boolean getDisplayStepLabel()
/*  64:    */   {
/*  65:118 */     return false;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void paintComponent(Graphics gx)
/*  69:    */   {
/*  70:123 */     this.m_label.setText(convertToHTML(((Note)getStepManager().getManagedStep()).getNoteText()));
/*  71:    */   }
/*  72:    */   
/*  73:    */   private String convertToHTML(String text)
/*  74:    */   {
/*  75:134 */     String htmlString = text.replace("\n", "<br>");
/*  76:135 */     htmlString = "<html><font size=" + this.m_fontSizeAdjust + ">" + htmlString + "</font>" + "</html>";
/*  77:    */     
/*  78:    */ 
/*  79:    */ 
/*  80:139 */     return htmlString;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public int getFontSizeAdjust()
/*  84:    */   {
/*  85:148 */     return this.m_fontSizeAdjust;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void setFontSizeAdjust(int adjust)
/*  89:    */   {
/*  90:157 */     this.m_fontSizeAdjust = adjust;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public void decreaseFontSize()
/*  94:    */   {
/*  95:164 */     this.m_fontSizeAdjust -= 1;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void increaseFontSize()
/*  99:    */   {
/* 100:171 */     this.m_fontSizeAdjust += 1;
/* 101:    */   }
/* 102:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.NoteVisual
 * JD-Core Version:    0.7.0.1
 */