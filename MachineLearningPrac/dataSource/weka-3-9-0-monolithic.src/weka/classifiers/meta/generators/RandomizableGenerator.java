/*   1:    */ package weka.classifiers.meta.generators;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Option;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public abstract class RandomizableGenerator
/*  11:    */   extends Generator
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = -4182619078970023472L;
/*  14: 44 */   protected Random m_Random = new Random(1L);
/*  15: 47 */   protected long m_Seed = 1L;
/*  16:    */   
/*  17:    */   public Enumeration<Option> listOptions()
/*  18:    */   {
/*  19: 57 */     Vector<Option> result = new Vector();
/*  20:    */     
/*  21: 59 */     result.addAll(Collections.list(super.listOptions()));
/*  22:    */     
/*  23: 61 */     result.addElement(new Option("\tSets the seed of the random number generator of the generator\t(default: 1)", "S", 1, "-S <seed>"));
/*  24:    */     
/*  25:    */ 
/*  26:    */ 
/*  27: 65 */     return result.elements();
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void setOptions(String[] options)
/*  31:    */     throws Exception
/*  32:    */   {
/*  33: 78 */     super.setOptions(options);
/*  34:    */     
/*  35: 80 */     String tmpStr = Utils.getOption("S", options);
/*  36: 81 */     if (tmpStr.length() != 0) {
/*  37: 82 */       setSeed(Long.parseLong(tmpStr));
/*  38:    */     } else {
/*  39: 84 */       setSeed(1L);
/*  40:    */     }
/*  41:    */   }
/*  42:    */   
/*  43:    */   public String[] getOptions()
/*  44:    */   {
/*  45: 96 */     Vector<String> result = new Vector();
/*  46:    */     
/*  47: 98 */     Collections.addAll(result, super.getOptions());
/*  48:    */     
/*  49:100 */     result.add("-S");
/*  50:101 */     result.add("" + this.m_Seed);
/*  51:    */     
/*  52:103 */     return (String[])result.toArray(new String[result.size()]);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setSeed(long value)
/*  56:    */   {
/*  57:112 */     this.m_Seed = value;
/*  58:113 */     this.m_Random = new Random(this.m_Seed);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public long getSeed()
/*  62:    */   {
/*  63:122 */     return this.m_Seed;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String seedTipText()
/*  67:    */   {
/*  68:132 */     return "The seed value for the random number generator.";
/*  69:    */   }
/*  70:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.generators.RandomizableGenerator
 * JD-Core Version:    0.7.0.1
 */