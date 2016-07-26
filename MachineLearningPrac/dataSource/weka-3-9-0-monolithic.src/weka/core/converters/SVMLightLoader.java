/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.InputStream;
/*   7:    */ import java.io.InputStreamReader;
/*   8:    */ import java.io.PrintStream;
/*   9:    */ import java.io.Reader;
/*  10:    */ import java.net.URL;
/*  11:    */ import java.util.ArrayList;
/*  12:    */ import java.util.StringTokenizer;
/*  13:    */ import java.util.Vector;
/*  14:    */ import weka.core.Attribute;
/*  15:    */ import weka.core.Instance;
/*  16:    */ import weka.core.Instances;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.SparseInstance;
/*  19:    */ 
/*  20:    */ public class SVMLightLoader
/*  21:    */   extends AbstractFileLoader
/*  22:    */   implements BatchConverter, URLSourcedLoader
/*  23:    */ {
/*  24:    */   private static final long serialVersionUID = 4988360125354664417L;
/*  25: 63 */   public static String FILE_EXTENSION = ".dat";
/*  26: 66 */   protected String m_URL = "http://";
/*  27: 69 */   protected transient Reader m_sourceReader = null;
/*  28: 72 */   protected Vector<double[]> m_Buffer = null;
/*  29:    */   
/*  30:    */   public String globalInfo()
/*  31:    */   {
/*  32: 81 */     return "Reads a source that is in svm light format.\n\nFor more information about svm light see:\n\nhttp://svmlight.joachims.org/";
/*  33:    */   }
/*  34:    */   
/*  35:    */   public String getFileExtension()
/*  36:    */   {
/*  37: 93 */     return FILE_EXTENSION;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public String[] getFileExtensions()
/*  41:    */   {
/*  42:102 */     return new String[] { getFileExtension() };
/*  43:    */   }
/*  44:    */   
/*  45:    */   public String getFileDescription()
/*  46:    */   {
/*  47:111 */     return "svm light data files";
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void reset()
/*  51:    */     throws IOException
/*  52:    */   {
/*  53:120 */     this.m_structure = null;
/*  54:121 */     this.m_Buffer = null;
/*  55:    */     
/*  56:123 */     setRetrieval(0);
/*  57:125 */     if (this.m_File != null) {
/*  58:126 */       setFile(new File(this.m_File));
/*  59:128 */     } else if ((this.m_URL != null) && (!this.m_URL.equals("http://"))) {
/*  60:129 */       setURL(this.m_URL);
/*  61:    */     }
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setSource(URL url)
/*  65:    */     throws IOException
/*  66:    */   {
/*  67:141 */     this.m_structure = null;
/*  68:142 */     this.m_Buffer = null;
/*  69:    */     
/*  70:144 */     setRetrieval(0);
/*  71:    */     
/*  72:146 */     setSource(url.openStream());
/*  73:    */     
/*  74:148 */     this.m_URL = url.toString();
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setURL(String url)
/*  78:    */     throws IOException
/*  79:    */   {
/*  80:158 */     this.m_URL = url;
/*  81:159 */     setSource(new URL(url));
/*  82:    */   }
/*  83:    */   
/*  84:    */   public String retrieveURL()
/*  85:    */   {
/*  86:168 */     return this.m_URL;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setSource(InputStream in)
/*  90:    */     throws IOException
/*  91:    */   {
/*  92:179 */     this.m_File = new File(System.getProperty("user.dir")).getAbsolutePath();
/*  93:180 */     this.m_URL = "http://";
/*  94:    */     
/*  95:182 */     this.m_sourceReader = new BufferedReader(new InputStreamReader(in));
/*  96:    */   }
/*  97:    */   
/*  98:    */   protected double[] svmlightToArray(String row)
/*  99:    */     throws Exception
/* 100:    */   {
/* 101:    */     double[] result;
/* 102:    */     try
/* 103:    */     {
/* 104:204 */       int max = 0;
/* 105:205 */       StringTokenizer tok = new StringTokenizer(row, " \t");
/* 106:206 */       tok.nextToken();
/* 107:207 */       while (tok.hasMoreTokens())
/* 108:    */       {
/* 109:208 */         String col = tok.nextToken();
/* 110:210 */         if (col.startsWith("#")) {
/* 111:    */           break;
/* 112:    */         }
/* 113:213 */         if (!col.startsWith("qid:"))
/* 114:    */         {
/* 115:216 */           int index = Integer.parseInt(col.substring(0, col.indexOf(":")));
/* 116:217 */           if (index > max) {
/* 117:218 */             max = index;
/* 118:    */           }
/* 119:    */         }
/* 120:    */       }
/* 121:222 */       tok = new StringTokenizer(row, " \t");
/* 122:223 */       result = new double[max + 1];
/* 123:    */       
/* 124:    */ 
/* 125:226 */       result[(result.length - 1)] = Double.parseDouble(tok.nextToken());
/* 126:229 */       while (tok.hasMoreTokens())
/* 127:    */       {
/* 128:230 */         String col = tok.nextToken();
/* 129:232 */         if (col.startsWith("#")) {
/* 130:    */           break;
/* 131:    */         }
/* 132:235 */         if (!col.startsWith("qid:"))
/* 133:    */         {
/* 134:238 */           int index = Integer.parseInt(col.substring(0, col.indexOf(":")));
/* 135:239 */           double value = Double.parseDouble(col.substring(col.indexOf(":") + 1));
/* 136:240 */           result[(index - 1)] = value;
/* 137:    */         }
/* 138:    */       }
/* 139:    */     }
/* 140:    */     catch (Exception e)
/* 141:    */     {
/* 142:244 */       System.err.println("Error parsing line '" + row + "': " + e);
/* 143:245 */       throw new Exception(e);
/* 144:    */     }
/* 145:248 */     return result;
/* 146:    */   }
/* 147:    */   
/* 148:    */   protected int determineNumAttributes(double[] values, int num)
/* 149:    */     throws Exception
/* 150:    */   {
/* 151:265 */     int result = num;
/* 152:    */     
/* 153:267 */     int count = values.length;
/* 154:268 */     if (count > result) {
/* 155:269 */       result = count;
/* 156:    */     }
/* 157:271 */     return result;
/* 158:    */   }
/* 159:    */   
/* 160:    */   protected Attribute determineClassAttribute()
/* 161:    */   {
/* 162:287 */     boolean binary = true;
/* 163:289 */     for (int i = 0; i < this.m_Buffer.size(); i++)
/* 164:    */     {
/* 165:290 */       double[] dbls = (double[])this.m_Buffer.get(i);
/* 166:291 */       double cls = dbls[(dbls.length - 1)];
/* 167:292 */       if ((cls != -1.0D) && (cls != 1.0D))
/* 168:    */       {
/* 169:293 */         binary = false;
/* 170:294 */         break;
/* 171:    */       }
/* 172:    */     }
/* 173:    */     Attribute result;
/* 174:    */     Attribute result;
/* 175:298 */     if (binary)
/* 176:    */     {
/* 177:299 */       ArrayList<String> values = new ArrayList();
/* 178:300 */       values.add("+1");
/* 179:301 */       values.add("-1");
/* 180:302 */       result = new Attribute("class", values);
/* 181:    */     }
/* 182:    */     else
/* 183:    */     {
/* 184:305 */       result = new Attribute("class");
/* 185:    */     }
/* 186:308 */     return result;
/* 187:    */   }
/* 188:    */   
/* 189:    */   public Instances getStructure()
/* 190:    */     throws IOException
/* 191:    */   {
/* 192:328 */     if (this.m_sourceReader == null) {
/* 193:329 */       throw new IOException("No source has been specified");
/* 194:    */     }
/* 195:331 */     if (this.m_structure == null)
/* 196:    */     {
/* 197:332 */       this.m_Buffer = new Vector();
/* 198:    */       try
/* 199:    */       {
/* 200:335 */         int numAtt = 0;
/* 201:336 */         StringBuffer line = new StringBuffer();
/* 202:    */         int cInt;
/* 203:337 */         while ((cInt = this.m_sourceReader.read()) != -1)
/* 204:    */         {
/* 205:338 */           char c = (char)cInt;
/* 206:339 */           if ((c == '\n') || (c == '\r'))
/* 207:    */           {
/* 208:340 */             if ((line.length() > 0) && (line.charAt(0) != '#')) {
/* 209:    */               try
/* 210:    */               {
/* 211:343 */                 this.m_Buffer.add(svmlightToArray(line.toString()));
/* 212:344 */                 numAtt = determineNumAttributes((double[])this.m_Buffer.lastElement(), numAtt);
/* 213:    */               }
/* 214:    */               catch (Exception e)
/* 215:    */               {
/* 216:347 */                 throw new Exception("Error parsing line '" + line + "': " + e);
/* 217:    */               }
/* 218:    */             }
/* 219:350 */             line = new StringBuffer();
/* 220:    */           }
/* 221:    */           else
/* 222:    */           {
/* 223:353 */             line.append(c);
/* 224:    */           }
/* 225:    */         }
/* 226:358 */         if ((line.length() != 0) && (line.charAt(0) != '#'))
/* 227:    */         {
/* 228:359 */           this.m_Buffer.add(svmlightToArray(line.toString()));
/* 229:360 */           numAtt = determineNumAttributes((double[])this.m_Buffer.lastElement(), numAtt);
/* 230:    */         }
/* 231:364 */         ArrayList<Attribute> atts = new ArrayList(numAtt);
/* 232:365 */         for (int i = 0; i < numAtt - 1; i++) {
/* 233:366 */           atts.add(new Attribute("att_" + (i + 1)));
/* 234:    */         }
/* 235:367 */         atts.add(determineClassAttribute());
/* 236:    */         String relName;
/* 237:    */         String relName;
/* 238:369 */         if (!this.m_URL.equals("http://")) {
/* 239:370 */           relName = this.m_URL;
/* 240:    */         } else {
/* 241:372 */           relName = this.m_File;
/* 242:    */         }
/* 243:374 */         this.m_structure = new Instances(relName, atts, 0);
/* 244:375 */         this.m_structure.setClassIndex(this.m_structure.numAttributes() - 1);
/* 245:    */       }
/* 246:    */       catch (Exception ex)
/* 247:    */       {
/* 248:378 */         ex.printStackTrace();
/* 249:379 */         throw new IOException("Unable to determine structure as svm light: " + ex);
/* 250:    */       }
/* 251:    */     }
/* 252:383 */     return new Instances(this.m_structure, 0);
/* 253:    */   }
/* 254:    */   
/* 255:    */   public Instances getDataSet()
/* 256:    */     throws IOException
/* 257:    */   {
/* 258:401 */     if (this.m_sourceReader == null) {
/* 259:402 */       throw new IOException("No source has been specified");
/* 260:    */     }
/* 261:404 */     if (getRetrieval() == 2) {
/* 262:405 */       throw new IOException("Cannot mix getting Instances in both incremental and batch modes");
/* 263:    */     }
/* 264:407 */     setRetrieval(1);
/* 265:408 */     if (this.m_structure == null) {
/* 266:409 */       getStructure();
/* 267:    */     }
/* 268:411 */     Instances result = new Instances(this.m_structure, 0);
/* 269:414 */     for (int i = 0; i < this.m_Buffer.size(); i++)
/* 270:    */     {
/* 271:415 */       double[] sparse = (double[])this.m_Buffer.get(i);
/* 272:    */       double[] data;
/* 273:417 */       if (sparse.length != this.m_structure.numAttributes())
/* 274:    */       {
/* 275:418 */         double[] data = new double[this.m_structure.numAttributes()];
/* 276:    */         
/* 277:420 */         System.arraycopy(sparse, 0, data, 0, sparse.length - 1);
/* 278:    */         
/* 279:422 */         data[(data.length - 1)] = sparse[(sparse.length - 1)];
/* 280:    */       }
/* 281:    */       else
/* 282:    */       {
/* 283:425 */         data = sparse;
/* 284:    */       }
/* 285:429 */       if (result.classAttribute().isNominal()) {
/* 286:430 */         if (data[(data.length - 1)] == 1.0D) {
/* 287:431 */           data[(data.length - 1)] = result.classAttribute().indexOfValue("+1");
/* 288:432 */         } else if (data[(data.length - 1)] == -1.0D) {
/* 289:433 */           data[(data.length - 1)] = result.classAttribute().indexOfValue("-1");
/* 290:    */         } else {
/* 291:435 */           throw new IllegalStateException("Class is not binary!");
/* 292:    */         }
/* 293:    */       }
/* 294:438 */       result.add(new SparseInstance(1.0D, data));
/* 295:    */     }
/* 296:    */     try
/* 297:    */     {
/* 298:443 */       this.m_sourceReader.close();
/* 299:    */     }
/* 300:    */     catch (Exception ex) {}
/* 301:448 */     return result;
/* 302:    */   }
/* 303:    */   
/* 304:    */   public Instance getNextInstance(Instances structure)
/* 305:    */     throws IOException
/* 306:    */   {
/* 307:460 */     throw new IOException("SVMLightLoader can't read data sets incrementally.");
/* 308:    */   }
/* 309:    */   
/* 310:    */   public String getRevision()
/* 311:    */   {
/* 312:469 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 313:    */   }
/* 314:    */   
/* 315:    */   public static void main(String[] args)
/* 316:    */   {
/* 317:478 */     runFileLoader(new SVMLightLoader(), args);
/* 318:    */   }
/* 319:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.SVMLightLoader
 * JD-Core Version:    0.7.0.1
 */