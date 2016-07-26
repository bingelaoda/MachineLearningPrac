/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.OptionHandler;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Utils;
/*  15:    */ import weka.filters.Filter;
/*  16:    */ import weka.filters.supervised.instance.SpreadSubsample;
/*  17:    */ 
/*  18:    */ public class FilteredAttributeEval
/*  19:    */   extends ASEvaluation
/*  20:    */   implements Serializable, AttributeEvaluator, OptionHandler
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = 2111121880778327334L;
/*  23: 74 */   protected AttributeEvaluator m_evaluator = new InfoGainAttributeEval();
/*  24: 77 */   protected Filter m_filter = new SpreadSubsample();
/*  25:    */   protected Instances m_filteredInstances;
/*  26:    */   
/*  27:    */   public FilteredAttributeEval()
/*  28:    */   {
/*  29: 83 */     this.m_filteredInstances = null;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public Capabilities getCapabilities()
/*  33:    */   {
/*  34:    */     Capabilities result;
/*  35: 95 */     if (getFilter() == null)
/*  36:    */     {
/*  37: 96 */       Capabilities result = super.getCapabilities();
/*  38: 97 */       result.disableAll();
/*  39:    */     }
/*  40:    */     else
/*  41:    */     {
/*  42: 99 */       result = getFilter().getCapabilities();
/*  43:    */     }
/*  44:103 */     for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/*  45:104 */       result.enableDependency(cap);
/*  46:    */     }
/*  47:107 */     return result;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public String globalInfo()
/*  51:    */   {
/*  52:115 */     return "Class for running an arbitrary attribute evaluator on data that has been passed through an arbitrary filter (note: filters that alter the order or number of attributes are not allowed). Like the evaluator, the structure of the filter is based exclusively on the training data.";
/*  53:    */   }
/*  54:    */   
/*  55:    */   public Enumeration<Option> listOptions()
/*  56:    */   {
/*  57:128 */     Vector<Option> newVector = new Vector(2);
/*  58:    */     
/*  59:130 */     newVector.addElement(new Option("\tFull name of base evaluator to use, followed by evaluator options.\n\teg: \"weka.attributeSelection.InfoGainAttributeEval -M\"", "W", 1, "-W <evaluator specification>"));
/*  60:    */     
/*  61:    */ 
/*  62:    */ 
/*  63:    */ 
/*  64:    */ 
/*  65:136 */     newVector.addElement(new Option("\tFull class name of filter to use, followed\n\tby filter options.\n\teg: \"weka.filters.supervised.instance.SpreadSubsample -M 1\"", "F", 1, "-F <filter specification>"));
/*  66:    */     
/*  67:    */ 
/*  68:    */ 
/*  69:    */ 
/*  70:    */ 
/*  71:142 */     return newVector.elements();
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setOptions(String[] options)
/*  75:    */     throws Exception
/*  76:    */   {
/*  77:172 */     String evaluator = Utils.getOption('W', options);
/*  78:174 */     if (evaluator.length() > 0)
/*  79:    */     {
/*  80:175 */       String[] evaluatorSpec = Utils.splitOptions(evaluator);
/*  81:176 */       if (evaluatorSpec.length == 0) {
/*  82:177 */         throw new IllegalArgumentException("Invalid evaluator specification string");
/*  83:    */       }
/*  84:181 */       String evaluatorName = evaluatorSpec[0];
/*  85:182 */       evaluatorSpec[0] = "";
/*  86:183 */       setAttributeEvaluator((ASEvaluation)Utils.forName(AttributeEvaluator.class, evaluatorName, evaluatorSpec));
/*  87:    */     }
/*  88:    */     else
/*  89:    */     {
/*  90:187 */       setAttributeEvaluator(new InfoGainAttributeEval());
/*  91:    */     }
/*  92:191 */     String filterString = Utils.getOption('F', options);
/*  93:192 */     if (filterString.length() > 0)
/*  94:    */     {
/*  95:193 */       String[] filterSpec = Utils.splitOptions(filterString);
/*  96:194 */       if (filterSpec.length == 0) {
/*  97:195 */         throw new IllegalArgumentException("Invalid filter specification string");
/*  98:    */       }
/*  99:198 */       String filterName = filterSpec[0];
/* 100:199 */       filterSpec[0] = "";
/* 101:200 */       setFilter((Filter)Utils.forName(Filter.class, filterName, filterSpec));
/* 102:    */     }
/* 103:    */     else
/* 104:    */     {
/* 105:202 */       setFilter(new SpreadSubsample());
/* 106:    */     }
/* 107:    */   }
/* 108:    */   
/* 109:    */   public String[] getOptions()
/* 110:    */   {
/* 111:213 */     ArrayList<String> options = new ArrayList();
/* 112:    */     
/* 113:215 */     options.add("-W");
/* 114:216 */     options.add(getEvaluatorSpec());
/* 115:    */     
/* 116:218 */     options.add("-F");
/* 117:219 */     options.add(getFilterSpec());
/* 118:    */     
/* 119:221 */     return (String[])options.toArray(new String[0]);
/* 120:    */   }
/* 121:    */   
/* 122:    */   protected String getEvaluatorSpec()
/* 123:    */   {
/* 124:230 */     AttributeEvaluator a = this.m_evaluator;
/* 125:231 */     if ((a instanceof OptionHandler)) {
/* 126:232 */       return a.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)a).getOptions());
/* 127:    */     }
/* 128:235 */     return a.getClass().getName();
/* 129:    */   }
/* 130:    */   
/* 131:    */   public String attributeEvaluatorTipText()
/* 132:    */   {
/* 133:245 */     return "The attribute evaluator to be used.";
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void setAttributeEvaluator(ASEvaluation newEvaluator)
/* 137:    */   {
/* 138:254 */     if (!(newEvaluator instanceof AttributeEvaluator)) {
/* 139:255 */       throw new IllegalArgumentException("Evaluator must be an AttributeEvaluator!");
/* 140:    */     }
/* 141:258 */     this.m_evaluator = ((AttributeEvaluator)newEvaluator);
/* 142:    */   }
/* 143:    */   
/* 144:    */   public ASEvaluation getAttributeEvaluator()
/* 145:    */   {
/* 146:267 */     return (ASEvaluation)this.m_evaluator;
/* 147:    */   }
/* 148:    */   
/* 149:    */   protected String getFilterSpec()
/* 150:    */   {
/* 151:276 */     Filter c = getFilter();
/* 152:277 */     if ((c instanceof OptionHandler)) {
/* 153:278 */       return c.getClass().getName() + " " + Utils.joinOptions(c.getOptions());
/* 154:    */     }
/* 155:281 */     return c.getClass().getName();
/* 156:    */   }
/* 157:    */   
/* 158:    */   public String filterTipText()
/* 159:    */   {
/* 160:291 */     return "The filter to be used.";
/* 161:    */   }
/* 162:    */   
/* 163:    */   public void setFilter(Filter newFilter)
/* 164:    */   {
/* 165:300 */     this.m_filter = newFilter;
/* 166:    */   }
/* 167:    */   
/* 168:    */   public Filter getFilter()
/* 169:    */   {
/* 170:309 */     return this.m_filter;
/* 171:    */   }
/* 172:    */   
/* 173:    */   public String getRevision()
/* 174:    */   {
/* 175:319 */     return RevisionUtils.extract("$Revision: 10361 $");
/* 176:    */   }
/* 177:    */   
/* 178:    */   public void buildEvaluator(Instances data)
/* 179:    */     throws Exception
/* 180:    */   {
/* 181:331 */     getCapabilities().testWithFail(data);
/* 182:    */     
/* 183:    */ 
/* 184:334 */     Instances original = new Instances(data, 0);
/* 185:    */     
/* 186:336 */     this.m_filter.setInputFormat(data);
/* 187:337 */     data = Filter.useFilter(data, this.m_filter);
/* 188:341 */     if (data.numAttributes() != original.numAttributes()) {
/* 189:342 */       throw new Exception("Filter must not alter the number of attributes in the data!");
/* 190:    */     }
/* 191:347 */     if ((original.classIndex() >= 0) && 
/* 192:348 */       (data.classIndex() != original.classIndex())) {
/* 193:349 */       throw new Exception("Filter must not change the class attribute!");
/* 194:    */     }
/* 195:354 */     for (int i = 0; i < original.numAttributes(); i++) {
/* 196:355 */       if (!data.attribute(i).name().equals(original.attribute(i).name())) {
/* 197:356 */         throw new Exception("Filter must not alter the order of the attributes!");
/* 198:    */       }
/* 199:    */     }
/* 200:362 */     getAttributeEvaluator().getCapabilities().testWithFail(data);
/* 201:363 */     this.m_filteredInstances = data.stringFreeStructure();
/* 202:    */     
/* 203:365 */     ((ASEvaluation)this.m_evaluator).buildEvaluator(data);
/* 204:    */   }
/* 205:    */   
/* 206:    */   public double evaluateAttribute(int attribute)
/* 207:    */     throws Exception
/* 208:    */   {
/* 209:377 */     return this.m_evaluator.evaluateAttribute(attribute);
/* 210:    */   }
/* 211:    */   
/* 212:    */   public String toString()
/* 213:    */   {
/* 214:387 */     StringBuffer text = new StringBuffer();
/* 215:389 */     if (this.m_filteredInstances == null)
/* 216:    */     {
/* 217:390 */       text.append("Filtered attribute evaluator has not been built");
/* 218:    */     }
/* 219:    */     else
/* 220:    */     {
/* 221:392 */       text.append("Filtered Attribute Evaluator");
/* 222:393 */       text.append("\nFilter: " + getFilterSpec());
/* 223:394 */       text.append("\nAttribute evaluator: " + getEvaluatorSpec());
/* 224:395 */       text.append("\n\nFiltered header:\n");
/* 225:396 */       text.append(this.m_filteredInstances);
/* 226:    */     }
/* 227:398 */     text.append("\n");
/* 228:399 */     return text.toString();
/* 229:    */   }
/* 230:    */   
/* 231:    */   public static void main(String[] args)
/* 232:    */   {
/* 233:411 */     runEvaluator(new FilteredAttributeEval(), args);
/* 234:    */   }
/* 235:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.FilteredAttributeEval
 * JD-Core Version:    0.7.0.1
 */