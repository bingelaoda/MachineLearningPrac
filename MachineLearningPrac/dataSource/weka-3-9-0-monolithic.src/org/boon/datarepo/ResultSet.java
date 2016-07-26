package org.boon.datarepo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.boon.criteria.Selector;
import org.boon.criteria.internal.Criteria;
import org.boon.sort.Sort;

public abstract interface ResultSet<T>
  extends Iterable<T>
{
  public abstract ResultSet expectOne();
  
  public abstract <EXPECT> ResultSet<EXPECT> expectOne(Class<EXPECT> paramClass);
  
  public abstract ResultSet expectMany();
  
  public abstract ResultSet expectNone();
  
  public abstract ResultSet expectOneOrMany();
  
  public abstract ResultSet removeDuplication();
  
  public abstract ResultSet sort(Sort paramSort);
  
  public abstract Collection<T> filter(Criteria paramCriteria);
  
  public abstract ResultSet<List<Map<String, Object>>> select(Selector... paramVarArgs);
  
  public abstract int[] selectInts(Selector paramSelector);
  
  public abstract float[] selectFloats(Selector paramSelector);
  
  public abstract short[] selectShorts(Selector paramSelector);
  
  public abstract double[] selectDoubles(Selector paramSelector);
  
  public abstract byte[] selectBytes(Selector paramSelector);
  
  public abstract char[] selectChars(Selector paramSelector);
  
  public abstract Object[] selectObjects(Selector paramSelector);
  
  public abstract <OBJ> OBJ[] selectObjects(Class<OBJ> paramClass, Selector paramSelector);
  
  public abstract <OBJ> ResultSet<OBJ> selectObjectsAsResultSet(Class<OBJ> paramClass, Selector paramSelector);
  
  public abstract Collection<T> asCollection();
  
  public abstract String asJSONString();
  
  public abstract List<Map<String, Object>> asListOfMaps();
  
  public abstract List<T> asList();
  
  public abstract <G> List<G> asList(Class<G> paramClass);
  
  public abstract Set<T> asSet();
  
  public abstract List<PlanStep> queryPlan();
  
  public abstract T firstItem();
  
  public abstract Map<String, Object> firstMap();
  
  public abstract String firstJSON();
  
  public abstract int firstInt(Selector paramSelector);
  
  public abstract float firstFloat(Selector paramSelector);
  
  public abstract short firstShort(Selector paramSelector);
  
  public abstract double firstDouble(Selector paramSelector);
  
  public abstract byte firstByte(Selector paramSelector);
  
  public abstract char firstChar(Selector paramSelector);
  
  public abstract Object firstObject(Selector paramSelector);
  
  public abstract <OBJ> OBJ firstObject(Class<OBJ> paramClass, Selector paramSelector);
  
  public abstract List<T> paginate(int paramInt1, int paramInt2);
  
  public abstract List<Map<String, Object>> paginateMaps(int paramInt1, int paramInt2);
  
  public abstract String paginateJSON(int paramInt1, int paramInt2);
  
  public abstract int size();
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.ResultSet
 * JD-Core Version:    0.7.0.1
 */