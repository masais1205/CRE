
package cre.algorithm.crcs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author turinglife
 */
public class PreprocessingLogic {

    public String namebase;
    private File nf;
    private Scanner scan;
    private String scannedLine;
    private String storeName = "";
    private char delimiter;
    private String fileName;            // zx, specify the file path and name(xxx/*.data, *.names)
    private String[] className;         // zx, store class name from names file
    private int maxItem;                // zx, index of each attribute value which numbers from 1.
    private int maxClass;               // zx, this variable specifies the total amount of class in the names file. 
    private String[] attName;           // zx, store all attribute name from names file whether it is ignore or not.
    private String[] specialStatus;     // zx, this array indicates the specific attribute whether it is ignore or continuous.
    private AttributeCode[] attCode;    // zx, It's a adjacency list data structure

    private ItemRecord[] itemRecord;    // zx, it's an array that contains items(attribute name + attribute value) 
    // zx, it's initial length is item_Id_Ceiling = 50.
    private int item_Id_Ceiling = 50;
    private int[][] attValue;           // zx, store index of attribute values under an attribute which don't contain the ignored attribute. 
    // zx, numbered from 1.

    private int[] attribute;            // zx, store indexes of attribute name of an item. 
    // zx, notice: attribute[1] as the first position to store
    private int maxAtt;                 // zx, it specifies total amount of attributes whether it is ignore or not.
    private int realAtt;                // zx, the amount of real attributes, which means it doesn't contain the ignored attributes.
    private int[] maxAttVal;            // zx, 
    private int maxData;                // zx, max line number of data files

    final int IGNORE = 1;               // special attribute status: do not use
    final int DISCRETE = 2;             // Discrete: collect values as data read
    final int CONTINUOUS = 3;           // continuous attribute

    double[][] rawDataSpace;
    int[][] dataSpace;

    public class AttributeCode {
        String attr;          // zx, the value range of specific attribute
        int itemID;           // zx, serial number of scope
        AttributeCode next;   // zx, a pointer to the next object of AttributeCode
    }

    public class ItemRecord {
        public String attr;          // zx, the value range of specific attribute
        public String attName;       // zx, attribute name
    }

    public class retclass {
        public String storeName;
        //public String filetrueName;
        public String namebase;
        public File nf;
        public Scanner scan;
        public String scannedLine;
        public String fileName;
        public String[] className;
        public int maxItem;
        public int maxClass;
        public String[] attName;
        /**
         * zx, this array indicates the specific attribute whether it is ignore or continuous.
         */
        public String[] specialStatus;
        public ItemRecord[] itemRecord;
        public int item_Id_Ceiling = 50;
        public int[][] attValue; // to store attribute values under an attribute   # for debug, this attribute won't be return to MainUI.
        public int[] attribute; // to store attribute name an item belongs
        /**
         * zx, it specifies total amount of attributes whether it is ignore or not.
         */
        public int maxAtt;

        /**
         * zx, the amount of real attributes, which means it doesn't contain the ignored attributes.
         */
        public int realAtt;
        public int[] maxAttVal;
        public int maxData;

        public double[][] rawDataSpace;
        public int[][] dataSpace;
    }

    public retclass rc = new retclass();


    /**
     * Yizhao Han
     * <p>
     * Only read .names file to get attributes.
     *
     * @param nameFile the name file
     */
    public void onlyGetNames(File nameFile) throws Exception {
        String f = nameFile.getAbsolutePath();
        f = f.substring(0, f.length() - 6);
        this.fileName = f;
        this.namebase = f;
        getNames(null);
        copyRC();
    }

    private void copyRC() {
        rc.attName = attName;
        rc.attValue = attValue;
        rc.attribute = attribute;
        rc.className = className;
        rc.dataSpace = dataSpace;
        rc.fileName = fileName;
        rc.itemRecord = itemRecord;
        rc.item_Id_Ceiling = item_Id_Ceiling;
        rc.maxAtt = maxAtt;
        rc.maxAttVal = maxAttVal;
        rc.maxClass = maxClass;
        rc.maxData = maxData;
        rc.maxItem = maxItem;
        rc.namebase = namebase;
        rc.nf = nf;
        rc.rawDataSpace = rawDataSpace;
        rc.realAtt = realAtt;
        rc.scan = scan;
        rc.scannedLine = scannedLine;
        rc.specialStatus = specialStatus;
    }

    public void loadData(String nameFileContent, File dataFile) throws Exception {
        String f = dataFile.getAbsolutePath();
        f = f.substring(0, f.length() - 5);
        this.fileName = f;
        this.namebase = f;
        getNames(nameFileContent);
        getData(".data", dataFile);
        copyRC();
    }

    public void loadData(String filename) throws Exception {
        System.out.println("---------filename--------");
        System.out.println(filename);

        //filetrueName = filename;
        fileName = filename;
        namebase = filename;


        getNames(null);


        // read data

        getData(".data", null);

        copyRC();
        
        /*
        System.out.println("---------attCode[]-----------");
        for(int i = 0; i < attCode.length; i++)
        {
            //System.out.print(itemRecord[i].attName + " | " + itemRecord[i].attr + ": ");
            
            for(AttributeCode p = attCode[i]; p != null; )
            {   
                if(p != null)
                {
                    System.out.print("(" + p.attr + ", " + p.itemID + ")");
                    p = p.next;

                    if(p != null)
                    {
                        System.out.print(" " + "->" + " ");
                    }
                }
                else
                {
                    System.out.println("empty attCode[i], index is: " + i);
                    break;
                }
            }
            
            System.out.println();
        }
        
        System.out.println("---------itemRecord[]---------");
        for(int i = 0; i < 50; i++)
        {
            System.out.println(itemRecord[i].attName + " " + itemRecord[i].attr);
        }  
        
        
        
        System.out.println("---------attribute---------");
        for(int i = 0; i < attribute.length; i++)
        {
            System.out.print(attribute[i] + " ");
        }
        System.out.println();
        
        System.out.println("---------maxClass---------");
        System.out.println(maxClass);
        
        System.out.println("---------maxAtt---------");
        System.out.println(maxAtt);
        
        System.out.println("---------realAtt---------");
        System.out.println(realAtt);
        
        System.out.println("---------specialStatus----------");
        for(int i = 0; i < specialStatus.length; i++)
        {
            System.out.println(i + ": " + specialStatus[i]);
        }
        
        System.out.println("-----------attName-----------");
        for(int i = 0; i < attName.length; i++)
        {
            System.out.println(i + ": " + attName[i]);
        }
        
        
        
        System.out.println("-----------rawDataSpace------------");
        for(int i = 0; i < maxData; i++)
        {
            for(int j = 0; j < (realAtt + 1); j++)
            {
                System.out.print(rawDataSpace[i][j] + " ");
            }    
            System.out.println();
        }
        
        
        System.out.println("-----------dataSpace------------");
        for(int i = 0; i < maxData; i++)
        {
            for(int j = 0; j < (realAtt + 1); j++)
            {
                System.out.print(dataSpace[i][j] + " ");
            }    
            System.out.println();
        }
        */


    }

    public retclass getData() {
        return rc;
    }
    
    /*
    @comment by zx
    
    function: read names file, line by line.ss
    It only can return the first class name from names file and the first attribute
    from names file.
    */

    public boolean readName() {
        try {
            if (scan.hasNextLine()) {
                scannedLine = scan.nextLine();
                // skip the blank lines, comment lines

                // zx begin
                //System.out.println("--------testing3-------");
                //System.out.println(scannedLine);
                //System.out.println("--------testing5-------");
                // zx end

                // zx, If it is a blank line or comment line, just skip it.
                while ((scannedLine.length() == 0)
                        || (scannedLine.length() > 0 & scannedLine.charAt(0) == '|')
                        || (scannedLine.length() > 0 & scannedLine.charAt(0) == ' ')) {
                    scannedLine = scan.nextLine();
                }

                storeName = "";

                /* Read in characters up to the next delimiter */
                int i = 0;

                for (; i < scannedLine.length() && scannedLine.charAt(i) != ':'
                        && scannedLine.charAt(i) != ','
                        && scannedLine.charAt(i) != '\n'
                        && scannedLine.charAt(i) != '|'; i++) {
                    //System.out.println("\t!! Rule - readName() - charAt:" +
                    //line.charAt(i) + ": equal?:" + (line.charAt(i) == ',') +
                    //":");
                    storeName += scannedLine.charAt(i);
                    //System.out.println("!! Rule - readName() - compiling name:"
                    // + storeName);
                }

                if (i < scannedLine.length()) {
                    // zx, at this time, charAt(i) points to the delimiter. 
                    delimiter = scannedLine.charAt(i);
                    //System.out.println("delimiter: "+delimiter);

                    // if after , is a space then skip the space
                    if (scannedLine.charAt(i + 1) != ' ') {
                        scannedLine = scannedLine.substring(storeName.length() + 1);
                    } else {
                        scannedLine = scannedLine.substring(storeName.length() + 2);
                    }

                    //System.out.println("scannedLine: "+scannedLine);
                    return true;
                } else {
                    delimiter = ' ';
                    return false;
                }
            } else {
                // System.out.println("!! Rule - readName() - END OF FILE FOUND");
                return false;
            }
        } catch (Exception e) {
            System.out.println("## ERROR -  Rule - readName() - error reading names from file");

            e.printStackTrace();
            return false;
        }

    }
    

    /*
    comment by Xin Zhu(zx)
    Yizhao Han Modifies.

    */

    public void getNames(String nameFileContent) throws FileNotFoundException {
        try {
            String fn;
            int v, k;

            // int att_ceiling = 50
            int att_ceiling = 1500, class_ceiling = 50, att_val_ceiling = 10;
            AttributeCode attptr;

            /* Open names file */
            fn = fileName;
            fn = fn + ".names";
            if (nameFileContent == null) {
                nf = new File(fn);
                scan = new Scanner(nf);
            } else {
                scan = new Scanner(nameFileContent);
            }


            /* Get class names from names file */
            className = new String[class_ceiling];
            maxItem = 0;
            /* Item=0 is for missing attribute */
            maxClass = -1;

            // zx, read the first class name from names file.
            // zx, in addition, it is also used to read the attribute name from names file.
            readName();

            if (++maxClass >= class_ceiling) {
                class_ceiling += 50;
                String[] tmpString = new String[class_ceiling];
                //System.out.println("class_ceiling="+class_ceiling);
                for (int i = 0; i < className.length; i++) {
                    tmpString[i] = className[i];
                }
                className = tmpString;
            }
            // zx, put the first class name from names file into the first position of array of className.
            className[maxClass] = storeName;
            //hs.test System.out.println("className[maxClass] ="+className[maxClass] ); 0

            // zx, put the remaining class name from names file into the remaining positions of array of className.
            while (delimiter == ',') {
                storeName = "";
                int i = 0;
                for (; i < scannedLine.length() && scannedLine.charAt(i) != '.'
                        && scannedLine.charAt(i) != ':'
                        && scannedLine.charAt(i) != ','
                        && scannedLine.charAt(i) != '\n'
                        && scannedLine.charAt(i) != '|'; i++) {
                    storeName += scannedLine.charAt(i);
                }

                if (++maxClass >= class_ceiling) {
                    class_ceiling += 50;
                    String[] tmpString = new String[class_ceiling];
                    //System.out.println("className.length="+className.length);
                    for (int j = 0; j < className.length; j++) {
                        tmpString[j] = className[j];
                    }
                    className = tmpString;

                }
                delimiter = scannedLine.charAt(i);
                //System.out.println("delimiter:"+delimiter);
                className[maxClass] = storeName;
            }
            //hs.test System.out.println("className[maxClass] ="+className[maxClass] ); 1

            // beajy003 - remove blank line if there is a blank line
            // Thuc: this has been done in readNames
            // scannedLine = scan.nextLine();

            /* Get attribute and attribute value names from names file */

            /*
            zx
            @attName: storing all attributes from names files whether it is ignore or not.
            @specialStatus
            @attCode
            @itemRecord
            @attribute
            @attValue
            @maxAttVal

            */

            attName = new String[att_ceiling];
            specialStatus = new String[att_ceiling];
            attCode = new AttributeCode[att_ceiling];
            itemRecord = new ItemRecord[item_Id_Ceiling];

            if (attName == null || specialStatus == null || attCode == null || itemRecord == null) {
                errorOut(1);
            }

            attribute = new int[item_Id_Ceiling];
            attValue = new int[att_ceiling][];
            maxAttVal = new int[att_ceiling];

            maxAtt = -1;    // zx, it specifies total amount of attributes whether it is ignore or not.
            realAtt = -1;   // zx, the amount of real attributes, which means it doesn't contain the ignored attributes.
            /* unknow attribute put in AttCode[0] */
            itemRecord[0] = addItemRecord("?", "unknown attribute");
            // AttCode[++MaxAtt] = AddAttCode("?", ++MaxItem);

            while (readName()) {
                // zx, in this case, readName() has read the attribute name and delimiter has got the colon following the attribute name.
                // zx, colon always follows each of attributes. so, if the delimiter is not colon, it's definitely wrong.
                if (delimiter != ':') {
                    error(1, storeName, "");
                }

                if (++maxAtt >= att_ceiling - 1) {
                    att_ceiling += 50;
                    attName = new String[att_ceiling];
                    specialStatus = new String[att_ceiling];
                    attCode = new AttributeCode[att_ceiling];
                    if (attName == null || specialStatus == null
                            || attCode == null) {
                        errorOut(1);
                    }

                    attValue = new int[att_ceiling][];
                    maxAttVal = new int[att_ceiling];
                }

                // zx, the first attribute name will be written into the first position of array of attName.
                attName[maxAtt] = storeName;
                //System.out.println("maxAtt"+maxAtt);
                //System.out.println("attName[maxAtt] ="+attName[maxAtt]);

                specialStatus[maxAtt] = "";
                attptr = attCode[maxAtt] = null;
                realAtt++;

                maxAttVal[realAtt] = 0;
                //System.out.println(" realAtt="+ realAtt);

                attValue[realAtt] = new int[att_val_ceiling];


                /*

                zx, attCode is an adjacency list data structure.

                sex: (M, 1) -> (F, 2)
                on_thyroxine: (f, 3) -> (t, 4)
                ...

                the following code's aims to read the attribute value from behind colon

                */

                do {
                    storeName = "";
                    int i = 0;
                    for (; i < scannedLine.length()
                            && !(scannedLine.charAt(i) == '.' && scannedLine.length() == i)
                            && scannedLine.charAt(i) != ':'
                            && scannedLine.charAt(i) != ','
                            && scannedLine.charAt(i) != '|'; i++) {
                        storeName += scannedLine.charAt(i);
                    }
                    if (i == scannedLine.length()) {
                        delimiter = ' ';
                    } else {
                        delimiter = scannedLine.charAt(i);
                    }

                    try {
                        // Thuc add to handle ,or , or.
                        if (scannedLine.charAt(i + 1) == ' ') {
                            scannedLine = scannedLine.substring(storeName.length() + 2);
                        } else {
                            scannedLine = scannedLine.substring(storeName.length() + 1);
                        }
                    } catch (Exception ex) {
                        storeName = storeName.substring(0,
                                storeName.length() - 1);
                        delimiter = '.';
                    }

                    if (attptr == null) {
                        if (storeName.compareTo("continuous") == 0) {
                            specialStatus[maxAtt] = "" + CONTINUOUS;
                            System.out.println("I do not process continuous attribute, discretize it first.");
                            System.exit(0);
                        }
                        if (storeName.compareTo("discrete") == 0) {
                            specialStatus[maxAtt] = "" + DISCRETE;
                            System.out.println("I find the discrete value in " + fileName + ".names");
                            System.exit(0);
                        }
                        if (storeName.compareTo("ignore") == 0
                                || storeName.compareTo("ignore. ") == 0
                                || storeName.compareTo("ignore ") == 0) {
                            specialStatus[maxAtt] = "" + IGNORE;
                            System.out.println("storeName=" + storeName);

                            //free(attValue[realAtt]);
                            realAtt--;
                            break;
                        }

                        attCode[maxAtt] = addAttCode(storeName, ++maxItem);
                        attptr = attCode[maxAtt];
                    } else {
                        attptr.next = addAttCode(storeName, ++maxItem);
                        attptr = attptr.next;
                    }

                    k = maxAttVal[realAtt];
                    //System.out.println(k);
                    maxAttVal[realAtt]++;
                    attValue[realAtt][k] = maxItem;
                    attribute[maxItem] = realAtt;
                    //System.out.println("RealAtt ="+realAtt+"MaxAttVal ="+maxAttVal[realAtt]+"MaxItem ="+maxItem );

                    //printf("%s,RealAtt = %d, MaxAttVal = %d, MaxItem = %d \n", realAtt, maxAttVal[realAtt], maxItem);
                    if (maxAttVal[realAtt] >= att_val_ceiling - 1) {
                        att_val_ceiling += 10;
                        int[] tmpInt = new int[att_val_ceiling];
                        for (int j = 0; j < attValue[realAtt].length; j++) {
                            tmpInt[j] = attValue[realAtt][j];
                        }
                        attValue[realAtt] = tmpInt;
                    }

                    if (maxItem >= item_Id_Ceiling - 1) {
                        item_Id_Ceiling += 50;

                        ItemRecord[] tmpStore = new ItemRecord[item_Id_Ceiling];
                        for (int j = 0; j < itemRecord.length; j++) {
                            tmpStore[j] = itemRecord[j];
                        }
                        itemRecord = tmpStore;

                        int[] tmpInt = new int[item_Id_Ceiling];
                        for (int j = 0; j < attribute.length; j++) {
                            tmpInt[j] = attribute[j];
                        }

                        attribute = tmpInt;
                    }
                    itemRecord[maxItem] = addItemRecord(attName[maxAtt], storeName);
                    specialStatus[maxAtt] = "" + 0;
                } while (delimiter == ',');
            }
            maxClass++;
            maxAtt++;
            realAtt++;
            // maxItem ++;
            scan.close();

            // zx begin

            /*
            System.out.println("----------------attribute----------------");

            for (int i = 0; i < 100; i++)
            {
                System.out.println(attribute[i]);
            }
            */
            //System.out.println(attValue);
            // zx end
        } catch (Exception e) {
            System.out.println("## ERROR - Rule - getNames() - Error reading names from file");
            throw e;
        }
    }

    /**
     * Method by beajy003 to read the data values from the data file. This was
     * origionally done by the readName() method in c. However it is easier if I
     * create a new method.
     *
     * @return True if there was data read
     * @author beajy003
     */
    public boolean readData() {
        // System.out.println("!! Rule - readData() - Begin - line:"+scannedLine+": name:"+storeName+": del:"+delimiter+":");
        // remove comments - Thuc
        if (delimiter == '|') {
            delimiter = '.';
            scannedLine = scan.nextLine();
        }

        if (delimiter == '.') { // beajy003 read next line
            char firstChar = '|';
            while (firstChar == '|') {
                if (!scan.hasNextLine()) {
                    return false;
                }
                scannedLine = scan.nextLine();
                try {
                    firstChar = scannedLine.charAt(0);
                } catch (Exception e) {
                    firstChar = '|';
                }
            }
        }
        storeName = "";
        int i = 0;
        for (; i < scannedLine.length()
                && scannedLine.charAt(i) != ','
                && !(scannedLine.charAt(i) == '.' && (i + 1 == scannedLine
                .length())); i++) {
            // System.out.println("\t!! Rule - readName() - charAt:" +
            // line.charAt(i) + ": equal?:" + (line.charAt(i) == ',') + ":");
            storeName += scannedLine.charAt(i);
            //System.out.println("!! Rule - readName() - compiling name:" +
            //storeName);
        }
        try {
            delimiter = scannedLine.charAt(i);
            // if there is a space after the , then skip the space
            if (scannedLine.charAt(i + 1) == ' ') {
                scannedLine = scannedLine.substring(storeName.length() + 2);
            } else {
                scannedLine = scannedLine.substring(storeName.length() + 1);
            }
        } catch (Exception ex) {
            delimiter = '.';
        }
        // System.out.println("!! Rule - readData() - END - line:"+scannedLine+": name:"+storeName+": del:"+delimiter+":");
        return true;
    }


    /**
     * **********************************************************************
     */
    /*									 */
    /* Read raw case descriptions from file with given extension. */
    /* On completion, the description store in the int **item */
    /* item :{val1, val2.......valn, object} */
    /*									 */
    /* Read a raw case description from file df. */
    /*									 */
    /* missing value (?) or the unknown value put in AttCode[0] */
    /*									 */

    /**
     * **********************************************************************
     * Yizhao Han
     * <p>
     * parameter file is added.
     */
    public int getData(String extension, File file) {
        String fn = "";
        try {
            int att = 0, i, k, dataceil = 100, dat;
            int count;

            /* Open data file */
            fn = fileName;
            fn = fn + extension;
            if (file == null) {
                nf = new File(fn);
            } else {
                nf = file;
            }
            scan = new Scanner(nf);

            rawDataSpace = new double[dataceil][];
            maxData = 0;
            att = 0;
            k = -1;
            rawDataSpace[maxData] = new double[realAtt + 2];
            //hs.  System.out.println("realAtt"+realAtt);realAtt=49
            //System.out.println("realAtt"+realAtt);

            count = 0;

            while (readData()) {

                //System.out.println("Dataread: "+storeName+" - current count: "+count);
                if ((1 + count++) % (maxAtt + 1) == 0) { // beajy003 exclude 0
                    dat = getNameCode(storeName);
                    //System.out.println("dat="+dat);
                    //System.out.println("maxAtt="+maxAtt);

                    if (dat == -1) {
                        System.out.println("## ERROR - Rule - getData() - class name not found :" + className + ":");
                    }

                    rawDataSpace[maxData][att] = dat;
                    //System.out.println("maxData: "+maxData+"att: "+att+"rawDataSpace[maxData][att]="+rawDataSpace[maxData][att]);
                    //hs. this is take each row of target into rawDataSpace  
                    att = 0;
                    k = -1;
                    if (++maxData > dataceil - 1) {
                        dataceil += 100;
                        double[][] tmpSpare = new double[dataceil][];
                        for (int j = 0; j < rawDataSpace.length; j++) {
                            tmpSpare[j] = rawDataSpace[j];
                        }
                        rawDataSpace = tmpSpare;
                    }
                    rawDataSpace[maxData] = new double[realAtt + 2];
                    continue;
                }

                k++;
                if (specialStatus[k] == "" + IGNORE) {
                    // System.out.println("Skip the ignore ones");
                    continue;
                }

                if (specialStatus[k] == "" + CONTINUOUS) {
                    if (storeName.compareTo("?") == 0) {
                        rawDataSpace[maxData][att++] = -1;
                    } else {
                        rawDataSpace[maxData][att++] = Double.parseDouble(storeName);
                    }
                    continue;
                }
                dat = getAttCode(storeName, k);
                // System.out.println(dat);
                if (dat == -1) {
                    for (i = k + 1; i < maxAtt + 1; i++) {
                        readName();
                        count++;
                    }
                    att = 0;
                    k = -1;
                    maxData = -1;
                    continue;
                }
                rawDataSpace[maxData][att++] = dat;
                //System.out.println("rawDataSpace for att: "+rawDataSpace[maxData][att-1]);
            }
            // Thuc delete maxData++ Why needs maxData++?
            // maxData++;
            //System.out.println(maxData);
            scan.close();


            // This does not allow continuous attribute
            // Thuc print out rawDataSpace
            //for(int m=0;m<maxData;m++){
            //for(int n=0; n<realAtt+2;n++)
            //System.out.println(rawDataSpace[m][n]);
            // }
            convertDataSpace();

            return 1;
        } catch (Exception e) {
            System.out.println("## ERROR - Rule - getData() - Error reading data from file :" + fn + ": \n\n## ERROR - Program will exit.\n");
            e.printStackTrace();
            System.exit(1);
            return (0);
        }

    }

    public AttributeCode addAttCode(String att, int id) {
        AttributeCode tmp;

        tmp = new AttributeCode();
        tmp.itemID = id;
        tmp.attr = "";
        tmp.attr = att;
        tmp.next = null;
        return tmp;
    }

    public ItemRecord addItemRecord(String attname, String att) {
        ItemRecord tmp;
        //tmp = new crcs.ItemRecord();
        tmp = new ItemRecord();

        tmp.attName = "";
        tmp.attName = attname;

        tmp.attr = "";
        tmp.attr = att;

        return (tmp);
    }

    public int getNameCode(String nm) {
        int i;

        for (i = 0; i < maxClass; i++) {
            if (nm.compareTo(className[i]) == 0) {
                return i;
            }
        }
        return -1;
    }

    public int getAttCode(String nm, int attnum) {
        AttributeCode tmp;

        tmp = attCode[attnum];
        while (tmp != null) {
            if (nm.compareTo(tmp.attr) == 0) {
                return tmp.itemID;
            }
            tmp = tmp.next;
        }
        return 0;
    }

    /*
     * // This old function is for convert dataspace after discretization // It
     * cannot ussed in a data base there is missing value int ConvertDataSpace()
     * { int i, j, k, temp[1000], index[1000], tmp;
     * 
     * DataSpace = (int **) calloc(MaxData, sizeof(int*)); if(!RawDataSpace)
     * ErrorOut(1);
     * 
     * /// To sort the cord in the order from small to big /// the index the
     * sequence for geting the code
     * 
     * for(i=0; i<RealAtt; i++) temp[i] = RawDataSpace[0][i]; for(i=0; i<1000;
     * i++) index[i] = i;
     * 
     * for(i=0; i<RealAtt; i++){ k=i; for(j=i+1; j<RealAtt; j++){
     * if(temp[j]<temp[k]) k=j; } if(k!=i){ // swap tmp = temp[k]; temp[k] =
     * temp[i]; temp[i] = tmp; tmp = index[k]; index[k] = index[i]; index[i] =
     * tmp; } }
     * 
     * 
     * for(i=0; i<MaxData; i++){ DataSpace[i] = (int *)calloc(RealAtt+1,
     * sizeof(int)); if(!DataSpace[i]) ErrorOut (1); for(j=0; j<RealAtt+1; j++)
     * DataSpace[i][j] = (int)RawDataSpace[i][index[j]]; free(RawDataSpace[i]);
     * } free(RawDataSpace); return(1); }
     */
    public int convertDataSpace() {
        int i, j, k, tmp;
        int[] temp = new int[1000];
        int[] index = new int[1000];

        dataSpace = new int[maxData][];

        for (i = 0; i < maxData; i++) {
            dataSpace[i] = new int[realAtt + 1];
            for (j = 0; j < realAtt + 1; j++) {
                dataSpace[i][j] = (int) rawDataSpace[i][j];
                //System.out.println(dataSpace[i][j]);
            }
            free(rawDataSpace[i]);
        }
        free(rawDataSpace);
        return (1);
    }

    void errorOut(int errnum) {
        switch (errnum) {
            case 0:
                System.out.println("Can not Open File");
                break;

            case 1:
                System.out.println("Not enough memory");
                break;

            case 11:
                System.out.println("error in Filling the buff");
                break;

            case 12:
                System.out.println("error in Reading Next Transaation");
                break;

        }
    }

    public void error(int n, String s1, String s2) {
        int messages = 0;

        System.out.println("ERROR:  ");
        switch (n) {
            case 0:
                System.out.println("cannot open file " + s1 + " " + s2);
                System.exit(1);

            case 1:
                System.out.println("colon expected after attribute name " + s1);
                break;

            case 2:
                System.out.println("unexpected eof while reading attribute " + s1);
                break;

            case 3:
                System.out.println("attribute" + s1 + " has only one value");
                break;

            case 4:
                System.out.println("case" + (maxData + 1) + "'s value of '" + s2 + "' for attribute" + s1 + " is illegal");
                break;

            case 5:
                System.out.println("case " + (maxData + 1) + "'s class of '" + s2 + "' is illegal");
        }

        if (++messages > 10) {
            System.out.println("Error limit exceeded\n");
            System.exit(1);
        }
    }

    public void free(double d) {
        d = 0;
    }

    public void free(Object o) {
        o = null;
    }


}
