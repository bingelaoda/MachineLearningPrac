/*  1:   */ package weka.classifiers.xml;
/*  2:   */ 
/*  3:   */ import weka.classifiers.Classifier;
/*  4:   */ import weka.core.RevisionUtils;
/*  5:   */ import weka.core.xml.PropertyHandler;
/*  6:   */ import weka.core.xml.XMLBasicSerialization;
/*  7:   */ 
/*  8:   */ public class XMLClassifier
/*  9:   */   extends XMLBasicSerialization
/* 10:   */ {
/* 11:   */   public XMLClassifier()
/* 12:   */     throws Exception
/* 13:   */   {}
/* 14:   */   
/* 15:   */   public void clear()
/* 16:   */     throws Exception
/* 17:   */   {
/* 18:50 */     super.clear();
/* 19:   */     
/* 20:   */ 
/* 21:53 */     this.m_Properties.addAllowed(Classifier.class, "debug");
/* 22:54 */     this.m_Properties.addAllowed(Classifier.class, "options");
/* 23:   */   }
/* 24:   */   
/* 25:   */   public String getRevision()
/* 26:   */   {
/* 27:63 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 28:   */   }
/* 29:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.xml.XMLClassifier
 * JD-Core Version:    0.7.0.1
 */