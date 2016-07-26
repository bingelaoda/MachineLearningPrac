/*   1:    */ package org.boon;
/*   2:    */ 
/*   3:    */ import java.lang.management.ManagementFactory;
/*   4:    */ import java.lang.reflect.Array;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.HashMap;
/*   7:    */ import java.util.LinkedHashMap;
/*   8:    */ import java.util.List;
/*   9:    */ import java.util.Map;
/*  10:    */ import java.util.Set;
/*  11:    */ import javax.management.Attribute;
/*  12:    */ import javax.management.AttributeList;
/*  13:    */ import javax.management.DynamicMBean;
/*  14:    */ import javax.management.MBeanAttributeInfo;
/*  15:    */ import javax.management.MBeanInfo;
/*  16:    */ import javax.management.MBeanServer;
/*  17:    */ import javax.management.NotCompliantMBeanException;
/*  18:    */ import javax.management.ObjectName;
/*  19:    */ import javax.management.StandardMBean;
/*  20:    */ import javax.management.openmbean.CompositeData;
/*  21:    */ import javax.management.openmbean.CompositeType;
/*  22:    */ import javax.management.openmbean.TabularData;
/*  23:    */ 
/*  24:    */ public class MBeans
/*  25:    */ {
/*  26:    */   public static String toJson()
/*  27:    */   {
/*  28: 48 */     MBeanServer server = ManagementFactory.getPlatformMBeanServer();
/*  29: 49 */     return toJson(server);
/*  30:    */   }
/*  31:    */   
/*  32:    */   public static String toJson(MBeanServer server)
/*  33:    */   {
/*  34: 55 */     Set<ObjectName> objectNames = server.queryNames(null, null);
/*  35:    */     
/*  36: 57 */     Map<String, Map<String, Object>> map = new LinkedHashMap();
/*  37: 59 */     for (ObjectName name : objectNames) {
/*  38: 61 */       map.put(name.toString(), map(server, name));
/*  39:    */     }
/*  40: 65 */     return Boon.toJson(map);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public static Map<String, Object> map(ObjectName name)
/*  44:    */   {
/*  45: 69 */     MBeanServer server = ManagementFactory.getPlatformMBeanServer();
/*  46: 70 */     return map(server, name);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public static Map<String, Object> map(MBeanServer server, ObjectName name)
/*  50:    */   {
/*  51: 78 */     Exceptions.requireNonNull(server, "server cannot be null");
/*  52: 79 */     Exceptions.requireNonNull(name, "name cannot be null");
/*  53:    */     
/*  54:    */ 
/*  55:    */ 
/*  56:    */ 
/*  57: 84 */     MBeanInfo info = null;
/*  58:    */     try
/*  59:    */     {
/*  60: 89 */       info = server.getMBeanInfo(name);
/*  61:    */       
/*  62: 91 */       String[] attributeNames = getAttributeNames(info);
/*  63: 92 */       Map<String, Object> result = new HashMap(attributeNames.length);
/*  64:    */       
/*  65:    */ 
/*  66: 95 */       AttributeList attributeList = server.getAttributes(name, attributeNames);
/*  67: 98 */       for (Object obj : attributeList)
/*  68:    */       {
/*  69: 99 */         Attribute attribute = (Attribute)obj;
/*  70:100 */         result.put(attribute.getName(), convertValue(attribute.getValue()));
/*  71:    */       }
/*  72:103 */       return result;
/*  73:    */     }
/*  74:    */     catch (Exception ex)
/*  75:    */     {
/*  76:107 */       return (Map)Exceptions.handle(Map.class, String.format("Unable to turn mbean into map %s ", new Object[] { name.getCanonicalName() }), ex);
/*  77:    */     }
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static String[] getAttributeNames(MBeanInfo info)
/*  81:    */   {
/*  82:115 */     MBeanAttributeInfo[] attributes = info.getAttributes();
/*  83:116 */     String[] attributeNames = new String[attributes.length];
/*  84:118 */     for (int index = 0; index < attributes.length; index++) {
/*  85:120 */       attributeNames[index] = attributes[index].getName();
/*  86:    */     }
/*  87:122 */     return attributeNames;
/*  88:    */   }
/*  89:    */   
/*  90:    */   private static Object convertValue(Object value)
/*  91:    */   {
/*  92:129 */     if (value == null) {
/*  93:130 */       value = "null";
/*  94:    */     }
/*  95:135 */     if (value.getClass().isArray()) {
/*  96:137 */       value = convertFromArrayToList(value);
/*  97:139 */     } else if ((value instanceof CompositeData)) {
/*  98:141 */       value = convertFromCompositeDataToToMap(value);
/*  99:143 */     } else if ((value instanceof TabularData)) {
/* 100:144 */       value = convertFromTabularDataToMap(value);
/* 101:    */     }
/* 102:147 */     return value;
/* 103:    */   }
/* 104:    */   
/* 105:    */   private static Object convertFromTabularDataToMap(Object value)
/* 106:    */   {
/* 107:151 */     TabularData data = (TabularData)value;
/* 108:    */     
/* 109:153 */     Set<List<?>> keys = data.keySet();
/* 110:    */     
/* 111:155 */     Map<String, Object> map = new HashMap();
/* 112:156 */     for (List<?> key : keys)
/* 113:    */     {
/* 114:157 */       Object subValue = convertValue(data.get(key.toArray()));
/* 115:159 */       if (key.size() == 1) {
/* 116:160 */         map.put(convertValue(key.get(0)).toString(), subValue);
/* 117:    */       } else {
/* 118:162 */         map.put(convertValue(key).toString(), subValue);
/* 119:    */       }
/* 120:    */     }
/* 121:166 */     value = map;
/* 122:167 */     return value;
/* 123:    */   }
/* 124:    */   
/* 125:    */   private static Object convertFromCompositeDataToToMap(Object value)
/* 126:    */   {
/* 127:171 */     CompositeData data = (CompositeData)value;
/* 128:172 */     Map<String, Object> map = new HashMap();
/* 129:173 */     Set<String> keySet = data.getCompositeType().keySet();
/* 130:175 */     for (String key : keySet) {
/* 131:176 */       map.put(key, convertValue(data.get(key)));
/* 132:    */     }
/* 133:179 */     value = map;
/* 134:180 */     return value;
/* 135:    */   }
/* 136:    */   
/* 137:    */   private static Object convertFromArrayToList(Object value)
/* 138:    */   {
/* 139:184 */     List<Object> list = new ArrayList();
/* 140:    */     
/* 141:186 */     int length = Array.getLength(value);
/* 142:188 */     for (int index = 0; index < length; index++) {
/* 143:189 */       list.add(convertValue(Array.get(value, index)));
/* 144:    */     }
/* 145:192 */     value = list;
/* 146:193 */     return value;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public static DynamicMBean createMBean(Object instance, Class<?> managedInterface)
/* 150:    */   {
/* 151:199 */     Exceptions.requireNonNull(instance, "instance cannot be null");
/* 152:200 */     Exceptions.requireNonNull(managedInterface, "managedInterface cannot be null");
/* 153:    */     try
/* 154:    */     {
/* 155:206 */       return new StandardMBean(instance, managedInterface);
/* 156:    */     }
/* 157:    */     catch (NotCompliantMBeanException ex)
/* 158:    */     {
/* 159:209 */       return (DynamicMBean)Exceptions.handle(DynamicMBean.class, String.format("createMBean unable to register %s under interface %s", new Object[] { instance.getClass().getName(), managedInterface.getClass().getName() }), ex);
/* 160:    */     }
/* 161:    */   }
/* 162:    */   
/* 163:    */   public static void registerMBean(String prefix, String name, Object mbean)
/* 164:    */   {
/* 165:219 */     Exceptions.requireNonNull(prefix, "prefix can't be null");
/* 166:220 */     Exceptions.requireNonNull(name, "name can't be null");
/* 167:221 */     Exceptions.requireNonNull(mbean, "mbean can't be null");
/* 168:    */     
/* 169:223 */     String nameOfBean = nameOfBean = String.format("%s.%s:type=%s", new Object[] { prefix, mbean.getClass().getSimpleName(), name });
/* 170:    */     try
/* 171:    */     {
/* 172:230 */       ObjectName objectName = new ObjectName(nameOfBean);
/* 173:    */       
/* 174:232 */       MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
/* 175:    */       
/* 176:234 */       beanServer.registerMBean(mbean, objectName);
/* 177:    */     }
/* 178:    */     catch (Exception ex)
/* 179:    */     {
/* 180:237 */       Exceptions.handle(String.format("registerMBean %s %s %s %s", new Object[] { prefix, name, mbean, nameOfBean }), ex);
/* 181:    */     }
/* 182:    */   }
/* 183:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.MBeans
 * JD-Core Version:    0.7.0.1
 */