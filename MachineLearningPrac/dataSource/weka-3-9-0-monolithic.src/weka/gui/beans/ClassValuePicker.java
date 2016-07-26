/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.beans.EventSetDescriptor;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.Serializable;
/*   7:    */ import java.util.Vector;
/*   8:    */ import javax.swing.JPanel;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.filters.Filter;
/*  12:    */ import weka.filters.unsupervised.attribute.SwapValues;
/*  13:    */ import weka.gui.Logger;
/*  14:    */ 
/*  15:    */ public class ClassValuePicker
/*  16:    */   extends JPanel
/*  17:    */   implements Visible, DataSourceListener, BeanCommon, EventConstraints, Serializable, StructureProducer
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = -1196143276710882989L;
/*  20:    */   private String m_classValue;
/*  21:    */   private Instances m_connectedFormat;
/*  22:    */   private Object m_dataProvider;
/*  23: 55 */   private final Vector<DataSourceListener> m_dataListeners = new Vector();
/*  24: 57 */   private final Vector<DataFormatListener> m_dataFormatListeners = new Vector();
/*  25: 60 */   protected transient Logger m_logger = null;
/*  26: 62 */   protected BeanVisual m_visual = new BeanVisual("ClassValuePicker", "weka/gui/beans/icons/ClassValuePicker.gif", "weka/gui/beans/icons/ClassValuePicker_animated.gif");
/*  27:    */   
/*  28:    */   public String globalInfo()
/*  29:    */   {
/*  30: 72 */     return "Designate which class value is to be considered the \"positive\" class value (useful for ROC style curves).";
/*  31:    */   }
/*  32:    */   
/*  33:    */   public ClassValuePicker()
/*  34:    */   {
/*  35: 77 */     setLayout(new BorderLayout());
/*  36: 78 */     add(this.m_visual, "Center");
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setCustomName(String name)
/*  40:    */   {
/*  41: 88 */     this.m_visual.setText(name);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String getCustomName()
/*  45:    */   {
/*  46: 98 */     return this.m_visual.getText();
/*  47:    */   }
/*  48:    */   
/*  49:    */   public Instances getStructure(String eventName)
/*  50:    */   {
/*  51:103 */     if (!eventName.equals("dataSet")) {
/*  52:104 */       return null;
/*  53:    */     }
/*  54:106 */     if (this.m_dataProvider == null) {
/*  55:107 */       return null;
/*  56:    */     }
/*  57:110 */     if ((this.m_dataProvider != null) && ((this.m_dataProvider instanceof StructureProducer))) {
/*  58:111 */       this.m_connectedFormat = ((StructureProducer)this.m_dataProvider).getStructure("dataSet");
/*  59:    */     }
/*  60:115 */     return this.m_connectedFormat;
/*  61:    */   }
/*  62:    */   
/*  63:    */   protected Instances getStructure()
/*  64:    */   {
/*  65:119 */     if (this.m_dataProvider != null) {
/*  66:120 */       return getStructure("dataSet");
/*  67:    */     }
/*  68:123 */     return null;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public Instances getConnectedFormat()
/*  72:    */   {
/*  73:146 */     return getStructure();
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setClassValue(String value)
/*  77:    */   {
/*  78:155 */     this.m_classValue = value;
/*  79:156 */     if (this.m_connectedFormat != null) {
/*  80:157 */       notifyDataFormatListeners();
/*  81:    */     }
/*  82:    */   }
/*  83:    */   
/*  84:    */   public String getClassValue()
/*  85:    */   {
/*  86:167 */     return this.m_classValue;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void acceptDataSet(DataSetEvent e)
/*  90:    */   {
/*  91:172 */     if ((e.isStructureOnly()) && (
/*  92:173 */       (this.m_connectedFormat == null) || (!this.m_connectedFormat.equalHeaders(e.getDataSet()))))
/*  93:    */     {
/*  94:175 */       this.m_connectedFormat = new Instances(e.getDataSet(), 0);
/*  95:    */       
/*  96:177 */       notifyDataFormatListeners();
/*  97:    */     }
/*  98:180 */     Instances dataSet = e.getDataSet();
/*  99:181 */     Instances newDataSet = assignClassValue(dataSet);
/* 100:183 */     if (newDataSet != null)
/* 101:    */     {
/* 102:184 */       e = new DataSetEvent(this, newDataSet);
/* 103:185 */       notifyDataListeners(e);
/* 104:    */     }
/* 105:187 */     else if (this.m_logger != null)
/* 106:    */     {
/* 107:188 */       this.m_logger.logMessage("[ClassValuePicker] " + statusMessagePrefix() + " Class value '" + this.m_classValue + "' does not seem to exist!");
/* 108:    */       
/* 109:190 */       this.m_logger.statusMessage(statusMessagePrefix() + "ERROR: Class value '" + this.m_classValue + "' does not seem to exist!");
/* 110:    */     }
/* 111:    */   }
/* 112:    */   
/* 113:    */   private Instances assignClassValue(Instances dataSet)
/* 114:    */   {
/* 115:199 */     if (dataSet.classIndex() < 0)
/* 116:    */     {
/* 117:200 */       if (this.m_logger != null)
/* 118:    */       {
/* 119:201 */         this.m_logger.logMessage("[ClassValuePicker] " + statusMessagePrefix() + " No class attribute defined in data set.");
/* 120:    */         
/* 121:203 */         this.m_logger.statusMessage(statusMessagePrefix() + "WARNING: No class attribute defined in data set.");
/* 122:    */       }
/* 123:206 */       return dataSet;
/* 124:    */     }
/* 125:209 */     if (dataSet.classAttribute().isNumeric())
/* 126:    */     {
/* 127:210 */       if (this.m_logger != null)
/* 128:    */       {
/* 129:211 */         this.m_logger.logMessage("[ClassValuePicker] " + statusMessagePrefix() + " Class attribute must be nominal (ClassValuePicker)");
/* 130:    */         
/* 131:213 */         this.m_logger.statusMessage(statusMessagePrefix() + "WARNING: Class attribute must be nominal.");
/* 132:    */       }
/* 133:216 */       return dataSet;
/* 134:    */     }
/* 135:218 */     if (this.m_logger != null) {
/* 136:219 */       this.m_logger.statusMessage(statusMessagePrefix() + "remove");
/* 137:    */     }
/* 138:223 */     if (((this.m_classValue == null) || (this.m_classValue.length() == 0)) && (dataSet.numInstances() > 0))
/* 139:    */     {
/* 140:226 */       if (this.m_logger != null)
/* 141:    */       {
/* 142:227 */         this.m_logger.logMessage("[ClassValuePicker] " + statusMessagePrefix() + " Class value to consider as positive has not been set" + " (ClassValuePicker)");
/* 143:    */         
/* 144:    */ 
/* 145:230 */         this.m_logger.statusMessage(statusMessagePrefix() + "WARNING: Class value to consider as positive has not been set.");
/* 146:    */       }
/* 147:233 */       return dataSet;
/* 148:    */     }
/* 149:236 */     if (this.m_classValue == null) {
/* 150:240 */       return dataSet;
/* 151:    */     }
/* 152:243 */     Attribute classAtt = dataSet.classAttribute();
/* 153:244 */     int classValueIndex = -1;
/* 154:248 */     if ((this.m_classValue.startsWith("/")) && (this.m_classValue.length() > 1))
/* 155:    */     {
/* 156:249 */       String remainder = this.m_classValue.substring(1);
/* 157:250 */       remainder = remainder.trim();
/* 158:251 */       if (remainder.equalsIgnoreCase("first")) {
/* 159:252 */         classValueIndex = 0;
/* 160:253 */       } else if (remainder.equalsIgnoreCase("last")) {
/* 161:254 */         classValueIndex = classAtt.numValues() - 1;
/* 162:    */       } else {
/* 163:    */         try
/* 164:    */         {
/* 165:258 */           classValueIndex = Integer.parseInt(remainder);
/* 166:259 */           classValueIndex--;
/* 167:261 */           if (((classValueIndex < 0) || (classValueIndex > classAtt.numValues() - 1)) && 
/* 168:262 */             (this.m_logger != null))
/* 169:    */           {
/* 170:263 */             this.m_logger.logMessage("[ClassValuePicker] " + statusMessagePrefix() + " Class value index is out of range!" + " (ClassValuePicker)");
/* 171:    */             
/* 172:    */ 
/* 173:    */ 
/* 174:267 */             this.m_logger.statusMessage(statusMessagePrefix() + "ERROR: Class value index is out of range!.");
/* 175:    */           }
/* 176:    */         }
/* 177:    */         catch (NumberFormatException n)
/* 178:    */         {
/* 179:272 */           if (this.m_logger != null)
/* 180:    */           {
/* 181:273 */             this.m_logger.logMessage("[ClassValuePicker] " + statusMessagePrefix() + " Unable to parse supplied class value index as an integer" + " (ClassValuePicker)");
/* 182:    */             
/* 183:    */ 
/* 184:276 */             this.m_logger.statusMessage(statusMessagePrefix() + "WARNING: Unable to parse supplied class value index " + "as an integer.");
/* 185:    */             
/* 186:    */ 
/* 187:279 */             return dataSet;
/* 188:    */           }
/* 189:    */         }
/* 190:    */       }
/* 191:    */     }
/* 192:    */     else
/* 193:    */     {
/* 194:285 */       classValueIndex = classAtt.indexOfValue(this.m_classValue.trim());
/* 195:    */     }
/* 196:288 */     if (classValueIndex < 0) {
/* 197:289 */       return null;
/* 198:    */     }
/* 199:292 */     if (classValueIndex != 0) {
/* 200:    */       try
/* 201:    */       {
/* 202:295 */         SwapValues sv = new SwapValues();
/* 203:296 */         sv.setAttributeIndex("" + (dataSet.classIndex() + 1));
/* 204:297 */         sv.setFirstValueIndex("first");
/* 205:298 */         sv.setSecondValueIndex("" + (classValueIndex + 1));
/* 206:299 */         sv.setInputFormat(dataSet);
/* 207:300 */         Instances newDataSet = Filter.useFilter(dataSet, sv);
/* 208:301 */         newDataSet.setRelationName(dataSet.relationName());
/* 209:302 */         return newDataSet;
/* 210:    */       }
/* 211:    */       catch (Exception ex)
/* 212:    */       {
/* 213:304 */         if (this.m_logger != null)
/* 214:    */         {
/* 215:305 */           this.m_logger.logMessage("[ClassValuePicker] " + statusMessagePrefix() + " Unable to swap class attibute values.");
/* 216:    */           
/* 217:307 */           this.m_logger.statusMessage(statusMessagePrefix() + "ERROR: (See log for details)");
/* 218:    */           
/* 219:309 */           return null;
/* 220:    */         }
/* 221:    */       }
/* 222:    */     }
/* 223:313 */     return dataSet;
/* 224:    */   }
/* 225:    */   
/* 226:    */   protected void notifyDataListeners(DataSetEvent tse)
/* 227:    */   {
/* 228:    */     Vector<DataSourceListener> l;
/* 229:319 */     synchronized (this)
/* 230:    */     {
/* 231:320 */       l = (Vector)this.m_dataListeners.clone();
/* 232:    */     }
/* 233:322 */     if (l.size() > 0) {
/* 234:323 */       for (int i = 0; i < l.size(); i++)
/* 235:    */       {
/* 236:324 */         System.err.println("Notifying data listeners (ClassValuePicker)");
/* 237:325 */         ((DataSourceListener)l.elementAt(i)).acceptDataSet(tse);
/* 238:    */       }
/* 239:    */     }
/* 240:    */   }
/* 241:    */   
/* 242:    */   protected void notifyDataFormatListeners()
/* 243:    */   {
/* 244:    */     Vector<DataFormatListener> l;
/* 245:333 */     synchronized (this)
/* 246:    */     {
/* 247:334 */       l = (Vector)this.m_dataFormatListeners.clone();
/* 248:    */     }
/* 249:336 */     if (l.size() > 0)
/* 250:    */     {
/* 251:337 */       DataSetEvent dse = new DataSetEvent(this, this.m_connectedFormat);
/* 252:338 */       for (int i = 0; i < l.size(); i++) {
/* 253:339 */         ((DataFormatListener)l.elementAt(i)).newDataFormat(dse);
/* 254:    */       }
/* 255:    */     }
/* 256:    */   }
/* 257:    */   
/* 258:    */   public synchronized void addDataSourceListener(DataSourceListener tsl)
/* 259:    */   {
/* 260:345 */     this.m_dataListeners.addElement(tsl);
/* 261:    */   }
/* 262:    */   
/* 263:    */   public synchronized void removeDataSourceListener(DataSourceListener tsl)
/* 264:    */   {
/* 265:349 */     this.m_dataListeners.removeElement(tsl);
/* 266:    */   }
/* 267:    */   
/* 268:    */   public synchronized void addDataFormatListener(DataFormatListener dfl)
/* 269:    */   {
/* 270:353 */     this.m_dataFormatListeners.addElement(dfl);
/* 271:    */   }
/* 272:    */   
/* 273:    */   public synchronized void removeDataFormatListener(DataFormatListener dfl)
/* 274:    */   {
/* 275:357 */     this.m_dataFormatListeners.removeElement(dfl);
/* 276:    */   }
/* 277:    */   
/* 278:    */   public void setVisual(BeanVisual newVisual)
/* 279:    */   {
/* 280:362 */     this.m_visual = newVisual;
/* 281:    */   }
/* 282:    */   
/* 283:    */   public BeanVisual getVisual()
/* 284:    */   {
/* 285:367 */     return this.m_visual;
/* 286:    */   }
/* 287:    */   
/* 288:    */   public void useDefaultVisual()
/* 289:    */   {
/* 290:372 */     this.m_visual.loadIcons("weka/gui/beans/icons/ClassValuePicker.gif", "weka/gui/beans/icons/ClassValuePicker_animated.gif");
/* 291:    */   }
/* 292:    */   
/* 293:    */   public boolean connectionAllowed(String eventName)
/* 294:    */   {
/* 295:385 */     if ((eventName.compareTo("dataSet") == 0) && (this.m_dataProvider != null)) {
/* 296:386 */       return false;
/* 297:    */     }
/* 298:389 */     return true;
/* 299:    */   }
/* 300:    */   
/* 301:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/* 302:    */   {
/* 303:401 */     return connectionAllowed(esd.getName());
/* 304:    */   }
/* 305:    */   
/* 306:    */   public synchronized void connectionNotification(String eventName, Object source)
/* 307:    */   {
/* 308:415 */     if ((connectionAllowed(eventName)) && 
/* 309:416 */       (eventName.compareTo("dataSet") == 0)) {
/* 310:417 */       this.m_dataProvider = source;
/* 311:    */     }
/* 312:420 */     this.m_connectedFormat = null;
/* 313:    */   }
/* 314:    */   
/* 315:    */   public synchronized void disconnectionNotification(String eventName, Object source)
/* 316:    */   {
/* 317:435 */     if ((eventName.compareTo("dataSet") == 0) && 
/* 318:436 */       (this.m_dataProvider == source)) {
/* 319:437 */       this.m_dataProvider = null;
/* 320:    */     }
/* 321:440 */     this.m_connectedFormat = null;
/* 322:    */   }
/* 323:    */   
/* 324:    */   public void setLog(Logger logger)
/* 325:    */   {
/* 326:445 */     this.m_logger = logger;
/* 327:    */   }
/* 328:    */   
/* 329:    */   public void stop() {}
/* 330:    */   
/* 331:    */   public boolean isBusy()
/* 332:    */   {
/* 333:461 */     return false;
/* 334:    */   }
/* 335:    */   
/* 336:    */   public boolean eventGeneratable(String eventName)
/* 337:    */   {
/* 338:474 */     if (eventName.compareTo("dataSet") != 0) {
/* 339:475 */       return false;
/* 340:    */     }
/* 341:478 */     if (eventName.compareTo("dataSet") == 0)
/* 342:    */     {
/* 343:479 */       if (this.m_dataProvider == null)
/* 344:    */       {
/* 345:480 */         this.m_connectedFormat = null;
/* 346:481 */         notifyDataFormatListeners();
/* 347:482 */         return false;
/* 348:    */       }
/* 349:484 */       if (((this.m_dataProvider instanceof EventConstraints)) && 
/* 350:485 */         (!((EventConstraints)this.m_dataProvider).eventGeneratable("dataSet")))
/* 351:    */       {
/* 352:486 */         this.m_connectedFormat = null;
/* 353:487 */         notifyDataFormatListeners();
/* 354:488 */         return false;
/* 355:    */       }
/* 356:    */     }
/* 357:493 */     return true;
/* 358:    */   }
/* 359:    */   
/* 360:    */   private String statusMessagePrefix()
/* 361:    */   {
/* 362:497 */     return getCustomName() + "$" + hashCode() + "|";
/* 363:    */   }
/* 364:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ClassValuePicker
 * JD-Core Version:    0.7.0.1
 */