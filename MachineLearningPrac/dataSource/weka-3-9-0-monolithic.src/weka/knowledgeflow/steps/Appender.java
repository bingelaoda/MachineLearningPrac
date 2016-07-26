/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.io.BufferedInputStream;
/*   4:    */ import java.io.BufferedOutputStream;
/*   5:    */ import java.io.File;
/*   6:    */ import java.io.FileInputStream;
/*   7:    */ import java.io.FileOutputStream;
/*   8:    */ import java.io.IOException;
/*   9:    */ import java.io.ObjectInputStream;
/*  10:    */ import java.io.ObjectOutputStream;
/*  11:    */ import java.util.ArrayList;
/*  12:    */ import java.util.Arrays;
/*  13:    */ import java.util.Collection;
/*  14:    */ import java.util.HashMap;
/*  15:    */ import java.util.List;
/*  16:    */ import java.util.Map;
/*  17:    */ import java.util.Map.Entry;
/*  18:    */ import java.util.Set;
/*  19:    */ import java.util.TreeSet;
/*  20:    */ import java.util.concurrent.atomic.AtomicInteger;
/*  21:    */ import weka.core.Attribute;
/*  22:    */ import weka.core.DenseInstance;
/*  23:    */ import weka.core.Instance;
/*  24:    */ import weka.core.Instances;
/*  25:    */ import weka.core.Utils;
/*  26:    */ import weka.core.WekaException;
/*  27:    */ import weka.core.converters.SerializedInstancesLoader;
/*  28:    */ import weka.knowledgeflow.Data;
/*  29:    */ import weka.knowledgeflow.StepManager;
/*  30:    */ 
/*  31:    */ @KFStep(name="Appender", category="Flow", toolTipText="Append multiple sets of instances", iconPath="weka/gui/knowledgeflow/icons/Appender.png")
/*  32:    */ public class Appender
/*  33:    */   extends BaseStep
/*  34:    */ {
/*  35:    */   private static final long serialVersionUID = -3003135257112845998L;
/*  36:    */   protected Map<Step, Instances> m_completed;
/*  37:    */   protected Map<Step, File> m_tempBatchFiles;
/*  38:    */   protected Instances m_completeHeader;
/*  39:    */   protected AtomicInteger m_streamingCountDown;
/*  40:    */   protected transient Map<Step, ObjectOutputStream> m_incrementalSavers;
/*  41:    */   protected transient Map<Step, File> m_incrementalFiles;
/*  42:    */   protected Data m_streamingData;
/*  43:    */   protected boolean m_isReset;
/*  44:    */   
/*  45:    */   public void stepInit()
/*  46:    */     throws WekaException
/*  47:    */   {
/*  48:113 */     this.m_isReset = true;
/*  49:114 */     this.m_completed = new HashMap();
/*  50:115 */     this.m_tempBatchFiles = new HashMap();
/*  51:116 */     this.m_completeHeader = null;
/*  52:117 */     this.m_incrementalSavers = new HashMap();
/*  53:118 */     this.m_incrementalFiles = new HashMap();
/*  54:119 */     this.m_streamingCountDown = new AtomicInteger(getStepManager().numIncomingConnectionsOfType("instance"));
/*  55:    */     
/*  56:121 */     this.m_streamingData = new Data("instance");
/*  57:    */   }
/*  58:    */   
/*  59:    */   public List<String> getIncomingConnectionTypes()
/*  60:    */   {
/*  61:131 */     List<String> result = new ArrayList();
/*  62:132 */     if ((getStepManager().numIncomingConnections() == 0) || (getStepManager().numIncomingConnectionsOfType("instance") == 0)) {
/*  63:134 */       result.addAll(Arrays.asList(new String[] { "dataSet", "trainingSet", "testSet" }));
/*  64:    */     }
/*  65:138 */     if ((getStepManager().numIncomingConnections() == 0) || (getStepManager().numIncomingConnectionsOfType("instance") > 0)) {
/*  66:140 */       result.add("instance");
/*  67:    */     }
/*  68:143 */     return result;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public List<String> getOutgoingConnectionTypes()
/*  72:    */   {
/*  73:154 */     List<String> result = new ArrayList();
/*  74:156 */     if (getStepManager().numIncomingConnectionsOfType("instance") > 0) {
/*  75:158 */       result.add("instance");
/*  76:    */     } else {
/*  77:160 */       result.addAll(Arrays.asList(new String[] { "dataSet", "trainingSet", "testSet" }));
/*  78:    */     }
/*  79:164 */     return result;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void processIncoming(Data data)
/*  83:    */     throws WekaException
/*  84:    */   {
/*  85:175 */     if ((this.m_isReset) && (!data.getConnectionName().equals("instance")))
/*  86:    */     {
/*  87:177 */       getStepManager().processing();
/*  88:178 */       this.m_isReset = false;
/*  89:    */     }
/*  90:181 */     if (data.getConnectionName().equals("instance"))
/*  91:    */     {
/*  92:182 */       processStreaming(data);
/*  93:184 */       if (this.m_streamingCountDown.get() == 0)
/*  94:    */       {
/*  95:186 */         this.m_streamingData.clearPayload();
/*  96:187 */         getStepManager().throughputFinished(new Data[] { this.m_streamingData });
/*  97:    */       }
/*  98:    */     }
/*  99:    */     else
/* 100:    */     {
/* 101:190 */       processBatch(data);
/* 102:191 */       if (this.m_completed.size() == getStepManager().numIncomingConnections())
/* 103:    */       {
/* 104:193 */         getStepManager().finished();
/* 105:    */         
/* 106:195 */         this.m_completed.clear();
/* 107:196 */         this.m_tempBatchFiles.clear();
/* 108:    */       }
/* 109:    */     }
/* 110:200 */     if (isStopRequested())
/* 111:    */     {
/* 112:201 */       getStepManager().interrupted();
/* 113:    */       
/* 114:203 */       this.m_completed.clear();
/* 115:204 */       this.m_tempBatchFiles.clear();
/* 116:205 */       this.m_incrementalSavers.clear();
/* 117:206 */       this.m_incrementalFiles.clear();
/* 118:    */     }
/* 119:    */   }
/* 120:    */   
/* 121:    */   protected synchronized void processBatch(Data data)
/* 122:    */     throws WekaException
/* 123:    */   {
/* 124:217 */     Integer setNum = (Integer)data.getPayloadElement("aux_set_num", Integer.valueOf(1));
/* 125:    */     
/* 126:219 */     Integer maxSetNum = (Integer)data.getPayloadElement("aux_max_set_num", Integer.valueOf(1));
/* 127:    */     
/* 128:221 */     Instances insts = (Instances)data.getPrimaryPayload();
/* 129:223 */     if ((setNum.intValue() > 1) || (maxSetNum.intValue() > 1)) {
/* 130:225 */       throw new WekaException("Source " + data.getSourceStep().getName() + " " + "is generating more than one " + data.getConnectionName() + " " + "in a batch");
/* 131:    */     }
/* 132:230 */     Instances header = new Instances(insts, 0);
/* 133:231 */     this.m_completed.put(data.getSourceStep(), header);
/* 134:    */     try
/* 135:    */     {
/* 136:234 */       File tmpF = File.createTempFile("weka", SerializedInstancesLoader.FILE_EXTENSION);
/* 137:    */       
/* 138:    */ 
/* 139:237 */       ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(tmpF)));
/* 140:    */       
/* 141:239 */       oos.writeObject(insts);
/* 142:240 */       oos.flush();
/* 143:241 */       oos.close();
/* 144:    */       
/* 145:243 */       this.m_tempBatchFiles.put(data.getSourceStep(), tmpF);
/* 146:    */     }
/* 147:    */     catch (IOException e1)
/* 148:    */     {
/* 149:245 */       throw new WekaException(e1);
/* 150:    */     }
/* 151:248 */     if (isStopRequested()) {
/* 152:249 */       return;
/* 153:    */     }
/* 154:253 */     if (this.m_completed.size() == getStepManager().numIncomingConnections())
/* 155:    */     {
/* 156:257 */       Instances output = makeOutputHeader();
/* 157:258 */       getStepManager().logDetailed("Making output header structure");
/* 158:    */       try
/* 159:    */       {
/* 160:261 */         for (File f : this.m_tempBatchFiles.values())
/* 161:    */         {
/* 162:262 */           ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
/* 163:    */           
/* 164:264 */           Instances temp = (Instances)ois.readObject();
/* 165:265 */           ois.close();
/* 166:268 */           for (int i = 0; i < temp.numInstances(); i++)
/* 167:    */           {
/* 168:269 */             Instance converted = makeOutputInstance(output, temp.instance(i));
/* 169:270 */             output.add(converted);
/* 170:    */           }
/* 171:    */         }
/* 172:274 */         Data outputD = new Data(data.getConnectionName(), output);
/* 173:275 */         outputD.setPayloadElement("aux_set_num", Integer.valueOf(1));
/* 174:276 */         outputD.setPayloadElement("aux_max_set_num", Integer.valueOf(1));
/* 175:277 */         getStepManager().outputData(new Data[] { outputD });
/* 176:    */       }
/* 177:    */       catch (Exception ex)
/* 178:    */       {
/* 179:279 */         throw new WekaException(ex);
/* 180:    */       }
/* 181:    */     }
/* 182:    */   }
/* 183:    */   
/* 184:    */   protected synchronized void processStreaming(Data data)
/* 185:    */     throws WekaException
/* 186:    */   {
/* 187:291 */     if (isStopRequested()) {
/* 188:292 */       return;
/* 189:    */     }
/* 190:295 */     Step source = data.getSourceStep();
/* 191:296 */     Instance inst = (Instance)data.getPrimaryPayload();
/* 192:297 */     if (!this.m_completed.containsKey(source)) {
/* 193:298 */       this.m_completed.put(source, inst.dataset());
/* 194:    */     }
/* 195:301 */     if ((this.m_completed.size() == getStepManager().numIncomingConnections()) && (this.m_completeHeader == null))
/* 196:    */     {
/* 197:304 */       getStepManager().logDetailed("Creating output header structure");
/* 198:305 */       this.m_completeHeader = makeOutputHeader();
/* 199:308 */       if (this.m_incrementalSavers.size() > 0)
/* 200:    */       {
/* 201:310 */         for (Map.Entry<Step, ObjectOutputStream> e : this.m_incrementalSavers.entrySet())
/* 202:    */         {
/* 203:313 */           ObjectOutputStream s = (ObjectOutputStream)e.getValue();
/* 204:    */           try
/* 205:    */           {
/* 206:317 */             s.flush();
/* 207:318 */             s.close();
/* 208:    */             
/* 209:    */ 
/* 210:321 */             File tmpFile = (File)this.m_incrementalFiles.get(e.getKey());
/* 211:322 */             ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(tmpFile)));
/* 212:    */             
/* 213:324 */             Instance tmpLoaded = null;
/* 214:    */             do
/* 215:    */             {
/* 216:    */               try
/* 217:    */               {
/* 218:327 */                 tmpLoaded = (Instance)ois.readObject();
/* 219:328 */                 Instance converted = makeOutputInstance(this.m_completeHeader, tmpLoaded);
/* 220:    */                 
/* 221:330 */                 this.m_streamingData.setPayloadElement("instance", converted);
/* 222:    */                 
/* 223:332 */                 getStepManager().outputData(new Data[] { this.m_streamingData });
/* 224:    */               }
/* 225:    */               catch (Exception ex)
/* 226:    */               {
/* 227:335 */                 ois.close();
/* 228:336 */                 break;
/* 229:    */               }
/* 230:338 */             } while (tmpLoaded != null);
/* 231:    */           }
/* 232:    */           catch (Exception ex)
/* 233:    */           {
/* 234:352 */             throw new WekaException(ex);
/* 235:    */           }
/* 236:    */         }
/* 237:355 */         this.m_incrementalSavers.clear();
/* 238:356 */         this.m_incrementalFiles.clear();
/* 239:    */       }
/* 240:    */     }
/* 241:360 */     if (isStopRequested()) {
/* 242:361 */       return;
/* 243:    */     }
/* 244:364 */     if (getStepManager().isStreamFinished(data))
/* 245:    */     {
/* 246:365 */       this.m_streamingCountDown.decrementAndGet();
/* 247:366 */       return;
/* 248:    */     }
/* 249:369 */     if (this.m_completeHeader == null)
/* 250:    */     {
/* 251:371 */       ObjectOutputStream saver = (ObjectOutputStream)this.m_incrementalSavers.get(data.getSourceStep());
/* 252:372 */       if (saver == null) {
/* 253:    */         try
/* 254:    */         {
/* 255:374 */           File tmpFile = File.createTempFile("weka", ".arff");
/* 256:375 */           saver = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(tmpFile)));
/* 257:    */           
/* 258:377 */           this.m_incrementalSavers.put(data.getSourceStep(), saver);
/* 259:378 */           this.m_incrementalFiles.put(data.getSourceStep(), tmpFile);
/* 260:    */         }
/* 261:    */         catch (IOException ex)
/* 262:    */         {
/* 263:380 */           throw new WekaException(ex);
/* 264:    */         }
/* 265:    */       }
/* 266:    */       try
/* 267:    */       {
/* 268:397 */         saver.writeObject(inst);
/* 269:    */       }
/* 270:    */       catch (IOException e1)
/* 271:    */       {
/* 272:399 */         throw new WekaException(e1);
/* 273:    */       }
/* 274:    */     }
/* 275:    */     else
/* 276:    */     {
/* 277:403 */       Instance newI = makeOutputInstance(this.m_completeHeader, inst);
/* 278:404 */       this.m_streamingData.setPayloadElement("instance", newI);
/* 279:405 */       getStepManager().outputData(new Data[] { this.m_streamingData });
/* 280:    */     }
/* 281:    */   }
/* 282:    */   
/* 283:    */   private Instance makeOutputInstance(Instances output, Instance source)
/* 284:    */   {
/* 285:418 */     double[] newVals = new double[output.numAttributes()];
/* 286:419 */     for (int i = 0; i < newVals.length; i++) {
/* 287:420 */       newVals[i] = Utils.missingValue();
/* 288:    */     }
/* 289:423 */     for (int i = 0; i < source.numAttributes(); i++) {
/* 290:424 */       if (!source.isMissing(i))
/* 291:    */       {
/* 292:425 */         Attribute s = source.attribute(i);
/* 293:426 */         int outputIndex = output.attribute(s.name()).index();
/* 294:427 */         if (s.isNumeric())
/* 295:    */         {
/* 296:428 */           newVals[outputIndex] = source.value(s);
/* 297:    */         }
/* 298:429 */         else if (s.isString())
/* 299:    */         {
/* 300:430 */           String sVal = source.stringValue(s);
/* 301:431 */           newVals[outputIndex] = output.attribute(outputIndex).addStringValue(sVal);
/* 302:    */         }
/* 303:433 */         else if (s.isRelationValued())
/* 304:    */         {
/* 305:434 */           Instances rVal = source.relationalValue(s);
/* 306:435 */           newVals[outputIndex] = output.attribute(outputIndex).addRelation(rVal);
/* 307:    */         }
/* 308:437 */         else if (s.isNominal())
/* 309:    */         {
/* 310:438 */           String nomVal = source.stringValue(s);
/* 311:439 */           newVals[outputIndex] = output.attribute(outputIndex).indexOfValue(nomVal);
/* 312:    */         }
/* 313:    */       }
/* 314:    */     }
/* 315:445 */     Instance newInst = new DenseInstance(source.weight(), newVals);
/* 316:446 */     newInst.setDataset(output);
/* 317:    */     
/* 318:448 */     return newInst;
/* 319:    */   }
/* 320:    */   
/* 321:    */   protected Instances makeOutputHeader()
/* 322:    */     throws WekaException
/* 323:    */   {
/* 324:458 */     return makeOutputHeader(this.m_completed.values());
/* 325:    */   }
/* 326:    */   
/* 327:    */   protected Instances makeOutputHeader(Collection<Instances> headers)
/* 328:    */     throws WekaException
/* 329:    */   {
/* 330:471 */     Map<String, Attribute> attLookup = new HashMap();
/* 331:472 */     List<Attribute> attList = new ArrayList();
/* 332:473 */     Map<String, Set<String>> nominalLookups = new HashMap();
/* 333:475 */     for (Instances h : headers) {
/* 334:476 */       for (int i = 0; i < h.numAttributes(); i++)
/* 335:    */       {
/* 336:477 */         Attribute a = h.attribute(i);
/* 337:478 */         if (!attLookup.containsKey(a.name()))
/* 338:    */         {
/* 339:479 */           attLookup.put(a.name(), a);
/* 340:480 */           attList.add(a);
/* 341:481 */           if (a.isNominal())
/* 342:    */           {
/* 343:482 */             TreeSet<String> nVals = new TreeSet();
/* 344:483 */             for (int j = 0; j < a.numValues(); j++) {
/* 345:484 */               nVals.add(a.value(j));
/* 346:    */             }
/* 347:486 */             nominalLookups.put(a.name(), nVals);
/* 348:    */           }
/* 349:    */         }
/* 350:    */         else
/* 351:    */         {
/* 352:489 */           Attribute storedVersion = (Attribute)attLookup.get(a.name());
/* 353:491 */           if (storedVersion.type() != a.type()) {
/* 354:492 */             throw new WekaException("Conflicting types for attribute name '" + a.name() + "' between incoming " + "instance sets");
/* 355:    */           }
/* 356:496 */           if (storedVersion.isNominal())
/* 357:    */           {
/* 358:497 */             Set<String> storedVals = (Set)nominalLookups.get(a.name());
/* 359:498 */             for (int j = 0; j < a.numValues(); j++) {
/* 360:499 */               storedVals.add(a.value(j));
/* 361:    */             }
/* 362:    */           }
/* 363:    */         }
/* 364:    */       }
/* 365:    */     }
/* 366:506 */     ArrayList<Attribute> finalAttList = new ArrayList();
/* 367:507 */     for (Attribute a : attList)
/* 368:    */     {
/* 369:508 */       Attribute newAtt = null;
/* 370:509 */       if (a.isDate())
/* 371:    */       {
/* 372:510 */         newAtt = new Attribute(a.name(), a.getDateFormat());
/* 373:    */       }
/* 374:511 */       else if (a.isNumeric())
/* 375:    */       {
/* 376:512 */         newAtt = new Attribute(a.name());
/* 377:    */       }
/* 378:513 */       else if (a.isRelationValued())
/* 379:    */       {
/* 380:514 */         newAtt = new Attribute(a.name(), a.relation());
/* 381:    */       }
/* 382:515 */       else if (a.isNominal())
/* 383:    */       {
/* 384:516 */         Set<String> vals = (Set)nominalLookups.get(a.name());
/* 385:517 */         List<String> newVals = new ArrayList();
/* 386:518 */         for (String v : vals) {
/* 387:519 */           newVals.add(v);
/* 388:    */         }
/* 389:521 */         newAtt = new Attribute(a.name(), newVals);
/* 390:    */       }
/* 391:522 */       else if (a.isString())
/* 392:    */       {
/* 393:523 */         newAtt = new Attribute(a.name(), (List)null);
/* 394:    */       }
/* 395:526 */       finalAttList.add(newAtt);
/* 396:    */     }
/* 397:529 */     return new Instances("Appended_" + getStepManager().numIncomingConnections() + "_sets", finalAttList, 0);
/* 398:    */   }
/* 399:    */   
/* 400:    */   public Instances outputStructureForConnectionType(String connectionName)
/* 401:    */     throws WekaException
/* 402:    */   {
/* 403:547 */     if (getStepManager().numIncomingConnections() > 0)
/* 404:    */     {
/* 405:548 */       List<Instances> incomingHeaders = new ArrayList();
/* 406:549 */       for (Map.Entry<String, List<StepManager>> e : getStepManager().getIncomingConnections().entrySet()) {
/* 407:551 */         if (((List)e.getValue()).size() > 0)
/* 408:    */         {
/* 409:552 */           incomingConType = (String)e.getKey();
/* 410:553 */           for (StepManager sm : (List)e.getValue())
/* 411:    */           {
/* 412:554 */             Instances incomingStruc = getStepManager().getIncomingStructureFromStep(sm, incomingConType);
/* 413:556 */             if (incomingStruc == null) {
/* 414:559 */               return null;
/* 415:    */             }
/* 416:561 */             incomingHeaders.add(incomingStruc);
/* 417:    */           }
/* 418:    */         }
/* 419:    */       }
/* 420:    */       String incomingConType;
/* 421:565 */       if (incomingHeaders.size() > 0) {
/* 422:566 */         return makeOutputHeader(incomingHeaders);
/* 423:    */       }
/* 424:    */     }
/* 425:570 */     return null;
/* 426:    */   }
/* 427:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.Appender
 * JD-Core Version:    0.7.0.1
 */