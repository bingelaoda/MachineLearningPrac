/*   1:    */ package weka.core.tokenizers;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Option;
/*   7:    */ import weka.core.RevisionUtils;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public class CharacterNGramTokenizer
/*  11:    */   extends Tokenizer
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = -1181896253171647218L;
/*  14: 63 */   protected int m_NMax = 3;
/*  15: 66 */   protected int m_NMin = 1;
/*  16:    */   protected int m_N;
/*  17:    */   protected int m_CurrentPosition;
/*  18:    */   protected String m_String;
/*  19:    */   
/*  20:    */   public String globalInfo()
/*  21:    */   {
/*  22: 85 */     return "Splits a string into all character n-grams it contains based on the given maximum and minimum for n.";
/*  23:    */   }
/*  24:    */   
/*  25:    */   public Enumeration<Option> listOptions()
/*  26:    */   {
/*  27: 95 */     Vector<Option> result = new Vector();
/*  28:    */     
/*  29: 97 */     result.addElement(new Option("\tThe maximum number of characters (default = 3).", "max", 1, "-max <int>"));
/*  30:    */     
/*  31:    */ 
/*  32:100 */     result.addElement(new Option("\tThe minimum number of characters (default = 1).", "min", 1, "-min <int>"));
/*  33:    */     
/*  34:    */ 
/*  35:103 */     result.addAll(Collections.list(super.listOptions()));
/*  36:    */     
/*  37:105 */     return result.elements();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public String[] getOptions()
/*  41:    */   {
/*  42:115 */     Vector<String> result = new Vector();
/*  43:    */     
/*  44:117 */     result.add("-max");
/*  45:118 */     result.add("" + getNGramMaxSize());
/*  46:    */     
/*  47:120 */     result.add("-min");
/*  48:121 */     result.add("" + getNGramMinSize());
/*  49:    */     
/*  50:123 */     Collections.addAll(result, super.getOptions());
/*  51:    */     
/*  52:125 */     return (String[])result.toArray(new String[result.size()]);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setOptions(String[] options)
/*  56:    */     throws Exception
/*  57:    */   {
/*  58:154 */     String value = Utils.getOption("max", options);
/*  59:155 */     if (value.length() != 0) {
/*  60:156 */       setNGramMaxSize(Integer.parseInt(value));
/*  61:    */     } else {
/*  62:158 */       setNGramMaxSize(3);
/*  63:    */     }
/*  64:161 */     value = Utils.getOption("min", options);
/*  65:162 */     if (value.length() != 0) {
/*  66:163 */       setNGramMinSize(Integer.parseInt(value));
/*  67:    */     } else {
/*  68:165 */       setNGramMinSize(1);
/*  69:    */     }
/*  70:168 */     super.setOptions(options);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public int getNGramMaxSize()
/*  74:    */   {
/*  75:177 */     return this.m_NMax;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void setNGramMaxSize(int value)
/*  79:    */   {
/*  80:186 */     if (value < 1) {
/*  81:187 */       this.m_NMax = 1;
/*  82:    */     } else {
/*  83:189 */       this.m_NMax = value;
/*  84:    */     }
/*  85:    */   }
/*  86:    */   
/*  87:    */   public String NGramMaxSizeTipText()
/*  88:    */   {
/*  89:200 */     return "The maximum size of an n-gram.";
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void setNGramMinSize(int value)
/*  93:    */   {
/*  94:209 */     if (value < 1) {
/*  95:210 */       this.m_NMin = 1;
/*  96:    */     } else {
/*  97:212 */       this.m_NMin = value;
/*  98:    */     }
/*  99:    */   }
/* 100:    */   
/* 101:    */   public int getNGramMinSize()
/* 102:    */   {
/* 103:222 */     return this.m_NMin;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public String NGramMinSizeTipText()
/* 107:    */   {
/* 108:232 */     return "The minimum size of an n-gram.";
/* 109:    */   }
/* 110:    */   
/* 111:    */   public boolean hasMoreElements()
/* 112:    */   {
/* 113:243 */     return this.m_CurrentPosition + this.m_N <= this.m_String.length();
/* 114:    */   }
/* 115:    */   
/* 116:    */   public String nextElement()
/* 117:    */   {
/* 118:254 */     String result = null;
/* 119:    */     try
/* 120:    */     {
/* 121:256 */       result = this.m_String.substring(this.m_CurrentPosition, this.m_CurrentPosition + this.m_N);
/* 122:    */     }
/* 123:    */     catch (StringIndexOutOfBoundsException ex) {}
/* 124:260 */     this.m_N += 1;
/* 125:261 */     if ((this.m_N > this.m_NMax) || (this.m_CurrentPosition + this.m_N > this.m_String.length()))
/* 126:    */     {
/* 127:262 */       this.m_N = this.m_NMin;
/* 128:263 */       this.m_CurrentPosition += 1;
/* 129:    */     }
/* 130:265 */     return result;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public void tokenize(String s)
/* 134:    */   {
/* 135:277 */     this.m_CurrentPosition = 0;
/* 136:278 */     this.m_String = s;
/* 137:279 */     this.m_N = this.m_NMin;
/* 138:    */   }
/* 139:    */   
/* 140:    */   public String getRevision()
/* 141:    */   {
/* 142:289 */     return RevisionUtils.extract("$Revision: 10971 $");
/* 143:    */   }
/* 144:    */   
/* 145:    */   public static void main(String[] args)
/* 146:    */   {
/* 147:299 */     runTokenizer(new CharacterNGramTokenizer(), args);
/* 148:    */   }
/* 149:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.tokenizers.CharacterNGramTokenizer
 * JD-Core Version:    0.7.0.1
 */