/*   1:    */ package weka.classifiers.meta.generators;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Option;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public class MixedGaussianGenerator
/*  11:    */   extends RandomizableDistributionGenerator
/*  12:    */   implements NumericAttributeGenerator
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 1516470615315381362L;
/*  15: 87 */   protected double m_Distance = 3.0D;
/*  16: 93 */   protected boolean m_DistanceAbsolute = false;
/*  17:    */   
/*  18:    */   public String globalInfo()
/*  19:    */   {
/*  20:102 */     return "A mixed Gaussian artificial data generator.\n\nThis generator only has two Gaussians, each sitting 3 standard deviations (by default) away from the mean of the main distribution.  Each model has half of the probability.  The idea is that the two sub-models form a boundary either side of the main distribution.";
/*  21:    */   }
/*  22:    */   
/*  23:    */   public Enumeration<Option> listOptions()
/*  24:    */   {
/*  25:117 */     Vector<Option> result = new Vector();
/*  26:    */     
/*  27:119 */     result.addAll(Collections.list(super.listOptions()));
/*  28:    */     
/*  29:121 */     result.addElement(new Option("\tSets the difference between the mean and what will be used\n\ton the lower and higher distributions for the generator.\t(default: 3)", "di", 1, "-di <distance>"));
/*  30:    */     
/*  31:    */ 
/*  32:    */ 
/*  33:    */ 
/*  34:126 */     result.addElement(new Option("\tIf set, the generator will use the absolute value of the\n\tdifference. If not set, it will multiply the difference by\n\tthe standard deviation.", "da", 0, "-da"));
/*  35:    */     
/*  36:    */ 
/*  37:    */ 
/*  38:    */ 
/*  39:131 */     return result.elements();
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setOptions(String[] options)
/*  43:    */     throws Exception
/*  44:    */   {
/*  45:180 */     super.setOptions(options);
/*  46:    */     
/*  47:182 */     setDistanceAbsolute(Utils.getFlag("da", options));
/*  48:    */     
/*  49:184 */     String tmpStr = Utils.getOption("di", options);
/*  50:185 */     if (tmpStr.length() != 0) {
/*  51:186 */       setDistance(Double.parseDouble(tmpStr));
/*  52:    */     } else {
/*  53:188 */       setDistance(3.0D);
/*  54:    */     }
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String[] getOptions()
/*  58:    */   {
/*  59:200 */     Vector<String> result = new Vector();
/*  60:    */     
/*  61:202 */     Collections.addAll(result, super.getOptions());
/*  62:204 */     if (getDistanceAbsolute()) {
/*  63:205 */       result.add("-da");
/*  64:    */     }
/*  65:208 */     result.add("-di");
/*  66:209 */     result.add("" + this.m_Distance);
/*  67:    */     
/*  68:211 */     return (String[])result.toArray(new String[result.size()]);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public double getDistance()
/*  72:    */   {
/*  73:223 */     return this.m_Distance;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setDistance(double diff)
/*  77:    */   {
/*  78:233 */     this.m_Distance = diff;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public String distanceTipText()
/*  82:    */   {
/*  83:243 */     return "The difference between the main distribution and the models.";
/*  84:    */   }
/*  85:    */   
/*  86:    */   public boolean getDistanceAbsolute()
/*  87:    */   {
/*  88:253 */     return this.m_DistanceAbsolute;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setDistanceAbsolute(boolean newdiff)
/*  92:    */   {
/*  93:263 */     this.m_DistanceAbsolute = newdiff;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public String distanceAbsoluteTipText()
/*  97:    */   {
/*  98:273 */     return "If true, then the distance is absolute.";
/*  99:    */   }
/* 100:    */   
/* 101:    */   public double generate()
/* 102:    */   {
/* 103:283 */     double difference = this.m_Distance;
/* 104:284 */     if (!this.m_DistanceAbsolute) {
/* 105:285 */       difference = this.m_StandardDeviation * this.m_Distance;
/* 106:    */     }
/* 107:288 */     if (this.m_Random.nextBoolean())
/* 108:    */     {
/* 109:290 */       double gaussian = this.m_Random.nextGaussian();
/* 110:291 */       double value = this.m_Mean - difference + gaussian * this.m_StandardDeviation;
/* 111:292 */       return value;
/* 112:    */     }
/* 113:295 */     double gaussian = this.m_Random.nextGaussian();
/* 114:296 */     double value = this.m_Mean + difference + gaussian * this.m_StandardDeviation;
/* 115:297 */     return value;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public double getProbabilityOf(double valuex)
/* 119:    */   {
/* 120:310 */     double difference = this.m_Distance;
/* 121:311 */     if (!this.m_DistanceAbsolute) {
/* 122:312 */       difference = this.m_StandardDeviation * this.m_Distance;
/* 123:    */     }
/* 124:315 */     double prob1 = 0.5D * getProbability(valuex, this.m_Mean - difference, this.m_StandardDeviation);
/* 125:    */     
/* 126:317 */     double prob2 = 0.5D * getProbability(valuex, this.m_Mean + difference, this.m_StandardDeviation);
/* 127:    */     
/* 128:319 */     return prob1 + prob2;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public double getProbability(double valuex, double mean, double stddev)
/* 132:    */   {
/* 133:333 */     double twopisqrt = Math.sqrt(6.283185307179586D);
/* 134:334 */     double left = 1.0D / (stddev * twopisqrt);
/* 135:335 */     double diffsquared = Math.pow(valuex - mean, 2.0D);
/* 136:336 */     double bottomright = 2.0D * Math.pow(stddev, 2.0D);
/* 137:337 */     double brackets = -1.0D * (diffsquared / bottomright);
/* 138:    */     
/* 139:339 */     double probx = left * Math.exp(brackets);
/* 140:    */     
/* 141:341 */     return probx;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public double getLogProbabilityOf(double valuex)
/* 145:    */   {
/* 146:352 */     return Math.log(getProbabilityOf(valuex));
/* 147:    */   }
/* 148:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.generators.MixedGaussianGenerator
 * JD-Core Version:    0.7.0.1
 */