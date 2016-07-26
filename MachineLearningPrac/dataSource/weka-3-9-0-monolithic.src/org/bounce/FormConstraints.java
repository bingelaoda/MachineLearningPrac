/*   1:    */ package org.bounce;
/*   2:    */ 
/*   3:    */ public class FormConstraints
/*   4:    */ {
/*   5:    */   public static final int LEFT = 0;
/*   6:    */   public static final int BOTTOM = 1;
/*   7:    */   public static final int RIGHT = 2;
/*   8:    */   public static final int TOP = 3;
/*   9:    */   public static final int CENTER = 4;
/*  10:    */   public static final int FULL = 5;
/*  11:    */   private int position;
/*  12:    */   private int horizontalAlignment;
/*  13:    */   private int verticalAlignment;
/*  14:    */   private boolean filled;
/*  15:    */   
/*  16:    */   public FormConstraints()
/*  17:    */   {
/*  18: 82 */     this(5, 0, 4);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public FormConstraints(int paramInt)
/*  22:    */   {
/*  23: 94 */     this(paramInt, 0, 4);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public FormConstraints(int paramInt, boolean paramBoolean)
/*  27:    */   {
/*  28:107 */     this(paramInt, 0, 4);
/*  29:    */     
/*  30:109 */     this.filled = paramBoolean;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public FormConstraints(int paramInt1, int paramInt2)
/*  34:    */   {
/*  35:121 */     this(paramInt1, paramInt2, 4);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public FormConstraints(int paramInt1, int paramInt2, int paramInt3)
/*  39:    */   {
/*  40:133 */     this.position = paramInt1;
/*  41:134 */     this.horizontalAlignment = paramInt2;
/*  42:135 */     this.verticalAlignment = paramInt3;
/*  43:136 */     this.filled = false;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public FormConstraints(FormConstraints paramFormConstraints)
/*  47:    */   {
/*  48:146 */     this.horizontalAlignment = paramFormConstraints.getHorizontalAlignment();
/*  49:147 */     this.verticalAlignment = paramFormConstraints.getVerticalAlignment();
/*  50:148 */     this.filled = paramFormConstraints.isFilled();
/*  51:149 */     this.position = paramFormConstraints.getPosition();
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void setFilled(boolean paramBoolean)
/*  55:    */   {
/*  56:160 */     this.filled = paramBoolean;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public boolean isFilled()
/*  60:    */   {
/*  61:171 */     return this.filled;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setHorizontalAlignment(int paramInt)
/*  65:    */   {
/*  66:184 */     this.horizontalAlignment = paramInt;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public int getHorizontalAlignment()
/*  70:    */   {
/*  71:194 */     return this.horizontalAlignment;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setVerticalAlignment(int paramInt)
/*  75:    */   {
/*  76:207 */     this.verticalAlignment = paramInt;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public int getVerticalAlignment()
/*  80:    */   {
/*  81:217 */     return this.verticalAlignment;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setPosition(int paramInt)
/*  85:    */   {
/*  86:229 */     this.position = paramInt;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public int getPosition()
/*  90:    */   {
/*  91:239 */     return this.position;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public boolean equals(Object paramObject)
/*  95:    */   {
/*  96:251 */     boolean bool = true;
/*  97:253 */     if (this != paramObject) {
/*  98:254 */       if ((paramObject instanceof FormConstraints))
/*  99:    */       {
/* 100:255 */         FormConstraints localFormConstraints = (FormConstraints)paramObject;
/* 101:257 */         if (localFormConstraints.isFilled() != this.filled) {
/* 102:258 */           bool = false;
/* 103:259 */         } else if (localFormConstraints.getPosition() != this.position) {
/* 104:260 */           bool = false;
/* 105:261 */         } else if (localFormConstraints.getHorizontalAlignment() != this.horizontalAlignment) {
/* 106:262 */           bool = false;
/* 107:263 */         } else if (localFormConstraints.getVerticalAlignment() != this.verticalAlignment) {
/* 108:264 */           bool = false;
/* 109:    */         }
/* 110:    */       }
/* 111:    */       else
/* 112:    */       {
/* 113:267 */         bool = false;
/* 114:    */       }
/* 115:    */     }
/* 116:271 */     return bool;
/* 117:    */   }
/* 118:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.bounce.FormConstraints
 * JD-Core Version:    0.7.0.1
 */