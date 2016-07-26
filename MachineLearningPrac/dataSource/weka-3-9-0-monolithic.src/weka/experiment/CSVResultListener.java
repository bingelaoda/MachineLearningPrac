/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.BufferedOutputStream;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileOutputStream;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.io.PrintWriter;
/*   8:    */ import java.util.Enumeration;
/*   9:    */ import java.util.Vector;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.OptionHandler;
/*  12:    */ import weka.core.RevisionHandler;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Utils;
/*  15:    */ import weka.gui.FilePropertyMetadata;
/*  16:    */ 
/*  17:    */ public class CSVResultListener
/*  18:    */   implements ResultListener, OptionHandler, RevisionHandler
/*  19:    */ {
/*  20:    */   static final long serialVersionUID = -623185072785174658L;
/*  21:    */   protected ResultProducer m_RP;
/*  22: 70 */   protected File m_OutputFile = null;
/*  23: 73 */   protected String m_OutputFileName = "";
/*  24: 76 */   protected transient PrintWriter m_Out = new PrintWriter(System.out, true);
/*  25:    */   
/*  26:    */   public CSVResultListener()
/*  27:    */   {
/*  28:    */     File resultsFile;
/*  29:    */     try
/*  30:    */     {
/*  31: 85 */       resultsFile = File.createTempFile("weka_experiment", ".csv");
/*  32: 86 */       resultsFile.deleteOnExit();
/*  33:    */     }
/*  34:    */     catch (Exception e)
/*  35:    */     {
/*  36: 88 */       System.err.println("Cannot create temp file, writing to standard out.");
/*  37: 89 */       resultsFile = new File("-");
/*  38:    */     }
/*  39: 91 */     setOutputFile(resultsFile);
/*  40: 92 */     setOutputFileName("");
/*  41:    */   }
/*  42:    */   
/*  43:    */   public String globalInfo()
/*  44:    */   {
/*  45:102 */     return "Takes results from a result producer and assembles them into comma separated value form.";
/*  46:    */   }
/*  47:    */   
/*  48:    */   public Enumeration<Option> listOptions()
/*  49:    */   {
/*  50:114 */     Vector<Option> newVector = new Vector(1);
/*  51:    */     
/*  52:116 */     newVector.addElement(new Option("\tThe filename where output will be stored. Use - for stdout.\n\t(default temp file)", "O", 1, "-O <file name>"));
/*  53:    */     
/*  54:    */ 
/*  55:    */ 
/*  56:120 */     return newVector.elements();
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setOptions(String[] options)
/*  60:    */     throws Exception
/*  61:    */   {
/*  62:144 */     String fName = Utils.getOption('O', options);
/*  63:145 */     if (fName.length() != 0)
/*  64:    */     {
/*  65:146 */       setOutputFile(new File(fName));
/*  66:    */     }
/*  67:    */     else
/*  68:    */     {
/*  69:    */       File resultsFile;
/*  70:    */       try
/*  71:    */       {
/*  72:150 */         resultsFile = File.createTempFile("weka_experiment", null);
/*  73:151 */         resultsFile.deleteOnExit();
/*  74:    */       }
/*  75:    */       catch (Exception e)
/*  76:    */       {
/*  77:153 */         System.err.println("Cannot create temp file, writing to standard out.");
/*  78:154 */         resultsFile = new File("-");
/*  79:    */       }
/*  80:156 */       setOutputFile(resultsFile);
/*  81:157 */       setOutputFileName("");
/*  82:    */     }
/*  83:    */   }
/*  84:    */   
/*  85:    */   public String[] getOptions()
/*  86:    */   {
/*  87:169 */     String[] options = new String[2];
/*  88:170 */     int current = 0;
/*  89:    */     
/*  90:172 */     options[(current++)] = "-O";
/*  91:173 */     options[(current++)] = getOutputFile().getName();
/*  92:174 */     while (current < options.length) {
/*  93:175 */       options[(current++)] = "";
/*  94:    */     }
/*  95:177 */     return options;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public String outputFileTipText()
/*  99:    */   {
/* 100:187 */     return "File to save to. Use '-' to write to standard out.";
/* 101:    */   }
/* 102:    */   
/* 103:    */   @FilePropertyMetadata(fileChooserDialogType=1, directoriesOnly=false)
/* 104:    */   public File getOutputFile()
/* 105:    */   {
/* 106:198 */     return this.m_OutputFile;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void setOutputFile(File newOutputFile)
/* 110:    */   {
/* 111:208 */     this.m_OutputFile = newOutputFile;
/* 112:209 */     setOutputFileName(newOutputFile.getName());
/* 113:    */   }
/* 114:    */   
/* 115:    */   public String outputFileName()
/* 116:    */   {
/* 117:219 */     return this.m_OutputFileName;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void setOutputFileName(String name)
/* 121:    */   {
/* 122:229 */     this.m_OutputFileName = name;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public void preProcess(ResultProducer rp)
/* 126:    */     throws Exception
/* 127:    */   {
/* 128:241 */     this.m_RP = rp;
/* 129:242 */     if ((this.m_OutputFile == null) || (this.m_OutputFile.getName().equals("-"))) {
/* 130:243 */       this.m_Out = new PrintWriter(System.out, true);
/* 131:    */     } else {
/* 132:245 */       this.m_Out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(this.m_OutputFile)), true);
/* 133:    */     }
/* 134:248 */     printResultNames(this.m_RP);
/* 135:    */   }
/* 136:    */   
/* 137:    */   public void postProcess(ResultProducer rp)
/* 138:    */     throws Exception
/* 139:    */   {
/* 140:261 */     if ((this.m_OutputFile != null) && (!this.m_OutputFile.getName().equals("-"))) {
/* 141:262 */       this.m_Out.close();
/* 142:    */     }
/* 143:    */   }
/* 144:    */   
/* 145:    */   public String[] determineColumnConstraints(ResultProducer rp)
/* 146:    */     throws Exception
/* 147:    */   {
/* 148:280 */     return null;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void acceptResult(ResultProducer rp, Object[] key, Object[] result)
/* 152:    */     throws Exception
/* 153:    */   {
/* 154:295 */     if (this.m_RP != rp) {
/* 155:296 */       throw new Error("Unrecognized ResultProducer sending results!!");
/* 156:    */     }
/* 157:298 */     for (int i = 0; i < key.length; i++)
/* 158:    */     {
/* 159:299 */       if (i != 0) {
/* 160:300 */         this.m_Out.print(',');
/* 161:    */       }
/* 162:302 */       if (key[i] == null) {
/* 163:303 */         this.m_Out.print("?");
/* 164:    */       } else {
/* 165:305 */         this.m_Out.print(Utils.quote(key[i].toString()));
/* 166:    */       }
/* 167:    */     }
/* 168:308 */     for (Object element : result)
/* 169:    */     {
/* 170:309 */       this.m_Out.print(',');
/* 171:310 */       if (element == null) {
/* 172:311 */         this.m_Out.print("?");
/* 173:    */       } else {
/* 174:313 */         this.m_Out.print(Utils.quote(element.toString()));
/* 175:    */       }
/* 176:    */     }
/* 177:316 */     this.m_Out.println("");
/* 178:    */   }
/* 179:    */   
/* 180:    */   public boolean isResultRequired(ResultProducer rp, Object[] key)
/* 181:    */     throws Exception
/* 182:    */   {
/* 183:332 */     return true;
/* 184:    */   }
/* 185:    */   
/* 186:    */   private void printResultNames(ResultProducer rp)
/* 187:    */     throws Exception
/* 188:    */   {
/* 189:343 */     String[] key = rp.getKeyNames();
/* 190:344 */     for (int i = 0; i < key.length; i++)
/* 191:    */     {
/* 192:345 */       if (i != 0) {
/* 193:346 */         this.m_Out.print(',');
/* 194:    */       }
/* 195:348 */       if (key[i] == null) {
/* 196:349 */         this.m_Out.print("?");
/* 197:    */       } else {
/* 198:351 */         this.m_Out.print("Key_" + key[i].toString());
/* 199:    */       }
/* 200:    */     }
/* 201:354 */     String[] result = rp.getResultNames();
/* 202:355 */     for (String element : result)
/* 203:    */     {
/* 204:356 */       this.m_Out.print(',');
/* 205:357 */       if (element == null) {
/* 206:358 */         this.m_Out.print("?");
/* 207:    */       } else {
/* 208:360 */         this.m_Out.print(element.toString());
/* 209:    */       }
/* 210:    */     }
/* 211:363 */     this.m_Out.println("");
/* 212:    */   }
/* 213:    */   
/* 214:    */   public String getRevision()
/* 215:    */   {
/* 216:373 */     return RevisionUtils.extract("$Revision: 11690 $");
/* 217:    */   }
/* 218:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.CSVResultListener
 * JD-Core Version:    0.7.0.1
 */