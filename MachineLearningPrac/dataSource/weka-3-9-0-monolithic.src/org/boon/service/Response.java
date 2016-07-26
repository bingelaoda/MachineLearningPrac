/*   1:    */ package org.boon.service;
/*   2:    */ 
/*   3:    */ import java.util.Map;
/*   4:    */ import org.boon.core.Conversions;
/*   5:    */ 
/*   6:    */ public class Response
/*   7:    */ {
/*   8:    */   private final int status;
/*   9:    */   private final Object headers;
/*  10:    */   private final Object statusMessage;
/*  11:    */   private final Object payload;
/*  12:    */   private final Class<? extends Enum> enumStatusClass;
/*  13:    */   
/*  14:    */   public Response(int status, Object headers, Object statusMessage, Object payload)
/*  15:    */   {
/*  16: 48 */     this.status = status;
/*  17: 49 */     this.headers = headers;
/*  18: 50 */     this.statusMessage = statusMessage;
/*  19: 51 */     this.payload = payload;
/*  20: 52 */     this.enumStatusClass = null;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public Response(int status, Object headers, Object statusMessage, Object payload, Class<? extends Enum> enumStatusClass)
/*  24:    */   {
/*  25: 57 */     this.status = status;
/*  26: 58 */     this.headers = headers;
/*  27: 59 */     this.statusMessage = statusMessage;
/*  28: 60 */     this.payload = payload;
/*  29: 61 */     this.enumStatusClass = enumStatusClass;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public int status()
/*  33:    */   {
/*  34: 66 */     return this.status;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public <E extends Enum> E statusEnum(Class<E> enumClass)
/*  38:    */   {
/*  39: 71 */     return Conversions.toEnum(enumClass, this.status);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public Enum statusEnum()
/*  43:    */   {
/*  44: 76 */     return Conversions.toEnum(this.enumStatusClass, this.status);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public Object headers()
/*  48:    */   {
/*  49: 80 */     return this.headers;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public Map<String, Object> headerMap()
/*  53:    */   {
/*  54: 85 */     return Conversions.toMap(this.headers);
/*  55:    */   }
/*  56:    */   
/*  57:    */   public Object statusMessage()
/*  58:    */   {
/*  59: 89 */     return this.statusMessage;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String statusMessageAsString()
/*  63:    */   {
/*  64: 94 */     return Conversions.toString(this.statusMessage);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public Object payload()
/*  68:    */   {
/*  69: 99 */     return this.payload;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public String payloadAsString()
/*  73:    */   {
/*  74:104 */     return Conversions.toString(this.payload);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public static Response response(int status, Map headers, String statusMessage, String payload)
/*  78:    */   {
/*  79:108 */     return new Response(status, headers, statusMessage, payload);
/*  80:    */   }
/*  81:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.service.Response
 * JD-Core Version:    0.7.0.1
 */