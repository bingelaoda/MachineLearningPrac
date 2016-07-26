/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.IOException;
/*   6:    */ import javax.swing.JPanel;
/*   7:    */ import weka.core.converters.FileSourcedConverter;
/*   8:    */ import weka.gui.FileEnvironmentField;
/*   9:    */ import weka.gui.knowledgeflow.GOEStepEditorDialog;
/*  10:    */ import weka.knowledgeflow.steps.Loader;
/*  11:    */ import weka.knowledgeflow.steps.Step;
/*  12:    */ 
/*  13:    */ public class LoaderStepEditorDialog
/*  14:    */   extends GOEStepEditorDialog
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = -6501371943783384741L;
/*  17:    */   protected FileEnvironmentField m_fileLoader;
/*  18:    */   
/*  19:    */   public void setStepToEdit(Step step)
/*  20:    */   {
/*  21: 62 */     copyOriginal(step);
/*  22: 63 */     Loader wrappedStep = (Loader)step;
/*  23: 65 */     if ((wrappedStep.getLoader() instanceof FileSourcedConverter)) {
/*  24: 66 */       setupFileLoader(wrappedStep);
/*  25:    */     } else {
/*  26: 68 */       super.setStepToEdit(step);
/*  27:    */     }
/*  28:    */   }
/*  29:    */   
/*  30:    */   protected void setupFileLoader(Loader wrappedStep)
/*  31:    */   {
/*  32: 80 */     addPrimaryEditorPanel("North");
/*  33: 81 */     this.m_fileLoader = new FileEnvironmentField("Filename", 0, false);
/*  34:    */     
/*  35: 83 */     this.m_fileLoader.setEnvironment(this.m_env);
/*  36: 84 */     JPanel p = new JPanel();
/*  37: 85 */     p.setLayout(new BorderLayout());
/*  38: 86 */     p.add(this.m_fileLoader, "North");
/*  39: 87 */     this.m_primaryEditorHolder.add(p, "Center");
/*  40:    */     
/*  41: 89 */     add(this.m_editorHolder, "Center");
/*  42: 90 */     File currentFile = ((FileSourcedConverter)wrappedStep.getLoader()).retrieveFile();
/*  43:    */     
/*  44: 92 */     this.m_fileLoader.setValue(currentFile);
/*  45:    */   }
/*  46:    */   
/*  47:    */   protected void okPressed()
/*  48:    */   {
/*  49:100 */     if ((((Loader)this.m_stepToEdit).getLoader() instanceof FileSourcedConverter)) {
/*  50:    */       try
/*  51:    */       {
/*  52:102 */         ((FileSourcedConverter)((Loader)this.m_stepToEdit).getLoader()).setFile((File)this.m_fileLoader.getValue());
/*  53:    */       }
/*  54:    */       catch (IOException e)
/*  55:    */       {
/*  56:105 */         e.printStackTrace();
/*  57:    */       }
/*  58:    */     }
/*  59:108 */     super.okPressed();
/*  60:    */   }
/*  61:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.LoaderStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */