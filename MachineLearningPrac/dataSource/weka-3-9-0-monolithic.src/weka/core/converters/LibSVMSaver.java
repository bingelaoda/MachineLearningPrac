/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.io.PrintWriter;
/*   6:    */ import java.util.Collections;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.SingleIndex;
/*  16:    */ import weka.core.Utils;
/*  17:    */ 
/*  18:    */ public class LibSVMSaver
/*  19:    */   extends AbstractFileSaver
/*  20:    */   implements BatchConverter, IncrementalConverter
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = 2792295817125694786L;
/*  23: 80 */   public static String FILE_EXTENSION = LibSVMLoader.FILE_EXTENSION;
/*  24: 83 */   protected SingleIndex m_ClassIndex = new SingleIndex("last");
/*  25:    */   
/*  26:    */   public LibSVMSaver()
/*  27:    */   {
/*  28: 89 */     resetOptions();
/*  29:    */   }
/*  30:    */   
/*  31:    */   public String globalInfo()
/*  32:    */   {
/*  33: 99 */     return "Writes to a destination that is in libsvm format.\n\nFor more information about libsvm see:\n\nhttp://www.csie.ntu.edu.tw/~cjlin/libsvm/";
/*  34:    */   }
/*  35:    */   
/*  36:    */   public Enumeration<Option> listOptions()
/*  37:    */   {
/*  38:111 */     Vector<Option> result = new Vector();
/*  39:    */     
/*  40:113 */     result.addElement(new Option("\tThe class index\n\t(default: last)", "c", 1, "-c <class index>"));
/*  41:    */     
/*  42:    */ 
/*  43:116 */     result.addAll(Collections.list(super.listOptions()));
/*  44:    */     
/*  45:118 */     return result.elements();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public String[] getOptions()
/*  49:    */   {
/*  50:129 */     Vector<String> result = new Vector();
/*  51:    */     
/*  52:131 */     result.add("-c");
/*  53:132 */     result.add(getClassIndex());
/*  54:    */     
/*  55:134 */     Collections.addAll(result, super.getOptions());
/*  56:    */     
/*  57:136 */     return (String[])result.toArray(new String[result.size()]);
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void setOptions(String[] options)
/*  61:    */     throws Exception
/*  62:    */   {
/*  63:171 */     String tmpStr = Utils.getOption('c', options);
/*  64:172 */     if (tmpStr.length() != 0) {
/*  65:173 */       setClassIndex(tmpStr);
/*  66:    */     } else {
/*  67:175 */       setClassIndex("last");
/*  68:    */     }
/*  69:178 */     super.setOptions(options);
/*  70:    */     
/*  71:180 */     Utils.checkForRemainingOptions(options);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public String getFileDescription()
/*  75:    */   {
/*  76:190 */     return "libsvm data files";
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void resetOptions()
/*  80:    */   {
/*  81:198 */     super.resetOptions();
/*  82:199 */     setFileExtension(LibSVMLoader.FILE_EXTENSION);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public String classIndexTipText()
/*  86:    */   {
/*  87:209 */     return "Sets the class index (\"first\" and \"last\" are valid values)";
/*  88:    */   }
/*  89:    */   
/*  90:    */   public String getClassIndex()
/*  91:    */   {
/*  92:218 */     return this.m_ClassIndex.getSingleIndex();
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void setClassIndex(String value)
/*  96:    */   {
/*  97:227 */     this.m_ClassIndex.setSingleIndex(value);
/*  98:    */   }
/*  99:    */   
/* 100:    */   public Capabilities getCapabilities()
/* 101:    */   {
/* 102:238 */     Capabilities result = super.getCapabilities();
/* 103:    */     
/* 104:    */ 
/* 105:241 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 106:242 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 107:243 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 108:    */     
/* 109:    */ 
/* 110:246 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 111:247 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 112:248 */     result.enable(Capabilities.Capability.DATE_CLASS);
/* 113:    */     
/* 114:250 */     return result;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void setInstances(Instances instances)
/* 118:    */   {
/* 119:260 */     this.m_ClassIndex.setUpper(instances.numAttributes() - 1);
/* 120:261 */     instances.setClassIndex(this.m_ClassIndex.getIndex());
/* 121:    */     
/* 122:263 */     super.setInstances(instances);
/* 123:    */   }
/* 124:    */   
/* 125:    */   protected String instanceToLibsvm(Instance inst)
/* 126:    */   {
/* 127:277 */     StringBuffer result = new StringBuffer("" + inst.classValue());
/* 128:280 */     for (int i = 0; i < inst.numAttributes(); i++) {
/* 129:281 */       if (i != inst.classIndex()) {
/* 130:284 */         if (inst.value(i) != 0.0D) {
/* 131:287 */           result.append(" " + (i + 1) + ":" + inst.value(i));
/* 132:    */         }
/* 133:    */       }
/* 134:    */     }
/* 135:290 */     return result.toString();
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void writeIncremental(Instance inst)
/* 139:    */     throws IOException
/* 140:    */   {
/* 141:303 */     int writeMode = getWriteMode();
/* 142:304 */     Instances structure = getInstances();
/* 143:305 */     PrintWriter outW = null;
/* 144:307 */     if ((getRetrieval() == 1) || (getRetrieval() == 0)) {
/* 145:308 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/* 146:    */     }
/* 147:311 */     if (getWriter() != null) {
/* 148:312 */       outW = new PrintWriter(getWriter());
/* 149:    */     }
/* 150:315 */     if (writeMode == 1)
/* 151:    */     {
/* 152:316 */       if (structure == null)
/* 153:    */       {
/* 154:317 */         setWriteMode(2);
/* 155:318 */         if (inst != null) {
/* 156:319 */           System.err.println("Structure (Header Information) has to be set in advance");
/* 157:    */         }
/* 158:    */       }
/* 159:    */       else
/* 160:    */       {
/* 161:323 */         setWriteMode(3);
/* 162:    */       }
/* 163:325 */       writeMode = getWriteMode();
/* 164:    */     }
/* 165:328 */     if (writeMode == 2)
/* 166:    */     {
/* 167:329 */       if (outW != null) {
/* 168:330 */         outW.close();
/* 169:    */       }
/* 170:332 */       cancel();
/* 171:    */     }
/* 172:336 */     if (writeMode == 3)
/* 173:    */     {
/* 174:337 */       setWriteMode(0);
/* 175:    */       
/* 176:339 */       writeMode = getWriteMode();
/* 177:    */     }
/* 178:343 */     if (writeMode == 0)
/* 179:    */     {
/* 180:344 */       if (structure == null) {
/* 181:345 */         throw new IOException("No instances information available.");
/* 182:    */       }
/* 183:348 */       if (inst != null)
/* 184:    */       {
/* 185:350 */         if ((retrieveFile() == null) && (outW == null))
/* 186:    */         {
/* 187:351 */           System.out.println(instanceToLibsvm(inst));
/* 188:    */         }
/* 189:    */         else
/* 190:    */         {
/* 191:353 */           outW.println(instanceToLibsvm(inst));
/* 192:354 */           this.m_incrementalCounter += 1;
/* 193:356 */           if (this.m_incrementalCounter > 100)
/* 194:    */           {
/* 195:357 */             this.m_incrementalCounter = 0;
/* 196:358 */             outW.flush();
/* 197:    */           }
/* 198:    */         }
/* 199:    */       }
/* 200:    */       else
/* 201:    */       {
/* 202:363 */         if (outW != null)
/* 203:    */         {
/* 204:364 */           outW.flush();
/* 205:365 */           outW.close();
/* 206:    */         }
/* 207:367 */         this.m_incrementalCounter = 0;
/* 208:368 */         resetStructure();
/* 209:369 */         outW = null;
/* 210:370 */         resetWriter();
/* 211:    */       }
/* 212:    */     }
/* 213:    */   }
/* 214:    */   
/* 215:    */   public void writeBatch()
/* 216:    */     throws IOException
/* 217:    */   {
/* 218:383 */     if (getInstances() == null) {
/* 219:384 */       throw new IOException("No instances to save");
/* 220:    */     }
/* 221:387 */     if (getRetrieval() == 2) {
/* 222:388 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/* 223:    */     }
/* 224:391 */     setRetrieval(1);
/* 225:392 */     setWriteMode(0);
/* 226:394 */     if ((retrieveFile() == null) && (getWriter() == null))
/* 227:    */     {
/* 228:395 */       for (int i = 0; i < getInstances().numInstances(); i++) {
/* 229:396 */         System.out.println(instanceToLibsvm(getInstances().instance(i)));
/* 230:    */       }
/* 231:398 */       setWriteMode(1);
/* 232:    */     }
/* 233:    */     else
/* 234:    */     {
/* 235:400 */       PrintWriter outW = new PrintWriter(getWriter());
/* 236:401 */       for (int i = 0; i < getInstances().numInstances(); i++) {
/* 237:402 */         outW.println(instanceToLibsvm(getInstances().instance(i)));
/* 238:    */       }
/* 239:404 */       outW.flush();
/* 240:405 */       outW.close();
/* 241:406 */       setWriteMode(1);
/* 242:407 */       outW = null;
/* 243:408 */       resetWriter();
/* 244:409 */       setWriteMode(2);
/* 245:    */     }
/* 246:    */   }
/* 247:    */   
/* 248:    */   public String getRevision()
/* 249:    */   {
/* 250:420 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 251:    */   }
/* 252:    */   
/* 253:    */   public static void main(String[] args)
/* 254:    */   {
/* 255:429 */     runFileSaver(new LibSVMSaver(), args);
/* 256:    */   }
/* 257:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.LibSVMSaver
 * JD-Core Version:    0.7.0.1
 */