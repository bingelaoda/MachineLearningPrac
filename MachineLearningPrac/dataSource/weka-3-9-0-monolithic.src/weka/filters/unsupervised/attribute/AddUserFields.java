/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.text.ParseException;
/*   5:    */ import java.text.SimpleDateFormat;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.Date;
/*   8:    */ import java.util.Enumeration;
/*   9:    */ import java.util.List;
/*  10:    */ import java.util.Vector;
/*  11:    */ import weka.core.Attribute;
/*  12:    */ import weka.core.Capabilities;
/*  13:    */ import weka.core.Capabilities.Capability;
/*  14:    */ import weka.core.DenseInstance;
/*  15:    */ import weka.core.Environment;
/*  16:    */ import weka.core.EnvironmentHandler;
/*  17:    */ import weka.core.Instance;
/*  18:    */ import weka.core.Instances;
/*  19:    */ import weka.core.Option;
/*  20:    */ import weka.core.OptionHandler;
/*  21:    */ import weka.core.SparseInstance;
/*  22:    */ import weka.core.Utils;
/*  23:    */ import weka.filters.Filter;
/*  24:    */ import weka.filters.UnsupervisedFilter;
/*  25:    */ 
/*  26:    */ public class AddUserFields
/*  27:    */   extends Filter
/*  28:    */   implements OptionHandler, EnvironmentHandler, UnsupervisedFilter
/*  29:    */ {
/*  30:    */   private static final long serialVersionUID = -2761427344847891585L;
/*  31:    */   protected List<AttributeSpec> m_attributeSpecs;
/*  32:    */   protected transient Environment m_env;
/*  33:    */   
/*  34:    */   public static class AttributeSpec
/*  35:    */     implements Serializable
/*  36:    */   {
/*  37:    */     private static final long serialVersionUID = -617328946241474608L;
/*  38:100 */     protected String m_name = "";
/*  39:103 */     protected String m_value = "";
/*  40:106 */     protected String m_type = "";
/*  41:    */     protected String m_nameS;
/*  42:    */     protected String m_valueS;
/*  43:    */     protected String m_typeS;
/*  44:    */     protected SimpleDateFormat m_dateFormat;
/*  45:    */     protected Date m_parsedDate;
/*  46:    */     
/*  47:    */     public AttributeSpec() {}
/*  48:    */     
/*  49:    */     public AttributeSpec(String spec)
/*  50:    */     {
/*  51:135 */       parseFromInternal(spec);
/*  52:    */     }
/*  53:    */     
/*  54:    */     public void setName(String name)
/*  55:    */     {
/*  56:144 */       this.m_name = name;
/*  57:    */     }
/*  58:    */     
/*  59:    */     public String getName()
/*  60:    */     {
/*  61:153 */       return this.m_name;
/*  62:    */     }
/*  63:    */     
/*  64:    */     public void setType(String type)
/*  65:    */     {
/*  66:162 */       this.m_type = type;
/*  67:    */     }
/*  68:    */     
/*  69:    */     public String getType()
/*  70:    */     {
/*  71:171 */       return this.m_type;
/*  72:    */     }
/*  73:    */     
/*  74:    */     public void setValue(String value)
/*  75:    */     {
/*  76:183 */       this.m_value = value;
/*  77:    */     }
/*  78:    */     
/*  79:    */     public String getValue()
/*  80:    */     {
/*  81:195 */       return this.m_value;
/*  82:    */     }
/*  83:    */     
/*  84:    */     public String getResolvedName()
/*  85:    */     {
/*  86:206 */       return this.m_nameS;
/*  87:    */     }
/*  88:    */     
/*  89:    */     public String getResolvedValue()
/*  90:    */     {
/*  91:217 */       return this.m_valueS;
/*  92:    */     }
/*  93:    */     
/*  94:    */     public String getResolvedType()
/*  95:    */     {
/*  96:228 */       return this.m_typeS;
/*  97:    */     }
/*  98:    */     
/*  99:    */     public String getDateFormat()
/* 100:    */     {
/* 101:237 */       if (this.m_dateFormat != null) {
/* 102:238 */         return this.m_dateFormat.toPattern();
/* 103:    */       }
/* 104:240 */       return null;
/* 105:    */     }
/* 106:    */     
/* 107:    */     public Date getDateValue()
/* 108:    */     {
/* 109:251 */       if (this.m_parsedDate != null) {
/* 110:252 */         return this.m_parsedDate;
/* 111:    */       }
/* 112:255 */       if (getResolvedType().toLowerCase().startsWith("date")) {
/* 113:256 */         return new Date();
/* 114:    */       }
/* 115:259 */       return null;
/* 116:    */     }
/* 117:    */     
/* 118:    */     public double getNumericValue()
/* 119:    */     {
/* 120:269 */       if (getResolvedType().toLowerCase().startsWith("numeric")) {
/* 121:270 */         return Double.parseDouble(getResolvedValue());
/* 122:    */       }
/* 123:273 */       return Utils.missingValue();
/* 124:    */     }
/* 125:    */     
/* 126:    */     public String getNominalOrStringValue()
/* 127:    */     {
/* 128:283 */       if ((getResolvedType().toLowerCase().startsWith("nominal")) || (getResolvedType().toLowerCase().startsWith("string"))) {
/* 129:285 */         return getResolvedValue();
/* 130:    */       }
/* 131:288 */       return null;
/* 132:    */     }
/* 133:    */     
/* 134:    */     protected void parseFromInternal(String spec)
/* 135:    */     {
/* 136:292 */       String[] parts = spec.split("@");
/* 137:294 */       if (parts.length > 0) {
/* 138:295 */         this.m_name = parts[0].trim();
/* 139:    */       }
/* 140:297 */       if (parts.length > 1) {
/* 141:298 */         this.m_type = parts[1].trim();
/* 142:    */       }
/* 143:300 */       if (parts.length > 2) {
/* 144:301 */         this.m_value = parts[2].trim();
/* 145:    */       }
/* 146:    */     }
/* 147:    */     
/* 148:    */     public void init(Environment env)
/* 149:    */     {
/* 150:312 */       this.m_nameS = this.m_name;
/* 151:313 */       this.m_typeS = this.m_type;
/* 152:314 */       this.m_valueS = this.m_value;
/* 153:    */       try
/* 154:    */       {
/* 155:317 */         this.m_nameS = env.substitute(this.m_nameS);
/* 156:318 */         this.m_typeS = env.substitute(this.m_typeS);
/* 157:319 */         this.m_valueS = env.substitute(this.m_valueS);
/* 158:    */       }
/* 159:    */       catch (Exception ex) {}
/* 160:323 */       if ((this.m_typeS.toLowerCase().startsWith("date")) && (this.m_typeS.indexOf(":") > 0))
/* 161:    */       {
/* 162:324 */         String format = this.m_typeS.substring(this.m_typeS.indexOf(":") + 1, this.m_typeS.length());
/* 163:    */         
/* 164:326 */         this.m_dateFormat = new SimpleDateFormat(format);
/* 165:327 */         if (!this.m_valueS.toLowerCase().equals("now")) {
/* 166:    */           try
/* 167:    */           {
/* 168:329 */             this.m_parsedDate = this.m_dateFormat.parse(this.m_valueS);
/* 169:    */           }
/* 170:    */           catch (ParseException e)
/* 171:    */           {
/* 172:331 */             throw new IllegalArgumentException("Date value \"" + this.m_valueS + " \" can't be parsed with formatting string \"" + format + "\"");
/* 173:    */           }
/* 174:    */         }
/* 175:    */       }
/* 176:    */     }
/* 177:    */     
/* 178:    */     public String toString()
/* 179:    */     {
/* 180:345 */       StringBuffer buff = new StringBuffer();
/* 181:    */       
/* 182:347 */       buff.append("Name: ").append(this.m_name).append(" ");
/* 183:348 */       String type = this.m_type;
/* 184:349 */       if ((type.toLowerCase().startsWith("date")) && (type.indexOf(":") > 0))
/* 185:    */       {
/* 186:350 */         type = type.substring(0, type.indexOf(":"));
/* 187:351 */         String format = this.m_type.substring(this.m_type.indexOf(":1", this.m_type.length()));
/* 188:    */         
/* 189:353 */         buff.append("Type: ").append(type).append(" [").append(format).append("] ");
/* 190:    */       }
/* 191:    */       else
/* 192:    */       {
/* 193:356 */         buff.append("Type: ").append(type).append(" ");
/* 194:    */       }
/* 195:358 */       buff.append("Value: ").append(this.m_value);
/* 196:    */       
/* 197:360 */       return buff.toString();
/* 198:    */     }
/* 199:    */     
/* 200:    */     public String toStringInternal()
/* 201:    */     {
/* 202:364 */       StringBuffer buff = new StringBuffer();
/* 203:    */       
/* 204:366 */       buff.append(this.m_name).append("@").append(this.m_type).append("@").append(this.m_value);
/* 205:    */       
/* 206:    */ 
/* 207:369 */       return buff.toString();
/* 208:    */     }
/* 209:    */   }
/* 210:    */   
/* 211:    */   public AddUserFields()
/* 212:    */   {
/* 213:377 */     this.m_attributeSpecs = new ArrayList();
/* 214:    */   }
/* 215:    */   
/* 216:    */   public String globalInfo()
/* 217:    */   {
/* 218:387 */     return "A filter that adds new attributes with user specified type and constant value. Numeric, nominal, string and date attributes can be created. Attribute name, and value can be set with environment variables. Date attributes can also specify a formatting string by which to parse the supplied date value. Alternatively, a current time stamp can be specified by supplying the special string \"now\" as the value for a date attribute.";
/* 219:    */   }
/* 220:    */   
/* 221:    */   public Capabilities getCapabilities()
/* 222:    */   {
/* 223:404 */     Capabilities result = super.getCapabilities();
/* 224:405 */     result.disableAll();
/* 225:    */     
/* 226:    */ 
/* 227:408 */     result.enableAllAttributes();
/* 228:409 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 229:    */     
/* 230:    */ 
/* 231:412 */     result.enableAllClasses();
/* 232:413 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 233:    */     
/* 234:415 */     return result;
/* 235:    */   }
/* 236:    */   
/* 237:    */   public void clearAttributeSpecs()
/* 238:    */   {
/* 239:422 */     if (this.m_attributeSpecs == null) {
/* 240:423 */       this.m_attributeSpecs = new ArrayList();
/* 241:    */     }
/* 242:425 */     this.m_attributeSpecs.clear();
/* 243:    */   }
/* 244:    */   
/* 245:    */   public Enumeration<Option> listOptions()
/* 246:    */   {
/* 247:436 */     Vector<Option> newVector = new Vector(1);
/* 248:    */     
/* 249:438 */     newVector.addElement(new Option("\tNew field specification (name@type@value).\n\t Environment variables may be used for any/all parts of the\n\tspecification. Type can be one of (numeric, nominal, string or date).\n\tThe value for date be a specific date string or the special string\n\t\"now\" to indicate the current date-time. A specific date format\n\tstring for parsing specific date values can be specified by suffixing\n\tthe type specification - e.g. \"myTime@date:MM-dd-yyyy@08-23-2009\".This option may be specified multiple times", "A", 1, "-A <name@type@value>"));
/* 250:    */     
/* 251:    */ 
/* 252:    */ 
/* 253:    */ 
/* 254:    */ 
/* 255:    */ 
/* 256:    */ 
/* 257:    */ 
/* 258:    */ 
/* 259:    */ 
/* 260:    */ 
/* 261:450 */     return newVector.elements();
/* 262:    */   }
/* 263:    */   
/* 264:    */   public void setOptions(String[] options)
/* 265:    */     throws Exception
/* 266:    */   {
/* 267:479 */     clearAttributeSpecs();
/* 268:    */     
/* 269:481 */     String attS = "";
/* 270:482 */     while ((attS = Utils.getOption('A', options)).length() > 0) {
/* 271:483 */       addAttributeSpec(attS);
/* 272:    */     }
/* 273:486 */     Utils.checkForRemainingOptions(options);
/* 274:    */   }
/* 275:    */   
/* 276:    */   public String[] getOptions()
/* 277:    */   {
/* 278:497 */     ArrayList<String> options = new ArrayList();
/* 279:499 */     for (int i = 0; i < this.m_attributeSpecs.size(); i++)
/* 280:    */     {
/* 281:500 */       options.add("-A");
/* 282:501 */       options.add(((AttributeSpec)this.m_attributeSpecs.get(i)).toStringInternal());
/* 283:    */     }
/* 284:504 */     if (options.size() == 0) {
/* 285:505 */       return new String[0];
/* 286:    */     }
/* 287:508 */     return (String[])options.toArray(new String[1]);
/* 288:    */   }
/* 289:    */   
/* 290:    */   public void addAttributeSpec(String spec)
/* 291:    */   {
/* 292:517 */     AttributeSpec newSpec = new AttributeSpec(spec);
/* 293:518 */     this.m_attributeSpecs.add(newSpec);
/* 294:    */   }
/* 295:    */   
/* 296:    */   public String attributeSpecsTipText()
/* 297:    */   {
/* 298:528 */     return "Specifications of the new attributes to create";
/* 299:    */   }
/* 300:    */   
/* 301:    */   public void setAttributeSpecs(List<AttributeSpec> specs)
/* 302:    */   {
/* 303:537 */     this.m_attributeSpecs = specs;
/* 304:    */   }
/* 305:    */   
/* 306:    */   public List<AttributeSpec> getAttributeSpecs()
/* 307:    */   {
/* 308:546 */     return this.m_attributeSpecs;
/* 309:    */   }
/* 310:    */   
/* 311:    */   public void setEnvironment(Environment env)
/* 312:    */   {
/* 313:556 */     this.m_env = env;
/* 314:    */   }
/* 315:    */   
/* 316:    */   public boolean setInputFormat(Instances instanceInfo)
/* 317:    */     throws Exception
/* 318:    */   {
/* 319:570 */     super.setInputFormat(instanceInfo);
/* 320:    */     
/* 321:572 */     setOutputFormat();
/* 322:    */     
/* 323:574 */     return true;
/* 324:    */   }
/* 325:    */   
/* 326:    */   public boolean input(Instance instance)
/* 327:    */   {
/* 328:589 */     if (getInputFormat() == null) {
/* 329:590 */       throw new IllegalStateException("No input instance format defined");
/* 330:    */     }
/* 331:592 */     if (this.m_NewBatch)
/* 332:    */     {
/* 333:593 */       resetQueue();
/* 334:594 */       this.m_NewBatch = false;
/* 335:    */     }
/* 336:597 */     if (outputFormatPeek() == null) {
/* 337:598 */       setOutputFormat();
/* 338:    */     }
/* 339:601 */     Instance inst = (Instance)instance.copy();
/* 340:    */     
/* 341:    */ 
/* 342:604 */     copyValues(inst, true, inst.dataset(), outputFormatPeek());
/* 343:    */     
/* 344:606 */     convertInstance(inst);
/* 345:607 */     return true;
/* 346:    */   }
/* 347:    */   
/* 348:    */   protected void convertInstance(Instance instance)
/* 349:    */   {
/* 350:616 */     double[] vals = new double[outputFormatPeek().numAttributes()];
/* 351:619 */     for (int i = 0; i < instance.numAttributes(); i++) {
/* 352:620 */       vals[i] = instance.value(i);
/* 353:    */     }
/* 354:624 */     Instances outputFormat = getOutputFormat();
/* 355:625 */     for (int i = instance.numAttributes(); i < outputFormatPeek().numAttributes(); i++)
/* 356:    */     {
/* 357:627 */       AttributeSpec spec = (AttributeSpec)this.m_attributeSpecs.get(i - instance.numAttributes());
/* 358:628 */       Attribute outAtt = outputFormat.attribute(i);
/* 359:629 */       if (outAtt.isDate())
/* 360:    */       {
/* 361:630 */         vals[i] = spec.getDateValue().getTime();
/* 362:    */       }
/* 363:631 */       else if (outAtt.isNumeric())
/* 364:    */       {
/* 365:632 */         vals[i] = spec.getNumericValue();
/* 366:    */       }
/* 367:633 */       else if (outAtt.isNominal())
/* 368:    */       {
/* 369:634 */         String nomVal = spec.getNominalOrStringValue();
/* 370:635 */         vals[i] = outAtt.indexOfValue(nomVal);
/* 371:    */       }
/* 372:    */       else
/* 373:    */       {
/* 374:638 */         String nomVal = spec.getNominalOrStringValue();
/* 375:639 */         vals[i] = outAtt.addStringValue(nomVal);
/* 376:    */       }
/* 377:    */     }
/* 378:643 */     Instance inst = null;
/* 379:644 */     if ((instance instanceof SparseInstance)) {
/* 380:645 */       inst = new SparseInstance(instance.weight(), vals);
/* 381:    */     } else {
/* 382:647 */       inst = new DenseInstance(instance.weight(), vals);
/* 383:    */     }
/* 384:649 */     inst.setDataset(outputFormat);
/* 385:650 */     push(inst, false);
/* 386:    */   }
/* 387:    */   
/* 388:    */   protected void setOutputFormat()
/* 389:    */   {
/* 390:657 */     if (this.m_env == null) {
/* 391:658 */       this.m_env = Environment.getSystemWide();
/* 392:    */     }
/* 393:661 */     Instances inputF = getInputFormat();
/* 394:662 */     ArrayList<Attribute> newAtts = new ArrayList();
/* 395:665 */     for (int i = 0; i < inputF.numAttributes(); i++) {
/* 396:666 */       newAtts.add((Attribute)inputF.attribute(i).copy());
/* 397:    */     }
/* 398:670 */     for (int i = 0; i < this.m_attributeSpecs.size(); i++)
/* 399:    */     {
/* 400:671 */       AttributeSpec a = (AttributeSpec)this.m_attributeSpecs.get(i);
/* 401:672 */       a.init(this.m_env);
/* 402:    */       
/* 403:674 */       String type = a.getResolvedType();
/* 404:675 */       Attribute newAtt = null;
/* 405:676 */       if (type.toLowerCase().startsWith("date"))
/* 406:    */       {
/* 407:677 */         String format = a.getDateFormat();
/* 408:678 */         if (format == null) {
/* 409:679 */           format = "yyyy-MM-dd'T'HH:mm:ss";
/* 410:    */         }
/* 411:681 */         newAtt = new Attribute(a.getResolvedName(), format);
/* 412:    */       }
/* 413:682 */       else if (type.toLowerCase().startsWith("string"))
/* 414:    */       {
/* 415:683 */         newAtt = new Attribute(a.getResolvedName(), (List)null);
/* 416:    */       }
/* 417:684 */       else if (type.toLowerCase().startsWith("nominal"))
/* 418:    */       {
/* 419:685 */         List<String> vals = new ArrayList();
/* 420:686 */         vals.add(a.getResolvedValue());
/* 421:687 */         newAtt = new Attribute(a.getResolvedName(), vals);
/* 422:    */       }
/* 423:    */       else
/* 424:    */       {
/* 425:690 */         newAtt = new Attribute(a.getResolvedName());
/* 426:    */       }
/* 427:693 */       newAtts.add(newAtt);
/* 428:    */     }
/* 429:696 */     Instances outputFormat = new Instances(inputF.relationName(), newAtts, 0);
/* 430:697 */     outputFormat.setClassIndex(inputF.classIndex());
/* 431:698 */     setOutputFormat(outputFormat);
/* 432:    */   }
/* 433:    */   
/* 434:    */   public static void main(String[] argv)
/* 435:    */   {
/* 436:707 */     runFilter(new AddUserFields(), argv);
/* 437:    */   }
/* 438:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.AddUserFields
 * JD-Core Version:    0.7.0.1
 */