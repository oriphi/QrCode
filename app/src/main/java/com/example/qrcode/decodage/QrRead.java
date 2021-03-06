package com.example.qrcode.decodage;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import static com.example.qrcode.decodage.BCHDecoder.qrFormat;

public class QrRead {

    /* ------------ VARIABLES -------------------- */

  private int[][] qr_data ;
  private int[][] qr_data_unmask;

  private int qr_size;
  private int qr_version;
  private int qr_formatbits_size = 15;
  protected int qr_nbBytes;

  private HashMap<Integer, String> Int2AlphaNum;

  private ReedSolomon RS;

    /* ------------ CONSTRUCTEURS ---------------- */

  public QrRead(int[][] qrcode_table)   // Partiellement implémentée dans les sous classes
  {
    initInt2AlphaNum();
    // Classes correction d'erreurs
    this.RS = new ReedSolomon();

    // Paramètres du QR code
    this.qr_size = qrcode_table.length;
    this.qr_version = (this.qr_size - 17) / 4;
    this.qr_data = new int[this.qr_size][this.qr_size];
    this.qr_data_unmask = new int[this.qr_size][this.qr_size]; // Permet de conserver le tableau des données après le dé-masquage

    // Initialisation du tableau
    for (int i = 0; i < this.qr_size; i++) {
      for (int j = 0; j < this.qr_size; j++) {
        this.qr_data[i][j] = qrcode_table[i][j];
      }
    }
  }

    /* ------------ METHODES --------------------- */

  public String getQrMessageDecode()    // Procédure Générale, renvoie le message décodé
  {
    // Récupération des bits de format corrigés
    int formatbits_decode = this.getFormatBits();

    // Démasquage du QRcode (masque données)
    this.unmaskData(formatbits_decode);

    // Récupération des octets de données
    int[] qrBytes = this.getQRBytes();
    System.out.println("\nBytes non corrigés : " + Arrays.toString(qrBytes));

    // Correction des données
    int nbRedundantBytes = this.getCorrectionValue(formatbits_decode)[0];
    System.out.println("Niveau de correction : " + (formatbits_decode >> 13) + " (" + nbRedundantBytes + " / " + this.qr_nbBytes + " octets) (corr / tous)");
    int[] qrBytes_decode = this.correctMessage(qrBytes, formatbits_decode);

    // Traduction des données corrigées
    String msg = this.getQRMessage(qrBytes_decode, formatbits_decode);
    System.out.println("[QrRead] Message : " + msg);
    return msg;
  }

  private int getFormatBits()     // Récupère les deux mots de format, applique le masque et renvoie deux entiers
  {
    System.out.println("  [getFormatBits] ");

    /* Variables ----------------------------------------- */

    // emplacement des bits de format pour un QRcode 21x21
    int[][] formatbits_location = new int[][] {{ this.qr_size - 1, 8}, {this.qr_size - 2, 8}, {this.qr_size - 3, 8}, {this.qr_size - 4, 8}, {this.qr_size - 5, 8}, {this.qr_size - 6, 8}, {this.qr_size - 7,8},
            {8, 8}, {7, 8}, {5, 8}, {4, 8}, {3, 8}, {2, 8}, {1, 8}, {0, 8}};
    int[][] formatbits_location_bis = new int[][] {{8, 0}, {8, 1}, {8, 2}, {8, 3}, {8, 4}, {8, 5}, {8, 7},
            {8, this.qr_size - 8}, {8, this.qr_size - 7}, {8, this.qr_size - 6}, {8, this.qr_size - 5}, {8, this.qr_size - 4}, {8, this.qr_size - 3}, {8, this.qr_size - 2}, {8, this.qr_size - 1}};

    // masque des bits de format
    int formatbits_mask = 21522;

    // variables internes
    String temp = "";
    int bit; // var temporaire
    int[] formatbits = new int[2];
    int[] pos;

    /* Récupération des bits de format sur la colonne (v1) ----- */

    for (int i = 0; i < this.qr_formatbits_size; i++) {
      pos = formatbits_location[i];															// récupération des 2 indices (ligne,colonne)
      bit = this.qr_data[pos[0]][pos[1]];
      if((bit != 0) && (bit != 1)) bit = 0;                     // gestion des effacements
      temp += Integer.toString(bit);  	// récupération du bit et ajout au nombre binaire stocké en string
    }

    formatbits[0] = Integer.parseInt(temp, 2);				      	// sauvegarde version 1
    System.out.println("Bits de format masqués (v1)   : " + temp);
    formatbits[0] = formatbits[0] ^ formatbits_mask;		          // application du masque des bits de format

    temp = "";																								    // remise à zéro de la variable temporaire

  /* Récupération des bits de format sur la ligne (v2) --------- */

    for (int i = 0; i < this.qr_formatbits_size; i++) {
      pos = formatbits_location_bis[i];
      bit = this.qr_data[pos[0]][pos[1]];
      if(bit != 0 && bit != 1) bit = 0;                     // gestion des effacements
      temp += Integer.toString(bit);  	// récupération du bit et ajout au nombre binaire stocké en string
    }

    formatbits[1] = Integer.parseInt(temp, 2);				       	// sauvegarde version 2
    System.out.println("Bits de format masqués (v2)   : " + temp);
    formatbits[1] = formatbits[1] ^ formatbits_mask;	          	// application du masque des bits de format
    System.out.println("Bits de format démasqués (v1) : " + String.format("%15s", Integer.toBinaryString(formatbits[0])).replace(" ", "0"));
    System.out.println("Bits de format démasqués (v2) : " + String.format("%15s", Integer.toBinaryString(formatbits[1])).replace(" ", "0"));

    /* Correction des bits de format ---------------------------- */

    int formatbits_decode;
    int[] formatbits_decode1 = qrFormat(formatbits[0]);
    int[] formatbits_decode2 = qrFormat(formatbits[1]); // qrFormat(int) renvoie la valeur du code la plus proche [0] et la distance [1]

    System.out.println("Erreurs version 1 : " + formatbits_decode1[1]);
    System.out.println("Erreurs version 2 : " + formatbits_decode2[1]);

    if (formatbits_decode1[1] <= formatbits_decode2[1]) // On garde celui qui a été le moins corrigé (distance la plus faible)
      formatbits_decode = formatbits_decode1[0];
    else formatbits_decode = formatbits_decode2[0];

    System.out.println("Bits de format corrigés (v1)  : " + String.format("%15s", Integer.toBinaryString(formatbits_decode1[0])).replace(" ", "0"));
    System.out.println("Bits de format corrigés (v2)  : " + String.format("%15s", Integer.toBinaryString(formatbits_decode2[0])).replace(" ", "0"));


    System.out.println("Bits de format corrigés       : " + String.format("%15s", Integer.toBinaryString(formatbits_decode)).replace(" ", "0"));
      return formatbits_decode;

  }

  private int getMask(int formatbits)                               // Renvoie le numéro correspodant au masque dans les bits de format
  {
      int mask;
      int mask_maskbits = 7168; 									// masque pour les bits en position 2, 3 et 4 (sur 15 en partant des poids forts)

      mask = (formatbits & mask_maskbits) >> 10;	// récupération des bits {2, 3, 4 }

      return mask;
  }

  private int getMaskValue(int mask, int i, int j)                  // Renvoie la valeur du bit du masque mask à la position i,j
  {
    int value = 0;							// par défaut la valeur est 0
    switch(mask) {							// selon le mask on met à 1 les positions validant la condition du masque
      case 0:
        if ((i + j) % 2 == 0) value = 1;
        break;
      case 1:
        if (i % 2 == 0) value = 1;
        break;
      case 2:
        if (j % 3 == 0) value = 1;
        break;
      case 3:
        if ((i + j) % 3 == 0) value = 1;
        break;
      case 4:
        if ((i/2 + j/3) % 2 == 0) value = 1;
        break;
      case 5:
        if ((i * j) % 2 + (i * j) % 3 == 0) value = 1;
        break;
      case 6:
        if (((i * j) % 3 + i * j) % 2 == 0) value = 1;
        break;
      case 7:
        if (((i * j) % 3 + i + j) % 2 == 0) value = 1;
        break;
    }
    return value;
  }

  private void unmaskData(int formatbits)                           // Applique le masque décrit dans les bits de format au bit de données écrit dans qr_data_unmask
  {
    int mask;
    int mask_value;
    int bit;
    System.out.println("\n  [ unmaskData ] ");
    mask = this.getMask(formatbits); // récupérer le masque
    System.out.println("Masque : " + mask);

    for (int i = 0; i < this.qr_size; i++) {					                // parcourir les bits
      for (int j = 0; j < this.qr_size; j++) {
        mask_value = getMaskValue(mask,i,j);
        bit = this.qr_data[i][j];
        if(bit != 0 && bit != 1) this.qr_data_unmask[i][j] = -1;                               // on garde les effacements tels quels
        else                     this.qr_data_unmask[i][j] = this.qr_data[i][j] ^ mask_value;  // Sinon ou exclusif avec le masque
      }
    }
  }

  protected int[] getCorrectionValue(int formatbits)                // Renvoie le nombres d'octets de redondance
  {
      System.out.println("ERROR : appel à getCorrectionValue dans la classe mère");
      return null;
  }

  protected String getDataUp(int i, int j, int nb_lines, int col)   // Récupère nb_lines du QR code à partir du coin inf droit (i,j) en montant sur 1 colones si col = 0 2 sinon
  {
      String temp = "";
      int bit;

      for (int y = 0; y < nb_lines; y++) {
        bit = this.qr_data_unmask[i - y][j];
        if(bit != 0 && bit != 1) temp += "e";
        else                     temp += Integer.toString(bit);

        if(col == 0) { // Lecture 2 colonnes par 2
          bit = this.qr_data_unmask[i - y][j - 1];
          if(bit != 0 && bit != 1) temp += "e";
          else                     temp += Integer.toString(bit);
        }
      }
      return temp;
  }

  protected String getDataDown(int i, int j, int nb_lines, int col) // Récupère nb_line du QR code à partir du coin sup droit (i,j) en descendant sur 1 colones si col = 0 2 sinon
  {
      String temp = "";
      int bit;

      for (int y = 0; y < nb_lines; y++) {
        bit = this.qr_data_unmask[i + y][j];
        if(bit != 0 && bit != 1) temp += "e";
        else          temp += Integer.toString(bit);

        if(col == 0) { // Lecture 2 colonnes par 2
          bit = this.qr_data_unmask[i + y][j - 1];
          if(bit != 0 && bit != 1) temp += "e";
          else                     temp += Integer.toString(bit);
        }
      }
      return temp;
  }

  protected String getQRData()                                      // Renvoie une String de tous les bits (Dépends du type de QrCode)
  {
    System.out.println("ERROR : getQRData n'est pas implémentée pour ce type de QRCode");
    return "";
  }

  private int[] getQRBytes()                                        // Renvoie les données sous forme d'un tableau d'octets
  {
    /* ---  Variables --- */
    String data = this.getQRData();											// Récupère une chaîne de caractère contenant tous les bits
    int[] QRbytes = new int[data.length() / 8];					// Init le tableau d'octets à renvoyer
    String[] temp = new String[data.length()/ 8];
    String octet = "";																	// Var temporaire
    Character bit;

    /* ---  Traitement --- */
    for (int i = 0; i < data.length(); i = i + 8) {	// Parcours octet par octet
        for(int j = 0; j < 8; j ++) {
            bit = data.charAt(i + j);
            if(bit == 'e') {    // 1 effacement => l'octet est effacé
              octet = "e";
              break;
            }
            octet += bit; 			// Concaténation des bits
        }

        if(octet !="e") QRbytes[i / 8] = Integer.parseInt(octet, 2); // Convertion du binaire en int
        else            QRbytes[i / 8] = -1;
        octet = "";											 						      // RAZ de la var temp
    }
    return QRbytes;
  }

  private String getQRMessage(int[] bytesList, int formatbits)      // Renvoie le message à partir de la liste d'octet corrigée
  {
    System.out.println("\n  [ getQRMessage ]");
      /* --- Variables  --- */
      String msg ="";
      String bitList = bytesList2BinaryString(bytesList);
      int ind = 0;
      int mode;
      int[] modeValues;
      int nbCharInMode;
      int valueChar;
      int nbCharTotal = this.qr_nbBytes - this.getCorrectionValue(formatbits)[0];

      for(int x = 0; x < nbCharTotal;) {

          /* Récupération des 4 premiers bits de mode */
          mode = Integer.parseInt(bitList.substring(ind, ind + 4),2);
          System.out.println("Mode des données : " + mode);
          modeValues = getModeValues(mode);
          if(modeValues[0] == 0)  break;	// Caractère de fin de données => Arrêt de la lecture
          ind = ind + 4;

          /* Récupération du nombre de caractères dans le mode donné */
          nbCharInMode = Integer.parseInt(bitList.substring(ind, ind + modeValues[1]), 2);
          System.out.println("Nombre de char dans le mode : " + nbCharInMode);
          ind += modeValues[1];

          /* Récupération des caractères */
          for(int i = 0; i < nbCharInMode; i++) {
              valueChar = Integer.parseInt(bitList.substring(ind, ind + modeValues[0]),2);
              msg += getNextChar(mode, valueChar);
              x = x + 1;
              ind += modeValues[0];
          }
          if(mode == 4) {
            msg = new String(msg.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8); // formule magique !
          }
      }

      return msg;
  }

  private String getNextChar(int mode, int valueChar)               // Renvoie le charactère codé selon mode dans bitList
  {
      String nextChar = "";
      switch(mode) {
          case 1:
              nextChar = bit2NumericString(valueChar);
              break;
          case 2:
              nextChar = bit2AlphaNumString(valueChar);
              break;
          case 4:
              nextChar = bit2ByteString(valueChar);
              break;
          case 8:
              nextChar = bit2KanjiString(valueChar);
              break;
      }
      return nextChar;
  }

  private String bit2NumericString(int valueChar)                   // Renvoie un nombre entier sous forme de String
  {
      return Integer.toString(valueChar);
  }

  private String bit2AlphaNumString(int valueChar)                  // Renvoie 2 caractères Alphanumériques
  {
      String AlphaNumString;
      int a;
      int b;

      b = valueChar % 45;
      a = (valueChar - b) / 45 ;

      AlphaNumString = this.Int2AlphaNum.get(a) + this.Int2AlphaNum.get(b);

      return AlphaNumString;
  }

  private String bit2ByteString(int valueChar)                      // Renvoie un caractère ASCII
  {
      return Character.toString((char) valueChar);
  }

  private String bit2KanjiString(int valueChar)                     //TODO Kanji
  {
      return "Kanji";
  }

  private int[] getModeValues(int mode)                             // Renvoie les paramètres propres à un modes de codage
  {
      int[] modeValues = new int[2]; // [0] nb de bit par caractère
                                     //     0: fin des données, 10: Numeric, 11: Alphanumeric,
                                     //     8: Byte, 13: Kanji
                                     // [1] longueur du code du nb de données codées dans le mode
      switch(mode) {
          case 0:
              break; // caractère de fin -> on sort avec les valeurs [0,0]
          case 1:
              modeValues[0] = 10;
              if(this.qr_version <= 9)       modeValues[1] = 10;
              else if(this.qr_version <= 26) modeValues[1] = 12;
              else                           modeValues[1] = 14;
              break;
          case 2:
              modeValues[0] = 11;
              if(this.qr_version <= 9)       modeValues[1] = 9;
              else if(this.qr_version <= 26) modeValues[1] = 11;
              else                           modeValues[1] = 13;
              break;
          case 4:
              modeValues[0] = 8;
              if(this.qr_version <= 9)       modeValues[1] = 8;
              else if(this.qr_version <= 26) modeValues[1] = 16;
              else                           modeValues[1] = 16;
              break;
          case 7:
              System.out.println("[QrRead] mode ECI non implémenté");
              throw new ArithmeticException("[QrRead]\nMode ECI non implémenté"); // TODO mode ECI
          case 8:
              modeValues[0] = 13;
              if(this.qr_version <= 9)       modeValues[1] = 8;
              else if(this.qr_version <= 26) modeValues[1] = 10;
              else                           modeValues[1] = 12;
              break;
          default :
              System.out.println("[QrRead] mode erroné " + mode ); // TODO d'autres modes bizarres
              throw new ArithmeticException("[QrRead]\nMode erroné " + mode );
      }
      System.out.println(Arrays.toString(modeValues));
      return modeValues;
  }

  private String bytesList2BinaryString(int[] bytesList)            // Renvoie la liste de bits correspondant à une suite d'entier
  {
      String bitList = "";
      for (int i = 0; i < bytesList.length; i++) {
          bitList += Integer.toBinaryString(0x100| bytesList[i] ).substring(1); // Zero padding sur 8 bit
      }
      return bitList;
  }

  private int[] correctMessage(int[] qrBytes, int formatbits)       // Corrige la liste d'octets par bloc et renvoie qrBytes_decode
  {
    /* --- Variables --- */
    // paramètres "primaires" dus à la version
    int[] correctParameters = this.getCorrectionValue(formatbits);
    int nbRedundantBytes = correctParameters[0];
    int nbDataBytes = this.qr_nbBytes - nbRedundantBytes;
    int nbBlocs = correctParameters[1];

    // paramètres "secondaires" nécessaire au dé-entrelaçement
    int nbLongBlocs = this.qr_nbBytes % nbBlocs;
    int nbShortBlocs = nbBlocs - nbLongBlocs;
    int lenShortBlocs = nbDataBytes / nbBlocs;
    int lenLongBlocs = (nbLongBlocs != 0)? (nbDataBytes - nbShortBlocs * lenShortBlocs) / nbLongBlocs : 0;
    int lenCorrect = nbRedundantBytes / nbBlocs;

    System.out.println("\n  [ correctMessage() ]  ");
    System.out.println("Nb Blocs: " + nbBlocs + " --  Short/Long : " + nbShortBlocs + "(" + lenShortBlocs + ") / " + nbLongBlocs + "(" + lenLongBlocs + ")");


    /* --- Dé-entrelacement --- */
    int count = 0; // indice sur qrBytes
    int[][] bytesShort = new int[nbShortBlocs][lenShortBlocs + lenCorrect]; // ligne : un bloc court, permet d'adresser un bloc par bytesShort[nbBloc]
    int[][] bytesLong = new int[nbLongBlocs][lenLongBlocs + lenCorrect];    // ligne : un bloc long,

    // -- Parcours des données entrelacées --

    // [S1, S2, L3, L4,    S1, S2, L3, L4   ... , S1, S2, L3, L4,   L3, L4, L3, L4    ..., L3, L4 ,  C1, C2, C3, C4]
    // Commence avec TOUS les blocs       ...   fin des courts reste les longs       ...   fin données et début correct

    for(int i = 0; i < lenShortBlocs; i++) {       // on parcourt nbBloc * lenShortBlocs octets
      for(int j = 0; j < nbShortBlocs; j++) {
        bytesShort[j][i] = qrBytes[count];         // Premiers octets -> blocs courts (S1, S2)
        count++;
      }
      for(int j = nbShortBlocs; j < nbBlocs; j++) {
        bytesLong[j - nbShortBlocs][i] = qrBytes[count]; // Seconds octets -> blocs longs (L3, L4)
        count++;
      }
    }
    for(int i = lenShortBlocs; i < lenLongBlocs; i++) {  // Reste octets -> blocs longs (L3, L4)
      for(int j = 0; j < nbLongBlocs; j++) {
        bytesLong[j][i] = qrBytes[count];
        count++;
      }
    }
    System.out.println("nbdatabytes : " + nbDataBytes);
    System.out.println("count fin data : " + count);
    // -- Parcours des corrections entrelacées --

    // Tous font la même taille

    for(int i = 0; i < lenCorrect; i++) {
      for(int j = 0; j < nbShortBlocs; j++) {
        bytesShort[j][i + lenShortBlocs] = qrBytes[count];              // Premiers octets -> blocs courts (C1, C2)
        count++;
      }
      for(int j = nbShortBlocs; j < nbBlocs; j++) {
        bytesLong[j - nbShortBlocs][i + lenLongBlocs] = qrBytes[count]; // Seconds octets -> blocs longs (C3, C4)
        count++;
      }
    }
    System.out.println("dé-entrelaçement : " + count + " / " + this.qr_nbBytes);

    /* --- Correction --- */
    int[][] bytesShort_corr = new int[nbShortBlocs][lenShortBlocs + lenCorrect]; // Stockage des versions corrigées
    int[][] bytesLong_corr = new int[nbLongBlocs][lenLongBlocs + lenCorrect];


    for(int i = 0; i < nbShortBlocs; i++) { // Correction blocs courts
      System.out.println("\nCorr bloc court : " + i);
      System.out.println(Arrays.toString(bytesShort[i]));
      bytesShort_corr[i] = this.RS.correctRs(bytesShort[i], lenCorrect);
    }
    for(int i = 0; i < nbLongBlocs; i++) { // Correction blocs longs
      System.out.println("\nCorr bloc long  : " + i);
      System.out.println(Arrays.toString(bytesLong[i]));
      bytesLong_corr[i] = this.RS.correctRs(bytesLong[i], lenCorrect);
    }


    /* --- Assembler --- */

    int[] dataBytes = new int[nbDataBytes];

    int i = 0;
    int j;
    count = 0;     // position d'ajout dans dataBytes
    for(; i < nbShortBlocs; i++){
      for(j = 0; j < lenShortBlocs; j++) {
        dataBytes[count] = bytesShort_corr[i][j]; // Ajout des blocs courts
        count++;
      }
    }
    for(; i < nbBlocs; i++){
      for(j = 0; j < lenLongBlocs; j++) {
        dataBytes[count] = bytesLong_corr[i - nbShortBlocs][j]; // Ajout des blocs longs
        count++;
      }
    }
    System.out.println("\nDatabytes    : " + Arrays.toString(dataBytes));
    return dataBytes;
  }



  /* ------------ GETTERS / SETTERS ---------------- */

  public int[][] getQRcode() {
      return this.qr_data;
  }

  public void getQRcode_unmasked() {
    System.out.print("[");
    for(int i = 0; i< this.qr_size; i++) {
      System.out.print("[");
      for(int j = 0; j < this.qr_size; j++) {
        System.out.print(this.qr_data_unmask[i][j]);
        if(j < this.qr_size - 1) System.out.print(",");
      }
      System.out.println("]");
    }
    System.out.println("]");
  }

  public int getBit(int i, int j) {
      return this.qr_data[i][j];
  }

  public void invertBits(int[][] pos) {
      for(int p = 0; p < pos.length; p++) {
        this.qr_data[pos[p][0]][pos[p][1]] = (this.qr_data[pos[p][0]][pos[p][1]] + 1) % 2;
      }
  }

  public void setBit(int[][] pos, int value){
    for(int p = 0; p < pos.length; p++) {
      this.qr_data[pos[p][0]][pos[p][1]] = value;
    }
  }

  /* ------------ TABLES DE CONVERSION ------------- */

  private void initInt2AlphaNum() {

      this.Int2AlphaNum =  new HashMap<Integer, String>();

      this.Int2AlphaNum.put(0, "0");
      this.Int2AlphaNum.put(1, "1");
      this.Int2AlphaNum.put(2, "2");
      this.Int2AlphaNum.put(3, "3");
      this.Int2AlphaNum.put(4, "4");
      this.Int2AlphaNum.put(5, "5");
      this.Int2AlphaNum.put(6, "6");
      this.Int2AlphaNum.put(7, "7");
      this.Int2AlphaNum.put(8, "8");
      this.Int2AlphaNum.put(9, "9");
      this.Int2AlphaNum.put(10, "A");
      this.Int2AlphaNum.put(11, "B");
      this.Int2AlphaNum.put(12, "C");
      this.Int2AlphaNum.put(13, "D");
      this.Int2AlphaNum.put(14, "E");
      this.Int2AlphaNum.put(15, "F");
      this.Int2AlphaNum.put(16, "G");
      this.Int2AlphaNum.put(17, "H");
      this.Int2AlphaNum.put(18, "I");
      this.Int2AlphaNum.put(19, "J");
      this.Int2AlphaNum.put(20, "K");
      this.Int2AlphaNum.put(21, "L");
      this.Int2AlphaNum.put(22, "M");
      this.Int2AlphaNum.put(23, "N");
      this.Int2AlphaNum.put(24, "O");
      this.Int2AlphaNum.put(25, "P");
      this.Int2AlphaNum.put(26, "Q");
      this.Int2AlphaNum.put(27, "R");
      this.Int2AlphaNum.put(28, "S");
      this.Int2AlphaNum.put(29, "T");
      this.Int2AlphaNum.put(30, "U");
      this.Int2AlphaNum.put(31, "V");
      this.Int2AlphaNum.put(32, "W");
      this.Int2AlphaNum.put(33, "X");
      this.Int2AlphaNum.put(34, "Y");
      this.Int2AlphaNum.put(35, "Z");
      this.Int2AlphaNum.put(36, " ");
      this.Int2AlphaNum.put(37, "$");
      this.Int2AlphaNum.put(38, "%");
      this.Int2AlphaNum.put(39, "*");
      this.Int2AlphaNum.put(40, "+");
      this.Int2AlphaNum.put(41, "-");
      this.Int2AlphaNum.put(42, ".");
      this.Int2AlphaNum.put(43, "/");
      this.Int2AlphaNum.put(44, ":");
  }

}
