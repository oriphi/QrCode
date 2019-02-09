package com.example.qrcode;

public class gfArithmetic {
    public int gfAdd(int x, int y){
        // Addition dans les espaces de Galois
        return x ^ y;
    }
    public int gfSub(int x, int y){
        // Soustraction dans les espaces de Galois
        return x ^ y;
    }
    public int bitLength(int i){
        // Renvoie la taille en nombre de bits de l'entier i
        int l = 0;
        while ((i >> l) > 0){l ++;}
        return l;
    }
    public int gfDiv(int x, int y) throws ArithmeticException
    {
        if (x == 0) return 0;
        if (y == 0) throw new ArithmeticException("Division by Zero");
        int d1 = bitLength(x);
        int d2 = bitLength(y);
        int res = x;
        int i;
        while (d1 >= d2)
        {
            i = d1 - d2;
            res ^= y << i;
            if (res != 0)
                d1 = bitLength(res);
            else
                break;
        }
        return res;
    }
    public int gfMulOverflow(int x, int y)
    {
        int res = 0;
        int i = 0;
        while((y >> i) > 0)
        {
            if((y & (1 << i)) != 0 )
                res ^= x << i;
            i++;
        }
        return res;
    }
    public int gfMul(int x, int y)
    {
        // Véritable routine de multiplication, on prend le résultat modulo un entier générateur
        int res = gfMulOverflow(x,y);
        return gfDiv(x,0x11d);
    }
}
