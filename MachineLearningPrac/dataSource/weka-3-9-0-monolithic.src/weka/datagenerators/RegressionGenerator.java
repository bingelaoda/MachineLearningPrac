/*   1:    */ package weka.datagenerators;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Option;
/*   7:    */ import weka.core.Utils;
/*   8:    */ 
/*   9:    */ public abstract class RegressionGenerator
/*  10:    */   extends DataGenerator
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = 3073254041275658221L;
/*  13:    */   protected int m_NumExamples;
/*  14:    */   
/*  15:    */   public RegressionGenerator()
/*  16:    */   {
/*  17: 65 */     setNumExamples(defaultNumExamples());
/*  18:    */   }
/*  19:    */   
/*  20:    */   public Enumeration<Option> listOptions()
/*  21:    */   {
/*  22: 75 */     Vector<Option> result = enumToVector(super.listOptions());
/*  23:    */     
/*  24: 77 */     result.addElement(new Option("\tThe number of examples to generate (default " + defaultNumExamples() + ")", "n", 1, "-n <num>"));
/*  25:    */     
/*  26:    */ 
/*  27:    */ 
/*  28: 81 */     return result.elements();
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void setOptions(String[] options)
/*  32:    */     throws Exception
/*  33:    */   {
/*  34: 94 */     super.setOptions(options);
/*  35:    */     
/*  36: 96 */     String tmpStr = Utils.getOption('n', options);
/*  37: 97 */     if (tmpStr.length() != 0) {
/*  38: 98 */       setNumExamples(Integer.parseInt(tmpStr));
/*  39:    */     } else {
/*  40:100 */       setNumExamples(defaultNumExamples());
/*  41:    */     }
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String[] getOptions()
/*  45:    */   {
/*  46:112 */     Vector<String> result = new Vector();
/*  47:    */     
/*  48:114 */     Collections.addAll(result, super.getOptions());
/*  49:    */     
/*  50:116 */     result.add("-n");
/*  51:117 */     result.add("" + getNumExamples());
/*  52:    */     
/*  53:119 */     return (String[])result.toArray(new String[result.size()]);
/*  54:    */   }
/*  55:    */   
/*  56:    */   protected int defaultNumExamples()
/*  57:    */   {
/*  58:128 */     return 100;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setNumExamples(int numExamples)
/*  62:    */   {
/*  63:137 */     this.m_NumExamples = numExamples;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public int getNumExamples()
/*  67:    */   {
/*  68:146 */     return this.m_NumExamples;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public String numExamplesTipText()
/*  72:    */   {
/*  73:156 */     return "The number of examples to generate.";
/*  74:    */   }
/*  75:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.datagenerators.RegressionGenerator
 * JD-Core Version:    0.7.0.1
 */