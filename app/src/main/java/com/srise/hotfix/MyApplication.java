package com.srise.hotfix;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.PathClassLoader;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "patch.dex";

        ClassLoader baseClassLoader = getClassLoader();
        PathClassLoader patchClassLoader = new PathClassLoader(path, baseClassLoader);

        try {
            Object baseElements = getElements(baseClassLoader);
            Object patchElements = getElements(patchClassLoader);
            Object finalElements = combineElements(baseElements, patchElements);
            setFinalElements(baseClassLoader, finalElements);
        } catch (Exception e) {
            Log.e("shixi", e.getMessage(), e);
        }
    }

    /**
     * 获取dex的elements对象
     *
     * @param classLoader
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private Object getElements(ClassLoader classLoader) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> BaseLoaderClazz = Class.forName("dalvik.system.BaseDexClassLoader");
        Field pathList = BaseLoaderClazz.getDeclaredField("pathList");
        pathList.setAccessible(true);
        Object pathListReal = pathList.get(classLoader);
        Field dexElements = pathListReal.getClass().getDeclaredField("dexElements");
        dexElements.setAccessible(true);
        return dexElements.get(pathListReal);
    }

    private Object combineElements(Object baseElements, Object patchElements) {
        Class<?> componentType = baseElements.getClass().getComponentType();
        int baseElementsLength = Array.getLength(baseElements);
        int patchElementsLength = Array.getLength(patchElements);
        Object finalElements = Array.newInstance(componentType, baseElementsLength + patchElementsLength);
        System.arraycopy(patchElements, 0, finalElements, 0, patchElementsLength);
        System.arraycopy(baseElements, 0, finalElements, patchElementsLength, baseElementsLength);
        return finalElements;
    }

    private void setFinalElements(ClassLoader classLoader, Object finalElements) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> BaseLoaderClazz = Class.forName("dalvik.system.BaseDexClassLoader");
        Field pathList = BaseLoaderClazz.getDeclaredField("pathList");
        pathList.setAccessible(true);
        Object pathListReal = pathList.get(classLoader);
        Field dexElements = pathListReal.getClass().getDeclaredField("dexElements");
        dexElements.setAccessible(true);
        dexElements.set(pathListReal, finalElements);
    }
}



