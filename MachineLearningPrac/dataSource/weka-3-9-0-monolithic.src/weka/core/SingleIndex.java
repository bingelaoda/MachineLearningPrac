/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ 
/*   6:    */ public class SingleIndex
/*   7:    */   implements Serializable, RevisionHandler, CustomDisplayStringProvider
/*   8:    */ {
/*   9:    */   static final long serialVersionUID = 5285169134430839303L;
/*  10: 49 */   protected String m_IndexString = "";
/*  11: 52 */   protected int m_SelectedIndex = -1;
/*  12: 56 */   protected int m_Upper = -1;
/*  13:    */   
/*  14:    */   public SingleIndex() {}
/*  15:    */   
/*  16:    */   public SingleIndex(String index)
/*  17:    */   {
/*  18: 80 */     setSingleIndex(index);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public void setUpper(int newUpper)
/*  22:    */   {
/*  23: 93 */     if (newUpper >= 0)
/*  24:    */     {
/*  25: 94 */       this.m_Upper = newUpper;
/*  26: 95 */       setValue();
/*  27:    */     }
/*  28:    */   }
/*  29:    */   
/*  30:    */   public String getSingleIndex()
/*  31:    */   {
/*  32:107 */     return this.m_IndexString;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void setSingleIndex(String index)
/*  36:    */   {
/*  37:122 */     this.m_IndexString = index;
/*  38:123 */     this.m_SelectedIndex = -1;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public String toString()
/*  42:    */   {
/*  43:136 */     if (this.m_IndexString.equals("")) {
/*  44:137 */       return "No index set";
/*  45:    */     }
/*  46:139 */     if (this.m_Upper == -1) {
/*  47:140 */       throw new RuntimeException("Upper limit has not been specified");
/*  48:    */     }
/*  49:142 */     return this.m_IndexString;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public int getIndex()
/*  53:    */   {
/*  54:156 */     if (this.m_IndexString.equals("")) {
/*  55:157 */       throw new RuntimeException("No index set");
/*  56:    */     }
/*  57:159 */     if (this.m_Upper == -1) {
/*  58:160 */       throw new RuntimeException("No upper limit has been specified for index");
/*  59:    */     }
/*  60:162 */     return this.m_SelectedIndex;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public static String indexToString(int index)
/*  64:    */   {
/*  65:176 */     return "" + (index + 1);
/*  66:    */   }
/*  67:    */   
/*  68:    */   protected void setValue()
/*  69:    */   {
/*  70:185 */     if (this.m_IndexString.equals("")) {
/*  71:186 */       throw new RuntimeException("No index set");
/*  72:    */     }
/*  73:188 */     if (this.m_IndexString.toLowerCase().equals("first"))
/*  74:    */     {
/*  75:189 */       this.m_SelectedIndex = 0;
/*  76:    */     }
/*  77:190 */     else if (this.m_IndexString.toLowerCase().equals("last"))
/*  78:    */     {
/*  79:191 */       this.m_SelectedIndex = this.m_Upper;
/*  80:    */     }
/*  81:    */     else
/*  82:    */     {
/*  83:193 */       this.m_SelectedIndex = (Integer.parseInt(this.m_IndexString) - 1);
/*  84:194 */       if (this.m_SelectedIndex < 0)
/*  85:    */       {
/*  86:195 */         this.m_IndexString = "";
/*  87:196 */         throw new IllegalArgumentException("Index must be greater than zero");
/*  88:    */       }
/*  89:198 */       if (this.m_SelectedIndex > this.m_Upper)
/*  90:    */       {
/*  91:199 */         this.m_IndexString = "";
/*  92:200 */         throw new IllegalArgumentException("Index is too large");
/*  93:    */       }
/*  94:    */     }
/*  95:    */   }
/*  96:    */   
/*  97:    */   public String getRevision()
/*  98:    */   {
/*  99:211 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 100:    */   }
/* 101:    */   
/* 102:    */   public String toDisplay()
/* 103:    */   {
/* 104:220 */     return getSingleIndex();
/* 105:    */   }
/* 106:    */   
/* 107:    */   public static void main(String[] argv)
/* 108:    */   {
/* 109:    */     try
/* 110:    */     {
/* 111:232 */       if (argv.length == 0) {
/* 112:233 */         throw new Exception("Usage: SingleIndex <indexspec>");
/* 113:    */       }
/* 114:235 */       SingleIndex singleIndex = new SingleIndex();
/* 115:236 */       singleIndex.setSingleIndex(argv[0]);
/* 116:237 */       singleIndex.setUpper(9);
/* 117:238 */       System.out.println("Input: " + argv[0] + "\n" + singleIndex.toString());
/* 118:    */       
/* 119:240 */       int selectedIndex = singleIndex.getIndex();
/* 120:241 */       System.out.println(selectedIndex + "");
/* 121:    */     }
/* 122:    */     catch (Exception ex)
/* 123:    */     {
/* 124:243 */       ex.printStackTrace();
/* 125:244 */       System.out.println(ex.getMessage());
/* 126:    */     }
/* 127:    */   }
/* 128:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.SingleIndex
 * JD-Core Version:    0.7.0.1
 */