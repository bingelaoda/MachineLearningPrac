/*   1:    */ package weka.classifiers;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.Option;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public abstract class IteratedSingleClassifierEnhancer
/*  11:    */   extends SingleClassifierEnhancer
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = -6217979135443319724L;
/*  14:    */   protected Classifier[] m_Classifiers;
/*  15: 49 */   protected int m_NumIterations = defaultNumberOfIterations();
/*  16:    */   
/*  17:    */   protected int defaultNumberOfIterations()
/*  18:    */   {
/*  19: 55 */     return 10;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public void buildClassifier(Instances data)
/*  23:    */     throws Exception
/*  24:    */   {
/*  25: 67 */     if (this.m_Classifier == null) {
/*  26: 68 */       throw new Exception("A base classifier has not been specified!");
/*  27:    */     }
/*  28: 70 */     this.m_Classifiers = AbstractClassifier.makeCopies(this.m_Classifier, this.m_NumIterations);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Enumeration<Option> listOptions()
/*  32:    */   {
/*  33: 80 */     Vector<Option> newVector = new Vector(2);
/*  34:    */     
/*  35: 82 */     newVector.addElement(new Option("\tNumber of iterations.\n\t(current value " + getNumIterations() + ")", "I", 1, "-I <num>"));
/*  36:    */     
/*  37:    */ 
/*  38:    */ 
/*  39:    */ 
/*  40: 87 */     newVector.addAll(Collections.list(super.listOptions()));
/*  41:    */     
/*  42: 89 */     return newVector.elements();
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setOptions(String[] options)
/*  46:    */     throws Exception
/*  47:    */   {
/*  48:108 */     String iterations = Utils.getOption('I', options);
/*  49:109 */     if (iterations.length() != 0) {
/*  50:110 */       setNumIterations(Integer.parseInt(iterations));
/*  51:    */     } else {
/*  52:112 */       setNumIterations(defaultNumberOfIterations());
/*  53:    */     }
/*  54:115 */     super.setOptions(options);
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String[] getOptions()
/*  58:    */   {
/*  59:125 */     String[] superOptions = super.getOptions();
/*  60:126 */     String[] options = new String[superOptions.length + 2];
/*  61:    */     
/*  62:128 */     int current = 0;
/*  63:129 */     options[(current++)] = "-I";
/*  64:130 */     options[(current++)] = ("" + getNumIterations());
/*  65:    */     
/*  66:132 */     System.arraycopy(superOptions, 0, options, current, superOptions.length);
/*  67:    */     
/*  68:    */ 
/*  69:135 */     return options;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public String numIterationsTipText()
/*  73:    */   {
/*  74:144 */     return "The number of iterations to be performed.";
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setNumIterations(int numIterations)
/*  78:    */   {
/*  79:152 */     this.m_NumIterations = numIterations;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public int getNumIterations()
/*  83:    */   {
/*  84:162 */     return this.m_NumIterations;
/*  85:    */   }
/*  86:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.IteratedSingleClassifierEnhancer
 * JD-Core Version:    0.7.0.1
 */