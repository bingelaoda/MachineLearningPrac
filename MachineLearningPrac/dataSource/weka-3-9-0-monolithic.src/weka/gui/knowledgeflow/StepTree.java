/*   1:    */ package weka.gui.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.awt.Component;
/*   4:    */ import java.awt.Cursor;
/*   5:    */ import java.awt.event.MouseAdapter;
/*   6:    */ import java.awt.event.MouseEvent;
/*   7:    */ import java.beans.Beans;
/*   8:    */ import java.io.InputStream;
/*   9:    */ import java.io.PrintStream;
/*  10:    */ import java.lang.annotation.Annotation;
/*  11:    */ import java.util.Arrays;
/*  12:    */ import java.util.Enumeration;
/*  13:    */ import java.util.HashMap;
/*  14:    */ import java.util.Hashtable;
/*  15:    */ import java.util.LinkedHashSet;
/*  16:    */ import java.util.Map;
/*  17:    */ import java.util.Map.Entry;
/*  18:    */ import java.util.Properties;
/*  19:    */ import java.util.Set;
/*  20:    */ import javax.swing.Icon;
/*  21:    */ import javax.swing.JTree;
/*  22:    */ import javax.swing.tree.DefaultMutableTreeNode;
/*  23:    */ import javax.swing.tree.DefaultTreeCellRenderer;
/*  24:    */ import javax.swing.tree.DefaultTreeSelectionModel;
/*  25:    */ import javax.swing.tree.TreeModel;
/*  26:    */ import javax.swing.tree.TreePath;
/*  27:    */ import weka.core.PluginManager;
/*  28:    */ import weka.core.Utils;
/*  29:    */ import weka.core.WekaException;
/*  30:    */ import weka.gui.GenericObjectEditor;
/*  31:    */ import weka.gui.GenericPropertiesCreator;
/*  32:    */ import weka.gui.HierarchyPropertyParser;
/*  33:    */ import weka.gui.beans.KFIgnore;
/*  34:    */ import weka.knowledgeflow.steps.KFStep;
/*  35:    */ import weka.knowledgeflow.steps.Step;
/*  36:    */ import weka.knowledgeflow.steps.WekaAlgorithmWrapper;
/*  37:    */ 
/*  38:    */ public class StepTree
/*  39:    */   extends JTree
/*  40:    */ {
/*  41:    */   protected static final String STEP_LIST_PROPS = "weka/knowledgeflow/steps/steps.props";
/*  42:    */   private static final long serialVersionUID = 3646119269455293741L;
/*  43:    */   protected MainKFPerspective m_mainPerspective;
/*  44: 80 */   protected Map<String, DefaultMutableTreeNode> m_nodeTextIndex = new HashMap();
/*  45:    */   
/*  46:    */   public StepTree(MainKFPerspective mainPerspective)
/*  47:    */   {
/*  48: 89 */     this.m_mainPerspective = mainPerspective;
/*  49: 90 */     DefaultMutableTreeNode jtreeRoot = new DefaultMutableTreeNode("Weka");
/*  50:    */     
/*  51:    */ 
/*  52: 93 */     InvisibleTreeModel model = new InvisibleTreeModel(jtreeRoot);
/*  53: 94 */     model.activateFilter(true);
/*  54: 95 */     setModel(model);
/*  55:    */     
/*  56: 97 */     setEnabled(true);
/*  57: 98 */     setToolTipText("");
/*  58: 99 */     setShowsRootHandles(true);
/*  59:100 */     setCellRenderer(new StepIconRenderer());
/*  60:101 */     DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
/*  61:102 */     selectionModel.setSelectionMode(1);
/*  62:103 */     setSelectionModel(selectionModel);
/*  63:    */     
/*  64:105 */     addMouseListener(new MouseAdapter()
/*  65:    */     {
/*  66:    */       public void mouseClicked(MouseEvent e)
/*  67:    */       {
/*  68:108 */         if (((e.getModifiers() & 0x10) != 16) || (e.isAltDown()))
/*  69:    */         {
/*  70:110 */           StepTree.this.m_mainPerspective.setFlowLayoutOperation(VisibleLayout.LayoutOperation.NONE);
/*  71:111 */           StepTree.this.m_mainPerspective.setPalleteSelectedStep(null);
/*  72:112 */           StepTree.this.m_mainPerspective.setCursor(Cursor.getPredefinedCursor(0));
/*  73:    */           
/*  74:114 */           StepTree.this.clearSelection();
/*  75:    */         }
/*  76:117 */         TreePath p = StepTree.this.getSelectionPath();
/*  77:118 */         if ((p != null) && 
/*  78:119 */           ((p.getLastPathComponent() instanceof DefaultMutableTreeNode)))
/*  79:    */         {
/*  80:120 */           DefaultMutableTreeNode tNode = (DefaultMutableTreeNode)p.getLastPathComponent();
/*  81:123 */           if (tNode.isLeaf())
/*  82:    */           {
/*  83:124 */             Object userObject = tNode.getUserObject();
/*  84:125 */             if ((userObject instanceof StepTreeLeafDetails)) {
/*  85:    */               try
/*  86:    */               {
/*  87:127 */                 StepVisual visual = ((StepTreeLeafDetails)userObject).instantiateStep();
/*  88:    */                 
/*  89:129 */                 StepTree.this.m_mainPerspective.setCursor(Cursor.getPredefinedCursor(1));
/*  90:131 */                 if (StepTree.this.m_mainPerspective.getDebug()) {
/*  91:132 */                   System.err.println("Instantiated " + visual.getStepName());
/*  92:    */                 }
/*  93:134 */                 StepTree.this.m_mainPerspective.setPalleteSelectedStep(visual.getStepManager());
/*  94:    */               }
/*  95:    */               catch (Exception ex)
/*  96:    */               {
/*  97:137 */                 StepTree.this.m_mainPerspective.showErrorDialog(ex);
/*  98:    */               }
/*  99:    */             }
/* 100:    */           }
/* 101:    */         }
/* 102:    */       }
/* 103:    */     });
/* 104:    */     try
/* 105:    */     {
/* 106:148 */       populateTree(jtreeRoot);
/* 107:    */     }
/* 108:    */     catch (Exception ex)
/* 109:    */     {
/* 110:150 */       ex.printStackTrace();
/* 111:    */     }
/* 112:153 */     expandRow(0);
/* 113:154 */     setRootVisible(false);
/* 114:    */   }
/* 115:    */   
/* 116:    */   protected void populateTree(DefaultMutableTreeNode jtreeRoot)
/* 117:    */     throws Exception
/* 118:    */   {
/* 119:165 */     Properties GOEProps = initGOEProps();
/* 120:    */     
/* 121:    */ 
/* 122:    */ 
/* 123:    */ 
/* 124:    */ 
/* 125:    */ 
/* 126:172 */     InputStream inputStream = getClass().getClassLoader().getResourceAsStream("weka/knowledgeflow/steps/steps.props");
/* 127:    */     
/* 128:174 */     Properties builtinSteps = new Properties();
/* 129:175 */     builtinSteps.load(inputStream);
/* 130:176 */     inputStream.close();
/* 131:177 */     inputStream = null;
/* 132:178 */     String stepClassNames = builtinSteps.getProperty("weka.knowledgeflow.steps.Step");
/* 133:    */     
/* 134:180 */     String[] s = stepClassNames.split(",");
/* 135:181 */     Set<String> stepImpls = new LinkedHashSet();
/* 136:182 */     stepImpls.addAll(Arrays.asList(s));
/* 137:183 */     populateTree(stepImpls, jtreeRoot, GOEProps);
/* 138:    */     
/* 139:    */ 
/* 140:186 */     Set<String> stepClasses = PluginManager.getPluginNamesOfType("weka.knowledgeflow.steps.Step");
/* 141:188 */     if ((stepClasses != null) && (stepClasses.size() > 0))
/* 142:    */     {
/* 143:196 */       Set<String> filteredStepClasses = new LinkedHashSet();
/* 144:197 */       for (String plugin : stepClasses) {
/* 145:198 */         if (!stepClassNames.contains(plugin)) {
/* 146:199 */           filteredStepClasses.add(plugin);
/* 147:    */         }
/* 148:    */       }
/* 149:202 */       populateTree(filteredStepClasses, jtreeRoot, GOEProps);
/* 150:    */     }
/* 151:    */   }
/* 152:    */   
/* 153:    */   protected void populateTree(Set<String> stepClasses, DefaultMutableTreeNode jtreeRoot, Properties GOEProps)
/* 154:    */     throws Exception
/* 155:    */   {
/* 156:216 */     for (String stepClass : stepClasses) {
/* 157:    */       try
/* 158:    */       {
/* 159:218 */         Step toAdd = (Step)Beans.instantiate(getClass().getClassLoader(), stepClass);
/* 160:221 */         if ((toAdd.getClass().getAnnotation(StepTreeIgnore.class) != null) || (toAdd.getClass().getAnnotation(KFIgnore.class) == null))
/* 161:    */         {
/* 162:226 */           String category = getStepCategory(toAdd);
/* 163:227 */           DefaultMutableTreeNode targetFolder = getCategoryFolder(jtreeRoot, category);
/* 164:230 */           if ((toAdd instanceof WekaAlgorithmWrapper))
/* 165:    */           {
/* 166:231 */             populateForWekaWrapper(targetFolder, (WekaAlgorithmWrapper)toAdd, GOEProps);
/* 167:    */           }
/* 168:    */           else
/* 169:    */           {
/* 170:234 */             StepTreeLeafDetails leafData = new StepTreeLeafDetails(toAdd);
/* 171:235 */             DefaultMutableTreeNode fixedLeafNode = new InvisibleNode(leafData);
/* 172:236 */             targetFolder.add(fixedLeafNode);
/* 173:    */             
/* 174:238 */             String tipText = leafData.getToolTipText() != null ? leafData.getToolTipText() : "";
/* 175:    */             
/* 176:    */ 
/* 177:241 */             this.m_nodeTextIndex.put(stepClass.toLowerCase() + " " + tipText, fixedLeafNode);
/* 178:    */           }
/* 179:    */         }
/* 180:    */       }
/* 181:    */       catch (Exception ex)
/* 182:    */       {
/* 183:245 */         ex.printStackTrace();
/* 184:    */       }
/* 185:    */     }
/* 186:    */   }
/* 187:    */   
/* 188:    */   protected void populateForWekaWrapper(DefaultMutableTreeNode targetFolder, WekaAlgorithmWrapper wrapper, Properties GOEProps)
/* 189:    */     throws Exception
/* 190:    */   {
/* 191:261 */     Class wrappedAlgoClass = wrapper.getWrappedAlgorithmClass();
/* 192:262 */     String implList = GOEProps.getProperty(wrappedAlgoClass.getCanonicalName());
/* 193:263 */     String hppRoot = wrappedAlgoClass.getCanonicalName();
/* 194:264 */     hppRoot = hppRoot.substring(0, hppRoot.lastIndexOf('.'));
/* 195:266 */     if (implList == null) {
/* 196:267 */       throw new WekaException("Unable to get a list of weka implementations for class '" + wrappedAlgoClass.getCanonicalName() + "'");
/* 197:    */     }
/* 198:272 */     Hashtable<String, String> roots = GenericObjectEditor.sortClassesByRoot(implList);
/* 199:274 */     for (Map.Entry<String, String> e : roots.entrySet())
/* 200:    */     {
/* 201:275 */       String classes = (String)e.getValue();
/* 202:276 */       HierarchyPropertyParser hpp = new HierarchyPropertyParser();
/* 203:277 */       hpp.build(classes, ", ");
/* 204:    */       
/* 205:279 */       hpp.goTo(hppRoot);
/* 206:280 */       processPackage(hpp, targetFolder, wrapper);
/* 207:    */     }
/* 208:    */   }
/* 209:    */   
/* 210:    */   protected void processPackage(HierarchyPropertyParser hpp, DefaultMutableTreeNode parentFolder, WekaAlgorithmWrapper wrapper)
/* 211:    */     throws Exception
/* 212:    */   {
/* 213:297 */     String[] primaryPackages = hpp.childrenValues();
/* 214:298 */     for (String primaryPackage : primaryPackages)
/* 215:    */     {
/* 216:299 */       hpp.goToChild(primaryPackage);
/* 217:300 */       if (hpp.isLeafReached())
/* 218:    */       {
/* 219:301 */         String algName = hpp.fullValue();
/* 220:302 */         Object wrappedA = Beans.instantiate(getClass().getClassLoader(), algName);
/* 221:305 */         if ((wrappedA.getClass().getAnnotation(StepTreeIgnore.class) == null) && (wrappedA.getClass().getAnnotation(KFIgnore.class) == null))
/* 222:    */         {
/* 223:307 */           WekaAlgorithmWrapper wrapperCopy = (WekaAlgorithmWrapper)Beans.instantiate(getClass().getClassLoader(), wrapper.getClass().getCanonicalName());
/* 224:    */           
/* 225:    */ 
/* 226:310 */           wrapperCopy.setWrappedAlgorithm(wrappedA);
/* 227:311 */           StepTreeLeafDetails leafData = new StepTreeLeafDetails(wrapperCopy);
/* 228:312 */           DefaultMutableTreeNode wrapperLeafNode = new InvisibleNode(leafData);
/* 229:313 */           parentFolder.add(wrapperLeafNode);
/* 230:314 */           String tipText = leafData.getToolTipText() != null ? leafData.getToolTipText() : "";
/* 231:    */           
/* 232:    */ 
/* 233:317 */           this.m_nodeTextIndex.put(algName.toLowerCase() + " " + tipText, wrapperLeafNode);
/* 234:    */         }
/* 235:321 */         hpp.goToParent();
/* 236:    */       }
/* 237:    */       else
/* 238:    */       {
/* 239:323 */         DefaultMutableTreeNode firstLevelOfMainAlgoType = new InvisibleNode(primaryPackage);
/* 240:    */         
/* 241:325 */         parentFolder.add(firstLevelOfMainAlgoType);
/* 242:326 */         processPackage(hpp, firstLevelOfMainAlgoType, wrapper);
/* 243:327 */         hpp.goToParent();
/* 244:    */       }
/* 245:    */     }
/* 246:    */   }
/* 247:    */   
/* 248:    */   protected DefaultMutableTreeNode getCategoryFolder(DefaultMutableTreeNode jtreeRoot, String category)
/* 249:    */   {
/* 250:342 */     DefaultMutableTreeNode targetFolder = null;
/* 251:    */     
/* 252:    */ 
/* 253:345 */     Enumeration<Object> children = jtreeRoot.children();
/* 254:346 */     while (children.hasMoreElements())
/* 255:    */     {
/* 256:347 */       Object child = children.nextElement();
/* 257:348 */       if (((child instanceof DefaultMutableTreeNode)) && 
/* 258:349 */         (((DefaultMutableTreeNode)child).getUserObject().toString().equals(category)))
/* 259:    */       {
/* 260:351 */         targetFolder = (DefaultMutableTreeNode)child;
/* 261:352 */         break;
/* 262:    */       }
/* 263:    */     }
/* 264:357 */     if (targetFolder == null)
/* 265:    */     {
/* 266:358 */       targetFolder = new InvisibleNode(category);
/* 267:359 */       jtreeRoot.add(targetFolder);
/* 268:    */     }
/* 269:362 */     return targetFolder;
/* 270:    */   }
/* 271:    */   
/* 272:    */   protected String getStepCategory(Step toAdd)
/* 273:    */   {
/* 274:374 */     String category = "Plugin";
/* 275:    */     
/* 276:376 */     Annotation a = toAdd.getClass().getAnnotation(KFStep.class);
/* 277:377 */     if (a != null) {
/* 278:378 */       category = ((KFStep)a).category();
/* 279:    */     }
/* 280:381 */     return category;
/* 281:    */   }
/* 282:    */   
/* 283:    */   protected Properties initGOEProps()
/* 284:    */     throws Exception
/* 285:    */   {
/* 286:391 */     Properties GOEProps = GenericPropertiesCreator.getGlobalOutputProperties();
/* 287:392 */     if (GOEProps == null)
/* 288:    */     {
/* 289:393 */       GenericPropertiesCreator creator = new GenericPropertiesCreator();
/* 290:394 */       if (creator.useDynamic())
/* 291:    */       {
/* 292:395 */         creator.execute(false);
/* 293:396 */         GOEProps = creator.getOutputProperties();
/* 294:    */       }
/* 295:    */       else
/* 296:    */       {
/* 297:398 */         GOEProps = Utils.readProperties("weka/gui/GenericObjectEditor.props");
/* 298:    */       }
/* 299:    */     }
/* 300:402 */     return GOEProps;
/* 301:    */   }
/* 302:    */   
/* 303:    */   public String getToolTipText(MouseEvent e)
/* 304:    */   {
/* 305:414 */     if (getRowForLocation(e.getX(), e.getY()) == -1) {
/* 306:415 */       return null;
/* 307:    */     }
/* 308:417 */     TreePath currPath = getPathForLocation(e.getX(), e.getY());
/* 309:418 */     if ((currPath.getLastPathComponent() instanceof DefaultMutableTreeNode))
/* 310:    */     {
/* 311:419 */       DefaultMutableTreeNode node = (DefaultMutableTreeNode)currPath.getLastPathComponent();
/* 312:421 */       if (node.isLeaf())
/* 313:    */       {
/* 314:422 */         StepTreeLeafDetails leaf = (StepTreeLeafDetails)node.getUserObject();
/* 315:423 */         return leaf.getToolTipText();
/* 316:    */       }
/* 317:    */     }
/* 318:426 */     return null;
/* 319:    */   }
/* 320:    */   
/* 321:    */   public void setShowLeafTipText(boolean show)
/* 322:    */   {
/* 323:435 */     DefaultMutableTreeNode root = (DefaultMutableTreeNode)getModel().getRoot();
/* 324:436 */     Enumeration e = root.depthFirstEnumeration();
/* 325:438 */     while (e.hasMoreElements())
/* 326:    */     {
/* 327:439 */       DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
/* 328:440 */       if (node.isLeaf()) {
/* 329:441 */         ((StepTreeLeafDetails)node.getUserObject()).setShowTipTexts(show);
/* 330:    */       }
/* 331:    */     }
/* 332:    */   }
/* 333:    */   
/* 334:    */   protected Map<String, DefaultMutableTreeNode> getNodeTextIndex()
/* 335:    */   {
/* 336:452 */     return this.m_nodeTextIndex;
/* 337:    */   }
/* 338:    */   
/* 339:    */   protected static class StepIconRenderer
/* 340:    */     extends DefaultTreeCellRenderer
/* 341:    */   {
/* 342:    */     private static final long serialVersionUID = -4488876734500244945L;
/* 343:    */     
/* 344:    */     public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
/* 345:    */     {
/* 346:463 */       super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
/* 347:466 */       if (leaf)
/* 348:    */       {
/* 349:467 */         Object userO = ((DefaultMutableTreeNode)value).getUserObject();
/* 350:468 */         if ((userO instanceof StepTreeLeafDetails))
/* 351:    */         {
/* 352:469 */           Icon i = ((StepTreeLeafDetails)userO).getIcon();
/* 353:470 */           if (i != null) {
/* 354:471 */             setIcon(i);
/* 355:    */           }
/* 356:    */         }
/* 357:    */       }
/* 358:475 */       return this;
/* 359:    */     }
/* 360:    */   }
/* 361:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.StepTree
 * JD-Core Version:    0.7.0.1
 */