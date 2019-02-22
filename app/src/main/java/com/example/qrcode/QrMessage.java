package com.example.qrcode;

import static com.example.qrcode.BCHDecoder.qrFormat;


/* Délivre le message du QR code */

public class QrMessage {

  private QrRead qrcode;
  private ReedSolomon RS;


  /* ------------ CONSTRUCTEUR ---------------- */

  public QrMessage() {
    this.qrcode = new QrRead(); // TODO - construire un Qrcode à partir d'une liste externe
    this.RS = new ReedSolomon();
    }

  /* ------------ METHODE --------------------- */

  public String getQrMessage() {

    // erreurs
    int[][] erreurs = new int[][]{{20, 8}, {19, 8}, {18, 8}, {17, 8}, {14, 14}, {1, 15}, {3, 20}, {12, 20}, {18, 15}};

    for (int i = 0; i < erreurs.length; i++) {
      this.qrcode.invertBit(erreurs[i][0], erreurs[i][1]);
    }

    // Récupération des bits de format
    int[] formatbits = this.qrcode.getFormatBits();

    // Correction des bits de formats
    int formatbits_decode;
    int[] formatbits_decode1 = qrFormat(formatbits[0]);
    int[] formatbits_decode2 = qrFormat(formatbits[1]);

    if(formatbits_decode1[1] <= formatbits_decode2[1]) // On garde le format qui a été le moins corrigé
      formatbits_decode = formatbits_decode1[0];
    else formatbits_decode = formatbits_decode2[0];


    // Démasquage du QRcode (masque données)
    this.qrcode.unmaskData(formatbits_decode);

    // Récupération des octets de données
    int[] qrBytes = this.qrcode.getQRBytes();

    // Correction des données
    int nbRedundantBytes = this.qrcode.getCorrectionValue(formatbits_decode);
    int[] qrBytes_decode = this.RS.correctRs(qrBytes, nbRedundantBytes);

    // Traduction des données corrigées
    String msg = this.qrcode.getQRMessage(qrBytes_decode, formatbits_decode);

    return msg;
  }



}
