/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.util.EventObject;
/*  4:   */ 
/*  5:   */ public class TextEvent
/*  6:   */   extends EventObject
/*  7:   */ {
/*  8:   */   private static final long serialVersionUID = 4196810607402973744L;
/*  9:   */   protected String m_text;
/* 10:   */   protected String m_textTitle;
/* 11:   */   
/* 12:   */   public TextEvent(Object source, String text, String textTitle)
/* 13:   */   {
/* 14:55 */     super(source);
/* 15:   */     
/* 16:57 */     this.m_text = text;
/* 17:58 */     this.m_textTitle = textTitle;
/* 18:   */   }
/* 19:   */   
/* 20:   */   public String getText()
/* 21:   */   {
/* 22:67 */     return this.m_text;
/* 23:   */   }
/* 24:   */   
/* 25:   */   public String getTextTitle()
/* 26:   */   {
/* 27:76 */     return this.m_textTitle;
/* 28:   */   }
/* 29:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.TextEvent
 * JD-Core Version:    0.7.0.1
 */