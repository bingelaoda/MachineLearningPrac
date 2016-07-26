/*   1:    */ package weka.clusterers;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Option;
/*   7:    */ import weka.core.OptionHandler;
/*   8:    */ import weka.core.Randomizable;
/*   9:    */ import weka.core.Utils;
/*  10:    */ 
/*  11:    */ public abstract class RandomizableSingleClustererEnhancer
/*  12:    */   extends AbstractClusterer
/*  13:    */   implements OptionHandler, Randomizable
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = -644847037106316249L;
/*  16: 47 */   protected int m_SeedDefault = 1;
/*  17: 50 */   protected int m_Seed = this.m_SeedDefault;
/*  18:    */   
/*  19:    */   public Enumeration<Option> listOptions()
/*  20:    */   {
/*  21: 59 */     Vector<Option> result = new Vector();
/*  22:    */     
/*  23: 61 */     result.addElement(new Option("\tRandom number seed.\n\t(default " + this.m_SeedDefault + ")", "S", 1, "-S <num>"));
/*  24:    */     
/*  25:    */ 
/*  26: 64 */     result.addAll(Collections.list(super.listOptions()));
/*  27:    */     
/*  28: 66 */     return result.elements();
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void setOptions(String[] options)
/*  32:    */     throws Exception
/*  33:    */   {
/*  34: 79 */     String tmpStr = Utils.getOption('S', options);
/*  35: 80 */     if (tmpStr.length() != 0) {
/*  36: 81 */       setSeed(Integer.parseInt(tmpStr));
/*  37:    */     } else {
/*  38: 83 */       setSeed(this.m_SeedDefault);
/*  39:    */     }
/*  40: 86 */     super.setOptions(options);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public String[] getOptions()
/*  44:    */   {
/*  45: 96 */     Vector<String> result = new Vector();
/*  46:    */     
/*  47: 98 */     result.add("-S");
/*  48: 99 */     result.add("" + getSeed());
/*  49:    */     
/*  50:101 */     Collections.addAll(result, super.getOptions());
/*  51:    */     
/*  52:103 */     return (String[])result.toArray(new String[result.size()]);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public String seedTipText()
/*  56:    */   {
/*  57:113 */     return "The random number seed to be used.";
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void setSeed(int value)
/*  61:    */   {
/*  62:123 */     this.m_Seed = value;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public int getSeed()
/*  66:    */   {
/*  67:133 */     return this.m_Seed;
/*  68:    */   }
/*  69:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.RandomizableSingleClustererEnhancer
 * JD-Core Version:    0.7.0.1
 */