package com.example.qrcode;

public class BCHDecoder {
    static private int poids(int x)
    {
        // Prend un entier x et renvoie son poids (somme des bits non nuls)
        int p = 0;
        int x2 = x;
        while (x2 > 0)
        {
            p += x2 & 1;
            x2 >>= 1;
        }
        return p;
    }
    static private int bitLength(int x)
    {
        int l = 0;
        while ((x >> l) > 0 )l++;
        return l;
    }
    static private int bchCheck(int msg, int generator) throws ArithmeticException{
        // msg : entier
        // generator: polynome générateur du code bch
        // Réalise la division de msg par generator et renvoie le reste
        if (msg == 0) {
            return 0;
        }
        if (generator == 0) {
            throw new ArithmeticException("Division by Zero");
        }

        int d1 = bitLength(msg);
        int d2 = bitLength(generator);
        int i;
        int reste = msg;


        while (d1 >= d2)
        {
            i = d1 - d2;
            reste ^= generator << i;
            if (reste == 0)
                return reste;
            else
                d1 = bitLength(reste);
        }
        return reste;
    }
    static public int[] qrFormat(int format)
    {
        // Permet de corriger le format
        // format: entier correspondant aux bits de format lus après le démasquage

        // Renvoie le format corrigé et la distance au mot corrigé

        int g = 0x537; // Polynome générateur du code BCH
        int res = bchCheck(format, g);
        if (res == 0) {
            System.out.println("[BCH] Aucune erreur detectée !");
            // La transmission s'est bien faite, on a pas détecté d'errreur dans le format
            return new int[]{format,0};
        }
        System.out.println("[BCH] Attention passage en mode correcteur !");

        int[] tests = new int[32]; // On va encoder tout les mots de 10 bits et regarder lequel est le plus proche
        int testCode;
        int mini = 0;
        int i;
        for(i = 0;i < 32; i++)
        {
            testCode = (i << 10) ^ bchCheck(i << 10, g);
            // On récupère sa distance au mot lu sur le QR Code
            tests[i] = poids(testCode ^ format);
        }

        // Maintenant on va chercher le mot le plus proche
        for(i = 0;i < 32; i++){
            if(tests[i] < tests[mini])
                mini = i;
        }
        // On renvoie le mot selectionné
        return new int[]{(mini << 10) ^ bchCheck(mini << 10,g), tests[mini]};
    }
}
