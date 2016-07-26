/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.io.PrintWriter;
/*   6:    */ import java.util.Collections;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Capabilities.Capability;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.Option;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SingleIndex;
/*  17:    */ import weka.core.Utils;
/*  18:    */ 
/*  19:    */ public class SVMLightSaver
/*  20:    */   extends AbstractFileSaver
/*  21:    */   implements BatchConverter, IncrementalConverter
/*  22:    */ {
/*  23:    */   private static final long serialVersionUID = 2605714599263995835L;
/*  24: 81 */   public static String FILE_EXTENSION = SVMLightLoader.FILE_EXTENSION;
/*  25: 84 */   public static int MAX_DIGITS = 18;
/*  26: 87 */   protected SingleIndex m_ClassIndex = new SingleIndex("last");
/*  27:    */   
/*  28:    */   public SVMLightSaver()
/*  29:    */   {
/*  30: 93 */     resetOptions();
/*  31:    */   }
/*  32:    */   
/*  33:    */   public String globalInfo()
/*  34:    */   {
/*  35:103 */     return "Writes to a destination that is in svm light format.\n\nFor more information about svm light see:\n\nhttp://svmlight.joachims.org/";
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Enumeration<Option> listOptions()
/*  39:    */   {
/*  40:115 */     Vector<Option> result = new Vector();
/*  41:    */     
/*  42:117 */     result.addElement(new Option("\tThe class index\n\t(default: last)", "c", 1, "-c <class index>"));
/*  43:    */     
/*  44:    */ 
/*  45:120 */     result.addAll(Collections.list(super.listOptions()));
/*  46:    */     
/*  47:122 */     return result.elements();
/*  48:    */   }
/*  49:    */   
/*  50:    */   public String[] getOptions()
/*  51:    */   {
/*  52:133 */     Vector<String> result = new Vector();
/*  53:    */     
/*  54:135 */     result.add("-c");
/*  55:136 */     result.add(getClassIndex());
/*  56:    */     
/*  57:138 */     Collections.addAll(result, super.getOptions());
/*  58:    */     
/*  59:140 */     return (String[])result.toArray(new String[result.size()]);
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setOptions(String[] options)
/*  63:    */     throws Exception
/*  64:    */   {
/*  65:175 */     String tmpStr = Utils.getOption('c', options);
/*  66:176 */     if (tmpStr.length() != 0) {
/*  67:177 */       setClassIndex(tmpStr);
/*  68:    */     } else {
/*  69:179 */       setClassIndex("last");
/*  70:    */     }
/*  71:182 */     super.setOptions(options);
/*  72:    */     
/*  73:184 */     Utils.checkForRemainingOptions(options);
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String getFileDescription()
/*  77:    */   {
/*  78:194 */     return "svm light data files";
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void resetOptions()
/*  82:    */   {
/*  83:202 */     super.resetOptions();
/*  84:203 */     setFileExtension(SVMLightLoader.FILE_EXTENSION);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public String classIndexTipText()
/*  88:    */   {
/*  89:213 */     return "Sets the class index (\"first\" and \"last\" are valid values)";
/*  90:    */   }
/*  91:    */   
/*  92:    */   public String getClassIndex()
/*  93:    */   {
/*  94:222 */     return this.m_ClassIndex.getSingleIndex();
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void setClassIndex(String value)
/*  98:    */   {
/*  99:231 */     this.m_ClassIndex.setSingleIndex(value);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public Capabilities getCapabilities()
/* 103:    */   {
/* 104:242 */     Capabilities result = super.getCapabilities();
/* 105:    */     
/* 106:    */ 
/* 107:245 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 108:246 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 109:247 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 110:    */     
/* 111:    */ 
/* 112:250 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/* 113:251 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 114:252 */     result.enable(Capabilities.Capability.DATE_CLASS);
/* 115:    */     
/* 116:254 */     return result;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void setInstances(Instances instances)
/* 120:    */   {
/* 121:264 */     this.m_ClassIndex.setUpper(instances.numAttributes() - 1);
/* 122:265 */     instances.setClassIndex(this.m_ClassIndex.getIndex());
/* 123:    */     
/* 124:267 */     super.setInstances(instances);
/* 125:    */   }
/* 126:    */   
/* 127:    */   protected String instanceToSvmlight(Instance inst)
/* 128:    */   {
/* 129:280 */     StringBuffer result = new StringBuffer();
/* 130:283 */     if (inst.classAttribute().isNominal())
/* 131:    */     {
/* 132:284 */       if (inst.classValue() == 0.0D) {
/* 133:285 */         result.append("1");
/* 134:286 */       } else if (inst.classValue() == 1.0D) {
/* 135:287 */         result.append("-1");
/* 136:    */       }
/* 137:    */     }
/* 138:    */     else {
/* 139:290 */       result.append("" + Utils.doubleToString(inst.classValue(), MAX_DIGITS));
/* 140:    */     }
/* 141:294 */     for (int i = 0; i < inst.numAttributes(); i++) {
/* 142:295 */       if (i != inst.classIndex()) {
/* 143:298 */         if (inst.value(i) != 0.0D) {
/* 144:301 */           result.append(" " + (i + 1) + ":" + Utils.doubleToString(inst.value(i), MAX_DIGITS));
/* 145:    */         }
/* 146:    */       }
/* 147:    */     }
/* 148:305 */     return result.toString();
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void writeIncremental(Instance inst)
/* 152:    */     throws IOException
/* 153:    */   {
/* 154:318 */     int writeMode = getWriteMode();
/* 155:319 */     Instances structure = getInstances();
/* 156:320 */     PrintWriter outW = null;
/* 157:322 */     if ((getRetrieval() == 1) || (getRetrieval() == 0)) {
/* 158:323 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/* 159:    */     }
/* 160:326 */     if (getWriter() != null) {
/* 161:327 */       outW = new PrintWriter(getWriter());
/* 162:    */     }
/* 163:330 */     if (writeMode == 1)
/* 164:    */     {
/* 165:331 */       if (structure == null)
/* 166:    */       {
/* 167:332 */         setWriteMode(2);
/* 168:333 */         if (inst != null) {
/* 169:334 */           System.err.println("Structure (Header Information) has to be set in advance");
/* 170:    */         }
/* 171:    */       }
/* 172:    */       else
/* 173:    */       {
/* 174:338 */         setWriteMode(3);
/* 175:    */       }
/* 176:340 */       writeMode = getWriteMode();
/* 177:    */     }
/* 178:343 */     if (writeMode == 2)
/* 179:    */     {
/* 180:344 */       if (outW != null) {
/* 181:345 */         outW.close();
/* 182:    */       }
/* 183:347 */       cancel();
/* 184:    */     }
/* 185:351 */     if (writeMode == 3)
/* 186:    */     {
/* 187:352 */       setWriteMode(0);
/* 188:    */       
/* 189:354 */       writeMode = getWriteMode();
/* 190:    */     }
/* 191:358 */     if (writeMode == 0)
/* 192:    */     {
/* 193:359 */       if (structure == null) {
/* 194:360 */         throw new IOException("No instances information available.");
/* 195:    */       }
/* 196:363 */       if (inst != null)
/* 197:    */       {
/* 198:365 */         if ((retrieveFile() == null) && (outW == null))
/* 199:    */         {
/* 200:366 */           System.out.println(instanceToSvmlight(inst));
/* 201:    */         }
/* 202:    */         else
/* 203:    */         {
/* 204:368 */           outW.println(instanceToSvmlight(inst));
/* 205:369 */           this.m_incrementalCounter += 1;
/* 206:371 */           if (this.m_incrementalCounter > 100)
/* 207:    */           {
/* 208:372 */             this.m_incrementalCounter = 0;
/* 209:373 */             outW.flush();
/* 210:    */           }
/* 211:    */         }
/* 212:    */       }
/* 213:    */       else
/* 214:    */       {
/* 215:378 */         if (outW != null)
/* 216:    */         {
/* 217:379 */           outW.flush();
/* 218:380 */           outW.close();
/* 219:    */         }
/* 220:382 */         this.m_incrementalCounter = 0;
/* 221:383 */         resetStructure();
/* 222:384 */         outW = null;
/* 223:385 */         resetWriter();
/* 224:    */       }
/* 225:    */     }
/* 226:    */   }
/* 227:    */   
/* 228:    */   public void writeBatch()
/* 229:    */     throws IOException
/* 230:    */   {
/* 231:398 */     if (getInstances() == null) {
/* 232:399 */       throw new IOException("No instances to save");
/* 233:    */     }
/* 234:402 */     if (getRetrieval() == 2) {
/* 235:403 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/* 236:    */     }
/* 237:406 */     setRetrieval(1);
/* 238:407 */     setWriteMode(0);
/* 239:409 */     if ((retrieveFile() == null) && (getWriter() == null))
/* 240:    */     {
/* 241:410 */       for (int i = 0; i < getInstances().numInstances(); i++) {
/* 242:411 */         System.out.println(instanceToSvmlight(getInstances().instance(i)));
/* 243:    */       }
/* 244:413 */       setWriteMode(1);
/* 245:    */     }
/* 246:    */     else
/* 247:    */     {
/* 248:415 */       PrintWriter outW = new PrintWriter(getWriter());
/* 249:416 */       for (int i = 0; i < getInstances().numInstances(); i++) {
/* 250:417 */         outW.println(instanceToSvmlight(getInstances().instance(i)));
/* 251:    */       }
/* 252:419 */       outW.flush();
/* 253:420 */       outW.close();
/* 254:421 */       setWriteMode(1);
/* 255:422 */       outW = null;
/* 256:423 */       resetWriter();
/* 257:424 */       setWriteMode(2);
/* 258:    */     }
/* 259:    */   }
/* 260:    */   
/* 261:    */   public String getRevision()
/* 262:    */   {
/* 263:435 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 264:    */   }
/* 265:    */   
/* 266:    */   public static void main(String[] args)
/* 267:    */   {
/* 268:444 */     runFileSaver(new SVMLightSaver(), args);
/* 269:    */   }
/* 270:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.SVMLightSaver
 * JD-Core Version:    0.7.0.1
 */