package com.example.qrcode;



import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Arrays;
import java.util.Random;

public class ReedSolomonTest {
    ReedSolomon rs;

    @Test
    public void ErrorTest() {
        this.rs = new ReedSolomon();
        int[] res;
        int[] out = new int[4];
        //int[] pmsg = new int[]{64, 210, 117, 71, 118, 23, 50, 6, 39, 38, 150, 198, 198, 150, 112, 236, 188, 42, 144, 19, 107, 175, 239, 253, 75, 224};
        int[] pmsg = new int[]{1,2,3,4,182,17,65,188,231,2,0,187};
        // ! \ Aux symboles correcteurs
        nextPermutation(out, 0,11,pmsg);
    }

    public void nextPermutation(int[] out, int nb, int imax,int[] pmsg) {
        // imax : entier maximal pour les permutations que l'on souhaite réaliser
        Random rand = new Random();
        int nb0 = out.length;
        int r;
        int[]pf = pmsg.clone();
        int[]pCorrected;
        if (nb == nb0)
        {
            System.out.println(Arrays.toString(out));
            // On modifie la valeurs aux indices
            for(int i = 0;i < out.length;i++) {
                r = rand.nextInt(256);
                pf[out[i]] ^= r;
            }
            System.out.println("pf   " + Arrays.toString(pf));
            pCorrected = rs.correctRs(pf,8);
            System.out.println("pC  " + Arrays.toString(pCorrected));

            Assert.assertArrayEquals(pmsg, pCorrected);
            return;
        }
        // Cas particulier out = [0,0,0]



        int max = 0;
        int[] out2;
        for (int i = 0; i < nb; i++) {
            if (out[i] > max)
                max = out[i];
        }
        if(max == 0)
            nextPermutation(out, nb + 1, imax, pmsg);
        for (int i = max + 1; i <= imax; i++)
        {

            // On copie la liste out
            out2 = out.clone();
            out2[nb] = i;
            nextPermutation(out2, nb + 1, imax, pmsg);
        }
    }


}
