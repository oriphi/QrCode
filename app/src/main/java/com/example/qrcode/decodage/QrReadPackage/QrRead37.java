package com.example.qrcode.decodage.QrReadPackage;

import com.example.qrcode.decodage.QrRead;

public class QrRead37 extends QrRead {

  public QrRead37(int[][] qrcode_table) {	// créer un QRcode carré à partir d'un tableau
    super(qrcode_table);
    this.qr_nbBytes = 134;
    System.out.println("QRCode 37x37 construit\n");
  }

  protected String getQRData() {			// Renvoie une String de tous les bits

    /* --- Variables --- */
    String data = "";

    // positions de départ hardcodées (i,j), sens de parcours nombres de lignes (0:down, 1:up),
    // nombres de lignes à lire (nb_lines), spécial 1 colonnes (0:2 col, 1:1 col)
    int[][] start_bits_list = new int[][] {{36,36,1,28,0}, {9,34,0,28,0}, {36,32,1,4,0}, {27,32,1,19,0}, {9,30,0,19,0},
                                          {33,30,0,4,0}, {36,28,1,4,0}, {32,27,1,5,1}, {27,28,1,21,0}, {5,28,1,6,0},
                                          {0,26,0,6,0}, {7,26,0,30,0}, {36,24,1,30,0}, {5,24,1,6,0}, {0,22,0,6,0},
                                          {7,22,0,30,0}, {36,20,1,30,0}, {5,20,1,6,0}, {0,18,0,6,0}, {7,18,0,30,0},
                                          {36,16,1,30,0}, {5,16,1,6,0}, {0,14,0,6,0}, {7,14,0,30,0}, {36,12,1,30,0},
                                          {5,12,1,6,0}, {0,10,0,6,0}, {7,10,0,30,0}, {28,8,1,20,0}, {9,5,0,20,0},
                                          {28,3,1,20,0}, {9,1,0,16,0}, {25,1,0,1,1}};

    int[] start_bit;

    /* --- Traitement --- */
    for (int l = 0; l < start_bits_list.length; l++) {
      start_bit = start_bits_list[l];

      if (start_bit[2] == 1) {     // sens de parcours : UP
        data += this.getDataUp(start_bit[0], start_bit[1], start_bit[3], start_bit[4]);
      }
      else {                       // sens de parcours : DOWN
        data += this.getDataDown(start_bit[0], start_bit[1], start_bit[3], start_bit[4]);
      }
    }

    return data;
  }

  protected int[] getCorrectionValue(int formatbits) { // Renvoie le nombres d'octets de redondance
    int[] correctionValue = new int[] {-1, -1};
    int correctionLevel = formatbits >> 13;

    switch(correctionLevel) { //nb de bytes de redondance (total - bytes de données); nb de blocs
      case 1 : // Low
        correctionValue = new int[] {24,1};
        break;
      case 0 :
        correctionValue = new int[] {48,2};
        break;
      case 3 :
        correctionValue = new int[] {72,4};
        break;
      case 2 :
        correctionValue = new int[] {88,4};
        break;
    }
    return correctionValue;
  }
}

