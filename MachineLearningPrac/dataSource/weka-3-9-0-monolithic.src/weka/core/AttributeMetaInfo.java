/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.io.StreamTokenizer;
/*   6:    */ import java.io.StringReader;
/*   7:    */ 
/*   8:    */ public class AttributeMetaInfo
/*   9:    */   implements Serializable, RevisionHandler
/*  10:    */ {
/*  11:    */   protected ProtectedProperties m_Metadata;
/*  12:    */   protected int m_Ordering;
/*  13:    */   protected boolean m_IsRegular;
/*  14:    */   protected boolean m_IsAveragable;
/*  15:    */   protected boolean m_HasZeropoint;
/*  16:    */   protected double m_LowerBound;
/*  17:    */   protected boolean m_LowerBoundIsOpen;
/*  18:    */   protected double m_UpperBound;
/*  19:    */   protected boolean m_UpperBoundIsOpen;
/*  20:    */   
/*  21:    */   public AttributeMetaInfo(ProtectedProperties metadata, Attribute att)
/*  22:    */   {
/*  23: 64 */     setMetadata(metadata, att);
/*  24:    */   }
/*  25:    */   
/*  26:    */   private void setMetadata(ProtectedProperties metadata, Attribute att)
/*  27:    */   {
/*  28: 86 */     this.m_Metadata = metadata;
/*  29: 88 */     if (att.m_Type == 3)
/*  30:    */     {
/*  31: 89 */       this.m_Ordering = 1;
/*  32: 90 */       this.m_IsRegular = true;
/*  33: 91 */       this.m_IsAveragable = false;
/*  34: 92 */       this.m_HasZeropoint = false;
/*  35:    */     }
/*  36:    */     else
/*  37:    */     {
/*  38: 96 */       String orderString = this.m_Metadata.getProperty("ordering", "");
/*  39:    */       String def;
/*  40:    */       String def;
/*  41:100 */       if ((att.m_Type == 0) && (orderString.compareTo("modulo") != 0) && (orderString.compareTo("symbolic") != 0)) {
/*  42:102 */         def = "true";
/*  43:    */       } else {
/*  44:104 */         def = "false";
/*  45:    */       }
/*  46:108 */       this.m_IsAveragable = (this.m_Metadata.getProperty("averageable", def).compareTo("true") == 0);
/*  47:    */       
/*  48:110 */       this.m_HasZeropoint = (this.m_Metadata.getProperty("zeropoint", def).compareTo("true") == 0);
/*  49:113 */       if ((this.m_IsAveragable) || (this.m_HasZeropoint)) {
/*  50:114 */         def = "true";
/*  51:    */       }
/*  52:116 */       this.m_IsRegular = (this.m_Metadata.getProperty("regular", def).compareTo("true") == 0);
/*  53:119 */       if (orderString.compareTo("symbolic") == 0) {
/*  54:120 */         this.m_Ordering = 0;
/*  55:121 */       } else if (orderString.compareTo("ordered") == 0) {
/*  56:122 */         this.m_Ordering = 1;
/*  57:123 */       } else if (orderString.compareTo("modulo") == 0) {
/*  58:124 */         this.m_Ordering = 2;
/*  59:126 */       } else if ((att.m_Type == 0) || (this.m_IsAveragable) || (this.m_HasZeropoint)) {
/*  60:127 */         this.m_Ordering = 1;
/*  61:    */       } else {
/*  62:129 */         this.m_Ordering = 0;
/*  63:    */       }
/*  64:    */     }
/*  65:135 */     if ((this.m_IsAveragable) && (!this.m_IsRegular)) {
/*  66:136 */       throw new IllegalArgumentException("An averagable attribute must be regular");
/*  67:    */     }
/*  68:139 */     if ((this.m_HasZeropoint) && (!this.m_IsRegular)) {
/*  69:140 */       throw new IllegalArgumentException("A zeropoint attribute must be regular");
/*  70:    */     }
/*  71:143 */     if ((this.m_IsRegular) && (this.m_Ordering == 0)) {
/*  72:144 */       throw new IllegalArgumentException("A symbolic attribute cannot be regular");
/*  73:    */     }
/*  74:147 */     if ((this.m_IsAveragable) && (this.m_Ordering != 1)) {
/*  75:148 */       throw new IllegalArgumentException("An averagable attribute must be ordered");
/*  76:    */     }
/*  77:151 */     if ((this.m_HasZeropoint) && (this.m_Ordering != 1)) {
/*  78:152 */       throw new IllegalArgumentException("A zeropoint attribute must be ordered");
/*  79:    */     }
/*  80:157 */     att.m_Weight = 1.0D;
/*  81:158 */     String weightString = this.m_Metadata.getProperty("weight");
/*  82:159 */     if (weightString != null) {
/*  83:    */       try
/*  84:    */       {
/*  85:161 */         att.m_Weight = Double.valueOf(weightString).doubleValue();
/*  86:    */       }
/*  87:    */       catch (NumberFormatException e)
/*  88:    */       {
/*  89:164 */         throw new IllegalArgumentException("Not a valid attribute weight: '" + weightString + "'");
/*  90:    */       }
/*  91:    */     }
/*  92:170 */     if (att.m_Type == 0) {
/*  93:171 */       setNumericRange(this.m_Metadata.getProperty("range"));
/*  94:    */     }
/*  95:    */   }
/*  96:    */   
/*  97:    */   private void setNumericRange(String rangeString)
/*  98:    */   {
/*  99:187 */     this.m_LowerBound = (-1.0D / 0.0D);
/* 100:188 */     this.m_LowerBoundIsOpen = false;
/* 101:189 */     this.m_UpperBound = (1.0D / 0.0D);
/* 102:190 */     this.m_UpperBoundIsOpen = false;
/* 103:192 */     if (rangeString == null) {
/* 104:193 */       return;
/* 105:    */     }
/* 106:197 */     StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(rangeString));
/* 107:    */     
/* 108:199 */     tokenizer.resetSyntax();
/* 109:200 */     tokenizer.whitespaceChars(0, 32);
/* 110:201 */     tokenizer.wordChars(33, 255);
/* 111:202 */     tokenizer.ordinaryChar(91);
/* 112:203 */     tokenizer.ordinaryChar(40);
/* 113:204 */     tokenizer.ordinaryChar(44);
/* 114:205 */     tokenizer.ordinaryChar(93);
/* 115:206 */     tokenizer.ordinaryChar(41);
/* 116:    */     try
/* 117:    */     {
/* 118:211 */       tokenizer.nextToken();
/* 119:213 */       if (tokenizer.ttype == 91) {
/* 120:214 */         this.m_LowerBoundIsOpen = false;
/* 121:215 */       } else if (tokenizer.ttype == 40) {
/* 122:216 */         this.m_LowerBoundIsOpen = true;
/* 123:    */       } else {
/* 124:218 */         throw new IllegalArgumentException("Expected opening brace on range, found: " + tokenizer.toString());
/* 125:    */       }
/* 126:223 */       tokenizer.nextToken();
/* 127:224 */       if (tokenizer.ttype != -3) {
/* 128:225 */         throw new IllegalArgumentException("Expected lower bound in range, found: " + tokenizer.toString());
/* 129:    */       }
/* 130:228 */       if (tokenizer.sval.compareToIgnoreCase("-inf") == 0) {
/* 131:229 */         this.m_LowerBound = (-1.0D / 0.0D);
/* 132:230 */       } else if (tokenizer.sval.compareToIgnoreCase("+inf") == 0) {
/* 133:231 */         this.m_LowerBound = (1.0D / 0.0D);
/* 134:232 */       } else if (tokenizer.sval.compareToIgnoreCase("inf") == 0) {
/* 135:233 */         this.m_LowerBound = (-1.0D / 0.0D);
/* 136:    */       } else {
/* 137:    */         try
/* 138:    */         {
/* 139:236 */           this.m_LowerBound = Double.valueOf(tokenizer.sval).doubleValue();
/* 140:    */         }
/* 141:    */         catch (NumberFormatException e)
/* 142:    */         {
/* 143:238 */           throw new IllegalArgumentException("Expected lower bound in range, found: '" + tokenizer.sval + "'");
/* 144:    */         }
/* 145:    */       }
/* 146:244 */       if (tokenizer.nextToken() != 44) {
/* 147:245 */         throw new IllegalArgumentException("Expected comma in range, found: " + tokenizer.toString());
/* 148:    */       }
/* 149:250 */       tokenizer.nextToken();
/* 150:251 */       if (tokenizer.ttype != -3) {
/* 151:252 */         throw new IllegalArgumentException("Expected upper bound in range, found: " + tokenizer.toString());
/* 152:    */       }
/* 153:255 */       if (tokenizer.sval.compareToIgnoreCase("-inf") == 0) {
/* 154:256 */         this.m_UpperBound = (-1.0D / 0.0D);
/* 155:257 */       } else if (tokenizer.sval.compareToIgnoreCase("+inf") == 0) {
/* 156:258 */         this.m_UpperBound = (1.0D / 0.0D);
/* 157:259 */       } else if (tokenizer.sval.compareToIgnoreCase("inf") == 0) {
/* 158:260 */         this.m_UpperBound = (1.0D / 0.0D);
/* 159:    */       } else {
/* 160:    */         try
/* 161:    */         {
/* 162:263 */           this.m_UpperBound = Double.valueOf(tokenizer.sval).doubleValue();
/* 163:    */         }
/* 164:    */         catch (NumberFormatException e)
/* 165:    */         {
/* 166:265 */           throw new IllegalArgumentException("Expected upper bound in range, found: '" + tokenizer.sval + "'");
/* 167:    */         }
/* 168:    */       }
/* 169:271 */       tokenizer.nextToken();
/* 170:273 */       if (tokenizer.ttype == 93) {
/* 171:274 */         this.m_UpperBoundIsOpen = false;
/* 172:275 */       } else if (tokenizer.ttype == 41) {
/* 173:276 */         this.m_UpperBoundIsOpen = true;
/* 174:    */       } else {
/* 175:278 */         throw new IllegalArgumentException("Expected closing brace on range, found: " + tokenizer.toString());
/* 176:    */       }
/* 177:283 */       if (tokenizer.nextToken() != -1) {
/* 178:284 */         throw new IllegalArgumentException("Expected end of range string, found: " + tokenizer.toString());
/* 179:    */       }
/* 180:    */     }
/* 181:    */     catch (IOException e)
/* 182:    */     {
/* 183:289 */       throw new IllegalArgumentException("IOException reading attribute range string: " + e.getMessage());
/* 184:    */     }
/* 185:293 */     if (this.m_UpperBound < this.m_LowerBound) {
/* 186:294 */       throw new IllegalArgumentException("Upper bound (" + this.m_UpperBound + ") on numeric range is" + " less than lower bound (" + this.m_LowerBound + ")!");
/* 187:    */     }
/* 188:    */   }
/* 189:    */   
/* 190:    */   public String getRevision()
/* 191:    */   {
/* 192:307 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 193:    */   }
/* 194:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.AttributeMetaInfo
 * JD-Core Version:    0.7.0.1
 */