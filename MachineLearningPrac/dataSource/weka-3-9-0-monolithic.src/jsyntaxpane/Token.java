/*   1:    */ package jsyntaxpane;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.logging.Level;
/*   5:    */ import java.util.logging.Logger;
/*   6:    */ import javax.swing.text.BadLocationException;
/*   7:    */ import javax.swing.text.Document;
/*   8:    */ import javax.swing.text.Segment;
/*   9:    */ 
/*  10:    */ public class Token
/*  11:    */   implements Serializable, Comparable
/*  12:    */ {
/*  13:    */   public final TokenType type;
/*  14:    */   public final int start;
/*  15:    */   public final int length;
/*  16:    */   public final byte pairValue;
/*  17: 54 */   public final short kind = 0;
/*  18:    */   
/*  19:    */   public Token(TokenType type, int start, int length)
/*  20:    */   {
/*  21: 63 */     this.type = type;
/*  22: 64 */     this.start = start;
/*  23: 65 */     this.length = length;
/*  24: 66 */     this.pairValue = 0;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public Token(TokenType type, int start, int length, byte pairValue)
/*  28:    */   {
/*  29: 77 */     this.type = type;
/*  30: 78 */     this.start = start;
/*  31: 79 */     this.length = length;
/*  32: 80 */     this.pairValue = pairValue;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public boolean equals(Object obj)
/*  36:    */   {
/*  37: 85 */     if ((obj instanceof Object))
/*  38:    */     {
/*  39: 86 */       Token token = (Token)obj;
/*  40: 87 */       return (this.start == token.start) && (this.length == token.length) && (this.type.equals(token.type));
/*  41:    */     }
/*  42: 91 */     return false;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public int hashCode()
/*  46:    */   {
/*  47: 97 */     return this.start;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public String toString()
/*  51:    */   {
/*  52:102 */     if (this.pairValue == 0) {
/*  53:103 */       return String.format("%s (%d, %d)", new Object[] { this.type, Integer.valueOf(this.start), Integer.valueOf(this.length) });
/*  54:    */     }
/*  55:105 */     return String.format("%s (%d, %d) (%d)", new Object[] { this.type, Integer.valueOf(this.start), Integer.valueOf(this.length), Byte.valueOf(this.pairValue) });
/*  56:    */   }
/*  57:    */   
/*  58:    */   public int compareTo(Object o)
/*  59:    */   {
/*  60:111 */     Token t = (Token)o;
/*  61:112 */     if (this.start != t.start) {
/*  62:113 */       return this.start - t.start;
/*  63:    */     }
/*  64:114 */     if (this.length != t.length) {
/*  65:115 */       return this.length - t.length;
/*  66:    */     }
/*  67:117 */     return this.type.compareTo(t.type);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public int end()
/*  71:    */   {
/*  72:126 */     return this.start + this.length;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public CharSequence getText(Document doc)
/*  76:    */   {
/*  77:135 */     Segment text = new Segment();
/*  78:    */     try
/*  79:    */     {
/*  80:137 */       doc.getText(this.start, this.length, text);
/*  81:    */       
/*  82:    */ 
/*  83:    */ 
/*  84:141 */       return text;
/*  85:    */     }
/*  86:    */     catch (BadLocationException ex)
/*  87:    */     {
/*  88:138 */       ex = 
/*  89:    */       
/*  90:    */ 
/*  91:141 */         ex;Logger.getLogger(Token.class.getName()).log(Level.SEVERE, null, ex);return text;
/*  92:    */     }
/*  93:    */     finally {}
/*  94:141 */     return text;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public String getString(Document doc)
/*  98:    */   {
/*  99:146 */     String result = "";
/* 100:    */     try
/* 101:    */     {
/* 102:148 */       result = doc.getText(this.start, this.length);
/* 103:    */       
/* 104:    */ 
/* 105:    */ 
/* 106:152 */       return result;
/* 107:    */     }
/* 108:    */     catch (BadLocationException ex)
/* 109:    */     {
/* 110:149 */       ex = 
/* 111:    */       
/* 112:    */ 
/* 113:152 */         ex;Logger.getLogger(Token.class.getName()).log(Level.SEVERE, null, ex);return result;
/* 114:    */     }
/* 115:    */     finally {}
/* 116:152 */     return result;
/* 117:    */   }
/* 118:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.Token
 * JD-Core Version:    0.7.0.1
 */