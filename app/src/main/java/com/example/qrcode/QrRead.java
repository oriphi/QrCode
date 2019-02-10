package com.example.qrcode;

public class QrRead {

    /* ------------ VARIABLES -------------------- */

    private int[][] qr_data = null;
    private int qr_size = 21;
    private int qr_formatbits_size = 15;

    /* ------------ CONSTRUCTEURS ---------------- */

    public QrRead() {	// créer un qr code de valeurs prédéfinies

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
        ;
        this.qr_formatbits_size = 15;
        this.qr_size = 21;
    }

    public QrRead(int size) {	// créer un QRcode vide carré de taille size initialisé à 0

        this.qr_size = size;
        this.qr_data = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.qr_data[i][j] = 0;
            }
        }

    }

    /* ------------ METHODES --------------------- */

    public int[] getFormatBits() {	// Récupère les deux mots de format, applique le masque et renvoie deux entiers

        /** Variables **/

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

        /** Récupération des bits de format sur la colonne **/
        for (int i = 0; i < this.qr_formatbits_size; i++) {

            int[] pos = new int[2];
            pos = formatbits_location[i];								// récupération des indices
            temp += Integer.toString(this.qr_data[pos[0]][pos[1]]);  	// récupération du bit et ajout au nombre binaire																	// stocké en string
        }
        formatbits[0] = Integer.parseInt(temp, 2);						// ajout à la variable de retour
        formatbits[0] = formatbits[0] ^ formatbits_mask;		        // application du masque des bits de format
        temp = "";														// remise à zéro de la variable temporaire

        /** Récupération des bits de format sur la ligne **/
        for (int i = 0; i < this.qr_formatbits_size; i++) {

            int[] pos = new int[2];
            pos = formatbits_location_bis[i];
            temp += Integer.toString(this.qr_data[pos[0]][pos[1]]);
        }
        formatbits[1] = Integer.parseInt(temp, 2);				// ajout à la variable de retour en deuxième position
        formatbits[1] = formatbits[1] ^ formatbits_mask;		// application du masque des bits de format

        return formatbits;

    }

    public int getMask() {		// Renvoie le numéro correspodant au masque dans les bits de format
        int mask;
        int formatbits;
        int mask_maskbits = 7168; 					// masque pour les bits en position 2, 3 et 4 (sur 15 en partant des poids forts)

        formatbits = this.getFormatBits()[0];		// récupération des bits de format
        mask = (formatbits & mask_maskbits) >> 10;	// récupération des bits {2, 3, 4 }

        return mask;
    }

    public void unmaskData() {	// Applique le masque décrit dans les bits de format au bit de données
        /** Variables **/
        int mask;
        int mask_value;
        mask = this.getMask(); // récupérer le masque

        for (int i = 0; i < this.qr_size; i++) {					  // parcourir les bits
            for (int j = 0; j < this.qr_size; j++) {
                mask_value = getMaskValue(mask,i,j);
                this.qr_data[i][j] = this.qr_data[i][j] ^ mask_value; // ou exclusif avec le masque
            }
        }
    }

    private int getMaskValue(int mask, int i, int j) {	 	// Renvoie la valeur du bit du masque mask à la position i,j
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

    public String getDataUp(int i, int j, int nb_lines) {	// Récupère nb_lines du QR code à partir du coin inf droit (i,j) en montant
        /** Variables **/
        String temp = "";


        for (int y = 0; y < nb_lines; y++) {
            temp += Integer.toString(this.qr_data[i - y][j]);
            temp += Integer.toString(this.qr_data[i - y][j - 1]);
        }
        return temp;
    }

    public String getDataDown(int i, int j, int nb_lines) {	// Récupère nb_line du QR code à partir du coin sup droit (i,j) en descendant
        /** Variables **/
        String temp = "";


        for (int y = 0; y < nb_lines; y++) {
            temp += Integer.toString(this.qr_data[i + y][j]);
            temp += Integer.toString(this.qr_data[i + y][j - 1]);
        }
        return temp;
    }

    public String getQRData() {	// Renvoie une String de tous les bits

        /** Variables **/
        String data = "";

        // positions de départ hardcodées (i,j), sens de parcours nombres de lignes (0:down, 1:up),
        // nombres de lignes à lire (nb_lines)
        int[][] start_bits_list = new int[][] {{20, 20, 1, 12}, {9, 18, 0, 12}, {20, 16, 1, 12}, {9, 14, 0, 12},
                {20, 12, 1, 14}, {5, 12, 1, 6}, {0, 10, 0, 6}, {7, 10, 0, 14},
                {12, 8, 1, 4}, {9, 5, 0, 4}, {12, 3, 1, 4}, {9, 1, 0, 4}};
        int[] start_bit = new int[4];

        /** Traitement **/
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

    public String[] getQRBytes() { // Renvoie un tableau 1 octet = 1 nombre hexa

        /** Variables **/
        String data = this.getQRData();								// Récupère une chaîne de caractère contenant tous les bits
        String[] QRbytes = new String[(int) (data.length() / 8)];	// Init le tableau d'hexa à renvoyer
        String octet = "";											// Var temporaire

        /** Traitement **/
        for (int i = 0; i < data.length(); i = i + 8) {	// Parcours octet par octet
            for(int j = 0; j < 8; j ++) {
                octet += data.charAt(i + j); 			// Concaténation des bits
            }
            QRbytes[(int) (i / 8)] = Integer.toString(Integer.parseInt(octet, 2),16); // Convertion du binaire en hexa
            octet = "";																            // RAZ de la var temp
        }
        return QRbytes;
    }

    /* ------------ GETTERS / SETTERS ---------------- */

    public int[][] getQRcode() {
        return this.qr_data;
    }

    public int getBit(int i, int j) {
        return this.qr_data[i][j];
    }




}

