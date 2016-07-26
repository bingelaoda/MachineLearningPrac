/*   1:    */ package weka.classifiers;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Option;
/*   7:    */ import weka.core.Randomizable;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public abstract class RandomizableMultipleClassifiersCombiner
/*  11:    */   extends MultipleClassifiersCombiner
/*  12:    */   implements Randomizable
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 5057936555724785679L;
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
/*  34: 84 */     String seed = Utils.getOption('S', options);
/*  35: 85 */     if (seed.length() != 0) {
/*  36: 86 */       setSeed(Integer.parseInt(seed));
/*  37:    */     } else {
/*  38: 88 */       setSeed(1);
/*  39:    */     }
/*  40: 91 */     super.setOptions(options);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public String[] getOptions()
/*  44:    */   {
/*  45:101 */     Vector<String> options = new Vector();
/*  46:    */     
/*  47:103 */     options.add("-S");
/*  48:104 */     options.add("" + getSeed());
/*  49:    */     
/*  50:106 */     Collections.addAll(options, super.getOptions());
/*  51:    */     
/*  52:108 */     return (String[])options.toArray(new String[0]);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public String seedTipText()
/*  56:    */   {
/*  57:117 */     return "The random number seed to be used.";
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void setSeed(int seed)
/*  61:    */   {
/*  62:127 */     this.m_Seed = seed;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public int getSeed()
/*  66:    */   {
/*  67:137 */     return this.m_Seed;
/*  68:    */   }
/*  69:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.RandomizableMultipleClassifiersCombiner
 * JD-Core Version:    0.7.0.1
 */