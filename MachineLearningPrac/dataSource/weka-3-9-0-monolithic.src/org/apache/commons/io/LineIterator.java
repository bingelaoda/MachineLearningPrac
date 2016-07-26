/*   1:    */ package org.apache.commons.io;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.Reader;
/*   5:    */ import java.util.Iterator;
/*   6:    */ import java.util.NoSuchElementException;
/*   7:    */ 
/*   8:    */ public class LineIterator
/*   9:    */   implements Iterator
/*  10:    */ {
/*  11:    */   private final BufferedReader bufferedReader;
/*  12:    */   private String cachedLine;
/*  13: 60 */   private boolean finished = false;
/*  14:    */   
/*  15:    */   public LineIterator(Reader reader)
/*  16:    */     throws IllegalArgumentException
/*  17:    */   {
/*  18: 69 */     if (reader == null) {
/*  19: 70 */       throw new IllegalArgumentException("Reader must not be null");
/*  20:    */     }
/*  21: 72 */     if ((reader instanceof BufferedReader)) {
/*  22: 73 */       this.bufferedReader = ((BufferedReader)reader);
/*  23:    */     } else {
/*  24: 75 */       this.bufferedReader = new BufferedReader(reader);
/*  25:    */     }
/*  26:    */   }
/*  27:    */   
/*  28:    */   /* Error */
/*  29:    */   public boolean hasNext()
/*  30:    */   {
/*  31:    */     // Byte code:
/*  32:    */     //   0: aload_0
/*  33:    */     //   1: getfield 9	org/apache/commons/io/LineIterator:cachedLine	Ljava/lang/String;
/*  34:    */     //   4: ifnull +5 -> 9
/*  35:    */     //   7: iconst_1
/*  36:    */     //   8: ireturn
/*  37:    */     //   9: aload_0
/*  38:    */     //   10: getfield 2	org/apache/commons/io/LineIterator:finished	Z
/*  39:    */     //   13: ifeq +5 -> 18
/*  40:    */     //   16: iconst_0
/*  41:    */     //   17: ireturn
/*  42:    */     //   18: aload_0
/*  43:    */     //   19: getfield 7	org/apache/commons/io/LineIterator:bufferedReader	Ljava/io/BufferedReader;
/*  44:    */     //   22: invokevirtual 10	java/io/BufferedReader:readLine	()Ljava/lang/String;
/*  45:    */     //   25: astore_1
/*  46:    */     //   26: aload_1
/*  47:    */     //   27: ifnonnull +10 -> 37
/*  48:    */     //   30: aload_0
/*  49:    */     //   31: iconst_1
/*  50:    */     //   32: putfield 2	org/apache/commons/io/LineIterator:finished	Z
/*  51:    */     //   35: iconst_0
/*  52:    */     //   36: ireturn
/*  53:    */     //   37: aload_0
/*  54:    */     //   38: aload_1
/*  55:    */     //   39: invokevirtual 11	org/apache/commons/io/LineIterator:isValidLine	(Ljava/lang/String;)Z
/*  56:    */     //   42: ifeq +10 -> 52
/*  57:    */     //   45: aload_0
/*  58:    */     //   46: aload_1
/*  59:    */     //   47: putfield 9	org/apache/commons/io/LineIterator:cachedLine	Ljava/lang/String;
/*  60:    */     //   50: iconst_1
/*  61:    */     //   51: ireturn
/*  62:    */     //   52: goto -34 -> 18
/*  63:    */     //   55: astore_1
/*  64:    */     //   56: aload_0
/*  65:    */     //   57: invokevirtual 13	org/apache/commons/io/LineIterator:close	()V
/*  66:    */     //   60: new 14	java/lang/IllegalStateException
/*  67:    */     //   63: dup
/*  68:    */     //   64: aload_1
/*  69:    */     //   65: invokevirtual 15	java/io/IOException:toString	()Ljava/lang/String;
/*  70:    */     //   68: invokespecial 16	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
/*  71:    */     //   71: athrow
/*  72:    */     // Line number table:
/*  73:    */     //   Java source line #89	-> byte code offset #0
/*  74:    */     //   Java source line #90	-> byte code offset #7
/*  75:    */     //   Java source line #91	-> byte code offset #9
/*  76:    */     //   Java source line #92	-> byte code offset #16
/*  77:    */     //   Java source line #96	-> byte code offset #18
/*  78:    */     //   Java source line #97	-> byte code offset #26
/*  79:    */     //   Java source line #98	-> byte code offset #30
/*  80:    */     //   Java source line #99	-> byte code offset #35
/*  81:    */     //   Java source line #100	-> byte code offset #37
/*  82:    */     //   Java source line #101	-> byte code offset #45
/*  83:    */     //   Java source line #102	-> byte code offset #50
/*  84:    */     //   Java source line #104	-> byte code offset #52
/*  85:    */     //   Java source line #105	-> byte code offset #55
/*  86:    */     //   Java source line #106	-> byte code offset #56
/*  87:    */     //   Java source line #107	-> byte code offset #60
/*  88:    */     // Local variable table:
/*  89:    */     //   start	length	slot	name	signature
/*  90:    */     //   0	72	0	this	LineIterator
/*  91:    */     //   25	22	1	line	String
/*  92:    */     //   55	10	1	ioe	java.io.IOException
/*  93:    */     // Exception table:
/*  94:    */     //   from	to	target	type
/*  95:    */     //   18	36	55	java/io/IOException
/*  96:    */     //   37	51	55	java/io/IOException
/*  97:    */     //   52	55	55	java/io/IOException
/*  98:    */   }
/*  99:    */   
/* 100:    */   protected boolean isValidLine(String line)
/* 101:    */   {
/* 102:119 */     return true;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public Object next()
/* 106:    */   {
/* 107:129 */     return nextLine();
/* 108:    */   }
/* 109:    */   
/* 110:    */   public String nextLine()
/* 111:    */   {
/* 112:139 */     if (!hasNext()) {
/* 113:140 */       throw new NoSuchElementException("No more lines");
/* 114:    */     }
/* 115:142 */     String currentLine = this.cachedLine;
/* 116:143 */     this.cachedLine = null;
/* 117:144 */     return currentLine;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void close()
/* 121:    */   {
/* 122:155 */     this.finished = true;
/* 123:156 */     IOUtils.closeQuietly(this.bufferedReader);
/* 124:157 */     this.cachedLine = null;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public void remove()
/* 128:    */   {
/* 129:166 */     throw new UnsupportedOperationException("Remove unsupported on LineIterator");
/* 130:    */   }
/* 131:    */   
/* 132:    */   public static void closeQuietly(LineIterator iterator)
/* 133:    */   {
/* 134:176 */     if (iterator != null) {
/* 135:177 */       iterator.close();
/* 136:    */     }
/* 137:    */   }
/* 138:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.LineIterator
 * JD-Core Version:    0.7.0.1
 */