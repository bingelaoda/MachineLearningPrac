/*    1:     */ package org.j_paine.formatter;
/*    2:     */ 
/*    3:     */ import java.io.DataInputStream;
/*    4:     */ import java.io.IOException;
/*    5:     */ 
/*    6:     */ class InputStreamAndBuffer
/*    7:     */ {
/*    8:     */   private DataInputStream in;
/*    9:     */   private String line;
/*   10:     */   private int ptr;
/*   11:     */   private int line_number;
/*   12:     */   private boolean nothing_read;
/*   13:     */   
/*   14:     */   public InputStreamAndBuffer(DataInputStream paramDataInputStream)
/*   15:     */   {
/*   16:1254 */     this.in = paramDataInputStream;
/*   17:1255 */     this.ptr = 0;
/*   18:1256 */     this.line = "";
/*   19:1257 */     this.line_number = 0;
/*   20:1258 */     this.nothing_read = true;
/*   21:     */   }
/*   22:     */   
/*   23:     */   public void readLine(int paramInt, FormatElement paramFormatElement)
/*   24:     */     throws EndOfFileWhenStartingReadException, LineMissingOnReadException, IOExceptionOnReadException
/*   25:     */   {
/*   26:     */     try
/*   27:     */     {
/*   28:1271 */       String str = this.in.readLine();
/*   29:1273 */       if (str == null)
/*   30:     */       {
/*   31:1274 */         if (this.nothing_read) {
/*   32:1275 */           throw new EndOfFileWhenStartingReadException(paramInt, paramFormatElement.toString(), this.line, this.line_number);
/*   33:     */         }
/*   34:1281 */         throw new LineMissingOnReadException(paramInt, paramFormatElement.toString(), this.line, this.line_number);
/*   35:     */       }
/*   36:1288 */       this.ptr = 0;
/*   37:1289 */       this.nothing_read = false;
/*   38:1290 */       this.line_number += 1;
/*   39:1291 */       this.line = str;
/*   40:     */     }
/*   41:     */     catch (IOException localIOException)
/*   42:     */     {
/*   43:1298 */       throw new IOExceptionOnReadException(this.line, this.line_number, localIOException.getMessage());
/*   44:     */     }
/*   45:     */   }
/*   46:     */   
/*   47:     */   public String getSlice(int paramInt1, int paramInt2, FormatElement paramFormatElement)
/*   48:     */     throws DataMissingOnReadException, LineMissingOnReadException, EndOfFileWhenStartingReadException, IOExceptionOnReadException
/*   49:     */   {
/*   50:1316 */     if (this.nothing_read) {
/*   51:1317 */       readLine(paramInt2, paramFormatElement);
/*   52:     */     }
/*   53:1318 */     if (this.ptr + paramInt1 > this.line.length()) {
/*   54:1322 */       return this.line.substring(this.ptr);
/*   55:     */     }
/*   56:1325 */     return this.line.substring(this.ptr, this.ptr + paramInt1);
/*   57:     */   }
/*   58:     */   
/*   59:     */   public void advance(int paramInt)
/*   60:     */   {
/*   61:1334 */     this.ptr += paramInt;
/*   62:     */   }
/*   63:     */   
/*   64:     */   public String getLineErrorReport()
/*   65:     */   {
/*   66:1343 */     StringBuffer localStringBuffer = new StringBuffer();
/*   67:     */     
/*   68:     */ 
/*   69:1346 */     localStringBuffer.append("  Line number = " + this.line_number + ":\n");
/*   70:     */     
/*   71:     */ 
/*   72:1349 */     localStringBuffer.append(this.line + "\n");
/*   73:1352 */     for (int i = 0; i < this.ptr; i++) {
/*   74:1353 */       localStringBuffer.append(" ");
/*   75:     */     }
/*   76:1354 */     localStringBuffer.append("^");
/*   77:     */     
/*   78:1356 */     return localStringBuffer.toString();
/*   79:     */   }
/*   80:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.InputStreamAndBuffer
 * JD-Core Version:    0.7.0.1
 */