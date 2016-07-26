/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ 
/*   6:    */ class ExplodingInputStream
/*   7:    */   extends InputStream
/*   8:    */ {
/*   9:    */   private final InputStream in;
/*  10:    */   private BitStream bits;
/*  11:    */   private final int dictionarySize;
/*  12:    */   private final int numberOfTrees;
/*  13:    */   private final int minimumMatchLength;
/*  14:    */   private BinaryTree literalTree;
/*  15:    */   private BinaryTree lengthTree;
/*  16:    */   private BinaryTree distanceTree;
/*  17: 63 */   private final CircularBuffer buffer = new CircularBuffer(32768);
/*  18:    */   
/*  19:    */   public ExplodingInputStream(int dictionarySize, int numberOfTrees, InputStream in)
/*  20:    */   {
/*  21: 74 */     if ((dictionarySize != 4096) && (dictionarySize != 8192)) {
/*  22: 75 */       throw new IllegalArgumentException("The dictionary size must be 4096 or 8192");
/*  23:    */     }
/*  24: 77 */     if ((numberOfTrees != 2) && (numberOfTrees != 3)) {
/*  25: 78 */       throw new IllegalArgumentException("The number of trees must be 2 or 3");
/*  26:    */     }
/*  27: 80 */     this.dictionarySize = dictionarySize;
/*  28: 81 */     this.numberOfTrees = numberOfTrees;
/*  29: 82 */     this.minimumMatchLength = numberOfTrees;
/*  30: 83 */     this.in = in;
/*  31:    */   }
/*  32:    */   
/*  33:    */   private void init()
/*  34:    */     throws IOException
/*  35:    */   {
/*  36: 92 */     if (this.bits == null)
/*  37:    */     {
/*  38: 93 */       if (this.numberOfTrees == 3) {
/*  39: 94 */         this.literalTree = BinaryTree.decode(this.in, 256);
/*  40:    */       }
/*  41: 97 */       this.lengthTree = BinaryTree.decode(this.in, 64);
/*  42: 98 */       this.distanceTree = BinaryTree.decode(this.in, 64);
/*  43:    */       
/*  44:100 */       this.bits = new BitStream(this.in);
/*  45:    */     }
/*  46:    */   }
/*  47:    */   
/*  48:    */   public int read()
/*  49:    */     throws IOException
/*  50:    */   {
/*  51:106 */     if (!this.buffer.available()) {
/*  52:107 */       fillBuffer();
/*  53:    */     }
/*  54:110 */     return this.buffer.get();
/*  55:    */   }
/*  56:    */   
/*  57:    */   private void fillBuffer()
/*  58:    */     throws IOException
/*  59:    */   {
/*  60:118 */     init();
/*  61:    */     
/*  62:120 */     int bit = this.bits.nextBit();
/*  63:121 */     if (bit == 1)
/*  64:    */     {
/*  65:    */       int literal;
/*  66:    */       int literal;
/*  67:124 */       if (this.literalTree != null) {
/*  68:125 */         literal = this.literalTree.read(this.bits);
/*  69:    */       } else {
/*  70:127 */         literal = this.bits.nextByte();
/*  71:    */       }
/*  72:130 */       if (literal == -1) {
/*  73:132 */         return;
/*  74:    */       }
/*  75:135 */       this.buffer.put(literal);
/*  76:    */     }
/*  77:137 */     else if (bit == 0)
/*  78:    */     {
/*  79:139 */       int distanceLowSize = this.dictionarySize == 4096 ? 6 : 7;
/*  80:140 */       int distanceLow = (int)this.bits.nextBits(distanceLowSize);
/*  81:141 */       int distanceHigh = this.distanceTree.read(this.bits);
/*  82:142 */       if ((distanceHigh == -1) && (distanceLow <= 0)) {
/*  83:144 */         return;
/*  84:    */       }
/*  85:146 */       int distance = distanceHigh << distanceLowSize | distanceLow;
/*  86:    */       
/*  87:148 */       int length = this.lengthTree.read(this.bits);
/*  88:149 */       if (length == 63) {
/*  89:150 */         length = (int)(length + this.bits.nextBits(8));
/*  90:    */       }
/*  91:152 */       length += this.minimumMatchLength;
/*  92:    */       
/*  93:154 */       this.buffer.copy(distance + 1, length);
/*  94:    */     }
/*  95:    */   }
/*  96:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ExplodingInputStream
 * JD-Core Version:    0.7.0.1
 */