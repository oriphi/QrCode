package com.example.qrcode.QrReadPackage;

import com.example.qrcode.QrRead;

public class QrRead21 extends QrRead {

  public QrRead21(int[][] qrcode_table) {	// créer un QRcode carré à partir d'un tableau
    super(qrcode_table);
    this.qr_nbBytes = 26;
    System.out.println("QRCode 21x21 construit\n");
  }

  protected String getQRData() {			// Renvoie une String de tous les bits

    /* --- Variables --- */
    String data = "";

    // positions de départ hardcodées (i,j), sens de parcours nombres de lignes (0:down, 1:up),
    // nombres de lignes à lire (nb_lines) , (sur 1 seule colonne:1)
    int[][] start_bits_list = new int[][] {{20,20,1,12,0}, {9,18,0,12,0}, {20,16,1,12,0}, {9,14,0,12,0},
            {20,12,1,14,0}, {5,12,1,6,0}, {0,10,0,6,0}, {7,10,0,14,0},
            {12,8,1,4,0}, {9,5,0,4,0}, {12,3,1,4,0}, {9,1,0,4,0}};
    int[] start_bit;

    /* --- Traitement --- */
    for (int l = 0; l < start_bits_list.length; l++) {
      start_bit = start_bits_list[l];

      if (start_bit[2] == 1) { // sens de parcours : UP
        data += this.getDataUp(start_bit[0], start_bit[1], start_bit[3], start_bit[4]);
      }
      else {					 // sens de parcours : DOWN
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
        correctionValue = new int[] {7,1};
        break;
      case 0 :
        correctionValue = new int[] {10,1};
        break;
      case 3 :
        correctionValue = new int[] {13,1};
        break;
      case 2 :
        correctionValue = new int[] {17,1};
        break;
    }
    return correctionValue;
  }

}
