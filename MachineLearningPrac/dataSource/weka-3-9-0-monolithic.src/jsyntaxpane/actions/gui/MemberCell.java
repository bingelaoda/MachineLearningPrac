/*   1:    */ package jsyntaxpane.actions.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.Font;
/*   6:    */ import java.awt.FontMetrics;
/*   7:    */ import java.awt.Graphics;
/*   8:    */ import java.awt.Graphics2D;
/*   9:    */ import java.awt.Image;
/*  10:    */ import java.lang.reflect.Member;
/*  11:    */ import java.net.URL;
/*  12:    */ import java.util.HashMap;
/*  13:    */ import java.util.Map;
/*  14:    */ import javax.swing.ImageIcon;
/*  15:    */ import javax.swing.JList;
/*  16:    */ import javax.swing.JPanel;
/*  17:    */ import jsyntaxpane.SyntaxView;
/*  18:    */ import jsyntaxpane.actions.ActionUtils;
/*  19:    */ 
/*  20:    */ abstract class MemberCell
/*  21:    */   extends JPanel
/*  22:    */ {
/*  23:    */   private final JList list;
/*  24:    */   private final boolean isSelected;
/*  25:    */   private final Color backColor;
/*  26:    */   private final Member member;
/*  27:    */   private final Class theClass;
/*  28:    */   
/*  29:    */   public MemberCell(JList list, boolean isSelected, Color backColor, Member member, Class clazz)
/*  30:    */   {
/*  31: 52 */     this.list = list;
/*  32: 53 */     this.isSelected = isSelected;
/*  33: 54 */     this.backColor = backColor;
/*  34: 55 */     this.member = member;
/*  35: 56 */     this.theClass = clazz;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void paintComponent(Graphics g)
/*  39:    */   {
/*  40: 61 */     SyntaxView.setRenderingHits((Graphics2D)g);
/*  41: 62 */     g.setFont(this.list.getFont());
/*  42: 63 */     super.paintComponent(g);
/*  43: 64 */     FontMetrics fm = g.getFontMetrics();
/*  44: 65 */     g.setColor(this.isSelected ? this.list.getSelectionBackground() : this.backColor);
/*  45: 66 */     g.fillRect(0, 0, getWidth(), getHeight());
/*  46: 67 */     g.setColor(this.isSelected ? this.list.getSelectionForeground() : this.list.getForeground());
/*  47: 68 */     g.drawImage(getIcon(), 2, 0, null);
/*  48: 69 */     int x = 6 + getIcon().getWidth(this);
/*  49: 70 */     int y = fm.getHeight();
/*  50: 71 */     if (this.member.getDeclaringClass().equals(this.theClass))
/*  51:    */     {
/*  52: 72 */       Font bold = this.list.getFont().deriveFont(1);
/*  53: 73 */       g.setFont(bold);
/*  54:    */     }
/*  55: 75 */     x = drawString(getMemberName(), x, y, g);
/*  56: 76 */     g.setFont(this.list.getFont());
/*  57: 77 */     x = drawString(getArguments(), x, y, g);
/*  58: 78 */     String right = getReturnType();
/*  59: 79 */     int rw = fm.stringWidth(right);
/*  60: 80 */     g.drawString(right, getWidth() - rw - 4, fm.getAscent());
/*  61:    */   }
/*  62:    */   
/*  63:    */   public Dimension getPreferredSize()
/*  64:    */   {
/*  65: 85 */     Font font = this.list.getFont();
/*  66: 86 */     Graphics g = getGraphics();
/*  67: 87 */     FontMetrics fm = g.getFontMetrics(font);
/*  68:    */     
/*  69: 89 */     String total = getMemberName() + getArguments() + getReturnType() + "  ";
/*  70: 90 */     return new Dimension(fm.stringWidth(total) + 20, Math.max(fm.getHeight(), 16));
/*  71:    */   }
/*  72:    */   
/*  73:    */   private int drawString(String string, int x, int y, Graphics g)
/*  74:    */   {
/*  75: 94 */     if (ActionUtils.isEmptyOrBlanks(string)) {
/*  76: 95 */       return x;
/*  77:    */     }
/*  78: 97 */     int w = g.getFontMetrics().stringWidth(string);
/*  79: 98 */     g.drawString(string, x, y);
/*  80: 99 */     return x + w;
/*  81:    */   }
/*  82:    */   
/*  83:    */   Map<Integer, Image> readIcons(String loc)
/*  84:    */   {
/*  85:111 */     Map<Integer, Image> icons = new HashMap();
/*  86:112 */     icons.put(Integer.valueOf(1), readImage(loc, ""));
/*  87:113 */     icons.put(Integer.valueOf(2), readImage(loc, "_private"));
/*  88:114 */     icons.put(Integer.valueOf(4), readImage(loc, "_protected"));
/*  89:115 */     icons.put(Integer.valueOf(9), readImage(loc, "_static"));
/*  90:116 */     icons.put(Integer.valueOf(10), readImage(loc, "_static_private"));
/*  91:117 */     icons.put(Integer.valueOf(12), readImage(loc, "_static_protected"));
/*  92:118 */     return icons;
/*  93:    */   }
/*  94:    */   
/*  95:    */   private Image readImage(String iconLoc, String kind)
/*  96:    */   {
/*  97:122 */     String fullPath = iconLoc + kind + ".png";
/*  98:123 */     URL loc = getClass().getResource(fullPath);
/*  99:124 */     if (loc == null) {
/* 100:125 */       return null;
/* 101:    */     }
/* 102:127 */     Image i = new ImageIcon(loc).getImage();
/* 103:128 */     return i;
/* 104:    */   }
/* 105:    */   
/* 106:    */   protected String getMemberName()
/* 107:    */   {
/* 108:133 */     return this.member.getName();
/* 109:    */   }
/* 110:    */   
/* 111:    */   protected abstract String getArguments();
/* 112:    */   
/* 113:    */   protected abstract String getReturnType();
/* 114:    */   
/* 115:    */   protected abstract Image getIcon();
/* 116:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.gui.MemberCell
 * JD-Core Version:    0.7.0.1
 */