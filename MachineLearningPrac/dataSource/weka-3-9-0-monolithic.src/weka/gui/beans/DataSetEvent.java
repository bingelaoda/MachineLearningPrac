/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.util.EventObject;
/*  4:   */ import weka.core.Instances;
/*  5:   */ 
/*  6:   */ public class DataSetEvent
/*  7:   */   extends EventObject
/*  8:   */ {
/*  9:   */   private static final long serialVersionUID = -5111218447577318057L;
/* 10:   */   private Instances m_dataSet;
/* 11:   */   private boolean m_structureOnly;
/* 12:   */   
/* 13:   */   public DataSetEvent(Object source, Instances dataSet)
/* 14:   */   {
/* 15:45 */     super(source);
/* 16:46 */     this.m_dataSet = dataSet;
/* 17:47 */     if ((this.m_dataSet != null) && (this.m_dataSet.numInstances() == 0)) {
/* 18:48 */       this.m_structureOnly = true;
/* 19:   */     }
/* 20:   */   }
/* 21:   */   
/* 22:   */   public Instances getDataSet()
/* 23:   */   {
/* 24:58 */     return this.m_dataSet;
/* 25:   */   }
/* 26:   */   
/* 27:   */   public boolean isStructureOnly()
/* 28:   */   {
/* 29:69 */     return this.m_structureOnly;
/* 30:   */   }
/* 31:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.DataSetEvent
 * JD-Core Version:    0.7.0.1
 */