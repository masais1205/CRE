package cre.algorithm.cdt;

import cre.Config.OtherConfig;
import cre.algorithm.AbstractAlgorithm;
import cre.algorithm.CanShowOutput;
import cre.algorithm.CanShowStatus;
import cre.view.ResizablePanel;
import cre.view.tree.Node;
import cre.view.tree.TreePanel;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by HanYizhao on 2017/4/7.
 */
public class CDTAlgorithm extends AbstractAlgorithm {

    public CDTConfig config;
    public Boolean shouldStop = false;

    public CDTAlgorithm(File filePath, CDTConfig oldConfig) {
        super(filePath);
        if (oldConfig == null) {
            config = new CDTConfig();
            config.setHeight(5);
            config.setPruned(true);
        } else {
            config = oldConfig;
        }
    }

    public CDTAlgorithm(File filePath) {
        this(filePath, null);
    }

    @Override
    public String getName() {
        return "CDT";
    }

    @Override
    public String getIntroduction() {
        return "NAME\n" +
                "cre.algorithm.CDT\n" +
                "\n" +
                "SYNOPSIS\n" +
                "Class for generating a causal decision tree. For more information, see\n" +
                "\n" +
                "J. Li, S. Ma, T. Le, L. Liu, J. Liu (2015). CDT: Programs for Causal Decision Tree (Coded by S. Ma). .\n";
    }

    @Override
    public Collection<String> getSupportLowerFileExtension() {
        Collection<String> s = super.getSupportLowerFileExtension();
        s.add("csv");
        return s;
    }

    @Override
    public Cloneable getConfiguration() {
        return config;
    }

    @Override
    public AbstractAlgorithm getCloneBecauseChangeOfFile(File newFile) {
        CDTAlgorithm a = new CDTAlgorithm(newFile, this.config);
        return a;
    }

    @Override
    public List<ResizablePanel> doAlgorithm(CanShowOutput outPutArea, CanShowStatus canShowStatus, OtherConfig otherConfig) {
        shouldStop = false;
        String fileName = filePath.getAbsolutePath();
        if (fileName.toLowerCase().endsWith(".csv")) {
            fileName = fileName.substring(0, fileName.length() - 4);
        }

        String[] attributes = null;
        int instancesCount = 0;
        outPutArea.showOutputString("Scheme: " + config.toString());
        outPutArea.showOutputString("File Name: " + fileName + "\n");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            String temp = br.readLine();
            attributes = temp.split(",");
            while ((temp = br.readLine()) != null) {
                if (temp.split(",").length == attributes.length) {
                    instancesCount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        outPutArea.showOutputString("Attributes:");
        for (String i : attributes) {
            outPutArea.showOutputString("\t" + i);
        }

        outPutArea.showOutputString("==== full training set ===");

        CDT nCDT = new CDT(config, fileName, outPutArea);
        List<ResizablePanel> result = new ArrayList<>();
        if (nCDT != null) {
            result.add(new TreePanel(nCDT.rootYizhao));
        }


        switch (otherConfig.getValidation()) {
            case CROSS_VALIDATION:
                break;
            case VALIDATION:
                break;
            case NONE:
                break;
        }
        //new CDT(config, fileName, outPutArea);
        return result;
    }

    @Override
    public void setShouldStop() {
        shouldStop = true;
    }

    @Override
    public Object clone() {
        CDTAlgorithm algorithm = null;
        try {
            algorithm = (CDTAlgorithm) super.clone();
            if (this.config != null) {
                algorithm.config = (CDTConfig) this.config.clone();
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return algorithm;
    }
}
