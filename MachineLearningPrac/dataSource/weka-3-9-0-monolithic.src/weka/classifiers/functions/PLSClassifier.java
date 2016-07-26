/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Random;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.classifiers.RandomizableClassifier;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SelectedTag;
/*  17:    */ import weka.core.Tag;
/*  18:    */ import weka.core.Utils;
/*  19:    */ import weka.core.WeightedInstancesHandler;
/*  20:    */ import weka.filters.Filter;
/*  21:    */ import weka.filters.supervised.attribute.PLSFilter;
/*  22:    */ 
/*  23:    */ public class PLSClassifier
/*  24:    */   extends RandomizableClassifier
/*  25:    */   implements WeightedInstancesHandler
/*  26:    */ {
/*  27:    */   private static final long serialVersionUID = 4819775160590973256L;
/*  28:113 */   protected PLSFilter m_Filter = new PLSFilter();
/*  29:116 */   protected PLSFilter m_ActualFilter = null;
/*  30:    */   
/*  31:    */   public String globalInfo()
/*  32:    */   {
/*  33:125 */     return "A wrapper classifier for the PLSFilter, utilizing the PLSFilter's ability to perform predictions.";
/*  34:    */   }
/*  35:    */   
/*  36:    */   public Enumeration<Option> listOptions()
/*  37:    */   {
/*  38:137 */     Vector<Option> result = new Vector();
/*  39:    */     
/*  40:139 */     result.addElement(new Option("\tThe PLS filter to use. Full classname of filter to include, \tfollowed by scheme options.\n\t(default: weka.filters.supervised.attribute.PLSFilter)", "filter", 1, "-filter <filter specification>"));
/*  41:    */     
/*  42:    */ 
/*  43:    */ 
/*  44:    */ 
/*  45:    */ 
/*  46:145 */     result.addAll(Collections.list(super.listOptions()));
/*  47:147 */     if ((getFilter() instanceof OptionHandler))
/*  48:    */     {
/*  49:148 */       result.addElement(new Option("", "", 0, "\nOptions specific to filter " + getFilter().getClass().getName() + " ('-filter'):"));
/*  50:    */       
/*  51:    */ 
/*  52:151 */       result.addAll(Collections.list(getFilter().listOptions()));
/*  53:    */     }
/*  54:155 */     return result.elements();
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String[] getOptions()
/*  58:    */   {
/*  59:166 */     Vector<String> result = new Vector();
/*  60:    */     
/*  61:168 */     result.add("-filter");
/*  62:169 */     if ((getFilter() instanceof OptionHandler)) {
/*  63:170 */       result.add(getFilter().getClass().getName() + " " + Utils.joinOptions(getFilter().getOptions()));
/*  64:    */     } else {
/*  65:173 */       result.add(getFilter().getClass().getName());
/*  66:    */     }
/*  67:176 */     Collections.addAll(result, super.getOptions());
/*  68:    */     
/*  69:178 */     return (String[])result.toArray(new String[result.size()]);
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setOptions(String[] options)
/*  73:    */     throws Exception
/*  74:    */   {
/*  75:247 */     String tmpStr = Utils.getOption("filter", options);
/*  76:248 */     String[] tmpOptions = Utils.splitOptions(tmpStr);
/*  77:249 */     if (tmpOptions.length != 0)
/*  78:    */     {
/*  79:250 */       tmpStr = tmpOptions[0];
/*  80:251 */       tmpOptions[0] = "";
/*  81:252 */       setFilter((Filter)Utils.forName(Filter.class, tmpStr, tmpOptions));
/*  82:    */     }
/*  83:255 */     super.setOptions(options);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public String filterTipText()
/*  87:    */   {
/*  88:265 */     return "The PLS filter to be used (only used for setup).";
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setFilter(Filter value)
/*  92:    */     throws Exception
/*  93:    */   {
/*  94:275 */     if (!(value instanceof PLSFilter)) {
/*  95:276 */       throw new Exception("Filter has to be PLSFilter!");
/*  96:    */     }
/*  97:278 */     this.m_Filter = ((PLSFilter)value);
/*  98:    */   }
/*  99:    */   
/* 100:    */   public Filter getFilter()
/* 101:    */   {
/* 102:288 */     return this.m_Filter;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public Capabilities getCapabilities()
/* 106:    */   {
/* 107:298 */     Capabilities result = getFilter().getCapabilities();
/* 108:    */     
/* 109:    */ 
/* 110:301 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 111:    */     
/* 112:    */ 
/* 113:304 */     result.setMinimumNumberInstances(1);
/* 114:    */     
/* 115:306 */     return result;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void buildClassifier(Instances data)
/* 119:    */     throws Exception
/* 120:    */   {
/* 121:318 */     boolean resample = false;
/* 122:319 */     for (int i = 0; i < data.numInstances(); i++) {
/* 123:320 */       if (data.instance(i).weight() != 1.0D)
/* 124:    */       {
/* 125:321 */         resample = true;
/* 126:322 */         break;
/* 127:    */       }
/* 128:    */     }
/* 129:325 */     if (resample)
/* 130:    */     {
/* 131:326 */       if (getDebug()) {
/* 132:327 */         System.err.println(getClass().getName() + ": resampling training data");
/* 133:    */       }
/* 134:328 */       data = data.resampleWithWeights(new Random(this.m_Seed));
/* 135:    */     }
/* 136:332 */     getCapabilities().testWithFail(data);
/* 137:    */     
/* 138:    */ 
/* 139:335 */     data = new Instances(data);
/* 140:336 */     data.deleteWithMissingClass();
/* 141:    */     
/* 142:    */ 
/* 143:339 */     this.m_ActualFilter = ((PLSFilter)Filter.makeCopy(this.m_Filter));
/* 144:340 */     this.m_ActualFilter.setPerformPrediction(false);
/* 145:341 */     this.m_ActualFilter.setInputFormat(data);
/* 146:342 */     Filter.useFilter(data, this.m_ActualFilter);
/* 147:343 */     this.m_ActualFilter.setPerformPrediction(true);
/* 148:    */   }
/* 149:    */   
/* 150:    */   public double classifyInstance(Instance instance)
/* 151:    */     throws Exception
/* 152:    */   {
/* 153:360 */     this.m_ActualFilter.input(instance);
/* 154:361 */     this.m_ActualFilter.batchFinished();
/* 155:362 */     Instance pred = this.m_ActualFilter.output();
/* 156:363 */     double result = pred.classValue();
/* 157:    */     
/* 158:365 */     return result;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public String toString()
/* 162:    */   {
/* 163:377 */     String result = getClass().getName() + "\n" + getClass().getName().replaceAll(".", "=") + "\n\n";
/* 164:    */     
/* 165:379 */     result = result + "# Components..........: " + this.m_Filter.getNumComponents() + "\n";
/* 166:380 */     result = result + "Algorithm.............: " + this.m_Filter.getAlgorithm().getSelectedTag().getReadable() + "\n";
/* 167:    */     
/* 168:382 */     result = result + "Replace missing values: " + (this.m_Filter.getReplaceMissing() ? "yes" : "no") + "\n";
/* 169:    */     
/* 170:384 */     result = result + "Preprocessing.........: " + this.m_Filter.getPreprocessing().getSelectedTag().getReadable() + "\n";
/* 171:    */     
/* 172:    */ 
/* 173:387 */     return result;
/* 174:    */   }
/* 175:    */   
/* 176:    */   public String getRevision()
/* 177:    */   {
/* 178:397 */     return RevisionUtils.extract("$Revision: 12228 $");
/* 179:    */   }
/* 180:    */   
/* 181:    */   public static void main(String[] args)
/* 182:    */   {
/* 183:406 */     runClassifier(new PLSClassifier(), args);
/* 184:    */   }
/* 185:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.PLSClassifier
 * JD-Core Version:    0.7.0.1
 */