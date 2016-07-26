/*   1:    */ package weka.gui.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.beans.Beans;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.lang.annotation.Annotation;
/*   6:    */ import javax.swing.Icon;
/*   7:    */ import weka.core.Utils;
/*   8:    */ import weka.knowledgeflow.StepManagerImpl;
/*   9:    */ import weka.knowledgeflow.steps.KFStep;
/*  10:    */ import weka.knowledgeflow.steps.Step;
/*  11:    */ import weka.knowledgeflow.steps.WekaAlgorithmWrapper;
/*  12:    */ 
/*  13:    */ public class StepTreeLeafDetails
/*  14:    */   implements Serializable
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = 6347861816716877761L;
/*  17:    */   protected Class m_stepClazz;
/*  18: 53 */   protected String m_wrappedWekaAlgoName = "";
/*  19: 59 */   protected String m_leafLabel = "";
/*  20: 62 */   protected transient Icon m_scaledIcon = null;
/*  21: 65 */   protected String m_toolTipText = null;
/*  22: 68 */   protected boolean m_showTipText = true;
/*  23:    */   
/*  24:    */   public StepTreeLeafDetails(Object step)
/*  25:    */   {
/*  26: 76 */     this(step, true);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public StepTreeLeafDetails(Object step, boolean showTipText)
/*  30:    */   {
/*  31: 87 */     this.m_stepClazz = step.getClass();
/*  32:    */     
/*  33: 89 */     Annotation[] annotations = this.m_stepClazz.getAnnotations();
/*  34: 90 */     for (Annotation a : annotations) {
/*  35: 91 */       if ((a instanceof KFStep))
/*  36:    */       {
/*  37: 92 */         this.m_leafLabel = ((KFStep)a).name();
/*  38: 93 */         if (!showTipText) {
/*  39:    */           break;
/*  40:    */         }
/*  41: 94 */         this.m_toolTipText = ((KFStep)a).toolTipText(); break;
/*  42:    */       }
/*  43:    */     }
/*  44:100 */     if ((step instanceof Step)) {
/*  45:101 */       this.m_leafLabel = ((Step)step).getName();
/*  46:    */     }
/*  47:104 */     if ((step instanceof WekaAlgorithmWrapper)) {
/*  48:105 */       this.m_wrappedWekaAlgoName = ((WekaAlgorithmWrapper)step).getWrappedAlgorithm().getClass().getCanonicalName();
/*  49:    */     }
/*  50:110 */     if (showTipText)
/*  51:    */     {
/*  52:111 */       String globalInfo = Utils.getGlobalInfo(step, false);
/*  53:112 */       if (globalInfo != null) {
/*  54:113 */         this.m_toolTipText = globalInfo;
/*  55:    */       }
/*  56:    */     }
/*  57:117 */     this.m_scaledIcon = StepVisual.scaleIcon(StepVisual.iconForStep((Step)step), 0.33D);
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void setShowTipTexts(boolean show)
/*  61:    */   {
/*  62:127 */     this.m_showTipText = show;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public String getToolTipText()
/*  66:    */   {
/*  67:136 */     return this.m_showTipText ? this.m_toolTipText : null;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public String toString()
/*  71:    */   {
/*  72:146 */     return this.m_leafLabel;
/*  73:    */   }
/*  74:    */   
/*  75:    */   protected Icon getIcon()
/*  76:    */   {
/*  77:155 */     return this.m_scaledIcon;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public boolean isWrappedAlgorithm()
/*  81:    */   {
/*  82:165 */     return (this.m_wrappedWekaAlgoName != null) && (this.m_wrappedWekaAlgoName.length() > 0);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public StepVisual instantiateStep()
/*  86:    */     throws Exception
/*  87:    */   {
/*  88:175 */     Step step = null;
/*  89:    */     
/*  90:177 */     step = (Step)Beans.instantiate(getClass().getClassLoader(), this.m_stepClazz.getCanonicalName());
/*  91:    */     
/*  92:    */ 
/*  93:180 */     StepManagerImpl manager = new StepManagerImpl(step);
/*  94:182 */     if ((step instanceof WekaAlgorithmWrapper))
/*  95:    */     {
/*  96:183 */       Object algo = Beans.instantiate(getClass().getClassLoader(), this.m_wrappedWekaAlgoName);
/*  97:    */       
/*  98:    */ 
/*  99:186 */       ((WekaAlgorithmWrapper)step).setWrappedAlgorithm(algo);
/* 100:    */     }
/* 101:189 */     StepVisual visual = StepVisual.createVisual(manager);
/* 102:    */     
/* 103:191 */     return visual;
/* 104:    */   }
/* 105:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.StepTreeLeafDetails
 * JD-Core Version:    0.7.0.1
 */