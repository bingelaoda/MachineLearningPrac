/*  1:   */ package weka.gui.sql.event;
/*  2:   */ 
/*  3:   */ import java.util.EventObject;
/*  4:   */ import javax.swing.DefaultListModel;
/*  5:   */ 
/*  6:   */ public class HistoryChangedEvent
/*  7:   */   extends EventObject
/*  8:   */ {
/*  9:   */   private static final long serialVersionUID = 7476087315774869973L;
/* 10:   */   protected String m_HistoryName;
/* 11:   */   protected DefaultListModel m_History;
/* 12:   */   
/* 13:   */   public HistoryChangedEvent(Object source, String name, DefaultListModel history)
/* 14:   */   {
/* 15:55 */     super(source);
/* 16:   */     
/* 17:57 */     this.m_HistoryName = name;
/* 18:58 */     this.m_History = history;
/* 19:   */   }
/* 20:   */   
/* 21:   */   public String getHistoryName()
/* 22:   */   {
/* 23:65 */     return this.m_HistoryName;
/* 24:   */   }
/* 25:   */   
/* 26:   */   public DefaultListModel getHistory()
/* 27:   */   {
/* 28:72 */     return this.m_History;
/* 29:   */   }
/* 30:   */   
/* 31:   */   public String toString()
/* 32:   */   {
/* 33:82 */     String result = super.toString();
/* 34:83 */     result = result.substring(0, result.length() - 1);
/* 35:84 */     result = result + ",name=" + getHistoryName() + ",history=" + getHistory() + "]";
/* 36:   */     
/* 37:   */ 
/* 38:   */ 
/* 39:88 */     return result;
/* 40:   */   }
/* 41:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.event.HistoryChangedEvent
 * JD-Core Version:    0.7.0.1
 */