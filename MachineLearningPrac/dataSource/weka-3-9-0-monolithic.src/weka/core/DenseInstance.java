/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ 
/*   7:    */ public class DenseInstance
/*   8:    */   extends AbstractInstance
/*   9:    */ {
/*  10:    */   static final long serialVersionUID = 1482635194499365122L;
/*  11:    */   
/*  12:    */   public DenseInstance(Instance instance)
/*  13:    */   {
/*  14: 88 */     if ((instance instanceof DenseInstance)) {
/*  15: 89 */       this.m_AttValues = ((DenseInstance)instance).m_AttValues;
/*  16:    */     } else {
/*  17: 91 */       this.m_AttValues = instance.toDoubleArray();
/*  18:    */     }
/*  19: 93 */     this.m_Weight = instance.weight();
/*  20: 94 */     this.m_Dataset = null;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public DenseInstance(double weight, double[] attValues)
/*  24:    */   {
/*  25:108 */     this.m_AttValues = attValues;
/*  26:109 */     this.m_Weight = weight;
/*  27:110 */     this.m_Dataset = null;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public DenseInstance(int numAttributes)
/*  31:    */   {
/*  32:124 */     this.m_AttValues = new double[numAttributes];
/*  33:125 */     for (int i = 0; i < this.m_AttValues.length; i++) {
/*  34:126 */       this.m_AttValues[i] = Utils.missingValue();
/*  35:    */     }
/*  36:128 */     this.m_Weight = 1.0D;
/*  37:129 */     this.m_Dataset = null;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public Object copy()
/*  41:    */   {
/*  42:145 */     DenseInstance result = new DenseInstance(this);
/*  43:146 */     result.m_Dataset = this.m_Dataset;
/*  44:147 */     return result;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public Instance copy(double[] values)
/*  48:    */   {
/*  49:159 */     DenseInstance result = new DenseInstance(this.m_Weight, values);
/*  50:160 */     result.m_Dataset = this.m_Dataset;
/*  51:161 */     return result;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public int index(int position)
/*  55:    */   {
/*  56:174 */     return position;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public Instance mergeInstance(Instance inst)
/*  60:    */   {
/*  61:188 */     int m = 0;
/*  62:189 */     double[] newVals = new double[numAttributes() + inst.numAttributes()];
/*  63:190 */     for (int j = 0; j < numAttributes(); m++)
/*  64:    */     {
/*  65:191 */       newVals[m] = value(j);j++;
/*  66:    */     }
/*  67:193 */     for (int j = 0; j < inst.numAttributes(); m++)
/*  68:    */     {
/*  69:194 */       newVals[m] = inst.value(j);j++;
/*  70:    */     }
/*  71:196 */     return new DenseInstance(1.0D, newVals);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public int numAttributes()
/*  75:    */   {
/*  76:208 */     return this.m_AttValues.length;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public int numValues()
/*  80:    */   {
/*  81:220 */     return this.m_AttValues.length;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void replaceMissingValues(double[] array)
/*  85:    */   {
/*  86:234 */     if ((array == null) || (array.length != this.m_AttValues.length)) {
/*  87:235 */       throw new IllegalArgumentException("Unequal number of attributes!");
/*  88:    */     }
/*  89:237 */     freshAttributeVector();
/*  90:238 */     for (int i = 0; i < this.m_AttValues.length; i++) {
/*  91:239 */       if (isMissing(i)) {
/*  92:240 */         this.m_AttValues[i] = array[i];
/*  93:    */       }
/*  94:    */     }
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void setValue(int attIndex, double value)
/*  98:    */   {
/*  99:258 */     freshAttributeVector();
/* 100:259 */     this.m_AttValues[attIndex] = value;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void setValueSparse(int indexOfIndex, double value)
/* 104:    */   {
/* 105:275 */     freshAttributeVector();
/* 106:276 */     this.m_AttValues[indexOfIndex] = value;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public double[] toDoubleArray()
/* 110:    */   {
/* 111:287 */     double[] newValues = new double[this.m_AttValues.length];
/* 112:288 */     System.arraycopy(this.m_AttValues, 0, newValues, 0, this.m_AttValues.length);
/* 113:289 */     return newValues;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public String toStringNoWeight()
/* 117:    */   {
/* 118:305 */     return toStringNoWeight(AbstractInstance.s_numericAfterDecimalPoint);
/* 119:    */   }
/* 120:    */   
/* 121:    */   public String toStringNoWeight(int afterDecimalPoint)
/* 122:    */   {
/* 123:324 */     StringBuffer text = new StringBuffer();
/* 124:326 */     for (int i = 0; i < this.m_AttValues.length; i++)
/* 125:    */     {
/* 126:327 */       if (i > 0) {
/* 127:328 */         text.append(",");
/* 128:    */       }
/* 129:330 */       text.append(toString(i, afterDecimalPoint));
/* 130:    */     }
/* 131:333 */     return text.toString();
/* 132:    */   }
/* 133:    */   
/* 134:    */   public double value(int attIndex)
/* 135:    */   {
/* 136:347 */     return this.m_AttValues[attIndex];
/* 137:    */   }
/* 138:    */   
/* 139:    */   protected void forceDeleteAttributeAt(int position)
/* 140:    */   {
/* 141:358 */     double[] newValues = new double[this.m_AttValues.length - 1];
/* 142:    */     
/* 143:360 */     System.arraycopy(this.m_AttValues, 0, newValues, 0, position);
/* 144:361 */     if (position < this.m_AttValues.length - 1) {
/* 145:362 */       System.arraycopy(this.m_AttValues, position + 1, newValues, position, this.m_AttValues.length - (position + 1));
/* 146:    */     }
/* 147:365 */     this.m_AttValues = newValues;
/* 148:    */   }
/* 149:    */   
/* 150:    */   protected void forceInsertAttributeAt(int position)
/* 151:    */   {
/* 152:377 */     double[] newValues = new double[this.m_AttValues.length + 1];
/* 153:    */     
/* 154:379 */     System.arraycopy(this.m_AttValues, 0, newValues, 0, position);
/* 155:380 */     newValues[position] = Utils.missingValue();
/* 156:381 */     System.arraycopy(this.m_AttValues, position, newValues, position + 1, this.m_AttValues.length - position);
/* 157:    */     
/* 158:383 */     this.m_AttValues = newValues;
/* 159:    */   }
/* 160:    */   
/* 161:    */   private void freshAttributeVector()
/* 162:    */   {
/* 163:392 */     this.m_AttValues = toDoubleArray();
/* 164:    */   }
/* 165:    */   
/* 166:    */   public static void main(String[] options)
/* 167:    */   {
/* 168:    */     try
/* 169:    */     {
/* 170:406 */       Attribute length = new Attribute("length");
/* 171:407 */       Attribute weight = new Attribute("weight");
/* 172:    */       
/* 173:    */ 
/* 174:410 */       ArrayList<String> my_nominal_values = new ArrayList(3);
/* 175:411 */       my_nominal_values.add("first");
/* 176:412 */       my_nominal_values.add("second");
/* 177:413 */       my_nominal_values.add("third");
/* 178:    */       
/* 179:    */ 
/* 180:416 */       Attribute position = new Attribute("position", my_nominal_values);
/* 181:    */       
/* 182:    */ 
/* 183:419 */       ArrayList<Attribute> attributes = new ArrayList(3);
/* 184:420 */       attributes.add(length);
/* 185:421 */       attributes.add(weight);
/* 186:422 */       attributes.add(position);
/* 187:    */       
/* 188:    */ 
/* 189:425 */       Instances race = new Instances("race", attributes, 0);
/* 190:    */       
/* 191:    */ 
/* 192:428 */       race.setClassIndex(position.index());
/* 193:    */       
/* 194:    */ 
/* 195:431 */       Instance inst = new DenseInstance(3);
/* 196:    */       
/* 197:    */ 
/* 198:    */ 
/* 199:435 */       inst.setValue(length, 5.3D);
/* 200:436 */       inst.setValue(weight, 300.0D);
/* 201:437 */       inst.setValue(position, "first");
/* 202:    */       
/* 203:    */ 
/* 204:440 */       inst.setDataset(race);
/* 205:    */       
/* 206:    */ 
/* 207:443 */       System.out.println("The instance: " + inst);
/* 208:    */       
/* 209:    */ 
/* 210:446 */       System.out.println("First attribute: " + inst.attribute(0));
/* 211:    */       
/* 212:    */ 
/* 213:449 */       System.out.println("Class attribute: " + inst.classAttribute());
/* 214:    */       
/* 215:    */ 
/* 216:452 */       System.out.println("Class index: " + inst.classIndex());
/* 217:    */       
/* 218:    */ 
/* 219:455 */       System.out.println("Class is missing: " + inst.classIsMissing());
/* 220:    */       
/* 221:    */ 
/* 222:458 */       System.out.println("Class value (internal format): " + inst.classValue());
/* 223:    */       
/* 224:    */ 
/* 225:461 */       Instance copy = (Instance)inst.copy();
/* 226:462 */       System.out.println("Shallow copy: " + copy);
/* 227:    */       
/* 228:    */ 
/* 229:465 */       copy.setDataset(inst.dataset());
/* 230:466 */       System.out.println("Shallow copy with dataset set: " + copy);
/* 231:    */       
/* 232:    */ 
/* 233:469 */       copy.setDataset(null);
/* 234:470 */       copy.deleteAttributeAt(0);
/* 235:471 */       copy.insertAttributeAt(0);
/* 236:472 */       copy.setDataset(inst.dataset());
/* 237:473 */       System.out.println("Copy with first attribute deleted and inserted: " + copy);
/* 238:    */       
/* 239:    */ 
/* 240:    */ 
/* 241:477 */       System.out.println("Enumerating attributes (leaving out class):");
/* 242:478 */       Enumeration<Attribute> enu = inst.enumerateAttributes();
/* 243:479 */       while (enu.hasMoreElements())
/* 244:    */       {
/* 245:480 */         Attribute att = (Attribute)enu.nextElement();
/* 246:481 */         System.out.println(att);
/* 247:    */       }
/* 248:485 */       System.out.println("Header of original and copy equivalent: " + inst.equalHeaders(copy));
/* 249:    */       
/* 250:    */ 
/* 251:    */ 
/* 252:489 */       System.out.println("Length of copy missing: " + copy.isMissing(length));
/* 253:490 */       System.out.println("Weight of copy missing: " + copy.isMissing(weight.index()));
/* 254:    */       
/* 255:492 */       System.out.println("Length of copy missing: " + Utils.isMissingValue(copy.value(length)));
/* 256:    */       
/* 257:    */ 
/* 258:    */ 
/* 259:496 */       System.out.println("Number of attributes: " + copy.numAttributes());
/* 260:497 */       System.out.println("Number of classes: " + copy.numClasses());
/* 261:    */       
/* 262:    */ 
/* 263:500 */       double[] meansAndModes = { 2.0D, 3.0D, 0.0D };
/* 264:501 */       copy.replaceMissingValues(meansAndModes);
/* 265:502 */       System.out.println("Copy with missing value replaced: " + copy);
/* 266:    */       
/* 267:    */ 
/* 268:505 */       copy.setClassMissing();
/* 269:506 */       System.out.println("Copy with missing class: " + copy);
/* 270:507 */       copy.setClassValue(0.0D);
/* 271:508 */       System.out.println("Copy with class value set to first value: " + copy);
/* 272:509 */       copy.setClassValue("third");
/* 273:510 */       System.out.println("Copy with class value set to \"third\": " + copy);
/* 274:511 */       copy.setMissing(1);
/* 275:512 */       System.out.println("Copy with second attribute set to be missing: " + copy);
/* 276:    */       
/* 277:514 */       copy.setMissing(length);
/* 278:515 */       System.out.println("Copy with length set to be missing: " + copy);
/* 279:516 */       copy.setValue(0, 0.0D);
/* 280:517 */       System.out.println("Copy with first attribute set to 0: " + copy);
/* 281:518 */       copy.setValue(weight, 1.0D);
/* 282:519 */       System.out.println("Copy with weight attribute set to 1: " + copy);
/* 283:520 */       copy.setValue(position, "second");
/* 284:521 */       System.out.println("Copy with position set to \"second\": " + copy);
/* 285:522 */       copy.setValue(2, "first");
/* 286:523 */       System.out.println("Copy with last attribute set to \"first\": " + copy);
/* 287:524 */       System.out.println("Current weight of instance copy: " + copy.weight());
/* 288:525 */       copy.setWeight(2.0D);
/* 289:526 */       System.out.println("Current weight of instance copy (set to 2): " + copy.weight());
/* 290:    */       
/* 291:528 */       System.out.println("Last value of copy: " + copy.toString(2));
/* 292:529 */       System.out.println("Value of position for copy: " + copy.toString(position));
/* 293:    */       
/* 294:531 */       System.out.println("Last value of copy (internal format): " + copy.value(2));
/* 295:    */       
/* 296:533 */       System.out.println("Value of position for copy (internal format): " + copy.value(position));
/* 297:    */     }
/* 298:    */     catch (Exception e)
/* 299:    */     {
/* 300:536 */       e.printStackTrace();
/* 301:    */     }
/* 302:    */   }
/* 303:    */   
/* 304:    */   public String getRevision()
/* 305:    */   {
/* 306:547 */     return RevisionUtils.extract("$Revision: 12472 $");
/* 307:    */   }
/* 308:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.DenseInstance
 * JD-Core Version:    0.7.0.1
 */