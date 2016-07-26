/*  1:   */ package weka.gui.visualize;
/*  2:   */ 
/*  3:   */ public class AttributePanelEvent
/*  4:   */ {
/*  5:   */   public boolean m_xChange;
/*  6:   */   public boolean m_yChange;
/*  7:   */   public int m_indexVal;
/*  8:   */   
/*  9:   */   public AttributePanelEvent(boolean xChange, boolean yChange, int indexVal)
/* 10:   */   {
/* 11:50 */     this.m_xChange = xChange;
/* 12:51 */     this.m_yChange = yChange;
/* 13:52 */     this.m_indexVal = indexVal;
/* 14:   */   }
/* 15:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.AttributePanelEvent
 * JD-Core Version:    0.7.0.1
 */