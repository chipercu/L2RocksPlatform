package com.fuzzy.subsystem.extensions.scripts.jarloader;

/**
 * * JarClassLoader provides a minimalistic ClassLoader which shows how to
 * * instantiate a class which resides in a .jar file.
 * <br><br>
 * *
 * * @author	John D. Mitchell, Non, Inc., Mar  3, 1999
 * *
 * * @version 0.5
 * *
 */

public class JarClassLoader extends MultiClassLoader {
    private JarResources jarResources;

    public JarClassLoader(String jarName) {
        jarResources = new JarResources(jarName);
    }

    @Override
    protected byte[] loadClassBytes(String className) {
        className = formatClassName(className);
        return jarResources.getResource(className);
    }

    public String[] getClassNames() {
        return jarResources.getResources().toArray(new String[]{});
    }
}