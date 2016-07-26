/*   1:    */ package no.uib.cipr.matrix.io;
/*   2:    */ 
/*   3:    */ public class MatrixInfo
/*   4:    */ {
/*   5:    */   private boolean sparse;
/*   6:    */   private MatrixField field;
/*   7:    */   private MatrixSymmetry symmetry;
/*   8:    */   
/*   9:    */   public static enum MatrixField
/*  10:    */   {
/*  11: 38 */     Real,  Integer,  Complex,  Pattern;
/*  12:    */     
/*  13:    */     private MatrixField() {}
/*  14:    */   }
/*  15:    */   
/*  16:    */   public static enum MatrixSymmetry
/*  17:    */   {
/*  18: 64 */     General,  Symmetric,  SkewSymmetric,  Hermitian;
/*  19:    */     
/*  20:    */     private MatrixSymmetry() {}
/*  21:    */   }
/*  22:    */   
/*  23:    */   public MatrixInfo(boolean sparse, MatrixField field, MatrixSymmetry symmetry)
/*  24:    */   {
/*  25:108 */     this.sparse = sparse;
/*  26:109 */     this.field = field;
/*  27:110 */     this.symmetry = symmetry;
/*  28:    */     
/*  29:112 */     validate();
/*  30:    */   }
/*  31:    */   
/*  32:    */   private void validate()
/*  33:    */   {
/*  34:119 */     if ((isDense()) && (isPattern())) {
/*  35:120 */       throw new IllegalArgumentException("Matrix cannot be dense with pattern storage");
/*  36:    */     }
/*  37:122 */     if ((isReal()) && (isHermitian())) {
/*  38:123 */       throw new IllegalArgumentException("Data cannot be real with hermitian symmetry");
/*  39:    */     }
/*  40:125 */     if ((!isComplex()) && (isHermitian())) {
/*  41:126 */       throw new IllegalArgumentException("Data must be complex with hermitian symmetry");
/*  42:    */     }
/*  43:128 */     if ((isPattern()) && (isSkewSymmetric())) {
/*  44:129 */       throw new IllegalArgumentException("Storage cannot be pattern and skew symmetrical");
/*  45:    */     }
/*  46:    */   }
/*  47:    */   
/*  48:    */   public boolean isSparse()
/*  49:    */   {
/*  50:138 */     return this.sparse;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public boolean isCoordinate()
/*  54:    */   {
/*  55:146 */     return this.sparse;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public boolean isDense()
/*  59:    */   {
/*  60:154 */     return !this.sparse;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public boolean isArray()
/*  64:    */   {
/*  65:162 */     return !this.sparse;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public boolean isReal()
/*  69:    */   {
/*  70:170 */     return this.field == MatrixField.Real;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public boolean isInteger()
/*  74:    */   {
/*  75:178 */     return this.field == MatrixField.Integer;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public boolean isComplex()
/*  79:    */   {
/*  80:186 */     return this.field == MatrixField.Complex;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public boolean isPattern()
/*  84:    */   {
/*  85:194 */     return this.field == MatrixField.Pattern;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public boolean isGeneral()
/*  89:    */   {
/*  90:202 */     return this.symmetry == MatrixSymmetry.General;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public boolean isSymmetric()
/*  94:    */   {
/*  95:210 */     return this.symmetry == MatrixSymmetry.Symmetric;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public boolean isSkewSymmetric()
/*  99:    */   {
/* 100:218 */     return this.symmetry == MatrixSymmetry.SkewSymmetric;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public boolean isHermitian()
/* 104:    */   {
/* 105:226 */     return this.symmetry == MatrixSymmetry.Hermitian;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public String toString()
/* 109:    */   {
/* 110:240 */     StringBuilder buf = new StringBuilder();
/* 111:    */     
/* 112:242 */     buf.append("%%MatrixMarket matrix ");
/* 113:244 */     if (isSparse()) {
/* 114:245 */       buf.append("coordinate ");
/* 115:    */     } else {
/* 116:247 */       buf.append("array ");
/* 117:    */     }
/* 118:249 */     if (isReal()) {
/* 119:250 */       buf.append("real ");
/* 120:251 */     } else if (isComplex()) {
/* 121:252 */       buf.append("complex ");
/* 122:253 */     } else if (isPattern()) {
/* 123:254 */       buf.append("pattern ");
/* 124:255 */     } else if (isInteger()) {
/* 125:256 */       buf.append("integer ");
/* 126:    */     } else {
/* 127:259 */       throw new IllegalArgumentException("Unknown field specification");
/* 128:    */     }
/* 129:261 */     if (isGeneral()) {
/* 130:262 */       buf.append("general\n");
/* 131:263 */     } else if (isSymmetric()) {
/* 132:264 */       buf.append("symmetric\n");
/* 133:265 */     } else if (isSkewSymmetric()) {
/* 134:266 */       buf.append("skew-symmetric\n");
/* 135:267 */     } else if (isHermitian()) {
/* 136:268 */       buf.append("Hermitian\n");
/* 137:    */     } else {
/* 138:271 */       throw new IllegalArgumentException("Unknown symmetry specification");
/* 139:    */     }
/* 140:273 */     return buf.toString();
/* 141:    */   }
/* 142:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.io.MatrixInfo
 * JD-Core Version:    0.7.0.1
 */