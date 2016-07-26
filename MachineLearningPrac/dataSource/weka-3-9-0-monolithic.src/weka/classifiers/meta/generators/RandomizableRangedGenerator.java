/*   1:    */ package weka.classifiers.meta.generators;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Option;
/*   7:    */ import weka.core.Utils;
/*   8:    */ 
/*   9:    */ public abstract class RandomizableRangedGenerator
/*  10:    */   extends RandomizableGenerator
/*  11:    */   implements Ranged
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = -5766761200929361752L;
/*  14: 44 */   protected double m_LowerRange = 0.0D;
/*  15: 47 */   protected double m_UpperRange = 1.0D;
/*  16:    */   
/*  17:    */   public Enumeration<Option> listOptions()
/*  18:    */   {
/*  19: 57 */     Vector<Option> result = new Vector();
/*  20:    */     
/*  21: 59 */     result.addAll(Collections.list(super.listOptions()));
/*  22:    */     
/*  23: 61 */     result.addElement(new Option("\tSets the lower range of the generator\n\t(default: 0)", "L", 1, "-L <num>"));
/*  24:    */     
/*  25:    */ 
/*  26: 64 */     result.addElement(new Option("\tSets the upper range of the generator\n\t(default: 1)", "U", 1, "-U <num>"));
/*  27:    */     
/*  28:    */ 
/*  29: 67 */     return result.elements();
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void setOptions(String[] options)
/*  33:    */     throws Exception
/*  34:    */   {
/*  35: 81 */     super.setOptions(options);
/*  36:    */     
/*  37: 83 */     String tmpStr = Utils.getOption("L", options);
/*  38: 84 */     if (tmpStr.length() != 0) {
/*  39: 85 */       setLowerRange(Double.parseDouble(tmpStr));
/*  40:    */     } else {
/*  41: 87 */       setLowerRange(0.0D);
/*  42:    */     }
/*  43: 90 */     tmpStr = Utils.getOption("U", options);
/*  44: 91 */     if (tmpStr.length() != 0) {
/*  45: 92 */       setUpperRange(Double.parseDouble(tmpStr));
/*  46:    */     } else {
/*  47: 94 */       setUpperRange(1.0D);
/*  48:    */     }
/*  49:    */   }
/*  50:    */   
/*  51:    */   public String[] getOptions()
/*  52:    */   {
/*  53:106 */     Vector<String> result = new Vector();
/*  54:    */     
/*  55:108 */     Collections.addAll(result, super.getOptions());
/*  56:    */     
/*  57:110 */     result.add("-L");
/*  58:111 */     result.add("" + this.m_LowerRange);
/*  59:    */     
/*  60:113 */     result.add("-U");
/*  61:114 */     result.add("" + this.m_UpperRange);
/*  62:    */     
/*  63:116 */     return (String[])result.toArray(new String[result.size()]);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public double getLowerRange()
/*  67:    */   {
/*  68:125 */     return this.m_LowerRange;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setLowerRange(double value)
/*  72:    */   {
/*  73:135 */     this.m_LowerRange = value;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String lowerRangeTipText()
/*  77:    */   {
/*  78:145 */     return "The lower range.";
/*  79:    */   }
/*  80:    */   
/*  81:    */   public double getUpperRange()
/*  82:    */   {
/*  83:154 */     return this.m_UpperRange;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setUpperRange(double value)
/*  87:    */   {
/*  88:164 */     this.m_UpperRange = value;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public String upperRangeTipText()
/*  92:    */   {
/*  93:174 */     return "The upper range.";
/*  94:    */   }
/*  95:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.generators.RandomizableRangedGenerator
 * JD-Core Version:    0.7.0.1
 */