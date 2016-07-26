/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import org.w3c.dom.Element;
/*   6:    */ import org.w3c.dom.Node;
/*   7:    */ import org.w3c.dom.NodeList;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Utils;
/*  10:    */ 
/*  11:    */ public class Discretize
/*  12:    */   extends Expression
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = -5809107997906180082L;
/*  15:    */   protected String m_fieldName;
/*  16:    */   protected int m_fieldIndex;
/*  17:    */   
/*  18:    */   protected class DiscretizeBin
/*  19:    */     implements Serializable
/*  20:    */   {
/*  21:    */     private static final long serialVersionUID = 5810063243316808400L;
/*  22: 56 */     private final ArrayList<FieldMetaInfo.Interval> m_intervals = new ArrayList();
/*  23:    */     private final String m_binValue;
/*  24: 65 */     private double m_numericBinValue = Utils.missingValue();
/*  25:    */     
/*  26:    */     protected DiscretizeBin(Element bin, FieldMetaInfo.Optype opType)
/*  27:    */       throws Exception
/*  28:    */     {
/*  29: 68 */       NodeList iL = bin.getElementsByTagName("Interval");
/*  30: 69 */       for (int i = 0; i < iL.getLength(); i++)
/*  31:    */       {
/*  32: 70 */         Node iN = iL.item(i);
/*  33: 71 */         if (iN.getNodeType() == 1)
/*  34:    */         {
/*  35: 72 */           FieldMetaInfo.Interval tempInterval = new FieldMetaInfo.Interval((Element)iN);
/*  36:    */           
/*  37: 74 */           this.m_intervals.add(tempInterval);
/*  38:    */         }
/*  39:    */       }
/*  40: 78 */       this.m_binValue = bin.getAttribute("binValue");
/*  41: 80 */       if ((opType == FieldMetaInfo.Optype.CONTINUOUS) || (opType == FieldMetaInfo.Optype.ORDINAL)) {
/*  42:    */         try
/*  43:    */         {
/*  44: 83 */           this.m_numericBinValue = Double.parseDouble(this.m_binValue);
/*  45:    */         }
/*  46:    */         catch (NumberFormatException ex) {}
/*  47:    */       }
/*  48:    */     }
/*  49:    */     
/*  50:    */     protected String getBinValue()
/*  51:    */     {
/*  52: 96 */       return this.m_binValue;
/*  53:    */     }
/*  54:    */     
/*  55:    */     protected double getBinValueNumeric()
/*  56:    */     {
/*  57:106 */       return this.m_numericBinValue;
/*  58:    */     }
/*  59:    */     
/*  60:    */     protected boolean containsValue(double value)
/*  61:    */     {
/*  62:116 */       boolean result = false;
/*  63:118 */       for (FieldMetaInfo.Interval i : this.m_intervals) {
/*  64:119 */         if (i.containsValue(value))
/*  65:    */         {
/*  66:120 */           result = true;
/*  67:121 */           break;
/*  68:    */         }
/*  69:    */       }
/*  70:125 */       return result;
/*  71:    */     }
/*  72:    */     
/*  73:    */     public String toString()
/*  74:    */     {
/*  75:130 */       StringBuffer buff = new StringBuffer();
/*  76:    */       
/*  77:132 */       buff.append("\"" + this.m_binValue + "\" if value in: ");
/*  78:133 */       boolean first = true;
/*  79:134 */       for (FieldMetaInfo.Interval i : this.m_intervals)
/*  80:    */       {
/*  81:135 */         if (!first) {
/*  82:136 */           buff.append(", ");
/*  83:    */         } else {
/*  84:138 */           first = false;
/*  85:    */         }
/*  86:140 */         buff.append(i.toString());
/*  87:    */       }
/*  88:143 */       return buff.toString();
/*  89:    */     }
/*  90:    */   }
/*  91:    */   
/*  92:154 */   protected boolean m_mapMissingDefined = false;
/*  93:    */   protected String m_mapMissingTo;
/*  94:160 */   protected boolean m_defaultValueDefined = false;
/*  95:    */   protected String m_defaultValue;
/*  96:166 */   protected ArrayList<DiscretizeBin> m_bins = new ArrayList();
/*  97:    */   protected Attribute m_outputDef;
/*  98:    */   
/*  99:    */   public Discretize(Element discretize, FieldMetaInfo.Optype opType, ArrayList<Attribute> fieldDefs)
/* 100:    */     throws Exception
/* 101:    */   {
/* 102:182 */     super(opType, fieldDefs);
/* 103:    */     
/* 104:    */ 
/* 105:    */ 
/* 106:    */ 
/* 107:    */ 
/* 108:    */ 
/* 109:189 */     this.m_fieldName = discretize.getAttribute("field");
/* 110:    */     
/* 111:191 */     this.m_mapMissingTo = discretize.getAttribute("mapMissingTo");
/* 112:192 */     if ((this.m_mapMissingTo != null) && (this.m_mapMissingTo.length() > 0)) {
/* 113:193 */       this.m_mapMissingDefined = true;
/* 114:    */     }
/* 115:196 */     this.m_defaultValue = discretize.getAttribute("defaultValue");
/* 116:197 */     if ((this.m_defaultValue != null) && (this.m_defaultValue.length() > 0)) {
/* 117:198 */       this.m_defaultValueDefined = true;
/* 118:    */     }
/* 119:202 */     NodeList dbL = discretize.getElementsByTagName("DiscretizeBin");
/* 120:203 */     for (int i = 0; i < dbL.getLength(); i++)
/* 121:    */     {
/* 122:204 */       Node dbN = dbL.item(i);
/* 123:205 */       if (dbN.getNodeType() == 1)
/* 124:    */       {
/* 125:206 */         Element dbE = (Element)dbN;
/* 126:207 */         DiscretizeBin db = new DiscretizeBin(dbE, this.m_opType);
/* 127:208 */         this.m_bins.add(db);
/* 128:    */       }
/* 129:    */     }
/* 130:212 */     if (fieldDefs != null) {
/* 131:213 */       setUpField();
/* 132:    */     }
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void setFieldDefs(ArrayList<Attribute> fieldDefs)
/* 136:    */     throws Exception
/* 137:    */   {
/* 138:225 */     super.setFieldDefs(fieldDefs);
/* 139:226 */     setUpField();
/* 140:    */   }
/* 141:    */   
/* 142:    */   private void setUpField()
/* 143:    */     throws Exception
/* 144:    */   {
/* 145:230 */     this.m_fieldIndex = -1;
/* 146:232 */     if (this.m_fieldDefs != null)
/* 147:    */     {
/* 148:233 */       this.m_fieldIndex = getFieldDefIndex(this.m_fieldName);
/* 149:234 */       if (this.m_fieldIndex < 0) {
/* 150:235 */         throw new Exception("[Discretize] Can't find field " + this.m_fieldName + " in the supplied field definitions.");
/* 151:    */       }
/* 152:239 */       Attribute field = (Attribute)this.m_fieldDefs.get(this.m_fieldIndex);
/* 153:240 */       if (!field.isNumeric()) {
/* 154:241 */         throw new Exception("[Discretize] reference field " + this.m_fieldName + " must be continuous.");
/* 155:    */       }
/* 156:    */     }
/* 157:247 */     Attribute tempAtt = null;
/* 158:248 */     boolean categorical = false;
/* 159:249 */     if ((this.m_opType == FieldMetaInfo.Optype.CONTINUOUS) || (this.m_opType == FieldMetaInfo.Optype.ORDINAL)) {
/* 160:252 */       for (DiscretizeBin d : this.m_bins) {
/* 161:253 */         if (Utils.isMissingValue(d.getBinValueNumeric()))
/* 162:    */         {
/* 163:254 */           categorical = true;
/* 164:255 */           break;
/* 165:    */         }
/* 166:    */       }
/* 167:    */     } else {
/* 168:259 */       categorical = true;
/* 169:    */     }
/* 170:261 */     tempAtt = categorical ? new Attribute("temp", (ArrayList)null) : new Attribute(this.m_fieldName + "_discretized(optype=continuous)");
/* 171:264 */     if (categorical)
/* 172:    */     {
/* 173:265 */       for (DiscretizeBin d : this.m_bins) {
/* 174:266 */         tempAtt.addStringValue(d.getBinValue());
/* 175:    */       }
/* 176:271 */       if (this.m_defaultValueDefined) {
/* 177:272 */         tempAtt.addStringValue(this.m_defaultValue);
/* 178:    */       }
/* 179:278 */       if (this.m_mapMissingDefined) {
/* 180:279 */         tempAtt.addStringValue(this.m_mapMissingTo);
/* 181:    */       }
/* 182:283 */       ArrayList<String> values = new ArrayList();
/* 183:284 */       for (int i = 0; i < tempAtt.numValues(); i++) {
/* 184:285 */         values.add(tempAtt.value(i));
/* 185:    */       }
/* 186:288 */       this.m_outputDef = new Attribute(this.m_fieldName + "_discretized", values);
/* 187:    */     }
/* 188:    */     else
/* 189:    */     {
/* 190:290 */       this.m_outputDef = tempAtt;
/* 191:    */     }
/* 192:    */   }
/* 193:    */   
/* 194:    */   protected Attribute getOutputDef()
/* 195:    */   {
/* 196:303 */     if (this.m_outputDef == null) {
/* 197:307 */       return (this.m_opType == FieldMetaInfo.Optype.CATEGORICAL) || (this.m_opType == FieldMetaInfo.Optype.ORDINAL) ? new Attribute(this.m_fieldName + "_discretized", new ArrayList()) : new Attribute(this.m_fieldName + "_discretized(optype=continuous)");
/* 198:    */     }
/* 199:311 */     return this.m_outputDef;
/* 200:    */   }
/* 201:    */   
/* 202:    */   public double getResult(double[] incoming)
/* 203:    */     throws Exception
/* 204:    */   {
/* 205:328 */     double result = Utils.missingValue();
/* 206:    */     
/* 207:330 */     double value = incoming[this.m_fieldIndex];
/* 208:332 */     if (Utils.isMissingValue(value))
/* 209:    */     {
/* 210:333 */       if (this.m_mapMissingDefined) {
/* 211:334 */         if (this.m_outputDef.isNominal()) {
/* 212:335 */           result = this.m_outputDef.indexOfValue(this.m_mapMissingTo);
/* 213:    */         } else {
/* 214:    */           try
/* 215:    */           {
/* 216:338 */             result = Double.parseDouble(this.m_mapMissingTo);
/* 217:    */           }
/* 218:    */           catch (NumberFormatException ex)
/* 219:    */           {
/* 220:340 */             throw new Exception("[Discretize] Optype is continuous but value of mapMissingTo can not be parsed as a number!");
/* 221:    */           }
/* 222:    */         }
/* 223:    */       }
/* 224:    */     }
/* 225:    */     else
/* 226:    */     {
/* 227:348 */       boolean found = false;
/* 228:349 */       for (DiscretizeBin b : this.m_bins) {
/* 229:350 */         if (b.containsValue(value))
/* 230:    */         {
/* 231:351 */           found = true;
/* 232:352 */           if (this.m_outputDef.isNominal())
/* 233:    */           {
/* 234:353 */             result = this.m_outputDef.indexOfValue(b.getBinValue()); break;
/* 235:    */           }
/* 236:355 */           result = b.getBinValueNumeric();
/* 237:    */           
/* 238:357 */           break;
/* 239:    */         }
/* 240:    */       }
/* 241:361 */       if ((!found) && 
/* 242:362 */         (this.m_defaultValueDefined)) {
/* 243:363 */         if (this.m_outputDef.isNominal()) {
/* 244:364 */           result = this.m_outputDef.indexOfValue(this.m_defaultValue);
/* 245:    */         } else {
/* 246:    */           try
/* 247:    */           {
/* 248:367 */             result = Double.parseDouble(this.m_defaultValue);
/* 249:    */           }
/* 250:    */           catch (NumberFormatException ex)
/* 251:    */           {
/* 252:369 */             throw new Exception("[Discretize] Optype is continuous but value of default value can not be parsed as a number!");
/* 253:    */           }
/* 254:    */         }
/* 255:    */       }
/* 256:    */     }
/* 257:378 */     return result;
/* 258:    */   }
/* 259:    */   
/* 260:    */   public String getResultCategorical(double[] incoming)
/* 261:    */     throws Exception
/* 262:    */   {
/* 263:391 */     double index = getResult(incoming);
/* 264:392 */     if (Utils.isMissingValue(index)) {
/* 265:393 */       return "**Missing Value**";
/* 266:    */     }
/* 267:396 */     return this.m_outputDef.value((int)index);
/* 268:    */   }
/* 269:    */   
/* 270:    */   public String toString(String pad)
/* 271:    */   {
/* 272:406 */     StringBuffer buff = new StringBuffer();
/* 273:    */     
/* 274:408 */     buff.append(pad + "Discretize (" + this.m_fieldName + "):");
/* 275:409 */     for (DiscretizeBin d : this.m_bins) {
/* 276:410 */       buff.append("\n" + pad + d.toString());
/* 277:    */     }
/* 278:413 */     if (this.m_outputDef.isNumeric()) {
/* 279:414 */       buff.append("\n" + pad + "(bin values interpreted as numbers)");
/* 280:    */     }
/* 281:417 */     if (this.m_mapMissingDefined) {
/* 282:418 */       buff.append("\n" + pad + "map missing values to: " + this.m_mapMissingTo);
/* 283:    */     }
/* 284:421 */     if (this.m_defaultValueDefined) {
/* 285:422 */       buff.append("\n" + pad + "default value: " + this.m_defaultValue);
/* 286:    */     }
/* 287:425 */     return buff.toString();
/* 288:    */   }
/* 289:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.Discretize
 * JD-Core Version:    0.7.0.1
 */