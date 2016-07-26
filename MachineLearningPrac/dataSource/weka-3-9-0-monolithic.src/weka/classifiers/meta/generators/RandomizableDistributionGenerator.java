/*   1:    */ package weka.classifiers.meta.generators;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Option;
/*   7:    */ import weka.core.Utils;
/*   8:    */ 
/*   9:    */ public abstract class RandomizableDistributionGenerator
/*  10:    */   extends RandomizableGenerator
/*  11:    */   implements Mean
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = 955762136858704289L;
/*  14: 44 */   protected double m_Mean = 0.0D;
/*  15: 47 */   protected double m_StandardDeviation = 1.0D;
/*  16:    */   
/*  17:    */   public Enumeration<Option> listOptions()
/*  18:    */   {
/*  19: 57 */     Vector<Option> result = new Vector();
/*  20:    */     
/*  21: 59 */     result.addAll(Collections.list(super.listOptions()));
/*  22:    */     
/*  23: 61 */     result.addElement(new Option("\tSets the mean of the generator\n\t(default: 0)", "M", 1, "-M <num>"));
/*  24:    */     
/*  25:    */ 
/*  26: 64 */     result.addElement(new Option("\tSets the standard deviation of the generator\n\t(default: 1)", "SD", 1, "-SD <num>"));
/*  27:    */     
/*  28:    */ 
/*  29:    */ 
/*  30: 68 */     return result.elements();
/*  31:    */   }
/*  32:    */   
/*  33:    */   public void setOptions(String[] options)
/*  34:    */     throws Exception
/*  35:    */   {
/*  36: 82 */     super.setOptions(options);
/*  37:    */     
/*  38: 84 */     String tmpStr = Utils.getOption("M", options);
/*  39: 85 */     if (tmpStr.length() != 0) {
/*  40: 86 */       setMean(Double.parseDouble(tmpStr));
/*  41:    */     } else {
/*  42: 88 */       setMean(0.0D);
/*  43:    */     }
/*  44: 91 */     tmpStr = Utils.getOption("SD", options);
/*  45: 92 */     if (tmpStr.length() != 0) {
/*  46: 93 */       setStandardDeviation(Double.parseDouble(tmpStr));
/*  47:    */     } else {
/*  48: 95 */       setStandardDeviation(1.0D);
/*  49:    */     }
/*  50:    */   }
/*  51:    */   
/*  52:    */   public String[] getOptions()
/*  53:    */   {
/*  54:107 */     Vector<String> result = new Vector();
/*  55:    */     
/*  56:109 */     Collections.addAll(result, super.getOptions());
/*  57:    */     
/*  58:111 */     result.add("-M");
/*  59:112 */     result.add("" + this.m_Mean);
/*  60:    */     
/*  61:114 */     result.add("-SD");
/*  62:115 */     result.add("" + this.m_StandardDeviation);
/*  63:    */     
/*  64:117 */     return (String[])result.toArray(new String[result.size()]);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public double getMean()
/*  68:    */   {
/*  69:126 */     return this.m_Mean;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setMean(double value)
/*  73:    */   {
/*  74:136 */     this.m_Mean = value;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public String meanTipText()
/*  78:    */   {
/*  79:146 */     return "The mean of the underlying distribution.";
/*  80:    */   }
/*  81:    */   
/*  82:    */   public double getStandardDeviation()
/*  83:    */   {
/*  84:155 */     return this.m_StandardDeviation;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setStandardDeviation(double value)
/*  88:    */   {
/*  89:165 */     if (value > 0.0D) {
/*  90:166 */       this.m_StandardDeviation = value;
/*  91:    */     } else {
/*  92:168 */       this.m_StandardDeviation = 0.01D;
/*  93:    */     }
/*  94:    */   }
/*  95:    */   
/*  96:    */   public String standardDeviationTipText()
/*  97:    */   {
/*  98:179 */     return "The standard deviation of the underlying distribution.";
/*  99:    */   }
/* 100:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.generators.RandomizableDistributionGenerator
 * JD-Core Version:    0.7.0.1
 */