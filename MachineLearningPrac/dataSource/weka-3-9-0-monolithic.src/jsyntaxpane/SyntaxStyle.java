/*   1:    */ package jsyntaxpane;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Font;
/*   5:    */ import java.awt.FontMetrics;
/*   6:    */ import java.awt.Graphics;
/*   7:    */ import javax.swing.text.Segment;
/*   8:    */ import javax.swing.text.TabExpander;
/*   9:    */ import javax.swing.text.Utilities;
/*  10:    */ 
/*  11:    */ public final class SyntaxStyle
/*  12:    */ {
/*  13:    */   private Color color;
/*  14:    */   private int fontStyle;
/*  15:    */   
/*  16:    */   public SyntaxStyle() {}
/*  17:    */   
/*  18:    */   public SyntaxStyle(Color color, boolean bold, boolean italic)
/*  19:    */   {
/*  20: 41 */     this.color = color;
/*  21: 42 */     setBold(Boolean.valueOf(bold));
/*  22: 43 */     setItalic(Boolean.valueOf(italic));
/*  23:    */   }
/*  24:    */   
/*  25:    */   public SyntaxStyle(Color color, int fontStyle)
/*  26:    */   {
/*  27: 48 */     this.color = color;
/*  28: 49 */     this.fontStyle = fontStyle;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public SyntaxStyle(String str)
/*  32:    */   {
/*  33: 53 */     String[] parts = str.split("\\s*,\\s*");
/*  34: 54 */     if (parts.length != 2) {
/*  35: 55 */       throw new IllegalArgumentException("style not correct format: " + str);
/*  36:    */     }
/*  37: 57 */     this.color = new Color(Integer.decode(parts[0]).intValue());
/*  38: 58 */     this.fontStyle = Integer.decode(parts[1]).intValue();
/*  39:    */   }
/*  40:    */   
/*  41:    */   public boolean isBold()
/*  42:    */   {
/*  43: 62 */     return (this.fontStyle & 0x1) != 0;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setBold(Boolean bold)
/*  47:    */   {
/*  48: 66 */     if (bold.booleanValue())
/*  49:    */     {
/*  50: 67 */       this.fontStyle |= 0x1;
/*  51:    */     }
/*  52:    */     else
/*  53:    */     {
/*  54: 69 */       int mask = -2;
/*  55: 70 */       this.fontStyle &= mask;
/*  56:    */     }
/*  57:    */   }
/*  58:    */   
/*  59:    */   public String getColorString()
/*  60:    */   {
/*  61: 75 */     return String.format("0x%06x", new Object[] { Integer.valueOf(this.color.getRGB() & 0xFFFFFF) });
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setColorString(String color)
/*  65:    */   {
/*  66: 79 */     this.color = Color.decode(color);
/*  67:    */   }
/*  68:    */   
/*  69:    */   public Boolean isItalic()
/*  70:    */   {
/*  71: 83 */     return Boolean.valueOf((this.fontStyle & 0x2) != 0);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setItalic(Boolean italic)
/*  75:    */   {
/*  76: 87 */     if (italic.booleanValue()) {
/*  77: 88 */       this.fontStyle |= 0x2;
/*  78:    */     } else {
/*  79: 90 */       this.fontStyle &= 0xFFFFFFFD;
/*  80:    */     }
/*  81:    */   }
/*  82:    */   
/*  83:    */   public int getFontStyle()
/*  84:    */   {
/*  85: 95 */     return this.fontStyle;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public Color getColor()
/*  89:    */   {
/*  90: 99 */     return this.color;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public int drawText(Segment segment, int x, int y, Graphics graphics, TabExpander e, int startOffset)
/*  94:    */   {
/*  95:116 */     graphics.setFont(graphics.getFont().deriveFont(getFontStyle()));
/*  96:117 */     FontMetrics fontMetrics = graphics.getFontMetrics();
/*  97:118 */     int a = fontMetrics.getAscent();
/*  98:119 */     int h = a + fontMetrics.getDescent();
/*  99:120 */     int w = Utilities.getTabbedTextWidth(segment, fontMetrics, 0, e, startOffset);
/* 100:121 */     int rX = x - 1;
/* 101:122 */     int rY = y - a;
/* 102:123 */     int rW = w + 2;
/* 103:124 */     int rH = h;
/* 104:125 */     if ((getFontStyle() & 0x10) != 0)
/* 105:    */     {
/* 106:126 */       graphics.setColor(Color.decode("#EEEEEE"));
/* 107:127 */       graphics.fillRect(rX, rY, rW, rH);
/* 108:    */     }
/* 109:129 */     graphics.setColor(getColor());
/* 110:130 */     x = Utilities.drawTabbedText(segment, x, y, graphics, e, startOffset);
/* 111:131 */     if ((getFontStyle() & 0x8) != 0)
/* 112:    */     {
/* 113:132 */       graphics.setColor(Color.RED);
/* 114:133 */       graphics.drawRect(rX, rY, rW, rH);
/* 115:    */     }
/* 116:135 */     return x;
/* 117:    */   }
/* 118:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.SyntaxStyle
 * JD-Core Version:    0.7.0.1
 */