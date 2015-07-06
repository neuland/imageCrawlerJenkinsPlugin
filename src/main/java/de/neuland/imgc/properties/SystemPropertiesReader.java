package de.neuland.imgc.properties;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Created by jannikbohling on 31.03.15.
 */
public class SystemPropertiesReader {

    private static final String settingsPropertiesFilename = "de.neuland.imgc.properties.Settings";
    private static ResourceBundle r = null;

    public static final String readImgCLogFileName(){return readSettingsProperty("imgC_log_file_name");}
    public static final String readImgCReportFilesName(){return readSettingsProperty("imgC_report_files_name");}

    public static final String readImgCCacheFileDefault(){return readSettingsProperty("imgC_cache_file_default");}
    public static final String readImgCIgnorePathDefault(){return readSettingsProperty("imgC_ignore_path_default");}

    public static final String readImgCConfigurationFileArgsKey(){return readSettingsProperty("imgC_configuration_file_args_key");}
    public static final String readImgCCacheFileArgsKey(){return readSettingsProperty("imgC_cache_file_args_key");}
    public static final String readImgCIgnorePathArgsKey(){return readSettingsProperty("imgC_ignore_path_args_key");}
    public static final String readImgCReportFileNameArgsKey(){return readSettingsProperty("imgC_report_file_name_args_key");}

    private static final String readSettingsProperty(String key){
        if(r == null) loadSettingProperties();
        return (r.containsKey(key)) ? r.getString(key) : "";
    }

    private static final void loadSettingProperties(){
        try {
            r = ResourceBundle.getBundle(settingsPropertiesFilename);
        } catch (MissingResourceException ex) {
            ex.printStackTrace();
            System.out.println("Can not load " + settingsPropertiesFilename + " : MissingResourceException");
        }
    }

}
