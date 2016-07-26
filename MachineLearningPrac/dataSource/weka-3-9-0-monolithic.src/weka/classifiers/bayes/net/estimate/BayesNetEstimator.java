/*   1:    */ package weka.classifiers.bayes.net.estimate;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.bayes.BayesNet;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Option;
/*   9:    */ import weka.core.OptionHandler;
/*  10:    */ import weka.core.RevisionHandler;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.Utils;
/*  13:    */ 
/*  14:    */ public class BayesNetEstimator
/*  15:    */   implements OptionHandler, Serializable, RevisionHandler
/*  16:    */ {
/*  17:    */   static final long serialVersionUID = 2184330197666253884L;
/*  18: 65 */   protected double m_fAlpha = 0.5D;
/*  19:    */   
/*  20:    */   public void estimateCPTs(BayesNet bayesNet)
/*  21:    */     throws Exception
/*  22:    */   {
/*  23: 76 */     throw new Exception("Incorrect BayesNetEstimator: use subclass instead.");
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void updateClassifier(BayesNet bayesNet, Instance instance)
/*  27:    */     throws Exception
/*  28:    */   {
/*  29: 89 */     throw new Exception("Incorrect BayesNetEstimator: use subclass instead.");
/*  30:    */   }
/*  31:    */   
/*  32:    */   public double[] distributionForInstance(BayesNet bayesNet, Instance instance)
/*  33:    */     throws Exception
/*  34:    */   {
/*  35:103 */     throw new Exception("Incorrect BayesNetEstimator: use subclass instead.");
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void initCPTs(BayesNet bayesNet)
/*  39:    */     throws Exception
/*  40:    */   {
/*  41:114 */     throw new Exception("Incorrect BayesNetEstimator: use subclass instead.");
/*  42:    */   }
/*  43:    */   
/*  44:    */   public Enumeration<Option> listOptions()
/*  45:    */   {
/*  46:124 */     Vector<Option> newVector = new Vector(1);
/*  47:    */     
/*  48:126 */     newVector.addElement(new Option("\tInitial count (alpha)\n", "A", 1, "-A <alpha>"));
/*  49:    */     
/*  50:    */ 
/*  51:129 */     return newVector.elements();
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void setOptions(String[] options)
/*  55:    */     throws Exception
/*  56:    */   {
/*  57:151 */     String sAlpha = Utils.getOption('A', options);
/*  58:153 */     if (sAlpha.length() != 0) {
/*  59:154 */       this.m_fAlpha = new Float(sAlpha).floatValue();
/*  60:    */     } else {
/*  61:156 */       this.m_fAlpha = 0.5D;
/*  62:    */     }
/*  63:159 */     Utils.checkForRemainingOptions(options);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String[] getOptions()
/*  67:    */   {
/*  68:169 */     String[] options = new String[2];
/*  69:170 */     int current = 0;
/*  70:    */     
/*  71:172 */     options[(current++)] = "-A";
/*  72:173 */     options[(current++)] = ("" + this.m_fAlpha);
/*  73:    */     
/*  74:175 */     return options;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setAlpha(double fAlpha)
/*  78:    */   {
/*  79:184 */     this.m_fAlpha = fAlpha;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public double getAlpha()
/*  83:    */   {
/*  84:193 */     return this.m_fAlpha;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public String alphaTipText()
/*  88:    */   {
/*  89:200 */     return "Alpha is used for estimating the probability tables and can be interpreted as the initial count on each value.";
/*  90:    */   }
/*  91:    */   
/*  92:    */   public String globalInfo()
/*  93:    */   {
/*  94:210 */     return "BayesNetEstimator is the base class for estimating the conditional probability tables of a Bayes network once the structure has been learned.";
/*  95:    */   }
/*  96:    */   
/*  97:    */   public String getRevision()
/*  98:    */   {
/*  99:222 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 100:    */   }
/* 101:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.estimate.BayesNetEstimator
 * JD-Core Version:    0.7.0.1
 */