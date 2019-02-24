package com.example.qrcode;



import android.accessibilityservice.FingerprintGestureController;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Arrays;
import java.util.Random;

public class ReedSolomonTest {
    ReedSolomon rs;
    int k;

    @Test
    public void ErrorTest() {
        this.k = 0;
        this.rs = new ReedSolomon();
        int N = 10;
        int[] pmsg = rs.encodeRs(new String("Salut comment ca va"),N);
        int[] res;
        int imax = pmsg.length - 1;
        int[] out = new int[ N / 2];

        //   / ! \ Aux symboles correcteurs
        nextPermutation(out, 0,imax,pmsg, N);
    }

    public void nextPermutation(int[] out, int nb, int imax,int[] pmsg,int N) {
        // imax : entier maximal pour les permutations que l'on souhaite réaliser
        Random rand = new Random();
        int nb0 = out.length;
        int r;
        int[]pf = pmsg.clone();
        int[] pCorrected = new int[]  {0};
        if (nb == nb0)
        {
            //System.out.println("==================================================");
            // On modifie la valeurs aux indices
            for(int i = 0;i < out.length;i++) {
                r = rand.nextInt(256);
                pf[out[i]] ^= r;
            }
            try {
                pCorrected = rs.correctRs(pf, N);
            } catch (ArithmeticException e)
            {
                System.out.println(Arrays.toString(out));
                System.out.println("pmsg " + Arrays.toString(pmsg));
                System.out.println("pf = " + Arrays.toString(pf));
            }
            Assert.assertArrayEquals(pmsg, pCorrected);
            k = k + 1;
            System.out.println(Integer.toString(k));

            return;
        }



        int max = 0;
        int[] out2;
        for (int i = 0; i < nb; i++) {
            if (out[i] > max)
                max = out[i];
        }
        if(max == 0)
            nextPermutation(out, nb + 1, imax, pmsg, N);
        for (int i = max + 1; i <= imax; i++)
        {

            // On copie la liste out
            out2 = out.clone();
            out2[nb] = i;
            nextPermutation(out2, nb + 1, imax, pmsg, N);
        }
    }
}
