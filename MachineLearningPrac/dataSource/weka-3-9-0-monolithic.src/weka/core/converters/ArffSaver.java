/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.OutputStream;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.io.PrintWriter;
/*   8:    */ import java.util.Collections;
/*   9:    */ import java.util.Enumeration;
/*  10:    */ import java.util.Vector;
/*  11:    */ import java.util.zip.GZIPOutputStream;
/*  12:    */ import weka.core.AbstractInstance;
/*  13:    */ import weka.core.Capabilities;
/*  14:    */ import weka.core.Capabilities.Capability;
/*  15:    */ import weka.core.Instance;
/*  16:    */ import weka.core.Instances;
/*  17:    */ import weka.core.Option;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.Utils;
/*  20:    */ 
/*  21:    */ public class ArffSaver
/*  22:    */   extends AbstractFileSaver
/*  23:    */   implements BatchConverter, IncrementalConverter
/*  24:    */ {
/*  25:    */   static final long serialVersionUID = 2223634248900042228L;
/*  26: 84 */   protected boolean m_CompressOutput = false;
/*  27: 87 */   protected int m_MaxDecimalPlaces = AbstractInstance.s_numericAfterDecimalPoint;
/*  28:    */   
/*  29:    */   public ArffSaver()
/*  30:    */   {
/*  31: 92 */     resetOptions();
/*  32:    */   }
/*  33:    */   
/*  34:    */   public Enumeration<Option> listOptions()
/*  35:    */   {
/*  36:102 */     Vector<Option> result = new Vector();
/*  37:    */     
/*  38:104 */     result.addElement(new Option("\tCompresses the data (uses '" + ArffLoader.FILE_EXTENSION_COMPRESSED + "' as extension instead of '" + ArffLoader.FILE_EXTENSION + "')\n" + "\t(default: off)", "compress", 0, "-compress"));
/*  39:    */     
/*  40:    */ 
/*  41:    */ 
/*  42:    */ 
/*  43:109 */     result.addElement(new Option("\tThe maximum number of digits to print after the decimal\n\tplace for numeric values (default: 6)", "decimal", 1, "-decimal <num>"));
/*  44:    */     
/*  45:    */ 
/*  46:    */ 
/*  47:    */ 
/*  48:114 */     result.addAll(Collections.list(super.listOptions()));
/*  49:    */     
/*  50:116 */     return result.elements();
/*  51:    */   }
/*  52:    */   
/*  53:    */   public String[] getOptions()
/*  54:    */   {
/*  55:127 */     Vector<String> result = new Vector();
/*  56:129 */     if (getCompressOutput()) {
/*  57:130 */       result.add("-compress");
/*  58:    */     }
/*  59:133 */     result.add("-decimal");
/*  60:134 */     result.add("" + getMaxDecimalPlaces());
/*  61:    */     
/*  62:136 */     Collections.addAll(result, super.getOptions());
/*  63:    */     
/*  64:138 */     return (String[])result.toArray(new String[result.size()]);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setOptions(String[] options)
/*  68:    */     throws Exception
/*  69:    */   {
/*  70:178 */     setCompressOutput(Utils.getFlag("compress", options));
/*  71:    */     
/*  72:180 */     String tmpStr = Utils.getOption("decimal", options);
/*  73:181 */     if (tmpStr.length() > 0) {
/*  74:182 */       setMaxDecimalPlaces(Integer.parseInt(tmpStr));
/*  75:    */     }
/*  76:185 */     super.setOptions(options);
/*  77:    */     
/*  78:187 */     Utils.checkForRemainingOptions(options);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void setMaxDecimalPlaces(int maxDecimal)
/*  82:    */   {
/*  83:196 */     this.m_MaxDecimalPlaces = maxDecimal;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public int getMaxDecimalPlaces()
/*  87:    */   {
/*  88:205 */     return this.m_MaxDecimalPlaces;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public String maxDecimalPlacesTipText()
/*  92:    */   {
/*  93:215 */     return "The maximum number of digits to print after the decimal point for numeric values";
/*  94:    */   }
/*  95:    */   
/*  96:    */   public String compressOutputTipText()
/*  97:    */   {
/*  98:226 */     return "Optional compression of the output data";
/*  99:    */   }
/* 100:    */   
/* 101:    */   public boolean getCompressOutput()
/* 102:    */   {
/* 103:235 */     return this.m_CompressOutput;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public void setCompressOutput(boolean value)
/* 107:    */   {
/* 108:244 */     this.m_CompressOutput = value;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public String globalInfo()
/* 112:    */   {
/* 113:254 */     return "Writes to a destination that is in arff (attribute relation file format) format. The data can be compressed with gzip in order to save space.";
/* 114:    */   }
/* 115:    */   
/* 116:    */   public String getFileDescription()
/* 117:    */   {
/* 118:265 */     return "Arff data files";
/* 119:    */   }
/* 120:    */   
/* 121:    */   public String[] getFileExtensions()
/* 122:    */   {
/* 123:275 */     return new String[] { ArffLoader.FILE_EXTENSION, ArffLoader.FILE_EXTENSION_COMPRESSED };
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void setFile(File outputFile)
/* 127:    */     throws IOException
/* 128:    */   {
/* 129:287 */     if (outputFile.getAbsolutePath().endsWith(ArffLoader.FILE_EXTENSION_COMPRESSED)) {
/* 130:289 */       setCompressOutput(true);
/* 131:    */     }
/* 132:292 */     super.setFile(outputFile);
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void setDestination(OutputStream output)
/* 136:    */     throws IOException
/* 137:    */   {
/* 138:303 */     if (getCompressOutput()) {
/* 139:304 */       super.setDestination(new GZIPOutputStream(output));
/* 140:    */     } else {
/* 141:306 */       super.setDestination(output);
/* 142:    */     }
/* 143:    */   }
/* 144:    */   
/* 145:    */   public void resetOptions()
/* 146:    */   {
/* 147:316 */     super.resetOptions();
/* 148:317 */     setFileExtension(".arff");
/* 149:    */   }
/* 150:    */   
/* 151:    */   public Capabilities getCapabilities()
/* 152:    */   {
/* 153:328 */     Capabilities result = super.getCapabilities();
/* 154:    */     
/* 155:    */ 
/* 156:331 */     result.enableAllAttributes();
/* 157:332 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 158:    */     
/* 159:    */ 
/* 160:335 */     result.enableAllClasses();
/* 161:336 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 162:337 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 163:    */     
/* 164:339 */     return result;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public void writeIncremental(Instance inst)
/* 168:    */     throws IOException
/* 169:    */   {
/* 170:353 */     int writeMode = getWriteMode();
/* 171:354 */     Instances structure = getInstances();
/* 172:355 */     PrintWriter outW = null;
/* 173:357 */     if ((getRetrieval() == 1) || (getRetrieval() == 0)) {
/* 174:358 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/* 175:    */     }
/* 176:360 */     if (getWriter() != null) {
/* 177:361 */       outW = new PrintWriter(getWriter());
/* 178:    */     }
/* 179:364 */     if (writeMode == 1)
/* 180:    */     {
/* 181:365 */       if (structure == null)
/* 182:    */       {
/* 183:366 */         setWriteMode(2);
/* 184:367 */         if (inst != null) {
/* 185:368 */           System.err.println("Structure(Header Information) has to be set in advance");
/* 186:    */         }
/* 187:    */       }
/* 188:    */       else
/* 189:    */       {
/* 190:372 */         setWriteMode(3);
/* 191:    */       }
/* 192:374 */       writeMode = getWriteMode();
/* 193:    */     }
/* 194:376 */     if (writeMode == 2)
/* 195:    */     {
/* 196:377 */       if (outW != null) {
/* 197:378 */         outW.close();
/* 198:    */       }
/* 199:380 */       cancel();
/* 200:    */     }
/* 201:382 */     if (writeMode == 3)
/* 202:    */     {
/* 203:383 */       setWriteMode(0);
/* 204:    */       
/* 205:385 */       Instances header = new Instances(structure, 0);
/* 206:386 */       if ((retrieveFile() == null) && (outW == null))
/* 207:    */       {
/* 208:387 */         System.out.println(header.toString());
/* 209:    */       }
/* 210:    */       else
/* 211:    */       {
/* 212:389 */         outW.print(header.toString());
/* 213:390 */         outW.print("\n");
/* 214:391 */         outW.flush();
/* 215:    */       }
/* 216:393 */       writeMode = getWriteMode();
/* 217:    */     }
/* 218:395 */     if (writeMode == 0)
/* 219:    */     {
/* 220:396 */       if (structure == null) {
/* 221:397 */         throw new IOException("No instances information available.");
/* 222:    */       }
/* 223:399 */       if (inst != null)
/* 224:    */       {
/* 225:402 */         if ((retrieveFile() == null) && (outW == null))
/* 226:    */         {
/* 227:403 */           System.out.println(inst.toStringMaxDecimalDigits(this.m_MaxDecimalPlaces));
/* 228:    */         }
/* 229:    */         else
/* 230:    */         {
/* 231:405 */           outW.println(inst.toStringMaxDecimalDigits(this.m_MaxDecimalPlaces));
/* 232:406 */           this.m_incrementalCounter += 1;
/* 233:408 */           if (this.m_incrementalCounter > 100)
/* 234:    */           {
/* 235:409 */             this.m_incrementalCounter = 0;
/* 236:410 */             outW.flush();
/* 237:    */           }
/* 238:    */         }
/* 239:    */       }
/* 240:    */       else
/* 241:    */       {
/* 242:415 */         if (outW != null)
/* 243:    */         {
/* 244:416 */           outW.flush();
/* 245:417 */           outW.close();
/* 246:    */         }
/* 247:419 */         this.m_incrementalCounter = 0;
/* 248:420 */         resetStructure();
/* 249:421 */         outW = null;
/* 250:422 */         resetWriter();
/* 251:    */       }
/* 252:    */     }
/* 253:    */   }
/* 254:    */   
/* 255:    */   public void writeBatch()
/* 256:    */     throws IOException
/* 257:    */   {
/* 258:435 */     if (getInstances() == null) {
/* 259:436 */       throw new IOException("No instances to save");
/* 260:    */     }
/* 261:438 */     if (getRetrieval() == 2) {
/* 262:439 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/* 263:    */     }
/* 264:441 */     setRetrieval(1);
/* 265:442 */     setWriteMode(0);
/* 266:443 */     if ((retrieveFile() == null) && (getWriter() == null))
/* 267:    */     {
/* 268:444 */       Instances data = getInstances();
/* 269:445 */       System.out.println(new Instances(data, 0));
/* 270:446 */       for (int i = 0; i < data.numInstances(); i++) {
/* 271:447 */         System.out.println(data.instance(i).toStringMaxDecimalDigits(this.m_MaxDecimalPlaces));
/* 272:    */       }
/* 273:450 */       setWriteMode(1);
/* 274:451 */       return;
/* 275:    */     }
/* 276:454 */     PrintWriter outW = new PrintWriter(getWriter());
/* 277:455 */     Instances data = getInstances();
/* 278:    */     
/* 279:    */ 
/* 280:458 */     Instances header = new Instances(data, 0);
/* 281:459 */     outW.print(header.toString());
/* 282:462 */     for (int i = 0; i < data.numInstances(); i++)
/* 283:    */     {
/* 284:463 */       if (i % 1000 == 0) {
/* 285:464 */         outW.flush();
/* 286:    */       }
/* 287:466 */       outW.println(data.instance(i).toStringMaxDecimalDigits(this.m_MaxDecimalPlaces));
/* 288:    */     }
/* 289:469 */     outW.flush();
/* 290:470 */     outW.close();
/* 291:    */     
/* 292:472 */     setWriteMode(1);
/* 293:473 */     outW = null;
/* 294:474 */     resetWriter();
/* 295:475 */     setWriteMode(2);
/* 296:    */   }
/* 297:    */   
/* 298:    */   public String getRevision()
/* 299:    */   {
/* 300:485 */     return RevisionUtils.extract("$Revision: 11506 $");
/* 301:    */   }
/* 302:    */   
/* 303:    */   public static void main(String[] args)
/* 304:    */   {
/* 305:494 */     runFileSaver(new ArffSaver(), args);
/* 306:    */   }
/* 307:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.ArffSaver
 * JD-Core Version:    0.7.0.1
 */