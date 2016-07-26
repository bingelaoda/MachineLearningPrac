/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.clusterers.AbstractDensityBasedClusterer;
/*   8:    */ import weka.clusterers.DensityBasedClusterer;
/*   9:    */ import weka.clusterers.EM;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.Capabilities;
/*  12:    */ import weka.core.DenseInstance;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.OptionHandler;
/*  17:    */ import weka.core.Range;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.Utils;
/*  20:    */ import weka.filters.Filter;
/*  21:    */ import weka.filters.UnsupervisedFilter;
/*  22:    */ 
/*  23:    */ public class ClusterMembership
/*  24:    */   extends Filter
/*  25:    */   implements UnsupervisedFilter, OptionHandler
/*  26:    */ {
/*  27:    */   static final long serialVersionUID = 6675702504667714026L;
/*  28: 86 */   protected DensityBasedClusterer m_clusterer = new EM();
/*  29:    */   protected DensityBasedClusterer[] m_clusterers;
/*  30:    */   protected Range m_ignoreAttributesRange;
/*  31:    */   protected Filter m_removeAttributes;
/*  32:    */   protected double[] m_priors;
/*  33:    */   
/*  34:    */   public Capabilities getCapabilities()
/*  35:    */   {
/*  36:108 */     Capabilities result = this.m_clusterer.getCapabilities();
/*  37:109 */     result.enableAllClasses();
/*  38:    */     
/*  39:111 */     result.setMinimumNumberInstances(0);
/*  40:    */     
/*  41:113 */     return result;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public Capabilities getCapabilities(Instances data)
/*  45:    */   {
/*  46:128 */     Instances newData = new Instances(data, 0);
/*  47:129 */     newData.setClassIndex(-1);
/*  48:    */     
/*  49:131 */     return super.getCapabilities(newData);
/*  50:    */   }
/*  51:    */   
/*  52:    */   protected void testInputFormat(Instances instanceInfo)
/*  53:    */     throws Exception
/*  54:    */   {
/*  55:142 */     getCapabilities(instanceInfo).testWithFail(removeIgnored(instanceInfo));
/*  56:    */   }
/*  57:    */   
/*  58:    */   public boolean setInputFormat(Instances instanceInfo)
/*  59:    */     throws Exception
/*  60:    */   {
/*  61:157 */     super.setInputFormat(instanceInfo);
/*  62:158 */     this.m_removeAttributes = null;
/*  63:159 */     this.m_priors = null;
/*  64:    */     
/*  65:161 */     return false;
/*  66:    */   }
/*  67:    */   
/*  68:    */   protected Instances removeIgnored(Instances data)
/*  69:    */     throws Exception
/*  70:    */   {
/*  71:172 */     Instances result = data;
/*  72:174 */     if ((this.m_ignoreAttributesRange != null) || (data.classIndex() >= 0))
/*  73:    */     {
/*  74:175 */       result = new Instances(data);
/*  75:176 */       this.m_removeAttributes = new Remove();
/*  76:177 */       String rangeString = "";
/*  77:178 */       if (this.m_ignoreAttributesRange != null) {
/*  78:179 */         rangeString = rangeString + this.m_ignoreAttributesRange.getRanges();
/*  79:    */       }
/*  80:181 */       if (data.classIndex() >= 0) {
/*  81:182 */         if (rangeString.length() > 0) {
/*  82:183 */           rangeString = rangeString + "," + (data.classIndex() + 1);
/*  83:    */         } else {
/*  84:185 */           rangeString = "" + (data.classIndex() + 1);
/*  85:    */         }
/*  86:    */       }
/*  87:188 */       ((Remove)this.m_removeAttributes).setAttributeIndices(rangeString);
/*  88:189 */       ((Remove)this.m_removeAttributes).setInvertSelection(false);
/*  89:190 */       this.m_removeAttributes.setInputFormat(data);
/*  90:191 */       result = Filter.useFilter(data, this.m_removeAttributes);
/*  91:    */     }
/*  92:194 */     return result;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public boolean batchFinished()
/*  96:    */     throws Exception
/*  97:    */   {
/*  98:206 */     if (getInputFormat() == null) {
/*  99:207 */       throw new IllegalStateException("No input instance format defined");
/* 100:    */     }
/* 101:210 */     if (outputFormatPeek() == null)
/* 102:    */     {
/* 103:211 */       Instances toFilter = getInputFormat();
/* 104:    */       Instances[] toFilterIgnoringAttributes;
/* 105:215 */       if ((toFilter.classIndex() >= 0) && (toFilter.classAttribute().isNominal()))
/* 106:    */       {
/* 107:216 */         Instances[] toFilterIgnoringAttributes = new Instances[toFilter.numClasses()];
/* 108:217 */         for (int i = 0; i < toFilter.numClasses(); i++) {
/* 109:218 */           toFilterIgnoringAttributes[i] = new Instances(toFilter, toFilter.numInstances());
/* 110:    */         }
/* 111:221 */         for (int i = 0; i < toFilter.numInstances(); i++) {
/* 112:222 */           toFilterIgnoringAttributes[((int)toFilter.instance(i).classValue())].add(toFilter.instance(i));
/* 113:    */         }
/* 114:225 */         this.m_priors = new double[toFilter.numClasses()];
/* 115:226 */         for (int i = 0; i < toFilter.numClasses(); i++)
/* 116:    */         {
/* 117:227 */           toFilterIgnoringAttributes[i].compactify();
/* 118:228 */           this.m_priors[i] = toFilterIgnoringAttributes[i].sumOfWeights();
/* 119:    */         }
/* 120:230 */         Utils.normalize(this.m_priors);
/* 121:    */       }
/* 122:    */       else
/* 123:    */       {
/* 124:232 */         toFilterIgnoringAttributes = new Instances[1];
/* 125:233 */         toFilterIgnoringAttributes[0] = toFilter;
/* 126:234 */         this.m_priors = new double[1];
/* 127:235 */         this.m_priors[0] = 1.0D;
/* 128:    */       }
/* 129:239 */       for (int i = 0; i < toFilterIgnoringAttributes.length; i++) {
/* 130:240 */         toFilterIgnoringAttributes[i] = removeIgnored(toFilterIgnoringAttributes[i]);
/* 131:    */       }
/* 132:244 */       if ((toFilter.classIndex() <= 0) || (!toFilter.classAttribute().isNominal()))
/* 133:    */       {
/* 134:246 */         this.m_clusterers = AbstractDensityBasedClusterer.makeCopies(this.m_clusterer, 1);
/* 135:247 */         this.m_clusterers[0].buildClusterer(toFilterIgnoringAttributes[0]);
/* 136:    */       }
/* 137:    */       else
/* 138:    */       {
/* 139:249 */         this.m_clusterers = AbstractDensityBasedClusterer.makeCopies(this.m_clusterer, toFilter.numClasses());
/* 140:251 */         for (int i = 0; i < this.m_clusterers.length; i++) {
/* 141:252 */           if (toFilterIgnoringAttributes[i].numInstances() == 0) {
/* 142:253 */             this.m_clusterers[i] = null;
/* 143:    */           } else {
/* 144:255 */             this.m_clusterers[i].buildClusterer(toFilterIgnoringAttributes[i]);
/* 145:    */           }
/* 146:    */         }
/* 147:    */       }
/* 148:261 */       ArrayList<Attribute> attInfo = new ArrayList();
/* 149:262 */       for (int j = 0; j < this.m_clusterers.length; j++) {
/* 150:263 */         if (this.m_clusterers[j] != null) {
/* 151:264 */           for (int i = 0; i < this.m_clusterers[j].numberOfClusters(); i++) {
/* 152:265 */             attInfo.add(new Attribute("pCluster_" + j + "_" + i));
/* 153:    */           }
/* 154:    */         }
/* 155:    */       }
/* 156:269 */       if (toFilter.classIndex() >= 0) {
/* 157:270 */         attInfo.add((Attribute)toFilter.classAttribute().copy());
/* 158:    */       }
/* 159:272 */       attInfo.trimToSize();
/* 160:273 */       Instances filtered = new Instances(toFilter.relationName() + "_clusterMembership", attInfo, 0);
/* 161:275 */       if (toFilter.classIndex() >= 0) {
/* 162:276 */         filtered.setClassIndex(filtered.numAttributes() - 1);
/* 163:    */       }
/* 164:278 */       setOutputFormat(filtered);
/* 165:281 */       for (int i = 0; i < toFilter.numInstances(); i++) {
/* 166:282 */         convertInstance(toFilter.instance(i));
/* 167:    */       }
/* 168:    */     }
/* 169:285 */     flushInput();
/* 170:    */     
/* 171:287 */     this.m_NewBatch = true;
/* 172:288 */     return numPendingOutput() != 0;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public boolean input(Instance instance)
/* 176:    */     throws Exception
/* 177:    */   {
/* 178:303 */     if (getInputFormat() == null) {
/* 179:304 */       throw new IllegalStateException("No input instance format defined");
/* 180:    */     }
/* 181:306 */     if (this.m_NewBatch)
/* 182:    */     {
/* 183:307 */       resetQueue();
/* 184:308 */       this.m_NewBatch = false;
/* 185:    */     }
/* 186:311 */     if (outputFormatPeek() != null)
/* 187:    */     {
/* 188:312 */       convertInstance(instance);
/* 189:313 */       return true;
/* 190:    */     }
/* 191:316 */     bufferInput(instance);
/* 192:317 */     return false;
/* 193:    */   }
/* 194:    */   
/* 195:    */   protected double[] logs2densities(int j, Instance in)
/* 196:    */     throws Exception
/* 197:    */   {
/* 198:330 */     double[] logs = this.m_clusterers[j].logJointDensitiesForInstance(in);
/* 199:332 */     for (int i = 0; i < logs.length; i++) {
/* 200:333 */       logs[i] += Math.log(this.m_priors[j]);
/* 201:    */     }
/* 202:335 */     return logs;
/* 203:    */   }
/* 204:    */   
/* 205:    */   protected void convertInstance(Instance instance)
/* 206:    */     throws Exception
/* 207:    */   {
/* 208:348 */     double[] instanceVals = new double[outputFormatPeek().numAttributes()];
/* 209:    */     double[] tempvals;
/* 210:350 */     if (instance.classIndex() >= 0) {
/* 211:351 */       tempvals = new double[outputFormatPeek().numAttributes() - 1];
/* 212:    */     } else {
/* 213:353 */       tempvals = new double[outputFormatPeek().numAttributes()];
/* 214:    */     }
/* 215:355 */     int pos = 0;
/* 216:356 */     for (int j = 0; j < this.m_clusterers.length; j++) {
/* 217:357 */       if (this.m_clusterers[j] != null)
/* 218:    */       {
/* 219:    */         double[] probs;
/* 220:    */         double[] probs;
/* 221:359 */         if (this.m_removeAttributes != null)
/* 222:    */         {
/* 223:360 */           this.m_removeAttributes.input(instance);
/* 224:361 */           probs = logs2densities(j, this.m_removeAttributes.output());
/* 225:    */         }
/* 226:    */         else
/* 227:    */         {
/* 228:363 */           probs = logs2densities(j, instance);
/* 229:    */         }
/* 230:365 */         System.arraycopy(probs, 0, tempvals, pos, probs.length);
/* 231:366 */         pos += probs.length;
/* 232:    */       }
/* 233:    */     }
/* 234:369 */     double[] tempvals = Utils.logs2probs(tempvals);
/* 235:370 */     System.arraycopy(tempvals, 0, instanceVals, 0, tempvals.length);
/* 236:371 */     if (instance.classIndex() >= 0) {
/* 237:372 */       instanceVals[(instanceVals.length - 1)] = instance.classValue();
/* 238:    */     }
/* 239:375 */     push(new DenseInstance(instance.weight(), instanceVals));
/* 240:    */   }
/* 241:    */   
/* 242:    */   public Enumeration<Option> listOptions()
/* 243:    */   {
/* 244:386 */     Vector<Option> newVector = new Vector(2);
/* 245:    */     
/* 246:388 */     newVector.addElement(new Option("\tFull name of clusterer to use. eg:\n\t\tweka.clusterers.EM\n\tAdditional options after the '--'.\n\t(default: weka.clusterers.EM)", "W", 1, "-W <clusterer name>"));
/* 247:    */     
/* 248:    */ 
/* 249:    */ 
/* 250:392 */     newVector.addElement(new Option("\tThe range of attributes the clusterer should ignore.\n\t(the class attribute is automatically ignored)", "I", 1, "-I <att1,att2-att4,...>"));
/* 251:    */     
/* 252:    */ 
/* 253:    */ 
/* 254:    */ 
/* 255:397 */     return newVector.elements();
/* 256:    */   }
/* 257:    */   
/* 258:    */   public void setOptions(String[] options)
/* 259:    */     throws Exception
/* 260:    */   {
/* 261:431 */     String clustererString = Utils.getOption('W', options);
/* 262:432 */     if (clustererString.length() == 0) {
/* 263:433 */       clustererString = EM.class.getName();
/* 264:    */     }
/* 265:435 */     setDensityBasedClusterer((DensityBasedClusterer)Utils.forName(DensityBasedClusterer.class, clustererString, Utils.partitionOptions(options)));
/* 266:    */     
/* 267:    */ 
/* 268:    */ 
/* 269:439 */     setIgnoredAttributeIndices(Utils.getOption('I', options));
/* 270:440 */     Utils.checkForRemainingOptions(options);
/* 271:    */   }
/* 272:    */   
/* 273:    */   public String[] getOptions()
/* 274:    */   {
/* 275:451 */     Vector<String> options = new Vector();
/* 276:453 */     if (!getIgnoredAttributeIndices().equals(""))
/* 277:    */     {
/* 278:454 */       options.add("-I");
/* 279:455 */       options.add(getIgnoredAttributeIndices());
/* 280:    */     }
/* 281:458 */     if (this.m_clusterer != null)
/* 282:    */     {
/* 283:459 */       options.add("-W");
/* 284:460 */       options.add(getDensityBasedClusterer().getClass().getName());
/* 285:    */     }
/* 286:463 */     if ((this.m_clusterer != null) && ((this.m_clusterer instanceof OptionHandler)))
/* 287:    */     {
/* 288:464 */       String[] clustererOptions = ((OptionHandler)this.m_clusterer).getOptions();
/* 289:465 */       if (clustererOptions.length > 0)
/* 290:    */       {
/* 291:466 */         options.add("--");
/* 292:467 */         Collections.addAll(options, clustererOptions);
/* 293:    */       }
/* 294:    */     }
/* 295:470 */     return (String[])options.toArray(new String[0]);
/* 296:    */   }
/* 297:    */   
/* 298:    */   public String globalInfo()
/* 299:    */   {
/* 300:481 */     return "A filter that uses a density-based clusterer to generate cluster membership values; filtered instances are composed of these values plus the class attribute (if set in the input data). If a (nominal) class attribute is set, the clusterer is run separately for each class. The class attribute (if set) and any user-specified attributes are ignored during the clustering operation";
/* 301:    */   }
/* 302:    */   
/* 303:    */   public String densityBasedClustererTipText()
/* 304:    */   {
/* 305:496 */     return "The clusterer that will generate membership values for the instances.";
/* 306:    */   }
/* 307:    */   
/* 308:    */   public void setDensityBasedClusterer(DensityBasedClusterer newClusterer)
/* 309:    */   {
/* 310:505 */     this.m_clusterer = newClusterer;
/* 311:    */   }
/* 312:    */   
/* 313:    */   public DensityBasedClusterer getDensityBasedClusterer()
/* 314:    */   {
/* 315:514 */     return this.m_clusterer;
/* 316:    */   }
/* 317:    */   
/* 318:    */   public String ignoredAttributeIndicesTipText()
/* 319:    */   {
/* 320:525 */     return "The range of attributes to be ignored by the clusterer. eg: first-3,5,9-last";
/* 321:    */   }
/* 322:    */   
/* 323:    */   public String getIgnoredAttributeIndices()
/* 324:    */   {
/* 325:535 */     if (this.m_ignoreAttributesRange == null) {
/* 326:536 */       return "";
/* 327:    */     }
/* 328:538 */     return this.m_ignoreAttributesRange.getRanges();
/* 329:    */   }
/* 330:    */   
/* 331:    */   public void setIgnoredAttributeIndices(String rangeList)
/* 332:    */   {
/* 333:552 */     if ((rangeList == null) || (rangeList.length() == 0))
/* 334:    */     {
/* 335:553 */       this.m_ignoreAttributesRange = null;
/* 336:    */     }
/* 337:    */     else
/* 338:    */     {
/* 339:555 */       this.m_ignoreAttributesRange = new Range();
/* 340:556 */       this.m_ignoreAttributesRange.setRanges(rangeList);
/* 341:    */     }
/* 342:    */   }
/* 343:    */   
/* 344:    */   public String getRevision()
/* 345:    */   {
/* 346:567 */     return RevisionUtils.extract("$Revision: 10215 $");
/* 347:    */   }
/* 348:    */   
/* 349:    */   public static void main(String[] argv)
/* 350:    */   {
/* 351:576 */     runFilter(new ClusterMembership(), argv);
/* 352:    */   }
/* 353:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.ClusterMembership
 * JD-Core Version:    0.7.0.1
 */