/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.io.Reader;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.Capabilities.Capability;
/*   9:    */ import weka.core.DictionaryBuilder;
/*  10:    */ import weka.core.Environment;
/*  11:    */ import weka.core.EnvironmentHandler;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.OptionMetadata;
/*  15:    */ import weka.core.stemmers.NullStemmer;
/*  16:    */ import weka.core.stemmers.Stemmer;
/*  17:    */ import weka.core.stopwords.Null;
/*  18:    */ import weka.core.stopwords.StopwordsHandler;
/*  19:    */ import weka.core.tokenizers.Tokenizer;
/*  20:    */ import weka.filters.SimpleStreamFilter;
/*  21:    */ import weka.filters.UnsupervisedFilter;
/*  22:    */ import weka.gui.FilePropertyMetadata;
/*  23:    */ 
/*  24:    */ public class FixedDictionaryStringToWordVector
/*  25:    */   extends SimpleStreamFilter
/*  26:    */   implements UnsupervisedFilter, EnvironmentHandler
/*  27:    */ {
/*  28:    */   private static final long serialVersionUID = 7990892846966916757L;
/*  29:152 */   protected DictionaryBuilder m_vectorizer = new DictionaryBuilder();
/*  30:154 */   protected File m_dictionaryFile = new File("-- set me --");
/*  31:    */   protected transient InputStream m_dictionarySource;
/*  32:    */   protected transient Reader m_textDictionarySource;
/*  33:    */   protected boolean m_dictionaryIsBinary;
/*  34:168 */   protected transient Environment m_env = Environment.getSystemWide();
/*  35:    */   
/*  36:    */   public Capabilities getCapabilities()
/*  37:    */   {
/*  38:178 */     Capabilities result = super.getCapabilities();
/*  39:179 */     result.disableAll();
/*  40:    */     
/*  41:    */ 
/*  42:182 */     result.enableAllAttributes();
/*  43:183 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  44:    */     
/*  45:    */ 
/*  46:186 */     result.enableAllClasses();
/*  47:187 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  48:188 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  49:    */     
/*  50:190 */     return result;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public DictionaryBuilder getDictionaryHandler()
/*  54:    */   {
/*  55:200 */     return this.m_vectorizer;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setDictionarySource(InputStream source)
/*  59:    */   {
/*  60:211 */     this.m_dictionarySource = source;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void setDictionarySource(Reader source)
/*  64:    */   {
/*  65:221 */     this.m_textDictionarySource = source;
/*  66:    */   }
/*  67:    */   
/*  68:    */   @OptionMetadata(displayName="Dictionary file", description="The path to the dictionary to use", commandLineParamName="dictionary", commandLineParamSynopsis="-dictionary <path to dictionary file>", displayOrder=1)
/*  69:    */   @FilePropertyMetadata(fileChooserDialogType=0, directoriesOnly=false)
/*  70:    */   public void setDictionaryFile(File file)
/*  71:    */   {
/*  72:237 */     this.m_dictionaryFile = file;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public File getDictionaryFile()
/*  76:    */   {
/*  77:246 */     return this.m_dictionaryFile;
/*  78:    */   }
/*  79:    */   
/*  80:    */   @OptionMetadata(displayName="Dictionary is binary", description="Dictionary file contains a binary serialized dictionary", commandLineParamName="binary-dict", commandLineParamSynopsis="-binary-dict", commandLineParamIsFlag=true, displayOrder=2)
/*  81:    */   public void setDictionaryIsBinary(boolean binary)
/*  82:    */   {
/*  83:261 */     this.m_dictionaryIsBinary = binary;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public boolean getDictionaryIsBinary()
/*  87:    */   {
/*  88:265 */     return this.m_dictionaryIsBinary;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public boolean getOutputWordCounts()
/*  92:    */   {
/*  93:275 */     return this.m_vectorizer.getOutputWordCounts();
/*  94:    */   }
/*  95:    */   
/*  96:    */   @OptionMetadata(displayName="Output word counts", description="Output word counts rather than boolean 0 or 1 (indicating presence or absence of a word", commandLineParamName="C", commandLineParamSynopsis="-C", commandLineParamIsFlag=true, displayOrder=3)
/*  97:    */   public void setOutputWordCounts(boolean outputWordCounts)
/*  98:    */   {
/*  99:290 */     this.m_vectorizer.setOutputWordCounts(outputWordCounts);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public String getAttributeIndices()
/* 103:    */   {
/* 104:299 */     return this.m_vectorizer.getAttributeIndices();
/* 105:    */   }
/* 106:    */   
/* 107:    */   @OptionMetadata(displayName="Range of attributes to operate on", description="Specify range of attributes to act on. This is a comma separated list of attribute\nindices, with \"first\" and \"last\" valid values.", commandLineParamName="R", commandLineParamSynopsis="-R <range>", displayOrder=4)
/* 108:    */   public void setAttributeIndices(String rangeList)
/* 109:    */   {
/* 110:318 */     this.m_vectorizer.setAttributeIndices(rangeList);
/* 111:    */   }
/* 112:    */   
/* 113:    */   public boolean getInvertSelection()
/* 114:    */   {
/* 115:327 */     return this.m_vectorizer.getInvertSelection();
/* 116:    */   }
/* 117:    */   
/* 118:    */   @OptionMetadata(displayName="Invert selection", description="Set attributes selection mode. If false, only selected attributes in the range will\nbe worked on. If true, only non-selected attributes will be processed", commandLineParamName="V", commandLineParamSynopsis="-V", commandLineParamIsFlag=true, displayOrder=5)
/* 119:    */   public void setInvertSelection(boolean invert)
/* 120:    */   {
/* 121:342 */     this.m_vectorizer.setInvertSelection(invert);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public String getAttributeNamePrefix()
/* 125:    */   {
/* 126:351 */     return this.m_vectorizer.getAttributeNamePrefix();
/* 127:    */   }
/* 128:    */   
/* 129:    */   @OptionMetadata(displayName="Prefix for created attribute names", description="Specify a prefix for the created attribute names (default: \"\")", commandLineParamName="P", commandLineParamSynopsis="-P <attribute name prefix>", displayOrder=6)
/* 130:    */   public void setAttributeNamePrefix(String newPrefix)
/* 131:    */   {
/* 132:365 */     this.m_vectorizer.setAttributeNamePrefix(newPrefix);
/* 133:    */   }
/* 134:    */   
/* 135:    */   public boolean getTFTransform()
/* 136:    */   {
/* 137:375 */     return this.m_vectorizer.getTFTransform();
/* 138:    */   }
/* 139:    */   
/* 140:    */   @OptionMetadata(displayName="TFT transform", description="Set whether the word frequencies should be transformed into\nlog(1+fij), where fij is the frequency of word i in document (instance) j.", commandLineParamName="T", commandLineParamSynopsis="-T", displayOrder=7)
/* 141:    */   public void setTFTransform(boolean TFTransform)
/* 142:    */   {
/* 143:391 */     this.m_vectorizer.setTFTransform(TFTransform);
/* 144:    */   }
/* 145:    */   
/* 146:    */   public boolean getIDFTransform()
/* 147:    */   {
/* 148:403 */     return this.m_vectorizer.getIDFTransform();
/* 149:    */   }
/* 150:    */   
/* 151:    */   @OptionMetadata(displayName="IDF transform", description="Set whether the word frequencies in a document should be transformed into\nfij*log(num of Docs/num of docs with word i), where fij is the frequency\nof word i in document (instance) j.", commandLineParamName="I", commandLineParamSynopsis="-I", displayOrder=8)
/* 152:    */   public void setIDFTransform(boolean IDFTransform)
/* 153:    */   {
/* 154:421 */     this.m_vectorizer.setIDFTransform(IDFTransform);
/* 155:    */   }
/* 156:    */   
/* 157:    */   @OptionMetadata(displayName="Normalize word frequencies", description="Whether to normalize to average length of documents seen during dictionary construction", commandLineParamName="N", commandLineParamSynopsis="-N", commandLineParamIsFlag=true, displayOrder=9)
/* 158:    */   public void setNormalizeDocLength(boolean normalize)
/* 159:    */   {
/* 160:436 */     this.m_vectorizer.setNormalize(normalize);
/* 161:    */   }
/* 162:    */   
/* 163:    */   public boolean getNormalizeDocLength()
/* 164:    */   {
/* 165:446 */     return this.m_vectorizer.getNormalize();
/* 166:    */   }
/* 167:    */   
/* 168:    */   public boolean getLowerCaseTokens()
/* 169:    */   {
/* 170:455 */     return this.m_vectorizer.getLowerCaseTokens();
/* 171:    */   }
/* 172:    */   
/* 173:    */   @OptionMetadata(displayName="Lower case tokens", description="Convert all tokens to lowercase when matching against dictionary entries.", commandLineParamName="L", commandLineParamSynopsis="-L", commandLineParamIsFlag=true, displayOrder=10)
/* 174:    */   public void setLowerCaseTokens(boolean downCaseTokens)
/* 175:    */   {
/* 176:471 */     this.m_vectorizer.setLowerCaseTokens(downCaseTokens);
/* 177:    */   }
/* 178:    */   
/* 179:    */   @OptionMetadata(displayName="Stemmer to use", description="The stemming algorithm (classname plus parameters) to use.", commandLineParamName="stemmer", commandLineParamSynopsis="-stemmer <spec>", displayOrder=11)
/* 180:    */   public void setStemmer(Stemmer value)
/* 181:    */   {
/* 182:486 */     if (value != null) {
/* 183:487 */       this.m_vectorizer.setStemmer(value);
/* 184:    */     } else {
/* 185:489 */       this.m_vectorizer.setStemmer(new NullStemmer());
/* 186:    */     }
/* 187:    */   }
/* 188:    */   
/* 189:    */   public Stemmer getStemmer()
/* 190:    */   {
/* 191:499 */     return this.m_vectorizer.getStemmer();
/* 192:    */   }
/* 193:    */   
/* 194:    */   @OptionMetadata(displayName="Stop words handler", description="The stopwords handler to use (default = Null)", commandLineParamName="stopwords-handler", commandLineParamSynopsis="-stopwords-handler <spec>", displayOrder=12)
/* 195:    */   public void setStopwordsHandler(StopwordsHandler value)
/* 196:    */   {
/* 197:512 */     if (value != null) {
/* 198:513 */       this.m_vectorizer.setStopwordsHandler(value);
/* 199:    */     } else {
/* 200:515 */       this.m_vectorizer.setStopwordsHandler(new Null());
/* 201:    */     }
/* 202:    */   }
/* 203:    */   
/* 204:    */   public StopwordsHandler getStopwordsHandler()
/* 205:    */   {
/* 206:525 */     return this.m_vectorizer.getStopwordsHandler();
/* 207:    */   }
/* 208:    */   
/* 209:    */   @OptionMetadata(displayName="Tokenizer", description="The tokenizing algorithm (classname plus parameters) to use.\n(default: weka.core.tokenizers.WordTokenizer)", commandLineParamName="tokenizer", commandLineParamSynopsis="-tokenizer <spec>", displayOrder=13)
/* 210:    */   public void setTokenizer(Tokenizer value)
/* 211:    */   {
/* 212:539 */     this.m_vectorizer.setTokenizer(value);
/* 213:    */   }
/* 214:    */   
/* 215:    */   public Tokenizer getTokenizer()
/* 216:    */   {
/* 217:548 */     return this.m_vectorizer.getTokenizer();
/* 218:    */   }
/* 219:    */   
/* 220:    */   public String globalInfo()
/* 221:    */   {
/* 222:553 */     return "Converts String attributes into a set of attributes representing word occurrence (depending on the tokenizer) information from the text contained in the strings. The set of words (attributes) is taken from a user-supplied dictionary, either in plain text form or as a serialized java object.";
/* 223:    */   }
/* 224:    */   
/* 225:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 226:    */     throws Exception
/* 227:    */   {
/* 228:564 */     if ((this.m_vectorizer.readyToVectorize()) && (inputFormat.equalHeaders(this.m_vectorizer.getInputFormat()))) {
/* 229:566 */       return this.m_vectorizer.getVectorizedFormat();
/* 230:    */     }
/* 231:569 */     this.m_vectorizer.reset();
/* 232:570 */     this.m_vectorizer.setup(inputFormat);
/* 233:572 */     if ((this.m_dictionaryFile == null) && (this.m_dictionarySource == null) && (this.m_textDictionarySource == null)) {
/* 234:574 */       throw new IOException("No dictionary file/source specified!");
/* 235:    */     }
/* 236:577 */     if (this.m_dictionarySource != null)
/* 237:    */     {
/* 238:578 */       this.m_vectorizer.loadDictionary(this.m_dictionarySource);
/* 239:    */     }
/* 240:579 */     else if (this.m_textDictionarySource != null)
/* 241:    */     {
/* 242:580 */       this.m_vectorizer.loadDictionary(this.m_textDictionarySource);
/* 243:    */     }
/* 244:    */     else
/* 245:    */     {
/* 246:583 */       String fString = this.m_dictionaryFile.toString();
/* 247:584 */       if (fString.length() == 0) {
/* 248:585 */         throw new IOException("No dictionary file specified!");
/* 249:    */       }
/* 250:    */       try
/* 251:    */       {
/* 252:588 */         fString = this.m_env.substitute(fString);
/* 253:    */       }
/* 254:    */       catch (Exception ex) {}
/* 255:592 */       File dictFile = new File(fString);
/* 256:593 */       if (!dictFile.exists()) {
/* 257:594 */         throw new IOException("Specified dictionary file '" + fString + "' does not seem to exist!");
/* 258:    */       }
/* 259:597 */       this.m_vectorizer.loadDictionary(dictFile, !this.m_dictionaryIsBinary);
/* 260:    */     }
/* 261:600 */     return this.m_vectorizer.getVectorizedFormat();
/* 262:    */   }
/* 263:    */   
/* 264:    */   protected Instance process(Instance instance)
/* 265:    */     throws Exception
/* 266:    */   {
/* 267:605 */     return this.m_vectorizer.vectorizeInstance(instance);
/* 268:    */   }
/* 269:    */   
/* 270:    */   public void setEnvironment(Environment env)
/* 271:    */   {
/* 272:610 */     this.m_env = env;
/* 273:    */   }
/* 274:    */   
/* 275:    */   public static void main(String[] args)
/* 276:    */   {
/* 277:614 */     runFilter(new FixedDictionaryStringToWordVector(), args);
/* 278:    */   }
/* 279:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector
 * JD-Core Version:    0.7.0.1
 */