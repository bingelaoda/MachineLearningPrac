/*   1:    */ package weka.classifiers.rules;
/*   2:    */ 
/*   3:    */ import weka.classifiers.trees.m5.M5Base;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ import weka.core.TechnicalInformation;
/*   6:    */ import weka.core.TechnicalInformation.Field;
/*   7:    */ import weka.core.TechnicalInformation.Type;
/*   8:    */ import weka.core.TechnicalInformationHandler;
/*   9:    */ 
/*  10:    */ public class M5Rules
/*  11:    */   extends M5Base
/*  12:    */   implements TechnicalInformationHandler
/*  13:    */ {
/*  14:    */   static final long serialVersionUID = -1746114858746563180L;
/*  15:    */   
/*  16:    */   public String globalInfo()
/*  17:    */   {
/*  18:112 */     return "Generates a decision list for regression problems using separate-and-conquer. In each iteration it builds a model tree using M5 and makes the \"best\" leaf into a rule.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  19:    */   }
/*  20:    */   
/*  21:    */   public M5Rules()
/*  22:    */   {
/*  23:125 */     setGenerateRules(true);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public TechnicalInformation getTechnicalInformation()
/*  27:    */   {
/*  28:138 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  29:139 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Geoffrey Holmes and Mark Hall and Eibe Frank");
/*  30:140 */     result.setValue(TechnicalInformation.Field.TITLE, "Generating Rule Sets from Model Trees");
/*  31:141 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Twelfth Australian Joint Conference on Artificial Intelligence");
/*  32:142 */     result.setValue(TechnicalInformation.Field.YEAR, "1999");
/*  33:143 */     result.setValue(TechnicalInformation.Field.PAGES, "1-12");
/*  34:144 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*  35:    */     
/*  36:146 */     result.add(super.getTechnicalInformation());
/*  37:    */     
/*  38:148 */     return result;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public String getRevision()
/*  42:    */   {
/*  43:157 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  44:    */   }
/*  45:    */   
/*  46:    */   public static void main(String[] args)
/*  47:    */   {
/*  48:166 */     runClassifier(new M5Rules(), args);
/*  49:    */   }
/*  50:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.rules.M5Rules
 * JD-Core Version:    0.7.0.1
 */