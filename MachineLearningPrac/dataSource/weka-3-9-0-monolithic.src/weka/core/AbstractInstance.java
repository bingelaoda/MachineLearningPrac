/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ 
/*   6:    */ public abstract class AbstractInstance
/*   7:    */   implements Instance, Serializable, RevisionHandler
/*   8:    */ {
/*   9:    */   static final long serialVersionUID = 1482635194499365155L;
/*  10:    */   protected Instances m_Dataset;
/*  11:    */   protected double[] m_AttValues;
/*  12:    */   protected double m_Weight;
/*  13: 54 */   public static int s_numericAfterDecimalPoint = 6;
/*  14:    */   
/*  15:    */   public Attribute attribute(int index)
/*  16:    */   {
/*  17: 68 */     if (this.m_Dataset == null) {
/*  18: 69 */       throw new UnassignedDatasetException("Instance doesn't have access to a dataset!");
/*  19:    */     }
/*  20: 72 */     return this.m_Dataset.attribute(index);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public Attribute attributeSparse(int indexOfIndex)
/*  24:    */   {
/*  25: 87 */     if (this.m_Dataset == null) {
/*  26: 88 */       throw new UnassignedDatasetException("Instance doesn't have access to a dataset!");
/*  27:    */     }
/*  28: 91 */     return this.m_Dataset.attribute(index(indexOfIndex));
/*  29:    */   }
/*  30:    */   
/*  31:    */   public Attribute classAttribute()
/*  32:    */   {
/*  33:105 */     if (this.m_Dataset == null) {
/*  34:106 */       throw new UnassignedDatasetException("Instance doesn't have access to a dataset!");
/*  35:    */     }
/*  36:109 */     return this.m_Dataset.classAttribute();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public int classIndex()
/*  40:    */   {
/*  41:124 */     if (this.m_Dataset == null) {
/*  42:125 */       throw new UnassignedDatasetException("Instance doesn't have access to a dataset!");
/*  43:    */     }
/*  44:128 */     return this.m_Dataset.classIndex();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public boolean classIsMissing()
/*  48:    */   {
/*  49:142 */     int classIndex = classIndex();
/*  50:143 */     if (classIndex < 0) {
/*  51:144 */       throw new UnassignedClassException("Class is not set!");
/*  52:    */     }
/*  53:146 */     return isMissing(classIndex);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public double classValue()
/*  57:    */   {
/*  58:163 */     int classIndex = classIndex();
/*  59:164 */     if (classIndex < 0) {
/*  60:165 */       throw new UnassignedClassException("Class is not set!");
/*  61:    */     }
/*  62:167 */     return value(classIndex);
/*  63:    */   }
/*  64:    */   
/*  65:    */   public Instances dataset()
/*  66:    */   {
/*  67:181 */     return this.m_Dataset;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void deleteAttributeAt(int position)
/*  71:    */   {
/*  72:196 */     if (this.m_Dataset != null) {
/*  73:197 */       throw new RuntimeException("Instance has access to a dataset!");
/*  74:    */     }
/*  75:199 */     forceDeleteAttributeAt(position);
/*  76:    */   }
/*  77:    */   
/*  78:    */   public Enumeration<Attribute> enumerateAttributes()
/*  79:    */   {
/*  80:213 */     if (this.m_Dataset == null) {
/*  81:214 */       throw new UnassignedDatasetException("Instance doesn't have access to a dataset!");
/*  82:    */     }
/*  83:217 */     return this.m_Dataset.enumerateAttributes();
/*  84:    */   }
/*  85:    */   
/*  86:    */   public boolean equalHeaders(Instance inst)
/*  87:    */   {
/*  88:233 */     if (this.m_Dataset == null) {
/*  89:234 */       throw new UnassignedDatasetException("Instance doesn't have access to a dataset!");
/*  90:    */     }
/*  91:237 */     return this.m_Dataset.equalHeaders(inst.dataset());
/*  92:    */   }
/*  93:    */   
/*  94:    */   public String equalHeadersMsg(Instance inst)
/*  95:    */   {
/*  96:251 */     if (this.m_Dataset == null) {
/*  97:252 */       throw new UnassignedDatasetException("Instance doesn't have access to a dataset!");
/*  98:    */     }
/*  99:256 */     return this.m_Dataset.equalHeadersMsg(inst.dataset());
/* 100:    */   }
/* 101:    */   
/* 102:    */   public boolean hasMissingValue()
/* 103:    */   {
/* 104:271 */     if (this.m_Dataset == null) {
/* 105:272 */       throw new UnassignedDatasetException("Instance doesn't have access to a dataset!");
/* 106:    */     }
/* 107:275 */     int classIndex = classIndex();
/* 108:276 */     for (int i = 0; i < numValues(); i++) {
/* 109:277 */       if ((index(i) != classIndex) && 
/* 110:278 */         (isMissingSparse(i))) {
/* 111:279 */         return true;
/* 112:    */       }
/* 113:    */     }
/* 114:283 */     return false;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void insertAttributeAt(int position)
/* 118:    */   {
/* 119:300 */     if (this.m_Dataset != null) {
/* 120:301 */       throw new RuntimeException("Instance has accesss to a dataset!");
/* 121:    */     }
/* 122:303 */     if ((position < 0) || (position > numAttributes())) {
/* 123:304 */       throw new IllegalArgumentException("Can't insert attribute: index out of range");
/* 124:    */     }
/* 125:307 */     forceInsertAttributeAt(position);
/* 126:    */   }
/* 127:    */   
/* 128:    */   public boolean isMissing(int attIndex)
/* 129:    */   {
/* 130:319 */     if (Utils.isMissingValue(value(attIndex))) {
/* 131:320 */       return true;
/* 132:    */     }
/* 133:322 */     return false;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public boolean isMissingSparse(int indexOfIndex)
/* 137:    */   {
/* 138:335 */     if (Utils.isMissingValue(valueSparse(indexOfIndex))) {
/* 139:336 */       return true;
/* 140:    */     }
/* 141:338 */     return false;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public boolean isMissing(Attribute att)
/* 145:    */   {
/* 146:351 */     return isMissing(att.index());
/* 147:    */   }
/* 148:    */   
/* 149:    */   public int numClasses()
/* 150:    */   {
/* 151:366 */     if (this.m_Dataset == null) {
/* 152:367 */       throw new UnassignedDatasetException("Instance doesn't have access to a dataset!");
/* 153:    */     }
/* 154:370 */     return this.m_Dataset.numClasses();
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void setClassMissing()
/* 158:    */   {
/* 159:386 */     int classIndex = classIndex();
/* 160:387 */     if (classIndex < 0) {
/* 161:388 */       throw new UnassignedClassException("Class is not set!");
/* 162:    */     }
/* 163:390 */     setMissing(classIndex);
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void setClassValue(double value)
/* 167:    */   {
/* 168:409 */     int classIndex = classIndex();
/* 169:410 */     if (classIndex < 0) {
/* 170:411 */       throw new UnassignedClassException("Class is not set!");
/* 171:    */     }
/* 172:413 */     setValue(classIndex, value);
/* 173:    */   }
/* 174:    */   
/* 175:    */   public final void setClassValue(String value)
/* 176:    */   {
/* 177:431 */     int classIndex = classIndex();
/* 178:432 */     if (classIndex < 0) {
/* 179:433 */       throw new UnassignedClassException("Class is not set!");
/* 180:    */     }
/* 181:435 */     setValue(classIndex, value);
/* 182:    */   }
/* 183:    */   
/* 184:    */   public final void setDataset(Instances instances)
/* 185:    */   {
/* 186:449 */     this.m_Dataset = instances;
/* 187:    */   }
/* 188:    */   
/* 189:    */   public final void setMissing(int attIndex)
/* 190:    */   {
/* 191:461 */     setValue(attIndex, Utils.missingValue());
/* 192:    */   }
/* 193:    */   
/* 194:    */   public final void setMissing(Attribute att)
/* 195:    */   {
/* 196:474 */     setMissing(att.index());
/* 197:    */   }
/* 198:    */   
/* 199:    */   public final void setValue(int attIndex, String value)
/* 200:    */   {
/* 201:496 */     if (this.m_Dataset == null) {
/* 202:497 */       throw new UnassignedDatasetException("Instance doesn't have access to a dataset!");
/* 203:    */     }
/* 204:500 */     if ((!attribute(attIndex).isNominal()) && (!attribute(attIndex).isString())) {
/* 205:501 */       throw new IllegalArgumentException("Attribute neither nominal nor string!");
/* 206:    */     }
/* 207:504 */     int valIndex = attribute(attIndex).indexOfValue(value);
/* 208:505 */     if (valIndex == -1)
/* 209:    */     {
/* 210:506 */       if (attribute(attIndex).isNominal()) {
/* 211:507 */         throw new IllegalArgumentException("Value not defined for given nominal attribute!");
/* 212:    */       }
/* 213:510 */       attribute(attIndex).forceAddValue(value);
/* 214:511 */       valIndex = attribute(attIndex).indexOfValue(value);
/* 215:    */     }
/* 216:514 */     setValue(attIndex, valIndex);
/* 217:    */   }
/* 218:    */   
/* 219:    */   public final void setValue(Attribute att, double value)
/* 220:    */   {
/* 221:532 */     setValue(att.index(), value);
/* 222:    */   }
/* 223:    */   
/* 224:    */   public final void setValue(Attribute att, String value)
/* 225:    */   {
/* 226:552 */     if ((!att.isNominal()) && (!att.isString())) {
/* 227:553 */       throw new IllegalArgumentException("Attribute neither nominal nor string!");
/* 228:    */     }
/* 229:556 */     int valIndex = att.indexOfValue(value);
/* 230:557 */     if (valIndex == -1)
/* 231:    */     {
/* 232:558 */       if (att.isNominal()) {
/* 233:559 */         throw new IllegalArgumentException("Value not defined for given nominal attribute!");
/* 234:    */       }
/* 235:562 */       att.forceAddValue(value);
/* 236:563 */       valIndex = att.indexOfValue(value);
/* 237:    */     }
/* 238:566 */     setValue(att.index(), valIndex);
/* 239:    */   }
/* 240:    */   
/* 241:    */   public final void setWeight(double weight)
/* 242:    */   {
/* 243:577 */     this.m_Weight = weight;
/* 244:    */   }
/* 245:    */   
/* 246:    */   public final Instances relationalValue(int attIndex)
/* 247:    */   {
/* 248:594 */     if (this.m_Dataset == null) {
/* 249:595 */       throw new UnassignedDatasetException("Instance doesn't have access to a dataset!");
/* 250:    */     }
/* 251:598 */     return relationalValue(this.m_Dataset.attribute(attIndex));
/* 252:    */   }
/* 253:    */   
/* 254:    */   public final Instances relationalValue(Attribute att)
/* 255:    */   {
/* 256:614 */     int attIndex = att.index();
/* 257:615 */     if (att.isRelationValued())
/* 258:    */     {
/* 259:616 */       if (isMissing(attIndex)) {
/* 260:617 */         return null;
/* 261:    */       }
/* 262:619 */       return att.relation((int)value(attIndex));
/* 263:    */     }
/* 264:621 */     throw new IllegalArgumentException("Attribute isn't relation-valued!");
/* 265:    */   }
/* 266:    */   
/* 267:    */   public final String stringValue(int attIndex)
/* 268:    */   {
/* 269:640 */     if (this.m_Dataset == null) {
/* 270:641 */       throw new UnassignedDatasetException("Instance doesn't have access to a dataset!");
/* 271:    */     }
/* 272:644 */     return stringValue(this.m_Dataset.attribute(attIndex));
/* 273:    */   }
/* 274:    */   
/* 275:    */   public final String stringValue(Attribute att)
/* 276:    */   {
/* 277:661 */     int attIndex = att.index();
/* 278:662 */     if (isMissing(attIndex)) {
/* 279:663 */       return "?";
/* 280:    */     }
/* 281:665 */     switch (att.type())
/* 282:    */     {
/* 283:    */     case 1: 
/* 284:    */     case 2: 
/* 285:668 */       return att.value((int)value(attIndex));
/* 286:    */     case 3: 
/* 287:670 */       return att.formatDate(value(attIndex));
/* 288:    */     case 4: 
/* 289:672 */       return att.relation((int)value(attIndex)).stringWithoutHeader();
/* 290:    */     }
/* 291:674 */     throw new IllegalArgumentException("Attribute isn't nominal, string or date!");
/* 292:    */   }
/* 293:    */   
/* 294:    */   public final String toStringMaxDecimalDigits(int afterDecimalPoint)
/* 295:    */   {
/* 296:692 */     StringBuffer text = new StringBuffer(toStringNoWeight(afterDecimalPoint));
/* 297:694 */     if (this.m_Weight != 1.0D) {
/* 298:695 */       text.append(",{" + Utils.doubleToString(this.m_Weight, afterDecimalPoint) + "}");
/* 299:    */     }
/* 300:699 */     return text.toString();
/* 301:    */   }
/* 302:    */   
/* 303:    */   public String toString()
/* 304:    */   {
/* 305:712 */     return toStringMaxDecimalDigits(s_numericAfterDecimalPoint);
/* 306:    */   }
/* 307:    */   
/* 308:    */   public final String toString(int attIndex)
/* 309:    */   {
/* 310:726 */     return toString(attIndex, s_numericAfterDecimalPoint);
/* 311:    */   }
/* 312:    */   
/* 313:    */   public final String toString(int attIndex, int afterDecimalPoint)
/* 314:    */   {
/* 315:743 */     StringBuffer text = new StringBuffer();
/* 316:745 */     if (isMissing(attIndex)) {
/* 317:746 */       text.append("?");
/* 318:748 */     } else if (this.m_Dataset == null) {
/* 319:749 */       text.append(Utils.doubleToString(value(attIndex), afterDecimalPoint));
/* 320:    */     } else {
/* 321:751 */       switch (this.m_Dataset.attribute(attIndex).type())
/* 322:    */       {
/* 323:    */       case 1: 
/* 324:    */       case 2: 
/* 325:    */       case 3: 
/* 326:    */       case 4: 
/* 327:756 */         text.append(Utils.quote(stringValue(attIndex)));
/* 328:757 */         break;
/* 329:    */       case 0: 
/* 330:759 */         text.append(Utils.doubleToString(value(attIndex), afterDecimalPoint));
/* 331:760 */         break;
/* 332:    */       default: 
/* 333:762 */         throw new IllegalStateException("Unknown attribute type");
/* 334:    */       }
/* 335:    */     }
/* 336:766 */     return text.toString();
/* 337:    */   }
/* 338:    */   
/* 339:    */   public final String toString(Attribute att)
/* 340:    */   {
/* 341:782 */     return toString(att.index());
/* 342:    */   }
/* 343:    */   
/* 344:    */   public final String toString(Attribute att, int afterDecimalPoint)
/* 345:    */   {
/* 346:799 */     return toString(att.index(), afterDecimalPoint);
/* 347:    */   }
/* 348:    */   
/* 349:    */   public double value(Attribute att)
/* 350:    */   {
/* 351:814 */     return value(att.index());
/* 352:    */   }
/* 353:    */   
/* 354:    */   public double valueSparse(int indexOfIndex)
/* 355:    */   {
/* 356:829 */     return this.m_AttValues[indexOfIndex];
/* 357:    */   }
/* 358:    */   
/* 359:    */   public final double weight()
/* 360:    */   {
/* 361:840 */     return this.m_Weight;
/* 362:    */   }
/* 363:    */   
/* 364:    */   public String getRevision()
/* 365:    */   {
/* 366:850 */     return RevisionUtils.extract("$Revision: 10649 $");
/* 367:    */   }
/* 368:    */   
/* 369:    */   protected abstract void forceDeleteAttributeAt(int paramInt);
/* 370:    */   
/* 371:    */   protected abstract void forceInsertAttributeAt(int paramInt);
/* 372:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.AbstractInstance
 * JD-Core Version:    0.7.0.1
 */