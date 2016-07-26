/*   1:    */ package weka.classifiers;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Option;
/*   7:    */ import weka.core.Randomizable;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public abstract class RandomizableParallelIteratedSingleClassifierEnhancer
/*  11:    */   extends ParallelIteratedSingleClassifierEnhancer
/*  12:    */   implements Randomizable
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 1298141000373615374L;
/*  15: 49 */   protected int m_Seed = 1;
/*  16:    */   
/*  17:    */   public Enumeration<Option> listOptions()
/*  18:    */   {
/*  19: 58 */     Vector<Option> newVector = new Vector(1);
/*  20:    */     
/*  21: 60 */     newVector.addElement(new Option("\tRandom number seed.\n\t(default 1)", "S", 1, "-S <num>"));
/*  22:    */     
/*  23:    */ 
/*  24:    */ 
/*  25:    */ 
/*  26: 65 */     newVector.addAll(Collections.list(super.listOptions()));
/*  27: 66 */     return newVector.elements();
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void setOptions(String[] options)
/*  31:    */     throws Exception
/*  32:    */   {
/*  33: 88 */     String seed = Utils.getOption('S', options);
/*  34: 89 */     if (seed.length() != 0) {
/*  35: 90 */       setSeed(Integer.parseInt(seed));
/*  36:    */     } else {
/*  37: 92 */       setSeed(1);
/*  38:    */     }
/*  39: 95 */     super.setOptions(options);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public String[] getOptions()
/*  43:    */   {
/*  44:105 */     Vector<String> options = new Vector();
/*  45:    */     
/*  46:107 */     options.add("-S");
/*  47:108 */     options.add("" + getSeed());
/*  48:    */     
/*  49:110 */     Collections.addAll(options, super.getOptions());
/*  50:    */     
/*  51:112 */     return (String[])options.toArray(new String[0]);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String seedTipText()
/*  55:    */   {
/*  56:121 */     return "The random number seed to be used.";
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setSeed(int seed)
/*  60:    */   {
/*  61:131 */     this.m_Seed = seed;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public int getSeed()
/*  65:    */   {
/*  66:141 */     return this.m_Seed;
/*  67:    */   }
/*  68:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.RandomizableParallelIteratedSingleClassifierEnhancer
 * JD-Core Version:    0.7.0.1
 */