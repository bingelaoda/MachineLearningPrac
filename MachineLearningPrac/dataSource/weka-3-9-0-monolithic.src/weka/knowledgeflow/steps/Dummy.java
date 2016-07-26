/*  1:   */ package weka.knowledgeflow.steps;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ import java.util.List;
/*  5:   */ import weka.core.WekaException;
/*  6:   */ 
/*  7:   */ @KFStep(name="Dummy", category="Misc", toolTipText="A step that is the equivalent of /dev/null", iconPath="weka/gui/knowledgeflow/icons/DiamondPlain.gif")
/*  8:   */ public class Dummy
/*  9:   */   extends BaseStep
/* 10:   */ {
/* 11:   */   private static final long serialVersionUID = -5822675617001689385L;
/* 12:   */   
/* 13:   */   public void stepInit()
/* 14:   */     throws WekaException
/* 15:   */   {}
/* 16:   */   
/* 17:   */   public List<String> getIncomingConnectionTypes()
/* 18:   */   {
/* 19:68 */     return Arrays.asList(new String[] { "dataSet", "trainingSet", "testSet", "instance" });
/* 20:   */   }
/* 21:   */   
/* 22:   */   public List<String> getOutgoingConnectionTypes()
/* 23:   */   {
/* 24:83 */     return null;
/* 25:   */   }
/* 26:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.Dummy
 * JD-Core Version:    0.7.0.1
 */