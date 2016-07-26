/*  1:   */ package weka.gui.scripting.event;
/*  2:   */ 
/*  3:   */ import java.util.EventObject;
/*  4:   */ import weka.gui.scripting.ScriptingPanel;
/*  5:   */ 
/*  6:   */ public class TitleUpdatedEvent
/*  7:   */   extends EventObject
/*  8:   */ {
/*  9:   */   private static final long serialVersionUID = 4971569742479666535L;
/* 10:   */   
/* 11:   */   public TitleUpdatedEvent(ScriptingPanel source)
/* 12:   */   {
/* 13:45 */     super(source);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public ScriptingPanel getPanel()
/* 17:   */   {
/* 18:55 */     return (ScriptingPanel)getSource();
/* 19:   */   }
/* 20:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.scripting.event.TitleUpdatedEvent
 * JD-Core Version:    0.7.0.1
 */