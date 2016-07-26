/*   1:    */ package org.apache.commons.io;
/*   2:    */ 
/*   3:    */ import java.io.ByteArrayInputStream;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.io.InputStreamReader;
/*   7:    */ import java.io.OutputStream;
/*   8:    */ import java.io.OutputStreamWriter;
/*   9:    */ import java.io.Reader;
/*  10:    */ import java.io.StringReader;
/*  11:    */ import java.io.Writer;
/*  12:    */ 
/*  13:    */ /**
/*  14:    */  * @deprecated
/*  15:    */  */
/*  16:    */ public class CopyUtils
/*  17:    */ {
/*  18:    */   private static final int DEFAULT_BUFFER_SIZE = 4096;
/*  19:    */   
/*  20:    */   public static void copy(byte[] input, OutputStream output)
/*  21:    */     throws IOException
/*  22:    */   {
/*  23:138 */     output.write(input);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public static void copy(byte[] input, Writer output)
/*  27:    */     throws IOException
/*  28:    */   {
/*  29:155 */     ByteArrayInputStream in = new ByteArrayInputStream(input);
/*  30:156 */     copy(in, output);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public static void copy(byte[] input, Writer output, String encoding)
/*  34:    */     throws IOException
/*  35:    */   {
/*  36:175 */     ByteArrayInputStream in = new ByteArrayInputStream(input);
/*  37:176 */     copy(in, output, encoding);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public static int copy(InputStream input, OutputStream output)
/*  41:    */     throws IOException
/*  42:    */   {
/*  43:196 */     byte[] buffer = new byte[4096];
/*  44:197 */     int count = 0;
/*  45:198 */     int n = 0;
/*  46:199 */     while (-1 != (n = input.read(buffer)))
/*  47:    */     {
/*  48:200 */       output.write(buffer, 0, n);
/*  49:201 */       count += n;
/*  50:    */     }
/*  51:203 */     return count;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public static int copy(Reader input, Writer output)
/*  55:    */     throws IOException
/*  56:    */   {
/*  57:221 */     char[] buffer = new char[4096];
/*  58:222 */     int count = 0;
/*  59:223 */     int n = 0;
/*  60:224 */     while (-1 != (n = input.read(buffer)))
/*  61:    */     {
/*  62:225 */       output.write(buffer, 0, n);
/*  63:226 */       count += n;
/*  64:    */     }
/*  65:228 */     return count;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public static void copy(InputStream input, Writer output)
/*  69:    */     throws IOException
/*  70:    */   {
/*  71:247 */     InputStreamReader in = new InputStreamReader(input);
/*  72:248 */     copy(in, output);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public static void copy(InputStream input, Writer output, String encoding)
/*  76:    */     throws IOException
/*  77:    */   {
/*  78:266 */     InputStreamReader in = new InputStreamReader(input, encoding);
/*  79:267 */     copy(in, output);
/*  80:    */   }
/*  81:    */   
/*  82:    */   public static void copy(Reader input, OutputStream output)
/*  83:    */     throws IOException
/*  84:    */   {
/*  85:286 */     OutputStreamWriter out = new OutputStreamWriter(output);
/*  86:287 */     copy(input, out);
/*  87:    */     
/*  88:    */ 
/*  89:290 */     out.flush();
/*  90:    */   }
/*  91:    */   
/*  92:    */   public static void copy(String input, OutputStream output)
/*  93:    */     throws IOException
/*  94:    */   {
/*  95:309 */     StringReader in = new StringReader(input);
/*  96:310 */     OutputStreamWriter out = new OutputStreamWriter(output);
/*  97:311 */     copy(in, out);
/*  98:    */     
/*  99:    */ 
/* 100:314 */     out.flush();
/* 101:    */   }
/* 102:    */   
/* 103:    */   public static void copy(String input, Writer output)
/* 104:    */     throws IOException
/* 105:    */   {
/* 106:329 */     output.write(input);
/* 107:    */   }
/* 108:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.io.CopyUtils
 * JD-Core Version:    0.7.0.1
 */