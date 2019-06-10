package com.example.qrcode.decodage;
import java.util.Arrays;


public class ReedSolomon {
    GfArithmetic gf;

    public ReedSolomon() {
        this.gf = new GfArithmetic();
    }

    public int[] evalueSyndromes(int[] msg, int N) {
        // N: Nombre de symboles de redondance
        //msg: liste comprenant le message et les symbole de redondance

        // On ajoute un syndrome nul pour une question d'indice
        int[] syndromes = new int[N];
        for (int i = 0; i < N; i++) {
            syndromes[i] = this.gf.polyEval(msg, this.gf.gfPow(2, i));
        }

        return syndromes;
    }

    public int[] evalueForneySyndromes(int[] syndromes, int[] Gamma) {
        int s = Gamma.length - 1;
        int N = syndromes.length;
        int[] T = new int[N - s];
        int r = 0;
        for (int k = 0; k < N - s; k++) {
            r = 0;
            for (int j = 0; j < s + 1; j++) {
                r ^= gf.gfMul(Gamma[j], syndromes[k + s - j]);
            }
            T[k] = r;
        }
        return T;
    }


    public int[] encodeRs(String msg, int N) {
        // N nombre de symboles correcteur d'erreur
        int[] res = new int[msg.length() + N];
        int[] generateur = this.gf.createGenPoly(N);
        int[] reste;
        char c;
        for (int i = msg.length() - 1; i >= 0; i++) {
            c = msg.charAt(i);
            res[i] = (int) c;
        }
        reste = this.gf.polyDiv(res, generateur);
        int k = 0;
        //System.out.println("Reste: " + Arrays.toString(reste));
        for (int i = reste.length - 1; i >= 0; i--) {
            res[res.length - 1 - k] = reste[i];
            k++;
        }
        return res;
    }

    // Portage en cours
    private int[] bM(int[] syndromes) {
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
        int z;

        // Si les syndromes sont tous nuls on passe
        boolean f = true;
        for (int i = 0; i < syndromes.length; i++) {
            if (syndromes[i] != 0) {
                f = false;
                break;
            }
        }
        if (f)
            return null;

        while (K <= N) {
            //System.out.println("====== K = "+ Integer.toString(K) + " ======");
            e = syndromes[K - 1];
            for (int i = 1; i < L + 1; i++) {
                if(i >= Lambda.length)
                    z = 0;
                else
                    z = Lambda[i];
                e ^= gf.gfMul(z, syndromes[K - 1 - i]);
            }

            if (e != 0) {
                Lambda2 = this.gf.polyAdd(Lambda, this.gf.polyScal(C, e));
                if ((2 * L) < K) {
                    L = K - L;
                    C = gf.polyScal(Lambda, gf.gfInv(e));
                }
                Lambda = gf.polyCopy(Lambda2);
            }
            // On shift C
            C = this.gf.polyShift(C);
            K++;
            /*System.out.println("e: " + Integer.toString(e));
            System.out.println("L: " + Integer.toString(L));
            System.out.println("K: " + Integer.toString(K));
            System.out.println("Lambda: " + Arrays.toString(Lambda));
            System.out.println("C: " + Arrays.toString(C));*/
        }
        return Lambda;
    }


    public int[] correctRs(int[] pmsg, int N) {
        // N: nombre de symboles correcteur d'erreurs
        // msg: message, liste d'octet
        int[] Lambda, LambdaReverse, LambdaPrime, Gamma, forneySyndroms;
        int[] msg = new int[pmsg.length];
        int X, k = 0, i, O, L2, a;
        for (i = 0; i < pmsg.length; i++) {
            msg[pmsg.length - 1 - i] = pmsg[i];
        }
        int t = N / 2;
        boolean flag = true;

        // Récupération des degrés des effacements
        int effCount = 0;
        for (i = 0; i < pmsg.length; i++) {
            if (pmsg[i] == -1)
                effCount += 1;
        }
        int[] eraseDeg = new int[effCount];
        int[] msgCorrected = msg.clone();
        k = 0;
        for (i = 0; i < msgCorrected.length; i++) {
            if (msgCorrected[i] == -1) {
                msgCorrected[i] = 0;
                eraseDeg[k] = i;
                k++;
            }
        }
        int[] syndromes = this.evalueSyndromes(msgCorrected, N);
        //System.out.println("S : " + Arrays.toString(syndromes));
        for (i = 0; i < syndromes.length; i++) {
            if (syndromes[i] != 0) {
                flag = false;
                break;
            }
        }
        System.out.println("[RS] syndromes : " + Arrays.toString(syndromes)); //TODO

        //On a pas trouvé d'erreurs
        if (flag)
            return pmsg;

        if (effCount == 0) {
            Lambda = this.gf.stripPoly(this.bM(syndromes));
        } else {
            Gamma = this.gf.createErasurePoly(eraseDeg);
            forneySyndroms = evalueForneySyndromes(syndromes, Gamma);
            System.out.println("FS: " + Arrays.toString(forneySyndroms));
            Lambda = bM(forneySyndroms);
            System.out.println("Lambda [BM]: " + Arrays.toString(Lambda));
            if (Lambda == null) {
                Lambda = Gamma;
            }
            else
                Lambda = this.gf.polyMul(Lambda, Gamma);
        }

        LambdaReverse = new int[Lambda.length];
        for (i = 0; i < Lambda.length; i++)
            LambdaReverse[i] = Lambda[Lambda.length - 1 - i];
        LambdaPrime = this.gf.polyDeriv(Lambda);

        int[] racines = new int[Lambda.length - 1];
        int[] indices = new int[Lambda.length - 1];



        k = 0;
        for (i = 0; i < msg.length; i++) {
            if (this.gf.polyEval(LambdaReverse, this.gf.gfPow(2, i)) == 0) {
                racines[k] = this.gf.gfInv(this.gf.gfPow(2, i));
                indices[k] = i;
                k++;
            }
        }


        int[] p = new int[N + 1];
        p[N] = 1;
        int[] Omega = this.gf.polyMul(Lambda, syndromes);
        /*
        System.out.println("S = "+ Arrays.toString(syndromes));
        System.out.println("Sr = "+ Arrays.toString(syndromesReverse));
        System.out.println("Omega1 = "+ Arrays.toString(Omega));
        */
        Omega = this.gf.polyDiv(Omega, p);
        //System.out.println("Omega = "+ Arrays.toString(Omega));
        System.out.println("[RS] indices : " + Arrays.toString(indices));
        //System.out.println("Omega: " + Arrays.toString(Omega));
        //System.out.println("Lambda" + Arrays.toString(Lambda));
        for (i = 0; i < racines.length; i++) {
            X = racines[i];
            O = this.gf.polyEval(Omega, X);
            L2 = this.gf.gfInv(this.gf.polyEval(LambdaPrime, X));

            a = this.gf.gfMul(O, L2);
            msgCorrected[indices[i]] ^= this.gf.gfMul(this.gf.gfInv(X), a);
        }
        //System.out.println("msgCorr: " + Arrays.toString(msgCorrected));
        int[] syndtest = evalueSyndromes(msgCorrected, N);
        System.out.println("SyndTest: " + Arrays.toString(syndtest));
        for (i = 0; i < syndtest.length; i++) {
            if (syndtest[i] != 0) {
                throw new ArithmeticException("Trop d'erreurs, j'ai pas pu corriger");
            }
        }
        for (i = 0; i < pmsg.length; i++) {
            pmsg[pmsg.length - 1 - i] = msgCorrected[i];
        }
        return pmsg;
    }
}
