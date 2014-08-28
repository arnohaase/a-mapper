package com.ajjpj.amapper.collection;

import com.ajjpj.abase.collection.immutable.AOption;
import com.ajjpj.abase.function.AFunction2NoThrow;
import com.ajjpj.abase.function.APredicate2NoThrow;

import java.util.*;

/**
 * Map collection A into existing List B by editing B with minimal costs.
 * In other words:
 * modify list B, with minimal cost, to be a mapped equivalent of list A.
 *
 * Available operations (on B):
 *   - remove an element
 *   - insert an element
 *   - replace an element
 *
 * Work is done using the optimal Levenshtein edit distance.
 *
 * Algorithm optimization: If more than one one good edit path exists, only one of them is tracked, because one optimal way is enough here.
 *
 * @author Roman
 *
 * @param <S> type of objects in source list
 * @param <T> type of objects in target list
 */
public class LevenshteinDistance <S, T> {

    private final Collection<S> source;
    private final List<T> target;
    private final APredicate2NoThrow<S, T> equalsPredicate;

    private List <List <MElement>> m = null;

    public enum EditChoice {
        noOp, // (diagonal)
        replace, // (diagonal)
        delete, // (up)
        insert // (left)
    }

    class MElement {
        boolean eq;
        int cost;
        EditChoice choice;

        MElement (boolean eq, int cost, EditChoice choice) {
            this.eq = eq;
            this.cost = cost;
            this.choice = choice;
        }
    }

    /**
     * @param source source collection (not null)
     * @param target target collection (not null) - implementing the List interface (in fact a List is expected, with has random write access implemented - e.g. ArrayList)
     * @param equalsPredicate equality function returning true, if object from source list is equivalent to object from target list
     */
    public LevenshteinDistance (Collection<S> source, List<T> target,
                                APredicate2NoThrow<S, T> equalsPredicate) {
        this.source = source;
        this.target = target;
        this.equalsPredicate = equalsPredicate;
    }

    /**
     * transform target list into a representation of source list
     * @param mapFunction mapping function from object in source list A to object in target list B.
     *                    1. param: source object
     *                    2. param: eventual existing target object
     *                    return: mapped source - if successful, AOption.none otherwise
     */
    public void editTarget (AFunction2NoThrow<S, T, AOption<T>> mapFunction) {
        edit (getEditPath(), mapFunction);
    }

    private void calcEditDistanceMatrix() {
        m = new ArrayList<> (source.size()+1);
        m.add (new ArrayList<MElement> (target.size()+1));
        m.get (0).add (new MElement (false, 0, EditChoice.noOp));
        for (int j=1; j <= target.size(); j++) {
            m.get (0).add (new MElement(false, j, EditChoice.delete));
        }
        int i=1;
        for (S sElem: source) {
            m.add (new ArrayList<MElement> (target.size()+1));
            m.get (i).add (new MElement (false, i, EditChoice.insert));
            int j=1;
            for (T tElem: target) {
                m.get (i).add (levMin (equalsPredicate.apply (sElem, tElem), i, j));
                j++;
            }
            i++;
        }
    }

    private MElement levMin (boolean eq, int i, int j) {
        MElement result = null;

        if (i>0 && j>0) {
            result = new MElement (
                    eq,
                    m.get (i - 1).get (j-1).cost + (eq ? 0 : 1),
                    eq ? EditChoice.noOp : EditChoice.replace
            );
        }

        if (j>0) {
            int delCost = m.get (i).get (j-1).cost + 1;
            if (result==null || delCost < result.cost) {
                result = new MElement (eq, delCost, EditChoice.delete);
            }
        }

        if (i>0) {
            int insCost = m.get (i-1).get (j).cost + 1;
            if (result==null || insCost < result.cost) {
                result = new MElement (eq, insCost, EditChoice.insert);
            }
        }
        return result;
    }


    /**
     * @return one possible edit-path of minimal distance
     */
    public List<EditChoice> getEditPath() {
        if (m==null) {
            calcEditDistanceMatrix();
        }
        List<EditChoice> result = new ArrayList<>();
        int i = source.size();
        int j = target.size();
        while (i > 0 || j > 0) {
            final EditChoice c = m.get (i).get (j).choice;
            result.add (0, c);
            switch (c) {
                case noOp:
                    i--;
                    j--;
                    break;
                case replace:
                    i--;
                    j--;
                    break;
                case delete:
                    j--;
                    break;
                case insert:
                    i--;
                    break;
            }
        }
        return result;
    }

    private void edit (List<EditChoice> editPath, AFunction2NoThrow<S, T, AOption<T>> mapFunction) {
        Iterator<S> sIter = source.iterator();
        int j=0;
        for (EditChoice c: editPath) {
            switch (c) {
                case replace: // fall through is intended
                case noOp: {
                    final AOption<T> mapResult = mapFunction.apply (sIter.next(), target.get (j));
                    if (mapResult.isDefined ()) {
                        target.set (j, mapResult.get());
                        j++;
                    } else {
                        target.remove (j);
                    }
                    break;
                }
                case delete: {
                    target.remove (j);
                    break;
                }
                case insert: {
                    final AOption<T> mapResult = mapFunction.apply (sIter.next(), null);
                    if (mapResult.isDefined ()) {
                        target.add (j, mapResult.get());
                        j++;
                    }
                    break;
                }
            }
        }
    }
}
