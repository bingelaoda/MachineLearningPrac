/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.Range;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.filters.SimpleStreamFilter;
/*  17:    */ 
/*  18:    */ public class RenameAttribute
/*  19:    */   extends SimpleStreamFilter
/*  20:    */ {
/*  21:    */   private static final long serialVersionUID = 4216491776378279596L;
/*  22:103 */   protected String m_Find = "([\\s\\S]+)";
/*  23:106 */   protected String m_Replace = "$0";
/*  24:109 */   protected Range m_AttributeIndices = new Range("first-last");
/*  25:112 */   protected boolean m_ReplaceAll = false;
/*  26:    */   
/*  27:    */   public String globalInfo()
/*  28:    */   {
/*  29:122 */     return "This filter is used for renaming attribute names.\nRegular expressions can be used in the matching and replacing.\nSee Javadoc of java.util.regex.Pattern class for more information:\nhttp://java.sun.com/javase/6/docs/api/java/util/regex/Pattern.html";
/*  30:    */   }
/*  31:    */   
/*  32:    */   public Enumeration<Option> listOptions()
/*  33:    */   {
/*  34:136 */     Vector<Option> result = new Vector(6);
/*  35:    */     
/*  36:138 */     result.addElement(new Option("\tThe regular expression that the attribute names must match.\n\t(default: ([\\s\\S]+))", "find", 1, "-find <regexp>"));
/*  37:    */     
/*  38:    */ 
/*  39:    */ 
/*  40:142 */     result.addElement(new Option("\tThe string to replace the regular expression of matching attributes with.\n\tCannot be used in conjunction with '-remove'.\n\t(default: $0)", "replace", 1, "-replace <string>"));
/*  41:    */     
/*  42:    */ 
/*  43:    */ 
/*  44:    */ 
/*  45:147 */     result.addElement(new Option("\tIn case the matching string needs to be removed instead of replaced.\n\tCannot be used in conjunction with '-replace <string>'.\n\t(default: off)", "remove", 0, "-remove"));
/*  46:    */     
/*  47:    */ 
/*  48:    */ 
/*  49:    */ 
/*  50:152 */     result.addElement(new Option("\tReplaces all occurrences instead of just the first.\n\t(default: only first occurrence)", "all", 0, "-all"));
/*  51:    */     
/*  52:    */ 
/*  53:    */ 
/*  54:156 */     result.addElement(new Option("\tThe attribute range to work on.\nThis is a comma separated list of attribute indices, with \"first\" and \"last\" valid values.\n\tSpecify an inclusive range with \"-\".\n\tE.g: \"first-3,5,6-10,last\".\n\t(default: first-last)", "R", 1, "-R <range>"));
/*  55:    */     
/*  56:    */ 
/*  57:    */ 
/*  58:    */ 
/*  59:    */ 
/*  60:    */ 
/*  61:163 */     result.addElement(new Option("\tInverts the attribute selection range.\n\t(default: off)", "V", 0, "-V"));
/*  62:    */     
/*  63:    */ 
/*  64:166 */     result.addAll(Collections.list(super.listOptions()));
/*  65:    */     
/*  66:168 */     return result.elements();
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void setOptions(String[] options)
/*  70:    */     throws Exception
/*  71:    */   {
/*  72:227 */     String tmpStr = Utils.getOption("find", options);
/*  73:228 */     if (tmpStr.length() != 0) {
/*  74:229 */       setFind(tmpStr);
/*  75:    */     } else {
/*  76:231 */       setFind("([\\s\\S]+)");
/*  77:    */     }
/*  78:234 */     if (Utils.getFlag("remove", options))
/*  79:    */     {
/*  80:235 */       setReplace("");
/*  81:    */     }
/*  82:    */     else
/*  83:    */     {
/*  84:237 */       tmpStr = Utils.getOption("replace", options);
/*  85:238 */       if (tmpStr.length() > 0) {
/*  86:239 */         setReplace(tmpStr);
/*  87:    */       } else {
/*  88:241 */         setReplace("$0");
/*  89:    */       }
/*  90:    */     }
/*  91:245 */     setReplaceAll(Utils.getFlag("all", options));
/*  92:    */     
/*  93:247 */     tmpStr = Utils.getOption("R", options);
/*  94:248 */     if (tmpStr.length() != 0) {
/*  95:249 */       setAttributeIndices(tmpStr);
/*  96:    */     } else {
/*  97:251 */       setAttributeIndices("first-last");
/*  98:    */     }
/*  99:254 */     setInvertSelection(Utils.getFlag("V", options));
/* 100:256 */     if (getInputFormat() != null) {
/* 101:257 */       setInputFormat(getInputFormat());
/* 102:    */     }
/* 103:260 */     super.setOptions(options);
/* 104:    */     
/* 105:262 */     Utils.checkForRemainingOptions(options);
/* 106:    */   }
/* 107:    */   
/* 108:    */   public String[] getOptions()
/* 109:    */   {
/* 110:273 */     Vector<String> result = new Vector();
/* 111:    */     
/* 112:275 */     result.add("-find");
/* 113:276 */     result.add(getFind());
/* 114:278 */     if (getReplace().length() > 0)
/* 115:    */     {
/* 116:279 */       result.add("-replace");
/* 117:280 */       result.add(getReplace());
/* 118:    */     }
/* 119:    */     else
/* 120:    */     {
/* 121:282 */       result.add("-remove");
/* 122:    */     }
/* 123:285 */     if (getReplaceAll()) {
/* 124:286 */       result.add("-all");
/* 125:    */     }
/* 126:289 */     result.add("-R");
/* 127:290 */     result.add(getAttributeIndices());
/* 128:292 */     if (getInvertSelection()) {
/* 129:293 */       result.add("-V");
/* 130:    */     }
/* 131:296 */     Collections.addAll(result, super.getOptions());
/* 132:    */     
/* 133:298 */     return (String[])result.toArray(new String[result.size()]);
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void setFind(String value)
/* 137:    */   {
/* 138:307 */     this.m_Find = value;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public String getFind()
/* 142:    */   {
/* 143:316 */     return this.m_Find;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public String findTipText()
/* 147:    */   {
/* 148:326 */     return "The regular expression that the attribute names must match.";
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void setReplace(String value)
/* 152:    */   {
/* 153:335 */     this.m_Replace = value;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public String getReplace()
/* 157:    */   {
/* 158:344 */     return this.m_Replace;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public String replaceTipText()
/* 162:    */   {
/* 163:354 */     return "The regular expression to use for replacing the matching attribute names with.";
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void setReplaceAll(boolean value)
/* 167:    */   {
/* 168:364 */     this.m_ReplaceAll = value;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public boolean getReplaceAll()
/* 172:    */   {
/* 173:373 */     return this.m_ReplaceAll;
/* 174:    */   }
/* 175:    */   
/* 176:    */   public String replaceAllTipText()
/* 177:    */   {
/* 178:383 */     return "If set to true, then all occurrences of the match will be replaced; otherwise only the first.";
/* 179:    */   }
/* 180:    */   
/* 181:    */   public void setAttributeIndices(String value)
/* 182:    */   {
/* 183:395 */     this.m_AttributeIndices.setRanges(value);
/* 184:    */   }
/* 185:    */   
/* 186:    */   public String getAttributeIndices()
/* 187:    */   {
/* 188:404 */     return this.m_AttributeIndices.getRanges();
/* 189:    */   }
/* 190:    */   
/* 191:    */   public String attributeIndicesTipText()
/* 192:    */   {
/* 193:414 */     return "Specify range of attributes to act on; this is a comma separated list of attribute indices, with \"first\" and \"last\" valid values; specify an inclusive range with \"-\"; eg: \"first-3,5,6-10,last\".";
/* 194:    */   }
/* 195:    */   
/* 196:    */   public void setInvertSelection(boolean value)
/* 197:    */   {
/* 198:426 */     this.m_AttributeIndices.setInvert(value);
/* 199:    */   }
/* 200:    */   
/* 201:    */   public boolean getInvertSelection()
/* 202:    */   {
/* 203:435 */     return this.m_AttributeIndices.getInvert();
/* 204:    */   }
/* 205:    */   
/* 206:    */   public String invertSelectionTipText()
/* 207:    */   {
/* 208:445 */     return "If set to true, the selection will be inverted; eg: the attribute indices '2-4' then mean everything apart from '2-4'.";
/* 209:    */   }
/* 210:    */   
/* 211:    */   public Capabilities getCapabilities()
/* 212:    */   {
/* 213:457 */     Capabilities result = super.getCapabilities();
/* 214:458 */     result.disableAll();
/* 215:    */     
/* 216:    */ 
/* 217:461 */     result.enableAllAttributes();
/* 218:462 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 219:    */     
/* 220:    */ 
/* 221:465 */     result.enableAllClasses();
/* 222:466 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 223:467 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 224:    */     
/* 225:469 */     return result;
/* 226:    */   }
/* 227:    */   
/* 228:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 229:    */     throws Exception
/* 230:    */   {
/* 231:491 */     this.m_AttributeIndices.setUpper(inputFormat.numAttributes() - 1);
/* 232:    */     
/* 233:    */ 
/* 234:494 */     ArrayList<Attribute> atts = new ArrayList();
/* 235:495 */     for (int i = 0; i < inputFormat.numAttributes(); i++)
/* 236:    */     {
/* 237:496 */       Attribute att = inputFormat.attribute(i);
/* 238:497 */       if (this.m_AttributeIndices.isInRange(i))
/* 239:    */       {
/* 240:498 */         if (this.m_ReplaceAll) {
/* 241:499 */           atts.add(att.copy(att.name().replaceAll(this.m_Find, this.m_Replace)));
/* 242:    */         } else {
/* 243:501 */           atts.add(att.copy(att.name().replaceFirst(this.m_Find, this.m_Replace)));
/* 244:    */         }
/* 245:    */       }
/* 246:    */       else {
/* 247:504 */         atts.add((Attribute)att.copy());
/* 248:    */       }
/* 249:    */     }
/* 250:507 */     Instances result = new Instances(inputFormat.relationName(), atts, 0);
/* 251:508 */     result.setClassIndex(inputFormat.classIndex());
/* 252:    */     
/* 253:510 */     return result;
/* 254:    */   }
/* 255:    */   
/* 256:    */   protected Instance process(Instance instance)
/* 257:    */     throws Exception
/* 258:    */   {
/* 259:523 */     return (Instance)instance.copy();
/* 260:    */   }
/* 261:    */   
/* 262:    */   public String getRevision()
/* 263:    */   {
/* 264:533 */     return RevisionUtils.extract("$Revision: 10215 $");
/* 265:    */   }
/* 266:    */   
/* 267:    */   public static void main(String[] args)
/* 268:    */   {
/* 269:542 */     runFilter(new RenameAttribute(), args);
/* 270:    */   }
/* 271:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.RenameAttribute
 * JD-Core Version:    0.7.0.1
 */