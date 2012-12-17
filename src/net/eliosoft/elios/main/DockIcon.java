/*
 * @(#)DockIcon.java
 *
 * $Date$
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood.
 * You may not use, copy or modify this software, except in
 * accordance with the license agreement you entered into with
 * Jeremy Wood. For details see accompanying license terms.
 *
 * This software is probably, but not necessarily, discussed here:
 * http://javagraphics.java.net/
 *
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
/*
 * Source found at
 * http://java.net/projects/javagraphics/sources/svn/content/trunk/src/com/bric/mac/DockIcon.java?rev=1654
 * Released under BSD http://java.net/projects/javagraphics
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.eliosoft.elios.main;

import java.awt.Image;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * This uses reflection to set the dock icon. If you're using Mac 10.5+, this
 * uses the new methods mentioned here:
 * http://developer.apple.com/releasenotes/Java
 * /JavaLeopardUpdate2RN/ResolvedIssues/chapter_3_section_13.html
 *
 * <P>
 * Otherwise this tries to use the older Cocoa classes (which are ideal for Mac
 * 10.4-ish).
 *
 * <P>
 * Note that the "correct" way to set the dock icon changed once, and it may
 * change again, so I would be reluctant to use this feature as a vital element
 * in my program. (That is: it may fail in coming years.)
 *
 * <P>
 * The Cocoa classes are deprecated, but if you have users running 10.4 it's
 * probably the best way to go. You must add "System/Library/Java" to your
 * classpath (either in Eclipse, your app's Info.plist, etc.) for the Cocoa
 * classes to work.
 *
 * @see <a
 *      href="http://javagraphics.blogspot.com/2007/06/dock-icon-setting-dock-icon.html">Dock
 *      Icon: Setting the Dock Icon</a>
 */
@SuppressWarnings("unused")
public class DockIcon {
    private static boolean isMac = (System.getProperty("os.name").toLowerCase()
            .indexOf("mac") != -1);

    /** These relate to the Mac OS 10.5+ model: */
    private static Object theApplication;
    private static Method setDockIconImage;
    private static Object defaultImage;

    /** These relate to the pre Mac OS 10.5 approach: */
    private static Constructor<?> NSImageURLConstructor;
    private static Object theNSApplication;
    private static Object defaultNSImage;
    private static Method setApplicationIconImageMethod;

    private static boolean initialized = false;
    private static boolean working = false;
    private static boolean debugging = false;

    private static void init() {
        if (initialized) {
            return;
        }
        try {
            if (isMac == false) {
                return;
            }

            Throwable error1 = null;
            try {
                Class<?> appClass = Class.forName("com.apple.eawt.Application");
                theApplication = appClass.getMethod("getApplication",
                        new Class[] {}).invoke(null, new Object[] {});
                setDockIconImage = appClass.getMethod("setDockIconImage",
                        new Class[] { Image.class });

                Method getMethod = appClass.getMethod("getDockIconImage",
                        new Class[] {});
                defaultImage = getMethod
                        .invoke(theApplication, new Object[] {});

                working = true;
                if (debugging) {
                    System.out.println("Using Application.setDockImage()");
                }
                return;
            } catch (Throwable t) {
                error1 = t;
            }
            try {
                Class<?> NSImageClass = Class
                        .forName("com.apple.cocoa.application.NSImage");
                NSImageURLConstructor = NSImageClass
                        .getConstructor(new Class[] { URL.class });
                Class<?> NSApplicationClass = Class
                        .forName("com.apple.cocoa.application.NSApplication");
                Method sharedMethod = NSApplicationClass.getMethod(
                        "sharedApplication", new Class[0]);
                theNSApplication = sharedMethod.invoke(null, new Object[0]);
                setApplicationIconImageMethod = NSApplicationClass
                        .getMethod("setApplicationIconImage",
                                new Class[] { NSImageClass });

                Method currentAppImage = NSApplicationClass.getMethod(
                        "applicationIconImage", new Class[0]);
                defaultNSImage = currentAppImage.invoke(theNSApplication,
                        (Object[]) null);

                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        set(null);
                    }
                });
                working = true;
                if (debugging) {
                    System.out
                            .println("Using NSApplication.setApplicationIconImage()");
                }
            } catch (Throwable t) {
                // do nothing
            }

            if (!working) {
                handleError(error1);
            }
        } finally {
            initialized = true;
        }
    }

    /**
     * @return <code>true</code> if this class can modify the dock icon.
     */
    public static boolean isActive() {
        init();
        return working;
    }

    private static void handleError(Throwable t) {
        // you may want to deal with this differently?
        if (debugging) {
            t.printStackTrace();
        }
    }

    /**
     * This tries to reassign the icon of this application in the dock.
     *
     * @param icons
     *            the image to set the dock icon to. If this is null, then the
     *            default icon is restored.
     * @return <code>true</code> if it appears this call worked.
     */
    public static synchronized boolean set(Image icons) {
        init();
        if (working == false) {
            return false;
        }

        if (setDockIconImage != null && theApplication != null) {
            Image image = icons;
            if (icons == null) {
                image = (Image) defaultImage;
            }
            try {
                setDockIconImage.invoke(theApplication, new Object[] { image });
                return true;
            } catch (Throwable t) {
                handleError(t);
            }
        }
        return false;
    }
}
