package org.boon.config;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.boon.IO;
import org.boon.di.Context;
import org.boon.di.DependencyInjection;
import org.boon.json.JsonParserAndMapper;
import org.boon.json.JsonParserFactory;

public enum ContextConfig
{
  JSON;
  
  private ContextConfig() {}
  
  public abstract Context createContext(String... paramVarArgs);
  
  public abstract Context createContext(List<String> paramList);
  
  public abstract Context createContext(String paramString, boolean paramBoolean, String... paramVarArgs);
  
  public abstract Context createContext(MetaConfigEvents paramMetaConfigEvents, String... paramVarArgs);
  
  public abstract Context createContext(String paramString, boolean paramBoolean, MetaConfigEvents paramMetaConfigEvents, String... paramVarArgs);
  
  public abstract Context createContext(String paramString, boolean paramBoolean, MetaConfigEvents paramMetaConfigEvents, List<String> paramList);
}


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.config.ContextConfig
 * JD-Core Version:    0.7.0.1
 */