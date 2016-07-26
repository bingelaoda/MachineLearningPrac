/*   1:    */ package weka.gui.experiment;
/*   2:    */ 
/*   3:    */ import java.beans.PropertyChangeListener;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Collections;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.Vector;
/*   9:    */ import javax.swing.JPanel;
/*  10:    */ import weka.experiment.Experiment;
/*  11:    */ import weka.gui.GenericObjectEditor;
/*  12:    */ 
/*  13:    */ public abstract class AbstractSetupPanel
/*  14:    */   extends JPanel
/*  15:    */   implements Comparable<AbstractSetupPanel>
/*  16:    */ {
/*  17:    */   public abstract String getName();
/*  18:    */   
/*  19:    */   public abstract void setModePanel(SetupModePanel paramSetupModePanel);
/*  20:    */   
/*  21:    */   public abstract boolean setExperiment(Experiment paramExperiment);
/*  22:    */   
/*  23:    */   public abstract Experiment getExperiment();
/*  24:    */   
/*  25:    */   public void cleanUpAfterSwitch() {}
/*  26:    */   
/*  27:    */   public abstract void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);
/*  28:    */   
/*  29:    */   public abstract void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);
/*  30:    */   
/*  31:    */   public int compareTo(AbstractSetupPanel o)
/*  32:    */   {
/*  33:102 */     return getName().compareTo(o.getName());
/*  34:    */   }
/*  35:    */   
/*  36:    */   public String toString()
/*  37:    */   {
/*  38:111 */     return getName();
/*  39:    */   }
/*  40:    */   
/*  41:    */   public static AbstractSetupPanel[] getPanels()
/*  42:    */   {
/*  43:125 */     List<AbstractSetupPanel> result = new ArrayList();
/*  44:126 */     Vector<String> names = GenericObjectEditor.getClassnames(AbstractSetupPanel.class.getName());
/*  45:127 */     for (String name : names) {
/*  46:    */       try
/*  47:    */       {
/*  48:129 */         Class cls = Class.forName(name);
/*  49:130 */         AbstractSetupPanel panel = (AbstractSetupPanel)cls.newInstance();
/*  50:131 */         result.add(panel);
/*  51:    */       }
/*  52:    */       catch (Exception e)
/*  53:    */       {
/*  54:134 */         System.err.println("Failed to instantiate setup panel: " + name);
/*  55:135 */         e.printStackTrace();
/*  56:    */       }
/*  57:    */     }
/*  58:139 */     Collections.sort(result);
/*  59:    */     
/*  60:141 */     return (AbstractSetupPanel[])result.toArray(new AbstractSetupPanel[result.size()]);
/*  61:    */   }
/*  62:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.experiment.AbstractSetupPanel
 * JD-Core Version:    0.7.0.1
 */