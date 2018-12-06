package com.gin.ngemart.baseui.component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by manbaul on 6/7/2017.
 */

public class NgemartChainComparator<T> implements Comparator<T> {

    private List<Comparator<T>> listComparators;

    @SafeVarargs
    public NgemartChainComparator(Comparator<T>... comparators) {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(T t, T t1) {
        for (Comparator<T> comparator : listComparators) {
            int result = comparator.compare(t, t1);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

}
