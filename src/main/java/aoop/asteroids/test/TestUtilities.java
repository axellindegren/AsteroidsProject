package aoop.asteroids.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

abstract class TestUtilities {
    static <ReturnType> ReturnType callMethod(Object instance, String methodName, Object... params)
            throws NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

        Method method;
        ReturnType result = null;

        Object[] parameters = new Object[params.length];
        Class[] classArray = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            parameters[i] = params[i];
            Class c = params[i].getClass();
            if ((classArray[i] = getPrimitive(c)) == null) classArray[i] = c;
        }

        method = instance.getClass().getDeclaredMethod(methodName, classArray);
        method.setAccessible(true);
        try {
            result = (ReturnType) method.invoke(instance, parameters);
        } catch (IllegalAccessException e) {
            // Can never happen
        }

        return result;
    }

    private static Class<?> getPrimitive(Class<?> c) {
        if (c.isPrimitive()) return c;
        if (c.equals(Byte.class)) return byte.class;
        if (c.equals(Short.class)) return short.class;
        if (c.equals(Integer.class)) return int.class;
        if (c.equals(Long.class)) return long.class;
        if (c.equals(Double.class)) return double.class;
        if (c.equals(Float.class)) return float.class;
        if (c.equals(Boolean.class)) return boolean.class;
        if (c.equals(Character.class)) return char.class;
        return null;
    }
}
