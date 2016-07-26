/*   1:    */ package weka.classifiers.trees;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import weka.classifiers.AbstractClassifier;
/*   5:    */ import weka.classifiers.Sourcable;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Capabilities.Capability;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.NoSupportForMissingValuesException;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.TechnicalInformation;
/*  14:    */ import weka.core.TechnicalInformation.Field;
/*  15:    */ import weka.core.TechnicalInformation.Type;
/*  16:    */ import weka.core.TechnicalInformationHandler;
/*  17:    */ import weka.core.Utils;
/*  18:    */ 
/*  19:    */ public class Id3
/*  20:    */   extends AbstractClassifier
/*  21:    */   implements TechnicalInformationHandler, Sourcable
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = -2693678647096322561L;
/*  24:    */   private Id3[] m_Successors;
/*  25:    */   private Attribute m_Attribute;
/*  26:    */   private double m_ClassValue;
/*  27:    */   private double[] m_Distribution;
/*  28:    */   private Attribute m_ClassAttribute;
/*  29:    */   
/*  30:    */   public String globalInfo()
/*  31:    */   {
/*  32:110 */     return "Class for constructing an unpruned decision tree based on the ID3 algorithm. Can only deal with nominal attributes. No missing values allowed. Empty leaves may result in unclassified instances. For more information see: \n\n" + getTechnicalInformation().toString();
/*  33:    */   }
/*  34:    */   
/*  35:    */   public TechnicalInformation getTechnicalInformation()
/*  36:    */   {
/*  37:127 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  38:128 */     result.setValue(TechnicalInformation.Field.AUTHOR, "R. Quinlan");
/*  39:129 */     result.setValue(TechnicalInformation.Field.YEAR, "1986");
/*  40:130 */     result.setValue(TechnicalInformation.Field.TITLE, "Induction of decision trees");
/*  41:131 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/*  42:132 */     result.setValue(TechnicalInformation.Field.VOLUME, "1");
/*  43:133 */     result.setValue(TechnicalInformation.Field.NUMBER, "1");
/*  44:134 */     result.setValue(TechnicalInformation.Field.PAGES, "81-106");
/*  45:    */     
/*  46:136 */     return result;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public Capabilities getCapabilities()
/*  50:    */   {
/*  51:146 */     Capabilities result = super.getCapabilities();
/*  52:147 */     result.disableAll();
/*  53:    */     
/*  54:    */ 
/*  55:150 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  56:    */     
/*  57:    */ 
/*  58:153 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  59:154 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  60:    */     
/*  61:    */ 
/*  62:157 */     result.setMinimumNumberInstances(0);
/*  63:    */     
/*  64:159 */     return result;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void buildClassifier(Instances data)
/*  68:    */     throws Exception
/*  69:    */   {
/*  70:172 */     getCapabilities().testWithFail(data);
/*  71:    */     
/*  72:    */ 
/*  73:175 */     data = new Instances(data);
/*  74:176 */     data.deleteWithMissingClass();
/*  75:    */     
/*  76:178 */     makeTree(data);
/*  77:    */   }
/*  78:    */   
/*  79:    */   private void makeTree(Instances data)
/*  80:    */     throws Exception
/*  81:    */   {
/*  82:190 */     if (data.numInstances() == 0)
/*  83:    */     {
/*  84:191 */       this.m_Attribute = null;
/*  85:192 */       this.m_ClassValue = Utils.missingValue();
/*  86:193 */       this.m_Distribution = new double[data.numClasses()];
/*  87:194 */       return;
/*  88:    */     }
/*  89:198 */     double[] infoGains = new double[data.numAttributes()];
/*  90:199 */     Enumeration<Attribute> attEnum = data.enumerateAttributes();
/*  91:200 */     while (attEnum.hasMoreElements())
/*  92:    */     {
/*  93:201 */       Attribute att = (Attribute)attEnum.nextElement();
/*  94:202 */       infoGains[att.index()] = computeInfoGain(data, att);
/*  95:    */     }
/*  96:204 */     this.m_Attribute = data.attribute(Utils.maxIndex(infoGains));
/*  97:208 */     if (Utils.eq(infoGains[this.m_Attribute.index()], 0.0D))
/*  98:    */     {
/*  99:209 */       this.m_Attribute = null;
/* 100:210 */       this.m_Distribution = new double[data.numClasses()];
/* 101:211 */       Enumeration<Instance> instEnum = data.enumerateInstances();
/* 102:212 */       while (instEnum.hasMoreElements())
/* 103:    */       {
/* 104:213 */         Instance inst = (Instance)instEnum.nextElement();
/* 105:214 */         this.m_Distribution[((int)inst.classValue())] += 1.0D;
/* 106:    */       }
/* 107:216 */       Utils.normalize(this.m_Distribution);
/* 108:217 */       this.m_ClassValue = Utils.maxIndex(this.m_Distribution);
/* 109:218 */       this.m_ClassAttribute = data.classAttribute();
/* 110:    */     }
/* 111:    */     else
/* 112:    */     {
/* 113:220 */       Instances[] splitData = splitData(data, this.m_Attribute);
/* 114:221 */       this.m_Successors = new Id3[this.m_Attribute.numValues()];
/* 115:222 */       for (int j = 0; j < this.m_Attribute.numValues(); j++)
/* 116:    */       {
/* 117:223 */         this.m_Successors[j] = new Id3();
/* 118:224 */         this.m_Successors[j].makeTree(splitData[j]);
/* 119:    */       }
/* 120:    */     }
/* 121:    */   }
/* 122:    */   
/* 123:    */   public double classifyInstance(Instance instance)
/* 124:    */     throws NoSupportForMissingValuesException
/* 125:    */   {
/* 126:240 */     if (instance.hasMissingValue()) {
/* 127:241 */       throw new NoSupportForMissingValuesException("Id3: no missing values, please.");
/* 128:    */     }
/* 129:244 */     if (this.m_Attribute == null) {
/* 130:245 */       return this.m_ClassValue;
/* 131:    */     }
/* 132:247 */     return this.m_Successors[((int)instance.value(this.m_Attribute))].classifyInstance(instance);
/* 133:    */   }
/* 134:    */   
/* 135:    */   public double[] distributionForInstance(Instance instance)
/* 136:    */     throws NoSupportForMissingValuesException
/* 137:    */   {
/* 138:263 */     if (instance.hasMissingValue()) {
/* 139:264 */       throw new NoSupportForMissingValuesException("Id3: no missing values, please.");
/* 140:    */     }
/* 141:267 */     if (this.m_Attribute == null) {
/* 142:268 */       return this.m_Distribution;
/* 143:    */     }
/* 144:270 */     return this.m_Successors[((int)instance.value(this.m_Attribute))].distributionForInstance(instance);
/* 145:    */   }
/* 146:    */   
/* 147:    */   public String toString()
/* 148:    */   {
/* 149:283 */     if ((this.m_Distribution == null) && (this.m_Successors == null)) {
/* 150:284 */       return "Id3: No model built yet.";
/* 151:    */     }
/* 152:286 */     return "Id3\n\n" + toString(0);
/* 153:    */   }
/* 154:    */   
/* 155:    */   private double computeInfoGain(Instances data, Attribute att)
/* 156:    */     throws Exception
/* 157:    */   {
/* 158:300 */     double infoGain = computeEntropy(data);
/* 159:301 */     Instances[] splitData = splitData(data, att);
/* 160:302 */     for (int j = 0; j < att.numValues(); j++) {
/* 161:303 */       if (splitData[j].numInstances() > 0) {
/* 162:304 */         infoGain -= splitData[j].numInstances() / data.numInstances() * computeEntropy(splitData[j]);
/* 163:    */       }
/* 164:    */     }
/* 165:308 */     return infoGain;
/* 166:    */   }
/* 167:    */   
/* 168:    */   private double computeEntropy(Instances data)
/* 169:    */     throws Exception
/* 170:    */   {
/* 171:320 */     double[] classCounts = new double[data.numClasses()];
/* 172:321 */     Enumeration<Instance> instEnum = data.enumerateInstances();
/* 173:322 */     while (instEnum.hasMoreElements())
/* 174:    */     {
/* 175:323 */       Instance inst = (Instance)instEnum.nextElement();
/* 176:324 */       classCounts[((int)inst.classValue())] += 1.0D;
/* 177:    */     }
/* 178:326 */     double entropy = 0.0D;
/* 179:327 */     for (int j = 0; j < data.numClasses(); j++) {
/* 180:328 */       if (classCounts[j] > 0.0D) {
/* 181:329 */         entropy -= classCounts[j] * Utils.log2(classCounts[j]);
/* 182:    */       }
/* 183:    */     }
/* 184:332 */     entropy /= data.numInstances();
/* 185:333 */     return entropy + Utils.log2(data.numInstances());
/* 186:    */   }
/* 187:    */   
/* 188:    */   private Instances[] splitData(Instances data, Attribute att)
/* 189:    */   {
/* 190:345 */     Instances[] splitData = new Instances[att.numValues()];
/* 191:346 */     for (int j = 0; j < att.numValues(); j++) {
/* 192:347 */       splitData[j] = new Instances(data, data.numInstances());
/* 193:    */     }
/* 194:349 */     Enumeration<Instance> instEnum = data.enumerateInstances();
/* 195:350 */     while (instEnum.hasMoreElements())
/* 196:    */     {
/* 197:351 */       Instance inst = (Instance)instEnum.nextElement();
/* 198:352 */       splitData[((int)inst.value(att))].add(inst);
/* 199:    */     }
/* 200:354 */     for (Instances element : splitData) {
/* 201:355 */       element.compactify();
/* 202:    */     }
/* 203:357 */     return splitData;
/* 204:    */   }
/* 205:    */   
/* 206:    */   private String toString(int level)
/* 207:    */   {
/* 208:368 */     StringBuffer text = new StringBuffer();
/* 209:370 */     if (this.m_Attribute == null)
/* 210:    */     {
/* 211:371 */       if (Utils.isMissingValue(this.m_ClassValue)) {
/* 212:372 */         text.append(": null");
/* 213:    */       } else {
/* 214:374 */         text.append(": " + this.m_ClassAttribute.value((int)this.m_ClassValue));
/* 215:    */       }
/* 216:    */     }
/* 217:    */     else {
/* 218:377 */       for (int j = 0; j < this.m_Attribute.numValues(); j++)
/* 219:    */       {
/* 220:378 */         text.append("\n");
/* 221:379 */         for (int i = 0; i < level; i++) {
/* 222:380 */           text.append("|  ");
/* 223:    */         }
/* 224:382 */         text.append(this.m_Attribute.name() + " = " + this.m_Attribute.value(j));
/* 225:383 */         text.append(this.m_Successors[j].toString(level + 1));
/* 226:    */       }
/* 227:    */     }
/* 228:386 */     return text.toString();
/* 229:    */   }
/* 230:    */   
/* 231:    */   protected int toSource(int id, StringBuffer buffer)
/* 232:    */     throws Exception
/* 233:    */   {
/* 234:403 */     buffer.append("\n");
/* 235:404 */     buffer.append("  protected static double node" + id + "(Object[] i) {\n");
/* 236:    */     int result;
/* 237:407 */     if (this.m_Attribute == null)
/* 238:    */     {
/* 239:408 */       int result = id;
/* 240:409 */       if (Double.isNaN(this.m_ClassValue)) {
/* 241:410 */         buffer.append("    return Double.NaN;");
/* 242:    */       } else {
/* 243:412 */         buffer.append("    return " + this.m_ClassValue + ";");
/* 244:    */       }
/* 245:414 */       if (this.m_ClassAttribute != null) {
/* 246:415 */         buffer.append(" // " + this.m_ClassAttribute.value((int)this.m_ClassValue));
/* 247:    */       }
/* 248:417 */       buffer.append("\n");
/* 249:418 */       buffer.append("  }\n");
/* 250:    */     }
/* 251:    */     else
/* 252:    */     {
/* 253:420 */       buffer.append("    checkMissing(i, " + this.m_Attribute.index() + ");\n\n");
/* 254:421 */       buffer.append("    // " + this.m_Attribute.name() + "\n");
/* 255:    */       
/* 256:    */ 
/* 257:424 */       StringBuffer[] subBuffers = new StringBuffer[this.m_Attribute.numValues()];
/* 258:425 */       int newID = id;
/* 259:426 */       for (int i = 0; i < this.m_Attribute.numValues(); i++)
/* 260:    */       {
/* 261:427 */         newID++;
/* 262:    */         
/* 263:429 */         buffer.append("    ");
/* 264:430 */         if (i > 0) {
/* 265:431 */           buffer.append("else ");
/* 266:    */         }
/* 267:433 */         buffer.append("if (((String) i[" + this.m_Attribute.index() + "]).equals(\"" + this.m_Attribute.value(i) + "\"))\n");
/* 268:    */         
/* 269:435 */         buffer.append("      return node" + newID + "(i);\n");
/* 270:    */         
/* 271:437 */         subBuffers[i] = new StringBuffer();
/* 272:438 */         newID = this.m_Successors[i].toSource(newID, subBuffers[i]);
/* 273:    */       }
/* 274:440 */       buffer.append("    else\n");
/* 275:441 */       buffer.append("      throw new IllegalArgumentException(\"Value '\" + i[" + this.m_Attribute.index() + "] + \"' is not allowed!\");\n");
/* 276:    */       
/* 277:443 */       buffer.append("  }\n");
/* 278:446 */       for (i = 0; i < this.m_Attribute.numValues(); i++) {
/* 279:447 */         buffer.append(subBuffers[i].toString());
/* 280:    */       }
/* 281:449 */       subBuffers = null;
/* 282:    */       
/* 283:451 */       result = newID;
/* 284:    */     }
/* 285:454 */     return result;
/* 286:    */   }
/* 287:    */   
/* 288:    */   public String toSource(String className)
/* 289:    */     throws Exception
/* 290:    */   {
/* 291:482 */     StringBuffer result = new StringBuffer();
/* 292:    */     
/* 293:484 */     result.append("class " + className + " {\n");
/* 294:485 */     result.append("  private static void checkMissing(Object[] i, int index) {\n");
/* 295:    */     
/* 296:487 */     result.append("    if (i[index] == null)\n");
/* 297:488 */     result.append("      throw new IllegalArgumentException(\"Null values are not allowed!\");\n");
/* 298:    */     
/* 299:490 */     result.append("  }\n\n");
/* 300:491 */     result.append("  public static double classify(Object[] i) {\n");
/* 301:492 */     int id = 0;
/* 302:493 */     result.append("    return node" + id + "(i);\n");
/* 303:494 */     result.append("  }\n");
/* 304:495 */     toSource(id, result);
/* 305:496 */     result.append("}\n");
/* 306:    */     
/* 307:498 */     return result.toString();
/* 308:    */   }
/* 309:    */   
/* 310:    */   public String getRevision()
/* 311:    */   {
/* 312:508 */     return RevisionUtils.extract("$Revision: 10390 $");
/* 313:    */   }
/* 314:    */   
/* 315:    */   public static void main(String[] args)
/* 316:    */   {
/* 317:517 */     runClassifier(new Id3(), args);
/* 318:    */   }
/* 319:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.Id3
 * JD-Core Version:    0.7.0.1
 */