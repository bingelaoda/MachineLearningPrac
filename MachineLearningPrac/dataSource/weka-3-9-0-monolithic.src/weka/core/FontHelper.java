/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.awt.Font;
/*   4:    */ 
/*   5:    */ public class FontHelper
/*   6:    */ {
/*   7:    */   protected String m_fontName;
/*   8:    */   protected int m_fontStyle;
/*   9:    */   protected int m_fontSize;
/*  10:    */   
/*  11:    */   public FontHelper(Font font)
/*  12:    */   {
/*  13: 50 */     this.m_fontName = font.getFontName();
/*  14: 51 */     this.m_fontSize = font.getSize();
/*  15: 52 */     this.m_fontStyle = font.getStyle();
/*  16:    */   }
/*  17:    */   
/*  18:    */   public FontHelper() {}
/*  19:    */   
/*  20:    */   public void setFontName(String fontName)
/*  21:    */   {
/*  22: 67 */     this.m_fontName = fontName;
/*  23:    */   }
/*  24:    */   
/*  25:    */   public String getFontName()
/*  26:    */   {
/*  27: 76 */     return this.m_fontName;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void setFontStyle(int style)
/*  31:    */   {
/*  32: 85 */     this.m_fontStyle = style;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public int getFontStyle()
/*  36:    */   {
/*  37: 94 */     return this.m_fontStyle;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void setFontSize(int size)
/*  41:    */   {
/*  42:103 */     this.m_fontSize = size;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public int getFontSize()
/*  46:    */   {
/*  47:112 */     return this.m_fontSize;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public Font getFont()
/*  51:    */   {
/*  52:121 */     if (this.m_fontName != null) {
/*  53:122 */       return new Font(this.m_fontName, this.m_fontStyle, this.m_fontSize);
/*  54:    */     }
/*  55:124 */     return new Font("Monospaced", 0, 12);
/*  56:    */   }
/*  57:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.FontHelper
 * JD-Core Version:    0.7.0.1
 */