/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.HashMap;
/*   5:    */ import java.util.Map;
/*   6:    */ 
/*   7:    */ public enum ZipMethod
/*   8:    */ {
/*   9: 39 */   STORED(0),  UNSHRINKING(1),  EXPANDING_LEVEL_1(2),  EXPANDING_LEVEL_2(3),  EXPANDING_LEVEL_3(4),  EXPANDING_LEVEL_4(5),  IMPLODING(6),  TOKENIZATION(7),  DEFLATED(8),  ENHANCED_DEFLATED(9),  PKWARE_IMPLODING(10),  BZIP2(12),  LZMA(14),  JPEG(96),  WAVPACK(97),  PPMD(98),  AES_ENCRYPTED(99),  UNKNOWN;
/*  10:    */   
/*  11:    */   static final int UNKNOWN_CODE = -1;
/*  12:    */   private final int code;
/*  13:    */   private static final Map<Integer, ZipMethod> codeToEnum;
/*  14:    */   
/*  15:    */   static
/*  16:    */   {
/*  17:176 */     Map<Integer, ZipMethod> cte = new HashMap();
/*  18:177 */     for (ZipMethod method : values()) {
/*  19:178 */       cte.put(Integer.valueOf(method.getCode()), method);
/*  20:    */     }
/*  21:180 */     codeToEnum = Collections.unmodifiableMap(cte);
/*  22:    */   }
/*  23:    */   
/*  24:    */   private ZipMethod()
/*  25:    */   {
/*  26:184 */     this(-1);
/*  27:    */   }
/*  28:    */   
/*  29:    */   private ZipMethod(int code)
/*  30:    */   {
/*  31:191 */     this.code = code;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public int getCode()
/*  35:    */   {
/*  36:202 */     return this.code;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public static ZipMethod getMethodByCode(int code)
/*  40:    */   {
/*  41:214 */     return (ZipMethod)codeToEnum.get(Integer.valueOf(code));
/*  42:    */   }
/*  43:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.ZipMethod
 * JD-Core Version:    0.7.0.1
 */