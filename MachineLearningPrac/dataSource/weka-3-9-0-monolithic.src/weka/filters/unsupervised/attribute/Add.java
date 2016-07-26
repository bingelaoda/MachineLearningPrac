/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.text.SimpleDateFormat;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.Range;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SelectedTag;
/*  17:    */ import weka.core.SingleIndex;
/*  18:    */ import weka.core.Tag;
/*  19:    */ import weka.core.Utils;
/*  20:    */ import weka.filters.Filter;
/*  21:    */ import weka.filters.StreamableFilter;
/*  22:    */ import weka.filters.UnsupervisedFilter;
/*  23:    */ 
/*  24:    */ public class Add
/*  25:    */   extends Filter
/*  26:    */   implements UnsupervisedFilter, StreamableFilter, OptionHandler
/*  27:    */ {
/*  28:    */   static final long serialVersionUID = 761386447332932389L;
/*  29:101 */   public static final Tag[] TAGS_TYPE = { new Tag(0, "NUM", "Numeric attribute"), new Tag(1, "NOM", "Nominal attribute"), new Tag(2, "STR", "String attribute"), new Tag(3, "DAT", "Date attribute") };
/*  30:108 */   protected int m_AttributeType = 0;
/*  31:111 */   protected String m_Name = "unnamed";
/*  32:114 */   private final SingleIndex m_Insert = new SingleIndex("last");
/*  33:117 */   protected ArrayList<String> m_Labels = new ArrayList();
/*  34:120 */   protected String m_DateFormat = "yyyy-MM-dd'T'HH:mm:ss";
/*  35:    */   
/*  36:    */   public String globalInfo()
/*  37:    */   {
/*  38:130 */     return "An instance filter that adds a new attribute to the dataset. The new attribute will contain all missing values.";
/*  39:    */   }
/*  40:    */   
/*  41:    */   public Enumeration<Option> listOptions()
/*  42:    */   {
/*  43:146 */     Vector<Option> newVector = new Vector();
/*  44:    */     
/*  45:148 */     String desc = "";
/*  46:149 */     for (int i = 0; i < TAGS_TYPE.length; i++)
/*  47:    */     {
/*  48:150 */       SelectedTag tag = new SelectedTag(TAGS_TYPE[i].getID(), TAGS_TYPE);
/*  49:151 */       desc = desc + "\t" + tag.getSelectedTag().getIDStr() + " = " + tag.getSelectedTag().getReadable() + "\n";
/*  50:    */     }
/*  51:154 */     newVector.addElement(new Option("\tThe type of attribute to create:\n" + desc + "\t(default: " + new SelectedTag(0, TAGS_TYPE) + ")", "T", 1, "-T " + Tag.toOptionList(TAGS_TYPE)));
/*  52:    */     
/*  53:    */ 
/*  54:    */ 
/*  55:158 */     newVector.addElement(new Option("\tSpecify where to insert the column. First and last\n\tare valid indexes.(default: last)", "C", 1, "-C <index>"));
/*  56:    */     
/*  57:    */ 
/*  58:    */ 
/*  59:162 */     newVector.addElement(new Option("\tName of the new attribute.\n\t(default: 'Unnamed')", "N", 1, "-N <name>"));
/*  60:    */     
/*  61:    */ 
/*  62:165 */     newVector.addElement(new Option("\tCreate nominal attribute with given labels\n\t(default: numeric attribute)", "L", 1, "-L <label1,label2,...>"));
/*  63:    */     
/*  64:    */ 
/*  65:    */ 
/*  66:169 */     newVector.addElement(new Option("\tThe format of the date values (see ISO-8601)\n\t(default: yyyy-MM-dd'T'HH:mm:ss)", "F", 1, "-F <format>"));
/*  67:    */     
/*  68:    */ 
/*  69:    */ 
/*  70:173 */     return newVector.elements();
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setOptions(String[] options)
/*  74:    */     throws Exception
/*  75:    */   {
/*  76:226 */     String tmpStr = Utils.getOption('T', options);
/*  77:227 */     if (tmpStr.length() != 0) {
/*  78:228 */       setAttributeType(new SelectedTag(tmpStr, TAGS_TYPE));
/*  79:    */     } else {
/*  80:230 */       setAttributeType(new SelectedTag(0, TAGS_TYPE));
/*  81:    */     }
/*  82:233 */     tmpStr = Utils.getOption('C', options);
/*  83:234 */     if (tmpStr.length() == 0) {
/*  84:235 */       tmpStr = "last";
/*  85:    */     }
/*  86:237 */     setAttributeIndex(tmpStr);
/*  87:    */     
/*  88:239 */     setAttributeName(Utils.unbackQuoteChars(Utils.getOption('N', options)));
/*  89:241 */     if (this.m_AttributeType == 1)
/*  90:    */     {
/*  91:242 */       tmpStr = Utils.getOption('L', options);
/*  92:243 */       if (tmpStr.length() != 0) {
/*  93:244 */         setNominalLabels(tmpStr);
/*  94:    */       }
/*  95:    */     }
/*  96:246 */     else if (this.m_AttributeType == 3)
/*  97:    */     {
/*  98:247 */       tmpStr = Utils.getOption('F', options);
/*  99:248 */       if (tmpStr.length() != 0) {
/* 100:249 */         setDateFormat(tmpStr);
/* 101:    */       }
/* 102:    */     }
/* 103:253 */     if (getInputFormat() != null) {
/* 104:254 */       setInputFormat(getInputFormat());
/* 105:    */     }
/* 106:257 */     Utils.checkForRemainingOptions(options);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public String[] getOptions()
/* 110:    */   {
/* 111:269 */     Vector<String> result = new Vector();
/* 112:271 */     if (this.m_AttributeType != 0)
/* 113:    */     {
/* 114:272 */       result.add("-T");
/* 115:273 */       result.add("" + getAttributeType());
/* 116:    */     }
/* 117:276 */     result.add("-N");
/* 118:277 */     result.add(Utils.backQuoteChars(getAttributeName()));
/* 119:279 */     if (this.m_AttributeType == 1)
/* 120:    */     {
/* 121:280 */       result.add("-L");
/* 122:281 */       result.add(getNominalLabels());
/* 123:    */     }
/* 124:282 */     else if (this.m_AttributeType == 1)
/* 125:    */     {
/* 126:283 */       result.add("-F");
/* 127:284 */       result.add(getDateFormat());
/* 128:    */     }
/* 129:287 */     result.add("-C");
/* 130:288 */     result.add("" + getAttributeIndex());
/* 131:    */     
/* 132:290 */     return (String[])result.toArray(new String[result.size()]);
/* 133:    */   }
/* 134:    */   
/* 135:    */   public Capabilities getCapabilities()
/* 136:    */   {
/* 137:301 */     Capabilities result = super.getCapabilities();
/* 138:302 */     result.disableAll();
/* 139:    */     
/* 140:    */ 
/* 141:305 */     result.enableAllAttributes();
/* 142:306 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 143:    */     
/* 144:    */ 
/* 145:309 */     result.enableAllClasses();
/* 146:310 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 147:311 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 148:    */     
/* 149:313 */     return result;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public boolean setInputFormat(Instances instanceInfo)
/* 153:    */     throws Exception
/* 154:    */   {
/* 155:328 */     super.setInputFormat(instanceInfo);
/* 156:    */     
/* 157:330 */     this.m_Insert.setUpper(instanceInfo.numAttributes());
/* 158:331 */     Instances outputFormat = new Instances(instanceInfo, 0);
/* 159:332 */     Attribute newAttribute = null;
/* 160:333 */     switch (this.m_AttributeType)
/* 161:    */     {
/* 162:    */     case 0: 
/* 163:335 */       newAttribute = new Attribute(this.m_Name);
/* 164:336 */       break;
/* 165:    */     case 1: 
/* 166:338 */       newAttribute = new Attribute(this.m_Name, this.m_Labels);
/* 167:339 */       break;
/* 168:    */     case 2: 
/* 169:341 */       newAttribute = new Attribute(this.m_Name, (ArrayList)null);
/* 170:342 */       break;
/* 171:    */     case 3: 
/* 172:344 */       newAttribute = new Attribute(this.m_Name, this.m_DateFormat);
/* 173:345 */       break;
/* 174:    */     default: 
/* 175:347 */       throw new IllegalArgumentException("Unknown attribute type in Add");
/* 176:    */     }
/* 177:350 */     if ((this.m_Insert.getIndex() < 0) || (this.m_Insert.getIndex() > getInputFormat().numAttributes())) {
/* 178:352 */       throw new IllegalArgumentException("Index out of range");
/* 179:    */     }
/* 180:354 */     outputFormat.insertAttributeAt(newAttribute, this.m_Insert.getIndex());
/* 181:355 */     setOutputFormat(outputFormat);
/* 182:    */     
/* 183:    */ 
/* 184:    */ 
/* 185:359 */     Range atts = new Range(this.m_Insert.getSingleIndex());
/* 186:360 */     atts.setInvert(true);
/* 187:361 */     atts.setUpper(outputFormat.numAttributes() - 1);
/* 188:362 */     initOutputLocators(outputFormat, atts.getSelection());
/* 189:    */     
/* 190:364 */     return true;
/* 191:    */   }
/* 192:    */   
/* 193:    */   public boolean input(Instance instance)
/* 194:    */   {
/* 195:379 */     if (getInputFormat() == null) {
/* 196:380 */       throw new IllegalStateException("No input instance format defined");
/* 197:    */     }
/* 198:382 */     if (this.m_NewBatch)
/* 199:    */     {
/* 200:383 */       resetQueue();
/* 201:384 */       this.m_NewBatch = false;
/* 202:    */     }
/* 203:387 */     Instance inst = (Instance)instance.copy();
/* 204:    */     
/* 205:    */ 
/* 206:390 */     copyValues(inst, true, inst.dataset(), outputFormatPeek());
/* 207:    */     
/* 208:    */ 
/* 209:393 */     inst.setDataset(null);
/* 210:394 */     inst.insertAttributeAt(this.m_Insert.getIndex());
/* 211:    */     
/* 212:396 */     push(inst);
/* 213:397 */     return true;
/* 214:    */   }
/* 215:    */   
/* 216:    */   public String attributeNameTipText()
/* 217:    */   {
/* 218:408 */     return "Set the new attribute's name.";
/* 219:    */   }
/* 220:    */   
/* 221:    */   public String getAttributeName()
/* 222:    */   {
/* 223:418 */     return this.m_Name;
/* 224:    */   }
/* 225:    */   
/* 226:    */   public void setAttributeName(String name)
/* 227:    */   {
/* 228:427 */     if (name.trim().equals("")) {
/* 229:428 */       this.m_Name = "unnamed";
/* 230:    */     } else {
/* 231:430 */       this.m_Name = name;
/* 232:    */     }
/* 233:    */   }
/* 234:    */   
/* 235:    */   public String attributeIndexTipText()
/* 236:    */   {
/* 237:442 */     return "The position (starting from 1) where the attribute will be inserted (first and last are valid indices).";
/* 238:    */   }
/* 239:    */   
/* 240:    */   public String getAttributeIndex()
/* 241:    */   {
/* 242:453 */     return this.m_Insert.getSingleIndex();
/* 243:    */   }
/* 244:    */   
/* 245:    */   public void setAttributeIndex(String attIndex)
/* 246:    */   {
/* 247:463 */     this.m_Insert.setSingleIndex(attIndex);
/* 248:    */   }
/* 249:    */   
/* 250:    */   public String nominalLabelsTipText()
/* 251:    */   {
/* 252:473 */     return "The list of value labels (nominal attribute creation only).  The list must be comma-separated, eg: \"red,green,blue\". If this is empty, the created attribute will be numeric.";
/* 253:    */   }
/* 254:    */   
/* 255:    */   public String getNominalLabels()
/* 256:    */   {
/* 257:485 */     String labelList = "";
/* 258:486 */     for (int i = 0; i < this.m_Labels.size(); i++) {
/* 259:487 */       if (i == 0) {
/* 260:488 */         labelList = (String)this.m_Labels.get(i);
/* 261:    */       } else {
/* 262:490 */         labelList = labelList + "," + (String)this.m_Labels.get(i);
/* 263:    */       }
/* 264:    */     }
/* 265:493 */     return labelList;
/* 266:    */   }
/* 267:    */   
/* 268:    */   public void setNominalLabels(String labelList)
/* 269:    */   {
/* 270:504 */     ArrayList<String> labels = new ArrayList(10);
/* 271:    */     int commaLoc;
/* 272:508 */     while ((commaLoc = labelList.indexOf(',')) >= 0)
/* 273:    */     {
/* 274:509 */       String label = labelList.substring(0, commaLoc).trim();
/* 275:510 */       if (!label.equals("")) {
/* 276:511 */         labels.add(label);
/* 277:    */       } else {
/* 278:513 */         throw new IllegalArgumentException("Invalid label list at " + labelList.substring(commaLoc));
/* 279:    */       }
/* 280:516 */       labelList = labelList.substring(commaLoc + 1);
/* 281:    */     }
/* 282:518 */     String label = labelList.trim();
/* 283:519 */     if (!label.equals("")) {
/* 284:520 */       labels.add(label);
/* 285:    */     }
/* 286:524 */     this.m_Labels = labels;
/* 287:525 */     if (labels.size() == 0) {
/* 288:526 */       this.m_AttributeType = 0;
/* 289:    */     } else {
/* 290:528 */       this.m_AttributeType = 1;
/* 291:    */     }
/* 292:    */   }
/* 293:    */   
/* 294:    */   public String attributeTypeTipText()
/* 295:    */   {
/* 296:539 */     return "Defines the type of the attribute to generate.";
/* 297:    */   }
/* 298:    */   
/* 299:    */   public void setAttributeType(SelectedTag value)
/* 300:    */   {
/* 301:548 */     if (value.getTags() == TAGS_TYPE) {
/* 302:549 */       this.m_AttributeType = value.getSelectedTag().getID();
/* 303:    */     }
/* 304:    */   }
/* 305:    */   
/* 306:    */   public SelectedTag getAttributeType()
/* 307:    */   {
/* 308:559 */     return new SelectedTag(this.m_AttributeType, TAGS_TYPE);
/* 309:    */   }
/* 310:    */   
/* 311:    */   public String dateFormatTipText()
/* 312:    */   {
/* 313:569 */     return "The format of the date values (see ISO-8601).";
/* 314:    */   }
/* 315:    */   
/* 316:    */   public String getDateFormat()
/* 317:    */   {
/* 318:578 */     return this.m_DateFormat;
/* 319:    */   }
/* 320:    */   
/* 321:    */   public void setDateFormat(String value)
/* 322:    */   {
/* 323:    */     try
/* 324:    */     {
/* 325:588 */       new SimpleDateFormat(value);
/* 326:589 */       this.m_DateFormat = value;
/* 327:    */     }
/* 328:    */     catch (Exception e)
/* 329:    */     {
/* 330:591 */       e.printStackTrace();
/* 331:    */     }
/* 332:    */   }
/* 333:    */   
/* 334:    */   public String getRevision()
/* 335:    */   {
/* 336:602 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 337:    */   }
/* 338:    */   
/* 339:    */   public static void main(String[] argv)
/* 340:    */   {
/* 341:611 */     runFilter(new Add(), argv);
/* 342:    */   }
/* 343:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.Add
 * JD-Core Version:    0.7.0.1
 */