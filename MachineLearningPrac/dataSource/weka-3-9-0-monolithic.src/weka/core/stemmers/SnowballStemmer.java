/*   1:    */ package weka.core.stemmers;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.lang.reflect.Method;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.ClassDiscovery;
/*   8:    */ import weka.core.Option;
/*   9:    */ import weka.core.OptionHandler;
/*  10:    */ import weka.core.RevisionUtils;
/*  11:    */ import weka.core.Utils;
/*  12:    */ import weka.gui.GenericObjectEditor;
/*  13:    */ 
/*  14:    */ public class SnowballStemmer
/*  15:    */   implements Stemmer, OptionHandler
/*  16:    */ {
/*  17:    */   static final long serialVersionUID = -6111170431963015178L;
/*  18:    */   public static final String PACKAGE = "org.tartarus.snowball";
/*  19:    */   public static final String PACKAGE_EXT = "org.tartarus.snowball.ext";
/*  20:    */   protected static final String SNOWBALL_PROGRAM = "org.tartarus.snowball.SnowballProgram";
/*  21: 84 */   protected static boolean m_Present = false;
/*  22:    */   protected static Vector<String> m_Stemmers;
/*  23:    */   protected Object m_Stemmer;
/*  24:    */   protected transient Method m_StemMethod;
/*  25:    */   protected transient Method m_SetCurrentMethod;
/*  26:    */   protected transient Method m_GetCurrentMethod;
/*  27:    */   
/*  28:    */   static
/*  29:    */   {
/*  30:103 */     checkForSnowball();
/*  31:    */   }
/*  32:    */   
/*  33:    */   public SnowballStemmer()
/*  34:    */   {
/*  35:110 */     this("porter");
/*  36:111 */     initStemmers();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public SnowballStemmer(String name)
/*  40:    */   {
/*  41:122 */     setStemmer(name);
/*  42:    */   }
/*  43:    */   
/*  44:    */   private static void checkForSnowball()
/*  45:    */   {
/*  46:    */     try
/*  47:    */     {
/*  48:130 */       Class.forName("org.tartarus.snowball.SnowballProgram");
/*  49:131 */       m_Present = true;
/*  50:    */     }
/*  51:    */     catch (Exception e)
/*  52:    */     {
/*  53:133 */       m_Present = false;
/*  54:    */     }
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String globalInfo()
/*  58:    */   {
/*  59:144 */     return "A wrapper class for the Snowball stemmers. Only available if the Snowball classes are in the classpath.\nIf the class discovery is not dynamic, i.e., the property 'UseDynamic' in the props file 'weka/gui/GenericPropertiesCreator.props' is 'false', then the property 'org.tartarus.snowball.SnowballProgram' in the 'weka/gui/GenericObjectEditor.props' file has to be uncommented as well. If necessary you have to discover and fill in the snowball stemmers manually. You can use the 'weka.core.ClassDiscovery' for this:\n  java weka.core.ClassDiscovery org.tartarus.snowball.SnowballProgram org.tartarus.snowball.ext\n\nFor more information visit these web sites:\n  http://weka.wikispaces.com/Stemmers\n  http://snowball.tartarus.org/\n";
/*  60:    */   }
/*  61:    */   
/*  62:    */   public Enumeration<Option> listOptions()
/*  63:    */   {
/*  64:165 */     Vector<Option> result = new Vector();
/*  65:    */     
/*  66:167 */     result.addElement(new Option("\tThe name of the snowball stemmer (default 'porter').\n\tavailable stemmers:\n" + getStemmerList(65, "\t   "), "S", 1, "-S <name>"));
/*  67:    */     
/*  68:    */ 
/*  69:    */ 
/*  70:    */ 
/*  71:172 */     return result.elements();
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setOptions(String[] options)
/*  75:    */     throws Exception
/*  76:    */   {
/*  77:199 */     String tmpStr = Utils.getOption('S', options);
/*  78:200 */     if (tmpStr.length() != 0) {
/*  79:201 */       setStemmer(tmpStr);
/*  80:    */     } else {
/*  81:203 */       setStemmer("porter");
/*  82:    */     }
/*  83:    */   }
/*  84:    */   
/*  85:    */   public String[] getOptions()
/*  86:    */   {
/*  87:216 */     Vector<String> result = new Vector();
/*  88:218 */     if (getStemmer() != null)
/*  89:    */     {
/*  90:219 */       result.add("-S");
/*  91:220 */       result.add("" + getStemmer());
/*  92:    */     }
/*  93:223 */     return (String[])result.toArray(new String[result.size()]);
/*  94:    */   }
/*  95:    */   
/*  96:    */   private static String getStemmerName(String classname)
/*  97:    */   {
/*  98:233 */     return classname.replaceAll(".*\\.", "").replaceAll("Stemmer$", "");
/*  99:    */   }
/* 100:    */   
/* 101:    */   private static String getStemmerClassname(String name)
/* 102:    */   {
/* 103:244 */     return "org.tartarus.snowball.ext." + name + "Stemmer";
/* 104:    */   }
/* 105:    */   
/* 106:    */   private static void initStemmers()
/* 107:    */   {
/* 108:254 */     if (m_Stemmers != null) {
/* 109:255 */       return;
/* 110:    */     }
/* 111:258 */     m_Stemmers = new Vector();
/* 112:260 */     if (!m_Present) {
/* 113:261 */       return;
/* 114:    */     }
/* 115:264 */     Vector<String> classnames = GenericObjectEditor.getClassnames("org.tartarus.snowball.SnowballProgram");
/* 116:266 */     if (classnames.size() == 0)
/* 117:    */     {
/* 118:267 */       classnames = ClassDiscovery.find("org.tartarus.snowball.SnowballProgram", "org.tartarus.snowball.ext");
/* 119:268 */       for (int i = 0; i < classnames.size(); i++) {
/* 120:269 */         m_Stemmers.add(getStemmerName(((String)classnames.get(i)).toString()));
/* 121:    */       }
/* 122:    */     }
/* 123:    */   }
/* 124:    */   
/* 125:    */   public static boolean isPresent()
/* 126:    */   {
/* 127:281 */     return m_Present;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public static Enumeration<String> listStemmers()
/* 131:    */   {
/* 132:290 */     initStemmers();
/* 133:    */     
/* 134:292 */     return m_Stemmers.elements();
/* 135:    */   }
/* 136:    */   
/* 137:    */   private static String getStemmerList(int lineLength, String indention)
/* 138:    */   {
/* 139:309 */     String result = "";
/* 140:310 */     String line = "";
/* 141:311 */     Enumeration<String> enm = listStemmers();
/* 142:312 */     while (enm.hasMoreElements())
/* 143:    */     {
/* 144:313 */       String name = ((String)enm.nextElement()).toString();
/* 145:314 */       if (line.length() > 0) {
/* 146:315 */         line = line + ", ";
/* 147:    */       }
/* 148:317 */       if ((lineLength > 0) && (line.length() + name.length() > lineLength))
/* 149:    */       {
/* 150:318 */         result = result + indention + line + "\n";
/* 151:319 */         line = "";
/* 152:    */       }
/* 153:321 */       line = line + name;
/* 154:    */     }
/* 155:324 */     if (line.length() > 0) {
/* 156:325 */       result = result + indention + line + "\n";
/* 157:    */     }
/* 158:328 */     return result;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public String getStemmer()
/* 162:    */   {
/* 163:    */     
/* 164:339 */     if (this.m_Stemmer == null) {
/* 165:340 */       return null;
/* 166:    */     }
/* 167:342 */     return getStemmerName(this.m_Stemmer.getClass().getName());
/* 168:    */   }
/* 169:    */   
/* 170:    */   public void setStemmer(String name)
/* 171:    */   {
/* 172:    */     
/* 173:357 */     if (m_Stemmers.contains(name))
/* 174:    */     {
/* 175:    */       try
/* 176:    */       {
/* 177:359 */         Class<?> snowballClass = Class.forName(getStemmerClassname(name));
/* 178:360 */         this.m_Stemmer = snowballClass.newInstance();
/* 179:    */         
/* 180:    */ 
/* 181:363 */         Class<?>[] argClasses = new Class[0];
/* 182:364 */         this.m_StemMethod = snowballClass.getMethod("stem", argClasses);
/* 183:    */         
/* 184:366 */         argClasses = new Class[1];
/* 185:367 */         argClasses[0] = String.class;
/* 186:368 */         this.m_SetCurrentMethod = snowballClass.getMethod("setCurrent", argClasses);
/* 187:    */         
/* 188:370 */         argClasses = new Class[0];
/* 189:371 */         this.m_GetCurrentMethod = snowballClass.getMethod("getCurrent", argClasses);
/* 190:    */       }
/* 191:    */       catch (Exception e)
/* 192:    */       {
/* 193:373 */         System.out.println("Error initializing stemmer '" + name + "'!" + e.getMessage());
/* 194:    */         
/* 195:375 */         this.m_Stemmer = null;
/* 196:    */       }
/* 197:    */     }
/* 198:    */     else
/* 199:    */     {
/* 200:378 */       System.err.println("Stemmer '" + name + "' unknown!");
/* 201:379 */       this.m_Stemmer = null;
/* 202:    */     }
/* 203:    */   }
/* 204:    */   
/* 205:    */   public String stemmerTipText()
/* 206:    */   {
/* 207:390 */     return "The Snowball stemmer to use, available: " + getStemmerList(0, "");
/* 208:    */   }
/* 209:    */   
/* 210:    */   public String stem(String word)
/* 211:    */   {
/* 212:    */     String result;
/* 213:    */     String result;
/* 214:404 */     if (this.m_Stemmer == null)
/* 215:    */     {
/* 216:405 */       result = new String(word);
/* 217:    */     }
/* 218:    */     else
/* 219:    */     {
/* 220:409 */       if (this.m_SetCurrentMethod == null) {
/* 221:410 */         setStemmer(getStemmer());
/* 222:    */       }
/* 223:    */       try
/* 224:    */       {
/* 225:415 */         Object[] args = new Object[1];
/* 226:416 */         args[0] = word;
/* 227:417 */         this.m_SetCurrentMethod.invoke(this.m_Stemmer, args);
/* 228:    */         
/* 229:    */ 
/* 230:420 */         args = new Object[0];
/* 231:421 */         this.m_StemMethod.invoke(this.m_Stemmer, args);
/* 232:    */         
/* 233:    */ 
/* 234:424 */         args = new Object[0];
/* 235:425 */         result = (String)this.m_GetCurrentMethod.invoke(this.m_Stemmer, args);
/* 236:    */       }
/* 237:    */       catch (Exception e)
/* 238:    */       {
/* 239:427 */         e.printStackTrace();
/* 240:428 */         result = word;
/* 241:    */       }
/* 242:    */     }
/* 243:432 */     return result;
/* 244:    */   }
/* 245:    */   
/* 246:    */   public String toString()
/* 247:    */   {
/* 248:444 */     String result = getClass().getName();
/* 249:445 */     result = result + " " + Utils.joinOptions(getOptions());
/* 250:    */     
/* 251:447 */     return result.trim();
/* 252:    */   }
/* 253:    */   
/* 254:    */   public String getRevision()
/* 255:    */   {
/* 256:457 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 257:    */   }
/* 258:    */   
/* 259:    */   public static void main(String[] args)
/* 260:    */   {
/* 261:    */     try
/* 262:    */     {
/* 263:467 */       Stemming.useStemmer(new SnowballStemmer(), args);
/* 264:    */     }
/* 265:    */     catch (Exception e)
/* 266:    */     {
/* 267:469 */       e.printStackTrace();
/* 268:    */     }
/* 269:    */   }
/* 270:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.stemmers.SnowballStemmer
 * JD-Core Version:    0.7.0.1
 */