/*   1:    */ package weka.datagenerators;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Option;
/*   7:    */ import weka.core.Utils;
/*   8:    */ 
/*   9:    */ public abstract class ClassificationGenerator
/*  10:    */   extends DataGenerator
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -5261662546673517844L;
/*  13:    */   protected int m_NumExamples;
/*  14:    */   
/*  15:    */   public ClassificationGenerator()
/*  16:    */   {
/*  17: 53 */     setNumExamples(defaultNumExamples());
/*  18:    */   }
/*  19:    */   
/*  20:    */   public Enumeration<Option> listOptions()
/*  21:    */   {
/*  22: 63 */     Vector<Option> result = enumToVector(super.listOptions());
/*  23:    */     
/*  24: 65 */     result.addElement(new Option("\tThe number of examples to generate (default " + defaultNumExamples() + ")", "n", 1, "-n <num>"));
/*  25:    */     
/*  26:    */ 
/*  27:    */ 
/*  28: 69 */     return result.elements();
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void setOptions(String[] options)
/*  32:    */     throws Exception
/*  33:    */   {
/*  34: 82 */     super.setOptions(options);
/*  35:    */     
/*  36: 84 */     String tmpStr = Utils.getOption('n', options);
/*  37: 85 */     if (tmpStr.length() != 0) {
/*  38: 86 */       setNumExamples(Integer.parseInt(tmpStr));
/*  39:    */     } else {
/*  40: 88 */       setNumExamples(defaultNumExamples());
/*  41:    */     }
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String[] getOptions()
/*  45:    */   {
/*  46:100 */     Vector<String> result = new Vector();
/*  47:    */     
/*  48:102 */     Collections.addAll(result, super.getOptions());
/*  49:    */     
/*  50:104 */     result.add("-n");
/*  51:105 */     result.add("" + getNumExamples());
/*  52:    */     
/*  53:107 */     return (String[])result.toArray(new String[result.size()]);
/*  54:    */   }
/*  55:    */   
/*  56:    */   protected int defaultNumExamples()
/*  57:    */   {
/*  58:116 */     return 100;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setNumExamples(int numExamples)
/*  62:    */   {
/*  63:125 */     this.m_NumExamples = numExamples;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public int getNumExamples()
/*  67:    */   {
/*  68:134 */     return this.m_NumExamples;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public String numExamplesTipText()
/*  72:    */   {
/*  73:144 */     return "The number of examples to generate.";
/*  74:    */   }
/*  75:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.datagenerators.ClassificationGenerator
 * JD-Core Version:    0.7.0.1
 */