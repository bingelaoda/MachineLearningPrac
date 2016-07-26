/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ 
/*   5:    */ public abstract interface Matrix
/*   6:    */   extends Iterable<MatrixEntry>, Serializable
/*   7:    */ {
/*   8:    */   public abstract int numRows();
/*   9:    */   
/*  10:    */   public abstract int numColumns();
/*  11:    */   
/*  12:    */   public abstract boolean isSquare();
/*  13:    */   
/*  14:    */   public abstract void set(int paramInt1, int paramInt2, double paramDouble);
/*  15:    */   
/*  16:    */   public abstract void add(int paramInt1, int paramInt2, double paramDouble);
/*  17:    */   
/*  18:    */   public abstract double get(int paramInt1, int paramInt2);
/*  19:    */   
/*  20:    */   public abstract Matrix copy();
/*  21:    */   
/*  22:    */   public abstract Matrix zero();
/*  23:    */   
/*  24:    */   public abstract Vector mult(Vector paramVector1, Vector paramVector2);
/*  25:    */   
/*  26:    */   public abstract Vector mult(double paramDouble, Vector paramVector1, Vector paramVector2);
/*  27:    */   
/*  28:    */   public abstract Vector multAdd(Vector paramVector1, Vector paramVector2);
/*  29:    */   
/*  30:    */   public abstract Vector multAdd(double paramDouble, Vector paramVector1, Vector paramVector2);
/*  31:    */   
/*  32:    */   public abstract Vector transMult(Vector paramVector1, Vector paramVector2);
/*  33:    */   
/*  34:    */   public abstract Vector transMult(double paramDouble, Vector paramVector1, Vector paramVector2);
/*  35:    */   
/*  36:    */   public abstract Vector transMultAdd(Vector paramVector1, Vector paramVector2);
/*  37:    */   
/*  38:    */   public abstract Vector transMultAdd(double paramDouble, Vector paramVector1, Vector paramVector2);
/*  39:    */   
/*  40:    */   public abstract Vector solve(Vector paramVector1, Vector paramVector2)
/*  41:    */     throws MatrixSingularException, MatrixNotSPDException;
/*  42:    */   
/*  43:    */   public abstract Vector transSolve(Vector paramVector1, Vector paramVector2)
/*  44:    */     throws MatrixSingularException, MatrixNotSPDException;
/*  45:    */   
/*  46:    */   public abstract Matrix rank1(Vector paramVector);
/*  47:    */   
/*  48:    */   public abstract Matrix rank1(double paramDouble, Vector paramVector);
/*  49:    */   
/*  50:    */   public abstract Matrix rank1(Vector paramVector1, Vector paramVector2);
/*  51:    */   
/*  52:    */   public abstract Matrix rank1(double paramDouble, Vector paramVector1, Vector paramVector2);
/*  53:    */   
/*  54:    */   public abstract Matrix rank2(Vector paramVector1, Vector paramVector2);
/*  55:    */   
/*  56:    */   public abstract Matrix rank2(double paramDouble, Vector paramVector1, Vector paramVector2);
/*  57:    */   
/*  58:    */   public abstract Matrix mult(Matrix paramMatrix1, Matrix paramMatrix2);
/*  59:    */   
/*  60:    */   public abstract Matrix mult(double paramDouble, Matrix paramMatrix1, Matrix paramMatrix2);
/*  61:    */   
/*  62:    */   public abstract Matrix multAdd(Matrix paramMatrix1, Matrix paramMatrix2);
/*  63:    */   
/*  64:    */   public abstract Matrix multAdd(double paramDouble, Matrix paramMatrix1, Matrix paramMatrix2);
/*  65:    */   
/*  66:    */   public abstract Matrix transAmult(Matrix paramMatrix1, Matrix paramMatrix2);
/*  67:    */   
/*  68:    */   public abstract Matrix transAmult(double paramDouble, Matrix paramMatrix1, Matrix paramMatrix2);
/*  69:    */   
/*  70:    */   public abstract Matrix transAmultAdd(Matrix paramMatrix1, Matrix paramMatrix2);
/*  71:    */   
/*  72:    */   public abstract Matrix transAmultAdd(double paramDouble, Matrix paramMatrix1, Matrix paramMatrix2);
/*  73:    */   
/*  74:    */   public abstract Matrix transBmult(Matrix paramMatrix1, Matrix paramMatrix2);
/*  75:    */   
/*  76:    */   public abstract Matrix transBmult(double paramDouble, Matrix paramMatrix1, Matrix paramMatrix2);
/*  77:    */   
/*  78:    */   public abstract Matrix transBmultAdd(Matrix paramMatrix1, Matrix paramMatrix2);
/*  79:    */   
/*  80:    */   public abstract Matrix transBmultAdd(double paramDouble, Matrix paramMatrix1, Matrix paramMatrix2);
/*  81:    */   
/*  82:    */   public abstract Matrix transABmult(Matrix paramMatrix1, Matrix paramMatrix2);
/*  83:    */   
/*  84:    */   public abstract Matrix transABmult(double paramDouble, Matrix paramMatrix1, Matrix paramMatrix2);
/*  85:    */   
/*  86:    */   public abstract Matrix transABmultAdd(Matrix paramMatrix1, Matrix paramMatrix2);
/*  87:    */   
/*  88:    */   public abstract Matrix transABmultAdd(double paramDouble, Matrix paramMatrix1, Matrix paramMatrix2);
/*  89:    */   
/*  90:    */   public abstract Matrix solve(Matrix paramMatrix1, Matrix paramMatrix2)
/*  91:    */     throws MatrixSingularException, MatrixNotSPDException;
/*  92:    */   
/*  93:    */   public abstract Matrix transSolve(Matrix paramMatrix1, Matrix paramMatrix2)
/*  94:    */     throws MatrixSingularException, MatrixNotSPDException;
/*  95:    */   
/*  96:    */   public abstract Matrix rank1(Matrix paramMatrix);
/*  97:    */   
/*  98:    */   public abstract Matrix rank1(double paramDouble, Matrix paramMatrix);
/*  99:    */   
/* 100:    */   public abstract Matrix transRank1(Matrix paramMatrix);
/* 101:    */   
/* 102:    */   public abstract Matrix transRank1(double paramDouble, Matrix paramMatrix);
/* 103:    */   
/* 104:    */   public abstract Matrix rank2(Matrix paramMatrix1, Matrix paramMatrix2);
/* 105:    */   
/* 106:    */   public abstract Matrix rank2(double paramDouble, Matrix paramMatrix1, Matrix paramMatrix2);
/* 107:    */   
/* 108:    */   public abstract Matrix transRank2(Matrix paramMatrix1, Matrix paramMatrix2);
/* 109:    */   
/* 110:    */   public abstract Matrix transRank2(double paramDouble, Matrix paramMatrix1, Matrix paramMatrix2);
/* 111:    */   
/* 112:    */   public abstract Matrix scale(double paramDouble);
/* 113:    */   
/* 114:    */   public abstract Matrix set(Matrix paramMatrix);
/* 115:    */   
/* 116:    */   public abstract Matrix set(double paramDouble, Matrix paramMatrix);
/* 117:    */   
/* 118:    */   public abstract Matrix add(Matrix paramMatrix);
/* 119:    */   
/* 120:    */   public abstract Matrix add(double paramDouble, Matrix paramMatrix);
/* 121:    */   
/* 122:    */   public abstract Matrix transpose();
/* 123:    */   
/* 124:    */   public abstract Matrix transpose(Matrix paramMatrix);
/* 125:    */   
/* 126:    */   public abstract double norm(Norm paramNorm);
/* 127:    */   
/* 128:    */   public static enum Norm
/* 129:    */   {
/* 130:720 */     One,  Frobenius,  Infinity,  Maxvalue;
/* 131:    */     
/* 132:    */     private Norm() {}
/* 133:    */     
/* 134:    */     public String netlib()
/* 135:    */     {
/* 136:744 */       if (this == One) {
/* 137:745 */         return "1";
/* 138:    */       }
/* 139:746 */       if (this == Infinity) {
/* 140:747 */         return "I";
/* 141:    */       }
/* 142:749 */       throw new IllegalArgumentException("Norm must be the 1 or the Infinity norm");
/* 143:    */     }
/* 144:    */   }
/* 145:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.Matrix
 * JD-Core Version:    0.7.0.1
 */