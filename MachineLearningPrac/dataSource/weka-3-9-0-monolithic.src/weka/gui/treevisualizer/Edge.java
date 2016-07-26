/*   1:    */ package weka.gui.treevisualizer;
/*   2:    */ 
/*   3:    */ import java.awt.Dimension;
/*   4:    */ import java.awt.FontMetrics;
/*   5:    */ import java.util.Vector;
/*   6:    */ 
/*   7:    */ public class Edge
/*   8:    */ {
/*   9:    */   private final String m_label;
/*  10:    */   private String m_rsource;
/*  11:    */   private String m_rtarget;
/*  12:    */   private Node m_source;
/*  13:    */   private Node m_target;
/*  14:    */   private final Vector<String> m_lines;
/*  15:    */   
/*  16:    */   public Edge(String label, String source, String target)
/*  17:    */   {
/*  18: 70 */     this.m_label = label;
/*  19: 71 */     this.m_rsource = source;
/*  20: 72 */     this.m_rtarget = target;
/*  21: 73 */     this.m_lines = new Vector(3, 2);
/*  22: 74 */     breakupLabel();
/*  23:    */   }
/*  24:    */   
/*  25:    */   public String getLabel()
/*  26:    */   {
/*  27: 84 */     return this.m_label;
/*  28:    */   }
/*  29:    */   
/*  30:    */   private void breakupLabel()
/*  31:    */   {
/*  32: 92 */     int prev = 0;
/*  33: 93 */     for (int noa = 0; noa < this.m_label.length(); noa++) {
/*  34: 94 */       if (this.m_label.charAt(noa) == '\n')
/*  35:    */       {
/*  36: 95 */         this.m_lines.addElement(this.m_label.substring(prev, noa));
/*  37: 96 */         prev = noa + 1;
/*  38:    */       }
/*  39:    */     }
/*  40: 99 */     this.m_lines.addElement(this.m_label.substring(prev, noa));
/*  41:    */   }
/*  42:    */   
/*  43:    */   public Dimension stringSize(FontMetrics f)
/*  44:    */   {
/*  45:110 */     Dimension d = new Dimension();
/*  46:111 */     int old = 0;
/*  47:    */     
/*  48:113 */     int noa = 0;
/*  49:    */     String s;
/*  50:114 */     while ((s = getLine(noa)) != null)
/*  51:    */     {
/*  52:115 */       noa++;
/*  53:116 */       old = f.stringWidth(s);
/*  54:118 */       if (old > d.width) {
/*  55:119 */         d.width = old;
/*  56:    */       }
/*  57:    */     }
/*  58:122 */     d.height = (noa * f.getHeight());
/*  59:123 */     return d;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String getLine(int n)
/*  63:    */   {
/*  64:133 */     if (n < this.m_lines.size()) {
/*  65:134 */       return (String)this.m_lines.elementAt(n);
/*  66:    */     }
/*  67:136 */     return null;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public String getRsource()
/*  71:    */   {
/*  72:147 */     return this.m_rsource;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void setRsource(String v)
/*  76:    */   {
/*  77:157 */     this.m_rsource = v;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public String getRtarget()
/*  81:    */   {
/*  82:167 */     return this.m_rtarget;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setRtarget(String v)
/*  86:    */   {
/*  87:177 */     this.m_rtarget = v;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public Node getSource()
/*  91:    */   {
/*  92:187 */     return this.m_source;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void setSource(Node v)
/*  96:    */   {
/*  97:198 */     this.m_source = v;
/*  98:199 */     v.addChild(this);
/*  99:    */   }
/* 100:    */   
/* 101:    */   public Node getTarget()
/* 102:    */   {
/* 103:209 */     return this.m_target;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public void setTarget(Node v)
/* 107:    */   {
/* 108:220 */     this.m_target = v;
/* 109:221 */     v.setParent(this);
/* 110:    */   }
/* 111:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.treevisualizer.Edge
 * JD-Core Version:    0.7.0.1
 */