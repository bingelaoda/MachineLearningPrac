/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.util.EventObject;
/*  4:   */ 
/*  5:   */ public class GraphEvent
/*  6:   */   extends EventObject
/*  7:   */ {
/*  8:   */   private static final long serialVersionUID = 2099494034652519986L;
/*  9:   */   protected String m_graphString;
/* 10:   */   protected String m_graphTitle;
/* 11:   */   protected int m_graphType;
/* 12:   */   
/* 13:   */   public GraphEvent(Object source, String graphString, String graphTitle, int graphType)
/* 14:   */   {
/* 15:52 */     super(source);
/* 16:53 */     this.m_graphString = graphString;
/* 17:54 */     this.m_graphTitle = graphTitle;
/* 18:55 */     this.m_graphType = graphType;
/* 19:   */   }
/* 20:   */   
/* 21:   */   public String getGraphString()
/* 22:   */   {
/* 23:64 */     return this.m_graphString;
/* 24:   */   }
/* 25:   */   
/* 26:   */   public String getGraphTitle()
/* 27:   */   {
/* 28:73 */     return this.m_graphTitle;
/* 29:   */   }
/* 30:   */   
/* 31:   */   public int getGraphType()
/* 32:   */   {
/* 33:82 */     return this.m_graphType;
/* 34:   */   }
/* 35:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.GraphEvent
 * JD-Core Version:    0.7.0.1
 */