package com.example.qrcode.decodage.QrReadPackage;

import com.example.qrcode.decodage.QrRead;

public class QrRead29 extends QrRead {

  public QrRead29(int[][] qrcode_table) {	// créer un QRcode carré à partir d'un tableau
    super(qrcode_table);
    this.qr_nbBytes = 70;
    System.out.println("QRCode 29x29 construit\n");
  }

  protected String getQRData() {			// Renvoie une String de tous les bits

    /* --- Variables --- */
    String data = "";

    // positions de départ hardcodées (i,j), sens de parcours nombres de lignes (0:down, 1:up),
    // nombres de lignes à lire (nb_lines), spécial 1 colonnes (0:2 col, 1:1 col)
    int[][] start_bits_list = new int[][] {{28,28,1,20,0}, {9,26,0,20,0}, {28,24,1,4,0}, {19,24,1,11,0}, {9,22,0,11,0}, {25,22,0,4,0},
                                           {28,20,1,4,0}, {24,19,1,5,1}, {19,20,1,13,0}, {5,20,1,6,0}, {0,18,0,6,0}, {7,18,0,22,0},
                                           {28,16,1,22,0}, {5,16,1,6,0}, {0,14,0,6,0}, {7,14,0,22,0}, {28,12,1,22,0}, {5,12,1,6,0},
                                           {0,10,0,6,0}, {7,10,0,22,0}, {20,8,1,12,0}, {9,5,0,12,0}, {20,3,1,12,0}, {9,1,0,8,0},
                                           {17,1,0,1,1}};
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
        correctionValue = new int[] {15,1};
        break;
      case 0 :
        correctionValue = new int[] {26,1};
        break;
      case 3 :
        correctionValue = new int[] {36,2};
        break;
      case 2 :
        correctionValue = new int[] {44,2};
        break;
    }
    return correctionValue;
  }
}

