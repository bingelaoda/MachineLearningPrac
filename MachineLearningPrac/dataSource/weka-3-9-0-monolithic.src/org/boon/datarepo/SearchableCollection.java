package org.boon.datarepo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.boon.criteria.Selector;
import org.boon.criteria.internal.Criteria;
import org.boon.criteria.internal.Visitor;
import org.boon.datarepo.spi.SearchIndex;
import org.boon.sort.Sort;

public abstract interface SearchableCollection<KEY, ITEM>
  extends Collection<ITEM>
{
  public abstract ITEM get(KEY paramKEY);
  
  public abstract KEY getKey(ITEM paramITEM);
  
  public abstract void invalidateIndex(String paramString, ITEM paramITEM);
  
  public abstract void validateIndex(String paramString, ITEM paramITEM);
  
  public abstract void validateIndexes(ITEM paramITEM);
  
  public abstract int count(KEY paramKEY, String paramString, int paramInt);
  
  public abstract int count(KEY paramKEY, String paramString, short paramShort);
  
  public abstract int count(KEY paramKEY, String paramString, byte paramByte);
  
  public abstract int count(KEY paramKEY, String paramString, long paramLong);
  
  public abstract int count(KEY paramKEY, String paramString, char paramChar);
  
  public abstract int count(KEY paramKEY, String paramString, float paramFloat);
  
  public abstract int count(KEY paramKEY, String paramString, double paramDouble);
  
  public abstract int count(KEY paramKEY, String paramString, Object paramObject);
  
  public abstract <T> T max(KEY paramKEY, String paramString, Class<T> paramClass);
  
  public abstract String maxString(KEY paramKEY, String paramString);
  
  public abstract Number maxNumber(KEY paramKEY, String paramString);
  
  public abstract int maxInt(KEY paramKEY, String paramString);
  
  public abstract long maxLong(KEY paramKEY, String paramString);
  
  public abstract double maxDouble(KEY paramKEY, String paramString);
  
  public abstract <T> T min(KEY paramKEY, String paramString, Class<T> paramClass);
  
  public abstract String minString(KEY paramKEY, String paramString);
  
  public abstract Number minNumber(KEY paramKEY, String paramString);
  
  public abstract int minInt(KEY paramKEY, String paramString);
  
  public abstract long minLong(KEY paramKEY, String paramString);
  
  public abstract double minDouble(KEY paramKEY, String paramString);
  
  public abstract ResultSet<ITEM> results(Criteria... paramVarArgs);
  
  public abstract List<ITEM> query(Criteria... paramVarArgs);
  
  public abstract List<ITEM> query(List<Criteria> paramList);
  
  public abstract List<ITEM> sortedQuery(String paramString, Criteria... paramVarArgs);
  
  public abstract List<ITEM> sortedQuery(Sort paramSort, Criteria... paramVarArgs);
  
  public abstract List<Map<String, Object>> queryAsMaps(Criteria... paramVarArgs);
  
  public abstract List<Map<String, Object>> query(List<Selector> paramList, Criteria... paramVarArgs);
  
  public abstract List<Map<String, Object>> sortedQuery(String paramString, List<Selector> paramList, Criteria... paramVarArgs);
  
  public abstract List<Map<String, Object>> sortedQuery(Sort paramSort, List<Selector> paramList, Criteria... paramVarArgs);
  
  public abstract void query(Visitor<KEY, ITEM> paramVisitor, Criteria... paramVarArgs);
  
  public abstract void sortedQuery(Visitor<KEY, ITEM> paramVisitor, String paramString, Criteria... paramVarArgs);
  
  public abstract void sortedQuery(Visitor<KEY, ITEM> paramVisitor, Sort paramSort, Criteria... paramVarArgs);
  
  public abstract boolean delete(ITEM paramITEM);
  
  public abstract void addSearchIndex(String paramString, SearchIndex<?, ?> paramSearchIndex);
  
  public abstract void addLookupIndex(String paramString, LookupIndex<?, ?> paramLookupIndex);
  
  public abstract List<ITEM> all();
  
  public abstract void removeByKey(KEY paramKEY);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.SearchableCollection
 * JD-Core Version:    0.7.0.1
 */