/*    1:     */ package org.boon;
/*    2:     */ 
/*    3:     */ import java.io.BufferedReader;
/*    4:     */ import java.io.File;
/*    5:     */ import java.io.FileReader;
/*    6:     */ import java.io.IOException;
/*    7:     */ import java.io.InputStream;
/*    8:     */ import java.io.InputStreamReader;
/*    9:     */ import java.io.OutputStream;
/*   10:     */ import java.io.Reader;
/*   11:     */ import java.io.Writer;
/*   12:     */ import java.net.URI;
/*   13:     */ import java.net.URL;
/*   14:     */ import java.nio.charset.Charset;
/*   15:     */ import java.nio.charset.StandardCharsets;
/*   16:     */ import java.nio.file.CopyOption;
/*   17:     */ import java.nio.file.DirectoryStream;
/*   18:     */ import java.nio.file.FileSystem;
/*   19:     */ import java.nio.file.FileSystems;
/*   20:     */ import java.nio.file.Files;
/*   21:     */ import java.nio.file.LinkOption;
/*   22:     */ import java.nio.file.OpenOption;
/*   23:     */ import java.nio.file.Path;
/*   24:     */ import java.nio.file.Paths;
/*   25:     */ import java.nio.file.ProviderNotFoundException;
/*   26:     */ import java.nio.file.attribute.FileAttribute;
/*   27:     */ import java.nio.file.spi.FileSystemProvider;
/*   28:     */ import java.util.ArrayList;
/*   29:     */ import java.util.Collections;
/*   30:     */ import java.util.List;
/*   31:     */ import java.util.Map;
/*   32:     */ import java.util.Set;
/*   33:     */ import java.util.concurrent.ConcurrentHashMap;
/*   34:     */ import org.boon.core.Function;
/*   35:     */ import org.boon.core.Sys;
/*   36:     */ import org.boon.core.Typ;
/*   37:     */ import org.boon.primitive.CharBuf;
/*   38:     */ 
/*   39:     */ public class IO
/*   40:     */ {
/*   41:  61 */   public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
/*   42:     */   public static final String FILE_SCHEMA = "file";
/*   43:     */   public static final String JAR_SCHEMA = "jar";
/*   44:     */   public static final String CLASSPATH_SCHEMA = "classpath";
/*   45:     */   public static final String JAR_FILE_SCHEMA = "jar:file";
/*   46:     */   private static final int DEFAULT_BUFFER_SIZE = 4096;
/*   47:     */   private static final int EOF = -1;
/*   48:  74 */   private static ConcurrentHashMap<String, FileSystem> pathToZipFileSystems = new ConcurrentHashMap();
/*   49:     */   
/*   50:     */   private static Path convertJarFileSystemURIToPath(String resourceURL)
/*   51:     */   {
/*   52:  78 */     String str = resourceURL;
/*   53:     */     
/*   54:  80 */     String[] strings = StringScanner.split(str, '!');
/*   55:     */     
/*   56:  82 */     URI fileJarURI = URI.create(strings[0]);
/*   57:  83 */     String resourcePath = strings[1];
/*   58:     */     
/*   59:  85 */     String key = Str.slc(strings[0], "jar:file".length() + 1);
/*   60:  86 */     if (!pathToZipFileSystems.containsKey(fileJarURI))
/*   61:     */     {
/*   62:  87 */       pathToZipFileSystems.put(key, zipFileSystem(fileJarURI));
/*   63:     */       
/*   64:  89 */       cleanPathToZipFileSystemMap();
/*   65:     */     }
/*   66:  92 */     FileSystem fileSystem = (FileSystem)pathToZipFileSystems.get(key);
/*   67:     */     
/*   68:  94 */     Path path = fileSystem.getPath(resourcePath, new String[0]);
/*   69:     */     
/*   70:  96 */     return path;
/*   71:     */   }
/*   72:     */   
/*   73:     */   private static void cleanPathToZipFileSystemMap()
/*   74:     */   {
/*   75: 101 */     Set<String> paths = pathToZipFileSystems.keySet();
/*   76: 102 */     for (String path : paths) {
/*   77: 103 */       if (!Files.exists(path(path), new LinkOption[0])) {
/*   78: 104 */         pathToZipFileSystems.remove(path);
/*   79:     */       }
/*   80:     */     }
/*   81:     */   }
/*   82:     */   
/*   83:     */   public static FileSystem zipFileSystem(URI fileJarURI)
/*   84:     */   {
/*   85: 113 */     Map<String, Object> env = Maps.map("create", "true");
/*   86:     */     
/*   87: 115 */     FileSystemProvider provider = loadFileSystemProvider("jar");
/*   88:     */     
/*   89: 117 */     Exceptions.requireNonNull(provider, "Zip file provider not found");
/*   90:     */     
/*   91: 119 */     FileSystem fs = null;
/*   92:     */     try
/*   93:     */     {
/*   94: 122 */       fs = provider.getFileSystem(fileJarURI);
/*   95:     */     }
/*   96:     */     catch (Exception ex)
/*   97:     */     {
/*   98: 124 */       if (provider != null) {
/*   99:     */         try
/*  100:     */         {
/*  101: 126 */           fs = provider.newFileSystem(fileJarURI, env);
/*  102:     */         }
/*  103:     */         catch (IOException ex2)
/*  104:     */         {
/*  105: 128 */           Exceptions.handle(FileSystem.class, Boon.sputs(new Object[] { "unable to load", fileJarURI, "as zip file system" }), ex2);
/*  106:     */         }
/*  107:     */       }
/*  108:     */     }
/*  109: 135 */     Exceptions.requireNonNull(provider, "Zip file system was not found");
/*  110:     */     
/*  111: 137 */     return fs;
/*  112:     */   }
/*  113:     */   
/*  114:     */   private static FileSystemProvider loadFileSystemProvider(String providerType)
/*  115:     */   {
/*  116: 141 */     FileSystemProvider provider = null;
/*  117: 142 */     for (FileSystemProvider p : FileSystemProvider.installedProviders()) {
/*  118: 143 */       if (providerType.equals(p.getScheme()))
/*  119:     */       {
/*  120: 144 */         provider = p;
/*  121: 145 */         break;
/*  122:     */       }
/*  123:     */     }
/*  124: 148 */     return provider;
/*  125:     */   }
/*  126:     */   
/*  127:     */   public static abstract interface EachLine
/*  128:     */   {
/*  129:     */     public abstract boolean line(String paramString, int paramInt);
/*  130:     */   }
/*  131:     */   
/*  132:     */   public static class ConvertToPathFunction
/*  133:     */     implements Function<String, Path>
/*  134:     */   {
/*  135:     */     public Path apply(String s)
/*  136:     */     {
/*  137: 156 */       return IO.path(s);
/*  138:     */     }
/*  139:     */   }
/*  140:     */   
/*  141: 160 */   public static ConvertToPathFunction convertToPathFunction = new ConvertToPathFunction();
/*  142:     */   
/*  143:     */   public static List<String> list(Path path)
/*  144:     */   {
/*  145: 165 */     if (!exists(path)) {
/*  146: 166 */       return Collections.EMPTY_LIST;
/*  147:     */     }
/*  148: 169 */     List<String> result = new ArrayList();
/*  149:     */     try
/*  150:     */     {
/*  151: 173 */       DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);Throwable localThrowable2 = null;
/*  152:     */       try
/*  153:     */       {
/*  154: 174 */         for (Path entry : directoryStream) {
/*  155: 175 */           result.add(entry.toAbsolutePath().toString());
/*  156:     */         }
/*  157:     */       }
/*  158:     */       catch (Throwable localThrowable1)
/*  159:     */       {
/*  160: 173 */         localThrowable2 = localThrowable1;throw localThrowable1;
/*  161:     */       }
/*  162:     */       finally
/*  163:     */       {
/*  164: 177 */         if (directoryStream != null) {
/*  165: 177 */           if (localThrowable2 != null) {
/*  166:     */             try
/*  167:     */             {
/*  168: 177 */               directoryStream.close();
/*  169:     */             }
/*  170:     */             catch (Throwable x2)
/*  171:     */             {
/*  172: 177 */               localThrowable2.addSuppressed(x2);
/*  173:     */             }
/*  174:     */           } else {
/*  175: 177 */             directoryStream.close();
/*  176:     */           }
/*  177:     */         }
/*  178:     */       }
/*  179: 178 */       return result;
/*  180:     */     }
/*  181:     */     catch (IOException ex)
/*  182:     */     {
/*  183: 180 */       return (List)Exceptions.handle(List.class, ex);
/*  184:     */     }
/*  185:     */   }
/*  186:     */   
/*  187:     */   public static List<Path> listPath(Path path)
/*  188:     */   {
/*  189: 188 */     if (!exists(path)) {
/*  190: 189 */       return Collections.EMPTY_LIST;
/*  191:     */     }
/*  192: 192 */     List<Path> result = new ArrayList();
/*  193:     */     try
/*  194:     */     {
/*  195: 196 */       DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);Throwable localThrowable2 = null;
/*  196:     */       try
/*  197:     */       {
/*  198: 197 */         for (Path entry : directoryStream) {
/*  199: 198 */           result.add(entry.toAbsolutePath());
/*  200:     */         }
/*  201:     */       }
/*  202:     */       catch (Throwable localThrowable1)
/*  203:     */       {
/*  204: 196 */         localThrowable2 = localThrowable1;throw localThrowable1;
/*  205:     */       }
/*  206:     */       finally
/*  207:     */       {
/*  208: 200 */         if (directoryStream != null) {
/*  209: 200 */           if (localThrowable2 != null) {
/*  210:     */             try
/*  211:     */             {
/*  212: 200 */               directoryStream.close();
/*  213:     */             }
/*  214:     */             catch (Throwable x2)
/*  215:     */             {
/*  216: 200 */               localThrowable2.addSuppressed(x2);
/*  217:     */             }
/*  218:     */           } else {
/*  219: 200 */             directoryStream.close();
/*  220:     */           }
/*  221:     */         }
/*  222:     */       }
/*  223: 201 */       return result;
/*  224:     */     }
/*  225:     */     catch (IOException ex)
/*  226:     */     {
/*  227: 203 */       return (List)Exceptions.handle(List.class, ex);
/*  228:     */     }
/*  229:     */   }
/*  230:     */   
/*  231:     */   public static List<String> listByGlob(String path, String glob)
/*  232:     */   {
/*  233: 208 */     Path pathFromFileSystem = path(path);
/*  234: 209 */     return listByGlob(pathFromFileSystem, glob);
/*  235:     */   }
/*  236:     */   
/*  237:     */   public static List<String> listByGlob(Path pathFromFileSystem, String glob)
/*  238:     */   {
/*  239: 215 */     List<String> result = new ArrayList();
/*  240:     */     try
/*  241:     */     {
/*  242: 218 */       DirectoryStream<Path> stream = Files.newDirectoryStream(pathFromFileSystem, glob);Throwable localThrowable2 = null;
/*  243:     */       try
/*  244:     */       {
/*  245: 219 */         for (Path entry : stream) {
/*  246: 220 */           result.add(entry.toAbsolutePath().toString());
/*  247:     */         }
/*  248:     */       }
/*  249:     */       catch (Throwable localThrowable1)
/*  250:     */       {
/*  251: 218 */         localThrowable2 = localThrowable1;throw localThrowable1;
/*  252:     */       }
/*  253:     */       finally
/*  254:     */       {
/*  255: 222 */         if (stream != null) {
/*  256: 222 */           if (localThrowable2 != null) {
/*  257:     */             try
/*  258:     */             {
/*  259: 222 */               stream.close();
/*  260:     */             }
/*  261:     */             catch (Throwable x2)
/*  262:     */             {
/*  263: 222 */               localThrowable2.addSuppressed(x2);
/*  264:     */             }
/*  265:     */           } else {
/*  266: 222 */             stream.close();
/*  267:     */           }
/*  268:     */         }
/*  269:     */       }
/*  270: 223 */       return result;
/*  271:     */     }
/*  272:     */     catch (IOException ex)
/*  273:     */     {
/*  274: 225 */       return (List)Exceptions.handle(List.class, ex);
/*  275:     */     }
/*  276:     */   }
/*  277:     */   
/*  278:     */   public static List<String> listByFileExtension(String path, String ext)
/*  279:     */   {
/*  280: 232 */     Path pathFromFileSystem = path(path);
/*  281: 233 */     return listByFileExtension(pathFromFileSystem, ext);
/*  282:     */   }
/*  283:     */   
/*  284:     */   public static List<String> listByFileExtension(Path pathFromFileSystem, String ext)
/*  285:     */   {
/*  286: 237 */     String extToLookForGlob = "*." + ext;
/*  287:     */     
/*  288: 239 */     List<String> result = new ArrayList();
/*  289:     */     try
/*  290:     */     {
/*  291: 242 */       DirectoryStream<Path> stream = Files.newDirectoryStream(pathFromFileSystem, extToLookForGlob);Throwable localThrowable2 = null;
/*  292:     */       try
/*  293:     */       {
/*  294: 243 */         for (Path entry : stream) {
/*  295: 244 */           result.add(entry.toAbsolutePath().toString());
/*  296:     */         }
/*  297:     */       }
/*  298:     */       catch (Throwable localThrowable1)
/*  299:     */       {
/*  300: 242 */         localThrowable2 = localThrowable1;throw localThrowable1;
/*  301:     */       }
/*  302:     */       finally
/*  303:     */       {
/*  304: 246 */         if (stream != null) {
/*  305: 246 */           if (localThrowable2 != null) {
/*  306:     */             try
/*  307:     */             {
/*  308: 246 */               stream.close();
/*  309:     */             }
/*  310:     */             catch (Throwable x2)
/*  311:     */             {
/*  312: 246 */               localThrowable2.addSuppressed(x2);
/*  313:     */             }
/*  314:     */           } else {
/*  315: 246 */             stream.close();
/*  316:     */           }
/*  317:     */         }
/*  318:     */       }
/*  319: 247 */       return result;
/*  320:     */     }
/*  321:     */     catch (IOException ex)
/*  322:     */     {
/*  323: 249 */       return (List)Exceptions.handle(List.class, ex);
/*  324:     */     }
/*  325:     */   }
/*  326:     */   
/*  327:     */   public static List<String> listByFileExtensionRecursive(String path, String ext)
/*  328:     */   {
/*  329: 256 */     Path pathFromFileSystem = path(path);
/*  330: 257 */     return listByFileExtensionRecursive(pathFromFileSystem, ext);
/*  331:     */   }
/*  332:     */   
/*  333:     */   public static List<String> listByFileExtensionRecursive(Path pathFromFileSystem, String ext)
/*  334:     */   {
/*  335: 263 */     String extToLookForGlob = "*." + ext;
/*  336:     */     
/*  337: 265 */     List<String> result = new ArrayList();
/*  338:     */     
/*  339: 267 */     return doListByFileExtensionRecursive(result, pathFromFileSystem, extToLookForGlob);
/*  340:     */   }
/*  341:     */   
/*  342:     */   private static List<String> doListByFileExtensionRecursive(List<String> result, Path pathFromFileSystem, String glob)
/*  343:     */   {
/*  344:     */     try
/*  345:     */     {
/*  346: 276 */       DirectoryStream<Path> stream = Files.newDirectoryStream(pathFromFileSystem, glob);Throwable localThrowable3 = null;
/*  347:     */       try
/*  348:     */       {
/*  349: 277 */         for (Path entry : stream) {
/*  350: 278 */           result.add(entry.toAbsolutePath().toString());
/*  351:     */         }
/*  352:     */       }
/*  353:     */       catch (Throwable localThrowable1)
/*  354:     */       {
/*  355: 276 */         localThrowable3 = localThrowable1;throw localThrowable1;
/*  356:     */       }
/*  357:     */       finally
/*  358:     */       {
/*  359: 280 */         if (stream != null) {
/*  360: 280 */           if (localThrowable3 != null) {
/*  361:     */             try
/*  362:     */             {
/*  363: 280 */               stream.close();
/*  364:     */             }
/*  365:     */             catch (Throwable x2)
/*  366:     */             {
/*  367: 280 */               localThrowable3.addSuppressed(x2);
/*  368:     */             }
/*  369:     */           } else {
/*  370: 280 */             stream.close();
/*  371:     */           }
/*  372:     */         }
/*  373:     */       }
/*  374: 281 */       DirectoryStream<Path> stream = Files.newDirectoryStream(pathFromFileSystem);localThrowable3 = null;
/*  375:     */       try
/*  376:     */       {
/*  377: 282 */         for (Path entry : stream) {
/*  378: 283 */           if (Files.isDirectory(entry, new LinkOption[0])) {
/*  379: 284 */             doListByFileExtensionRecursive(result, entry, glob);
/*  380:     */           }
/*  381:     */         }
/*  382:     */       }
/*  383:     */       catch (Throwable localThrowable2)
/*  384:     */       {
/*  385: 281 */         localThrowable3 = localThrowable2;throw localThrowable2;
/*  386:     */       }
/*  387:     */       finally
/*  388:     */       {
/*  389: 287 */         if (stream != null) {
/*  390: 287 */           if (localThrowable3 != null) {
/*  391:     */             try
/*  392:     */             {
/*  393: 287 */               stream.close();
/*  394:     */             }
/*  395:     */             catch (Throwable x2)
/*  396:     */             {
/*  397: 287 */               localThrowable3.addSuppressed(x2);
/*  398:     */             }
/*  399:     */           } else {
/*  400: 287 */             stream.close();
/*  401:     */           }
/*  402:     */         }
/*  403:     */       }
/*  404: 289 */       return result;
/*  405:     */     }
/*  406:     */     catch (IOException ex)
/*  407:     */     {
/*  408: 291 */       return (List)Exceptions.handle(List.class, ex);
/*  409:     */     }
/*  410:     */   }
/*  411:     */   
/*  412:     */   public static String readChild(Path parentDir, String childFileName)
/*  413:     */   {
/*  414:     */     try
/*  415:     */     {
/*  416: 299 */       Path newFilePath = path(parentDir.toString(), new String[] { childFileName });
/*  417:     */       
/*  418:     */ 
/*  419: 302 */       return read(newFilePath);
/*  420:     */     }
/*  421:     */     catch (Exception ex)
/*  422:     */     {
/*  423: 304 */       return (String)Exceptions.handle(String.class, ex);
/*  424:     */     }
/*  425:     */   }
/*  426:     */   
/*  427:     */   public static char[] readCharBuffer(Path path)
/*  428:     */   {
/*  429:     */     try
/*  430:     */     {
/*  431: 312 */       long bufSize = Files.size(path);
/*  432: 313 */       return readCharBuffer(Files.newBufferedReader(path, DEFAULT_CHARSET), (int)bufSize);
/*  433:     */     }
/*  434:     */     catch (IOException ex)
/*  435:     */     {
/*  436: 316 */       return (char[])Exceptions.handle([C.class, ex);
/*  437:     */     }
/*  438:     */   }
/*  439:     */   
/*  440:     */   /* Error */
/*  441:     */   public static String read(InputStream inputStream, Charset charset)
/*  442:     */   {
/*  443:     */     // Byte code:
/*  444:     */     //   0: new 84	java/io/InputStreamReader
/*  445:     */     //   3: dup
/*  446:     */     //   4: aload_0
/*  447:     */     //   5: aload_1
/*  448:     */     //   6: invokespecial 85	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/nio/charset/Charset;)V
/*  449:     */     //   9: astore_2
/*  450:     */     //   10: aconst_null
/*  451:     */     //   11: astore_3
/*  452:     */     //   12: aload_2
/*  453:     */     //   13: invokestatic 86	org/boon/IO:read	(Ljava/io/Reader;)Ljava/lang/String;
/*  454:     */     //   16: astore 4
/*  455:     */     //   18: aload_2
/*  456:     */     //   19: ifnull +29 -> 48
/*  457:     */     //   22: aload_3
/*  458:     */     //   23: ifnull +21 -> 44
/*  459:     */     //   26: aload_2
/*  460:     */     //   27: invokevirtual 87	java/io/Reader:close	()V
/*  461:     */     //   30: goto +18 -> 48
/*  462:     */     //   33: astore 5
/*  463:     */     //   35: aload_3
/*  464:     */     //   36: aload 5
/*  465:     */     //   38: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*  466:     */     //   41: goto +7 -> 48
/*  467:     */     //   44: aload_2
/*  468:     */     //   45: invokevirtual 87	java/io/Reader:close	()V
/*  469:     */     //   48: aload 4
/*  470:     */     //   50: areturn
/*  471:     */     //   51: astore 4
/*  472:     */     //   53: aload 4
/*  473:     */     //   55: astore_3
/*  474:     */     //   56: aload 4
/*  475:     */     //   58: athrow
/*  476:     */     //   59: astore 6
/*  477:     */     //   61: aload_2
/*  478:     */     //   62: ifnull +29 -> 91
/*  479:     */     //   65: aload_3
/*  480:     */     //   66: ifnull +21 -> 87
/*  481:     */     //   69: aload_2
/*  482:     */     //   70: invokevirtual 87	java/io/Reader:close	()V
/*  483:     */     //   73: goto +18 -> 91
/*  484:     */     //   76: astore 7
/*  485:     */     //   78: aload_3
/*  486:     */     //   79: aload 7
/*  487:     */     //   81: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*  488:     */     //   84: goto +7 -> 91
/*  489:     */     //   87: aload_2
/*  490:     */     //   88: invokevirtual 87	java/io/Reader:close	()V
/*  491:     */     //   91: aload 6
/*  492:     */     //   93: athrow
/*  493:     */     //   94: astore_2
/*  494:     */     //   95: ldc_w 19
/*  495:     */     //   98: aload_2
/*  496:     */     //   99: invokestatic 65	org/boon/Exceptions:handle	(Ljava/lang/Class;Ljava/lang/Exception;)Ljava/lang/Object;
/*  497:     */     //   102: checkcast 19	java/lang/String
/*  498:     */     //   105: areturn
/*  499:     */     // Line number table:
/*  500:     */     //   Java source line #322	-> byte code offset #0
/*  501:     */     //   Java source line #323	-> byte code offset #12
/*  502:     */     //   Java source line #324	-> byte code offset #18
/*  503:     */     //   Java source line #322	-> byte code offset #51
/*  504:     */     //   Java source line #324	-> byte code offset #59
/*  505:     */     //   Java source line #325	-> byte code offset #95
/*  506:     */     // Local variable table:
/*  507:     */     //   start	length	slot	name	signature
/*  508:     */     //   0	106	0	inputStream	InputStream
/*  509:     */     //   0	106	1	charset	Charset
/*  510:     */     //   9	79	2	reader	Reader
/*  511:     */     //   94	5	2	ex	Exception
/*  512:     */     //   11	68	3	localThrowable2	Throwable
/*  513:     */     //   51	6	4	localThrowable1	Throwable
/*  514:     */     //   51	6	4	localThrowable3	Throwable
/*  515:     */     //   33	4	5	x2	Throwable
/*  516:     */     //   59	33	6	localObject	Object
/*  517:     */     //   76	4	7	x2	Throwable
/*  518:     */     // Exception table:
/*  519:     */     //   from	to	target	type
/*  520:     */     //   26	30	33	java/lang/Throwable
/*  521:     */     //   12	18	51	java/lang/Throwable
/*  522:     */     //   12	18	59	finally
/*  523:     */     //   51	61	59	finally
/*  524:     */     //   69	73	76	java/lang/Throwable
/*  525:     */     //   0	48	94	java/lang/Exception
/*  526:     */     //   51	94	94	java/lang/Exception
/*  527:     */   }
/*  528:     */   
/*  529:     */   /* Error */
/*  530:     */   public static String read(InputStream inputStream, String charset)
/*  531:     */   {
/*  532:     */     // Byte code:
/*  533:     */     //   0: new 84	java/io/InputStreamReader
/*  534:     */     //   3: dup
/*  535:     */     //   4: aload_0
/*  536:     */     //   5: aload_1
/*  537:     */     //   6: invokespecial 88	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/lang/String;)V
/*  538:     */     //   9: astore_2
/*  539:     */     //   10: aconst_null
/*  540:     */     //   11: astore_3
/*  541:     */     //   12: aload_2
/*  542:     */     //   13: invokestatic 86	org/boon/IO:read	(Ljava/io/Reader;)Ljava/lang/String;
/*  543:     */     //   16: astore 4
/*  544:     */     //   18: aload_2
/*  545:     */     //   19: ifnull +29 -> 48
/*  546:     */     //   22: aload_3
/*  547:     */     //   23: ifnull +21 -> 44
/*  548:     */     //   26: aload_2
/*  549:     */     //   27: invokevirtual 87	java/io/Reader:close	()V
/*  550:     */     //   30: goto +18 -> 48
/*  551:     */     //   33: astore 5
/*  552:     */     //   35: aload_3
/*  553:     */     //   36: aload 5
/*  554:     */     //   38: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*  555:     */     //   41: goto +7 -> 48
/*  556:     */     //   44: aload_2
/*  557:     */     //   45: invokevirtual 87	java/io/Reader:close	()V
/*  558:     */     //   48: aload 4
/*  559:     */     //   50: areturn
/*  560:     */     //   51: astore 4
/*  561:     */     //   53: aload 4
/*  562:     */     //   55: astore_3
/*  563:     */     //   56: aload 4
/*  564:     */     //   58: athrow
/*  565:     */     //   59: astore 6
/*  566:     */     //   61: aload_2
/*  567:     */     //   62: ifnull +29 -> 91
/*  568:     */     //   65: aload_3
/*  569:     */     //   66: ifnull +21 -> 87
/*  570:     */     //   69: aload_2
/*  571:     */     //   70: invokevirtual 87	java/io/Reader:close	()V
/*  572:     */     //   73: goto +18 -> 91
/*  573:     */     //   76: astore 7
/*  574:     */     //   78: aload_3
/*  575:     */     //   79: aload 7
/*  576:     */     //   81: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*  577:     */     //   84: goto +7 -> 91
/*  578:     */     //   87: aload_2
/*  579:     */     //   88: invokevirtual 87	java/io/Reader:close	()V
/*  580:     */     //   91: aload 6
/*  581:     */     //   93: athrow
/*  582:     */     //   94: astore_2
/*  583:     */     //   95: ldc_w 19
/*  584:     */     //   98: aload_2
/*  585:     */     //   99: invokestatic 65	org/boon/Exceptions:handle	(Ljava/lang/Class;Ljava/lang/Exception;)Ljava/lang/Object;
/*  586:     */     //   102: checkcast 19	java/lang/String
/*  587:     */     //   105: areturn
/*  588:     */     // Line number table:
/*  589:     */     //   Java source line #332	-> byte code offset #0
/*  590:     */     //   Java source line #333	-> byte code offset #12
/*  591:     */     //   Java source line #334	-> byte code offset #18
/*  592:     */     //   Java source line #332	-> byte code offset #51
/*  593:     */     //   Java source line #334	-> byte code offset #59
/*  594:     */     //   Java source line #335	-> byte code offset #95
/*  595:     */     // Local variable table:
/*  596:     */     //   start	length	slot	name	signature
/*  597:     */     //   0	106	0	inputStream	InputStream
/*  598:     */     //   0	106	1	charset	String
/*  599:     */     //   9	79	2	reader	Reader
/*  600:     */     //   94	5	2	ex	Exception
/*  601:     */     //   11	68	3	localThrowable2	Throwable
/*  602:     */     //   51	6	4	localThrowable1	Throwable
/*  603:     */     //   51	6	4	localThrowable3	Throwable
/*  604:     */     //   33	4	5	x2	Throwable
/*  605:     */     //   59	33	6	localObject	Object
/*  606:     */     //   76	4	7	x2	Throwable
/*  607:     */     // Exception table:
/*  608:     */     //   from	to	target	type
/*  609:     */     //   26	30	33	java/lang/Throwable
/*  610:     */     //   12	18	51	java/lang/Throwable
/*  611:     */     //   12	18	59	finally
/*  612:     */     //   51	61	59	finally
/*  613:     */     //   69	73	76	java/lang/Throwable
/*  614:     */     //   0	48	94	java/lang/Exception
/*  615:     */     //   51	94	94	java/lang/Exception
/*  616:     */   }
/*  617:     */   
/*  618:     */   /* Error */
/*  619:     */   public static String readCharBuffer(InputStream inputStream, Charset charset)
/*  620:     */   {
/*  621:     */     // Byte code:
/*  622:     */     //   0: new 84	java/io/InputStreamReader
/*  623:     */     //   3: dup
/*  624:     */     //   4: aload_0
/*  625:     */     //   5: aload_1
/*  626:     */     //   6: invokespecial 85	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/nio/charset/Charset;)V
/*  627:     */     //   9: astore_2
/*  628:     */     //   10: aconst_null
/*  629:     */     //   11: astore_3
/*  630:     */     //   12: aload_2
/*  631:     */     //   13: invokestatic 86	org/boon/IO:read	(Ljava/io/Reader;)Ljava/lang/String;
/*  632:     */     //   16: astore 4
/*  633:     */     //   18: aload_2
/*  634:     */     //   19: ifnull +29 -> 48
/*  635:     */     //   22: aload_3
/*  636:     */     //   23: ifnull +21 -> 44
/*  637:     */     //   26: aload_2
/*  638:     */     //   27: invokevirtual 87	java/io/Reader:close	()V
/*  639:     */     //   30: goto +18 -> 48
/*  640:     */     //   33: astore 5
/*  641:     */     //   35: aload_3
/*  642:     */     //   36: aload 5
/*  643:     */     //   38: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*  644:     */     //   41: goto +7 -> 48
/*  645:     */     //   44: aload_2
/*  646:     */     //   45: invokevirtual 87	java/io/Reader:close	()V
/*  647:     */     //   48: aload 4
/*  648:     */     //   50: areturn
/*  649:     */     //   51: astore 4
/*  650:     */     //   53: aload 4
/*  651:     */     //   55: astore_3
/*  652:     */     //   56: aload 4
/*  653:     */     //   58: athrow
/*  654:     */     //   59: astore 6
/*  655:     */     //   61: aload_2
/*  656:     */     //   62: ifnull +29 -> 91
/*  657:     */     //   65: aload_3
/*  658:     */     //   66: ifnull +21 -> 87
/*  659:     */     //   69: aload_2
/*  660:     */     //   70: invokevirtual 87	java/io/Reader:close	()V
/*  661:     */     //   73: goto +18 -> 91
/*  662:     */     //   76: astore 7
/*  663:     */     //   78: aload_3
/*  664:     */     //   79: aload 7
/*  665:     */     //   81: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*  666:     */     //   84: goto +7 -> 91
/*  667:     */     //   87: aload_2
/*  668:     */     //   88: invokevirtual 87	java/io/Reader:close	()V
/*  669:     */     //   91: aload 6
/*  670:     */     //   93: athrow
/*  671:     */     //   94: astore_2
/*  672:     */     //   95: ldc_w 19
/*  673:     */     //   98: aload_2
/*  674:     */     //   99: invokestatic 65	org/boon/Exceptions:handle	(Ljava/lang/Class;Ljava/lang/Exception;)Ljava/lang/Object;
/*  675:     */     //   102: checkcast 19	java/lang/String
/*  676:     */     //   105: areturn
/*  677:     */     // Line number table:
/*  678:     */     //   Java source line #341	-> byte code offset #0
/*  679:     */     //   Java source line #342	-> byte code offset #12
/*  680:     */     //   Java source line #343	-> byte code offset #18
/*  681:     */     //   Java source line #341	-> byte code offset #51
/*  682:     */     //   Java source line #343	-> byte code offset #59
/*  683:     */     //   Java source line #344	-> byte code offset #95
/*  684:     */     // Local variable table:
/*  685:     */     //   start	length	slot	name	signature
/*  686:     */     //   0	106	0	inputStream	InputStream
/*  687:     */     //   0	106	1	charset	Charset
/*  688:     */     //   9	79	2	reader	Reader
/*  689:     */     //   94	5	2	ex	Exception
/*  690:     */     //   11	68	3	localThrowable2	Throwable
/*  691:     */     //   51	6	4	localThrowable1	Throwable
/*  692:     */     //   51	6	4	localThrowable3	Throwable
/*  693:     */     //   33	4	5	x2	Throwable
/*  694:     */     //   59	33	6	localObject	Object
/*  695:     */     //   76	4	7	x2	Throwable
/*  696:     */     // Exception table:
/*  697:     */     //   from	to	target	type
/*  698:     */     //   26	30	33	java/lang/Throwable
/*  699:     */     //   12	18	51	java/lang/Throwable
/*  700:     */     //   12	18	59	finally
/*  701:     */     //   51	61	59	finally
/*  702:     */     //   69	73	76	java/lang/Throwable
/*  703:     */     //   0	48	94	java/lang/Exception
/*  704:     */     //   51	94	94	java/lang/Exception
/*  705:     */   }
/*  706:     */   
/*  707:     */   /* Error */
/*  708:     */   public static String read(InputStream inputStream)
/*  709:     */   {
/*  710:     */     // Byte code:
/*  711:     */     //   0: new 84	java/io/InputStreamReader
/*  712:     */     //   3: dup
/*  713:     */     //   4: aload_0
/*  714:     */     //   5: getstatic 80	org/boon/IO:DEFAULT_CHARSET	Ljava/nio/charset/Charset;
/*  715:     */     //   8: invokespecial 85	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/nio/charset/Charset;)V
/*  716:     */     //   11: astore_1
/*  717:     */     //   12: aconst_null
/*  718:     */     //   13: astore_2
/*  719:     */     //   14: aload_1
/*  720:     */     //   15: invokestatic 86	org/boon/IO:read	(Ljava/io/Reader;)Ljava/lang/String;
/*  721:     */     //   18: astore_3
/*  722:     */     //   19: aload_1
/*  723:     */     //   20: ifnull +29 -> 49
/*  724:     */     //   23: aload_2
/*  725:     */     //   24: ifnull +21 -> 45
/*  726:     */     //   27: aload_1
/*  727:     */     //   28: invokevirtual 87	java/io/Reader:close	()V
/*  728:     */     //   31: goto +18 -> 49
/*  729:     */     //   34: astore 4
/*  730:     */     //   36: aload_2
/*  731:     */     //   37: aload 4
/*  732:     */     //   39: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*  733:     */     //   42: goto +7 -> 49
/*  734:     */     //   45: aload_1
/*  735:     */     //   46: invokevirtual 87	java/io/Reader:close	()V
/*  736:     */     //   49: aload_3
/*  737:     */     //   50: areturn
/*  738:     */     //   51: astore_3
/*  739:     */     //   52: aload_3
/*  740:     */     //   53: astore_2
/*  741:     */     //   54: aload_3
/*  742:     */     //   55: athrow
/*  743:     */     //   56: astore 5
/*  744:     */     //   58: aload_1
/*  745:     */     //   59: ifnull +29 -> 88
/*  746:     */     //   62: aload_2
/*  747:     */     //   63: ifnull +21 -> 84
/*  748:     */     //   66: aload_1
/*  749:     */     //   67: invokevirtual 87	java/io/Reader:close	()V
/*  750:     */     //   70: goto +18 -> 88
/*  751:     */     //   73: astore 6
/*  752:     */     //   75: aload_2
/*  753:     */     //   76: aload 6
/*  754:     */     //   78: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*  755:     */     //   81: goto +7 -> 88
/*  756:     */     //   84: aload_1
/*  757:     */     //   85: invokevirtual 87	java/io/Reader:close	()V
/*  758:     */     //   88: aload 5
/*  759:     */     //   90: athrow
/*  760:     */     //   91: astore_1
/*  761:     */     //   92: ldc_w 19
/*  762:     */     //   95: aload_1
/*  763:     */     //   96: invokestatic 65	org/boon/Exceptions:handle	(Ljava/lang/Class;Ljava/lang/Exception;)Ljava/lang/Object;
/*  764:     */     //   99: checkcast 19	java/lang/String
/*  765:     */     //   102: areturn
/*  766:     */     // Line number table:
/*  767:     */     //   Java source line #350	-> byte code offset #0
/*  768:     */     //   Java source line #351	-> byte code offset #14
/*  769:     */     //   Java source line #352	-> byte code offset #19
/*  770:     */     //   Java source line #350	-> byte code offset #51
/*  771:     */     //   Java source line #352	-> byte code offset #56
/*  772:     */     //   Java source line #353	-> byte code offset #92
/*  773:     */     // Local variable table:
/*  774:     */     //   start	length	slot	name	signature
/*  775:     */     //   0	103	0	inputStream	InputStream
/*  776:     */     //   11	74	1	reader	Reader
/*  777:     */     //   91	5	1	ex	Exception
/*  778:     */     //   13	63	2	localThrowable2	Throwable
/*  779:     */     //   51	4	3	localThrowable1	Throwable
/*  780:     */     //   51	4	3	localThrowable3	Throwable
/*  781:     */     //   34	4	4	x2	Throwable
/*  782:     */     //   56	33	5	localObject	Object
/*  783:     */     //   73	4	6	x2	Throwable
/*  784:     */     // Exception table:
/*  785:     */     //   from	to	target	type
/*  786:     */     //   27	31	34	java/lang/Throwable
/*  787:     */     //   14	19	51	java/lang/Throwable
/*  788:     */     //   14	19	56	finally
/*  789:     */     //   51	58	56	finally
/*  790:     */     //   66	70	73	java/lang/Throwable
/*  791:     */     //   0	49	91	java/lang/Exception
/*  792:     */     //   51	91	91	java/lang/Exception
/*  793:     */   }
/*  794:     */   
/*  795:     */   /* Error */
/*  796:     */   public static char[] readCharBuffer(InputStream inputStream)
/*  797:     */   {
/*  798:     */     // Byte code:
/*  799:     */     //   0: new 84	java/io/InputStreamReader
/*  800:     */     //   3: dup
/*  801:     */     //   4: aload_0
/*  802:     */     //   5: invokespecial 89	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
/*  803:     */     //   8: astore_1
/*  804:     */     //   9: aconst_null
/*  805:     */     //   10: astore_2
/*  806:     */     //   11: aload_1
/*  807:     */     //   12: invokestatic 90	org/boon/IO:readCharBuffer	(Ljava/io/Reader;)[C
/*  808:     */     //   15: astore_3
/*  809:     */     //   16: aload_1
/*  810:     */     //   17: ifnull +29 -> 46
/*  811:     */     //   20: aload_2
/*  812:     */     //   21: ifnull +21 -> 42
/*  813:     */     //   24: aload_1
/*  814:     */     //   25: invokevirtual 87	java/io/Reader:close	()V
/*  815:     */     //   28: goto +18 -> 46
/*  816:     */     //   31: astore 4
/*  817:     */     //   33: aload_2
/*  818:     */     //   34: aload 4
/*  819:     */     //   36: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*  820:     */     //   39: goto +7 -> 46
/*  821:     */     //   42: aload_1
/*  822:     */     //   43: invokevirtual 87	java/io/Reader:close	()V
/*  823:     */     //   46: aload_3
/*  824:     */     //   47: areturn
/*  825:     */     //   48: astore_3
/*  826:     */     //   49: aload_3
/*  827:     */     //   50: astore_2
/*  828:     */     //   51: aload_3
/*  829:     */     //   52: athrow
/*  830:     */     //   53: astore 5
/*  831:     */     //   55: aload_1
/*  832:     */     //   56: ifnull +29 -> 85
/*  833:     */     //   59: aload_2
/*  834:     */     //   60: ifnull +21 -> 81
/*  835:     */     //   63: aload_1
/*  836:     */     //   64: invokevirtual 87	java/io/Reader:close	()V
/*  837:     */     //   67: goto +18 -> 85
/*  838:     */     //   70: astore 6
/*  839:     */     //   72: aload_2
/*  840:     */     //   73: aload 6
/*  841:     */     //   75: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*  842:     */     //   78: goto +7 -> 85
/*  843:     */     //   81: aload_1
/*  844:     */     //   82: invokevirtual 87	java/io/Reader:close	()V
/*  845:     */     //   85: aload 5
/*  846:     */     //   87: athrow
/*  847:     */     //   88: astore_1
/*  848:     */     //   89: ldc_w 83
/*  849:     */     //   92: aload_1
/*  850:     */     //   93: invokestatic 65	org/boon/Exceptions:handle	(Ljava/lang/Class;Ljava/lang/Exception;)Ljava/lang/Object;
/*  851:     */     //   96: checkcast 83	[C
/*  852:     */     //   99: areturn
/*  853:     */     // Line number table:
/*  854:     */     //   Java source line #361	-> byte code offset #0
/*  855:     */     //   Java source line #362	-> byte code offset #11
/*  856:     */     //   Java source line #363	-> byte code offset #16
/*  857:     */     //   Java source line #361	-> byte code offset #48
/*  858:     */     //   Java source line #363	-> byte code offset #53
/*  859:     */     //   Java source line #364	-> byte code offset #89
/*  860:     */     // Local variable table:
/*  861:     */     //   start	length	slot	name	signature
/*  862:     */     //   0	100	0	inputStream	InputStream
/*  863:     */     //   8	74	1	reader	Reader
/*  864:     */     //   88	5	1	ex	Exception
/*  865:     */     //   10	63	2	localThrowable2	Throwable
/*  866:     */     //   48	4	3	localThrowable1	Throwable
/*  867:     */     //   48	4	3	localThrowable3	Throwable
/*  868:     */     //   31	4	4	x2	Throwable
/*  869:     */     //   53	33	5	localObject	Object
/*  870:     */     //   70	4	6	x2	Throwable
/*  871:     */     // Exception table:
/*  872:     */     //   from	to	target	type
/*  873:     */     //   24	28	31	java/lang/Throwable
/*  874:     */     //   11	16	48	java/lang/Throwable
/*  875:     */     //   11	16	53	finally
/*  876:     */     //   48	55	53	finally
/*  877:     */     //   63	67	70	java/lang/Throwable
/*  878:     */     //   0	46	88	java/lang/Exception
/*  879:     */     //   48	88	88	java/lang/Exception
/*  880:     */   }
/*  881:     */   
/*  882:     */   /* Error */
/*  883:     */   public static CharBuf read(InputStream inputStream, CharBuf charBuf)
/*  884:     */   {
/*  885:     */     // Byte code:
/*  886:     */     //   0: new 84	java/io/InputStreamReader
/*  887:     */     //   3: dup
/*  888:     */     //   4: aload_0
/*  889:     */     //   5: invokespecial 89	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
/*  890:     */     //   8: astore_2
/*  891:     */     //   9: aconst_null
/*  892:     */     //   10: astore_3
/*  893:     */     //   11: aload_2
/*  894:     */     //   12: aload_1
/*  895:     */     //   13: invokestatic 91	org/boon/IO:read	(Ljava/io/Reader;Lorg/boon/primitive/CharBuf;)Lorg/boon/primitive/CharBuf;
/*  896:     */     //   16: astore 4
/*  897:     */     //   18: aload_2
/*  898:     */     //   19: ifnull +29 -> 48
/*  899:     */     //   22: aload_3
/*  900:     */     //   23: ifnull +21 -> 44
/*  901:     */     //   26: aload_2
/*  902:     */     //   27: invokevirtual 87	java/io/Reader:close	()V
/*  903:     */     //   30: goto +18 -> 48
/*  904:     */     //   33: astore 5
/*  905:     */     //   35: aload_3
/*  906:     */     //   36: aload 5
/*  907:     */     //   38: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*  908:     */     //   41: goto +7 -> 48
/*  909:     */     //   44: aload_2
/*  910:     */     //   45: invokevirtual 87	java/io/Reader:close	()V
/*  911:     */     //   48: aload 4
/*  912:     */     //   50: areturn
/*  913:     */     //   51: astore 4
/*  914:     */     //   53: aload 4
/*  915:     */     //   55: astore_3
/*  916:     */     //   56: aload 4
/*  917:     */     //   58: athrow
/*  918:     */     //   59: astore 6
/*  919:     */     //   61: aload_2
/*  920:     */     //   62: ifnull +29 -> 91
/*  921:     */     //   65: aload_3
/*  922:     */     //   66: ifnull +21 -> 87
/*  923:     */     //   69: aload_2
/*  924:     */     //   70: invokevirtual 87	java/io/Reader:close	()V
/*  925:     */     //   73: goto +18 -> 91
/*  926:     */     //   76: astore 7
/*  927:     */     //   78: aload_3
/*  928:     */     //   79: aload 7
/*  929:     */     //   81: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*  930:     */     //   84: goto +7 -> 91
/*  931:     */     //   87: aload_2
/*  932:     */     //   88: invokevirtual 87	java/io/Reader:close	()V
/*  933:     */     //   91: aload 6
/*  934:     */     //   93: athrow
/*  935:     */     //   94: astore_2
/*  936:     */     //   95: ldc_w 92
/*  937:     */     //   98: aload_2
/*  938:     */     //   99: invokestatic 65	org/boon/Exceptions:handle	(Ljava/lang/Class;Ljava/lang/Exception;)Ljava/lang/Object;
/*  939:     */     //   102: checkcast 92	org/boon/primitive/CharBuf
/*  940:     */     //   105: areturn
/*  941:     */     // Line number table:
/*  942:     */     //   Java source line #371	-> byte code offset #0
/*  943:     */     //   Java source line #372	-> byte code offset #11
/*  944:     */     //   Java source line #373	-> byte code offset #18
/*  945:     */     //   Java source line #371	-> byte code offset #51
/*  946:     */     //   Java source line #373	-> byte code offset #59
/*  947:     */     //   Java source line #374	-> byte code offset #95
/*  948:     */     // Local variable table:
/*  949:     */     //   start	length	slot	name	signature
/*  950:     */     //   0	106	0	inputStream	InputStream
/*  951:     */     //   0	106	1	charBuf	CharBuf
/*  952:     */     //   8	80	2	reader	Reader
/*  953:     */     //   94	5	2	ex	Exception
/*  954:     */     //   10	69	3	localThrowable2	Throwable
/*  955:     */     //   51	6	4	localThrowable1	Throwable
/*  956:     */     //   51	6	4	localThrowable3	Throwable
/*  957:     */     //   33	4	5	x2	Throwable
/*  958:     */     //   59	33	6	localObject	Object
/*  959:     */     //   76	4	7	x2	Throwable
/*  960:     */     // Exception table:
/*  961:     */     //   from	to	target	type
/*  962:     */     //   26	30	33	java/lang/Throwable
/*  963:     */     //   11	18	51	java/lang/Throwable
/*  964:     */     //   11	18	59	finally
/*  965:     */     //   51	61	59	finally
/*  966:     */     //   69	73	76	java/lang/Throwable
/*  967:     */     //   0	48	94	java/lang/Exception
/*  968:     */     //   51	94	94	java/lang/Exception
/*  969:     */   }
/*  970:     */   
/*  971:     */   /* Error */
/*  972:     */   public static CharBuf read(InputStream inputStream, CharBuf charBuf, Charset charset)
/*  973:     */   {
/*  974:     */     // Byte code:
/*  975:     */     //   0: new 84	java/io/InputStreamReader
/*  976:     */     //   3: dup
/*  977:     */     //   4: aload_0
/*  978:     */     //   5: aload_2
/*  979:     */     //   6: invokespecial 85	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/nio/charset/Charset;)V
/*  980:     */     //   9: astore_3
/*  981:     */     //   10: aconst_null
/*  982:     */     //   11: astore 4
/*  983:     */     //   13: aload_3
/*  984:     */     //   14: aload_1
/*  985:     */     //   15: invokestatic 91	org/boon/IO:read	(Ljava/io/Reader;Lorg/boon/primitive/CharBuf;)Lorg/boon/primitive/CharBuf;
/*  986:     */     //   18: astore 5
/*  987:     */     //   20: aload_3
/*  988:     */     //   21: ifnull +31 -> 52
/*  989:     */     //   24: aload 4
/*  990:     */     //   26: ifnull +22 -> 48
/*  991:     */     //   29: aload_3
/*  992:     */     //   30: invokevirtual 87	java/io/Reader:close	()V
/*  993:     */     //   33: goto +19 -> 52
/*  994:     */     //   36: astore 6
/*  995:     */     //   38: aload 4
/*  996:     */     //   40: aload 6
/*  997:     */     //   42: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/*  998:     */     //   45: goto +7 -> 52
/*  999:     */     //   48: aload_3
/* 1000:     */     //   49: invokevirtual 87	java/io/Reader:close	()V
/* 1001:     */     //   52: aload 5
/* 1002:     */     //   54: areturn
/* 1003:     */     //   55: astore 5
/* 1004:     */     //   57: aload 5
/* 1005:     */     //   59: astore 4
/* 1006:     */     //   61: aload 5
/* 1007:     */     //   63: athrow
/* 1008:     */     //   64: astore 7
/* 1009:     */     //   66: aload_3
/* 1010:     */     //   67: ifnull +31 -> 98
/* 1011:     */     //   70: aload 4
/* 1012:     */     //   72: ifnull +22 -> 94
/* 1013:     */     //   75: aload_3
/* 1014:     */     //   76: invokevirtual 87	java/io/Reader:close	()V
/* 1015:     */     //   79: goto +19 -> 98
/* 1016:     */     //   82: astore 8
/* 1017:     */     //   84: aload 4
/* 1018:     */     //   86: aload 8
/* 1019:     */     //   88: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/* 1020:     */     //   91: goto +7 -> 98
/* 1021:     */     //   94: aload_3
/* 1022:     */     //   95: invokevirtual 87	java/io/Reader:close	()V
/* 1023:     */     //   98: aload 7
/* 1024:     */     //   100: athrow
/* 1025:     */     //   101: astore_3
/* 1026:     */     //   102: ldc_w 92
/* 1027:     */     //   105: aload_3
/* 1028:     */     //   106: invokestatic 65	org/boon/Exceptions:handle	(Ljava/lang/Class;Ljava/lang/Exception;)Ljava/lang/Object;
/* 1029:     */     //   109: checkcast 92	org/boon/primitive/CharBuf
/* 1030:     */     //   112: areturn
/* 1031:     */     // Line number table:
/* 1032:     */     //   Java source line #382	-> byte code offset #0
/* 1033:     */     //   Java source line #383	-> byte code offset #13
/* 1034:     */     //   Java source line #384	-> byte code offset #20
/* 1035:     */     //   Java source line #382	-> byte code offset #55
/* 1036:     */     //   Java source line #384	-> byte code offset #64
/* 1037:     */     //   Java source line #385	-> byte code offset #102
/* 1038:     */     // Local variable table:
/* 1039:     */     //   start	length	slot	name	signature
/* 1040:     */     //   0	113	0	inputStream	InputStream
/* 1041:     */     //   0	113	1	charBuf	CharBuf
/* 1042:     */     //   0	113	2	charset	Charset
/* 1043:     */     //   9	86	3	reader	Reader
/* 1044:     */     //   101	5	3	ex	Exception
/* 1045:     */     //   11	74	4	localThrowable2	Throwable
/* 1046:     */     //   55	7	5	localThrowable1	Throwable
/* 1047:     */     //   55	7	5	localThrowable3	Throwable
/* 1048:     */     //   36	5	6	x2	Throwable
/* 1049:     */     //   64	35	7	localObject	Object
/* 1050:     */     //   82	5	8	x2	Throwable
/* 1051:     */     // Exception table:
/* 1052:     */     //   from	to	target	type
/* 1053:     */     //   29	33	36	java/lang/Throwable
/* 1054:     */     //   13	20	55	java/lang/Throwable
/* 1055:     */     //   13	20	64	finally
/* 1056:     */     //   55	66	64	finally
/* 1057:     */     //   75	79	82	java/lang/Throwable
/* 1058:     */     //   0	52	101	java/lang/Exception
/* 1059:     */     //   55	101	101	java/lang/Exception
/* 1060:     */   }
/* 1061:     */   
/* 1062:     */   /* Error */
/* 1063:     */   public static CharBuf read(InputStream inputStream, CharBuf charBuf, Charset charset, int bufSize, char[] copyBuf)
/* 1064:     */   {
/* 1065:     */     // Byte code:
/* 1066:     */     //   0: new 84	java/io/InputStreamReader
/* 1067:     */     //   3: dup
/* 1068:     */     //   4: aload_0
/* 1069:     */     //   5: aload_2
/* 1070:     */     //   6: invokespecial 85	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/nio/charset/Charset;)V
/* 1071:     */     //   9: astore 5
/* 1072:     */     //   11: aconst_null
/* 1073:     */     //   12: astore 6
/* 1074:     */     //   14: aload 5
/* 1075:     */     //   16: aload_1
/* 1076:     */     //   17: iload_3
/* 1077:     */     //   18: aload 4
/* 1078:     */     //   20: invokestatic 93	org/boon/IO:read	(Ljava/io/Reader;Lorg/boon/primitive/CharBuf;I[C)Lorg/boon/primitive/CharBuf;
/* 1079:     */     //   23: astore 7
/* 1080:     */     //   25: aload 5
/* 1081:     */     //   27: ifnull +33 -> 60
/* 1082:     */     //   30: aload 6
/* 1083:     */     //   32: ifnull +23 -> 55
/* 1084:     */     //   35: aload 5
/* 1085:     */     //   37: invokevirtual 87	java/io/Reader:close	()V
/* 1086:     */     //   40: goto +20 -> 60
/* 1087:     */     //   43: astore 8
/* 1088:     */     //   45: aload 6
/* 1089:     */     //   47: aload 8
/* 1090:     */     //   49: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/* 1091:     */     //   52: goto +8 -> 60
/* 1092:     */     //   55: aload 5
/* 1093:     */     //   57: invokevirtual 87	java/io/Reader:close	()V
/* 1094:     */     //   60: aload 7
/* 1095:     */     //   62: areturn
/* 1096:     */     //   63: astore 7
/* 1097:     */     //   65: aload 7
/* 1098:     */     //   67: astore 6
/* 1099:     */     //   69: aload 7
/* 1100:     */     //   71: athrow
/* 1101:     */     //   72: astore 9
/* 1102:     */     //   74: aload 5
/* 1103:     */     //   76: ifnull +33 -> 109
/* 1104:     */     //   79: aload 6
/* 1105:     */     //   81: ifnull +23 -> 104
/* 1106:     */     //   84: aload 5
/* 1107:     */     //   86: invokevirtual 87	java/io/Reader:close	()V
/* 1108:     */     //   89: goto +20 -> 109
/* 1109:     */     //   92: astore 10
/* 1110:     */     //   94: aload 6
/* 1111:     */     //   96: aload 10
/* 1112:     */     //   98: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/* 1113:     */     //   101: goto +8 -> 109
/* 1114:     */     //   104: aload 5
/* 1115:     */     //   106: invokevirtual 87	java/io/Reader:close	()V
/* 1116:     */     //   109: aload 9
/* 1117:     */     //   111: athrow
/* 1118:     */     //   112: astore 5
/* 1119:     */     //   114: ldc_w 92
/* 1120:     */     //   117: aload 5
/* 1121:     */     //   119: invokestatic 65	org/boon/Exceptions:handle	(Ljava/lang/Class;Ljava/lang/Exception;)Ljava/lang/Object;
/* 1122:     */     //   122: checkcast 92	org/boon/primitive/CharBuf
/* 1123:     */     //   125: areturn
/* 1124:     */     // Line number table:
/* 1125:     */     //   Java source line #393	-> byte code offset #0
/* 1126:     */     //   Java source line #394	-> byte code offset #14
/* 1127:     */     //   Java source line #395	-> byte code offset #25
/* 1128:     */     //   Java source line #393	-> byte code offset #63
/* 1129:     */     //   Java source line #395	-> byte code offset #72
/* 1130:     */     //   Java source line #396	-> byte code offset #114
/* 1131:     */     // Local variable table:
/* 1132:     */     //   start	length	slot	name	signature
/* 1133:     */     //   0	126	0	inputStream	InputStream
/* 1134:     */     //   0	126	1	charBuf	CharBuf
/* 1135:     */     //   0	126	2	charset	Charset
/* 1136:     */     //   0	126	3	bufSize	int
/* 1137:     */     //   0	126	4	copyBuf	char[]
/* 1138:     */     //   9	96	5	reader	Reader
/* 1139:     */     //   112	6	5	ex	Exception
/* 1140:     */     //   12	83	6	localThrowable2	Throwable
/* 1141:     */     //   63	7	7	localThrowable1	Throwable
/* 1142:     */     //   63	7	7	localThrowable3	Throwable
/* 1143:     */     //   43	5	8	x2	Throwable
/* 1144:     */     //   72	38	9	localObject	Object
/* 1145:     */     //   92	5	10	x2	Throwable
/* 1146:     */     // Exception table:
/* 1147:     */     //   from	to	target	type
/* 1148:     */     //   35	40	43	java/lang/Throwable
/* 1149:     */     //   14	25	63	java/lang/Throwable
/* 1150:     */     //   14	25	72	finally
/* 1151:     */     //   63	74	72	finally
/* 1152:     */     //   84	89	92	java/lang/Throwable
/* 1153:     */     //   0	60	112	java/lang/Exception
/* 1154:     */     //   63	112	112	java/lang/Exception
/* 1155:     */   }
/* 1156:     */   
/* 1157:     */   public static byte[] input(String fileName)
/* 1158:     */   {
/* 1159:     */     try
/* 1160:     */     {
/* 1161: 403 */       return input(Files.newInputStream(path(fileName), new OpenOption[0]));
/* 1162:     */     }
/* 1163:     */     catch (IOException e)
/* 1164:     */     {
/* 1165: 405 */       return (byte[])Exceptions.handle([B.class, e);
/* 1166:     */     }
/* 1167:     */   }
/* 1168:     */   
/* 1169:     */   /* Error */
/* 1170:     */   public static byte[] input(InputStream inputStream)
/* 1171:     */   {
/* 1172:     */     // Byte code:
/* 1173:     */     //   0: aload_0
/* 1174:     */     //   1: astore_1
/* 1175:     */     //   2: aconst_null
/* 1176:     */     //   3: astore_2
/* 1177:     */     //   4: sipush 4096
/* 1178:     */     //   7: invokestatic 98	org/boon/primitive/ByteBuf:create	(I)Lorg/boon/primitive/ByteBuf;
/* 1179:     */     //   10: astore_3
/* 1180:     */     //   11: sipush 4096
/* 1181:     */     //   14: newarray byte
/* 1182:     */     //   16: astore 4
/* 1183:     */     //   18: bipush 254
/* 1184:     */     //   20: istore 5
/* 1185:     */     //   22: iload 5
/* 1186:     */     //   24: iconst_m1
/* 1187:     */     //   25: if_icmpeq +46 -> 71
/* 1188:     */     //   28: aload_0
/* 1189:     */     //   29: aload 4
/* 1190:     */     //   31: invokevirtual 99	java/io/InputStream:read	([B)I
/* 1191:     */     //   34: istore 5
/* 1192:     */     //   36: iload 5
/* 1193:     */     //   38: sipush 4096
/* 1194:     */     //   41: if_icmpne +13 -> 54
/* 1195:     */     //   44: aload_3
/* 1196:     */     //   45: aload 4
/* 1197:     */     //   47: invokevirtual 100	org/boon/primitive/ByteBuf:add	([B)Lorg/boon/primitive/ByteBuf;
/* 1198:     */     //   50: pop
/* 1199:     */     //   51: goto -29 -> 22
/* 1200:     */     //   54: iload 5
/* 1201:     */     //   56: ifle -34 -> 22
/* 1202:     */     //   59: aload_3
/* 1203:     */     //   60: aload 4
/* 1204:     */     //   62: iload 5
/* 1205:     */     //   64: invokevirtual 101	org/boon/primitive/ByteBuf:add	([BI)Lorg/boon/primitive/ByteBuf;
/* 1206:     */     //   67: pop
/* 1207:     */     //   68: goto -46 -> 22
/* 1208:     */     //   71: aload_3
/* 1209:     */     //   72: invokevirtual 102	org/boon/primitive/ByteBuf:toBytes	()[B
/* 1210:     */     //   75: astore 6
/* 1211:     */     //   77: aload_1
/* 1212:     */     //   78: ifnull +29 -> 107
/* 1213:     */     //   81: aload_2
/* 1214:     */     //   82: ifnull +21 -> 103
/* 1215:     */     //   85: aload_1
/* 1216:     */     //   86: invokevirtual 103	java/io/InputStream:close	()V
/* 1217:     */     //   89: goto +18 -> 107
/* 1218:     */     //   92: astore 7
/* 1219:     */     //   94: aload_2
/* 1220:     */     //   95: aload 7
/* 1221:     */     //   97: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/* 1222:     */     //   100: goto +7 -> 107
/* 1223:     */     //   103: aload_1
/* 1224:     */     //   104: invokevirtual 103	java/io/InputStream:close	()V
/* 1225:     */     //   107: aload 6
/* 1226:     */     //   109: areturn
/* 1227:     */     //   110: astore_3
/* 1228:     */     //   111: aload_3
/* 1229:     */     //   112: astore_2
/* 1230:     */     //   113: aload_3
/* 1231:     */     //   114: athrow
/* 1232:     */     //   115: astore 8
/* 1233:     */     //   117: aload_1
/* 1234:     */     //   118: ifnull +29 -> 147
/* 1235:     */     //   121: aload_2
/* 1236:     */     //   122: ifnull +21 -> 143
/* 1237:     */     //   125: aload_1
/* 1238:     */     //   126: invokevirtual 103	java/io/InputStream:close	()V
/* 1239:     */     //   129: goto +18 -> 147
/* 1240:     */     //   132: astore 9
/* 1241:     */     //   134: aload_2
/* 1242:     */     //   135: aload 9
/* 1243:     */     //   137: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/* 1244:     */     //   140: goto +7 -> 147
/* 1245:     */     //   143: aload_1
/* 1246:     */     //   144: invokevirtual 103	java/io/InputStream:close	()V
/* 1247:     */     //   147: aload 8
/* 1248:     */     //   149: athrow
/* 1249:     */     //   150: astore_1
/* 1250:     */     //   151: ldc_w 97
/* 1251:     */     //   154: aload_1
/* 1252:     */     //   155: invokestatic 65	org/boon/Exceptions:handle	(Ljava/lang/Class;Ljava/lang/Exception;)Ljava/lang/Object;
/* 1253:     */     //   158: checkcast 97	[B
/* 1254:     */     //   161: areturn
/* 1255:     */     // Line number table:
/* 1256:     */     //   Java source line #412	-> byte code offset #0
/* 1257:     */     //   Java source line #414	-> byte code offset #4
/* 1258:     */     //   Java source line #415	-> byte code offset #11
/* 1259:     */     //   Java source line #417	-> byte code offset #18
/* 1260:     */     //   Java source line #420	-> byte code offset #22
/* 1261:     */     //   Java source line #422	-> byte code offset #28
/* 1262:     */     //   Java source line #424	-> byte code offset #36
/* 1263:     */     //   Java source line #425	-> byte code offset #44
/* 1264:     */     //   Java source line #426	-> byte code offset #54
/* 1265:     */     //   Java source line #427	-> byte code offset #59
/* 1266:     */     //   Java source line #430	-> byte code offset #71
/* 1267:     */     //   Java source line #431	-> byte code offset #77
/* 1268:     */     //   Java source line #412	-> byte code offset #110
/* 1269:     */     //   Java source line #431	-> byte code offset #115
/* 1270:     */     //   Java source line #432	-> byte code offset #151
/* 1271:     */     // Local variable table:
/* 1272:     */     //   start	length	slot	name	signature
/* 1273:     */     //   0	162	0	inputStream	InputStream
/* 1274:     */     //   1	143	1	is	InputStream
/* 1275:     */     //   150	5	1	ex	Exception
/* 1276:     */     //   3	132	2	localThrowable2	Throwable
/* 1277:     */     //   10	62	3	buf	org.boon.primitive.ByteBuf
/* 1278:     */     //   110	4	3	localThrowable1	Throwable
/* 1279:     */     //   16	45	4	bytes	byte[]
/* 1280:     */     //   20	43	5	read	int
/* 1281:     */     //   92	4	7	x2	Throwable
/* 1282:     */     //   115	33	8	localObject	Object
/* 1283:     */     //   132	4	9	x2	Throwable
/* 1284:     */     // Exception table:
/* 1285:     */     //   from	to	target	type
/* 1286:     */     //   85	89	92	java/lang/Throwable
/* 1287:     */     //   4	77	110	java/lang/Throwable
/* 1288:     */     //   4	77	115	finally
/* 1289:     */     //   110	117	115	finally
/* 1290:     */     //   125	129	132	java/lang/Throwable
/* 1291:     */     //   0	107	150	java/lang/Exception
/* 1292:     */     //   110	150	150	java/lang/Exception
/* 1293:     */   }
/* 1294:     */   
/* 1295:     */   public static long copyLarge(Reader reader, Writer writer)
/* 1296:     */   {
/* 1297: 438 */     return copyLarge(reader, writer, null);
/* 1298:     */   }
/* 1299:     */   
/* 1300:     */   public static long copyLarge(Reader reader, Writer writer, char[] buffer)
/* 1301:     */   {
/* 1302: 442 */     long count = 0L;
/* 1303: 445 */     if (buffer == null) {
/* 1304: 446 */       buffer = new char[4096];
/* 1305:     */     }
/* 1306:     */     try
/* 1307:     */     {
/* 1308:     */       int n;
/* 1309: 449 */       while (-1 != (n = reader.read(buffer)))
/* 1310:     */       {
/* 1311: 450 */         writer.write(buffer, 0, n);
/* 1312: 451 */         count += n;
/* 1313:     */       }
/* 1314:     */     }
/* 1315:     */     catch (IOException e)
/* 1316:     */     {
/* 1317: 454 */       Exceptions.handle(e);
/* 1318:     */     }
/* 1319: 456 */     return count;
/* 1320:     */   }
/* 1321:     */   
/* 1322:     */   public static String read(Reader input)
/* 1323:     */   {
/* 1324:     */     try
/* 1325:     */     {
/* 1326: 463 */       CharBuf sw = CharBuf.create(4096);
/* 1327: 464 */       copy(input, sw);
/* 1328: 465 */       return sw.toString();
/* 1329:     */     }
/* 1330:     */     finally
/* 1331:     */     {
/* 1332:     */       try
/* 1333:     */       {
/* 1334: 469 */         input.close();
/* 1335:     */       }
/* 1336:     */       catch (IOException e)
/* 1337:     */       {
/* 1338: 471 */         Exceptions.handle(e);
/* 1339:     */       }
/* 1340:     */     }
/* 1341:     */   }
/* 1342:     */   
/* 1343:     */   public static CharBuf read(Reader input, CharBuf charBuf, int bufSize, char[] copyBuffer)
/* 1344:     */   {
/* 1345: 478 */     if (charBuf == null) {
/* 1346: 479 */       charBuf = CharBuf.create(bufSize);
/* 1347:     */     } else {
/* 1348: 481 */       charBuf.readForRecycle();
/* 1349:     */     }
/* 1350:     */     try
/* 1351:     */     {
/* 1352: 487 */       char[] buffer = charBuf.toCharArray();
/* 1353: 488 */       int size = input.read(buffer);
/* 1354: 489 */       if (size != -1) {
/* 1355: 490 */         charBuf._len(size);
/* 1356:     */       }
/* 1357: 492 */       if (size < buffer.length) {
/* 1358: 493 */         return charBuf;
/* 1359:     */       }
/* 1360: 496 */       copy(input, charBuf, copyBuffer);
/* 1361:     */       
/* 1362:     */ 
/* 1363:     */ 
/* 1364:     */ 
/* 1365:     */ 
/* 1366:     */ 
/* 1367:     */ 
/* 1368:     */ 
/* 1369:     */ 
/* 1370:     */ 
/* 1371:     */ 
/* 1372: 508 */       return charBuf;
/* 1373:     */     }
/* 1374:     */     catch (IOException e)
/* 1375:     */     {
/* 1376: 499 */       Exceptions.handle(e);
/* 1377:     */     }
/* 1378:     */     finally
/* 1379:     */     {
/* 1380:     */       try
/* 1381:     */       {
/* 1382: 502 */         input.close();
/* 1383:     */       }
/* 1384:     */       catch (IOException e)
/* 1385:     */       {
/* 1386: 504 */         Exceptions.handle(e);
/* 1387:     */       }
/* 1388:     */     }
/* 1389:     */   }
/* 1390:     */   
/* 1391:     */   public static CharBuf read(Reader input, CharBuf charBuf)
/* 1392:     */   {
/* 1393: 514 */     return read(input, charBuf, 2048, null);
/* 1394:     */   }
/* 1395:     */   
/* 1396:     */   public static char[] readCharBuffer(Reader input)
/* 1397:     */   {
/* 1398:     */     try
/* 1399:     */     {
/* 1400: 520 */       CharBuf sw = CharBuf.create(4096);
/* 1401: 521 */       copy(input, sw);
/* 1402: 522 */       return sw.toCharArray();
/* 1403:     */     }
/* 1404:     */     finally
/* 1405:     */     {
/* 1406:     */       try
/* 1407:     */       {
/* 1408: 526 */         input.close();
/* 1409:     */       }
/* 1410:     */       catch (IOException e)
/* 1411:     */       {
/* 1412: 528 */         Exceptions.handle(e);
/* 1413:     */       }
/* 1414:     */     }
/* 1415:     */   }
/* 1416:     */   
/* 1417:     */   public static int copy(Reader input, Writer output)
/* 1418:     */   {
/* 1419: 535 */     long count = copyLarge(input, output);
/* 1420: 536 */     if (count > 2147483647L) {
/* 1421: 537 */       return -1;
/* 1422:     */     }
/* 1423: 539 */     return (int)count;
/* 1424:     */   }
/* 1425:     */   
/* 1426:     */   public static int copy(Reader input, Writer output, char[] copyBuf)
/* 1427:     */   {
/* 1428: 543 */     long count = copyLarge(input, output, copyBuf);
/* 1429: 544 */     if (count > 2147483647L) {
/* 1430: 545 */       return -1;
/* 1431:     */     }
/* 1432: 547 */     return (int)count;
/* 1433:     */   }
/* 1434:     */   
/* 1435:     */   public static char[] readCharBuffer(Reader reader, int size)
/* 1436:     */   {
/* 1437: 553 */     char[] buffer = new char[size];
/* 1438:     */     try
/* 1439:     */     {
/* 1440: 555 */       Reader r = reader;Throwable localThrowable2 = null;
/* 1441:     */       try
/* 1442:     */       {
/* 1443: 557 */         reader.read(buffer);
/* 1444:     */       }
/* 1445:     */       catch (Throwable localThrowable1)
/* 1446:     */       {
/* 1447: 555 */         localThrowable2 = localThrowable1;throw localThrowable1;
/* 1448:     */       }
/* 1449:     */       finally
/* 1450:     */       {
/* 1451: 560 */         if (r != null) {
/* 1452: 560 */           if (localThrowable2 != null) {
/* 1453:     */             try
/* 1454:     */             {
/* 1455: 560 */               r.close();
/* 1456:     */             }
/* 1457:     */             catch (Throwable x2)
/* 1458:     */             {
/* 1459: 560 */               localThrowable2.addSuppressed(x2);
/* 1460:     */             }
/* 1461:     */           } else {
/* 1462: 560 */             r.close();
/* 1463:     */           }
/* 1464:     */         }
/* 1465:     */       }
/* 1466:     */     }
/* 1467:     */     catch (Exception ex)
/* 1468:     */     {
/* 1469: 561 */       return (char[])Exceptions.handle([C.class, ex);
/* 1470:     */     }
/* 1471: 564 */     return buffer;
/* 1472:     */   }
/* 1473:     */   
/* 1474:     */   /* Error */
/* 1475:     */   public static String read(File file)
/* 1476:     */   {
/* 1477:     */     // Byte code:
/* 1478:     */     //   0: new 118	java/io/FileReader
/* 1479:     */     //   3: dup
/* 1480:     */     //   4: aload_0
/* 1481:     */     //   5: invokespecial 119	java/io/FileReader:<init>	(Ljava/io/File;)V
/* 1482:     */     //   8: astore_1
/* 1483:     */     //   9: aconst_null
/* 1484:     */     //   10: astore_2
/* 1485:     */     //   11: aload_1
/* 1486:     */     //   12: invokestatic 86	org/boon/IO:read	(Ljava/io/Reader;)Ljava/lang/String;
/* 1487:     */     //   15: astore_3
/* 1488:     */     //   16: aload_1
/* 1489:     */     //   17: ifnull +29 -> 46
/* 1490:     */     //   20: aload_2
/* 1491:     */     //   21: ifnull +21 -> 42
/* 1492:     */     //   24: aload_1
/* 1493:     */     //   25: invokevirtual 87	java/io/Reader:close	()V
/* 1494:     */     //   28: goto +18 -> 46
/* 1495:     */     //   31: astore 4
/* 1496:     */     //   33: aload_2
/* 1497:     */     //   34: aload 4
/* 1498:     */     //   36: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/* 1499:     */     //   39: goto +7 -> 46
/* 1500:     */     //   42: aload_1
/* 1501:     */     //   43: invokevirtual 87	java/io/Reader:close	()V
/* 1502:     */     //   46: aload_3
/* 1503:     */     //   47: areturn
/* 1504:     */     //   48: astore_3
/* 1505:     */     //   49: aload_3
/* 1506:     */     //   50: astore_2
/* 1507:     */     //   51: aload_3
/* 1508:     */     //   52: athrow
/* 1509:     */     //   53: astore 5
/* 1510:     */     //   55: aload_1
/* 1511:     */     //   56: ifnull +29 -> 85
/* 1512:     */     //   59: aload_2
/* 1513:     */     //   60: ifnull +21 -> 81
/* 1514:     */     //   63: aload_1
/* 1515:     */     //   64: invokevirtual 87	java/io/Reader:close	()V
/* 1516:     */     //   67: goto +18 -> 85
/* 1517:     */     //   70: astore 6
/* 1518:     */     //   72: aload_2
/* 1519:     */     //   73: aload 6
/* 1520:     */     //   75: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/* 1521:     */     //   78: goto +7 -> 85
/* 1522:     */     //   81: aload_1
/* 1523:     */     //   82: invokevirtual 87	java/io/Reader:close	()V
/* 1524:     */     //   85: aload 5
/* 1525:     */     //   87: athrow
/* 1526:     */     //   88: astore_1
/* 1527:     */     //   89: ldc_w 19
/* 1528:     */     //   92: aload_1
/* 1529:     */     //   93: invokestatic 65	org/boon/Exceptions:handle	(Ljava/lang/Class;Ljava/lang/Exception;)Ljava/lang/Object;
/* 1530:     */     //   96: checkcast 19	java/lang/String
/* 1531:     */     //   99: areturn
/* 1532:     */     // Line number table:
/* 1533:     */     //   Java source line #570	-> byte code offset #0
/* 1534:     */     //   Java source line #571	-> byte code offset #11
/* 1535:     */     //   Java source line #572	-> byte code offset #16
/* 1536:     */     //   Java source line #570	-> byte code offset #48
/* 1537:     */     //   Java source line #572	-> byte code offset #53
/* 1538:     */     //   Java source line #573	-> byte code offset #89
/* 1539:     */     // Local variable table:
/* 1540:     */     //   start	length	slot	name	signature
/* 1541:     */     //   0	100	0	file	File
/* 1542:     */     //   8	74	1	reader	Reader
/* 1543:     */     //   88	5	1	ex	Exception
/* 1544:     */     //   10	63	2	localThrowable2	Throwable
/* 1545:     */     //   48	4	3	localThrowable1	Throwable
/* 1546:     */     //   48	4	3	localThrowable3	Throwable
/* 1547:     */     //   31	4	4	x2	Throwable
/* 1548:     */     //   53	33	5	localObject	Object
/* 1549:     */     //   70	4	6	x2	Throwable
/* 1550:     */     // Exception table:
/* 1551:     */     //   from	to	target	type
/* 1552:     */     //   24	28	31	java/lang/Throwable
/* 1553:     */     //   11	16	48	java/lang/Throwable
/* 1554:     */     //   11	16	53	finally
/* 1555:     */     //   48	55	53	finally
/* 1556:     */     //   63	67	70	java/lang/Throwable
/* 1557:     */     //   0	46	88	java/lang/Exception
/* 1558:     */     //   48	88	88	java/lang/Exception
/* 1559:     */   }
/* 1560:     */   
/* 1561:     */   /* Error */
/* 1562:     */   public static List<String> readLines(Reader reader)
/* 1563:     */   {
/* 1564:     */     // Byte code:
/* 1565:     */     //   0: new 120	java/io/BufferedReader
/* 1566:     */     //   3: dup
/* 1567:     */     //   4: aload_0
/* 1568:     */     //   5: invokespecial 121	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
/* 1569:     */     //   8: astore_1
/* 1570:     */     //   9: aconst_null
/* 1571:     */     //   10: astore_2
/* 1572:     */     //   11: aload_1
/* 1573:     */     //   12: invokestatic 122	org/boon/IO:readLines	(Ljava/io/BufferedReader;)Ljava/util/List;
/* 1574:     */     //   15: astore_3
/* 1575:     */     //   16: aload_1
/* 1576:     */     //   17: ifnull +29 -> 46
/* 1577:     */     //   20: aload_2
/* 1578:     */     //   21: ifnull +21 -> 42
/* 1579:     */     //   24: aload_1
/* 1580:     */     //   25: invokevirtual 123	java/io/BufferedReader:close	()V
/* 1581:     */     //   28: goto +18 -> 46
/* 1582:     */     //   31: astore 4
/* 1583:     */     //   33: aload_2
/* 1584:     */     //   34: aload 4
/* 1585:     */     //   36: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/* 1586:     */     //   39: goto +7 -> 46
/* 1587:     */     //   42: aload_1
/* 1588:     */     //   43: invokevirtual 123	java/io/BufferedReader:close	()V
/* 1589:     */     //   46: aload_3
/* 1590:     */     //   47: areturn
/* 1591:     */     //   48: astore_3
/* 1592:     */     //   49: aload_3
/* 1593:     */     //   50: astore_2
/* 1594:     */     //   51: aload_3
/* 1595:     */     //   52: athrow
/* 1596:     */     //   53: astore 5
/* 1597:     */     //   55: aload_1
/* 1598:     */     //   56: ifnull +29 -> 85
/* 1599:     */     //   59: aload_2
/* 1600:     */     //   60: ifnull +21 -> 81
/* 1601:     */     //   63: aload_1
/* 1602:     */     //   64: invokevirtual 123	java/io/BufferedReader:close	()V
/* 1603:     */     //   67: goto +18 -> 85
/* 1604:     */     //   70: astore 6
/* 1605:     */     //   72: aload_2
/* 1606:     */     //   73: aload 6
/* 1607:     */     //   75: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/* 1608:     */     //   78: goto +7 -> 85
/* 1609:     */     //   81: aload_1
/* 1610:     */     //   82: invokevirtual 123	java/io/BufferedReader:close	()V
/* 1611:     */     //   85: aload 5
/* 1612:     */     //   87: athrow
/* 1613:     */     //   88: astore_1
/* 1614:     */     //   89: ldc_w 64
/* 1615:     */     //   92: aload_1
/* 1616:     */     //   93: invokestatic 65	org/boon/Exceptions:handle	(Ljava/lang/Class;Ljava/lang/Exception;)Ljava/lang/Object;
/* 1617:     */     //   96: checkcast 64	java/util/List
/* 1618:     */     //   99: areturn
/* 1619:     */     // Line number table:
/* 1620:     */     //   Java source line #579	-> byte code offset #0
/* 1621:     */     //   Java source line #581	-> byte code offset #11
/* 1622:     */     //   Java source line #583	-> byte code offset #16
/* 1623:     */     //   Java source line #579	-> byte code offset #48
/* 1624:     */     //   Java source line #583	-> byte code offset #53
/* 1625:     */     //   Java source line #585	-> byte code offset #89
/* 1626:     */     // Local variable table:
/* 1627:     */     //   start	length	slot	name	signature
/* 1628:     */     //   0	100	0	reader	Reader
/* 1629:     */     //   8	74	1	bufferedReader	BufferedReader
/* 1630:     */     //   88	5	1	ex	Exception
/* 1631:     */     //   10	63	2	localThrowable2	Throwable
/* 1632:     */     //   48	4	3	localThrowable1	Throwable
/* 1633:     */     //   48	4	3	localThrowable3	Throwable
/* 1634:     */     //   31	4	4	x2	Throwable
/* 1635:     */     //   53	33	5	localObject	Object
/* 1636:     */     //   70	4	6	x2	Throwable
/* 1637:     */     // Exception table:
/* 1638:     */     //   from	to	target	type
/* 1639:     */     //   24	28	31	java/lang/Throwable
/* 1640:     */     //   11	16	48	java/lang/Throwable
/* 1641:     */     //   11	16	53	finally
/* 1642:     */     //   48	55	53	finally
/* 1643:     */     //   63	67	70	java/lang/Throwable
/* 1644:     */     //   0	46	88	java/lang/Exception
/* 1645:     */     //   48	88	88	java/lang/Exception
/* 1646:     */   }
/* 1647:     */   
/* 1648:     */   public static void eachLine(Reader reader, EachLine eachLine)
/* 1649:     */   {
/* 1650:     */     try
/* 1651:     */     {
/* 1652: 591 */       BufferedReader bufferedReader = new BufferedReader(reader);Throwable localThrowable2 = null;
/* 1653:     */       try
/* 1654:     */       {
/* 1655: 593 */         eachLine(bufferedReader, eachLine);
/* 1656:     */       }
/* 1657:     */       catch (Throwable localThrowable1)
/* 1658:     */       {
/* 1659: 591 */         localThrowable2 = localThrowable1;throw localThrowable1;
/* 1660:     */       }
/* 1661:     */       finally
/* 1662:     */       {
/* 1663: 595 */         if (bufferedReader != null) {
/* 1664: 595 */           if (localThrowable2 != null) {
/* 1665:     */             try
/* 1666:     */             {
/* 1667: 595 */               bufferedReader.close();
/* 1668:     */             }
/* 1669:     */             catch (Throwable x2)
/* 1670:     */             {
/* 1671: 595 */               localThrowable2.addSuppressed(x2);
/* 1672:     */             }
/* 1673:     */           } else {
/* 1674: 595 */             bufferedReader.close();
/* 1675:     */           }
/* 1676:     */         }
/* 1677:     */       }
/* 1678:     */     }
/* 1679:     */     catch (Exception ex)
/* 1680:     */     {
/* 1681: 597 */       Exceptions.handle(List.class, ex);
/* 1682:     */     }
/* 1683:     */   }
/* 1684:     */   
/* 1685:     */   /* Error */
/* 1686:     */   public static List<String> readLines(InputStream is)
/* 1687:     */   {
/* 1688:     */     // Byte code:
/* 1689:     */     //   0: new 84	java/io/InputStreamReader
/* 1690:     */     //   3: dup
/* 1691:     */     //   4: aload_0
/* 1692:     */     //   5: getstatic 80	org/boon/IO:DEFAULT_CHARSET	Ljava/nio/charset/Charset;
/* 1693:     */     //   8: invokespecial 85	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/nio/charset/Charset;)V
/* 1694:     */     //   11: astore_1
/* 1695:     */     //   12: aconst_null
/* 1696:     */     //   13: astore_2
/* 1697:     */     //   14: aload_1
/* 1698:     */     //   15: invokestatic 125	org/boon/IO:readLines	(Ljava/io/Reader;)Ljava/util/List;
/* 1699:     */     //   18: astore_3
/* 1700:     */     //   19: aload_1
/* 1701:     */     //   20: ifnull +29 -> 49
/* 1702:     */     //   23: aload_2
/* 1703:     */     //   24: ifnull +21 -> 45
/* 1704:     */     //   27: aload_1
/* 1705:     */     //   28: invokevirtual 87	java/io/Reader:close	()V
/* 1706:     */     //   31: goto +18 -> 49
/* 1707:     */     //   34: astore 4
/* 1708:     */     //   36: aload_2
/* 1709:     */     //   37: aload 4
/* 1710:     */     //   39: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/* 1711:     */     //   42: goto +7 -> 49
/* 1712:     */     //   45: aload_1
/* 1713:     */     //   46: invokevirtual 87	java/io/Reader:close	()V
/* 1714:     */     //   49: aload_3
/* 1715:     */     //   50: areturn
/* 1716:     */     //   51: astore_3
/* 1717:     */     //   52: aload_3
/* 1718:     */     //   53: astore_2
/* 1719:     */     //   54: aload_3
/* 1720:     */     //   55: athrow
/* 1721:     */     //   56: astore 5
/* 1722:     */     //   58: aload_1
/* 1723:     */     //   59: ifnull +29 -> 88
/* 1724:     */     //   62: aload_2
/* 1725:     */     //   63: ifnull +21 -> 84
/* 1726:     */     //   66: aload_1
/* 1727:     */     //   67: invokevirtual 87	java/io/Reader:close	()V
/* 1728:     */     //   70: goto +18 -> 88
/* 1729:     */     //   73: astore 6
/* 1730:     */     //   75: aload_2
/* 1731:     */     //   76: aload 6
/* 1732:     */     //   78: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/* 1733:     */     //   81: goto +7 -> 88
/* 1734:     */     //   84: aload_1
/* 1735:     */     //   85: invokevirtual 87	java/io/Reader:close	()V
/* 1736:     */     //   88: aload 5
/* 1737:     */     //   90: athrow
/* 1738:     */     //   91: astore_1
/* 1739:     */     //   92: ldc_w 64
/* 1740:     */     //   95: aload_1
/* 1741:     */     //   96: invokestatic 65	org/boon/Exceptions:handle	(Ljava/lang/Class;Ljava/lang/Exception;)Ljava/lang/Object;
/* 1742:     */     //   99: checkcast 64	java/util/List
/* 1743:     */     //   102: areturn
/* 1744:     */     // Line number table:
/* 1745:     */     //   Java source line #603	-> byte code offset #0
/* 1746:     */     //   Java source line #605	-> byte code offset #14
/* 1747:     */     //   Java source line #607	-> byte code offset #19
/* 1748:     */     //   Java source line #603	-> byte code offset #51
/* 1749:     */     //   Java source line #607	-> byte code offset #56
/* 1750:     */     //   Java source line #609	-> byte code offset #92
/* 1751:     */     // Local variable table:
/* 1752:     */     //   start	length	slot	name	signature
/* 1753:     */     //   0	103	0	is	InputStream
/* 1754:     */     //   11	74	1	reader	Reader
/* 1755:     */     //   91	5	1	ex	Exception
/* 1756:     */     //   13	63	2	localThrowable2	Throwable
/* 1757:     */     //   51	4	3	localThrowable1	Throwable
/* 1758:     */     //   51	4	3	localThrowable3	Throwable
/* 1759:     */     //   34	4	4	x2	Throwable
/* 1760:     */     //   56	33	5	localObject	Object
/* 1761:     */     //   73	4	6	x2	Throwable
/* 1762:     */     // Exception table:
/* 1763:     */     //   from	to	target	type
/* 1764:     */     //   27	31	34	java/lang/Throwable
/* 1765:     */     //   14	19	51	java/lang/Throwable
/* 1766:     */     //   14	19	56	finally
/* 1767:     */     //   51	58	56	finally
/* 1768:     */     //   66	70	73	java/lang/Throwable
/* 1769:     */     //   0	49	91	java/lang/Exception
/* 1770:     */     //   51	91	91	java/lang/Exception
/* 1771:     */   }
/* 1772:     */   
/* 1773:     */   public static void eachLine(InputStream is, EachLine eachLine)
/* 1774:     */   {
/* 1775:     */     try
/* 1776:     */     {
/* 1777: 615 */       Reader reader = new InputStreamReader(is, DEFAULT_CHARSET);Throwable localThrowable2 = null;
/* 1778:     */       try
/* 1779:     */       {
/* 1780: 617 */         eachLine(reader, eachLine);
/* 1781:     */       }
/* 1782:     */       catch (Throwable localThrowable1)
/* 1783:     */       {
/* 1784: 615 */         localThrowable2 = localThrowable1;throw localThrowable1;
/* 1785:     */       }
/* 1786:     */       finally
/* 1787:     */       {
/* 1788: 619 */         if (reader != null) {
/* 1789: 619 */           if (localThrowable2 != null) {
/* 1790:     */             try
/* 1791:     */             {
/* 1792: 619 */               reader.close();
/* 1793:     */             }
/* 1794:     */             catch (Throwable x2)
/* 1795:     */             {
/* 1796: 619 */               localThrowable2.addSuppressed(x2);
/* 1797:     */             }
/* 1798:     */           } else {
/* 1799: 619 */             reader.close();
/* 1800:     */           }
/* 1801:     */         }
/* 1802:     */       }
/* 1803:     */     }
/* 1804:     */     catch (Exception ex)
/* 1805:     */     {
/* 1806: 621 */       Exceptions.handle(ex);
/* 1807:     */     }
/* 1808:     */   }
/* 1809:     */   
/* 1810:     */   public static List<String> readLines(BufferedReader reader)
/* 1811:     */   {
/* 1812: 627 */     List<String> lines = new ArrayList(80);
/* 1813:     */     try
/* 1814:     */     {
/* 1815: 629 */       BufferedReader bufferedReader = reader;Throwable localThrowable2 = null;
/* 1816:     */       try
/* 1817:     */       {
/* 1818:     */         String line;
/* 1819: 633 */         while ((line = bufferedReader.readLine()) != null) {
/* 1820: 634 */           lines.add(line);
/* 1821:     */         }
/* 1822:     */       }
/* 1823:     */       catch (Throwable localThrowable1)
/* 1824:     */       {
/* 1825: 629 */         localThrowable2 = localThrowable1;throw localThrowable1;
/* 1826:     */       }
/* 1827:     */       finally
/* 1828:     */       {
/* 1829: 638 */         if (bufferedReader != null) {
/* 1830: 638 */           if (localThrowable2 != null) {
/* 1831:     */             try
/* 1832:     */             {
/* 1833: 638 */               bufferedReader.close();
/* 1834:     */             }
/* 1835:     */             catch (Throwable x2)
/* 1836:     */             {
/* 1837: 638 */               localThrowable2.addSuppressed(x2);
/* 1838:     */             }
/* 1839:     */           } else {
/* 1840: 638 */             bufferedReader.close();
/* 1841:     */           }
/* 1842:     */         }
/* 1843:     */       }
/* 1844:     */     }
/* 1845:     */     catch (Exception ex)
/* 1846:     */     {
/* 1847: 640 */       return (List)Exceptions.handle(List.class, ex);
/* 1848:     */     }
/* 1849: 642 */     return lines;
/* 1850:     */   }
/* 1851:     */   
/* 1852:     */   public static void eachLine(BufferedReader reader, EachLine eachLine)
/* 1853:     */   {
/* 1854:     */     try
/* 1855:     */     {
/* 1856: 651 */       BufferedReader bufferedReader = reader;Throwable localThrowable2 = null;
/* 1857:     */       try
/* 1858:     */       {
/* 1859: 655 */         int lineNumber = 0;
/* 1860:     */         String line;
/* 1861: 657 */         while (((line = bufferedReader.readLine()) != null) && (eachLine.line(line, lineNumber++))) {}
/* 1862:     */       }
/* 1863:     */       catch (Throwable localThrowable1)
/* 1864:     */       {
/* 1865: 651 */         localThrowable2 = localThrowable1;throw localThrowable1;
/* 1866:     */       }
/* 1867:     */       finally
/* 1868:     */       {
/* 1869: 661 */         if (bufferedReader != null) {
/* 1870: 661 */           if (localThrowable2 != null) {
/* 1871:     */             try
/* 1872:     */             {
/* 1873: 661 */               bufferedReader.close();
/* 1874:     */             }
/* 1875:     */             catch (Throwable x2)
/* 1876:     */             {
/* 1877: 661 */               localThrowable2.addSuppressed(x2);
/* 1878:     */             }
/* 1879:     */           } else {
/* 1880: 661 */             bufferedReader.close();
/* 1881:     */           }
/* 1882:     */         }
/* 1883:     */       }
/* 1884:     */     }
/* 1885:     */     catch (Exception ex)
/* 1886:     */     {
/* 1887: 663 */       Exceptions.handle(ex);
/* 1888:     */     }
/* 1889:     */   }
/* 1890:     */   
/* 1891:     */   public static void eachLine(File file, EachLine eachLine)
/* 1892:     */   {
/* 1893:     */     try
/* 1894:     */     {
/* 1895: 669 */       FileReader reader = new FileReader(file);Throwable localThrowable2 = null;
/* 1896:     */       try
/* 1897:     */       {
/* 1898: 670 */         eachLine(reader, eachLine);
/* 1899:     */       }
/* 1900:     */       catch (Throwable localThrowable1)
/* 1901:     */       {
/* 1902: 669 */         localThrowable2 = localThrowable1;throw localThrowable1;
/* 1903:     */       }
/* 1904:     */       finally
/* 1905:     */       {
/* 1906: 671 */         if (reader != null) {
/* 1907: 671 */           if (localThrowable2 != null) {
/* 1908:     */             try
/* 1909:     */             {
/* 1910: 671 */               reader.close();
/* 1911:     */             }
/* 1912:     */             catch (Throwable x2)
/* 1913:     */             {
/* 1914: 671 */               localThrowable2.addSuppressed(x2);
/* 1915:     */             }
/* 1916:     */           } else {
/* 1917: 671 */             reader.close();
/* 1918:     */           }
/* 1919:     */         }
/* 1920:     */       }
/* 1921:     */     }
/* 1922:     */     catch (Exception ex)
/* 1923:     */     {
/* 1924: 672 */       Exceptions.handle(List.class, ex);
/* 1925:     */     }
/* 1926:     */   }
/* 1927:     */   
/* 1928:     */   /* Error */
/* 1929:     */   public static List<String> readLines(File file)
/* 1930:     */   {
/* 1931:     */     // Byte code:
/* 1932:     */     //   0: new 118	java/io/FileReader
/* 1933:     */     //   3: dup
/* 1934:     */     //   4: aload_0
/* 1935:     */     //   5: invokespecial 119	java/io/FileReader:<init>	(Ljava/io/File;)V
/* 1936:     */     //   8: astore_1
/* 1937:     */     //   9: aconst_null
/* 1938:     */     //   10: astore_2
/* 1939:     */     //   11: aload_1
/* 1940:     */     //   12: invokestatic 125	org/boon/IO:readLines	(Ljava/io/Reader;)Ljava/util/List;
/* 1941:     */     //   15: astore_3
/* 1942:     */     //   16: aload_1
/* 1943:     */     //   17: ifnull +29 -> 46
/* 1944:     */     //   20: aload_2
/* 1945:     */     //   21: ifnull +21 -> 42
/* 1946:     */     //   24: aload_1
/* 1947:     */     //   25: invokevirtual 130	java/io/FileReader:close	()V
/* 1948:     */     //   28: goto +18 -> 46
/* 1949:     */     //   31: astore 4
/* 1950:     */     //   33: aload_2
/* 1951:     */     //   34: aload 4
/* 1952:     */     //   36: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/* 1953:     */     //   39: goto +7 -> 46
/* 1954:     */     //   42: aload_1
/* 1955:     */     //   43: invokevirtual 130	java/io/FileReader:close	()V
/* 1956:     */     //   46: aload_3
/* 1957:     */     //   47: areturn
/* 1958:     */     //   48: astore_3
/* 1959:     */     //   49: aload_3
/* 1960:     */     //   50: astore_2
/* 1961:     */     //   51: aload_3
/* 1962:     */     //   52: athrow
/* 1963:     */     //   53: astore 5
/* 1964:     */     //   55: aload_1
/* 1965:     */     //   56: ifnull +29 -> 85
/* 1966:     */     //   59: aload_2
/* 1967:     */     //   60: ifnull +21 -> 81
/* 1968:     */     //   63: aload_1
/* 1969:     */     //   64: invokevirtual 130	java/io/FileReader:close	()V
/* 1970:     */     //   67: goto +18 -> 85
/* 1971:     */     //   70: astore 6
/* 1972:     */     //   72: aload_2
/* 1973:     */     //   73: aload 6
/* 1974:     */     //   75: invokevirtual 63	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
/* 1975:     */     //   78: goto +7 -> 85
/* 1976:     */     //   81: aload_1
/* 1977:     */     //   82: invokevirtual 130	java/io/FileReader:close	()V
/* 1978:     */     //   85: aload 5
/* 1979:     */     //   87: athrow
/* 1980:     */     //   88: astore_1
/* 1981:     */     //   89: ldc_w 64
/* 1982:     */     //   92: aload_1
/* 1983:     */     //   93: invokestatic 65	org/boon/Exceptions:handle	(Ljava/lang/Class;Ljava/lang/Exception;)Ljava/lang/Object;
/* 1984:     */     //   96: checkcast 64	java/util/List
/* 1985:     */     //   99: areturn
/* 1986:     */     // Line number table:
/* 1987:     */     //   Java source line #678	-> byte code offset #0
/* 1988:     */     //   Java source line #679	-> byte code offset #11
/* 1989:     */     //   Java source line #680	-> byte code offset #16
/* 1990:     */     //   Java source line #678	-> byte code offset #48
/* 1991:     */     //   Java source line #680	-> byte code offset #53
/* 1992:     */     //   Java source line #681	-> byte code offset #89
/* 1993:     */     // Local variable table:
/* 1994:     */     //   start	length	slot	name	signature
/* 1995:     */     //   0	100	0	file	File
/* 1996:     */     //   8	74	1	reader	FileReader
/* 1997:     */     //   88	5	1	ex	Exception
/* 1998:     */     //   10	63	2	localThrowable2	Throwable
/* 1999:     */     //   48	4	3	localThrowable1	Throwable
/* 2000:     */     //   48	4	3	localThrowable3	Throwable
/* 2001:     */     //   31	4	4	x2	Throwable
/* 2002:     */     //   53	33	5	localObject	Object
/* 2003:     */     //   70	4	6	x2	Throwable
/* 2004:     */     // Exception table:
/* 2005:     */     //   from	to	target	type
/* 2006:     */     //   24	28	31	java/lang/Throwable
/* 2007:     */     //   11	16	48	java/lang/Throwable
/* 2008:     */     //   11	16	53	finally
/* 2009:     */     //   48	55	53	finally
/* 2010:     */     //   63	67	70	java/lang/Throwable
/* 2011:     */     //   0	46	88	java/lang/Exception
/* 2012:     */     //   48	88	88	java/lang/Exception
/* 2013:     */   }
/* 2014:     */   
/* 2015:     */   public static List<String> readLines(final String location)
/* 2016:     */   {
/* 2017: 689 */     final String path = getWindowsPathIfNeeded(location);
/* 2018:     */     
/* 2019: 691 */     URI uri = createURI(path);
/* 2020:     */     
/* 2021: 693 */     (List)Exceptions.tryIt(Typ.list, new Exceptions.TrialWithReturn()
/* 2022:     */     {
/* 2023:     */       public List<String> tryIt()
/* 2024:     */         throws Exception
/* 2025:     */       {
/* 2026: 696 */         if (this.val$uri.getScheme() == null)
/* 2027:     */         {
/* 2028: 698 */           Path thePath = FileSystems.getDefault().getPath(path, new String[0]);
/* 2029: 699 */           return Files.readAllLines(thePath, IO.DEFAULT_CHARSET);
/* 2030:     */         }
/* 2031: 701 */         if (this.val$uri.getScheme().equals("file"))
/* 2032:     */         {
/* 2033: 703 */           Path thePath = FileSystems.getDefault().getPath(this.val$uri.getPath(), new String[0]);
/* 2034: 704 */           return Files.readAllLines(thePath, IO.DEFAULT_CHARSET);
/* 2035:     */         }
/* 2036: 707 */         return IO.readLines(location, this.val$uri);
/* 2037:     */       }
/* 2038:     */     });
/* 2039:     */   }
/* 2040:     */   
/* 2041:     */   public static URI createURI(String path)
/* 2042:     */   {
/* 2043: 714 */     if (!Sys.isWindows()) {
/* 2044: 715 */       return URI.create(path);
/* 2045:     */     }
/* 2046: 719 */     if ((path.contains("\\")) || (path.startsWith("C:")) || (path.startsWith("D:")))
/* 2047:     */     {
/* 2048: 720 */       String newPath = new File(path).toURI().toString();
/* 2049: 721 */       if (newPath.startsWith("file:/C:"))
/* 2050:     */       {
/* 2051: 722 */         newPath = Str.slc(newPath, 8);
/* 2052: 723 */         return URI.create(newPath);
/* 2053:     */       }
/* 2054: 725 */       return URI.create(newPath);
/* 2055:     */     }
/* 2056: 729 */     return URI.create(path);
/* 2057:     */   }
/* 2058:     */   
/* 2059:     */   public static void eachLine(final String location, final EachLine eachLine)
/* 2060:     */   {
/* 2061: 737 */     URI uri = createURI(location);
/* 2062:     */     
/* 2063: 739 */     Exceptions.tryIt(new Exceptions.Trial()
/* 2064:     */     {
/* 2065:     */       public void tryIt()
/* 2066:     */         throws Exception
/* 2067:     */       {
/* 2068: 744 */         if (this.val$uri.getScheme() == null)
/* 2069:     */         {
/* 2070: 746 */           Path thePath = FileSystems.getDefault().getPath(location, new String[0]);
/* 2071: 747 */           BufferedReader buf = Files.newBufferedReader(thePath, IO.DEFAULT_CHARSET);
/* 2072:     */           
/* 2073: 749 */           IO.eachLine(buf, eachLine);
/* 2074:     */         }
/* 2075: 751 */         else if (this.val$uri.getScheme().equals("file"))
/* 2076:     */         {
/* 2077: 754 */           Path thePath = null;
/* 2078: 756 */           if (Sys.isWindows())
/* 2079:     */           {
/* 2080: 757 */             String path = this.val$uri.toString();
/* 2081:     */             
/* 2082: 759 */             path = path.replace('/', Sys.windowsPathSeparator());
/* 2083: 760 */             if (Str.slc(path, 0, 6).equals("file:\\")) {
/* 2084: 761 */               path = Str.slc(path, 6);
/* 2085:     */             }
/* 2086: 763 */             thePath = FileSystems.getDefault().getPath(path, new String[0]);
/* 2087:     */           }
/* 2088:     */           else
/* 2089:     */           {
/* 2090: 765 */             thePath = FileSystems.getDefault().getPath(this.val$uri.getPath(), new String[0]);
/* 2091:     */           }
/* 2092: 768 */           BufferedReader buf = Files.newBufferedReader(thePath, IO.DEFAULT_CHARSET);
/* 2093:     */           
/* 2094: 770 */           IO.eachLine(buf, eachLine);
/* 2095:     */         }
/* 2096:     */         else
/* 2097:     */         {
/* 2098: 774 */           IO.eachLine(location, this.val$uri, eachLine);
/* 2099:     */         }
/* 2100:     */       }
/* 2101:     */     });
/* 2102:     */   }
/* 2103:     */   
/* 2104:     */   private static String getWindowsPathIfNeeded(String path)
/* 2105:     */   {
/* 2106: 783 */     if (Sys.isWindows())
/* 2107:     */     {
/* 2108: 785 */       if ((!path.startsWith("http")) && (!path.startsWith("classpath")) && (!path.startsWith("jar")))
/* 2109:     */       {
/* 2110: 787 */         path = path.replace('/', Sys.windowsPathSeparator());
/* 2111: 788 */         if (Str.slc(path, 0, 6).equals("file:\\")) {
/* 2112: 789 */           path = Str.slc(path, 6);
/* 2113:     */         }
/* 2114:     */       }
/* 2115: 793 */       if (path.startsWith(".\\")) {
/* 2116: 794 */         path = Str.slc(path, 2);
/* 2117:     */       }
/* 2118:     */     }
/* 2119: 797 */     return path;
/* 2120:     */   }
/* 2121:     */   
/* 2122:     */   public static String read(Path path)
/* 2123:     */   {
/* 2124: 803 */     return readPath(path);
/* 2125:     */   }
/* 2126:     */   
/* 2127:     */   public static String readPath(Path path)
/* 2128:     */   {
/* 2129: 808 */     (String)Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn()
/* 2130:     */     {
/* 2131:     */       public String tryIt()
/* 2132:     */         throws Exception
/* 2133:     */       {
/* 2134: 813 */         return IO.read(Files.newBufferedReader(this.val$path, IO.DEFAULT_CHARSET));
/* 2135:     */       }
/* 2136:     */     });
/* 2137:     */   }
/* 2138:     */   
/* 2139:     */   public static String read(String location)
/* 2140:     */   {
/* 2141: 824 */     final URI uri = createURI(location);
/* 2142:     */     
/* 2143: 826 */     (String)Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn()
/* 2144:     */     {
/* 2145:     */       public String tryIt()
/* 2146:     */         throws Exception
/* 2147:     */       {
/* 2148: 831 */         String path = this.val$location;
/* 2149:     */         
/* 2150: 833 */         path = IO.getWindowsPathIfNeeded(path);
/* 2151: 835 */         if (uri.getScheme() == null)
/* 2152:     */         {
/* 2153: 837 */           Path thePath = FileSystems.getDefault().getPath(path, new String[0]);
/* 2154: 838 */           return IO.read(Files.newBufferedReader(thePath, IO.DEFAULT_CHARSET));
/* 2155:     */         }
/* 2156: 840 */         if (uri.getScheme().equals("file")) {
/* 2157: 842 */           return IO.readFromFileSchema(uri);
/* 2158:     */         }
/* 2159: 844 */         if ((uri.getScheme().equals("classpath")) || (uri.getScheme().equals("jar"))) {
/* 2160: 847 */           return IO.readFromClasspath(uri.toString());
/* 2161:     */         }
/* 2162: 850 */         return IO.read(this.val$location, uri);
/* 2163:     */       }
/* 2164:     */     });
/* 2165:     */   }
/* 2166:     */   
/* 2167:     */   public static String readResource(String location)
/* 2168:     */   {
/* 2169: 863 */     final URI uri = createURI(location);
/* 2170:     */     
/* 2171: 865 */     (String)Exceptions.tryIt(String.class, new Exceptions.TrialWithReturn()
/* 2172:     */     {
/* 2173:     */       public String tryIt()
/* 2174:     */         throws Exception
/* 2175:     */       {
/* 2176: 870 */         String path = this.val$location;
/* 2177:     */         
/* 2178: 872 */         path = IO.getWindowsPathIfNeeded(path);
/* 2179: 874 */         if (uri.getScheme() == null)
/* 2180:     */         {
/* 2181: 876 */           Path thePath = FileSystems.getDefault().getPath(path, new String[0]);
/* 2182: 877 */           if (IO.exists(thePath)) {
/* 2183: 878 */             return IO.read(Files.newBufferedReader(thePath, IO.DEFAULT_CHARSET));
/* 2184:     */           }
/* 2185: 880 */           path = "classpath:/" + this.val$location;
/* 2186: 881 */           thePath = IO.path(path);
/* 2187: 882 */           if (IO.exists(thePath)) {
/* 2188: 883 */             return IO.read(Files.newBufferedReader(thePath, IO.DEFAULT_CHARSET));
/* 2189:     */           }
/* 2190: 885 */           return null;
/* 2191:     */         }
/* 2192: 889 */         if (uri.getScheme().equals("file")) {
/* 2193: 891 */           return IO.readFromFileSchema(uri);
/* 2194:     */         }
/* 2195: 893 */         if ((uri.getScheme().equals("classpath")) || (uri.getScheme().equals("jar"))) {
/* 2196: 896 */           return IO.readFromClasspath(uri.toString());
/* 2197:     */         }
/* 2198: 899 */         return IO.read(this.val$location, uri);
/* 2199:     */       }
/* 2200:     */     });
/* 2201:     */   }
/* 2202:     */   
/* 2203:     */   private static String readFromFileSchema(URI uri)
/* 2204:     */   {
/* 2205: 910 */     Path thePath = uriToPath(uri);
/* 2206:     */     try
/* 2207:     */     {
/* 2208: 913 */       return read(Files.newBufferedReader(thePath, DEFAULT_CHARSET));
/* 2209:     */     }
/* 2210:     */     catch (IOException e)
/* 2211:     */     {
/* 2212: 916 */       return (String)Exceptions.handle(Typ.string, e);
/* 2213:     */     }
/* 2214:     */   }
/* 2215:     */   
/* 2216:     */   public static Path uriToPath(URI uri)
/* 2217:     */   {
/* 2218: 921 */     Path thePath = null;
/* 2219: 922 */     if (Sys.isWindows())
/* 2220:     */     {
/* 2221: 923 */       String newPath = uri.getPath();
/* 2222: 924 */       if (newPath.startsWith("/C:")) {
/* 2223: 925 */         newPath = Str.slc(newPath, 3);
/* 2224:     */       }
/* 2225: 927 */       thePath = FileSystems.getDefault().getPath(newPath, new String[0]);
/* 2226:     */     }
/* 2227:     */     else
/* 2228:     */     {
/* 2229: 929 */       thePath = FileSystems.getDefault().getPath(uri.getPath(), new String[0]);
/* 2230:     */     }
/* 2231: 931 */     return thePath;
/* 2232:     */   }
/* 2233:     */   
/* 2234:     */   private static List<String> readLines(String location, URI uri)
/* 2235:     */     throws Exception
/* 2236:     */   {
/* 2237:     */     try
/* 2238:     */     {
/* 2239: 936 */       String path = location;
/* 2240: 937 */       path = getWindowsPathIfNeeded(path);
/* 2241:     */       
/* 2242: 939 */       FileSystem fileSystem = FileSystems.getFileSystem(uri);
/* 2243: 940 */       Path fsPath = fileSystem.getPath(path, new String[0]);
/* 2244:     */       
/* 2245:     */ 
/* 2246: 943 */       return Files.readAllLines(fsPath, DEFAULT_CHARSET);
/* 2247:     */     }
/* 2248:     */     catch (ProviderNotFoundException ex) {}
/* 2249: 945 */     return readLines(uri.toURL().openStream());
/* 2250:     */   }
/* 2251:     */   
/* 2252:     */   private static void eachLine(String location, URI uri, EachLine eachLine)
/* 2253:     */     throws Exception
/* 2254:     */   {
/* 2255:     */     try
/* 2256:     */     {
/* 2257: 952 */       FileSystem fileSystem = FileSystems.getFileSystem(uri);
/* 2258: 953 */       Path fsPath = fileSystem.getPath(location, new String[0]);
/* 2259: 954 */       BufferedReader buf = Files.newBufferedReader(fsPath, DEFAULT_CHARSET);
/* 2260: 955 */       eachLine(buf, eachLine);
/* 2261:     */     }
/* 2262:     */     catch (ProviderNotFoundException ex)
/* 2263:     */     {
/* 2264: 959 */       eachLine(uri.toURL().openStream(), eachLine);
/* 2265:     */     }
/* 2266:     */   }
/* 2267:     */   
/* 2268:     */   private static String read(String location, URI uri)
/* 2269:     */     throws Exception
/* 2270:     */   {
/* 2271:     */     try
/* 2272:     */     {
/* 2273: 965 */       FileSystem fileSystem = FileSystems.getFileSystem(uri);
/* 2274: 966 */       Path fsPath = fileSystem.getPath(location, new String[0]);
/* 2275: 967 */       return read(Files.newBufferedReader(fsPath, DEFAULT_CHARSET));
/* 2276:     */     }
/* 2277:     */     catch (ProviderNotFoundException ex) {}
/* 2278: 969 */     return read(uri.toURL().openStream());
/* 2279:     */   }
/* 2280:     */   
/* 2281:     */   public static void write(OutputStream out, String content, Charset charset)
/* 2282:     */   {
/* 2283:     */     try
/* 2284:     */     {
/* 2285: 976 */       OutputStream o = out;Throwable localThrowable2 = null;
/* 2286:     */       try
/* 2287:     */       {
/* 2288: 977 */         o.write(content.getBytes(charset));
/* 2289:     */       }
/* 2290:     */       catch (Throwable localThrowable1)
/* 2291:     */       {
/* 2292: 976 */         localThrowable2 = localThrowable1;throw localThrowable1;
/* 2293:     */       }
/* 2294:     */       finally
/* 2295:     */       {
/* 2296: 978 */         if (o != null) {
/* 2297: 978 */           if (localThrowable2 != null) {
/* 2298:     */             try
/* 2299:     */             {
/* 2300: 978 */               o.close();
/* 2301:     */             }
/* 2302:     */             catch (Throwable x2)
/* 2303:     */             {
/* 2304: 978 */               localThrowable2.addSuppressed(x2);
/* 2305:     */             }
/* 2306:     */           } else {
/* 2307: 978 */             o.close();
/* 2308:     */           }
/* 2309:     */         }
/* 2310:     */       }
/* 2311:     */     }
/* 2312:     */     catch (Exception ex)
/* 2313:     */     {
/* 2314: 979 */       Exceptions.handle(ex);
/* 2315:     */     }
/* 2316:     */   }
/* 2317:     */   
/* 2318:     */   public static void writeChild(Path parentDir, String childFileName, String childContents)
/* 2319:     */   {
/* 2320:     */     try
/* 2321:     */     {
/* 2322: 988 */       Path newFilePath = path(parentDir.toString(), new String[] { childFileName });
/* 2323:     */       
/* 2324:     */ 
/* 2325: 991 */       write(newFilePath, childContents);
/* 2326:     */     }
/* 2327:     */     catch (Exception ex)
/* 2328:     */     {
/* 2329: 993 */       Exceptions.handle(ex);
/* 2330:     */     }
/* 2331:     */   }
/* 2332:     */   
/* 2333:     */   public static Path createChildDirectory(Path parentDir, String childDir)
/* 2334:     */   {
/* 2335:     */     try
/* 2336:     */     {
/* 2337:1002 */       Path newDir = path(parentDir.toString(), new String[] { childDir });
/* 2338:1006 */       if (!Files.exists(newDir, new LinkOption[0])) {
/* 2339:1007 */         Files.createDirectory(newDir, new FileAttribute[0]);
/* 2340:     */       }
/* 2341:1010 */       return newDir;
/* 2342:     */     }
/* 2343:     */     catch (Exception ex)
/* 2344:     */     {
/* 2345:1013 */       return (Path)Exceptions.handle(Path.class, ex);
/* 2346:     */     }
/* 2347:     */   }
/* 2348:     */   
/* 2349:     */   public static Path createDirectory(Path dir)
/* 2350:     */   {
/* 2351:     */     try
/* 2352:     */     {
/* 2353:1022 */       if (!Files.exists(dir, new LinkOption[0])) {
/* 2354:1023 */         return Files.createDirectory(dir, new FileAttribute[0]);
/* 2355:     */       }
/* 2356:1025 */       return null;
/* 2357:     */     }
/* 2358:     */     catch (Exception ex)
/* 2359:     */     {
/* 2360:1029 */       return (Path)Exceptions.handle(Path.class, ex);
/* 2361:     */     }
/* 2362:     */   }
/* 2363:     */   
/* 2364:     */   public static Path createDirectory(String dir)
/* 2365:     */   {
/* 2366:     */     try
/* 2367:     */     {
/* 2368:1037 */       Path newDir = path(dir);
/* 2369:1038 */       createDirectory(newDir);
/* 2370:     */       
/* 2371:1040 */       return newDir;
/* 2372:     */     }
/* 2373:     */     catch (Exception ex)
/* 2374:     */     {
/* 2375:1043 */       return (Path)Exceptions.handle(Path.class, ex);
/* 2376:     */     }
/* 2377:     */   }
/* 2378:     */   
/* 2379:     */   public static FileSystem fileSystem()
/* 2380:     */   {
/* 2381:1048 */     return FileSystems.getDefault();
/* 2382:     */   }
/* 2383:     */   
/* 2384:     */   public static Path path(String path, String... more)
/* 2385:     */   {
/* 2386:1053 */     return Paths.get(path, more);
/* 2387:     */   }
/* 2388:     */   
/* 2389:     */   public static Path path(Path path, String... more)
/* 2390:     */   {
/* 2391:1057 */     return Paths.get(path.toString(), more);
/* 2392:     */   }
/* 2393:     */   
/* 2394:     */   public static void write(Path file, String contents)
/* 2395:     */   {
/* 2396:1061 */     write(file, contents.getBytes(DEFAULT_CHARSET));
/* 2397:     */   }
/* 2398:     */   
/* 2399:     */   public static void write(String file, String contents)
/* 2400:     */   {
/* 2401:1065 */     write(path(file), contents.getBytes(DEFAULT_CHARSET));
/* 2402:     */   }
/* 2403:     */   
/* 2404:     */   public static void output(String file, byte[] bytes)
/* 2405:     */   {
/* 2406:1070 */     write(path(file), bytes);
/* 2407:     */   }
/* 2408:     */   
/* 2409:     */   public static void output(Path file, byte[] bytes)
/* 2410:     */   {
/* 2411:1074 */     write(file, bytes);
/* 2412:     */   }
/* 2413:     */   
/* 2414:     */   public static void write(String file, byte[] contents)
/* 2415:     */   {
/* 2416:1079 */     write(path(file), contents);
/* 2417:     */   }
/* 2418:     */   
/* 2419:     */   public static void write(Path file, byte[] contents)
/* 2420:     */   {
/* 2421:     */     try
/* 2422:     */     {
/* 2423:1084 */       Files.write(file, contents, new OpenOption[0]);
/* 2424:     */     }
/* 2425:     */     catch (Exception ex)
/* 2426:     */     {
/* 2427:1087 */       Exceptions.handle(ex);
/* 2428:     */     }
/* 2429:     */   }
/* 2430:     */   
/* 2431:     */   public static void write(OutputStream out, String content)
/* 2432:     */   {
/* 2433:     */     try
/* 2434:     */     {
/* 2435:1093 */       OutputStream o = out;Throwable localThrowable2 = null;
/* 2436:     */       try
/* 2437:     */       {
/* 2438:1094 */         o.write(content.getBytes(DEFAULT_CHARSET));
/* 2439:     */       }
/* 2440:     */       catch (Throwable localThrowable1)
/* 2441:     */       {
/* 2442:1093 */         localThrowable2 = localThrowable1;throw localThrowable1;
/* 2443:     */       }
/* 2444:     */       finally
/* 2445:     */       {
/* 2446:1095 */         if (o != null) {
/* 2447:1095 */           if (localThrowable2 != null) {
/* 2448:     */             try
/* 2449:     */             {
/* 2450:1095 */               o.close();
/* 2451:     */             }
/* 2452:     */             catch (Throwable x2)
/* 2453:     */             {
/* 2454:1095 */               localThrowable2.addSuppressed(x2);
/* 2455:     */             }
/* 2456:     */           } else {
/* 2457:1095 */             o.close();
/* 2458:     */           }
/* 2459:     */         }
/* 2460:     */       }
/* 2461:     */     }
/* 2462:     */     catch (Exception ex)
/* 2463:     */     {
/* 2464:1096 */       Exceptions.handle(ex);
/* 2465:     */     }
/* 2466:     */   }
/* 2467:     */   
/* 2468:     */   public static void writeNoClose(OutputStream out, String content)
/* 2469:     */   {
/* 2470:     */     try
/* 2471:     */     {
/* 2472:1104 */       out.write(content.getBytes(DEFAULT_CHARSET));
/* 2473:     */     }
/* 2474:     */     catch (Exception ex)
/* 2475:     */     {
/* 2476:1106 */       Exceptions.handle(ex);
/* 2477:     */     }
/* 2478:     */   }
/* 2479:     */   
/* 2480:     */   public static String readFromClasspath(Class<?> clazz, String location)
/* 2481:     */   {
/* 2482:1112 */     List<String> resources = Classpaths.resources(clazz, location);
/* 2483:1116 */     if (Lists.len(resources) > 0) {
/* 2484:     */       try
/* 2485:     */       {
/* 2486:1118 */         return read(Files.newBufferedReader(path((String)resources.get(0)), DEFAULT_CHARSET));
/* 2487:     */       }
/* 2488:     */       catch (IOException e)
/* 2489:     */       {
/* 2490:1120 */         return (String)Exceptions.handle(String.class, "unable to read classpath resource " + location, e);
/* 2491:     */       }
/* 2492:     */     }
/* 2493:1123 */     return null;
/* 2494:     */   }
/* 2495:     */   
/* 2496:     */   private static List<String> listFromDefaultClassLoader(String s)
/* 2497:     */   {
/* 2498:1128 */     List<String> result = new ArrayList();
/* 2499:     */     
/* 2500:1130 */     String newPath = s;
/* 2501:     */     
/* 2502:1132 */     List<String> resources = Classpaths.resources(IO.class, newPath);
/* 2503:1136 */     for (String resourcePath : resources)
/* 2504:     */     {
/* 2505:1137 */       Path path = path(resourcePath);
/* 2506:1138 */       if (Files.isDirectory(path, new LinkOption[0])) {
/* 2507:1139 */         result.addAll(list(resourcePath));
/* 2508:     */       } else {
/* 2509:1141 */         result.add(resourcePath.toString());
/* 2510:     */       }
/* 2511:     */     }
/* 2512:1146 */     return result;
/* 2513:     */   }
/* 2514:     */   
/* 2515:     */   private static List<Path> pathsFromDefaultClassLoader(String s)
/* 2516:     */   {
/* 2517:1151 */     List<Path> result = new ArrayList();
/* 2518:     */     
/* 2519:1153 */     String newPath = s;
/* 2520:     */     
/* 2521:1155 */     List<Path> resources = Classpaths.paths(IO.class, newPath);
/* 2522:1159 */     for (Path resourcePath : resources) {
/* 2523:1160 */       if (Files.isDirectory(resourcePath, new LinkOption[0])) {
/* 2524:1161 */         result.addAll(paths(resourcePath));
/* 2525:     */       } else {
/* 2526:1163 */         result.add(resourcePath);
/* 2527:     */       }
/* 2528:     */     }
/* 2529:1168 */     return result;
/* 2530:     */   }
/* 2531:     */   
/* 2532:     */   public static Path path(String location)
/* 2533:     */   {
/* 2534:1174 */     if (location.startsWith("classpath:"))
/* 2535:     */     {
/* 2536:1175 */       String path = StringScanner.split(location, ':')[1];
/* 2537:     */       
/* 2538:1177 */       List<String> resources = Classpaths.resources(IO.class, path);
/* 2539:1180 */       if ((resources == null) || (resources.size() == 0)) {
/* 2540:1181 */         Exceptions.die(new Object[] { "Resource not found", location });
/* 2541:     */       }
/* 2542:1184 */       String result = (String)Lists.idx(resources, 0);
/* 2543:1185 */       if (result == null) {
/* 2544:1186 */         return path(path);
/* 2545:     */       }
/* 2546:1188 */       return path(result);
/* 2547:     */     }
/* 2548:1190 */     if (location.startsWith("jar:file:")) {
/* 2549:1191 */       return convertJarFileSystemURIToPath(location);
/* 2550:     */     }
/* 2551:1193 */     return Paths.get(location, new String[0]);
/* 2552:     */   }
/* 2553:     */   
/* 2554:     */   public static String readFromClasspath(String location)
/* 2555:     */   {
/* 2556:1199 */     Exceptions.requireNonNull(location, "location can't be null");
/* 2557:1201 */     if (!location.startsWith("classpath:")) {
/* 2558:1202 */       Exceptions.die("Location must starts with classpath");
/* 2559:     */     }
/* 2560:1205 */     Path path = path(location);
/* 2561:1207 */     if (path == null) {
/* 2562:1208 */       return null;
/* 2563:     */     }
/* 2564:     */     try
/* 2565:     */     {
/* 2566:1211 */       return read(Files.newBufferedReader(path, DEFAULT_CHARSET));
/* 2567:     */     }
/* 2568:     */     catch (IOException e)
/* 2569:     */     {
/* 2570:1213 */       return (String)Exceptions.handle(String.class, "unable to read classpath resource " + location, e);
/* 2571:     */     }
/* 2572:     */   }
/* 2573:     */   
/* 2574:     */   public static InputStream inputStream(String resource)
/* 2575:     */   {
/* 2576:1220 */     Path path = path(resource);
/* 2577:     */     try
/* 2578:     */     {
/* 2579:1222 */       return Files.newInputStream(path, new OpenOption[0]);
/* 2580:     */     }
/* 2581:     */     catch (IOException e)
/* 2582:     */     {
/* 2583:1224 */       return (InputStream)Exceptions.handle(InputStream.class, "unable to open " + resource, e);
/* 2584:     */     }
/* 2585:     */   }
/* 2586:     */   
/* 2587:     */   public static List<String> list(String path)
/* 2588:     */   {
/* 2589:1232 */     URI uri = URI.create(path);
/* 2590:1233 */     if (uri.getScheme() == null)
/* 2591:     */     {
/* 2592:1234 */       Path pathFromFileSystem = path(path);
/* 2593:1235 */       return list(pathFromFileSystem);
/* 2594:     */     }
/* 2595:1236 */     if (uri.getScheme().equals("classpath")) {
/* 2596:1238 */       return listFromDefaultClassLoader(StringScanner.split(path, ':')[1]);
/* 2597:     */     }
/* 2598:1241 */     Path pathFromFileSystem = path(path);
/* 2599:1242 */     return list(pathFromFileSystem);
/* 2600:     */   }
/* 2601:     */   
/* 2602:     */   public static List<Path> paths(String path)
/* 2603:     */   {
/* 2604:1250 */     URI uri = URI.create(path);
/* 2605:1251 */     if (uri.getScheme() == null)
/* 2606:     */     {
/* 2607:1252 */       Path pathFromFileSystem = path(path);
/* 2608:1253 */       return listPath(pathFromFileSystem);
/* 2609:     */     }
/* 2610:1254 */     if (uri.getScheme().equals("classpath")) {
/* 2611:1256 */       return pathsFromDefaultClassLoader(StringScanner.split(path, ':')[1]);
/* 2612:     */     }
/* 2613:1259 */     Path pathFromFileSystem = path(path);
/* 2614:1260 */     return listPath(pathFromFileSystem);
/* 2615:     */   }
/* 2616:     */   
/* 2617:     */   public static List<Path> pathsByExt(String path, String ext)
/* 2618:     */   {
/* 2619:1266 */     List<Path> list = paths(path);
/* 2620:     */     
/* 2621:     */ 
/* 2622:1269 */     List<Path> newList = new ArrayList();
/* 2623:1271 */     for (Path file : list) {
/* 2624:1272 */       if (file.toString().endsWith(ext)) {
/* 2625:1273 */         newList.add(file);
/* 2626:     */       }
/* 2627:     */     }
/* 2628:1277 */     return newList;
/* 2629:     */   }
/* 2630:     */   
/* 2631:     */   public static List<Path> paths(Path path)
/* 2632:     */   {
/* 2633:1283 */     return listPath(path);
/* 2634:     */   }
/* 2635:     */   
/* 2636:     */   public static List<String> listByExt(String path, String ext)
/* 2637:     */   {
/* 2638:1291 */     List<String> list = list(path);
/* 2639:1292 */     List<String> newList = new ArrayList();
/* 2640:1294 */     for (String file : list) {
/* 2641:1295 */       if (file.endsWith(ext)) {
/* 2642:1296 */         newList.add(file);
/* 2643:     */       }
/* 2644:     */     }
/* 2645:1300 */     return newList;
/* 2646:     */   }
/* 2647:     */   
/* 2648:     */   public static void delete(Path path)
/* 2649:     */   {
/* 2650:     */     try
/* 2651:     */     {
/* 2652:1308 */       Files.delete(path);
/* 2653:     */     }
/* 2654:     */     catch (IOException e)
/* 2655:     */     {
/* 2656:1310 */       Exceptions.handle(e);
/* 2657:     */     }
/* 2658:     */   }
/* 2659:     */   
/* 2660:     */   public static void createDirectories(Path path)
/* 2661:     */   {
/* 2662:     */     try
/* 2663:     */     {
/* 2664:1316 */       Files.createDirectories(path, new FileAttribute[0]);
/* 2665:     */     }
/* 2666:     */     catch (IOException e)
/* 2667:     */     {
/* 2668:1318 */       Exceptions.handle(e);
/* 2669:     */     }
/* 2670:     */   }
/* 2671:     */   
/* 2672:     */   public static void delete(String path)
/* 2673:     */   {
/* 2674:     */     try
/* 2675:     */     {
/* 2676:1325 */       Files.delete(path(path));
/* 2677:     */     }
/* 2678:     */     catch (IOException e)
/* 2679:     */     {
/* 2680:1327 */       Exceptions.handle(e);
/* 2681:     */     }
/* 2682:     */   }
/* 2683:     */   
/* 2684:     */   public static void createDirectories(String path)
/* 2685:     */   {
/* 2686:     */     try
/* 2687:     */     {
/* 2688:1333 */       Files.createDirectories(path(path), new FileAttribute[0]);
/* 2689:     */     }
/* 2690:     */     catch (IOException e)
/* 2691:     */     {
/* 2692:1335 */       Exceptions.handle(e);
/* 2693:     */     }
/* 2694:     */   }
/* 2695:     */   
/* 2696:     */   public static boolean exists(Path path)
/* 2697:     */   {
/* 2698:1342 */     return Files.exists(path, new LinkOption[0]);
/* 2699:     */   }
/* 2700:     */   
/* 2701:     */   public static boolean exists(String path)
/* 2702:     */   {
/* 2703:1347 */     return Files.exists(path(path), new LinkOption[0]);
/* 2704:     */   }
/* 2705:     */   
/* 2706:     */   public static void move(Path source, Path target)
/* 2707:     */   {
/* 2708:     */     try
/* 2709:     */     {
/* 2710:1352 */       Files.move(source, target, new CopyOption[0]);
/* 2711:     */     }
/* 2712:     */     catch (IOException e)
/* 2713:     */     {
/* 2714:1354 */       Exceptions.handle(e);
/* 2715:     */     }
/* 2716:     */   }
/* 2717:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.IO
 * JD-Core Version:    0.7.0.1
 */