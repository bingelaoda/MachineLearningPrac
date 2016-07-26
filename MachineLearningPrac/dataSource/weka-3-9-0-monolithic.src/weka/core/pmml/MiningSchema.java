/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import org.w3c.dom.Element;
/*   6:    */ import org.w3c.dom.Node;
/*   7:    */ import org.w3c.dom.NodeList;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Instances;
/*  10:    */ 
/*  11:    */ public class MiningSchema
/*  12:    */   implements Serializable
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 7144380586726330455L;
/*  15:    */   protected Instances m_fieldInstancesStructure;
/*  16:    */   protected Instances m_miningSchemaInstancesStructure;
/*  17: 63 */   protected ArrayList<MiningFieldMetaInfo> m_miningMeta = new ArrayList();
/*  18: 71 */   protected ArrayList<DerivedFieldMetaInfo> m_derivedMeta = new ArrayList();
/*  19: 75 */   protected TransformationDictionary m_transformationDictionary = null;
/*  20: 78 */   protected TargetMetaInfo m_targetMetaInfo = null;
/*  21:    */   
/*  22:    */   private void getLocalTransformations(Element model)
/*  23:    */     throws Exception
/*  24:    */   {
/*  25: 81 */     NodeList temp = model.getElementsByTagName("LocalTransformations");
/*  26: 83 */     if (temp.getLength() > 0)
/*  27:    */     {
/*  28: 85 */       Element localT = (Element)temp.item(0);
/*  29:    */       
/*  30:    */ 
/*  31:    */ 
/*  32:    */ 
/*  33:    */ 
/*  34:    */ 
/*  35:    */ 
/*  36: 93 */       NodeList localDerivedL = localT.getElementsByTagName("DerivedField");
/*  37: 94 */       for (int i = 0; i < localDerivedL.getLength(); i++)
/*  38:    */       {
/*  39: 95 */         Node localDerived = localDerivedL.item(i);
/*  40: 96 */         if (localDerived.getNodeType() == 1)
/*  41:    */         {
/*  42: 97 */           DerivedFieldMetaInfo d = new DerivedFieldMetaInfo((Element)localDerived, null, this.m_transformationDictionary);
/*  43:    */           
/*  44: 99 */           this.m_derivedMeta.add(d);
/*  45:    */         }
/*  46:    */       }
/*  47:    */     }
/*  48:    */   }
/*  49:    */   
/*  50:    */   public MiningSchema(Element model, Instances dataDictionary, TransformationDictionary transDict)
/*  51:    */     throws Exception
/*  52:    */   {
/*  53:125 */     ArrayList<Attribute> attInfo = new ArrayList();
/*  54:126 */     NodeList fieldList = model.getElementsByTagName("MiningField");
/*  55:127 */     int classIndex = -1;
/*  56:128 */     int addedCount = 0;
/*  57:129 */     for (int i = 0; i < fieldList.getLength(); i++)
/*  58:    */     {
/*  59:130 */       Node miningField = fieldList.item(i);
/*  60:131 */       if (miningField.getNodeType() == 1)
/*  61:    */       {
/*  62:132 */         Element miningFieldEl = (Element)miningField;
/*  63:    */         
/*  64:134 */         MiningFieldMetaInfo mfi = new MiningFieldMetaInfo(miningFieldEl);
/*  65:136 */         if ((mfi.getUsageType() == MiningFieldMetaInfo.Usage.ACTIVE) || (mfi.getUsageType() == MiningFieldMetaInfo.Usage.PREDICTED))
/*  66:    */         {
/*  67:140 */           Attribute miningAtt = dataDictionary.attribute(mfi.getName());
/*  68:141 */           if (miningAtt != null)
/*  69:    */           {
/*  70:142 */             mfi.setIndex(addedCount);
/*  71:143 */             attInfo.add(miningAtt);
/*  72:144 */             addedCount++;
/*  73:146 */             if (mfi.getUsageType() == MiningFieldMetaInfo.Usage.PREDICTED) {
/*  74:147 */               classIndex = addedCount - 1;
/*  75:    */             }
/*  76:151 */             this.m_miningMeta.add(mfi);
/*  77:    */           }
/*  78:    */           else
/*  79:    */           {
/*  80:153 */             throw new Exception("Can't find mining field: " + mfi.getName() + " in the data dictionary.");
/*  81:    */           }
/*  82:    */         }
/*  83:    */       }
/*  84:    */     }
/*  85:160 */     this.m_miningSchemaInstancesStructure = new Instances("miningSchema", attInfo, 0);
/*  86:164 */     for (MiningFieldMetaInfo m : this.m_miningMeta) {
/*  87:165 */       m.setMiningSchemaInstances(this.m_miningSchemaInstancesStructure);
/*  88:    */     }
/*  89:168 */     this.m_transformationDictionary = transDict;
/*  90:171 */     if (this.m_transformationDictionary != null)
/*  91:    */     {
/*  92:172 */       ArrayList<DerivedFieldMetaInfo> transDerived = transDict.getDerivedFields();
/*  93:173 */       this.m_derivedMeta.addAll(transDerived);
/*  94:    */     }
/*  95:177 */     getLocalTransformations(model);
/*  96:    */     
/*  97:    */ 
/*  98:    */ 
/*  99:181 */     ArrayList<Attribute> newStructure = new ArrayList();
/* 100:182 */     for (MiningFieldMetaInfo m : this.m_miningMeta) {
/* 101:183 */       newStructure.add(m.getFieldAsAttribute());
/* 102:    */     }
/* 103:186 */     for (DerivedFieldMetaInfo d : this.m_derivedMeta) {
/* 104:187 */       newStructure.add(d.getFieldAsAttribute());
/* 105:    */     }
/* 106:189 */     this.m_fieldInstancesStructure = new Instances("FieldStructure", newStructure, 0);
/* 107:191 */     if (this.m_transformationDictionary != null) {
/* 108:197 */       this.m_transformationDictionary.setFieldDefsForDerivedFields(this.m_fieldInstancesStructure);
/* 109:    */     }
/* 110:201 */     for (DerivedFieldMetaInfo d : this.m_derivedMeta) {
/* 111:202 */       d.setFieldDefs(this.m_fieldInstancesStructure);
/* 112:    */     }
/* 113:205 */     if (classIndex != -1)
/* 114:    */     {
/* 115:206 */       this.m_fieldInstancesStructure.setClassIndex(classIndex);
/* 116:207 */       this.m_miningSchemaInstancesStructure.setClassIndex(classIndex);
/* 117:    */     }
/* 118:211 */     NodeList targetsList = model.getElementsByTagName("Targets");
/* 119:212 */     if (targetsList.getLength() > 0)
/* 120:    */     {
/* 121:213 */       if (targetsList.getLength() > 1) {
/* 122:214 */         throw new Exception("[MiningSchema] Can only handle a single Target");
/* 123:    */       }
/* 124:216 */       Node te = targetsList.item(0);
/* 125:217 */       if (te.getNodeType() == 1)
/* 126:    */       {
/* 127:218 */         this.m_targetMetaInfo = new TargetMetaInfo((Element)te);
/* 128:222 */         if ((this.m_fieldInstancesStructure.classIndex() >= 0) && (this.m_fieldInstancesStructure.classAttribute().isString()))
/* 129:    */         {
/* 130:224 */           ArrayList<String> targetVals = this.m_targetMetaInfo.getValues();
/* 131:225 */           if (targetVals.size() > 0)
/* 132:    */           {
/* 133:226 */             Attribute classAtt = this.m_fieldInstancesStructure.classAttribute();
/* 134:227 */             for (int i = 0; i < targetVals.size(); i++) {
/* 135:228 */               classAtt.addStringValue((String)targetVals.get(i));
/* 136:    */             }
/* 137:    */           }
/* 138:    */         }
/* 139:    */       }
/* 140:    */     }
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void applyMissingValuesTreatment(double[] values)
/* 144:    */     throws Exception
/* 145:    */   {
/* 146:246 */     for (int i = 0; i < this.m_miningMeta.size(); i++)
/* 147:    */     {
/* 148:247 */       MiningFieldMetaInfo mfi = (MiningFieldMetaInfo)this.m_miningMeta.get(i);
/* 149:248 */       values[i] = mfi.applyMissingValueTreatment(values[i]);
/* 150:    */     }
/* 151:    */   }
/* 152:    */   
/* 153:    */   public void applyOutlierTreatment(double[] values)
/* 154:    */     throws Exception
/* 155:    */   {
/* 156:261 */     for (int i = 0; i < this.m_miningMeta.size(); i++)
/* 157:    */     {
/* 158:262 */       MiningFieldMetaInfo mfi = (MiningFieldMetaInfo)this.m_miningMeta.get(i);
/* 159:263 */       values[i] = mfi.applyOutlierTreatment(values[i]);
/* 160:    */     }
/* 161:    */   }
/* 162:    */   
/* 163:    */   public void applyMissingAndOutlierTreatments(double[] values)
/* 164:    */     throws Exception
/* 165:    */   {
/* 166:275 */     for (int i = 0; i < this.m_miningMeta.size(); i++)
/* 167:    */     {
/* 168:276 */       MiningFieldMetaInfo mfi = (MiningFieldMetaInfo)this.m_miningMeta.get(i);
/* 169:277 */       values[i] = mfi.applyMissingValueTreatment(values[i]);
/* 170:278 */       values[i] = mfi.applyOutlierTreatment(values[i]);
/* 171:    */     }
/* 172:    */   }
/* 173:    */   
/* 174:    */   public Instances getFieldsAsInstances()
/* 175:    */   {
/* 176:291 */     return this.m_fieldInstancesStructure;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public Instances getMiningSchemaAsInstances()
/* 180:    */   {
/* 181:300 */     return this.m_miningSchemaInstancesStructure;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public TransformationDictionary getTransformationDictionary()
/* 185:    */   {
/* 186:310 */     return this.m_transformationDictionary;
/* 187:    */   }
/* 188:    */   
/* 189:    */   public boolean hasTargetMetaData()
/* 190:    */   {
/* 191:319 */     return this.m_targetMetaInfo != null;
/* 192:    */   }
/* 193:    */   
/* 194:    */   public TargetMetaInfo getTargetMetaData()
/* 195:    */   {
/* 196:328 */     return this.m_targetMetaInfo;
/* 197:    */   }
/* 198:    */   
/* 199:    */   public void convertStringAttsToNominal()
/* 200:    */   {
/* 201:339 */     Instances miningSchemaI = getFieldsAsInstances();
/* 202:340 */     if (miningSchemaI.checkForStringAttributes())
/* 203:    */     {
/* 204:341 */       ArrayList<Attribute> attInfo = new ArrayList();
/* 205:342 */       for (int i = 0; i < miningSchemaI.numAttributes(); i++)
/* 206:    */       {
/* 207:343 */         Attribute tempA = miningSchemaI.attribute(i);
/* 208:344 */         if (tempA.isString())
/* 209:    */         {
/* 210:345 */           ArrayList<String> valueVector = new ArrayList();
/* 211:346 */           for (int j = 0; j < tempA.numValues(); j++) {
/* 212:347 */             valueVector.add(tempA.value(j));
/* 213:    */           }
/* 214:349 */           Attribute newAtt = new Attribute(tempA.name(), valueVector);
/* 215:350 */           attInfo.add(newAtt);
/* 216:    */         }
/* 217:    */         else
/* 218:    */         {
/* 219:352 */           attInfo.add(tempA);
/* 220:    */         }
/* 221:    */       }
/* 222:355 */       Instances newI = new Instances("miningSchema", attInfo, 0);
/* 223:356 */       if (this.m_fieldInstancesStructure.classIndex() >= 0) {
/* 224:357 */         newI.setClassIndex(this.m_fieldInstancesStructure.classIndex());
/* 225:    */       }
/* 226:359 */       this.m_fieldInstancesStructure = newI;
/* 227:    */     }
/* 228:    */   }
/* 229:    */   
/* 230:    */   public void convertNumericAttToNominal(int index, ArrayList<String> newVals)
/* 231:    */   {
/* 232:376 */     Instances miningSchemaI = getFieldsAsInstances();
/* 233:377 */     if (miningSchemaI.attribute(index).isNominal()) {
/* 234:378 */       throw new IllegalArgumentException("[MiningSchema] convertNumericAttToNominal: attribute is already nominal!");
/* 235:    */     }
/* 236:382 */     ArrayList<String> newValues = new ArrayList();
/* 237:383 */     for (int i = 0; i < newVals.size(); i++) {
/* 238:384 */       newValues.add(newVals.get(i));
/* 239:    */     }
/* 240:387 */     ArrayList<Attribute> attInfo = new ArrayList();
/* 241:388 */     for (int i = 0; i < miningSchemaI.numAttributes(); i++)
/* 242:    */     {
/* 243:389 */       Attribute tempA = miningSchemaI.attribute(i);
/* 244:390 */       if (i == index)
/* 245:    */       {
/* 246:391 */         Attribute newAtt = new Attribute(tempA.name(), newValues);
/* 247:392 */         attInfo.add(newAtt);
/* 248:    */       }
/* 249:    */       else
/* 250:    */       {
/* 251:394 */         attInfo.add(tempA);
/* 252:    */       }
/* 253:    */     }
/* 254:398 */     Instances newI = new Instances("miningSchema", attInfo, 0);
/* 255:399 */     if (this.m_fieldInstancesStructure.classIndex() >= 0) {
/* 256:400 */       newI.setClassIndex(this.m_fieldInstancesStructure.classIndex());
/* 257:    */     }
/* 258:402 */     this.m_fieldInstancesStructure = newI;
/* 259:    */   }
/* 260:    */   
/* 261:    */   public ArrayList<DerivedFieldMetaInfo> getDerivedFields()
/* 262:    */   {
/* 263:406 */     return this.m_derivedMeta;
/* 264:    */   }
/* 265:    */   
/* 266:    */   public ArrayList<MiningFieldMetaInfo> getMiningFields()
/* 267:    */   {
/* 268:410 */     return this.m_miningMeta;
/* 269:    */   }
/* 270:    */   
/* 271:    */   public String toString()
/* 272:    */   {
/* 273:419 */     StringBuffer temp = new StringBuffer();
/* 274:421 */     if (this.m_transformationDictionary != null) {
/* 275:422 */       temp.append(this.m_transformationDictionary);
/* 276:    */     }
/* 277:425 */     temp.append("Mining schema:\n\n");
/* 278:426 */     for (MiningFieldMetaInfo m : this.m_miningMeta) {
/* 279:427 */       temp.append(m + "\n");
/* 280:    */     }
/* 281:430 */     if (this.m_derivedMeta.size() > 0)
/* 282:    */     {
/* 283:431 */       temp.append("\nDerived fields:\n\n");
/* 284:432 */       for (DerivedFieldMetaInfo d : this.m_derivedMeta) {
/* 285:433 */         temp.append(d + "\n");
/* 286:    */       }
/* 287:    */     }
/* 288:436 */     temp.append("\n");
/* 289:437 */     return temp.toString();
/* 290:    */   }
/* 291:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.MiningSchema
 * JD-Core Version:    0.7.0.1
 */