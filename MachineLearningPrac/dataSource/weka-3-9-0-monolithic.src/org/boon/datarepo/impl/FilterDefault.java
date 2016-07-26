/*   1:    */ package org.boon.datarepo.impl;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.List;
/*   5:    */ import java.util.Map;
/*   6:    */ import java.util.Set;
/*   7:    */ import org.boon.Sets;
/*   8:    */ import org.boon.core.Conversions;
/*   9:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  10:    */ import org.boon.criteria.Criterion;
/*  11:    */ import org.boon.criteria.ObjectFilter;
/*  12:    */ import org.boon.criteria.internal.Criteria;
/*  13:    */ import org.boon.criteria.internal.Group;
/*  14:    */ import org.boon.criteria.internal.Grouping;
/*  15:    */ import org.boon.criteria.internal.Operator;
/*  16:    */ import org.boon.criteria.internal.QueryFactory;
/*  17:    */ import org.boon.datarepo.Filter;
/*  18:    */ import org.boon.datarepo.LookupIndex;
/*  19:    */ import org.boon.datarepo.ResultSet;
/*  20:    */ import org.boon.datarepo.SearchableCollection;
/*  21:    */ import org.boon.datarepo.spi.FilterComposer;
/*  22:    */ import org.boon.datarepo.spi.ResultSetInternal;
/*  23:    */ import org.boon.datarepo.spi.SearchIndex;
/*  24:    */ 
/*  25:    */ public class FilterDefault
/*  26:    */   implements Filter, FilterComposer
/*  27:    */ {
/*  28: 62 */   private Set<Operator> indexedOperators = Sets.set(new Operator[] { Operator.BETWEEN, Operator.EQUAL, Operator.STARTS_WITH, Operator.GREATER_THAN, Operator.GREATER_THAN_EQUAL, Operator.LESS_THAN, Operator.LESS_THAN_EQUAL });
/*  29:    */   private Map<String, FieldAccess> fields;
/*  30:    */   private SearchableCollection searchableCollection;
/*  31:    */   private Map<String, SearchIndex> searchIndexMap;
/*  32:    */   private Map<String, LookupIndex> lookupIndexMap;
/*  33:    */   
/*  34:    */   public ResultSet filter(Criteria... expressions)
/*  35:    */   {
/*  36:    */     try
/*  37:    */     {
/*  38: 85 */       ResultSet localResultSet = mainQueryPlan(expressions);return localResultSet;
/*  39:    */     }
/*  40:    */     finally {}
/*  41:    */   }
/*  42:    */   
/*  43:    */   private ResultSet mainQueryPlan(Criteria[] expressions)
/*  44:    */   {
/*  45:103 */     ResultSetInternal results = new ResultSetImpl(this.fields);
/*  46:105 */     if ((expressions == null) || (expressions.length == 0)) {
/*  47:106 */       results.addResults(this.searchableCollection.all());
/*  48:    */     }
/*  49:113 */     Group group = (expressions.length == 1) && ((expressions[0] instanceof Group)) ? (Group)expressions[0] : ObjectFilter.and(expressions);
/*  50:    */     
/*  51:    */ 
/*  52:    */ 
/*  53:    */ 
/*  54:    */ 
/*  55:    */ 
/*  56:120 */     doFilterGroup(group, results);
/*  57:    */     
/*  58:122 */     return results;
/*  59:    */   }
/*  60:    */   
/*  61:    */   private void orPlanWithIndex(Criterion criterion, ResultSetInternal results)
/*  62:    */   {
/*  63:129 */     Operator operator = criterion.getOperator();
/*  64:130 */     if ((operator == Operator.EQUAL) && (this.lookupIndexMap.get(criterion.getName()) != null))
/*  65:    */     {
/*  66:131 */       doFilterWithIndex(criterion, this.fields, results);
/*  67:    */     }
/*  68:132 */     else if ((isIndexed(criterion.getName())) && (Sets.in(operator, this.indexedOperators)))
/*  69:    */     {
/*  70:133 */       doFilterWithIndex(criterion, this.fields, results);
/*  71:    */     }
/*  72:    */     else
/*  73:    */     {
/*  74:135 */       List list = QueryFactory.filter(this.searchableCollection.all(), criterion);
/*  75:136 */       results.addResults(list);
/*  76:    */     }
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void invalidate() {}
/*  80:    */   
/*  81:    */   private void doFilterGroup(Group group, ResultSetInternal results)
/*  82:    */   {
/*  83:154 */     if (group.getGrouping() == Grouping.OR)
/*  84:    */     {
/*  85:156 */       or(group.getExpressions(), this.fields, results);
/*  86:    */     }
/*  87:    */     else
/*  88:    */     {
/*  89:161 */       ResultSetInternal resultsForAnd = new ResultSetImpl(this.fields);
/*  90:162 */       and(group.getExpressions(), this.fields, resultsForAnd);
/*  91:163 */       results.addResults(resultsForAnd.asList());
/*  92:    */     }
/*  93:    */   }
/*  94:    */   
/*  95:    */   private void or(List<Criteria> expressions, Map<String, FieldAccess> fields, ResultSetInternal results)
/*  96:    */   {
/*  97:171 */     for (Criteria expression : expressions) {
/*  98:172 */       if ((expression instanceof Criterion)) {
/*  99:173 */         orPlanWithIndex((Criterion)expression, results);
/* 100:174 */       } else if ((expression instanceof Group)) {
/* 101:175 */         doFilterGroup((Group)expression, results);
/* 102:    */       }
/* 103:    */     }
/* 104:    */   }
/* 105:    */   
/* 106:    */   private void and(List<Criteria> expressions, Map<String, FieldAccess> fields, ResultSetInternal resultSet)
/* 107:    */   {
/* 108:183 */     Set<Criteria> expressionSet = Sets.set(expressions);
/* 109:    */     
/* 110:    */ 
/* 111:186 */     boolean foundIndex = applyIndexedFiltersForAnd(expressions, fields, expressionSet, resultSet);
/* 112:    */     
/* 113:188 */     resultSet.andResults();
/* 114:189 */     applyLinearSearch(expressionSet, resultSet, foundIndex);
/* 115:190 */     applyGroups(expressionSet, resultSet);
/* 116:    */   }
/* 117:    */   
/* 118:    */   private boolean applyIndexedFiltersForAnd(List<Criteria> expressions, Map<String, FieldAccess> fields, Set<Criteria> expressionSet, ResultSetInternal resultSet)
/* 119:    */   {
/* 120:197 */     Criterion criteria = null;
/* 121:198 */     boolean foundIndex = false;
/* 122:201 */     if ((expressions.size() == 1) && ((expressions.get(0) instanceof Criterion)))
/* 123:    */     {
/* 124:202 */       criteria = (Criterion)expressions.get(0);
/* 125:203 */       foundIndex = doFilterWithIndex(criteria, fields, resultSet);
/* 126:204 */       if (foundIndex) {
/* 127:205 */         expressionSet.remove(criteria);
/* 128:    */       }
/* 129:208 */       return foundIndex;
/* 130:    */     }
/* 131:211 */     int foundCount = 0;
/* 132:213 */     for (Criteria expression : expressions) {
/* 133:217 */       if ((expression instanceof Criterion))
/* 134:    */       {
/* 135:218 */         criteria = (Criterion)expression;
/* 136:221 */         if (doFilterWithIndex(criteria, fields, resultSet)) {
/* 137:222 */           foundCount++;
/* 138:    */         }
/* 139:    */       }
/* 140:    */     }
/* 141:227 */     return foundCount > 0;
/* 142:    */   }
/* 143:    */   
/* 144:    */   private void applyGroups(Set<Criteria> expressionSet, ResultSetInternal resultSet)
/* 145:    */   {
/* 146:278 */     if (expressionSet.size() == 0) {
/* 147:279 */       return;
/* 148:    */     }
/* 149:283 */     for (Criteria expression : expressionSet) {
/* 150:285 */       if ((expression instanceof Group)) {
/* 151:286 */         doFilterGroup((Group)expression, resultSet);
/* 152:    */       }
/* 153:    */     }
/* 154:    */   }
/* 155:    */   
/* 156:    */   private void applyLinearSearch(Set<Criteria> expressionSet, ResultSetInternal resultSet, boolean foundIndex)
/* 157:    */   {
/* 158:294 */     if (expressionSet.size() == 0) {
/* 159:295 */       return;
/* 160:    */     }
/* 161:298 */     Criteria[] expressions = (Criteria[])Conversions.array(Criteria.class, QueryFactory.filter(expressionSet, ObjectFilter.not(ObjectFilter.instanceOf(Group.class))));
/* 162:300 */     if (foundIndex) {
/* 163:301 */       resultSet.filterAndPrune(ObjectFilter.and(expressions));
/* 164:    */     } else {
/* 165:303 */       resultSet.addResults(QueryFactory.filter(this.searchableCollection.all(), ObjectFilter.and(expressions)));
/* 166:    */     }
/* 167:308 */     for (Criteria expression : expressions) {
/* 168:309 */       expressionSet.remove(expression);
/* 169:    */     }
/* 170:    */   }
/* 171:    */   
/* 172:    */   private boolean isIndexed(String name)
/* 173:    */   {
/* 174:316 */     return this.searchIndexMap.containsKey(name);
/* 175:    */   }
/* 176:    */   
/* 177:    */   private boolean doFilterWithIndex(Criterion criterion, Map<String, FieldAccess> fields, ResultSetInternal resultSet)
/* 178:    */   {
/* 179:322 */     boolean indexed = this.indexedOperators.contains(criterion.getOperator());
/* 180:324 */     if (!indexed) {
/* 181:325 */       return false;
/* 182:    */     }
/* 183:329 */     String name = criterion.getName();
/* 184:330 */     Object value = criterion.getValue();
/* 185:331 */     Operator operator = criterion.getOperator();
/* 186:332 */     SearchIndex searchIndex = (SearchIndex)this.searchIndexMap.get(name);
/* 187:333 */     LookupIndex lookupIndex = (LookupIndex)this.lookupIndexMap.get(name);
/* 188:334 */     List resultList = null;
/* 189:337 */     if ((lookupIndex != null) && (operator == Operator.EQUAL))
/* 190:    */     {
/* 191:338 */       boolean foundIndex = true;
/* 192:339 */       resultList = lookupIndex.getAll(value);
/* 193:340 */       if (resultList != null)
/* 194:    */       {
/* 195:341 */         resultSet.addResults(resultList);
/* 196:342 */         return foundIndex;
/* 197:    */       }
/* 198:344 */       resultSet.addResults(Collections.EMPTY_LIST);
/* 199:345 */       return foundIndex;
/* 200:    */     }
/* 201:349 */     if (searchIndex == null) {
/* 202:350 */       return false;
/* 203:    */     }
/* 204:353 */     boolean foundIndex = true;
/* 205:356 */     switch (1.$SwitchMap$org$boon$criteria$internal$Operator[operator.ordinal()])
/* 206:    */     {
/* 207:    */     case 1: 
/* 208:358 */       resultList = processResultsFromIndex(searchIndex, searchIndex.findEquals(value));
/* 209:359 */       break;
/* 210:    */     case 2: 
/* 211:361 */       resultList = searchIndex.findStartsWith(value);
/* 212:362 */       break;
/* 213:    */     case 3: 
/* 214:365 */       resultList = searchIndex.findGreaterThan(value);
/* 215:366 */       break;
/* 216:    */     case 4: 
/* 217:369 */       resultList = searchIndex.findGreaterThanEqual(value);
/* 218:370 */       break;
/* 219:    */     case 5: 
/* 220:373 */       resultList = searchIndex.findLessThan(value);
/* 221:374 */       break;
/* 222:    */     case 6: 
/* 223:377 */       resultList = searchIndex.findLessThanEqual(value);
/* 224:378 */       break;
/* 225:    */     case 7: 
/* 226:381 */       resultList = searchIndex.findBetween(criterion.getValue(), criterion.getValues()[1]);
/* 227:    */     }
/* 228:387 */     criterion.clean();
/* 229:389 */     if (resultList != null)
/* 230:    */     {
/* 231:390 */       resultSet.addResults(resultList);
/* 232:391 */       return foundIndex;
/* 233:    */     }
/* 234:393 */     return foundIndex;
/* 235:    */   }
/* 236:    */   
/* 237:    */   private List processResultsFromIndex(SearchIndex searchIndex, List results)
/* 238:    */   {
/* 239:399 */     if (searchIndex.isPrimaryKeyOnly()) {
/* 240:401 */       return null;
/* 241:    */     }
/* 242:403 */     return results;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public void setSearchableCollection(SearchableCollection searchableCollection)
/* 246:    */   {
/* 247:410 */     this.searchableCollection = searchableCollection;
/* 248:    */   }
/* 249:    */   
/* 250:    */   public void setFields(Map<String, FieldAccess> fields)
/* 251:    */   {
/* 252:415 */     this.fields = fields;
/* 253:    */   }
/* 254:    */   
/* 255:    */   public void setSearchIndexMap(Map<String, SearchIndex> searchIndexMap)
/* 256:    */   {
/* 257:420 */     this.searchIndexMap = searchIndexMap;
/* 258:    */   }
/* 259:    */   
/* 260:    */   public void setLookupIndexMap(Map<String, LookupIndex> lookupIndexMap)
/* 261:    */   {
/* 262:425 */     this.lookupIndexMap = lookupIndexMap;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public void init() {}
/* 266:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.FilterDefault
 * JD-Core Version:    0.7.0.1
 */