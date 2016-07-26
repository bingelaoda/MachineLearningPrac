/*    1:     */ package org.j_paine.formatter;
/*    2:     */ 
/*    3:     */ import java.util.Hashtable;
/*    4:     */ import java.util.Vector;
/*    5:     */ 
/*    6:     */ class StringsHashtableAndPointer
/*    7:     */   implements FormatInputList
/*    8:     */ {
/*    9:     */   private VectorAndPointer vp;
/*   10:     */   private Hashtable ht;
/*   11:     */   
/*   12:     */   public StringsHashtableAndPointer(Vector paramVector, Hashtable paramHashtable)
/*   13:     */   {
/*   14:1170 */     this.vp = new VectorAndPointer(paramVector);
/*   15:1171 */     this.ht = paramHashtable;
/*   16:     */   }
/*   17:     */   
/*   18:     */   public void checkCurrentElementForRead(FormatElement paramFormatElement, InputStreamAndBuffer paramInputStreamAndBuffer)
/*   19:     */     throws EndOfKeyVectorOnReadException
/*   20:     */   {
/*   21:1184 */     if (!this.vp.hasCurrentElement()) {
/*   22:1185 */       throw new EndOfKeyVectorOnReadException(this.vp.getPtr(), paramFormatElement.toString(), paramInputStreamAndBuffer.getLineErrorReport());
/*   23:     */     }
/*   24:     */   }
/*   25:     */   
/*   26:     */   public void putElementAndAdvance(Object paramObject, FormatElement paramFormatElement, InputStreamAndBuffer paramInputStreamAndBuffer)
/*   27:     */     throws KeyNotStringOnReadException
/*   28:     */   {
/*   29:1204 */     Object localObject = this.vp.getCurrentElement();
/*   30:1205 */     if ((localObject instanceof String))
/*   31:     */     {
/*   32:1206 */       this.ht.put((String)localObject, paramObject);
/*   33:1207 */       this.vp.advance();
/*   34:     */     }
/*   35:     */     else
/*   36:     */     {
/*   37:1210 */       throw new KeyNotStringOnReadException(localObject, this.vp.getPtr(), paramFormatElement.toString(), paramInputStreamAndBuffer.getLineErrorReport());
/*   38:     */     }
/*   39:     */   }
/*   40:     */   
/*   41:     */   public int getPtr()
/*   42:     */   {
/*   43:1223 */     return this.vp.getPtr();
/*   44:     */   }
/*   45:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.StringsHashtableAndPointer
 * JD-Core Version:    0.7.0.1
 */