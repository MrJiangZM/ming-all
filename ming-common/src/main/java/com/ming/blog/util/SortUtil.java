package com.ming.blog.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author MrJiangZM
 * @since <pre>2021/8/6</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SortUtil {

    /**
     * sort list for some field`s specific sort
     *
     * @param clazz           value class
     * @param fieldName       field name
     * @param dataList        value list
     * @param specifySortList specific sort list
     * @param <E>             value generic
     *
     * @return sorted list
     */
    public static <E> List<E> specifySort(Class<E> clazz, String fieldName,
                                          List<E> dataList, List<String> specifySortList) {
        int size = dataList.size();
        Field field = ReflectionUtils.findField(clazz, fieldName);
        if (field == null || CollectionUtils.isEmpty(dataList) || CollectionUtils.isEmpty(specifySortList)) {
            return dataList;
        }
        ReflectionUtils.makeAccessible(field);
        return dataList.stream()
                .sorted(Comparator.comparing(
                        e -> {
                            Object obj = ReflectionUtils.getField(field, e);
                            return obj != null ? obj.toString() : null;
                        },
                        Comparator.nullsLast((str1, str2) -> {
                            int i1 = specifySortList.indexOf(str1);
                            int i2 = specifySortList.indexOf(str2);
                            i1 = i1 == -1 ? size : i1;
                            i2 = i2 == -1 ? size : i2;
                            return Integer.compare(i1, i2);
                        })))
                .collect(Collectors.toList());
    }


}
