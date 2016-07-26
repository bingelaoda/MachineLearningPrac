/*   1:    */ package weka.clusterers;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Capabilities.Capability;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.OptionHandler;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Utils;
/*  15:    */ import weka.filters.AllFilter;
/*  16:    */ import weka.filters.Filter;
/*  17:    */ import weka.filters.SupervisedFilter;
/*  18:    */ 
/*  19:    */ public class FilteredClusterer
/*  20:    */   extends SingleClustererEnhancer
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = 1420005943163412943L;
/*  23:    */   protected Filter m_Filter;
/*  24:    */   protected Instances m_FilteredInstances;
/*  25:    */   
/*  26:    */   public FilteredClusterer()
/*  27:    */   {
/*  28:114 */     this.m_Clusterer = new SimpleKMeans();
/*  29:115 */     this.m_Filter = new AllFilter();
/*  30:    */   }
/*  31:    */   
/*  32:    */   public String globalInfo()
/*  33:    */   {
/*  34:125 */     return "Class for running an arbitrary clusterer on data that has been passed through an arbitrary filter. Like the clusterer, the structure of the filter is based exclusively on the training data and test instances will be processed by the filter without changing their structure.";
/*  35:    */   }
/*  36:    */   
/*  37:    */   protected String defaultFilterString()
/*  38:    */   {
/*  39:137 */     return AllFilter.class.getName();
/*  40:    */   }
/*  41:    */   
/*  42:    */   public Enumeration<Option> listOptions()
/*  43:    */   {
/*  44:147 */     Vector<Option> result = new Vector();
/*  45:    */     
/*  46:149 */     result.addElement(new Option("\tFull class name of filter to use, followed\n\tby filter options.\n\teg: \"weka.filters.unsupervised.attribute.Remove -V -R 1,2\"\n(default: " + defaultFilterString() + ")", "F", 1, "-F <filter specification>"));
/*  47:    */     
/*  48:    */ 
/*  49:    */ 
/*  50:    */ 
/*  51:    */ 
/*  52:    */ 
/*  53:156 */     result.addAll(Collections.list(super.listOptions()));
/*  54:    */     
/*  55:158 */     return result.elements();
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setOptions(String[] options)
/*  59:    */     throws Exception
/*  60:    */   {
/*  61:218 */     String tmpStr = Utils.getOption('F', options);
/*  62:219 */     if (tmpStr.length() > 0)
/*  63:    */     {
/*  64:220 */       String[] tmpOptions = Utils.splitOptions(tmpStr);
/*  65:221 */       if (tmpOptions.length == 0) {
/*  66:222 */         throw new IllegalArgumentException("Invalid filter specification string");
/*  67:    */       }
/*  68:225 */       tmpStr = tmpOptions[0];
/*  69:226 */       tmpOptions[0] = "";
/*  70:227 */       setFilter((Filter)Utils.forName(Filter.class, tmpStr, tmpOptions));
/*  71:    */     }
/*  72:    */     else
/*  73:    */     {
/*  74:229 */       setFilter(new AllFilter());
/*  75:    */     }
/*  76:232 */     super.setOptions(options);
/*  77:    */     
/*  78:234 */     Utils.checkForRemainingOptions(options);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public String[] getOptions()
/*  82:    */   {
/*  83:244 */     Vector<String> result = new Vector();
/*  84:    */     
/*  85:246 */     result.addElement("-F");
/*  86:247 */     result.addElement(getFilterSpec());
/*  87:    */     
/*  88:249 */     Collections.addAll(result, super.getOptions());
/*  89:    */     
/*  90:251 */     return (String[])result.toArray(new String[result.size()]);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String filterTipText()
/*  94:    */   {
/*  95:261 */     return "The filter to be used.";
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void setFilter(Filter filter)
/*  99:    */   {
/* 100:270 */     this.m_Filter = filter;
/* 101:272 */     if ((this.m_Filter instanceof SupervisedFilter)) {
/* 102:273 */       System.out.println("WARNING: you are using a supervised filter, which will leak information about the class attribute!");
/* 103:    */     }
/* 104:    */   }
/* 105:    */   
/* 106:    */   public Filter getFilter()
/* 107:    */   {
/* 108:285 */     return this.m_Filter;
/* 109:    */   }
/* 110:    */   
/* 111:    */   protected String getFilterSpec()
/* 112:    */   {
/* 113:298 */     Filter filter = getFilter();
/* 114:299 */     String result = filter.getClass().getName();
/* 115:301 */     if ((filter instanceof OptionHandler)) {
/* 116:302 */       result = result + " " + Utils.joinOptions(filter.getOptions());
/* 117:    */     }
/* 118:305 */     return result;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public Capabilities getCapabilities()
/* 122:    */   {
/* 123:    */     Capabilities result;
/* 124:317 */     if (getFilter() == null)
/* 125:    */     {
/* 126:318 */       Capabilities result = super.getCapabilities();
/* 127:319 */       result.disableAll();
/* 128:320 */       result.enable(Capabilities.Capability.NO_CLASS);
/* 129:    */     }
/* 130:    */     else
/* 131:    */     {
/* 132:322 */       result = getFilter().getCapabilities();
/* 133:    */     }
/* 134:326 */     for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/* 135:327 */       result.enableDependency(cap);
/* 136:    */     }
/* 137:330 */     return result;
/* 138:    */   }
/* 139:    */   
/* 140:    */   public void buildClusterer(Instances data)
/* 141:    */     throws Exception
/* 142:    */   {
/* 143:341 */     if (this.m_Clusterer == null) {
/* 144:342 */       throw new Exception("No base clusterer has been set!");
/* 145:    */     }
/* 146:346 */     if (data.classIndex() > -1)
/* 147:    */     {
/* 148:347 */       data = new Instances(data);
/* 149:348 */       data.deleteWithMissingClass();
/* 150:    */     }
/* 151:351 */     this.m_Filter.setInputFormat(data);
/* 152:352 */     data = Filter.useFilter(data, this.m_Filter);
/* 153:    */     
/* 154:    */ 
/* 155:355 */     getClusterer().getCapabilities().testWithFail(data);
/* 156:    */     
/* 157:357 */     this.m_FilteredInstances = data.stringFreeStructure();
/* 158:358 */     this.m_Clusterer.buildClusterer(data);
/* 159:    */   }
/* 160:    */   
/* 161:    */   public double[] distributionForInstance(Instance instance)
/* 162:    */     throws Exception
/* 163:    */   {
/* 164:371 */     if (this.m_Filter.numPendingOutput() > 0) {
/* 165:372 */       throw new Exception("Filter output queue not empty!");
/* 166:    */     }
/* 167:375 */     if (!this.m_Filter.input(instance)) {
/* 168:376 */       throw new Exception("Filter didn't make the test instance immediately available!");
/* 169:    */     }
/* 170:380 */     this.m_Filter.batchFinished();
/* 171:381 */     Instance newInstance = this.m_Filter.output();
/* 172:    */     
/* 173:383 */     return this.m_Clusterer.distributionForInstance(newInstance);
/* 174:    */   }
/* 175:    */   
/* 176:    */   public String toString()
/* 177:    */   {
/* 178:    */     String result;
/* 179:    */     String result;
/* 180:395 */     if (this.m_FilteredInstances == null) {
/* 181:396 */       result = "FilteredClusterer: No model built yet.";
/* 182:    */     } else {
/* 183:398 */       result = "FilteredClusterer using " + getClustererSpec() + " on data filtered through " + getFilterSpec() + "\n\nFiltered Header\n" + this.m_FilteredInstances.toString() + "\n\nClusterer Model\n" + this.m_Clusterer.toString();
/* 184:    */     }
/* 185:404 */     return result;
/* 186:    */   }
/* 187:    */   
/* 188:    */   public String getRevision()
/* 189:    */   {
/* 190:414 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 191:    */   }
/* 192:    */   
/* 193:    */   public static void main(String[] args)
/* 194:    */   {
/* 195:423 */     runClusterer(new FilteredClusterer(), args);
/* 196:    */   }
/* 197:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.FilteredClusterer
 * JD-Core Version:    0.7.0.1
 */