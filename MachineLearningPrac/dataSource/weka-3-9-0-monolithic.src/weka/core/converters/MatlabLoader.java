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
/*  11:    */ import java.util.Vector;
/*  12:    */ import weka.core.Attribute;
/*  13:    */ import weka.core.DenseInstance;
/*  14:    */ import weka.core.Instance;
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ 
/*  18:    */ public class MatlabLoader
/*  19:    */   extends AbstractFileLoader
/*  20:    */   implements BatchConverter, URLSourcedLoader
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = -8861142318612875251L;
/*  23: 58 */   public static String FILE_EXTENSION = ".m";
/*  24: 61 */   protected String m_URL = "http://";
/*  25: 64 */   protected transient Reader m_sourceReader = null;
/*  26: 67 */   protected Vector<Vector<Double>> m_Buffer = null;
/*  27:    */   
/*  28:    */   public String globalInfo()
/*  29:    */   {
/*  30: 76 */     return "Reads a Matlab file containing a single matrix in ASCII format.";
/*  31:    */   }
/*  32:    */   
/*  33:    */   public String getFileExtension()
/*  34:    */   {
/*  35: 85 */     return FILE_EXTENSION;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public String[] getFileExtensions()
/*  39:    */   {
/*  40: 94 */     return new String[] { getFileExtension() };
/*  41:    */   }
/*  42:    */   
/*  43:    */   public String getFileDescription()
/*  44:    */   {
/*  45:103 */     return "Matlab ASCII files";
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void reset()
/*  49:    */     throws IOException
/*  50:    */   {
/*  51:112 */     this.m_structure = null;
/*  52:113 */     this.m_Buffer = null;
/*  53:    */     
/*  54:115 */     setRetrieval(0);
/*  55:117 */     if ((this.m_File != null) && (new File(this.m_File).isFile())) {
/*  56:118 */       setFile(new File(this.m_File));
/*  57:120 */     } else if ((this.m_URL != null) && (!this.m_URL.equals("http://"))) {
/*  58:121 */       setURL(this.m_URL);
/*  59:    */     }
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setSource(URL url)
/*  63:    */     throws IOException
/*  64:    */   {
/*  65:133 */     this.m_structure = null;
/*  66:134 */     this.m_Buffer = null;
/*  67:    */     
/*  68:136 */     setRetrieval(0);
/*  69:    */     
/*  70:138 */     setSource(url.openStream());
/*  71:    */     
/*  72:140 */     this.m_URL = url.toString();
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void setURL(String url)
/*  76:    */     throws IOException
/*  77:    */   {
/*  78:150 */     this.m_URL = url;
/*  79:151 */     setSource(new URL(url));
/*  80:    */   }
/*  81:    */   
/*  82:    */   public String retrieveURL()
/*  83:    */   {
/*  84:160 */     return this.m_URL;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setSource(InputStream in)
/*  88:    */     throws IOException
/*  89:    */   {
/*  90:171 */     this.m_File = new File(System.getProperty("user.dir")).getAbsolutePath();
/*  91:172 */     this.m_URL = "http://";
/*  92:    */     
/*  93:174 */     this.m_sourceReader = new BufferedReader(new InputStreamReader(in));
/*  94:    */   }
/*  95:    */   
/*  96:    */   public Instances getStructure()
/*  97:    */     throws IOException
/*  98:    */   {
/*  99:196 */     if (this.m_sourceReader == null) {
/* 100:197 */       throw new IOException("No source has been specified");
/* 101:    */     }
/* 102:199 */     if (this.m_structure == null)
/* 103:    */     {
/* 104:200 */       int numAtt = 0;
/* 105:201 */       this.m_Buffer = new Vector();
/* 106:202 */       Vector<Double> row = new Vector();
/* 107:203 */       StringBuffer str = new StringBuffer();
/* 108:204 */       boolean isComment = false;
/* 109:205 */       this.m_Buffer.add(row);
/* 110:    */       try
/* 111:    */       {
/* 112:    */         int c;
/* 113:208 */         while ((c = this.m_sourceReader.read()) != -1)
/* 114:    */         {
/* 115:209 */           char chr = (char)c;
/* 116:212 */           if (chr == '%') {
/* 117:213 */             isComment = true;
/* 118:    */           }
/* 119:216 */           if ((chr == '\n') || (chr == '\r'))
/* 120:    */           {
/* 121:217 */             isComment = false;
/* 122:218 */             if (str.length() > 0) {
/* 123:219 */               row.add(new Double(str.toString()));
/* 124:    */             }
/* 125:220 */             if (numAtt == 0) {
/* 126:221 */               numAtt = row.size();
/* 127:    */             }
/* 128:222 */             if (row.size() > 0)
/* 129:    */             {
/* 130:223 */               row = new Vector();
/* 131:224 */               this.m_Buffer.add(row);
/* 132:    */             }
/* 133:226 */             str = new StringBuffer();
/* 134:    */           }
/* 135:231 */           else if (!isComment)
/* 136:    */           {
/* 137:235 */             if ((chr == '\t') || (chr == ' '))
/* 138:    */             {
/* 139:236 */               if (str.length() > 0)
/* 140:    */               {
/* 141:237 */                 row.add(new Double(str.toString()));
/* 142:238 */                 str = new StringBuffer();
/* 143:    */               }
/* 144:    */             }
/* 145:    */             else {
/* 146:242 */               str.append(chr);
/* 147:    */             }
/* 148:    */           }
/* 149:    */         }
/* 150:247 */         if (str.length() > 0) {
/* 151:248 */           row.add(new Double(str.toString()));
/* 152:    */         }
/* 153:251 */         ArrayList<Attribute> atts = new ArrayList(numAtt);
/* 154:252 */         for (int i = 0; i < numAtt; i++) {
/* 155:253 */           atts.add(new Attribute("att_" + (i + 1)));
/* 156:    */         }
/* 157:    */         String relName;
/* 158:    */         String relName;
/* 159:255 */         if (!this.m_URL.equals("http://")) {
/* 160:256 */           relName = this.m_URL;
/* 161:    */         } else {
/* 162:258 */           relName = this.m_File;
/* 163:    */         }
/* 164:260 */         this.m_structure = new Instances(relName, atts, 0);
/* 165:261 */         this.m_structure.setClassIndex(this.m_structure.numAttributes() - 1);
/* 166:    */       }
/* 167:    */       catch (Exception ex)
/* 168:    */       {
/* 169:264 */         ex.printStackTrace();
/* 170:265 */         throw new IOException("Unable to determine structure as Matlab ASCII file: " + ex);
/* 171:    */       }
/* 172:    */     }
/* 173:269 */     return new Instances(this.m_structure, 0);
/* 174:    */   }
/* 175:    */   
/* 176:    */   public Instances getDataSet()
/* 177:    */     throws IOException
/* 178:    */   {
/* 179:288 */     if (this.m_sourceReader == null) {
/* 180:289 */       throw new IOException("No source has been specified");
/* 181:    */     }
/* 182:291 */     if (getRetrieval() == 2) {
/* 183:292 */       throw new IOException("Cannot mix getting Instances in both incremental and batch modes");
/* 184:    */     }
/* 185:294 */     setRetrieval(1);
/* 186:295 */     if (this.m_structure == null) {
/* 187:296 */       getStructure();
/* 188:    */     }
/* 189:298 */     Instances result = new Instances(this.m_structure, 0);
/* 190:301 */     for (int i = 0; i < this.m_Buffer.size(); i++)
/* 191:    */     {
/* 192:302 */       Vector<Double> row = (Vector)this.m_Buffer.get(i);
/* 193:303 */       if (row.size() != 0)
/* 194:    */       {
/* 195:305 */         double[] data = new double[row.size()];
/* 196:306 */         for (int n = 0; n < row.size(); n++) {
/* 197:307 */           data[n] = ((Double)row.get(n)).doubleValue();
/* 198:    */         }
/* 199:309 */         result.add(new DenseInstance(1.0D, data));
/* 200:    */       }
/* 201:    */     }
/* 202:    */     try
/* 203:    */     {
/* 204:314 */       this.m_sourceReader.close();
/* 205:    */     }
/* 206:    */     catch (Exception ex) {}
/* 207:320 */     return result;
/* 208:    */   }
/* 209:    */   
/* 210:    */   public Instance getNextInstance(Instances structure)
/* 211:    */     throws IOException
/* 212:    */   {
/* 213:332 */     throw new IOException("MatlabLoader can't read data sets incrementally.");
/* 214:    */   }
/* 215:    */   
/* 216:    */   public String getRevision()
/* 217:    */   {
/* 218:341 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 219:    */   }
/* 220:    */   
/* 221:    */   public static void main(String[] args)
/* 222:    */   {
/* 223:350 */     runFileLoader(new MatlabLoader(), args);
/* 224:    */   }
/* 225:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.MatlabLoader
 * JD-Core Version:    0.7.0.1
 */