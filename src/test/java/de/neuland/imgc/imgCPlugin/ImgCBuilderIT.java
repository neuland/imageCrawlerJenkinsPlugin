package de.neuland.imgc.imgCPlugin;

import de.neuland.imgc.command.impl.CommandExecutor;
import de.neuland.imgc.command.impl.CommandExecutorFactory;
import de.neuland.imgc.imgCPlugin.ImgCBuilder;
import de.neuland.imgc.validator.impl.ImgCValidator;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ImgCBuilderIT {


    private final static String TEST_DATA_NAME = "example";
    private final static String IMG_C_NAME = "imageCrawler-1.1-SNAPSHOT.jar";
    private final static String ARCHIVE_DIR = "archive";
    private final static String EXAMPLE_CONFIG_NAME = "example_config.config";
    private final static String WORK_DIR = System.getProperty("user.dir");
    private final static String RESOURCE_DIR = WORK_DIR+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator+
            "de"+File.separator+"neuland"+File.separator+"imgc"+File.separator+"command"+File.separator+"impl"+File.separator;

    private final static Random random = new Random();

    private String testDirPath;
    private BuildListener blMock;
    private AbstractBuild abMock;
    private Launcher lMock;
    private ImgCValidator vSpy;
    private CommandExecutorFactory cefMock;
    private CommandExecutor ceSpy;

    @Before
    public void setup(){
        testDirPath = WORK_DIR+File.separator+"TestDir"+new Date().getTime();
        while(true) {if (createTestDir()) break;}

        vSpy = spy(new ImgCValidator());

        blMock = mock(BuildListener.class);
        when(blMock.getLogger()).thenReturn(System.out);

        abMock = mock(AbstractBuild.class);
        doNothing().when(abMock).setResult(any(Result.class));
        when(abMock.getArtifactsDir()).thenReturn(new File(testDirPath,ARCHIVE_DIR));
        when(abMock.getRootDir()).thenReturn(new File(testDirPath));

        lMock = mock(Launcher.class);

        cefMock = mock(CommandExecutorFactory.class);
        ceSpy = spy(new CommandExecutor(blMock, ARCHIVE_DIR));
        when(cefMock.createCommandExecutor(any(BuildListener.class),any(String.class))).thenReturn(ceSpy);;
    }

    @After
    public void cleanUp(){
        deleteFolder(new File(testDirPath));
    }

    @Test
    public void simpleImgCBuilderPerformIT() throws IOException {
        // given
        final String imgCPath = RESOURCE_DIR + File.separator + IMG_C_NAME;
        final String executeDirectory = testDirPath;
        final String configurationFile = RESOURCE_DIR+File.separator+EXAMPLE_CONFIG_NAME;
        final String cacheFile = testDirPath + File.separator + "c.cache";
        final String ignorePath = testDirPath;
        final String startingWarning = "9";
        final String startingFail = "10";
        final boolean useInternalIgnoreAndConfig = false;
        final ImgCBuilder icb = new ImgCBuilder(imgCPath, configurationFile, cacheFile,
                ignorePath, executeDirectory, startingWarning, startingFail,useInternalIgnoreAndConfig);
        icb.setIImgCValidator(vSpy);
        icb.setCommandExecutorFactory(cefMock);
        final String resultName = "out";

        // create given stuff
        copy(new File(RESOURCE_DIR+File.separator+EXAMPLE_CONFIG_NAME), new File(testDirPath+File.separator+EXAMPLE_CONFIG_NAME));
        copy(new File(RESOURCE_DIR+File.separator+TEST_DATA_NAME), new File(testDirPath+File.separator+TEST_DATA_NAME));

        // when
        final boolean result = icb.perform(abMock, lMock, blMock);

        // then
        assertTrue(result);
        final File cache = new File(cacheFile);
        assertTrue(cache.exists());
        assertTrue(cache.isFile());
        final File xmlResult = new File(testDirPath+File.separator+resultName+".xml");
        assertTrue(xmlResult.exists());
        assertTrue(xmlResult.isFile());
        final File htmlResult = new File(testDirPath+File.separator+resultName+".html");
        assertTrue(htmlResult.exists());
        assertTrue(htmlResult.isFile());

        verify(vSpy).isValidImgCPath(imgCPath);
        verify(vSpy).isValidExecuteDirectory(executeDirectory);

        verify(cefMock).createCommandExecutor(any(BuildListener.class),any(String.class));

        verify(ceSpy).copyImageCrawler(any(String.class), any(String.class));
        verify(ceSpy).runImgCrawler(any(String.class),any(String.class),any(List.class));
        verify(ceSpy,times(2)).saveImageCrawlerResultAsArtifact(any(String.class),any(String.class),any(String.class));
    }

    @Test
    public void shouldWorkWithoutACacheFileAndIgnorePathImgCBuilderPerformIT() throws IOException {
        // given
        final String imgCPath = RESOURCE_DIR + File.separator + IMG_C_NAME;
        final String executeDirectory = testDirPath;
        final String configurationFile = RESOURCE_DIR+File.separator+EXAMPLE_CONFIG_NAME;
        final String cacheFile = null;
        final String ignorePath = testDirPath;
        final String startingWarning = "9";
        final String startingFail = "10";
        final boolean useInternalIgnoreAndConfig = false;
        final ImgCBuilder icb = new ImgCBuilder(imgCPath, configurationFile, cacheFile,
                ignorePath, executeDirectory, startingWarning, startingFail, useInternalIgnoreAndConfig);
        icb.setIImgCValidator(vSpy);
        icb.setCommandExecutorFactory(cefMock);
        final String resultName = "out";

        // create given stuff
        copy(new File(RESOURCE_DIR+File.separator+EXAMPLE_CONFIG_NAME), new File(testDirPath+File.separator+EXAMPLE_CONFIG_NAME));
        copy(new File(RESOURCE_DIR+File.separator+TEST_DATA_NAME), new File(testDirPath+File.separator+TEST_DATA_NAME));

        // when
        final boolean result = icb.perform(abMock, lMock, blMock);

        // then
        assertTrue(result);
        final File xmlResult = new File(testDirPath+File.separator+resultName+".xml");
        assertTrue(xmlResult.exists());
        assertTrue(xmlResult.isFile());
        final File htmlResult = new File(testDirPath+File.separator+resultName+".html");
        assertTrue(htmlResult.exists());
        assertTrue(htmlResult.isFile());

        verify(vSpy).isValidImgCPath(imgCPath);
        verify(vSpy).isValidExecuteDirectory(executeDirectory);

        verify(cefMock).createCommandExecutor(any(BuildListener.class),any(String.class));

        verify(ceSpy).copyImageCrawler(any(String.class), any(String.class));
        verify(ceSpy).runImgCrawler(any(String.class),any(String.class),any(List.class));
        verify(ceSpy,times(2)).saveImageCrawlerResultAsArtifact(any(String.class),any(String.class),any(String.class));
    }

    @Test
    public void shouldFailIfExecuteDirectoryIsInvalidImgCBuilderPerformIT() throws IOException {
        // given
        final String imgCPath = RESOURCE_DIR + File.separator + IMG_C_NAME;
        final String executeDirectory = testDirPath+"A";
        final String configurationFile = RESOURCE_DIR+File.separator+EXAMPLE_CONFIG_NAME;
        final String cacheFile = null;
        final String ignorePath = testDirPath;
        final String startingWarning = "9";
        final String startingFail = "10";
        final boolean useInternalIgnoreAndConfig = false;
        final ImgCBuilder icb = new ImgCBuilder(imgCPath, configurationFile, cacheFile,
                ignorePath, executeDirectory, startingWarning, startingFail, useInternalIgnoreAndConfig);
        icb.setIImgCValidator(vSpy);
        // create given stuff
        copy(new File(RESOURCE_DIR+File.separator+EXAMPLE_CONFIG_NAME), new File(testDirPath+File.separator+EXAMPLE_CONFIG_NAME));
        copy(new File(RESOURCE_DIR+File.separator+TEST_DATA_NAME), new File(testDirPath+File.separator+TEST_DATA_NAME));

        // when
        final boolean result = icb.perform(abMock, lMock, blMock);

        // then
        assertFalse(result);
        verify(vSpy).isValidExecuteDirectory(executeDirectory);
    }

    @Test
    public void shouldFailIfConfigurationFileIsInvalidImgCBuilderPerformIT() throws IOException {
        // given
        final String imgCPath = RESOURCE_DIR + File.separator + IMG_C_NAME;
        final String executeDirectory = testDirPath;
        final String configurationFile = "INVALIDCONFIGFILE_AOJN)(//&$§$%&/(";
        final String cacheFile = null;
        final String ignorePath = null;
        final String startingWarning = "9";
        final String startingFail = "10";
        final boolean useInternalIgnoreAndConfig = false;
        final ImgCBuilder icb = new ImgCBuilder(imgCPath, configurationFile, cacheFile,
                ignorePath, executeDirectory, startingWarning, startingFail, useInternalIgnoreAndConfig);
        icb.setIImgCValidator(vSpy);
        // create given stuff
        copy(new File(RESOURCE_DIR+File.separator+EXAMPLE_CONFIG_NAME), new File(testDirPath+File.separator+EXAMPLE_CONFIG_NAME));
        copy(new File(RESOURCE_DIR+File.separator+TEST_DATA_NAME), new File(testDirPath+File.separator+TEST_DATA_NAME));

        // when
        final boolean result = icb.perform(abMock, lMock, blMock);

        // then
        assertFalse(result);
        verify(vSpy).isValidConfigurationFile(configurationFile);
    }

    @Test
    public void shouldFailIfCacheFileIsInvalidImgCBuilderPerformIT() throws IOException {
        // given
        final String imgCPath = RESOURCE_DIR + File.separator + IMG_C_NAME;
        final String executeDirectory = testDirPath;
        final String configurationFile = RESOURCE_DIR+File.separator+EXAMPLE_CONFIG_NAME;
        final String cacheFile = "afaf.invaldiC";
        final String ignorePath = null;
        final String startingWarning = "9";
        final String startingFail = "10";
        final boolean useInternalIgnoreAndConfig = false;
        final ImgCBuilder icb = new ImgCBuilder(imgCPath, configurationFile, cacheFile,
                ignorePath, executeDirectory, startingWarning, startingFail,useInternalIgnoreAndConfig);
        icb.setIImgCValidator(vSpy);
        // create given stuff
        copy(new File(RESOURCE_DIR+File.separator+EXAMPLE_CONFIG_NAME), new File(testDirPath+File.separator+EXAMPLE_CONFIG_NAME));
        copy(new File(RESOURCE_DIR+File.separator+TEST_DATA_NAME), new File(testDirPath+File.separator+TEST_DATA_NAME));

        // when
        final boolean result = icb.perform(abMock, lMock, blMock);

        // then
        assertFalse(result);
        verify(vSpy).isValidCacheFile(cacheFile);
    }

    @Test
    public void shouldFailIfIgnorePathIsInvalidImgCBuilderPerformIT() throws IOException {
        // given
        final String imgCPath = RESOURCE_DIR + File.separator + IMG_C_NAME;
        final String executeDirectory = testDirPath;
        final String configurationFile = RESOURCE_DIR+File.separator+EXAMPLE_CONFIG_NAME;
        final String cacheFile = null;
        final String ignorePath = RESOURCE_DIR + File.separator + IMG_C_NAME;
        final String startingWarning = "9";
        final String startingFail = "10";
        final boolean useInternalIgnoreAndConfig = false;
        final ImgCBuilder icb = new ImgCBuilder(imgCPath, configurationFile, cacheFile,
                ignorePath, executeDirectory, startingWarning, startingFail,useInternalIgnoreAndConfig);
        icb.setIImgCValidator(vSpy);
        // create given stuff
        copy(new File(RESOURCE_DIR+File.separator+EXAMPLE_CONFIG_NAME), new File(testDirPath+File.separator+EXAMPLE_CONFIG_NAME));
        copy(new File(RESOURCE_DIR+File.separator+TEST_DATA_NAME), new File(testDirPath+File.separator+TEST_DATA_NAME));

        // when
        final boolean result = icb.perform(abMock, lMock, blMock);

        // then
        assertFalse(result);
        verify(vSpy).isValidIgnoreDir(ignorePath);
    }

    @Test
    public void shouldFailIfImgCPathIsInvalidImgCBuilderPerformIT() throws IOException {
        // given
        final String imgCPath = RESOURCE_DIR + File.separator + IMG_C_NAME + "D";
        final String executeDirectory = testDirPath;
        final String configurationFile = RESOURCE_DIR+File.separator+EXAMPLE_CONFIG_NAME;
        final String cacheFile = null;
        final String ignorePath = null;
        final String startingWarning = "9";
        final String startingFail = "10";
        final boolean useInternalIgnoreAndConfig = false;
        final ImgCBuilder icb = new ImgCBuilder(imgCPath, configurationFile, cacheFile,
                ignorePath, executeDirectory, startingWarning, startingFail,useInternalIgnoreAndConfig);
        icb.setIImgCValidator(vSpy);
        // create given stuff
        copy(new File(RESOURCE_DIR + File.separator + EXAMPLE_CONFIG_NAME), new File(testDirPath + File.separator + EXAMPLE_CONFIG_NAME));
        copy(new File(RESOURCE_DIR+File.separator+TEST_DATA_NAME), new File(testDirPath+File.separator+TEST_DATA_NAME));

        // when
        final boolean result = icb.perform(abMock, lMock, blMock);

        // then
        assertFalse(result);
        verify(vSpy).isValidImgCPath(imgCPath);
    }

    @Test
    public void shouldFailIfStartingWarningIsInvalidImgCBuilderPerformIT() throws IOException {
        // given
        final String imgCPath = RESOURCE_DIR + File.separator + IMG_C_NAME;
        final String executeDirectory = testDirPath;
        final String configurationFile = RESOURCE_DIR+File.separator+EXAMPLE_CONFIG_NAME;
        final String cacheFile = null;
        final String ignorePath = testDirPath;
        final String startingWarning = "-42";
        final String startingFail = "10";
        final boolean useInternalIgnoreAndConfig = false;
        final ImgCBuilder icb = new ImgCBuilder(imgCPath, configurationFile, cacheFile,
                ignorePath, executeDirectory, startingWarning, startingFail, useInternalIgnoreAndConfig);
        icb.setIImgCValidator(vSpy);
        // create given stuff
        copy(new File(RESOURCE_DIR+File.separator+EXAMPLE_CONFIG_NAME), new File(testDirPath+File.separator+EXAMPLE_CONFIG_NAME));
        copy(new File(RESOURCE_DIR+File.separator+TEST_DATA_NAME), new File(testDirPath+File.separator+TEST_DATA_NAME));

        // when
        final boolean result = icb.perform(abMock, lMock, blMock);

        // then
        assertFalse(result);
        verify(vSpy).isValidStartingWarning(startingWarning);
    }

    @Test
    public void shouldFailIfStartingFailIsInvalidImgCBuilderPerformIT() throws IOException {
        // given
        final String imgCPath = RESOURCE_DIR + File.separator + IMG_C_NAME;
        final String executeDirectory = testDirPath;
        final String configurationFile = RESOURCE_DIR+File.separator+EXAMPLE_CONFIG_NAME;
        final String cacheFile = null;
        final String ignorePath = testDirPath;
        final String startingWarning = "9";
        final String startingFail = "-42";
        final boolean useInternalIgnoreAndConfig = false;
        final ImgCBuilder icb = new ImgCBuilder(imgCPath, configurationFile, cacheFile,
                ignorePath, executeDirectory, startingWarning, startingFail, useInternalIgnoreAndConfig);
        icb.setIImgCValidator(vSpy);
        // create given stuff
        copy(new File(RESOURCE_DIR+File.separator+EXAMPLE_CONFIG_NAME), new File(testDirPath+File.separator+EXAMPLE_CONFIG_NAME));
        copy(new File(RESOURCE_DIR+File.separator+TEST_DATA_NAME), new File(testDirPath+File.separator+TEST_DATA_NAME));

        // when
        final boolean result = icb.perform(abMock, lMock, blMock);

        // then
        assertFalse(result);
        verify(vSpy).isValidStartingFail(startingFail);
    }

    private boolean createTestDir(){
        File testDir = new File(testDirPath);
        if(testDir.exists()){
            final int min = 1;
            final int max = 10000000;
            testDirPath = WORK_DIR+File.separator+"TestDir"+new Date().getTime()+(random.nextInt((max - min) + 1) + min);
            return createTestDir();
        }else{
            return testDir.mkdirs();
        }
    }

    // http://stackoverflow.com/questions/7768071/how-to-delete-directory-content-in-java
    private static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    // http://stackoverflow.com/questions/5368724/how-to-copy-a-folder-and-all-its-subfolders-and-files-into-another-folder
    private void copy(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            copyDirectory(sourceLocation, targetLocation);
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }

    private void copyDirectory(File source, File target) throws IOException {
        if (!target.exists()) {
            target.mkdir();
        }
        for (String f : source.list()) {
            copy(new File(source, f), new File(target, f));
        }
    }

    private void copyFile(File source, File target) throws IOException {
        InputStream in = new FileInputStream(source);
        OutputStream out = new FileOutputStream(target);
        byte[] buf = new byte[1024];
        int length;
        while ((length = in.read(buf)) > 0) {
            out.write(buf, 0, length);
        }
    }

}
