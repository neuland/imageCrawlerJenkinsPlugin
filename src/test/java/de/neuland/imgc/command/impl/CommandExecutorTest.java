package de.neuland.imgc.command.impl;

import de.neuland.imgc.command.InvalidXMLException;
import de.neuland.imgc.command.impl.CommandExecutor;
import de.neuland.imgc.command.impl.xml.ImageCrawlerReport;
import hudson.model.BuildListener;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;



public class CommandExecutorTest {

    protected final static String ARCHIVE_DIR_SUB_PATH = "archive";
    protected final static String IMG_C_NAME = "imageCrawler-1.1-SNAPSHOT.jar";
    protected final static String WORK_DIR = System.getProperty("user.dir");
    protected final static String RESOURCE_DIR = WORK_DIR+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator+
           "de"+File.separator+"neuland"+File.separator+"imgc"+File.separator+"command"+File.separator+"impl"+File.separator;
    private final static String defaultXsdPath = File.separator + "de" + File.separator + "neuland" + File.separator + "imgc" + File.separator + "command" + File.separator + "impl" + File.separator + "imageCrawlerReport.xsd";

    protected CommandExecutor c;
    private BuildListener l;


    @Before
    public void before(){
        l = mock(BuildListener.class);
        when(l.getLogger()).thenReturn(System.out);
        c = new CommandExecutor(l,ARCHIVE_DIR_SUB_PATH);
    }

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionAtConstructorCommandExecutorNoBuildListener() {
        new CommandExecutor(null,ARCHIVE_DIR_SUB_PATH);
	}

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionAtConstructorCommandExecutorNullArchiveDirSubPath() {
        BuildListener l = mock(BuildListener.class);
        when(l.getLogger()).thenReturn(System.out);
        new CommandExecutor(l,null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionAtConstructorCommandExecutorEmptyArchiveDirSubPath() {
        BuildListener l = mock(BuildListener.class);
        when(l.getLogger()).thenReturn(System.out);
        new CommandExecutor(l," ");
    }

    @Test
    public void saveImageCrawlerResultAsArtifactShouldFailIfTheArchiveDirIsAFile() throws IOException {
        // given
        final String archiveFile = "testArchiveFileX";
        final String buildRootDir = WORK_DIR;
        final String absoluteFilePath = "absoluteFileX";
        try{
            // create given stuff
            new File(buildRootDir,archiveFile).createNewFile();
            new File(buildRootDir,absoluteFilePath).createNewFile();
            CommandExecutor c = new CommandExecutor(l,archiveFile);
            // when
            final boolean result = c.saveImageCrawlerResultAsArtifact(WORK_DIR,buildRootDir,absoluteFilePath);
            // then
            assertFalse(result);
        }finally {
            new File(buildRootDir,archiveFile).delete();
            new File(buildRootDir,absoluteFilePath).delete();
        }
    }

    @Test
    public void shouldCopyImageCrawler(){
        // given
        final String targetDirectory = RESOURCE_DIR+File.separator+"TESTING";
        final String imgCPath = RESOURCE_DIR+File.separator+IMG_C_NAME;
        try{
            // when
            new File(targetDirectory).mkdirs();
            final boolean r = c.copyImageCrawler(targetDirectory,imgCPath);
            // then
            assertTrue(r);
            final File newImgC = new File(targetDirectory+File.separator+IMG_C_NAME);
            assertTrue(newImgC.exists());
            assertTrue(newImgC.isFile());
        } finally {
            // cleanUp
            final File newImgC = new File(targetDirectory+File.separator+IMG_C_NAME);
            if(newImgC.exists()) newImgC.delete();
            final File targetD = new File(targetDirectory);
            if(targetD.exists()) targetD.delete();
        }
    }

    @Test
    public void shouldNotCopyImageCrawler(){
        // given
        final String targetDirectory = RESOURCE_DIR+File.separator+"TESTING";
        final String imgCPath = RESOURCE_DIR+File.separator+IMG_C_NAME;
        // when
        final boolean r = c.copyImageCrawler(targetDirectory,imgCPath);
        // then
        assertFalse(r);
    }

    @Test
    public void shouldNotSaveImageCrawlerResultAsArtifactNoAccessPermissionLevel(){
        // given
        final String executeDirectory = RESOURCE_DIR;
        final String buildRootDir = "";
        final String absoluteFilePath = "";
        // when
        final boolean r = c.saveImageCrawlerResultAsArtifact(executeDirectory,buildRootDir,absoluteFilePath);
        // then
        assertFalse(r);
    }

    @Test(expected = InvalidXMLException.class)
    public void shouldThrowInvalidXMLExceptionIfFileNotFoundAtAnalyseImageCrawlerResult() throws Exception {
        // given
        final String xml = RESOURCE_DIR + "NOTFOUND.xml";
        // when
        c.analyseImageCrawlerResult(xml);
        // then expected
    }

    @Test(expected = InvalidXMLException.class)
    public void shouldThrowInvalidXMLExceptionIfXMLIsNotValidAtAnalyseImageCrawlerResult() throws Exception {
        // given
        final String xml = RESOURCE_DIR + "invalid.xml";
        // when
        c.analyseImageCrawlerResult(xml);
        // then expected
    }

    @Test
    public void shouldFoundRightNumberAtAnalyseImageCrawlerResult() throws Exception {
        // given
        final String xml = RESOURCE_DIR + "valid.xml";
        // when
        final int r = c.analyseImageCrawlerResult(xml);
        // then
        assertEquals(6,r);
    }

    @Test(expected = InvalidXMLException.class)
    public void analyseImageCrawlerResultShouldFailIfJAXBEFailed() throws InvalidXMLException, JAXBException {
        // given
        final String xml = RESOURCE_DIR + "valid.xml";
        try{
            // mock
            JAXBContext jAXBContext = mock(JAXBContext.class);
            Unmarshaller jaxbUnmarshaller = mock(Unmarshaller.class);
            when(jAXBContext.createUnmarshaller()).thenReturn(jaxbUnmarshaller);
            when(jaxbUnmarshaller.unmarshal(any(File.class))).thenThrow(JAXBException.class);
            c.setJAXBContext(jAXBContext);
            // when
            c.analyseImageCrawlerResult(xml);
        }finally {
            c.setJAXBContext(JAXBContext.newInstance(ImageCrawlerReport.class)); // isStatic => Reset
        }

    }

    @Test
	public void shouldBeTrueIfXMLIsValidAtIsValidXMLTest() {
        // given
        final String xsdPath = defaultXsdPath;
        final URL schemaFile = CommandExecutor.class.getResource(xsdPath);
        final String xml = RESOURCE_DIR + "valid.xml";
        // when
        final boolean r = c.isValidXML(xml, schemaFile);
        // then
        assertTrue(r);
	}

    @Test
    public void shouldBeFalseIfXMLIsInvalidAtIsValidXMLTest() {
        // given
        final String xsdPath = defaultXsdPath;
        final URL schemaFile = CommandExecutor.class.getResource(xsdPath);
        final String xml = RESOURCE_DIR + "invalid.xml";
        // when
        final boolean r = c.isValidXML(xml, schemaFile);
        // then
        assertFalse(r);
    }

    @Test
    public void shouldSaveImageCrawlerResultAsArtifact() throws IOException {
        // given
        final String testFileName = "saveFile.txt";
        final String executeDirectory = RESOURCE_DIR;
        final String buildRootDir = RESOURCE_DIR+File.separator+"buildRootDir";
        final String absoluteFilePath = RESOURCE_DIR+File.separator+testFileName;
        final String resultFileAbsolutePath = buildRootDir+File.separator+ARCHIVE_DIR_SUB_PATH+File.separator+testFileName;
        try{
            // create given stuff
            new File(buildRootDir).mkdirs();
            assertTrue(new File(absoluteFilePath).createNewFile());
            // when
            final boolean result = c.saveImageCrawlerResultAsArtifact(executeDirectory, buildRootDir, absoluteFilePath);
            // then
            assertTrue(result);
            assertTrue(new File(resultFileAbsolutePath).exists());
        } finally {
            // cleanUp
            final File resultFile = new File(resultFileAbsolutePath);
            if(resultFile.exists()) resultFile.delete();
            final File testFile = new File(absoluteFilePath);
            if(testFile.exists()) testFile.delete();
            final File targetCreatedD = new File(buildRootDir+File.separator+ARCHIVE_DIR_SUB_PATH);
            if(targetCreatedD.exists()) targetCreatedD.delete();
            final File targetD = new File(buildRootDir);
            if(targetD.exists()) targetD.delete();
        }
    }

    @Test
    public void shouldNotSaveImageCrawlerResultAsArtifactNoValidSource() {
        // given
        final String testFileName = "saveFile.txt";
        final String executeDirectory = RESOURCE_DIR;
        final String buildRootDir = RESOURCE_DIR+File.separator+"buildRootDir";
        final String absoluteFilePath = RESOURCE_DIR+File.separator+testFileName;
        try {
            // create given stuff
            new File(buildRootDir).mkdirs();
            // when
            final boolean result = c.saveImageCrawlerResultAsArtifact(executeDirectory, buildRootDir, absoluteFilePath);
            // then
            assertFalse(result);
        } finally {
            // cleanUp
            final File targetCreatedD = new File(buildRootDir, ARCHIVE_DIR_SUB_PATH);
            if(targetCreatedD.exists()) targetCreatedD.delete();
            final File targetD = new File(buildRootDir);
            if(targetD.exists()) targetD.delete();
        }
    }

    @Test
    public void shouldSaveImageCrawlerResultAsArtifactNoExistingTarget() throws IOException {
        // given
        final String testFileName = "saveFile.txt";
        final String executeDirectory = RESOURCE_DIR;
        final String buildRootDir = RESOURCE_DIR+File.separator+"buildRootDir";
        final String absoluteFilePath = RESOURCE_DIR+File.separator+testFileName;
        final String resultFileAbsolutePath = buildRootDir+File.separator+ARCHIVE_DIR_SUB_PATH+File.separator+testFileName;
        try{
            // create given stuff
            assertTrue(new File(absoluteFilePath).createNewFile());
            // when
            final boolean result = c.saveImageCrawlerResultAsArtifact(executeDirectory, buildRootDir, absoluteFilePath);
            // then
            assertTrue(result);
            assertTrue(new File(resultFileAbsolutePath).exists());
        } finally {
            // cleanUp
            final File resultFile = new File(resultFileAbsolutePath);
            if(resultFile.exists()) resultFile.delete();
            final File testFile = new File(absoluteFilePath);
            if(testFile.exists()) testFile.delete();
            final File targetCreatedD = new File(buildRootDir+File.separator+ARCHIVE_DIR_SUB_PATH);
            if(targetCreatedD.exists()) targetCreatedD.delete();
            final File targetD = new File(buildRootDir);
            if(targetD.exists()) targetD.delete();
        }

    }

    @Test
    public void shouldGetJarNameSimpleJarName(){


    }
}