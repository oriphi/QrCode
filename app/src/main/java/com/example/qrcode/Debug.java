package com.example.qrcode;

import com.example.qrcode.ReedSolomon;

import java.util.Arrays;

public class Debug {
    public static void main(String[] args) {
        GfArithmetic gf = new GfArithmetic();
        //System.out.println(Arrays.toString(gf.gfExp));
        int[] msg = new int[]{64, 210, 117, 71, 118, 23, 50, 6, 39, 38, 150, 198, 198, 150, 112, 236, 188, 42, 144, 19, 107, 175, 239, 253, 75, 224};
        msg[0] = 0;
        msg[1] = 0;
        msg[2] = 0;
        msg[3] = 0;
        msg[4] = 0;
        ReedSolomon rs = new ReedSolomon();
        int[] Corrected = rs.correctRs(msg,10);
        System.out.println(Arrays.toString(Corrected));

    }
}
