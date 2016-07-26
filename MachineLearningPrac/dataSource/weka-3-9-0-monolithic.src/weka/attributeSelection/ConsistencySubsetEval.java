/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.BitSet;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Hashtable;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.RevisionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.TechnicalInformation;
/*  16:    */ import weka.core.TechnicalInformation.Field;
/*  17:    */ import weka.core.TechnicalInformation.Type;
/*  18:    */ import weka.core.TechnicalInformationHandler;
/*  19:    */ import weka.core.Utils;
/*  20:    */ import weka.filters.Filter;
/*  21:    */ import weka.filters.supervised.attribute.Discretize;
/*  22:    */ 
/*  23:    */ public class ConsistencySubsetEval
/*  24:    */   extends ASEvaluation
/*  25:    */   implements SubsetEvaluator, TechnicalInformationHandler
/*  26:    */ {
/*  27:    */   static final long serialVersionUID = -2880323763295270402L;
/*  28:    */   private Instances m_trainInstances;
/*  29:    */   private int m_classIndex;
/*  30:    */   private int m_numAttribs;
/*  31:    */   private int m_numInstances;
/*  32:    */   private Discretize m_disTransform;
/*  33:    */   private Hashtable<hashKey, double[]> m_table;
/*  34:    */   
/*  35:    */   public class hashKey
/*  36:    */     implements Serializable, RevisionHandler
/*  37:    */   {
/*  38:    */     static final long serialVersionUID = 6144138512017017408L;
/*  39:    */     private final double[] attributes;
/*  40:    */     private final boolean[] missing;
/*  41:    */     private int key;
/*  42:    */     
/*  43:    */     public hashKey(Instance t, int numAtts)
/*  44:    */       throws Exception
/*  45:    */     {
/*  46:132 */       int cindex = t.classIndex();
/*  47:    */       
/*  48:134 */       this.key = -999;
/*  49:135 */       this.attributes = new double[numAtts];
/*  50:136 */       this.missing = new boolean[numAtts];
/*  51:137 */       for (int i = 0; i < numAtts; i++) {
/*  52:138 */         if (i == cindex) {
/*  53:139 */           this.missing[i] = true;
/*  54:141 */         } else if (!(this.missing[i] = t.isMissing(i))) {
/*  55:142 */           this.attributes[i] = t.value(i);
/*  56:    */         }
/*  57:    */       }
/*  58:    */     }
/*  59:    */     
/*  60:    */     public String toString(Instances t, int maxColWidth)
/*  61:    */     {
/*  62:158 */       int cindex = t.classIndex();
/*  63:159 */       StringBuffer text = new StringBuffer();
/*  64:161 */       for (int i = 0; i < this.attributes.length; i++) {
/*  65:162 */         if (i != cindex) {
/*  66:163 */           if (this.missing[i] != 0)
/*  67:    */           {
/*  68:164 */             text.append("?");
/*  69:165 */             for (int j = 0; j < maxColWidth; j++) {
/*  70:166 */               text.append(" ");
/*  71:    */             }
/*  72:    */           }
/*  73:    */           else
/*  74:    */           {
/*  75:169 */             String ss = t.attribute(i).value((int)this.attributes[i]);
/*  76:170 */             StringBuffer sb = new StringBuffer(ss);
/*  77:172 */             for (int j = 0; j < maxColWidth - ss.length() + 1; j++) {
/*  78:173 */               sb.append(" ");
/*  79:    */             }
/*  80:175 */             text.append(sb);
/*  81:    */           }
/*  82:    */         }
/*  83:    */       }
/*  84:179 */       return text.toString();
/*  85:    */     }
/*  86:    */     
/*  87:    */     public hashKey(double[] t)
/*  88:    */     {
/*  89:190 */       int l = t.length;
/*  90:    */       
/*  91:192 */       this.key = -999;
/*  92:193 */       this.attributes = new double[l];
/*  93:194 */       this.missing = new boolean[l];
/*  94:195 */       for (int i = 0; i < l; i++) {
/*  95:196 */         if (t[i] == 1.7976931348623157E+308D)
/*  96:    */         {
/*  97:197 */           this.missing[i] = true;
/*  98:    */         }
/*  99:    */         else
/* 100:    */         {
/* 101:199 */           this.missing[i] = false;
/* 102:200 */           this.attributes[i] = t[i];
/* 103:    */         }
/* 104:    */       }
/* 105:    */     }
/* 106:    */     
/* 107:    */     public int hashCode()
/* 108:    */     {
/* 109:213 */       int hv = 0;
/* 110:215 */       if (this.key != -999) {
/* 111:216 */         return this.key;
/* 112:    */       }
/* 113:218 */       for (int i = 0; i < this.attributes.length; i++) {
/* 114:219 */         if (this.missing[i] != 0) {
/* 115:220 */           hv += i * 13;
/* 116:    */         } else {
/* 117:222 */           hv = (int)(hv + i * 5 * (this.attributes[i] + 1.0D));
/* 118:    */         }
/* 119:    */       }
/* 120:225 */       if (this.key == -999) {
/* 121:226 */         this.key = hv;
/* 122:    */       }
/* 123:228 */       return hv;
/* 124:    */     }
/* 125:    */     
/* 126:    */     public boolean equals(Object b)
/* 127:    */     {
/* 128:240 */       if ((b == null) || (!b.getClass().equals(getClass()))) {
/* 129:241 */         return false;
/* 130:    */       }
/* 131:243 */       boolean ok = true;
/* 132:245 */       if ((b instanceof hashKey))
/* 133:    */       {
/* 134:246 */         hashKey n = (hashKey)b;
/* 135:247 */         for (int i = 0; i < this.attributes.length; i++)
/* 136:    */         {
/* 137:248 */           boolean l = n.missing[i];
/* 138:249 */           if ((this.missing[i] != 0) || (l))
/* 139:    */           {
/* 140:250 */             if (((this.missing[i] != 0) && (!l)) || ((this.missing[i] == 0) && (l)))
/* 141:    */             {
/* 142:251 */               ok = false;
/* 143:252 */               break;
/* 144:    */             }
/* 145:    */           }
/* 146:255 */           else if (this.attributes[i] != n.attributes[i])
/* 147:    */           {
/* 148:256 */             ok = false;
/* 149:257 */             break;
/* 150:    */           }
/* 151:    */         }
/* 152:    */       }
/* 153:    */       else
/* 154:    */       {
/* 155:262 */         return false;
/* 156:    */       }
/* 157:264 */       return ok;
/* 158:    */     }
/* 159:    */     
/* 160:    */     public void print_hash_code()
/* 161:    */     {
/* 162:272 */       System.out.println("Hash val: " + hashCode());
/* 163:    */     }
/* 164:    */     
/* 165:    */     public String getRevision()
/* 166:    */     {
/* 167:282 */       return RevisionUtils.extract("$Revision: 11854 $");
/* 168:    */     }
/* 169:    */   }
/* 170:    */   
/* 171:    */   public String globalInfo()
/* 172:    */   {
/* 173:293 */     return "ConsistencySubsetEval :\n\nEvaluates the worth of a subset of attributes by the level of consistency in the class values when the training instances are projected onto the subset of attributes. \n\nConsistency of any subset can never be lower than that of the full set of attributes, hence the usual practice is to use this subset evaluator in conjunction with a Random or Exhaustive search which looks for the smallest subset with consistency equal to that of the full set of attributes.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/* 174:    */   }
/* 175:    */   
/* 176:    */   public TechnicalInformation getTechnicalInformation()
/* 177:    */   {
/* 178:315 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/* 179:316 */     result.setValue(TechnicalInformation.Field.AUTHOR, "H. Liu and R. Setiono");
/* 180:317 */     result.setValue(TechnicalInformation.Field.TITLE, "A probabilistic approach to feature selection - A filter solution");
/* 181:    */     
/* 182:319 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "13th International Conference on Machine Learning");
/* 183:    */     
/* 184:321 */     result.setValue(TechnicalInformation.Field.YEAR, "1996");
/* 185:322 */     result.setValue(TechnicalInformation.Field.PAGES, "319-327");
/* 186:    */     
/* 187:324 */     return result;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public ConsistencySubsetEval()
/* 191:    */   {
/* 192:331 */     resetOptions();
/* 193:    */   }
/* 194:    */   
/* 195:    */   private void resetOptions()
/* 196:    */   {
/* 197:338 */     this.m_trainInstances = null;
/* 198:    */   }
/* 199:    */   
/* 200:    */   public Capabilities getCapabilities()
/* 201:    */   {
/* 202:349 */     Capabilities result = super.getCapabilities();
/* 203:350 */     result.disableAll();
/* 204:    */     
/* 205:    */ 
/* 206:353 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 207:354 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 208:355 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 209:356 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 210:    */     
/* 211:    */ 
/* 212:359 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 213:360 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 214:    */     
/* 215:362 */     return result;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public void buildEvaluator(Instances data)
/* 219:    */     throws Exception
/* 220:    */   {
/* 221:376 */     getCapabilities().testWithFail(data);
/* 222:    */     
/* 223:378 */     this.m_trainInstances = new Instances(data);
/* 224:379 */     this.m_trainInstances.deleteWithMissingClass();
/* 225:380 */     this.m_classIndex = this.m_trainInstances.classIndex();
/* 226:381 */     this.m_numAttribs = this.m_trainInstances.numAttributes();
/* 227:382 */     this.m_numInstances = this.m_trainInstances.numInstances();
/* 228:    */     
/* 229:384 */     this.m_disTransform = new Discretize();
/* 230:385 */     this.m_disTransform.setUseBetterEncoding(true);
/* 231:386 */     this.m_disTransform.setInputFormat(this.m_trainInstances);
/* 232:387 */     this.m_trainInstances = Filter.useFilter(this.m_trainInstances, this.m_disTransform);
/* 233:    */   }
/* 234:    */   
/* 235:    */   public double evaluateSubset(BitSet subset)
/* 236:    */     throws Exception
/* 237:    */   {
/* 238:400 */     int count = 0;
/* 239:402 */     for (int i = 0; i < this.m_numAttribs; i++) {
/* 240:403 */       if (subset.get(i)) {
/* 241:404 */         count++;
/* 242:    */       }
/* 243:    */     }
/* 244:408 */     double[] instArray = new double[count];
/* 245:409 */     int index = 0;
/* 246:410 */     int[] fs = new int[count];
/* 247:411 */     for (i = 0; i < this.m_numAttribs; i++) {
/* 248:412 */       if (subset.get(i)) {
/* 249:413 */         fs[(index++)] = i;
/* 250:    */       }
/* 251:    */     }
/* 252:418 */     this.m_table = new Hashtable((int)(this.m_numInstances * 1.5D));
/* 253:420 */     for (i = 0; i < this.m_numInstances; i++)
/* 254:    */     {
/* 255:421 */       Instance inst = this.m_trainInstances.instance(i);
/* 256:422 */       for (int j = 0; j < fs.length; j++)
/* 257:    */       {
/* 258:423 */         if (fs[j] == this.m_classIndex) {
/* 259:424 */           throw new Exception("A subset should not contain the class!");
/* 260:    */         }
/* 261:426 */         if (inst.isMissing(fs[j])) {
/* 262:427 */           instArray[j] = 1.7976931348623157E+308D;
/* 263:    */         } else {
/* 264:429 */           instArray[j] = inst.value(fs[j]);
/* 265:    */         }
/* 266:    */       }
/* 267:432 */       insertIntoTable(inst, instArray);
/* 268:    */     }
/* 269:435 */     return consistencyCount();
/* 270:    */   }
/* 271:    */   
/* 272:    */   private double consistencyCount()
/* 273:    */   {
/* 274:449 */     Enumeration<hashKey> e = this.m_table.keys();
/* 275:    */     
/* 276:451 */     double count = 0.0D;
/* 277:453 */     while (e.hasMoreElements())
/* 278:    */     {
/* 279:454 */       hashKey tt = (hashKey)e.nextElement();
/* 280:455 */       double[] classDist = (double[])this.m_table.get(tt);
/* 281:456 */       count += Utils.sum(classDist);
/* 282:457 */       int max = Utils.maxIndex(classDist);
/* 283:458 */       count -= classDist[max];
/* 284:    */     }
/* 285:461 */     count /= this.m_numInstances;
/* 286:462 */     return 1.0D - count;
/* 287:    */   }
/* 288:    */   
/* 289:    */   private void insertIntoTable(Instance inst, double[] instA)
/* 290:    */     throws Exception
/* 291:    */   {
/* 292:478 */     hashKey thekey = new hashKey(instA);
/* 293:    */     
/* 294:    */ 
/* 295:481 */     double[] tempClassDist2 = (double[])this.m_table.get(thekey);
/* 296:482 */     if (tempClassDist2 == null)
/* 297:    */     {
/* 298:483 */       double[] newDist = new double[this.m_trainInstances.classAttribute().numValues()];
/* 299:484 */       newDist[((int)inst.classValue())] = inst.weight();
/* 300:    */       
/* 301:    */ 
/* 302:487 */       this.m_table.put(thekey, newDist);
/* 303:    */     }
/* 304:    */     else
/* 305:    */     {
/* 306:490 */       tempClassDist2[((int)inst.classValue())] += inst.weight();
/* 307:    */       
/* 308:    */ 
/* 309:493 */       this.m_table.put(thekey, tempClassDist2);
/* 310:    */     }
/* 311:    */   }
/* 312:    */   
/* 313:    */   public String toString()
/* 314:    */   {
/* 315:504 */     StringBuffer text = new StringBuffer();
/* 316:506 */     if (this.m_trainInstances == null) {
/* 317:507 */       text.append("\tConsistency subset evaluator has not been built yet\n");
/* 318:    */     } else {
/* 319:509 */       text.append("\tConsistency Subset Evaluator\n");
/* 320:    */     }
/* 321:512 */     return text.toString();
/* 322:    */   }
/* 323:    */   
/* 324:    */   public String getRevision()
/* 325:    */   {
/* 326:522 */     return RevisionUtils.extract("$Revision: 11854 $");
/* 327:    */   }
/* 328:    */   
/* 329:    */   public void clean()
/* 330:    */   {
/* 331:529 */     this.m_trainInstances = new Instances(this.m_trainInstances, 0);
/* 332:    */   }
/* 333:    */   
/* 334:    */   public static void main(String[] args)
/* 335:    */   {
/* 336:538 */     runEvaluator(new ConsistencySubsetEval(), args);
/* 337:    */   }
/* 338:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.ConsistencySubsetEval
 * JD-Core Version:    0.7.0.1
 */