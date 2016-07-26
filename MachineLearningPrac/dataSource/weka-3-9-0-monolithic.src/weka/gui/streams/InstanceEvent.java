/*  1:   */ package weka.gui.streams;
/*  2:   */ 
/*  3:   */ import java.util.EventObject;
/*  4:   */ 
/*  5:   */ public class InstanceEvent
/*  6:   */   extends EventObject
/*  7:   */ {
/*  8:   */   private static final long serialVersionUID = 3207259868110667379L;
/*  9:   */   public static final int FORMAT_AVAILABLE = 1;
/* 10:   */   public static final int INSTANCE_AVAILABLE = 2;
/* 11:   */   public static final int BATCH_FINISHED = 3;
/* 12:   */   private int m_ID;
/* 13:   */   
/* 14:   */   public InstanceEvent(Object source, int ID)
/* 15:   */   {
/* 16:58 */     super(source);
/* 17:59 */     this.m_ID = ID;
/* 18:   */   }
/* 19:   */   
/* 20:   */   public int getID()
/* 21:   */   {
/* 22:69 */     return this.m_ID;
/* 23:   */   }
/* 24:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.streams.InstanceEvent
 * JD-Core Version:    0.7.0.1
 */