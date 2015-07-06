package de.neuland.imgc.imgCPlugin;


import de.neuland.imgc.command.InvalidXMLException;
import de.neuland.imgc.command.impl.CommandExecutor;
import de.neuland.imgc.command.impl.CommandExecutorFactory;
import de.neuland.imgc.imgCPlugin.ImgCBuilder;
import de.neuland.imgc.validator.impl.ImgCValidator;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class ImgCBuilderTest {

    private final int defaultAnalyseImageCrawlerResult = 0;

    private ImgCBuilder icb;
    private BuildListener blMock;
    private AbstractBuild abMock;
    private Launcher lMock;
    private ImgCValidator vMock;
    private CommandExecutorFactory cefMock;
    private CommandExecutor ceMock;

    @Before
    public void setup() throws InvalidXMLException{
        vMock = mock(ImgCValidator.class);

        blMock = mock(BuildListener.class);
        when(blMock.getLogger()).thenReturn(System.out);

        abMock = mock(AbstractBuild.class);
        doNothing().when(abMock).setResult(any(Result.class));
        File defaultFile = new File("Default");
        when(abMock.getArtifactsDir()).thenReturn(defaultFile);
        when(abMock.getRootDir()).thenReturn(defaultFile);

        lMock = mock(Launcher.class);

        cefMock = mock(CommandExecutorFactory.class);
        ceMock = mock(CommandExecutor.class);
        when(cefMock.createCommandExecutor(any(BuildListener.class),any(String.class))).thenReturn(ceMock);
        icb = new ImgCBuilder("","","","","","","",false);
        icb.setCommandExecutorFactory(cefMock);
        icb.setIImgCValidator(vMock);

        // all Validations true
        when(vMock.isValidExecuteDirectory(any(String.class))).thenReturn(true);
        when(vMock.isValidCacheFile(any(String.class))).thenReturn(true);
        when(vMock.isValidConfigurationFile(any(String.class))).thenReturn(true);
        when(vMock.isValidIgnoreDir(any(String.class))).thenReturn(true);
        when(vMock.isValidImgCPath(any(String.class))).thenReturn(true);
        when(vMock.isValidStartingFail(any(String.class))).thenReturn(true);
        when(vMock.isValidStartingWarning(any(String.class))).thenReturn(true);

        // all ceMock Methods => Valid return values
        when(ceMock.copyImageCrawler(any(String.class), any(String.class))).thenReturn(true);
        when(ceMock.runImgCrawler(any(String.class), any(String.class), any(List.class))).thenReturn(true);
        when(ceMock.saveImageCrawlerResultAsArtifact(any(String.class), any(String.class), any(String.class))).thenReturn(true,true);
        when(ceMock.analyseImageCrawlerResult(any(String.class))).thenReturn(defaultAnalyseImageCrawlerResult);
    }


    @Test
    public void performShouldFailIfExecuteDirectoryIsInvalidTest(){
        // mock
        when(vMock.isValidExecuteDirectory(any(String.class))).thenReturn(false);

        // when
        final boolean result = icb.perform(abMock,lMock,blMock);

        // then
        assertFalse(result);
        verify(vMock).isValidExecuteDirectory(any(String.class));
        verify(abMock).setResult(Result.FAILURE);
    }

    @Test
    public void performShouldFailIfCacheFileIsInvalidTest(){
        // mock
        when(vMock.isValidCacheFile(any(String.class))).thenReturn(false);

        // when
        final boolean result = icb.perform(abMock,lMock,blMock);

        // then
        assertFalse(result);
        verify(vMock).isValidCacheFile(any(String.class));
        verify(abMock).setResult(Result.FAILURE);
    }

    @Test
    public void performShouldFailIfConfigurationFileIsInvalidTest(){
        // mock
        when(vMock.isValidConfigurationFile(any(String.class))).thenReturn(false);

        // when
        final boolean result = icb.perform(abMock,lMock,blMock);

        // then
        assertFalse(result);
        verify(vMock).isValidConfigurationFile(any(String.class));
        verify(abMock).setResult(Result.FAILURE);
    }

    @Test
    public void performShouldFailIfIgnoreDirIsInvalidTest(){
        // mock
        when(vMock.isValidIgnoreDir(any(String.class))).thenReturn(false);

        // when
        final boolean result = icb.perform(abMock,lMock,blMock);

        // then
        assertFalse(result);
        verify(vMock).isValidIgnoreDir(any(String.class));
        verify(abMock).setResult(Result.FAILURE);
    }

    @Test
    public void performShouldFailIfImgCPathIsInvalidTest(){
        // mock
        when(vMock.isValidImgCPath(any(String.class))).thenReturn(false);

        // when
        final boolean result = icb.perform(abMock,lMock,blMock);

        // then
        assertFalse(result);
        verify(vMock).isValidImgCPath(any(String.class));
        verify(abMock).setResult(Result.FAILURE);
    }

    @Test
    public void performShouldFailIfStartingFailIsInvalidTest(){
        // mock
        when(vMock.isValidStartingFail(any(String.class))).thenReturn(false);

        // when
        final boolean result = icb.perform(abMock,lMock,blMock);

        // then
        assertFalse(result);
        verify(vMock).isValidStartingFail(any(String.class));
        verify(abMock).setResult(Result.FAILURE);
    }

    @Test
    public void performShouldFailIfStartingWarningIsInvalidTest(){
        // mock
        when(vMock.isValidStartingWarning(any(String.class))).thenReturn(false);

        // when
        final boolean result = icb.perform(abMock,lMock,blMock);

        // then
        assertFalse(result);
        verify(vMock).isValidStartingWarning(any(String.class));
        verify(abMock).setResult(Result.FAILURE);
    }


    @Test
    public void performShouldFailIfCommandExecutorCanNotCopyImgCTest(){
        // mock
        when(ceMock.copyImageCrawler(any(String.class),any(String.class))).thenReturn(false);

        // when
        final boolean result = icb.perform(abMock,lMock,blMock);

        // then
        assertFalse(result);
        verify(ceMock).copyImageCrawler(any(String.class), any(String.class));
        verify(abMock).setResult(Result.FAILURE);
    }

    @Test
    public void performShouldFailIfCommandExecutorCanNotRunImgCrawlerTest(){
        // mock
        when(ceMock.runImgCrawler(any(String.class), any(String.class), any(List.class))).thenReturn(false);

        // when
        final boolean result = icb.perform(abMock,lMock,blMock);

        // then
        assertFalse(result);
        verify(ceMock).runImgCrawler(any(String.class), any(String.class), any(List.class));
        verify(abMock).setResult(Result.FAILURE);
    }

    @Test
    public void performShouldFailIfCommandExecutorCanNotSaveFirstImageCrawlerResultAsArtifactTest(){
        // mock
        when(ceMock.saveImageCrawlerResultAsArtifact(any(String.class), any(String.class), any(String.class))).thenReturn(false,true);

        // when
        final boolean result = icb.perform(abMock,lMock,blMock);

        // then
        assertFalse(result);
        verify(ceMock).saveImageCrawlerResultAsArtifact(any(String.class), any(String.class), any(String.class));
        verify(abMock).setResult(Result.FAILURE);
    }

    @Test
    public void performShouldFailIfCommandExecutorCanNotSaveSecondImageCrawlerResultAsArtifactTest(){
        // mock
        when(ceMock.saveImageCrawlerResultAsArtifact(any(String.class), any(String.class), any(String.class))).thenReturn(true,false);

        // when
        final boolean result = icb.perform(abMock,lMock,blMock);

        // then
        assertFalse(result);
        verify(ceMock, times(2)).saveImageCrawlerResultAsArtifact(any(String.class), any(String.class), any(String.class));
        verify(abMock).setResult(Result.FAILURE);
    }

    @Test
    public void performShouldFailIfCommandExecutorCanNotAnalyseImageCrawlerResultTest() throws InvalidXMLException {
        // mock;
        when(ceMock.analyseImageCrawlerResult(any(String.class))).thenThrow(InvalidXMLException.class);

        // when
        final boolean result = icb.perform(abMock,lMock,blMock);

        // then
        assertFalse(result);
        verify(ceMock).analyseImageCrawlerResult(any(String.class));
        verify(abMock).setResult(Result.FAILURE);
    }

    @Test
    public void performShouldFailIfCommandExecutorAnalyseImageCrawlerResultIsNegativeTest() throws InvalidXMLException {
        // mock
        when(ceMock.analyseImageCrawlerResult(any(String.class))).thenReturn(-1);

        // when
        final boolean result = icb.perform(abMock,lMock,blMock);

        // then
        assertFalse(result);
        verify(ceMock).analyseImageCrawlerResult(any(String.class));
        verify(abMock).setResult(Result.FAILURE);
    }

    @Test
    public void performShouldFailIfCommandExecutorAnalyseImageCrawlerResultIsGreaterThanStartErrorTest() throws InvalidXMLException {
        // given
        final int analyseImageCrawlerResult = 5;
        final String startError = "4";
        final String startWarning = "1";

        // mock
        when(ceMock.analyseImageCrawlerResult(any(String.class))).thenReturn(analyseImageCrawlerResult);
        icb = new ImgCBuilder("","","","","",startWarning,startError,false);
        icb.setIImgCValidator(vMock);
        icb.setCommandExecutorFactory(cefMock);

        // when
        final boolean result = icb.perform(abMock,lMock,blMock);

        // then
        assertFalse(result);
        verify(ceMock).analyseImageCrawlerResult(any(String.class));
        verify(abMock).setResult(Result.FAILURE);
    }

    @Test
    public void performShouldWarnIfCommandExecutorAnalyseImageCrawlerResultIsGreaterThanStartWarningTest() throws InvalidXMLException {
        // given
        final int analyseImageCrawlerResult = 5;
        final String startError = "10";
        final String startWarning = "4";

        // mock
        when(ceMock.analyseImageCrawlerResult(any(String.class))).thenReturn(analyseImageCrawlerResult);
        icb = new ImgCBuilder("","","","","",startWarning,startError,false);
        icb.setIImgCValidator(vMock);
        icb.setCommandExecutorFactory(cefMock);

        // when
        final boolean result = icb.perform(abMock,lMock,blMock);

        // then
        assertTrue(result);
        verify(ceMock).analyseImageCrawlerResult(any(String.class));
        verify(abMock).setResult(Result.UNSTABLE);
    }

    @Test
    public void performShouldPerformWithoutErrorsTest() throws InvalidXMLException {
        // given
        final int analyseImageCrawlerResult = 0;
        final String startError = "10";
        final String startWarning = "4";

        // mock
        icb = new ImgCBuilder("","","","","",startWarning,startError,false);
        icb.setIImgCValidator(vMock);
        icb.setCommandExecutorFactory(cefMock);

        // when
        final boolean result = icb.perform(abMock,lMock,blMock);

        // then
        assertTrue(result);
        verify(ceMock).copyImageCrawler(any(String.class), any(String.class));
        verify(ceMock).runImgCrawler(any(String.class), any(String.class), any(List.class));
        verify(ceMock, times(2)).saveImageCrawlerResultAsArtifact(any(String.class), any(String.class), any(String.class));
        verify(ceMock).analyseImageCrawlerResult(any(String.class));
        verify(abMock, never()).setResult(any(Result.class));
    }

    @Test
    public void performSimpleImgCBuilderDataTest() {
        // given
        final String imgCPath = "Test1";
        final String executeDirectory = "Test2";
        final String configurationFile = "Test3";
        final String cacheFile = "Test4";
        final String ignorePath = "Test5";
        final String startingWarning = "-1";
        final String startingFail = "10";
        final boolean useInternalIgnoreAndConfig = false;
        // when
        final ImgCBuilder icb = new ImgCBuilder(imgCPath, configurationFile, cacheFile,
                ignorePath, executeDirectory, startingWarning, startingFail, useInternalIgnoreAndConfig);
        // then
        assertEquals(icb.getImgCPath(),imgCPath);
        assertEquals(icb.getExecuteDirectory(),executeDirectory);
        assertEquals(icb.getConfigurationFile(),configurationFile);
        assertEquals(icb.getCacheFile(),cacheFile);
        assertEquals(icb.getIgnorePath(),ignorePath);
        assertEquals(icb.getStartingWarning(),startingWarning);
        assertEquals(icb.getStartingFail(),startingFail);
    }

}
