package ru.readme.chatapp.util;

import java.util.Random;

public class Truster {
    private static int[] code(int x, int z) {
        int n1 = 2 * x + 4 * z + 124;
        int n2 = 4 * x + 2 * z + 64;
        return new int[]{n1, n2};
    }

    private static int[] decode(int[] n) {
        int x, z;
        int[] r = new int[2];
        int t1 = n[0] - 124;
        int t2 = n[1] - 64;
        z = (2 * t1 - t2) / 6;
        x = t1 / 2 - 2 * z;
        r[0] = x;
        r[1] = z;
        return r;
    }

    public static boolean trust(String code) {
        String sl1 = code.substring(0, 1);
        String sl2 = code.substring(1, 2);
        int l1 = Integer.parseInt(sl1);
        int l2 = Integer.parseInt(sl2);
        String sn1 = code.substring(2, l1 + 2);
        String sn2 = code.substring(2 + l1, l2 + l1 + 2);
        int y = l2 + l1 + 2;
        int n1 = Integer.parseInt(sn1);
        int n2 = Integer.parseInt(sn2);
        String md5 = code.substring(y, y + 32);
        String stime = code.substring(y + 32);

        long time = Long.parseLong(stime);
        if (Math.abs(System.currentTimeMillis() - time) < 1000 * 60 * 60 * 24) {
            int[] xz = decode(new int[]{n1, n2});
            String forMd5 = xz[0] + "%" + xz[1] + "" + stime;
            boolean truest = MD5.MD5(forMd5).equals(md5);
            if (!truest) {
                System.out.println(forMd5);
                System.out.println("l1=" + l1 + " l2=" + l2 + " n1=" + n1 + " n2=" + n2 + " x=" + xz[0] + " z=" + xz[1] + " stime=" + stime + " md5=" + MD5.MD5(forMd5));
            }
            return truest;
        } else {
            System.out.println("time error");
            return false;
        }

    }

    public static String generate() {
        int x = new Random().nextInt(1000000);
        int z = new Random().nextInt(1000000);
        //   System.out.println("x="+x+" z="+z);
        int[] code = code(x, z);
        String s1 = code[0] + "";
        String s2 = code[1] + "";
        int l1 = s1.length();
        int l2 = s2.length();
        String forMD = x + "%" + z;
        long t = System.currentTimeMillis();
        // System.out.println(forMD + t);
        String md5 = MD5.MD5(forMD + t);
        String res = l1 + "" + l2 + code[0] + "" + code[1] + md5 + "" + t;
        return res;

    }
}
