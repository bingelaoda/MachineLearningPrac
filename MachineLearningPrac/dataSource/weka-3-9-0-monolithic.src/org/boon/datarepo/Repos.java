/*  1:   */ package org.boon.datarepo;
/*  2:   */ 
/*  3:   */ import org.boon.core.Function;
/*  4:   */ import org.boon.core.Supplier;
/*  5:   */ import org.boon.datarepo.spi.RepoComposer;
/*  6:   */ import org.boon.datarepo.spi.SPIFactory;
/*  7:   */ import org.boon.datarepo.spi.SearchIndex;
/*  8:   */ 
/*  9:   */ public class Repos
/* 10:   */ {
/* 11:   */   public static void setRepoBuilder(Supplier<RepoBuilder> factory)
/* 12:   */   {
/* 13:39 */     SPIFactory.setRepoBuilderFactory(factory);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public static void setDefaultSearchIndexFactory(Function<Class, SearchIndex> factory)
/* 17:   */   {
/* 18:43 */     SPIFactory.setSearchIndexFactory(factory);
/* 19:   */   }
/* 20:   */   
/* 21:   */   public static void setLookupIndexFactory(Function<Class, LookupIndex> factory)
/* 22:   */   {
/* 23:47 */     SPIFactory.setLookupIndexFactory(factory);
/* 24:   */   }
/* 25:   */   
/* 26:   */   public static void setUniqueLookupIndexFactory(Function<Class, LookupIndex> factory)
/* 27:   */   {
/* 28:51 */     SPIFactory.setUniqueLookupIndexFactory(factory);
/* 29:   */   }
/* 30:   */   
/* 31:   */   public static void setUniqueSearchIndexFactory(Function<Class, SearchIndex> factory)
/* 32:   */   {
/* 33:55 */     SPIFactory.setUniqueSearchIndexFactory(factory);
/* 34:   */   }
/* 35:   */   
/* 36:   */   public static void setRepoFactory(Supplier<RepoComposer> factory)
/* 37:   */   {
/* 38:59 */     SPIFactory.setRepoFactory(factory);
/* 39:   */   }
/* 40:   */   
/* 41:   */   public static void setFilterFactory(Supplier<Filter> factory)
/* 42:   */   {
/* 43:63 */     SPIFactory.setFilterFactory(factory);
/* 44:   */   }
/* 45:   */   
/* 46:   */   public static RepoBuilder builder()
/* 47:   */   {
/* 48:67 */     SPIFactory.init();
/* 49:68 */     return (RepoBuilder)SPIFactory.getRepoBuilderFactory().get();
/* 50:   */   }
/* 51:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.Repos
 * JD-Core Version:    0.7.0.1
 */