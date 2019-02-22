package com.example.qrcode;

import java.util.HashMap;

public class QrRead {

    /* ------------ VARIABLES -------------------- */

    private int[][] qr_data ;
    private int[][] qr_data_unmask;

    private int qr_size = 21;
    private int qr_formatbits_size = 15;

    private HashMap<Integer, String> Int2AlphaNum;

    /* ------------ CONSTRUCTEURS ---------------- */

    public QrRead() {	// créer un qr code de valeurs prédéfinies (article wiki)

        this.qr_data = new int[][] {{1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1},
                {1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1},
                {1, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1},
                {0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 0},
                {0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1},
                {0, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1},
                {1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0},
                {1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0},
                {1, 0, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 0},
                {1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0},
                {1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0},
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0}};
        this.qr_data_unmask = new int[qr_size][qr_size];
        this.qr_formatbits_size = 15;
        this.qr_size = 21;
        this.initInt2AlphaNum();
    }


    public QrRead(int size, int[][] qrcode_table) {	// créer un QRcode vide carré de taille size initialisé à 0

        this.qr_size = size;
        this.qr_data = new int[size][size];
        this.qr_data_unmask = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.qr_data[i][j] = qrcode_table[i][j];
                this.qr_data_unmask[i][j] = 0;
            }
        }

    }

    /* ------------ METHODES --------------------- */

    public int[] getFormatBits() {		// Récupère les deux mots de format, applique le masque et renvoie deux entiers

        /* --- Variables --- */
        // emplacement des bits de format pour un QRcode 21x21
        int[][] formatbits_location = new int[][] {{20, 8}, {19, 8}, {18, 8}, {17, 8}, {16, 8}, {15, 8}, {14,8},
                {8, 8}, {7, 8}, {5, 8}, {4, 8}, {3, 8}, {2, 8}, {1, 8}, {0, 8}};
        int[][] formatbits_location_bis = new int[][] {{8, 0}, {8, 1}, {8, 2}, {8, 3}, {8, 4}, {8, 5}, {8, 7},
                {8, 13}, {8, 14}, {8, 15}, {8, 16}, {8, 17}, {8, 18}, {8, 19}, {8, 20}};

        // masque des bits de format
        int formatbits_mask = 21522;

        // variables internes
        String temp = "";
        int[] formatbits = new int[2];
        int[] pos;

        /* --- Récupération des bits de format sur la colonne --- */
        for (int i = 0; i < this.qr_formatbits_size; i++) {

            pos = formatbits_location[i];															// récupération des indices
            temp += Integer.toString(this.qr_data[pos[0]][pos[1]]);  	// récupération du bit et ajout au nombre binaire																	// stocké en string
        }
        formatbits[0] = Integer.parseInt(temp, 2);				      	// ajout à la variable de retour
        formatbits[0] = formatbits[0] ^ formatbits_mask;		      // application du masque des bits de format
        temp = "";																								// remise à zéro de la variable temporaire

        /* --- Récupération des bits de format sur la ligne --- */
        for (int i = 0; i < this.qr_formatbits_size; i++) {

            pos = formatbits_location_bis[i];
            temp += Integer.toString(this.qr_data[pos[0]][pos[1]]);
        }
        formatbits[1] = Integer.parseInt(temp, 2);				       			// ajout à la variable de retour en deuxième position
        formatbits[1] = formatbits[1] ^ formatbits_mask;	          	// application du masque des bits de format

        return formatbits;

    }

    private int getMask(int formatbits) {						// Renvoie le numéro correspodant au masque dans les bits de format
        int mask;
        int mask_maskbits = 7168; 									// masque pour les bits en position 2, 3 et 4 (sur 15 en partant des poids forts)

        mask = (formatbits & mask_maskbits) >> 10;	// récupération des bits {2, 3, 4 }

        return mask;
    }

    public int getCorrectionValue(int formatbits) { // Renvoie le nombres d'octets de redondance
        int correctionValue = 0;
        int correctionLevel = formatbits >> 13;

        switch(correctionLevel) {
            case 1 :
                correctionValue = 7;
                break;
            case 0 :
                correctionValue = 10;
                break;
            case 3 :
                correctionValue = 13;
                break;
            case 2 :
                correctionValue = 17;
                break;
        }
        return correctionValue;
    }

    public void unmaskData(int formatbits) {		    // Applique le masque décrit dans les bits de format au bit de données écrit dans qr_data_unmask
        int mask;
        int mask_value;
        mask = this.getMask(formatbits); // récupérer le masque

        for (int i = 0; i < this.qr_size; i++) {					                   			 // parcourir les bits
            for (int j = 0; j < this.qr_size; j++) {
                mask_value = getMaskValue(mask,i,j);
                this.qr_data_unmask[i][j] = this.qr_data[i][j] ^ mask_value; 		   // ou exclusif avec le masque
            }
        }
    }

    private int getMaskValue(int mask, int i, int j) {	 			// Renvoie la valeur du bit du masque mask à la position i,j
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

    private String getDataUp(int i, int j, int nb_lines) {		// Récupère nb_lines du QR code à partir du coin inf droit (i,j) en montant
        String temp = "";

        for (int y = 0; y < nb_lines; y++) {
            temp += Integer.toString(this.qr_data_unmask[i - y][j]);
            temp += Integer.toString(this.qr_data_unmask[i - y][j - 1]);
        }
        return temp;
    }

    private String getDataDown(int i, int j, int nb_lines) {	// Récupère nb_line du QR code à partir du coin sup droit (i,j) en descendant
        String temp = "";

        for (int y = 0; y < nb_lines; y++) {
            temp += Integer.toString(this.qr_data_unmask[i + y][j]);
            temp += Integer.toString(this.qr_data_unmask[i + y][j - 1]);
        }
        return temp;
    }

    private String getQRData() {			// Renvoie une String de tous les bits

        /* --- Variables --- */
        String data = "";

        // positions de départ hardcodées (i,j), sens de parcours nombres de lignes (0:down, 1:up),
        // nombres de lignes à lire (nb_lines)
        int[][] start_bits_list = new int[][] {{20, 20, 1, 12}, {9, 18, 0, 12}, {20, 16, 1, 12}, {9, 14, 0, 12},
                {20, 12, 1, 14}, {5, 12, 1, 6}, {0, 10, 0, 6}, {7, 10, 0, 14},
                {12, 8, 1, 4}, {9, 5, 0, 4}, {12, 3, 1, 4}, {9, 1, 0, 4}};
        int[] start_bit;

        /* --- Traitement --- */
        for (int l = 0; l < start_bits_list.length; l++) {
            start_bit = start_bits_list[l];

            if (start_bit[2] == 1) { // sens de parcours : UP
                data += this.getDataUp(start_bit[0], start_bit[1], start_bit[3]);
            }
            else {					 // sens de parcours : DOWN
                data += this.getDataDown(start_bit[0], start_bit[1], start_bit[3]);
            }
        }

        return data;
    }

    public int[] getQRBytes() { 		  // Renvoie un tableau 1 octet = 1 nombre hexa

        /* ---  Variables --- */
        String data = this.getQRData();											// Récupère une chaîne de caractère contenant tous les bits
        int[] QRbytes = new int[data.length() / 8];					// Init le tableau d'hexa à renvoyer
        String octet = "";																	// Var temporaire

        /* ---  Traitement --- */
        for (int i = 0; i < data.length(); i = i + 8) {	// Parcours octet par octet
            for(int j = 0; j < 8; j ++) {
                octet += data.charAt(i + j); 			// Concaténation des bits
            }
            // QRbytes[(int) (i / 8)] = Integer.toString(Integer.parseInt(octet, 2),16); // Convertion du binaire en hexa
            QRbytes[i / 8] = Integer.parseInt(octet, 2); // Convertion du binaire en int
            octet = "";											 						 // RAZ de la var temp
        }
        return QRbytes;
    }

    public String getQRMessage(int[] bytesList, int formatbits) {							// Renvoie le message décodé
        /* --- Variables  --- */
        String msg ="";
        String bitList = bytesList2BinaryString(bytesList);
        int ind = 0;
        int mode;
        int[] modeValues = new int[2];
        int nbCharInMode;
        int valueChar;
        int nbCharTotal = 26 - this.getCorrectionValue(formatbits);


        for(int x = 0; x < nbCharTotal;) {

            /* Récupération des 4 premiers bits de mode */
            mode = Integer.parseInt(bitList.substring(ind, ind + 4),2);
            modeValues = getModeValues(mode);
            if(modeValues[0] == 0)  break;	// si on a le caractère de fin de données on sort
            ind = ind + 4;

            /* Récupération du nombre de caractères dans le mode donné */
            nbCharInMode = Integer.parseInt(bitList.substring(ind, ind + modeValues[1]), 2);
            ind += modeValues[1];

            /* Récupération des caractères */
            for(int i = 0; i < nbCharInMode; i++) {
                valueChar = Integer.parseInt(bitList.substring(ind, ind + modeValues[0]),2);
                msg += getNextChar(mode, valueChar);
                x = x + 1;
                ind += modeValues[0];
            }
        }

        return msg;
    }

    private String getNextChar(int mode, int valueChar) { 		// Renvoie le charactère codé selon mode dans bitList
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

    private String bit2NumericString(int valueChar) { 				// Renvoie un nombre entier sous forme de String
        return Integer.toString(valueChar);
    }

    private String bit2AlphaNumString(int valueChar) { 				// Renvoie 2 caractères Alphanumériques
        String AlphaNumString;
        int a;
        int b;

        b = valueChar % 45;
        a = (valueChar - b) / 45 ;

        AlphaNumString = this.Int2AlphaNum.get(a) + this.Int2AlphaNum.get(b);

        return AlphaNumString;
    }

    private String bit2ByteString(int valueChar) {						// Renvoie un caractère ASCII
        return Character.toString((char) valueChar);
    }

    private String bit2KanjiString(int valueChar) { 					//TODO
        return "Kanji";
    }

    private int[] getModeValues(int mode) { 									// Renvoie les paramètres propres à un modes de codage
        int[] modeValues = new int[2]; // [0] 0: fin des données, 10: Numeric, 11: Alphanumeric,
        //     8: Byte, 13: Kanji
        // [1] longueur du code du nb de données codées dans le mode
        switch(mode) {
            case 0:
                modeValues[0] = 0;
                modeValues[1] = 0;
                break;
            case 1:
                modeValues[0] = 10;
                modeValues[1] = 10;
                break;
            case 2:
                modeValues[0] = 11;
                modeValues[1] = 9;
                break;
            case 4:
                modeValues[0] = 8;
                modeValues[1] = 8;
                break;
            case 8:
                modeValues[0] = 13;
                modeValues[1] = 8;
                break;
        }
        return modeValues;
    }

    private String bytesList2BinaryString(int[] bytesList) {	// Renvoie la liste de bits correspondant à une suite d'entier
        String bitList = "";
        for (int i = 0; i < bytesList.length; i++) {
            bitList += Integer.toBinaryString(0x100| bytesList[i] ).substring(1); // Zero padding sur 8 bit
        }
        return bitList;
    }

    /* ------------ GETTERS / SETTERS ---------------- */

    public int[][] getQRcode() {
        return this.qr_data;
    }

    public int getBit(int i, int j) {
        return this.qr_data[i][j];
    }

    public void invertBit(int i, int j) {
        this.qr_data[i][j] = (this.qr_data[i][j] + 1) % 2;
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
