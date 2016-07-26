/*   1:    */ package weka.classifiers.rules;
/*   2:    */ 
/*   3:    */ import weka.classifiers.AbstractClassifier;
/*   4:    */ import weka.classifiers.Sourcable;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.RevisionUtils;
/*  11:    */ import weka.core.Utils;
/*  12:    */ import weka.core.WeightedInstancesHandler;
/*  13:    */ 
/*  14:    */ public class ZeroR
/*  15:    */   extends AbstractClassifier
/*  16:    */   implements WeightedInstancesHandler, Sourcable
/*  17:    */ {
/*  18:    */   static final long serialVersionUID = 48055541465867954L;
/*  19:    */   private double m_ClassValue;
/*  20:    */   private double[] m_Counts;
/*  21:    */   private Attribute m_Class;
/*  22:    */   
/*  23:    */   public String globalInfo()
/*  24:    */   {
/*  25: 79 */     return "Class for building and using a 0-R classifier. Predicts the mean (for a numeric class) or the mode (for a nominal class).";
/*  26:    */   }
/*  27:    */   
/*  28:    */   public Capabilities getCapabilities()
/*  29:    */   {
/*  30: 90 */     Capabilities result = super.getCapabilities();
/*  31: 91 */     result.disableAll();
/*  32:    */     
/*  33:    */ 
/*  34: 94 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  35: 95 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  36: 96 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  37: 97 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/*  38: 98 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/*  39: 99 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  40:    */     
/*  41:    */ 
/*  42:102 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  43:103 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  44:104 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  45:105 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  46:    */     
/*  47:    */ 
/*  48:108 */     result.setMinimumNumberInstances(0);
/*  49:    */     
/*  50:110 */     return result;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void buildClassifier(Instances instances)
/*  54:    */     throws Exception
/*  55:    */   {
/*  56:122 */     getCapabilities().testWithFail(instances);
/*  57:    */     
/*  58:124 */     double sumOfWeights = 0.0D;
/*  59:    */     
/*  60:126 */     this.m_Class = instances.classAttribute();
/*  61:127 */     this.m_ClassValue = 0.0D;
/*  62:128 */     switch (instances.classAttribute().type())
/*  63:    */     {
/*  64:    */     case 0: 
/*  65:130 */       this.m_Counts = null;
/*  66:131 */       break;
/*  67:    */     case 1: 
/*  68:133 */       this.m_Counts = new double[instances.numClasses()];
/*  69:134 */       for (int i = 0; i < this.m_Counts.length; i++) {
/*  70:135 */         this.m_Counts[i] = 1.0D;
/*  71:    */       }
/*  72:137 */       sumOfWeights = instances.numClasses();
/*  73:    */     }
/*  74:140 */     for (Instance instance : instances)
/*  75:    */     {
/*  76:141 */       double classValue = instance.classValue();
/*  77:142 */       if (!Utils.isMissingValue(classValue))
/*  78:    */       {
/*  79:143 */         if (instances.classAttribute().isNominal()) {
/*  80:144 */           this.m_Counts[((int)classValue)] += instance.weight();
/*  81:    */         } else {
/*  82:146 */           this.m_ClassValue += instance.weight() * classValue;
/*  83:    */         }
/*  84:148 */         sumOfWeights += instance.weight();
/*  85:    */       }
/*  86:    */     }
/*  87:151 */     if (instances.classAttribute().isNumeric())
/*  88:    */     {
/*  89:152 */       if (Utils.gr(sumOfWeights, 0.0D)) {
/*  90:153 */         this.m_ClassValue /= sumOfWeights;
/*  91:    */       }
/*  92:    */     }
/*  93:    */     else
/*  94:    */     {
/*  95:156 */       this.m_ClassValue = Utils.maxIndex(this.m_Counts);
/*  96:157 */       Utils.normalize(this.m_Counts, sumOfWeights);
/*  97:    */     }
/*  98:    */   }
/*  99:    */   
/* 100:    */   public double classifyInstance(Instance instance)
/* 101:    */   {
/* 102:170 */     return this.m_ClassValue;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public double[] distributionForInstance(Instance instance)
/* 106:    */     throws Exception
/* 107:    */   {
/* 108:183 */     if (this.m_Counts == null)
/* 109:    */     {
/* 110:184 */       double[] result = new double[1];
/* 111:185 */       result[0] = this.m_ClassValue;
/* 112:186 */       return result;
/* 113:    */     }
/* 114:188 */     return (double[])this.m_Counts.clone();
/* 115:    */   }
/* 116:    */   
/* 117:    */   public String toSource(String className)
/* 118:    */     throws Exception
/* 119:    */   {
/* 120:215 */     StringBuffer result = new StringBuffer();
/* 121:    */     
/* 122:217 */     result.append("class " + className + " {\n");
/* 123:218 */     result.append("  public static double classify(Object[] i) {\n");
/* 124:219 */     if (this.m_Counts != null) {
/* 125:220 */       result.append("    // always predicts label '" + this.m_Class.value((int)this.m_ClassValue) + "'\n");
/* 126:    */     }
/* 127:223 */     result.append("    return " + this.m_ClassValue + ";\n");
/* 128:224 */     result.append("  }\n");
/* 129:225 */     result.append("}\n");
/* 130:    */     
/* 131:227 */     return result.toString();
/* 132:    */   }
/* 133:    */   
/* 134:    */   public String toString()
/* 135:    */   {
/* 136:238 */     if (this.m_Class == null) {
/* 137:239 */       return "ZeroR: No model built yet.";
/* 138:    */     }
/* 139:241 */     if (this.m_Counts == null) {
/* 140:242 */       return "ZeroR predicts class value: " + this.m_ClassValue;
/* 141:    */     }
/* 142:244 */     return "ZeroR predicts class value: " + this.m_Class.value((int)this.m_ClassValue);
/* 143:    */   }
/* 144:    */   
/* 145:    */   public String getRevision()
/* 146:    */   {
/* 147:255 */     return RevisionUtils.extract("$Revision: 12024 $");
/* 148:    */   }
/* 149:    */   
/* 150:    */   public static void main(String[] argv)
/* 151:    */   {
/* 152:264 */     runClassifier(new ZeroR(), argv);
/* 153:    */   }
/* 154:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.rules.ZeroR
 * JD-Core Version:    0.7.0.1
 */