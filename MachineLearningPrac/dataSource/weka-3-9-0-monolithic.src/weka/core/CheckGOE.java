/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.beans.BeanInfo;
/*   4:    */ import java.beans.Introspector;
/*   5:    */ import java.beans.PropertyDescriptor;
/*   6:    */ import java.lang.reflect.Method;
/*   7:    */ import java.util.Collections;
/*   8:    */ import java.util.Enumeration;
/*   9:    */ import java.util.HashSet;
/*  10:    */ import java.util.Vector;
/*  11:    */ import weka.classifiers.rules.ZeroR;
/*  12:    */ import weka.gui.ProgrammaticProperty;
/*  13:    */ 
/*  14:    */ public class CheckGOE
/*  15:    */   extends Check
/*  16:    */ {
/*  17: 79 */   protected Object m_Object = new ZeroR();
/*  18:    */   protected boolean m_Success;
/*  19: 89 */   protected HashSet<String> m_IgnoredProperties = new HashSet();
/*  20:    */   
/*  21:    */   public CheckGOE()
/*  22:    */   {
/*  23:    */     try
/*  24:    */     {
/*  25: 99 */       setOptions(new String[0]);
/*  26:    */     }
/*  27:    */     catch (Exception e)
/*  28:    */     {
/*  29:101 */       e.printStackTrace();
/*  30:    */     }
/*  31:    */   }
/*  32:    */   
/*  33:    */   public Enumeration<Option> listOptions()
/*  34:    */   {
/*  35:112 */     Vector<Option> result = new Vector();
/*  36:    */     
/*  37:114 */     result.addAll(Collections.list(super.listOptions()));
/*  38:    */     
/*  39:116 */     result.addElement(new Option("\tSkipped properties.\n\t(default: capabilities,options)", "ignored", 1, "-ignored <comma-separated list of properties>"));
/*  40:    */     
/*  41:    */ 
/*  42:    */ 
/*  43:120 */     result.addElement(new Option("\tFull name of the class analysed.\n\teg: weka.classifiers.rules.ZeroR\n\t(default weka.classifiers.rules.ZeroR)", "W", 1, "-W"));
/*  44:    */     
/*  45:    */ 
/*  46:    */ 
/*  47:124 */     return result.elements();
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setOptions(String[] options)
/*  51:    */     throws Exception
/*  52:    */   {
/*  53:166 */     super.setOptions(options);
/*  54:    */     
/*  55:168 */     String tmpStr = Utils.getOption('W', options);
/*  56:169 */     if (tmpStr.length() == 0) {
/*  57:170 */       tmpStr = ZeroR.class.getName();
/*  58:    */     }
/*  59:172 */     setObject(Utils.forName(Object.class, tmpStr, null));
/*  60:    */     
/*  61:174 */     tmpStr = Utils.getOption("ignored", options);
/*  62:175 */     if (tmpStr.length() == 0) {
/*  63:176 */       tmpStr = "capabilities,options";
/*  64:    */     }
/*  65:178 */     setIgnoredProperties(tmpStr);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public String[] getOptions()
/*  69:    */   {
/*  70:188 */     Vector<String> result = new Vector();
/*  71:    */     
/*  72:190 */     Collections.addAll(result, super.getOptions());
/*  73:    */     
/*  74:192 */     result.add("-ignored");
/*  75:193 */     result.add(getIgnoredProperties());
/*  76:195 */     if (getObject() != null)
/*  77:    */     {
/*  78:196 */       result.add("-W");
/*  79:197 */       result.add(getObject().getClass().getName());
/*  80:    */     }
/*  81:200 */     return (String[])result.toArray(new String[result.size()]);
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setObject(Object value)
/*  85:    */   {
/*  86:209 */     this.m_Object = value;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public Object getObject()
/*  90:    */   {
/*  91:218 */     return this.m_Object;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void setIgnoredProperties(String value)
/*  95:    */   {
/*  96:231 */     this.m_IgnoredProperties.clear();
/*  97:232 */     String[] props = value.split(",");
/*  98:233 */     for (int i = 0; i < props.length; i++) {
/*  99:234 */       this.m_IgnoredProperties.add(props[i]);
/* 100:    */     }
/* 101:    */   }
/* 102:    */   
/* 103:    */   public String getIgnoredProperties()
/* 104:    */   {
/* 105:250 */     Vector<String> list = new Vector();
/* 106:251 */     list.addAll(this.m_IgnoredProperties);
/* 107:254 */     if (list.size() > 1) {
/* 108:255 */       Collections.sort(list);
/* 109:    */     }
/* 110:258 */     String result = "";
/* 111:259 */     for (int i = 0; i < list.size(); i++)
/* 112:    */     {
/* 113:260 */       if (i > 0) {
/* 114:261 */         result = result + ",";
/* 115:    */       }
/* 116:263 */       result = result + (String)list.get(i);
/* 117:    */     }
/* 118:266 */     return result;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public boolean getSuccess()
/* 122:    */   {
/* 123:275 */     return this.m_Success;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public boolean checkGlobalInfo()
/* 127:    */   {
/* 128:287 */     print("Global info...");
/* 129:    */     
/* 130:289 */     boolean result = true;
/* 131:290 */     Class<?> cls = getObject().getClass();
/* 132:    */     try
/* 133:    */     {
/* 134:294 */       cls.getMethod("globalInfo", (Class[])null);
/* 135:    */     }
/* 136:    */     catch (Exception e)
/* 137:    */     {
/* 138:296 */       result = false;
/* 139:    */     }
/* 140:299 */     if (result) {
/* 141:300 */       println("yes");
/* 142:    */     } else {
/* 143:302 */       println("no");
/* 144:    */     }
/* 145:305 */     return result;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public boolean checkToolTips()
/* 149:    */   {
/* 150:322 */     print("Tool tips...");
/* 151:    */     
/* 152:324 */     boolean result = true;
/* 153:325 */     String suffix = "TipText";
/* 154:326 */     Class<?> cls = getObject().getClass();
/* 155:    */     PropertyDescriptor[] desc;
/* 156:    */     try
/* 157:    */     {
/* 158:330 */       BeanInfo info = Introspector.getBeanInfo(cls, Object.class);
/* 159:331 */       desc = info.getPropertyDescriptors();
/* 160:    */     }
/* 161:    */     catch (Exception e)
/* 162:    */     {
/* 163:333 */       e.printStackTrace();
/* 164:334 */       desc = null;
/* 165:    */     }
/* 166:338 */     if (desc != null)
/* 167:    */     {
/* 168:339 */       Vector<String> missing = new Vector();
/* 169:341 */       for (int i = 0; i < desc.length; i++) {
/* 170:343 */         if (!this.m_IgnoredProperties.contains(desc[i].getName())) {
/* 171:346 */           if ((desc[i].getReadMethod() != null) && (desc[i].getWriteMethod() != null))
/* 172:    */           {
/* 173:351 */             OptionMetadata m = (OptionMetadata)desc[i].getReadMethod().getAnnotation(OptionMetadata.class);
/* 174:352 */             if (m == null) {
/* 175:353 */               m = (OptionMetadata)desc[i].getWriteMethod().getAnnotation(OptionMetadata.class);
/* 176:    */             }
/* 177:355 */             if (m == null)
/* 178:    */             {
/* 179:360 */               ProgrammaticProperty p = (ProgrammaticProperty)desc[i].getReadMethod().getAnnotation(ProgrammaticProperty.class);
/* 180:361 */               if (p == null) {
/* 181:362 */                 p = (ProgrammaticProperty)desc[i].getWriteMethod().getAnnotation(ProgrammaticProperty.class);
/* 182:    */               }
/* 183:364 */               if (p == null) {
/* 184:    */                 try
/* 185:    */                 {
/* 186:369 */                   cls.getMethod(desc[i].getName() + suffix, (Class[])null);
/* 187:    */                 }
/* 188:    */                 catch (Exception e)
/* 189:    */                 {
/* 190:371 */                   result = false;
/* 191:372 */                   missing.add(desc[i].getName() + suffix);
/* 192:    */                 }
/* 193:    */               }
/* 194:    */             }
/* 195:    */           }
/* 196:    */         }
/* 197:    */       }
/* 198:376 */       if (result) {
/* 199:377 */         println("yes");
/* 200:    */       } else {
/* 201:379 */         println("no (missing: " + missing + ")");
/* 202:    */       }
/* 203:    */     }
/* 204:    */     else
/* 205:    */     {
/* 206:383 */       println("maybe");
/* 207:    */     }
/* 208:386 */     return result;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public void doTests()
/* 212:    */   {
/* 213:395 */     println("Object: " + this.m_Object.getClass().getName() + "\n");
/* 214:    */     
/* 215:397 */     println("--> Tests");
/* 216:    */     
/* 217:399 */     this.m_Success = checkGlobalInfo();
/* 218:401 */     if (this.m_Success) {
/* 219:402 */       this.m_Success = checkToolTips();
/* 220:    */     }
/* 221:    */   }
/* 222:    */   
/* 223:    */   public String getRevision()
/* 224:    */   {
/* 225:413 */     return RevisionUtils.extract("$Revision: 11726 $");
/* 226:    */   }
/* 227:    */   
/* 228:    */   public static void main(String[] args)
/* 229:    */   {
/* 230:422 */     CheckGOE check = new CheckGOE();
/* 231:423 */     runCheck(check, args);
/* 232:424 */     if (check.getSuccess()) {
/* 233:425 */       System.exit(0);
/* 234:    */     } else {
/* 235:427 */       System.exit(1);
/* 236:    */     }
/* 237:    */   }
/* 238:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.CheckGOE
 * JD-Core Version:    0.7.0.1
 */