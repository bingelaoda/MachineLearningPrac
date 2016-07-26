/*   1:    */ package org.boon.criteria;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.List;
/*   5:    */ import org.boon.Lists;
/*   6:    */ import org.boon.datarepo.ObjectEditor;
/*   7:    */ 
/*   8:    */ public abstract class Update
/*   9:    */   implements Serializable
/*  10:    */ {
/*  11:    */   private String name;
/*  12:    */   
/*  13:    */   public String getName()
/*  14:    */   {
/*  15: 43 */     return this.name;
/*  16:    */   }
/*  17:    */   
/*  18:    */   public abstract void doSet(ObjectEditor paramObjectEditor, Object paramObject);
/*  19:    */   
/*  20:    */   public static Update set(String name, final int value)
/*  21:    */   {
/*  22: 50 */     new Update()
/*  23:    */     {
/*  24:    */       public void doSet(ObjectEditor repo, Object item)
/*  25:    */       {
/*  26: 53 */         repo.modify(item, this.val$name, value);
/*  27:    */       }
/*  28:    */     };
/*  29:    */   }
/*  30:    */   
/*  31:    */   public static Update delete()
/*  32:    */   {
/*  33: 59 */     new Update()
/*  34:    */     {
/*  35:    */       public void doSet(ObjectEditor repo, Object item)
/*  36:    */       {
/*  37: 62 */         repo.delete(item);
/*  38:    */       }
/*  39:    */     };
/*  40:    */   }
/*  41:    */   
/*  42:    */   public static Update incInt(String name)
/*  43:    */   {
/*  44: 68 */     new Update()
/*  45:    */     {
/*  46:    */       public void doSet(ObjectEditor repo, Object item)
/*  47:    */       {
/*  48: 71 */         int v = repo.getInt(item, this.val$name);
/*  49: 72 */         v++;
/*  50: 73 */         repo.modify(item, this.val$name, v);
/*  51:    */       }
/*  52:    */     };
/*  53:    */   }
/*  54:    */   
/*  55:    */   public static Update incPercent(String name, final int percent)
/*  56:    */   {
/*  57: 79 */     new Update()
/*  58:    */     {
/*  59:    */       public void doSet(ObjectEditor repo, Object item)
/*  60:    */       {
/*  61: 84 */         int value = repo.getInt(item, this.val$name);
/*  62: 85 */         double dvalue = value;
/*  63: 86 */         double dprecent = percent / 100.0D;
/*  64: 87 */         dvalue += dvalue * dprecent;
/*  65: 88 */         value = (int)dvalue;
/*  66: 89 */         repo.modify(item, this.val$name, value);
/*  67:    */       }
/*  68:    */     };
/*  69:    */   }
/*  70:    */   
/*  71:    */   public static Update set(String name, final long value)
/*  72:    */   {
/*  73: 95 */     new Update()
/*  74:    */     {
/*  75:    */       public void doSet(ObjectEditor repo, Object item)
/*  76:    */       {
/*  77: 98 */         repo.modify(item, this.val$name, value);
/*  78:    */       }
/*  79:    */     };
/*  80:    */   }
/*  81:    */   
/*  82:    */   public static Update set(String name, final Object value)
/*  83:    */   {
/*  84:104 */     new Update()
/*  85:    */     {
/*  86:    */       public void doSet(ObjectEditor repo, Object item)
/*  87:    */       {
/*  88:107 */         repo.modify(item, this.val$name, value);
/*  89:    */       }
/*  90:    */     };
/*  91:    */   }
/*  92:    */   
/*  93:    */   public static Update set(String name, final byte value)
/*  94:    */   {
/*  95:113 */     new Update()
/*  96:    */     {
/*  97:    */       public void doSet(ObjectEditor repo, Object item)
/*  98:    */       {
/*  99:116 */         repo.modify(item, this.val$name, value);
/* 100:    */       }
/* 101:    */     };
/* 102:    */   }
/* 103:    */   
/* 104:    */   public static Update set(String name, final float value)
/* 105:    */   {
/* 106:122 */     new Update()
/* 107:    */     {
/* 108:    */       public void doSet(ObjectEditor repo, Object item)
/* 109:    */       {
/* 110:125 */         repo.modify(item, this.val$name, value);
/* 111:    */       }
/* 112:    */     };
/* 113:    */   }
/* 114:    */   
/* 115:    */   public static Update set(String name, final char value)
/* 116:    */   {
/* 117:131 */     new Update()
/* 118:    */     {
/* 119:    */       public void doSet(ObjectEditor repo, Object item)
/* 120:    */       {
/* 121:134 */         repo.modify(item, this.val$name, value);
/* 122:    */       }
/* 123:    */     };
/* 124:    */   }
/* 125:    */   
/* 126:    */   public static Update set(String name, final String value)
/* 127:    */   {
/* 128:140 */     new Update()
/* 129:    */     {
/* 130:    */       public void doSet(ObjectEditor repo, Object item)
/* 131:    */       {
/* 132:143 */         repo.modify(item, this.val$name, value);
/* 133:    */       }
/* 134:    */     };
/* 135:    */   }
/* 136:    */   
/* 137:    */   public static List<Update> update(Update... values)
/* 138:    */   {
/* 139:149 */     return Lists.list(values);
/* 140:    */   }
/* 141:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.criteria.Update
 * JD-Core Version:    0.7.0.1
 */