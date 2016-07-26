/*   1:    */ package weka.core.neighboursearch;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.CapabilitiesHandler;
/*   9:    */ import weka.core.DistanceFunction;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.SerializedObject;
/*  16:    */ import weka.core.Utils;
/*  17:    */ import weka.filters.AllFilter;
/*  18:    */ import weka.filters.Filter;
/*  19:    */ import weka.filters.unsupervised.attribute.AddID;
/*  20:    */ 
/*  21:    */ public class FilteredNeighbourSearch
/*  22:    */   extends NearestNeighbourSearch
/*  23:    */   implements CapabilitiesHandler
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 1369174644087067375L;
/*  26: 93 */   protected AddID m_AddID = new AddID();
/*  27: 96 */   protected int m_IndexOfID = -1;
/*  28: 99 */   protected Filter m_Filter = new AllFilter();
/*  29:102 */   protected NearestNeighbourSearch m_SearchMethod = new LinearNNSearch();
/*  30:105 */   protected NearestNeighbourSearch m_ModifiedSearchMethod = null;
/*  31:    */   
/*  32:    */   public Capabilities getCapabilities()
/*  33:    */   {
/*  34:114 */     Capabilities result = getFilter().getCapabilities();
/*  35:117 */     for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/*  36:118 */       result.enableDependency(cap);
/*  37:    */     }
/*  38:120 */     return result;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setInstances(Instances data)
/*  42:    */   {
/*  43:    */     try
/*  44:    */     {
/*  45:131 */       super.setInstances(data);
/*  46:    */       
/*  47:    */ 
/*  48:134 */       getCapabilities().testWithFail(data);
/*  49:135 */       Instances filteredData = new Instances(data);
/*  50:136 */       getFilter().setInputFormat(filteredData);
/*  51:137 */       filteredData = Filter.useFilter(data, getFilter());
/*  52:138 */       if (data.numInstances() != filteredData.numInstances()) {
/*  53:139 */         throw new IllegalArgumentException("FilteredNeighbourSearch: Filter has changed the number of instances!");
/*  54:    */       }
/*  55:144 */       this.m_IndexOfID = filteredData.numAttributes();
/*  56:145 */       this.m_AddID.setIDIndex("" + (filteredData.numAttributes() + 1));
/*  57:    */       
/*  58:147 */       this.m_AddID.setInputFormat(filteredData);
/*  59:148 */       filteredData = Filter.useFilter(filteredData, this.m_AddID);
/*  60:    */       
/*  61:    */ 
/*  62:    */ 
/*  63:    */ 
/*  64:153 */       this.m_ModifiedSearchMethod = ((NearestNeighbourSearch)new SerializedObject(getSearchMethod()).getObject());
/*  65:    */       
/*  66:155 */       this.m_ModifiedSearchMethod.getDistanceFunction().setAttributeIndices("1-" + this.m_IndexOfID);
/*  67:    */       
/*  68:157 */       this.m_ModifiedSearchMethod.getDistanceFunction().setInvertSelection(false);
/*  69:    */       
/*  70:    */ 
/*  71:160 */       this.m_ModifiedSearchMethod.setInstances(filteredData);
/*  72:    */     }
/*  73:    */     catch (Exception e)
/*  74:    */     {
/*  75:162 */       e.printStackTrace();
/*  76:    */     }
/*  77:    */   }
/*  78:    */   
/*  79:    */   public String globalInfo()
/*  80:    */   {
/*  81:173 */     return "Applies the given filter before calling the given neighbour search method. The filter must not change the size of the dataset or the order of the instances! Also, the range setting that is specified for the distance function is ignored: all attributes are used for the distance calculation.";
/*  82:    */   }
/*  83:    */   
/*  84:    */   public String filterTipText()
/*  85:    */   {
/*  86:186 */     return "The filter to be used.";
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setFilter(Filter filter)
/*  90:    */   {
/*  91:195 */     this.m_Filter = filter;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public Filter getFilter()
/*  95:    */   {
/*  96:204 */     return this.m_Filter;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public String searchMethodTipText()
/* 100:    */   {
/* 101:214 */     return "The search method to be used.";
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void setSearchMethod(NearestNeighbourSearch search)
/* 105:    */   {
/* 106:223 */     this.m_SearchMethod = search;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public NearestNeighbourSearch getSearchMethod()
/* 110:    */   {
/* 111:232 */     return this.m_SearchMethod;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public Enumeration<Option> listOptions()
/* 115:    */   {
/* 116:242 */     Vector<Option> result = new Vector();
/* 117:    */     
/* 118:244 */     result.add(new Option("\tThe filter to use. (default: weka.filters.AllFilter", "F", 1, "-F"));
/* 119:    */     
/* 120:    */ 
/* 121:    */ 
/* 122:    */ 
/* 123:249 */     result.addElement(new Option("\tThe search method to use. (default: weka.core.neighboursearch.LinearNNSearch)", "S", 0, "-S"));
/* 124:254 */     if ((this.m_Filter instanceof OptionHandler))
/* 125:    */     {
/* 126:255 */       result.addElement(new Option("", "", 0, "\nOptions specific to filter " + this.m_Filter.getClass().getName() + ":"));
/* 127:    */       
/* 128:257 */       result.addAll(Collections.list(this.m_Filter.listOptions()));
/* 129:    */     }
/* 130:260 */     if ((this.m_SearchMethod instanceof OptionHandler))
/* 131:    */     {
/* 132:262 */       result.addElement(new Option("", "", 0, "\nOptions specific to search method " + this.m_SearchMethod.getClass().getName() + ":"));
/* 133:    */       
/* 134:    */ 
/* 135:265 */       result.addAll(Collections.list(this.m_SearchMethod.listOptions()));
/* 136:    */     }
/* 137:269 */     return result.elements();
/* 138:    */   }
/* 139:    */   
/* 140:    */   public String[] getOptions()
/* 141:    */   {
/* 142:280 */     Vector<String> result = new Vector();
/* 143:    */     
/* 144:282 */     result.add("-F");
/* 145:283 */     result.add("" + getFilterSpec());
/* 146:    */     
/* 147:285 */     result.add("-S");
/* 148:286 */     result.add("" + getSearchMethodSpec());
/* 149:    */     
/* 150:288 */     return (String[])result.toArray(new String[result.size()]);
/* 151:    */   }
/* 152:    */   
/* 153:    */   protected String getFilterSpec()
/* 154:    */   {
/* 155:299 */     Filter c = getFilter();
/* 156:300 */     if ((c instanceof OptionHandler)) {
/* 157:301 */       return c.getClass().getName() + " " + Utils.joinOptions(c.getOptions());
/* 158:    */     }
/* 159:304 */     return c.getClass().getName();
/* 160:    */   }
/* 161:    */   
/* 162:    */   protected String getSearchMethodSpec()
/* 163:    */   {
/* 164:315 */     NearestNeighbourSearch c = getSearchMethod();
/* 165:316 */     if ((c instanceof OptionHandler)) {
/* 166:317 */       return c.getClass().getName() + " " + Utils.joinOptions(c.getOptions());
/* 167:    */     }
/* 168:320 */     return c.getClass().getName();
/* 169:    */   }
/* 170:    */   
/* 171:    */   public void setOptions(String[] options)
/* 172:    */     throws Exception
/* 173:    */   {
/* 174:332 */     String searchMethod = Utils.getOption('S', options);
/* 175:333 */     if (searchMethod.length() != 0)
/* 176:    */     {
/* 177:334 */       String[] searchMethodSpec = Utils.splitOptions(searchMethod);
/* 178:335 */       if (searchMethodSpec.length == 0) {
/* 179:336 */         throw new Exception("Invalid search method specification string.");
/* 180:    */       }
/* 181:338 */       String className = searchMethodSpec[0];
/* 182:339 */       searchMethodSpec[0] = "";
/* 183:    */       
/* 184:341 */       setSearchMethod((NearestNeighbourSearch)Utils.forName(NearestNeighbourSearch.class, className, searchMethodSpec));
/* 185:    */     }
/* 186:    */     else
/* 187:    */     {
/* 188:344 */       setSearchMethod(new LinearNNSearch());
/* 189:    */     }
/* 190:347 */     String filter = Utils.getOption('F', options);
/* 191:348 */     if (filter.length() != 0)
/* 192:    */     {
/* 193:349 */       String[] filterSpec = Utils.splitOptions(filter);
/* 194:350 */       if (filterSpec.length == 0) {
/* 195:351 */         throw new Exception("Invalid filter specification string.");
/* 196:    */       }
/* 197:353 */       String className = filterSpec[0];
/* 198:354 */       filterSpec[0] = "";
/* 199:    */       
/* 200:356 */       setFilter((Filter)Utils.forName(Filter.class, className, filterSpec));
/* 201:    */     }
/* 202:    */     else
/* 203:    */     {
/* 204:358 */       setFilter(new AllFilter());
/* 205:    */     }
/* 206:    */   }
/* 207:    */   
/* 208:    */   public String getRevision()
/* 209:    */   {
/* 210:371 */     return RevisionUtils.extract("$Revision: 11006 $");
/* 211:    */   }
/* 212:    */   
/* 213:    */   public Instance nearestNeighbour(Instance target)
/* 214:    */     throws Exception
/* 215:    */   {
/* 216:386 */     getFilter().input(target);
/* 217:387 */     this.m_AddID.input(getFilter().output());
/* 218:388 */     return getInstances().instance((int)this.m_ModifiedSearchMethod.nearestNeighbour(this.m_AddID.output()).value(this.m_IndexOfID) - 1);
/* 219:    */   }
/* 220:    */   
/* 221:    */   public Instances kNearestNeighbours(Instance target, int k)
/* 222:    */     throws Exception
/* 223:    */   {
/* 224:408 */     getFilter().input(target);
/* 225:409 */     this.m_AddID.input(getFilter().output());
/* 226:410 */     Instances neighboursInFilteredSpace = this.m_ModifiedSearchMethod.kNearestNeighbours(this.m_AddID.output(), k);
/* 227:    */     
/* 228:    */ 
/* 229:    */ 
/* 230:414 */     Instances neighbours = new Instances(getInstances(), k);
/* 231:415 */     for (Instance inst : neighboursInFilteredSpace) {
/* 232:416 */       neighbours.add(getInstances().instance((int)inst.value(this.m_IndexOfID) - 1));
/* 233:    */     }
/* 234:419 */     return neighbours;
/* 235:    */   }
/* 236:    */   
/* 237:    */   public double[] getDistances()
/* 238:    */     throws Exception
/* 239:    */   {
/* 240:431 */     return this.m_ModifiedSearchMethod.getDistances();
/* 241:    */   }
/* 242:    */   
/* 243:    */   public void update(Instance ins)
/* 244:    */     throws Exception
/* 245:    */   {
/* 246:442 */     getFilter().input(ins);
/* 247:443 */     this.m_AddID.input(getFilter().output());
/* 248:444 */     this.m_ModifiedSearchMethod.update(this.m_AddID.output());
/* 249:    */   }
/* 250:    */   
/* 251:    */   public void addInstanceInfo(Instance ins)
/* 252:    */   {
/* 253:455 */     if (this.m_Instances != null) {
/* 254:    */       try
/* 255:    */       {
/* 256:457 */         getFilter().input(ins);
/* 257:458 */         this.m_AddID.input(getFilter().output());
/* 258:459 */         this.m_ModifiedSearchMethod.addInstanceInfo(this.m_AddID.output());
/* 259:    */       }
/* 260:    */       catch (Exception ex)
/* 261:    */       {
/* 262:461 */         ex.printStackTrace();
/* 263:    */       }
/* 264:    */     }
/* 265:    */   }
/* 266:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.FilteredNeighbourSearch
 * JD-Core Version:    0.7.0.1
 */