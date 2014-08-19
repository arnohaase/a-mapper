package com.ajjpj.amapper.util;

import java.lang.reflect.Array;
import java.util.*;


/**
 * This class helps dealing with arrays generically, providing a common API for primitive and non-primitive arrays
 *
 * @author arno
 */
public class AArraySupport {
    private static final Map<Class<?>, TypeHandler> primitiveHandlers = new HashMap<> ();

    static {
        primitiveHandlers.put (boolean.class, new TypeHandler () {
            @Override public Collection wrap (Object array) {
                return new WrappedArray (array) {
                    @Override Object getValue (Object array, int idx) {
                        return Array.getBoolean (array, idx);
                    }
                };
            }
            @Override public void setArray (Object array, List<Object> values) {
                final boolean[] arr = (boolean[]) array;

                for (int i=0; i<arr.length; i++) {
                    arr[i] = (boolean) values.get(i);
                }
            }
        });
        primitiveHandlers.put (char.class, new TypeHandler () {
            @Override public Collection wrap (Object array) {
                return new WrappedArray (array) {
                    @Override Object getValue (Object array, int idx) {
                        return Array.getChar (array, idx);
                    }
                };
            }
            @Override public void setArray (Object array, List<Object> values) {
                final char[] arr = (char[]) array;

                for (int i=0; i<arr.length; i++) {
                    arr[i] = (char) values.get(i);
                }
            }
        });

        primitiveHandlers.put (byte.class, new TypeHandler () {
            @Override public Collection wrap (Object array) {
                return new WrappedArray (array) {
                    @Override Object getValue (Object array, int idx) {
                        return Array.getByte (array, idx);
                    }
                };
            }
            @Override public void setArray (Object array, List<Object> values) {
                final byte[] arr = (byte[]) array;

                for (int i=0; i<arr.length; i++) {
                    arr[i] = (byte) values.get(i);
                }
            }
        });
        primitiveHandlers.put (short.class, new TypeHandler () {
            @Override public Collection wrap (Object array) {
                return new WrappedArray (array) {
                    @Override Object getValue (Object array, int idx) {
                        return Array.getShort (array, idx);
                    }
                };
            }
            @Override public void setArray (Object array, List<Object> values) {
                final short[] arr = (short[]) array;

                for (int i=0; i<arr.length; i++) {
                    arr[i] = (short) values.get(i);
                }
            }
        });
        primitiveHandlers.put (int.class, new TypeHandler () {
            @Override public Collection wrap (Object array) {
                return new WrappedArray (array) {
                    @Override Object getValue (Object array, int idx) {
                        return Array.getInt (array, idx);
                    }
                };
            }
            @Override public void setArray (Object array, List<Object> values) {
                final int[] arr = (int[]) array;

                for (int i=0; i<arr.length; i++) {
                    arr[i] = (int) values.get(i);
                }
            }
        });
        primitiveHandlers.put (long.class, new TypeHandler () {
            @Override public Collection wrap (Object array) {
                return new WrappedArray (array) {
                    @Override Object getValue (Object array, int idx) {
                        return Array.getLong (array, idx);
                    }
                };
            }
            @Override public void setArray (Object array, List<Object> values) {
                final long[] arr = (long[]) array;

                for (int i=0; i<arr.length; i++) {
                    arr[i] = (long) values.get(i);
                }
            }
        });

        primitiveHandlers.put (float.class, new TypeHandler () {
            @Override public Collection wrap (Object array) {
                return new WrappedArray (array) {
                    @Override Object getValue (Object array, int idx) {
                        return Array.getFloat (array, idx);
                    }
                };
            }
            @Override public void setArray (Object array, List<Object> values) {
                final float[] arr = (float[]) array;

                for (int i=0; i<arr.length; i++) {
                    arr[i] = (float) values.get(i);
                }
            }
        });
        primitiveHandlers.put (double.class, new TypeHandler () {
            @Override public Collection wrap (Object array) {
                return new WrappedArray (array) {
                    @Override Object getValue (Object array, int idx) {
                        return Array.getDouble (array, idx);
                    }
                };
            }
            @Override public void setArray (Object array, List<Object> values) {
                final double[] arr = (double[]) array;

                for (int i=0; i<arr.length; i++) {
                    arr[i] = (double) values.get(i);
                }
            }
        });
    }

    private static final TypeHandler defaultHandler = new TypeHandler () {
        @Override public Collection wrap (Object array) {
            return Arrays.asList ((Object[]) array);
        }
        @Override public void setArray (Object array, List<Object> values) {
            final int arrLength = Array.getLength (array);
            for (int i=0; i<arrLength; i++) {
                Array.set (array, i, values.get(i));
            }
        }
    };

    public static Collection wrap (Object array) {
        final Class<?> componentType = array.getClass ().getComponentType ();
        if (componentType.isPrimitive ()) {
            return primitiveHandlers.get (componentType).wrap (array);
        }
        else {
            return defaultHandler.wrap (array);
        }
    }

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
        Collection wrap (Object array);
    }

    private static abstract class WrappedArray extends AbstractCollection {
        private final Object array;
        private final int length;

        protected WrappedArray (Object array) {
            this.array = array;
            this.length = Array.getLength (array);
        }

        @SuppressWarnings ("NullableProblems")
        @Override public Iterator iterator () {
            return new Iterator () {
                int nextIdx = 0;

                @Override public boolean hasNext () {
                    return nextIdx < size ();
                }
                @Override public Object next () {
                    return getValue (array, nextIdx++);
                }
                @Override public void remove () {
                    throw new UnsupportedOperationException ();
                }
            };
        }

        abstract Object getValue (Object array, int idx);

        @Override public int size () {
            return length;
        }
    }
}

