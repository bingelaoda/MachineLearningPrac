/*  1:   */ package weka.knowledgeflow.steps;
/*  2:   */ 
/*  3:   */ import java.util.ArrayList;
/*  4:   */ import java.util.List;
/*  5:   */ 
/*  6:   */ public class Note
/*  7:   */   extends BaseStep
/*  8:   */ {
/*  9:36 */   protected String m_noteText = "New note";
/* 10:   */   
/* 11:   */   public void stepInit() {}
/* 12:   */   
/* 13:   */   public void setNoteText(String text)
/* 14:   */   {
/* 15:52 */     this.m_noteText = text;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public String getNoteText()
/* 19:   */   {
/* 20:61 */     return this.m_noteText;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public List<String> getIncomingConnectionTypes()
/* 24:   */   {
/* 25:71 */     return new ArrayList();
/* 26:   */   }
/* 27:   */   
/* 28:   */   public List<String> getOutgoingConnectionTypes()
/* 29:   */   {
/* 30:81 */     return new ArrayList();
/* 31:   */   }
/* 32:   */   
/* 33:   */   public String getCustomEditorForStep()
/* 34:   */   {
/* 35:94 */     return "weka.gui.knowledgeflow.steps.NoteEditorDialog";
/* 36:   */   }
/* 37:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.Note
 * JD-Core Version:    0.7.0.1
 */