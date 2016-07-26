package org.boon.datarepo;

import java.util.Comparator;
import java.util.Locale;
import java.util.logging.Level;
import org.boon.core.Function;
import org.boon.core.Supplier;
import org.boon.datarepo.modification.ModificationListener;
import org.boon.datarepo.spi.RepoComposer;
import org.boon.datarepo.spi.SearchIndex;

public abstract interface RepoBuilder
{
  public abstract RepoBuilder searchIndexFactory(Function<Class, SearchIndex> paramFunction);
  
  public abstract RepoBuilder lookupIndexFactory(Function<Class, LookupIndex> paramFunction);
  
  public abstract RepoBuilder uniqueLookupIndexFactory(Function<Class, LookupIndex> paramFunction);
  
  public abstract RepoBuilder uniqueSearchIndexFactory(Function<Class, SearchIndex> paramFunction);
  
  public abstract RepoBuilder repoFactory(Supplier<RepoComposer> paramSupplier);
  
  public abstract RepoBuilder primaryKey(String paramString);
  
  public abstract RepoBuilder lookupIndex(String paramString);
  
  public abstract RepoBuilder uniqueLookupIndex(String paramString);
  
  public abstract RepoBuilder searchIndex(String paramString);
  
  public abstract RepoBuilder uniqueSearchIndex(String paramString);
  
  public abstract RepoBuilder collateIndex(String paramString, Comparator paramComparator);
  
  public abstract RepoBuilder collateIndex(String paramString);
  
  public abstract RepoBuilder collateIndex(String paramString, Locale paramLocale);
  
  public abstract RepoBuilder keyGetter(String paramString, Function<?, ?> paramFunction);
  
  public abstract RepoBuilder filterFactory(Supplier<Filter> paramSupplier);
  
  public abstract RepoBuilder usePropertyForAccess(boolean paramBoolean);
  
  public abstract RepoBuilder useFieldForAccess(boolean paramBoolean);
  
  public abstract RepoBuilder useUnsafe(boolean paramBoolean);
  
  public abstract RepoBuilder nullChecks(boolean paramBoolean);
  
  public abstract RepoBuilder addLogging(boolean paramBoolean);
  
  public abstract RepoBuilder cloneEdits(boolean paramBoolean);
  
  public abstract RepoBuilder useCache();
  
  public abstract RepoBuilder storeKeyInIndexOnly();
  
  public abstract RepoBuilder events(ModificationListener... paramVarArgs);
  
  public abstract RepoBuilder debug();
  
  public abstract <KEY, ITEM> Repo<KEY, ITEM> build(Class<KEY> paramClass, Class<ITEM> paramClass1, Class<?>... paramVarArgs);
  
  public abstract RepoBuilder level(Level paramLevel);
  
  public abstract RepoBuilder upperCaseIndex(String paramString);
  
  public abstract RepoBuilder lowerCaseIndex(String paramString);
  
  public abstract RepoBuilder camelCaseIndex(String paramString);
  
  public abstract RepoBuilder underBarCaseIndex(String paramString);
  
  @Deprecated
  public abstract RepoBuilder nestedIndex(String... paramVarArgs);
  
  public abstract RepoBuilder indexHierarchy();
  
  public abstract RepoBuilder indexBucketSize(String paramString, int paramInt);
  
  public abstract RepoBuilder hashCodeOptimizationOn();
  
  public abstract RepoBuilder removeDuplication(boolean paramBoolean);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.RepoBuilder
 * JD-Core Version:    0.7.0.1
 */