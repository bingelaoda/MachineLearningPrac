/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FileInputStream;
/*   5:    */ import java.io.FileNotFoundException;
/*   6:    */ import java.io.ObjectInputStream;
/*   7:    */ import java.util.ArrayList;
/*   8:    */ import java.util.Enumeration;
/*   9:    */ import java.util.Vector;
/*  10:    */ import weka.clusterers.AbstractClusterer;
/*  11:    */ import weka.clusterers.Clusterer;
/*  12:    */ import weka.clusterers.SimpleKMeans;
/*  13:    */ import weka.core.Attribute;
/*  14:    */ import weka.core.Capabilities;
/*  15:    */ import weka.core.DenseInstance;
/*  16:    */ import weka.core.Instance;
/*  17:    */ import weka.core.Instances;
/*  18:    */ import weka.core.Option;
/*  19:    */ import weka.core.OptionHandler;
/*  20:    */ import weka.core.Range;
/*  21:    */ import weka.core.RevisionUtils;
/*  22:    */ import weka.core.SparseInstance;
/*  23:    */ import weka.core.Utils;
/*  24:    */ import weka.core.WekaException;
/*  25:    */ import weka.filters.Filter;
/*  26:    */ import weka.filters.UnsupervisedFilter;
/*  27:    */ 
/*  28:    */ public class AddCluster
/*  29:    */   extends Filter
/*  30:    */   implements UnsupervisedFilter, OptionHandler
/*  31:    */ {
/*  32:    */   static final long serialVersionUID = 7414280611943807337L;
/*  33: 93 */   protected Clusterer m_Clusterer = new SimpleKMeans();
/*  34: 96 */   protected File m_SerializedClustererFile = new File(System.getProperty("user.dir"));
/*  35:100 */   protected Clusterer m_ActualClusterer = null;
/*  36:103 */   protected Range m_IgnoreAttributesRange = null;
/*  37:106 */   protected Filter m_removeAttributes = new Remove();
/*  38:    */   
/*  39:    */   public Capabilities getCapabilities(Instances data)
/*  40:    */   {
/*  41:120 */     Instances newData = new Instances(data, 0);
/*  42:121 */     newData.setClassIndex(-1);
/*  43:    */     
/*  44:123 */     return super.getCapabilities(newData);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public Capabilities getCapabilities()
/*  48:    */   {
/*  49:134 */     Capabilities result = this.m_Clusterer.getCapabilities();
/*  50:135 */     result.enableAllClasses();
/*  51:    */     
/*  52:137 */     result.setMinimumNumberInstances(0);
/*  53:    */     
/*  54:139 */     return result;
/*  55:    */   }
/*  56:    */   
/*  57:    */   protected void testInputFormat(Instances instanceInfo)
/*  58:    */     throws Exception
/*  59:    */   {
/*  60:150 */     getCapabilities(instanceInfo).testWithFail(removeIgnored(instanceInfo));
/*  61:    */   }
/*  62:    */   
/*  63:    */   public boolean setInputFormat(Instances instanceInfo)
/*  64:    */     throws Exception
/*  65:    */   {
/*  66:164 */     super.setInputFormat(instanceInfo);
/*  67:    */     
/*  68:166 */     this.m_removeAttributes = null;
/*  69:    */     
/*  70:168 */     return false;
/*  71:    */   }
/*  72:    */   
/*  73:    */   protected Instances removeIgnored(Instances data)
/*  74:    */     throws Exception
/*  75:    */   {
/*  76:179 */     Instances result = data;
/*  77:181 */     if ((this.m_IgnoreAttributesRange != null) || (data.classIndex() >= 0))
/*  78:    */     {
/*  79:182 */       this.m_removeAttributes = new Remove();
/*  80:183 */       String rangeString = "";
/*  81:184 */       if (this.m_IgnoreAttributesRange != null) {
/*  82:185 */         rangeString = rangeString + this.m_IgnoreAttributesRange.getRanges();
/*  83:    */       }
/*  84:187 */       if (data.classIndex() >= 0) {
/*  85:188 */         if (rangeString.length() > 0) {
/*  86:189 */           rangeString = rangeString + "," + (data.classIndex() + 1);
/*  87:    */         } else {
/*  88:191 */           rangeString = "" + (data.classIndex() + 1);
/*  89:    */         }
/*  90:    */       }
/*  91:194 */       ((Remove)this.m_removeAttributes).setAttributeIndices(rangeString);
/*  92:195 */       ((Remove)this.m_removeAttributes).setInvertSelection(false);
/*  93:196 */       this.m_removeAttributes.setInputFormat(data);
/*  94:197 */       result = Filter.useFilter(data, this.m_removeAttributes);
/*  95:    */     }
/*  96:200 */     return result;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public boolean batchFinished()
/* 100:    */     throws Exception
/* 101:    */   {
/* 102:211 */     if (getInputFormat() == null) {
/* 103:212 */       throw new IllegalStateException("No input instance format defined");
/* 104:    */     }
/* 105:215 */     Instances toFilter = getInputFormat();
/* 106:217 */     if (!isFirstBatchDone())
/* 107:    */     {
/* 108:219 */       Instances toFilterIgnoringAttributes = removeIgnored(toFilter);
/* 109:    */       
/* 110:    */ 
/* 111:222 */       File file = getSerializedClustererFile();
/* 112:223 */       if (!file.isDirectory())
/* 113:    */       {
/* 114:224 */         ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
/* 115:225 */         this.m_ActualClusterer = ((Clusterer)ois.readObject());
/* 116:226 */         Instances header = null;
/* 117:    */         try
/* 118:    */         {
/* 119:229 */           header = (Instances)ois.readObject();
/* 120:    */         }
/* 121:    */         catch (Exception e) {}
/* 122:233 */         ois.close();
/* 123:235 */         if ((header != null) && (!header.equalHeaders(toFilterIgnoringAttributes))) {
/* 124:237 */           throw new WekaException("Training header of clusterer and filter dataset don't match:\n" + header.equalHeadersMsg(toFilterIgnoringAttributes));
/* 125:    */         }
/* 126:    */       }
/* 127:    */       else
/* 128:    */       {
/* 129:242 */         this.m_ActualClusterer = AbstractClusterer.makeCopy(this.m_Clusterer);
/* 130:243 */         this.m_ActualClusterer.buildClusterer(toFilterIgnoringAttributes);
/* 131:    */       }
/* 132:247 */       Instances filtered = new Instances(toFilter, 0);
/* 133:248 */       ArrayList<String> nominal_values = new ArrayList(this.m_ActualClusterer.numberOfClusters());
/* 134:250 */       for (int i = 0; i < this.m_ActualClusterer.numberOfClusters(); i++) {
/* 135:251 */         nominal_values.add("cluster" + (i + 1));
/* 136:    */       }
/* 137:253 */       filtered.insertAttributeAt(new Attribute("cluster", nominal_values), filtered.numAttributes());
/* 138:    */       
/* 139:    */ 
/* 140:256 */       setOutputFormat(filtered);
/* 141:    */     }
/* 142:260 */     for (int i = 0; i < toFilter.numInstances(); i++) {
/* 143:261 */       convertInstance(toFilter.instance(i));
/* 144:    */     }
/* 145:264 */     flushInput();
/* 146:265 */     this.m_NewBatch = true;
/* 147:266 */     this.m_FirstBatchDone = true;
/* 148:    */     
/* 149:268 */     return numPendingOutput() != 0;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public boolean input(Instance instance)
/* 153:    */     throws Exception
/* 154:    */   {
/* 155:282 */     if (getInputFormat() == null) {
/* 156:283 */       throw new IllegalStateException("No input instance format defined");
/* 157:    */     }
/* 158:286 */     if (this.m_NewBatch)
/* 159:    */     {
/* 160:287 */       resetQueue();
/* 161:288 */       this.m_NewBatch = false;
/* 162:    */     }
/* 163:291 */     if (outputFormatPeek() != null)
/* 164:    */     {
/* 165:292 */       convertInstance(instance);
/* 166:293 */       return true;
/* 167:    */     }
/* 168:296 */     bufferInput(instance);
/* 169:297 */     return false;
/* 170:    */   }
/* 171:    */   
/* 172:    */   protected void convertInstance(Instance instance)
/* 173:    */     throws Exception
/* 174:    */   {
/* 175:309 */     Instance original = instance;
/* 176:    */     
/* 177:    */ 
/* 178:312 */     double[] instanceVals = new double[instance.numAttributes() + 1];
/* 179:313 */     for (int j = 0; j < instance.numAttributes(); j++) {
/* 180:314 */       instanceVals[j] = original.value(j);
/* 181:    */     }
/* 182:316 */     Instance filteredI = null;
/* 183:317 */     if (this.m_removeAttributes != null)
/* 184:    */     {
/* 185:318 */       this.m_removeAttributes.input(instance);
/* 186:319 */       filteredI = this.m_removeAttributes.output();
/* 187:    */     }
/* 188:    */     else
/* 189:    */     {
/* 190:321 */       filteredI = instance;
/* 191:    */     }
/* 192:    */     try
/* 193:    */     {
/* 194:326 */       instanceVals[instance.numAttributes()] = this.m_ActualClusterer.clusterInstance(filteredI);
/* 195:    */     }
/* 196:    */     catch (Exception e)
/* 197:    */     {
/* 198:330 */       instanceVals[instance.numAttributes()] = Utils.missingValue();
/* 199:    */     }
/* 200:    */     Instance processed;
/* 201:    */     Instance processed;
/* 202:334 */     if ((original instanceof SparseInstance)) {
/* 203:335 */       processed = new SparseInstance(original.weight(), instanceVals);
/* 204:    */     } else {
/* 205:337 */       processed = new DenseInstance(original.weight(), instanceVals);
/* 206:    */     }
/* 207:340 */     copyValues(processed, false, instance.dataset(), outputFormatPeek());
/* 208:    */     
/* 209:342 */     push(processed);
/* 210:    */   }
/* 211:    */   
/* 212:    */   public Enumeration<Option> listOptions()
/* 213:    */   {
/* 214:352 */     Vector<Option> result = new Vector(3);
/* 215:    */     
/* 216:354 */     result.addElement(new Option("\tFull class name of clusterer to use, followed\n\tby scheme options. eg:\n\t\t\"weka.clusterers.SimpleKMeans -N 3\"\n\t(default: weka.clusterers.SimpleKMeans)", "W", 1, "-W <clusterer specification>"));
/* 217:    */     
/* 218:    */ 
/* 219:    */ 
/* 220:    */ 
/* 221:    */ 
/* 222:    */ 
/* 223:361 */     result.addElement(new Option("\tInstead of building a clusterer on the data, one can also provide\n\ta serialized model and use that for adding the clusters.", "serialized", 1, "-serialized <file>"));
/* 224:    */     
/* 225:    */ 
/* 226:    */ 
/* 227:    */ 
/* 228:366 */     result.addElement(new Option("\tThe range of attributes the clusterer should ignore.\n", "I", 1, "-I <att1,att2-att4,...>"));
/* 229:    */     
/* 230:    */ 
/* 231:    */ 
/* 232:370 */     return result.elements();
/* 233:    */   }
/* 234:    */   
/* 235:    */   public void setOptions(String[] options)
/* 236:    */     throws Exception
/* 237:    */   {
/* 238:411 */     boolean serializedModel = false;
/* 239:412 */     String tmpStr = Utils.getOption("serialized", options);
/* 240:413 */     if (tmpStr.length() != 0)
/* 241:    */     {
/* 242:414 */       File file = new File(tmpStr);
/* 243:415 */       if (!file.exists()) {
/* 244:416 */         throw new FileNotFoundException("File '" + file.getAbsolutePath() + "' not found!");
/* 245:    */       }
/* 246:419 */       if (file.isDirectory()) {
/* 247:420 */         throw new FileNotFoundException("'" + file.getAbsolutePath() + "' points to a directory not a file!");
/* 248:    */       }
/* 249:423 */       setSerializedClustererFile(file);
/* 250:424 */       serializedModel = true;
/* 251:    */     }
/* 252:    */     else
/* 253:    */     {
/* 254:426 */       setSerializedClustererFile(null);
/* 255:    */     }
/* 256:429 */     if (!serializedModel)
/* 257:    */     {
/* 258:430 */       tmpStr = Utils.getOption('W', options);
/* 259:431 */       if (tmpStr.length() == 0) {
/* 260:432 */         tmpStr = SimpleKMeans.class.getName();
/* 261:    */       }
/* 262:434 */       String[] tmpOptions = Utils.splitOptions(tmpStr);
/* 263:435 */       if (tmpOptions.length == 0) {
/* 264:436 */         throw new Exception("Invalid clusterer specification string");
/* 265:    */       }
/* 266:438 */       tmpStr = tmpOptions[0];
/* 267:439 */       tmpOptions[0] = "";
/* 268:440 */       setClusterer(AbstractClusterer.forName(tmpStr, tmpOptions));
/* 269:    */     }
/* 270:443 */     setIgnoredAttributeIndices(Utils.getOption('I', options));
/* 271:    */     
/* 272:445 */     Utils.checkForRemainingOptions(options);
/* 273:    */   }
/* 274:    */   
/* 275:    */   public String[] getOptions()
/* 276:    */   {
/* 277:458 */     Vector<String> result = new Vector();
/* 278:    */     
/* 279:460 */     File file = getSerializedClustererFile();
/* 280:461 */     if ((file != null) && (!file.isDirectory()))
/* 281:    */     {
/* 282:462 */       result.add("-serialized");
/* 283:463 */       result.add(file.getAbsolutePath());
/* 284:    */     }
/* 285:    */     else
/* 286:    */     {
/* 287:465 */       result.add("-W");
/* 288:466 */       result.add(getClustererSpec());
/* 289:    */     }
/* 290:469 */     if (!getIgnoredAttributeIndices().equals(""))
/* 291:    */     {
/* 292:470 */       result.add("-I");
/* 293:471 */       result.add(getIgnoredAttributeIndices());
/* 294:    */     }
/* 295:474 */     return (String[])result.toArray(new String[result.size()]);
/* 296:    */   }
/* 297:    */   
/* 298:    */   public String globalInfo()
/* 299:    */   {
/* 300:484 */     return "A filter that adds a new nominal attribute representing the cluster assigned to each instance by the specified clustering algorithm.\nEither the clustering algorithm gets built with the first batch of data or one specifies are serialized clusterer model file to use instead.";
/* 301:    */   }
/* 302:    */   
/* 303:    */   public String clustererTipText()
/* 304:    */   {
/* 305:498 */     return "The clusterer to assign clusters with.";
/* 306:    */   }
/* 307:    */   
/* 308:    */   public void setClusterer(Clusterer clusterer)
/* 309:    */   {
/* 310:507 */     this.m_Clusterer = clusterer;
/* 311:    */   }
/* 312:    */   
/* 313:    */   public Clusterer getClusterer()
/* 314:    */   {
/* 315:516 */     return this.m_Clusterer;
/* 316:    */   }
/* 317:    */   
/* 318:    */   protected String getClustererSpec()
/* 319:    */   {
/* 320:526 */     Clusterer c = getClusterer();
/* 321:527 */     if ((c instanceof OptionHandler)) {
/* 322:528 */       return c.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)c).getOptions());
/* 323:    */     }
/* 324:531 */     return c.getClass().getName();
/* 325:    */   }
/* 326:    */   
/* 327:    */   public String ignoredAttributeIndicesTipText()
/* 328:    */   {
/* 329:541 */     return "The range of attributes to be ignored by the clusterer. eg: first-3,5,9-last";
/* 330:    */   }
/* 331:    */   
/* 332:    */   public String getIgnoredAttributeIndices()
/* 333:    */   {
/* 334:550 */     if (this.m_IgnoreAttributesRange == null) {
/* 335:551 */       return "";
/* 336:    */     }
/* 337:553 */     return this.m_IgnoreAttributesRange.getRanges();
/* 338:    */   }
/* 339:    */   
/* 340:    */   public void setIgnoredAttributeIndices(String rangeList)
/* 341:    */   {
/* 342:566 */     if ((rangeList == null) || (rangeList.length() == 0))
/* 343:    */     {
/* 344:567 */       this.m_IgnoreAttributesRange = null;
/* 345:    */     }
/* 346:    */     else
/* 347:    */     {
/* 348:569 */       this.m_IgnoreAttributesRange = new Range();
/* 349:570 */       this.m_IgnoreAttributesRange.setRanges(rangeList);
/* 350:    */     }
/* 351:    */   }
/* 352:    */   
/* 353:    */   public File getSerializedClustererFile()
/* 354:    */   {
/* 355:581 */     return this.m_SerializedClustererFile;
/* 356:    */   }
/* 357:    */   
/* 358:    */   public void setSerializedClustererFile(File value)
/* 359:    */   {
/* 360:591 */     if ((value == null) || (!value.exists())) {
/* 361:592 */       value = new File(System.getProperty("user.dir"));
/* 362:    */     }
/* 363:595 */     this.m_SerializedClustererFile = value;
/* 364:    */   }
/* 365:    */   
/* 366:    */   public String serializedClustererFileTipText()
/* 367:    */   {
/* 368:605 */     return "A file containing the serialized model of a built clusterer.";
/* 369:    */   }
/* 370:    */   
/* 371:    */   public String getRevision()
/* 372:    */   {
/* 373:615 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 374:    */   }
/* 375:    */   
/* 376:    */   public static void main(String[] argv)
/* 377:    */   {
/* 378:624 */     runFilter(new AddCluster(), argv);
/* 379:    */   }
/* 380:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.AddCluster
 * JD-Core Version:    0.7.0.1
 */