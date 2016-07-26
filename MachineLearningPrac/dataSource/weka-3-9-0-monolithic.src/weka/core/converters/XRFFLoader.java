/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileInputStream;
/*   6:    */ import java.io.FileNotFoundException;
/*   7:    */ import java.io.IOException;
/*   8:    */ import java.io.InputStream;
/*   9:    */ import java.io.InputStreamReader;
/*  10:    */ import java.io.Reader;
/*  11:    */ import java.net.URL;
/*  12:    */ import java.util.zip.GZIPInputStream;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.xml.XMLInstances;
/*  17:    */ 
/*  18:    */ public class XRFFLoader
/*  19:    */   extends AbstractFileLoader
/*  20:    */   implements BatchConverter, URLSourcedLoader
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = 3764533621135196582L;
/*  23: 58 */   public static String FILE_EXTENSION = XMLInstances.FILE_EXTENSION;
/*  24: 61 */   public static String FILE_EXTENSION_COMPRESSED = FILE_EXTENSION + ".gz";
/*  25: 64 */   protected String m_URL = "http://";
/*  26: 67 */   protected transient Reader m_sourceReader = null;
/*  27:    */   protected XMLInstances m_XMLInstances;
/*  28:    */   
/*  29:    */   public String globalInfo()
/*  30:    */   {
/*  31: 79 */     return "Reads a source that is in the XML version of the ARFF format. It automatically decompresses the data if the extension is '" + FILE_EXTENSION_COMPRESSED + "'.";
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String getFileExtension()
/*  35:    */   {
/*  36: 91 */     return FILE_EXTENSION;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public String[] getFileExtensions()
/*  40:    */   {
/*  41:100 */     return new String[] { FILE_EXTENSION, FILE_EXTENSION_COMPRESSED };
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String getFileDescription()
/*  45:    */   {
/*  46:109 */     return "XRFF data files";
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void reset()
/*  50:    */     throws IOException
/*  51:    */   {
/*  52:118 */     this.m_structure = null;
/*  53:119 */     this.m_XMLInstances = null;
/*  54:    */     
/*  55:121 */     setRetrieval(0);
/*  56:123 */     if (this.m_File != null) {
/*  57:124 */       setFile(new File(this.m_File));
/*  58:126 */     } else if ((this.m_URL != null) && (!this.m_URL.equals("http://"))) {
/*  59:127 */       setURL(this.m_URL);
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void setSource(File file)
/*  64:    */     throws IOException
/*  65:    */   {
/*  66:139 */     this.m_structure = null;
/*  67:140 */     this.m_XMLInstances = null;
/*  68:    */     
/*  69:142 */     setRetrieval(0);
/*  70:144 */     if (file == null) {
/*  71:145 */       throw new IOException("Source file object is null!");
/*  72:    */     }
/*  73:    */     try
/*  74:    */     {
/*  75:148 */       if (file.getName().endsWith(FILE_EXTENSION_COMPRESSED)) {
/*  76:149 */         setSource(new GZIPInputStream(new FileInputStream(file)));
/*  77:    */       } else {
/*  78:151 */         setSource(new FileInputStream(file));
/*  79:    */       }
/*  80:    */     }
/*  81:    */     catch (FileNotFoundException ex)
/*  82:    */     {
/*  83:154 */       throw new IOException("File not found");
/*  84:    */     }
/*  85:157 */     this.m_sourceFile = file;
/*  86:158 */     this.m_File = file.getAbsolutePath();
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setSource(URL url)
/*  90:    */     throws IOException
/*  91:    */   {
/*  92:169 */     this.m_structure = null;
/*  93:170 */     this.m_XMLInstances = null;
/*  94:    */     
/*  95:172 */     setRetrieval(0);
/*  96:    */     
/*  97:174 */     setSource(url.openStream());
/*  98:    */     
/*  99:176 */     this.m_URL = url.toString();
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setURL(String url)
/* 103:    */     throws IOException
/* 104:    */   {
/* 105:186 */     this.m_URL = url;
/* 106:187 */     setSource(new URL(url));
/* 107:    */   }
/* 108:    */   
/* 109:    */   public String retrieveURL()
/* 110:    */   {
/* 111:196 */     return this.m_URL;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setSource(InputStream in)
/* 115:    */     throws IOException
/* 116:    */   {
/* 117:207 */     this.m_File = new File(System.getProperty("user.dir")).getAbsolutePath();
/* 118:208 */     this.m_URL = "http://";
/* 119:    */     
/* 120:210 */     this.m_sourceReader = new BufferedReader(new InputStreamReader(in));
/* 121:    */   }
/* 122:    */   
/* 123:    */   public Instances getStructure()
/* 124:    */     throws IOException
/* 125:    */   {
/* 126:222 */     if (this.m_sourceReader == null) {
/* 127:223 */       throw new IOException("No source has been specified");
/* 128:    */     }
/* 129:225 */     if (this.m_structure == null) {
/* 130:    */       try
/* 131:    */       {
/* 132:227 */         this.m_XMLInstances = new XMLInstances(this.m_sourceReader);
/* 133:228 */         this.m_structure = new Instances(this.m_XMLInstances.getInstances(), 0);
/* 134:    */       }
/* 135:    */       catch (IOException ioe)
/* 136:    */       {
/* 137:232 */         throw ioe;
/* 138:    */       }
/* 139:    */       catch (Exception e)
/* 140:    */       {
/* 141:235 */         throw new RuntimeException(e);
/* 142:    */       }
/* 143:    */     }
/* 144:239 */     return new Instances(this.m_structure, 0);
/* 145:    */   }
/* 146:    */   
/* 147:    */   public Instances getDataSet()
/* 148:    */     throws IOException
/* 149:    */   {
/* 150:252 */     if (this.m_sourceReader == null) {
/* 151:253 */       throw new IOException("No source has been specified");
/* 152:    */     }
/* 153:255 */     if (getRetrieval() == 2) {
/* 154:256 */       throw new IOException("Cannot mix getting Instances in both incremental and batch modes");
/* 155:    */     }
/* 156:258 */     setRetrieval(1);
/* 157:259 */     if (this.m_structure == null) {
/* 158:260 */       getStructure();
/* 159:    */     }
/* 160:    */     try
/* 161:    */     {
/* 162:264 */       this.m_sourceReader.close();
/* 163:    */     }
/* 164:    */     catch (Exception ex) {}
/* 165:268 */     return this.m_XMLInstances.getInstances();
/* 166:    */   }
/* 167:    */   
/* 168:    */   public Instance getNextInstance(Instances structure)
/* 169:    */     throws IOException
/* 170:    */   {
/* 171:280 */     throw new IOException("XRFFLoader can't read data sets incrementally.");
/* 172:    */   }
/* 173:    */   
/* 174:    */   public String getRevision()
/* 175:    */   {
/* 176:289 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 177:    */   }
/* 178:    */   
/* 179:    */   public static void main(String[] args)
/* 180:    */   {
/* 181:298 */     runFileLoader(new XRFFLoader(), args);
/* 182:    */   }
/* 183:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.XRFFLoader
 * JD-Core Version:    0.7.0.1
 */