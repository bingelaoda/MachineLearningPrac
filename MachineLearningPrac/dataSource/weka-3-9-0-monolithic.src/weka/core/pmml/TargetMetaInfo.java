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
/*  11:    */ public class TargetMetaInfo
/*  12:    */   extends FieldMetaInfo
/*  13:    */   implements Serializable
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = 863500462237904927L;
/*  16: 46 */   protected double m_min = (0.0D / 0.0D);
/*  17: 47 */   protected double m_max = (0.0D / 0.0D);
/*  18: 50 */   protected double m_rescaleConstant = 0.0D;
/*  19: 51 */   protected double m_rescaleFactor = 1.0D;
/*  20: 54 */   protected String m_castInteger = "";
/*  21:    */   protected double[] m_defaultValueOrPriorProbs;
/*  22: 62 */   protected ArrayList<String> m_values = new ArrayList();
/*  23: 65 */   protected ArrayList<String> m_displayValues = new ArrayList();
/*  24:    */   
/*  25:    */   protected TargetMetaInfo(Element target)
/*  26:    */     throws Exception
/*  27:    */   {
/*  28: 77 */     super(target);
/*  29:    */     
/*  30:    */ 
/*  31:    */ 
/*  32:    */ 
/*  33:    */ 
/*  34:    */ 
/*  35:    */ 
/*  36:    */ 
/*  37:    */ 
/*  38:    */ 
/*  39:    */ 
/*  40:    */ 
/*  41:    */ 
/*  42: 91 */     String min = target.getAttribute("min");
/*  43: 92 */     if ((min != null) && (min.length() > 0)) {
/*  44:    */       try
/*  45:    */       {
/*  46: 94 */         this.m_min = Double.parseDouble(min);
/*  47:    */       }
/*  48:    */       catch (IllegalArgumentException ex)
/*  49:    */       {
/*  50: 96 */         throw new Exception("[TargetMetaInfo] can't parse min value for target field " + this.m_fieldName);
/*  51:    */       }
/*  52:    */     }
/*  53:101 */     String max = target.getAttribute("max");
/*  54:102 */     if ((max != null) && (max.length() > 0)) {
/*  55:    */       try
/*  56:    */       {
/*  57:104 */         this.m_max = Double.parseDouble(max);
/*  58:    */       }
/*  59:    */       catch (IllegalArgumentException ex)
/*  60:    */       {
/*  61:106 */         throw new Exception("[TargetMetaInfo] can't parse max value for target field " + this.m_fieldName);
/*  62:    */       }
/*  63:    */     }
/*  64:112 */     String rsc = target.getAttribute("rescaleConstant");
/*  65:113 */     if ((rsc != null) && (rsc.length() > 0)) {
/*  66:    */       try
/*  67:    */       {
/*  68:115 */         this.m_rescaleConstant = Double.parseDouble(rsc);
/*  69:    */       }
/*  70:    */       catch (IllegalArgumentException ex)
/*  71:    */       {
/*  72:117 */         throw new Exception("[TargetMetaInfo] can't parse rescale constant value for target field " + this.m_fieldName);
/*  73:    */       }
/*  74:    */     }
/*  75:121 */     String rsf = target.getAttribute("rescaleFactor");
/*  76:122 */     if ((rsf != null) && (rsf.length() > 0)) {
/*  77:    */       try
/*  78:    */       {
/*  79:124 */         this.m_rescaleFactor = Double.parseDouble(rsf);
/*  80:    */       }
/*  81:    */       catch (IllegalArgumentException ex)
/*  82:    */       {
/*  83:126 */         throw new Exception("[TargetMetaInfo] can't parse rescale factor value for target field " + this.m_fieldName);
/*  84:    */       }
/*  85:    */     }
/*  86:132 */     String cstI = target.getAttribute("castInteger");
/*  87:133 */     if ((cstI != null) && (cstI.length() > 0)) {
/*  88:134 */       this.m_castInteger = cstI;
/*  89:    */     }
/*  90:139 */     NodeList vals = target.getElementsByTagName("TargetValue");
/*  91:140 */     if (vals.getLength() > 0)
/*  92:    */     {
/*  93:141 */       this.m_defaultValueOrPriorProbs = new double[vals.getLength()];
/*  94:143 */       for (int i = 0; i < vals.getLength(); i++)
/*  95:    */       {
/*  96:144 */         Node value = vals.item(i);
/*  97:145 */         if (value.getNodeType() == 1)
/*  98:    */         {
/*  99:146 */           Element valueE = (Element)value;
/* 100:147 */           String valueName = valueE.getAttribute("value");
/* 101:148 */           if ((valueName != null) && (valueName.length() > 0))
/* 102:    */           {
/* 103:151 */             if ((this.m_optype != FieldMetaInfo.Optype.CATEGORICAL) && (this.m_optype != FieldMetaInfo.Optype.NONE)) {
/* 104:153 */               throw new Exception("[TargetMetaInfo] TargetValue element has categorical value but optype is not categorical!");
/* 105:    */             }
/* 106:157 */             if (this.m_optype == FieldMetaInfo.Optype.NONE) {
/* 107:158 */               this.m_optype = FieldMetaInfo.Optype.CATEGORICAL;
/* 108:    */             }
/* 109:161 */             this.m_values.add(valueName);
/* 110:    */             
/* 111:163 */             String displayValue = valueE.getAttribute("displayValue");
/* 112:164 */             if ((displayValue != null) && (displayValue.length() > 0)) {
/* 113:165 */               this.m_displayValues.add(displayValue);
/* 114:    */             } else {
/* 115:168 */               this.m_displayValues.add(valueName);
/* 116:    */             }
/* 117:172 */             String prior = valueE.getAttribute("priorProbability");
/* 118:173 */             if ((prior != null) && (prior.length() > 0)) {
/* 119:    */               try
/* 120:    */               {
/* 121:175 */                 this.m_defaultValueOrPriorProbs[i] = Double.parseDouble(prior);
/* 122:    */               }
/* 123:    */               catch (IllegalArgumentException ex)
/* 124:    */               {
/* 125:177 */                 throw new Exception("[TargetMetaInfo] Can't parse probability from TargetValue element.");
/* 126:    */               }
/* 127:    */             } else {
/* 128:181 */               throw new Exception("[TargetMetaInfo] No prior probability defined for value " + valueName);
/* 129:    */             }
/* 130:    */           }
/* 131:    */           else
/* 132:    */           {
/* 133:187 */             if ((this.m_optype != FieldMetaInfo.Optype.CONTINUOUS) && (this.m_optype != FieldMetaInfo.Optype.NONE)) {
/* 134:189 */               throw new Exception("[TargetMetaInfo] TargetValue element has continuous value but optype is not continuous!");
/* 135:    */             }
/* 136:193 */             if (this.m_optype == FieldMetaInfo.Optype.NONE) {
/* 137:194 */               this.m_optype = FieldMetaInfo.Optype.CONTINUOUS;
/* 138:    */             }
/* 139:198 */             String defaultV = valueE.getAttribute("defaultValue");
/* 140:199 */             if ((defaultV != null) && (defaultV.length() > 0)) {
/* 141:    */               try
/* 142:    */               {
/* 143:201 */                 this.m_defaultValueOrPriorProbs[i] = Double.parseDouble(defaultV);
/* 144:    */               }
/* 145:    */               catch (IllegalArgumentException ex)
/* 146:    */               {
/* 147:203 */                 throw new Exception("[TargetMetaInfo] Can't parse default value from TargetValue element.");
/* 148:    */               }
/* 149:    */             } else {
/* 150:207 */               throw new Exception("[TargetMetaInfo] No default value defined for target " + this.m_fieldName);
/* 151:    */             }
/* 152:    */           }
/* 153:    */         }
/* 154:    */       }
/* 155:    */     }
/* 156:    */   }
/* 157:    */   
/* 158:    */   public double getPriorProbability(String value)
/* 159:    */     throws Exception
/* 160:    */   {
/* 161:225 */     if (this.m_defaultValueOrPriorProbs == null) {
/* 162:226 */       throw new Exception("[TargetMetaInfo] no TargetValues defined (getPriorProbability)");
/* 163:    */     }
/* 164:228 */     double result = (0.0D / 0.0D);
/* 165:229 */     boolean found = false;
/* 166:230 */     for (int i = 0; i < this.m_values.size(); i++) {
/* 167:231 */       if (value.equals(this.m_values.get(i)))
/* 168:    */       {
/* 169:232 */         found = true;
/* 170:233 */         result = this.m_defaultValueOrPriorProbs[i];
/* 171:234 */         break;
/* 172:    */       }
/* 173:    */     }
/* 174:237 */     if (!found) {
/* 175:238 */       throw new Exception("[TargetMetaInfo] couldn't find value " + value + "(getPriorProbability)");
/* 176:    */     }
/* 177:241 */     return result;
/* 178:    */   }
/* 179:    */   
/* 180:    */   public double getDefaultValue()
/* 181:    */     throws Exception
/* 182:    */   {
/* 183:251 */     if (this.m_defaultValueOrPriorProbs == null) {
/* 184:252 */       throw new Exception("[TargetMetaInfo] no TargetValues defined (getPriorProbability)");
/* 185:    */     }
/* 186:254 */     return this.m_defaultValueOrPriorProbs[0];
/* 187:    */   }
/* 188:    */   
/* 189:    */   public ArrayList<String> getValues()
/* 190:    */   {
/* 191:264 */     return new ArrayList(this.m_values);
/* 192:    */   }
/* 193:    */   
/* 194:    */   public double applyMinMaxRescaleCast(double prediction)
/* 195:    */     throws Exception
/* 196:    */   {
/* 197:276 */     if (this.m_optype != FieldMetaInfo.Optype.CONTINUOUS) {
/* 198:277 */       throw new Exception("[TargetMetaInfo] target must be continuous!");
/* 199:    */     }
/* 200:280 */     if ((!Utils.isMissingValue(this.m_min)) && (prediction < this.m_min)) {
/* 201:281 */       prediction = this.m_min;
/* 202:    */     }
/* 203:283 */     if ((!Utils.isMissingValue(this.m_max)) && (prediction > this.m_max)) {
/* 204:284 */       prediction = this.m_max;
/* 205:    */     }
/* 206:287 */     prediction *= this.m_rescaleFactor;
/* 207:288 */     prediction += this.m_rescaleConstant;
/* 208:290 */     if (this.m_castInteger.length() > 0) {
/* 209:291 */       if (this.m_castInteger.equals("round")) {
/* 210:292 */         prediction = Math.round(prediction);
/* 211:293 */       } else if (this.m_castInteger.equals("ceiling")) {
/* 212:294 */         prediction = Math.ceil(prediction);
/* 213:295 */       } else if (this.m_castInteger.equals("floor")) {
/* 214:296 */         prediction = Math.floor(prediction);
/* 215:    */       } else {
/* 216:298 */         throw new Exception("[TargetMetaInfo] unknown castInteger value " + this.m_castInteger);
/* 217:    */       }
/* 218:    */     }
/* 219:303 */     return prediction;
/* 220:    */   }
/* 221:    */   
/* 222:    */   public Attribute getFieldAsAttribute()
/* 223:    */   {
/* 224:312 */     if (this.m_optype == FieldMetaInfo.Optype.CONTINUOUS) {
/* 225:313 */       return new Attribute(this.m_fieldName);
/* 226:    */     }
/* 227:315 */     if (this.m_values.size() == 0) {
/* 228:317 */       return new Attribute(this.m_fieldName, (ArrayList)null);
/* 229:    */     }
/* 230:320 */     ArrayList<String> values = new ArrayList();
/* 231:321 */     for (String val : this.m_values) {
/* 232:322 */       values.add(val);
/* 233:    */     }
/* 234:324 */     return new Attribute(this.m_fieldName, values);
/* 235:    */   }
/* 236:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.TargetMetaInfo
 * JD-Core Version:    0.7.0.1
 */