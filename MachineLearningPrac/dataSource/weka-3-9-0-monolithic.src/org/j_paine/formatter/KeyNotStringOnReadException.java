/*    1:     */ package org.j_paine.formatter;
/*    2:     */ 
/*    3:     */ class KeyNotStringOnReadException
/*    4:     */   extends InputFormatException
/*    5:     */ {
/*    6:     */   public KeyNotStringOnReadException(Object paramObject, int paramInt, String paramString1, String paramString2)
/*    7:     */   {
/*    8:1658 */     this("Key not string while reading formatted data:\n  Key    = \"" + paramInt + "\"\n" + "  Index  = " + paramInt + "\n" + "  Format = " + paramString1 + "\n" + paramString2 + "\n");
/*    9:     */   }
/*   10:     */   
/*   11:     */   public KeyNotStringOnReadException(String paramString)
/*   12:     */   {
/*   13:1668 */     super(paramString);
/*   14:     */   }
/*   15:     */   
/*   16:     */   public KeyNotStringOnReadException() {}
/*   17:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.KeyNotStringOnReadException
 * JD-Core Version:    0.7.0.1
 */