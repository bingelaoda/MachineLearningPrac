/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Capabilities.Capability;
/*   9:    */ import weka.core.DenseInstance;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.Range;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SparseInstance;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.filters.Filter;
/*  19:    */ import weka.filters.StreamableFilter;
/*  20:    */ import weka.filters.UnsupervisedFilter;
/*  21:    */ 
/*  22:    */ public class NominalToBinary
/*  23:    */   extends Filter
/*  24:    */   implements UnsupervisedFilter, OptionHandler, StreamableFilter
/*  25:    */ {
/*  26:    */   static final long serialVersionUID = -1130642825710549138L;
/*  27: 92 */   protected Range m_Columns = new Range();
/*  28: 95 */   private boolean m_Numeric = true;
/*  29: 98 */   private boolean m_TransformAll = false;
/*  30:101 */   private boolean m_needToTransform = false;
/*  31:    */   
/*  32:    */   public NominalToBinary()
/*  33:    */   {
/*  34:106 */     setAttributeIndices("first-last");
/*  35:    */   }
/*  36:    */   
/*  37:    */   public String globalInfo()
/*  38:    */   {
/*  39:117 */     return "Converts all nominal attributes into binary numeric attributes. An attribute with k values is transformed into k binary attributes if the class is nominal (using the one-attribute-per-value approach). Binary attributes are left binary, if option '-A' is not given.If the class is numeric, you might want to use the supervised version of this filter.";
/*  40:    */   }
/*  41:    */   
/*  42:    */   public Capabilities getCapabilities()
/*  43:    */   {
/*  44:133 */     Capabilities result = super.getCapabilities();
/*  45:134 */     result.disableAll();
/*  46:    */     
/*  47:    */ 
/*  48:137 */     result.enableAllAttributes();
/*  49:138 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  50:    */     
/*  51:    */ 
/*  52:141 */     result.enableAllClasses();
/*  53:142 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  54:143 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  55:    */     
/*  56:145 */     return result;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public boolean setInputFormat(Instances instanceInfo)
/*  60:    */     throws Exception
/*  61:    */   {
/*  62:160 */     super.setInputFormat(instanceInfo);
/*  63:    */     
/*  64:162 */     this.m_Columns.setUpper(instanceInfo.numAttributes() - 1);
/*  65:    */     
/*  66:164 */     setOutputFormat();
/*  67:165 */     return true;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public boolean input(Instance instance)
/*  71:    */   {
/*  72:179 */     if (getInputFormat() == null) {
/*  73:180 */       throw new IllegalStateException("No input instance format defined");
/*  74:    */     }
/*  75:182 */     if (this.m_NewBatch)
/*  76:    */     {
/*  77:183 */       resetQueue();
/*  78:184 */       this.m_NewBatch = false;
/*  79:    */     }
/*  80:187 */     convertInstance(instance);
/*  81:188 */     return true;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public Enumeration<Option> listOptions()
/*  85:    */   {
/*  86:199 */     Vector<Option> newVector = new Vector(4);
/*  87:    */     
/*  88:201 */     newVector.addElement(new Option("\tSets if binary attributes are to be coded as nominal ones.", "N", 0, "-N"));
/*  89:    */     
/*  90:    */ 
/*  91:    */ 
/*  92:205 */     newVector.addElement(new Option("\tFor each nominal value a new attribute is created, \n\tnot only if there are more than 2 values.", "A", 0, "-A"));
/*  93:    */     
/*  94:    */ 
/*  95:    */ 
/*  96:209 */     newVector.addElement(new Option("\tSpecifies list of columns to act on. First and last are \n\tvalid indexes.\n\t(default: first-last)", "R", 1, "-R <col1,col2-col4,...>"));
/*  97:    */     
/*  98:    */ 
/*  99:    */ 
/* 100:    */ 
/* 101:214 */     newVector.addElement(new Option("\tInvert matching sense of column indexes.", "V", 0, "-V"));
/* 102:    */     
/* 103:    */ 
/* 104:217 */     return newVector.elements();
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void setOptions(String[] options)
/* 108:    */     throws Exception
/* 109:    */   {
/* 110:258 */     setBinaryAttributesNominal(Utils.getFlag('N', options));
/* 111:    */     
/* 112:260 */     setTransformAllValues(Utils.getFlag('A', options));
/* 113:    */     
/* 114:262 */     String convertList = Utils.getOption('R', options);
/* 115:263 */     if (convertList.length() != 0) {
/* 116:264 */       setAttributeIndices(convertList);
/* 117:    */     } else {
/* 118:266 */       setAttributeIndices("first-last");
/* 119:    */     }
/* 120:268 */     setInvertSelection(Utils.getFlag('V', options));
/* 121:270 */     if (getInputFormat() != null) {
/* 122:271 */       setInputFormat(getInputFormat());
/* 123:    */     }
/* 124:274 */     Utils.checkForRemainingOptions(options);
/* 125:    */   }
/* 126:    */   
/* 127:    */   public String[] getOptions()
/* 128:    */   {
/* 129:285 */     Vector<String> options = new Vector();
/* 130:287 */     if (getBinaryAttributesNominal()) {
/* 131:288 */       options.add("-N");
/* 132:    */     }
/* 133:291 */     if (getTransformAllValues()) {
/* 134:292 */       options.add("-A");
/* 135:    */     }
/* 136:295 */     if (!getAttributeIndices().equals(""))
/* 137:    */     {
/* 138:296 */       options.add("-R");
/* 139:297 */       options.add(getAttributeIndices());
/* 140:    */     }
/* 141:299 */     if (getInvertSelection()) {
/* 142:300 */       options.add("-V");
/* 143:    */     }
/* 144:303 */     return (String[])options.toArray(new String[0]);
/* 145:    */   }
/* 146:    */   
/* 147:    */   public String binaryAttributesNominalTipText()
/* 148:    */   {
/* 149:313 */     return "Whether resulting binary attributes will be nominal.";
/* 150:    */   }
/* 151:    */   
/* 152:    */   public boolean getBinaryAttributesNominal()
/* 153:    */   {
/* 154:323 */     return !this.m_Numeric;
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void setBinaryAttributesNominal(boolean bool)
/* 158:    */   {
/* 159:333 */     this.m_Numeric = (!bool);
/* 160:    */   }
/* 161:    */   
/* 162:    */   public String transformAllValuesTipText()
/* 163:    */   {
/* 164:343 */     return "Whether all nominal values are turned into new attributes, not only if there are more than 2.";
/* 165:    */   }
/* 166:    */   
/* 167:    */   public boolean getTransformAllValues()
/* 168:    */   {
/* 169:354 */     return this.m_TransformAll;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public void setTransformAllValues(boolean bool)
/* 173:    */   {
/* 174:365 */     this.m_TransformAll = bool;
/* 175:    */   }
/* 176:    */   
/* 177:    */   public String invertSelectionTipText()
/* 178:    */   {
/* 179:376 */     return "Set attribute selection mode. If false, only selected (numeric) attributes in the range will be discretized; if true, only non-selected attributes will be discretized.";
/* 180:    */   }
/* 181:    */   
/* 182:    */   public boolean getInvertSelection()
/* 183:    */   {
/* 184:388 */     return this.m_Columns.getInvert();
/* 185:    */   }
/* 186:    */   
/* 187:    */   public void setInvertSelection(boolean invert)
/* 188:    */   {
/* 189:400 */     this.m_Columns.setInvert(invert);
/* 190:    */   }
/* 191:    */   
/* 192:    */   public String attributeIndicesTipText()
/* 193:    */   {
/* 194:410 */     return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/* 195:    */   }
/* 196:    */   
/* 197:    */   public String getAttributeIndices()
/* 198:    */   {
/* 199:423 */     return this.m_Columns.getRanges();
/* 200:    */   }
/* 201:    */   
/* 202:    */   public void setAttributeIndices(String rangeList)
/* 203:    */   {
/* 204:437 */     this.m_Columns.setRanges(rangeList);
/* 205:    */   }
/* 206:    */   
/* 207:    */   private void setOutputFormat()
/* 208:    */   {
/* 209:452 */     this.m_needToTransform = false;
/* 210:453 */     for (int i = 0; i < getInputFormat().numAttributes(); i++)
/* 211:    */     {
/* 212:454 */       Attribute att = getInputFormat().attribute(i);
/* 213:455 */       if ((att.isNominal()) && (i != getInputFormat().classIndex()) && ((att.numValues() > 2) || (this.m_TransformAll) || (this.m_Numeric)))
/* 214:    */       {
/* 215:457 */         this.m_needToTransform = true;
/* 216:458 */         break;
/* 217:    */       }
/* 218:    */     }
/* 219:462 */     if (!this.m_needToTransform)
/* 220:    */     {
/* 221:463 */       setOutputFormat(getInputFormat());
/* 222:464 */       return;
/* 223:    */     }
/* 224:467 */     int newClassIndex = getInputFormat().classIndex();
/* 225:468 */     ArrayList<Attribute> newAtts = new ArrayList();
/* 226:469 */     for (int j = 0; j < getInputFormat().numAttributes(); j++)
/* 227:    */     {
/* 228:470 */       Attribute att = getInputFormat().attribute(j);
/* 229:471 */       if ((!att.isNominal()) || (j == getInputFormat().classIndex()) || (!this.m_Columns.isInRange(j)))
/* 230:    */       {
/* 231:473 */         newAtts.add((Attribute)att.copy());
/* 232:    */       }
/* 233:475 */       else if ((att.numValues() <= 2) && (!this.m_TransformAll))
/* 234:    */       {
/* 235:476 */         if (this.m_Numeric)
/* 236:    */         {
/* 237:477 */           String value = "";
/* 238:478 */           if (att.numValues() == 2) {
/* 239:479 */             value = "=" + att.value(1);
/* 240:    */           }
/* 241:481 */           newAtts.add(new Attribute(att.name() + value));
/* 242:    */         }
/* 243:    */         else
/* 244:    */         {
/* 245:483 */           newAtts.add((Attribute)att.copy());
/* 246:    */         }
/* 247:    */       }
/* 248:    */       else
/* 249:    */       {
/* 250:487 */         if ((newClassIndex >= 0) && (j < getInputFormat().classIndex())) {
/* 251:488 */           newClassIndex += att.numValues() - 1;
/* 252:    */         }
/* 253:492 */         for (int k = 0; k < att.numValues(); k++)
/* 254:    */         {
/* 255:493 */           StringBuffer attributeName = new StringBuffer(att.name() + "=");
/* 256:494 */           attributeName.append(att.value(k));
/* 257:495 */           if (this.m_Numeric)
/* 258:    */           {
/* 259:496 */             newAtts.add(new Attribute(attributeName.toString()));
/* 260:    */           }
/* 261:    */           else
/* 262:    */           {
/* 263:498 */             ArrayList<String> vals = new ArrayList(2);
/* 264:499 */             vals.add("f");
/* 265:500 */             vals.add("t");
/* 266:501 */             newAtts.add(new Attribute(attributeName.toString(), vals));
/* 267:    */           }
/* 268:    */         }
/* 269:    */       }
/* 270:    */     }
/* 271:507 */     Instances outputFormat = new Instances(getInputFormat().relationName(), newAtts, 0);
/* 272:508 */     outputFormat.setClassIndex(newClassIndex);
/* 273:509 */     setOutputFormat(outputFormat);
/* 274:    */   }
/* 275:    */   
/* 276:    */   private void convertInstance(Instance instance)
/* 277:    */   {
/* 278:520 */     if (!this.m_needToTransform)
/* 279:    */     {
/* 280:521 */       push(instance);
/* 281:522 */       return;
/* 282:    */     }
/* 283:525 */     double[] vals = new double[outputFormatPeek().numAttributes()];
/* 284:526 */     int attSoFar = 0;
/* 285:528 */     for (int j = 0; j < getInputFormat().numAttributes(); j++)
/* 286:    */     {
/* 287:529 */       Attribute att = getInputFormat().attribute(j);
/* 288:530 */       if ((!att.isNominal()) || (j == getInputFormat().classIndex()) || (!this.m_Columns.isInRange(j)))
/* 289:    */       {
/* 290:532 */         vals[attSoFar] = instance.value(j);
/* 291:533 */         attSoFar++;
/* 292:    */       }
/* 293:535 */       else if ((att.numValues() <= 2) && (!this.m_TransformAll))
/* 294:    */       {
/* 295:536 */         vals[attSoFar] = instance.value(j);
/* 296:537 */         attSoFar++;
/* 297:    */       }
/* 298:    */       else
/* 299:    */       {
/* 300:539 */         if (instance.isMissing(j)) {
/* 301:540 */           for (int k = 0; k < att.numValues(); k++) {
/* 302:541 */             vals[(attSoFar + k)] = instance.value(j);
/* 303:    */           }
/* 304:    */         } else {
/* 305:544 */           for (int k = 0; k < att.numValues(); k++) {
/* 306:545 */             if (k == (int)instance.value(j)) {
/* 307:546 */               vals[(attSoFar + k)] = 1.0D;
/* 308:    */             } else {
/* 309:548 */               vals[(attSoFar + k)] = 0.0D;
/* 310:    */             }
/* 311:    */           }
/* 312:    */         }
/* 313:552 */         attSoFar += att.numValues();
/* 314:    */       }
/* 315:    */     }
/* 316:556 */     Instance inst = null;
/* 317:557 */     if ((instance instanceof SparseInstance)) {
/* 318:558 */       inst = new SparseInstance(instance.weight(), vals);
/* 319:    */     } else {
/* 320:560 */       inst = new DenseInstance(instance.weight(), vals);
/* 321:    */     }
/* 322:563 */     copyValues(inst, false, instance.dataset(), getOutputFormat());
/* 323:    */     
/* 324:565 */     push(inst);
/* 325:    */   }
/* 326:    */   
/* 327:    */   public String getRevision()
/* 328:    */   {
/* 329:575 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 330:    */   }
/* 331:    */   
/* 332:    */   public static void main(String[] argv)
/* 333:    */   {
/* 334:584 */     runFilter(new NominalToBinary(), argv);
/* 335:    */   }
/* 336:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.NominalToBinary
 * JD-Core Version:    0.7.0.1
 */