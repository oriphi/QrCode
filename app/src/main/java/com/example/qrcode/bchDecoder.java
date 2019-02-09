package com.example.qrcode;

public class bchDecoder {
    private int poids(int x)
    {
        // Prend un entier x et renvoie son poids (somme des bits non nuls)
        int p = 0;
        int x2 = x;
        while (x2 > 0)
        {
            p += x & 1;
            x >>= 1;
        }
        return p;
    }
    private int bitLength(int x)
    {
        int l = 0;
        while ((x >> l) > 0 )l++;
        return l;
    }
    private int bchCheck(int msg, int generator) throws ArithmeticException{
        // msg : entier
        // generator: polynome générateur du code bch
        // Réalise la division de msg par generator et renvoie le reste
        if (msg == 0)
            return 0;
        if (generator == 0)
            throw new ArithmeticException("Division by Zero");
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
    public int qrFormat(int format)
    {
        // Permet de corriger le format
        // format: entier correspondant aux bits de format lus après le démasquage

        int g = 0x537; // Polynome générateur du code BCH
        int res = bchCheck(format, g);
        if (res == 0)
            // La transmission s'est bien faite, on a pas détecté d'errreur dans le format
            return format;

        int[] tests = new int[32]; // On va encoder tout les mots de 10 bits et regarder lequel est le plus proche
        int testCode;
        int mini = 0;
        int i;
        for(i = 0;i < 32; i++)
        {
            testCode = i << 10 ^ bchCheck(i << 10, g);
            // On récupère sa distance au mot lu sur le QR Code
            tests[i] = poids(testCode ^ format);
        }

        // Maintenant on va chercher le mot le plus proche
        for(i = 0;i < 32; i++){
            if(tests[i] < tests[mini])
                mini = i;
        }
        // On renvoie le mot selectionné
        return mini << i ^ bchCheck(i << 10,g);
    }
}
