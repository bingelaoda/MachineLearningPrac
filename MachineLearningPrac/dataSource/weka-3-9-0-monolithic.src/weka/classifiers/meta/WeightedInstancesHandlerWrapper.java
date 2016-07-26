/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.Random;
/*   9:    */ import java.util.Vector;
/*  10:    */ import weka.classifiers.Classifier;
/*  11:    */ import weka.classifiers.RandomizableSingleClassifierEnhancer;
/*  12:    */ import weka.core.Capabilities;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.ResampleUtils;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.Utils;
/*  19:    */ import weka.core.WeightedInstancesHandler;
/*  20:    */ 
/*  21:    */ public class WeightedInstancesHandlerWrapper
/*  22:    */   extends RandomizableSingleClassifierEnhancer
/*  23:    */   implements WeightedInstancesHandler
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 2980789213434466135L;
/*  26:    */   public static final String FORCE_RESAMPLE_WITH_WEIGHTS = "force-resample-with-weights";
/*  27:102 */   protected boolean m_ForceResampleWithWeights = false;
/*  28:    */   
/*  29:    */   public String globalInfo()
/*  30:    */   {
/*  31:110 */     return "Generic wrapper around any classifier to enable weighted instances support.\nUses resampling with weights if the base classifier is not implementing the " + WeightedInstancesHandler.class.getName() + " interface and there " + "are instance weights other than 1.0 present. By default, " + "the training data is passed through to the base classifier if it can handle " + "instance weights. However, it is possible to force the use of resampling " + "with weights as well.";
/*  32:    */   }
/*  33:    */   
/*  34:    */   public Enumeration<Option> listOptions()
/*  35:    */   {
/*  36:128 */     Vector<Option> result = new Vector();
/*  37:    */     
/*  38:130 */     result.addElement(new Option("\tForces resampling of weights, regardless of whether\n\tbase classifier handles instance weights", "force-resample-with-weights", 0, "-force-resample-with-weights"));
/*  39:    */     
/*  40:    */ 
/*  41:    */ 
/*  42:    */ 
/*  43:135 */     result.addAll(Collections.list(super.listOptions()));
/*  44:    */     
/*  45:137 */     return result.elements();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setOptions(String[] options)
/*  49:    */     throws Exception
/*  50:    */   {
/*  51:147 */     setForceResampleWithWeights(Utils.getFlag("force-resample-with-weights", options));
/*  52:148 */     super.setOptions(options);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public String[] getOptions()
/*  56:    */   {
/*  57:159 */     List<String> result = new ArrayList();
/*  58:161 */     if (getForceResampleWithWeights()) {
/*  59:162 */       result.add("-force-resample-with-weights");
/*  60:    */     }
/*  61:164 */     Collections.addAll(result, super.getOptions());
/*  62:    */     
/*  63:166 */     return (String[])result.toArray(new String[result.size()]);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public boolean getForceResampleWithWeights()
/*  67:    */   {
/*  68:175 */     return this.m_ForceResampleWithWeights;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setForceResampleWithWeights(boolean value)
/*  72:    */   {
/*  73:184 */     this.m_ForceResampleWithWeights = value;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String forceResampleWithWeightsTipText()
/*  77:    */   {
/*  78:194 */     return "If enabled, forces the data to be resampled with weights, regardless of whether the base classifier can handle instance weights.";
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void buildClassifier(Instances data)
/*  82:    */     throws Exception
/*  83:    */   {
/*  84:208 */     getCapabilities().testWithFail(data);
/*  85:    */     
/*  86:210 */     boolean resample = (getForceResampleWithWeights()) || ((!(this.m_Classifier instanceof WeightedInstancesHandler)) && (ResampleUtils.hasInstanceWeights(data)));
/*  87:213 */     if (resample)
/*  88:    */     {
/*  89:214 */       if (getDebug()) {
/*  90:215 */         System.err.println(getClass().getName() + ": resampling training data");
/*  91:    */       }
/*  92:216 */       data = data.resampleWithWeights(new Random(this.m_Seed));
/*  93:    */     }
/*  94:219 */     this.m_Classifier.buildClassifier(data);
/*  95:    */   }
/*  96:    */   
/*  97:    */   public double[] distributionForInstance(Instance instance)
/*  98:    */     throws Exception
/*  99:    */   {
/* 100:231 */     return this.m_Classifier.distributionForInstance(instance);
/* 101:    */   }
/* 102:    */   
/* 103:    */   public double classifyInstance(Instance instance)
/* 104:    */     throws Exception
/* 105:    */   {
/* 106:244 */     return this.m_Classifier.classifyInstance(instance);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public String toString()
/* 110:    */   {
/* 111:255 */     StringBuilder result = new StringBuilder();
/* 112:256 */     result.append(getClass().getSimpleName()).append("\n");
/* 113:257 */     result.append(getClass().getSimpleName().replaceAll(".", "=")).append("\n\n");
/* 114:258 */     result.append("Force resample with weights: " + getForceResampleWithWeights() + "\n");
/* 115:259 */     result.append("Base classifier:\n");
/* 116:260 */     result.append("- command-line: " + Utils.toCommandLine(this.m_Classifier) + "\n");
/* 117:261 */     result.append("- handles instance weights: " + (this.m_Classifier instanceof WeightedInstancesHandler) + "\n\n");
/* 118:262 */     result.append(this.m_Classifier.toString());
/* 119:    */     
/* 120:264 */     return result.toString();
/* 121:    */   }
/* 122:    */   
/* 123:    */   public String getRevision()
/* 124:    */   {
/* 125:273 */     return RevisionUtils.extract("$Revision: 12226 $");
/* 126:    */   }
/* 127:    */   
/* 128:    */   public static void main(String[] args)
/* 129:    */   {
/* 130:282 */     runClassifier(new RandomSubSpace(), args);
/* 131:    */   }
/* 132:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.WeightedInstancesHandlerWrapper
 * JD-Core Version:    0.7.0.1
 */