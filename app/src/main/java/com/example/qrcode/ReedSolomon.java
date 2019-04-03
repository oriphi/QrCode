package com.example.qrcode;
import com.example.qrcode.GfArithmetic;
import java.util.Arrays;


public class ReedSolomon {
    GfArithmetic gf;
    ReedSolomon()
    {
        this.gf = new GfArithmetic();
    }

    public int[] evalueSyndromes(int[] msg, int N)
    {
        // N: Nombre de symboles de redondance
        //msg: liste comprenant le message et les symbole de redondance

        // On ajoute un syndrome nul pour une question d'indice
        int[] syndromes = new int[N];
        for(int i = 0; i < N; i++ )
        {
            syndromes[i] = this.gf.polyEval(msg, this.gf.gfPow(2,i));
        }

        return syndromes;
    }


    public int[] encodeRs(String msg, int N)
    {
        // N nombre de symboles correcteur d'erreur
        int[] res = new int[msg.length() + N];
        int[] generateur = this.gf.polyGenerator(N);
        int[] reste;
        char c;
        for(int i = 0; i < msg.length();i++) {
            c = msg.charAt(i);
            res[i] = (int) c;
        }
        reste = this.gf.polyDiv(res, generateur);
        int k = 0;
        //System.out.println("Reste: " + Arrays.toString(reste));
        for(int i = reste.length - 1; i >= 0; i-- )
        {
            res[res.length  - 1 - k] = reste[i];
            k ++;
        }
        return res;
    }

    private int[] bM(int[] syndromes)
    {
        int K = 1;
        int L = 0;
        int e;
        int N = syndromes.length;
        int[] Lambda = new int[N + 1];
        int[] Lambda2 = new int[N + 1];
        int[] C = new int[N + 1];
        int[] C2 = new int[N + 1];
        Lambda[0] = 1;
        Lambda2[0] = 1;
        C[1] = 1;
        C2[1] = 1;
        while(K <= N)
        {
            //System.out.println("====== K = "+ Integer.toString(K) + " ======");
            e = syndromes[ K - 1 ];
            for(int i = 1; i < L + 1;i++)
            {
                e ^= gf.gfMul(Lambda[i], syndromes[ K  - 1 - i ]);
            }

            if(e != 0)
            {
                Lambda2 = this.gf.polyAddReverse(Lambda, this.gf.polyScal(C,e));
                if((2 * L ) < K)
                {
                    L = K  - L;
                    C = gf.polyScal(Lambda, gf.gfInv(e));
                }
                Lambda = gf.polyCopy(Lambda2);
            }
            // On shift C
            C = this.gf.polyShift(C);
            K++;
            /*
            System.out.println("e: " + Integer.toString(e));
            System.out.println("L: " + Integer.toString(L));
            System.out.println("K: " + Integer.toString(K));
            System.out.println("Lambda: " + Arrays.toString(Lambda));
            System.out.println("C: " + Arrays.toString(C));
            */
        }
        //System.out.println("Lambda " + Arrays.toString(this.gf.polyReverse(Lambda)));
        return this.gf.polyReverse(Lambda);
    }



    public int[] correctRs(int[] msg,int N)
    {
        // N: nombre de symboles correcteur d'erreurs
        // msg: message, liste d'octet
       int t = N / 2;
       boolean flag = true;

       int X,k = 0,i, O, L2, a;

       int[] syndromes = this.evalueSyndromes(msg,N);
       //System.out.println("S : " + Arrays.toString(syndromes));
       for(i = 0; i < syndromes.length;i ++ )
       {
           if (syndromes[i] != 0)
           {
               flag = false;
               break;
           }
       }
      System.out.println("[RS] syndromes : " + Arrays.toString(syndromes)); //TODO

       //On a pas trouvé d'erreurs
       if (flag)
           return msg;
       int[] syndromesReverse = this.gf.polyReverse(syndromes);

       int[] msgCorrected = new int[msg.length];
       for(i = 0;i < msg.length;i++)
           msgCorrected[i] = msg[i];

       int[] Lambda = this.gf.stripPoly(this.bM(syndromes));
       int[] LambdaReverse = new int[Lambda.length];
       for(i = 0; i < Lambda.length; i++)
           LambdaReverse[i] = Lambda[Lambda.length - 1 - i];

       int[] LambdaPrime = this.gf.polyDeriv(Lambda);


       int[] racines   = new int[Lambda.length - 1];
       int[] indices   = new int[Lambda.length - 1];
       int[] magnitude = new int[Lambda.length - 1];

       int[] p = new int[N + 1];
       p[0] = 1;
       int[] Omega = this.gf.polyMul(Lambda, syndromesReverse);
       /*
       System.out.println("S = "+ Arrays.toString(syndromes));
       System.out.println("Sr = "+ Arrays.toString(syndromesReverse));
       System.out.println("Omega1 = "+ Arrays.toString(Omega));
       */
       Omega = this.gf.polyDiv(Omega,p);
       //System.out.println("Omega = "+ Arrays.toString(Omega));

       for(i = 0; i< msg.length; i++)
       {
           if(this.gf.polyEval(LambdaReverse, this.gf.gfPow(2,i)) == 0)
           {
              racines[k] = this.gf.gfInv(this.gf.gfPow(2,i));
              indices[k] = msg.length - i - 1;
              //System.out.println("indices[k] :" + indices[k]);
              k ++;
           }
       }
        System.out.println("[RS] indices : " + Arrays.toString(indices)); //TODO
       for(i = 0; i < racines.length; i++)
       {
           X = racines[i];
           O = this.gf.polyEval(Omega,X);
           L2 = this.gf.gfInv(this.gf.polyEval(LambdaPrime,X));

           a = this.gf.gfMul(O,L2);
           msgCorrected[indices[i]] ^= this.gf.gfMul(this.gf.gfInv(X),a);
       }
       int[] syndtest = evalueSyndromes(msgCorrected, N);
       for(i = 0; i < syndtest.length; i++)
       {
           if(syndtest[i] != 0)
               throw new ArithmeticException("Trop d'erreurs, j'ai pas pu corriger");
       }
       return msgCorrected;
    }
}
