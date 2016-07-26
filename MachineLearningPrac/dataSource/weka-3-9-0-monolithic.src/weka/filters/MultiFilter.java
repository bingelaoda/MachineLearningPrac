/*   1:    */ package weka.filters;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.OptionHandler;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.Utils;
/*  14:    */ 
/*  15:    */ public class MultiFilter
/*  16:    */   extends SimpleStreamFilter
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = -6293720886005713120L;
/*  19: 67 */   protected Filter[] m_Filters = { new AllFilter() };
/*  20: 70 */   protected boolean m_Streamable = false;
/*  21: 73 */   protected boolean m_StreamableChecked = false;
/*  22:    */   
/*  23:    */   public String globalInfo()
/*  24:    */   {
/*  25: 83 */     return "Applies several filters successively. In case all supplied filters are StreamableFilters, it will act as a streamable one, too.";
/*  26:    */   }
/*  27:    */   
/*  28:    */   public Enumeration<Option> listOptions()
/*  29:    */   {
/*  30: 94 */     Vector<Option> result = new Vector();
/*  31:    */     
/*  32: 96 */     result.addElement(new Option("\tA filter to apply (can be specified multiple times).", "F", 1, "-F <classname [options]>"));
/*  33:    */     
/*  34:    */ 
/*  35:    */ 
/*  36:100 */     result.addAll(Collections.list(super.listOptions()));
/*  37:    */     
/*  38:102 */     return result.elements();
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setOptions(String[] options)
/*  42:    */     throws Exception
/*  43:    */   {
/*  44:134 */     super.setOptions(options);
/*  45:    */     
/*  46:136 */     Vector<Filter> filters = new Vector();
/*  47:    */     String tmpStr;
/*  48:137 */     while ((tmpStr = Utils.getOption("F", options)).length() != 0)
/*  49:    */     {
/*  50:138 */       String[] options2 = Utils.splitOptions(tmpStr);
/*  51:139 */       String filter = options2[0];
/*  52:140 */       options2[0] = "";
/*  53:141 */       filters.add((Filter)Utils.forName(Filter.class, filter, options2));
/*  54:    */     }
/*  55:145 */     if (filters.size() == 0) {
/*  56:146 */       filters.add(new AllFilter());
/*  57:    */     }
/*  58:149 */     setFilters((Filter[])filters.toArray(new Filter[filters.size()]));
/*  59:    */   }
/*  60:    */   
/*  61:    */   public String[] getOptions()
/*  62:    */   {
/*  63:163 */     Vector<String> result = new Vector();
/*  64:    */     
/*  65:165 */     String[] options = super.getOptions();
/*  66:166 */     for (int i = 0; i < options.length; i++) {
/*  67:167 */       result.add(options[i]);
/*  68:    */     }
/*  69:170 */     for (i = 0; i < getFilters().length; i++)
/*  70:    */     {
/*  71:171 */       result.add("-F");
/*  72:172 */       result.add(getFilterSpec(getFilter(i)));
/*  73:    */     }
/*  74:175 */     return (String[])result.toArray(new String[result.size()]);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public Capabilities getCapabilities()
/*  78:    */   {
/*  79:186 */     if (getFilters().length == 0)
/*  80:    */     {
/*  81:187 */       Capabilities result = super.getCapabilities();
/*  82:188 */       result.disableAll();
/*  83:    */       
/*  84:190 */       return result;
/*  85:    */     }
/*  86:192 */     return getFilters()[0].getCapabilities();
/*  87:    */   }
/*  88:    */   
/*  89:    */   protected void reset()
/*  90:    */   {
/*  91:204 */     super.reset();
/*  92:205 */     this.m_StreamableChecked = false;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void setFilters(Filter[] filters)
/*  96:    */   {
/*  97:216 */     this.m_Filters = filters;
/*  98:217 */     reset();
/*  99:    */   }
/* 100:    */   
/* 101:    */   public Filter[] getFilters()
/* 102:    */   {
/* 103:226 */     return this.m_Filters;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public String filtersTipText()
/* 107:    */   {
/* 108:236 */     return "The base filters to be used.";
/* 109:    */   }
/* 110:    */   
/* 111:    */   public Filter getFilter(int index)
/* 112:    */   {
/* 113:246 */     return this.m_Filters[index];
/* 114:    */   }
/* 115:    */   
/* 116:    */   protected String getFilterSpec(Filter filter)
/* 117:    */   {
/* 118:    */     String result;
/* 119:    */     String result;
/* 120:258 */     if (filter == null)
/* 121:    */     {
/* 122:259 */       result = "";
/* 123:    */     }
/* 124:    */     else
/* 125:    */     {
/* 126:261 */       result = filter.getClass().getName();
/* 127:262 */       if ((filter instanceof OptionHandler)) {
/* 128:263 */         result = result + " " + Utils.joinOptions(filter.getOptions());
/* 129:    */       }
/* 130:    */     }
/* 131:268 */     return result;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public boolean isStreamableFilter()
/* 135:    */   {
/* 136:279 */     if (!this.m_StreamableChecked)
/* 137:    */     {
/* 138:280 */       this.m_Streamable = true;
/* 139:281 */       this.m_StreamableChecked = true;
/* 140:283 */       for (int i = 0; i < getFilters().length; i++)
/* 141:    */       {
/* 142:284 */         if ((getFilter(i) instanceof MultiFilter)) {
/* 143:285 */           this.m_Streamable = ((MultiFilter)getFilter(i)).isStreamableFilter();
/* 144:286 */         } else if ((getFilter(i) instanceof StreamableFilter)) {
/* 145:287 */           this.m_Streamable = true;
/* 146:    */         } else {
/* 147:289 */           this.m_Streamable = false;
/* 148:    */         }
/* 149:292 */         if (!this.m_Streamable) {
/* 150:    */           break;
/* 151:    */         }
/* 152:    */       }
/* 153:297 */       if (getDebug()) {
/* 154:298 */         System.out.println("Streamable: " + this.m_Streamable);
/* 155:    */       }
/* 156:    */     }
/* 157:302 */     return this.m_Streamable;
/* 158:    */   }
/* 159:    */   
/* 160:    */   protected boolean hasImmediateOutputFormat()
/* 161:    */   {
/* 162:319 */     return isStreamableFilter();
/* 163:    */   }
/* 164:    */   
/* 165:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 166:    */     throws Exception
/* 167:    */   {
/* 168:342 */     Instances result = getInputFormat();
/* 169:344 */     for (int i = 0; i < getFilters().length; i++)
/* 170:    */     {
/* 171:345 */       if (!isFirstBatchDone()) {
/* 172:346 */         getFilter(i).setInputFormat(result);
/* 173:    */       }
/* 174:348 */       result = getFilter(i).getOutputFormat();
/* 175:    */     }
/* 176:351 */     return result;
/* 177:    */   }
/* 178:    */   
/* 179:    */   protected Instance process(Instance instance)
/* 180:    */     throws Exception
/* 181:    */   {
/* 182:367 */     Instance result = (Instance)instance.copy();
/* 183:369 */     for (int i = 0; i < getFilters().length; i++) {
/* 184:370 */       if (getFilter(i).input(result))
/* 185:    */       {
/* 186:371 */         result = getFilter(i).output();
/* 187:    */       }
/* 188:    */       else
/* 189:    */       {
/* 190:374 */         result = null;
/* 191:375 */         break;
/* 192:    */       }
/* 193:    */     }
/* 194:379 */     return result;
/* 195:    */   }
/* 196:    */   
/* 197:    */   protected Instances process(Instances instances)
/* 198:    */     throws Exception
/* 199:    */   {
/* 200:399 */     Instances result = instances;
/* 201:401 */     for (int i = 0; i < getFilters().length; i++)
/* 202:    */     {
/* 203:402 */       if (!isFirstBatchDone()) {
/* 204:403 */         getFilter(i).setInputFormat(result);
/* 205:    */       }
/* 206:405 */       result = Filter.useFilter(result, getFilter(i));
/* 207:    */     }
/* 208:408 */     return result;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public boolean batchFinished()
/* 212:    */     throws Exception
/* 213:    */   {
/* 214:423 */     super.batchFinished();
/* 215:425 */     for (int i = 0; i > getFilters().length; i++) {
/* 216:426 */       getFilter(i).batchFinished();
/* 217:    */     }
/* 218:429 */     return numPendingOutput() != 0;
/* 219:    */   }
/* 220:    */   
/* 221:    */   public boolean mayRemoveInstanceAfterFirstBatchDone()
/* 222:    */   {
/* 223:442 */     boolean result = false;
/* 224:444 */     for (Filter f : this.m_Filters) {
/* 225:445 */       result = (result) || (f.mayRemoveInstanceAfterFirstBatchDone());
/* 226:    */     }
/* 227:448 */     return result;
/* 228:    */   }
/* 229:    */   
/* 230:    */   public String getRevision()
/* 231:    */   {
/* 232:458 */     return RevisionUtils.extract("$Revision: 10210 $");
/* 233:    */   }
/* 234:    */   
/* 235:    */   public static void main(String[] args)
/* 236:    */   {
/* 237:467 */     runFilter(new MultiFilter(), args);
/* 238:    */   }
/* 239:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.MultiFilter
 * JD-Core Version:    0.7.0.1
 */