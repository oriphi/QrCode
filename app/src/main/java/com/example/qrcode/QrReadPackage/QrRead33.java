package com.example.qrcode.QrReadPackage;

import com.example.qrcode.QrRead;

public class QrRead33 extends QrRead {

  public QrRead33(int[][] qrcode_table) {	// créer un QRcode carré à partir d'un tableau
    super(qrcode_table);
    this.qr_nbBytes = 100;
    System.out.println("QRCode 33x33 construit\n");
  }

  protected String getQRData() {			// Renvoie une String de tous les bits

    /* --- Variables --- */
    String data = "";

    // positions de départ hardcodées (i,j), sens de parcours nombres de lignes (0:down, 1:up),
    // nombres de lignes à lire (nb_lines), spécial 1 colonnes (0:2 col, 1:1 col)
    int[][] start_bits_list = new int[][] {{32,32,1,24,0}, {9,30,0,24,0}, {32,28,1,4,0}, {23,28,1,15,0}, {9,26,0,15,0},
            {29,26,0,4,0}, {32,24,1,4,0}, {28,23,1,5,1}, {23,24,1,17,0}, {5,24,1,6,0}, {0,22,0,6,0}, {7,22,0,26,0},
            {32,20,1,26,0}, {5,20,1,6,0}, {0,18,0,6,0}, {7,18,0,26,0}, {32,16,1,26,0}, {5,16,1,6,0}, {0,14,0,6,0},
            {7,14,0,26,0}, {32,12,1,26,0}, {5,12,1,6,0}, {0,10,0,6,0}, {7,10,0,26,0}, {24,8,1,16,0}, {9,5,0,16,0},
            {24,3,1,16,0}, {9,1,0,12,0}, {21,1,0,1,1}};

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
        correctionValue = new int[] {20,1};
        break;
      case 0 :
        correctionValue = new int[] {36,2};
        break;
      case 3 :
        correctionValue = new int[] {52,2};
        break;
      case 2 :
        correctionValue = new int[] {64,4};
        break;
    }
    return correctionValue;
  }
}

