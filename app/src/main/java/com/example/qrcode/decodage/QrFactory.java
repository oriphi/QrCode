package com.example.qrcode.decodage;

import com.example.qrcode.decodage.QrReadPackage.*; // on importe toutes les classes de QrReadPackage

public class QrFactory {

  public QrRead getQrType(int[][] qr_table) {

    // Récupération de la taille du QR code
    int size = qr_table[0].length;
    System.out.println("[QrFactory] Taille du QrCode : " + Integer.toString(size) + "x" + Integer.toString(qr_table.length));
    if (qr_table.length != size) throw new ArithmeticException("[QrFactory] ERREUR : table non carrée");

    // Crée selon size un objet de la taille adéquate
    switch (size) {
      case 21:  return new QrRead21(qr_table);

      case 25:  return new QrRead25(qr_table);

      case 29:  return new QrRead29(qr_table);

      case 33:  return new QrRead33(qr_table);

      case 37:  return new QrRead37(qr_table);

      case 41:  return new QrRead41(qr_table);

      default:  throw new ArithmeticException("[QrFactory] ERREUR : taille de QrCode non implémentée");

    }
  }
}
