/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.OutputStream;
/*   6:    */ import java.io.PrintWriter;
/*   7:    */ import java.util.Collections;
/*   8:    */ import java.util.Enumeration;
/*   9:    */ import java.util.Vector;
/*  10:    */ import java.util.zip.GZIPOutputStream;
/*  11:    */ import weka.core.Capabilities;
/*  12:    */ import weka.core.Capabilities.Capability;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.Option;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SingleIndex;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.core.json.JSONInstances;
/*  19:    */ import weka.core.json.JSONNode;
/*  20:    */ 
/*  21:    */ public class JSONSaver
/*  22:    */   extends AbstractFileSaver
/*  23:    */   implements BatchConverter
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = -1047134047244534557L;
/*  26: 89 */   protected SingleIndex m_ClassIndex = new SingleIndex();
/*  27: 92 */   protected boolean m_CompressOutput = false;
/*  28:    */   
/*  29:    */   public JSONSaver()
/*  30:    */   {
/*  31: 98 */     resetOptions();
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String globalInfo()
/*  35:    */   {
/*  36:108 */     return "Writes to a destination that is in JSON format.\nThe data can be compressed with gzip, in order to save space.\n\nFor more information, see JSON homepage:\nhttp://www.json.org/";
/*  37:    */   }
/*  38:    */   
/*  39:    */   public Enumeration<Option> listOptions()
/*  40:    */   {
/*  41:120 */     Vector<Option> result = new Vector();
/*  42:    */     
/*  43:122 */     result.addElement(new Option("\tThe class index (first and last are valid as well).\n\t(default: last)", "C", 1, "-C <class index>"));
/*  44:    */     
/*  45:    */ 
/*  46:    */ 
/*  47:126 */     result.addElement(new Option("\tCompresses the data (uses '" + JSONLoader.FILE_EXTENSION_COMPRESSED + "' as extension instead of '" + JSONLoader.FILE_EXTENSION + "')\n" + "\t(default: off)", "compress", 0, "-compress"));
/*  48:    */     
/*  49:    */ 
/*  50:    */ 
/*  51:    */ 
/*  52:131 */     result.addAll(Collections.list(super.listOptions()));
/*  53:    */     
/*  54:133 */     return result.elements();
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String[] getOptions()
/*  58:    */   {
/*  59:144 */     Vector<String> result = new Vector();
/*  60:146 */     if (getClassIndex().length() != 0)
/*  61:    */     {
/*  62:147 */       result.add("-C");
/*  63:148 */       result.add(getClassIndex());
/*  64:    */     }
/*  65:151 */     if (getCompressOutput()) {
/*  66:152 */       result.add("-compress");
/*  67:    */     }
/*  68:155 */     Collections.addAll(result, super.getOptions());
/*  69:    */     
/*  70:157 */     return (String[])result.toArray(new String[result.size()]);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setOptions(String[] options)
/*  74:    */     throws Exception
/*  75:    */   {
/*  76:198 */     String tmpStr = Utils.getOption('C', options);
/*  77:199 */     if (tmpStr.length() != 0) {
/*  78:200 */       setClassIndex(tmpStr);
/*  79:    */     } else {
/*  80:202 */       setClassIndex("last");
/*  81:    */     }
/*  82:205 */     setCompressOutput(Utils.getFlag("compress", options));
/*  83:    */     
/*  84:207 */     super.setOptions(options);
/*  85:    */     
/*  86:209 */     Utils.checkForRemainingOptions(options);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public String getFileDescription()
/*  90:    */   {
/*  91:219 */     return "JSON data files";
/*  92:    */   }
/*  93:    */   
/*  94:    */   public String[] getFileExtensions()
/*  95:    */   {
/*  96:229 */     return new String[] { JSONLoader.FILE_EXTENSION, JSONLoader.FILE_EXTENSION_COMPRESSED };
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void setFile(File outputFile)
/* 100:    */     throws IOException
/* 101:    */   {
/* 102:241 */     if (outputFile.getAbsolutePath().endsWith(JSONLoader.FILE_EXTENSION_COMPRESSED)) {
/* 103:243 */       setCompressOutput(true);
/* 104:    */     }
/* 105:246 */     super.setFile(outputFile);
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void resetOptions()
/* 109:    */   {
/* 110:254 */     super.resetOptions();
/* 111:256 */     if (getCompressOutput()) {
/* 112:257 */       setFileExtension(JSONLoader.FILE_EXTENSION_COMPRESSED);
/* 113:    */     } else {
/* 114:259 */       setFileExtension(JSONLoader.FILE_EXTENSION);
/* 115:    */     }
/* 116:    */   }
/* 117:    */   
/* 118:    */   public String classIndexTipText()
/* 119:    */   {
/* 120:270 */     return "Sets the class index (\"first\" and \"last\" are valid values)";
/* 121:    */   }
/* 122:    */   
/* 123:    */   public String getClassIndex()
/* 124:    */   {
/* 125:279 */     return this.m_ClassIndex.getSingleIndex();
/* 126:    */   }
/* 127:    */   
/* 128:    */   public void setClassIndex(String value)
/* 129:    */   {
/* 130:288 */     this.m_ClassIndex.setSingleIndex(value);
/* 131:    */   }
/* 132:    */   
/* 133:    */   public String compressOutputTipText()
/* 134:    */   {
/* 135:298 */     return "Optional compression of the output data";
/* 136:    */   }
/* 137:    */   
/* 138:    */   public boolean getCompressOutput()
/* 139:    */   {
/* 140:307 */     return this.m_CompressOutput;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void setCompressOutput(boolean value)
/* 144:    */   {
/* 145:316 */     this.m_CompressOutput = value;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public Capabilities getCapabilities()
/* 149:    */   {
/* 150:327 */     Capabilities result = super.getCapabilities();
/* 151:    */     
/* 152:    */ 
/* 153:330 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 154:331 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 155:332 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 156:333 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/* 157:334 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 158:    */     
/* 159:    */ 
/* 160:337 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 161:338 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 162:339 */     result.enable(Capabilities.Capability.DATE_CLASS);
/* 163:340 */     result.enable(Capabilities.Capability.STRING_CLASS);
/* 164:341 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 165:342 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 166:    */     
/* 167:344 */     return result;
/* 168:    */   }
/* 169:    */   
/* 170:    */   public void setInstances(Instances instances)
/* 171:    */   {
/* 172:354 */     if (this.m_ClassIndex.getSingleIndex().length() != 0)
/* 173:    */     {
/* 174:355 */       this.m_ClassIndex.setUpper(instances.numAttributes() - 1);
/* 175:356 */       instances.setClassIndex(this.m_ClassIndex.getIndex());
/* 176:    */     }
/* 177:359 */     super.setInstances(instances);
/* 178:    */   }
/* 179:    */   
/* 180:    */   public void setDestination(OutputStream output)
/* 181:    */     throws IOException
/* 182:    */   {
/* 183:370 */     if (getCompressOutput()) {
/* 184:371 */       super.setDestination(new GZIPOutputStream(output));
/* 185:    */     } else {
/* 186:373 */       super.setDestination(output);
/* 187:    */     }
/* 188:    */   }
/* 189:    */   
/* 190:    */   public void writeBatch()
/* 191:    */     throws IOException
/* 192:    */   {
/* 193:385 */     if (getInstances() == null) {
/* 194:386 */       throw new IOException("No instances to save");
/* 195:    */     }
/* 196:389 */     if (getRetrieval() == 2) {
/* 197:390 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/* 198:    */     }
/* 199:393 */     setRetrieval(1);
/* 200:394 */     setWriteMode(0);
/* 201:    */     PrintWriter outW;
/* 202:397 */     if ((retrieveFile() == null) && (getWriter() == null)) {
/* 203:398 */       outW = new PrintWriter(System.out);
/* 204:    */     } else {
/* 205:400 */       outW = new PrintWriter(getWriter());
/* 206:    */     }
/* 207:403 */     JSONNode json = JSONInstances.toJSON(getInstances());
/* 208:404 */     StringBuffer buffer = new StringBuffer();
/* 209:405 */     json.toString(buffer);
/* 210:406 */     outW.println(buffer.toString());
/* 211:407 */     outW.flush();
/* 212:409 */     if (getWriter() != null) {
/* 213:410 */       outW.close();
/* 214:    */     }
/* 215:413 */     setWriteMode(1);
/* 216:414 */     PrintWriter outW = null;
/* 217:415 */     resetWriter();
/* 218:416 */     setWriteMode(2);
/* 219:    */   }
/* 220:    */   
/* 221:    */   public String getRevision()
/* 222:    */   {
/* 223:426 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 224:    */   }
/* 225:    */   
/* 226:    */   public static void main(String[] args)
/* 227:    */   {
/* 228:435 */     runFileSaver(new JSONSaver(), args);
/* 229:    */   }
/* 230:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.JSONSaver
 * JD-Core Version:    0.7.0.1
 */