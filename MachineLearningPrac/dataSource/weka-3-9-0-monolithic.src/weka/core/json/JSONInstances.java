/*   1:    */ package weka.core.json;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.DenseInstance;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.SparseInstance;
/*  10:    */ import weka.core.Utils;
/*  11:    */ import weka.core.converters.ConverterUtils.DataSource;
/*  12:    */ 
/*  13:    */ public class JSONInstances
/*  14:    */ {
/*  15:    */   public static final String HEADER = "header";
/*  16:    */   public static final String DATA = "data";
/*  17:    */   public static final String RELATION = "relation";
/*  18:    */   public static final String ATTRIBUTES = "attributes";
/*  19:    */   public static final String NAME = "name";
/*  20:    */   public static final String TYPE = "type";
/*  21:    */   public static final String CLASS = "class";
/*  22:    */   public static final String LABELS = "labels";
/*  23:    */   public static final String WEIGHT = "weight";
/*  24:    */   public static final String DATEFORMAT = "dateformat";
/*  25:    */   public static final String SPARSE = "sparse";
/*  26:    */   public static final String VALUES = "values";
/*  27:    */   public static final String SPARSE_SEPARATOR = ":";
/*  28:    */   public static final String MISSING_VALUE = "?";
/*  29:    */   
/*  30:    */   protected static Attribute toAttribute(JSONNode att, boolean[] classAtt)
/*  31:    */   {
/*  32:103 */     String name = (String)att.getChild("name").getValue("noname");
/*  33:104 */     String type = (String)att.getChild("type").getValue("");
/*  34:105 */     double weight = ((Double)att.getChild("weight").getValue(new Double(1.0D))).doubleValue();
/*  35:    */     Attribute result;
/*  36:106 */     if (type.equals(Attribute.typeToString(0)))
/*  37:    */     {
/*  38:107 */       result = new Attribute(name);
/*  39:    */     }
/*  40:    */     else
/*  41:    */     {
/*  42:    */       Attribute result;
/*  43:109 */       if (type.equals(Attribute.typeToString(1)))
/*  44:    */       {
/*  45:110 */         JSONNode labels = att.getChild("labels");
/*  46:111 */         ArrayList<String> values = new ArrayList();
/*  47:112 */         for (int i = 0; i < labels.getChildCount(); i++)
/*  48:    */         {
/*  49:113 */           String label = (String)((JSONNode)labels.getChildAt(i)).getValue();
/*  50:114 */           if (label.equals("'?'")) {
/*  51:115 */             values.add("?");
/*  52:    */           } else {
/*  53:117 */             values.add(label);
/*  54:    */           }
/*  55:    */         }
/*  56:119 */         result = new Attribute(name, values);
/*  57:    */       }
/*  58:    */       else
/*  59:    */       {
/*  60:    */         Attribute result;
/*  61:121 */         if (type.equals(Attribute.typeToString(3)))
/*  62:    */         {
/*  63:122 */           String dateformat = (String)att.getChild("dateformat").getValue("yyyy-MM-dd'T'HH:mm:ss");
/*  64:123 */           result = new Attribute(name, dateformat);
/*  65:    */         }
/*  66:    */         else
/*  67:    */         {
/*  68:    */           Attribute result;
/*  69:125 */           if (type.equals(Attribute.typeToString(2)))
/*  70:    */           {
/*  71:126 */             result = new Attribute(name, (ArrayList)null);
/*  72:    */           }
/*  73:    */           else
/*  74:    */           {
/*  75:129 */             System.err.println("Unhandled attribute type '" + type + "'!");
/*  76:130 */             return null;
/*  77:    */           }
/*  78:    */         }
/*  79:    */       }
/*  80:    */     }
/*  81:    */     Attribute result;
/*  82:132 */     result.setWeight(weight);
/*  83:    */     
/*  84:134 */     return result;
/*  85:    */   }
/*  86:    */   
/*  87:    */   protected static Instance toInstance(JSONNode inst, Instances data)
/*  88:    */   {
/*  89:155 */     boolean sparse = ((Boolean)inst.getChild("sparse").getValue(new Boolean(false))).booleanValue();
/*  90:156 */     double weight = ((Double)inst.getChild("weight").getValue(new Double(1.0D))).doubleValue();
/*  91:157 */     JSONNode values = inst.getChild("values");
/*  92:158 */     double[] vals = new double[data.numAttributes()];
/*  93:159 */     for (int i = 0; i < values.getChildCount(); i++)
/*  94:    */     {
/*  95:    */       int index;
/*  96:    */       String value;
/*  97:160 */       if (sparse)
/*  98:    */       {
/*  99:161 */         String value = "" + ((JSONNode)values.getChildAt(i)).getValue();
/* 100:162 */         int pos = value.indexOf(":");
/* 101:163 */         int index = Integer.parseInt(value.substring(0, pos));
/* 102:164 */         value = value.substring(pos + 1);
/* 103:    */       }
/* 104:    */       else
/* 105:    */       {
/* 106:167 */         index = i;
/* 107:168 */         value = "" + ((JSONNode)values.getChildAt(i)).getValue();
/* 108:    */       }
/* 109:    */       try
/* 110:    */       {
/* 111:172 */         if (value.equals("?"))
/* 112:    */         {
/* 113:173 */           vals[index] = Utils.missingValue();
/* 114:    */         }
/* 115:    */         else
/* 116:    */         {
/* 117:177 */           if (value.equals("'?'")) {
/* 118:178 */             value = "?";
/* 119:    */           }
/* 120:179 */           if ((data.attribute(index).isNumeric()) && (!data.attribute(index).isDate()))
/* 121:    */           {
/* 122:180 */             vals[index] = Double.parseDouble(value);
/* 123:    */           }
/* 124:182 */           else if (data.attribute(index).isNominal())
/* 125:    */           {
/* 126:183 */             vals[index] = data.attribute(index).indexOfValue(value);
/* 127:184 */             if ((vals[index] == -1.0D) && (value.startsWith("'")) && (value.endsWith("'"))) {
/* 128:185 */               vals[index] = data.attribute(index).indexOfValue(Utils.unquote(value));
/* 129:    */             }
/* 130:187 */             if ((vals[index] == -1.0D) && (value.startsWith("'")) && (value.endsWith("'"))) {
/* 131:188 */               vals[index] = data.attribute(index).indexOfValue(Utils.unbackQuoteChars(Utils.unquote(value)));
/* 132:    */             }
/* 133:189 */             if (vals[index] == -1.0D)
/* 134:    */             {
/* 135:190 */               System.err.println("Unknown label '" + value + "' for attribute #" + (index + 1) + "!");
/* 136:191 */               return null;
/* 137:    */             }
/* 138:    */           }
/* 139:194 */           else if (data.attribute(index).isDate())
/* 140:    */           {
/* 141:195 */             vals[index] = data.attribute(index).parseDate(value);
/* 142:    */           }
/* 143:197 */           else if (data.attribute(index).isString())
/* 144:    */           {
/* 145:198 */             vals[index] = data.attribute(index).addStringValue(value);
/* 146:    */           }
/* 147:    */           else
/* 148:    */           {
/* 149:201 */             System.err.println("Unhandled attribute type '" + Attribute.typeToString(data.attribute(index).type()) + "'!");
/* 150:202 */             return null;
/* 151:    */           }
/* 152:    */         }
/* 153:    */       }
/* 154:    */       catch (Exception e)
/* 155:    */       {
/* 156:207 */         System.err.println("Error parsing value #" + (index + 1) + ": " + e.toString());
/* 157:208 */         return null;
/* 158:    */       }
/* 159:    */     }
/* 160:    */     Instance result;
/* 161:    */     Instance result;
/* 162:212 */     if (sparse) {
/* 163:213 */       result = new SparseInstance(weight, vals);
/* 164:    */     } else {
/* 165:215 */       result = new DenseInstance(weight, vals);
/* 166:    */     }
/* 167:216 */     result.setDataset(data);
/* 168:    */     
/* 169:218 */     return result;
/* 170:    */   }
/* 171:    */   
/* 172:    */   protected static Instances toInstances(JSONNode json, boolean onlyHeader)
/* 173:    */   {
/* 174:240 */     JSONNode header = json.getChild("header");
/* 175:241 */     if (header == null)
/* 176:    */     {
/* 177:242 */       System.err.println("No 'header' section!");
/* 178:243 */       return null;
/* 179:    */     }
/* 180:245 */     JSONNode data = json.getChild("data");
/* 181:246 */     if (data == null)
/* 182:    */     {
/* 183:247 */       System.err.println("No 'data' section!");
/* 184:248 */       return null;
/* 185:    */     }
/* 186:252 */     JSONNode attributes = header.getChild("attributes");
/* 187:253 */     if (attributes == null)
/* 188:    */     {
/* 189:254 */       System.err.println("No 'attributes' array!");
/* 190:255 */       return null;
/* 191:    */     }
/* 192:257 */     ArrayList<Attribute> atts = new ArrayList();
/* 193:258 */     boolean[] classAtt = new boolean[1];
/* 194:259 */     int classIndex = -1;
/* 195:260 */     for (int i = 0; i < attributes.getChildCount(); i++)
/* 196:    */     {
/* 197:261 */       Attribute att = toAttribute((JSONNode)attributes.getChildAt(i), classAtt);
/* 198:262 */       if (att == null)
/* 199:    */       {
/* 200:263 */         System.err.println("Could not convert attribute #" + (i + 1) + "!");
/* 201:264 */         return null;
/* 202:    */       }
/* 203:266 */       if (classAtt[0] != 0) {
/* 204:267 */         classIndex = i;
/* 205:    */       }
/* 206:268 */       atts.add(att);
/* 207:    */     }
/* 208:270 */     Instances result = new Instances(header.getChild("relation").getValue("unknown").toString(), atts, onlyHeader ? 0 : data.getChildCount());
/* 209:    */     
/* 210:    */ 
/* 211:    */ 
/* 212:274 */     result.setClassIndex(classIndex);
/* 213:277 */     if (!onlyHeader) {
/* 214:278 */       for (i = 0; i < data.getChildCount(); i++)
/* 215:    */       {
/* 216:279 */         Instance inst = toInstance((JSONNode)data.getChildAt(i), result);
/* 217:280 */         if (inst == null)
/* 218:    */         {
/* 219:281 */           System.err.println("Could not convert instance #" + (i + 1) + "!");
/* 220:282 */           return null;
/* 221:    */         }
/* 222:284 */         result.add(inst);
/* 223:    */       }
/* 224:    */     }
/* 225:288 */     return result;
/* 226:    */   }
/* 227:    */   
/* 228:    */   public static Instances toInstances(JSONNode json)
/* 229:    */   {
/* 230:298 */     return toInstances(json, false);
/* 231:    */   }
/* 232:    */   
/* 233:    */   public static Instances toHeader(JSONNode json)
/* 234:    */   {
/* 235:308 */     return toInstances(json, true);
/* 236:    */   }
/* 237:    */   
/* 238:    */   protected static JSONNode toJSON(Instances inst, Attribute att)
/* 239:    */   {
/* 240:323 */     JSONNode result = new JSONNode();
/* 241:    */     
/* 242:325 */     result.addPrimitive("name", att.name());
/* 243:326 */     result.addPrimitive("type", Attribute.typeToString(att));
/* 244:327 */     result.addPrimitive("class", Boolean.valueOf(att.index() == inst.classIndex()));
/* 245:328 */     result.addPrimitive("weight", Double.valueOf(att.weight()));
/* 246:329 */     if (att.isNominal())
/* 247:    */     {
/* 248:330 */       JSONNode labels = result.addArray("labels");
/* 249:331 */       for (int i = 0; i < att.numValues(); i++) {
/* 250:332 */         if (att.value(i).equals("?")) {
/* 251:333 */           labels.addArrayElement("'" + att.value(i) + "'");
/* 252:    */         } else {
/* 253:335 */           labels.addArrayElement(att.value(i));
/* 254:    */         }
/* 255:    */       }
/* 256:    */     }
/* 257:338 */     if (att.isDate()) {
/* 258:339 */       result.addPrimitive("dateformat", att.getDateFormat());
/* 259:    */     }
/* 260:341 */     return result;
/* 261:    */   }
/* 262:    */   
/* 263:    */   protected static JSONNode toJSON(Instance inst)
/* 264:    */   {
/* 265:356 */     JSONNode result = new JSONNode();
/* 266:    */     
/* 267:358 */     boolean sparse = inst instanceof SparseInstance;
/* 268:359 */     result.addPrimitive("sparse", Boolean.valueOf(sparse));
/* 269:360 */     result.addPrimitive("weight", Double.valueOf(inst.weight()));
/* 270:361 */     JSONNode values = result.addArray("values");
/* 271:362 */     if (sparse) {
/* 272:363 */       for (int i = 0; i < inst.numValues(); i++) {
/* 273:364 */         if (inst.isMissing(inst.index(i))) {
/* 274:365 */           values.addArrayElement(inst.index(i) + ":" + "?");
/* 275:366 */         } else if (inst.toString(inst.index(i)).equals("'?'")) {
/* 276:367 */           values.addArrayElement(inst.index(i) + ":" + "'" + "?" + "'");
/* 277:    */         } else {
/* 278:369 */           values.addArrayElement(inst.index(i) + ":" + inst.toString(inst.index(i)));
/* 279:    */         }
/* 280:    */       }
/* 281:    */     }
/* 282:373 */     for (int i = 0; i < inst.numAttributes(); i++) {
/* 283:374 */       if (inst.isMissing(i)) {
/* 284:375 */         values.addArrayElement("?");
/* 285:376 */       } else if (inst.toString(i).equals("'?'")) {
/* 286:377 */         values.addArrayElement("'?'");
/* 287:    */       } else {
/* 288:379 */         values.addArrayElement(inst.toString(i));
/* 289:    */       }
/* 290:    */     }
/* 291:383 */     return result;
/* 292:    */   }
/* 293:    */   
/* 294:    */   public static JSONNode toJSON(Instances inst)
/* 295:    */   {
/* 296:399 */     JSONNode result = new JSONNode();
/* 297:    */     
/* 298:    */ 
/* 299:402 */     JSONNode header = result.addObject("header");
/* 300:403 */     header.addPrimitive("relation", inst.relationName());
/* 301:404 */     JSONNode atts = header.addArray("attributes");
/* 302:405 */     for (int i = 0; i < inst.numAttributes(); i++) {
/* 303:406 */       atts.add(toJSON(inst, inst.attribute(i)));
/* 304:    */     }
/* 305:409 */     JSONNode data = result.addArray("data");
/* 306:410 */     for (i = 0; i < inst.numInstances(); i++) {
/* 307:411 */       data.add(toJSON(inst.instance(i)));
/* 308:    */     }
/* 309:413 */     return result;
/* 310:    */   }
/* 311:    */   
/* 312:    */   public static void main(String[] args)
/* 313:    */     throws Exception
/* 314:    */   {
/* 315:423 */     if (args.length != 1)
/* 316:    */     {
/* 317:424 */       System.err.println("No dataset supplied!");
/* 318:425 */       System.exit(1);
/* 319:    */     }
/* 320:429 */     Instances data = ConverterUtils.DataSource.read(args[0]);
/* 321:    */     
/* 322:    */ 
/* 323:432 */     JSONNode json = toJSON(data);
/* 324:433 */     StringBuffer buffer = new StringBuffer();
/* 325:434 */     json.toString(buffer);
/* 326:435 */     System.out.println(buffer.toString());
/* 327:    */     
/* 328:    */ 
/* 329:438 */     Instances inst = toInstances(json);
/* 330:439 */     System.out.println(inst);
/* 331:    */   }
/* 332:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.json.JSONInstances
 * JD-Core Version:    0.7.0.1
 */