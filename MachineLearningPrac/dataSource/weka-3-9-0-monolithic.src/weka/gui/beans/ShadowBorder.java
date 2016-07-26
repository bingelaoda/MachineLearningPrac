/*   1:    */ package weka.gui.beans;
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
/*  14: 39 */   private int m_width = 3;
/*  15: 42 */   private Color m_color = Color.BLACK;
/*  16:    */   
/*  17:    */   public ShadowBorder()
/*  18:    */   {
/*  19: 48 */     this(2);
/*  20:    */   }
/*  21:    */   
/*  22:    */   public ShadowBorder(int width)
/*  23:    */   {
/*  24: 56 */     this(width, Color.BLACK);
/*  25:    */   }
/*  26:    */   
/*  27:    */   public ShadowBorder(int width, Color color)
/*  28:    */   {
/*  29: 66 */     this.m_width = width;
/*  30: 67 */     this.m_color = color;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public Insets getBorderInsets(Component c)
/*  34:    */   {
/*  35: 78 */     return new Insets(1, 1, this.m_width + 1, this.m_width + 1);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Insets getBorderInsets(Component c, Insets insets)
/*  39:    */   {
/*  40: 90 */     insets.top = 1;
/*  41: 91 */     insets.left = 1;
/*  42: 92 */     insets.bottom = (this.m_width + 1);
/*  43: 93 */     insets.right = (this.m_width + 1);
/*  44: 94 */     return insets;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public boolean isBorderOpaque()
/*  48:    */   {
/*  49:103 */     return true;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
/*  53:    */   {
/*  54:117 */     Color old_color = g.getColor();
/*  55:    */     
/*  56:119 */     g.setColor(this.m_color);
/*  57:    */     
/*  58:    */ 
/*  59:122 */     g.drawRect(x, y, width - this.m_width - 1, height - this.m_width - 1);
/*  60:125 */     for (int i = 0; i <= this.m_width; i++)
/*  61:    */     {
/*  62:127 */       int x1 = x + this.m_width;
/*  63:128 */       int y1 = y + height - i;
/*  64:129 */       int x2 = x + width;
/*  65:130 */       int y2 = y1;
/*  66:131 */       g.drawLine(x1, y1, x2, y2);
/*  67:    */       
/*  68:    */ 
/*  69:134 */       x1 = x + width - this.m_width + i;
/*  70:135 */       y1 = y + this.m_width;
/*  71:136 */       x2 = x1;
/*  72:137 */       y2 = y + height;
/*  73:138 */       g.drawLine(x1, y1, x2, y2);
/*  74:    */     }
/*  75:143 */     if (c.getParent() != null)
/*  76:    */     {
/*  77:144 */       g.setColor(c.getParent().getBackground());
/*  78:145 */       for (int i = 0; i <= this.m_width; i++)
/*  79:    */       {
/*  80:146 */         int x1 = x;
/*  81:147 */         int y1 = y + height - i;
/*  82:148 */         int x2 = x + this.m_width;
/*  83:149 */         int y2 = y1;
/*  84:150 */         g.drawLine(x1, y1, x2, y2);
/*  85:151 */         x1 = x + width - this.m_width;
/*  86:152 */         y1 = y + i;
/*  87:153 */         x2 = x + width;
/*  88:154 */         y2 = y1;
/*  89:155 */         g.drawLine(x1, y1, x2, y2);
/*  90:    */       }
/*  91:158 */       g.setColor(g.getColor().darker());
/*  92:159 */       for (int i = 0; i < this.m_width; i++)
/*  93:    */       {
/*  94:161 */         int x1 = x + i + 1;
/*  95:162 */         int y1 = y + height - this.m_width + i;
/*  96:163 */         int x2 = x + this.m_width;
/*  97:164 */         int y2 = y1;
/*  98:165 */         g.drawLine(x1, y1, x2, y2);
/*  99:    */         
/* 100:    */ 
/* 101:168 */         x1 = x + width - this.m_width;
/* 102:169 */         y1 = y + i + 1;
/* 103:170 */         x2 = x1 + i;
/* 104:171 */         y2 = y1;
/* 105:172 */         g.drawLine(x1, y1, x2, y2);
/* 106:    */       }
/* 107:    */     }
/* 108:176 */     g.setColor(old_color);
/* 109:    */   }
/* 110:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ShadowBorder
 * JD-Core Version:    0.7.0.1
 */