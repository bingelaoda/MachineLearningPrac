/*    1:     */ package org.boon.datarepo.impl;
/*    2:     */ 
/*    3:     */ import java.math.BigDecimal;
/*    4:     */ import java.text.Collator;
/*    5:     */ import java.util.Comparator;
/*    6:     */ import java.util.HashMap;
/*    7:     */ import java.util.HashSet;
/*    8:     */ import java.util.Locale;
/*    9:     */ import java.util.Map;
/*   10:     */ import java.util.Set;
/*   11:     */ import java.util.logging.Level;
/*   12:     */ import org.boon.Exceptions;
/*   13:     */ import org.boon.Str;
/*   14:     */ import org.boon.core.Function;
/*   15:     */ import org.boon.core.Supplier;
/*   16:     */ import org.boon.core.Typ;
/*   17:     */ import org.boon.core.reflection.BeanUtils;
/*   18:     */ import org.boon.core.reflection.fields.FieldAccess;
/*   19:     */ import org.boon.datarepo.Filter;
/*   20:     */ import org.boon.datarepo.LookupIndex;
/*   21:     */ import org.boon.datarepo.ObjectEditor;
/*   22:     */ import org.boon.datarepo.Repo;
/*   23:     */ import org.boon.datarepo.RepoBuilder;
/*   24:     */ import org.boon.datarepo.SearchableCollection;
/*   25:     */ import org.boon.datarepo.impl.decorators.FilterWithSimpleCache;
/*   26:     */ import org.boon.datarepo.impl.decorators.ObjectEditorCloneDecorator;
/*   27:     */ import org.boon.datarepo.impl.decorators.ObjectEditorEventDecorator;
/*   28:     */ import org.boon.datarepo.impl.decorators.ObjectEditorLogNullCheckDecorator;
/*   29:     */ import org.boon.datarepo.impl.indexes.NestedKeySearchIndex;
/*   30:     */ import org.boon.datarepo.impl.indexes.TypeHierarchyIndex;
/*   31:     */ import org.boon.datarepo.modification.ModificationListener;
/*   32:     */ import org.boon.datarepo.spi.ObjectEditorComposer;
/*   33:     */ import org.boon.datarepo.spi.RepoComposer;
/*   34:     */ import org.boon.datarepo.spi.SPIFactory;
/*   35:     */ import org.boon.datarepo.spi.SearchIndex;
/*   36:     */ import org.boon.datarepo.spi.SearchableCollectionComposer;
/*   37:     */ import org.boon.functions.PropertyNameUtils;
/*   38:     */ 
/*   39:     */ public class RepoBuilderDefault
/*   40:     */   implements RepoBuilder
/*   41:     */ {
/*   42:     */   Function<Class, SearchIndex> searchIndexFactory;
/*   43:     */   Function<Class, LookupIndex> lookupIndexFactory;
/*   44:     */   Function<Class, LookupIndex> uniqueLookupIndexFactory;
/*   45:     */   Function<Class, SearchIndex> uniqueSearchIndexFactory;
/*   46:     */   Supplier<ObjectEditorComposer> objectEditorFactory;
/*   47:     */   Supplier<SearchableCollectionComposer> searchableCollectionFactory;
/*   48:     */   Supplier<RepoComposer> repoComposerFactory;
/*   49:     */   Supplier<Filter> filterFactory;
/*   50:     */   String primaryKey;
/*   51: 124 */   Set<String> searchIndexes = new HashSet();
/*   52: 128 */   Set<String> lookupIndexes = new HashSet();
/*   53: 132 */   Set<String> uniqueSearchIndexes = new HashSet();
/*   54: 136 */   Set<String> uniqueLookupIndexes = new HashSet();
/*   55: 148 */   Map<String, Function> keyGetterMap = new HashMap();
/*   56: 154 */   boolean useField = true;
/*   57: 159 */   boolean useUnSafe = false;
/*   58:     */   boolean nullChecksAndLogging;
/*   59:     */   boolean cloneEdits;
/*   60:     */   boolean storeKeyInIndexOnly;
/*   61:     */   boolean debug;
/*   62: 192 */   Level level = Level.FINER;
/*   63:     */   private Map<String, FieldAccess> fields;
/*   64:     */   private RepoComposer repo;
/*   65:     */   private ObjectEditor editor;
/*   66:     */   private SearchableCollectionComposer query;
/*   67: 232 */   private boolean cache = false;
/*   68: 237 */   private Map<String, Comparator> collators = new HashMap();
/*   69: 246 */   private Map<String, Function> keyTransformers = new HashMap();
/*   70:     */   @Deprecated
/*   71: 254 */   private Map<String, String[]> nestedIndexes = new HashMap();
/*   72:     */   private boolean indexHierarchy;
/*   73: 267 */   private Map<String, Integer> indexBucketSize = new HashMap();
/*   74:     */   private boolean hashCodeOptimizationOn;
/*   75:     */   private boolean removeDuplication;
/*   76: 285 */   boolean events = false;
/*   77:     */   ModificationListener[] listeners;
/*   78:     */   private Class<?> itemClass;
/*   79:     */   
/*   80:     */   public RepoBuilder usePropertyForAccess(boolean useProperty)
/*   81:     */   {
/*   82: 302 */     this.useField = (!useProperty);
/*   83: 303 */     return this;
/*   84:     */   }
/*   85:     */   
/*   86:     */   public RepoBuilder useFieldForAccess(boolean useField)
/*   87:     */   {
/*   88: 314 */     this.useField = useField;
/*   89: 315 */     return this;
/*   90:     */   }
/*   91:     */   
/*   92:     */   public RepoBuilder useUnsafe(boolean useUnSafe)
/*   93:     */   {
/*   94: 327 */     this.useUnSafe = useUnSafe;
/*   95: 328 */     return this;
/*   96:     */   }
/*   97:     */   
/*   98:     */   public RepoBuilder nullChecks(boolean nullChecks)
/*   99:     */   {
/*  100: 341 */     this.nullChecksAndLogging = nullChecks;
/*  101: 342 */     return this;
/*  102:     */   }
/*  103:     */   
/*  104:     */   public RepoBuilder addLogging(boolean logging)
/*  105:     */   {
/*  106: 354 */     this.nullChecksAndLogging = logging;
/*  107: 355 */     return this;
/*  108:     */   }
/*  109:     */   
/*  110:     */   public RepoBuilder cloneEdits(boolean cloneEdits)
/*  111:     */   {
/*  112: 370 */     this.cloneEdits = cloneEdits;
/*  113: 371 */     return this;
/*  114:     */   }
/*  115:     */   
/*  116:     */   public RepoBuilder useCache()
/*  117:     */   {
/*  118: 381 */     this.cache = true;
/*  119: 382 */     return this;
/*  120:     */   }
/*  121:     */   
/*  122:     */   public RepoBuilder storeKeyInIndexOnly()
/*  123:     */   {
/*  124: 393 */     this.storeKeyInIndexOnly = true;
/*  125:     */     
/*  126: 395 */     return this;
/*  127:     */   }
/*  128:     */   
/*  129:     */   public RepoBuilder events(ModificationListener... listeners)
/*  130:     */   {
/*  131: 407 */     this.events = true;
/*  132: 408 */     this.listeners = listeners;
/*  133: 409 */     return this;
/*  134:     */   }
/*  135:     */   
/*  136:     */   public RepoBuilder debug()
/*  137:     */   {
/*  138: 418 */     this.debug = true;
/*  139: 419 */     return this;
/*  140:     */   }
/*  141:     */   
/*  142:     */   public RepoBuilder searchIndexFactory(Function<Class, SearchIndex> factory)
/*  143:     */   {
/*  144: 428 */     this.searchIndexFactory = factory;
/*  145: 429 */     return this;
/*  146:     */   }
/*  147:     */   
/*  148:     */   public RepoBuilder uniqueLookupIndexFactory(Function<Class, LookupIndex> factory)
/*  149:     */   {
/*  150: 438 */     this.uniqueLookupIndexFactory = factory;
/*  151: 439 */     return this;
/*  152:     */   }
/*  153:     */   
/*  154:     */   public RepoBuilder uniqueSearchIndexFactory(Function<Class, SearchIndex> factory)
/*  155:     */   {
/*  156: 448 */     this.uniqueSearchIndexFactory = factory;
/*  157: 449 */     return this;
/*  158:     */   }
/*  159:     */   
/*  160:     */   public RepoBuilder lookupIndexFactory(Function<Class, LookupIndex> factory)
/*  161:     */   {
/*  162: 458 */     this.lookupIndexFactory = factory;
/*  163: 459 */     return this;
/*  164:     */   }
/*  165:     */   
/*  166:     */   public RepoBuilder repoFactory(Supplier<RepoComposer> factory)
/*  167:     */   {
/*  168: 468 */     this.repoComposerFactory = factory;
/*  169: 469 */     return this;
/*  170:     */   }
/*  171:     */   
/*  172:     */   public RepoBuilder filterFactory(Supplier<Filter> factory)
/*  173:     */   {
/*  174: 478 */     this.filterFactory = factory;
/*  175: 479 */     return this;
/*  176:     */   }
/*  177:     */   
/*  178:     */   public RepoBuilder primaryKey(String propertyName)
/*  179:     */   {
/*  180: 488 */     this.primaryKey = propertyName;
/*  181: 489 */     return this;
/*  182:     */   }
/*  183:     */   
/*  184:     */   public RepoBuilder lookupIndex(String propertyName)
/*  185:     */   {
/*  186: 498 */     this.lookupIndexes.add(propertyName);
/*  187: 499 */     return this;
/*  188:     */   }
/*  189:     */   
/*  190:     */   public RepoBuilder uniqueLookupIndex(String propertyName)
/*  191:     */   {
/*  192: 508 */     return lookupIndex(propertyName, true);
/*  193:     */   }
/*  194:     */   
/*  195:     */   public RepoBuilder lookupIndex(String propertyName, boolean unique)
/*  196:     */   {
/*  197: 517 */     if (unique) {
/*  198: 518 */       this.uniqueLookupIndexes.add(propertyName);
/*  199:     */     } else {
/*  200: 520 */       this.lookupIndexes.add(propertyName);
/*  201:     */     }
/*  202: 522 */     return this;
/*  203:     */   }
/*  204:     */   
/*  205:     */   public RepoBuilder searchIndex(String propertyName)
/*  206:     */   {
/*  207: 531 */     this.searchIndexes.add(propertyName);
/*  208: 532 */     return this;
/*  209:     */   }
/*  210:     */   
/*  211:     */   public RepoBuilder uniqueSearchIndex(String propertyName)
/*  212:     */   {
/*  213: 541 */     return searchIndex(propertyName, true);
/*  214:     */   }
/*  215:     */   
/*  216:     */   public RepoBuilder collateIndex(String propertyName)
/*  217:     */   {
/*  218: 551 */     this.collators.put(propertyName, Collator.getInstance());
/*  219: 552 */     return this;
/*  220:     */   }
/*  221:     */   
/*  222:     */   public RepoBuilder collateIndex(String propertyName, Locale locale)
/*  223:     */   {
/*  224: 562 */     this.collators.put(propertyName, Collator.getInstance(locale));
/*  225: 563 */     return this;
/*  226:     */   }
/*  227:     */   
/*  228:     */   public RepoBuilder collateIndex(String propertyName, Comparator collator)
/*  229:     */   {
/*  230: 573 */     this.collators.put(propertyName, collator);
/*  231: 574 */     return this;
/*  232:     */   }
/*  233:     */   
/*  234:     */   public RepoBuilder searchIndex(String propertyName, boolean unique)
/*  235:     */   {
/*  236: 583 */     if (unique) {
/*  237: 584 */       this.uniqueSearchIndexes.add(propertyName);
/*  238:     */     } else {
/*  239: 586 */       this.searchIndexes.add(propertyName);
/*  240:     */     }
/*  241: 588 */     return this;
/*  242:     */   }
/*  243:     */   
/*  244:     */   public RepoBuilder keyGetter(String propertyName, Function<?, ?> keyGetter)
/*  245:     */   {
/*  246: 598 */     this.keyGetterMap.put(propertyName, keyGetter);
/*  247: 599 */     return this;
/*  248:     */   }
/*  249:     */   
/*  250:     */   private void initializeTheFactories()
/*  251:     */   {
/*  252: 607 */     if (this.repoComposerFactory == null) {
/*  253: 608 */       this.repoComposerFactory = SPIFactory.getRepoFactory();
/*  254:     */     }
/*  255: 610 */     if (this.lookupIndexFactory == null) {
/*  256: 611 */       this.lookupIndexFactory = SPIFactory.getLookupIndexFactory();
/*  257:     */     }
/*  258: 613 */     if (this.searchIndexFactory == null) {
/*  259: 614 */       this.searchIndexFactory = SPIFactory.getSearchIndexFactory();
/*  260:     */     }
/*  261: 616 */     if (this.uniqueLookupIndexFactory == null) {
/*  262: 617 */       this.uniqueLookupIndexFactory = SPIFactory.getUniqueLookupIndexFactory();
/*  263:     */     }
/*  264: 619 */     if (this.uniqueSearchIndexFactory == null) {
/*  265: 620 */       this.uniqueSearchIndexFactory = SPIFactory.getUniqueSearchIndexFactory();
/*  266:     */     }
/*  267: 622 */     if (this.searchableCollectionFactory == null) {
/*  268: 623 */       this.searchableCollectionFactory = SPIFactory.getSearchableCollectionFactory();
/*  269:     */     }
/*  270: 625 */     if (this.filterFactory == null) {
/*  271: 626 */       this.filterFactory = SPIFactory.getFilterFactory();
/*  272:     */     }
/*  273: 629 */     if (this.objectEditorFactory == null) {
/*  274: 630 */       this.objectEditorFactory = SPIFactory.getObjectEditorFactory();
/*  275:     */     }
/*  276:     */   }
/*  277:     */   
/*  278:     */   public <KEY, ITEM> Repo<KEY, ITEM> build(Class<KEY> key, Class<ITEM> clazz, Class<?>... classes)
/*  279:     */   {
/*  280: 645 */     return build(null, key, clazz, classes);
/*  281:     */   }
/*  282:     */   
/*  283:     */   public <KEY, ITEM> Repo<KEY, ITEM> build(Class<?> primitiveKey, Class<KEY> key, Class<ITEM> clazz, Class<?>... classes)
/*  284:     */   {
/*  285: 660 */     initializeTheFactories();
/*  286:     */     
/*  287:     */ 
/*  288:     */ 
/*  289: 664 */     loadFields(clazz, classes);
/*  290:     */     
/*  291: 666 */     this.itemClass = clazz;
/*  292:     */     
/*  293:     */ 
/*  294:     */ 
/*  295:     */ 
/*  296: 671 */     this.repo = ((RepoComposer)this.repoComposerFactory.get());
/*  297: 672 */     this.editor = constructObjectEditor(this.fields);
/*  298: 673 */     SearchableCollectionComposer query = constructSearchableCollection(primitiveKey, clazz, this.repo, this.fields);
/*  299: 674 */     query.setRemoveDuplication(this.removeDuplication);
/*  300:     */     
/*  301:     */ 
/*  302: 677 */     this.repo.setSearchableCollection((SearchableCollection)query);
/*  303: 678 */     ((ObjectEditorComposer)this.editor).setSearchableCollection((SearchableCollection)query);
/*  304:     */     
/*  305: 680 */     this.editor = decorateEditor(this.editor);
/*  306: 681 */     this.repo.setObjectEditor(this.editor);
/*  307:     */     
/*  308: 683 */     return (Repo)this.repo;
/*  309:     */   }
/*  310:     */   
/*  311:     */   private <ITEM> void loadFields(Class<ITEM> clazz, Class<?>[] classes)
/*  312:     */   {
/*  313: 695 */     if (Typ.isMap(clazz)) {
/*  314: 696 */       return;
/*  315:     */     }
/*  316: 701 */     this.fields = BeanUtils.getFieldsFromObject(clazz);
/*  317:     */     Map<String, FieldAccess> fieldsComponentType;
/*  318: 703 */     for (Class<?> cls : classes)
/*  319:     */     {
/*  320: 704 */       fieldsComponentType = BeanUtils.getFieldsFromObject(cls);
/*  321: 707 */       for (String sKey : fieldsComponentType.keySet()) {
/*  322: 708 */         if (!this.fields.containsKey(sKey)) {
/*  323: 709 */           this.fields.put(sKey, fieldsComponentType.get(sKey));
/*  324:     */         }
/*  325:     */       }
/*  326:     */     }
/*  327:     */   }
/*  328:     */   
/*  329:     */   private SearchableCollectionComposer constructSearchableCollection(Class<?> primitiveKey, Class<?> itemClazz, RepoComposer repo, Map<String, FieldAccess> fields)
/*  330:     */   {
/*  331: 725 */     this.query = ((SearchableCollectionComposer)this.searchableCollectionFactory.get());
/*  332:     */     
/*  333:     */ 
/*  334: 728 */     Filter filter = (Filter)this.filterFactory.get();
/*  335:     */     
/*  336:     */ 
/*  337: 731 */     configPrimaryKey(primitiveKey == null ? itemClazz : primitiveKey, fields);
/*  338:     */     
/*  339:     */ 
/*  340: 734 */     configIndexes(repo, fields);
/*  341:     */     
/*  342:     */ 
/*  343: 737 */     this.query.setFilter(filter);
/*  344:     */     
/*  345:     */ 
/*  346: 740 */     this.query.setFields(fields);
/*  347:     */     
/*  348: 742 */     this.query.init();
/*  349: 744 */     if (this.cache) {
/*  350: 745 */       filter = new FilterWithSimpleCache(filter);
/*  351:     */     }
/*  352: 748 */     this.query.setFilter(filter);
/*  353:     */     
/*  354: 750 */     return this.query;
/*  355:     */   }
/*  356:     */   
/*  357:     */   private ObjectEditor constructObjectEditor(Map<String, FieldAccess> fields)
/*  358:     */   {
/*  359: 754 */     ObjectEditorComposer editorComposer = (ObjectEditorComposer)this.objectEditorFactory.get();
/*  360: 755 */     if (this.hashCodeOptimizationOn) {
/*  361: 756 */       editorComposer.hashCodeOptimizationOn();
/*  362:     */     }
/*  363: 759 */     ObjectEditor editor = (ObjectEditor)editorComposer;
/*  364: 760 */     editorComposer.init();
/*  365: 762 */     if (this.cloneEdits) {
/*  366: 763 */       editorComposer.setLookupAndExcept(true);
/*  367:     */     }
/*  368: 766 */     editorComposer.setFields(fields);
/*  369: 767 */     return editor;
/*  370:     */   }
/*  371:     */   
/*  372:     */   private ObjectEditor decorateEditor(ObjectEditor editor)
/*  373:     */   {
/*  374: 772 */     if ((this.debug) || (this.nullChecksAndLogging))
/*  375:     */     {
/*  376: 773 */       ObjectEditorLogNullCheckDecorator logNullCheckDecorator = new ObjectEditorLogNullCheckDecorator(editor);
/*  377: 774 */       logNullCheckDecorator.setLevel(this.level);
/*  378: 775 */       logNullCheckDecorator.setDebug(this.debug);
/*  379:     */       
/*  380: 777 */       editor = logNullCheckDecorator;
/*  381:     */     }
/*  382: 780 */     if (this.cloneEdits) {
/*  383: 781 */       editor = new ObjectEditorCloneDecorator(editor);
/*  384:     */     }
/*  385: 784 */     if (this.events)
/*  386:     */     {
/*  387: 785 */       ObjectEditorEventDecorator eventManager = new ObjectEditorEventDecorator(editor);
/*  388: 786 */       for (ModificationListener l : this.listeners) {
/*  389: 787 */         eventManager.add(l);
/*  390:     */       }
/*  391: 789 */       editor = eventManager;
/*  392:     */     }
/*  393: 791 */     return editor;
/*  394:     */   }
/*  395:     */   
/*  396:     */   public RepoBuilder level(Level level)
/*  397:     */   {
/*  398: 796 */     this.level = level;
/*  399: 797 */     return this;
/*  400:     */   }
/*  401:     */   
/*  402:     */   public RepoBuilder upperCaseIndex(String property)
/*  403:     */   {
/*  404: 802 */     this.keyTransformers.put(property, PropertyNameUtils.upperCase);
/*  405: 803 */     return this;
/*  406:     */   }
/*  407:     */   
/*  408:     */   public RepoBuilder lowerCaseIndex(String property)
/*  409:     */   {
/*  410: 808 */     this.keyTransformers.put(property, PropertyNameUtils.lowerCase);
/*  411: 809 */     return this;
/*  412:     */   }
/*  413:     */   
/*  414:     */   public RepoBuilder camelCaseIndex(String property)
/*  415:     */   {
/*  416: 815 */     this.keyTransformers.put(property, PropertyNameUtils.camelCase);
/*  417: 816 */     return this;
/*  418:     */   }
/*  419:     */   
/*  420:     */   public RepoBuilder underBarCaseIndex(String property)
/*  421:     */   {
/*  422: 822 */     this.keyTransformers.put(property, PropertyNameUtils.underBarCase);
/*  423: 823 */     return this;
/*  424:     */   }
/*  425:     */   
/*  426:     */   @Deprecated
/*  427:     */   public RepoBuilder nestedIndex(String... propertyPath)
/*  428:     */   {
/*  429: 830 */     this.nestedIndexes.put(Str.join('.', propertyPath), propertyPath);
/*  430:     */     
/*  431: 832 */     return this;
/*  432:     */   }
/*  433:     */   
/*  434:     */   public RepoBuilder indexHierarchy()
/*  435:     */   {
/*  436: 838 */     this.indexHierarchy = true;
/*  437: 839 */     return this;
/*  438:     */   }
/*  439:     */   
/*  440:     */   public RepoBuilder indexBucketSize(String propertyName, int size)
/*  441:     */   {
/*  442: 844 */     this.indexBucketSize.put(propertyName, Integer.valueOf(size));
/*  443: 845 */     return this;
/*  444:     */   }
/*  445:     */   
/*  446:     */   public RepoBuilder hashCodeOptimizationOn()
/*  447:     */   {
/*  448: 850 */     this.hashCodeOptimizationOn = true;
/*  449: 851 */     return this;
/*  450:     */   }
/*  451:     */   
/*  452:     */   public RepoBuilder removeDuplication(boolean removeDuplication)
/*  453:     */   {
/*  454: 856 */     this.removeDuplication = removeDuplication;
/*  455: 857 */     return this;
/*  456:     */   }
/*  457:     */   
/*  458:     */   private Function createKeyGetter(final FieldAccess field)
/*  459:     */   {
/*  460: 861 */     Exceptions.requireNonNull(field, "field cannot be null");
/*  461:     */     
/*  462: 863 */     new Function()
/*  463:     */     {
/*  464:     */       public Object apply(Object o)
/*  465:     */       {
/*  466: 866 */         return field.getValue(o);
/*  467:     */       }
/*  468:     */     };
/*  469:     */   }
/*  470:     */   
/*  471:     */   private void configIndexes(RepoComposer repo, Map<String, FieldAccess> fields)
/*  472:     */   {
/*  473: 878 */     if (this.indexHierarchy)
/*  474:     */     {
/*  475: 879 */       TypeHierarchyIndex index = new TypeHierarchyIndex();
/*  476: 880 */       index.setComparator((Comparator)this.collators.get("_type"));
/*  477: 881 */       index.setInputKeyTransformer((Function)this.keyTransformers.get("_type"));
/*  478: 882 */       index.init();
/*  479: 883 */       ((SearchableCollection)this.query).addSearchIndex("_type", index);
/*  480:     */     }
/*  481: 886 */     for (String prop : this.nestedIndexes.keySet())
/*  482:     */     {
/*  483: 887 */       NestedKeySearchIndex index = new NestedKeySearchIndex((String[])this.nestedIndexes.get(prop));
/*  484: 888 */       configIndex(prop, index);
/*  485:     */     }
/*  486: 890 */     for (String prop : this.searchIndexes)
/*  487:     */     {
/*  488: 891 */       SearchIndex searchIndex = null;
/*  489: 893 */       if ((!Typ.isMap(this.itemClass)) && (!isPropPath(prop)))
/*  490:     */       {
/*  491: 894 */         FieldAccess fieldAccess = (FieldAccess)fields.get(prop);
/*  492:     */         
/*  493: 896 */         Exceptions.requireNonNull(fieldAccess, "Field access for property was null. " + prop);
/*  494:     */         
/*  495: 898 */         Class<?> type = fieldAccess.type();
/*  496:     */         
/*  497: 900 */         searchIndex = (SearchIndex)this.searchIndexFactory.apply(type);
/*  498:     */       }
/*  499:     */       else
/*  500:     */       {
/*  501: 902 */         searchIndex = (SearchIndex)this.searchIndexFactory.apply(null);
/*  502:     */       }
/*  503: 905 */       configSearchIndex(fields, prop, searchIndex);
/*  504:     */     }
/*  505: 908 */     for (String prop : this.uniqueSearchIndexes) {
/*  506: 910 */       if ((!Typ.isMap(this.itemClass)) && (!isPropPath(prop)))
/*  507:     */       {
/*  508: 912 */         FieldAccess fieldAccess = (FieldAccess)fields.get(prop);
/*  509: 913 */         Exceptions.requireNonNull(fieldAccess, "Field access for property was null. " + prop);
/*  510:     */         
/*  511: 915 */         SearchIndex searchIndex = (SearchIndex)this.uniqueSearchIndexFactory.apply(fieldAccess.type());
/*  512: 916 */         configSearchIndex(fields, prop, searchIndex);
/*  513:     */       }
/*  514:     */       else
/*  515:     */       {
/*  516: 919 */         SearchIndex searchIndex = (SearchIndex)this.uniqueSearchIndexFactory.apply(Object.class);
/*  517: 920 */         configSearchIndex(fields, prop, searchIndex);
/*  518:     */       }
/*  519:     */     }
/*  520: 925 */     for (String prop : this.lookupIndexes) {
/*  521: 927 */       if ((!Typ.isMap(this.itemClass)) && (!isPropPath(prop)))
/*  522:     */       {
/*  523: 930 */         FieldAccess fieldAccess = (FieldAccess)fields.get(prop);
/*  524: 931 */         Exceptions.requireNonNull(fieldAccess, "Field access for property was null. " + prop);
/*  525:     */         
/*  526: 933 */         LookupIndex index = (LookupIndex)this.lookupIndexFactory.apply(fieldAccess.type());
/*  527: 934 */         configLookupIndex(fields, prop, index);
/*  528:     */       }
/*  529:     */       else
/*  530:     */       {
/*  531: 938 */         LookupIndex index = (LookupIndex)this.lookupIndexFactory.apply(Object.class);
/*  532: 939 */         configLookupIndex(fields, prop, index);
/*  533:     */       }
/*  534:     */     }
/*  535: 943 */     for (String prop : this.uniqueLookupIndexes) {
/*  536: 946 */       if ((!Typ.isMap(this.itemClass)) && (!isPropPath(prop)))
/*  537:     */       {
/*  538: 949 */         FieldAccess fieldAccess = (FieldAccess)fields.get(prop);
/*  539: 950 */         Exceptions.requireNonNull(fieldAccess, "Field access for property was null. " + prop);
/*  540:     */         
/*  541:     */ 
/*  542: 953 */         LookupIndex index = (LookupIndex)this.uniqueLookupIndexFactory.apply(fieldAccess.type());
/*  543: 954 */         configLookupIndex(fields, prop, index);
/*  544:     */       }
/*  545:     */       else
/*  546:     */       {
/*  547: 957 */         LookupIndex index = (LookupIndex)this.uniqueLookupIndexFactory.apply(Object.class);
/*  548: 958 */         configLookupIndex(fields, prop, index);
/*  549:     */       }
/*  550:     */     }
/*  551:     */   }
/*  552:     */   
/*  553:     */   private boolean isPropPath(String prop)
/*  554:     */   {
/*  555: 966 */     if (prop.contains(".")) {
/*  556: 966 */       return true;
/*  557:     */     }
/*  558: 967 */     if (prop.equals("this")) {
/*  559: 967 */       return true;
/*  560:     */     }
/*  561: 968 */     if (prop.equals("[")) {
/*  562: 968 */       return true;
/*  563:     */     }
/*  564: 970 */     return false;
/*  565:     */   }
/*  566:     */   
/*  567:     */   private void configLookupIndex(Map<String, FieldAccess> fields, String prop, LookupIndex index)
/*  568:     */   {
/*  569: 974 */     Function kg = getKeyGetterOrCreate(fields, prop);
/*  570:     */     
/*  571:     */ 
/*  572:     */ 
/*  573: 978 */     index.setInputKeyTransformer((Function)this.keyTransformers.get(prop));
/*  574: 979 */     index.setKeyGetter(kg);
/*  575: 980 */     index.setBucketSize(this.indexBucketSize.get(prop) == null ? 3 : ((Integer)this.indexBucketSize.get(prop)).intValue());
/*  576:     */     
/*  577: 982 */     index.init();
/*  578: 983 */     ((SearchableCollection)this.query).addLookupIndex(prop, index);
/*  579:     */   }
/*  580:     */   
/*  581:     */   private void configSearchIndex(Map<String, FieldAccess> fields, String prop, SearchIndex searchIndex)
/*  582:     */   {
/*  583: 988 */     FieldAccess fieldAccess = fields == null ? null : (FieldAccess)fields.get(prop);
/*  584:     */     
/*  585: 990 */     Comparator comparator = (Comparator)this.collators.get(prop);
/*  586: 991 */     if ((comparator == null) && 
/*  587: 992 */       (fieldAccess != null) && (fieldAccess.type() == Typ.number)) {
/*  588: 993 */       comparator = new Comparator()
/*  589:     */       {
/*  590:     */         public int compare(Number o1, Number o2)
/*  591:     */         {
/*  592: 997 */           if ((o1 instanceof Long))
/*  593:     */           {
/*  594: 998 */             long long1 = o1.longValue();
/*  595: 999 */             long long2 = o2.longValue();
/*  596:1000 */             if (long1 > long2) {
/*  597:1001 */               return 1;
/*  598:     */             }
/*  599:1002 */             if (long1 < long2) {
/*  600:1003 */               return -1;
/*  601:     */             }
/*  602:1005 */             return 0;
/*  603:     */           }
/*  604:1007 */           if ((o1 instanceof Double))
/*  605:     */           {
/*  606:1008 */             double long1 = o1.doubleValue();
/*  607:1009 */             double long2 = o2.doubleValue();
/*  608:1010 */             if (long1 > long2) {
/*  609:1011 */               return 1;
/*  610:     */             }
/*  611:1012 */             if (long1 < long2) {
/*  612:1013 */               return -1;
/*  613:     */             }
/*  614:1015 */             return 0;
/*  615:     */           }
/*  616:1017 */           if ((o1 instanceof BigDecimal))
/*  617:     */           {
/*  618:1018 */             double long1 = o1.doubleValue();
/*  619:1019 */             double long2 = o2.doubleValue();
/*  620:1020 */             if (long1 > long2) {
/*  621:1021 */               return 1;
/*  622:     */             }
/*  623:1022 */             if (long1 < long2) {
/*  624:1023 */               return -1;
/*  625:     */             }
/*  626:1025 */             return 0;
/*  627:     */           }
/*  628:1028 */           double long1 = o1.doubleValue();
/*  629:1029 */           double long2 = o2.doubleValue();
/*  630:1030 */           if (long1 > long2) {
/*  631:1031 */             return 1;
/*  632:     */           }
/*  633:1032 */           if (long1 < long2) {
/*  634:1033 */             return -1;
/*  635:     */           }
/*  636:1035 */           return 0;
/*  637:     */         }
/*  638:     */       };
/*  639:     */     }
/*  640:1044 */     searchIndex.setComparator(comparator);
/*  641:1045 */     searchIndex.setInputKeyTransformer((Function)this.keyTransformers.get(prop));
/*  642:1046 */     Function kg = getKeyGetterOrCreate(fields, prop);
/*  643:1047 */     searchIndex.setKeyGetter(kg);
/*  644:1048 */     searchIndex.setBucketSize(this.indexBucketSize.get(prop) == null ? 3 : ((Integer)this.indexBucketSize.get(prop)).intValue());
/*  645:     */     
/*  646:1050 */     searchIndex.init();
/*  647:1051 */     ((SearchableCollection)this.query).addSearchIndex(prop, searchIndex);
/*  648:     */   }
/*  649:     */   
/*  650:     */   private void configIndex(String prop, NestedKeySearchIndex index)
/*  651:     */   {
/*  652:1055 */     index.setComparator((Comparator)this.collators.get(prop));
/*  653:1056 */     index.setInputKeyTransformer((Function)this.keyTransformers.get(prop));
/*  654:1057 */     index.setBucketSize(this.indexBucketSize.get(prop) == null ? 3 : ((Integer)this.indexBucketSize.get(prop)).intValue());
/*  655:1058 */     index.init();
/*  656:1059 */     ((SearchableCollection)this.query).addSearchIndex(prop, index);
/*  657:     */   }
/*  658:     */   
/*  659:     */   private Function getKeyGetterOrCreate(Map<String, FieldAccess> fields, final String prop)
/*  660:     */   {
/*  661:1064 */     if (Typ.isMap(this.itemClass))
/*  662:     */     {
/*  663:1067 */       Function kg = null;
/*  664:     */       
/*  665:1069 */       kg = (Function)this.keyGetterMap.get(prop);
/*  666:1071 */       if (kg == null) {
/*  667:1073 */         this.keyGetterMap.put(prop, new Function()
/*  668:     */         {
/*  669:     */           public Object apply(Object o)
/*  670:     */           {
/*  671:1076 */             return BeanUtils.atIndex(o, prop);
/*  672:     */           }
/*  673:     */         });
/*  674:     */       }
/*  675:1080 */       return (Function)this.keyGetterMap.get(prop);
/*  676:     */     }
/*  677:1085 */     Exceptions.requireNonNull(fields, "fields cannot be null");
/*  678:1086 */     Exceptions.requireNonNull(prop, "prop cannot be null");
/*  679:     */     
/*  680:1088 */     Function kg = null;
/*  681:     */     
/*  682:1090 */     kg = (Function)this.keyGetterMap.get(prop);
/*  683:1092 */     if (kg == null) {
/*  684:1094 */       if ((prop.contains(".")) || (prop.contains("this")) || (prop.contains("[")))
/*  685:     */       {
/*  686:1095 */         this.keyGetterMap.put(prop, new Function()
/*  687:     */         {
/*  688:     */           public Object apply(Object o)
/*  689:     */           {
/*  690:1098 */             return BeanUtils.atIndex(o, prop);
/*  691:     */           }
/*  692:1100 */         });
/*  693:1101 */         kg = (Function)this.keyGetterMap.get(prop);
/*  694:     */       }
/*  695:     */       else
/*  696:     */       {
/*  697:1104 */         FieldAccess field = (FieldAccess)fields.get(prop);
/*  698:1105 */         kg = createKeyGetter(field);
/*  699:1106 */         this.keyGetterMap.put(prop, kg);
/*  700:     */       }
/*  701:     */     }
/*  702:1109 */     return kg;
/*  703:     */   }
/*  704:     */   
/*  705:     */   private void configPrimaryKey(Class<?> type, Map<String, FieldAccess> fields)
/*  706:     */   {
/*  707:1115 */     Exceptions.requireNonNull(this.primaryKey, "primary key cannot be null");
/*  708:     */     
/*  709:1117 */     LookupIndex primaryKeyIndex = (LookupIndex)this.uniqueLookupIndexFactory.apply(type);
/*  710:1120 */     if ((!Typ.isMap(this.itemClass)) && (!fields.containsKey(this.primaryKey))) {
/*  711:1121 */       throw new IllegalStateException(String.format("Fields does not have primary key %s", new Object[] { this.primaryKey }));
/*  712:     */     }
/*  713:1127 */     primaryKeyIndex.setKeyGetter(getKeyGetterOrCreate(fields, this.primaryKey));
/*  714:1128 */     this.query.setPrimaryKeyName(this.primaryKey);
/*  715:1129 */     this.query.setPrimaryKeyGetter((Function)this.keyGetterMap.get(this.primaryKey));
/*  716:     */     
/*  717:     */ 
/*  718:1132 */     ((SearchableCollection)this.query).addLookupIndex(this.primaryKey, primaryKeyIndex);
/*  719:     */   }
/*  720:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.RepoBuilderDefault
 * JD-Core Version:    0.7.0.1
 */