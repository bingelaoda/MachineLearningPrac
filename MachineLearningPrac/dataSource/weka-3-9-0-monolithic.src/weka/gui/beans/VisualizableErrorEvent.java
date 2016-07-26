/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.util.EventObject;
/*  4:   */ import weka.gui.visualize.PlotData2D;
/*  5:   */ 
/*  6:   */ public class VisualizableErrorEvent
/*  7:   */   extends EventObject
/*  8:   */ {
/*  9:   */   private static final long serialVersionUID = -5811819270887223400L;
/* 10:   */   private PlotData2D m_dataSet;
/* 11:   */   
/* 12:   */   public VisualizableErrorEvent(Object source, PlotData2D dataSet)
/* 13:   */   {
/* 14:45 */     super(source);
/* 15:46 */     this.m_dataSet = dataSet;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public PlotData2D getDataSet()
/* 19:   */   {
/* 20:55 */     return this.m_dataSet;
/* 21:   */   }
/* 22:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.VisualizableErrorEvent
 * JD-Core Version:    0.7.0.1
 */