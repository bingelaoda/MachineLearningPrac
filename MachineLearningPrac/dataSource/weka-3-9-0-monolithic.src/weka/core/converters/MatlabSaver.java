/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.io.PrintWriter;
/*   6:    */ import java.text.DecimalFormat;
/*   7:    */ import java.text.NumberFormat;
/*   8:    */ import java.util.Collections;
/*   9:    */ import java.util.Date;
/*  10:    */ import java.util.Enumeration;
/*  11:    */ import java.util.Locale;
/*  12:    */ import java.util.Vector;
/*  13:    */ import weka.core.Attribute;
/*  14:    */ import weka.core.Capabilities;
/*  15:    */ import weka.core.Capabilities.Capability;
/*  16:    */ import weka.core.Instance;
/*  17:    */ import weka.core.Instances;
/*  18:    */ import weka.core.Option;
/*  19:    */ import weka.core.RevisionUtils;
/*  20:    */ import weka.core.Utils;
/*  21:    */ import weka.core.Version;
/*  22:    */ 
/*  23:    */ public class MatlabSaver
/*  24:    */   extends AbstractFileSaver
/*  25:    */   implements BatchConverter, IncrementalConverter
/*  26:    */ {
/*  27:    */   private static final long serialVersionUID = 4118356803697172614L;
/*  28: 87 */   public static String FILE_EXTENSION = MatlabLoader.FILE_EXTENSION;
/*  29:    */   protected boolean m_UseDouble;
/*  30:    */   protected boolean m_UseTabs;
/*  31:    */   protected boolean m_HeaderWritten;
/*  32:    */   protected DecimalFormat m_Format;
/*  33:    */   
/*  34:    */   public MatlabSaver()
/*  35:    */   {
/*  36:105 */     resetOptions();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public String globalInfo()
/*  40:    */   {
/*  41:115 */     return "Writes Matlab ASCII files, in single or double precision format.";
/*  42:    */   }
/*  43:    */   
/*  44:    */   public Enumeration<Option> listOptions()
/*  45:    */   {
/*  46:125 */     Vector<Option> result = new Vector();
/*  47:    */     
/*  48:127 */     result.addElement(new Option("\tUse double precision format.\n\t(default: single precision)", "double", 0, "-double"));
/*  49:    */     
/*  50:    */ 
/*  51:130 */     result.addElement(new Option("\tUse tabs as separator.\n\t(default: blanks)", "tabs", 0, "-tabs"));
/*  52:    */     
/*  53:    */ 
/*  54:133 */     result.addAll(Collections.list(super.listOptions()));
/*  55:    */     
/*  56:135 */     return result.elements();
/*  57:    */   }
/*  58:    */   
/*  59:    */   public String[] getOptions()
/*  60:    */   {
/*  61:146 */     Vector<String> result = new Vector();
/*  62:148 */     if (getUseDouble()) {
/*  63:149 */       result.add("-double");
/*  64:    */     }
/*  65:152 */     if (getUseTabs()) {
/*  66:153 */       result.add("-tabs");
/*  67:    */     }
/*  68:156 */     Collections.addAll(result, super.getOptions());
/*  69:    */     
/*  70:158 */     return (String[])result.toArray(new String[result.size()]);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setOptions(String[] options)
/*  74:    */     throws Exception
/*  75:    */   {
/*  76:198 */     setUseDouble(Utils.getFlag("double", options));
/*  77:    */     
/*  78:200 */     setUseTabs(Utils.getFlag("tabs", options));
/*  79:    */     
/*  80:202 */     super.setOptions(options);
/*  81:    */     
/*  82:204 */     Utils.checkForRemainingOptions(options);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public String getFileDescription()
/*  86:    */   {
/*  87:214 */     return "Matlab ASCII files";
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void resetOptions()
/*  91:    */   {
/*  92:222 */     super.resetOptions();
/*  93:    */     
/*  94:224 */     setFileExtension(MatlabLoader.FILE_EXTENSION);
/*  95:225 */     setUseDouble(false);
/*  96:226 */     setUseTabs(false);
/*  97:    */     
/*  98:228 */     this.m_HeaderWritten = false;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void setUseDouble(boolean value)
/* 102:    */   {
/* 103:237 */     this.m_UseDouble = value;
/* 104:    */     
/* 105:239 */     this.m_Format = ((DecimalFormat)NumberFormat.getInstance(Locale.US));
/* 106:240 */     if (this.m_UseDouble) {
/* 107:243 */       this.m_Format.applyPattern("   0.0000000000000000E00;  -0.0000000000000000E00");
/* 108:    */     } else {
/* 109:246 */       this.m_Format.applyPattern("   0.00000000E00;  -0.00000000E00");
/* 110:    */     }
/* 111:    */   }
/* 112:    */   
/* 113:    */   public boolean getUseDouble()
/* 114:    */   {
/* 115:256 */     return this.m_UseDouble;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public String useDoubleTipText()
/* 119:    */   {
/* 120:266 */     return "Sets whether to use double instead of single precision.";
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void setUseTabs(boolean value)
/* 124:    */   {
/* 125:275 */     this.m_UseTabs = value;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public boolean getUseTabs()
/* 129:    */   {
/* 130:284 */     return this.m_UseTabs;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public String useTabsTipText()
/* 134:    */   {
/* 135:294 */     return "Sets whether to use tabs as separators instead of blanks.";
/* 136:    */   }
/* 137:    */   
/* 138:    */   public Capabilities getCapabilities()
/* 139:    */   {
/* 140:305 */     Capabilities result = super.getCapabilities();
/* 141:    */     
/* 142:    */ 
/* 143:308 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 144:    */     
/* 145:    */ 
/* 146:311 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 147:312 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 148:    */     
/* 149:314 */     return result;
/* 150:    */   }
/* 151:    */   
/* 152:    */   protected String matlabHeader()
/* 153:    */   {
/* 154:326 */     StringBuffer result = new StringBuffer();
/* 155:327 */     result.append("% Relation: " + getInstances().relationName() + "\n");
/* 156:328 */     result.append("% Generated on: " + new Date() + "\n");
/* 157:329 */     result.append("% Generated by: WEKA " + Version.VERSION + "\n");
/* 158:330 */     result.append("%\n");
/* 159:    */     
/* 160:332 */     result.append("%  ");
/* 161:333 */     for (int i = 0; i < getInstances().numAttributes(); i++)
/* 162:    */     {
/* 163:334 */       if (i > 0) {
/* 164:335 */         result.append(this.m_UseTabs ? "\t   " : "    ");
/* 165:    */       }
/* 166:337 */       result.append(Utils.padRight(getInstances().attribute(i).name(), (this.m_UseDouble ? 16 : 8) + 5));
/* 167:    */     }
/* 168:341 */     return result.toString();
/* 169:    */   }
/* 170:    */   
/* 171:    */   protected String instanceToMatlab(Instance inst)
/* 172:    */   {
/* 173:354 */     StringBuffer result = new StringBuffer();
/* 174:357 */     for (int i = 0; i < inst.numAttributes(); i++)
/* 175:    */     {
/* 176:358 */       if (i > 0) {
/* 177:359 */         result.append(this.m_UseTabs ? "\t" : " ");
/* 178:    */       }
/* 179:361 */       result.append(this.m_Format.format(inst.value(i)));
/* 180:    */     }
/* 181:364 */     return result.toString();
/* 182:    */   }
/* 183:    */   
/* 184:    */   public void writeIncremental(Instance inst)
/* 185:    */     throws IOException
/* 186:    */   {
/* 187:377 */     int writeMode = getWriteMode();
/* 188:378 */     Instances structure = getInstances();
/* 189:379 */     PrintWriter outW = null;
/* 190:381 */     if ((getRetrieval() == 1) || (getRetrieval() == 0)) {
/* 191:382 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/* 192:    */     }
/* 193:385 */     if (getWriter() != null) {
/* 194:386 */       outW = new PrintWriter(getWriter());
/* 195:    */     }
/* 196:389 */     if (writeMode == 1)
/* 197:    */     {
/* 198:390 */       if (structure == null)
/* 199:    */       {
/* 200:391 */         setWriteMode(2);
/* 201:392 */         if (inst != null) {
/* 202:393 */           System.err.println("Structure (Header Information) has to be set in advance");
/* 203:    */         }
/* 204:    */       }
/* 205:    */       else
/* 206:    */       {
/* 207:397 */         setWriteMode(3);
/* 208:    */       }
/* 209:399 */       writeMode = getWriteMode();
/* 210:    */     }
/* 211:402 */     if (writeMode == 2)
/* 212:    */     {
/* 213:403 */       if (outW != null) {
/* 214:404 */         outW.close();
/* 215:    */       }
/* 216:406 */       cancel();
/* 217:    */     }
/* 218:410 */     if (writeMode == 3)
/* 219:    */     {
/* 220:411 */       setWriteMode(0);
/* 221:412 */       if ((retrieveFile() == null) && (outW == null)) {
/* 222:413 */         System.out.println(matlabHeader());
/* 223:    */       } else {
/* 224:415 */         outW.println(matlabHeader());
/* 225:    */       }
/* 226:417 */       writeMode = getWriteMode();
/* 227:    */     }
/* 228:421 */     if (writeMode == 0)
/* 229:    */     {
/* 230:422 */       if (structure == null) {
/* 231:423 */         throw new IOException("No instances information available.");
/* 232:    */       }
/* 233:426 */       if (inst != null)
/* 234:    */       {
/* 235:428 */         if ((retrieveFile() == null) && (outW == null))
/* 236:    */         {
/* 237:429 */           System.out.println(instanceToMatlab(inst));
/* 238:    */         }
/* 239:    */         else
/* 240:    */         {
/* 241:431 */           outW.println(instanceToMatlab(inst));
/* 242:432 */           this.m_incrementalCounter += 1;
/* 243:434 */           if (this.m_incrementalCounter > 100)
/* 244:    */           {
/* 245:435 */             this.m_incrementalCounter = 0;
/* 246:436 */             outW.flush();
/* 247:    */           }
/* 248:    */         }
/* 249:    */       }
/* 250:    */       else
/* 251:    */       {
/* 252:441 */         if (outW != null)
/* 253:    */         {
/* 254:442 */           outW.flush();
/* 255:443 */           outW.close();
/* 256:    */         }
/* 257:445 */         this.m_incrementalCounter = 0;
/* 258:446 */         resetStructure();
/* 259:447 */         outW = null;
/* 260:448 */         resetWriter();
/* 261:    */       }
/* 262:    */     }
/* 263:    */   }
/* 264:    */   
/* 265:    */   public void writeBatch()
/* 266:    */     throws IOException
/* 267:    */   {
/* 268:461 */     if (getInstances() == null) {
/* 269:462 */       throw new IOException("No instances to save");
/* 270:    */     }
/* 271:465 */     if (getRetrieval() == 2) {
/* 272:466 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/* 273:    */     }
/* 274:469 */     setRetrieval(1);
/* 275:470 */     setWriteMode(0);
/* 276:472 */     if ((retrieveFile() == null) && (getWriter() == null))
/* 277:    */     {
/* 278:473 */       System.out.println(matlabHeader());
/* 279:474 */       for (int i = 0; i < getInstances().numInstances(); i++) {
/* 280:475 */         System.out.println(instanceToMatlab(getInstances().instance(i)));
/* 281:    */       }
/* 282:477 */       setWriteMode(1);
/* 283:    */     }
/* 284:    */     else
/* 285:    */     {
/* 286:479 */       PrintWriter outW = new PrintWriter(getWriter());
/* 287:480 */       outW.println(matlabHeader());
/* 288:481 */       for (int i = 0; i < getInstances().numInstances(); i++) {
/* 289:482 */         outW.println(instanceToMatlab(getInstances().instance(i)));
/* 290:    */       }
/* 291:484 */       outW.flush();
/* 292:485 */       outW.close();
/* 293:486 */       setWriteMode(1);
/* 294:487 */       outW = null;
/* 295:488 */       resetWriter();
/* 296:489 */       setWriteMode(2);
/* 297:    */     }
/* 298:    */   }
/* 299:    */   
/* 300:    */   public String getRevision()
/* 301:    */   {
/* 302:500 */     return RevisionUtils.extract("$Revision: 11211 $");
/* 303:    */   }
/* 304:    */   
/* 305:    */   public static void main(String[] args)
/* 306:    */   {
/* 307:509 */     runFileSaver(new MatlabSaver(), args);
/* 308:    */   }
/* 309:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.MatlabSaver
 * JD-Core Version:    0.7.0.1
 */