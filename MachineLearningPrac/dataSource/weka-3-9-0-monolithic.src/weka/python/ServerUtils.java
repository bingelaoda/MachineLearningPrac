/*    1:     */ package weka.python;
/*    2:     */ 
/*    3:     */ import java.awt.image.BufferedImage;
/*    4:     */ import java.io.BufferedInputStream;
/*    5:     */ import java.io.BufferedWriter;
/*    6:     */ import java.io.ByteArrayInputStream;
/*    7:     */ import java.io.ByteArrayOutputStream;
/*    8:     */ import java.io.FileReader;
/*    9:     */ import java.io.IOException;
/*   10:     */ import java.io.InputStream;
/*   11:     */ import java.io.OutputStream;
/*   12:     */ import java.io.OutputStreamWriter;
/*   13:     */ import java.io.PrintStream;
/*   14:     */ import java.io.StringReader;
/*   15:     */ import java.nio.ByteBuffer;
/*   16:     */ import java.util.ArrayList;
/*   17:     */ import java.util.HashMap;
/*   18:     */ import java.util.List;
/*   19:     */ import java.util.Map;
/*   20:     */ import javax.imageio.ImageIO;
/*   21:     */ import org.apache.commons.codec.binary.Base64;
/*   22:     */ import org.boon.Boon;
/*   23:     */ import org.boon.json.JsonFactory;
/*   24:     */ import org.boon.json.ObjectMapper;
/*   25:     */ import weka.core.Attribute;
/*   26:     */ import weka.core.Instance;
/*   27:     */ import weka.core.Instances;
/*   28:     */ import weka.core.Utils;
/*   29:     */ import weka.core.WekaException;
/*   30:     */ import weka.core.converters.CSVSaver;
/*   31:     */ import weka.gui.Logger;
/*   32:     */ 
/*   33:     */ public class ServerUtils
/*   34:     */ {
/*   35:     */   protected static Map<String, Object> createSimpleHeader(Instances header, String frameName)
/*   36:     */   {
/*   37:  75 */     Map<String, Object> result = new HashMap();
/*   38:  76 */     result.put("relation_name", header.relationName());
/*   39:  77 */     result.put("frame_name", frameName);
/*   40:  78 */     result.put("class_index", Integer.valueOf(header.classIndex()));
/*   41:  79 */     if (header.classIndex() >= 0)
/*   42:     */     {
/*   43:  80 */       result.put("class_name", header.classAttribute().name());
/*   44:  81 */       if (header.classAttribute().isNominal())
/*   45:     */       {
/*   46:  82 */         List<String> classVals = new ArrayList();
/*   47:  83 */         for (int i = 0; i < header.classAttribute().numValues(); i++) {
/*   48:  84 */           classVals.add(header.classAttribute().value(i));
/*   49:     */         }
/*   50:  86 */         result.put("class_values", classVals);
/*   51:     */       }
/*   52:     */     }
/*   53:  90 */     if (header.checkForAttributeType(3))
/*   54:     */     {
/*   55:  91 */       List<Integer> dateAtts = new ArrayList();
/*   56:  92 */       for (int i = 0; i < header.numAttributes(); i++) {
/*   57:  93 */         if (header.attribute(i).isDate()) {
/*   58:  94 */           dateAtts.add(Integer.valueOf(i));
/*   59:     */         }
/*   60:     */       }
/*   61:  97 */       result.put("date_atts", dateAtts);
/*   62:     */     }
/*   63: 100 */     return result;
/*   64:     */   }
/*   65:     */   
/*   66:     */   protected static Instances jsonToInstancesHeader(Map<String, Object> header)
/*   67:     */   {
/*   68: 112 */     String relationName = header.get("relation_name").toString();
/*   69: 113 */     List<Map<String, Object>> attributes = (List)header.get("attributes");
/*   70: 115 */     if (attributes == null) {
/*   71: 116 */       throw new IllegalStateException("No attributes in header map!");
/*   72:     */     }
/*   73: 119 */     ArrayList<Attribute> atts = new ArrayList();
/*   74: 120 */     for (Map<String, Object> a : attributes)
/*   75:     */     {
/*   76: 121 */       String attName = a.get("name").toString();
/*   77: 122 */       String type = a.get("type").toString();
/*   78: 123 */       String format = null;
/*   79: 124 */       List<String> values = null;
/*   80: 125 */       if (a.get("format") != null) {
/*   81: 126 */         format = a.get("format").toString();
/*   82:     */       }
/*   83: 128 */       if (a.get("values") != null) {
/*   84: 129 */         values = (List)a.get("values");
/*   85:     */       }
/*   86: 132 */       if (type.equals("NUMERIC")) {
/*   87: 133 */         atts.add(new Attribute(attName));
/*   88: 134 */       } else if (type.equals("DATE")) {
/*   89: 135 */         atts.add(new Attribute(attName, format));
/*   90: 136 */       } else if (type.equals("NOMINAL")) {
/*   91: 137 */         atts.add(new Attribute(attName, values));
/*   92: 138 */       } else if (type.equals("STRING")) {
/*   93: 139 */         atts.add(new Attribute(attName, (List)null));
/*   94:     */       }
/*   95:     */     }
/*   96: 143 */     return new Instances(relationName, atts, 0);
/*   97:     */   }
/*   98:     */   
/*   99:     */   protected static void sendServerShutdown(OutputStream outputStream)
/*  100:     */     throws WekaException
/*  101:     */   {
/*  102: 154 */     Map<String, Object> command = new HashMap();
/*  103: 155 */     command.put("command", "shutdown");
/*  104: 156 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  105: 157 */     ObjectMapper mapper = JsonFactory.create();
/*  106: 158 */     mapper.writeValue(bos, command);
/*  107: 159 */     byte[] bytes = bos.toByteArray();
/*  108:     */     try
/*  109:     */     {
/*  110: 162 */       writeDelimitedToOutputStream(bytes, outputStream);
/*  111:     */     }
/*  112:     */     catch (IOException ex)
/*  113:     */     {
/*  114: 164 */       throw new WekaException(ex);
/*  115:     */     }
/*  116:     */   }
/*  117:     */   
/*  118:     */   protected static List<String> executeUserScript(String script, OutputStream outputStream, InputStream inputStream, Logger log, boolean debug)
/*  119:     */     throws WekaException
/*  120:     */   {
/*  121: 184 */     if (!script.endsWith("\n")) {
/*  122: 185 */       script = script + "\n";
/*  123:     */     }
/*  124: 187 */     List<String> outAndErr = new ArrayList();
/*  125:     */     
/*  126: 189 */     ObjectMapper objectMapper = JsonFactory.create();
/*  127: 190 */     Map<String, Object> command = new HashMap();
/*  128: 191 */     command.put("command", "execute_script");
/*  129: 192 */     command.put("script", script);
/*  130: 193 */     command.put("debug", Boolean.valueOf(debug));
/*  131: 194 */     if ((inputStream != null) && (outputStream != null))
/*  132:     */     {
/*  133: 195 */       ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  134: 196 */       objectMapper.writeValue(bos, command);
/*  135: 197 */       byte[] bytes = bos.toByteArray();
/*  136:     */       try
/*  137:     */       {
/*  138: 199 */         if (debug) {
/*  139: 200 */           outputCommandDebug(command, log);
/*  140:     */         }
/*  141: 202 */         writeDelimitedToOutputStream(bytes, outputStream);
/*  142:     */         
/*  143:     */ 
/*  144: 205 */         bytes = readDelimitedFromInputStream(inputStream);
/*  145: 206 */         ObjectMapper mapper = JsonFactory.create();
/*  146: 207 */         Map<String, Object> ack = (Map)mapper.readValue(bytes, Map.class);
/*  147: 208 */         if (!ack.get("response").toString().equals("ok")) {
/*  148: 210 */           throw new WekaException(ack.get("error_message").toString());
/*  149:     */         }
/*  150: 213 */         outAndErr.add(ack.get("script_out").toString());
/*  151: 214 */         outAndErr.add(ack.get("script_error").toString());
/*  152: 215 */         if (debug) {
/*  153: 216 */           if (log != null)
/*  154:     */           {
/*  155: 217 */             log.logMessage("Script output:\n" + (String)outAndErr.get(0));
/*  156: 218 */             log.logMessage("\nScript error:\n" + (String)outAndErr.get(1));
/*  157:     */           }
/*  158:     */           else
/*  159:     */           {
/*  160: 220 */             System.err.println("Script output:\n" + (String)outAndErr.get(0));
/*  161: 221 */             System.err.println("\nScript error:\n" + (String)outAndErr.get(1));
/*  162:     */           }
/*  163:     */         }
/*  164: 225 */         if (((String)outAndErr.get(1)).contains("Warning:")) {
/*  165: 228 */           outAndErr.set(1, "");
/*  166:     */         }
/*  167:     */       }
/*  168:     */       catch (IOException ex)
/*  169:     */       {
/*  170: 231 */         throw new WekaException(ex);
/*  171:     */       }
/*  172:     */     }
/*  173: 233 */     else if (debug)
/*  174:     */     {
/*  175: 234 */       outputCommandDebug(command, log);
/*  176:     */     }
/*  177: 237 */     return outAndErr;
/*  178:     */   }
/*  179:     */   
/*  180:     */   protected static void sendInstancesScikitLearn(Instances instances, String frameName, OutputStream outputStream, InputStream inputStream, Logger log, boolean debug)
/*  181:     */     throws WekaException
/*  182:     */   {
/*  183: 262 */     ObjectMapper mapper = JsonFactory.create();
/*  184: 263 */     Map<String, Object> simpleHeader = createSimpleHeader(instances, frameName);
/*  185: 264 */     Map<String, Object> command = new HashMap();
/*  186: 265 */     command.put("command", "put_instances");
/*  187: 266 */     command.put("num_instances", Integer.valueOf(instances.numInstances()));
/*  188: 267 */     command.put("header", simpleHeader);
/*  189: 268 */     command.put("debug", Boolean.valueOf(debug));
/*  190: 269 */     if ((inputStream != null) && (outputStream != null))
/*  191:     */     {
/*  192: 270 */       ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  193: 271 */       mapper.writeValue(bos, command);
/*  194: 272 */       byte[] bytes = bos.toByteArray();
/*  195:     */       try
/*  196:     */       {
/*  197: 274 */         if (debug) {
/*  198: 275 */           outputCommandDebug(command, log);
/*  199:     */         }
/*  200: 278 */         writeDelimitedToOutputStream(bytes, outputStream);
/*  201: 279 */         if (instances.numInstances() > 0)
/*  202:     */         {
/*  203: 280 */           StringBuilder builder = new StringBuilder();
/*  204: 283 */           for (int i = 0; i < instances.numAttributes(); i++)
/*  205:     */           {
/*  206: 284 */             builder.append(Utils.quote(instances.attribute(i).name()));
/*  207: 285 */             if (i < instances.numAttributes() - 1) {
/*  208: 286 */               builder.append(",");
/*  209:     */             } else {
/*  210: 288 */               builder.append("\n");
/*  211:     */             }
/*  212:     */           }
/*  213: 293 */           for (int i = 0; i < instances.numInstances(); i++)
/*  214:     */           {
/*  215: 294 */             Instance current = instances.instance(i);
/*  216: 295 */             for (int j = 0; j < instances.numAttributes(); j++)
/*  217:     */             {
/*  218: 296 */               builder.append(current.value(j));
/*  219: 297 */               if (j < instances.numAttributes() - 1) {
/*  220: 298 */                 builder.append(",");
/*  221:     */               } else {
/*  222: 300 */                 builder.append("\n");
/*  223:     */               }
/*  224:     */             }
/*  225:     */           }
/*  226: 304 */           if (debug) {
/*  227: 305 */             System.err.println(builder.toString());
/*  228:     */           }
/*  229: 307 */           bos = new ByteArrayOutputStream();
/*  230: 308 */           BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(bos));
/*  231: 309 */           bw.write(builder.toString());
/*  232: 310 */           bw.flush();
/*  233: 311 */           bytes = bos.toByteArray();
/*  234: 312 */           writeDelimitedToOutputStream(bytes, outputStream);
/*  235:     */           
/*  236: 314 */           String serverAck = receiveServerAck(inputStream);
/*  237: 315 */           if (serverAck != null) {
/*  238: 316 */             throw new WekaException("Transfer of instances failed: " + serverAck);
/*  239:     */           }
/*  240: 321 */           int classIndex = instances.classIndex();
/*  241: 322 */           builder = new StringBuilder();
/*  242: 323 */           for (int i = 0; i < instances.numAttributes(); i++) {
/*  243: 324 */             if (i != classIndex) {
/*  244: 325 */               builder.append(i).append(",");
/*  245:     */             }
/*  246:     */           }
/*  247: 328 */           String xList = builder.substring(0, builder.length() - 1);
/*  248: 329 */           builder = new StringBuilder();
/*  249: 330 */           builder.append("X = " + frameName + ".iloc[:,[" + xList + "]].values\n");
/*  250: 332 */           if (classIndex >= 0) {
/*  251: 333 */             builder.append("Y = " + frameName + ".iloc[:,[" + classIndex + "]].values\n");
/*  252:     */           }
/*  253: 337 */           if (debug) {
/*  254: 338 */             if (log != null) {
/*  255: 339 */               log.logMessage("Executing python script:\n\n" + builder.toString());
/*  256:     */             } else {
/*  257: 342 */               System.err.println("Executing python script:\n\n" + builder.toString());
/*  258:     */             }
/*  259:     */           }
/*  260: 347 */           List<String> outErr = executeUserScript(builder.toString(), outputStream, inputStream, log, debug);
/*  261: 351 */           if ((outErr.size() == 2) && (((String)outErr.get(1)).length() > 0)) {
/*  262: 352 */             throw new WekaException((String)outErr.get(1));
/*  263:     */           }
/*  264:     */         }
/*  265:     */       }
/*  266:     */       catch (IOException e)
/*  267:     */       {
/*  268: 356 */         throw new WekaException(e);
/*  269:     */       }
/*  270:     */     }
/*  271: 358 */     else if (debug)
/*  272:     */     {
/*  273: 359 */       outputCommandDebug(command, log);
/*  274:     */     }
/*  275:     */   }
/*  276:     */   
/*  277:     */   protected static void sendInstances(Instances instances, String frameName, OutputStream outputStream, InputStream inputStream, Logger log, boolean debug)
/*  278:     */     throws WekaException
/*  279:     */   {
/*  280: 378 */     ObjectMapper mapper = JsonFactory.create();
/*  281: 379 */     Map<String, Object> simpleHeader = createSimpleHeader(instances, frameName);
/*  282: 380 */     Map<String, Object> command = new HashMap();
/*  283: 381 */     command.put("command", "put_instances");
/*  284: 382 */     command.put("num_instances", Integer.valueOf(instances.numInstances()));
/*  285: 383 */     command.put("header", simpleHeader);
/*  286: 384 */     command.put("debug", Boolean.valueOf(debug));
/*  287: 386 */     if (instances.checkForAttributeType(3))
/*  288:     */     {
/*  289: 388 */       ArrayList<Attribute> newAtts = new ArrayList();
/*  290: 389 */       for (int i = 0; i < instances.numAttributes(); i++) {
/*  291: 390 */         if (!instances.attribute(i).isDate())
/*  292:     */         {
/*  293: 391 */           newAtts.add((Attribute)instances.attribute(i).copy());
/*  294:     */         }
/*  295:     */         else
/*  296:     */         {
/*  297: 393 */           Attribute newDate = new Attribute(instances.attribute(i).name(), "yyyy-MM-dd HH:mm:ss");
/*  298:     */           
/*  299: 395 */           newAtts.add(newDate);
/*  300:     */         }
/*  301:     */       }
/*  302: 398 */       Instances newInsts = new Instances(instances.relationName(), newAtts, instances.numInstances());
/*  303: 401 */       for (int i = 0; i < instances.numInstances(); i++) {
/*  304: 402 */         newInsts.add(instances.instance(i));
/*  305:     */       }
/*  306: 404 */       newInsts.setClassIndex(instances.classIndex());
/*  307: 405 */       instances = newInsts;
/*  308:     */     }
/*  309: 408 */     if ((inputStream != null) && (outputStream != null))
/*  310:     */     {
/*  311: 409 */       ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  312: 410 */       mapper.writeValue(bos, command);
/*  313: 411 */       byte[] bytes = bos.toByteArray();
/*  314:     */       try
/*  315:     */       {
/*  316: 413 */         if (debug) {
/*  317: 414 */           outputCommandDebug(command, log);
/*  318:     */         }
/*  319: 418 */         writeDelimitedToOutputStream(bytes, outputStream);
/*  320: 421 */         if (instances.numInstances() > 0)
/*  321:     */         {
/*  322: 422 */           CSVSaver saver = new CSVSaver();
/*  323: 423 */           saver.setInstances(instances);
/*  324: 424 */           bos = new ByteArrayOutputStream();
/*  325: 425 */           saver.setDestination(bos);
/*  326: 426 */           saver.writeBatch();
/*  327: 427 */           bytes = bos.toByteArray();
/*  328: 428 */           writeDelimitedToOutputStream(bytes, outputStream);
/*  329:     */         }
/*  330: 431 */         String serverAck = receiveServerAck(inputStream);
/*  331: 432 */         if (serverAck != null) {
/*  332: 433 */           throw new WekaException("Transfer of instances failed: " + serverAck);
/*  333:     */         }
/*  334:     */       }
/*  335:     */       catch (IOException e)
/*  336:     */       {
/*  337: 436 */         throw new WekaException(e);
/*  338:     */       }
/*  339:     */     }
/*  340: 438 */     else if (debug)
/*  341:     */     {
/*  342: 439 */       outputCommandDebug(command, log);
/*  343:     */     }
/*  344:     */   }
/*  345:     */   
/*  346:     */   protected static String receiveServerAck(InputStream inputStream)
/*  347:     */     throws IOException
/*  348:     */   {
/*  349: 454 */     byte[] bytes = readDelimitedFromInputStream(inputStream);
/*  350: 455 */     ObjectMapper mapper = JsonFactory.create();
/*  351: 456 */     Map<String, Object> ack = (Map)mapper.readValue(bytes, Map.class);
/*  352:     */     
/*  353: 458 */     String response = ack.get("response").toString();
/*  354: 459 */     if (response.equals("ok")) {
/*  355: 460 */       return null;
/*  356:     */     }
/*  357: 463 */     return ack.get("error_message").toString();
/*  358:     */   }
/*  359:     */   
/*  360:     */   protected static int receiveServerPIDAck(InputStream inputStream)
/*  361:     */     throws IOException
/*  362:     */   {
/*  363: 476 */     byte[] bytes = readDelimitedFromInputStream(inputStream);
/*  364: 477 */     ObjectMapper mapper = JsonFactory.create();
/*  365: 478 */     Map<String, Object> ack = (Map)mapper.readValue(bytes, Map.class);
/*  366:     */     
/*  367: 480 */     String response = ack.get("response").toString();
/*  368: 481 */     if (response.equals("pid_response")) {
/*  369: 482 */       return ((Integer)ack.get("pid")).intValue();
/*  370:     */     }
/*  371: 484 */     throw new IOException("Server did not send a pid_response");
/*  372:     */   }
/*  373:     */   
/*  374:     */   protected static List<String[]> receiveVariableList(OutputStream outputStream, InputStream inputStream, Logger log, boolean debug)
/*  375:     */     throws WekaException
/*  376:     */   {
/*  377: 505 */     List<String[]> results = new ArrayList();
/*  378: 506 */     ObjectMapper mapper = JsonFactory.create();
/*  379: 507 */     Map<String, Object> command = new HashMap();
/*  380: 508 */     command.put("command", "get_variable_list");
/*  381: 509 */     command.put("debug", Boolean.valueOf(debug));
/*  382: 510 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  383: 511 */     mapper.writeValue(bos, command);
/*  384: 512 */     byte[] bytes = bos.toByteArray();
/*  385: 513 */     if ((inputStream != null) && (outputStream != null)) {
/*  386:     */       try
/*  387:     */       {
/*  388: 515 */         if (debug) {
/*  389: 516 */           outputCommandDebug(command, log);
/*  390:     */         }
/*  391: 520 */         writeDelimitedToOutputStream(bytes, outputStream);
/*  392:     */         
/*  393: 522 */         bytes = readDelimitedFromInputStream(inputStream);
/*  394: 523 */         Map<String, Object> ack = (Map)mapper.readValue(bytes, Map.class);
/*  395: 524 */         if (!ack.get("response").toString().equals("ok")) {
/*  396: 526 */           throw new WekaException(ack.get("error_message").toString());
/*  397:     */         }
/*  398: 529 */         Object l = ack.get("variable_list");
/*  399: 530 */         if (!(l instanceof List)) {
/*  400: 531 */           throw new WekaException("Was expecting the variable list to be a List object!");
/*  401:     */         }
/*  402: 534 */         List<Map<String, String>> vList = (List)l;
/*  403: 535 */         for (Map<String, String> v : vList)
/*  404:     */         {
/*  405: 536 */           String[] vEntry = new String[2];
/*  406: 537 */           vEntry[0] = ((String)v.get("name"));
/*  407: 538 */           vEntry[1] = ((String)v.get("type"));
/*  408: 539 */           results.add(vEntry);
/*  409:     */         }
/*  410:     */       }
/*  411:     */       catch (IOException ex)
/*  412:     */       {
/*  413: 542 */         throw new WekaException(ex);
/*  414:     */       }
/*  415: 544 */     } else if (debug) {
/*  416: 545 */       outputCommandDebug(command, log);
/*  417:     */     }
/*  418: 548 */     return results;
/*  419:     */   }
/*  420:     */   
/*  421:     */   protected static Object receiveJsonVariableValue(String varName, OutputStream outputStream, InputStream inputStream, Logger log, boolean debug)
/*  422:     */     throws WekaException
/*  423:     */   {
/*  424: 567 */     Object variableValue = "";
/*  425: 568 */     ObjectMapper mapper = JsonFactory.create();
/*  426: 569 */     Map<String, Object> command = new HashMap();
/*  427: 570 */     command.put("command", "get_variable_value");
/*  428: 571 */     command.put("variable_name", varName);
/*  429: 572 */     command.put("variable_encoding", "json");
/*  430: 573 */     command.put("debug", Boolean.valueOf(debug));
/*  431: 574 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  432: 575 */     mapper.writeValue(bos, command);
/*  433: 576 */     byte[] bytes = bos.toByteArray();
/*  434: 577 */     if ((inputStream != null) && (outputStream != null)) {
/*  435:     */       try
/*  436:     */       {
/*  437: 579 */         if (debug) {
/*  438: 580 */           outputCommandDebug(command, log);
/*  439:     */         }
/*  440: 584 */         writeDelimitedToOutputStream(bytes, outputStream);
/*  441:     */         
/*  442: 586 */         bytes = readDelimitedFromInputStream(inputStream);
/*  443: 587 */         Map<String, Object> ack = (Map)mapper.readValue(bytes, Map.class);
/*  444: 588 */         if (!ack.get("response").toString().equals("ok")) {
/*  445: 590 */           throw new WekaException(ack.get("error_message").toString());
/*  446:     */         }
/*  447: 592 */         if (!ack.get("variable_name").toString().equals(varName)) {
/*  448: 593 */           throw new WekaException("Server sent back a value for a different variable!");
/*  449:     */         }
/*  450: 596 */         if (!ack.get("variable_encoding").toString().equals("json")) {
/*  451: 597 */           throw new WekaException("Encoding of variable value received from server is not Json!");
/*  452:     */         }
/*  453: 600 */         variableValue = ack.get("variable_value");
/*  454:     */       }
/*  455:     */       catch (IOException ex)
/*  456:     */       {
/*  457: 602 */         throw new WekaException(ex);
/*  458:     */       }
/*  459: 604 */     } else if (debug) {
/*  460: 605 */       outputCommandDebug(command, log);
/*  461:     */     }
/*  462: 608 */     return variableValue;
/*  463:     */   }
/*  464:     */   
/*  465:     */   protected static String receivePickledVariableValue(String varName, OutputStream outputStream, InputStream inputStream, boolean plainString, Logger log, boolean debug)
/*  466:     */     throws WekaException
/*  467:     */   {
/*  468: 633 */     String objectValue = "";
/*  469: 634 */     ObjectMapper mapper = JsonFactory.create();
/*  470: 635 */     Map<String, Object> command = new HashMap();
/*  471: 636 */     command.put("command", "get_variable_value");
/*  472: 637 */     command.put("variable_name", varName);
/*  473: 638 */     command.put("variable_encoding", plainString ? "string" : "pickled");
/*  474: 639 */     command.put("debug", Boolean.valueOf(debug));
/*  475: 640 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  476: 641 */     mapper.writeValue(bos, command);
/*  477: 642 */     byte[] bytes = bos.toByteArray();
/*  478: 644 */     if ((inputStream != null) && (outputStream != null)) {
/*  479:     */       try
/*  480:     */       {
/*  481: 646 */         if (debug) {
/*  482: 647 */           outputCommandDebug(command, log);
/*  483:     */         }
/*  484: 651 */         writeDelimitedToOutputStream(bytes, outputStream);
/*  485:     */         
/*  486: 653 */         bytes = readDelimitedFromInputStream(inputStream);
/*  487: 654 */         Map<String, Object> ack = (Map)mapper.readValue(bytes, Map.class);
/*  488: 655 */         if (!ack.get("response").toString().equals("ok")) {
/*  489: 657 */           throw new WekaException(ack.get("error_message").toString());
/*  490:     */         }
/*  491: 659 */         if (!ack.get("variable_name").toString().equals(varName)) {
/*  492: 660 */           throw new WekaException("Server sent back a value for a different variable!");
/*  493:     */         }
/*  494: 663 */         objectValue = ack.get("variable_value").toString();
/*  495:     */       }
/*  496:     */       catch (IOException ex)
/*  497:     */       {
/*  498: 665 */         throw new WekaException(ex);
/*  499:     */       }
/*  500: 667 */     } else if (debug) {
/*  501: 668 */       outputCommandDebug(command, log);
/*  502:     */     }
/*  503: 671 */     return objectValue;
/*  504:     */   }
/*  505:     */   
/*  506:     */   protected static List<String> receiveDebugBuffer(OutputStream outputStream, InputStream inputStream, Logger log, boolean debug)
/*  507:     */     throws WekaException
/*  508:     */   {
/*  509: 688 */     List<String> stdOutStdErr = new ArrayList();
/*  510:     */     
/*  511: 690 */     ObjectMapper mapper = JsonFactory.create();
/*  512: 691 */     Map<String, Object> command = new HashMap();
/*  513: 692 */     command.put("command", "get_debug_buffer");
/*  514: 693 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  515: 694 */     mapper.writeValue(bos, command);
/*  516: 695 */     byte[] bytes = bos.toByteArray();
/*  517: 697 */     if ((inputStream != null) && (outputStream != null)) {
/*  518:     */       try
/*  519:     */       {
/*  520: 699 */         if (debug) {
/*  521: 700 */           outputCommandDebug(command, log);
/*  522:     */         }
/*  523: 704 */         writeDelimitedToOutputStream(bytes, outputStream);
/*  524:     */         
/*  525: 706 */         bytes = readDelimitedFromInputStream(inputStream);
/*  526: 707 */         Map<String, Object> ack = (Map)mapper.readValue(bytes, Map.class);
/*  527: 708 */         if (!ack.get("response").toString().equals("ok")) {
/*  528: 710 */           throw new WekaException(ack.get("error_message").toString());
/*  529:     */         }
/*  530: 712 */         Object stOut = ack.get("std_out");
/*  531: 713 */         stdOutStdErr.add(stOut != null ? stOut.toString() : "");
/*  532: 714 */         Object stdErr = ack.get("std_err");
/*  533: 715 */         stdOutStdErr.add(stdErr != null ? stdErr.toString() : "");
/*  534:     */       }
/*  535:     */       catch (IOException ex)
/*  536:     */       {
/*  537: 717 */         throw new WekaException(ex);
/*  538:     */       }
/*  539: 719 */     } else if (debug) {
/*  540: 720 */       outputCommandDebug(command, log);
/*  541:     */     }
/*  542: 723 */     return stdOutStdErr;
/*  543:     */   }
/*  544:     */   
/*  545:     */   protected static void sendPickledVariableValue(String varName, String varValue, OutputStream outputStream, InputStream inputStream, Logger log, boolean debug)
/*  546:     */     throws WekaException
/*  547:     */   {
/*  548: 742 */     ObjectMapper mapper = JsonFactory.create();
/*  549: 743 */     Map<String, Object> command = new HashMap();
/*  550: 744 */     command.put("command", "set_variable_value");
/*  551: 745 */     command.put("variable_name", varName);
/*  552: 746 */     command.put("variable_encoding", "pickled");
/*  553: 747 */     command.put("variable_value", varValue);
/*  554: 748 */     command.put("debug", Boolean.valueOf(debug));
/*  555: 749 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  556: 750 */     mapper.writeValue(bos, command);
/*  557: 751 */     byte[] bytes = bos.toByteArray();
/*  558: 752 */     if ((inputStream != null) && (outputStream != null)) {
/*  559:     */       try
/*  560:     */       {
/*  561: 754 */         if (debug) {
/*  562: 755 */           outputCommandDebug(command, log);
/*  563:     */         }
/*  564: 758 */         writeDelimitedToOutputStream(bytes, outputStream);
/*  565:     */         
/*  566: 760 */         String serverAck = receiveServerAck(inputStream);
/*  567: 761 */         if (serverAck != null) {
/*  568: 762 */           throw new WekaException(serverAck);
/*  569:     */         }
/*  570:     */       }
/*  571:     */       catch (IOException ex)
/*  572:     */       {
/*  573: 765 */         throw new WekaException(ex);
/*  574:     */       }
/*  575: 767 */     } else if (debug) {
/*  576: 768 */       outputCommandDebug(command, log);
/*  577:     */     }
/*  578:     */   }
/*  579:     */   
/*  580:     */   protected static BufferedImage getPNGImageFromPython(String varName, OutputStream outputStream, InputStream inputStream, Logger log, boolean debug)
/*  581:     */     throws WekaException
/*  582:     */   {
/*  583: 790 */     ObjectMapper mapper = JsonFactory.create();
/*  584: 791 */     Map<String, Object> command = new HashMap();
/*  585: 792 */     command.put("command", "get_image");
/*  586: 793 */     command.put("variable_name", varName);
/*  587: 794 */     command.put("debug", Boolean.valueOf(debug));
/*  588: 795 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  589: 796 */     mapper.writeValue(bos, command);
/*  590: 797 */     byte[] bytes = bos.toByteArray();
/*  591: 799 */     if ((inputStream != null) && (outputStream != null)) {
/*  592:     */       try
/*  593:     */       {
/*  594: 801 */         if (debug) {
/*  595: 802 */           outputCommandDebug(command, log);
/*  596:     */         }
/*  597: 805 */         writeDelimitedToOutputStream(bytes, outputStream);
/*  598:     */         
/*  599: 807 */         bytes = readDelimitedFromInputStream(inputStream);
/*  600: 808 */         mapper = JsonFactory.create();
/*  601: 809 */         Map<String, Object> ack = (Map)mapper.readValue(bytes, Map.class);
/*  602: 810 */         if (!ack.get("response").toString().equals("ok")) {
/*  603: 812 */           throw new WekaException(ack.get("error_message").toString());
/*  604:     */         }
/*  605: 815 */         if (!ack.get("variable_name").toString().equals(varName)) {
/*  606: 816 */           throw new WekaException("Server sent back a response for a different variable!");
/*  607:     */         }
/*  608: 820 */         String encoding = ack.get("encoding").toString();
/*  609: 821 */         String imageData = ack.get("image_data").toString();
/*  610:     */         byte[] imageBytes;
/*  611:     */         byte[] imageBytes;
/*  612: 823 */         if (encoding.equals("base64")) {
/*  613: 824 */           imageBytes = Base64.decodeBase64(imageData.getBytes());
/*  614:     */         } else {
/*  615: 826 */           imageBytes = imageData.getBytes();
/*  616:     */         }
/*  617: 828 */         return ImageIO.read(new BufferedInputStream(new ByteArrayInputStream(imageBytes)));
/*  618:     */       }
/*  619:     */       catch (IOException ex)
/*  620:     */       {
/*  621: 831 */         throw new WekaException(ex);
/*  622:     */       }
/*  623:     */     }
/*  624: 834 */     outputCommandDebug(command, log);
/*  625:     */     
/*  626: 836 */     return null;
/*  627:     */   }
/*  628:     */   
/*  629:     */   protected static PythonSession.PythonVariableType getPythonVariableType(String varName, OutputStream outputStream, InputStream inputStream, Logger log, boolean debug)
/*  630:     */     throws WekaException
/*  631:     */   {
/*  632: 855 */     ObjectMapper mapper = JsonFactory.create();
/*  633: 856 */     Map<String, Object> command = new HashMap();
/*  634: 857 */     command.put("command", "get_variable_type");
/*  635: 858 */     command.put("variable_name", varName);
/*  636: 859 */     command.put("debug", Boolean.valueOf(debug));
/*  637: 860 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  638: 861 */     mapper.writeValue(bos, command);
/*  639: 862 */     byte[] bytes = bos.toByteArray();
/*  640: 864 */     if ((inputStream != null) && (outputStream != null)) {
/*  641:     */       try
/*  642:     */       {
/*  643: 866 */         if (debug) {
/*  644: 867 */           outputCommandDebug(command, log);
/*  645:     */         }
/*  646: 870 */         writeDelimitedToOutputStream(bytes, outputStream);
/*  647:     */         
/*  648: 872 */         bytes = readDelimitedFromInputStream(inputStream);
/*  649: 873 */         mapper = JsonFactory.create();
/*  650: 874 */         Map<String, Object> ack = (Map)mapper.readValue(bytes, Map.class);
/*  651: 875 */         if (!ack.get("response").toString().equals("ok")) {
/*  652: 877 */           throw new WekaException(ack.get("error_message").toString());
/*  653:     */         }
/*  654: 880 */         if (!ack.get("variable_name").toString().equals(varName)) {
/*  655: 881 */           throw new WekaException("Server sent back a response for a different variable!");
/*  656:     */         }
/*  657: 885 */         String varType = ack.get("type").toString();
/*  658: 886 */         PythonSession.PythonVariableType pvt = PythonSession.PythonVariableType.Unknown;
/*  659: 888 */         for (PythonSession.PythonVariableType t : PythonSession.PythonVariableType.values()) {
/*  660: 890 */           if (t.toString().toLowerCase().equals(varType))
/*  661:     */           {
/*  662: 891 */             pvt = t;
/*  663: 892 */             break;
/*  664:     */           }
/*  665:     */         }
/*  666: 895 */         return pvt;
/*  667:     */       }
/*  668:     */       catch (IOException ex)
/*  669:     */       {
/*  670: 897 */         throw new WekaException(ex);
/*  671:     */       }
/*  672:     */     }
/*  673: 900 */     outputCommandDebug(command, log);
/*  674:     */     
/*  675:     */ 
/*  676: 903 */     return PythonSession.PythonVariableType.Unknown;
/*  677:     */   }
/*  678:     */   
/*  679:     */   protected static boolean checkIfPythonVariableIsSet(String varName, OutputStream outputStream, InputStream inputStream, Logger log, boolean debug)
/*  680:     */     throws WekaException
/*  681:     */   {
/*  682: 922 */     ObjectMapper mapper = JsonFactory.create();
/*  683: 923 */     Map<String, Object> command = new HashMap();
/*  684: 924 */     command.put("command", "variable_is_set");
/*  685: 925 */     command.put("variable_name", varName);
/*  686: 926 */     command.put("debug", Boolean.valueOf(debug));
/*  687: 927 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  688: 928 */     mapper.writeValue(bos, command);
/*  689: 929 */     byte[] bytes = bos.toByteArray();
/*  690: 931 */     if ((inputStream != null) && (outputStream != null)) {
/*  691:     */       try
/*  692:     */       {
/*  693: 933 */         if (debug) {
/*  694: 934 */           outputCommandDebug(command, log);
/*  695:     */         }
/*  696: 937 */         writeDelimitedToOutputStream(bytes, outputStream);
/*  697:     */         
/*  698: 939 */         bytes = readDelimitedFromInputStream(inputStream);
/*  699: 940 */         mapper = JsonFactory.create();
/*  700: 941 */         Map<String, Object> ack = (Map)mapper.readValue(bytes, Map.class);
/*  701: 942 */         if (!ack.get("response").toString().equals("ok")) {
/*  702: 944 */           throw new WekaException(ack.get("error_message").toString());
/*  703:     */         }
/*  704: 947 */         if (!ack.get("variable_name").toString().equals(varName)) {
/*  705: 948 */           throw new WekaException("Server sent back a response for a different variable!");
/*  706:     */         }
/*  707: 952 */         return ((Boolean)ack.get("variable_exists")).booleanValue();
/*  708:     */       }
/*  709:     */       catch (IOException ex)
/*  710:     */       {
/*  711: 954 */         throw new WekaException(ex);
/*  712:     */       }
/*  713:     */     }
/*  714: 956 */     if (debug) {
/*  715: 957 */       outputCommandDebug(command, log);
/*  716:     */     }
/*  717: 961 */     return true;
/*  718:     */   }
/*  719:     */   
/*  720:     */   protected static Instances receiveInstances(String frameName, OutputStream outputStream, InputStream inputStream, Logger log, boolean debug)
/*  721:     */     throws WekaException
/*  722:     */   {
/*  723: 981 */     ObjectMapper mapper = JsonFactory.create();
/*  724: 982 */     Map<String, Object> command = new HashMap();
/*  725: 983 */     command.put("command", "get_instances");
/*  726: 984 */     command.put("frame_name", frameName);
/*  727: 985 */     command.put("debug", Boolean.valueOf(debug));
/*  728: 986 */     ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*  729: 987 */     mapper.writeValue(bos, command);
/*  730: 988 */     byte[] bytes = bos.toByteArray();
/*  731: 990 */     if ((inputStream != null) && (outputStream != null)) {
/*  732:     */       try
/*  733:     */       {
/*  734: 992 */         if (debug) {
/*  735: 993 */           outputCommandDebug(command, log);
/*  736:     */         }
/*  737: 997 */         writeDelimitedToOutputStream(bytes, outputStream);
/*  738: 998 */         String serverAck = receiveServerAck(inputStream);
/*  739: 999 */         if (serverAck != null) {
/*  740:1000 */           throw new WekaException(serverAck);
/*  741:     */         }
/*  742:1004 */         bytes = readDelimitedFromInputStream(inputStream);
/*  743:1005 */         Map<String, Object> headerResponse = (Map)mapper.readValue(bytes, Map.class);
/*  744:1006 */         if (headerResponse == null) {
/*  745:1007 */           throw new WekaException("Map is null!");
/*  746:     */         }
/*  747:1009 */         if (headerResponse.get("response").toString().equals("instances_header"))
/*  748:     */         {
/*  749:1011 */           if (debug) {
/*  750:1012 */             if (log != null) {
/*  751:1013 */               log.logMessage("Received header response command with " + headerResponse.get("num_instances") + " instances");
/*  752:     */             } else {
/*  753:1016 */               System.err.println("Received header response command with " + headerResponse.get("num_instances") + " instances");
/*  754:     */             }
/*  755:     */           }
/*  756:     */         }
/*  757:     */         else {
/*  758:1021 */           throw new WekaException("Unknown response type from server");
/*  759:     */         }
/*  760:1024 */         Instances header = jsonToInstancesHeader((Map)headerResponse.get("header"));
/*  761:     */         
/*  762:     */ 
/*  763:     */ 
/*  764:     */ 
/*  765:     */ 
/*  766:1030 */         bytes = readDelimitedFromInputStream(inputStream);
/*  767:1031 */         String CSV = new String(bytes);
/*  768:1032 */         StringBuilder b = new StringBuilder();
/*  769:1033 */         b.append(header.toString()).append("\n");
/*  770:1034 */         b.append(CSV).append("\n");
/*  771:     */         
/*  772:1036 */         return new Instances(new StringReader(b.toString()));
/*  773:     */       }
/*  774:     */       catch (IOException ex)
/*  775:     */       {
/*  776:1038 */         throw new WekaException(ex);
/*  777:     */       }
/*  778:     */     }
/*  779:1040 */     if (debug) {
/*  780:1041 */       outputCommandDebug(command, log);
/*  781:     */     }
/*  782:1043 */     return null;
/*  783:     */   }
/*  784:     */   
/*  785:     */   protected static void writeDelimitedToOutputStream(byte[] bytes, OutputStream outputStream)
/*  786:     */     throws IOException
/*  787:     */   {
/*  788:1057 */     outputStream.write(ByteBuffer.allocate(4).putInt(bytes.length).array());
/*  789:     */     
/*  790:     */ 
/*  791:1060 */     outputStream.write(bytes);
/*  792:     */   }
/*  793:     */   
/*  794:     */   protected static byte[] readDelimitedFromInputStream(InputStream inputStream)
/*  795:     */     throws IOException
/*  796:     */   {
/*  797:1072 */     byte[] sizeBytes = new byte[4];
/*  798:1073 */     int numRead = inputStream.read(sizeBytes, 0, 4);
/*  799:1074 */     if (numRead < 4) {
/*  800:1075 */       throw new IOException("Failed to read the message size from the input stream!");
/*  801:     */     }
/*  802:1079 */     int messageLength = ByteBuffer.wrap(sizeBytes).getInt();
/*  803:1080 */     byte[] messageData = new byte[messageLength];
/*  804:1083 */     for (numRead = 0; numRead < messageLength;)
/*  805:     */     {
/*  806:1084 */       int currentNumRead = inputStream.read(messageData, numRead, messageLength - numRead);
/*  807:1086 */       if (currentNumRead < 0) {
/*  808:1087 */         throw new IOException("Unexpected end of stream!");
/*  809:     */       }
/*  810:1089 */       numRead += currentNumRead;
/*  811:     */     }
/*  812:1092 */     return messageData;
/*  813:     */   }
/*  814:     */   
/*  815:     */   protected static void outputCommandDebug(Map<String, Object> command, Logger log)
/*  816:     */   {
/*  817:1103 */     ObjectMapper mapper = JsonFactory.create();
/*  818:1104 */     String serialized = mapper.writeValueAsString(command);
/*  819:1105 */     if (log != null)
/*  820:     */     {
/*  821:1106 */       log.logMessage("Sending command:\n" + serialized);
/*  822:     */     }
/*  823:     */     else
/*  824:     */     {
/*  825:1108 */       System.err.println("Sending command: ");
/*  826:1109 */       Boon.puts(new Object[] { serialized });
/*  827:     */     }
/*  828:     */   }
/*  829:     */   
/*  830:     */   public static void main(String[] args)
/*  831:     */   {
/*  832:     */     try
/*  833:     */     {
/*  834:1115 */       Instances insts = new Instances(new FileReader(args[0]));
/*  835:1116 */       insts.setClassIndex(insts.numAttributes() - 1);
/*  836:1117 */       sendInstances(insts, "test", null, null, null, false);
/*  837:     */     }
/*  838:     */     catch (Exception ex)
/*  839:     */     {
/*  840:1119 */       ex.printStackTrace();
/*  841:     */     }
/*  842:     */   }
/*  843:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.python.ServerUtils
 * JD-Core Version:    0.7.0.1
 */