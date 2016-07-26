/*   1:    */ package org.boon.di;
/*   2:    */ 
/*   3:    */ import org.boon.core.Supplier;
/*   4:    */ 
/*   5:    */ public class ProviderInfo<T>
/*   6:    */ {
/*   7:    */   private String name;
/*   8:    */   private Class<T> type;
/*   9:    */   private Supplier<T> supplier;
/*  10:    */   private T object;
/*  11:    */   private boolean postConstructCalled;
/*  12:    */   boolean prototype;
/*  13:    */   
/*  14:    */   public ProviderInfo(Class<T> type)
/*  15:    */   {
/*  16: 51 */     this.type = type;
/*  17:    */   }
/*  18:    */   
/*  19:    */   public ProviderInfo(String name, Class<T> type, Supplier<T> supplier, T object)
/*  20:    */   {
/*  21: 59 */     this.name = name;
/*  22: 60 */     this.type = type;
/*  23: 61 */     this.supplier = supplier;
/*  24: 62 */     this.object = object;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public ProviderInfo(String name, Class<T> type, Supplier<T> supplier, T object, boolean prototype)
/*  28:    */   {
/*  29: 67 */     this.name = name;
/*  30: 68 */     this.type = type;
/*  31: 69 */     this.supplier = supplier;
/*  32: 70 */     this.object = object;
/*  33: 71 */     this.prototype = prototype;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public static <T> ProviderInfo<T> providerOf(Class<T> type, Supplier<T> supplier)
/*  37:    */   {
/*  38: 75 */     return new ProviderInfo(null, type, supplier, null);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public static <T> ProviderInfo<T> providerOf(String name, Class<T> type, Supplier<T> supplier)
/*  42:    */   {
/*  43: 80 */     return new ProviderInfo(name, type, supplier, null);
/*  44:    */   }
/*  45:    */   
/*  46:    */   public static <T> ProviderInfo<T> providerOf(String name, Supplier<T> supplier)
/*  47:    */   {
/*  48: 85 */     return new ProviderInfo(name, null, supplier, null);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static <T> ProviderInfo<T> providerOf(String name, Class<T> type)
/*  52:    */   {
/*  53: 90 */     return new ProviderInfo(name, type, null, null);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public static <T> ProviderInfo<T> providerOf(Class<T> type)
/*  57:    */   {
/*  58: 95 */     return new ProviderInfo(null, type, null, null);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public static <T> ProviderInfo<T> providerOf(String name, T object)
/*  62:    */   {
/*  63:100 */     return new ProviderInfo(name, null, null, object);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public static <T> ProviderInfo<T> providerOf(T object)
/*  67:    */   {
/*  68:105 */     return new ProviderInfo(null, null, null, object);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public static <T> ProviderInfo<T> objectProviderOf(T object)
/*  72:    */   {
/*  73:110 */     return new ProviderInfo(null, null, null, object);
/*  74:    */   }
/*  75:    */   
/*  76:    */   public static <T> ProviderInfo<T> prototypeProviderOf(T object)
/*  77:    */   {
/*  78:115 */     return new ProviderInfo(null, null, null, object, true);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public static <T> ProviderInfo<T> provider(Object name, Object value)
/*  82:    */   {
/*  83:    */     ProviderInfo info;
/*  84:    */     ProviderInfo info;
/*  85:122 */     if ((value instanceof ProviderInfo))
/*  86:    */     {
/*  87:123 */       ProviderInfo valueInfo = (ProviderInfo)value;
/*  88:124 */       info = new ProviderInfo(name.toString(), valueInfo.type(), valueInfo.supplier(), valueInfo.value());
/*  89:    */     }
/*  90:    */     else
/*  91:    */     {
/*  92:    */       ProviderInfo info;
/*  93:125 */       if ((value instanceof Class))
/*  94:    */       {
/*  95:126 */         info = new ProviderInfo(name.toString(), (Class)value, null, null);
/*  96:    */       }
/*  97:    */       else
/*  98:    */       {
/*  99:    */         ProviderInfo info;
/* 100:127 */         if ((value instanceof Supplier))
/* 101:    */         {
/* 102:128 */           info = new ProviderInfo(name.toString(), null, (Supplier)value, null);
/* 103:    */         }
/* 104:    */         else
/* 105:    */         {
/* 106:    */           ProviderInfo info;
/* 107:130 */           if (value == null) {
/* 108:131 */             info = new ProviderInfo(name.toString(), null, null, value);
/* 109:    */           } else {
/* 110:133 */             info = new ProviderInfo(name.toString(), value.getClass(), null, value);
/* 111:    */           }
/* 112:    */         }
/* 113:    */       }
/* 114:    */     }
/* 115:136 */     return info;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public Class<T> type()
/* 119:    */   {
/* 120:140 */     return this.type;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public Supplier<T> supplier()
/* 124:    */   {
/* 125:144 */     return this.supplier;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public String name()
/* 129:    */   {
/* 130:148 */     return this.name;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public Object value()
/* 134:    */   {
/* 135:152 */     return this.object;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public boolean isPostConstructCalled()
/* 139:    */   {
/* 140:157 */     return this.postConstructCalled;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void setPostConstructCalled(boolean postConstructCalled)
/* 144:    */   {
/* 145:161 */     this.postConstructCalled = postConstructCalled;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public boolean prototype()
/* 149:    */   {
/* 150:166 */     return this.prototype;
/* 151:    */   }
/* 152:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.di.ProviderInfo
 * JD-Core Version:    0.7.0.1
 */