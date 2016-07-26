/*  1:   */ package org.boon.datarepo.impl.maps;
/*  2:   */ 
/*  3:   */ import java.util.Comparator;
/*  4:   */ import java.util.Map;
/*  5:   */ import java.util.NavigableMap;
/*  6:   */ import org.boon.datarepo.spi.MapCreator;
/*  7:   */ 
/*  8:   */ public class MapCreatorImpl
/*  9:   */   implements MapCreator
/* 10:   */ {
/* 11:   */   public NavigableMap createNavigableMap(Class<?> keyType)
/* 12:   */   {
/* 13:40 */     if (keyType == String.class) {
/* 14:41 */       return new JavaUtilNavigableMap();
/* 15:   */     }
/* 16:43 */     return new JavaUtilNavigableMap();
/* 17:   */   }
/* 18:   */   
/* 19:   */   public NavigableMap createNavigableMap(Class<?> keyType, Comparator collator)
/* 20:   */   {
/* 21:50 */     if (collator != null) {
/* 22:51 */       return new JavaUtilNavigableMap(collator);
/* 23:   */     }
/* 24:53 */     return new JavaUtilNavigableMap();
/* 25:   */   }
/* 26:   */   
/* 27:   */   public Map createMap(Class<?> keyType)
/* 28:   */   {
/* 29:59 */     return new JavaUtilMap();
/* 30:   */   }
/* 31:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.maps.MapCreatorImpl
 * JD-Core Version:    0.7.0.1
 */