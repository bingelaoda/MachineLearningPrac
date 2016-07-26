/*  1:   */ package org.boon.json.serializers.impl;
/*  2:   */ 
/*  3:   */ import java.util.Collection;
/*  4:   */ import java.util.Map;
/*  5:   */ import java.util.Set;
/*  6:   */ import org.boon.Boon;
/*  7:   */ import org.boon.core.TypeType;
/*  8:   */ import org.boon.json.serializers.CustomObjectSerializer;
/*  9:   */ import org.boon.json.serializers.JsonSerializerInternal;
/* 10:   */ import org.boon.primitive.CharBuf;
/* 11:   */ 
/* 12:   */ public class SerializeUtils
/* 13:   */ {
/* 14:   */   public static void handleInstance(JsonSerializerInternal jsonSerializer, Object obj, CharBuf builder, Map<Class, CustomObjectSerializer> overrideMap, Set<Class> noHandle, boolean typeInfo, TypeType type)
/* 15:   */   {
/* 16:24 */     if (overrideMap != null)
/* 17:   */     {
/* 18:25 */       Class<?> cls = Boon.cls(obj);
/* 19:26 */       if ((cls != null) && (!cls.isPrimitive()) && (!noHandle.contains(cls)))
/* 20:   */       {
/* 21:27 */         CustomObjectSerializer customObjectSerializer = (CustomObjectSerializer)overrideMap.get(cls);
/* 22:28 */         if (customObjectSerializer != null)
/* 23:   */         {
/* 24:29 */           customObjectSerializer.serializeObject(jsonSerializer, obj, builder);
/* 25:30 */           return;
/* 26:   */         }
/* 27:32 */         customObjectSerializer = (CustomObjectSerializer)overrideMap.get(cls.getSuperclass());
/* 28:33 */         if (customObjectSerializer != null)
/* 29:   */         {
/* 30:34 */           overrideMap.put(cls.getSuperclass(), customObjectSerializer);
/* 31:35 */           customObjectSerializer.serializeObject(jsonSerializer, obj, builder);
/* 32:36 */           return;
/* 33:   */         }
/* 34:39 */         Class<?>[] interfaces = cls.getInterfaces();
/* 35:40 */         for (Class interf : interfaces)
/* 36:   */         {
/* 37:42 */           customObjectSerializer = (CustomObjectSerializer)overrideMap.get(interf);
/* 38:43 */           if (customObjectSerializer != null)
/* 39:   */           {
/* 40:44 */             overrideMap.put(interf, customObjectSerializer);
/* 41:45 */             customObjectSerializer.serializeObject(jsonSerializer, obj, builder);
/* 42:46 */             return;
/* 43:   */           }
/* 44:   */         }
/* 45:51 */         noHandle.add(cls);
/* 46:   */       }
/* 47:   */     }
/* 48:57 */     switch (1.$SwitchMap$org$boon$core$TypeType[type.ordinal()])
/* 49:   */     {
/* 50:   */     case 1: 
/* 51:59 */       jsonSerializer.serializeMap((Map)obj, builder);
/* 52:60 */       return;
/* 53:   */     case 2: 
/* 54:   */     case 3: 
/* 55:   */     case 4: 
/* 56:64 */       jsonSerializer.serializeCollection((Collection)obj, builder);
/* 57:65 */       return;
/* 58:   */     case 5: 
/* 59:67 */       jsonSerializer.serializeInstance(obj, builder, typeInfo);
/* 60:68 */       return;
/* 61:   */     case 6: 
/* 62:   */     case 7: 
/* 63:71 */       jsonSerializer.serializeSubtypeInstance(obj, builder);
/* 64:72 */       return;
/* 65:   */     }
/* 66:75 */     jsonSerializer.serializeUnknown(obj, builder);
/* 67:   */   }
/* 68:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.impl.SerializeUtils
 * JD-Core Version:    0.7.0.1
 */