/*    1:     */ package org.j_paine.formatter;
/*    2:     */ 
/*    3:     */ class UnmatchedStringOnReadException
/*    4:     */   extends InputFormatException
/*    5:     */ {
/*    6:     */   public UnmatchedStringOnReadException(String paramString1, int paramInt, String paramString2, String paramString3)
/*    7:     */   {
/*    8:1604 */     this("Unmatched string while reading formatted data:\n  String = \"" + paramString1 + "\"\n" + "  Index  = " + paramInt + "\n" + "  Format = " + paramString2 + "\n" + paramString3 + "\n");
/*    9:     */   }
/*   10:     */   
/*   11:     */   public UnmatchedStringOnReadException(String paramString)
/*   12:     */   {
/*   13:1614 */     super(paramString);
/*   14:     */   }
/*   15:     */   
/*   16:     */   public UnmatchedStringOnReadException() {}
/*   17:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.UnmatchedStringOnReadException
 * JD-Core Version:    0.7.0.1
 */