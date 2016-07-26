/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.OptionHandler;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.SelectedTag;
/*  14:    */ import weka.core.Tag;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.filters.Filter;
/*  17:    */ import weka.filters.StreamableFilter;
/*  18:    */ import weka.filters.UnsupervisedFilter;
/*  19:    */ 
/*  20:    */ public class RemoveType
/*  21:    */   extends Filter
/*  22:    */   implements UnsupervisedFilter, StreamableFilter, OptionHandler
/*  23:    */ {
/*  24:    */   static final long serialVersionUID = -3563999462782486279L;
/*  25: 74 */   protected Remove m_attributeFilter = new Remove();
/*  26: 77 */   protected int m_attTypeToDelete = 2;
/*  27: 80 */   protected boolean m_invert = false;
/*  28: 83 */   public static final Tag[] TAGS_ATTRIBUTETYPE = { new Tag(1, "Delete nominal attributes"), new Tag(0, "Delete numeric attributes"), new Tag(2, "Delete string attributes"), new Tag(3, "Delete date attributes"), new Tag(4, "Delete relational attributes") };
/*  29:    */   
/*  30:    */   public Capabilities getCapabilities()
/*  31:    */   {
/*  32: 98 */     Capabilities result = super.getCapabilities();
/*  33: 99 */     result.disableAll();
/*  34:    */     
/*  35:    */ 
/*  36:102 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  37:103 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  38:104 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  39:105 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/*  40:106 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/*  41:107 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  42:    */     
/*  43:    */ 
/*  44:110 */     result.enableAllClasses();
/*  45:111 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  46:112 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  47:    */     
/*  48:114 */     return result;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public boolean setInputFormat(Instances instanceInfo)
/*  52:    */     throws Exception
/*  53:    */   {
/*  54:129 */     super.setInputFormat(instanceInfo);
/*  55:    */     
/*  56:131 */     int[] attsToDelete = new int[instanceInfo.numAttributes()];
/*  57:132 */     int numToDelete = 0;
/*  58:133 */     for (int i = 0; i < instanceInfo.numAttributes(); i++) {
/*  59:134 */       if (i == instanceInfo.classIndex())
/*  60:    */       {
/*  61:135 */         if (this.m_invert) {
/*  62:138 */           attsToDelete[(numToDelete++)] = i;
/*  63:    */         }
/*  64:    */       }
/*  65:142 */       else if (instanceInfo.attribute(i).type() == this.m_attTypeToDelete) {
/*  66:143 */         attsToDelete[(numToDelete++)] = i;
/*  67:    */       }
/*  68:    */     }
/*  69:147 */     int[] finalAttsToDelete = new int[numToDelete];
/*  70:148 */     System.arraycopy(attsToDelete, 0, finalAttsToDelete, 0, numToDelete);
/*  71:    */     
/*  72:150 */     this.m_attributeFilter.setAttributeIndicesArray(finalAttsToDelete);
/*  73:151 */     this.m_attributeFilter.setInvertSelection(this.m_invert);
/*  74:    */     
/*  75:153 */     boolean result = this.m_attributeFilter.setInputFormat(instanceInfo);
/*  76:154 */     Instances afOutputFormat = this.m_attributeFilter.getOutputFormat();
/*  77:    */     
/*  78:    */ 
/*  79:157 */     afOutputFormat.setRelationName(instanceInfo.relationName());
/*  80:    */     
/*  81:159 */     setOutputFormat(afOutputFormat);
/*  82:160 */     return result;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public boolean input(Instance instance)
/*  86:    */   {
/*  87:172 */     return this.m_attributeFilter.input(instance);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public boolean batchFinished()
/*  91:    */     throws Exception
/*  92:    */   {
/*  93:184 */     return this.m_attributeFilter.batchFinished();
/*  94:    */   }
/*  95:    */   
/*  96:    */   public Instance output()
/*  97:    */   {
/*  98:196 */     return this.m_attributeFilter.output();
/*  99:    */   }
/* 100:    */   
/* 101:    */   public Instance outputPeek()
/* 102:    */   {
/* 103:208 */     return this.m_attributeFilter.outputPeek();
/* 104:    */   }
/* 105:    */   
/* 106:    */   public int numPendingOutput()
/* 107:    */   {
/* 108:219 */     return this.m_attributeFilter.numPendingOutput();
/* 109:    */   }
/* 110:    */   
/* 111:    */   public boolean isOutputFormatDefined()
/* 112:    */   {
/* 113:230 */     return this.m_attributeFilter.isOutputFormatDefined();
/* 114:    */   }
/* 115:    */   
/* 116:    */   public Enumeration<Option> listOptions()
/* 117:    */   {
/* 118:241 */     Vector<Option> newVector = new Vector(2);
/* 119:    */     
/* 120:243 */     newVector.addElement(new Option("\tAttribute type to delete. Valid options are \"nominal\", \n\t\"numeric\", \"string\", \"date\" and \"relational\".\n\t(default \"string\")", "T", 1, "-T <nominal|numeric|string|date|relational>"));
/* 121:    */     
/* 122:    */ 
/* 123:    */ 
/* 124:    */ 
/* 125:    */ 
/* 126:249 */     newVector.addElement(new Option("\tInvert matching sense (i.e. only keep specified columns)", "V", 0, "-V"));
/* 127:    */     
/* 128:    */ 
/* 129:    */ 
/* 130:253 */     return newVector.elements();
/* 131:    */   }
/* 132:    */   
/* 133:    */   public void setOptions(String[] options)
/* 134:    */     throws Exception
/* 135:    */   {
/* 136:283 */     String tString = Utils.getOption('T', options);
/* 137:284 */     if (tString.length() != 0) {
/* 138:285 */       setAttributeTypeString(tString);
/* 139:    */     }
/* 140:287 */     setInvertSelection(Utils.getFlag('V', options));
/* 141:289 */     if (getInputFormat() != null) {
/* 142:290 */       setInputFormat(getInputFormat());
/* 143:    */     }
/* 144:293 */     Utils.checkForRemainingOptions(options);
/* 145:    */   }
/* 146:    */   
/* 147:    */   public String[] getOptions()
/* 148:    */   {
/* 149:304 */     Vector<String> options = new Vector();
/* 150:306 */     if (getInvertSelection()) {
/* 151:307 */       options.add("-V");
/* 152:    */     }
/* 153:309 */     options.add("-T");
/* 154:310 */     options.add(getAttributeTypeString());
/* 155:    */     
/* 156:312 */     return (String[])options.toArray(new String[0]);
/* 157:    */   }
/* 158:    */   
/* 159:    */   public String globalInfo()
/* 160:    */   {
/* 161:323 */     return "Removes attributes of a given type.";
/* 162:    */   }
/* 163:    */   
/* 164:    */   public String attributeTypeTipText()
/* 165:    */   {
/* 166:334 */     return "The type of attribute to remove.";
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void setAttributeType(SelectedTag type)
/* 170:    */   {
/* 171:344 */     if (type.getTags() == TAGS_ATTRIBUTETYPE) {
/* 172:345 */       this.m_attTypeToDelete = type.getSelectedTag().getID();
/* 173:    */     }
/* 174:    */   }
/* 175:    */   
/* 176:    */   public SelectedTag getAttributeType()
/* 177:    */   {
/* 178:356 */     return new SelectedTag(this.m_attTypeToDelete, TAGS_ATTRIBUTETYPE);
/* 179:    */   }
/* 180:    */   
/* 181:    */   public String invertSelectionTipText()
/* 182:    */   {
/* 183:367 */     return "Determines whether action is to select or delete. If set to true, only the specified attributes will be kept; If set to false, specified attributes will be deleted.";
/* 184:    */   }
/* 185:    */   
/* 186:    */   public boolean getInvertSelection()
/* 187:    */   {
/* 188:379 */     return this.m_invert;
/* 189:    */   }
/* 190:    */   
/* 191:    */   public void setInvertSelection(boolean invert)
/* 192:    */   {
/* 193:391 */     this.m_invert = invert;
/* 194:    */   }
/* 195:    */   
/* 196:    */   protected String getAttributeTypeString()
/* 197:    */   {
/* 198:401 */     if (this.m_attTypeToDelete == 1) {
/* 199:402 */       return "nominal";
/* 200:    */     }
/* 201:403 */     if (this.m_attTypeToDelete == 0) {
/* 202:404 */       return "numeric";
/* 203:    */     }
/* 204:405 */     if (this.m_attTypeToDelete == 2) {
/* 205:406 */       return "string";
/* 206:    */     }
/* 207:407 */     if (this.m_attTypeToDelete == 3) {
/* 208:408 */       return "date";
/* 209:    */     }
/* 210:409 */     if (this.m_attTypeToDelete == 4) {
/* 211:410 */       return "relational";
/* 212:    */     }
/* 213:412 */     return "unknown";
/* 214:    */   }
/* 215:    */   
/* 216:    */   protected void setAttributeTypeString(String typeString)
/* 217:    */   {
/* 218:424 */     typeString = typeString.toLowerCase();
/* 219:425 */     if (typeString.equals("nominal")) {
/* 220:426 */       this.m_attTypeToDelete = 1;
/* 221:427 */     } else if (typeString.equals("numeric")) {
/* 222:428 */       this.m_attTypeToDelete = 0;
/* 223:429 */     } else if (typeString.equals("string")) {
/* 224:430 */       this.m_attTypeToDelete = 2;
/* 225:431 */     } else if (typeString.equals("date")) {
/* 226:432 */       this.m_attTypeToDelete = 3;
/* 227:433 */     } else if (typeString.equals("relational")) {
/* 228:434 */       this.m_attTypeToDelete = 4;
/* 229:    */     }
/* 230:    */   }
/* 231:    */   
/* 232:    */   public String getRevision()
/* 233:    */   {
/* 234:445 */     return RevisionUtils.extract("$Revision: 10215 $");
/* 235:    */   }
/* 236:    */   
/* 237:    */   public static void main(String[] argv)
/* 238:    */   {
/* 239:454 */     runFilter(new RemoveType(), argv);
/* 240:    */   }
/* 241:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.RemoveType
 * JD-Core Version:    0.7.0.1
 */