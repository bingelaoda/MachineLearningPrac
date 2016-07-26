/*   1:    */ package jsyntaxpane;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.Font;
/*   6:    */ import java.awt.FontMetrics;
/*   7:    */ import java.awt.Graphics;
/*   8:    */ import java.awt.Graphics2D;
/*   9:    */ import java.awt.RenderingHints;
/*  10:    */ import java.awt.RenderingHints.Key;
/*  11:    */ import java.awt.Shape;
/*  12:    */ import java.awt.Toolkit;
/*  13:    */ import java.util.Iterator;
/*  14:    */ import java.util.Map;
/*  15:    */ import java.util.logging.Level;
/*  16:    */ import java.util.logging.Logger;
/*  17:    */ import javax.swing.event.DocumentEvent;
/*  18:    */ import javax.swing.text.BadLocationException;
/*  19:    */ import javax.swing.text.Element;
/*  20:    */ import javax.swing.text.PlainView;
/*  21:    */ import javax.swing.text.Segment;
/*  22:    */ import javax.swing.text.ViewFactory;
/*  23:    */ import jsyntaxpane.util.Configuration;
/*  24:    */ 
/*  25:    */ public class SyntaxView
/*  26:    */   extends PlainView
/*  27:    */ {
/*  28:    */   public static final String PROPERTY_RIGHT_MARGIN_COLOR = "RightMarginColor";
/*  29:    */   public static final String PROPERTY_RIGHT_MARGIN_COLUMN = "RightMarginColumn";
/*  30:    */   public static final String PROPERTY_SINGLE_COLOR_SELECT = "SingleColorSelect";
/*  31: 39 */   private static final Logger log = Logger.getLogger(SyntaxView.class.getName());
/*  32: 40 */   private SyntaxStyle DEFAULT_STYLE = SyntaxStyles.getInstance().getStyle(TokenType.DEFAULT);
/*  33:    */   private final boolean singleColorSelect;
/*  34:    */   private final int rightMarginColumn;
/*  35:    */   private final Color rightMarginColor;
/*  36:    */   private final SyntaxStyles styles;
/*  37:    */   
/*  38:    */   public SyntaxView(Element element, Configuration config)
/*  39:    */   {
/*  40: 53 */     super(element);
/*  41: 54 */     this.singleColorSelect = config.getBoolean("SingleColorSelect", false);
/*  42: 55 */     this.rightMarginColor = new Color(config.getInteger("RightMarginColor", 16742263));
/*  43:    */     
/*  44: 57 */     this.rightMarginColumn = config.getInteger("RightMarginColumn", 0);
/*  45:    */     
/*  46: 59 */     this.styles = SyntaxStyles.read(config);
/*  47:    */   }
/*  48:    */   
/*  49:    */   protected int drawUnselectedText(Graphics graphics, int x, int y, int p0, int p1)
/*  50:    */   {
/*  51: 65 */     setRenderingHits((Graphics2D)graphics);
/*  52: 66 */     Font saveFont = graphics.getFont();
/*  53: 67 */     Color saveColor = graphics.getColor();
/*  54: 68 */     SyntaxDocument doc = (SyntaxDocument)getDocument();
/*  55: 69 */     Segment segment = getLineBuffer();
/*  56: 72 */     if (this.rightMarginColumn > 0)
/*  57:    */     {
/*  58: 73 */       int m_x = this.rightMarginColumn * graphics.getFontMetrics().charWidth('m');
/*  59: 74 */       int h = graphics.getFontMetrics().getHeight();
/*  60: 75 */       graphics.setColor(this.rightMarginColor);
/*  61: 76 */       graphics.drawLine(m_x, y, m_x, y - h);
/*  62:    */     }
/*  63:    */     try
/*  64:    */     {
/*  65: 80 */       Iterator<Token> i = doc.getTokens(p0, p1);
/*  66: 81 */       int start = p0;
/*  67: 82 */       while (i.hasNext())
/*  68:    */       {
/*  69: 83 */         Token t = (Token)i.next();
/*  70: 87 */         if (start < t.start)
/*  71:    */         {
/*  72: 88 */           doc.getText(start, t.start - start, segment);
/*  73: 89 */           x = this.DEFAULT_STYLE.drawText(segment, x, y, graphics, this, start);
/*  74:    */         }
/*  75: 93 */         int l = t.length;
/*  76: 94 */         int s = t.start;
/*  77: 96 */         if (s < p0)
/*  78:    */         {
/*  79: 98 */           l -= p0 - s;
/*  80: 99 */           s = p0;
/*  81:    */         }
/*  82:103 */         if (s + l > p1) {
/*  83:104 */           l = p1 - s;
/*  84:    */         }
/*  85:106 */         doc.getText(s, l, segment);
/*  86:107 */         x = this.styles.drawText(segment, x, y, graphics, this, t);
/*  87:108 */         start = t.end();
/*  88:    */       }
/*  89:111 */       if (start < p1)
/*  90:    */       {
/*  91:112 */         doc.getText(start, p1 - start, segment);
/*  92:113 */         x = this.DEFAULT_STYLE.drawText(segment, x, y, graphics, this, start);
/*  93:    */       }
/*  94:    */     }
/*  95:    */     catch (BadLocationException ex)
/*  96:    */     {
/*  97:116 */       log.log(Level.SEVERE, "Requested: " + ex.offsetRequested(), ex);
/*  98:    */     }
/*  99:    */     finally
/* 100:    */     {
/* 101:118 */       graphics.setFont(saveFont);
/* 102:119 */       graphics.setColor(saveColor);
/* 103:    */     }
/* 104:121 */     return x;
/* 105:    */   }
/* 106:    */   
/* 107:    */   protected int drawSelectedText(Graphics graphics, int x, int y, int p0, int p1)
/* 108:    */     throws BadLocationException
/* 109:    */   {
/* 110:127 */     if (this.singleColorSelect)
/* 111:    */     {
/* 112:128 */       if (this.rightMarginColumn > 0)
/* 113:    */       {
/* 114:129 */         int m_x = this.rightMarginColumn * graphics.getFontMetrics().charWidth('m');
/* 115:130 */         int h = graphics.getFontMetrics().getHeight();
/* 116:131 */         graphics.setColor(this.rightMarginColor);
/* 117:132 */         graphics.drawLine(m_x, y, m_x, y - h);
/* 118:    */       }
/* 119:134 */       return super.drawUnselectedText(graphics, x, y, p0, p1);
/* 120:    */     }
/* 121:136 */     return drawUnselectedText(graphics, x, y, p0, p1);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public static void setRenderingHits(Graphics2D g2d)
/* 125:    */   {
/* 126:146 */     g2d.addRenderingHints(sysHints);
/* 127:    */   }
/* 128:    */   
/* 129:    */   protected void updateDamage(DocumentEvent changes, Shape a, ViewFactory f)
/* 130:    */   {
/* 131:153 */     super.updateDamage(changes, a, f);
/* 132:154 */     Component host = getContainer();
/* 133:155 */     host.repaint();
/* 134:    */   }
/* 135:    */   
/* 136:163 */   private static RenderingHints sysHints = null;
/* 137:    */   
/* 138:    */   static
/* 139:    */   {
/* 140:    */     try
/* 141:    */     {
/* 142:165 */       Toolkit toolkit = Toolkit.getDefaultToolkit();
/* 143:    */       
/* 144:167 */       Map<RenderingHints.Key, ?> map = (Map)toolkit.getDesktopProperty("awt.font.desktophints");
/* 145:    */       
/* 146:169 */       sysHints = new RenderingHints(map);
/* 147:    */     }
/* 148:    */     catch (Throwable t) {}
/* 149:    */   }
/* 150:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.SyntaxView
 * JD-Core Version:    0.7.0.1
 */