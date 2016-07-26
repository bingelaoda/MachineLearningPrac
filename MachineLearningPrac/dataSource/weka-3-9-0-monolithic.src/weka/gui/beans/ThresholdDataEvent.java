/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.util.EventObject;
/*  4:   */ import weka.core.Attribute;
/*  5:   */ import weka.gui.visualize.PlotData2D;
/*  6:   */ 
/*  7:   */ public class ThresholdDataEvent
/*  8:   */   extends EventObject
/*  9:   */ {
/* 10:   */   private static final long serialVersionUID = -8309334224492439644L;
/* 11:   */   private PlotData2D m_dataSet;
/* 12:   */   private Attribute m_classAttribute;
/* 13:   */   
/* 14:   */   public ThresholdDataEvent(Object source, PlotData2D dataSet)
/* 15:   */   {
/* 16:48 */     this(source, dataSet, null);
/* 17:   */   }
/* 18:   */   
/* 19:   */   public ThresholdDataEvent(Object source, PlotData2D dataSet, Attribute classAtt)
/* 20:   */   {
/* 21:52 */     super(source);
/* 22:53 */     this.m_dataSet = dataSet;
/* 23:54 */     this.m_classAttribute = classAtt;
/* 24:   */   }
/* 25:   */   
/* 26:   */   public PlotData2D getDataSet()
/* 27:   */   {
/* 28:63 */     return this.m_dataSet;
/* 29:   */   }
/* 30:   */   
/* 31:   */   public Attribute getClassAttribute()
/* 32:   */   {
/* 33:73 */     return this.m_classAttribute;
/* 34:   */   }
/* 35:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ThresholdDataEvent
 * JD-Core Version:    0.7.0.1
 */