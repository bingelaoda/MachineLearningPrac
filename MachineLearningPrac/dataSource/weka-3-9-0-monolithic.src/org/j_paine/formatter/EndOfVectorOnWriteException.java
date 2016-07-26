/*    1:     */ package org.j_paine.formatter;
/*    2:     */ 
/*    3:     */ class EndOfVectorOnWriteException
/*    4:     */   extends OutputFormatException
/*    5:     */ {
/*    6:     */   public EndOfVectorOnWriteException(int paramInt, String paramString)
/*    7:     */   {
/*    8:1388 */     this("End of vector while writing formatted data:\n  Index  = " + paramInt + "\n" + "  Format = " + paramString + " .");
/*    9:     */   }
/*   10:     */   
/*   11:     */   public EndOfVectorOnWriteException(String paramString)
/*   12:     */   {
/*   13:1396 */     super(paramString);
/*   14:     */   }
/*   15:     */   
/*   16:     */   public EndOfVectorOnWriteException() {}
/*   17:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.EndOfVectorOnWriteException
 * JD-Core Version:    0.7.0.1
 */