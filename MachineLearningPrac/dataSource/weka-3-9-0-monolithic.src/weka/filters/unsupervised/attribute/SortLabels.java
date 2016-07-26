/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Comparator;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Capabilities.Capability;
/*  12:    */ import weka.core.DenseInstance;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.Range;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.SelectedTag;
/*  19:    */ import weka.core.Tag;
/*  20:    */ import weka.core.Utils;
/*  21:    */ import weka.filters.SimpleStreamFilter;
/*  22:    */ 
/*  23:    */ public class SortLabels
/*  24:    */   extends SimpleStreamFilter
/*  25:    */ {
/*  26:    */   private static final long serialVersionUID = 7815204879694105691L;
/*  27:    */   public static final int SORT_CASESENSITIVE = 0;
/*  28:    */   public static final int SORT_CASEINSENSITIVE = 1;
/*  29:    */   
/*  30:    */   public static class CaseSensitiveComparator
/*  31:    */     implements Comparator<String>, Serializable
/*  32:    */   {
/*  33:    */     private static final long serialVersionUID = 7071450356783873277L;
/*  34:    */     
/*  35:    */     public int compare(String o1, String o2)
/*  36:    */     {
/*  37:112 */       if ((o1 == null) && (o2 == null)) {
/*  38:113 */         return 0;
/*  39:    */       }
/*  40:114 */       if (o1 == null) {
/*  41:115 */         return -1;
/*  42:    */       }
/*  43:116 */       if (o2 == null) {
/*  44:117 */         return 1;
/*  45:    */       }
/*  46:120 */       String s1 = o1;
/*  47:121 */       String s2 = o2;
/*  48:    */       
/*  49:123 */       return s1.compareTo(s2);
/*  50:    */     }
/*  51:    */   }
/*  52:    */   
/*  53:    */   public static class CaseInsensitiveComparator
/*  54:    */     implements Comparator<String>, Serializable
/*  55:    */   {
/*  56:    */     private static final long serialVersionUID = -4515292733342486066L;
/*  57:    */     
/*  58:    */     public int compare(String o1, String o2)
/*  59:    */     {
/*  60:152 */       if ((o1 == null) && (o2 == null)) {
/*  61:153 */         return 0;
/*  62:    */       }
/*  63:154 */       if (o1 == null) {
/*  64:155 */         return -1;
/*  65:    */       }
/*  66:156 */       if (o2 == null) {
/*  67:157 */         return 1;
/*  68:    */       }
/*  69:160 */       String s1 = o1;
/*  70:161 */       String s2 = o2;
/*  71:    */       
/*  72:163 */       return s1.toLowerCase().compareTo(s2.toLowerCase());
/*  73:    */     }
/*  74:    */   }
/*  75:    */   
/*  76:174 */   public static final Tag[] TAGS_SORTTYPE = { new Tag(0, "case", "Case-sensitive"), new Tag(1, "non-case", "Case-insensitive") };
/*  77:182 */   protected Range m_AttributeIndices = new Range("first-last");
/*  78:185 */   protected int[][] m_NewOrder = (int[][])null;
/*  79:188 */   protected int m_SortType = 1;
/*  80:191 */   protected Comparator<String> m_Comparator = new CaseSensitiveComparator();
/*  81:    */   
/*  82:    */   public String globalInfo()
/*  83:    */   {
/*  84:201 */     return "A simple filter for sorting the labels of nominal attributes.";
/*  85:    */   }
/*  86:    */   
/*  87:    */   public Enumeration<Option> listOptions()
/*  88:    */   {
/*  89:212 */     Vector<Option> result = new Vector();
/*  90:    */     
/*  91:214 */     result.addElement(new Option("\tSpecify list of string attributes to convert to words.\n\t(default: select all relational attributes)", "R", 1, "-R <index1,index2-index4,...>"));
/*  92:    */     
/*  93:    */ 
/*  94:    */ 
/*  95:    */ 
/*  96:219 */     result.addElement(new Option("\tInverts the matching sense of the selection.", "V", 0, "-V"));
/*  97:    */     
/*  98:    */ 
/*  99:222 */     String desc = "";
/* 100:223 */     for (Tag element : TAGS_SORTTYPE)
/* 101:    */     {
/* 102:224 */       SelectedTag tag = new SelectedTag(element.getID(), TAGS_SORTTYPE);
/* 103:225 */       desc = desc + "\t" + tag.getSelectedTag().getIDStr() + " = " + tag.getSelectedTag().getReadable() + "\n";
/* 104:    */     }
/* 105:228 */     result.addElement(new Option("\tDetermines the type of sorting:\n" + desc + "\t(default: " + new SelectedTag(0, TAGS_SORTTYPE) + ")", "S", 1, "-S " + Tag.toOptionList(TAGS_SORTTYPE)));
/* 106:    */     
/* 107:    */ 
/* 108:    */ 
/* 109:232 */     result.addAll(Collections.list(super.listOptions()));
/* 110:    */     
/* 111:234 */     return result.elements();
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setOptions(String[] options)
/* 115:    */     throws Exception
/* 116:    */   {
/* 117:276 */     String tmpStr = Utils.getOption('R', options);
/* 118:277 */     if (tmpStr.length() != 0) {
/* 119:278 */       setAttributeIndices(tmpStr);
/* 120:    */     } else {
/* 121:280 */       setAttributeIndices("first-last");
/* 122:    */     }
/* 123:283 */     setInvertSelection(Utils.getFlag('V', options));
/* 124:    */     
/* 125:285 */     tmpStr = Utils.getOption('S', options);
/* 126:286 */     if (tmpStr.length() != 0) {
/* 127:287 */       setSortType(new SelectedTag(tmpStr, TAGS_SORTTYPE));
/* 128:    */     } else {
/* 129:289 */       setSortType(new SelectedTag(0, TAGS_SORTTYPE));
/* 130:    */     }
/* 131:292 */     super.setOptions(options);
/* 132:    */     
/* 133:294 */     Utils.checkForRemainingOptions(options);
/* 134:    */   }
/* 135:    */   
/* 136:    */   public String[] getOptions()
/* 137:    */   {
/* 138:305 */     Vector<String> result = new Vector();
/* 139:    */     
/* 140:307 */     result.add("-R");
/* 141:308 */     result.add(getAttributeIndices());
/* 142:310 */     if (getInvertSelection()) {
/* 143:311 */       result.add("-V");
/* 144:    */     }
/* 145:314 */     result.add("-S");
/* 146:315 */     result.add("" + getSortType());
/* 147:    */     
/* 148:317 */     Collections.addAll(result, super.getOptions());
/* 149:    */     
/* 150:319 */     return (String[])result.toArray(new String[result.size()]);
/* 151:    */   }
/* 152:    */   
/* 153:    */   public String attributeIndicesTipText()
/* 154:    */   {
/* 155:329 */     return "Specify range of attributes to act on; this is a comma separated list of attribute indices, with \"first\" and \"last\" valid values; Specify an inclusive range with \"-\"; eg: \"first-3,5,6-10,last\".";
/* 156:    */   }
/* 157:    */   
/* 158:    */   public void setAttributeIndices(String value)
/* 159:    */   {
/* 160:341 */     this.m_AttributeIndices = new Range(value);
/* 161:    */   }
/* 162:    */   
/* 163:    */   public String getAttributeIndices()
/* 164:    */   {
/* 165:350 */     return this.m_AttributeIndices.getRanges();
/* 166:    */   }
/* 167:    */   
/* 168:    */   public String invertSelectionTipText()
/* 169:    */   {
/* 170:360 */     return "Set attribute selection mode. If false, only selected attributes in the range will be worked on; if true, only non-selected attributes will be processed.";
/* 171:    */   }
/* 172:    */   
/* 173:    */   public void setInvertSelection(boolean value)
/* 174:    */   {
/* 175:371 */     this.m_AttributeIndices.setInvert(value);
/* 176:    */   }
/* 177:    */   
/* 178:    */   public boolean getInvertSelection()
/* 179:    */   {
/* 180:380 */     return this.m_AttributeIndices.getInvert();
/* 181:    */   }
/* 182:    */   
/* 183:    */   public String sortTypeTipText()
/* 184:    */   {
/* 185:390 */     return "The type of sorting to use.";
/* 186:    */   }
/* 187:    */   
/* 188:    */   public void setSortType(SelectedTag type)
/* 189:    */   {
/* 190:399 */     if (type.getTags() == TAGS_SORTTYPE)
/* 191:    */     {
/* 192:400 */       this.m_SortType = type.getSelectedTag().getID();
/* 193:402 */       if (this.m_SortType == 0) {
/* 194:403 */         this.m_Comparator = new CaseSensitiveComparator();
/* 195:404 */       } else if (this.m_SortType == 1) {
/* 196:405 */         this.m_Comparator = new CaseInsensitiveComparator();
/* 197:    */       } else {
/* 198:407 */         throw new IllegalStateException("Unhandled sort type '" + type + "'!");
/* 199:    */       }
/* 200:    */     }
/* 201:    */   }
/* 202:    */   
/* 203:    */   public SelectedTag getSortType()
/* 204:    */   {
/* 205:418 */     return new SelectedTag(this.m_SortType, TAGS_SORTTYPE);
/* 206:    */   }
/* 207:    */   
/* 208:    */   public Capabilities getCapabilities()
/* 209:    */   {
/* 210:429 */     Capabilities result = super.getCapabilities();
/* 211:430 */     result.disableAll();
/* 212:    */     
/* 213:    */ 
/* 214:433 */     result.enableAllAttributes();
/* 215:434 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 216:    */     
/* 217:    */ 
/* 218:437 */     result.enableAllClasses();
/* 219:438 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 220:439 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 221:    */     
/* 222:441 */     return result;
/* 223:    */   }
/* 224:    */   
/* 225:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 226:    */     throws Exception
/* 227:    */   {
/* 228:463 */     this.m_AttributeIndices.setUpper(inputFormat.numAttributes() - 1);
/* 229:    */     
/* 230:    */ 
/* 231:466 */     ArrayList<Attribute> atts = new ArrayList();
/* 232:467 */     this.m_NewOrder = new int[inputFormat.numAttributes()][];
/* 233:468 */     for (int i = 0; i < inputFormat.numAttributes(); i++)
/* 234:    */     {
/* 235:469 */       Attribute att = inputFormat.attribute(i);
/* 236:470 */       if ((!att.isNominal()) || (!this.m_AttributeIndices.isInRange(i)))
/* 237:    */       {
/* 238:471 */         this.m_NewOrder[i] = new int[0];
/* 239:472 */         atts.add((Attribute)inputFormat.attribute(i).copy());
/* 240:    */       }
/* 241:    */       else
/* 242:    */       {
/* 243:477 */         Vector<String> sorted = new Vector();
/* 244:478 */         for (int n = 0; n < att.numValues(); n++) {
/* 245:479 */           sorted.add(att.value(n));
/* 246:    */         }
/* 247:481 */         Collections.sort(sorted, this.m_Comparator);
/* 248:    */         
/* 249:    */ 
/* 250:484 */         this.m_NewOrder[i] = new int[att.numValues()];
/* 251:485 */         ArrayList<String> values = new ArrayList();
/* 252:486 */         for (n = 0; n < att.numValues(); n++)
/* 253:    */         {
/* 254:487 */           this.m_NewOrder[i][n] = sorted.indexOf(att.value(n));
/* 255:488 */           values.add(sorted.get(n));
/* 256:    */         }
/* 257:490 */         Attribute attSorted = new Attribute(att.name(), values);
/* 258:491 */         attSorted.setWeight(att.weight());
/* 259:492 */         atts.add(attSorted);
/* 260:    */       }
/* 261:    */     }
/* 262:496 */     Instances result = new Instances(inputFormat.relationName(), atts, 0);
/* 263:497 */     result.setClassIndex(inputFormat.classIndex());
/* 264:    */     
/* 265:499 */     return result;
/* 266:    */   }
/* 267:    */   
/* 268:    */   protected Instance process(Instance instance)
/* 269:    */     throws Exception
/* 270:    */   {
/* 271:518 */     double[] values = new double[instance.numAttributes()];
/* 272:519 */     for (int i = 0; i < instance.numAttributes(); i++)
/* 273:    */     {
/* 274:520 */       Attribute att = instance.attribute(i);
/* 275:521 */       if ((!att.isNominal()) || (!this.m_AttributeIndices.isInRange(i)) || (instance.isMissing(i))) {
/* 276:523 */         values[i] = instance.value(i);
/* 277:    */       } else {
/* 278:525 */         values[i] = this.m_NewOrder[i][((int)instance.value(i))];
/* 279:    */       }
/* 280:    */     }
/* 281:530 */     Instance result = new DenseInstance(instance.weight(), values);
/* 282:    */     
/* 283:    */ 
/* 284:533 */     copyValues(result, false, instance.dataset(), outputFormatPeek());
/* 285:    */     
/* 286:535 */     return result;
/* 287:    */   }
/* 288:    */   
/* 289:    */   public String getRevision()
/* 290:    */   {
/* 291:545 */     return RevisionUtils.extract("$Revision: 12395 $");
/* 292:    */   }
/* 293:    */   
/* 294:    */   public static void main(String[] args)
/* 295:    */   {
/* 296:554 */     runFilter(new SortLabels(), args);
/* 297:    */   }
/* 298:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.SortLabels
 * JD-Core Version:    0.7.0.1
 */