/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ import java.io.DataInputStream;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.util.Hashtable;
/*   7:    */ import java.util.Vector;
/*   8:    */ 
/*   9:    */ public class Formatter
/*  10:    */ {
/*  11: 24 */   private Format format = null;
/*  12: 25 */   private FormatMap format_map = null;
/*  13:    */   
/*  14:    */   public Formatter(String paramString)
/*  15:    */     throws InvalidFormatException
/*  16:    */   {
/*  17: 30 */     this(new Format(paramString));
/*  18:    */   }
/*  19:    */   
/*  20:    */   public Formatter(Format paramFormat)
/*  21:    */   {
/*  22: 35 */     this.format = paramFormat;
/*  23:    */   }
/*  24:    */   
/*  25:    */   public void setFormatMap(FormatMap paramFormatMap)
/*  26:    */   {
/*  27: 41 */     this.format_map = paramFormatMap;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void write(Vector paramVector, PrintStream paramPrintStream)
/*  31:    */     throws OutputFormatException
/*  32:    */   {
/*  33: 48 */     FormatX localFormatX = new FormatX();
/*  34: 49 */     VectorAndPointer localVectorAndPointer = new VectorAndPointer(paramVector);
/*  35:    */     try
/*  36:    */     {
/*  37:    */       for (;;)
/*  38:    */       {
/*  39: 58 */         this.format.write(localVectorAndPointer, paramPrintStream);
/*  40: 59 */         localVectorAndPointer.checkCurrentElementForWrite(localFormatX);
/*  41: 60 */         paramPrintStream.println();
/*  42:    */       }
/*  43:    */       return;
/*  44:    */     }
/*  45:    */     catch (EndOfVectorOnWriteException localEndOfVectorOnWriteException) {}
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void write(int paramInt, PrintStream paramPrintStream)
/*  49:    */     throws OutputFormatException
/*  50:    */   {
/*  51: 70 */     write(new Integer(paramInt), paramPrintStream);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void write(long paramLong, PrintStream paramPrintStream)
/*  55:    */     throws OutputFormatException
/*  56:    */   {
/*  57: 76 */     write(new Long(paramLong), paramPrintStream);
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void write(float paramFloat, PrintStream paramPrintStream)
/*  61:    */     throws OutputFormatException
/*  62:    */   {
/*  63: 82 */     write(new Float(paramFloat), paramPrintStream);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void write(double paramDouble, PrintStream paramPrintStream)
/*  67:    */     throws OutputFormatException
/*  68:    */   {
/*  69: 88 */     write(new Double(paramDouble), paramPrintStream);
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void write(Object paramObject, PrintStream paramPrintStream)
/*  73:    */     throws OutputFormatException
/*  74:    */   {
/*  75: 94 */     Vector localVector = new Vector();
/*  76: 95 */     localVector.addElement(paramObject);
/*  77: 96 */     write(localVector, paramPrintStream);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void read(Vector paramVector, DataInputStream paramDataInputStream)
/*  81:    */     throws InputFormatException
/*  82:    */   {
/*  83:103 */     VectorAndPointer localVectorAndPointer = new VectorAndPointer(paramVector);
/*  84:104 */     InputStreamAndBuffer localInputStreamAndBuffer = new InputStreamAndBuffer(paramDataInputStream);
/*  85:105 */     this.format.read(localVectorAndPointer, localInputStreamAndBuffer, this.format_map);
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void read(Vector paramVector, Hashtable paramHashtable, DataInputStream paramDataInputStream)
/*  89:    */     throws InputFormatException
/*  90:    */   {
/*  91:111 */     StringsHashtableAndPointer localStringsHashtableAndPointer = new StringsHashtableAndPointer(paramVector, paramHashtable);
/*  92:112 */     InputStreamAndBuffer localInputStreamAndBuffer = new InputStreamAndBuffer(paramDataInputStream);
/*  93:113 */     this.format.read(localStringsHashtableAndPointer, localInputStreamAndBuffer, this.format_map);
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void read(String[] paramArrayOfString, Hashtable paramHashtable, DataInputStream paramDataInputStream)
/*  97:    */     throws InputFormatException
/*  98:    */   {
/*  99:119 */     Vector localVector = new Vector();
/* 100:120 */     for (int i = 0; i < paramArrayOfString.length; i++) {
/* 101:121 */       localVector.addElement(paramArrayOfString[i]);
/* 102:    */     }
/* 103:122 */     read(localVector, paramHashtable, paramDataInputStream);
/* 104:    */   }
/* 105:    */   
/* 106:    */   public Object read(DataInputStream paramDataInputStream)
/* 107:    */     throws InputFormatException
/* 108:    */   {
/* 109:128 */     Vector localVector = new Vector();
/* 110:129 */     read(localVector, paramDataInputStream);
/* 111:130 */     return localVector.elementAt(0);
/* 112:    */   }
/* 113:    */   
/* 114:    */   public boolean eof(DataInputStream paramDataInputStream)
/* 115:    */     throws IOException
/* 116:    */   {
/* 117:137 */     return paramDataInputStream.available() <= 0;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public String toString()
/* 121:    */   {
/* 122:143 */     return "[Formatter " + this.format.toString() + "]";
/* 123:    */   }
/* 124:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.Formatter
 * JD-Core Version:    0.7.0.1
 */