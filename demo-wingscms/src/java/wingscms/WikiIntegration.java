package wingscms;

import java.util.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;

import org.wings.*;
import org.wings.session.*;

import java.awt.Color;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

/**
 * Wiki integration example
 *
 * @author <a href="mailto:christian.fetzer@uni-ulm.de">Christian Fetzer</a>
 */
public class WikiIntegration {

    private SFrame rootFrame = new SFrame();
    private SPanel panel = new SPanel();
    private STemplateLayout templateLayout = new STemplateLayout();

    /**
     * Default constructor
     */
    public WikiIntegration() {

//        // Sets the session's resource mapper
//        SessionManager.getSession().setResourceMapper(new WikiResourceMapper(rootFrame));
//
//        // Shoppingcart contents (Key = product index in PLCONTENT, Value = amount)
//        //final HashMap<Integer, Integer> SCCONTENT = new HashMap<Integer, Integer>();
//        //  SessionManager.getSession().setProperty("shoppingcart", SCCONTENT);
//        final LinkedList<ShoppingCartItem> SCLIST = new LinkedList<ShoppingCartItem>();
//
//        // Pricelist panel contains the pricelist table,
//        // an addtoshoppingcart Button and a message label.
//        final SPanel ppricelist = new SPanel(new SFlowDownLayout());
//
//        // Result label
//        final SLabel message = new SLabel();
//
//        // Products
//        final LinkedList<Product> PRODUCTS = new LinkedList<Product>();
//        PRODUCTS.add(new Product(1001, "Rotes Auto", 50000d));
//        PRODUCTS.add(new Product(1002, "Blaues Auto", 75000d));
//        PRODUCTS.add(new Product(1003, "Schwarzes Auto", 100000d));
//
//        // Productlist table
//        final String[] PLCOLNAMES = { "Art. Nr.", "Artikel", "Stückpreis (in €)" };
//        final STable productlist = new STable(new DefaultTableModel() {
//            public Object getValueAt(int row, int col) {
//                Product product = PRODUCTS.get(row);
//                switch (col) {
//                    case 0:
//                        return product.getItemnumber();
//                    case 1:
//                        return product.getDescription();
//                    case 2:
//                        return product.getPrice();
//                    default:
//                        return null;
//                }
//            }
//
//            public int getRowCount() {
//                return PRODUCTS.size();
//            }
//
//            public int getColumnCount() {
//                return PLCOLNAMES.length;
//            }
//
//            public String getColumnName(int i) {
//                return PLCOLNAMES[i];
//            }
//        });
//        productlist.addSelectionListener(new ListSelectionListener() {
//            public void valueChanged(ListSelectionEvent e) {
//                if (productlist.getSelectedRow() != -1) {
//                    message.setText("");
//                    ppricelist.reload();
//                }
//            }
//        });
//        productlist.setEditable(false);
//
//        // Shoppingcart table
//        final String[] SCCOLNAMES = { "Art. Nr.", "Artikel", "Stückpreis (in €)", "Anzahl", "Gesamtpreis (in €)" };
//        final STable shoppingcart = new STable(new DefaultTableModel() {
//            public Object getValueAt(int row, int col) {
//                ShoppingCartItem item = SCLIST.get(row);
//                switch (col) {
//                    case 0:
//                        return item.getProduct().getItemnumber();
//                    case 1:
//                        return item.getProduct().getDescription();
//                    case 2:
//                        return item.getProduct().getPrice();
//                    case 3:
//                        return item.getAmount();
//                    case 4:
//                        return item.getAllRoundPrice();
//                    default:
//                        return null;
//                }
//            }
//
//            public int getRowCount() {
//                return SCLIST.size();
//            }
//
//            public int getColumnCount() {
//                return SCCOLNAMES.length;
//            }
//
//            public String getColumnName(int i) {
//                return SCCOLNAMES[i];
//            }
//        });
//        shoppingcart.setEditable(false);
//
//        // Addtoshoppingcart button
//        SButton addtoshoppingcart = new SButton("In den Warenkorb");
//        addtoshoppingcart.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent ae) {
//                if (productlist.getSelectedRow() != -1) {
//                    Product selection = PRODUCTS.get(productlist.getSelectedRow());
//
//                    // If the selected item is already in the shoppingcart just increase the amount
//                    boolean isinlist = false;
//                    for (ShoppingCartItem item : SCLIST) {
//                        if (item.getProduct().equals(selection)) {
//                            item.setAmount(item.getAmount() + 1);
//                            isinlist = true;
//                            break;
//                        }
//                    }
//                    if (!isinlist) {
//                        SCLIST.add(new ShoppingCartItem(selection));
//                        shoppingcart.reload();
//                    }
//
//                    message.setForeground(Color.BLACK);
//                    message.setText(
//                            "Das Produkt \"" + selection.getDescription() + "\" wurde zum Warenkorb hinzugefügt.");
//                    productlist.clearSelection();
//                    ppricelist.reload();
//                } else {
//                    message.setForeground(Color.RED);
//                    message.setText("Fehler: Kein Produkt ausgewählt.");
//                }
//            }
//        });
//
//        // Pricelist panel
//        ppricelist.add(productlist);
//        ppricelist.add(addtoshoppingcart);
//
//        panel.add(ppricelist, "PRICELIST");
//        panel.add(shoppingcart, "SHOPPINGCART");
//        panel.add(message, "MESSAGE");
//
//        panel.setLayout(templateLayout);
//        panel.setPreferredSize(SDimension.FULLAREA);
//        panel.setVerticalAlignment(SConstants.TOP_ALIGN);
//
//        rootFrame.getContentPane().add(panel);
//        rootFrame.getContentPane().setPreferredSize(SDimension.FULLAREA);
//        rootFrame.getContentPane().setVerticalAlignment(SConstants.TOP_ALIGN);
//        rootFrame.setBorder(null);
//        rootFrame.setAttribute("margin", "0px");
//        rootFrame.setVisible(true);
    }

//    /**
//     * Implements a wiki resource mapper:
//     * Load wiki page as template (with caching)
//     */
//    class WikiResourceMapper
//            implements ResourceMapper {
//        final static String WIKIHOST = "localhost";
//        final static String TEMPLATEPATH = "/joomla";
//
//        DynamicResource defaultResource;
//        Map<String, String> contentMap = new HashMap<String, String>();
//        Map<String, Date> obtainedMap = new HashMap<String, Date>();
//        SimpleDateFormat httpdate;
//        private String path;
//
//        private SFrame frame;
//        private List<Script> scripts = new ArrayList<Script>();
//        private Collection<Link> links = new ArrayList<Link>();
//
//        /**
//         * Determine the DefaultResource and initialize the httpdate
//         *
//         * @param frame
//         */
//        public WikiResourceMapper(SFrame frame) {
//            this.frame = frame;
//            defaultResource = frame.getDynamicResource(ReloadResource.class);
//
//            // httpdate parses and formats dates in HTTP Date Format (RFC1123)
//            httpdate = new SimpleDateFormat(DateParser.PATTERN_RFC1123, Locale.ENGLISH);
//            httpdate.setTimeZone(TimeZone.getTimeZone("GMT"));
//
//            navigate(SessionManager.getSession().getServletRequest().getPathInfo());
//        }
//
//        /**
//         * Load wiki page as template
//         *
//         * @param url the path info
//         * @return the default resource
//         */
//        public Resource mapResource(String url) {
//            navigate(url);
//            return defaultResource;
//        }
//
////        private void navigate(String url) {
////
////            Map<String, String[]> parameters = SessionManager.getSession().getServletRequest().getParameterMap();
////
////            url += "?";
////            for (String key : parameters.keySet()) {
////                url += key + "=" + parameters.get(key)[0] + "&";
////            }
////            url = url.substring(0, url.length() - 1);
////
////            HttpMethod method = new GetMethod("http://" + WIKIHOST + TEMPLATEPATH + url);
////            HttpClient httpclient = new HttpClient();
////            String templateString = null;
////
////            // Sets the "If-Modified-Since" header to the date in cache
////            if (obtainedMap.containsKey(url)) {
////                method.addRequestHeader("If-Modified-Since", httpdate.format(obtainedMap.get(url)));
////            }
////
////            try {
////                // Execute http request
////                int httpstatus = httpclient.executeMethod(method);
////
////                // Invoke handleUnknownResourceRequested
////                if (httpstatus != HttpStatus.SC_OK && httpstatus != HttpStatus.SC_NOT_MODIFIED)
////                    return;
////
////                // If the 'If-Modified-Since' header is sent, the server should set the status to
////                // SC_NOT_MODIFIED (304). Sometimes this does not work. In this case we try to compare the
////                // 'Last-Modified' response header with the date in cache ourselves.
////                boolean cached = true;
////                if (httpstatus == HttpStatus.SC_OK) {
////                    try {
////                        Date httplastmodified = httpdate.parse(method.getResponseHeader("Last-Modified").getValue());
////                        if (!httplastmodified.before(obtainedMap.get(url))) cached = false;
////                    }
////                    catch (Exception ex) {
////                        // Cannot parse the Last-Modified header or file is not in cache --> Don't use caching
////                        cached = false;
////                    }
////                }
////
////                // Load the template from cache or use the response body as new template
////                if (cached) {
////                    templateString = contentMap.get(url);
////                } else {
////                    templateString = method.getResponseBodyAsString();
////                    templateString = process(templateString);
////
////                    // Add template to cache
////                    contentMap.put(url, templateString);
////                    obtainedMap.put(url, new Date());
////                }
////
////                method.releaseConnection();
////
////                templateLayout.setTemplate(new StringTemplateSource(templateString));
////
////            }
////            catch (Exception ex) {
////                // Http get request failed or can't set template --> Invoke handleUnknownResourceRequested
////                ex.printStackTrace();
////                System.err.println(templateString);
////            }
////        }
//
////        private String process(String templateString) {
////            String path = getPath();
////
////            Source source = new Source(templateString);
////
////            grabTitle(source);
////            grabLinks(source);
////            grabScripts(source);
////
//////            Segment content = source.getElementById("body").getContent();
////
////            Element body = source.findNextElement(0, "body");
////            if (body == null) {
////                return templateString;
////            }
////            Segment content = body.getContent();
////            source = new Source(content.toString());
////
////            OutputDocument outputDocument = new OutputDocument(source);
////
////            List<StartTag> anchorTags = source.findAllStartTags(Tag.A);
////            for (StartTag anchorTag : anchorTags) {
////                Attributes attributes = anchorTag.getAttributes();
////                Attribute attribute = attributes.get("href");
////                if (attribute != null) {
////                    String value = attribute.getValue();
////
////                    String replacedValue = value.replaceFirst("http://" + WIKIHOST + TEMPLATEPATH, path);
////                    if (!replacedValue.equals(value)) {
////                        outputDocument.replace(attribute.getValueSegment(), replacedValue);
////                    }
////                }
////            }
////
////            List<StartTag> imgTags = source.findAllStartTags(Tag.IMG);
////            for (StartTag imgTag : imgTags) {
////                Attributes attributes = imgTag.getAttributes();
////                Attribute attribute = attributes.get("src");
////                if (attribute != null) {
////                    String value = attribute.getValue();
////
////                    if (!value.startsWith("http")) {
////                        value = "http://" + WIKIHOST + TEMPLATEPATH + "/" + value;
////                    }
////                    outputDocument.replace(attribute.getValueSegment(), value);
////                }
////            }
////
////            templateString = outputDocument.toString();
////            return templateString;
////        }
//
//        private void grabTitle(Source source) {
////            Element titleElement = source.findNextElement(0, "title");
////
////            if (titleElement != null) {
////                String title = titleElement.getTextExtractor().toString();
////                frame.setTitle(title);
////            }
//        }
//
//        private void grabLinks(Source source) {
////            List<Link> newLinks = new ArrayList<Link>();
////            List<StartTag> linkTags = source.findAllStartTags("link");
////            for (StartTag linkTag : linkTags) {
////                Attributes attributes = linkTag.getAttributes();
////                String rel = attributes.getValue("rel");
////                String rev = attributes.getValue("rev");
////                String type = attributes.getValue("type");
////                String target = attributes.getValue("target");
////                String href = attributes.getValue("href");
////                if (!href.startsWith("http")) {
////                    href = "http://" + WIKIHOST + TEMPLATEPATH + "/" + href;
////                }
////
////                newLinks.add(new Link(rel, rev, type, target, new Url(href)));
////            }
////            if (!newLinks.equals(links)) {
////                System.out.println("links    = " + links);
////                System.out.println("newLinks = " + newLinks);
////
////                for (Link link : links)
////                    frame.removeHeader(link);
////                for (Link link : newLinks)
////                    frame.addHeader(link);
////
////                links.clear();
////                links.addAll(newLinks);
////            }
//        }
//
//        private void grabScripts(Source source) {
////            List<Script> newScripts = new ArrayList<Script>();
////            List<StartTag> scriptTags = source.findAllStartTags("script");
////            for (StartTag scriptTag : scriptTags) {
////                Attributes attributes = scriptTag.getAttributes();
////                String type = attributes.getValue("type");
////                String src = attributes.getValue("src");
////                if (src != null)
////                    newScripts.add(new Script(type, new Url(src)));
////            }
////            if (!newScripts.equals(scripts)) {
////                System.out.println("scripts    = " + scripts);
////                System.out.println("newScripts = " + newScripts);
////
////                for (Script script : scripts)
////                    frame.removeHeader(script);
////                for (Script script : newScripts)
////                    frame.addHeader(script);
////
////                scripts.clear();
////                scripts.addAll(newScripts);
////            }
//        }
//
////        public String getPath() {
////            if (path == null) {
////                //path = SessionManager.getSession().getServletRequest().getContextPath() + SessionManager.getSession().getServletRequest().getServletPath();
////                HttpServletRequest request = SessionManager.getSession().getServletRequest();
////
////                String path = request.getRequestURL().toString();
////                String pathInfo = request.getPathInfo();
////                this.path = path.substring(0, path.lastIndexOf(pathInfo));
////            }
////
////            return path;
////        }
//
////        private class Url
////                implements URLResource {
////            private SimpleURL url;
////
////            public Url(String href) {
////                this.url = new SimpleURL(href);
////            }
////
////            public SimpleURL getURL() {
////                return url;
////            }
////        }
//    }
}
