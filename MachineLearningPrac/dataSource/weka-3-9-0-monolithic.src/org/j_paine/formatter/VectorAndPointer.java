/*    1:     */ package org.j_paine.formatter;
/*    2:     */ 
/*    3:     */ import java.util.Vector;
/*    4:     */ 
/*    5:     */ class VectorAndPointer
/*    6:     */   implements FormatInputList, FormatOutputList
/*    7:     */ {
/*    8:1068 */   private Vector v = null;
/*    9:1069 */   private int vecptr = 0;
/*   10:     */   
/*   11:     */   public VectorAndPointer(Vector paramVector)
/*   12:     */   {
/*   13:1076 */     this.v = paramVector;
/*   14:     */   }
/*   15:     */   
/*   16:     */   public VectorAndPointer()
/*   17:     */   {
/*   18:1082 */     this.v = new Vector();
/*   19:     */   }
/*   20:     */   
/*   21:     */   public boolean hasCurrentElement()
/*   22:     */   {
/*   23:1088 */     return this.vecptr < this.v.size();
/*   24:     */   }
/*   25:     */   
/*   26:     */   public void checkCurrentElementForWrite(FormatElement paramFormatElement)
/*   27:     */     throws EndOfVectorOnWriteException
/*   28:     */   {
/*   29:1095 */     if (!hasCurrentElement()) {
/*   30:1096 */       throw new EndOfVectorOnWriteException(this.vecptr, paramFormatElement.toString());
/*   31:     */     }
/*   32:     */   }
/*   33:     */   
/*   34:     */   public void checkCurrentElementForRead(FormatElement paramFormatElement, InputStreamAndBuffer paramInputStreamAndBuffer) {}
/*   35:     */   
/*   36:     */   public Object getCurrentElement()
/*   37:     */   {
/*   38:1118 */     return this.v.elementAt(this.vecptr);
/*   39:     */   }
/*   40:     */   
/*   41:     */   public Object getCurrentElementAndAdvance()
/*   42:     */   {
/*   43:1123 */     this.vecptr += 1;
/*   44:1124 */     return this.v.elementAt(this.vecptr - 1);
/*   45:     */   }
/*   46:     */   
/*   47:     */   public void putElementAndAdvance(Object paramObject, FormatElement paramFormatElement, InputStreamAndBuffer paramInputStreamAndBuffer)
/*   48:     */   {
/*   49:1138 */     this.v.addElement(paramObject);
/*   50:1139 */     this.vecptr += 1;
/*   51:     */   }
/*   52:     */   
/*   53:     */   public void advance()
/*   54:     */   {
/*   55:1145 */     this.vecptr += 1;
/*   56:     */   }
/*   57:     */   
/*   58:     */   public int getPtr()
/*   59:     */   {
/*   60:1154 */     return this.vecptr;
/*   61:     */   }
/*   62:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.VectorAndPointer
 * JD-Core Version:    0.7.0.1
 */