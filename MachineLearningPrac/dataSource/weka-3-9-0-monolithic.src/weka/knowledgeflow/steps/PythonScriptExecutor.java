/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.image.BufferedImage;
/*   4:    */ import java.io.BufferedReader;
/*   5:    */ import java.io.File;
/*   6:    */ import java.io.FileReader;
/*   7:    */ import java.io.IOException;
/*   8:    */ import java.util.ArrayList;
/*   9:    */ import java.util.List;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.OptionMetadata;
/*  12:    */ import weka.core.WekaException;
/*  13:    */ import weka.gui.FilePropertyMetadata;
/*  14:    */ import weka.gui.ProgrammaticProperty;
/*  15:    */ import weka.knowledgeflow.Data;
/*  16:    */ import weka.knowledgeflow.StepManager;
/*  17:    */ import weka.python.PythonSession;
/*  18:    */ import weka.python.PythonSession.PythonVariableType;
/*  19:    */ 
/*  20:    */ @KFStep(name="PythonScriptExecutor", category="Scripting", toolTipText="CPython scripting step", iconPath="weka/gui/knowledgeflow/icons/PythonScriptExecutor.gif")
/*  21:    */ public class PythonScriptExecutor
/*  22:    */   extends BaseStep
/*  23:    */ {
/*  24:    */   private static final long serialVersionUID = -491300310357178468L;
/*  25: 57 */   protected String m_pyScript = "";
/*  26: 60 */   protected File m_scriptFile = new File("");
/*  27: 63 */   protected String m_varsToGet = "";
/*  28:    */   protected boolean m_debug;
/*  29:    */   protected boolean m_continueOnSysErr;
/*  30:    */   
/*  31:    */   @OptionMetadata(displayName="Output debugging info from python", description="Whether to output debugging info from python", displayOrder=10)
/*  32:    */   public void setDebug(boolean debug)
/*  33:    */   {
/*  34: 80 */     this.m_debug = debug;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public boolean getDebug()
/*  38:    */   {
/*  39: 89 */     return this.m_debug;
/*  40:    */   }
/*  41:    */   
/*  42:    */   @ProgrammaticProperty
/*  43:    */   public void setPythonScript(String script)
/*  44:    */   {
/*  45:100 */     this.m_pyScript = script;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public String getPythonScript()
/*  49:    */   {
/*  50:110 */     return this.m_pyScript;
/*  51:    */   }
/*  52:    */   
/*  53:    */   @OptionMetadata(displayName="File to load script from", description="A file to load the python script from (if set takes precendenceover any script from the editor", displayOrder=1)
/*  54:    */   @FilePropertyMetadata(fileChooserDialogType=0, directoriesOnly=false)
/*  55:    */   public void setScriptFile(File scriptFile)
/*  56:    */   {
/*  57:125 */     this.m_scriptFile = scriptFile;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public File getScriptFile()
/*  61:    */   {
/*  62:134 */     return this.m_scriptFile;
/*  63:    */   }
/*  64:    */   
/*  65:    */   @OptionMetadata(displayName="Variables to get from Python", description="A comma-separated list of variables to retrieve from Python", displayOrder=2)
/*  66:    */   public void setVariablesToGetFromPython(String varsToGet)
/*  67:    */   {
/*  68:146 */     this.m_varsToGet = varsToGet;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public String getVariablesToGetFromPython()
/*  72:    */   {
/*  73:155 */     return this.m_varsToGet;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void stepInit()
/*  77:    */     throws WekaException
/*  78:    */   {}
/*  79:    */   
/*  80:    */   public void start()
/*  81:    */     throws WekaException
/*  82:    */   {
/*  83:175 */     if ((getStepManager().numIncomingConnections() == 0) && (
/*  84:176 */       ((this.m_pyScript != null) && (this.m_pyScript.length() > 0)) || ((this.m_scriptFile != null) && (this.m_scriptFile.toString().length() > 0))))
/*  85:    */     {
/*  86:178 */       String script = this.m_pyScript;
/*  87:179 */       if ((this.m_scriptFile != null) && (this.m_scriptFile.toString().length() > 0)) {
/*  88:    */         try
/*  89:    */         {
/*  90:181 */           script = loadScript();
/*  91:    */         }
/*  92:    */         catch (Exception ex)
/*  93:    */         {
/*  94:183 */           throw new WekaException(ex);
/*  95:    */         }
/*  96:    */       }
/*  97:187 */       getStepManager().processing();
/*  98:188 */       executeScript(getSession(), script);
/*  99:189 */       getStepManager().finished();
/* 100:    */     }
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void processIncoming(Data data)
/* 104:    */     throws WekaException
/* 105:    */   {
/* 106:203 */     Instances incoming = (Instances)data.getPrimaryPayload();
/* 107:205 */     if (incoming.numInstances() > 0)
/* 108:    */     {
/* 109:206 */       PythonSession session = null;
/* 110:    */       try
/* 111:    */       {
/* 112:208 */         getStepManager().processing();
/* 113:209 */         session = getSession();
/* 114:211 */         if (((this.m_pyScript != null) && (this.m_pyScript.length() > 0)) || ((this.m_scriptFile != null) && (this.m_scriptFile.toString().length() > 0)))
/* 115:    */         {
/* 116:213 */           getStepManager().logDetailed("Converting incoming instances to pandas data frame");
/* 117:    */           
/* 118:215 */           session.instancesToPython(incoming, "py_data", getDebug());
/* 119:    */           
/* 120:    */ 
/* 121:218 */           String script = this.m_pyScript;
/* 122:219 */           if ((this.m_scriptFile != null) && (this.m_scriptFile.toString().length() > 0)) {
/* 123:220 */             script = loadScript();
/* 124:    */           }
/* 125:223 */           executeScript(session, script);
/* 126:224 */           getStepManager().finished();
/* 127:    */         }
/* 128:    */       }
/* 129:    */       catch (Exception ex)
/* 130:    */       {
/* 131:227 */         if ((getDebug()) && 
/* 132:228 */           (session != null))
/* 133:    */         {
/* 134:229 */           getStepManager().logBasic("Getting debug info...");
/* 135:230 */           List<String> outAndErr = session.getPythonDebugBuffer(getDebug());
/* 136:231 */           getStepManager().logBasic("Output from python:\n" + (String)outAndErr.get(0));
/* 137:    */           
/* 138:233 */           getStepManager().logBasic("Error from python:\n" + (String)outAndErr.get(1));
/* 139:    */         }
/* 140:    */       }
/* 141:    */       finally
/* 142:    */       {
/* 143:238 */         PythonSession.releaseSession(this);
/* 144:    */       }
/* 145:    */     }
/* 146:    */   }
/* 147:    */   
/* 148:    */   public List<String> getIncomingConnectionTypes()
/* 149:    */   {
/* 150:250 */     List<String> result = new ArrayList();
/* 151:251 */     if (getStepManager().numIncomingConnections() == 0)
/* 152:    */     {
/* 153:252 */       result.add("dataSet");
/* 154:253 */       result.add("trainingSet");
/* 155:254 */       result.add("testSet");
/* 156:    */     }
/* 157:256 */     return result;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public List<String> getOutgoingConnectionTypes()
/* 161:    */   {
/* 162:266 */     List<String> result = new ArrayList();
/* 163:267 */     if (((this.m_pyScript != null) && (this.m_pyScript.length() > 0)) || ((this.m_scriptFile != null) && (this.m_scriptFile.length() > 0L)))
/* 164:    */     {
/* 165:269 */       result.add("dataSet");
/* 166:270 */       result.add("text");
/* 167:271 */       result.add("image");
/* 168:    */     }
/* 169:274 */     return result;
/* 170:    */   }
/* 171:    */   
/* 172:    */   protected String loadScript()
/* 173:    */     throws IOException
/* 174:    */   {
/* 175:285 */     String scriptFile = environmentSubstitute(this.m_scriptFile.toString());
/* 176:    */     
/* 177:287 */     StringBuilder sb = new StringBuilder();
/* 178:288 */     BufferedReader br = new BufferedReader(new FileReader(scriptFile));
/* 179:    */     String line;
/* 180:290 */     while ((line = br.readLine()) != null) {
/* 181:291 */       sb.append(line).append("\n");
/* 182:    */     }
/* 183:293 */     br.close();
/* 184:    */     
/* 185:295 */     return sb.toString();
/* 186:    */   }
/* 187:    */   
/* 188:    */   protected PythonSession getSession()
/* 189:    */     throws WekaException
/* 190:    */   {
/* 191:306 */     if (!PythonSession.pythonAvailable()) {
/* 192:308 */       if (!PythonSession.initSession("python", getDebug()))
/* 193:    */       {
/* 194:309 */         String envEvalResults = PythonSession.getPythonEnvCheckResults();
/* 195:310 */         throw new WekaException("Was unable to start python environment: " + envEvalResults);
/* 196:    */       }
/* 197:    */     }
/* 198:315 */     PythonSession session = PythonSession.acquireSession(this);
/* 199:316 */     session.setLog(getStepManager().getLog());
/* 200:    */     
/* 201:318 */     return session;
/* 202:    */   }
/* 203:    */   
/* 204:    */   protected void executeScript(PythonSession session, String script)
/* 205:    */     throws WekaException
/* 206:    */   {
/* 207:    */     try
/* 208:    */     {
/* 209:330 */       script = environmentSubstitute(script);
/* 210:331 */       getStepManager().statusMessage("Executing user script");
/* 211:332 */       getStepManager().logBasic("Executing user script");
/* 212:333 */       List<String> outAndErr = session.executeScript(script, getDebug());
/* 213:334 */       if ((outAndErr.size() == 2) && (((String)outAndErr.get(1)).length() > 0)) {
/* 214:335 */         if (this.m_continueOnSysErr) {
/* 215:336 */           getStepManager().logWarning((String)outAndErr.get(1));
/* 216:    */         } else {
/* 217:338 */           throw new WekaException((String)outAndErr.get(1));
/* 218:    */         }
/* 219:    */       }
/* 220:342 */       if ((this.m_varsToGet != null) && (this.m_varsToGet.length() > 0))
/* 221:    */       {
/* 222:343 */         String[] vars = environmentSubstitute(this.m_varsToGet).split(",");
/* 223:344 */         boolean[] ok = new boolean[vars.length];
/* 224:345 */         PythonSession.PythonVariableType[] types = new PythonSession.PythonVariableType[vars.length];
/* 225:    */         
/* 226:    */ 
/* 227:    */ 
/* 228:349 */         int i = 0;
/* 229:350 */         for (String v : vars) {
/* 230:351 */           if (!session.checkIfPythonVariableIsSet(v.trim(), getDebug()))
/* 231:    */           {
/* 232:352 */             if (this.m_continueOnSysErr) {
/* 233:353 */               getStepManager().logWarning("Requested output variable '" + v + "' does not seem " + "to be set in python");
/* 234:    */             } else {
/* 235:356 */               throw new WekaException("Requested output variable '" + v + "' does not seem to be set in python");
/* 236:    */             }
/* 237:    */           }
/* 238:    */           else
/* 239:    */           {
/* 240:360 */             ok[i] = true;
/* 241:361 */             types[(i++)] = session.getPythonVariableType(v, getDebug());
/* 242:    */           }
/* 243:    */         }
/* 244:365 */         for (i = 0; i < vars.length; i++) {
/* 245:366 */           if (ok[i] != 0)
/* 246:    */           {
/* 247:367 */             if (getDebug()) {
/* 248:368 */               getStepManager().logDetailed("Retrieving variable '" + vars[i].trim() + "' from python. Type: " + types[i].toString());
/* 249:    */             }
/* 250:372 */             if (types[i] == PythonSession.PythonVariableType.DataFrame)
/* 251:    */             {
/* 252:375 */               if (getStepManager().numOutgoingConnectionsOfType("dataSet") > 0)
/* 253:    */               {
/* 254:377 */                 Instances pyFrame = session.getDataFrameAsInstances(vars[i].trim(), getDebug());
/* 255:    */                 
/* 256:379 */                 Data output = new Data("dataSet", pyFrame);
/* 257:380 */                 output.setPayloadElement("aux_set_num", Integer.valueOf(1));
/* 258:381 */                 output.setPayloadElement("aux_max_set_num", Integer.valueOf(1));
/* 259:    */                 
/* 260:383 */                 getStepManager().outputData(new Data[] { output });
/* 261:    */               }
/* 262:384 */               else if (getStepManager().numOutgoingConnectionsOfType("text") > 0)
/* 263:    */               {
/* 264:386 */                 String textPyFrame = session.getVariableValueFromPythonAsPlainString(vars[i].trim(), getDebug());
/* 265:    */                 
/* 266:    */ 
/* 267:389 */                 Data output = new Data("text", textPyFrame);
/* 268:390 */                 output.setPayloadElement("aux_textTitle", vars[i].trim() + ": data frame");
/* 269:    */                 
/* 270:392 */                 getStepManager().outputData(new Data[] { output });
/* 271:    */               }
/* 272:    */             }
/* 273:394 */             else if (types[i] == PythonSession.PythonVariableType.Image)
/* 274:    */             {
/* 275:395 */               if (getStepManager().numOutgoingConnectionsOfType("image") > 0)
/* 276:    */               {
/* 277:397 */                 BufferedImage image = session.getImageFromPython(vars[i].trim(), getDebug());
/* 278:    */                 
/* 279:399 */                 Data output = new Data("image", image);
/* 280:400 */                 output.setPayloadElement("aux_textTitle", vars[i].trim());
/* 281:    */                 
/* 282:402 */                 getStepManager().outputData(new Data[] { output });
/* 283:    */               }
/* 284:    */             }
/* 285:404 */             else if ((types[i] == PythonSession.PythonVariableType.String) || (types[i] == PythonSession.PythonVariableType.Unknown)) {
/* 286:406 */               if (getStepManager().numOutgoingConnectionsOfType("text") > 0)
/* 287:    */               {
/* 288:408 */                 String varAsText = session.getVariableValueFromPythonAsPlainString(vars[i].trim(), getDebug());
/* 289:    */                 
/* 290:    */ 
/* 291:411 */                 Data output = new Data("text", varAsText);
/* 292:412 */                 output.setPayloadElement("aux_textTitle", vars[i].trim());
/* 293:    */                 
/* 294:414 */                 getStepManager().outputData(new Data[] { output });
/* 295:    */               }
/* 296:    */             }
/* 297:    */           }
/* 298:    */         }
/* 299:    */       }
/* 300:    */     }
/* 301:    */     finally
/* 302:    */     {
/* 303:    */       List<String> outAndErr;
/* 304:421 */       if ((getDebug()) && 
/* 305:422 */         (session != null))
/* 306:    */       {
/* 307:423 */         getStepManager().logBasic("Getting debug info....");
/* 308:424 */         List<String> outAndErr = session.getPythonDebugBuffer(getDebug());
/* 309:425 */         getStepManager().logBasic("Output from python:\n" + (String)outAndErr.get(0));
/* 310:426 */         getStepManager().logBasic("Error from python:\n" + (String)outAndErr.get(1));
/* 311:    */       }
/* 312:430 */       getStepManager().logBasic("Releasing python session");
/* 313:    */     }
/* 314:432 */     PythonSession.releaseSession(this);
/* 315:    */   }
/* 316:    */   
/* 317:    */   public String getCustomEditorForStep()
/* 318:    */   {
/* 319:437 */     return "weka.gui.knowledgeflow.steps.PythonScriptExecutorStepEditorDialog";
/* 320:    */   }
/* 321:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.PythonScriptExecutor
 * JD-Core Version:    0.7.0.1
 */