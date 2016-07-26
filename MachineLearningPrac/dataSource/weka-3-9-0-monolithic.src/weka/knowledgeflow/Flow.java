/*   1:    */ package weka.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.Reader;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.Collection;
/*   8:    */ import java.util.Iterator;
/*   9:    */ import java.util.LinkedHashMap;
/*  10:    */ import java.util.List;
/*  11:    */ import java.util.Map;
/*  12:    */ import java.util.Map.Entry;
/*  13:    */ import java.util.Set;
/*  14:    */ import javax.swing.filechooser.FileFilter;
/*  15:    */ import weka.core.PluginManager;
/*  16:    */ import weka.core.WekaException;
/*  17:    */ import weka.gui.ExtensionFileFilter;
/*  18:    */ import weka.gui.Logger;
/*  19:    */ import weka.knowledgeflow.steps.SetVariables;
/*  20:    */ import weka.knowledgeflow.steps.Step;
/*  21:    */ 
/*  22:    */ public class Flow
/*  23:    */ {
/*  24: 50 */   public static final List<FileFilter> FLOW_FILE_EXTENSIONS = new ArrayList();
/*  25:    */   
/*  26:    */   static
/*  27:    */   {
/*  28: 55 */     PluginManager.addPlugin(FlowLoader.class.getCanonicalName(), JSONFlowLoader.class.getCanonicalName(), JSONFlowLoader.class.getCanonicalName(), true);
/*  29:    */     
/*  30:    */ 
/*  31:    */ 
/*  32:    */ 
/*  33: 60 */     PluginManager.addPlugin(FlowLoader.class.getCanonicalName(), LegacyFlowLoader.class.getCanonicalName(), LegacyFlowLoader.class.getCanonicalName(), true);
/*  34:    */     
/*  35:    */ 
/*  36:    */ 
/*  37: 64 */     Set<String> flowLoaders = PluginManager.getPluginNamesOfType(FlowLoader.class.getCanonicalName());
/*  38: 66 */     if (flowLoaders != null) {
/*  39:    */       try
/*  40:    */       {
/*  41: 68 */         for (String f : flowLoaders)
/*  42:    */         {
/*  43: 69 */           FlowLoader fl = (FlowLoader)PluginManager.getPluginInstance(FlowLoader.class.getCanonicalName(), f);
/*  44:    */           
/*  45:    */ 
/*  46: 72 */           String extension = fl.getFlowFileExtension();
/*  47: 73 */           String description = fl.getFlowFileExtensionDescription();
/*  48: 74 */           FLOW_FILE_EXTENSIONS.add(new ExtensionFileFilter("." + extension, description + " (*." + extension + ")"));
/*  49:    */         }
/*  50:    */       }
/*  51:    */       catch (Exception ex)
/*  52:    */       {
/*  53: 78 */         ex.printStackTrace();
/*  54:    */       }
/*  55:    */     }
/*  56:    */   }
/*  57:    */   
/*  58: 84 */   protected Map<String, StepManagerImpl> m_flowSteps = new LinkedHashMap();
/*  59: 88 */   protected String m_flowName = "Untitled";
/*  60:    */   
/*  61:    */   public static FlowLoader getFlowLoader(String flowFileExtension, Logger log)
/*  62:    */     throws WekaException
/*  63:    */   {
/*  64:101 */     Set<String> availableLoaders = PluginManager.getPluginNamesOfType(FlowLoader.class.getCanonicalName());
/*  65:    */     
/*  66:103 */     FlowLoader result = null;
/*  67:104 */     if (availableLoaders != null) {
/*  68:    */       try
/*  69:    */       {
/*  70:106 */         for (String l : availableLoaders)
/*  71:    */         {
/*  72:107 */           FlowLoader candidate = (FlowLoader)PluginManager.getPluginInstance(FlowLoader.class.getCanonicalName(), l);
/*  73:110 */           if (candidate.getFlowFileExtension().equalsIgnoreCase(flowFileExtension))
/*  74:    */           {
/*  75:112 */             result = candidate;
/*  76:113 */             break;
/*  77:    */           }
/*  78:    */         }
/*  79:    */       }
/*  80:    */       catch (Exception ex)
/*  81:    */       {
/*  82:117 */         throw new WekaException(ex);
/*  83:    */       }
/*  84:    */     }
/*  85:121 */     if (result != null) {
/*  86:122 */       result.setLog(log);
/*  87:    */     }
/*  88:124 */     return result;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public static Flow loadFlow(File flowFile, Logger log)
/*  92:    */     throws WekaException
/*  93:    */   {
/*  94:136 */     String extension = "kf";
/*  95:137 */     if (flowFile.toString().lastIndexOf('.') > 0) {
/*  96:138 */       extension = flowFile.toString().substring(flowFile.toString().lastIndexOf('.') + 1, flowFile.toString().length());
/*  97:    */     }
/*  98:142 */     FlowLoader toUse = getFlowLoader(extension, log);
/*  99:143 */     if (toUse == null) {
/* 100:144 */       throw new WekaException("Was unable to find a loader for flow file: " + flowFile.toString());
/* 101:    */     }
/* 102:147 */     return toUse.readFlow(flowFile);
/* 103:    */   }
/* 104:    */   
/* 105:    */   public static Flow loadFlow(InputStream is, FlowLoader loader)
/* 106:    */     throws WekaException
/* 107:    */   {
/* 108:161 */     return loader.readFlow(is);
/* 109:    */   }
/* 110:    */   
/* 111:    */   public static Flow loadFlow(Reader r, FlowLoader loader)
/* 112:    */     throws WekaException
/* 113:    */   {
/* 114:174 */     return loader.readFlow(r);
/* 115:    */   }
/* 116:    */   
/* 117:    */   public static Flow JSONToFlow(String flowJSON)
/* 118:    */     throws WekaException
/* 119:    */   {
/* 120:185 */     return JSONToFlow(flowJSON, false);
/* 121:    */   }
/* 122:    */   
/* 123:    */   public static Flow JSONToFlow(String flowJSON, boolean dontComplainAboutMissingConnections)
/* 124:    */     throws WekaException
/* 125:    */   {
/* 126:199 */     return JSONFlowUtils.JSONToFlow(flowJSON, dontComplainAboutMissingConnections);
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void saveFlow(File file)
/* 130:    */     throws WekaException
/* 131:    */   {
/* 132:210 */     JSONFlowUtils.writeFlow(this, file);
/* 133:    */   }
/* 134:    */   
/* 135:    */   public String getFlowName()
/* 136:    */   {
/* 137:219 */     return this.m_flowName;
/* 138:    */   }
/* 139:    */   
/* 140:    */   public void setFlowName(String name)
/* 141:    */   {
/* 142:228 */     this.m_flowName = name;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public String getFlowID()
/* 146:    */   {
/* 147:238 */     String ID = getFlowName();
/* 148:    */     try
/* 149:    */     {
/* 150:240 */       ID = ID + "_" + toJSON().hashCode();
/* 151:    */     }
/* 152:    */     catch (WekaException ex)
/* 153:    */     {
/* 154:242 */       ex.printStackTrace();
/* 155:    */     }
/* 156:245 */     return ID;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public synchronized void addAll(List<StepManagerImpl> steps)
/* 160:    */   {
/* 161:254 */     for (StepManagerImpl s : steps) {
/* 162:255 */       addStep(s);
/* 163:    */     }
/* 164:    */   }
/* 165:    */   
/* 166:    */   public synchronized void addStep(StepManagerImpl manager)
/* 167:    */   {
/* 168:268 */     String toAddName = manager.getManagedStep().getName();
/* 169:269 */     if ((toAddName != null) && (toAddName.length() > 0))
/* 170:    */     {
/* 171:271 */       boolean exactMatch = false;
/* 172:272 */       int maxCopyNum = 1;
/* 173:273 */       for (Map.Entry<String, StepManagerImpl> e : this.m_flowSteps.entrySet())
/* 174:    */       {
/* 175:274 */         String compName = ((StepManagerImpl)e.getValue()).getManagedStep().getName();
/* 176:275 */         if (toAddName.equals(compName))
/* 177:    */         {
/* 178:276 */           exactMatch = true;
/* 179:    */         }
/* 180:278 */         else if (compName.startsWith(toAddName))
/* 181:    */         {
/* 182:279 */           String num = compName.replace(toAddName, "");
/* 183:    */           try
/* 184:    */           {
/* 185:281 */             int compNum = Integer.parseInt(num);
/* 186:282 */             if (compNum > maxCopyNum) {
/* 187:283 */               maxCopyNum = compNum;
/* 188:    */             }
/* 189:    */           }
/* 190:    */           catch (NumberFormatException ex) {}
/* 191:    */         }
/* 192:    */       }
/* 193:291 */       if (exactMatch)
/* 194:    */       {
/* 195:292 */         maxCopyNum++;
/* 196:293 */         toAddName = toAddName + "" + maxCopyNum;
/* 197:294 */         manager.getManagedStep().setName(toAddName);
/* 198:    */       }
/* 199:    */     }
/* 200:298 */     this.m_flowSteps.put(toAddName, manager);
/* 201:    */   }
/* 202:    */   
/* 203:    */   public synchronized boolean connectSteps(StepManagerImpl source, StepManagerImpl target, String connectionType)
/* 204:    */   {
/* 205:315 */     return connectSteps(source, target, connectionType, false);
/* 206:    */   }
/* 207:    */   
/* 208:    */   public synchronized boolean connectSteps(StepManagerImpl source, StepManagerImpl target, String connectionType, boolean force)
/* 209:    */   {
/* 210:333 */     boolean connSuccessful = false;
/* 211:335 */     if ((findStep(source.getName()) == source) && (findStep(target.getName()) == target)) {
/* 212:341 */       connSuccessful = source.addOutgoingConnection(connectionType, target, force);
/* 213:    */     }
/* 214:343 */     return connSuccessful;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public synchronized void renameStep(StepManagerImpl step, String newName)
/* 218:    */     throws WekaException
/* 219:    */   {
/* 220:355 */     renameStep(step.getName(), newName);
/* 221:    */   }
/* 222:    */   
/* 223:    */   public synchronized void renameStep(String oldName, String newName)
/* 224:    */     throws WekaException
/* 225:    */   {
/* 226:368 */     if (!this.m_flowSteps.containsKey(oldName)) {
/* 227:369 */       throw new WekaException("Step " + oldName + " does not seem to be part of the flow!");
/* 228:    */     }
/* 229:373 */     StepManagerImpl toRename = (StepManagerImpl)this.m_flowSteps.remove(oldName);
/* 230:374 */     toRename.getManagedStep().setName(newName);
/* 231:375 */     this.m_flowSteps.put(newName, toRename);
/* 232:    */   }
/* 233:    */   
/* 234:    */   public synchronized void removeStep(StepManagerImpl manager)
/* 235:    */     throws WekaException
/* 236:    */   {
/* 237:388 */     if (!this.m_flowSteps.containsKey(manager.getManagedStep().getName())) {
/* 238:389 */       throw new WekaException("Step " + manager.getManagedStep().getName() + " does not seem to be part of the flow!");
/* 239:    */     }
/* 240:393 */     this.m_flowSteps.remove(manager.getManagedStep().getName());
/* 241:394 */     manager.clearAllConnections();
/* 242:396 */     for (Map.Entry<String, StepManagerImpl> e : this.m_flowSteps.entrySet()) {
/* 243:397 */       ((StepManagerImpl)e.getValue()).disconnectStep(manager.getManagedStep());
/* 244:    */     }
/* 245:    */   }
/* 246:    */   
/* 247:    */   public List<StepManagerImpl> getSteps()
/* 248:    */   {
/* 249:407 */     return new ArrayList(this.m_flowSteps.values());
/* 250:    */   }
/* 251:    */   
/* 252:    */   public Iterator<StepManagerImpl> iterator()
/* 253:    */   {
/* 254:416 */     return this.m_flowSteps.values().iterator();
/* 255:    */   }
/* 256:    */   
/* 257:    */   public int size()
/* 258:    */   {
/* 259:425 */     return this.m_flowSteps.size();
/* 260:    */   }
/* 261:    */   
/* 262:    */   public StepManagerImpl findStep(String stepName)
/* 263:    */   {
/* 264:436 */     return (StepManagerImpl)this.m_flowSteps.get(stepName);
/* 265:    */   }
/* 266:    */   
/* 267:    */   public List<StepManagerImpl> findPotentialStartPoints()
/* 268:    */   {
/* 269:446 */     List<StepManagerImpl> startPoints = new ArrayList();
/* 270:449 */     for (Map.Entry<String, StepManagerImpl> e : this.m_flowSteps.entrySet())
/* 271:    */     {
/* 272:450 */       StepManagerImpl candidate = (StepManagerImpl)e.getValue();
/* 273:451 */       if (candidate.getIncomingConnections().size() == 0) {
/* 274:452 */         startPoints.add(candidate);
/* 275:    */       }
/* 276:    */     }
/* 277:456 */     return startPoints;
/* 278:    */   }
/* 279:    */   
/* 280:    */   public boolean initFlow(FlowExecutor executor)
/* 281:    */     throws WekaException
/* 282:    */   {
/* 283:469 */     boolean initOK = true;
/* 284:471 */     for (Map.Entry<String, StepManagerImpl> s : this.m_flowSteps.entrySet()) {
/* 285:472 */       ((StepManagerImpl)s.getValue()).setExecutionEnvironment(executor.getExecutionEnvironment());
/* 286:    */     }
/* 287:476 */     for (Map.Entry<String, StepManagerImpl> s : this.m_flowSteps.entrySet()) {
/* 288:477 */       if (((((StepManagerImpl)s.getValue()).getManagedStep() instanceof SetVariables)) && 
/* 289:478 */         (!((StepManagerImpl)s.getValue()).initStep()))
/* 290:    */       {
/* 291:479 */         initOK = false;
/* 292:480 */         break;
/* 293:    */       }
/* 294:    */     }
/* 295:485 */     if (initOK) {
/* 296:487 */       for (Map.Entry<String, StepManagerImpl> s : this.m_flowSteps.entrySet()) {
/* 297:488 */         if (!((StepManagerImpl)s.getValue()).initStep())
/* 298:    */         {
/* 299:489 */           initOK = false;
/* 300:490 */           break;
/* 301:    */         }
/* 302:    */       }
/* 303:    */     }
/* 304:495 */     return initOK;
/* 305:    */   }
/* 306:    */   
/* 307:    */   public String toJSON()
/* 308:    */     throws WekaException
/* 309:    */   {
/* 310:505 */     return JSONFlowUtils.flowToJSON(this);
/* 311:    */   }
/* 312:    */   
/* 313:    */   public Flow copyFlow()
/* 314:    */     throws WekaException
/* 315:    */   {
/* 316:515 */     return JSONToFlow(toJSON());
/* 317:    */   }
/* 318:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.Flow
 * JD-Core Version:    0.7.0.1
 */