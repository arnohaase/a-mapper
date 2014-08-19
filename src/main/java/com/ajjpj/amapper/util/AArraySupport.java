package com.ajjpj.amapper.util;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class helps dealing with arrays generically, providing a common API for primitive and non-primitive arrays
 *
 * @author arno
 */
public class AArraySupport {
    private static final Map<Class<?>, TypeHandler> primitiveHandlers = new HashMap<> ();

    static {
        primitiveHandlers.put (boolean.class, new TypeHandler () {
            @Override public void setArray (Object array, List<Object> values) {
                final boolean[] arr = (boolean[]) array;

                for (int i=0; i<arr.length; i++) {
                    arr[i] = (boolean) values.get(i);
                }
            }
        });
        primitiveHandlers.put (char.class, new TypeHandler () {
            @Override public void setArray (Object array, List<Object> values) {
                final char[] arr = (char[]) array;

                for (int i=0; i<arr.length; i++) {
                    arr[i] = (char) values.get(i);
                }
            }
        });

        primitiveHandlers.put (byte.class, new TypeHandler () {
            @Override public void setArray (Object array, List<Object> values) {
                final byte[] arr = (byte[]) array;

                for (int i=0; i<arr.length; i++) {
                    arr[i] = (byte) values.get(i);
                }
            }
        });
        primitiveHandlers.put (short.class, new TypeHandler () {
            @Override public void setArray (Object array, List<Object> values) {
                final short[] arr = (short[]) array;

                for (int i=0; i<arr.length; i++) {
                    arr[i] = (short) values.get(i);
                }
            }
        });
        primitiveHandlers.put (int.class, new TypeHandler () {
            @Override public void setArray (Object array, List<Object> values) {
                final int[] arr = (int[]) array;

                for (int i=0; i<arr.length; i++) {
                    arr[i] = (int) values.get(i);
                }
            }
        });
        primitiveHandlers.put (long.class, new TypeHandler () {
            @Override public void setArray (Object array, List<Object> values) {
                final long[] arr = (long[]) array;

                for (int i=0; i<arr.length; i++) {
                    arr[i] = (long) values.get(i);
                }
            }
        });

        primitiveHandlers.put (float.class, new TypeHandler () {
            @Override public void setArray (Object array, List<Object> values) {
                final float[] arr = (float[]) array;

                for (int i=0; i<arr.length; i++) {
                    arr[i] = (float) values.get(i);
                }
            }
        });
        primitiveHandlers.put (double.class, new TypeHandler () {
            @Override public void setArray (Object array, List<Object> values) {
                final double[] arr = (double[]) array;

                for (int i=0; i<arr.length; i++) {
                    arr[i] = (double) values.get(i);
                }
            }
        });
    }

    private static final TypeHandler defaultHandler = new TypeHandler () {
        @Override public void setArray (Object array, List<Object> values) {
            final int arrLength = Array.getLength (array);
            for (int i=0; i<arrLength; i++) {
                Array.set (array, i, values.get(i));
            }
        }
    };

    public static void setValues (Object array, List<Object> values) {
        final Class<?> componentType = array.getClass ().getComponentType ();
        if (componentType.isPrimitive ()) {
            primitiveHandlers.get (componentType).setArray (array, values);
        }
        else {
            defaultHandler.setArray (array, values);
        }
    }


    private interface TypeHandler {
        void setArray(Object array, List<Object> values);
    }
}

