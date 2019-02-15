package com.example.qrcode;
import com.example.qrcode.GfArithmetic;


public class ReedSolomon {
    GfArithmetic gf;
    public ReedSolomon()
    {
        this.gf = new GfArithmetic();
    }
    private int[] evalueSyndromes(int[] msg, int N)
    {
        // N: Nombre de symboles de redondance
        //msg: liste comprenant le message et les symbole de redondance

        // On ajoute un syndrome nul pour une question d'indice
        int[] syndromes = new int[N + 1];
        for(int i = 1; i < N; i++ )
        {
            syndromes[i] = this.gf.polyEval(msg, this.gf.gfPow(2,i));
        }
        return syndromes;
    }

    private int[] polyShift(int[] p) throws ArithmeticException
    {
       int[] res = new int[p.length];
       for(int i = 0; i < p.length; i++)
       {
           if ((i + 1) >= p.length)
               throw new ArithmeticException("Overflow");
           else
               res[i + 1] = p[i];
       }
       return res;
    }

    private int[] polyReverse(int[] p)
    {
        int[] res;
        int i =p.length - 1;
        while(p[i] != 0)i--;
        res = new int[i + 1];
        for(i = 0; i< res.length;i++)
            res[res.length - 1 -i] = p[i];
        return res;
    }

    private int[] berlekampMassey(int[] syndromes) throws ArithmeticException
    {
        int N = syndromes.length - 1;
        int t =  N / 2;


        // Initialisation des paramètres de l'algo
        int L = 0;
        int r = 0;
        int d,b;

        int[] B = new int[t + 1];
        int[] Lambda = new int[t + 1];
        int[] LambdaOld;
        int[] p;

        Lambda[0] = 1;
        B[0] = 1;

        int j;

        while(r < N)
        {
            d = 0;
            r ++;
            for(j = 0; j < L + 1; j++)
            {
                d ^= this.gf.gfMul(syndromes[r - j], Lambda[j]);
            }
            if(d != 0)
            {
                // p = x.B[x].d
               p = this.gf.polyMul(new int[]{1,0}, B);
               try {
                   p = this.polyShift(B);
               }
               catch (ArithmeticException e)
                   throw new ArithmeticException("Too much error");

               p = this.gf.polyScal(p,d);
               LambdaOld = Lambda; // Attention peut-être des problèmes de copie
               Lambda = gf.polyAddReverse(p,Lambda);

               if (2 * L <= r - 1)
               {
                   L = r - L;
                   b = this.gf.gfInv(d);
                   B = this.gf.polyScal(LambdaOld, b);
               }
               else
               {
                   B = this.polyShift(B);
               }
            }
        }
        return this.polyReverse(Lambda);
    }



    public int[] correctRs(int[] msg,int N)
    {
       int t = N / 2;


       int[] syndromes = this.evalueSyndromes(msg,N);
       int[] syndromesReverse = new int[syndromes.length - 1];
       for(int i =0; i< syndromesReverse.length; i ++)
           syndromesReverse[i] = syndromes[syndromes.length - 1 - i];
       int[] p = new int[N + 1];
       p[0] = 1;

       int[] msgCorrected = new int[msg.length];
       for(int i = 0;i < msg.length;i++)
           msgCorrected[i] = msg[i];

       int[] Lambda = this.berlekampMassey(syndromes);
       int[] LambdaReverse = new int[Lambda.length];
       for(int i = 0; i < Lambda.length; i++)
       {

       }

       int[] racines   = new int[Lambda.length - 1];
       int[] indices   = new int[Lambda.length - 1];
       int[] magnitude = new int[Lambda.length - 1];

       int[] Omega = this.gf.polyMul(Lambda, syndromesReverse);

    }
}
