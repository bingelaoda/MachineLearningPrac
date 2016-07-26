/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.Option;
/*  10:    */ import weka.core.OptionHandler;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.Utils;
/*  13:    */ import weka.filters.Filter;
/*  14:    */ import weka.filters.MultiFilter;
/*  15:    */ import weka.filters.supervised.attribute.Discretize;
/*  16:    */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*  17:    */ 
/*  18:    */ public class FilteredAssociator
/*  19:    */   extends SingleAssociatorEnhancer
/*  20:    */   implements AssociationRulesProducer
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = -4523450618538717400L;
/*  23:    */   protected Filter m_Filter;
/*  24:    */   protected Instances m_FilteredInstances;
/*  25:    */   protected int m_ClassIndex;
/*  26:    */   
/*  27:    */   public FilteredAssociator()
/*  28:    */   {
/*  29:161 */     this.m_Associator = new Apriori();
/*  30:162 */     this.m_Filter = new MultiFilter();
/*  31:163 */     ((MultiFilter)this.m_Filter).setFilters(new Filter[] { new ReplaceMissingValues() });
/*  32:    */     
/*  33:165 */     this.m_ClassIndex = -1;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public String globalInfo()
/*  37:    */   {
/*  38:175 */     return "Class for running an arbitrary associator on data that has been passed through an arbitrary filter. Like the associator, the structure of the filter is based exclusively on the training data and test instances will be processed by the filter without changing their structure.";
/*  39:    */   }
/*  40:    */   
/*  41:    */   protected String defaultAssociatorString()
/*  42:    */   {
/*  43:188 */     return Apriori.class.getName();
/*  44:    */   }
/*  45:    */   
/*  46:    */   public Enumeration<Option> listOptions()
/*  47:    */   {
/*  48:198 */     Vector<Option> result = new Vector();
/*  49:    */     
/*  50:200 */     result.addElement(new Option("\tFull class name of filter to use, followed\n\tby filter options.\n\teg: \"weka.filters.unsupervised.attribute.Remove -V -R 1,2\"\n\t(default: weka.filters.MultiFilter with\n\tweka.filters.unsupervised.attribute.ReplaceMissingValues)", "F", 1, "-F <filter specification>"));
/*  51:    */     
/*  52:    */ 
/*  53:    */ 
/*  54:    */ 
/*  55:    */ 
/*  56:    */ 
/*  57:    */ 
/*  58:208 */     result.addElement(new Option("\tThe class index.\n\t(default: -1, i.e. unset)", "c", 1, "-c <the class index>"));
/*  59:    */     
/*  60:    */ 
/*  61:211 */     result.addAll(Collections.list(super.listOptions()));
/*  62:    */     
/*  63:213 */     return result.elements();
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void setOptions(String[] options)
/*  67:    */     throws Exception
/*  68:    */   {
/*  69:319 */     String tmpStr = Utils.getOption('F', options);
/*  70:320 */     if (tmpStr.length() > 0)
/*  71:    */     {
/*  72:321 */       String[] filterSpec = Utils.splitOptions(tmpStr);
/*  73:322 */       if (filterSpec.length == 0) {
/*  74:323 */         throw new IllegalArgumentException("Invalid filter specification string");
/*  75:    */       }
/*  76:326 */       String filterName = filterSpec[0];
/*  77:327 */       filterSpec[0] = "";
/*  78:328 */       setFilter((Filter)Utils.forName(Filter.class, filterName, filterSpec));
/*  79:    */     }
/*  80:    */     else
/*  81:    */     {
/*  82:330 */       setFilter(new Discretize());
/*  83:    */     }
/*  84:333 */     tmpStr = Utils.getOption('c', options);
/*  85:334 */     if (tmpStr.length() > 0)
/*  86:    */     {
/*  87:335 */       if (tmpStr.equalsIgnoreCase("last")) {
/*  88:336 */         setClassIndex(0);
/*  89:337 */       } else if (tmpStr.equalsIgnoreCase("first")) {
/*  90:338 */         setClassIndex(1);
/*  91:    */       } else {
/*  92:340 */         setClassIndex(Integer.parseInt(tmpStr));
/*  93:    */       }
/*  94:    */     }
/*  95:    */     else {
/*  96:343 */       setClassIndex(-1);
/*  97:    */     }
/*  98:346 */     super.setOptions(options);
/*  99:    */   }
/* 100:    */   
/* 101:    */   public String[] getOptions()
/* 102:    */   {
/* 103:356 */     Vector<String> result = new Vector();
/* 104:    */     
/* 105:358 */     result.add("-F");
/* 106:359 */     result.add("" + getFilterSpec());
/* 107:    */     
/* 108:361 */     result.add("-c");
/* 109:362 */     result.add("" + getClassIndex());
/* 110:    */     
/* 111:364 */     Collections.addAll(result, super.getOptions());
/* 112:    */     
/* 113:366 */     return (String[])result.toArray(new String[result.size()]);
/* 114:    */   }
/* 115:    */   
/* 116:    */   public String filterTipText()
/* 117:    */   {
/* 118:376 */     return "The filter to be used.";
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void setFilter(Filter value)
/* 122:    */   {
/* 123:385 */     this.m_Filter = value;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public Filter getFilter()
/* 127:    */   {
/* 128:394 */     return this.m_Filter;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public String classIndexTipText()
/* 132:    */   {
/* 133:404 */     return "Index of the class attribute. If set to -1, the last attribute is taken as class attribute.";
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void setClassIndex(int value)
/* 137:    */   {
/* 138:413 */     this.m_ClassIndex = value;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public int getClassIndex()
/* 142:    */   {
/* 143:422 */     return this.m_ClassIndex;
/* 144:    */   }
/* 145:    */   
/* 146:    */   protected String getFilterSpec()
/* 147:    */   {
/* 148:432 */     Filter c = getFilter();
/* 149:434 */     if ((c instanceof OptionHandler)) {
/* 150:435 */       return c.getClass().getName() + " " + Utils.joinOptions(c.getOptions());
/* 151:    */     }
/* 152:438 */     return c.getClass().getName();
/* 153:    */   }
/* 154:    */   
/* 155:    */   public Capabilities getCapabilities()
/* 156:    */   {
/* 157:    */     Capabilities result;
/* 158:451 */     if (getFilter() == null)
/* 159:    */     {
/* 160:452 */       Capabilities result = super.getCapabilities();
/* 161:453 */       result.disableAll();
/* 162:    */     }
/* 163:    */     else
/* 164:    */     {
/* 165:455 */       result = getFilter().getCapabilities();
/* 166:    */     }
/* 167:458 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 168:461 */     for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/* 169:462 */       result.enableDependency(cap);
/* 170:    */     }
/* 171:465 */     return result;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public void buildAssociations(Instances data)
/* 175:    */     throws Exception
/* 176:    */   {
/* 177:476 */     if (this.m_Associator == null) {
/* 178:477 */       throw new Exception("No base associator has been set!");
/* 179:    */     }
/* 180:481 */     data = new Instances(data);
/* 181:482 */     if (getClassIndex() == 0) {
/* 182:483 */       data.setClassIndex(data.numAttributes() - 1);
/* 183:    */     } else {
/* 184:485 */       data.setClassIndex(getClassIndex() - 1);
/* 185:    */     }
/* 186:488 */     if (getClassIndex() != -1) {
/* 187:490 */       data.deleteWithMissingClass();
/* 188:    */     }
/* 189:493 */     this.m_Filter.setInputFormat(data);
/* 190:494 */     data = Filter.useFilter(data, this.m_Filter);
/* 191:    */     
/* 192:    */ 
/* 193:497 */     getAssociator().getCapabilities().testWithFail(data);
/* 194:    */     
/* 195:499 */     this.m_FilteredInstances = data.stringFreeStructure();
/* 196:500 */     this.m_Associator.buildAssociations(data);
/* 197:    */   }
/* 198:    */   
/* 199:    */   public AssociationRules getAssociationRules()
/* 200:    */   {
/* 201:511 */     if ((this.m_Associator instanceof AssociationRulesProducer))
/* 202:    */     {
/* 203:512 */       AssociationRules rules = ((AssociationRulesProducer)this.m_Associator).getAssociationRules();
/* 204:    */       
/* 205:    */ 
/* 206:    */ 
/* 207:516 */       FilteredAssociationRules fRules = new FilteredAssociationRules(this, this.m_Filter, rules);
/* 208:    */       
/* 209:    */ 
/* 210:519 */       return fRules;
/* 211:    */     }
/* 212:523 */     return null;
/* 213:    */   }
/* 214:    */   
/* 215:    */   public String[] getRuleMetricNames()
/* 216:    */   {
/* 217:536 */     if ((this.m_Associator instanceof AssociationRulesProducer)) {
/* 218:537 */       return ((AssociationRulesProducer)this.m_Associator).getRuleMetricNames();
/* 219:    */     }
/* 220:540 */     return new String[0];
/* 221:    */   }
/* 222:    */   
/* 223:    */   public boolean canProduceRules()
/* 224:    */   {
/* 225:556 */     if ((this.m_Associator instanceof AssociationRulesProducer)) {
/* 226:557 */       return ((AssociationRulesProducer)this.m_Associator).canProduceRules();
/* 227:    */     }
/* 228:560 */     return false;
/* 229:    */   }
/* 230:    */   
/* 231:    */   public String toString()
/* 232:    */   {
/* 233:    */     String result;
/* 234:    */     String result;
/* 235:572 */     if (this.m_FilteredInstances == null) {
/* 236:573 */       result = "FilteredAssociator: No model built yet.";
/* 237:    */     } else {
/* 238:575 */       result = "FilteredAssociator using " + getAssociatorSpec() + " on data filtered through " + getFilterSpec() + "\n\nFiltered Header\n" + this.m_FilteredInstances.toString() + "\n\nAssociator Model\n" + this.m_Associator.toString();
/* 239:    */     }
/* 240:581 */     return result;
/* 241:    */   }
/* 242:    */   
/* 243:    */   public String getRevision()
/* 244:    */   {
/* 245:591 */     return RevisionUtils.extract("$Revision: 10172 $");
/* 246:    */   }
/* 247:    */   
/* 248:    */   public static void main(String[] args)
/* 249:    */   {
/* 250:600 */     runAssociator(new FilteredAssociator(), args);
/* 251:    */   }
/* 252:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.FilteredAssociator
 * JD-Core Version:    0.7.0.1
 */