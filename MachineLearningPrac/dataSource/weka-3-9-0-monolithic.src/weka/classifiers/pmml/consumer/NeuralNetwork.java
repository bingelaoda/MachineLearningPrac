/*   1:    */ package weka.classifiers.pmml.consumer;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.HashMap;
/*   7:    */ import org.w3c.dom.Element;
/*   8:    */ import org.w3c.dom.Node;
/*   9:    */ import org.w3c.dom.NodeList;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Utils;
/*  15:    */ import weka.core.pmml.DerivedFieldMetaInfo;
/*  16:    */ import weka.core.pmml.FieldMetaInfo.Optype;
/*  17:    */ import weka.core.pmml.MappingInfo;
/*  18:    */ import weka.core.pmml.MiningSchema;
/*  19:    */ import weka.core.pmml.NormContinuous;
/*  20:    */ import weka.core.pmml.TargetMetaInfo;
/*  21:    */ import weka.gui.Logger;
/*  22:    */ 
/*  23:    */ public class NeuralNetwork
/*  24:    */   extends PMMLClassifier
/*  25:    */ {
/*  26:    */   private static final long serialVersionUID = -4545904813133921249L;
/*  27:    */   
/*  28:    */   static class NeuralInput
/*  29:    */     implements Serializable
/*  30:    */   {
/*  31:    */     private static final long serialVersionUID = -1902233762824835563L;
/*  32:    */     private DerivedFieldMetaInfo m_field;
/*  33: 72 */     private String m_ID = null;
/*  34:    */     
/*  35:    */     private String getID()
/*  36:    */     {
/*  37: 75 */       return this.m_ID;
/*  38:    */     }
/*  39:    */     
/*  40:    */     protected NeuralInput(Element input, MiningSchema miningSchema)
/*  41:    */       throws Exception
/*  42:    */     {
/*  43: 79 */       this.m_ID = input.getAttribute("id");
/*  44:    */       
/*  45: 81 */       NodeList fL = input.getElementsByTagName("DerivedField");
/*  46: 82 */       if (fL.getLength() != 1) {
/*  47: 83 */         throw new Exception("[NeuralInput] expecting just one derived field!");
/*  48:    */       }
/*  49: 86 */       Element dF = (Element)fL.item(0);
/*  50: 87 */       Instances allFields = miningSchema.getFieldsAsInstances();
/*  51: 88 */       ArrayList<Attribute> fieldDefs = new ArrayList();
/*  52: 89 */       for (int i = 0; i < allFields.numAttributes(); i++) {
/*  53: 90 */         fieldDefs.add(allFields.attribute(i));
/*  54:    */       }
/*  55: 92 */       this.m_field = new DerivedFieldMetaInfo(dF, fieldDefs, miningSchema.getTransformationDictionary());
/*  56:    */     }
/*  57:    */     
/*  58:    */     protected double getValue(double[] incoming)
/*  59:    */       throws Exception
/*  60:    */     {
/*  61: 96 */       return this.m_field.getDerivedValue(incoming);
/*  62:    */     }
/*  63:    */     
/*  64:    */     public String toString()
/*  65:    */     {
/*  66:100 */       StringBuffer temp = new StringBuffer();
/*  67:    */       
/*  68:102 */       temp.append("Nueral input (" + getID() + ")\n");
/*  69:103 */       temp.append(this.m_field);
/*  70:    */       
/*  71:105 */       return temp.toString();
/*  72:    */     }
/*  73:    */   }
/*  74:    */   
/*  75:    */   class NeuralLayer
/*  76:    */     implements Serializable
/*  77:    */   {
/*  78:    */     private static final long serialVersionUID = -8386042001675763922L;
/*  79:120 */     private int m_numNeurons = 0;
/*  80:123 */     private NeuralNetwork.ActivationFunction m_layerActivationFunction = null;
/*  81:126 */     private double m_layerThreshold = (0.0D / 0.0D);
/*  82:129 */     private double m_layerWidth = (0.0D / 0.0D);
/*  83:132 */     private double m_layerAltitude = (0.0D / 0.0D);
/*  84:135 */     private NeuralNetwork.Normalization m_layerNormalization = null;
/*  85:138 */     private NeuralNetwork.Neuron[] m_layerNeurons = null;
/*  86:141 */     private HashMap<String, Double> m_layerOutput = new HashMap();
/*  87:    */     
/*  88:    */     protected NeuralLayer(Element layerE)
/*  89:    */     {
/*  90:145 */       String activationFunction = layerE.getAttribute("activationFunction");
/*  91:146 */       if ((activationFunction != null) && (activationFunction.length() > 0)) {
/*  92:147 */         for (NeuralNetwork.ActivationFunction a : NeuralNetwork.ActivationFunction.values()) {
/*  93:148 */           if (a.toString().equals(activationFunction))
/*  94:    */           {
/*  95:149 */             this.m_layerActivationFunction = a;
/*  96:150 */             break;
/*  97:    */           }
/*  98:    */         }
/*  99:    */       } else {
/* 100:155 */         this.m_layerActivationFunction = NeuralNetwork.this.m_activationFunction;
/* 101:    */       }
/* 102:158 */       String threshold = layerE.getAttribute("threshold");
/* 103:159 */       if ((threshold != null) && (threshold.length() > 0)) {
/* 104:160 */         this.m_layerThreshold = Double.parseDouble(threshold);
/* 105:    */       } else {
/* 106:163 */         this.m_layerThreshold = NeuralNetwork.this.m_threshold;
/* 107:    */       }
/* 108:166 */       String width = layerE.getAttribute("width");
/* 109:167 */       if ((width != null) && (width.length() > 0)) {
/* 110:168 */         this.m_layerWidth = Double.parseDouble(width);
/* 111:    */       } else {
/* 112:171 */         this.m_layerWidth = NeuralNetwork.this.m_width;
/* 113:    */       }
/* 114:174 */       String altitude = layerE.getAttribute("altitude");
/* 115:175 */       if ((altitude != null) && (altitude.length() > 0)) {
/* 116:176 */         this.m_layerAltitude = Double.parseDouble(altitude);
/* 117:    */       } else {
/* 118:179 */         this.m_layerAltitude = NeuralNetwork.this.m_altitude;
/* 119:    */       }
/* 120:182 */       String normMethod = layerE.getAttribute("normalizationMethod");
/* 121:183 */       if ((normMethod != null) && (normMethod.length() > 0)) {
/* 122:184 */         for (NeuralNetwork.Normalization n : NeuralNetwork.Normalization.values()) {
/* 123:185 */           if (n.toString().equals(normMethod))
/* 124:    */           {
/* 125:186 */             this.m_layerNormalization = n;
/* 126:187 */             break;
/* 127:    */           }
/* 128:    */         }
/* 129:    */       } else {
/* 130:192 */         this.m_layerNormalization = NeuralNetwork.this.m_normalizationMethod;
/* 131:    */       }
/* 132:195 */       NodeList neuronL = layerE.getElementsByTagName("Neuron");
/* 133:196 */       this.m_numNeurons = neuronL.getLength();
/* 134:197 */       this.m_layerNeurons = new NeuralNetwork.Neuron[this.m_numNeurons];
/* 135:198 */       for (int i = 0; i < neuronL.getLength(); i++)
/* 136:    */       {
/* 137:199 */         Node neuronN = neuronL.item(i);
/* 138:200 */         if (neuronN.getNodeType() == 1) {
/* 139:201 */           this.m_layerNeurons[i] = new NeuralNetwork.Neuron((Element)neuronN, this);
/* 140:    */         }
/* 141:    */       }
/* 142:    */     }
/* 143:    */     
/* 144:    */     protected NeuralNetwork.ActivationFunction getActivationFunction()
/* 145:    */     {
/* 146:207 */       return this.m_layerActivationFunction;
/* 147:    */     }
/* 148:    */     
/* 149:    */     protected double getThreshold()
/* 150:    */     {
/* 151:211 */       return this.m_layerThreshold;
/* 152:    */     }
/* 153:    */     
/* 154:    */     protected double getWidth()
/* 155:    */     {
/* 156:215 */       return this.m_layerWidth;
/* 157:    */     }
/* 158:    */     
/* 159:    */     protected double getAltitude()
/* 160:    */     {
/* 161:219 */       return this.m_layerAltitude;
/* 162:    */     }
/* 163:    */     
/* 164:    */     protected NeuralNetwork.Normalization getNormalization()
/* 165:    */     {
/* 166:223 */       return this.m_layerNormalization;
/* 167:    */     }
/* 168:    */     
/* 169:    */     protected HashMap<String, Double> computeOutput(HashMap<String, Double> incoming)
/* 170:    */       throws Exception
/* 171:    */     {
/* 172:236 */       this.m_layerOutput.clear();
/* 173:    */       
/* 174:238 */       double normSum = 0.0D;
/* 175:239 */       for (int i = 0; i < this.m_layerNeurons.length; i++)
/* 176:    */       {
/* 177:240 */         double neuronOut = this.m_layerNeurons[i].getValue(incoming);
/* 178:241 */         String neuronID = this.m_layerNeurons[i].getID();
/* 179:243 */         if (this.m_layerNormalization == NeuralNetwork.Normalization.SOFTMAX) {
/* 180:244 */           normSum += Math.exp(neuronOut);
/* 181:245 */         } else if (this.m_layerNormalization == NeuralNetwork.Normalization.SIMPLEMAX) {
/* 182:246 */           normSum += neuronOut;
/* 183:    */         }
/* 184:249 */         this.m_layerOutput.put(neuronID, Double.valueOf(neuronOut));
/* 185:    */       }
/* 186:253 */       if (this.m_layerNormalization != NeuralNetwork.Normalization.NONE) {
/* 187:254 */         for (int i = 0; i < this.m_layerNeurons.length; i++)
/* 188:    */         {
/* 189:255 */           double val = ((Double)this.m_layerOutput.get(this.m_layerNeurons[i].getID())).doubleValue();
/* 190:257 */           if (this.m_layerNormalization == NeuralNetwork.Normalization.SOFTMAX) {
/* 191:258 */             val = Math.exp(val) / normSum;
/* 192:    */           } else {
/* 193:260 */             val /= normSum;
/* 194:    */           }
/* 195:262 */           this.m_layerOutput.put(this.m_layerNeurons[i].getID(), Double.valueOf(val));
/* 196:    */         }
/* 197:    */       }
/* 198:265 */       return this.m_layerOutput;
/* 199:    */     }
/* 200:    */     
/* 201:    */     public String toString()
/* 202:    */     {
/* 203:269 */       StringBuffer temp = new StringBuffer();
/* 204:    */       
/* 205:271 */       temp.append("activation: " + getActivationFunction() + "\n");
/* 206:272 */       if (!Double.isNaN(getThreshold())) {
/* 207:273 */         temp.append("threshold: " + getThreshold() + "\n");
/* 208:    */       }
/* 209:275 */       if (!Double.isNaN(getWidth())) {
/* 210:276 */         temp.append("width: " + getWidth() + "\n");
/* 211:    */       }
/* 212:278 */       if (!Double.isNaN(getAltitude())) {
/* 213:279 */         temp.append("altitude: " + getAltitude() + "\n");
/* 214:    */       }
/* 215:281 */       temp.append("normalization: " + this.m_layerNormalization + "\n");
/* 216:282 */       for (int i = 0; i < this.m_numNeurons; i++) {
/* 217:283 */         temp.append(this.m_layerNeurons[i] + "\n");
/* 218:    */       }
/* 219:286 */       return temp.toString();
/* 220:    */     }
/* 221:    */   }
/* 222:    */   
/* 223:    */   static class Neuron
/* 224:    */     implements Serializable
/* 225:    */   {
/* 226:    */     private static final long serialVersionUID = -3817434025682603443L;
/* 227:301 */     private String m_ID = null;
/* 228:    */     private NeuralNetwork.NeuralLayer m_layer;
/* 229:307 */     private double m_bias = 0.0D;
/* 230:310 */     private double m_neuronWidth = (0.0D / 0.0D);
/* 231:313 */     private double m_neuronAltitude = (0.0D / 0.0D);
/* 232:316 */     private String[] m_connectionIDs = null;
/* 233:319 */     private double[] m_weights = null;
/* 234:    */     
/* 235:    */     protected Neuron(Element neuronE, NeuralNetwork.NeuralLayer layer)
/* 236:    */     {
/* 237:322 */       this.m_layer = layer;
/* 238:    */       
/* 239:324 */       this.m_ID = neuronE.getAttribute("id");
/* 240:    */       
/* 241:326 */       String bias = neuronE.getAttribute("bias");
/* 242:327 */       if ((bias != null) && (bias.length() > 0)) {
/* 243:328 */         this.m_bias = Double.parseDouble(bias);
/* 244:    */       }
/* 245:331 */       String width = neuronE.getAttribute("width");
/* 246:332 */       if ((width != null) && (width.length() > 0)) {
/* 247:333 */         this.m_neuronWidth = Double.parseDouble(width);
/* 248:    */       }
/* 249:336 */       String altitude = neuronE.getAttribute("altitude");
/* 250:337 */       if ((altitude != null) && (altitude.length() > 0)) {
/* 251:338 */         this.m_neuronAltitude = Double.parseDouble(altitude);
/* 252:    */       }
/* 253:342 */       NodeList conL = neuronE.getElementsByTagName("Con");
/* 254:343 */       this.m_connectionIDs = new String[conL.getLength()];
/* 255:344 */       this.m_weights = new double[conL.getLength()];
/* 256:345 */       for (int i = 0; i < conL.getLength(); i++)
/* 257:    */       {
/* 258:346 */         Node conN = conL.item(i);
/* 259:347 */         if (conN.getNodeType() == 1)
/* 260:    */         {
/* 261:348 */           Element conE = (Element)conN;
/* 262:349 */           this.m_connectionIDs[i] = conE.getAttribute("from");
/* 263:350 */           String weight = conE.getAttribute("weight");
/* 264:351 */           this.m_weights[i] = Double.parseDouble(weight);
/* 265:    */         }
/* 266:    */       }
/* 267:    */     }
/* 268:    */     
/* 269:    */     protected String getID()
/* 270:    */     {
/* 271:357 */       return this.m_ID;
/* 272:    */     }
/* 273:    */     
/* 274:    */     protected double getValue(HashMap<String, Double> incoming)
/* 275:    */       throws Exception
/* 276:    */     {
/* 277:374 */       double z = 0.0D;
/* 278:375 */       double result = (0.0D / 0.0D);
/* 279:    */       
/* 280:377 */       double width = Double.isNaN(this.m_neuronWidth) ? this.m_layer.getWidth() : this.m_neuronWidth;
/* 281:    */       
/* 282:    */ 
/* 283:    */ 
/* 284:381 */       z = this.m_bias;
/* 285:382 */       for (int i = 0; i < this.m_connectionIDs.length; i++)
/* 286:    */       {
/* 287:383 */         Double inVal = (Double)incoming.get(this.m_connectionIDs[i]);
/* 288:384 */         if (inVal == null) {
/* 289:385 */           throw new Exception("[Neuron] unable to find connection " + this.m_connectionIDs[i] + " in input Map!");
/* 290:    */         }
/* 291:389 */         if (this.m_layer.getActivationFunction() != NeuralNetwork.ActivationFunction.RADIALBASIS)
/* 292:    */         {
/* 293:391 */           double inV = inVal.doubleValue() * this.m_weights[i];
/* 294:392 */           z += inV;
/* 295:    */         }
/* 296:    */         else
/* 297:    */         {
/* 298:395 */           double inV = Math.pow(inVal.doubleValue() - this.m_weights[i], 2.0D);
/* 299:396 */           z += inV;
/* 300:    */         }
/* 301:    */       }
/* 302:401 */       if (this.m_layer.getActivationFunction() == NeuralNetwork.ActivationFunction.RADIALBASIS) {
/* 303:402 */         z /= 2.0D * (width * width);
/* 304:    */       }
/* 305:405 */       double threshold = this.m_layer.getThreshold();
/* 306:406 */       double altitude = Double.isNaN(this.m_neuronAltitude) ? this.m_layer.getAltitude() : this.m_neuronAltitude;
/* 307:    */       
/* 308:    */ 
/* 309:    */ 
/* 310:410 */       double fanIn = this.m_connectionIDs.length;
/* 311:411 */       result = this.m_layer.getActivationFunction().eval(z, threshold, altitude, fanIn);
/* 312:    */       
/* 313:413 */       return result;
/* 314:    */     }
/* 315:    */     
/* 316:    */     public String toString()
/* 317:    */     {
/* 318:417 */       StringBuffer temp = new StringBuffer();
/* 319:418 */       temp.append("Nueron (" + this.m_ID + ") [bias:" + this.m_bias);
/* 320:419 */       if (!Double.isNaN(this.m_neuronWidth)) {
/* 321:420 */         temp.append(" width:" + this.m_neuronWidth);
/* 322:    */       }
/* 323:422 */       if (!Double.isNaN(this.m_neuronAltitude)) {
/* 324:423 */         temp.append(" altitude:" + this.m_neuronAltitude);
/* 325:    */       }
/* 326:425 */       temp.append("]\n");
/* 327:426 */       temp.append("  con. (ID:weight): ");
/* 328:427 */       for (int i = 0; i < this.m_connectionIDs.length; i++)
/* 329:    */       {
/* 330:428 */         temp.append(this.m_connectionIDs[i] + ":" + Utils.doubleToString(this.m_weights[i], 2));
/* 331:429 */         if (((i + 1) % 10 == 0) || (i == this.m_connectionIDs.length - 1)) {
/* 332:430 */           temp.append("\n                    ");
/* 333:    */         } else {
/* 334:432 */           temp.append(", ");
/* 335:    */         }
/* 336:    */       }
/* 337:435 */       return temp.toString();
/* 338:    */     }
/* 339:    */   }
/* 340:    */   
/* 341:    */   static class NeuralOutputs
/* 342:    */     implements Serializable
/* 343:    */   {
/* 344:    */     private static final long serialVersionUID = -233611113950482952L;
/* 345:447 */     private String[] m_outputNeurons = null;
/* 346:453 */     private int[] m_categoricalIndexes = null;
/* 347:456 */     private Attribute m_classAttribute = null;
/* 348:459 */     private NormContinuous m_regressionMapping = null;
/* 349:    */     
/* 350:    */     protected NeuralOutputs(Element outputs, MiningSchema miningSchema)
/* 351:    */       throws Exception
/* 352:    */     {
/* 353:462 */       this.m_classAttribute = miningSchema.getMiningSchemaAsInstances().classAttribute();
/* 354:    */       
/* 355:464 */       int vals = this.m_classAttribute.isNumeric() ? 1 : this.m_classAttribute.numValues();
/* 356:    */       
/* 357:    */ 
/* 358:    */ 
/* 359:468 */       this.m_outputNeurons = new String[vals];
/* 360:469 */       this.m_categoricalIndexes = new int[vals];
/* 361:    */       
/* 362:471 */       NodeList outputL = outputs.getElementsByTagName("NeuralOutput");
/* 363:472 */       if (outputL.getLength() != this.m_outputNeurons.length) {
/* 364:473 */         throw new Exception("[NeuralOutputs] the number of neural outputs does not match the number expected!");
/* 365:    */       }
/* 366:477 */       for (int i = 0; i < outputL.getLength(); i++)
/* 367:    */       {
/* 368:478 */         Node outputN = outputL.item(i);
/* 369:479 */         if (outputN.getNodeType() == 1)
/* 370:    */         {
/* 371:480 */           Element outputE = (Element)outputN;
/* 372:    */           
/* 373:482 */           this.m_outputNeurons[i] = outputE.getAttribute("outputNeuron");
/* 374:484 */           if (this.m_classAttribute.isNumeric())
/* 375:    */           {
/* 376:486 */             NodeList contL = outputE.getElementsByTagName("NormContinuous");
/* 377:487 */             if (contL.getLength() != 1) {
/* 378:488 */               throw new Exception("[NeuralOutputs] Should be exactly one norm continuous element for numeric class!");
/* 379:    */             }
/* 380:491 */             Node normContNode = contL.item(0);
/* 381:492 */             String attName = ((Element)normContNode).getAttribute("field");
/* 382:493 */             Attribute dummyTargetDef = new Attribute(attName);
/* 383:494 */             ArrayList<Attribute> dummyFieldDefs = new ArrayList();
/* 384:495 */             dummyFieldDefs.add(dummyTargetDef);
/* 385:    */             
/* 386:497 */             this.m_regressionMapping = new NormContinuous((Element)normContNode, FieldMetaInfo.Optype.CONTINUOUS, dummyFieldDefs);
/* 387:    */             
/* 388:499 */             break;
/* 389:    */           }
/* 390:503 */           NodeList discL = outputE.getElementsByTagName("NormDiscrete");
/* 391:504 */           if (discL.getLength() != 1) {
/* 392:505 */             throw new Exception("[NeuralOutputs] Should be only one norm discrete element per derived field/neural output for a nominal class!");
/* 393:    */           }
/* 394:508 */           Node normDiscNode = discL.item(0);
/* 395:509 */           String attValue = ((Element)normDiscNode).getAttribute("value");
/* 396:510 */           int index = this.m_classAttribute.indexOfValue(attValue);
/* 397:511 */           if (index < 0) {
/* 398:512 */             throw new Exception("[NeuralOutputs] Can't find specified target value " + attValue + " in class attribute " + this.m_classAttribute.name());
/* 399:    */           }
/* 400:515 */           this.m_categoricalIndexes[i] = index;
/* 401:    */         }
/* 402:    */       }
/* 403:    */     }
/* 404:    */     
/* 405:    */     protected void getOuput(HashMap<String, Double> incoming, double[] preds)
/* 406:    */       throws Exception
/* 407:    */     {
/* 408:531 */       if (preds.length != this.m_outputNeurons.length) {
/* 409:532 */         throw new Exception("[NeuralOutputs] Incorrect number of predictions requested: " + preds.length + "requested, " + this.m_outputNeurons.length + " expected");
/* 410:    */       }
/* 411:535 */       for (int i = 0; i < this.m_outputNeurons.length; i++)
/* 412:    */       {
/* 413:536 */         Double neuronOut = (Double)incoming.get(this.m_outputNeurons[i]);
/* 414:537 */         if (neuronOut == null) {
/* 415:538 */           throw new Exception("[NeuralOutputs] Unable to find output neuron " + this.m_outputNeurons[i] + " in the incoming HashMap!!");
/* 416:    */         }
/* 417:541 */         if (this.m_classAttribute.isNumeric())
/* 418:    */         {
/* 419:543 */           preds[0] = neuronOut.doubleValue();
/* 420:    */           
/* 421:545 */           preds[0] = this.m_regressionMapping.getResultInverse(preds);
/* 422:    */         }
/* 423:    */         else
/* 424:    */         {
/* 425:550 */           preds[this.m_categoricalIndexes[i]] = neuronOut.doubleValue();
/* 426:    */         }
/* 427:    */       }
/* 428:554 */       if (this.m_classAttribute.isNominal())
/* 429:    */       {
/* 430:556 */         double min = preds[Utils.minIndex(preds)];
/* 431:557 */         if (min < 0.0D) {
/* 432:558 */           for (int i = 0; i < preds.length; i++) {
/* 433:559 */             preds[i] -= min;
/* 434:    */           }
/* 435:    */         }
/* 436:563 */         Utils.normalize(preds);
/* 437:    */       }
/* 438:    */     }
/* 439:    */     
/* 440:    */     public String toString()
/* 441:    */     {
/* 442:568 */       StringBuffer temp = new StringBuffer();
/* 443:570 */       for (int i = 0; i < this.m_outputNeurons.length; i++)
/* 444:    */       {
/* 445:571 */         temp.append("Output neuron (" + this.m_outputNeurons[i] + ")\n");
/* 446:572 */         temp.append("mapping:\n");
/* 447:573 */         if (this.m_classAttribute.isNumeric()) {
/* 448:574 */           temp.append(this.m_regressionMapping + "\n");
/* 449:    */         } else {
/* 450:576 */           temp.append(this.m_classAttribute.name() + " = " + this.m_classAttribute.value(this.m_categoricalIndexes[i]) + "\n");
/* 451:    */         }
/* 452:    */       }
/* 453:581 */       return temp.toString();
/* 454:    */     }
/* 455:    */   }
/* 456:    */   
/* 457:    */   static enum MiningFunction
/* 458:    */   {
/* 459:589 */     CLASSIFICATION,  REGRESSION;
/* 460:    */     
/* 461:    */     private MiningFunction() {}
/* 462:    */   }
/* 463:    */   
/* 464:594 */   protected MiningFunction m_functionType = MiningFunction.CLASSIFICATION;
/* 465:    */   
/* 466:    */   static abstract enum ActivationFunction
/* 467:    */   {
/* 468:600 */     THRESHOLD("threshold"),  LOGISTIC("logistic"),  TANH("tanh"),  IDENTITY("identity"),  EXPONENTIAL("exponential"),  RECIPROCAL("reciprocal"),  SQUARE("square"),  GAUSS("gauss"),  SINE("sine"),  COSINE("cosine"),  ELLICOT("ellicot"),  ARCTAN("arctan"),  RADIALBASIS("radialBasis");
/* 469:    */     
/* 470:    */     private final String m_stringVal;
/* 471:    */     
/* 472:    */     abstract double eval(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
/* 473:    */     
/* 474:    */     private ActivationFunction(String name)
/* 475:    */     {
/* 476:677 */       this.m_stringVal = name;
/* 477:    */     }
/* 478:    */     
/* 479:    */     public String toString()
/* 480:    */     {
/* 481:681 */       return this.m_stringVal;
/* 482:    */     }
/* 483:    */   }
/* 484:    */   
/* 485:686 */   protected ActivationFunction m_activationFunction = ActivationFunction.ARCTAN;
/* 486:    */   
/* 487:    */   static enum Normalization
/* 488:    */   {
/* 489:692 */     NONE("none"),  SIMPLEMAX("simplemax"),  SOFTMAX("softmax");
/* 490:    */     
/* 491:    */     private final String m_stringVal;
/* 492:    */     
/* 493:    */     private Normalization(String name)
/* 494:    */     {
/* 495:699 */       this.m_stringVal = name;
/* 496:    */     }
/* 497:    */     
/* 498:    */     public String toString()
/* 499:    */     {
/* 500:703 */       return this.m_stringVal;
/* 501:    */     }
/* 502:    */   }
/* 503:    */   
/* 504:708 */   protected Normalization m_normalizationMethod = Normalization.NONE;
/* 505:711 */   protected double m_threshold = 0.0D;
/* 506:714 */   protected double m_width = (0.0D / 0.0D);
/* 507:717 */   protected double m_altitude = 1.0D;
/* 508:720 */   protected int m_numberOfInputs = 0;
/* 509:723 */   protected int m_numberOfLayers = 0;
/* 510:726 */   protected NeuralInput[] m_inputs = null;
/* 511:729 */   protected HashMap<String, Double> m_inputMap = new HashMap();
/* 512:732 */   protected NeuralLayer[] m_layers = null;
/* 513:735 */   protected NeuralOutputs m_outputs = null;
/* 514:    */   
/* 515:    */   public NeuralNetwork(Element model, Instances dataDictionary, MiningSchema miningSchema)
/* 516:    */     throws Exception
/* 517:    */   {
/* 518:740 */     super(dataDictionary, miningSchema);
/* 519:    */     
/* 520:742 */     String fn = model.getAttribute("functionName");
/* 521:743 */     if (fn.equals("regression")) {
/* 522:744 */       this.m_functionType = MiningFunction.REGRESSION;
/* 523:    */     }
/* 524:747 */     String act = model.getAttribute("activationFunction");
/* 525:748 */     if ((act == null) || (act.length() == 0)) {
/* 526:749 */       throw new Exception("[NeuralNetwork] no activation functon defined");
/* 527:    */     }
/* 528:753 */     for (ActivationFunction a : ActivationFunction.values()) {
/* 529:754 */       if (a.toString().equals(act))
/* 530:    */       {
/* 531:755 */         this.m_activationFunction = a;
/* 532:756 */         break;
/* 533:    */       }
/* 534:    */     }
/* 535:761 */     String norm = model.getAttribute("normalizationMethod");
/* 536:762 */     if ((norm != null) && (norm.length() > 0)) {
/* 537:763 */       for (Normalization n : Normalization.values()) {
/* 538:764 */         if (n.toString().equals(norm))
/* 539:    */         {
/* 540:765 */           this.m_normalizationMethod = n;
/* 541:766 */           break;
/* 542:    */         }
/* 543:    */       }
/* 544:    */     }
/* 545:771 */     String thresh = model.getAttribute("threshold");
/* 546:772 */     if ((thresh != null) && (thresh.length() > 0)) {
/* 547:773 */       this.m_threshold = Double.parseDouble(thresh);
/* 548:    */     }
/* 549:775 */     String width = model.getAttribute("width");
/* 550:776 */     if ((width != null) && (width.length() > 0)) {
/* 551:777 */       this.m_width = Double.parseDouble(width);
/* 552:    */     }
/* 553:779 */     String alt = model.getAttribute("altitude");
/* 554:780 */     if ((alt != null) && (alt.length() > 0)) {
/* 555:781 */       this.m_altitude = Double.parseDouble(alt);
/* 556:    */     }
/* 557:785 */     NodeList inputL = model.getElementsByTagName("NeuralInput");
/* 558:786 */     this.m_numberOfInputs = inputL.getLength();
/* 559:787 */     this.m_inputs = new NeuralInput[this.m_numberOfInputs];
/* 560:788 */     for (int i = 0; i < this.m_numberOfInputs; i++)
/* 561:    */     {
/* 562:789 */       Node inputN = inputL.item(i);
/* 563:790 */       if (inputN.getNodeType() == 1)
/* 564:    */       {
/* 565:791 */         NeuralInput nI = new NeuralInput((Element)inputN, this.m_miningSchema);
/* 566:792 */         this.m_inputs[i] = nI;
/* 567:    */       }
/* 568:    */     }
/* 569:797 */     NodeList layerL = model.getElementsByTagName("NeuralLayer");
/* 570:798 */     this.m_numberOfLayers = layerL.getLength();
/* 571:799 */     this.m_layers = new NeuralLayer[this.m_numberOfLayers];
/* 572:800 */     for (int i = 0; i < this.m_numberOfLayers; i++)
/* 573:    */     {
/* 574:801 */       Node layerN = layerL.item(i);
/* 575:802 */       if (layerN.getNodeType() == 1)
/* 576:    */       {
/* 577:803 */         NeuralLayer nL = new NeuralLayer((Element)layerN);
/* 578:804 */         this.m_layers[i] = nL;
/* 579:    */       }
/* 580:    */     }
/* 581:809 */     NodeList outputL = model.getElementsByTagName("NeuralOutputs");
/* 582:810 */     if (outputL.getLength() != 1) {
/* 583:811 */       throw new Exception("[NeuralNetwork] Should be just one NeuralOutputs element defined!");
/* 584:    */     }
/* 585:814 */     this.m_outputs = new NeuralOutputs((Element)outputL.item(0), this.m_miningSchema);
/* 586:    */   }
/* 587:    */   
/* 588:    */   public String getRevision()
/* 589:    */   {
/* 590:821 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 591:    */   }
/* 592:    */   
/* 593:    */   public double[] distributionForInstance(Instance inst)
/* 594:    */     throws Exception
/* 595:    */   {
/* 596:834 */     if (!this.m_initialized) {
/* 597:835 */       mapToMiningSchema(inst.dataset());
/* 598:    */     }
/* 599:837 */     double[] preds = null;
/* 600:839 */     if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNumeric()) {
/* 601:840 */       preds = new double[1];
/* 602:    */     } else {
/* 603:842 */       preds = new double[this.m_miningSchema.getFieldsAsInstances().classAttribute().numValues()];
/* 604:    */     }
/* 605:845 */     double[] incoming = this.m_fieldsMap.instanceToSchema(inst, this.m_miningSchema);
/* 606:    */     
/* 607:847 */     boolean hasMissing = false;
/* 608:848 */     for (int i = 0; i < incoming.length; i++) {
/* 609:849 */       if ((i != this.m_miningSchema.getFieldsAsInstances().classIndex()) && (Double.isNaN(incoming[i])))
/* 610:    */       {
/* 611:851 */         hasMissing = true;
/* 612:    */         
/* 613:853 */         break;
/* 614:    */       }
/* 615:    */     }
/* 616:857 */     if (hasMissing)
/* 617:    */     {
/* 618:858 */       if (!this.m_miningSchema.hasTargetMetaData())
/* 619:    */       {
/* 620:859 */         String message = "[NeuralNetwork] WARNING: Instance to predict has missing value(s) but there is no missing value handling meta data and no prior probabilities/default value to fall back to. No prediction will be made (" + ((this.m_miningSchema.getFieldsAsInstances().classAttribute().isNominal()) || (this.m_miningSchema.getFieldsAsInstances().classAttribute().isString()) ? "zero probabilities output)." : "NaN output).");
/* 621:867 */         if (this.m_log == null) {
/* 622:868 */           System.err.println(message);
/* 623:    */         } else {
/* 624:870 */           this.m_log.logMessage(message);
/* 625:    */         }
/* 626:873 */         if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNumeric()) {
/* 627:874 */           preds[0] = Utils.missingValue();
/* 628:    */         }
/* 629:876 */         return preds;
/* 630:    */       }
/* 631:879 */       TargetMetaInfo targetData = this.m_miningSchema.getTargetMetaData();
/* 632:880 */       if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNumeric())
/* 633:    */       {
/* 634:881 */         preds[0] = targetData.getDefaultValue();
/* 635:    */       }
/* 636:    */       else
/* 637:    */       {
/* 638:883 */         Instances miningSchemaI = this.m_miningSchema.getFieldsAsInstances();
/* 639:884 */         for (int i = 0; i < miningSchemaI.classAttribute().numValues(); i++) {
/* 640:885 */           preds[i] = targetData.getPriorProbability(miningSchemaI.classAttribute().value(i));
/* 641:    */         }
/* 642:    */       }
/* 643:888 */       return preds;
/* 644:    */     }
/* 645:893 */     this.m_inputMap.clear();
/* 646:894 */     for (int i = 0; i < this.m_inputs.length; i++)
/* 647:    */     {
/* 648:895 */       double networkInVal = this.m_inputs[i].getValue(incoming);
/* 649:896 */       String ID = this.m_inputs[i].getID();
/* 650:897 */       this.m_inputMap.put(ID, Double.valueOf(networkInVal));
/* 651:    */     }
/* 652:901 */     HashMap<String, Double> layerOut = this.m_layers[0].computeOutput(this.m_inputMap);
/* 653:902 */     for (int i = 1; i < this.m_layers.length; i++) {
/* 654:903 */       layerOut = this.m_layers[i].computeOutput(layerOut);
/* 655:    */     }
/* 656:907 */     this.m_outputs.getOuput(layerOut, preds);
/* 657:    */     
/* 658:    */ 
/* 659:910 */     return preds;
/* 660:    */   }
/* 661:    */   
/* 662:    */   public String toString()
/* 663:    */   {
/* 664:914 */     StringBuffer temp = new StringBuffer();
/* 665:    */     
/* 666:916 */     temp.append("PMML version " + getPMMLVersion());
/* 667:917 */     if (!getCreatorApplication().equals("?")) {
/* 668:918 */       temp.append("\nApplication: " + getCreatorApplication());
/* 669:    */     }
/* 670:920 */     temp.append("\nPMML Model: Neural network");
/* 671:921 */     temp.append("\n\n");
/* 672:922 */     temp.append(this.m_miningSchema);
/* 673:    */     
/* 674:924 */     temp.append("Inputs:\n");
/* 675:925 */     for (int i = 0; i < this.m_inputs.length; i++) {
/* 676:926 */       temp.append(this.m_inputs[i] + "\n");
/* 677:    */     }
/* 678:929 */     for (int i = 0; i < this.m_layers.length; i++)
/* 679:    */     {
/* 680:930 */       temp.append("Layer: " + (i + 1) + "\n");
/* 681:931 */       temp.append(this.m_layers[i] + "\n");
/* 682:    */     }
/* 683:934 */     temp.append("Outputs:\n");
/* 684:935 */     temp.append(this.m_outputs);
/* 685:    */     
/* 686:937 */     return temp.toString();
/* 687:    */   }
/* 688:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.pmml.consumer.NeuralNetwork
 * JD-Core Version:    0.7.0.1
 */