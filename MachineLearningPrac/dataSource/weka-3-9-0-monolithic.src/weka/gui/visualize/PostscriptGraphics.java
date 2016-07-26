/*    1:     */ package weka.gui.visualize;
/*    2:     */ 
/*    3:     */ import java.awt.AlphaComposite;
/*    4:     */ import java.awt.BasicStroke;
/*    5:     */ import java.awt.Color;
/*    6:     */ import java.awt.Composite;
/*    7:     */ import java.awt.Font;
/*    8:     */ import java.awt.FontMetrics;
/*    9:     */ import java.awt.Graphics;
/*   10:     */ import java.awt.Graphics2D;
/*   11:     */ import java.awt.GraphicsConfiguration;
/*   12:     */ import java.awt.GraphicsDevice;
/*   13:     */ import java.awt.GraphicsEnvironment;
/*   14:     */ import java.awt.Image;
/*   15:     */ import java.awt.Paint;
/*   16:     */ import java.awt.Polygon;
/*   17:     */ import java.awt.Rectangle;
/*   18:     */ import java.awt.RenderingHints;
/*   19:     */ import java.awt.RenderingHints.Key;
/*   20:     */ import java.awt.Shape;
/*   21:     */ import java.awt.Stroke;
/*   22:     */ import java.awt.Toolkit;
/*   23:     */ import java.awt.font.FontRenderContext;
/*   24:     */ import java.awt.font.GlyphVector;
/*   25:     */ import java.awt.geom.AffineTransform;
/*   26:     */ import java.awt.image.BufferedImage;
/*   27:     */ import java.awt.image.BufferedImageOp;
/*   28:     */ import java.awt.image.ColorModel;
/*   29:     */ import java.awt.image.ImageObserver;
/*   30:     */ import java.awt.image.PixelGrabber;
/*   31:     */ import java.awt.image.RenderedImage;
/*   32:     */ import java.awt.image.renderable.RenderableImage;
/*   33:     */ import java.io.OutputStream;
/*   34:     */ import java.io.PrintStream;
/*   35:     */ import java.text.AttributedCharacterIterator;
/*   36:     */ import java.util.Calendar;
/*   37:     */ import java.util.Hashtable;
/*   38:     */ import java.util.Map;
/*   39:     */ 
/*   40:     */ public class PostscriptGraphics
/*   41:     */   extends Graphics2D
/*   42:     */ {
/*   43:     */   protected Rectangle m_extent;
/*   44:     */   protected PrintStream m_printstream;
/*   45:     */   protected GraphicsState m_psGraphicsState;
/*   46:     */   protected GraphicsState m_localGraphicsState;
/*   47:     */   protected static final boolean DEBUG = false;
/*   48:     */   
/*   49:     */   private class GraphicsState
/*   50:     */   {
/*   51:     */     protected Color m_currentColor;
/*   52:     */     protected Font m_currentFont;
/*   53:     */     protected Stroke m_currentStroke;
/*   54:     */     protected int m_xOffset;
/*   55:     */     protected int m_yOffset;
/*   56:     */     protected double m_xScale;
/*   57:     */     protected double m_yScale;
/*   58:     */     
/*   59:     */     GraphicsState()
/*   60:     */     {
/*   61: 108 */       this.m_currentColor = Color.white;
/*   62: 109 */       this.m_currentFont = new Font("Courier", 0, 11);
/*   63: 110 */       this.m_currentStroke = new BasicStroke();
/*   64: 111 */       this.m_xOffset = 0;
/*   65: 112 */       this.m_yOffset = 0;
/*   66: 113 */       this.m_xScale = 1.0D;
/*   67: 114 */       this.m_yScale = 1.0D;
/*   68:     */     }
/*   69:     */     
/*   70:     */     GraphicsState(GraphicsState copy)
/*   71:     */     {
/*   72: 123 */       this.m_currentColor = copy.m_currentColor;
/*   73: 124 */       this.m_currentFont = copy.m_currentFont;
/*   74: 125 */       this.m_currentStroke = copy.m_currentStroke;
/*   75: 126 */       this.m_xOffset = copy.m_xOffset;
/*   76: 127 */       this.m_yOffset = copy.m_yOffset;
/*   77: 128 */       this.m_xScale = copy.m_xScale;
/*   78: 129 */       this.m_yScale = copy.m_yScale;
/*   79:     */     }
/*   80:     */     
/*   81:     */     protected Stroke getStroke()
/*   82:     */     {
/*   83: 134 */       return this.m_currentStroke;
/*   84:     */     }
/*   85:     */     
/*   86:     */     protected void setStroke(Stroke s)
/*   87:     */     {
/*   88: 138 */       this.m_currentStroke = s;
/*   89:     */     }
/*   90:     */     
/*   91:     */     protected Font getFont()
/*   92:     */     {
/*   93: 143 */       return this.m_currentFont;
/*   94:     */     }
/*   95:     */     
/*   96:     */     protected void setFont(Font f)
/*   97:     */     {
/*   98: 147 */       this.m_currentFont = f;
/*   99:     */     }
/*  100:     */     
/*  101:     */     protected Color getColor()
/*  102:     */     {
/*  103: 152 */       return this.m_currentColor;
/*  104:     */     }
/*  105:     */     
/*  106:     */     protected void setColor(Color c)
/*  107:     */     {
/*  108: 156 */       this.m_currentColor = c;
/*  109:     */     }
/*  110:     */     
/*  111:     */     protected void setXOffset(int xo)
/*  112:     */     {
/*  113: 161 */       this.m_xOffset = xo;
/*  114:     */     }
/*  115:     */     
/*  116:     */     protected void setYOffset(int yo)
/*  117:     */     {
/*  118: 165 */       this.m_yOffset = yo;
/*  119:     */     }
/*  120:     */     
/*  121:     */     protected int getXOffset()
/*  122:     */     {
/*  123: 169 */       return this.m_xOffset;
/*  124:     */     }
/*  125:     */     
/*  126:     */     protected int getYOffset()
/*  127:     */     {
/*  128: 173 */       return this.m_yOffset;
/*  129:     */     }
/*  130:     */     
/*  131:     */     protected void setXScale(double x)
/*  132:     */     {
/*  133: 177 */       this.m_xScale = x;
/*  134:     */     }
/*  135:     */     
/*  136:     */     protected void setYScale(double y)
/*  137:     */     {
/*  138: 181 */       this.m_yScale = y;
/*  139:     */     }
/*  140:     */     
/*  141:     */     protected double getXScale()
/*  142:     */     {
/*  143: 185 */       return this.m_xScale;
/*  144:     */     }
/*  145:     */     
/*  146:     */     protected double getYScale()
/*  147:     */     {
/*  148: 189 */       return this.m_yScale;
/*  149:     */     }
/*  150:     */   }
/*  151:     */   
/*  152: 218 */   protected static Hashtable<String, String> m_PSFontReplacement = new Hashtable();
/*  153:     */   
/*  154:     */   static
/*  155:     */   {
/*  156: 219 */     m_PSFontReplacement.put("SansSerif.plain", "Helvetica.plain");
/*  157:     */     
/*  158:     */ 
/*  159:     */ 
/*  160:     */ 
/*  161: 224 */     m_PSFontReplacement.put("Dialog.plain", "Helvetica.plain");
/*  162:     */     
/*  163:     */ 
/*  164:     */ 
/*  165:     */ 
/*  166: 229 */     m_PSFontReplacement.put("Microsoft Sans Serif", "Helvetica.plain");
/*  167:     */     
/*  168:     */ 
/*  169:     */ 
/*  170:     */ 
/*  171:     */ 
/*  172:     */ 
/*  173:     */ 
/*  174:     */ 
/*  175:     */ 
/*  176:     */ 
/*  177:     */ 
/*  178:     */ 
/*  179:     */ 
/*  180:     */ 
/*  181: 244 */     m_PSFontReplacement.put("MicrosoftSansSerif", "Helvetica.plain");
/*  182:     */   }
/*  183:     */   
/*  184:     */   public PostscriptGraphics(int width, int height, OutputStream os)
/*  185:     */   {
/*  186: 269 */     this.m_extent = new Rectangle(0, 0, height, width);
/*  187: 270 */     this.m_printstream = new PrintStream(os);
/*  188: 271 */     this.m_localGraphicsState = new GraphicsState();
/*  189: 272 */     this.m_psGraphicsState = new GraphicsState();
/*  190:     */     
/*  191: 274 */     Header();
/*  192:     */   }
/*  193:     */   
/*  194:     */   PostscriptGraphics(PostscriptGraphics copy)
/*  195:     */   {
/*  196: 284 */     this.m_extent = new Rectangle(copy.m_extent);
/*  197: 285 */     this.m_printstream = copy.m_printstream;
/*  198: 286 */     this.m_localGraphicsState = new GraphicsState(copy.m_localGraphicsState);
/*  199:     */     
/*  200:     */ 
/*  201:     */ 
/*  202:     */ 
/*  203:     */ 
/*  204:     */ 
/*  205:     */ 
/*  206: 294 */     this.m_psGraphicsState = copy.m_psGraphicsState;
/*  207:     */   }
/*  208:     */   
/*  209:     */   public void finished()
/*  210:     */   {
/*  211: 302 */     this.m_printstream.flush();
/*  212:     */   }
/*  213:     */   
/*  214:     */   private void Header()
/*  215:     */   {
/*  216: 309 */     this.m_printstream.println("%!PS-Adobe-3.0 EPSF-3.0");
/*  217: 310 */     this.m_printstream.println("%%BoundingBox: 0 0 " + xScale(this.m_extent.width) + " " + yScale(this.m_extent.height));
/*  218:     */     
/*  219: 312 */     this.m_printstream.println("%%CreationDate: " + Calendar.getInstance().getTime());
/*  220:     */     
/*  221:     */ 
/*  222: 315 */     this.m_printstream.println("/Oval { % x y w h filled");
/*  223: 316 */     this.m_printstream.println("gsave");
/*  224: 317 */     this.m_printstream.println("/filled exch def /h exch def /w exch def /y exch def /x exch def");
/*  225:     */     
/*  226: 319 */     this.m_printstream.println("x w 2 div add y h 2 div sub translate");
/*  227: 320 */     this.m_printstream.println("1 h w div scale");
/*  228: 321 */     this.m_printstream.println("filled {0 0 moveto} if");
/*  229: 322 */     this.m_printstream.println("0 0 w 2 div 0 360 arc");
/*  230: 323 */     this.m_printstream.println("filled {closepath fill} {stroke} ifelse grestore} bind def");
/*  231:     */     
/*  232:     */ 
/*  233: 326 */     this.m_printstream.println("/Rect { % x y w h filled");
/*  234: 327 */     this.m_printstream.println("/filled exch def /h exch def /w exch def /y exch def /x exch def");
/*  235:     */     
/*  236: 329 */     this.m_printstream.println("newpath ");
/*  237: 330 */     this.m_printstream.println("x y moveto");
/*  238: 331 */     this.m_printstream.println("w 0 rlineto");
/*  239: 332 */     this.m_printstream.println("0 h neg rlineto");
/*  240: 333 */     this.m_printstream.println("w neg 0 rlineto");
/*  241: 334 */     this.m_printstream.println("closepath");
/*  242: 335 */     this.m_printstream.println("filled {fill} {stroke} ifelse} bind def");
/*  243:     */     
/*  244: 337 */     this.m_printstream.println("%%BeginProlog\n%%EndProlog");
/*  245: 338 */     this.m_printstream.println("%%Page 1 1");
/*  246: 339 */     setFont(null);
/*  247: 340 */     setColor(null);
/*  248: 341 */     setStroke(null);
/*  249:     */   }
/*  250:     */   
/*  251:     */   public static void addPSFontReplacement(String replace, String with)
/*  252:     */   {
/*  253: 352 */     m_PSFontReplacement.put(replace, with);
/*  254:     */   }
/*  255:     */   
/*  256:     */   private int yTransform(int y)
/*  257:     */   {
/*  258: 363 */     return this.m_extent.height - (this.m_localGraphicsState.getYOffset() + y);
/*  259:     */   }
/*  260:     */   
/*  261:     */   private int xTransform(int x)
/*  262:     */   {
/*  263: 373 */     return this.m_localGraphicsState.getXOffset() + x;
/*  264:     */   }
/*  265:     */   
/*  266:     */   private int doScale(int number, double factor)
/*  267:     */   {
/*  268: 380 */     return (int)StrictMath.round(number * factor);
/*  269:     */   }
/*  270:     */   
/*  271:     */   private int xScale(int x)
/*  272:     */   {
/*  273: 387 */     return doScale(x, this.m_localGraphicsState.getXScale());
/*  274:     */   }
/*  275:     */   
/*  276:     */   private int yScale(int y)
/*  277:     */   {
/*  278: 394 */     return doScale(y, this.m_localGraphicsState.getYScale());
/*  279:     */   }
/*  280:     */   
/*  281:     */   private void setStateToLocal()
/*  282:     */   {
/*  283: 401 */     setColor(getColor());
/*  284: 402 */     setFont(getFont());
/*  285: 403 */     setStroke(getStroke());
/*  286:     */   }
/*  287:     */   
/*  288:     */   private String toHex(int i)
/*  289:     */   {
/*  290: 413 */     String result = Integer.toHexString(i);
/*  291: 414 */     if (result.length() < 2) {
/*  292: 415 */       result = "0" + result;
/*  293:     */     }
/*  294: 418 */     return result;
/*  295:     */   }
/*  296:     */   
/*  297:     */   public void clearRect(int x, int y, int width, int height)
/*  298:     */   {
/*  299: 433 */     setStateToLocal();
/*  300: 434 */     Color saveColor = getColor();
/*  301: 435 */     setColor(Color.white);
/*  302: 436 */     this.m_printstream.println(xTransform(xScale(x)) + " " + yTransform(yScale(y)) + " " + xScale(width) + " " + yScale(height) + " true Rect");
/*  303:     */     
/*  304: 438 */     setColor(saveColor);
/*  305:     */   }
/*  306:     */   
/*  307:     */   public Graphics create()
/*  308:     */   {
/*  309: 463 */     PostscriptGraphics psg = new PostscriptGraphics(this);
/*  310: 464 */     return psg;
/*  311:     */   }
/*  312:     */   
/*  313:     */   public void draw3DRect(int x, int y, int width, int height, boolean raised)
/*  314:     */   {
/*  315: 486 */     drawRect(x, y, width, height);
/*  316:     */   }
/*  317:     */   
/*  318:     */   public void drawBytes(byte[] data, int offset, int length, int x, int y)
/*  319:     */   {
/*  320: 504 */     drawString(new String(data, offset, length), x, y);
/*  321:     */   }
/*  322:     */   
/*  323:     */   public void drawChars(char[] data, int offset, int length, int x, int y)
/*  324:     */   {
/*  325: 514 */     drawString(new String(data, offset, length), x, y);
/*  326:     */   }
/*  327:     */   
/*  328:     */   public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer)
/*  329:     */   {
/*  330: 525 */     return drawImage(img, x, y, img.getWidth(observer), img.getHeight(observer), bgcolor, observer);
/*  331:     */   }
/*  332:     */   
/*  333:     */   public boolean drawImage(Image img, int x, int y, ImageObserver observer)
/*  334:     */   {
/*  335: 538 */     return drawImage(img, x, y, Color.WHITE, observer);
/*  336:     */   }
/*  337:     */   
/*  338:     */   public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer)
/*  339:     */   {
/*  340:     */     try
/*  341:     */     {
/*  342: 552 */       int[] pixels = new int[width * height];
/*  343: 553 */       PixelGrabber grabber = new PixelGrabber(img, 0, 0, width, height, pixels, 0, width);
/*  344:     */       
/*  345: 555 */       grabber.grabPixels();
/*  346: 556 */       ColorModel model = ColorModel.getRGBdefault();
/*  347:     */       
/*  348:     */ 
/*  349: 559 */       this.m_printstream.println("gsave");
/*  350: 560 */       this.m_printstream.println(xTransform(xScale(x)) + " " + (yTransform(yScale(y)) - yScale(height)) + " translate");
/*  351:     */       
/*  352: 562 */       this.m_printstream.println(xScale(width) + " " + yScale(height) + " scale");
/*  353: 563 */       this.m_printstream.println(width + " " + height + " " + "8" + " [" + width + " 0 0 " + -height + " 0 " + height + "]");
/*  354:     */       
/*  355: 565 */       this.m_printstream.println("{<");
/*  356: 568 */       for (int i = 0; i < height; i++)
/*  357:     */       {
/*  358: 569 */         for (int j = 0; j < width; j++)
/*  359:     */         {
/*  360: 570 */           int index = i * width + j;
/*  361: 571 */           this.m_printstream.print(toHex(model.getRed(pixels[index])));
/*  362: 572 */           this.m_printstream.print(toHex(model.getGreen(pixels[index])));
/*  363: 573 */           this.m_printstream.print(toHex(model.getBlue(pixels[index])));
/*  364:     */         }
/*  365: 575 */         this.m_printstream.println();
/*  366:     */       }
/*  367: 578 */       this.m_printstream.println(">}");
/*  368: 579 */       this.m_printstream.println("false 3 colorimage");
/*  369: 580 */       this.m_printstream.println("grestore");
/*  370: 581 */       return true;
/*  371:     */     }
/*  372:     */     catch (Exception e)
/*  373:     */     {
/*  374: 583 */       e.printStackTrace();
/*  375:     */     }
/*  376: 584 */     return false;
/*  377:     */   }
/*  378:     */   
/*  379:     */   public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer)
/*  380:     */   {
/*  381: 598 */     return drawImage(img, x, y, width, height, Color.WHITE, observer);
/*  382:     */   }
/*  383:     */   
/*  384:     */   public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer)
/*  385:     */   {
/*  386: 607 */     return false;
/*  387:     */   }
/*  388:     */   
/*  389:     */   public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer)
/*  390:     */   {
/*  391: 619 */     return drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, Color.WHITE, observer);
/*  392:     */   }
/*  393:     */   
/*  394:     */   public void drawLine(int x1, int y1, int x2, int y2)
/*  395:     */   {
/*  396: 633 */     setStateToLocal();
/*  397: 634 */     this.m_printstream.println(xTransform(xScale(x1)) + " " + yTransform(yScale(y1)) + " moveto " + xTransform(xScale(x2)) + " " + yTransform(yScale(y2)) + " lineto stroke");
/*  398:     */   }
/*  399:     */   
/*  400:     */   public void drawOval(int x, int y, int width, int height)
/*  401:     */   {
/*  402: 649 */     setStateToLocal();
/*  403: 650 */     this.m_printstream.println(xTransform(xScale(x)) + " " + yTransform(yScale(y)) + " " + xScale(width) + " " + yScale(height) + " false Oval");
/*  404:     */   }
/*  405:     */   
/*  406:     */   public void drawRect(int x, int y, int width, int height)
/*  407:     */   {
/*  408: 678 */     setStateToLocal();
/*  409: 679 */     this.m_printstream.println(xTransform(xScale(x)) + " " + yTransform(yScale(y)) + " " + xScale(width) + " " + yScale(height) + " false Rect");
/*  410:     */   }
/*  411:     */   
/*  412:     */   protected String escape(String s)
/*  413:     */   {
/*  414: 708 */     StringBuffer result = new StringBuffer();
/*  415: 710 */     for (int i = 0; i < s.length(); i++)
/*  416:     */     {
/*  417: 711 */       if ((s.charAt(i) == '(') || (s.charAt(i) == ')')) {
/*  418: 712 */         result.append('\\');
/*  419:     */       }
/*  420: 714 */       result.append(s.charAt(i));
/*  421:     */     }
/*  422: 717 */     return result.toString();
/*  423:     */   }
/*  424:     */   
/*  425:     */   public void drawString(String str, int x, int y)
/*  426:     */   {
/*  427: 729 */     setStateToLocal();
/*  428: 730 */     this.m_printstream.println(xTransform(xScale(x)) + " " + yTransform(yScale(y)) + " moveto" + " (" + escape(str) + ") show stroke");
/*  429:     */   }
/*  430:     */   
/*  431:     */   public void fill3DRect(int x, int y, int width, int height, boolean raised)
/*  432:     */   {
/*  433: 746 */     fillRect(x, y, width, height);
/*  434:     */   }
/*  435:     */   
/*  436:     */   public void fillOval(int x, int y, int width, int height)
/*  437:     */   {
/*  438: 767 */     setStateToLocal();
/*  439: 768 */     this.m_printstream.println(xTransform(xScale(x)) + " " + yTransform(yScale(y)) + " " + xScale(width) + " " + yScale(height) + " true Oval");
/*  440:     */   }
/*  441:     */   
/*  442:     */   public void fillRect(int x, int y, int width, int height)
/*  443:     */   {
/*  444: 797 */     if ((width == this.m_extent.width) && (height == this.m_extent.height))
/*  445:     */     {
/*  446: 798 */       clearRect(x, y, width, height);
/*  447:     */     }
/*  448:     */     else
/*  449:     */     {
/*  450: 804 */       setStateToLocal();
/*  451: 805 */       this.m_printstream.println(xTransform(xScale(x)) + " " + yTransform(yScale(y)) + " " + xScale(width) + " " + yScale(height) + " true Rect");
/*  452:     */     }
/*  453:     */   }
/*  454:     */   
/*  455:     */   public Shape getClip()
/*  456:     */   {
/*  457: 830 */     return null;
/*  458:     */   }
/*  459:     */   
/*  460:     */   public Rectangle getClipBounds()
/*  461:     */   {
/*  462: 840 */     return new Rectangle(0, 0, this.m_extent.width, this.m_extent.height);
/*  463:     */   }
/*  464:     */   
/*  465:     */   public Rectangle getClipBounds(Rectangle r)
/*  466:     */   {
/*  467: 850 */     r.setBounds(0, 0, this.m_extent.width, this.m_extent.height);
/*  468: 851 */     return r;
/*  469:     */   }
/*  470:     */   
/*  471:     */   public Rectangle getClipRect()
/*  472:     */   {
/*  473: 859 */     return null;
/*  474:     */   }
/*  475:     */   
/*  476:     */   public Color getColor()
/*  477:     */   {
/*  478: 869 */     return this.m_localGraphicsState.getColor();
/*  479:     */   }
/*  480:     */   
/*  481:     */   public Font getFont()
/*  482:     */   {
/*  483: 879 */     return this.m_localGraphicsState.getFont();
/*  484:     */   }
/*  485:     */   
/*  486:     */   public FontMetrics getFontMetrics(Font f)
/*  487:     */   {
/*  488: 891 */     return Toolkit.getDefaultToolkit().getFontMetrics(f);
/*  489:     */   }
/*  490:     */   
/*  491:     */   public void setColor(Color c)
/*  492:     */   {
/*  493: 916 */     if (c != null)
/*  494:     */     {
/*  495: 917 */       this.m_localGraphicsState.setColor(c);
/*  496: 918 */       if (this.m_psGraphicsState.getColor().equals(c)) {
/*  497: 919 */         return;
/*  498:     */       }
/*  499: 921 */       this.m_psGraphicsState.setColor(c);
/*  500:     */     }
/*  501:     */     else
/*  502:     */     {
/*  503: 923 */       this.m_localGraphicsState.setColor(Color.black);
/*  504: 924 */       this.m_psGraphicsState.setColor(getColor());
/*  505:     */     }
/*  506: 926 */     this.m_printstream.print(getColor().getRed() / 255.0D);
/*  507: 927 */     this.m_printstream.print(" ");
/*  508: 928 */     this.m_printstream.print(getColor().getGreen() / 255.0D);
/*  509: 929 */     this.m_printstream.print(" ");
/*  510: 930 */     this.m_printstream.print(getColor().getBlue() / 255.0D);
/*  511: 931 */     this.m_printstream.println(" setrgbcolor");
/*  512:     */   }
/*  513:     */   
/*  514:     */   private static String replacePSFont(String font)
/*  515:     */   {
/*  516: 940 */     String result = font;
/*  517: 943 */     if (m_PSFontReplacement.containsKey(font)) {
/*  518: 944 */       result = ((String)m_PSFontReplacement.get(font)).toString();
/*  519:     */     }
/*  520: 951 */     return result;
/*  521:     */   }
/*  522:     */   
/*  523:     */   public void setFont(Font font)
/*  524:     */   {
/*  525: 962 */     if (font != null)
/*  526:     */     {
/*  527: 963 */       this.m_localGraphicsState.setFont(font);
/*  528: 964 */       if ((font.getName().equals(this.m_psGraphicsState.getFont().getName())) && (this.m_psGraphicsState.getFont().getStyle() == font.getStyle()) && (this.m_psGraphicsState.getFont().getSize() == yScale(font.getSize()))) {
/*  529: 967 */         return;
/*  530:     */       }
/*  531: 969 */       this.m_psGraphicsState.setFont(new Font(font.getName(), font.getStyle(), yScale(getFont().getSize())));
/*  532:     */     }
/*  533:     */     else
/*  534:     */     {
/*  535: 972 */       this.m_localGraphicsState.setFont(new Font("Courier", 0, 11));
/*  536: 973 */       this.m_psGraphicsState.setFont(getFont());
/*  537:     */     }
/*  538: 976 */     this.m_printstream.println("/(" + replacePSFont(getFont().getPSName()) + ")" + " findfont");
/*  539:     */     
/*  540: 978 */     this.m_printstream.println(yScale(getFont().getSize()) + " scalefont setfont");
/*  541:     */   }
/*  542:     */   
/*  543:     */   public void translate(int x, int y)
/*  544:     */   {
/*  545:1010 */     this.m_localGraphicsState.setXOffset(this.m_localGraphicsState.getXOffset() + xScale(x));
/*  546:     */     
/*  547:1012 */     this.m_localGraphicsState.setYOffset(this.m_localGraphicsState.getYOffset() + yScale(y));
/*  548:     */     
/*  549:1014 */     this.m_psGraphicsState.setXOffset(this.m_psGraphicsState.getXOffset() + xScale(x));
/*  550:1015 */     this.m_psGraphicsState.setYOffset(this.m_psGraphicsState.getYOffset() + yScale(y));
/*  551:     */   }
/*  552:     */   
/*  553:     */   public FontRenderContext getFontRenderContext()
/*  554:     */   {
/*  555:1024 */     return new FontRenderContext(null, true, true);
/*  556:     */   }
/*  557:     */   
/*  558:     */   public Stroke getStroke()
/*  559:     */   {
/*  560:1033 */     return this.m_localGraphicsState.getStroke();
/*  561:     */   }
/*  562:     */   
/*  563:     */   public Color getBackground()
/*  564:     */   {
/*  565:1038 */     return Color.white;
/*  566:     */   }
/*  567:     */   
/*  568:     */   public Composite getComposite()
/*  569:     */   {
/*  570:1047 */     return AlphaComposite.getInstance(2);
/*  571:     */   }
/*  572:     */   
/*  573:     */   public Paint getPaint()
/*  574:     */   {
/*  575:1052 */     return new Color(getColor().getRed(), getColor().getGreen(), getColor().getBlue());
/*  576:     */   }
/*  577:     */   
/*  578:     */   public AffineTransform getTransform()
/*  579:     */   {
/*  580:1058 */     return new AffineTransform();
/*  581:     */   }
/*  582:     */   
/*  583:     */   public void scale(double d1, double d2)
/*  584:     */   {
/*  585:1075 */     this.m_localGraphicsState.setXScale(d1);
/*  586:1076 */     this.m_localGraphicsState.setYScale(d2);
/*  587:     */   }
/*  588:     */   
/*  589:     */   public RenderingHints getRenderingHints()
/*  590:     */   {
/*  591:1096 */     return new RenderingHints(null);
/*  592:     */   }
/*  593:     */   
/*  594:     */   public Object getRenderingHint(RenderingHints.Key key)
/*  595:     */   {
/*  596:1109 */     return null;
/*  597:     */   }
/*  598:     */   
/*  599:     */   public void setStroke(Stroke s)
/*  600:     */   {
/*  601:1118 */     if (s != null)
/*  602:     */     {
/*  603:1119 */       this.m_localGraphicsState.setStroke(s);
/*  604:1120 */       if (s.equals(this.m_psGraphicsState.getStroke())) {
/*  605:1121 */         return;
/*  606:     */       }
/*  607:1123 */       this.m_psGraphicsState.setStroke(s);
/*  608:     */     }
/*  609:     */     else
/*  610:     */     {
/*  611:1125 */       this.m_localGraphicsState.setStroke(new BasicStroke());
/*  612:1126 */       this.m_psGraphicsState.setStroke(getStroke());
/*  613:     */     }
/*  614:     */   }
/*  615:     */   
/*  616:     */   public GraphicsConfiguration getDeviceConfiguration()
/*  617:     */   {
/*  618:1141 */     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
/*  619:1142 */     GraphicsDevice gd = ge.getDefaultScreenDevice();
/*  620:1143 */     return gd.getDefaultConfiguration();
/*  621:     */   }
/*  622:     */   
/*  623:     */   public boolean hit(Rectangle r, Shape s, boolean onstroke)
/*  624:     */   {
/*  625:1148 */     return false;
/*  626:     */   }
/*  627:     */   
/*  628:     */   public void drawString(String str, float x, float y)
/*  629:     */   {
/*  630:1165 */     drawString(str, (int)x, (int)y);
/*  631:     */   }
/*  632:     */   
/*  633:     */   public boolean drawImage(Image im, AffineTransform at, ImageObserver io)
/*  634:     */   {
/*  635:1182 */     return false;
/*  636:     */   }
/*  637:     */   
/*  638:     */   public void clipRect(int x, int y, int width, int height) {}
/*  639:     */   
/*  640:     */   public void copyArea(int x, int y, int width, int height, int dx, int dy) {}
/*  641:     */   
/*  642:     */   public void dispose() {}
/*  643:     */   
/*  644:     */   public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {}
/*  645:     */   
/*  646:     */   public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {}
/*  647:     */   
/*  648:     */   public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {}
/*  649:     */   
/*  650:     */   public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {}
/*  651:     */   
/*  652:     */   public void drawString(AttributedCharacterIterator iterator, int x, int y) {}
/*  653:     */   
/*  654:     */   public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {}
/*  655:     */   
/*  656:     */   public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {}
/*  657:     */   
/*  658:     */   public void fillPolygon(Polygon p) {}
/*  659:     */   
/*  660:     */   public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {}
/*  661:     */   
/*  662:     */   public void finalize() {}
/*  663:     */   
/*  664:     */   public void setClip(int x, int y, int width, int height) {}
/*  665:     */   
/*  666:     */   public void setClip(Shape clip) {}
/*  667:     */   
/*  668:     */   public void setPaintMode() {}
/*  669:     */   
/*  670:     */   public void setXORMode(Color c1) {}
/*  671:     */   
/*  672:     */   public void clip(Shape s) {}
/*  673:     */   
/*  674:     */   public void setBackground(Color c) {}
/*  675:     */   
/*  676:     */   public void setTransform(AffineTransform at) {}
/*  677:     */   
/*  678:     */   public void transform(AffineTransform at) {}
/*  679:     */   
/*  680:     */   public void shear(double d1, double d2) {}
/*  681:     */   
/*  682:     */   public void rotate(double d1, double d2, double d3) {}
/*  683:     */   
/*  684:     */   public void rotate(double d1) {}
/*  685:     */   
/*  686:     */   public void translate(double d1, double d2) {}
/*  687:     */   
/*  688:     */   public void addRenderingHints(Map<?, ?> m) {}
/*  689:     */   
/*  690:     */   public void setRenderingHints(Map<?, ?> m) {}
/*  691:     */   
/*  692:     */   public void setRenderingHint(RenderingHints.Key key, Object o) {}
/*  693:     */   
/*  694:     */   public void setPaint(Paint p) {}
/*  695:     */   
/*  696:     */   public void setComposite(Composite c) {}
/*  697:     */   
/*  698:     */   public void fill(Shape s) {}
/*  699:     */   
/*  700:     */   public void drawGlyphVector(GlyphVector gv, float f1, float f2) {}
/*  701:     */   
/*  702:     */   public void drawString(AttributedCharacterIterator aci, float f1, float f2) {}
/*  703:     */   
/*  704:     */   public void drawRenderableImage(RenderableImage ri, AffineTransform at) {}
/*  705:     */   
/*  706:     */   public void drawRenderedImage(RenderedImage ri, AffineTransform af) {}
/*  707:     */   
/*  708:     */   public void drawImage(BufferedImage bi, BufferedImageOp bio, int i1, int i2) {}
/*  709:     */   
/*  710:     */   public void draw(Shape s) {}
/*  711:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.PostscriptGraphics
 * JD-Core Version:    0.7.0.1
 */