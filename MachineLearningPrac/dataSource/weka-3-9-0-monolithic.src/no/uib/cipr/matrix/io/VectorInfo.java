/*   1:    */ package no.uib.cipr.matrix.io;
/*   2:    */ 
/*   3:    */ public class VectorInfo
/*   4:    */ {
/*   5:    */   private boolean sparse;
/*   6:    */   private VectorField field;
/*   7:    */   
/*   8:    */   public static enum VectorField
/*   9:    */   {
/*  10: 36 */     Real,  Integer,  Complex,  Pattern;
/*  11:    */     
/*  12:    */     private VectorField() {}
/*  13:    */   }
/*  14:    */   
/*  15:    */   public VectorInfo(boolean sparse, VectorField field)
/*  16:    */   {
/*  17: 73 */     this.sparse = sparse;
/*  18: 74 */     this.field = field;
/*  19:    */     
/*  20: 76 */     validate();
/*  21:    */   }
/*  22:    */   
/*  23:    */   private void validate()
/*  24:    */   {
/*  25: 83 */     if ((isDense()) && (isPattern())) {
/*  26: 84 */       throw new IllegalArgumentException("Vector cannot be dense with pattern storage");
/*  27:    */     }
/*  28:    */   }
/*  29:    */   
/*  30:    */   public boolean isSparse()
/*  31:    */   {
/*  32: 93 */     return this.sparse;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public boolean isCoordinate()
/*  36:    */   {
/*  37:101 */     return this.sparse;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public boolean isDense()
/*  41:    */   {
/*  42:109 */     return !this.sparse;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public boolean isArray()
/*  46:    */   {
/*  47:117 */     return !this.sparse;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public boolean isReal()
/*  51:    */   {
/*  52:125 */     return this.field == VectorField.Real;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public boolean isInteger()
/*  56:    */   {
/*  57:133 */     return this.field == VectorField.Integer;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public boolean isComplex()
/*  61:    */   {
/*  62:141 */     return this.field == VectorField.Complex;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public boolean isPattern()
/*  66:    */   {
/*  67:149 */     return this.field == VectorField.Pattern;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public String toString()
/*  71:    */   {
/*  72:163 */     StringBuilder buf = new StringBuilder();
/*  73:    */     
/*  74:165 */     buf.append("%%MatrixMarket vector ");
/*  75:167 */     if (isSparse()) {
/*  76:168 */       buf.append("coordinate ");
/*  77:    */     } else {
/*  78:170 */       buf.append("array ");
/*  79:    */     }
/*  80:172 */     if (isReal()) {
/*  81:173 */       buf.append("real\n");
/*  82:174 */     } else if (isComplex()) {
/*  83:175 */       buf.append("complex\n");
/*  84:176 */     } else if (isPattern()) {
/*  85:177 */       buf.append("pattern\n");
/*  86:178 */     } else if (isInteger()) {
/*  87:179 */       buf.append("integer\n");
/*  88:    */     } else {
/*  89:182 */       throw new IllegalArgumentException("Unknown field specification");
/*  90:    */     }
/*  91:184 */     return buf.toString();
/*  92:    */   }
/*  93:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.io.VectorInfo
 * JD-Core Version:    0.7.0.1
 */