/*   1:    */ package org.boon.core.value;
/*   2:    */ 
/*   3:    */ import java.util.AbstractList;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Iterator;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.ListIterator;
/*   8:    */ import org.boon.core.Value;
/*   9:    */ 
/*  10:    */ public class ValueList
/*  11:    */   extends AbstractList<Object>
/*  12:    */   implements List<Object>
/*  13:    */ {
/*  14: 37 */   List<Object> list = new ArrayList(5);
/*  15:    */   private final boolean lazyChop;
/*  16: 40 */   boolean converted = false;
/*  17:    */   
/*  18:    */   public ValueList(boolean lazyChop)
/*  19:    */   {
/*  20: 45 */     this.lazyChop = lazyChop;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public Object get(int index)
/*  24:    */   {
/*  25: 51 */     Object obj = this.list.get(index);
/*  26: 53 */     if ((obj instanceof Value))
/*  27:    */     {
/*  28: 54 */       obj = convert((Value)obj);
/*  29: 55 */       this.list.set(index, obj);
/*  30:    */     }
/*  31: 58 */     chopIfNeeded(obj);
/*  32: 59 */     return obj;
/*  33:    */   }
/*  34:    */   
/*  35:    */   private Object convert(Value value)
/*  36:    */   {
/*  37: 65 */     return value.toValue();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public int size()
/*  41:    */   {
/*  42: 70 */     return this.list.size();
/*  43:    */   }
/*  44:    */   
/*  45:    */   public Iterator<Object> iterator()
/*  46:    */   {
/*  47: 76 */     convertAllIfNeeded();
/*  48: 77 */     return this.list.iterator();
/*  49:    */   }
/*  50:    */   
/*  51:    */   private void convertAllIfNeeded()
/*  52:    */   {
/*  53: 82 */     if (!this.converted)
/*  54:    */     {
/*  55: 83 */       this.converted = true;
/*  56: 84 */       for (int index = 0; index < this.list.size(); index++) {
/*  57: 85 */         get(index);
/*  58:    */       }
/*  59:    */     }
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void clear()
/*  63:    */   {
/*  64: 94 */     this.list.clear();
/*  65:    */   }
/*  66:    */   
/*  67:    */   public boolean add(Object obj)
/*  68:    */   {
/*  69: 99 */     return this.list.add(obj);
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void chopList()
/*  73:    */   {
/*  74:105 */     for (Object obj : this.list) {
/*  75:106 */       if (obj != null) {
/*  76:108 */         if ((obj instanceof Value))
/*  77:    */         {
/*  78:109 */           Value value = (Value)obj;
/*  79:110 */           if (value.isContainer()) {
/*  80:111 */             chopContainer(value);
/*  81:    */           } else {
/*  82:113 */             value.chop();
/*  83:    */           }
/*  84:    */         }
/*  85:    */       }
/*  86:    */     }
/*  87:    */   }
/*  88:    */   
/*  89:    */   private void chopIfNeeded(Object object)
/*  90:    */   {
/*  91:120 */     if (this.lazyChop) {
/*  92:121 */       if ((object instanceof LazyValueMap))
/*  93:    */       {
/*  94:122 */         LazyValueMap m = (LazyValueMap)object;
/*  95:123 */         m.chopMap();
/*  96:    */       }
/*  97:124 */       else if ((object instanceof ValueList))
/*  98:    */       {
/*  99:125 */         ValueList list = (ValueList)object;
/* 100:126 */         list.chopList();
/* 101:    */       }
/* 102:    */     }
/* 103:    */   }
/* 104:    */   
/* 105:    */   void chopContainer(Value value)
/* 106:    */   {
/* 107:134 */     Object obj = value.toValue();
/* 108:135 */     if ((obj instanceof LazyValueMap))
/* 109:    */     {
/* 110:136 */       LazyValueMap map = (LazyValueMap)obj;
/* 111:137 */       map.chopMap();
/* 112:    */     }
/* 113:138 */     else if ((obj instanceof ValueList))
/* 114:    */     {
/* 115:139 */       ValueList list = (ValueList)obj;
/* 116:140 */       list.chopList();
/* 117:    */     }
/* 118:    */   }
/* 119:    */   
/* 120:    */   public ListIterator<Object> listIterator()
/* 121:    */   {
/* 122:147 */     convertAllIfNeeded();
/* 123:148 */     return this.list.listIterator();
/* 124:    */   }
/* 125:    */   
/* 126:    */   public List<Object> list()
/* 127:    */   {
/* 128:152 */     return this.list;
/* 129:    */   }
/* 130:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.value.ValueList
 * JD-Core Version:    0.7.0.1
 */