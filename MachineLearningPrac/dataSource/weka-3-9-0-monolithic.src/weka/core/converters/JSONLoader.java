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
/*  16:    */ import weka.core.json.JSONInstances;
/*  17:    */ import weka.core.json.JSONNode;
/*  18:    */ 
/*  19:    */ public class JSONLoader
/*  20:    */   extends AbstractFileLoader
/*  21:    */   implements BatchConverter, URLSourcedLoader
/*  22:    */ {
/*  23:    */   private static final long serialVersionUID = 3764533621135196582L;
/*  24: 63 */   public static String FILE_EXTENSION = ".json";
/*  25: 66 */   public static String FILE_EXTENSION_COMPRESSED = FILE_EXTENSION + ".gz";
/*  26: 69 */   protected String m_URL = "http://";
/*  27: 72 */   protected transient Reader m_sourceReader = null;
/*  28:    */   protected JSONNode m_JSON;
/*  29:    */   
/*  30:    */   public String globalInfo()
/*  31:    */   {
/*  32: 84 */     return "Reads a source that is in the JSON format.\nIt automatically decompresses the data if the extension is '" + FILE_EXTENSION_COMPRESSED + "'.\n\n" + "For more information, see JSON homepage:\n" + "http://www.json.org/";
/*  33:    */   }
/*  34:    */   
/*  35:    */   public String getFileExtension()
/*  36:    */   {
/*  37: 98 */     return FILE_EXTENSION;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public String[] getFileExtensions()
/*  41:    */   {
/*  42:107 */     return new String[] { FILE_EXTENSION, FILE_EXTENSION_COMPRESSED };
/*  43:    */   }
/*  44:    */   
/*  45:    */   public String getFileDescription()
/*  46:    */   {
/*  47:116 */     return "JSON Instances files";
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void reset()
/*  51:    */     throws IOException
/*  52:    */   {
/*  53:125 */     this.m_structure = null;
/*  54:126 */     this.m_JSON = null;
/*  55:    */     
/*  56:128 */     setRetrieval(0);
/*  57:130 */     if (this.m_File != null) {
/*  58:131 */       setFile(new File(this.m_File));
/*  59:133 */     } else if ((this.m_URL != null) && (!this.m_URL.equals("http://"))) {
/*  60:134 */       setURL(this.m_URL);
/*  61:    */     }
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setSource(File file)
/*  65:    */     throws IOException
/*  66:    */   {
/*  67:146 */     this.m_structure = null;
/*  68:147 */     this.m_JSON = null;
/*  69:    */     
/*  70:149 */     setRetrieval(0);
/*  71:151 */     if (file == null) {
/*  72:152 */       throw new IOException("Source file object is null!");
/*  73:    */     }
/*  74:    */     try
/*  75:    */     {
/*  76:155 */       if (file.getName().endsWith(FILE_EXTENSION_COMPRESSED)) {
/*  77:156 */         setSource(new GZIPInputStream(new FileInputStream(file)));
/*  78:    */       } else {
/*  79:158 */         setSource(new FileInputStream(file));
/*  80:    */       }
/*  81:    */     }
/*  82:    */     catch (FileNotFoundException ex)
/*  83:    */     {
/*  84:161 */       throw new IOException("File not found");
/*  85:    */     }
/*  86:164 */     this.m_sourceFile = file;
/*  87:165 */     this.m_File = file.getAbsolutePath();
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void setSource(URL url)
/*  91:    */     throws IOException
/*  92:    */   {
/*  93:176 */     this.m_structure = null;
/*  94:177 */     this.m_JSON = null;
/*  95:    */     
/*  96:179 */     setRetrieval(0);
/*  97:    */     
/*  98:181 */     setSource(url.openStream());
/*  99:    */     
/* 100:183 */     this.m_URL = url.toString();
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void setURL(String url)
/* 104:    */     throws IOException
/* 105:    */   {
/* 106:193 */     this.m_URL = url;
/* 107:194 */     setSource(new URL(url));
/* 108:    */   }
/* 109:    */   
/* 110:    */   public String retrieveURL()
/* 111:    */   {
/* 112:203 */     return this.m_URL;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public void setSource(InputStream in)
/* 116:    */     throws IOException
/* 117:    */   {
/* 118:214 */     this.m_File = new File(System.getProperty("user.dir")).getAbsolutePath();
/* 119:215 */     this.m_URL = "http://";
/* 120:    */     
/* 121:217 */     this.m_sourceReader = new BufferedReader(new InputStreamReader(in));
/* 122:    */   }
/* 123:    */   
/* 124:    */   public Instances getStructure()
/* 125:    */     throws IOException
/* 126:    */   {
/* 127:229 */     if (this.m_sourceReader == null) {
/* 128:230 */       throw new IOException("No source has been specified");
/* 129:    */     }
/* 130:232 */     if (this.m_structure == null) {
/* 131:    */       try
/* 132:    */       {
/* 133:234 */         this.m_JSON = JSONNode.read(this.m_sourceReader);
/* 134:235 */         this.m_structure = new Instances(JSONInstances.toHeader(this.m_JSON), 0);
/* 135:    */       }
/* 136:    */       catch (IOException ioe)
/* 137:    */       {
/* 138:239 */         throw ioe;
/* 139:    */       }
/* 140:    */       catch (Exception e)
/* 141:    */       {
/* 142:242 */         throw new RuntimeException(e);
/* 143:    */       }
/* 144:    */     }
/* 145:246 */     return new Instances(this.m_structure, 0);
/* 146:    */   }
/* 147:    */   
/* 148:    */   public Instances getDataSet()
/* 149:    */     throws IOException
/* 150:    */   {
/* 151:259 */     if (this.m_sourceReader == null) {
/* 152:260 */       throw new IOException("No source has been specified");
/* 153:    */     }
/* 154:262 */     if (getRetrieval() == 2) {
/* 155:263 */       throw new IOException("Cannot mix getting Instances in both incremental and batch modes");
/* 156:    */     }
/* 157:265 */     setRetrieval(1);
/* 158:266 */     if (this.m_structure == null) {
/* 159:267 */       getStructure();
/* 160:    */     }
/* 161:    */     try
/* 162:    */     {
/* 163:271 */       this.m_sourceReader.close();
/* 164:    */     }
/* 165:    */     catch (Exception ex) {}
/* 166:275 */     return JSONInstances.toInstances(this.m_JSON);
/* 167:    */   }
/* 168:    */   
/* 169:    */   public Instance getNextInstance(Instances structure)
/* 170:    */     throws IOException
/* 171:    */   {
/* 172:287 */     throw new IOException("JSONLoader can't read data sets incrementally.");
/* 173:    */   }
/* 174:    */   
/* 175:    */   public String getRevision()
/* 176:    */   {
/* 177:296 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 178:    */   }
/* 179:    */   
/* 180:    */   public static void main(String[] args)
/* 181:    */   {
/* 182:305 */     runFileLoader(new JSONLoader(), args);
/* 183:    */   }
/* 184:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.JSONLoader
 * JD-Core Version:    0.7.0.1
 */