package com.fuzzy.subsystem.extensions.scripts;

import com.fuzzy.subsystem.Server;
import com.fuzzy.subsystem.config.ConfigValue;
import com.fuzzy.subsystem.extensions.scripts.Compiler.MemoryClassLoader;
import com.fuzzy.subsystem.extensions.scripts.jarloader.JarClassLoader;
import com.fuzzy.subsystem.gameserver.GameServer;
import com.fuzzy.subsystem.gameserver.handler.AdminCommandHandler;
import com.fuzzy.subsystem.gameserver.instancemanager.QuestManager;
import com.fuzzy.subsystem.gameserver.model.quest.Quest;
import com.fuzzy.subsystem.util.GArray;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Scripts {
    private static final Logger _log = Logger.getLogger(Scripts.class.getName());

    public static boolean JAR;

    private static Scripts _instance;

    private static int _version = 0;

    public static boolean loading;

    private final HashMap<String, Script> _classes = new HashMap<>();
    public static final HashMap<Integer, GArray<ScriptClassAndMethod>> itemHandlers = new HashMap<>();
    public static final HashMap<Integer, GArray<ScriptClassAndMethod>> dialogAppends = new HashMap<>();
    public static final HashMap<String, ScriptClassAndMethod> onAction = new HashMap<>();
    public static final HashMap<String, ScriptClassAndMethod> onActionShift = new HashMap<>();
    public static final GArray<ScriptClassAndMethod> onPlayerExit = new GArray<>();
    public static final GArray<ScriptClassAndMethod> onPlayerEnter = new GArray<>();
    public static final GArray<ScriptClassAndMethod> onReloadMultiSell = new GArray<>();
    public static final GArray<ScriptClassAndMethod> onDie = new GArray<>();
    public static final GArray<ScriptClassAndMethod> onEscape = new GArray<>();

    public static final GArray<ScriptClassAndMethod> onPlayerTeleport = new GArray<>();

    public static Scripts getInstance() {
        if (_instance == null)
            new Scripts();
        return _instance;
    }

    public Scripts() {
        _instance = this;
        load(false);
    }

    public boolean reload() {
        loading = true;

        for (ScriptObject go : GameServer.scriptsObjects.values())
            try {
                go.invokeMethod("onReload");
            } catch (Exception f) {
                f.printStackTrace();
            }
        GameServer.scriptsObjects.clear();

        boolean error = load(true);
        callOnLoad();

        loading = false;
        return error;
    }

    public void shutdown() {
        for (ScriptObject go : GameServer.scriptsObjects.values())
            try {
                go.invokeMethod("onShutdown");
            } catch (Exception f) {
                f.printStackTrace();
            }
        GameServer.scriptsObjects.clear();
    }

    private synchronized boolean load(boolean reload) {
        _log.info("Scripts loading...");
        boolean error = false;
        Class<?> c;


        if (ConfigValue.develop) {
            JAR = new File("lib/tsrsvp.jar").exists();
        } else {
            JAR = new File("./lib/tsrsvp.jar").exists();
        }


        if (JAR) {
            GArray<File> scriptFiles = new GArray<File>();
            _log.info("Scripts start parse class...");

            if (ConfigValue.develop) {
                parseClasses(new File("data/scripts"), scriptFiles);
            } else {
                parseClasses(new File("./data/scripts"), scriptFiles);
            }

            _log.info("Scripts end parse class...");

            if (Compiler.getInstance().compile(scriptFiles, System.out)) {
                _log.info("Scripts end compile class...");
                MemoryClassLoader classLoader = Compiler.getInstance().classLoader; //TODO
                for (String name : Compiler.getInstance().classLoader.byteCodes.keySet()) {
                    if (name.contains("$"))
                        continue; // пропускаем вложенные классы
                    try {
                        c = classLoader.loadClass(name);
                        Script s = new Script(c);
                        _classes.put(name, s);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        _log.warning("Can't load script class:" + e.getMessage());
                        error = true;
                    }
                }
                Compiler.getInstance().classLoader = null;
            } else {
                _log.warning("Can't compile scripts!");
                error = true;
            }
            JarClassLoader jcl;
            try {
                if (ConfigValue.develop) {
                    jcl = new JarClassLoader("lib/tsrsvp.jar");
                } else {
                    jcl = new JarClassLoader("./lib/tsrsvp.jar");
                }


                for (String name : jcl.getClassNames()) {
                    if (!name.contains(".class"))
                        continue;
                    if (name.contains("$"))
                        continue; // пропускаем вложенные классы
                    name = name.replace(".class", "").replace("/", ".");
                    c = jcl.loadClass(name);
                    Script s = new Script(c);
                    _classes.put(c.getName(), s);
                }
            } catch (Exception e) {
                error = true;
                e.printStackTrace();
            }
        }

        if (error) {
            _log.info("Scripts loaded with errors. Loaded " + _classes.size() + " classes.");
            if (!reload)
                Server.halt(0, "Scripts loaded with errors. Loaded " + _classes.size() + " classes.");
        } else
            _log.info("Scripts successfully loaded. Loaded " + _classes.size() + " classes.");
        return error;
    }

    public void callOnLoad() {
        loadAndInitHandlers();
        AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminScripts());
    }

    private void loadAndInitHandlers() {
        itemHandlers.clear();
        dialogAppends.clear();
        onAction.clear();
        onActionShift.clear();
        onPlayerExit.clear();
        onPlayerEnter.clear();
        onPlayerTeleport.clear();
        onReloadMultiSell.clear();
        onDie.clear();
        onEscape.clear();

        for (Script _class : _classes.values())
            loadAndInitHandlersForClass(_class);
        //for(Integer id : itemHandlers.keySet())
        //	System.out.println(id);
		/*_log.info("Scripts loaded:->itemHandlers :"+itemHandlers.size());
		_log.info("Scripts loaded:->dialogAppends :"+dialogAppends.size());
		_log.info("Scripts loaded:->onAction :"+onAction.size());
		_log.info("Scripts loaded:->onActionShift :"+onActionShift.size());
		_log.info("Scripts loaded:->onPlayerExit :"+onPlayerExit.size());
		_log.info("Scripts loaded:->onPlayerEnter :"+onPlayerEnter.size());
		_log.info("Scripts loaded:->onReloadMultiSell :"+onReloadMultiSell.size());
		_log.info("Scripts loaded:->onDie :"+onDie.size());
		_log.info("Scripts loaded:->onEscape :"+onEscape.size());
		_log.info("Scripts loaded:->onPlayerTeleport :"+onPlayerTeleport.size());*/
    }

    private void loadAndInitHandlersForClass(Script _class) {
        try {
            if (!GameServer.scriptsObjects.containsKey(_class.getName())) {
                ScriptObject go = _class.newInstance();
                GameServer.scriptsObjects.put(_class.getName(), go);
                go.invokeMethod("onLoad");

                Integer revision = (Integer) go.invokeMethod("getRevision");
                if (revision != null) {
                    if (revision > _version)
                        _version = revision;
                } else
                    ;//System.out.println("----------------------------- Class name is null: " + _class.getName() + "-----------------------------");
            }

            for (Method method : _class.getRawClass().getMethods())
                if (method.getName().equals("onLoad")) {
                    if (Modifier.isStatic(method.getModifiers()))
                        method.invoke(null);
                } else if (method.getName().contains("ItemHandler_")) {
                    Integer id = Integer.parseInt(method.getName().substring(12));
                    GArray<ScriptClassAndMethod> handlers = itemHandlers.get(id);
                    if (handlers == null) {
                        handlers = new GArray<ScriptClassAndMethod>();
                        itemHandlers.put(id, handlers);
                    }
                    handlers.add(new ScriptClassAndMethod(_class, method));
                } else if (method.getName().contains("DialogAppend_")) {
                    Integer id = Integer.parseInt(method.getName().substring(13));
                    GArray<ScriptClassAndMethod> handlers = dialogAppends.get(id);
                    if (handlers == null) {
                        handlers = new GArray<ScriptClassAndMethod>();
                        dialogAppends.put(id, handlers);
                    }
                    handlers.add(new ScriptClassAndMethod(_class, method));
                } else if (method.getName().contains("OnAction_")) {
                    String name = method.getName().substring(9);
                    if (onAction.containsKey(name))
                        onAction.remove(name);
                    onAction.put(name, new ScriptClassAndMethod(_class, method));
                } else if (method.getName().contains("OnActionShift_")) {
                    String name = method.getName().substring(14);
                    if (onActionShift.containsKey(name))
                        onActionShift.remove(name);
                    onActionShift.put(name, new ScriptClassAndMethod(_class, method));
                } else if (method.getName().equals("OnPlayerExit"))
                    onPlayerExit.add(new ScriptClassAndMethod(_class, method));
                else if (method.getName().equals("OnPlayerEnter"))
                    onPlayerEnter.add(new ScriptClassAndMethod(_class, method));
                else if (method.getName().equals("OnReloadMultiSell"))
                    onReloadMultiSell.add(new ScriptClassAndMethod(_class, method));
                else if (method.getName().equals("OnDie"))
                    onDie.add(new ScriptClassAndMethod(_class, method));
                else if (method.getName().equals("OnEscape"))
                    onEscape.add(new ScriptClassAndMethod(_class, method));
                else if (method.getName().equals("onPlayerTeleport"))
                    onPlayerTeleport.add(new ScriptClassAndMethod(_class, method));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearHandlersForClass(Script _class) {
        try {
            for (GArray<ScriptClassAndMethod> entry : itemHandlers.values()) {
                GArray<ScriptClassAndMethod> toRemove = new GArray<ScriptClassAndMethod>();
                for (ScriptClassAndMethod sc : entry)
                    if (sc.scriptClass == _class)
                        toRemove.add(sc);
                for (ScriptClassAndMethod sc : toRemove)
                    entry.remove(sc);
            }

            for (GArray<ScriptClassAndMethod> entry : dialogAppends.values()) {
                GArray<ScriptClassAndMethod> toRemove = new GArray<ScriptClassAndMethod>();
                for (ScriptClassAndMethod sc : entry)
                    if (sc.scriptClass == _class)
                        toRemove.add(sc);
                for (ScriptClassAndMethod sc : toRemove)
                    entry.remove(sc);
            }

            GArray<String> toRemove = new GArray<String>();
            for (Map.Entry<String, ScriptClassAndMethod> entry : onAction.entrySet())
                if (entry.getValue().scriptClass == _class)
                    toRemove.add(entry.getKey());
            for (String key : toRemove)
                onAction.remove(key);

            toRemove = new GArray<String>();
            for (Map.Entry<String, ScriptClassAndMethod> entry : onActionShift.entrySet())
                if (entry.getValue().scriptClass == _class)
                    toRemove.add(entry.getKey());
            for (String key : toRemove)
                onActionShift.remove(key);

            GArray<ScriptClassAndMethod> toRemove2 = new GArray<ScriptClassAndMethod>();
            for (ScriptClassAndMethod sc : onPlayerExit)
                if (sc.scriptClass == _class)
                    toRemove2.add(sc);
            for (ScriptClassAndMethod sc : toRemove2)
                onPlayerExit.remove(sc);

            toRemove2 = new GArray<ScriptClassAndMethod>();
            for (ScriptClassAndMethod sc : onPlayerEnter)
                if (sc.scriptClass == _class)
                    toRemove2.add(sc);
            for (ScriptClassAndMethod sc : toRemove2)
                onPlayerEnter.remove(sc);

            toRemove2 = new GArray<ScriptClassAndMethod>();
            for (ScriptClassAndMethod sc : onPlayerTeleport)
                if (sc.scriptClass == _class)
                    toRemove2.add(sc);
            for (ScriptClassAndMethod sc : toRemove2)
                onPlayerTeleport.remove(sc);

            toRemove2 = new GArray<ScriptClassAndMethod>();
            for (ScriptClassAndMethod sc : onReloadMultiSell)
                if (sc.scriptClass == _class)
                    toRemove2.add(sc);
            for (ScriptClassAndMethod sc : toRemove2)
                onReloadMultiSell.remove(sc);

            toRemove2 = new GArray<ScriptClassAndMethod>();
            for (ScriptClassAndMethod sc : onDie)
                if (sc.scriptClass == _class)
                    toRemove2.add(sc);
            for (ScriptClassAndMethod sc : toRemove2)
                onDie.remove(sc);

            toRemove2 = new GArray<ScriptClassAndMethod>();
            for (ScriptClassAndMethod sc : onEscape)
                if (sc.scriptClass == _class)
                    toRemove2.add(sc);
            for (ScriptClassAndMethod sc : toRemove2)
                onEscape.remove(sc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean reloadClass(String name) {
        JarClassLoader jcl;
        try {
            if (ConfigValue.develop) {
                jcl = new JarClassLoader("lib/tsrsvp.jar");
            } else {
                jcl = new JarClassLoader("./lib/tsrsvp.jar");
            }


            Class<?> c;
            for (String names : jcl.getClassNames()) {
                if (!names.contains(".class"))
                    continue;
                if (names.contains("$"))
                    continue; // пропускаем вложенные классы
                names = names.replace(".class", "").replace("/", ".");
                c = jcl.loadClass(names);
                Script s = new Script(c);
                ScriptObject oldSo = GameServer.scriptsObjects.remove(names);
                if (oldSo != null)
                    oldSo.invokeMethod("onReload");
                Script oldS = _classes.remove(names);
                if (oldS != null)
                    clearHandlersForClass(oldS);
                _classes.put(c.getName(), s);
                loadAndInitHandlersForClass(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        File f;
        if (ConfigValue.develop) {
            f = new File("data/scripts/" + name.replace(".", "/") + ".java");
        } else {
            f = new File("./data/scripts/" + name.replace(".", "/") + ".java");
        }


        if (f.exists() && f.isFile())
            return reloadClassByName(name.replace("/", "."));

        f = new File("./data/scripts/" + name.replace(".", "/"));

        if (ConfigValue.develop) {
            f = new File("data/scripts/" + name.replace(".", "/"));
        } else {
            f = new File("./data/scripts/" + name.replace(".", "/"));
        }


        if (f.exists() && f.isDirectory())
            return reloadClassByPath(f);

        _log.warning("Can't find class or package by path: " + name);
        return true;
    }

    private boolean reloadClassByPath(File f) {
        GArray<File> scriptFiles = new GArray<File>();
        parseClasses(f, scriptFiles);
        if (Compiler.getInstance().compile(scriptFiles, System.out)) {
//			ClassLoader classLoader = Compiler.class.getClassLoader(); // Compiler.getInstance().classLoader;
            MemoryClassLoader classLoader = Compiler.getInstance().classLoader; //TODO
            Class<?> c;
            for (String name : Compiler.getInstance().classLoader.byteCodes.keySet()) {
                if (name.contains("$"))
                    continue; // пропускаем вложенные классы
                try {
                    c = classLoader.loadClass(name);
                    Script s = new Script(c);
                    ScriptObject oldSo = GameServer.scriptsObjects.remove(name);
                    if (oldSo != null)
                        oldSo.invokeMethod("onReload");
                    Script oldS = _classes.remove(name);
                    if (oldS != null)
                        clearHandlersForClass(oldS);
                    _classes.put(name, s);
                    loadAndInitHandlersForClass(s);
                } catch (ClassNotFoundException e) {
                    _log.warning("Can't load script class:" + e.getMessage());
                    return true;
                }
            }
            Compiler.getInstance().classLoader = null;
        } else {
            _log.warning("Can't recompile scripts: " + f.getPath());
            return true;
        }
        return false;
    }

    private boolean reloadClassByName(String name) {
        String path = "./data/scripts/";
        if (ConfigValue.develop) {
            path = "data/scripts/";
        }

        if (Compiler.getInstance().compile(new File(path + name.replace(".", "/") + ".java"), System.out)) {
            MemoryClassLoader classLoader = Compiler.getInstance().classLoader;
            try {
                Class<?> c = classLoader.loadClass(name);
                Script s = new Script(c);
                ScriptObject oldSo = GameServer.scriptsObjects.remove(name);
                if (oldSo != null)
                    oldSo.invokeMethod("onReload");
                Script oldS = _classes.remove(name);
                if (oldS != null)
                    clearHandlersForClass(oldS);
                _classes.put(name, s);
                loadAndInitHandlersForClass(s);
                return false;
            } catch (ClassNotFoundException e) {
                _log.warning("Can't load script class:" + e.getMessage());
            }
            Compiler.getInstance().classLoader = null;
        } else
            _log.warning("Can't recompile script: " + name);
        return true;
    }

    public boolean reloadQuest(String name) {
        String s_path = "./data/scripts/";
        if (ConfigValue.develop) {
            s_path = "data/scripts/";
        }

        if (ConfigValue.StartWhisoutQuest)
            return true;
        Quest q = QuestManager.getQuest(name);
        File f;
        if (q != null) {
            String path = q.getClass().getPackage().getName().replace(".", "/");
            f = new File(s_path + path + "/");
            if (f.isDirectory())
                return reloadClassByPath(f);
        }
        q = QuestManager.getQuest(Integer.parseInt(name));
        if (q != null) {
            String path = q.getClass().getPackage().getName().replace(".", "/");
            f = new File(s_path + path + "/");
            if (f.isDirectory())
                return reloadClassByPath(f);
        }
        return reloadClassByPath(new File(s_path + "quests/" + name + "/"));
    }

    private void parseClasses(File f, GArray<File> list) {
        for (File z : f.listFiles())
            if (z.isDirectory()) {
                if (z.getName().equals(".svn"))
                    continue;
                if (ConfigValue.StartWhisoutQuest && z.getName().equals("quests") && z.getParentFile().getName().equals("scripts"))
                    continue;
                parseClasses(z, list);
            } else {
                if (!z.getName().contains(".java"))
                    continue;
                list.add(z);
            }
    }

    public HashMap<String, Script> getClasses() {
        return _classes;
    }

    public static class ScriptClassAndMethod {
        public final Script scriptClass;
        public final Method method;

        public ScriptClassAndMethod(Script _scriptClass, Method _method) {
            scriptClass = _scriptClass;
            method = _method;
        }
    }

    public static int getRevision() {
        return _version;
    }
}