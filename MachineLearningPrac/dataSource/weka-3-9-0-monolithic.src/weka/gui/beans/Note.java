/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import javax.swing.BorderFactory;
/*   6:    */ import javax.swing.JLabel;
/*   7:    */ import javax.swing.JPanel;
/*   8:    */ 
/*   9:    */ public class Note
/*  10:    */   extends JPanel
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -7272355421198069040L;
/*  13: 46 */   protected String m_noteText = "New note";
/*  14: 49 */   protected JLabel m_label = new JLabel();
/*  15: 52 */   protected int m_fontSizeAdjust = -1;
/*  16:    */   
/*  17:    */   public Note()
/*  18:    */   {
/*  19: 58 */     setLayout(new BorderLayout());
/*  20:    */     
/*  21: 60 */     setBorder(new ShadowBorder(2, Color.GRAY));
/*  22:    */     
/*  23: 62 */     this.m_label.setText(convertToHTML(this.m_noteText));
/*  24: 63 */     this.m_label.setOpaque(true);
/*  25: 64 */     this.m_label.setBackground(Color.YELLOW);
/*  26: 65 */     JPanel holder = new JPanel();
/*  27: 66 */     holder.setLayout(new BorderLayout());
/*  28: 67 */     holder.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
/*  29: 68 */     holder.setOpaque(true);
/*  30: 69 */     holder.setBackground(Color.YELLOW);
/*  31: 70 */     holder.add(this.m_label, "Center");
/*  32: 71 */     add(holder, "Center");
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void setHighlighted(boolean highlighted)
/*  36:    */   {
/*  37: 75 */     if (highlighted) {
/*  38: 76 */       setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLUE));
/*  39:    */     } else {
/*  40: 79 */       setBorder(new ShadowBorder(2, Color.GRAY));
/*  41:    */     }
/*  42: 81 */     revalidate();
/*  43:    */   }
/*  44:    */   
/*  45:    */   private String convertToHTML(String text)
/*  46:    */   {
/*  47: 85 */     String htmlString = this.m_noteText.replace("\n", "<br>");
/*  48: 86 */     htmlString = "<html><font size=" + this.m_fontSizeAdjust + ">" + htmlString + "</font>" + "</html>";
/*  49:    */     
/*  50:    */ 
/*  51:    */ 
/*  52:    */ 
/*  53:    */ 
/*  54: 92 */     return htmlString;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setNoteText(String noteText)
/*  58:    */   {
/*  59:101 */     this.m_noteText = noteText;
/*  60:    */     
/*  61:103 */     this.m_label.setText(convertToHTML(this.m_noteText));
/*  62:    */   }
/*  63:    */   
/*  64:    */   public String getNoteText()
/*  65:    */   {
/*  66:112 */     return this.m_noteText;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void setFontSizeAdjust(int adjust)
/*  70:    */   {
/*  71:121 */     this.m_fontSizeAdjust = adjust;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public int getFontSizeAdjust()
/*  75:    */   {
/*  76:130 */     return this.m_fontSizeAdjust;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void decreaseFontSize()
/*  80:    */   {
/*  81:137 */     this.m_fontSizeAdjust -= 1;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void increaseFontSize()
/*  85:    */   {
/*  86:144 */     this.m_fontSizeAdjust += 1;
/*  87:    */   }
/*  88:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.Note
 * JD-Core Version:    0.7.0.1
 */