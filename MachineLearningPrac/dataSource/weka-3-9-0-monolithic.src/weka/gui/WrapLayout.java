/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Component;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.FlowLayout;
/*   7:    */ import java.awt.Insets;
/*   8:    */ import javax.swing.JScrollPane;
/*   9:    */ import javax.swing.SwingUtilities;
/*  10:    */ 
/*  11:    */ public class WrapLayout
/*  12:    */   extends FlowLayout
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 7055023419450383821L;
/*  15:    */   private Dimension preferredLayoutSize;
/*  16:    */   
/*  17:    */   public WrapLayout() {}
/*  18:    */   
/*  19:    */   public WrapLayout(int align)
/*  20:    */   {
/*  21: 52 */     super(align);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public WrapLayout(int align, int hgap, int vgap)
/*  25:    */   {
/*  26: 67 */     super(align, hgap, vgap);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public Dimension preferredLayoutSize(Container target)
/*  30:    */   {
/*  31: 80 */     return layoutSize(target, true);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public Dimension minimumLayoutSize(Container target)
/*  35:    */   {
/*  36: 93 */     Dimension minimum = layoutSize(target, false);
/*  37: 94 */     minimum.width -= getHgap() + 1;
/*  38: 95 */     return minimum;
/*  39:    */   }
/*  40:    */   
/*  41:    */   private Dimension layoutSize(Container target, boolean preferred)
/*  42:    */   {
/*  43:107 */     synchronized (target.getTreeLock())
/*  44:    */     {
/*  45:112 */       int targetWidth = target.getSize().width;
/*  46:113 */       Container container = target;
/*  47:115 */       while ((container.getSize().width == 0) && (container.getParent() != null)) {
/*  48:116 */         container = container.getParent();
/*  49:    */       }
/*  50:119 */       targetWidth = container.getSize().width;
/*  51:121 */       if (targetWidth == 0) {
/*  52:122 */         targetWidth = 2147483647;
/*  53:    */       }
/*  54:124 */       int hgap = getHgap();
/*  55:125 */       int vgap = getVgap();
/*  56:126 */       Insets insets = target.getInsets();
/*  57:127 */       int horizontalInsetsAndGap = insets.left + insets.right + hgap * 2;
/*  58:128 */       int maxWidth = targetWidth - horizontalInsetsAndGap;
/*  59:    */       
/*  60:    */ 
/*  61:    */ 
/*  62:132 */       Dimension dim = new Dimension(0, 0);
/*  63:133 */       int rowWidth = 0;
/*  64:134 */       int rowHeight = 0;
/*  65:    */       
/*  66:136 */       int nmembers = target.getComponentCount();
/*  67:138 */       for (int i = 0; i < nmembers; i++)
/*  68:    */       {
/*  69:139 */         Component m = target.getComponent(i);
/*  70:141 */         if (m.isVisible())
/*  71:    */         {
/*  72:142 */           Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
/*  73:146 */           if (rowWidth + d.width > maxWidth)
/*  74:    */           {
/*  75:147 */             addRow(dim, rowWidth, rowHeight);
/*  76:148 */             rowWidth = 0;
/*  77:149 */             rowHeight = 0;
/*  78:    */           }
/*  79:154 */           if (rowWidth != 0) {
/*  80:155 */             rowWidth += hgap;
/*  81:    */           }
/*  82:158 */           rowWidth += d.width;
/*  83:159 */           rowHeight = Math.max(rowHeight, d.height);
/*  84:    */         }
/*  85:    */       }
/*  86:163 */       addRow(dim, rowWidth, rowHeight);
/*  87:    */       
/*  88:165 */       dim.width += horizontalInsetsAndGap;
/*  89:166 */       dim.height += insets.top + insets.bottom + vgap * 2;
/*  90:    */       
/*  91:    */ 
/*  92:    */ 
/*  93:    */ 
/*  94:    */ 
/*  95:    */ 
/*  96:173 */       Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
/*  97:176 */       if ((scrollPane != null) && (target.isValid())) {
/*  98:177 */         dim.width -= hgap + 1;
/*  99:    */       }
/* 100:180 */       return dim;
/* 101:    */     }
/* 102:    */   }
/* 103:    */   
/* 104:    */   private void addRow(Dimension dim, int rowWidth, int rowHeight)
/* 105:    */   {
/* 106:195 */     dim.width = Math.max(dim.width, rowWidth);
/* 107:197 */     if (dim.height > 0) {
/* 108:198 */       dim.height += getVgap();
/* 109:    */     }
/* 110:201 */     dim.height += rowHeight;
/* 111:    */   }
/* 112:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.WrapLayout
 * JD-Core Version:    0.7.0.1
 */