/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ 
/*   5:    */ public class Tag
/*   6:    */   implements Serializable, RevisionHandler
/*   7:    */ {
/*   8:    */   private static final long serialVersionUID = 3326379903447135320L;
/*   9:    */   protected int m_ID;
/*  10:    */   protected String m_IDStr;
/*  11:    */   protected String m_Readable;
/*  12:    */   
/*  13:    */   public Tag()
/*  14:    */   {
/*  15: 51 */     this(0, "A new tag", "A new tag", true);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public Tag(int ident, String readable)
/*  19:    */   {
/*  20: 61 */     this(ident, "", readable);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public Tag(int ident, String identStr, String readable)
/*  24:    */   {
/*  25: 72 */     this(ident, identStr, readable, true);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public Tag(int ident, String identStr, String readable, boolean upperCase)
/*  29:    */   {
/*  30: 76 */     this.m_ID = ident;
/*  31: 77 */     if (identStr.length() == 0)
/*  32:    */     {
/*  33: 78 */       this.m_IDStr = ("" + ident);
/*  34:    */     }
/*  35:    */     else
/*  36:    */     {
/*  37: 80 */       this.m_IDStr = identStr;
/*  38: 81 */       if (upperCase) {
/*  39: 82 */         this.m_IDStr = identStr.toUpperCase();
/*  40:    */       }
/*  41:    */     }
/*  42: 85 */     this.m_Readable = readable;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public int getID()
/*  46:    */   {
/*  47: 94 */     return this.m_ID;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setID(int id)
/*  51:    */   {
/*  52:103 */     this.m_ID = id;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public String getIDStr()
/*  56:    */   {
/*  57:112 */     return this.m_IDStr;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void setIDStr(String str)
/*  61:    */   {
/*  62:121 */     this.m_IDStr = str;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public String getReadable()
/*  66:    */   {
/*  67:130 */     return this.m_Readable;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void setReadable(String r)
/*  71:    */   {
/*  72:139 */     this.m_Readable = r;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public String toString()
/*  76:    */   {
/*  77:148 */     return this.m_IDStr;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static String toOptionList(Tag[] tags)
/*  81:    */   {
/*  82:162 */     String result = "<";
/*  83:163 */     for (int i = 0; i < tags.length; i++)
/*  84:    */     {
/*  85:164 */       if (i > 0) {
/*  86:165 */         result = result + "|";
/*  87:    */       }
/*  88:166 */       result = result + tags[i];
/*  89:    */     }
/*  90:168 */     result = result + ">";
/*  91:    */     
/*  92:170 */     return result;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public static String toOptionSynopsis(Tag[] tags)
/*  96:    */   {
/*  97:184 */     String result = "";
/*  98:185 */     for (int i = 0; i < tags.length; i++) {
/*  99:186 */       result = result + "\t\t" + tags[i].getIDStr() + " = " + tags[i].getReadable() + "\n";
/* 100:    */     }
/* 101:189 */     return result;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public String getRevision()
/* 105:    */   {
/* 106:198 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 107:    */   }
/* 108:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Tag
 * JD-Core Version:    0.7.0.1
 */