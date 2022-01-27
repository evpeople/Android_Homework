package com.rocbirds.dotcraft;

import java.util.HashSet;

public class RandomLevel implements Level {
    private final int[] dotArr = new int[9];
    private final int[] containerArr = new int[9];

    public RandomLevel(int n) {

        HashSet<Integer> set = new HashSet<>();
        randomSet(0, 8, n, set);
        for (Integer i :
                set) {
            containerArr[i] = 1;
        }
        set.clear();
        randomSet(0, 8, n, set);

        for (Integer i :
                set) {
            dotArr[i] = 1;
        }
//        dotArr[0] = 1;
//        dotArr[5] = 1;
//        dotArr[6] = 1;
//        dotArr[7] = 1;
//
//        containerArr[0] = 1;
//        containerArr[2] = 1;
//        containerArr[5] = 1;
//        containerArr[4] = 1;
    }

    private static void randomSet(int min, int max, int n, HashSet<Integer> set) {
        if (n > (max - min + 1) || max < min) {
            return;
        }
        for (; true;) {
            // 调用Math.random()方法
            int num = (int) (Math.random() * (max - min)) + min;

            // 将不同的数存入HashSet中
            set.add(num);
            // 如果存入的数小于指定生成的个数，则调用递归再生成剩余个数的随机数，如此循环，直到达到指定大小
            if (set.size() >= n) {
                break;
            }
        }
//        for (int i = 0; i < n; i++) {
//            int num = (int) (Math.random() * (max - min)) + min;
//            set.add(num);
//        }
//        int setSize = set.size();
//        if (setSize < n) {
//            randomSet(min, max, n - setSize, set);
//        }
    }
    @Override
    public int[] getDotArray() {
        return dotArr;
    }

    @Override
    public int[] getContainerArray() {
        return containerArr;
    }
}
