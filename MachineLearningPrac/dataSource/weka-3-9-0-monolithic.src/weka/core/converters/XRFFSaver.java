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
/*  12:    */ import weka.core.Capabilities;
/*  13:    */ import weka.core.Capabilities.Capability;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ import weka.core.SingleIndex;
/*  18:    */ import weka.core.Utils;
/*  19:    */ import weka.core.xml.XMLInstances;
/*  20:    */ 
/*  21:    */ public class XRFFSaver
/*  22:    */   extends AbstractFileSaver
/*  23:    */   implements BatchConverter
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = -7226404765213522043L;
/*  26: 86 */   protected SingleIndex m_ClassIndex = new SingleIndex();
/*  27:    */   protected XMLInstances m_XMLInstances;
/*  28: 92 */   protected boolean m_CompressOutput = false;
/*  29:    */   
/*  30:    */   public XRFFSaver()
/*  31:    */   {
/*  32: 98 */     resetOptions();
/*  33:    */   }
/*  34:    */   
/*  35:    */   public String globalInfo()
/*  36:    */   {
/*  37:108 */     return "Writes to a destination that is in the XML version of the ARFF format. The data can be compressed with gzip, in order to save space.";
/*  38:    */   }
/*  39:    */   
/*  40:    */   public Enumeration<Option> listOptions()
/*  41:    */   {
/*  42:119 */     Vector<Option> result = new Vector();
/*  43:    */     
/*  44:121 */     result.addElement(new Option("\tThe class index (first and last are valid as well).\n\t(default: last)", "C", 1, "-C <class index>"));
/*  45:    */     
/*  46:    */ 
/*  47:    */ 
/*  48:125 */     result.addElement(new Option("\tCompresses the data (uses '" + XRFFLoader.FILE_EXTENSION_COMPRESSED + "' as extension instead of '" + XRFFLoader.FILE_EXTENSION + "')\n" + "\t(default: off)", "compress", 0, "-compress"));
/*  49:    */     
/*  50:    */ 
/*  51:    */ 
/*  52:    */ 
/*  53:130 */     result.addAll(Collections.list(super.listOptions()));
/*  54:    */     
/*  55:132 */     return result.elements();
/*  56:    */   }
/*  57:    */   
/*  58:    */   public String[] getOptions()
/*  59:    */   {
/*  60:143 */     Vector<String> result = new Vector();
/*  61:145 */     if (getClassIndex().length() != 0)
/*  62:    */     {
/*  63:146 */       result.add("-C");
/*  64:147 */       result.add(getClassIndex());
/*  65:    */     }
/*  66:150 */     if (getCompressOutput()) {
/*  67:151 */       result.add("-compress");
/*  68:    */     }
/*  69:154 */     Collections.addAll(result, super.getOptions());
/*  70:    */     
/*  71:156 */     return (String[])result.toArray(new String[result.size()]);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setOptions(String[] options)
/*  75:    */     throws Exception
/*  76:    */   {
/*  77:197 */     String tmpStr = Utils.getOption('C', options);
/*  78:198 */     if (tmpStr.length() != 0) {
/*  79:199 */       setClassIndex(tmpStr);
/*  80:    */     } else {
/*  81:201 */       setClassIndex("last");
/*  82:    */     }
/*  83:204 */     setCompressOutput(Utils.getFlag("compress", options));
/*  84:    */     
/*  85:206 */     super.setOptions(options);
/*  86:    */   }
/*  87:    */   
/*  88:    */   public String getFileDescription()
/*  89:    */   {
/*  90:216 */     return "XRFF data files";
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String[] getFileExtensions()
/*  94:    */   {
/*  95:226 */     return new String[] { XRFFLoader.FILE_EXTENSION, XRFFLoader.FILE_EXTENSION_COMPRESSED };
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void setFile(File outputFile)
/*  99:    */     throws IOException
/* 100:    */   {
/* 101:238 */     if (outputFile.getAbsolutePath().endsWith(XRFFLoader.FILE_EXTENSION_COMPRESSED)) {
/* 102:240 */       setCompressOutput(true);
/* 103:    */     }
/* 104:243 */     super.setFile(outputFile);
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void resetOptions()
/* 108:    */   {
/* 109:251 */     super.resetOptions();
/* 110:253 */     if (getCompressOutput()) {
/* 111:254 */       setFileExtension(XRFFLoader.FILE_EXTENSION_COMPRESSED);
/* 112:    */     } else {
/* 113:256 */       setFileExtension(XRFFLoader.FILE_EXTENSION);
/* 114:    */     }
/* 115:    */     try
/* 116:    */     {
/* 117:260 */       this.m_XMLInstances = new XMLInstances();
/* 118:    */     }
/* 119:    */     catch (Exception e)
/* 120:    */     {
/* 121:262 */       this.m_XMLInstances = null;
/* 122:    */     }
/* 123:    */   }
/* 124:    */   
/* 125:    */   public String classIndexTipText()
/* 126:    */   {
/* 127:273 */     return "Sets the class index (\"first\" and \"last\" are valid values)";
/* 128:    */   }
/* 129:    */   
/* 130:    */   public String getClassIndex()
/* 131:    */   {
/* 132:282 */     return this.m_ClassIndex.getSingleIndex();
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void setClassIndex(String value)
/* 136:    */   {
/* 137:291 */     this.m_ClassIndex.setSingleIndex(value);
/* 138:    */   }
/* 139:    */   
/* 140:    */   public String compressOutputTipText()
/* 141:    */   {
/* 142:301 */     return "Optional compression of the output data";
/* 143:    */   }
/* 144:    */   
/* 145:    */   public boolean getCompressOutput()
/* 146:    */   {
/* 147:310 */     return this.m_CompressOutput;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public void setCompressOutput(boolean value)
/* 151:    */   {
/* 152:319 */     this.m_CompressOutput = value;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public Capabilities getCapabilities()
/* 156:    */   {
/* 157:330 */     Capabilities result = super.getCapabilities();
/* 158:    */     
/* 159:    */ 
/* 160:333 */     result.enableAllAttributes();
/* 161:334 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 162:    */     
/* 163:    */ 
/* 164:337 */     result.enableAllClasses();
/* 165:338 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 166:339 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 167:    */     
/* 168:341 */     return result;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public void setInstances(Instances instances)
/* 172:    */   {
/* 173:351 */     if (this.m_ClassIndex.getSingleIndex().length() != 0)
/* 174:    */     {
/* 175:352 */       this.m_ClassIndex.setUpper(instances.numAttributes() - 1);
/* 176:353 */       instances.setClassIndex(this.m_ClassIndex.getIndex());
/* 177:    */     }
/* 178:356 */     super.setInstances(instances);
/* 179:    */   }
/* 180:    */   
/* 181:    */   public void setDestination(OutputStream output)
/* 182:    */     throws IOException
/* 183:    */   {
/* 184:367 */     if (getCompressOutput()) {
/* 185:368 */       super.setDestination(new GZIPOutputStream(output));
/* 186:    */     } else {
/* 187:370 */       super.setDestination(output);
/* 188:    */     }
/* 189:    */   }
/* 190:    */   
/* 191:    */   public void writeBatch()
/* 192:    */     throws IOException
/* 193:    */   {
/* 194:382 */     if (getInstances() == null) {
/* 195:383 */       throw new IOException("No instances to save");
/* 196:    */     }
/* 197:386 */     if (getRetrieval() == 2) {
/* 198:387 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/* 199:    */     }
/* 200:390 */     setRetrieval(1);
/* 201:391 */     setWriteMode(0);
/* 202:    */     
/* 203:    */ 
/* 204:394 */     this.m_XMLInstances.setInstances(getInstances());
/* 205:396 */     if ((retrieveFile() == null) && (getWriter() == null))
/* 206:    */     {
/* 207:397 */       System.out.println(this.m_XMLInstances.toString());
/* 208:398 */       setWriteMode(1);
/* 209:    */     }
/* 210:    */     else
/* 211:    */     {
/* 212:400 */       PrintWriter outW = new PrintWriter(getWriter());
/* 213:401 */       outW.println(this.m_XMLInstances.toString());
/* 214:402 */       outW.flush();
/* 215:403 */       outW.close();
/* 216:404 */       setWriteMode(1);
/* 217:405 */       outW = null;
/* 218:406 */       resetWriter();
/* 219:407 */       setWriteMode(2);
/* 220:    */     }
/* 221:    */   }
/* 222:    */   
/* 223:    */   public String getRevision()
/* 224:    */   {
/* 225:418 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 226:    */   }
/* 227:    */   
/* 228:    */   public static void main(String[] args)
/* 229:    */   {
/* 230:427 */     runFileSaver(new XRFFSaver(), args);
/* 231:    */   }
/* 232:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.XRFFSaver
 * JD-Core Version:    0.7.0.1
 */