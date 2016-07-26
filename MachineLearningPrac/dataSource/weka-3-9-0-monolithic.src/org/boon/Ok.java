/*   1:    */ package org.boon;
/*   2:    */ 
/*   3:    */ import java.util.Collection;
/*   4:    */ import java.util.Map;
/*   5:    */ 
/*   6:    */ public class Ok
/*   7:    */ {
/*   8:    */   public static boolean ok(Object object)
/*   9:    */   {
/*  10: 42 */     return object != null;
/*  11:    */   }
/*  12:    */   
/*  13:    */   public static boolean ok(boolean value)
/*  14:    */   {
/*  15: 47 */     return value;
/*  16:    */   }
/*  17:    */   
/*  18:    */   public static boolean ok(Number i)
/*  19:    */   {
/*  20: 51 */     return (i != null) && (i.intValue() != 0);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public static boolean ok(int i)
/*  24:    */   {
/*  25: 55 */     return i != 0;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public static boolean ok(long i)
/*  29:    */   {
/*  30: 59 */     return i != 0L;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public static boolean ok(Map map)
/*  34:    */   {
/*  35: 63 */     return (map != null) && (map.size() > 0);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public static boolean ok(Collection c)
/*  39:    */   {
/*  40: 67 */     return (c != null) && (c.size() > 0);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public static boolean ok(CharSequence cs)
/*  44:    */   {
/*  45: 72 */     return (cs != null) && (cs.length() > 0);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public static boolean okOrDie(Object object)
/*  49:    */   {
/*  50: 79 */     return (object != null) || (Exceptions.die());
/*  51:    */   }
/*  52:    */   
/*  53:    */   public static boolean okOrDie(boolean value)
/*  54:    */   {
/*  55: 84 */     return (value) || (Exceptions.die());
/*  56:    */   }
/*  57:    */   
/*  58:    */   public static boolean okOrDie(Number i)
/*  59:    */   {
/*  60: 88 */     return ((i != null) && (i.intValue() != 0)) || (Exceptions.die());
/*  61:    */   }
/*  62:    */   
/*  63:    */   public static boolean okOrDie(int i)
/*  64:    */   {
/*  65: 93 */     return (i != 0) || (Exceptions.die());
/*  66:    */   }
/*  67:    */   
/*  68:    */   public static boolean okOrDie(long i)
/*  69:    */   {
/*  70: 97 */     return (i != 0L) || (Exceptions.die());
/*  71:    */   }
/*  72:    */   
/*  73:    */   public static boolean okOrDie(Map map)
/*  74:    */   {
/*  75:101 */     return ((map != null) && (map.size() > 0)) || (Exceptions.die());
/*  76:    */   }
/*  77:    */   
/*  78:    */   public static boolean okOrDie(Collection c)
/*  79:    */   {
/*  80:105 */     return ((c != null) && (c.size() > 0)) || (Exceptions.die());
/*  81:    */   }
/*  82:    */   
/*  83:    */   public static boolean okOrDie(CharSequence cs)
/*  84:    */   {
/*  85:110 */     return ((cs != null) && (cs.length() > 0)) || (Exceptions.die());
/*  86:    */   }
/*  87:    */   
/*  88:    */   public static boolean okOrDie(String message, Object object)
/*  89:    */   {
/*  90:115 */     return (object != null) || (Exceptions.die(message));
/*  91:    */   }
/*  92:    */   
/*  93:    */   public static boolean okOrDie(String message, int i)
/*  94:    */   {
/*  95:119 */     return (i != 0) || (Exceptions.die(message));
/*  96:    */   }
/*  97:    */   
/*  98:    */   public static boolean okOrDie(String message, long i)
/*  99:    */   {
/* 100:123 */     return (i != 0L) || (Exceptions.die(message));
/* 101:    */   }
/* 102:    */   
/* 103:    */   public static boolean okOrDie(String message, Map map)
/* 104:    */   {
/* 105:127 */     return ((map != null) && (map.size() > 0)) || (Exceptions.die(message));
/* 106:    */   }
/* 107:    */   
/* 108:    */   public static boolean okOrDie(String message, Collection c)
/* 109:    */   {
/* 110:131 */     return ((c != null) && (c.size() > 0)) || (Exceptions.die(message));
/* 111:    */   }
/* 112:    */   
/* 113:    */   public static boolean okOrDie(String message, CharSequence cs)
/* 114:    */   {
/* 115:136 */     return ((cs != null) && (cs.length() > 0)) || (Exceptions.die(message));
/* 116:    */   }
/* 117:    */   
/* 118:    */   public static boolean okOrDie(String message, boolean value)
/* 119:    */   {
/* 120:141 */     return (value) || (Exceptions.die(message));
/* 121:    */   }
/* 122:    */   
/* 123:    */   public static boolean okOrDie(String message, Number i)
/* 124:    */   {
/* 125:145 */     return ((i != null) && (i.intValue() != 0)) || (Exceptions.die(message));
/* 126:    */   }
/* 127:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.Ok
 * JD-Core Version:    0.7.0.1
 */