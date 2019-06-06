package com.example.qrcode.decodage.QrReadPackage;

import com.example.qrcode.decodage.QrRead;

public class QrRead25 extends QrRead {

  public QrRead25(int[][] qrcode_table) {	// créer un QRcode carré à partir d'un tableau
    super(qrcode_table);
    this.qr_nbBytes = 44;
    System.out.println("QRCode 25x25 construit\n");
  }

  protected String getQRData() {			// Renvoie une String de tous les bits

    /* --- Variables --- */
    String data = "";

    // positions de départ hardcodées (i,j), sens de parcours nombres de lignes (0:down, 1:up),
    // nombres de lignes à lire (nb_lines), spécial 1 colonnes (0:2 col, 1:1 col)
    int[][] start_bits_list = new int[][] { {24,24,1,16,0}, {9,22,0,16,0}, {24,20,1,4,0}, {15,20,1,7,0}, {9,18,0,7,0}, {21,18,0,4,0},
                                            {24,16,1,4,0}, {20,15,1,5,1}, {15,16,1,9,0}, {5,16,1,6,0}, {0,14,0,6,0}, {7,14,0,18,0},
                                            {24,12,1,18,0}, {5,12,1,6,0}, {0,10,0,6,0}, {7,10,0,18,0}, {16,8,1,8,0}, {9,5,0,8,0},
                                            {16,3,1,8,0},{9,1,0,4,0},{13,1,1,1,1}};
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
        correctionValue = new int[] {10,1};
        break;
      case 0 :
        correctionValue = new int[] {16,1};
        break;
      case 3 :
        correctionValue = new int[] {22,1};
        break;
      case 2 :
        correctionValue = new int[] {28,1};
        break;
    }
    return correctionValue;
  }
}
