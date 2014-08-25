package com.ajjpj.amapper.core.path;


/**
 * The index is the index (e.g. in a collection) to which this segment refers. Depending on the semantics of the collection
 *  and its mapping, this may or may not be meaningful information.
 *
 * @author arno
 */
public class AParameterizedPathSegment extends APathSegment {
    private final int index;
    private final Object key;

    protected AParameterizedPathSegment(String name, int index, Object key) {
        super(name);
        this.index = index;
        this.key = key;
    }

    public int getIndex() {
        return index;
    }

    public Object getKey() {
        return key;
    }

    @Override public String toString () {
        if (index == -1) {
            return getName () + "[" + key + "]";
        }
        return getName () + "[" + key + "@" + index + "]";
    }

    @Override
    public boolean equals (Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass () != o.getClass () ) return false;
        if ( !super.equals (o) ) return false;

        AParameterizedPathSegment that = (AParameterizedPathSegment) o;

        if ( index != that.index ) return false;
        if ( key != null ? !key.equals (that.key) : that.key != null ) return false;

        return true;
    }

    @Override
    public int hashCode () {
        int result = super.hashCode ();
        result = 31 * result + index;
        result = 31 * result + (key != null ? key.hashCode () : 0);
        return result;
    }
}
