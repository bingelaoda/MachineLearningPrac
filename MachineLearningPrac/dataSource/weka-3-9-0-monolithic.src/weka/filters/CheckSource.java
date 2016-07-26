/*   1:    */ package weka.filters;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.Option;
/*  10:    */ import weka.core.OptionHandler;
/*  11:    */ import weka.core.RevisionHandler;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.Utils;
/*  14:    */ import weka.core.converters.ConverterUtils.DataSource;
/*  15:    */ 
/*  16:    */ public class CheckSource
/*  17:    */   implements OptionHandler, RevisionHandler
/*  18:    */ {
/*  19: 82 */   protected Filter m_Filter = null;
/*  20: 85 */   protected Filter m_SourceCode = null;
/*  21: 88 */   protected File m_Dataset = null;
/*  22: 91 */   protected int m_ClassIndex = -1;
/*  23:    */   
/*  24:    */   public Enumeration<Option> listOptions()
/*  25:    */   {
/*  26:100 */     Vector<Option> result = new Vector();
/*  27:    */     
/*  28:102 */     result.addElement(new Option("\tThe filter (incl. options) that was used to generate\n\tthe source code.", "W", 1, "-W <classname and options>"));
/*  29:    */     
/*  30:    */ 
/*  31:    */ 
/*  32:106 */     result.addElement(new Option("\tThe classname of the generated source code.", "S", 1, "-S <classname>"));
/*  33:    */     
/*  34:    */ 
/*  35:    */ 
/*  36:110 */     result.addElement(new Option("\tThe training set with which the source code was generated.", "t", 1, "-t <file>"));
/*  37:    */     
/*  38:    */ 
/*  39:    */ 
/*  40:114 */     result.addElement(new Option("\tThe class index of the training set. 'first' and 'last' are\n\tvalid indices.\n\t(default: none)", "c", 1, "-c <index>"));
/*  41:    */     
/*  42:    */ 
/*  43:    */ 
/*  44:118 */     return result.elements();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setOptions(String[] options)
/*  48:    */     throws Exception
/*  49:    */   {
/*  50:165 */     String tmpStr = Utils.getOption('W', options);
/*  51:166 */     if (tmpStr.length() > 0)
/*  52:    */     {
/*  53:167 */       String[] spec = Utils.splitOptions(tmpStr);
/*  54:168 */       if (spec.length == 0) {
/*  55:169 */         throw new IllegalArgumentException("Invalid filter specification string");
/*  56:    */       }
/*  57:172 */       String classname = spec[0];
/*  58:173 */       spec[0] = "";
/*  59:174 */       setFilter((Filter)Utils.forName(Filter.class, classname, spec));
/*  60:    */     }
/*  61:    */     else
/*  62:    */     {
/*  63:176 */       throw new Exception("No filter (classname + options) provided!");
/*  64:    */     }
/*  65:    */     String classname;
/*  66:    */     String[] spec;
/*  67:179 */     tmpStr = Utils.getOption('S', options);
/*  68:180 */     if (tmpStr.length() > 0)
/*  69:    */     {
/*  70:181 */       spec = Utils.splitOptions(tmpStr);
/*  71:182 */       if (spec.length != 1) {
/*  72:183 */         throw new IllegalArgumentException("Invalid source code specification string");
/*  73:    */       }
/*  74:186 */       classname = spec[0];
/*  75:187 */       spec[0] = "";
/*  76:188 */       setSourceCode((Filter)Utils.forName(Filter.class, classname, spec));
/*  77:    */     }
/*  78:    */     else
/*  79:    */     {
/*  80:190 */       throw new Exception("No source code (classname) provided!");
/*  81:    */     }
/*  82:193 */     tmpStr = Utils.getOption('t', options);
/*  83:194 */     if (tmpStr.length() != 0) {
/*  84:195 */       setDataset(new File(tmpStr));
/*  85:    */     } else {
/*  86:197 */       throw new Exception("No dataset provided!");
/*  87:    */     }
/*  88:200 */     tmpStr = Utils.getOption('c', options);
/*  89:201 */     if (tmpStr.length() != 0)
/*  90:    */     {
/*  91:202 */       if (tmpStr.equals("first")) {
/*  92:203 */         setClassIndex(0);
/*  93:204 */       } else if (tmpStr.equals("last")) {
/*  94:205 */         setClassIndex(-2);
/*  95:    */       } else {
/*  96:207 */         setClassIndex(Integer.parseInt(tmpStr) - 1);
/*  97:    */       }
/*  98:    */     }
/*  99:    */     else {
/* 100:210 */       setClassIndex(-1);
/* 101:    */     }
/* 102:    */   }
/* 103:    */   
/* 104:    */   public String[] getOptions()
/* 105:    */   {
/* 106:223 */     Vector<String> result = new Vector();
/* 107:225 */     if (getFilter() != null)
/* 108:    */     {
/* 109:226 */       result.add("-W");
/* 110:227 */       result.add(getFilter().getClass().getName() + " " + Utils.joinOptions(getFilter().getOptions()));
/* 111:    */     }
/* 112:231 */     if (getSourceCode() != null)
/* 113:    */     {
/* 114:232 */       result.add("-S");
/* 115:233 */       result.add(getSourceCode().getClass().getName());
/* 116:    */     }
/* 117:236 */     if (getDataset() != null)
/* 118:    */     {
/* 119:237 */       result.add("-t");
/* 120:238 */       result.add(this.m_Dataset.getAbsolutePath());
/* 121:    */     }
/* 122:241 */     if (getClassIndex() != -1)
/* 123:    */     {
/* 124:242 */       result.add("-c");
/* 125:243 */       if (getClassIndex() == -2) {
/* 126:244 */         result.add("last");
/* 127:245 */       } else if (getClassIndex() == 0) {
/* 128:246 */         result.add("first");
/* 129:    */       } else {
/* 130:248 */         result.add("" + (getClassIndex() + 1));
/* 131:    */       }
/* 132:    */     }
/* 133:252 */     return (String[])result.toArray(new String[result.size()]);
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void setFilter(Filter value)
/* 137:    */   {
/* 138:261 */     this.m_Filter = value;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public Filter getFilter()
/* 142:    */   {
/* 143:270 */     return this.m_Filter;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public void setSourceCode(Filter value)
/* 147:    */   {
/* 148:279 */     this.m_SourceCode = value;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public Filter getSourceCode()
/* 152:    */   {
/* 153:288 */     return this.m_SourceCode;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void setDataset(File value)
/* 157:    */   {
/* 158:297 */     if (!value.exists()) {
/* 159:298 */       throw new IllegalArgumentException("Dataset '" + value.getAbsolutePath() + "' does not exist!");
/* 160:    */     }
/* 161:301 */     this.m_Dataset = value;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public File getDataset()
/* 165:    */   {
/* 166:311 */     return this.m_Dataset;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void setClassIndex(int value)
/* 170:    */   {
/* 171:320 */     this.m_ClassIndex = value;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public int getClassIndex()
/* 175:    */   {
/* 176:329 */     return this.m_ClassIndex;
/* 177:    */   }
/* 178:    */   
/* 179:    */   protected boolean compare(Instance inst1, Instance inst2)
/* 180:    */   {
/* 181:344 */     boolean result = inst1.numAttributes() == inst2.numAttributes();
/* 182:347 */     if (result) {
/* 183:348 */       for (int i = 0; i < inst1.numAttributes(); i++) {
/* 184:349 */         if ((!Double.isNaN(inst1.value(i))) || (!Double.isNaN(inst2.value(i)))) {
/* 185:353 */           if (inst1.value(i) != inst2.value(i))
/* 186:    */           {
/* 187:354 */             result = false;
/* 188:355 */             System.out.println("Values at position " + (i + 1) + " differ (Filter/Source code): " + inst1.value(i) + " != " + inst2.value(i));
/* 189:    */             
/* 190:    */ 
/* 191:358 */             break;
/* 192:    */           }
/* 193:    */         }
/* 194:    */       }
/* 195:    */     }
/* 196:363 */     return result;
/* 197:    */   }
/* 198:    */   
/* 199:    */   protected boolean compare(Instances inst1, Instances inst2)
/* 200:    */   {
/* 201:378 */     boolean result = inst1.numInstances() == inst2.numInstances();
/* 202:381 */     if (result) {
/* 203:382 */       for (int i = 0; i < inst1.numInstances(); i++)
/* 204:    */       {
/* 205:383 */         result = compare(inst1.instance(i), inst2.instance(i));
/* 206:384 */         if (!result)
/* 207:    */         {
/* 208:385 */           System.out.println("Values in line " + (i + 1) + " differ!");
/* 209:386 */           break;
/* 210:    */         }
/* 211:    */       }
/* 212:    */     }
/* 213:391 */     return result;
/* 214:    */   }
/* 215:    */   
/* 216:    */   public boolean execute()
/* 217:    */     throws Exception
/* 218:    */   {
/* 219:412 */     boolean result = true;
/* 220:415 */     if (getFilter() == null) {
/* 221:416 */       throw new Exception("No filter set!");
/* 222:    */     }
/* 223:418 */     if (getSourceCode() == null) {
/* 224:419 */       throw new Exception("No source code set!");
/* 225:    */     }
/* 226:421 */     if (getDataset() == null) {
/* 227:422 */       throw new Exception("No dataset set!");
/* 228:    */     }
/* 229:424 */     if (!getDataset().exists()) {
/* 230:425 */       throw new Exception("Dataset '" + getDataset().getAbsolutePath() + "' does not exist!");
/* 231:    */     }
/* 232:430 */     ConverterUtils.DataSource source = new ConverterUtils.DataSource(getDataset().getAbsolutePath());
/* 233:431 */     Instances data = source.getDataSet();
/* 234:432 */     if (getClassIndex() == -2) {
/* 235:433 */       data.setClassIndex(data.numAttributes() - 1);
/* 236:    */     } else {
/* 237:435 */       data.setClassIndex(getClassIndex());
/* 238:    */     }
/* 239:440 */     Filter filter = Filter.makeCopy(getFilter());
/* 240:441 */     filter.setInputFormat(data);
/* 241:442 */     Instances filteredInstances = Filter.useFilter(data, filter);
/* 242:    */     
/* 243:444 */     Filter filterSource = Filter.makeCopy(getSourceCode());
/* 244:445 */     filterSource.setInputFormat(data);
/* 245:446 */     Instances filteredInstancesSource = Filter.useFilter(data, filterSource);
/* 246:    */     
/* 247:448 */     result = compare(filteredInstances, filteredInstancesSource);
/* 248:451 */     if (result)
/* 249:    */     {
/* 250:452 */       filter = Filter.makeCopy(getFilter());
/* 251:453 */       filter.setInputFormat(data);
/* 252:454 */       Filter.useFilter(data, filter);
/* 253:    */       
/* 254:456 */       filterSource = Filter.makeCopy(getSourceCode());
/* 255:457 */       filterSource.setInputFormat(data);
/* 256:459 */       for (int i = 0; i < data.numInstances(); i++)
/* 257:    */       {
/* 258:460 */         filter.input(data.instance(i));
/* 259:461 */         filter.batchFinished();
/* 260:462 */         Instance filteredInstance = filter.output();
/* 261:    */         
/* 262:464 */         filterSource.input(data.instance(i));
/* 263:465 */         filterSource.batchFinished();
/* 264:466 */         Instance filteredInstanceSource = filterSource.output();
/* 265:468 */         if (!compare(filteredInstance, filteredInstanceSource)) {
/* 266:469 */           System.out.println(i + 1 + ". instance (Filter/Source code): " + filteredInstance + " != " + filteredInstanceSource);
/* 267:    */         }
/* 268:    */       }
/* 269:    */     }
/* 270:475 */     return result;
/* 271:    */   }
/* 272:    */   
/* 273:    */   public String getRevision()
/* 274:    */   {
/* 275:485 */     return RevisionUtils.extract("$Revision: 10210 $");
/* 276:    */   }
/* 277:    */   
/* 278:    */   public static void main(String[] args)
/* 279:    */     throws Exception
/* 280:    */   {
/* 281:499 */     CheckSource check = new CheckSource();
/* 282:500 */     if (Utils.getFlag('h', args))
/* 283:    */     {
/* 284:501 */       StringBuffer text = new StringBuffer();
/* 285:502 */       text.append("\nHelp requested:\n\n");
/* 286:503 */       Enumeration<Option> enm = check.listOptions();
/* 287:504 */       while (enm.hasMoreElements())
/* 288:    */       {
/* 289:505 */         Option option = (Option)enm.nextElement();
/* 290:506 */         text.append(option.synopsis() + "\n");
/* 291:507 */         text.append(option.description() + "\n");
/* 292:    */       }
/* 293:509 */       System.out.println("\n" + text + "\n");
/* 294:    */     }
/* 295:    */     else
/* 296:    */     {
/* 297:511 */       check.setOptions(args);
/* 298:512 */       if (check.execute()) {
/* 299:513 */         System.out.println("Tests OK!");
/* 300:    */       } else {
/* 301:515 */         System.out.println("Tests failed!");
/* 302:    */       }
/* 303:    */     }
/* 304:    */   }
/* 305:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.CheckSource
 * JD-Core Version:    0.7.0.1
 */