/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.List;
/*   6:    */ import weka.attributeSelection.ASSearch;
/*   7:    */ import weka.gui.ProgrammaticProperty;
/*   8:    */ 
/*   9:    */ @KFStep(name="ASSearchStrategy", category="AttSelection", toolTipText="Weka attribute selection search wrapper", iconPath="")
/*  10:    */ public class ASSearchStrategy
/*  11:    */   extends WekaAlgorithmWrapper
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = 5038697382280884975L;
/*  14:    */   
/*  15:    */   public void stepInit() {}
/*  16:    */   
/*  17:    */   public List<String> getIncomingConnectionTypes()
/*  18:    */   {
/*  19: 64 */     return new ArrayList();
/*  20:    */   }
/*  21:    */   
/*  22:    */   public List<String> getOutgoingConnectionTypes()
/*  23:    */   {
/*  24: 75 */     return Arrays.asList(new String[] { "info" });
/*  25:    */   }
/*  26:    */   
/*  27:    */   public Class getWrappedAlgorithmClass()
/*  28:    */   {
/*  29: 86 */     return ASSearch.class;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void setWrappedAlgorithm(Object algo)
/*  33:    */   {
/*  34: 96 */     super.setWrappedAlgorithm(algo);
/*  35: 97 */     this.m_defaultIconPath = "weka/gui/knowledgeflow/icons/filters.supervised.attribute.AttributeSelection.gif";
/*  36:    */   }
/*  37:    */   
/*  38:    */   @ProgrammaticProperty
/*  39:    */   public void setSearchStrategy(ASSearch searchStrategy)
/*  40:    */   {
/*  41:109 */     setWrappedAlgorithm(searchStrategy);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public ASSearch getSearchStrategy()
/*  45:    */   {
/*  46:118 */     return (ASSearch)getWrappedAlgorithm();
/*  47:    */   }
/*  48:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.ASSearchStrategy
 * JD-Core Version:    0.7.0.1
 */