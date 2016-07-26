/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.HashMap;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Map;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Capabilities.Capability;
/*  12:    */ import weka.core.DenseInstance;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.OptionHandler;
/*  17:    */ import weka.core.Range;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.SparseInstance;
/*  20:    */ import weka.core.Utils;
/*  21:    */ import weka.core.WekaException;
/*  22:    */ import weka.filters.Filter;
/*  23:    */ import weka.filters.StreamableFilter;
/*  24:    */ import weka.filters.UnsupervisedFilter;
/*  25:    */ 
/*  26:    */ public class RenameNominalValues
/*  27:    */   extends Filter
/*  28:    */   implements UnsupervisedFilter, StreamableFilter, OptionHandler
/*  29:    */ {
/*  30:    */   private static final long serialVersionUID = -2121767582746512209L;
/*  31: 93 */   protected String m_selectedColsString = "";
/*  32: 96 */   protected Range m_selectedCols = new Range();
/*  33: 99 */   protected String m_renameVals = "";
/*  34:102 */   protected boolean m_ignoreCase = false;
/*  35:105 */   protected boolean m_invert = false;
/*  36:    */   protected int[] m_selectedAttributes;
/*  37:114 */   protected Map<String, String> m_renameMap = new HashMap();
/*  38:    */   
/*  39:    */   public String globalInfo()
/*  40:    */   {
/*  41:122 */     return "Renames the values of nominal attributes.";
/*  42:    */   }
/*  43:    */   
/*  44:    */   public boolean setInputFormat(Instances instanceInfo)
/*  45:    */     throws Exception
/*  46:    */   {
/*  47:137 */     super.setInputFormat(instanceInfo);
/*  48:    */     
/*  49:139 */     int classIndex = instanceInfo.classIndex();
/*  50:142 */     if ((this.m_renameVals != null) && (this.m_renameVals.length() > 0))
/*  51:    */     {
/*  52:143 */       String[] vals = this.m_renameVals.split(",");
/*  53:145 */       for (String val : vals)
/*  54:    */       {
/*  55:146 */         String[] parts = val.split(":");
/*  56:147 */         if (parts.length != 2) {
/*  57:148 */           throw new WekaException("Invalid replacement string: " + val);
/*  58:    */         }
/*  59:151 */         if ((parts[0].length() == 0) || (parts[1].length() == 0)) {
/*  60:152 */           throw new WekaException("Invalid replacement string: " + val);
/*  61:    */         }
/*  62:155 */         this.m_renameMap.put(this.m_ignoreCase ? parts[0].toLowerCase().trim() : parts[0].trim(), parts[1].trim());
/*  63:    */       }
/*  64:    */     }
/*  65:162 */     Range tempRange = new Range();
/*  66:163 */     tempRange.setInvert(this.m_invert);
/*  67:164 */     if (this.m_selectedColsString == null) {
/*  68:165 */       this.m_selectedColsString = "";
/*  69:    */     }
/*  70:    */     try
/*  71:    */     {
/*  72:169 */       tempRange.setRanges(this.m_selectedColsString);
/*  73:170 */       tempRange.setUpper(instanceInfo.numAttributes() - 1);
/*  74:171 */       this.m_selectedAttributes = tempRange.getSelection();
/*  75:172 */       this.m_selectedCols = tempRange;
/*  76:    */     }
/*  77:    */     catch (Exception r)
/*  78:    */     {
/*  79:175 */       StringBuffer indexes = new StringBuffer();
/*  80:176 */       String[] attNames = this.m_selectedColsString.split(",");
/*  81:177 */       boolean first = true;
/*  82:178 */       for (String n : attNames)
/*  83:    */       {
/*  84:179 */         n = n.trim();
/*  85:180 */         Attribute found = instanceInfo.attribute(n);
/*  86:181 */         if (found == null) {
/*  87:182 */           throw new WekaException("Unable to find attribute '" + n + "' in the incoming instances'");
/*  88:    */         }
/*  89:185 */         if (first)
/*  90:    */         {
/*  91:186 */           indexes.append("" + (found.index() + 1));
/*  92:187 */           first = false;
/*  93:    */         }
/*  94:    */         else
/*  95:    */         {
/*  96:189 */           indexes.append("," + (found.index() + 1));
/*  97:    */         }
/*  98:    */       }
/*  99:193 */       tempRange = new Range();
/* 100:194 */       tempRange.setRanges(indexes.toString());
/* 101:195 */       tempRange.setUpper(instanceInfo.numAttributes() - 1);
/* 102:196 */       this.m_selectedAttributes = tempRange.getSelection();
/* 103:197 */       this.m_selectedCols = tempRange;
/* 104:    */     }
/* 105:200 */     ArrayList<Attribute> attributes = new ArrayList();
/* 106:201 */     for (int i = 0; i < instanceInfo.numAttributes(); i++) {
/* 107:202 */       if (this.m_selectedCols.isInRange(i))
/* 108:    */       {
/* 109:203 */         if (instanceInfo.attribute(i).isNominal())
/* 110:    */         {
/* 111:204 */           List<String> valsForAtt = new ArrayList();
/* 112:205 */           for (int j = 0; j < instanceInfo.attribute(i).numValues(); j++)
/* 113:    */           {
/* 114:206 */             String origV = instanceInfo.attribute(i).value(j);
/* 115:    */             
/* 116:208 */             String replace = this.m_ignoreCase ? (String)this.m_renameMap.get(origV.toLowerCase()) : (String)this.m_renameMap.get(origV);
/* 117:210 */             if ((replace != null) && (!valsForAtt.contains(replace))) {
/* 118:211 */               valsForAtt.add(replace);
/* 119:    */             } else {
/* 120:213 */               valsForAtt.add(origV);
/* 121:    */             }
/* 122:    */           }
/* 123:216 */           Attribute newAtt = new Attribute(instanceInfo.attribute(i).name(), valsForAtt);
/* 124:    */           
/* 125:218 */           attributes.add(newAtt);
/* 126:    */         }
/* 127:    */         else
/* 128:    */         {
/* 129:221 */           Attribute att = (Attribute)instanceInfo.attribute(i).copy();
/* 130:222 */           attributes.add(att);
/* 131:    */         }
/* 132:    */       }
/* 133:    */       else
/* 134:    */       {
/* 135:225 */         Attribute att = (Attribute)instanceInfo.attribute(i).copy();
/* 136:226 */         attributes.add(att);
/* 137:    */       }
/* 138:    */     }
/* 139:230 */     Instances outputFormat = new Instances(instanceInfo.relationName(), attributes, 0);
/* 140:    */     
/* 141:232 */     outputFormat.setClassIndex(classIndex);
/* 142:233 */     setOutputFormat(outputFormat);
/* 143:    */     
/* 144:235 */     return true;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public boolean input(Instance instance)
/* 148:    */   {
/* 149:250 */     if (getInputFormat() == null) {
/* 150:251 */       throw new IllegalStateException("No input instance format defined");
/* 151:    */     }
/* 152:253 */     if (this.m_NewBatch)
/* 153:    */     {
/* 154:254 */       resetQueue();
/* 155:255 */       this.m_NewBatch = false;
/* 156:    */     }
/* 157:258 */     if (getOutputFormat().numAttributes() == 0) {
/* 158:259 */       return false;
/* 159:    */     }
/* 160:262 */     if (this.m_selectedAttributes.length == 0)
/* 161:    */     {
/* 162:263 */       push(instance);
/* 163:    */     }
/* 164:    */     else
/* 165:    */     {
/* 166:265 */       double[] vals = new double[getOutputFormat().numAttributes()];
/* 167:266 */       for (int i = 0; i < instance.numAttributes(); i++)
/* 168:    */       {
/* 169:267 */         double currentV = instance.value(i);
/* 170:269 */         if (!this.m_selectedCols.isInRange(i))
/* 171:    */         {
/* 172:270 */           vals[i] = currentV;
/* 173:    */         }
/* 174:272 */         else if (currentV == Utils.missingValue())
/* 175:    */         {
/* 176:273 */           vals[i] = currentV;
/* 177:    */         }
/* 178:    */         else
/* 179:    */         {
/* 180:275 */           String currentS = instance.attribute(i).value((int)currentV);
/* 181:276 */           String replace = this.m_ignoreCase ? (String)this.m_renameMap.get(currentS.toLowerCase()) : (String)this.m_renameMap.get(currentS);
/* 182:278 */           if (replace == null) {
/* 183:279 */             vals[i] = currentV;
/* 184:    */           } else {
/* 185:281 */             vals[i] = getOutputFormat().attribute(i).indexOfValue(replace);
/* 186:    */           }
/* 187:    */         }
/* 188:    */       }
/* 189:287 */       Instance inst = null;
/* 190:288 */       if ((instance instanceof SparseInstance)) {
/* 191:289 */         inst = new SparseInstance(instance.weight(), vals);
/* 192:    */       } else {
/* 193:291 */         inst = new DenseInstance(instance.weight(), vals);
/* 194:    */       }
/* 195:294 */       copyValues(inst, false, instance.dataset(), outputFormatPeek());
/* 196:    */       
/* 197:296 */       push(inst);
/* 198:    */     }
/* 199:299 */     return true;
/* 200:    */   }
/* 201:    */   
/* 202:    */   public Capabilities getCapabilities()
/* 203:    */   {
/* 204:310 */     Capabilities result = super.getCapabilities();
/* 205:311 */     result.disableAll();
/* 206:    */     
/* 207:    */ 
/* 208:314 */     result.enableAllAttributes();
/* 209:315 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 210:    */     
/* 211:    */ 
/* 212:318 */     result.enableAllClasses();
/* 213:319 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 214:320 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 215:    */     
/* 216:322 */     return result;
/* 217:    */   }
/* 218:    */   
/* 219:    */   public String selectedAttributesTipText()
/* 220:    */   {
/* 221:332 */     return "The attributes (index range string or explicit comma-separated attribute names) to work on";
/* 222:    */   }
/* 223:    */   
/* 224:    */   public void setSelectedAttributes(String atts)
/* 225:    */   {
/* 226:337 */     this.m_selectedColsString = atts;
/* 227:    */   }
/* 228:    */   
/* 229:    */   public String getSelectedAttributes()
/* 230:    */   {
/* 231:341 */     return this.m_selectedColsString;
/* 232:    */   }
/* 233:    */   
/* 234:    */   public String valueReplacementsTipText()
/* 235:    */   {
/* 236:351 */     return "A comma separated list of values to replace and their replacements. E.g. red:green, blue:purple, fred:bob";
/* 237:    */   }
/* 238:    */   
/* 239:    */   public void setValueReplacements(String v)
/* 240:    */   {
/* 241:356 */     this.m_renameVals = v;
/* 242:    */   }
/* 243:    */   
/* 244:    */   public String getValueReplacements()
/* 245:    */   {
/* 246:360 */     return this.m_renameVals;
/* 247:    */   }
/* 248:    */   
/* 249:    */   public String invertSelectionTipText()
/* 250:    */   {
/* 251:371 */     return "Determines whether to apply the operation to the specified. attributes, or all attributes but the specified ones. If set to true, all attributes but the speficied ones will be affected.";
/* 252:    */   }
/* 253:    */   
/* 254:    */   public boolean getInvertSelection()
/* 255:    */   {
/* 256:383 */     return this.m_invert;
/* 257:    */   }
/* 258:    */   
/* 259:    */   public void setInvertSelection(boolean invert)
/* 260:    */   {
/* 261:395 */     this.m_invert = invert;
/* 262:    */   }
/* 263:    */   
/* 264:    */   public String ignoreCaseTipText()
/* 265:    */   {
/* 266:405 */     return "Whether to ignore case when matching nominal values";
/* 267:    */   }
/* 268:    */   
/* 269:    */   public void setIgnoreCase(boolean ignore)
/* 270:    */   {
/* 271:409 */     this.m_ignoreCase = ignore;
/* 272:    */   }
/* 273:    */   
/* 274:    */   public boolean getIgnoreCase()
/* 275:    */   {
/* 276:413 */     return this.m_ignoreCase;
/* 277:    */   }
/* 278:    */   
/* 279:    */   public Enumeration<Option> listOptions()
/* 280:    */   {
/* 281:419 */     Vector<Option> newVector = new Vector(4);
/* 282:420 */     newVector.addElement(new Option("\tAttributes to act on. Can be either a range\n\tstring (e.g. 1,2,6-10) OR a comma-separated list of named attributes\n\t(default none)", "R", 1, "-R <1,2-4 | attName1,attName2,...>"));
/* 283:    */     
/* 284:    */ 
/* 285:    */ 
/* 286:    */ 
/* 287:425 */     newVector.addElement(new Option("\tInvert matching sense (i.e. act on all attributes other than those specified)", "V", 0, "-V"));
/* 288:    */     
/* 289:    */ 
/* 290:    */ 
/* 291:429 */     newVector.addElement(new Option("\tNominal labels and their replacement values.\n\tE.g. red:blue, black:white, fred:bob", "N", 1, "-N <label:label,label:label,...>"));
/* 292:    */     
/* 293:    */ 
/* 294:    */ 
/* 295:433 */     newVector.addElement(new Option("\tIgnore case when matching nominal values", "I", 0, "-I"));
/* 296:    */     
/* 297:    */ 
/* 298:436 */     return newVector.elements();
/* 299:    */   }
/* 300:    */   
/* 301:    */   public void setOptions(String[] options)
/* 302:    */     throws Exception
/* 303:    */   {
/* 304:476 */     String atts = Utils.getOption('R', options);
/* 305:477 */     if (atts.length() > 0) {
/* 306:478 */       setSelectedAttributes(atts);
/* 307:    */     }
/* 308:481 */     String replacements = Utils.getOption('N', options);
/* 309:482 */     if (replacements.length() > 0) {
/* 310:483 */       setValueReplacements(replacements);
/* 311:    */     }
/* 312:486 */     setInvertSelection(Utils.getFlag('V', options));
/* 313:487 */     setIgnoreCase(Utils.getFlag('I', options));
/* 314:    */     
/* 315:489 */     Utils.checkForRemainingOptions(options);
/* 316:    */   }
/* 317:    */   
/* 318:    */   public String[] getOptions()
/* 319:    */   {
/* 320:495 */     List<String> opts = new ArrayList();
/* 321:497 */     if ((getSelectedAttributes() != null) && (getSelectedAttributes().length() > 0))
/* 322:    */     {
/* 323:498 */       opts.add("-R");
/* 324:499 */       opts.add(getSelectedAttributes());
/* 325:    */     }
/* 326:502 */     if (getInvertSelection()) {
/* 327:503 */       opts.add("-V");
/* 328:    */     }
/* 329:506 */     if ((getValueReplacements() != null) && (getValueReplacements().length() > 0))
/* 330:    */     {
/* 331:507 */       opts.add("-N");
/* 332:508 */       opts.add(getValueReplacements());
/* 333:    */     }
/* 334:511 */     if (getIgnoreCase()) {
/* 335:512 */       opts.add("-I");
/* 336:    */     }
/* 337:515 */     return (String[])opts.toArray(new String[opts.size()]);
/* 338:    */   }
/* 339:    */   
/* 340:    */   public String getRevision()
/* 341:    */   {
/* 342:525 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 343:    */   }
/* 344:    */   
/* 345:    */   public static void main(String[] argv)
/* 346:    */   {
/* 347:534 */     runFilter(new RenameNominalValues(), argv);
/* 348:    */   }
/* 349:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.RenameNominalValues
 * JD-Core Version:    0.7.0.1
 */