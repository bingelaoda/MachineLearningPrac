/*    1:     */ package org.j_paine.formatter;
/*    2:     */ 
/*    3:     */ class IllegalObjectOnWriteException
/*    4:     */   extends OutputFormatException
/*    5:     */ {
/*    6:     */   public IllegalObjectOnWriteException(Object paramObject, int paramInt, String paramString)
/*    7:     */   {
/*    8:1417 */     this("Illegal object while writing formatted data:\n  Object = \"" + paramObject + "\"\n" + "  Index  = " + paramInt + "\n" + "  Format = " + paramString + " .");
/*    9:     */   }
/*   10:     */   
/*   11:     */   public IllegalObjectOnWriteException(String paramString)
/*   12:     */   {
/*   13:1426 */     super(paramString);
/*   14:     */   }
/*   15:     */   
/*   16:     */   public IllegalObjectOnWriteException() {}
/*   17:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.IllegalObjectOnWriteException
 * JD-Core Version:    0.7.0.1
 */