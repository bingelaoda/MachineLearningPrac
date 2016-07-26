/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.Utils;
/*  10:    */ import weka.gui.Logger;
/*  11:    */ 
/*  12:    */ public class MappingInfo
/*  13:    */   implements Serializable
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = -475467721189397466L;
/*  16:    */   public static final int UNKNOWN_NOMINAL_VALUE = -1;
/*  17: 56 */   private int[] m_fieldsMap = null;
/*  18: 67 */   private int[][] m_nominalValueMaps = (int[][])null;
/*  19: 70 */   private String m_fieldsMappingText = null;
/*  20: 73 */   private Logger m_log = null;
/*  21:    */   
/*  22:    */   public MappingInfo(Instances dataSet, MiningSchema miningSchema, Logger log)
/*  23:    */     throws Exception
/*  24:    */   {
/*  25: 76 */     this.m_log = log;
/*  26:    */     
/*  27: 78 */     Instances fieldsI = miningSchema.getMiningSchemaAsInstances();
/*  28:    */     
/*  29: 80 */     this.m_fieldsMap = new int[fieldsI.numAttributes()];
/*  30: 81 */     this.m_nominalValueMaps = new int[fieldsI.numAttributes()][];
/*  31: 83 */     for (int i = 0; i < fieldsI.numAttributes(); i++)
/*  32:    */     {
/*  33: 84 */       String schemaAttName = fieldsI.attribute(i).name();
/*  34: 85 */       boolean found = false;
/*  35: 86 */       for (int j = 0; j < dataSet.numAttributes(); j++) {
/*  36: 87 */         if (dataSet.attribute(j).name().equals(schemaAttName))
/*  37:    */         {
/*  38: 88 */           Attribute miningSchemaAtt = fieldsI.attribute(i);
/*  39: 89 */           Attribute incomingAtt = dataSet.attribute(j);
/*  40: 91 */           if ((miningSchemaAtt.type() != incomingAtt.type()) && (
/*  41: 92 */             (!miningSchemaAtt.isString()) || (!incomingAtt.isNominal()))) {
/*  42: 99 */             throw new Exception("[MappingInfo] type mismatch for field " + schemaAttName + ". Mining schema type " + miningSchemaAtt.toString() + ". Incoming type " + incomingAtt.toString() + ".");
/*  43:    */           }
/*  44:107 */           if (miningSchemaAtt.numValues() != incomingAtt.numValues())
/*  45:    */           {
/*  46:108 */             String warningString = "[MappingInfo] WARNING: incoming nominal attribute " + incomingAtt.name() + " does not have the same " + "number of values as the corresponding mining " + "schema attribute.";
/*  47:113 */             if (this.m_log != null) {
/*  48:114 */               this.m_log.logMessage(warningString);
/*  49:    */             } else {
/*  50:116 */               System.err.println(warningString);
/*  51:    */             }
/*  52:    */           }
/*  53:119 */           if ((miningSchemaAtt.isNominal()) || (miningSchemaAtt.isString()))
/*  54:    */           {
/*  55:120 */             int[] valuesMap = new int[incomingAtt.numValues()];
/*  56:121 */             for (int k = 0; k < incomingAtt.numValues(); k++)
/*  57:    */             {
/*  58:122 */               String incomingNomVal = incomingAtt.value(k);
/*  59:123 */               int indexInSchema = miningSchemaAtt.indexOfValue(incomingNomVal);
/*  60:124 */               if (indexInSchema < 0)
/*  61:    */               {
/*  62:125 */                 String warningString = "[MappingInfo] WARNING: incoming nominal attribute " + incomingAtt.name() + " has value " + incomingNomVal + " that doesn't occur in the mining schema.";
/*  63:130 */                 if (this.m_log != null) {
/*  64:131 */                   this.m_log.logMessage(warningString);
/*  65:    */                 } else {
/*  66:133 */                   System.err.println(warningString);
/*  67:    */                 }
/*  68:135 */                 valuesMap[k] = -1;
/*  69:    */               }
/*  70:    */               else
/*  71:    */               {
/*  72:137 */                 valuesMap[k] = indexInSchema;
/*  73:    */               }
/*  74:    */             }
/*  75:140 */             this.m_nominalValueMaps[i] = valuesMap;
/*  76:    */           }
/*  77:154 */           found = true;
/*  78:155 */           this.m_fieldsMap[i] = j;
/*  79:    */         }
/*  80:    */       }
/*  81:158 */       if (!found) {
/*  82:159 */         throw new Exception("[MappingInfo] Unable to find a match for mining schema attribute " + schemaAttName + " in the " + "incoming instances!");
/*  83:    */       }
/*  84:    */     }
/*  85:166 */     if (fieldsI.classIndex() >= 0) {
/*  86:167 */       if (dataSet.classIndex() < 0)
/*  87:    */       {
/*  88:169 */         String className = fieldsI.classAttribute().name();
/*  89:170 */         Attribute classMatch = dataSet.attribute(className);
/*  90:171 */         if (classMatch == null) {
/*  91:172 */           throw new Exception("[MappingInfo] Can't find match for target field " + className + "in incoming instances!");
/*  92:    */         }
/*  93:176 */         dataSet.setClass(classMatch);
/*  94:    */       }
/*  95:177 */       else if (!fieldsI.classAttribute().name().equals(dataSet.classAttribute().name()))
/*  96:    */       {
/*  97:179 */         throw new Exception("[MappingInfo] class attribute in mining schema does not match class attribute in incoming instances!");
/*  98:    */       }
/*  99:    */     }
/* 100:186 */     fieldsMappingString(fieldsI, dataSet);
/* 101:    */   }
/* 102:    */   
/* 103:    */   private void fieldsMappingString(Instances miningSchemaI, Instances incomingI)
/* 104:    */   {
/* 105:190 */     StringBuffer result = new StringBuffer();
/* 106:    */     
/* 107:192 */     int maxLength = 0;
/* 108:193 */     for (int i = 0; i < miningSchemaI.numAttributes(); i++) {
/* 109:194 */       if (miningSchemaI.attribute(i).name().length() > maxLength) {
/* 110:195 */         maxLength = miningSchemaI.attribute(i).name().length();
/* 111:    */       }
/* 112:    */     }
/* 113:198 */     maxLength += 12;
/* 114:    */     
/* 115:200 */     int minLength = 13;
/* 116:201 */     String headerS = "Mining schema";
/* 117:202 */     String sep = "-------------";
/* 118:204 */     if (maxLength < minLength) {
/* 119:205 */       maxLength = minLength;
/* 120:    */     }
/* 121:208 */     headerS = PMMLUtils.pad(headerS, " ", maxLength, false);
/* 122:209 */     sep = PMMLUtils.pad(sep, "-", maxLength, false);
/* 123:    */     
/* 124:211 */     sep = sep + "\t    ----------------\n";
/* 125:212 */     headerS = headerS + "\t    Incoming fields\n";
/* 126:213 */     result.append(headerS);
/* 127:214 */     result.append(sep);
/* 128:216 */     for (int i = 0; i < miningSchemaI.numAttributes(); i++)
/* 129:    */     {
/* 130:217 */       Attribute temp = miningSchemaI.attribute(i);
/* 131:218 */       String attName = "(" + (temp.isNumeric() ? "numeric)" : "nominal)") + " " + temp.name();
/* 132:    */       
/* 133:220 */       attName = PMMLUtils.pad(attName, " ", maxLength, false);
/* 134:221 */       attName = attName + "\t--> ";
/* 135:222 */       result.append(attName);
/* 136:    */       
/* 137:224 */       Attribute incoming = incomingI.attribute(this.m_fieldsMap[i]);
/* 138:225 */       String fieldName = "" + (this.m_fieldsMap[i] + 1) + " (" + (incoming.isNumeric() ? "numeric)" : "nominal)");
/* 139:    */       
/* 140:227 */       fieldName = fieldName + " " + incoming.name();
/* 141:228 */       result.append(fieldName + "\n");
/* 142:    */     }
/* 143:231 */     this.m_fieldsMappingText = result.toString();
/* 144:    */   }
/* 145:    */   
/* 146:    */   public double[] instanceToSchema(Instance inst, MiningSchema miningSchema)
/* 147:    */     throws Exception
/* 148:    */   {
/* 149:248 */     Instances miningSchemaI = miningSchema.getMiningSchemaAsInstances();
/* 150:    */     
/* 151:    */ 
/* 152:    */ 
/* 153:252 */     double[] result = new double[miningSchema.getFieldsAsInstances().numAttributes()];
/* 154:256 */     for (int i = 0; i < miningSchemaI.numAttributes(); i++)
/* 155:    */     {
/* 156:258 */       result[i] = inst.value(this.m_fieldsMap[i]);
/* 157:259 */       if ((miningSchemaI.attribute(i).isNominal()) || (miningSchemaI.attribute(i).isString())) {
/* 158:264 */         if (!Utils.isMissingValue(inst.value(this.m_fieldsMap[i])))
/* 159:    */         {
/* 160:265 */           int[] valueMap = this.m_nominalValueMaps[i];
/* 161:266 */           int index = valueMap[((int)inst.value(this.m_fieldsMap[i]))];
/* 162:267 */           String incomingAttValue = inst.attribute(this.m_fieldsMap[i]).value((int)inst.value(this.m_fieldsMap[i]));
/* 163:273 */           if (index >= 0)
/* 164:    */           {
/* 165:274 */             result[i] = index;
/* 166:    */           }
/* 167:    */           else
/* 168:    */           {
/* 169:277 */             result[i] = -1.0D;
/* 170:278 */             String warningString = "[MappingInfo] WARNING: Can't match nominal value " + incomingAttValue;
/* 171:280 */             if (this.m_log != null) {
/* 172:281 */               this.m_log.logMessage(warningString);
/* 173:    */             } else {
/* 174:283 */               System.err.println(warningString);
/* 175:    */             }
/* 176:    */           }
/* 177:    */         }
/* 178:    */       }
/* 179:    */     }
/* 180:291 */     miningSchema.applyMissingAndOutlierTreatments(result);
/* 181:    */     
/* 182:    */ 
/* 183:    */ 
/* 184:295 */     ArrayList<DerivedFieldMetaInfo> derivedFields = miningSchema.getDerivedFields();
/* 185:297 */     for (int i = 0; i < derivedFields.size(); i++)
/* 186:    */     {
/* 187:298 */       DerivedFieldMetaInfo temp = (DerivedFieldMetaInfo)derivedFields.get(i);
/* 188:    */       
/* 189:300 */       double r = temp.getDerivedValue(result);
/* 190:301 */       result[(i + miningSchemaI.numAttributes())] = r;
/* 191:    */     }
/* 192:309 */     return result;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public String getFieldsMappingString()
/* 196:    */   {
/* 197:319 */     if (this.m_fieldsMappingText == null) {
/* 198:320 */       return "No fields mapping constructed!";
/* 199:    */     }
/* 200:322 */     return this.m_fieldsMappingText;
/* 201:    */   }
/* 202:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.MappingInfo
 * JD-Core Version:    0.7.0.1
 */