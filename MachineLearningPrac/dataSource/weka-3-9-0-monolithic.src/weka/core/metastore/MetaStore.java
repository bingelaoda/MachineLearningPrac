package weka.core.metastore;

import java.io.IOException;
import java.util.Set;

public abstract interface MetaStore
{
  public abstract Set<String> listMetaStores()
    throws IOException;
  
  public abstract Set<String> listMetaStoreEntries(String paramString)
    throws IOException;
  
  public abstract Set<String> listMetaStoreEntries(String paramString1, String paramString2)
    throws IOException;
  
  public abstract void createStore(String paramString)
    throws IOException;
  
  public abstract Object getEntry(String paramString1, String paramString2, Class<?> paramClass)
    throws IOException;
  
  public abstract void storeEntry(String paramString1, String paramString2, Object paramObject)
    throws IOException;
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.metastore.MetaStore
 * JD-Core Version:    0.7.0.1
 */