/*  1:   */ package weka.gui.treevisualizer;
/*  2:   */ 
/*  3:   */ import java.awt.Color;
/*  4:   */ 
/*  5:   */ public class NamedColor
/*  6:   */ {
/*  7:   */   public String m_name;
/*  8:   */   public Color m_col;
/*  9:   */   
/* 10:   */   public NamedColor(String n, int r, int g, int b)
/* 11:   */   {
/* 12:48 */     this.m_name = n;
/* 13:49 */     this.m_col = new Color(r, g, b);
/* 14:   */   }
/* 15:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.treevisualizer.NamedColor
 * JD-Core Version:    0.7.0.1
 */