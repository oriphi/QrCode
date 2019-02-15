package com.example.qrcode;

import com.example.qrcode.ReedSolomon;

import java.util.Arrays;

import static com.example.qrcode.BCHDecoder.qrFormat;
import com.example.qrcode.QrRead;

public class Debug {
    public static void main(String[] args) {
        GfArithmetic gf = new GfArithmetic();
        //System.out.println(Arrays.toString(gf.gfExp));
        int[] msg = new int[]{64, 210, 117, 71, 118, 23, 50, 6, 39, 38, 150, 198, 198, 150, 112, 236, 188, 42, 144, 19, 107, 175, 239, 253, 75, 224};
        msg[0] = 0;
        msg[10] = 0;
        msg[2] = 0;
        ReedSolomon rs = new ReedSolomon();
        int[] Corrected = rs.correctRs(msg,10);
        //System.out.println(Arrays.toString(Corrected));



        /* ROUTINE MESSAGE */

        // Récupération du QRcode
        QrRead qrcode = new QrRead();


        // erreurs
        int[][] erreurs = new int[][] {{20, 8}, {19, 8}, {18, 8}, {17, 8}};

        for(int i = 0; i < erreurs.length; i++) {
            qrcode.invertBit(erreurs[i][0], erreurs[i][1]);
        }


        // Récupération des bits de format
        int[] formatbits = qrcode.getFormatBits();

        // Correction des bits de formats
        int formatbits_decode = qrFormat(formatbits[0]);

        // Démasquage du QRcode
        qrcode.unmaskData(formatbits_decode);

        // Récupération des octets de données
        int[] qrBytes = qrcode.getQRBytes();

        // Correction des données
        int nbRedundantBytes = qrcode.getCorrectionValue(formatbits_decode);
        int[] qrBytes_decode = qrBytes;

        // Traduction des données corrigées
        String msg = qrcode.getQRMessage(qrBytes_decode, formatbits_decode);


        System.out.println(msg);
    }
}
