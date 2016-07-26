/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.InputStream;
/*   7:    */ import java.io.InputStreamReader;
/*   8:    */ import java.io.Reader;
/*   9:    */ import java.net.URL;
/*  10:    */ import java.util.ArrayList;
/*  11:    */ import java.util.StringTokenizer;
/*  12:    */ import java.util.Vector;
/*  13:    */ import weka.core.Attribute;
/*  14:    */ import weka.core.Instance;
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ import weka.core.SparseInstance;
/*  18:    */ 
/*  19:    */ public class LibSVMLoader
/*  20:    */   extends AbstractFileLoader
/*  21:    */   implements BatchConverter, URLSourcedLoader
/*  22:    */ {
/*  23:    */   private static final long serialVersionUID = 4988360125354664417L;
/*  24: 62 */   public static String FILE_EXTENSION = ".libsvm";
/*  25: 65 */   protected String m_URL = "http://";
/*  26: 68 */   protected transient Reader m_sourceReader = null;
/*  27: 71 */   protected Vector<double[]> m_Buffer = null;
/*  28:    */   
/*  29:    */   public String globalInfo()
/*  30:    */   {
/*  31: 80 */     return "Reads a source that is in libsvm format.\n\nFor more information about libsvm see:\n\nhttp://www.csie.ntu.edu.tw/~cjlin/libsvm/";
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String getFileExtension()
/*  35:    */   {
/*  36: 93 */     return FILE_EXTENSION;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public String[] getFileExtensions()
/*  40:    */   {
/*  41:103 */     return new String[] { getFileExtension() };
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String getFileDescription()
/*  45:    */   {
/*  46:113 */     return "libsvm data files";
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void reset()
/*  50:    */     throws IOException
/*  51:    */   {
/*  52:123 */     this.m_structure = null;
/*  53:124 */     this.m_Buffer = null;
/*  54:    */     
/*  55:126 */     setRetrieval(0);
/*  56:128 */     if ((this.m_File != null) && (new File(this.m_File).isFile())) {
/*  57:129 */       setFile(new File(this.m_File));
/*  58:131 */     } else if ((this.m_URL != null) && (!this.m_URL.equals("http://"))) {
/*  59:132 */       setURL(this.m_URL);
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void setSource(URL url)
/*  64:    */     throws IOException
/*  65:    */   {
/*  66:144 */     this.m_structure = null;
/*  67:145 */     this.m_Buffer = null;
/*  68:    */     
/*  69:147 */     setRetrieval(0);
/*  70:    */     
/*  71:149 */     setSource(url.openStream());
/*  72:    */     
/*  73:151 */     this.m_URL = url.toString();
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setURL(String url)
/*  77:    */     throws IOException
/*  78:    */   {
/*  79:162 */     this.m_URL = url;
/*  80:163 */     setSource(new URL(url));
/*  81:    */   }
/*  82:    */   
/*  83:    */   public String retrieveURL()
/*  84:    */   {
/*  85:173 */     return this.m_URL;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void setSource(InputStream in)
/*  89:    */     throws IOException
/*  90:    */   {
/*  91:185 */     this.m_File = new File(System.getProperty("user.dir")).getAbsolutePath();
/*  92:186 */     this.m_URL = "http://";
/*  93:    */     
/*  94:188 */     this.m_sourceReader = new BufferedReader(new InputStreamReader(in));
/*  95:    */   }
/*  96:    */   
/*  97:    */   protected double[] libsvmToArray(String row)
/*  98:    */   {
/*  99:206 */     int max = 0;
/* 100:207 */     StringTokenizer tok = new StringTokenizer(row, " \t");
/* 101:208 */     tok.nextToken();
/* 102:209 */     while (tok.hasMoreTokens())
/* 103:    */     {
/* 104:210 */       String col = tok.nextToken();
/* 105:211 */       int index = Integer.parseInt(col.substring(0, col.indexOf(":")));
/* 106:212 */       if (index > max) {
/* 107:213 */         max = index;
/* 108:    */       }
/* 109:    */     }
/* 110:218 */     tok = new StringTokenizer(row, " \t");
/* 111:219 */     double[] result = new double[max + 1];
/* 112:    */     
/* 113:    */ 
/* 114:222 */     result[(result.length - 1)] = Double.parseDouble(tok.nextToken());
/* 115:225 */     while (tok.hasMoreTokens())
/* 116:    */     {
/* 117:226 */       String col = tok.nextToken();
/* 118:227 */       int index = Integer.parseInt(col.substring(0, col.indexOf(":")));
/* 119:228 */       double value = Double.parseDouble(col.substring(col.indexOf(":") + 1));
/* 120:229 */       result[(index - 1)] = value;
/* 121:    */     }
/* 122:232 */     return result;
/* 123:    */   }
/* 124:    */   
/* 125:    */   protected int determineNumAttributes(String row, int num)
/* 126:    */   {
/* 127:248 */     int result = num;
/* 128:    */     
/* 129:250 */     int count = libsvmToArray(row).length;
/* 130:251 */     if (count > result) {
/* 131:252 */       result = count;
/* 132:    */     }
/* 133:255 */     return result;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public Instances getStructure()
/* 137:    */     throws IOException
/* 138:    */   {
/* 139:275 */     if (this.m_sourceReader == null) {
/* 140:276 */       throw new IOException("No source has been specified");
/* 141:    */     }
/* 142:279 */     if (this.m_structure == null)
/* 143:    */     {
/* 144:280 */       this.m_Buffer = new Vector();
/* 145:    */       try
/* 146:    */       {
/* 147:283 */         int numAtt = 0;
/* 148:284 */         int len = 8388608;
/* 149:285 */         char[] cbuf = new char[len];
/* 150:286 */         int iter = 0;
/* 151:287 */         String linesplitter = null;
/* 152:    */         
/* 153:289 */         String oldLine = null;
/* 154:290 */         String read = null;
/* 155:    */         int cInt;
/* 156:291 */         while ((cInt = this.m_sourceReader.read(cbuf, 0, len)) != -1)
/* 157:    */         {
/* 158:292 */           read = String.valueOf(cbuf, 0, cInt);
/* 159:294 */           if (oldLine != null) {
/* 160:295 */             read = oldLine + read;
/* 161:    */           }
/* 162:298 */           if (linesplitter == null) {
/* 163:299 */             if (read.contains("\r\n")) {
/* 164:300 */               linesplitter = "\r\n";
/* 165:301 */             } else if (read.contains("\n")) {
/* 166:302 */               linesplitter = "\n";
/* 167:    */             }
/* 168:    */           }
/* 169:    */           String[] lines;
/* 170:    */           String[] lines;
/* 171:306 */           if (linesplitter != null) {
/* 172:307 */             lines = read.split(linesplitter, -1);
/* 173:    */           } else {
/* 174:309 */             lines = new String[] { read };
/* 175:    */           }
/* 176:312 */           for (int j = 0; j < lines.length - 1; j++)
/* 177:    */           {
/* 178:313 */             String line = lines[j];
/* 179:    */             
/* 180:315 */             this.m_Buffer.add(libsvmToArray(line));
/* 181:316 */             numAtt = determineNumAttributes(line, numAtt);
/* 182:    */           }
/* 183:319 */           oldLine = lines[(lines.length - 1)];
/* 184:    */         }
/* 185:323 */         if ((oldLine != null) && (oldLine.length() != 0))
/* 186:    */         {
/* 187:324 */           this.m_Buffer.add(libsvmToArray(oldLine));
/* 188:325 */           numAtt = determineNumAttributes(oldLine, numAtt);
/* 189:    */         }
/* 190:329 */         ArrayList<Attribute> atts = new ArrayList(numAtt);
/* 191:330 */         for (int i = 0; i < numAtt - 1; i++) {
/* 192:331 */           atts.add(new Attribute("att_" + (i + 1)));
/* 193:    */         }
/* 194:333 */         atts.add(new Attribute("class"));
/* 195:    */         String relName;
/* 196:    */         String relName;
/* 197:335 */         if (!this.m_URL.equals("http://")) {
/* 198:336 */           relName = this.m_URL;
/* 199:    */         } else {
/* 200:338 */           relName = this.m_File;
/* 201:    */         }
/* 202:341 */         this.m_structure = new Instances(relName, atts, 0);
/* 203:342 */         this.m_structure.setClassIndex(this.m_structure.numAttributes() - 1);
/* 204:    */       }
/* 205:    */       catch (Exception ex)
/* 206:    */       {
/* 207:344 */         ex.printStackTrace();
/* 208:345 */         throw new IOException("Unable to determine structure as libsvm: " + ex);
/* 209:    */       }
/* 210:    */     }
/* 211:349 */     return new Instances(this.m_structure, 0);
/* 212:    */   }
/* 213:    */   
/* 214:    */   public Instances getDataSet()
/* 215:    */     throws IOException
/* 216:    */   {
/* 217:367 */     if (this.m_sourceReader == null) {
/* 218:368 */       throw new IOException("No source has been specified");
/* 219:    */     }
/* 220:371 */     if (getRetrieval() == 2) {
/* 221:372 */       throw new IOException("Cannot mix getting Instances in both incremental and batch modes");
/* 222:    */     }
/* 223:376 */     setRetrieval(1);
/* 224:377 */     if (this.m_structure == null) {
/* 225:378 */       getStructure();
/* 226:    */     }
/* 227:381 */     Instances result = new Instances(this.m_structure, 0);
/* 228:384 */     for (int i = 0; i < this.m_Buffer.size(); i++)
/* 229:    */     {
/* 230:385 */       double[] sparse = (double[])this.m_Buffer.get(i);
/* 231:    */       double[] data;
/* 232:387 */       if (sparse.length != this.m_structure.numAttributes())
/* 233:    */       {
/* 234:388 */         double[] data = new double[this.m_structure.numAttributes()];
/* 235:    */         
/* 236:390 */         System.arraycopy(sparse, 0, data, 0, sparse.length - 1);
/* 237:    */         
/* 238:392 */         data[(data.length - 1)] = sparse[(sparse.length - 1)];
/* 239:    */       }
/* 240:    */       else
/* 241:    */       {
/* 242:395 */         data = sparse;
/* 243:    */       }
/* 244:398 */       result.add(new SparseInstance(1.0D, data));
/* 245:    */     }
/* 246:    */     try
/* 247:    */     {
/* 248:403 */       this.m_sourceReader.close();
/* 249:    */     }
/* 250:    */     catch (Exception ex) {}
/* 251:408 */     return result;
/* 252:    */   }
/* 253:    */   
/* 254:    */   public Instance getNextInstance(Instances structure)
/* 255:    */     throws IOException
/* 256:    */   {
/* 257:421 */     throw new IOException("LibSVMLoader can't read data sets incrementally.");
/* 258:    */   }
/* 259:    */   
/* 260:    */   public String getRevision()
/* 261:    */   {
/* 262:431 */     return RevisionUtils.extract("$Revision: 11360 $");
/* 263:    */   }
/* 264:    */   
/* 265:    */   public static void main(String[] args)
/* 266:    */   {
/* 267:440 */     runFileLoader(new LibSVMLoader(), args);
/* 268:    */   }
/* 269:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.LibSVMLoader
 * JD-Core Version:    0.7.0.1
 */