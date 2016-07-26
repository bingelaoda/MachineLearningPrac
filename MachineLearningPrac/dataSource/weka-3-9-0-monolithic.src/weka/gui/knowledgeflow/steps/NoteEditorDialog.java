/*  1:   */ package weka.gui.knowledgeflow.steps;
/*  2:   */ 
/*  3:   */ import java.awt.BorderLayout;
/*  4:   */ import javax.swing.BorderFactory;
/*  5:   */ import javax.swing.JPanel;
/*  6:   */ import javax.swing.JScrollPane;
/*  7:   */ import javax.swing.JTextArea;
/*  8:   */ import weka.gui.knowledgeflow.StepEditorDialog;
/*  9:   */ import weka.knowledgeflow.steps.Note;
/* 10:   */ import weka.knowledgeflow.steps.Step;
/* 11:   */ 
/* 12:   */ public class NoteEditorDialog
/* 13:   */   extends StepEditorDialog
/* 14:   */ {
/* 15:   */   private static final long serialVersionUID = 2358735294813135692L;
/* 16:42 */   protected JTextArea m_textArea = new JTextArea(5, 40);
/* 17:   */   
/* 18:   */   protected void setStepToEdit(Step step)
/* 19:   */   {
/* 20:52 */     this.m_stepToEdit = step;
/* 21:53 */     layoutEditor();
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void layoutEditor()
/* 25:   */   {
/* 26:61 */     this.m_textArea.setLineWrap(true);
/* 27:62 */     String noteText = ((Note)getStepToEdit()).getNoteText();
/* 28:63 */     this.m_textArea.setText(noteText);
/* 29:64 */     JScrollPane sc = new JScrollPane(this.m_textArea);
/* 30:   */     
/* 31:66 */     JPanel holder = new JPanel(new BorderLayout());
/* 32:67 */     holder.setBorder(BorderFactory.createTitledBorder("Note Editor"));
/* 33:68 */     holder.add(sc, "Center");
/* 34:69 */     add(holder, "Center");
/* 35:   */   }
/* 36:   */   
/* 37:   */   public void okPressed()
/* 38:   */   {
/* 39:77 */     ((Note)getStepToEdit()).setNoteText(this.m_textArea.getText());
/* 40:   */   }
/* 41:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.NoteEditorDialog
 * JD-Core Version:    0.7.0.1
 */