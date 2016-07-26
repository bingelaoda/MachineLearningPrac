/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.FileInputStream;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import java.util.zip.GZIPInputStream;
/*   9:    */ import weka.core.Environment;
/*  10:    */ import weka.core.EnvironmentHandler;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.Utils;
/*  16:    */ 
/*  17:    */ public abstract class AbstractFileLoader
/*  18:    */   extends AbstractLoader
/*  19:    */   implements FileSourcedConverter, EnvironmentHandler
/*  20:    */ {
/*  21:    */   private static final long serialVersionUID = 5535537461920594758L;
/*  22: 50 */   protected String m_File = new File(System.getProperty("user.dir")).getAbsolutePath();
/*  23: 54 */   protected transient Instances m_structure = null;
/*  24: 57 */   protected File m_sourceFile = null;
/*  25: 60 */   public static String FILE_EXTENSION_COMPRESSED = ".gz";
/*  26: 63 */   protected boolean m_useRelativePath = false;
/*  27:    */   protected transient Environment m_env;
/*  28:    */   
/*  29:    */   public File retrieveFile()
/*  30:    */   {
/*  31: 75 */     return new File(this.m_File);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void setFile(File file)
/*  35:    */     throws IOException
/*  36:    */   {
/*  37: 86 */     this.m_structure = null;
/*  38: 87 */     setRetrieval(0);
/*  39:    */     
/*  40:    */ 
/*  41: 90 */     setSource(file);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setEnvironment(Environment env)
/*  45:    */   {
/*  46:100 */     this.m_env = env;
/*  47:    */     try
/*  48:    */     {
/*  49:106 */       reset();
/*  50:    */     }
/*  51:    */     catch (IOException ex) {}
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void reset()
/*  55:    */     throws IOException
/*  56:    */   {
/*  57:119 */     this.m_structure = null;
/*  58:120 */     setRetrieval(0);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setSource(File file)
/*  62:    */     throws IOException
/*  63:    */   {
/*  64:132 */     File original = file;
/*  65:133 */     this.m_structure = null;
/*  66:    */     
/*  67:135 */     setRetrieval(0);
/*  68:137 */     if (file == null) {
/*  69:138 */       throw new IOException("Source file object is null!");
/*  70:    */     }
/*  71:142 */     String fName = file.getPath();
/*  72:    */     try
/*  73:    */     {
/*  74:144 */       if (this.m_env == null) {
/*  75:145 */         this.m_env = Environment.getSystemWide();
/*  76:    */       }
/*  77:147 */       fName = this.m_env.substitute(fName);
/*  78:    */     }
/*  79:    */     catch (Exception e) {}
/*  80:155 */     file = new File(fName);
/*  81:157 */     if ((file.exists()) && (file.isFile()))
/*  82:    */     {
/*  83:158 */       if (file.getName().endsWith(getFileExtension() + FILE_EXTENSION_COMPRESSED)) {
/*  84:160 */         setSource(new GZIPInputStream(new FileInputStream(file)));
/*  85:    */       } else {
/*  86:162 */         setSource(new FileInputStream(file));
/*  87:    */       }
/*  88:    */     }
/*  89:    */     else
/*  90:    */     {
/*  91:170 */       String fnameWithCorrectSeparators = fName.replace(File.separatorChar, '/');
/*  92:172 */       if (getClass().getClassLoader().getResource(fnameWithCorrectSeparators) != null) {
/*  93:175 */         setSource(getClass().getClassLoader().getResourceAsStream(fnameWithCorrectSeparators));
/*  94:    */       }
/*  95:    */     }
/*  96:185 */     if (this.m_useRelativePath)
/*  97:    */     {
/*  98:    */       try
/*  99:    */       {
/* 100:187 */         this.m_sourceFile = Utils.convertToRelativePath(original);
/* 101:188 */         this.m_File = this.m_sourceFile.getPath();
/* 102:    */       }
/* 103:    */       catch (Exception ex)
/* 104:    */       {
/* 105:191 */         this.m_sourceFile = original;
/* 106:192 */         this.m_File = this.m_sourceFile.getPath();
/* 107:    */       }
/* 108:    */     }
/* 109:    */     else
/* 110:    */     {
/* 111:195 */       this.m_sourceFile = original;
/* 112:196 */       this.m_File = this.m_sourceFile.getPath();
/* 113:    */     }
/* 114:    */   }
/* 115:    */   
/* 116:    */   public String useRelativePathTipText()
/* 117:    */   {
/* 118:226 */     return "Use relative rather than absolute paths";
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void setUseRelativePath(boolean rp)
/* 122:    */   {
/* 123:236 */     this.m_useRelativePath = rp;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public boolean getUseRelativePath()
/* 127:    */   {
/* 128:246 */     return this.m_useRelativePath;
/* 129:    */   }
/* 130:    */   
/* 131:    */   protected static String makeOptionStr(AbstractFileLoader loader)
/* 132:    */   {
/* 133:260 */     StringBuffer result = new StringBuffer("\nUsage:\n");
/* 134:261 */     result.append("\t" + loader.getClass().getName().replaceAll(".*\\.", ""));
/* 135:262 */     result.append(" <");
/* 136:263 */     String[] ext = loader.getFileExtensions();
/* 137:264 */     for (int i = 0; i < ext.length; i++)
/* 138:    */     {
/* 139:265 */       if (i > 0) {
/* 140:266 */         result.append(" | ");
/* 141:    */       }
/* 142:268 */       result.append("file" + ext[i]);
/* 143:    */     }
/* 144:270 */     result.append(">");
/* 145:271 */     if ((loader instanceof OptionHandler)) {
/* 146:272 */       result.append(" [options]");
/* 147:    */     }
/* 148:274 */     result.append("\n");
/* 149:276 */     if ((loader instanceof OptionHandler))
/* 150:    */     {
/* 151:277 */       result.append("\nOptions:\n\n");
/* 152:278 */       Enumeration<Option> enm = ((OptionHandler)loader).listOptions();
/* 153:279 */       while (enm.hasMoreElements())
/* 154:    */       {
/* 155:280 */         Option option = (Option)enm.nextElement();
/* 156:281 */         result.append(option.synopsis() + "\n");
/* 157:282 */         result.append(option.description() + "\n");
/* 158:    */       }
/* 159:    */     }
/* 160:286 */     return result.toString();
/* 161:    */   }
/* 162:    */   
/* 163:    */   public static void runFileLoader(AbstractFileLoader loader, String[] options)
/* 164:    */   {
/* 165:    */     try
/* 166:    */     {
/* 167:299 */       String[] tmpOptions = (String[])options.clone();
/* 168:300 */       if (Utils.getFlag('h', tmpOptions))
/* 169:    */       {
/* 170:301 */         System.err.println("\nHelp requested\n" + makeOptionStr(loader));
/* 171:302 */         return;
/* 172:    */       }
/* 173:    */     }
/* 174:    */     catch (Exception e) {}
/* 175:308 */     if (options.length > 0)
/* 176:    */     {
/* 177:309 */       String fileName = options[0];
/* 178:310 */       options[0] = "";
/* 179:311 */       if ((loader instanceof OptionHandler)) {
/* 180:    */         try
/* 181:    */         {
/* 182:314 */           ((OptionHandler)loader).setOptions(options);
/* 183:316 */           for (int i = 0; i < options.length; i++) {
/* 184:317 */             if (options[i].length() > 0)
/* 185:    */             {
/* 186:318 */               options = new String[] { options[i] };
/* 187:319 */               break;
/* 188:    */             }
/* 189:    */           }
/* 190:    */         }
/* 191:    */         catch (Exception ex)
/* 192:    */         {
/* 193:323 */           System.err.println(makeOptionStr(loader));
/* 194:324 */           System.exit(1);
/* 195:    */         }
/* 196:    */       }
/* 197:    */       try
/* 198:    */       {
/* 199:329 */         loader.setFile(new File(fileName));
/* 200:331 */         if ((loader instanceof IncrementalConverter))
/* 201:    */         {
/* 202:332 */           Instances structure = loader.getStructure();
/* 203:333 */           System.out.println(structure);
/* 204:    */           Instance temp;
/* 205:    */           do
/* 206:    */           {
/* 207:336 */             temp = loader.getNextInstance(structure);
/* 208:337 */             if (temp != null) {
/* 209:338 */               System.out.println(temp);
/* 210:    */             }
/* 211:340 */           } while (temp != null);
/* 212:    */         }
/* 213:    */         else
/* 214:    */         {
/* 215:344 */           System.out.println(loader.getDataSet());
/* 216:    */         }
/* 217:    */       }
/* 218:    */       catch (Exception ex)
/* 219:    */       {
/* 220:347 */         ex.printStackTrace();
/* 221:    */       }
/* 222:    */     }
/* 223:    */     else
/* 224:    */     {
/* 225:350 */       System.err.println(makeOptionStr(loader));
/* 226:    */     }
/* 227:    */   }
/* 228:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.AbstractFileLoader
 * JD-Core Version:    0.7.0.1
 */