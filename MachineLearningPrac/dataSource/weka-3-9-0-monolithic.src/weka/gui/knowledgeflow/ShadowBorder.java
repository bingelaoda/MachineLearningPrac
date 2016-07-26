/*   1:    */ package weka.gui.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.Container;
/*   6:    */ import java.awt.Graphics;
/*   7:    */ import java.awt.Insets;
/*   8:    */ import javax.swing.border.AbstractBorder;
/*   9:    */ 
/*  10:    */ public class ShadowBorder
/*  11:    */   extends AbstractBorder
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = -2117842133475125463L;
/*  14: 41 */   private int m_width = 3;
/*  15: 44 */   private Color m_color = Color.BLACK;
/*  16:    */   
/*  17:    */   public ShadowBorder()
/*  18:    */   {
/*  19: 50 */     this(2);
/*  20:    */   }
/*  21:    */   
/*  22:    */   public ShadowBorder(int width)
/*  23:    */   {
/*  24: 59 */     this(width, Color.BLACK);
/*  25:    */   }
/*  26:    */   
/*  27:    */   public ShadowBorder(int width, Color color)
/*  28:    */   {
/*  29: 69 */     this.m_width = width;
/*  30: 70 */     this.m_color = color;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public Insets getBorderInsets(Component c)
/*  34:    */   {
/*  35: 82 */     return new Insets(1, 1, this.m_width + 1, this.m_width + 1);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Insets getBorderInsets(Component c, Insets insets)
/*  39:    */   {
/*  40: 95 */     insets.top = 1;
/*  41: 96 */     insets.left = 1;
/*  42: 97 */     insets.bottom = (this.m_width + 1);
/*  43: 98 */     insets.right = (this.m_width + 1);
/*  44: 99 */     return insets;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public boolean isBorderOpaque()
/*  48:    */   {
/*  49:109 */     return true;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
/*  53:    */   {
/*  54:125 */     Color old_color = g.getColor();
/*  55:    */     
/*  56:127 */     g.setColor(this.m_color);
/*  57:    */     
/*  58:    */ 
/*  59:130 */     g.drawRect(x, y, width - this.m_width - 1, height - this.m_width - 1);
/*  60:133 */     for (int i = 0; i <= this.m_width; i++)
/*  61:    */     {
/*  62:135 */       int x1 = x + this.m_width;
/*  63:136 */       int y1 = y + height - i;
/*  64:137 */       int x2 = x + width;
/*  65:138 */       int y2 = y1;
/*  66:139 */       g.drawLine(x1, y1, x2, y2);
/*  67:    */       
/*  68:    */ 
/*  69:142 */       x1 = x + width - this.m_width + i;
/*  70:143 */       y1 = y + this.m_width;
/*  71:144 */       x2 = x1;
/*  72:145 */       y2 = y + height;
/*  73:146 */       g.drawLine(x1, y1, x2, y2);
/*  74:    */     }
/*  75:151 */     if (c.getParent() != null)
/*  76:    */     {
/*  77:152 */       g.setColor(c.getParent().getBackground());
/*  78:153 */       for (int i = 0; i <= this.m_width; i++)
/*  79:    */       {
/*  80:154 */         int x1 = x;
/*  81:155 */         int y1 = y + height - i;
/*  82:156 */         int x2 = x + this.m_width;
/*  83:157 */         int y2 = y1;
/*  84:158 */         g.drawLine(x1, y1, x2, y2);
/*  85:159 */         x1 = x + width - this.m_width;
/*  86:160 */         y1 = y + i;
/*  87:161 */         x2 = x + width;
/*  88:162 */         y2 = y1;
/*  89:163 */         g.drawLine(x1, y1, x2, y2);
/*  90:    */       }
/*  91:166 */       g.setColor(g.getColor().darker());
/*  92:167 */       for (int i = 0; i < this.m_width; i++)
/*  93:    */       {
/*  94:169 */         int x1 = x + i + 1;
/*  95:170 */         int y1 = y + height - this.m_width + i;
/*  96:171 */         int x2 = x + this.m_width;
/*  97:172 */         int y2 = y1;
/*  98:173 */         g.drawLine(x1, y1, x2, y2);
/*  99:    */         
/* 100:    */ 
/* 101:176 */         x1 = x + width - this.m_width;
/* 102:177 */         y1 = y + i + 1;
/* 103:178 */         x2 = x1 + i;
/* 104:179 */         y2 = y1;
/* 105:180 */         g.drawLine(x1, y1, x2, y2);
/* 106:    */       }
/* 107:    */     }
/* 108:184 */     g.setColor(old_color);
/* 109:    */   }
/* 110:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.ShadowBorder
 * JD-Core Version:    0.7.0.1
 */