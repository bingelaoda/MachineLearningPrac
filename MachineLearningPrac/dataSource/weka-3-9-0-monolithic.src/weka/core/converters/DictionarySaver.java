/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.BufferedOutputStream;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.OutputStream;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.DictionaryBuilder;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.OptionMetadata;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.stemmers.NullStemmer;
/*  14:    */ import weka.core.stemmers.Stemmer;
/*  15:    */ import weka.core.stopwords.Null;
/*  16:    */ import weka.core.stopwords.StopwordsHandler;
/*  17:    */ import weka.core.tokenizers.Tokenizer;
/*  18:    */ 
/*  19:    */ public class DictionarySaver
/*  20:    */   extends AbstractFileSaver
/*  21:    */   implements BatchConverter, IncrementalConverter
/*  22:    */ {
/*  23:    */   private static final long serialVersionUID = -19891905988830722L;
/*  24:    */   protected transient OutputStream m_binaryStream;
/*  25:142 */   protected DictionaryBuilder m_dictionaryBuilder = new DictionaryBuilder();
/*  26:    */   protected boolean m_dictionaryIsBinary;
/*  27:    */   private long m_periodicPruningRate;
/*  28:    */   
/*  29:    */   public DictionarySaver()
/*  30:    */   {
/*  31:156 */     resetOptions();
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String globalInfo()
/*  35:    */   {
/*  36:166 */     return "Writes a dictionary constructed from string attributes in incoming instances to a destination.";
/*  37:    */   }
/*  38:    */   
/*  39:    */   @OptionMetadata(displayName="Save dictionary in binary form", description="Save as a binary serialized dictionary", commandLineParamName="binary-dict", commandLineParamSynopsis="-binary-dict", commandLineParamIsFlag=true, displayOrder=2)
/*  40:    */   public void setSaveBinaryDictionary(boolean binary)
/*  41:    */   {
/*  42:183 */     this.m_dictionaryIsBinary = binary;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public boolean getSaveBinaryDictionary()
/*  46:    */   {
/*  47:194 */     return this.m_dictionaryIsBinary;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public String getAttributeIndices()
/*  51:    */   {
/*  52:203 */     return this.m_dictionaryBuilder.getAttributeIndices();
/*  53:    */   }
/*  54:    */   
/*  55:    */   @OptionMetadata(displayName="Range of attributes to operate on", description="Specify range of attributes to act on. This is a comma separated list of attribute\nindices, with \"first\" and \"last\" valid values.", commandLineParamName="R", commandLineParamSynopsis="-R <range>", displayOrder=4)
/*  56:    */   public void setAttributeIndices(String rangeList)
/*  57:    */   {
/*  58:221 */     this.m_dictionaryBuilder.setAttributeIndices(rangeList);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public boolean getInvertSelection()
/*  62:    */   {
/*  63:230 */     return this.m_dictionaryBuilder.getInvertSelection();
/*  64:    */   }
/*  65:    */   
/*  66:    */   @OptionMetadata(displayName="Invert selection", description="Set attributes selection mode. If false, only selected attributes in the range will\nbe worked on. If true, only non-selected attributes will be processed", commandLineParamName="V", commandLineParamSynopsis="-V", commandLineParamIsFlag=true, displayOrder=5)
/*  67:    */   public void setInvertSelection(boolean invert)
/*  68:    */   {
/*  69:247 */     this.m_dictionaryBuilder.setInvertSelection(invert);
/*  70:    */   }
/*  71:    */   
/*  72:    */   public boolean getLowerCaseTokens()
/*  73:    */   {
/*  74:256 */     return this.m_dictionaryBuilder.getLowerCaseTokens();
/*  75:    */   }
/*  76:    */   
/*  77:    */   @OptionMetadata(displayName="Lower case tokens", description="Convert all tokens to lowercase when matching against dictionary entries.", commandLineParamName="L", commandLineParamSynopsis="-L", commandLineParamIsFlag=true, displayOrder=10)
/*  78:    */   public void setLowerCaseTokens(boolean downCaseTokens)
/*  79:    */   {
/*  80:272 */     this.m_dictionaryBuilder.setLowerCaseTokens(downCaseTokens);
/*  81:    */   }
/*  82:    */   
/*  83:    */   @OptionMetadata(displayName="Stemmer to use", description="The stemming algorithm (classname plus parameters) to use.", commandLineParamName="stemmer", commandLineParamSynopsis="-stemmer <spec>", displayOrder=11)
/*  84:    */   public void setStemmer(Stemmer value)
/*  85:    */   {
/*  86:287 */     if (value != null) {
/*  87:288 */       this.m_dictionaryBuilder.setStemmer(value);
/*  88:    */     } else {
/*  89:290 */       this.m_dictionaryBuilder.setStemmer(new NullStemmer());
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   public Stemmer getStemmer()
/*  94:    */   {
/*  95:300 */     return this.m_dictionaryBuilder.getStemmer();
/*  96:    */   }
/*  97:    */   
/*  98:    */   @OptionMetadata(displayName="Stop words handler", description="The stopwords handler to use (default = Null)", commandLineParamName="stopwords-handler", commandLineParamSynopsis="-stopwords-handler <spec>", displayOrder=12)
/*  99:    */   public void setStopwordsHandler(StopwordsHandler value)
/* 100:    */   {
/* 101:313 */     if (value != null) {
/* 102:314 */       this.m_dictionaryBuilder.setStopwordsHandler(value);
/* 103:    */     } else {
/* 104:316 */       this.m_dictionaryBuilder.setStopwordsHandler(new Null());
/* 105:    */     }
/* 106:    */   }
/* 107:    */   
/* 108:    */   public StopwordsHandler getStopwordsHandler()
/* 109:    */   {
/* 110:326 */     return this.m_dictionaryBuilder.getStopwordsHandler();
/* 111:    */   }
/* 112:    */   
/* 113:    */   @OptionMetadata(displayName="Tokenizer", description="The tokenizing algorithm (classname plus parameters) to use.\n(default: weka.core.tokenizers.WordTokenizer)", commandLineParamName="tokenizer", commandLineParamSynopsis="-tokenizer <spec>", displayOrder=13)
/* 114:    */   public void setTokenizer(Tokenizer value)
/* 115:    */   {
/* 116:342 */     this.m_dictionaryBuilder.setTokenizer(value);
/* 117:    */   }
/* 118:    */   
/* 119:    */   public Tokenizer getTokenizer()
/* 120:    */   {
/* 121:351 */     return this.m_dictionaryBuilder.getTokenizer();
/* 122:    */   }
/* 123:    */   
/* 124:    */   public long getPeriodicPruning()
/* 125:    */   {
/* 126:361 */     return this.m_periodicPruningRate;
/* 127:    */   }
/* 128:    */   
/* 129:    */   @OptionMetadata(displayName="Periodic pruning rate", description="Prune the dictionary every x instances\n(default = 0 - i.e. no periodic pruning)", commandLineParamName="P", commandLineParamSynopsis="-P <integer>", displayOrder=14)
/* 130:    */   public void setPeriodicPruning(long newPeriodicPruning)
/* 131:    */   {
/* 132:379 */     this.m_periodicPruningRate = newPeriodicPruning;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public int getWordsToKeep()
/* 136:    */   {
/* 137:390 */     return this.m_dictionaryBuilder.getWordsToKeep();
/* 138:    */   }
/* 139:    */   
/* 140:    */   @OptionMetadata(displayName="Number of words to attempt to keep", description="The number of words (per class if there is a class attribute assigned) to attempt to keep.", commandLineParamName="W", commandLineParamSynopsis="-W <integer>", displayOrder=15)
/* 141:    */   public void setWordsToKeep(int newWordsToKeep)
/* 142:    */   {
/* 143:407 */     this.m_dictionaryBuilder.setWordsToKeep(newWordsToKeep);
/* 144:    */   }
/* 145:    */   
/* 146:    */   public int getMinTermFreq()
/* 147:    */   {
/* 148:416 */     return this.m_dictionaryBuilder.getMinTermFreq();
/* 149:    */   }
/* 150:    */   
/* 151:    */   @OptionMetadata(displayName="Minimum term frequency", description="The minimum term frequency to use when pruning the dictionary\n(default = 1).", commandLineParamName="M", commandLineParamSynopsis="-M <integer>", displayOrder=16)
/* 152:    */   public void setMinTermFreq(int newMinTermFreq)
/* 153:    */   {
/* 154:431 */     this.m_dictionaryBuilder.setMinTermFreq(newMinTermFreq);
/* 155:    */   }
/* 156:    */   
/* 157:    */   public boolean getDoNotOperateOnPerClassBasis()
/* 158:    */   {
/* 159:440 */     return this.m_dictionaryBuilder.getDoNotOperateOnPerClassBasis();
/* 160:    */   }
/* 161:    */   
/* 162:    */   @OptionMetadata(displayName="Do not operate on a per-class basis", description="If this is set, the maximum number of words and the\nminimum term frequency is not enforced on a per-class\nbasis but based on the documents in all the classes\n(even if a class attribute is set).", commandLineParamName="O", commandLineParamSynopsis="-O", commandLineParamIsFlag=true, displayOrder=17)
/* 163:    */   public void setDoNotOperateOnPerClassBasis(boolean newDoNotOperateOnPerClassBasis)
/* 164:    */   {
/* 165:458 */     this.m_dictionaryBuilder.setDoNotOperateOnPerClassBasis(newDoNotOperateOnPerClassBasis);
/* 166:    */   }
/* 167:    */   
/* 168:    */   @OptionMetadata(displayName="Sort dictionary", description="Sort the dictionary alphabetically", commandLineParamName="sort", commandLineParamSynopsis="-sort", commandLineParamIsFlag=true, displayOrder=18)
/* 169:    */   public void setKeepDictionarySorted(boolean sorted)
/* 170:    */   {
/* 171:472 */     this.m_dictionaryBuilder.setSortDictionary(sorted);
/* 172:    */   }
/* 173:    */   
/* 174:    */   public boolean getKeepDictionarySorted()
/* 175:    */   {
/* 176:481 */     return this.m_dictionaryBuilder.getSortDictionary();
/* 177:    */   }
/* 178:    */   
/* 179:    */   public Capabilities getCapabilities()
/* 180:    */   {
/* 181:492 */     Capabilities result = super.getCapabilities();
/* 182:    */     
/* 183:    */ 
/* 184:495 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 185:496 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 186:497 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 187:498 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/* 188:499 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 189:    */     
/* 190:    */ 
/* 191:502 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 192:503 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 193:504 */     result.enable(Capabilities.Capability.DATE_CLASS);
/* 194:505 */     result.enable(Capabilities.Capability.STRING_CLASS);
/* 195:506 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 196:507 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 197:    */     
/* 198:509 */     return result;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public String getFileDescription()
/* 202:    */   {
/* 203:514 */     return "Plain text or binary serialized dictionary files created from text in string attributes";
/* 204:    */   }
/* 205:    */   
/* 206:    */   public void writeIncremental(Instance inst)
/* 207:    */     throws IOException
/* 208:    */   {
/* 209:520 */     int writeMode = getWriteMode();
/* 210:521 */     Instances structure = getInstances();
/* 211:523 */     if ((getRetrieval() == 1) || (getRetrieval() == 0)) {
/* 212:524 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/* 213:    */     }
/* 214:527 */     if (writeMode == 1)
/* 215:    */     {
/* 216:528 */       if (structure == null)
/* 217:    */       {
/* 218:529 */         setWriteMode(2);
/* 219:530 */         if (inst != null) {
/* 220:531 */           throw new IOException("Structure (header Information) has to be set in advance");
/* 221:    */         }
/* 222:    */       }
/* 223:    */       else
/* 224:    */       {
/* 225:535 */         setWriteMode(3);
/* 226:    */       }
/* 227:537 */       writeMode = getWriteMode();
/* 228:    */     }
/* 229:539 */     if (writeMode == 2) {
/* 230:540 */       cancel();
/* 231:    */     }
/* 232:543 */     if (writeMode == 3)
/* 233:    */     {
/* 234:544 */       this.m_dictionaryBuilder.reset();
/* 235:    */       try
/* 236:    */       {
/* 237:546 */         this.m_dictionaryBuilder.setup(structure);
/* 238:    */       }
/* 239:    */       catch (Exception ex)
/* 240:    */       {
/* 241:548 */         throw new IOException(ex);
/* 242:    */       }
/* 243:550 */       setWriteMode(0);
/* 244:551 */       writeMode = getWriteMode();
/* 245:    */     }
/* 246:554 */     if (writeMode == 0)
/* 247:    */     {
/* 248:555 */       if (structure == null) {
/* 249:556 */         throw new IOException("No instances information available.");
/* 250:    */       }
/* 251:559 */       if (inst != null)
/* 252:    */       {
/* 253:560 */         this.m_dictionaryBuilder.processInstance(inst);
/* 254:    */       }
/* 255:    */       else
/* 256:    */       {
/* 257:    */         try
/* 258:    */         {
/* 259:563 */           this.m_dictionaryBuilder.finalizeDictionary();
/* 260:    */         }
/* 261:    */         catch (Exception e)
/* 262:    */         {
/* 263:565 */           throw new IOException(e);
/* 264:    */         }
/* 265:567 */         if ((retrieveFile() == null) && (getWriter() == null))
/* 266:    */         {
/* 267:568 */           if (getSaveBinaryDictionary()) {
/* 268:569 */             throw new IOException("Can't output binary dictionary to standard out!");
/* 269:    */           }
/* 270:572 */           this.m_dictionaryBuilder.saveDictionary(System.out);
/* 271:    */         }
/* 272:574 */         else if (getSaveBinaryDictionary())
/* 273:    */         {
/* 274:575 */           this.m_dictionaryBuilder.saveDictionary(this.m_binaryStream);
/* 275:    */         }
/* 276:    */         else
/* 277:    */         {
/* 278:577 */           this.m_dictionaryBuilder.saveDictionary(getWriter());
/* 279:    */         }
/* 280:581 */         resetStructure();
/* 281:582 */         resetWriter();
/* 282:    */       }
/* 283:    */     }
/* 284:    */   }
/* 285:    */   
/* 286:    */   public void writeBatch()
/* 287:    */     throws IOException
/* 288:    */   {
/* 289:589 */     if (getInstances() == null) {
/* 290:590 */       throw new IOException("No instances to save");
/* 291:    */     }
/* 292:592 */     if (getRetrieval() == 2) {
/* 293:593 */       throw new IOException("Batch and incremental saving cannot be mixed.");
/* 294:    */     }
/* 295:595 */     setRetrieval(1);
/* 296:596 */     setWriteMode(0);
/* 297:    */     
/* 298:598 */     this.m_dictionaryBuilder.reset();
/* 299:    */     try
/* 300:    */     {
/* 301:600 */       this.m_dictionaryBuilder.setup(getInstances());
/* 302:    */     }
/* 303:    */     catch (Exception ex)
/* 304:    */     {
/* 305:602 */       throw new IOException(ex);
/* 306:    */     }
/* 307:605 */     for (int i = 0; i < getInstances().numInstances(); i++) {
/* 308:606 */       this.m_dictionaryBuilder.processInstance(getInstances().instance(i));
/* 309:    */     }
/* 310:    */     try
/* 311:    */     {
/* 312:609 */       this.m_dictionaryBuilder.finalizeDictionary();
/* 313:    */     }
/* 314:    */     catch (Exception ex)
/* 315:    */     {
/* 316:611 */       throw new IOException(ex);
/* 317:    */     }
/* 318:614 */     if ((retrieveFile() == null) && (getWriter() == null))
/* 319:    */     {
/* 320:615 */       if (getSaveBinaryDictionary()) {
/* 321:616 */         throw new IOException("Can't output binary dictionary to standard out!");
/* 322:    */       }
/* 323:619 */       this.m_dictionaryBuilder.saveDictionary(System.out);
/* 324:620 */       setWriteMode(1);
/* 325:621 */       return;
/* 326:    */     }
/* 327:624 */     if (getSaveBinaryDictionary()) {
/* 328:625 */       this.m_dictionaryBuilder.saveDictionary(this.m_binaryStream);
/* 329:    */     } else {
/* 330:627 */       this.m_dictionaryBuilder.saveDictionary(getWriter());
/* 331:    */     }
/* 332:629 */     setWriteMode(1);
/* 333:630 */     resetWriter();
/* 334:631 */     setWriteMode(2);
/* 335:    */   }
/* 336:    */   
/* 337:    */   public void resetOptions()
/* 338:    */   {
/* 339:636 */     super.resetOptions();
/* 340:637 */     setFileExtension(".dict");
/* 341:    */   }
/* 342:    */   
/* 343:    */   public void resetWriter()
/* 344:    */   {
/* 345:642 */     super.resetWriter();
/* 346:    */     
/* 347:644 */     this.m_binaryStream = null;
/* 348:    */   }
/* 349:    */   
/* 350:    */   public void setDestination(OutputStream output)
/* 351:    */     throws IOException
/* 352:    */   {
/* 353:649 */     super.setDestination(output);
/* 354:650 */     this.m_binaryStream = new BufferedOutputStream(output);
/* 355:    */   }
/* 356:    */   
/* 357:    */   public String getRevision()
/* 358:    */   {
/* 359:655 */     return RevisionUtils.extract("$Revision: 12690 $");
/* 360:    */   }
/* 361:    */   
/* 362:    */   public static void main(String[] args)
/* 363:    */   {
/* 364:659 */     runFileSaver(new DictionarySaver(), args);
/* 365:    */   }
/* 366:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.DictionarySaver
 * JD-Core Version:    0.7.0.1
 */