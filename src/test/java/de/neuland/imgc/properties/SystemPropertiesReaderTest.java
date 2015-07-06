package de.neuland.imgc.properties;

import de.neuland.imgc.properties.SystemPropertiesReader;
import org.junit.Test;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNotNull;

public class SystemPropertiesReaderTest {

    @Test
    public void readImgCLogFileNameTest() {
        // when
        final String r = SystemPropertiesReader.readImgCLogFileName();
        // then
        assertNotNull(r);
        assertNotSame(r,"");
    }

    @Test
    public void readImgCReportFilesNameTest() {
        // when
        final String r = SystemPropertiesReader.readImgCReportFilesName();
        // then
        assertNotNull(r);
        assertNotSame(r,"");
    }

    @Test
    public void readImgCCacheFileDefaultTest() {
        // when
        final String r = SystemPropertiesReader.readImgCCacheFileDefault();
        // then
        assertNotNull(r);
        assertNotSame(r,"");
    }

    @Test
    public void readImgCIgnorePathDefaultTest() {
        // when
        final String r = SystemPropertiesReader.readImgCIgnorePathDefault();
        // then
        assertNotNull(r);
        assertNotSame(r,"");
    }

    @Test
    public void readImgCConfigurationFileArgsKeyTest() {
        // when
        final String r = SystemPropertiesReader.readImgCConfigurationFileArgsKey();
        // then
        assertNotNull(r);
        assertNotSame(r,"");
    }

    @Test
    public void readImgCCacheFileArgsKeyTest() {
        // when
        final String r = SystemPropertiesReader.readImgCCacheFileArgsKey();
        // then
        assertNotNull(r);
        assertNotSame(r,"");
    }

    @Test
    public void readImgCIgnorePathArgsKeyTest() {
        // when
        final String r = SystemPropertiesReader.readImgCIgnorePathArgsKey();
        // then
        assertNotNull(r);
        assertNotSame(r,"");
    }

    @Test
    public void readImgCReportFileNameArgsKeyTest() {
        // when
        final String r = SystemPropertiesReader.readImgCReportFileNameArgsKey();
        // then
        assertNotNull(r);
        assertNotSame(r,"");
    }
}
