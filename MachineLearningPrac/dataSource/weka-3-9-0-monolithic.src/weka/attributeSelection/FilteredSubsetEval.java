/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.BitSet;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.filters.Filter;
/*  17:    */ import weka.filters.supervised.instance.SpreadSubsample;
/*  18:    */ 
/*  19:    */ public class FilteredSubsetEval
/*  20:    */   extends ASEvaluation
/*  21:    */   implements Serializable, SubsetEvaluator, OptionHandler
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = 2111121880778327334L;
/*  24: 75 */   protected SubsetEvaluator m_evaluator = new CfsSubsetEval();
/*  25: 78 */   protected Filter m_filter = new SpreadSubsample();
/*  26:    */   protected Instances m_filteredInstances;
/*  27:    */   
/*  28:    */   public FilteredSubsetEval()
/*  29:    */   {
/*  30: 84 */     this.m_filteredInstances = null;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public Capabilities getCapabilities()
/*  34:    */   {
/*  35:    */     Capabilities result;
/*  36: 96 */     if (getFilter() == null)
/*  37:    */     {
/*  38: 97 */       Capabilities result = super.getCapabilities();
/*  39: 98 */       result.disableAll();
/*  40:    */     }
/*  41:    */     else
/*  42:    */     {
/*  43:100 */       result = getFilter().getCapabilities();
/*  44:    */     }
/*  45:104 */     for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/*  46:105 */       result.enableDependency(cap);
/*  47:    */     }
/*  48:108 */     return result;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public String globalInfo()
/*  52:    */   {
/*  53:116 */     return "Class for running an arbitrary subset evaluator on data that has been passed through an arbitrary filter (note: filters that alter the order or number of attributes are not allowed). Like the evaluator, the structure of the filter is based exclusively on the training data.";
/*  54:    */   }
/*  55:    */   
/*  56:    */   public Enumeration<Option> listOptions()
/*  57:    */   {
/*  58:129 */     Vector<Option> newVector = new Vector(2);
/*  59:    */     
/*  60:131 */     newVector.addElement(new Option("\tFull name of base evaluator to use, followed by evaluator options.\n\teg: \"weka.attributeSelection.CfsSubsetEval -L\"", "W", 1, "-W <evaluator specification>"));
/*  61:    */     
/*  62:    */ 
/*  63:    */ 
/*  64:    */ 
/*  65:    */ 
/*  66:137 */     newVector.addElement(new Option("\tFull class name of filter to use, followed\n\tby filter options.\n\teg: \"weka.filters.supervised.instance.SpreadSubsample -M 1\"", "F", 1, "-F <filter specification>"));
/*  67:    */     
/*  68:    */ 
/*  69:    */ 
/*  70:    */ 
/*  71:    */ 
/*  72:143 */     return newVector.elements();
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void setOptions(String[] options)
/*  76:    */     throws Exception
/*  77:    */   {
/*  78:173 */     String evaluator = Utils.getOption('W', options);
/*  79:175 */     if (evaluator.length() > 0)
/*  80:    */     {
/*  81:176 */       String[] evaluatorSpec = Utils.splitOptions(evaluator);
/*  82:177 */       if (evaluatorSpec.length == 0) {
/*  83:178 */         throw new IllegalArgumentException("Invalid evaluator specification string");
/*  84:    */       }
/*  85:182 */       String evaluatorName = evaluatorSpec[0];
/*  86:183 */       evaluatorSpec[0] = "";
/*  87:184 */       setSubsetEvaluator((ASEvaluation)Utils.forName(SubsetEvaluator.class, evaluatorName, evaluatorSpec));
/*  88:    */     }
/*  89:    */     else
/*  90:    */     {
/*  91:188 */       setSubsetEvaluator(new CfsSubsetEval());
/*  92:    */     }
/*  93:192 */     String filterString = Utils.getOption('F', options);
/*  94:193 */     if (filterString.length() > 0)
/*  95:    */     {
/*  96:194 */       String[] filterSpec = Utils.splitOptions(filterString);
/*  97:195 */       if (filterSpec.length == 0) {
/*  98:196 */         throw new IllegalArgumentException("Invalid filter specification string");
/*  99:    */       }
/* 100:199 */       String filterName = filterSpec[0];
/* 101:200 */       filterSpec[0] = "";
/* 102:201 */       setFilter((Filter)Utils.forName(Filter.class, filterName, filterSpec));
/* 103:    */     }
/* 104:    */     else
/* 105:    */     {
/* 106:203 */       setFilter(new SpreadSubsample());
/* 107:    */     }
/* 108:    */   }
/* 109:    */   
/* 110:    */   public String[] getOptions()
/* 111:    */   {
/* 112:214 */     ArrayList<String> options = new ArrayList();
/* 113:    */     
/* 114:216 */     options.add("-W");
/* 115:217 */     options.add(getEvaluatorSpec());
/* 116:    */     
/* 117:219 */     options.add("-F");
/* 118:220 */     options.add(getFilterSpec());
/* 119:    */     
/* 120:222 */     return (String[])options.toArray(new String[0]);
/* 121:    */   }
/* 122:    */   
/* 123:    */   protected String getEvaluatorSpec()
/* 124:    */   {
/* 125:231 */     SubsetEvaluator a = this.m_evaluator;
/* 126:232 */     if ((a instanceof OptionHandler)) {
/* 127:233 */       return a.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)a).getOptions());
/* 128:    */     }
/* 129:236 */     return a.getClass().getName();
/* 130:    */   }
/* 131:    */   
/* 132:    */   public String subsetEvaluatorTipText()
/* 133:    */   {
/* 134:246 */     return "The subset evaluator to be used.";
/* 135:    */   }
/* 136:    */   
/* 137:    */   public void setSubsetEvaluator(ASEvaluation newEvaluator)
/* 138:    */   {
/* 139:255 */     if (!(newEvaluator instanceof SubsetEvaluator)) {
/* 140:256 */       throw new IllegalArgumentException("Evaluator must be a SubsetEvaluator!");
/* 141:    */     }
/* 142:258 */     this.m_evaluator = ((SubsetEvaluator)newEvaluator);
/* 143:    */   }
/* 144:    */   
/* 145:    */   public ASEvaluation getSubsetEvaluator()
/* 146:    */   {
/* 147:267 */     return (ASEvaluation)this.m_evaluator;
/* 148:    */   }
/* 149:    */   
/* 150:    */   protected String getFilterSpec()
/* 151:    */   {
/* 152:276 */     Filter c = getFilter();
/* 153:277 */     if ((c instanceof OptionHandler)) {
/* 154:278 */       return c.getClass().getName() + " " + Utils.joinOptions(c.getOptions());
/* 155:    */     }
/* 156:281 */     return c.getClass().getName();
/* 157:    */   }
/* 158:    */   
/* 159:    */   public String filterTipText()
/* 160:    */   {
/* 161:291 */     return "The filter to be used.";
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void setFilter(Filter newFilter)
/* 165:    */   {
/* 166:300 */     this.m_filter = newFilter;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public Filter getFilter()
/* 170:    */   {
/* 171:309 */     return this.m_filter;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public String getRevision()
/* 175:    */   {
/* 176:319 */     return RevisionUtils.extract("$Revision: 10361 $");
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void buildEvaluator(Instances data)
/* 180:    */     throws Exception
/* 181:    */   {
/* 182:331 */     getCapabilities().testWithFail(data);
/* 183:    */     
/* 184:    */ 
/* 185:334 */     Instances original = new Instances(data, 0);
/* 186:    */     
/* 187:336 */     this.m_filter.setInputFormat(data);
/* 188:337 */     data = Filter.useFilter(data, this.m_filter);
/* 189:341 */     if (data.numAttributes() != original.numAttributes()) {
/* 190:342 */       throw new Exception("Filter must not alter the number of attributes in the data!");
/* 191:    */     }
/* 192:347 */     if ((original.classIndex() >= 0) && 
/* 193:348 */       (data.classIndex() != original.classIndex())) {
/* 194:349 */       throw new Exception("Filter must not change the class attribute!");
/* 195:    */     }
/* 196:354 */     for (int i = 0; i < original.numAttributes(); i++) {
/* 197:355 */       if (!data.attribute(i).name().equals(original.attribute(i).name())) {
/* 198:356 */         throw new Exception("Filter must not alter the order of the attributes!");
/* 199:    */       }
/* 200:    */     }
/* 201:362 */     getSubsetEvaluator().getCapabilities().testWithFail(data);
/* 202:363 */     this.m_filteredInstances = data.stringFreeStructure();
/* 203:    */     
/* 204:365 */     ((ASEvaluation)this.m_evaluator).buildEvaluator(data);
/* 205:    */   }
/* 206:    */   
/* 207:    */   public double evaluateSubset(BitSet subset)
/* 208:    */     throws Exception
/* 209:    */   {
/* 210:377 */     return this.m_evaluator.evaluateSubset(subset);
/* 211:    */   }
/* 212:    */   
/* 213:    */   public String toString()
/* 214:    */   {
/* 215:387 */     StringBuffer text = new StringBuffer();
/* 216:389 */     if (this.m_filteredInstances == null)
/* 217:    */     {
/* 218:390 */       text.append("Filtered attribute evaluator has not been built");
/* 219:    */     }
/* 220:    */     else
/* 221:    */     {
/* 222:392 */       text.append("Filtered Attribute Evaluator");
/* 223:393 */       text.append("\nFilter: " + getFilterSpec());
/* 224:394 */       text.append("\nAttribute evaluator: " + getEvaluatorSpec());
/* 225:395 */       text.append("\n\nFiltered header:\n");
/* 226:396 */       text.append(this.m_filteredInstances);
/* 227:    */     }
/* 228:398 */     text.append("\n");
/* 229:399 */     return text.toString();
/* 230:    */   }
/* 231:    */   
/* 232:    */   public static void main(String[] args)
/* 233:    */   {
/* 234:411 */     runEvaluator(new FilteredSubsetEval(), args);
/* 235:    */   }
/* 236:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.FilteredSubsetEval
 * JD-Core Version:    0.7.0.1
 */