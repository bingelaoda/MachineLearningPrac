/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.neighboursearch.PerformanceStats;
/*   8:    */ import weka.filters.Filter;
/*   9:    */ import weka.filters.unsupervised.attribute.RandomProjection;
/*  10:    */ import weka.filters.unsupervised.attribute.Remove;
/*  11:    */ 
/*  12:    */ public class FilteredDistance
/*  13:    */   implements DistanceFunction, OptionHandler, Serializable
/*  14:    */ {
/*  15:114 */   DistanceFunction m_Distance = new EuclideanDistance();
/*  16:117 */   Filter m_Filter = new RandomProjection();
/*  17:120 */   Remove m_Remove = new Remove();
/*  18:    */   
/*  19:    */   public FilteredDistance()
/*  20:    */   {
/*  21:127 */     this.m_Remove.setInvertSelection(true);
/*  22:128 */     this.m_Remove.setAttributeIndices("first-last");
/*  23:    */   }
/*  24:    */   
/*  25:    */   public String globalInfo()
/*  26:    */   {
/*  27:138 */     return "Applies the given filter before calling the given distance function.";
/*  28:    */   }
/*  29:    */   
/*  30:    */   public String filterTipText()
/*  31:    */   {
/*  32:148 */     return "The filter to be used.";
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void setFilter(Filter filter)
/*  36:    */   {
/*  37:158 */     this.m_Filter = filter;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public Filter getFilter()
/*  41:    */   {
/*  42:168 */     return this.m_Filter;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public String distanceTipText()
/*  46:    */   {
/*  47:177 */     return "The distance to be used.";
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setDistance(DistanceFunction distance)
/*  51:    */   {
/*  52:187 */     this.m_Distance = distance;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public DistanceFunction getDistance()
/*  56:    */   {
/*  57:197 */     return this.m_Distance;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public Enumeration<Option> listOptions()
/*  61:    */   {
/*  62:207 */     Vector<Option> result = new Vector();
/*  63:    */     
/*  64:209 */     result.add(new Option("\tThe filter to use. (default: weka.unsupervised.attribute.RandomProjection", "F", 1, "-F"));
/*  65:    */     
/*  66:    */ 
/*  67:212 */     result.addElement(new Option("\tThe distance function to use. (default: weka.core.EuclideanDistance", "E", 0, "-E"));
/*  68:215 */     if ((this.m_Filter instanceof OptionHandler))
/*  69:    */     {
/*  70:216 */       result.addElement(new Option("", "", 0, "\nOptions specific to filter " + this.m_Filter.getClass().getName() + ":"));
/*  71:    */       
/*  72:    */ 
/*  73:    */ 
/*  74:220 */       result.addAll(Collections.list(this.m_Filter.listOptions()));
/*  75:    */     }
/*  76:223 */     if ((this.m_Distance instanceof OptionHandler))
/*  77:    */     {
/*  78:225 */       result.addElement(new Option("", "", 0, "\nOptions specific to distance function " + this.m_Distance.getClass().getName() + ":"));
/*  79:    */       
/*  80:    */ 
/*  81:    */ 
/*  82:229 */       result.addAll(Collections.list(this.m_Distance.listOptions()));
/*  83:    */     }
/*  84:232 */     result.addElement(new Option("\tSpecifies list of columns to used in the calculation of the \n\tdistance. 'first' and 'last' are valid indices.\n\t(default: first-last)", "R", 1, "-R <col1,col2-col4,...>"));
/*  85:    */     
/*  86:    */ 
/*  87:    */ 
/*  88:    */ 
/*  89:237 */     result.addElement(new Option("\tInvert matching sense of column indices.", "V", 0, "-V"));
/*  90:    */     
/*  91:    */ 
/*  92:240 */     return result.elements();
/*  93:    */   }
/*  94:    */   
/*  95:    */   public String[] getOptions()
/*  96:    */   {
/*  97:251 */     Vector<String> result = new Vector();
/*  98:    */     
/*  99:253 */     result.add("-R");
/* 100:254 */     result.add(getAttributeIndices());
/* 101:256 */     if (getInvertSelection()) {
/* 102:257 */       result.add("-V");
/* 103:    */     }
/* 104:260 */     result.add("-F");
/* 105:261 */     result.add("" + getFilterSpec());
/* 106:    */     
/* 107:263 */     result.add("-D");
/* 108:264 */     result.add("" + getDistanceSpec());
/* 109:    */     
/* 110:266 */     return (String[])result.toArray(new String[result.size()]);
/* 111:    */   }
/* 112:    */   
/* 113:    */   protected String getFilterSpec()
/* 114:    */   {
/* 115:277 */     Filter c = getFilter();
/* 116:278 */     if ((c instanceof OptionHandler)) {
/* 117:279 */       return c.getClass().getName() + " " + Utils.joinOptions(c.getOptions());
/* 118:    */     }
/* 119:282 */     return c.getClass().getName();
/* 120:    */   }
/* 121:    */   
/* 122:    */   protected String getDistanceSpec()
/* 123:    */   {
/* 124:293 */     DistanceFunction c = getDistance();
/* 125:294 */     if ((c instanceof OptionHandler)) {
/* 126:295 */       return c.getClass().getName() + " " + Utils.joinOptions(c.getOptions());
/* 127:    */     }
/* 128:298 */     return c.getClass().getName();
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void setOptions(String[] options)
/* 132:    */     throws Exception
/* 133:    */   {
/* 134:310 */     String distance = Utils.getOption('D', options);
/* 135:311 */     if (distance.length() != 0)
/* 136:    */     {
/* 137:312 */       String[] distanceSpec = Utils.splitOptions(distance);
/* 138:313 */       if (distanceSpec.length == 0) {
/* 139:314 */         throw new Exception("Invalid distance specification string.");
/* 140:    */       }
/* 141:316 */       String className = distanceSpec[0];
/* 142:317 */       distanceSpec[0] = "";
/* 143:    */       
/* 144:319 */       setDistance((DistanceFunction)Utils.forName(DistanceFunction.class, className, distanceSpec));
/* 145:    */     }
/* 146:    */     else
/* 147:    */     {
/* 148:322 */       setDistance(new EuclideanDistance());
/* 149:    */     }
/* 150:325 */     String filter = Utils.getOption('F', options);
/* 151:326 */     if (filter.length() != 0)
/* 152:    */     {
/* 153:327 */       String[] filterSpec = Utils.splitOptions(filter);
/* 154:328 */       if (filterSpec.length == 0) {
/* 155:329 */         throw new Exception("Invalid filter specification string.");
/* 156:    */       }
/* 157:331 */       String className = filterSpec[0];
/* 158:332 */       filterSpec[0] = "";
/* 159:    */       
/* 160:334 */       setFilter((Filter)Utils.forName(Filter.class, className, filterSpec));
/* 161:    */     }
/* 162:    */     else
/* 163:    */     {
/* 164:337 */       setFilter(new RandomProjection());
/* 165:    */     }
/* 166:340 */     String tmpStr = Utils.getOption('R', options);
/* 167:341 */     if (tmpStr.length() != 0) {
/* 168:342 */       setAttributeIndices(tmpStr);
/* 169:    */     } else {
/* 170:344 */       setAttributeIndices("first-last");
/* 171:    */     }
/* 172:347 */     setInvertSelection(Utils.getFlag('V', options));
/* 173:    */   }
/* 174:    */   
/* 175:    */   public void setInstances(Instances insts)
/* 176:    */   {
/* 177:    */     try
/* 178:    */     {
/* 179:358 */       this.m_Remove.setInputFormat(insts);
/* 180:359 */       Instances reducedInstances = Filter.useFilter(insts, this.m_Remove);
/* 181:360 */       this.m_Filter.setInputFormat(reducedInstances);
/* 182:361 */       this.m_Distance.setInstances(Filter.useFilter(reducedInstances, this.m_Filter));
/* 183:    */     }
/* 184:    */     catch (Exception e)
/* 185:    */     {
/* 186:363 */       e.printStackTrace();
/* 187:    */     }
/* 188:    */   }
/* 189:    */   
/* 190:    */   public Instances getInstances()
/* 191:    */   {
/* 192:374 */     return this.m_Distance.getInstances();
/* 193:    */   }
/* 194:    */   
/* 195:    */   public void setAttributeIndices(String value)
/* 196:    */   {
/* 197:386 */     this.m_Remove.setAttributeIndices(value);
/* 198:    */   }
/* 199:    */   
/* 200:    */   public String getAttributeIndices()
/* 201:    */   {
/* 202:396 */     return this.m_Remove.getAttributeIndices();
/* 203:    */   }
/* 204:    */   
/* 205:    */   public void setInvertSelection(boolean value)
/* 206:    */   {
/* 207:406 */     this.m_Remove.setInvertSelection(!value);
/* 208:    */   }
/* 209:    */   
/* 210:    */   public boolean getInvertSelection()
/* 211:    */   {
/* 212:416 */     return !this.m_Remove.getInvertSelection();
/* 213:    */   }
/* 214:    */   
/* 215:    */   public double distance(Instance first, Instance second)
/* 216:    */   {
/* 217:428 */     return distance(first, second, (1.0D / 0.0D), null);
/* 218:    */   }
/* 219:    */   
/* 220:    */   public double distance(Instance first, Instance second, PerformanceStats stats)
/* 221:    */     throws Exception
/* 222:    */   {
/* 223:443 */     return distance(first, second, (1.0D / 0.0D), stats);
/* 224:    */   }
/* 225:    */   
/* 226:    */   public double distance(Instance first, Instance second, double cutOffValue)
/* 227:    */   {
/* 228:463 */     return distance(first, second, cutOffValue, null);
/* 229:    */   }
/* 230:    */   
/* 231:    */   public double distance(Instance first, Instance second, double cutOffValue, PerformanceStats stats)
/* 232:    */   {
/* 233:    */     try
/* 234:    */     {
/* 235:486 */       this.m_Remove.input(first);
/* 236:487 */       this.m_Filter.input(this.m_Remove.output());
/* 237:488 */       Instance firstFiltered = this.m_Filter.output();
/* 238:489 */       this.m_Remove.input(second);
/* 239:490 */       this.m_Filter.input(this.m_Remove.output());
/* 240:491 */       Instance secondFiltered = this.m_Filter.output();
/* 241:492 */       return this.m_Distance.distance(firstFiltered, secondFiltered, cutOffValue, stats);
/* 242:    */     }
/* 243:    */     catch (Exception e)
/* 244:    */     {
/* 245:494 */       e.printStackTrace();
/* 246:    */     }
/* 247:495 */     return -1.0D;
/* 248:    */   }
/* 249:    */   
/* 250:    */   public void postProcessDistances(double[] distances)
/* 251:    */   {
/* 252:512 */     this.m_Distance.postProcessDistances(distances);
/* 253:    */   }
/* 254:    */   
/* 255:    */   public void update(Instance ins)
/* 256:    */   {
/* 257:    */     try
/* 258:    */     {
/* 259:523 */       this.m_Remove.input(ins);
/* 260:524 */       this.m_Filter.input(this.m_Remove.output());
/* 261:525 */       this.m_Distance.update(this.m_Filter.output());
/* 262:    */     }
/* 263:    */     catch (Exception e)
/* 264:    */     {
/* 265:527 */       e.printStackTrace();
/* 266:    */     }
/* 267:    */   }
/* 268:    */   
/* 269:    */   public void clean()
/* 270:    */   {
/* 271:536 */     this.m_Distance.clean();
/* 272:    */   }
/* 273:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.FilteredDistance
 * JD-Core Version:    0.7.0.1
 */