/*   1:    */ package weka.classifiers;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Option;
/*   7:    */ import weka.core.Randomizable;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public abstract class RandomizableParallelMultipleClassifiersCombiner
/*  11:    */   extends ParallelMultipleClassifiersCombiner
/*  12:    */   implements Randomizable
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 8274061943448676943L;
/*  15: 47 */   protected int m_Seed = 1;
/*  16:    */   
/*  17:    */   public Enumeration<Option> listOptions()
/*  18:    */   {
/*  19: 56 */     Vector<Option> newVector = new Vector(1);
/*  20:    */     
/*  21: 58 */     newVector.addElement(new Option("\tRandom number seed.\n\t(default 1)", "S", 1, "-S <num>"));
/*  22:    */     
/*  23:    */ 
/*  24:    */ 
/*  25:    */ 
/*  26: 63 */     newVector.addAll(Collections.list(super.listOptions()));
/*  27:    */     
/*  28: 65 */     return newVector.elements();
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void setOptions(String[] options)
/*  32:    */     throws Exception
/*  33:    */   {
/*  34: 85 */     String seed = Utils.getOption('S', options);
/*  35: 86 */     if (seed.length() != 0) {
/*  36: 87 */       setSeed(Integer.parseInt(seed));
/*  37:    */     } else {
/*  38: 89 */       setSeed(1);
/*  39:    */     }
/*  40: 92 */     super.setOptions(options);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public String[] getOptions()
/*  44:    */   {
/*  45:102 */     Vector<String> options = new Vector();
/*  46:    */     
/*  47:104 */     options.add("-S");
/*  48:105 */     options.add("" + getSeed());
/*  49:    */     
/*  50:107 */     Collections.addAll(options, super.getOptions());
/*  51:    */     
/*  52:109 */     return (String[])options.toArray(new String[0]);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public String seedTipText()
/*  56:    */   {
/*  57:118 */     return "The random number seed to be used.";
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void setSeed(int seed)
/*  61:    */   {
/*  62:128 */     this.m_Seed = seed;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public int getSeed()
/*  66:    */   {
/*  67:138 */     return this.m_Seed;
/*  68:    */   }
/*  69:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.RandomizableParallelMultipleClassifiersCombiner
 * JD-Core Version:    0.7.0.1
 */