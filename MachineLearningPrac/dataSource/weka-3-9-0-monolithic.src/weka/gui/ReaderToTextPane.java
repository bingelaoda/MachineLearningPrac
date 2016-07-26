/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.io.LineNumberReader;
/*   5:    */ import java.io.Reader;
/*   6:    */ import javax.swing.JTextPane;
/*   7:    */ import javax.swing.text.Style;
/*   8:    */ import javax.swing.text.StyleConstants;
/*   9:    */ import javax.swing.text.StyleContext;
/*  10:    */ import javax.swing.text.StyledDocument;
/*  11:    */ 
/*  12:    */ public class ReaderToTextPane
/*  13:    */   extends Thread
/*  14:    */ {
/*  15:    */   protected LineNumberReader m_Input;
/*  16:    */   protected JTextPane m_Output;
/*  17:    */   protected Color m_Color;
/*  18:    */   
/*  19:    */   public ReaderToTextPane(Reader input, JTextPane output)
/*  20:    */   {
/*  21: 59 */     this(input, output, Color.BLACK);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public ReaderToTextPane(Reader input, JTextPane output, Color color)
/*  25:    */   {
/*  26: 73 */     setDaemon(true);
/*  27:    */     
/*  28: 75 */     this.m_Color = color;
/*  29: 76 */     this.m_Input = new LineNumberReader(input);
/*  30: 77 */     this.m_Output = output;
/*  31:    */     
/*  32: 79 */     StyledDocument doc = this.m_Output.getStyledDocument();
/*  33: 80 */     Style style = StyleContext.getDefaultStyleContext().getStyle("default");
/*  34:    */     
/*  35: 82 */     style = doc.addStyle(getStyleName(), style);
/*  36: 83 */     StyleConstants.setFontFamily(style, "monospaced");
/*  37: 84 */     StyleConstants.setForeground(style, this.m_Color);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public Color getColor()
/*  41:    */   {
/*  42: 93 */     return this.m_Color;
/*  43:    */   }
/*  44:    */   
/*  45:    */   protected String getStyleName()
/*  46:    */   {
/*  47:102 */     return "" + this.m_Color.hashCode();
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void run()
/*  51:    */   {
/*  52:    */     try
/*  53:    */     {
/*  54:    */       for (;;)
/*  55:    */       {
/*  56:112 */         StyledDocument doc = this.m_Output.getStyledDocument();
/*  57:113 */         doc.insertString(doc.getLength(), this.m_Input.readLine() + '\n', doc.getStyle(getStyleName()));
/*  58:    */         
/*  59:    */ 
/*  60:    */ 
/*  61:117 */         this.m_Output.setCaretPosition(doc.getLength());
/*  62:    */       }
/*  63:    */     }
/*  64:    */     catch (Exception ex)
/*  65:    */     {
/*  66:    */       try
/*  67:    */       {
/*  68:121 */         sleep(100L);
/*  69:    */       }
/*  70:    */       catch (Exception e) {}
/*  71:    */     }
/*  72:    */   }
/*  73:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.ReaderToTextPane
 * JD-Core Version:    0.7.0.1
 */