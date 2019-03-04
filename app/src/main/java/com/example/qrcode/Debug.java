package com.example.qrcode;

import com.example.qrcode.ReedSolomon;

import java.util.Arrays;

public class Debug {
    public static void main(String[] args) {
        GfArithmetic gf = new GfArithmetic();
        ReedSolomon rs = new ReedSolomon();

        //System.out.println(Arrays.toString(gf.gfExp));
        /*
        int[] msg = new int[]{64, 210, 117, 71, 118, 23, 50, 6, 39, 38, 150, 198, 198, 150, 112, 236, 188, 42, 144, 19, 107, 175, 239, 253, 75, 224};
        msg[18] = 56;
        msg[19] = 86;
        int[] S = rs.evalueSyndromes(msg,10);
        System.out.println("Synd : " + Arrays.toString(S));
        int[] msgCorrected = rs.correctRs(msg,10);
        System.out.println("Msg Corrected :" + Arrays.toString(msgCorrected));
        */
        String msg = "'Twas brillig";
        int[] rsEncode = rs.encodeRs(msg,10);
        System.out.println(Arrays.toString(rsEncode));

    }
}
