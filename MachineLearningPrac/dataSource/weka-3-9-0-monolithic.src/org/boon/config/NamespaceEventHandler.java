/*   1:    */ package org.boon.config;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.List;
/*   5:    */ import java.util.Map;
/*   6:    */ import org.boon.Boon;
/*   7:    */ import org.boon.core.Value;
/*   8:    */ import org.boon.json.JsonParserEvents;
/*   9:    */ 
/*  10:    */ public class NamespaceEventHandler
/*  11:    */   implements JsonParserEvents
/*  12:    */ {
/*  13:    */   private final MetaConfigEvents events;
/*  14:    */   private String namespace;
/*  15:    */   private List<String> include;
/*  16: 50 */   boolean continueParse = true;
/*  17: 52 */   boolean inMeta = false;
/*  18:    */   
/*  19:    */   public List<String> include()
/*  20:    */   {
/*  21: 55 */     return this.include == null ? Collections.EMPTY_LIST : this.include;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public NamespaceEventHandler(String namespace, MetaConfigEvents events)
/*  25:    */   {
/*  26: 59 */     this.namespace = namespace;
/*  27: 60 */     this.events = events;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public boolean objectStart(int index)
/*  31:    */   {
/*  32: 65 */     return this.continueParse;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public boolean objectEnd(int index, Map<String, Object> object)
/*  36:    */   {
/*  37: 71 */     return this.continueParse;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public boolean objectFieldName(int index, Map<String, Object> map, CharSequence name)
/*  41:    */   {
/*  42: 76 */     if ((name != null) && (name.toString().equals("META"))) {
/*  43: 77 */       this.inMeta = true;
/*  44:    */     }
/*  45: 79 */     return this.continueParse;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public boolean objectField(int index, Map<String, Object> map, CharSequence name, Object value)
/*  49:    */   {
/*  50: 85 */     if ((this.inMeta) && (name != null) && (name.toString().equals("namespace")) && ((value instanceof CharSequence)) && (!value.toString().equals(this.namespace))) {
/*  51: 88 */       return this.continueParse = 0;
/*  52:    */     }
/*  53: 91 */     if ((name != null) && (name.toString().equals("META")))
/*  54:    */     {
/*  55: 93 */       Map<String, Object> meta = toMap(value);
/*  56: 94 */       if (meta.containsKey("include"))
/*  57:    */       {
/*  58: 95 */         this.include = toList(meta.get("include"));
/*  59: 96 */         Boon.puts(new Object[] { "include", this.include });
/*  60:    */       }
/*  61: 98 */       this.inMeta = false;
/*  62: 99 */       if (this.events != null) {
/*  63:100 */         this.continueParse = this.events.parsedMeta(meta);
/*  64:    */       }
/*  65:    */     }
/*  66:104 */     return this.continueParse;
/*  67:    */   }
/*  68:    */   
/*  69:    */   private List<String> toList(Object include)
/*  70:    */   {
/*  71:108 */     if ((include instanceof Value))
/*  72:    */     {
/*  73:109 */       Value value = (Value)include;
/*  74:110 */       return (List)value.toValue();
/*  75:    */     }
/*  76:112 */     return (List)include;
/*  77:    */   }
/*  78:    */   
/*  79:    */   private Map<String, Object> toMap(Object field)
/*  80:    */   {
/*  81:118 */     if ((field instanceof Value))
/*  82:    */     {
/*  83:119 */       Value value = (Value)field;
/*  84:120 */       return (Map)value.toValue();
/*  85:    */     }
/*  86:122 */     return (Map)field;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public boolean arrayStart(int index)
/*  90:    */   {
/*  91:128 */     return this.continueParse;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public boolean arrayEnd(int index, List<Object> list)
/*  95:    */   {
/*  96:133 */     return this.continueParse;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public boolean arrayItem(int index, List<Object> list, Object item)
/* 100:    */   {
/* 101:138 */     return this.continueParse;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public boolean number(int startIndex, int endIndex, Number number)
/* 105:    */   {
/* 106:143 */     return this.continueParse;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public boolean string(int startIndex, int endIndex, CharSequence string)
/* 110:    */   {
/* 111:148 */     return this.continueParse;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public boolean bool(int endIndex, boolean value)
/* 115:    */   {
/* 116:153 */     return this.continueParse;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public boolean nullValue(int endIndex)
/* 120:    */   {
/* 121:158 */     return this.continueParse;
/* 122:    */   }
/* 123:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.config.NamespaceEventHandler
 * JD-Core Version:    0.7.0.1
 */