/*   1:    */ package org.boon.validation;
/*   2:    */ 
/*   3:    */ import java.text.MessageFormat;
/*   4:    */ import java.util.ArrayDeque;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Arrays;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.Map;
/*   9:    */ import java.util.MissingResourceException;
/*  10:    */ import java.util.ResourceBundle;
/*  11:    */ import org.boon.messages.ResourceBundleLocator;
/*  12:    */ 
/*  13:    */ public class ValidationContext
/*  14:    */ {
/*  15: 47 */   private ArrayDeque<String> bindingPath = new ArrayDeque();
/*  16:    */   private ResourceBundleLocator resourceBundleLocator;
/*  17:    */   private Object parentObject;
/*  18:    */   private Map<String, Object> params;
/*  19: 72 */   private static ThreadLocal<ValidationContext> holder = new ThreadLocal();
/*  20: 79 */   private String i18nMarker = "{";
/*  21:    */   private String currentSubject;
/*  22:    */   Map<String, Object> objectRegistry;
/*  23:    */   
/*  24:    */   public static ValidationContext getCurrentInstance()
/*  25:    */   {
/*  26: 90 */     return (ValidationContext)holder.get();
/*  27:    */   }
/*  28:    */   
/*  29:    */   protected void register(ValidationContext context)
/*  30:    */   {
/*  31:101 */     holder.set(context);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public Object getParentObject()
/*  35:    */   {
/*  36:111 */     return this.parentObject;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setParentObject(Object parentObject)
/*  40:    */   {
/*  41:121 */     this.parentObject = parentObject;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public Map<String, Object> getParams()
/*  45:    */   {
/*  46:130 */     return this.params;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setParams(Map<String, Object> params)
/*  50:    */   {
/*  51:134 */     this.params = params;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public Object getProposedPropertyValue(String propertyName)
/*  55:    */   {
/*  56:145 */     return null;
/*  57:    */   }
/*  58:    */   
/*  59:    */   private String calculateBindingPath()
/*  60:    */   {
/*  61:149 */     StringBuilder builder = new StringBuilder(255);
/*  62:150 */     int index = 0;
/*  63:151 */     for (String component : this.bindingPath)
/*  64:    */     {
/*  65:152 */       index++;
/*  66:153 */       builder.append(component);
/*  67:154 */       if (index != this.bindingPath.size()) {
/*  68:155 */         builder.append('.');
/*  69:    */       }
/*  70:    */     }
/*  71:158 */     return builder.toString();
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void pop()
/*  75:    */   {
/*  76:162 */     this.bindingPath.pop();
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void pushProperty(String component)
/*  80:    */   {
/*  81:166 */     this.bindingPath.push(component);
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void pushObject(Object object)
/*  85:    */   {
/*  86:170 */     String simpleName = object.getClass().getSimpleName();
/*  87:171 */     simpleName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1, simpleName.length());
/*  88:172 */     this.bindingPath.push(simpleName);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public static String getBindingPath()
/*  92:    */   {
/*  93:176 */     if (getCurrentInstance() != null) {
/*  94:177 */       return getCurrentInstance().calculateBindingPath();
/*  95:    */     }
/*  96:179 */     return "";
/*  97:    */   }
/*  98:    */   
/*  99:    */   public static ValidationContext create()
/* 100:    */   {
/* 101:183 */     holder.set(new ValidationContext());
/* 102:184 */     return get();
/* 103:    */   }
/* 104:    */   
/* 105:    */   public static ValidationContext get()
/* 106:    */   {
/* 107:188 */     return (ValidationContext)holder.get();
/* 108:    */   }
/* 109:    */   
/* 110:    */   public static void destroy()
/* 111:    */   {
/* 112:192 */     holder.set(null);
/* 113:    */   }
/* 114:    */   
/* 115:    */   public Map<String, Object> getObjectRegistry()
/* 116:    */   {
/* 117:198 */     return this.objectRegistry;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void setObjectRegistry(Map<String, Object> objectRegistry)
/* 121:    */   {
/* 122:202 */     this.objectRegistry = objectRegistry;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public String getMessage(String key)
/* 126:    */   {
/* 127:207 */     String message = doGetMessageFromBundle(key);
/* 128:    */     
/* 129:209 */     return message == null ? key : message;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public String getCurrentSubject()
/* 133:    */   {
/* 134:215 */     return this.currentSubject;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public void setCurrentSubject(String currentSubject)
/* 138:    */   {
/* 139:219 */     this.currentSubject = currentSubject;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public String createMessage(String message, String subject, Object[] actualArgs)
/* 143:    */   {
/* 144:224 */     List argumentList = new ArrayList(Arrays.asList(actualArgs));
/* 145:228 */     if (subject != null) {
/* 146:229 */       argumentList.add(0, getMessage(subject));
/* 147:    */     }
/* 148:    */     try
/* 149:    */     {
/* 150:234 */       return MessageFormat.format(message, argumentList.toArray());
/* 151:    */     }
/* 152:    */     catch (Exception ex) {}
/* 153:237 */     return message;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void setResourceBundleLocator(ResourceBundleLocator resourceBundleLocator)
/* 157:    */   {
/* 158:244 */     this.resourceBundleLocator = resourceBundleLocator;
/* 159:    */   }
/* 160:    */   
/* 161:    */   protected ResourceBundleLocator getResourceBundleLocator()
/* 162:    */   {
/* 163:249 */     return this.resourceBundleLocator;
/* 164:    */   }
/* 165:    */   
/* 166:    */   private String doGetMessageFromBundle(String key)
/* 167:    */   {
/* 168:262 */     if (this.resourceBundleLocator == null) {
/* 169:263 */       return null;
/* 170:    */     }
/* 171:266 */     ResourceBundle bundle = this.resourceBundleLocator.getBundle();
/* 172:268 */     if (bundle == null) {
/* 173:269 */       return null;
/* 174:    */     }
/* 175:273 */     String message = null;
/* 176:278 */     if (key.startsWith(this.i18nMarker)) {
/* 177:    */       try
/* 178:    */       {
/* 179:280 */         key = key.substring(1, key.length() - 1);
/* 180:281 */         message = lookupMessageInBundle(key, bundle, message);
/* 181:    */       }
/* 182:    */       catch (MissingResourceException mre)
/* 183:    */       {
/* 184:283 */         message = key;
/* 185:    */       }
/* 186:291 */     } else if (key.contains(".")) {
/* 187:    */       try
/* 188:    */       {
/* 189:293 */         message = lookupMessageInBundle(key, bundle, message);
/* 190:    */       }
/* 191:    */       catch (MissingResourceException mre)
/* 192:    */       {
/* 193:295 */         message = key;
/* 194:    */       }
/* 195:    */     } else {
/* 196:298 */       message = key;
/* 197:    */     }
/* 198:301 */     return message;
/* 199:    */   }
/* 200:    */   
/* 201:    */   private String lookupMessageInBundle(String key, ResourceBundle bundle, String message)
/* 202:    */   {
/* 203:307 */     if (getCurrentSubject() != null) {
/* 204:    */       try
/* 205:    */       {
/* 206:309 */         message = bundle.getString(key + "." + getCurrentSubject());
/* 207:    */       }
/* 208:    */       catch (MissingResourceException mre)
/* 209:    */       {
/* 210:312 */         message = bundle.getString(key);
/* 211:    */       }
/* 212:    */     } else {
/* 213:315 */       return bundle.getString(key);
/* 214:    */     }
/* 215:317 */     return message;
/* 216:    */   }
/* 217:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.ValidationContext
 * JD-Core Version:    0.7.0.1
 */