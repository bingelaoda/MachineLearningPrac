/*    1:     */ package org.j_paine.formatter;
/*    2:     */ 
/*    3:     */ class NumberTooWideOnWriteException
/*    4:     */   extends OutputFormatException
/*    5:     */ {
/*    6:     */   public NumberTooWideOnWriteException(Number paramNumber, int paramInt, String paramString)
/*    7:     */   {
/*    8:1477 */     this("Number too wide while writing formatted data:\n  Number = \"" + paramNumber + "\"\n" + "  Index  = " + paramInt + "\n" + "  Format = " + paramString + " .");
/*    9:     */   }
/*   10:     */   
/*   11:     */   public NumberTooWideOnWriteException(String paramString)
/*   12:     */   {
/*   13:1486 */     super(paramString);
/*   14:     */   }
/*   15:     */   
/*   16:     */   public NumberTooWideOnWriteException() {}
/*   17:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.NumberTooWideOnWriteException
 * JD-Core Version:    0.7.0.1
 */