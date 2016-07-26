/*   1:    */ package weka.core.tokenizers;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.LinkedList;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Option;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ import weka.core.Utils;
/*  10:    */ 
/*  11:    */ public class NGramTokenizer
/*  12:    */   extends CharacterDelimitedTokenizer
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = -2181896254171647219L;
/*  15: 69 */   protected int m_NMax = 3;
/*  16: 72 */   protected int m_NMin = 1;
/*  17:    */   protected int m_N;
/*  18:    */   protected int m_MaxPosition;
/*  19:    */   protected int m_CurrentPosition;
/*  20:    */   protected String[] m_SplitString;
/*  21:    */   
/*  22:    */   public String globalInfo()
/*  23:    */   {
/*  24: 94 */     return "Splits a string into an n-gram with min and max grams.";
/*  25:    */   }
/*  26:    */   
/*  27:    */   public Enumeration<Option> listOptions()
/*  28:    */   {
/*  29:104 */     Vector<Option> result = new Vector();
/*  30:    */     
/*  31:106 */     result.addElement(new Option("\tThe max size of the Ngram (default = 3).", "max", 1, "-max <int>"));
/*  32:    */     
/*  33:    */ 
/*  34:109 */     result.addElement(new Option("\tThe min size of the Ngram (default = 1).", "min", 1, "-min <int>"));
/*  35:    */     
/*  36:    */ 
/*  37:112 */     result.addAll(Collections.list(super.listOptions()));
/*  38:    */     
/*  39:114 */     return result.elements();
/*  40:    */   }
/*  41:    */   
/*  42:    */   public String[] getOptions()
/*  43:    */   {
/*  44:124 */     Vector<String> result = new Vector();
/*  45:    */     
/*  46:126 */     result.add("-max");
/*  47:127 */     result.add("" + getNGramMaxSize());
/*  48:    */     
/*  49:129 */     result.add("-min");
/*  50:130 */     result.add("" + getNGramMinSize());
/*  51:    */     
/*  52:132 */     Collections.addAll(result, super.getOptions());
/*  53:    */     
/*  54:134 */     return (String[])result.toArray(new String[result.size()]);
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setOptions(String[] options)
/*  58:    */     throws Exception
/*  59:    */   {
/*  60:169 */     String value = Utils.getOption("max", options);
/*  61:170 */     if (value.length() != 0) {
/*  62:171 */       setNGramMaxSize(Integer.parseInt(value));
/*  63:    */     } else {
/*  64:173 */       setNGramMaxSize(3);
/*  65:    */     }
/*  66:176 */     value = Utils.getOption("min", options);
/*  67:177 */     if (value.length() != 0) {
/*  68:178 */       setNGramMinSize(Integer.parseInt(value));
/*  69:    */     } else {
/*  70:180 */       setNGramMinSize(1);
/*  71:    */     }
/*  72:183 */     super.setOptions(options);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public int getNGramMaxSize()
/*  76:    */   {
/*  77:192 */     return this.m_NMax;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void setNGramMaxSize(int value)
/*  81:    */   {
/*  82:201 */     if (value < 1) {
/*  83:202 */       this.m_NMax = 1;
/*  84:    */     } else {
/*  85:204 */       this.m_NMax = value;
/*  86:    */     }
/*  87:    */   }
/*  88:    */   
/*  89:    */   public String NGramMaxSizeTipText()
/*  90:    */   {
/*  91:215 */     return "The max N of the NGram.";
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void setNGramMinSize(int value)
/*  95:    */   {
/*  96:224 */     if (value < 1) {
/*  97:225 */       this.m_NMin = 1;
/*  98:    */     } else {
/*  99:227 */       this.m_NMin = value;
/* 100:    */     }
/* 101:    */   }
/* 102:    */   
/* 103:    */   public int getNGramMinSize()
/* 104:    */   {
/* 105:237 */     return this.m_NMin;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public String NGramMinSizeTipText()
/* 109:    */   {
/* 110:247 */     return "The min N of the NGram.";
/* 111:    */   }
/* 112:    */   
/* 113:    */   public boolean hasMoreElements()
/* 114:    */   {
/* 115:260 */     return this.m_N >= this.m_NMin;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public String nextElement()
/* 119:    */   {
/* 120:270 */     String retValue = "";
/* 121:276 */     for (int i = 0; i < this.m_N; i++) {
/* 122:277 */       retValue = retValue + " " + this.m_SplitString[(this.m_CurrentPosition + i)];
/* 123:    */     }
/* 124:280 */     this.m_CurrentPosition += 1;
/* 125:282 */     if (this.m_CurrentPosition + this.m_N - 1 == this.m_MaxPosition)
/* 126:    */     {
/* 127:283 */       this.m_CurrentPosition = 0;
/* 128:284 */       this.m_N -= 1;
/* 129:    */     }
/* 130:287 */     return retValue.trim();
/* 131:    */   }
/* 132:    */   
/* 133:    */   protected void filterOutEmptyStrings()
/* 134:    */   {
/* 135:298 */     LinkedList<String> clean = new LinkedList();
/* 136:300 */     for (int i = 0; i < this.m_SplitString.length; i++) {
/* 137:301 */       if (!this.m_SplitString[i].equals("")) {
/* 138:302 */         clean.add(this.m_SplitString[i]);
/* 139:    */       }
/* 140:    */     }
/* 141:306 */     String[] newSplit = new String[clean.size()];
/* 142:307 */     for (int i = 0; i < clean.size(); i++) {
/* 143:308 */       newSplit[i] = ((String)clean.get(i));
/* 144:    */     }
/* 145:311 */     this.m_SplitString = newSplit;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void tokenize(String s)
/* 149:    */   {
/* 150:321 */     this.m_N = this.m_NMax;
/* 151:322 */     this.m_SplitString = s.split("[" + getDelimiters() + "]");
/* 152:    */     
/* 153:324 */     filterOutEmptyStrings();
/* 154:    */     
/* 155:326 */     this.m_CurrentPosition = 0;
/* 156:327 */     this.m_MaxPosition = this.m_SplitString.length;
/* 157:329 */     if (this.m_SplitString.length < this.m_NMax) {
/* 158:330 */       this.m_N = this.m_SplitString.length;
/* 159:    */     }
/* 160:    */   }
/* 161:    */   
/* 162:    */   public String getRevision()
/* 163:    */   {
/* 164:341 */     return RevisionUtils.extract("$Revision: 10971 $");
/* 165:    */   }
/* 166:    */   
/* 167:    */   public static void main(String[] args)
/* 168:    */   {
/* 169:351 */     runTokenizer(new NGramTokenizer(), args);
/* 170:    */   }
/* 171:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.tokenizers.NGramTokenizer
 * JD-Core Version:    0.7.0.1
 */