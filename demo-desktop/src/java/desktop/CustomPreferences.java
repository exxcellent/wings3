/*
 * @(#)FileSystemPreferences.java	1.21 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package desktop;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wings.session.SessionManager;
import org.wings.session.Session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.prefs.*;


/**
 * Preferences implementation for Unix.  Preferences are stored in the file
 * system, with one directory per preferences node.  All of the preferences
 * at each node are stored in a single file.  Atomic file system operations
 * (e.g. File.renameTo) are used to ensure integrity.  An in-memory cache of
 * the "explored" portion of the tree is maintained for performance, and
 * written back to the disk periodically.  File-locking is used to ensure
 * reasonable behavior when multiple VMs are running at the same time.
 * (The file lock is obtained only for sync(), flush() and removeNode().)
 *
 * @author Josh Bloch
 * @version 1.21, 11/17/05
 * @see Preferences
 * @since 1.4
 */
public class CustomPreferences
    extends java.util.prefs.AbstractPreferences
{

    /**
     * Name for the cookies containing the users' state
     */
    private static String COOKIE_NAME = "PreferencesCookie";
    
    /**
     * Key for the next unused User ID in the System Preferences
     */
    private static String NEXT_FREE_USER_ID = "nextFreeUserID";
    
    
    /**
     * Returns logger for error messages. Backing store exceptions are logged at
     * WARNING level.
     */
    private static Logger getLogger() {
        return Logger.getLogger("java.util.prefs");
    }

    /**
     * Directory for system preferences.
     */
    private static File systemRootDir;

    /*
     * Flag, indicating whether systemRoot  directory is writable
     */
    private static boolean isSystemRootWritable;
    
    /**
     * the user roots
     */
    private static Map<String, CustomPreferences> userRoots = new HashMap<String, CustomPreferences>();
 
    static synchronized Preferences getUserRoot() {
        String userName = resolveUserName();
        
        if(userRoots.containsKey(userName))
            return userRoots.get(userName);
        
        CustomPreferences userRoot = new CustomPreferences(true);
        userRoots.put(userName, userRoot);
        return userRoot;
        
        
    }
    
    private static synchronized String resolveUserName(){
        String userName = "user/";

        Session session = SessionManager.getSession();
        HttpServletRequest request = session.getServletRequest();
        
        //if authorized user, use this name
        if(request.getUserPrincipal()!= null && request.getUserPrincipal().getName()!= null){
           userName = request.getUserPrincipal().getName();
           
        }else{
           boolean isAlreadyKnown = false;
            userName = (String)request.getSession().getAttribute(COOKIE_NAME);
            if (userName != null)
                isAlreadyKnown = true;
            else {
                Cookie[] cookies = request.getCookies();
                if(cookies != null){
                    for(int i=0; i< cookies.length; i++){
                        if(cookies[i].getName().equals(COOKIE_NAME)){
                            //pref = Preferences.userRoot().node(cookies[i].getValue());
                            userName = cookies[i].getValue();
                            request.getSession().setAttribute(COOKIE_NAME, userName);
                            isAlreadyKnown = true;
                            break;
                        }
                    }
                }
            }

            
            if(!isAlreadyKnown) {
                //pref = Preferences.userRoot().node(userID.toString());
                int userID = getSystemRoot().getInt(NEXT_FREE_USER_ID, 0);
                userName = ((Integer)userID).toString();
                
                //Set the cookie
                Cookie cookie= new Cookie(COOKIE_NAME, userName);
                cookie.setMaxAge(1000000000);
                org.wings.session.SessionManager.getSession().getServletResponse().addCookie(cookie);
                request.getSession().setAttribute(COOKIE_NAME, userName);

                userID++;
                getSystemRoot().putInt(NEXT_FREE_USER_ID,userID);
                
                try{
                    getSystemRoot().flush();
                    
                }catch(Exception ex){ex.printStackTrace();}
                
            }
            
        }
        
        return userName;
        
    }
    
    

    private static File setupUserRoot(String userName) {
         
        File userRootDir = new File("./Prefs/" + userName);
//                      new File(System.getProperty("java.util.prefs.userRoot",
//                      System.getProperty("user.home")), ".java/.userPrefs");
        // Attempt to create root dir if it does not yet exist.
        if (!userRootDir.exists()) {
            if (userRootDir.mkdirs()) {

                getLogger().info("Created user preferences directory.");
            }
            else
                getLogger().warning("Couldn't create user preferences" +
                    " directory. User preferences are unusable.");
        }
               
        String USER_NAME = System.getProperty("user.name");
        userLockFile = new File (userRootDir, ".user.lock." + USER_NAME);
        userRootModFile = new File (userRootDir, ".userRootModFile." + USER_NAME);
        if (!userRootModFile.exists())
            try {
                // create if does not exist.
                userRootModFile.createNewFile();
            } 
            catch (IOException e) {
            getLogger().warning(e.toString());}
        userRootModTime = userRootModFile.lastModified();
        return userRootDir;
    }


    /**
     * The system root.
     */
    static Preferences systemRoot;

    static synchronized Preferences getSystemRoot() {
        if (systemRoot == null) {
            setupSystemRoot();
            systemRoot = new CustomPreferences(false);
        }
        return systemRoot;
    }

    private static void setupSystemRoot() {
        String systemPrefsDirName = "./Prefs/system/";

        //(String)System.getProperty("java.util.prefs.systemRoot","/etc/.java");

        systemRootDir = new File(systemPrefsDirName);//, ".systemPrefs");
        // Attempt to create root dir if it does not yet exist.
        //if (!systemRootDir.exists()) {
        // system root does not exist in /etc/.java
        // Switching  to java.home
        //systemRootDir = new File(System.getProperty("java.home"),".systemPrefs");
        if (!systemRootDir.exists()) {
            if (systemRootDir.mkdirs()) {
                getLogger().info(
                    "Created system preferences directory "
                        + "in " + systemPrefsDirName);

            }
            else {
                getLogger().warning("Could not create "
                    + "system preferences directory. System "
                    + "preferences are unusable.");
            }

        }
        isSystemRootWritable = systemRootDir.canWrite();
        systemLockFile = new File(systemRootDir, ".system.lock");
        systemRootModFile =
            new File(systemRootDir, ".systemRootModFile");
        if (!systemRootModFile.exists() && isSystemRootWritable)
            try {
                // create if does not exist.
                systemRootModFile.createNewFile();

            }
            catch (IOException e) {
                getLogger().warning(e.toString());
            }
        systemRootModTime = systemRootModFile.lastModified();


    }


    /**
     * The lock file for the user tree.
     */
    static File userLockFile;


    /**
     * The lock file for the system tree.
     */
    static File systemLockFile;


    /**
     * The directory representing this preference node.  There is no guarantee
     * that this directory exits, as another VM can delete it at any time
     * that it (the other VM) holds the file-lock.  While the root node cannot
     * be deleted, it may not yet have been created, or the underlying
     * directory could have been deleted accidentally.
     */
    private final File dir;

    /**
     * The file representing this preference node's preferences.
     * The file format is undocumented, and subject to change
     * from release to release, but I'm sure that you can figure
     * it out if you try real hard.
     */
    private final File prefsFile;

    /**
     * A temporary file used for saving changes to preferences.  As part of
     * the sync operation, changes are first saved into this file, and then
     * atomically renamed to prefsFile.  This results in an atomic state
     * change from one valid set of preferences to another.  The
     * the file-lock is held for the duration of this transformation.
     */
    private final File tmpFile;

    /**
     * File, which keeps track of global modifications of userRoot.
     */
    private static File userRootModFile;

    /**
     * Flag, which indicated whether userRoot was modified by another VM
     */
    private static boolean isUserRootModified = false;

    /**
     * Keeps track of userRoot modification time. This time is reset to
     * zero after UNIX reboot, and is increased by 1 second each time
     * userRoot is modified.
     */
    private static long userRootModTime;


    /*
     * File, which keeps track of global modifications of systemRoot
     */
    private static File systemRootModFile;
    /*
     * Flag, which indicates whether systemRoot was modified by another VM
     */
    private static boolean isSystemRootModified = false;

    /**
     * Keeps track of systemRoot modification time. This time is reset to
     * zero after system reboot, and is increased by 1 second each time
     * systemRoot is modified.
     */
    private static long systemRootModTime;

    /**
     * Locally cached preferences for this node (includes uncommitted
     * changes).  This map is initialized with from disk when the first get or
     * put operation occurs on this node.  It is synchronized with the
     * corresponding disk file (prefsFile) by the sync operation.  The initial
     * value is read *without* acquiring the file-lock.
     */
    private Map<String, String> prefsCache = null;

    /**
     * The last modification time of the file backing this node at the time
     * that prefCache was last synchronized (or initially read).  This
     * value is set *before* reading the file, so it's conservative; the
     * actual timestamp could be (slightly) higher.  A value of zero indicates
     * that we were unable to initialize prefsCache from the disk, or
     * have not yet attempted to do so.  (If prefsCache is non-null, it
     * indicates the former; if it's null, the latter.)
     */
    private long lastSyncTime = 0;


    /**
     * A list of all uncommitted preference changes.  The elements in this
     * list are of type PrefChange.  If this node is concurrently modified on
     * disk by another VM, the two sets of changes are merged when this node
     * is sync'ed by overwriting our prefsCache with the preference map last
     * written out to disk (by the other VM), and then replaying this change
     * log against that map.  The resulting map is then written back
     * to the disk.
     */
    final List<Change> changeLog = new ArrayList<Change>();

    /**
     * Represents a change to a preference.
     */
    private abstract class Change
    {
        /**
         * Reapplies the change to prefsCache.
         */
        abstract void replay();
    }

    ;

    /**
     * Represents a preference put.
     */
    private class Put
        extends Change
    {
        String key, value;

        Put(String key, String value) {
            this.key = key;
            this.value = value;
        }

        void replay() {
            prefsCache.put(key, value);
        }
    }

    /**
     * Represents a preference remove.
     */
    private class Remove
        extends Change
    {
        String key;

        Remove(String key) {
            this.key = key;
        }

        void replay() {
            prefsCache.remove(key);
        }
    }

    /**
     * Represents the creation of this node.
     */
    private class NodeCreate
        extends Change
    {
        /**
         * Performs no action, but the presence of this object in changeLog
         * will force the node and its ancestors to be made permanent at the
         * next sync.
         */
        void replay() {
        }
    }

    /**
     * NodeCreate object for this node.
     */
    NodeCreate nodeCreate = null;

    /**
     * Replay changeLog against prefsCache.
     */
    private void replayChanges() {
        for (int i = 0, n = changeLog.size(); i < n; i++)
            ((Change)changeLog.get(i)).replay();
    }

    private final boolean isUserNode;

    /**
     * Special constructor for roots (both user and system).  This constructor
     * will only be called twice, by the static initializer.
     */
    private CustomPreferences(boolean user) {
        super(null, "");
        isUserNode = user;
                
        dir = (user ? setupUserRoot(resolveUserName()): systemRootDir);
        
        prefsFile = new File(dir, "prefs.xml");

        tmpFile = new File(dir, "prefs.tmp");

        if (newNode) {
            // These 2 things guarantee node will get wrtten at next flush/sync
            prefsCache = new TreeMap<String,String>();
            nodeCreate = new NodeCreate();
            changeLog.add(nodeCreate);
        }
    }

    /**
     * Construct a new FileSystemPreferences instance with the specified
     * parent node and name.  This constructor, called from childSpi,
     * is used to make every node except for the two //roots.
     */
    private CustomPreferences(CustomPreferences parent, String name) {
        super(parent, name);
        isUserNode = parent.isUserNode;
        dir = new File(parent.dir, name);
        prefsFile = new File(dir, "prefs.xml");
        tmpFile = new File(dir, "prefs.tmp");
        newNode = !dir.exists();
        //this.initCacheIfNecessary();


        if (newNode) {
            // These 2 things guarantee node will get wrtten at next flush/sync
            prefsCache = new TreeMap<String,String>();
            nodeCreate = new NodeCreate();
            changeLog.add(nodeCreate);
        }

    }

    public boolean isUserNode() {
        return isUserNode;
    }

    protected void putSpi(String key, String value) {
        initCacheIfNecessary();
        changeLog.add(new Put(key, value));
        prefsCache.put(key, value);
    }

    protected String getSpi(String key) {
        initCacheIfNecessary();
        return (String)prefsCache.get(key);
    }

    protected void removeSpi(String key) {
        initCacheIfNecessary();
        changeLog.add(new Remove(key));
        prefsCache.remove(key);
    }

    /**
     * Initialize prefsCache if it has yet to be initialized.  When this method
     * returns, prefsCache will be non-null.  If the data was successfully
     * read from the file, lastSyncTime will be updated.  If prefsCache was
     * null, but it was impossible to read the file (because it didn't
     * exist or for any other reason) prefsCache will be initialized to an
     * empty, modifiable Map, and lastSyncTime remain zero.
     */
    private void initCacheIfNecessary() {
        if (prefsCache != null)
            return;

        prefsCache = new HashMap<String, String>();
        try {
            loadCache();
        }
        catch (Exception e) {
            // assert lastSyncTime == 0;
            prefsCache = new TreeMap<String, String>();
        }
    }

    /**
     * Attempt to load prefsCache from the backing store.  If the attempt
     * succeeds, lastSyncTime will be updated (the new value will typically
     * correspond to the data loaded into the map, but it may be less,
     * if another VM is updating this node concurrently).  If the attempt
     * fails, a BackingStoreException is thrown and both prefsCache and
     * lastSyncTime are unaffected by the call.
     */
    private void loadCache() throws BackingStoreException {

        long newLastSyncTime = 0;

        try {

            newLastSyncTime = prefsFile.lastModified();

            DocumentBuilder docBuilder = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();
            org.w3c.dom.Document doc = null;

            doc = docBuilder.parse(prefsFile);

            NodeList entries = doc.getElementsByTagName("entry");

            for (int i = 0; i < entries.getLength(); i++) {
                Element entry = (Element)entries.item(i);


                String key = entry.getAttribute("key");


                String value = entry.getAttribute("value");

                prefsCache.put(key, value);
            }


        }
        catch (Exception e) {
            if (e instanceof InvalidPreferencesFormatException) {
                getLogger().warning("Invalid preferences format in "
                    + prefsFile.getPath());
                prefsFile.renameTo(new File(
                    prefsFile.getParentFile(),
                    "IncorrectFormatPrefs.xml"));

            }
            else if (e instanceof FileNotFoundException) {
                getLogger().warning("Prefs file removed in background "
                    + prefsFile.getPath());
            }
            else {
                throw new BackingStoreException(e);
            }
        }
        // Attempt succeeded; update state

        lastSyncTime = newLastSyncTime;


    }

    /**
     * Attempt to write back prefsCache to the backing store.  If the attempt
     * succeeds, lastSyncTime will be updated (the new value will correspond
     * exactly to the data thust written back, as we hold the file lock, which
     * prevents a concurrent write.  If the attempt fails, a
     * BackingStoreException is thrown and both the backing store (prefsFile)
     * and lastSyncTime will be unaffected by this call.  This call will
     * NEVER leave prefsFile in a corrupt state.
     */
    private void writeBackCache() throws BackingStoreException {
        OutputStream os;
        FileOutputStream fos;

        try {
            if (!dir.exists() && !dir.mkdirs())
                throw new BackingStoreException(dir +
                    " create failed.");
            fos = new FileOutputStream(prefsFile);
            os = new BufferedOutputStream(fos);
            //exportSubtree(os);

            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            DocumentBuilder build = f.newDocumentBuilder();


            org.w3c.dom.Document doc = build.newDocument();
            Element root = doc.createElement("PREFS");
            doc.appendChild(root);

            for (String key : prefsCache.keySet()) {
                Element e = doc.createElement("entry");
                e.setAttribute("key", key);
                e.setAttribute("value", prefsCache.get(key));
                root.appendChild(e);


            }


            Transformer trans = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(os);
            trans.transform(source, result);

            os.close();
            fos.close();

        }
        catch (Exception e) {
            if (e instanceof BackingStoreException)
                throw (BackingStoreException)e;
            throw new BackingStoreException(e);
        }


    }

    protected String[] keysSpi() {
        initCacheIfNecessary();
        return (String[])
            prefsCache.keySet().toArray(new String[prefsCache.size()]);
    }

    protected String[] childrenNamesSpi() {


        List<String> result = new ArrayList<String>();
        File[] dirContents = dir.listFiles();
        if (dirContents != null) {
            for (int i = 0; i < dirContents.length; i++)
                if (dirContents[i].isDirectory())
                    result.add(dirContents[i].getName());
        }
        return result.toArray(EMPTY_STRING_ARRAY);

    }

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    protected AbstractPreferences childSpi(String name) {
        return new CustomPreferences(this, name);
    }

    public void removeNode() throws BackingStoreException {
        synchronized (isUserNode() ? userLockFile : systemLockFile) {
            // to remove a node we need an exclusive lock
           super.removeNode();
        }
    }

    /**
     * Called with file lock held (in addition to node locks).
     */
    protected void removeNodeSpi() throws BackingStoreException {


        if (changeLog.contains(nodeCreate)) {
            changeLog.remove(nodeCreate);
            nodeCreate = null;
            return;// null;
        }
        if (!dir.exists())
            return;
        //                        return null;
        prefsFile.delete();
        tmpFile.delete();
        // dir should be empty now.  If it's not, empty it
        File[] junk = dir.listFiles();
        if (junk.length != 0) {
            getLogger().warning(
                "Found extraneous files when removing node: "
                    + Arrays.asList(junk));
            for (int i = 0; i < junk.length; i++) {
                junk[i].delete();
            }

        }
        if (!dir.delete())
            throw new BackingStoreException("Couldn't delete dir: "
                + dir);

    }

    public synchronized void sync() throws BackingStoreException {

        synchronized (isUserNode() ? userLockFile : systemLockFile) {
            final Long newModTime;


            if (isUserNode()) {
                newModTime = userRootModFile.lastModified();
                isUserRootModified = userRootModTime == newModTime;
            }
            else {
                newModTime = systemRootModFile.lastModified();
                isSystemRootModified = systemRootModTime == newModTime;
            }


            super.sync();

            if (isUserNode()) {
                userRootModTime = newModTime.longValue() + 1000;
                userRootModFile.setLastModified(userRootModTime);
            }
            else {
                systemRootModTime = newModTime.longValue() + 1000;
                systemRootModFile.setLastModified(systemRootModTime);
            }
        }
    }

    protected void syncSpi() throws BackingStoreException {

        syncSpiPrivileged();

    }
    private void syncSpiPrivileged() throws BackingStoreException {
        if (isRemoved())
            throw new IllegalStateException("Node has been removed");
        if (prefsCache == null)
            return;  // We've never been used, don't bother syncing
        long lastModifiedTime;
        if ((isUserNode() ? isUserRootModified : isSystemRootModified)) {
            lastModifiedTime = prefsFile.lastModified();
            if (lastModifiedTime != lastSyncTime) {
                // Prefs at this node were externally modified; read in node and
                // playback any local mods since last sync
                loadCache();
                replayChanges();
                lastSyncTime = lastModifiedTime;
            }
        }
        else if (lastSyncTime != 0 && !dir.exists()) {
            // This node was removed in the background.  Playback any changes
            // against a virgin (empty) Map.
            prefsCache = new TreeMap<String, String>();
            replayChanges();
        }
        if (!changeLog.isEmpty()) {
            writeBackCache();  // Creates directory & file if necessary
            /*
            * Attempt succeeded; it's barely possible that the call to
            * lastModified might fail (i.e., return 0), but this would not
            * be a disaster, as lastSyncTime is allowed to lag.
            */
            lastModifiedTime = prefsFile.lastModified();
            /* If lastSyncTime did not change, or went back
             * increment by 1 second. Since we hold the lock
             * lastSyncTime always monotonically encreases in the
             * atomic sense.
             */
            if (lastSyncTime <= lastModifiedTime) {
                lastSyncTime = lastModifiedTime + 1000;
                prefsFile.setLastModified(lastSyncTime);
            }
            changeLog.clear();
        }
    }

    public void flush() throws BackingStoreException {
        if (isRemoved())
            return;
        sync();
    }

    protected void flushSpi() throws BackingStoreException {
        // assert false;
    }


}
