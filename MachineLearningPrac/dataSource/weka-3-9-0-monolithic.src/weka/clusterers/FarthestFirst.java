/*   1:    */ package weka.clusterers;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.TechnicalInformation;
/*  15:    */ import weka.core.TechnicalInformation.Field;
/*  16:    */ import weka.core.TechnicalInformation.Type;
/*  17:    */ import weka.core.TechnicalInformationHandler;
/*  18:    */ import weka.core.Utils;
/*  19:    */ import weka.filters.Filter;
/*  20:    */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*  21:    */ 
/*  22:    */ public class FarthestFirst
/*  23:    */   extends RandomizableClusterer
/*  24:    */   implements TechnicalInformationHandler
/*  25:    */ {
/*  26:    */   static final long serialVersionUID = 7499838100631329509L;
/*  27:    */   protected Instances m_instances;
/*  28:    */   protected ReplaceMissingValues m_ReplaceMissingFilter;
/*  29:123 */   protected int m_NumClusters = 2;
/*  30:    */   protected Instances m_ClusterCentroids;
/*  31:    */   private double[] m_Min;
/*  32:    */   private double[] m_Max;
/*  33:    */   
/*  34:    */   public String globalInfo()
/*  35:    */   {
/*  36:147 */     return "Cluster data using the FarthestFirst algorithm.\n\nFor more information see:\n\n" + getTechnicalInformation().toString() + "\n\n" + "Notes:\n" + "- works as a fast simple approximate clusterer\n" + "- modelled after SimpleKMeans, might be a useful initializer for it";
/*  37:    */   }
/*  38:    */   
/*  39:    */   public TechnicalInformation getTechnicalInformation()
/*  40:    */   {
/*  41:166 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  42:167 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Hochbaum and Shmoys");
/*  43:168 */     result.setValue(TechnicalInformation.Field.YEAR, "1985");
/*  44:169 */     result.setValue(TechnicalInformation.Field.TITLE, "A best possible heuristic for the k-center problem");
/*  45:    */     
/*  46:171 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Mathematics of Operations Research");
/*  47:172 */     result.setValue(TechnicalInformation.Field.VOLUME, "10");
/*  48:173 */     result.setValue(TechnicalInformation.Field.NUMBER, "2");
/*  49:174 */     result.setValue(TechnicalInformation.Field.PAGES, "180-184");
/*  50:    */     
/*  51:176 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/*  52:177 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Sanjoy Dasgupta");
/*  53:178 */     additional.setValue(TechnicalInformation.Field.TITLE, "Performance Guarantees for Hierarchical Clustering");
/*  54:    */     
/*  55:180 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "15th Annual Conference on Computational Learning Theory");
/*  56:    */     
/*  57:182 */     additional.setValue(TechnicalInformation.Field.YEAR, "2002");
/*  58:183 */     additional.setValue(TechnicalInformation.Field.PAGES, "351-363");
/*  59:184 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*  60:    */     
/*  61:186 */     return result;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public Capabilities getCapabilities()
/*  65:    */   {
/*  66:196 */     Capabilities result = super.getCapabilities();
/*  67:197 */     result.disableAll();
/*  68:198 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  69:    */     
/*  70:    */ 
/*  71:201 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  72:202 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  73:203 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  74:204 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  75:    */     
/*  76:206 */     return result;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void buildClusterer(Instances data)
/*  80:    */     throws Exception
/*  81:    */   {
/*  82:220 */     getCapabilities().testWithFail(data);
/*  83:    */     
/*  84:    */ 
/*  85:    */ 
/*  86:224 */     this.m_ReplaceMissingFilter = new ReplaceMissingValues();
/*  87:225 */     this.m_ReplaceMissingFilter.setInputFormat(data);
/*  88:226 */     this.m_instances = Filter.useFilter(data, this.m_ReplaceMissingFilter);
/*  89:    */     
/*  90:228 */     initMinMax(this.m_instances);
/*  91:    */     
/*  92:230 */     this.m_ClusterCentroids = new Instances(this.m_instances, this.m_NumClusters);
/*  93:    */     
/*  94:232 */     int n = this.m_instances.numInstances();
/*  95:233 */     Random r = new Random(getSeed());
/*  96:234 */     boolean[] selected = new boolean[n];
/*  97:235 */     double[] minDistance = new double[n];
/*  98:237 */     for (int i = 0; i < n; i++) {
/*  99:238 */       minDistance[i] = 1.7976931348623157E+308D;
/* 100:    */     }
/* 101:241 */     int firstI = r.nextInt(n);
/* 102:242 */     this.m_ClusterCentroids.add(this.m_instances.instance(firstI));
/* 103:243 */     selected[firstI] = true;
/* 104:    */     
/* 105:245 */     updateMinDistance(minDistance, selected, this.m_instances, this.m_instances.instance(firstI));
/* 106:248 */     if (this.m_NumClusters > n) {
/* 107:249 */       this.m_NumClusters = n;
/* 108:    */     }
/* 109:252 */     for (int i = 1; i < this.m_NumClusters; i++)
/* 110:    */     {
/* 111:253 */       int nextI = farthestAway(minDistance, selected);
/* 112:254 */       this.m_ClusterCentroids.add(this.m_instances.instance(nextI));
/* 113:255 */       selected[nextI] = true;
/* 114:256 */       updateMinDistance(minDistance, selected, this.m_instances, this.m_instances.instance(nextI));
/* 115:    */     }
/* 116:260 */     this.m_instances = new Instances(this.m_instances, 0);
/* 117:    */   }
/* 118:    */   
/* 119:    */   protected void updateMinDistance(double[] minDistance, boolean[] selected, Instances data, Instance center)
/* 120:    */   {
/* 121:267 */     for (int i = 0; i < selected.length; i++) {
/* 122:268 */       if (selected[i] == 0)
/* 123:    */       {
/* 124:269 */         double d = distance(center, data.instance(i));
/* 125:270 */         if (d < minDistance[i]) {
/* 126:271 */           minDistance[i] = d;
/* 127:    */         }
/* 128:    */       }
/* 129:    */     }
/* 130:    */   }
/* 131:    */   
/* 132:    */   protected int farthestAway(double[] minDistance, boolean[] selected)
/* 133:    */   {
/* 134:278 */     double maxDistance = -1.0D;
/* 135:279 */     int maxI = -1;
/* 136:280 */     for (int i = 0; i < selected.length; i++) {
/* 137:281 */       if ((selected[i] == 0) && 
/* 138:282 */         (maxDistance < minDistance[i]))
/* 139:    */       {
/* 140:283 */         maxDistance = minDistance[i];
/* 141:284 */         maxI = i;
/* 142:    */       }
/* 143:    */     }
/* 144:288 */     return maxI;
/* 145:    */   }
/* 146:    */   
/* 147:    */   protected void initMinMax(Instances data)
/* 148:    */   {
/* 149:292 */     this.m_Min = new double[data.numAttributes()];
/* 150:293 */     this.m_Max = new double[data.numAttributes()];
/* 151:294 */     for (int i = 0; i < data.numAttributes(); i++)
/* 152:    */     {
/* 153:295 */       double tmp43_40 = (0.0D / 0.0D);this.m_Max[i] = tmp43_40;this.m_Min[i] = tmp43_40;
/* 154:    */     }
/* 155:298 */     for (int i = 0; i < data.numInstances(); i++) {
/* 156:299 */       updateMinMax(data.instance(i));
/* 157:    */     }
/* 158:    */   }
/* 159:    */   
/* 160:    */   private void updateMinMax(Instance instance)
/* 161:    */   {
/* 162:311 */     for (int j = 0; j < instance.numAttributes(); j++) {
/* 163:312 */       if (Double.isNaN(this.m_Min[j]))
/* 164:    */       {
/* 165:313 */         this.m_Min[j] = instance.value(j);
/* 166:314 */         this.m_Max[j] = instance.value(j);
/* 167:    */       }
/* 168:316 */       else if (instance.value(j) < this.m_Min[j])
/* 169:    */       {
/* 170:317 */         this.m_Min[j] = instance.value(j);
/* 171:    */       }
/* 172:319 */       else if (instance.value(j) > this.m_Max[j])
/* 173:    */       {
/* 174:320 */         this.m_Max[j] = instance.value(j);
/* 175:    */       }
/* 176:    */     }
/* 177:    */   }
/* 178:    */   
/* 179:    */   protected int clusterProcessedInstance(Instance instance)
/* 180:    */   {
/* 181:334 */     double minDist = 1.7976931348623157E+308D;
/* 182:335 */     int bestCluster = 0;
/* 183:336 */     for (int i = 0; i < this.m_NumClusters; i++)
/* 184:    */     {
/* 185:337 */       double dist = distance(instance, this.m_ClusterCentroids.instance(i));
/* 186:338 */       if (dist < minDist)
/* 187:    */       {
/* 188:339 */         minDist = dist;
/* 189:340 */         bestCluster = i;
/* 190:    */       }
/* 191:    */     }
/* 192:343 */     return bestCluster;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public int clusterInstance(Instance instance)
/* 196:    */     throws Exception
/* 197:    */   {
/* 198:356 */     this.m_ReplaceMissingFilter.input(instance);
/* 199:357 */     this.m_ReplaceMissingFilter.batchFinished();
/* 200:358 */     Instance inst = this.m_ReplaceMissingFilter.output();
/* 201:    */     
/* 202:360 */     return clusterProcessedInstance(inst);
/* 203:    */   }
/* 204:    */   
/* 205:    */   protected double distance(Instance first, Instance second)
/* 206:    */   {
/* 207:372 */     double distance = 0.0D;
/* 208:    */     
/* 209:    */ 
/* 210:375 */     int p1 = 0;
/* 211:375 */     for (int p2 = 0; (p1 < first.numValues()) || (p2 < second.numValues());)
/* 212:    */     {
/* 213:    */       int firstI;
/* 214:    */       int firstI;
/* 215:376 */       if (p1 >= first.numValues()) {
/* 216:377 */         firstI = this.m_instances.numAttributes();
/* 217:    */       } else {
/* 218:379 */         firstI = first.index(p1);
/* 219:    */       }
/* 220:    */       int secondI;
/* 221:    */       int secondI;
/* 222:381 */       if (p2 >= second.numValues()) {
/* 223:382 */         secondI = this.m_instances.numAttributes();
/* 224:    */       } else {
/* 225:384 */         secondI = second.index(p2);
/* 226:    */       }
/* 227:386 */       if (firstI == this.m_instances.classIndex())
/* 228:    */       {
/* 229:387 */         p1++;
/* 230:    */       }
/* 231:390 */       else if (secondI == this.m_instances.classIndex())
/* 232:    */       {
/* 233:391 */         p2++;
/* 234:    */       }
/* 235:    */       else
/* 236:    */       {
/* 237:    */         double diff;
/* 238:395 */         if (firstI == secondI)
/* 239:    */         {
/* 240:396 */           double diff = difference(firstI, first.valueSparse(p1), second.valueSparse(p2));
/* 241:397 */           p1++;
/* 242:398 */           p2++;
/* 243:    */         }
/* 244:399 */         else if (firstI > secondI)
/* 245:    */         {
/* 246:400 */           double diff = difference(secondI, 0.0D, second.valueSparse(p2));
/* 247:401 */           p2++;
/* 248:    */         }
/* 249:    */         else
/* 250:    */         {
/* 251:403 */           diff = difference(firstI, first.valueSparse(p1), 0.0D);
/* 252:404 */           p1++;
/* 253:    */         }
/* 254:406 */         distance += diff * diff;
/* 255:    */       }
/* 256:    */     }
/* 257:409 */     return Math.sqrt(distance / this.m_instances.numAttributes());
/* 258:    */   }
/* 259:    */   
/* 260:    */   protected double difference(int index, double val1, double val2)
/* 261:    */   {
/* 262:417 */     switch (this.m_instances.attribute(index).type())
/* 263:    */     {
/* 264:    */     case 1: 
/* 265:421 */       if ((Utils.isMissingValue(val1)) || (Utils.isMissingValue(val2)) || ((int)val1 != (int)val2)) {
/* 266:423 */         return 1.0D;
/* 267:    */       }
/* 268:425 */       return 0.0D;
/* 269:    */     case 0: 
/* 270:430 */       if ((Utils.isMissingValue(val1)) || (Utils.isMissingValue(val2)))
/* 271:    */       {
/* 272:431 */         if ((Utils.isMissingValue(val1)) && (Utils.isMissingValue(val2))) {
/* 273:432 */           return 1.0D;
/* 274:    */         }
/* 275:    */         double diff;
/* 276:    */         double diff;
/* 277:435 */         if (Utils.isMissingValue(val2)) {
/* 278:436 */           diff = norm(val1, index);
/* 279:    */         } else {
/* 280:438 */           diff = norm(val2, index);
/* 281:    */         }
/* 282:440 */         if (diff < 0.5D) {
/* 283:441 */           diff = 1.0D - diff;
/* 284:    */         }
/* 285:443 */         return diff;
/* 286:    */       }
/* 287:446 */       return norm(val1, index) - norm(val2, index);
/* 288:    */     }
/* 289:449 */     return 0.0D;
/* 290:    */   }
/* 291:    */   
/* 292:    */   protected double norm(double x, int i)
/* 293:    */   {
/* 294:462 */     if ((Double.isNaN(this.m_Min[i])) || (Utils.eq(this.m_Max[i], this.m_Min[i]))) {
/* 295:463 */       return 0.0D;
/* 296:    */     }
/* 297:465 */     return (x - this.m_Min[i]) / (this.m_Max[i] - this.m_Min[i]);
/* 298:    */   }
/* 299:    */   
/* 300:    */   public int numberOfClusters()
/* 301:    */     throws Exception
/* 302:    */   {
/* 303:477 */     return this.m_NumClusters;
/* 304:    */   }
/* 305:    */   
/* 306:    */   public Instances getClusterCentroids()
/* 307:    */   {
/* 308:486 */     return this.m_ClusterCentroids;
/* 309:    */   }
/* 310:    */   
/* 311:    */   public Enumeration<Option> listOptions()
/* 312:    */   {
/* 313:496 */     Vector<Option> result = new Vector();
/* 314:    */     
/* 315:498 */     result.addElement(new Option("\tnumber of clusters. (default = 2).", "N", 1, "-N <num>"));
/* 316:    */     
/* 317:    */ 
/* 318:501 */     result.addAll(Collections.list(super.listOptions()));
/* 319:    */     
/* 320:503 */     return result.elements();
/* 321:    */   }
/* 322:    */   
/* 323:    */   public String numClustersTipText()
/* 324:    */   {
/* 325:513 */     return "set number of clusters";
/* 326:    */   }
/* 327:    */   
/* 328:    */   public void setNumClusters(int n)
/* 329:    */     throws Exception
/* 330:    */   {
/* 331:523 */     if (n < 0) {
/* 332:524 */       throw new Exception("Number of clusters must be > 0");
/* 333:    */     }
/* 334:526 */     this.m_NumClusters = n;
/* 335:    */   }
/* 336:    */   
/* 337:    */   public int getNumClusters()
/* 338:    */   {
/* 339:535 */     return this.m_NumClusters;
/* 340:    */   }
/* 341:    */   
/* 342:    */   public void setOptions(String[] options)
/* 343:    */     throws Exception
/* 344:    */   {
/* 345:560 */     String optionString = Utils.getOption('N', options);
/* 346:562 */     if (optionString.length() != 0) {
/* 347:563 */       setNumClusters(Integer.parseInt(optionString));
/* 348:    */     }
/* 349:566 */     super.setOptions(options);
/* 350:    */     
/* 351:568 */     Utils.checkForRemainingOptions(options);
/* 352:    */   }
/* 353:    */   
/* 354:    */   public String[] getOptions()
/* 355:    */   {
/* 356:579 */     Vector<String> result = new Vector();
/* 357:    */     
/* 358:581 */     result.add("-N");
/* 359:582 */     result.add("" + getNumClusters());
/* 360:    */     
/* 361:584 */     Collections.addAll(result, super.getOptions());
/* 362:    */     
/* 363:586 */     return (String[])result.toArray(new String[result.size()]);
/* 364:    */   }
/* 365:    */   
/* 366:    */   public String toString()
/* 367:    */   {
/* 368:596 */     StringBuffer temp = new StringBuffer();
/* 369:    */     
/* 370:598 */     temp.append("\nFarthestFirst\n==============\n");
/* 371:    */     
/* 372:600 */     temp.append("\nCluster centroids:\n");
/* 373:601 */     for (int i = 0; i < this.m_NumClusters; i++)
/* 374:    */     {
/* 375:602 */       temp.append("\nCluster " + i + "\n\t");
/* 376:603 */       for (int j = 0; j < this.m_ClusterCentroids.numAttributes(); j++) {
/* 377:604 */         if (this.m_ClusterCentroids.attribute(j).isNominal()) {
/* 378:605 */           temp.append(" " + this.m_ClusterCentroids.attribute(j).value((int)this.m_ClusterCentroids.instance(i).value(j)));
/* 379:    */         } else {
/* 380:609 */           temp.append(" " + this.m_ClusterCentroids.instance(i).value(j));
/* 381:    */         }
/* 382:    */       }
/* 383:    */     }
/* 384:613 */     temp.append("\n\n");
/* 385:614 */     return temp.toString();
/* 386:    */   }
/* 387:    */   
/* 388:    */   public String getRevision()
/* 389:    */   {
/* 390:624 */     return RevisionUtils.extract("$Revision: 10453 $");
/* 391:    */   }
/* 392:    */   
/* 393:    */   public static void main(String[] argv)
/* 394:    */   {
/* 395:635 */     runClusterer(new FarthestFirst(), argv);
/* 396:    */   }
/* 397:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.FarthestFirst
 * JD-Core Version:    0.7.0.1
 */